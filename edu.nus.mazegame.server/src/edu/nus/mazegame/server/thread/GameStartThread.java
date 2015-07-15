package edu.nus.mazegame.server.thread;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.nus.mazegame.model.enumeration.GameState;
import edu.nus.mazegame.model.impl.SafePoint;
import edu.nus.mazegame.model.interf.IBoard;
import edu.nus.mazegame.model.interf.ICell;
import edu.nus.mazegame.model.interf.IPlayer;
import edu.nus.mazegame.server.model.LocalContext;
import edu.nus.mazegame.server.util.GameBuilder;

public class GameStartThread extends Thread {

	private List<IPlayer> playerList;
	private IBoard board;
	private volatile LocalContext context;
	private int treasure;
	private int remainingTreasure;
	public static final int DISCONNECT_TIME_SEC = 10;
	private Map<String, Integer> countDownTimeOutMap = new ConcurrentHashMap<String, Integer>();

	public static final int JOIN_WAITING_TIME = 20;

	private static final Logger logger = Logger.getLogger(GameStartThread.class
			.getName());

	public GameStartThread(List<IPlayer> playerList, IBoard board,
			LocalContext context, int treasure, Map<String, Integer> countDownTimeOutMap) {
		super();
		this.playerList = playerList;
		this.board = board;
		this.context = context;
		this.treasure = treasure;
		this.remainingTreasure = treasure;
		this.countDownTimeOutMap = countDownTimeOutMap;
	}

	@Override
	public void run() {
		while (true) {
			// this needs to be synced. the method calling wait needs to 
			// own the object's monitor. In this case, the object is the 
			// thread object itself (this)
			synchronized (this) {
				try {
					switch (context.getGameState()) {
					case IDLE:
						wait();
						break;
					case WAITING_JOIN:
						executeGameStarting();
						break;
					case START:
						wait();
						break;
					case UPDATE_GAME_STATUS:
						updateGameBoard();
						break;
					case CREATING_GAME:
						break;
					case WAITING_FOR_RESPONSE:
						sleep(20);
						break;
					case FINISH:
						sleep(20);
					default:
						break;
					}
				} catch (InterruptedException e) {
					logger.log(Level.FINER, e.toString(), e);
				}// End try..
			}// end while loop
		}
	}
	

	private void resetGame() throws InterruptedException {
		logger.log(Level.FINE,"resetting game");
		// clean up board...
		board.reinitialize();
		// clean up playerList..
		playerList.clear();
		countDownTimeOutMap.clear();
		// reset context..
		context.reset();
		remainingTreasure = treasure;
	}

	private void updateGameBoard() throws InterruptedException {
		logger.log(Level.FINER, "Updating game board");
		// check latest player location,
		IPlayer player = context.getPlayer();
		SafePoint newPoint = context.getNewPoint();
		int treasureAdded = 0;
		synchronized (playerList) {
			if(validateForUpadating(player, newPoint)){
				treasureAdded = updateGameStatus(player, newPoint);
				}
			}
		
		// reset the counting down timer
		String name = player.getName();
		Iterator<Map.Entry<String, Integer>> iterator = countDownTimeOutMap
				.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, Integer> entry = iterator.next();
			if (entry.getKey().equals(name)) {
				entry.setValue(DISCONNECT_TIME_SEC);
				break;
				}
			}
				
		remainingTreasure -= treasureAdded;
		logger.log(Level.INFO, "Remaining treasure: " +  remainingTreasure);
		//Set end of game data
		if(remainingTreasure <= 0){
			context.setGameState(GameState.FINISH);
		}
		else{
			context.setGameState(GameState.WAITING_FOR_RESPONSE);
		}
		sleep(300);
	}
	
	private boolean validateForUpadating(IPlayer player, SafePoint newPoint){
		for (IPlayer tmpPlayer : playerList) {
			// if the tmp player is current player, go to next player
			if (tmpPlayer.getKey().equals(player.getKey())){
				continue;
			}
			SafePoint tmpPoint = tmpPlayer.getPoint();
			if (newPoint == null || newPoint.equals(tmpPoint)){
				return false;
			}
		}
		return true;
	}

	
	/*
	 * return value: number of treasure founded in the newPoint
	 * */
	private int updateGameStatus(IPlayer player, SafePoint newPoint) {
		// update player score
		synchronized (this) {
			ICell currentCell = board.getCells().get(newPoint);
			int cellTreasure = currentCell.getTreasureNumber();
			int newScore = player.getScore() + cellTreasure;
			currentCell.setTreasureNumber(0);
			
			remainingTreasure -= currentCell.getTreasureNumber();
			logger.log(Level.FINE, "Updating player " + player.getName() + " game status -- newScore: " + newScore + " newPoint:" + newPoint);
			for (IPlayer tmpPlayer : playerList) {
				if(tmpPlayer.getKey().equals(player.getKey())){
					tmpPlayer.setScoreAndPoint(newScore, newPoint);
				}
			}
			context.setPlayer(player);
			return cellTreasure;
		}
	}

	private void executeGameStarting() throws InterruptedException {
		int timer = JOIN_WAITING_TIME;
		// Waiting 20 second passed...
		while (timer != 0) {
			sleep(1000);
			timer--;
			continue;
		}
		// Now we have at least two player! Let's start game
		if (playerList.size() > 1) {
			// no lock required here as the List is confinement at this moment
			context.setGameState(GameState.CREATING_GAME);
			// build board..
			GameBuilder builder = new GameBuilder(board, playerList);
			builder.build(treasure);
			// start count down timer
			startTracingClientStatus();
			context.setGameState(GameState.START);
		} else {
			// Doing the same thing as end of game
			resetGame();
		}
	}

	private void startTracingClientStatus() {
		// build up countDownTimeMap
		synchronized (playerList) {
			for (IPlayer player : playerList) {
				countDownTimeOutMap.put(player.getKey(), DISCONNECT_TIME_SEC);
			}
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				runCountingDownTimer();
			}
		}).start();
	}

	private void runCountingDownTimer() {
		logger.log(Level.INFO, "Start running countingDown timer");
		try {// counting down timer will keep running till game over
			while (context.getGameState().equals(GameState.FINISH) == false) {
				Thread.sleep(1000);
				//reset game back if less than 2 players available, reset game, and stop this thread
				if(countDownTimeOutMap.size() < 2){
					resetGame();
					break;
				}
				Iterator<Map.Entry<String, Integer>> iterator = countDownTimeOutMap
						.entrySet().iterator();
				while (iterator.hasNext()) {
					Map.Entry<String, Integer> entry = iterator.next();
					int newValue = entry.getValue() - 1;
					logger.log(Level.FINE, entry.getKey() + "'s new timing is " + newValue);
					if (newValue == 0) {
						// player crash or too long time no response!
						// remove it from list
						logger.log(Level.FINE, entry.getKey() + " is out of time, to be removed");
						removePlayer(entry);
					} else {
						entry.setValue(newValue);
					}// end if(newValue == 0)/else
				}// end map iterator loop
			}// end timer while loop
		} catch (InterruptedException e) {
			logger.log(Level.FINER, e.toString(), e);
		}
	}

	private void removePlayer(Map.Entry<String, Integer> entry) {
		countDownTimeOutMap.remove(entry.getKey());
		synchronized (playerList) {
			IPlayer tmpPlayer = null;
			for (IPlayer player : playerList) {
				if (player.getKey().equals(entry.getKey())) {
					tmpPlayer = player;
					break;
				}
			}
			playerList.remove(tmpPlayer);
		}
	}
}

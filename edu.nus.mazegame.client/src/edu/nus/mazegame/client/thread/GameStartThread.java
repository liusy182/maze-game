package edu.nus.mazegame.client.thread;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.nus.mazegame.client.session.GameSessionManager;
import edu.nus.mazegame.client.util.GameBuilder;
import edu.nus.mazegame.model.enumeration.GameState;
import edu.nus.mazegame.model.enumeration.ResponseState;
import edu.nus.mazegame.model.impl.LocalContext;
import edu.nus.mazegame.model.impl.SafePoint;
import edu.nus.mazegame.model.impl.ServerResponse;
import edu.nus.mazegame.model.interf.IBoard;
import edu.nus.mazegame.model.interf.ICell;
import edu.nus.mazegame.model.interf.IPlayer;

public class GameStartThread extends Thread {

	private List<IPlayer> playerList;
	private IBoard board;
	private volatile LocalContext context;
	public static final int DISCONNECT_TIME_SEC =10; //Default is 10 sec
	public List<IPlayer> getPlayerList() {
		return playerList;
	}

	public static int getDisconnectTimeSec() {
		return DISCONNECT_TIME_SEC;
	}
	
	private Map<String, Integer> countDownTimeOutMap = new ConcurrentHashMap<String, Integer>();

	public static final int JOIN_WAITING_TIME = 20;

	private static final Logger logger = Logger.getLogger(GameStartThread.class
			.getName());
	
	public GameStartThread(List<IPlayer> playerList, IBoard board,
			LocalContext context,
			Map<String, Integer> countDownTimeOutMap) {
		super();
		updateAllField(playerList, board, context, countDownTimeOutMap);
	}
	
	public void updateAllField(List<IPlayer> playerList, IBoard board,
			LocalContext context, Map<String, Integer> countDownTimeOutMap){
		this.playerList = playerList;
		this.board = board;
		this.context = context;
		this.setCountDownTimeOutMap(countDownTimeOutMap);
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
					default:
						break;
					}
				} catch (InterruptedException e) {
					logger.log(Level.FINER, e.toString(), e);
				}// End try..
			}// end while loop
		}
	}

	public void resetGame() throws InterruptedException {
		logger.log(Level.FINE, "resetting game");
		// clean up board...
		board.reinitialize();
		// clean up playerList..
		playerList.clear();
		countDownTimeOutMap.clear();
		// reset context..
		context.reset();
		context.setRemainingTreasure(GameSessionManager.getInstance().getTreasure());
	}

	private void updateGameBoard() throws InterruptedException {
		logger.log(Level.FINER, "Updating game board");
		// check latest player location,
		IPlayer player = context.getPlayer();
		SafePoint newPoint = context.getNewPoint();
		synchronized (playerList) {
			if (validateForUpadating(player, newPoint)) {
				updateGameStatus(player, newPoint);
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


		logger.log(Level.INFO, "Remaining treasure: " + context.getRemainingTreasure());
		// Set end of game data
		if (context.getRemainingTreasure() <= 0) {
			ServerResponse response = new ServerResponse();
			response.setBoard(board);
			response.setPlayerList(playerList);
			response.setResponseState(ResponseState.GAME_OVER);
			context.setFinishTimeResponse(response);
		}
		context.setGameState(GameState.WAITING_FOR_RESPONSE);
		sleep(300);
	}

	private boolean validateForUpadating(IPlayer player, SafePoint newPoint) {
		for (IPlayer tmpPlayer : playerList) {
			// if the tmp player is current player, go to next player
			if (tmpPlayer.getKey().equals(player.getKey())) {
				continue;
			}
			SafePoint tmpPoint = tmpPlayer.getPoint();
			if (newPoint == null || newPoint.equals(tmpPoint)) {
				return false;
			}
		}
		return true;
	}

	/*
	 * return value: number of treasure founded in the newPoint
	 */
	private void updateGameStatus(IPlayer player, SafePoint newPoint) {
		// update player score
		synchronized (this) {
			ICell currentCell = board.getCells().get(newPoint);
			int cellTreasure = currentCell.getTreasureNumber();
			int currentTreause = context.getRemainingTreasure();
			int newScore = player.getScore() + cellTreasure;
			context.setRemainingTreasure(currentTreause - cellTreasure);
			
			currentCell.setTreasureNumber(0);
			logger.log(Level.FINE, "Updating player " + player.getName()
					+ " game status -- newScore: " + newScore + " newPoint:"
					+ newPoint);
			for (IPlayer tmpPlayer : playerList) {
				if (tmpPlayer.getKey().equals(player.getKey())) {
					tmpPlayer.setScoreAndPoint(newScore, newPoint);
				}
			}
			context.setPlayer(player);
		}
	}

	private void executeGameStarting() throws InterruptedException {
		//reset finish game state
		context.setFinishTimeResponse(null); 
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
			int treasureNumber = GameSessionManager.getInstance().getTreasure();
			context.setGameState(GameState.CREATING_GAME);
			context.setRemainingTreasure(treasureNumber);
			// build board..
			GameBuilder builder = new GameBuilder(board, playerList);
			builder.build(treasureNumber);
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
		new ClientRunningListener(this).start();
	}
	
	public synchronized Map<String, Integer> getCountDownTimeOutMap() {
		return countDownTimeOutMap;
	}
	
	public synchronized void  setCountDownTimeOutMap(Map<String, Integer> countDownTimeOutMap) {
		this.countDownTimeOutMap = countDownTimeOutMap;
	}

}

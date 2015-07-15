package edu.nus.mazegame.server.remote;

import java.io.InvalidObjectException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.nus.mazegame.model.enumeration.GameState;
import edu.nus.mazegame.model.enumeration.ResponseState;
import edu.nus.mazegame.model.impl.SafePoint;
import edu.nus.mazegame.model.impl.ServerResponse;
import edu.nus.mazegame.model.interf.IBoard;
import edu.nus.mazegame.model.interf.IPlayer;
import edu.nus.mazegame.model.rmi.ServerRemote;
import edu.nus.mazegame.server.model.LocalContext;
import edu.nus.mazegame.server.thread.GameStartThread;

public class ServerRemoteImpl implements ServerRemote{
	private IBoard board;
	private List<IPlayer> playerList;
	private LocalContext context;
	private GameStartThread mainGameThread;
	private Map<String, Integer> countDownTimeOutMap;
	public static final String CLASS_NAME = ServerRemoteImpl.class.getName();
	private static final Object lock = new Object();
	private static final Logger logger = Logger.getLogger(CLASS_NAME);

	public ServerRemoteImpl(IBoard board, List<IPlayer> playerList,
			LocalContext context, GameStartThread mainGameThread,
			 Map<String, Integer> countDownTimeOutMap) {
		super();
		this.board = board;
		this.playerList = playerList;
		this.context = context;
		this.mainGameThread = mainGameThread;
		this.countDownTimeOutMap = countDownTimeOutMap;
	}

	@Override
	public ServerResponse joinGame(IPlayer player) throws RemoteException {
		logger.log(Level.INFO, player.getName() + " calls joinGame");
		ServerResponse response = null;
		try {
			if (player.getName() == null) {
				throw new InvalidObjectException(
						"the player name couldn't be null");
			}
			switch (context.getGameState()) {
			case IDLE:
				context.setGameState(GameState.WAITING_JOIN);
				// we need to obtain the monitor object before calling notify
				synchronized(mainGameThread){
					// Wake up the main game thread to initialize game
					mainGameThread.notify();
				}
				// flow through!!
			case WAITING_JOIN:
				response = waitingForJoinGame(player);
				break;
			case START:
				// flow through!!
			case UPDATE_GAME_STATUS:
				response = new ServerResponse();
				response.setResponseState(ResponseState.JOIN_NOT_ALLOWED);
				break;
			case FINISH:
				response = new ServerResponse();
				response.setResponseState(ResponseState.GAME_OVER);
				break;
			default:
				break;
			}
			return response;
		} catch (InterruptedException e) {
			logger.log(Level.FINER, e.toString(), e);
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.toString(), e);
		}
		return response;
	}

	private ServerResponse waitingForJoinGame(IPlayer player) throws InvalidObjectException, InterruptedException{
		// Validate if the playerList already contains the input player
		synchronized (playerList) {
			for(IPlayer tmpPlayer: playerList){
				if(player.getName().equals(tmpPlayer.getName())){
					throw new InvalidObjectException("the player name couldn't be null");
				}
			}
			playerList.add(player);
		}
		//waiting for game state change
		while(context.getGameState().equals(GameState.WAITING_JOIN) || 
				context.getGameState().equals(GameState.CREATING_GAME)){
			Thread.sleep(500);
		}
		ServerResponse response = new ServerResponse();
		if(context.getGameState().equals(GameState.IDLE)){
			//No enough player join the game.. so game will not be started
			response.setResponseState(ResponseState.NO_ENOUGH_PALYER);
		}else if(context.getGameState().equals(GameState.START)){
			//game will be started normally
			response.setPlayerList(playerList);
			response.setResponseState(ResponseState.JOIN_SUCCESS);
			response.setBoard(board);
		}else{
			throw new IllegalStateException("No other game state is allowed in this stage");
		}
		return response;
	}

	@Override
	public ServerResponse move(IPlayer player, SafePoint newPoint, boolean isNoMove) throws RemoteException {
		
		//validate the game state 
		if(context.getGameState().equals(GameState.FINISH)){
			logger.log(Level.INFO, "server set Game Over.");
			return getResponse(ResponseState.GAME_OVER);
		}
		
		if(isNoMove){
			if(player != null){
				logger.log(Level.FINE, "ping from "+  player.getName() + ", " + player.getKey());
				countDownTimeOutMap.put(player.getKey(), GameStartThread.DISCONNECT_TIME_SEC);
			}
			return getResponse(ResponseState.UPDATED);
		}
		ServerResponse response = null;
		while(true){
			if(context.getGameState().equals(GameState.START)){
				synchronized (lock) {
					try {
						if (context.getGameState().equals(GameState.START)) {
							//prepare game updating
							context.setGameState(GameState.UPDATE_GAME_STATUS);
							context.setPlayer(player);
							context.setNewPoint(newPoint);
							// we need to obtain the object monitor before we do
							// notify
							synchronized (mainGameThread) {
								// Wake up the main game thread to update game
								mainGameThread.notify();
								Thread.sleep(30);
							}
							// waiting for main thread update complete
							while (context.getGameState().equals(
									GameState.WAITING_FOR_RESPONSE) == false &&
									context.getGameState().equals(GameState.FINISH) == false) {
								Thread.sleep(30);
							}
							if(context.getGameState().
									equals(GameState.WAITING_FOR_RESPONSE)){
								response = getResponse(ResponseState.UPDATED);
								context.setGameState(GameState.START);
							}
							else{
								response = getResponse(ResponseState.GAME_OVER);
								context.setGameState(GameState.FINISH);
							}
							break;
						}// end double state check
					} catch (InterruptedException e) {
						logger.log(Level.FINER, e.toString(), e);
					}
				}//end synchronized
			}//end state check
			try {
				Thread.sleep(50); //Sleep some time before checking next round
			} catch (InterruptedException e) {
				logger.log(Level.FINER, e.toString(), e);
			}
		}//end while loop
		return response;
	}

	private ServerResponse getResponse(ResponseState state) {
		ServerResponse response = new ServerResponse();
		response.setPlayerList(playerList);
		response.setBoard(board);
		response.setResponseState(state);

		return response;
	}
}

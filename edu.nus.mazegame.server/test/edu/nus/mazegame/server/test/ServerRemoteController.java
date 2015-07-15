package edu.nus.mazegame.server.test;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.nus.mazegame.model.impl.Board;
import edu.nus.mazegame.model.interf.IBoard;
import edu.nus.mazegame.model.interf.IPlayer;
import edu.nus.mazegame.model.interf.RMIConstant;
import edu.nus.mazegame.model.rmi.ServerRemote;
import edu.nus.mazegame.server.model.LocalContext;
import edu.nus.mazegame.server.remote.ServerRemoteImpl;
import edu.nus.mazegame.server.thread.GameStartThread;

public class ServerRemoteController {
	private static ServerRemote stub = null;
	private static Registry registry = null;
	private static final Logger logger = Logger.getLogger(ServerRemoteImplTest.class.getName());
	
	public static void startServer(int dimension, int treasure) {
		IBoard board = new Board(dimension);
		List<IPlayer> playerList = Collections.synchronizedList(new LinkedList<IPlayer>());
		//BoardBuilder builder = new BoardBuilder(dimension, playerList);
		LocalContext context = new LocalContext();
		//Start main game thread
		final Map<String, Integer> countDownTimeOutMap = new ConcurrentHashMap<String, Integer>();
		GameStartThread thread = new GameStartThread(playerList, board, context, treasure, countDownTimeOutMap);
		thread.start();
		
		//register RMI remote interface... 
		registerRMIObject(board, playerList, context, thread, countDownTimeOutMap);
	}
	
	private static void registerRMIObject(IBoard board, List<IPlayer> playerList, LocalContext context, GameStartThread thread, Map<String, Integer> countDownTimeOutMap){	
		try {
			ServerRemote serverObj = new ServerRemoteImpl(board, playerList, context, thread, countDownTimeOutMap);
		    stub = (ServerRemote) UnicastRemoteObject.exportObject(serverObj, 0);
		    
		    try{
		    	registry = LocateRegistry.createRegistry(RMIConstant.RMI_PORT);
		    } catch (RemoteException e){}
		    finally {registry.bind(RMIConstant.RMI_ID, stub);}
		    
		} catch (RemoteException re) {
		    try{
		    	registry.unbind(RMIConstant.RMI_ID);
		    	registry.bind(RMIConstant.RMI_ID,stub);
		    }catch(Exception ee){
		    	logger.log( Level.SEVERE, ee.toString(), ee);
		    }
		} catch (Exception e) {
			logger.log( Level.SEVERE, e.toString(), e);
		}
	}
	
	public static void stopServer()
	{
		try{
			registry.unbind(RMIConstant.RMI_ID);
		}catch(Exception e){
			logger.log( Level.SEVERE, e.toString(), e);
		}
	}

}

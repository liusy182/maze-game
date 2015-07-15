package edu.nus.mazegame.server.main;

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

public class ServerMain {
	/**
	 * Entry point for the server
	 * **/
	public static final String thisClass = ServerMain.class.getName();
	private static Logger logger = Logger.getLogger(thisClass);
	
	public static void main(String args[]) {
		if(args.length != 2 && args.length != 0){
			throw new IllegalArgumentException("Server main program require two arguments");
		}
		//initialize all shared variable
		int dimension = args.length == 2 ? Integer.parseInt(args[0]) : 6;
		int treasure = args.length == 2 ? Integer.parseInt(args[1]) :10;
		
		IBoard board = new Board(dimension);
		List<IPlayer> playerList = Collections.synchronizedList(new LinkedList<IPlayer>());
		//BoardBuilder builder = new BoardBuilder(dimension, playerList);
		LocalContext context = new LocalContext();
		final Map<String, Integer> countDownTimeOutMap = new ConcurrentHashMap<String, Integer>();
		//Start main game thread
		GameStartThread thread = new GameStartThread(playerList, board, context, treasure, countDownTimeOutMap);
		thread.start();
		
		//register RMI remote interface... 
		registerRMIObject(board, playerList, context, thread, countDownTimeOutMap);
	}
	
	private static void registerRMIObject(IBoard board, List<IPlayer> playerList, LocalContext context, GameStartThread thread, Map<String, Integer> countDownTimeOutMap){
		ServerRemote stub = null;
		Registry registry = null;
		try {
			ServerRemote serverObj = new ServerRemoteImpl(board, playerList, context, thread, countDownTimeOutMap);
		    stub = (ServerRemote) UnicastRemoteObject.exportObject(serverObj, 0);
		    registry = LocateRegistry.createRegistry(RMIConstant.RMI_PORT);
		    registry.bind(RMIConstant.RMI_ID, stub);
		} catch (RemoteException re) {
		    try{
		    	registry.unbind(RMIConstant.RMI_ID);
		    	System.exit(0);
		
		    }catch(Exception ee){
		    	logger.log( Level.SEVERE, ee.toString(), ee);
		    	System.exit(0);
		    }
		} catch (Exception e) {
			logger.log( Level.SEVERE, e.toString(), e);
			System.exit(0);
		}
	}
}

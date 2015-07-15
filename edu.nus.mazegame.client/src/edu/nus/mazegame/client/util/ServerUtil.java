package edu.nus.mazegame.client.util;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.nus.mazegame.client.remote.InternalServerRemoteImpl;
import edu.nus.mazegame.client.remote.MainServerRemoteProxy;
import edu.nus.mazegame.client.thread.GameStartThread;
import edu.nus.mazegame.model.impl.Board;
import edu.nus.mazegame.model.impl.IpAddressInfo;
import edu.nus.mazegame.model.impl.LocalContext;
import edu.nus.mazegame.model.impl.SafePoint;
import edu.nus.mazegame.model.interf.IBoard;
import edu.nus.mazegame.model.interf.IPlayer;
import edu.nus.mazegame.model.interf.RMIConstant;
import edu.nus.mazegame.model.rmi.InternalServerRemote;
import edu.nus.mazegame.model.rmi.ServerRemote;

public class ServerUtil {
	/**
	 * Entry point for the server
	 * **/
	public static final String thisClass = ServerUtil.class.getName();
	private static Logger logger = Logger.getLogger(thisClass);

	/*
	 * generates a random point within given dimension
	 * */
	public static SafePoint getRandomPoint(int dimension) {
		Random random = new Random();
		SafePoint randomPoint = new SafePoint(random.nextInt(dimension),
				random.nextInt(dimension));
		return randomPoint;
	}

	/*
	 * starts the game server by
	 * 1. create and start a GameStartThread
	 * 2. register the sever RMI object
	 * */
	public static ServerRemote startGameServer(int dimension,
			IpAddressInfo serverAddressInfo) {
		IBoard board = new Board(dimension);
		List<IPlayer> playerList = Collections
				.synchronizedList(new LinkedList<IPlayer>());
		// BoardBuilder builder = new BoardBuilder(dimension, playerList);
		LocalContext context = new LocalContext();
		final Map<String, Integer> countDownTimeOutMap = new ConcurrentHashMap<String, Integer>();
		// Start main game thread
		GameStartThread thread = new GameStartThread(playerList, board,
				context, countDownTimeOutMap);
		thread.start();
		InternalServerRemote serverObj = new InternalServerRemoteImpl(board,
				playerList, context, thread, countDownTimeOutMap);
		// register RMI remote interface...
		registerRMIObject(serverObj, serverAddressInfo);
		logger.log(Level.INFO, "Start main server");
		return new MainServerRemoteProxy(serverObj);
	}

	/*
	 * registers RMI object with given address
	 * */
	public static void registerClientRMI(IpAddressInfo addressInfo) {
		InternalServerRemote serverObj = new InternalServerRemoteImpl();
		logger.log(Level.INFO, "register client RMI");
		registerRMIObject(serverObj, addressInfo);
	}

	/*
	 * registers RMI object with given object and address
	 * */
	private static void registerRMIObject(Remote object,
			IpAddressInfo addressInfo) {
		InternalServerRemote stub = null;
		Registry registry = null;
		try {
			stub = (InternalServerRemote) UnicastRemoteObject.exportObject(
					object, 0);
			registry = LocateRegistry.createRegistry(addressInfo.getPort());
			registry.bind(RMIConstant.RMI_ID, stub);
		} catch (RemoteException re) {
			try {
				logger.log(Level.SEVERE, re.toString(), re);
				registry.unbind(RMIConstant.RMI_ID);
				System.exit(0);

			} catch (Exception ee) {
				logger.log(Level.SEVERE, ee.toString(), ee);
				System.exit(0);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.toString(), e);
			System.exit(0);
		}
	}

	/*
	 * remove the given address from registered addresses storage
	 * */
	public static void removeAddressFromMap(IpAddressInfo addressInfo,
			Map<String, IpAddressInfo> addressMap) {
		Iterator<Map.Entry<String, IpAddressInfo>> iterator = addressMap
				.entrySet().iterator();
		while (iterator.hasNext()) {
			if (iterator.next().getValue().equals(addressInfo)) {
				iterator.remove();
			}
		}
	}
}

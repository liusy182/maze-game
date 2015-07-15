package edu.nus.mazegame.server.test;

import static org.junit.Assert.assertEquals;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;

import edu.nus.mazegame.model.enumeration.ResponseState;
import edu.nus.mazegame.model.impl.Player;
import edu.nus.mazegame.model.impl.ServerResponse;
import edu.nus.mazegame.model.interf.IPlayer;
import edu.nus.mazegame.model.interf.RMIConstant;
import edu.nus.mazegame.model.rmi.ServerRemote;

public class ServerRemoteImplTest {
	private static final Logger logger = Logger.getLogger(ServerRemoteImplTest.class.getName());
	private int dimension  = 6;
	@Before
	public void beforeTest() throws RemoteException, NotBoundException{
	}

	/*
	 * when only one player calls joins game
	 * */
	@Test
	public void testJoinGame_1() throws RemoteException, NotBoundException {
		ServerRemoteController.startServer(dimension, 10);
		Registry registry = LocateRegistry.getRegistry(RMIConstant.HOST_NAME,
				RMIConstant.RMI_PORT);
		ServerRemote remoteStub = (ServerRemote) registry.lookup(RMIConstant.RMI_ID);
		IPlayer player = new Player("player1", 1);
		ServerResponse sr = remoteStub.joinGame(player);
		assertEquals(sr.getResponseState(), ResponseState.NO_ENOUGH_PALYER);
		ServerRemoteController.stopServer();
	}
	
	/*
	 * when multiple players calls join game
	 * */
	@Test
	public void testJoinGame_2() throws RemoteException, NotBoundException, InterruptedException {
		ServerRemoteController.startServer(dimension, 10);
		ArrayList<IPlayer> players = new ArrayList<IPlayer>();
		ArrayList<Thread> cts = new ArrayList<Thread>();
		for(int i = 0; i < 5; i ++){
			IPlayer player = new Player("player" + i, i);
			players.add(player);
		}
		// for each player, create a thread for RMI communication
		for(IPlayer player : players){
			Thread ct = new Thread(new ClientThread(dimension, player, TestSwitch.JOIN_GAME));
			cts.add(ct);
			ct.start();
		}
		for(Thread thread : cts){
			thread.join();
		}
		ServerRemoteController.stopServer();
		//assertEquals(sr.getResponseState(), ResponseState.NO_ENOUGH_PALYER);
	}
	
	/*
	 * test join game is not allowed after 20 secs
	 * */
	@Test
	public void testJoinGame_3() throws RemoteException, NotBoundException, InterruptedException {
		ServerRemoteController.startServer(dimension, 10);
		ArrayList<IPlayer> players = new ArrayList<IPlayer>();
		ArrayList<Thread> cts = new ArrayList<Thread>();
		for(int i = 0; i < 3; i ++){
			IPlayer player = new Player("player" + i, i);
			players.add(player);
		}
		// for each player, create a thread for RMI communication
		for(IPlayer player : players){
			Thread ct = new Thread(new ClientThread(dimension, player, TestSwitch.JOIN_GAME));
			cts.add(ct);
			ct.start();
		}
		for(Thread thread : cts){
			thread.join();
		}
		
		// this player shoudn't be allowed to join the existing
		// game since the game has already started
		Registry registry = LocateRegistry.getRegistry(RMIConstant.HOST_NAME,
				RMIConstant.RMI_PORT);
		ServerRemote remoteStub = (ServerRemote) registry.lookup(RMIConstant.RMI_ID);
		IPlayer player = new Player("last_player", 3);
		ServerResponse sr = remoteStub.joinGame(player);
		assertEquals(sr.getResponseState(), ResponseState.JOIN_NOT_ALLOWED);
	
		ServerRemoteController.stopServer();
		//assertEquals(sr.getResponseState(), ResponseState.NO_ENOUGH_PALYER);
	}
	
	/*
	 * test move game with IsNoMove update
	 * */
	@Test
	public void testMove_1() throws RemoteException, NotBoundException, InterruptedException {
		ServerRemoteController.startServer(dimension, 10);
		ArrayList<IPlayer> players = new ArrayList<IPlayer>();
		ArrayList<Thread> cts = new ArrayList<Thread>();
		for(int i = 0; i < 5; i ++){
			IPlayer player = new Player("player" + i, i);
			players.add(player);
		}
		// for each player, create a thread for RMI communication
		for(IPlayer player : players){
			Thread ct = new Thread(new ClientThread(dimension, player, TestSwitch.NO_MOVE));
			cts.add(ct);
			ct.start();
		}
		for(Thread thread : cts){
			thread.join();
		}
		ServerRemoteController.stopServer();
		//assertEquals(sr.getResponseState(), ResponseState.NO_ENOUGH_PALYER);
	}
	
	/*
	 * test move game
	 * */
	@Test
	public void testMove_2() throws RemoteException, NotBoundException, InterruptedException {
		ServerRemoteController.startServer(dimension, 10);
		ArrayList<IPlayer> players = new ArrayList<IPlayer>();
		ArrayList<Thread> cts = new ArrayList<Thread>();
		for(int i = 0; i < 5; i ++){
			IPlayer player = new Player("player" + i, i);
			players.add(player);
		}
		// for each player, create a thread for RMI communication
		for(IPlayer player : players){
			Thread ct = new Thread(new ClientThread(dimension, player, TestSwitch.MOVE));
			cts.add(ct);
			ct.start();
		}
		for(Thread thread : cts){
			thread.join();
		}
		ServerRemoteController.stopServer();
		//assertEquals(sr.getResponseState(), ResponseState.NO_ENOUGH_PALYER);
	}
}

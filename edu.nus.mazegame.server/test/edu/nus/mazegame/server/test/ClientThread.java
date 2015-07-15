package edu.nus.mazegame.server.test;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;

import edu.nus.mazegame.model.enumeration.ResponseState;
import edu.nus.mazegame.model.impl.SafePoint;
import edu.nus.mazegame.model.impl.ServerResponse;
import edu.nus.mazegame.model.interf.IPlayer;
import edu.nus.mazegame.model.interf.RMIConstant;
import edu.nus.mazegame.model.rmi.ServerRemote;

public class ClientThread implements Runnable{
	
	private int dimension;
	private IPlayer player;
	private Registry registry;
	private ServerRemote remoteStub;
	private TestSwitch testSwitch = TestSwitch.JOIN_GAME;
	private static final Logger logger = Logger.getLogger(ServerRemoteImplTest.class.getName());
	
	public ClientThread(int dimension, IPlayer player, TestSwitch testSwitch) throws RemoteException, NotBoundException{
		registry = LocateRegistry.getRegistry(RMIConstant.HOST_NAME,
				RMIConstant.RMI_PORT);
		remoteStub = (ServerRemote) registry.lookup(RMIConstant.RMI_ID);
		this.player = player;
		this.testSwitch = testSwitch;
		this.dimension = dimension;
	}
	
	@Override
	@Test
	public void run() {
		try {
			ServerResponse sr = remoteStub.joinGame(player);
			logger.log(Level.INFO, String.valueOf(sr.getResponseState()));
			Assert.assertEquals(sr.getResponseState(), ResponseState.JOIN_SUCCESS);
			if(testSwitch == TestSwitch.NO_MOVE){
				sr = remoteStub.move(null, null, true);
				Assert.assertEquals(sr.getResponseState(), ResponseState.UPDATED);
			}
			else if(testSwitch == TestSwitch.MOVE){
				SafePoint curPoint = null;
				for(IPlayer p : sr.getPlayerList()){
					if(player.getName().equals(p.getName()))
						curPoint = p.getPoint();
				}
				SafePoint newPoint= null;
				if(curPoint.x + 1 < dimension)
					newPoint = new SafePoint(curPoint.x + 1, curPoint.y);
				else
					newPoint = new SafePoint(0, curPoint.y);
				sr = remoteStub.move(player, newPoint, false);
				Assert.assertEquals(sr.getResponseState(), ResponseState.UPDATED);
			}
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

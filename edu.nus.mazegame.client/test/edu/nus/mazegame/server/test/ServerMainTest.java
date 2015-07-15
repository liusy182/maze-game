package edu.nus.mazegame.server.test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;

import org.junit.Before;
import org.junit.Test;

import edu.nus.mazegame.client.util.ServerUtil;
import edu.nus.mazegame.model.impl.IpAddressInfo;


public class ServerMainTest {
	@Before
	public void prepare(){
		
	}
	
	@Test
	public void testRegisterMainServer() throws RemoteException {
		String currentIp = null;
		try {
			currentIp = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int port = 7623;
		IpAddressInfo mainServerIpAddressInfo = new IpAddressInfo(currentIp,port);
		
		int dimension = 5;
		
		ServerUtil.startGameServer(dimension, mainServerIpAddressInfo);
//		ServerResponse response = serverRemote.joinGame(new Player("AV", 1));
//		assertEquals(response.getResponseState(), ResponseState.NO_ENOUGH_PALYER);
		
		
	}

}

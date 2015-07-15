package edu.nus.mazegame.client.main;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

import javax.swing.SwingUtilities;

import edu.nus.mazegame.client.remote.ClientServerRemoteProxy;
import edu.nus.mazegame.client.session.GameSessionManager;
import edu.nus.mazegame.client.util.ServerUtil;
import edu.nus.mazegame.client.view.MazeGameFrame;
import edu.nus.mazegame.model.impl.IpAddressInfo;
import edu.nus.mazegame.model.rmi.ServerRemote;

public class ClientMain{
	public static final String IP_PORT_SEPERATOR = ":";
	
	/*
	 * program's entry point
	 * to start as server, pass in arguments "--server [port] [dimension] [treasure]"
	 * to start as client, pass in arguments "--client [server ip]:[server port]"
	 * */
	public static void main(String [] args) throws IOException { 
		if(args.length <2){
			throw new IllegalArgumentException("There is at least one arguments should be provided");
		}else if(args[0].equals("--server")){
			//add own IP into the ip:port list
			if(args[1] == null || args[2] == null || args[3] == null){
				throw new IllegalArgumentException("main server port must be provided for server mode");				
			}
			try {
				String currentIp = InetAddress.getLocalHost().getHostAddress();
				int port = Integer.parseInt(args[1]);
				IpAddressInfo mainServerIpAddressInfo = new IpAddressInfo(currentIp,port);
				
				int dimension = Integer.parseInt(args[2]);
				int treasureNumber = Integer.parseInt(args[3]);
				ServerRemote serverRemote = ServerUtil.startGameServer(dimension, mainServerIpAddressInfo);
				startClient(mainServerIpAddressInfo, mainServerIpAddressInfo, true, serverRemote, treasureNumber);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}	
			//Start server 
			// directly add Inner Server Remote to be concreted implementation rather than proxy
		}else if(args[0].equals("--client")){
			if(args[1] == null){
				throw new IllegalArgumentException("main server ip/port must be provided for client mode");				
			}
			//build server host:ip
			String[] ipPort= args[1].split(IP_PORT_SEPERATOR);
			IpAddressInfo mainServerIpAddressInfo = new IpAddressInfo(ipPort[0],Integer.parseInt(ipPort[1]));
			//build own host:ip
			String currentIp = InetAddress.getLocalHost().getHostAddress();
			ServerSocket s = null;
			IpAddressInfo thisAddressInfo = null;
			try{
				s = new ServerSocket(0);
				int port = s.getLocalPort();
				thisAddressInfo = new IpAddressInfo(currentIp,port);
			}finally{
				if(s!=null){
					s.close();
				}
			}
			ServerUtil.registerClientRMI(thisAddressInfo);
			/**
			 * Client not necessary to set treasure number to be correct value at this moment of time. 
			 * Main server will update it when it becomes to backup server
			 */
			startClient(thisAddressInfo, mainServerIpAddressInfo, false, new ClientServerRemoteProxy(), 0);
		}else{
			throw new IllegalArgumentException("Unsupported mode");
		}  
	}


	public static void startClient(final IpAddressInfo thisIpAddressInfo, final IpAddressInfo serverIpAddressInfo, final boolean isServer, final ServerRemote serverRemote, final int treasureNumber){
		SwingUtilities.invokeLater(new Runnable() {
		        @Override
		        public void run() {                
		        	MazeGameFrame gameFrame = new MazeGameFrame();
		            GameSessionManager.newGameSessionManager(gameFrame);
		            GameSessionManager.getInstance().setMainServerAddress(serverIpAddressInfo);
		            GameSessionManager.getInstance().setBackupServerAddress(null);
		            GameSessionManager.getInstance().setThisAddress(thisIpAddressInfo);
					GameSessionManager.getInstance().setMainServer(isServer);
					GameSessionManager.getInstance().setTreasure(treasureNumber);
					GameSessionManager.getInstance().setServerStub(serverRemote);		           
					gameFrame.setVisible(true);  
		        }
		    });
	}
}

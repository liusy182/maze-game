package edu.nus.mazegame.client.remote;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.nus.mazegame.client.session.GameSessionManager;
import edu.nus.mazegame.client.util.ClientUtil;
import edu.nus.mazegame.model.impl.InternalServerResponse;
import edu.nus.mazegame.model.impl.IpAddressInfo;
import edu.nus.mazegame.model.impl.SafePoint;
import edu.nus.mazegame.model.impl.ServerResponse;
import edu.nus.mazegame.model.interf.IPlayer;
import edu.nus.mazegame.model.rmi.InternalServerRemote;
import edu.nus.mazegame.model.rmi.ServerRemote;

/*
 * proxy remote object used by client.
 * */
public class ClientServerRemoteProxy implements ServerRemote {
	private InternalServerRemote mainServerRemote;
	private static final Logger logger = Logger.getLogger(ClientServerRemoteProxy.class.getName());
	@Override
	public ServerResponse joinGame(IPlayer player){
		IpAddressInfo mainServerAddress = GameSessionManager.getInstance().getMainServerAddress();
		if(mainServerRemote == null){
			updateServerRemoteInstance(mainServerAddress);
		}
		InternalServerResponse internalServerResponse =null;
		try {
			internalServerResponse = mainServerRemote.joinGame(player, GameSessionManager.getInstance().getThisAddress());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//update Address before return
		updateAddressInfo(internalServerResponse);
		return internalServerResponse.getServerResponse();
	}

	@Override
	public ServerResponse move(IPlayer player, SafePoint point, boolean isNoMove){
		ServerResponse serverResponse = null;
		InternalServerResponse internalServerResponse = null;
		GameSessionManager.getInstance().printDebugInfo(logger);
		try{
			updateServerRemoteInstance(GameSessionManager.getInstance().getMainServerAddress());	
			internalServerResponse = mainServerRemote.move(player, point, isNoMove);
		}catch(Exception e){
			//main server may be down.. let's try back up server
			//need to log main server down here
			IpAddressInfo backupServerAddress = GameSessionManager.getInstance().getBackupServerAddress();
			logger.log(Level.INFO, "mainServerRemote is not working.. try backup server " + backupServerAddress);
			updateServerRemoteInstance(backupServerAddress);	
			try {
				internalServerResponse = mainServerRemote.move(player, point, isNoMove);
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}			
		}
		if(internalServerResponse != null){ 
			//update Address before return
			updateAddressInfo(internalServerResponse);
			serverResponse = internalServerResponse.getServerResponse();
		}
		return serverResponse;
	}

	private void updateAddressInfo(InternalServerResponse internalServerResponse) {
		GameSessionManager session = GameSessionManager.getInstance();
		session.setIpAddressInfoMap(internalServerResponse.getPlayerAddressSet());
		session.setMainServerAddress(internalServerResponse.getMainServerAddress());
		session.setBackupServerAddress(internalServerResponse.getBackupServerAddress());
	}

	/**
	 * get RMI remote obj 
	 * @param backupServerAddress
	 */
	private void updateServerRemoteInstance(IpAddressInfo serverAddress) {
		try {
			mainServerRemote = ClientUtil.getInternalServerRemote(serverAddress);
	
		} catch (RemoteException | NotBoundException e) {
			//Logging error properly here .. if really can't help.. throw exception 
		}
	}

}

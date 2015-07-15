package edu.nus.mazegame.client.remote;

import java.rmi.RemoteException;

import edu.nus.mazegame.client.session.GameSessionManager;
import edu.nus.mazegame.model.impl.InternalServerResponse;
import edu.nus.mazegame.model.impl.IpAddressInfo;
import edu.nus.mazegame.model.impl.SafePoint;
import edu.nus.mazegame.model.impl.ServerResponse;
import edu.nus.mazegame.model.interf.IPlayer;
import edu.nus.mazegame.model.rmi.InternalServerRemote;
import edu.nus.mazegame.model.rmi.ServerRemote;

/*
 * proxy remote object used by server
 * */
public class MainServerRemoteProxy implements ServerRemote {

	private InternalServerRemote interServerRemote;
	
	public MainServerRemoteProxy(InternalServerRemote interServerRemote) {
		super();
		this.interServerRemote = interServerRemote;
	}

	@Override
	public ServerResponse joinGame(IPlayer player){
		IpAddressInfo ownIpAddress = GameSessionManager.getInstance().getMainServerAddress();
		InternalServerResponse internalResponse = null;
		try {
			internalResponse = interServerRemote.joinGame(player, ownIpAddress);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//All the list are already updated for main server.. so no need to do anything here
		return internalResponse.getServerResponse();
	}

	@Override
	public ServerResponse move(IPlayer player, SafePoint point, boolean isNoMove)
	{
		//Main sever no need to care anything.. just call move as it's not a remove call
		InternalServerResponse response = null;
		try {
			response = interServerRemote.move(player, point, isNoMove);
		} catch (RemoteException e) {
			
		}
		if(response == null || response.getServerResponse() == null){
			return null;
		}
		return response.getServerResponse() ;
	}

}

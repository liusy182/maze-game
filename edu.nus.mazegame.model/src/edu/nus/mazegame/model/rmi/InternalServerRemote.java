package edu.nus.mazegame.model.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

import edu.nus.mazegame.model.impl.HostDataDTO;
import edu.nus.mazegame.model.impl.InternalServerResponse;
import edu.nus.mazegame.model.impl.IpAddressInfo;
import edu.nus.mazegame.model.impl.SafePoint;
import edu.nus.mazegame.model.interf.IPlayer;

public interface InternalServerRemote extends Remote{
	InternalServerResponse joinGame(IPlayer player, IpAddressInfo ownIpAddress) throws RemoteException;
	InternalServerResponse move(IPlayer player, SafePoint point, boolean isNoMove) throws RemoteException;
	void updateBackupServer(HostDataDTO hostDataDTO) throws RemoteException;

	void autoPinging() throws RemoteException;
	boolean requestBackupStartup(int treasureNumber,
			IpAddressInfo newMainServer, Map<String,IpAddressInfo> ipAddressInfoSet,
			HostDataDTO hostDataDTO) throws RemoteException;
}

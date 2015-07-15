package edu.nus.mazegame.client.remote;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.nus.mazegame.client.session.GameSessionManager;
import edu.nus.mazegame.client.thread.BackupServerRunningListener;
import edu.nus.mazegame.client.thread.BackupServerRunningNotifier;
import edu.nus.mazegame.client.thread.GameStartThread;
import edu.nus.mazegame.client.util.ClientUtil;
import edu.nus.mazegame.model.enumeration.ResponseState;
import edu.nus.mazegame.model.impl.HostDataDTO;
import edu.nus.mazegame.model.impl.InternalServerResponse;
import edu.nus.mazegame.model.impl.IpAddressInfo;
import edu.nus.mazegame.model.impl.LocalContext;
import edu.nus.mazegame.model.impl.SafePoint;
import edu.nus.mazegame.model.impl.ServerResponse;
import edu.nus.mazegame.model.interf.IBoard;
import edu.nus.mazegame.model.interf.IPlayer;
import edu.nus.mazegame.model.rmi.InternalServerRemote;

/*
 * RMI object implementation
 * */
public class InternalServerRemoteImpl implements InternalServerRemote {
	private ServerRemoteImpl serverRemote;
	private InternalServerRemote backupServerStub;
	private static Object lock = new Object();
	private static final Logger logger = Logger
			.getLogger(InternalServerRemoteImpl.class.getName());

	public InternalServerRemoteImpl(IBoard board, List<IPlayer> playerList,
			LocalContext context, GameStartThread mainGameThread,
			Map<String, Integer> countDownTimeOutMap) {
		serverRemote = new ServerRemoteImpl(board, playerList, context,
				mainGameThread, countDownTimeOutMap);
	}

	// this constructor for client use
	public InternalServerRemoteImpl() {
	}

	/**
	 * This call will be only possible to be received by main server first join
	 * client will be request to be as backup server at this moment of time
	 */
	@Override
	public InternalServerResponse joinGame(IPlayer player,
			IpAddressInfo ownIpAddress) throws RemoteException {
		if (serverRemote == null)
			return null;
		GameSessionManager session = GameSessionManager.getInstance();
		// add ownIpAddress to the player Address map
		Map<String, IpAddressInfo> ipAddressMap = session.getIpAddressInfoMap();
		// this map will be concurrency update by caller here
		ipAddressMap.put(player.getKey(), ownIpAddress);
		ServerResponse response = serverRemote.joinGame(player);
		InternalServerResponse internalResponse = createInternalServerResponse(
				session, response);
		// logger.log(Level.INFO, internalResponse.toString());
		// start up the backup server timer thread
		synchronized (lock) {
			if (session.isBackUpServerListenerStarted() == false) {
				new BackupServerRunningListener(this).start();
				session.setBackUpServerListenerStarted(true);
			}
		}

		return internalResponse;
	}

	/**
	 * This call is possible to be received by both main/backup server, backup
	 * server is only possible to receive this call when main server crash and
	 * it not totally converted as main server in this case, backup server just
	 * process this call as normal
	 */
	@Override
	public InternalServerResponse move(IPlayer player, SafePoint point,
			boolean isNoMove) throws RemoteException {
		if (serverRemote == null) {
			return null;
		}
		GameSessionManager session = GameSessionManager.getInstance();
		ServerResponse response = serverRemote.move(player, point, isNoMove);
		InternalServerResponse internalResponse = createInternalServerResponse(
				session, response);
		if (internalResponse != null
				&& internalResponse.getServerResponse() != null
				&& internalResponse.getServerResponse().getResponseState() != null
				&& internalResponse.getServerResponse().getResponseState()
						.equals(ResponseState.GAME_OVER)) {
			return internalResponse;
		}
		// if this main server, need to update backup server data at this time
		if (GameSessionManager.getInstance().isMainServer()
				&& session.getBackupServerAddress() != null) {
			synchronized (InternalServerRemoteImpl.class) {
				if (backupServerStub == null) {
					try {
						backupServerStub = ClientUtil
								.getInternalServerRemote(session
										.getBackupServerAddress());
					} catch (NotBoundException e) {
						logger.log(Level.SEVERE, e.toString(), e);
						throw new RuntimeException(e);
					}
				}
				backupServerStub.updateBackupServer(getHostDataDTO());
			}

		}
		return internalResponse;
	}

	private InternalServerResponse createInternalServerResponse(
			GameSessionManager session, ServerResponse serverResponse) {
		InternalServerResponse internalResponse = new InternalServerResponse();
		internalResponse.setServerResponse(serverResponse);
		internalResponse.setPlayerAddressSet(session.getIpAddressInfoMap());
		internalResponse.setMainServerAddress(session.getMainServerAddress());
		internalResponse.setBackupServerAddress(session
				.getBackupServerAddress());
		return internalResponse;
	}

	/**
	 * This method will only be received by main server, purpose is to
	 * acknowledge backup server is still alive back up server lost counting
	 * down timer will be refreshed
	 */
	@Override
	public void autoPinging() throws RemoteException {
		// reset auto-ping time
		// logger.log(Level.INFO,"received backup server auto ping...");
		GameSessionManager.getInstance().setBackupServerPingTimer(
				BackupServerRunningListener.LISTEN_TIME);
	}

	/**
	 * This method will be received by any client server request to be backup
	 * server, backup server starting up logic is implemented in this method
	 * 
	 */
	@Override
	public boolean requestBackupStartup(int treasureNumber,
			IpAddressInfo newMainServer, Map<String,IpAddressInfo> ipAddressInfoSet,
			HostDataDTO hostDataDTO) throws RemoteException {
		GameSessionManager session = GameSessionManager.getInstance();
		session.setTreasure(treasureNumber);
		// synchronize address informations
		session.setIpAddressInfoMap(ipAddressInfoSet);
		GameSessionManager.getInstance().setMainServerAddress(newMainServer);

		serverRemote = new ServerRemoteImpl();
		boolean success = serverRemote.startupBackupServer(hostDataDTO);
		// start pinging main server
		InternalServerRemote mainServerRemote;
		try {
			mainServerRemote = ClientUtil
					.getInternalServerRemote(newMainServer);
			new BackupServerRunningNotifier(mainServerRemote, this).start();
			GameSessionManager.getInstance().setBackUpServer(true);
			logger.log(Level.INFO, "backup server started as main server.");
		} catch (NotBoundException e) {
			logger.log(Level.SEVERE, e.toString(), e);
			throw new RuntimeException(e);
		}

		return success;
	}

	/**
	 * @return the backupServerStub
	 */
	public synchronized InternalServerRemote getBackupServerStub() {
		return backupServerStub;
	}

	/**
	 * @param backupServerStub
	 *            the backupServerStub to set
	 */
	public synchronized void setBackupServerStub(
			InternalServerRemote backupServerStub) {
		this.backupServerStub = backupServerStub;
	}

	/**
	 * This method will only be received by backup server, all the data is
	 * passed by main server. use this service to update the data in backup
	 * server
	 */
	public void updateBackupServer(HostDataDTO hostDataDTO) {
		// TODO Auto-generated method stub
		serverRemote.updateLocalData(hostDataDTO);

	}

	public HostDataDTO getHostDataDTO() {
		return serverRemote.getHostData();
	}

}

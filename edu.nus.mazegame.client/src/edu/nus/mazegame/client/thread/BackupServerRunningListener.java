package edu.nus.mazegame.client.thread;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.nus.mazegame.client.remote.InternalServerRemoteImpl;
import edu.nus.mazegame.client.session.GameSessionManager;
import edu.nus.mazegame.client.util.ClientUtil;
import edu.nus.mazegame.model.impl.IpAddressInfo;
import edu.nus.mazegame.model.rmi.InternalServerRemote;

/*
 * This thread runs on the main server. it receives ping from backup server.
 * If it does not receive ping after a timeout, this thread will find a new
 * backup server from the client pool
 * */
public class BackupServerRunningListener extends Thread {
	public static final int LISTEN_TIME = 7; // 5 sec to ping host if no
	// movement happen
	private static final Logger logger = Logger
			.getLogger(ClientRunningNotifier.class.getName());

	private volatile Thread blinker;
	private InternalServerRemoteImpl owner;
	
	public BackupServerRunningListener(InternalServerRemoteImpl owner){
		this.owner = owner;
	}

	public void stopRun() {
		blinker = null;
	}

	@Override
	public void run() {
		Thread thisThread = Thread.currentThread();
		GameSessionManager session = GameSessionManager.getInstance();
		session.setBackupServerPingTimer(LISTEN_TIME);
		blinker = thisThread;
		while (blinker == thisThread) {
			try {
				Thread.sleep(1000);
				int currentTimer = session.getBackupServerPingTimer() - 1;
				//reduce the frequency to print timing
				//if(currentTimer%3 == 1){
				//	logger.log(Level.INFO, "Current Timer is " + currentTimer);
				//}
				session.setBackupServerPingTimer(currentTimer);
				if (currentTimer <= 0) {
					// get a new client ip from list
					session.printDebugInfo(logger);
					IpAddressInfo clientIp = getRandomClientIp();
					if (clientIp == null){
						// if can't find clientIp
						// means game is over.. just stop this thread
						stopRun();
						break;
					}
					
					// request it be as a back up server..
					InternalServerRemote internalServerRemote = ClientUtil
							.getInternalServerRemote(clientIp);
					logger.log(Level.INFO, "begin request backup server " + clientIp.toString());
					boolean success = internalServerRemote
							.requestBackupStartup(session.getTreasure(), session.getThisAddress(), session.getIpAddressInfoMap(), owner.getHostDataDTO());
					// once backup server up, update backup server ip address in
					// context
					if (success) {
						logger.log(Level.INFO, "end request backup server " + clientIp.toString());
						GameSessionManager.getInstance()
								.setBackupServerAddress(clientIp);
						owner.setBackupServerStub(ClientUtil.getInternalServerRemote(clientIp));
					}
					session.setBackupServerPingTimer(LISTEN_TIME);
				}
			} catch (InterruptedException e) {
				logger.log(Level.SEVERE, e.toString());
				stopRun();
			} catch (RemoteException e) {
				//Using client for back up server haven't been removed yet, waiting for 3 secs and try again
				session.setBackupServerPingTimer(3);
			} catch (NotBoundException e) {
				logger.log(Level.SEVERE, e.toString(), e);
				stopRun();
			}// end catch
		}// end while
	}// end run

	private IpAddressInfo getRandomClientIp() {
		GameSessionManager session = GameSessionManager.getInstance();
		IpAddressInfo mainServerAddr = session.getMainServerAddress();
		IpAddressInfo subServerAddr = session.getBackupServerAddress();
		IpAddressInfo tempAddr = null;
		Map<String, IpAddressInfo> ipAddSet = session.getIpAddressInfoMap();
		//if can't get ipAddset, means game is already over.
		//so just stop this thread
		if(ipAddSet == null){
			stopRun();
			return null;
		}
		for (IpAddressInfo info : ipAddSet.values()) {
			if (info.equals(mainServerAddr)) {
				continue;
			} else if (subServerAddr != null && info.equals(subServerAddr)) {
				continue;
			}
			tempAddr = info;
			break;
		}
		return tempAddr;
	}
	

}

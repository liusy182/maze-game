package edu.nus.mazegame.client.thread;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.nus.mazegame.client.remote.InternalServerRemoteImpl;
import edu.nus.mazegame.client.remote.MainServerRemoteProxy;
import edu.nus.mazegame.client.session.GameSessionManager;
import edu.nus.mazegame.client.util.ServerUtil;
import edu.nus.mazegame.model.rmi.InternalServerRemote;

/*
 * this thread runs on backup server. It pings main server to notify main server
 * of its existence. If exception happens during pining, it treats main server
 * as dead, and then promotes itself to be the main server.
 * */
public class BackupServerRunningNotifier extends Thread {
	public static final int AUTO_PING_TIME = 2;
	private static Logger logger = Logger
			.getLogger(BackupServerRunningNotifier.class.getName());
	private int timer;
	private volatile Thread blinker;
	private InternalServerRemote mainServerRemote;
	private InternalServerRemoteImpl owner;

	public BackupServerRunningNotifier(InternalServerRemote mainServerRemote, InternalServerRemoteImpl owner) {
		this.mainServerRemote = mainServerRemote;
		this.owner = owner;
	}

	public void stopRun() {
		blinker = null;
	}

	@Override
	public void run() {
		Thread thisThread = Thread.currentThread();
		timer = AUTO_PING_TIME;
		blinker = thisThread;
		while (blinker == thisThread) {
			try {
			//	logger.log(Level.INFO, "to ping main server in " + timer + " secs");
				Thread.sleep(1000);
				timer--;
				if (timer <= 0) {
					timer = AUTO_PING_TIME;
					mainServerRemote.autoPinging();	
				}
			} catch (InterruptedException e) {
				logger.log(Level.SEVERE, e.toString());
			} catch (RemoteException e) {
				GameSessionManager sessionManager = GameSessionManager.getInstance();
				//remove current main server Ip address from set..
				logger.log(Level.INFO, 
						"Main server down. Set " + sessionManager.getThisAddress().toString() + " as main server");
				
				ServerUtil.removeAddressFromMap(sessionManager.getMainServerAddress(), sessionManager.getIpAddressInfoMap());
				sessionManager.setBackUpServer(false);
				sessionManager.setMainServer(true);
				sessionManager.setMainServerAddress(sessionManager.getThisAddress());
				GameSessionManager.getInstance().setServerStub(new MainServerRemoteProxy(owner));
				
				//start backup listener
				new BackupServerRunningListener(owner).start();
				//yeah! I am main server now! no need to ping others again! send of this thread.. 
				stopRun();
			}
		}
	}
}

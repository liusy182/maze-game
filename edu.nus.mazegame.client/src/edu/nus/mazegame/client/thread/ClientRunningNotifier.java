package edu.nus.mazegame.client.thread;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import edu.nus.mazegame.client.session.GameSessionManager;
import edu.nus.mazegame.model.enumeration.ResponseState;
import edu.nus.mazegame.model.impl.ServerResponse;

/*
 * this thread runs on the client. It notifies server of its existence.
 * */
public class ClientRunningNotifier extends Thread {
	public static final int AUTO_PING_TIME = 5; // 5 sec to ping host if no
													// movement happen
	private static final Logger logger = Logger
			.getLogger(ClientRunningNotifier.class.getName());
	private JFrame gameFrame;
	private int timer;
	private volatile Thread blinker;

	public ClientRunningNotifier(JFrame gameFrame) {
		this.gameFrame = gameFrame;
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
				Thread.sleep(1000);
				timer--;
				if (timer <= 0) {
					ServerResponse response = GameSessionManager
							.getInstance()
							.getServerStub()
							.move(GameSessionManager.getInstance()
									.getCurrentPlayer(), null, true);
					if(response == null){
						//try again..
						timer = AUTO_PING_TIME;
						continue;
					}
					if (response.getResponseState().equals(
							ResponseState.GAME_OVER)) {
						JOptionPane.showMessageDialog(
								gameFrame,
								"Game over",
								"Game over", JOptionPane.INFORMATION_MESSAGE);
						GameSessionManager.getInstance().destorySession();
						break;
					}
					timer = AUTO_PING_TIME;
				}
			} catch (InterruptedException e){
				logger.log(Level.SEVERE, e.toString(), e);
				throw new RuntimeException(e);
			}
		}
	}
}

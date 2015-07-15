package edu.nus.mazegame.client.thread;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.nus.mazegame.client.session.GameSessionManager;
import edu.nus.mazegame.model.interf.IPlayer;

/*
 * this thread runs on the main server. It listens to ping from all clients.
 * If it does not receive ping from a client within a timeout, it will treat
 * that client as dead and remove that client from the pool.
 * */
public class ClientRunningListener extends Thread{

	private static Logger logger = Logger
			.getLogger(ClientRunningListener.class.getName());
	private GameStartThread thread;
	
	public ClientRunningListener(GameStartThread thread){
		this.thread = thread;
	}
	@Override
	public void run() {
		runCountingDownTimer();
	}
	
	private void runCountingDownTimer() {
		logger.log(Level.INFO, "Start running countingDown timer");
		try {
			while (true) {
				Thread.sleep(1000);
				// reset game back if less than 2 players available, reset game,
				// and stop this thread
				 Map<String, Integer> countDownTimeOutMap = thread.getCountDownTimeOutMap();
				if (countDownTimeOutMap.size() < 2) {
					logger.log(Level.FINE, "Less than two player, game overs");
					thread.resetGame();
					break;
				}
				Iterator<Map.Entry<String, Integer>> iterator = countDownTimeOutMap
						.entrySet().iterator();
				while (iterator.hasNext()) {
					Map.Entry<String, Integer> entry = iterator.next();
					int newValue = entry.getValue() - 1;
					logger.log(Level.FINE, entry.getKey() + "'s new timing is "
							+ newValue);
					if (newValue == 0) {
						// player crash or too long time no response!
						// remove it from list
						logger.log(Level.INFO, entry.getKey()
								+ " is out of time, to be removed");
						removePlayer(countDownTimeOutMap, entry);
					} else {
						entry.setValue(newValue);
					}// end if(newValue == 0)/else
				}// end map iterator loop
			}// end timer while loop
		} catch (InterruptedException e) {
			logger.log(Level.FINER, e.toString(), e);
		}
	}

	private void removePlayer(Map<String, Integer> countDownTimeOutMap,Map.Entry<String, Integer> entry) {
		countDownTimeOutMap.remove(entry.getKey());
		if(GameSessionManager.getInstance() !=null && GameSessionManager.getInstance().getIpAddressInfoMap()!= null){
			GameSessionManager.getInstance().getIpAddressInfoMap().remove(entry.getKey());
		}
		List<IPlayer> playerList = thread.getPlayerList();
		synchronized (playerList) {
			IPlayer tmpPlayer = null;
			for (IPlayer player : playerList) {
				if (player.getKey().equals(entry.getKey())) {
					tmpPlayer = player;
					break;
				}
			}
			if(tmpPlayer!=null){
				playerList.remove(tmpPlayer);
			}
		}
	}
}

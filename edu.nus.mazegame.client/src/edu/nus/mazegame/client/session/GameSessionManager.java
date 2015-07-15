package edu.nus.mazegame.client.session;

import java.awt.BorderLayout;
import java.awt.event.KeyListener;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.nus.mazegame.client.controller.MazeGameKeyHandler;
import edu.nus.mazegame.client.thread.ClientRunningNotifier;
import edu.nus.mazegame.client.view.GamePanel;
import edu.nus.mazegame.client.view.MazeGameFrame;
import edu.nus.mazegame.client.view.SummaryPanel;
import edu.nus.mazegame.model.impl.IpAddressInfo;
import edu.nus.mazegame.model.interf.IBoard;
import edu.nus.mazegame.model.interf.IPlayer;
import edu.nus.mazegame.model.rmi.ServerRemote;

/*
 * this class holds all run time information with regard to a game session
 * */
public class GameSessionManager {
	private static GameSessionManager instance = null;
//	private static final Logger logger = Logger.getLogger(GameSessionManager.class.getName());
	private MazeGameFrame gameFrame;
	private GamePanel gamePanel;
	private SummaryPanel summaryPanel;
	private KeyListener keyListener;

	private ClientRunningNotifier autoHostPingThread; 
	/*- New defined variables for peer to peer version -*/
	private boolean mainServer;
	private boolean backUpServer;
	private IpAddressInfo mainServerAddress;
	private IpAddressInfo backupServerAddress;
	private IpAddressInfo thisAddress;
	/**
	 * Current server proxy stored here, if it's main server, it will be main server proxy
	 * if this is client server, it will be client server proxy
	 * if this is back up server, the client proxy will be updated to main server proxy when main server crush
	 */
	private ServerRemote serverStub;
	/**
	 * Fixed input treasure number
	 */
	private int treasure;
	
	/**
	 * Considered the serverRemote may be called at the same moment I trying to set it
	 * so need to be protected by synchronized
	 * @param serverStub
	 */
	public synchronized void  setServerStub(ServerRemote serverStub) {
		this.serverStub = serverStub;
	}
	
	public synchronized ServerRemote getServerStub() {
		return serverStub;
	}

	private class GameSession{
		private IBoard board;
		private boolean backUpServerListenerStarted;
		private List<IPlayer> playerList;
		private IPlayer currentPlayer;
		//use a concurrent set to store all the ip address info
		private Map<String, IpAddressInfo> ipAddressInfoMap =new ConcurrentHashMap<String, IpAddressInfo>();
		private volatile int backupServerPingTimer;
	}
	
	private GameSessionManager(MazeGameFrame gameFrame){
		this.gameFrame = gameFrame;
		this.autoHostPingThread = new ClientRunningNotifier(gameFrame);
	}
	
	public static void newGameSessionManager(MazeGameFrame gameFrame){
		instance = new GameSessionManager(gameFrame);
	}
	
	public static GameSessionManager getInstance(){
		return instance;	
	}
	
	private GameSession gameSession = null;
	
	public void newGameSession(IPlayer currentPlayer) throws RemoteException, NotBoundException{
		gameSession = new GameSession();
		gameSession.currentPlayer = currentPlayer;
		initializeGame();
	}

	private void initializeGame() {
		autoHostPingThread.start();
		//create game board
		//set Frame Layout to BorderLayout to locate summary panel and game panel
		gameFrame.setLayout(new BorderLayout());
		summaryPanel = new SummaryPanel();
		gamePanel = new GamePanel();
		gameFrame.add(summaryPanel, BorderLayout.WEST);
		gameFrame.add(gamePanel, BorderLayout.CENTER);
		//register keyListener
		keyListener = new MazeGameKeyHandler();
		gameFrame.addKeyListener(keyListener);
		gamePanel.addKeyListener(keyListener);
		summaryPanel.addKeyListener(keyListener);
	}
	
	public void destorySession(){
		//destroy session
		gameSession = null;
		//unregister keyListener
		gameFrame.removeKeyListener(keyListener);
		gamePanel.removeKeyListener(keyListener);
		summaryPanel.removeKeyListener(keyListener);
		//remove panel component in the frame
		gameFrame.remove(gamePanel);
		gameFrame.remove(summaryPanel);
		//clear up the instance in the sessionManager
		gamePanel = null;
		summaryPanel = null;
		keyListener = null;
		autoHostPingThread.stopRun();
		//suggest JVM to do garbage collection to clean up 
		System.gc();
	}
	
	public void updateSession(IBoard board, List<IPlayer> playerList){		
		if(gameSession != null && board != null && playerList != null){
			gameSession.board = board;
			gameSession.playerList = playerList;
			for(IPlayer player : playerList){
				if(player.getKey().equals(gameSession.currentPlayer.getKey())){
					gameSession.currentPlayer = player;
					break;
				}
			}
			gamePanel.setBoardAndPlayerList(board, playerList);
			gamePanel.repaint();
			summaryPanel.redrawSummaryPanel(playerList);

			//adjust screen size
			gameFrame.pack(); 
			gameFrame.setResizable(false);
		}
	}

	public List<IPlayer> getPlayerList() {
		if(gameSession == null){
			return null;
		}
		return gameSession.playerList;
	}

	public IPlayer getCurrentPlayer() {
		if(gameSession == null){
			return null;
		}
		return gameSession.currentPlayer;
	}

	
	public IBoard getBoard(){
		if(gameSession == null){
			return null;
		}
		return gameSession.board;
	}
	
	/**
	 * @return the ipAddressInfoMap
	 */
	public Map<String, IpAddressInfo> getIpAddressInfoMap() {
		if(gameSession == null){
			return null;
		}
		return gameSession.ipAddressInfoMap;
	}
	
	/**
	 * @return the ipAddressInfoMap
	 */
	public void setIpAddressInfoMap(Map<String, IpAddressInfo> infoMap) {
		if(gameSession == null || infoMap == null){
			return;
		}
		gameSession.ipAddressInfoMap = infoMap;
	}

	/**
	 * @return the mainServer
	 */
	public boolean isMainServer() {
		return mainServer;
	}

	/**
	 * @param mainServer the mainServer to set
	 */
	public void setMainServer(boolean mainServer) {
		this.mainServer = mainServer;
	}

	/**
	 * @return the backUpServer
	 */
	public boolean isBackUpServer() {
		return backUpServer;
	}

	/**
	 * @param backUpServer the backUpServer to set
	 */
	public void setBackUpServer(boolean backUpServer) {
		this.backUpServer = backUpServer;
	}

	/**
	 * @return the mainServerAddress
	 */
	public IpAddressInfo getMainServerAddress() {
		return mainServerAddress;
	}

	/**
	 * @param mainServerAddress the mainServerAddress to set
	 */
	public void setMainServerAddress(IpAddressInfo mainServerAddress) {
		this.mainServerAddress = mainServerAddress;
	}

	/**
	 * @return the backupServerAddress
	 */
	public IpAddressInfo getBackupServerAddress() {
		return backupServerAddress;
	}

	/**
	 * @param backupServerAddress the backupServerAddress to set
	 */
	public void setBackupServerAddress(IpAddressInfo backupServerAddress) {
		this.backupServerAddress = backupServerAddress;
	}

	/**
	 * @return the thisAddress
	 */
	public IpAddressInfo getThisAddress() {
		return thisAddress;
	}

	/**
	 * @param thisAddress the thisAddress to set
	 */
	public void setThisAddress(IpAddressInfo thisAddress) {
		this.thisAddress = thisAddress;
	}

	/**
	 * @return the backupServerPingTimer
	 */
	public int getBackupServerPingTimer() {
		if(gameSession == null){
			return 0;
		}
		return gameSession.backupServerPingTimer;
	}
	/**
	 * @param backupServerPingTimer the backupServerPingTimer to set
	 */
	public void setBackupServerPingTimer(int backupServerPingTimer) {
		if(gameSession == null){
			return;
		}
		this.gameSession.backupServerPingTimer = backupServerPingTimer;
	}

	/**
	 * @return the treasure
	 */
	public int getTreasure() {
		return treasure;
	}

	/**
	 * @param treasure the treasure to set
	 */
	public void setTreasure(int treasure) {
		this.treasure = treasure;
	}
	
	/**
	 * @return the backUpServerListenerStarted
	 */
	public boolean isBackUpServerListenerStarted() {
		if(gameSession == null){
			return false;
		}
		return gameSession.backUpServerListenerStarted;
	}
	/**
	 * @param backUpServerListenerStarted the backUpServerListenerStarted to set
	 */
	public void setBackUpServerListenerStarted(
			boolean backUpServerListenerStarted) {
		if(gameSession != null){
			this.gameSession.backUpServerListenerStarted = backUpServerListenerStarted;
		}
	}
	
	public void printDebugInfo(Logger logger){
		StringBuilder sb = new StringBuilder("");
		printDebugLine(sb, "is Main Server", Boolean.toString(isMainServer()));
		printDebugLine(sb, "is Sub Server", Boolean.toString(isBackUpServer()));
		printDebugLine(sb, "Main Server Address", mainServerAddress.toString());
		printDebugLine(sb, "Sub Server Address", backupServerAddress == null? "null":backupServerAddress.toString());
		printDebugLine(sb, "Ip Address Set", "{");
		if(gameSession!=null){
			for(IpAddressInfo info : gameSession.ipAddressInfoMap.values() ){
				sb.append(info.toString()).append(",\n");
			}
		}else{
			sb.append("null");
		}
		sb.append("}\n");
		logger.log(Level.INFO, sb.toString());
	}
	
	private void printDebugLine(StringBuilder sb, String title, String value){
		sb.append(String.format("%-25s",title)).append(value).append("\n");
	}
}

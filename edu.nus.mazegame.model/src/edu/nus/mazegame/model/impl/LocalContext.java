package edu.nus.mazegame.model.impl;

import java.io.Serializable;

import edu.nus.mazegame.model.enumeration.GameState;
import edu.nus.mazegame.model.interf.IPlayer;

public class LocalContext implements Serializable {
	/**
	 * Generated Serial ID
	 */
	private static final long serialVersionUID = -2418513202190472254L;
	private GameState gameState = GameState.IDLE;
	private IPlayer player;
	private SafePoint newPoint;
	private ServerResponse finishTimeResponse;
	private int remainingTreasure;
	
	public synchronized GameState getGameState() {
		return gameState;
	}

	public synchronized void setGameState(GameState gameState) {
		if(gameState != null){
			this.gameState = gameState;
		}
	}
	
	public synchronized void reset(){
		gameState = GameState.IDLE;
		player = null;
		newPoint = null;
	}

	public synchronized IPlayer getPlayer() {
		return player;
	}

	public synchronized void setPlayer(IPlayer player) {
		this.player = player;
	}
	
	public synchronized SafePoint getNewPoint() {
		return newPoint;
	}

	public synchronized void setNewPoint(SafePoint newPoint) {
		this.newPoint = newPoint;
	}

	/**
	 * @return the finishTimeResponse
	 */
	public ServerResponse getFinishTimeResponse() {
		return finishTimeResponse;
	}

	/**
	 * @param finishTimeResponse the finishTimeResponse to set
	 */
	public void setFinishTimeResponse(ServerResponse finishTimeResponse) {
		this.finishTimeResponse = finishTimeResponse;
	}

	/**
	 * @return the remainingTreasure
	 */
	public int getRemainingTreasure() {
		return remainingTreasure;
	}

	/**
	 * @param remainingTreasure the remainingTreasure to set
	 */
	public void setRemainingTreasure(int remainingTreasure) {
		this.remainingTreasure = remainingTreasure;
	}
	

}

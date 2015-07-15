package edu.nus.mazegame.server.model;

import java.util.List;

import edu.nus.mazegame.model.enumeration.GameState;
import edu.nus.mazegame.model.impl.SafePoint;
import edu.nus.mazegame.model.interf.IBoard;
import edu.nus.mazegame.model.interf.IPlayer;

public class LocalContext {
	private GameState gameState = GameState.IDLE;
	private IPlayer player;
	private SafePoint newPoint;
	
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
}

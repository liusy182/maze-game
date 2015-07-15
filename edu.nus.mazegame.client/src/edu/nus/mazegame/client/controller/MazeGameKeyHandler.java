package edu.nus.mazegame.client.controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.nus.mazegame.client.session.GameSessionManager;
import edu.nus.mazegame.model.impl.SafePoint;
import edu.nus.mazegame.model.impl.ServerResponse;
import edu.nus.mazegame.model.interf.IPlayer;
import edu.nus.mazegame.model.rmi.ServerRemote;

public class MazeGameKeyHandler implements KeyListener {

	private static final Logger logger = Logger
			.getLogger(MazeGameMenuItemHandler.class.getName());
	
	 private long lastPressProcessed = 0;

	 /*
	  * key event handler (up, down, right, left)
	  * */
	@Override
	public void keyPressed(KeyEvent key) {
       if(System.currentTimeMillis() - lastPressProcessed < 330) {
            return;    
        } 
        lastPressProcessed = System.currentTimeMillis();
		int keyCode = key.getKeyCode();
		IPlayer currentPlayer = GameSessionManager.getInstance()
				.getCurrentPlayer();
		SafePoint currentPoint = currentPlayer.getPoint();
		switch (keyCode) {
		case KeyEvent.VK_UP:
			updateGame(new SafePoint(currentPoint.x, currentPoint.y - 1),
					currentPlayer);
			break;
		case KeyEvent.VK_DOWN:
			updateGame(new SafePoint(currentPoint.x, currentPoint.y + 1),
					currentPlayer);
			break;
		case KeyEvent.VK_LEFT:
			updateGame(new SafePoint(currentPoint.x - 1, currentPoint.y),
					currentPlayer);
			break;
		case KeyEvent.VK_RIGHT:
			updateGame(new SafePoint(currentPoint.x + 1, currentPoint.y),
					currentPlayer);
			break;
		default:
			// press other keys will not effect the game! so just return
			return;
		}
	}

	/**
	 * used by keyPressed() to update the maze game
	 */
	private void updateGame(SafePoint newPoint, IPlayer currentPlayer) {
		if (validateOutOfBoundary(newPoint) == false) {
			return;
		}
		ServerRemote stub = GameSessionManager.getInstance().getServerStub();
		logger.log(Level.FINE,"current player " + currentPlayer.getName() + "'s new point is " + newPoint.toString());
		ServerResponse response = null;
		response = stub.move(currentPlayer, newPoint, false);
		if(response!=null){
			GameSessionManager.getInstance().updateSession(response.getBoard(), response.getPlayerList());
		}
	}

	/*
	 * validate if the movement is within the given dimension
	 * */
	private boolean validateOutOfBoundary(SafePoint newPoint) {
		int dimension = GameSessionManager.getInstance().getBoard()
				.getDimension();
		if (newPoint.x >= dimension || newPoint.y >= dimension || newPoint.x < 0 || newPoint.y < 0) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void keyReleased(KeyEvent key) {
		// Nothing to do here
	}

	@Override
	public void keyTyped(KeyEvent key) {
		// Nothing to do here
	}

}

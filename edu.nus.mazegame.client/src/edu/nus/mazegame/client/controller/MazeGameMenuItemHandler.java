package edu.nus.mazegame.client.controller;

import java.awt.event.ActionEvent;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import edu.nus.mazegame.client.session.GameSessionManager;
import edu.nus.mazegame.client.view.JoinGameDialog;
import edu.nus.mazegame.model.enumeration.ResponseState;
import edu.nus.mazegame.model.impl.Player;
import edu.nus.mazegame.model.impl.ServerResponse;
import edu.nus.mazegame.model.interf.IBoard;
import edu.nus.mazegame.model.interf.IPlayer;
import edu.nus.mazegame.model.rmi.ServerRemote;

public class MazeGameMenuItemHandler {
	//Needs Frame to perform detail implemented Action
	private static Logger logger = Logger.getLogger(MazeGameMenuItemHandler.class.getName());
	
	/*
	 * gets the server stub and does joinGame.
	 * starts the game if join is successful
	 * display error message if join fails
	 * */
	public static void joinGameActionPerformed(ActionEvent e, JoinGameDialog dialog){
		try {
			String name = dialog.getNameField().getText();
			Integer characterNumber = (Integer) dialog.getCharacterComboBox()
					.getSelectedItem();
			IPlayer player = new Player(name, characterNumber.intValue());
			GameSessionManager sessionManager = GameSessionManager
					.getInstance();
			sessionManager.newGameSession(player);
			// request to join game..
			ServerRemote serverStub = sessionManager.getServerStub();
			ServerResponse response = serverStub.joinGame(player);
			if(response.getResponseState().equals(ResponseState.JOIN_SUCCESS) == false){
				JOptionPane.showMessageDialog(dialog, "Game not start due to " + response.getResponseState().toString());
				dialog.setVisible(false);
				
			}else{
				IBoard board = response.getBoard();
				List<IPlayer> playerList = response.getPlayerList();
				player = response.getCurrentPlayer();
				sessionManager.updateSession(board, playerList);
				dialog.setVisible(false);
			}
		} catch (RemoteException e1) {
			logger.log(Level.SEVERE, e1.toString(), e1);
			// join game fail.. need to clear up the session
			GameSessionManager.getInstance().destorySession();
			// show up error message on the screen
			JOptionPane.showMessageDialog(dialog,
				    "error message: " + e1.toString() ,
				    "Join game error",
				    JOptionPane.ERROR_MESSAGE);
			dialog.setVisible(false);
		} catch (NotBoundException e1) {
			logger.log(Level.SEVERE, e1.toString(), e1);
			// join game fail.. need to clear up the session
			GameSessionManager.getInstance().destorySession();
			// show up error message on the screen
			JOptionPane.showMessageDialog(dialog,
				    "error message: " + e1.toString() ,
				    "Join game error",
				    JOptionPane.ERROR_MESSAGE);
		}

	
	}

}
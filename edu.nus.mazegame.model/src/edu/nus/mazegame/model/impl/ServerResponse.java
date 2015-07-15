package edu.nus.mazegame.model.impl;

import java.io.Serializable;
import java.util.*;

import edu.nus.mazegame.model.enumeration.ResponseState;
import edu.nus.mazegame.model.interf.*;
/**
 * This object contains all the required fields for the client program
 * @author Tang Hao
 */
public class ServerResponse implements Serializable{
	/**
	 * Generated Serialized Number
	 */
	private static final long serialVersionUID = -5683297537466385057L;
	private IBoard board;
	private IPlayer currentPlayer;
	private List<IPlayer> playerList;
	private ResponseState response;
	
	public IBoard getBoard() {
		return board;
	}
	
	public void setBoard(IBoard board) {
		this.board = board;
	}

	public List<IPlayer> getPlayerList() {
		return playerList;
	}
	
	public void setPlayerList(List<IPlayer> playerList) {
		this.playerList = playerList;
	}
	
	public ResponseState getResponseState() {
		return response;
	}

	public void setResponseState(ResponseState response) {
		this.response = response;
	}

	public IPlayer getCurrentPlayer() {
		return currentPlayer;
	}

	public void setCurrentPlayer(IPlayer currentPlayer) {
		this.currentPlayer = currentPlayer;
	}
}

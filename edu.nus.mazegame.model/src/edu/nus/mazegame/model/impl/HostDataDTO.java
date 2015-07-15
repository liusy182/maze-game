package edu.nus.mazegame.model.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import edu.nus.mazegame.model.interf.IBoard;
import edu.nus.mazegame.model.interf.IPlayer;

public class HostDataDTO implements Serializable{
	/**
	 * Generated Serial ID
	 */
	private static final long serialVersionUID = -2289974963140581369L;
	
	private IBoard board;
	private List<IPlayer> playerList;
	private LocalContext context;
	private Map<String, Integer> countDownTimeOutMap;
	
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
	public LocalContext getContext() {
		return context;
	}
	public void setContext(LocalContext context) {
		this.context = context;
	}
	public Map<String, Integer> getCountDownTimeOutMap() {
		return countDownTimeOutMap;
	}
	public void setCountDownTimeOutMap(Map<String, Integer> countDownTimeOutMap) {
		this.countDownTimeOutMap = countDownTimeOutMap;
	}
}

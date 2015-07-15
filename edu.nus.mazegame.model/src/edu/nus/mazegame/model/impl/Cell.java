package edu.nus.mazegame.model.impl;

import java.io.Serializable;

import edu.nus.mazegame.model.interf.ICell;

/**
 * Detailed cell implementation
 * this only contains treasure number of the cell in this version
 * @author Hao
 */
public class Cell implements ICell, Serializable{
	/**
	 * Generated serial Id
	 */
	private static final long serialVersionUID = -7123555625484234053L;
	private int treasureNumber;
	public Cell(int treasureNumber){
		this.treasureNumber = treasureNumber; 
	}
	
	public synchronized int getTreasureNumber() {
		return treasureNumber;
	}

	public synchronized void setTreasureNumber(int treasureNumber) {
		this.treasureNumber = treasureNumber;
	}

	
}

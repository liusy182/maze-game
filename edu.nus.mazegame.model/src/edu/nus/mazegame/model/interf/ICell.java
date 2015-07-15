package edu.nus.mazegame.model.interf;

import java.io.Serializable;


public interface ICell extends Serializable{
	public int getTreasureNumber();
	public void setTreasureNumber(int treasureNumber);
}

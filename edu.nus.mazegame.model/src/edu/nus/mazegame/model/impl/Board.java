package edu.nus.mazegame.model.impl;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.*;

import edu.nus.mazegame.model.interf.*;

/**
 * Entire board object implementation. contains the all the cell details without player information
 * the Player information is maintained in the list of player separately
 * @author Hao
 *
 */
public class Board implements IBoard, Serializable{
	/**
	 * Generated serial Id
	 */
	private static final long serialVersionUID = 5420270934283587432L;
	/*
	 * key = coordinate
	 * value = treasure number
	 * */
	private Map<SafePoint, ICell> cells = new ConcurrentHashMap<SafePoint, ICell>();
	private int dimension;
	
	public Board(int dimension){
		this.dimension = dimension;
		reinitialize();
	}
	
	@Override
	public int getDimension() {
		return dimension;
	}

	@Override
	public Map<SafePoint, ICell> getCells() {
		return Collections.unmodifiableMap(cells);
	}
	
	public String toString(){
		StringBuilder builder = new StringBuilder("");
		for(int i = 0; i < dimension ; i++ ){
			for(int j = 0; j < dimension ; j++ ){
				SafePoint point = new SafePoint(j, i);
				builder.append("| ").append(cells.get(point).getTreasureNumber()).append(" |");
			}
			builder.append("\n");
		}
		return builder.toString();
	}

	@Override
	public void reinitialize() {
		for (int x = 0; x < dimension; x++) {
			for (int y = 0; y < dimension; y++) {
				cells.put(new SafePoint(x, y), new Cell(0));
			}// end y loop
		}// end x loop
	}
}

package edu.nus.mazegame.server.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import edu.nus.mazegame.client.util.GameBuilder;
import edu.nus.mazegame.model.impl.Board;
import edu.nus.mazegame.model.impl.SafePoint;
import edu.nus.mazegame.model.interf.IBoard;
import edu.nus.mazegame.model.interf.ICell;
import edu.nus.mazegame.model.interf.IPlayer;

public class BoardBuilderTest {
	
	private List<IPlayer> playerList;
	private int dimension;
	private int totalTreasure;
	
	@Before
	public void before(){
		playerList = DummyDataFactory.getInstance().getPlayerList(14);
		dimension = 4;
		totalTreasure = 100;
	}
	
	@Test
	public void test(){
		IBoard board = new Board(dimension);
		GameBuilder builder = new GameBuilder(board, playerList);
		builder.build(totalTreasure);
		assertEquals(dimension, board.getDimension());
		assertEquals(dimension*dimension, board.getCells().size());
		Set<SafePoint> keySet = board.getCells().keySet();
		//assert if map has correct points 
		for(int i = 0 ; i < dimension ; i++){
			for(int j = 0 ; j < dimension ; j++){
				assertTrue(keySet.contains(new SafePoint(i, j)));
			}
		}
		//assert if total number of treasures in the map cells are correct
		int tmpTreasure = 0;
		for(ICell cell : board.getCells().values()){
			tmpTreasure += cell.getTreasureNumber();
		}
		assertEquals(totalTreasure, tmpTreasure);
		
		for(IPlayer player : playerList){
			for(SafePoint point: keySet){
				if(board.getCells().get(point).getTreasureNumber()!=0){
					assertFalse(player.getPoint().equals(point));	
				}
			}
		}
		
		System.out.println(board.toString());
		for(IPlayer player : playerList){
			System.out.println(player.getPoint().toString());
		}
	}
}

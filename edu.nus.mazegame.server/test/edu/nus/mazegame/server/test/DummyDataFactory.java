package edu.nus.mazegame.server.test;

import java.util.ArrayList;
import java.util.List;

import edu.nus.mazegame.model.impl.Player;
import edu.nus.mazegame.model.interf.IPlayer;

public class DummyDataFactory {

	private static DummyDataFactory instance;
	
	private  DummyDataFactory(){
		
	} 
	
	public static DummyDataFactory getInstance(){
		if(instance == null){
			instance = new DummyDataFactory();
		}
		return instance;
	}
	
	public List<IPlayer> getPlayerList(int num){
		List<IPlayer> playerList = new ArrayList<IPlayer>(num);
		for(int i = 0; i< num; i++){
			playerList.add(new Player("player" + (1 + num), num));
		}
		return playerList;
	}
	
}

package edu.nus.mazegame.model.impl;

import java.io.Serializable;
import java.util.UUID;

import edu.nus.mazegame.model.interf.IPlayer;


/**
 * this contains all the information about a player 
 * <br/>
 * This class this thread safe.
 * @author Tang Hao
 *
 */
public class Player implements IPlayer, Serializable {
	/**
	 * generated serial Id
	 */
	private static final long serialVersionUID = -1044688381408397985L;

	/**
	 * key field of player, will be auto-generated and initialized time, and be immutable after initialized
	 */
	private String key;

	/**
	 * name of each player, will be immutable after initialized
	 */
	private String name;
	
	/**
	 * score of this player got
	 */
	private int score = 0;
	
	/**
	 * Current point of this player located
	 */
	private SafePoint point = null;
	
	/**
	 * marked character number to identify the character image selected. only for UI display purpose
	 */
	private int character;
	
	/**
	 * name and character for the player is required to construct a player object 
	 * the key field of each instance will be generated at the player initialize time
	 * which is the identification of the object. this field is read only
	 * @param name
	 * @param character
	 */
	public Player(String name, int character){
		this.name = name;
		this.character = character;
		this.key = UUID.randomUUID().toString();
	}
	
	private Player(String name, int character, String key){
		this.name = name;
		this.character = character;
		this.key = key;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public synchronized void setScoreAndPoint(int score, SafePoint point) {
		this.score = score;
		this.point = point;
	}
	
	@Override
	public int getCharacter() {
		return character;
	}
	
	@Override
	public String getKey() {
		return key;
	}

	@Override
	public synchronized SafePoint getPoint() {
		return point;
	}

	@Override
	public synchronized int getScore() {
		return score;
	}
	
	public IPlayer deepCopy(){
		return new Player(this.name, this.character, this.key);
	}
	

}

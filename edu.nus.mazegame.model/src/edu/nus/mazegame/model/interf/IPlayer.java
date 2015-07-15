package edu.nus.mazegame.model.interf;

import java.io.Serializable;

import edu.nus.mazegame.model.impl.SafePoint;

/**
 * Interface of the player model
 * @author Hao
 *
 */
public interface IPlayer extends Serializable{
	/**
	 * get player name
	 * @return
	 */
	public String getName() ;
	
	/**
	 * get the player score
	 * @return
	 */
	public int getScore(); 
	
	/**
	 * set score and point at same time to guarantee the thread safe. this method should provide lock
	 * @param score
	 * @param point
	 */
	public void setScoreAndPoint(int score, SafePoint point);
	
	/**
	 * get player's character number, this method must be locked to guarantee thread safe
	 * @return character number of player
	 */
	public int getCharacter();
	
	/**
	 * get the SafePoint of player, this method must be locked to guarantee thread safe
	 * @return current point of player stand
	 */
	public SafePoint getPoint();
	/**
	 * get key field of player
	 * @return key string generated from UUID
	 */
	public String getKey();
	
	/**
	 * Deep copy current player instance 
	 * @return
	 */
	public IPlayer deepCopy();
}

package edu.nus.mazegame.model.impl;

import java.io.Serializable;

/**
 * the coordinate of each cell
 * <br/>
 * this class is immutable
 * @author Tang Hao
 *
 */
public class SafePoint implements Serializable{

	private static final long serialVersionUID = 189033735449826126L;
	final public int x;
	final public int y;
	
	public SafePoint(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	@Override
	public boolean equals(Object object){
		if(object instanceof SafePoint){
			SafePoint safePoint = (SafePoint)object;
			return equals(safePoint);
		}
		return false;
	}
	
	private boolean equals(SafePoint safePoint){
		if(this.x == safePoint.x && this.y == safePoint.y){
			return true;
		}else{
			return false;
		}
	}
	
	@Override
	public int hashCode(){
		return x*31 + 7*y;
	}
	
	public String toString(){
		StringBuilder builder = new StringBuilder("");
		builder.append("[").append(x).append(",").append(y).append("]");
		return builder.toString();
	}
}

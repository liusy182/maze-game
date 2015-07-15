package edu.nus.mazegame.server.util;

import java.util.Random;

import edu.nus.mazegame.model.impl.SafePoint;

public class ServerUtil{	
	public static SafePoint getRandomPoint(int dimension){
		Random random = new Random();
		SafePoint randomPoint = new SafePoint(random.nextInt(dimension),
				random.nextInt(dimension));
		return randomPoint;
	}
}

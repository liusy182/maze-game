package edu.nus.mazegame.model.interf;

import java.io.Serializable;
import java.util.*;

import edu.nus.mazegame.model.impl.SafePoint;


public interface IBoard extends Serializable{
	Map<SafePoint, ICell> getCells();
	int getDimension();
	void reinitialize();
}

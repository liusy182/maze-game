package edu.nus.mazegame.model.rmi;

import java.rmi.Remote;

import edu.nus.mazegame.model.impl.SafePoint;
import edu.nus.mazegame.model.impl.ServerResponse;
import edu.nus.mazegame.model.interf.IPlayer;

public interface ServerRemote extends Remote{
	ServerResponse joinGame(IPlayer player);
	ServerResponse move(IPlayer player, SafePoint point, boolean isNoMove);
}

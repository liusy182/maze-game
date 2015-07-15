package edu.nus.mazegame.client.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.nus.mazegame.model.impl.SafePoint;
import edu.nus.mazegame.model.interf.IBoard;
import edu.nus.mazegame.model.interf.ICell;
import edu.nus.mazegame.model.interf.IPlayer;

/*
 * builds the game with given playerList, board and treasure number by randomly assigning the 
 * position of the players and treasures
 * */
public class GameBuilder {
	private IBoard board;
	private List<IPlayer> playerList;

	// private static final Logger logger =
	// Logger.getLogger(GameBuilder.class.getName());

	public GameBuilder(IBoard board, List<IPlayer> playerList) {
		super();
		this.board = board;
		this.playerList = playerList;
	}

	public void build(int totalTreasure) {
		synchronized (GameBuilder.class) {
			addPlayerLocation();
			assignTreasureNumber(totalTreasure);
		}

	}

	/*
	 * given a list of cells, and a list of players, returns a list of players
	 * with location on the board
	 */
	private void addPlayerLocation() {
		Set<SafePoint> occupiedPoints = new HashSet<SafePoint>();
		for (IPlayer player : playerList) {
			SafePoint randomPoint = null;
			do {
				randomPoint = ServerUtil.getRandomPoint(board.getDimension());
			} while (occupiedPoints.contains(randomPoint));
			player.setScoreAndPoint(0, randomPoint);
			occupiedPoints.add(randomPoint);
		}
	}

	private void assignTreasureNumber(int totalTreasure) {
		outer: while (totalTreasure > 0) {
			SafePoint randomPoint = ServerUtil.getRandomPoint(board
					.getDimension());
			for (IPlayer p : playerList) {
				if (p.getPoint().equals(randomPoint)) {
					continue outer;
				}
			}
			ICell cell = board.getCells().get(randomPoint);
			int newNumber = cell.getTreasureNumber() + 1;
			cell.setTreasureNumber(newNumber);
			totalTreasure--;
		}
	}
}

package edu.nus.mazegame.client.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

import edu.nus.mazegame.client.util.ClientUtil;
import edu.nus.mazegame.model.impl.SafePoint;
import edu.nus.mazegame.model.interf.IBoard;
import edu.nus.mazegame.model.interf.IPlayer;

public class GamePanel extends JPanel {


	/**
	 * Generated Serial Number
	 */
	private static final long serialVersionUID = 4305414605298848627L;
	
	public static final int UNIT_WIDTH = 60;
	public static final int UNIT_HEIGHT = 60;
	public static final int OFFSET_WIDTH = 5;
	public static final int OFFSET_HEIGHT = 5;
	public static final String BACKGROUND_IMAGE_PATH = "/pictures/background.jpg"; 
	public static final String CHARACTER_IMAGE_BASE_PATH = "/pictures/character"; 
	public static final String TREASURE_PATH = "/pictures/treasure.png"; 	
	public static final int CHARACTER_X_OFFSET = 5;
	public static final int CHARACTER_Y_OFFSET = 5;
	public static final int CHARACTER_WIDTH = 40;
	public static final int CHARACTER_HEIGHT = 40;
	public static final int CHARACTER_NAME_X_OFFSET = 5;
	public static final int CHARACTER_NAME_Y_OFFSET = CHARACTER_Y_OFFSET + CHARACTER_HEIGHT + 5 ;
	public static final int TREASURE_X_OFFSET = 0;
	public static final int TREASURE_Y_OFFSET = 0;
	public static final int TREASURE_WIDTH = 40;
	public static final int TREASURE_HEIGHT = 40;
	public static final int TREASURE_NUM_X_OFFSET = TREASURE_WIDTH + 1;
	public static final int TREASURE_NUM_Y_OFFSET = TREASURE_Y_OFFSET + 30;
	private IBoard board;
	private List<IPlayer> playerList;
	
	
	public GamePanel(){
		Border outline = BorderFactory.createLineBorder(Color.DARK_GRAY);		 
		setBorder(outline);
	}
	
	public void setBoardAndPlayerList(IBoard board, List<IPlayer> playerList){
		if(board != null){
			int dimension = board.getDimension();
			setPreferredSize(new Dimension(dimension*(UNIT_WIDTH+OFFSET_WIDTH)  , dimension*(UNIT_HEIGHT+OFFSET_HEIGHT)));
			this.board = board;
		}
		if(playerList != null){
			this.playerList = playerList;
		}
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		setBackground(Color.GREEN);
		 Font font = new Font("Serif", Font.PLAIN, 18);   
		 g.setColor(Color.WHITE);
		 g.setFont(font);
		//Don't print anything if it's a null board!!
		if(board == null || playerList == null){
			return;
		}
		//draw cells
		for(SafePoint point : board.getCells().keySet()){
			int treasureNumber = board.getCells().get(point).getTreasureNumber();
			printCell(point, treasureNumber, g);	
		}

		//draw players
		for(IPlayer player : playerList){
			printPlayer(player, g);
		}
		
	}

	private void printCell(SafePoint point, int treasureNumber, Graphics g) {
		Image backgroundImg = ClientUtil.getImage(BACKGROUND_IMAGE_PATH);
		int[] location = new int[] {point.x, point.y};
		//draw Background
		int unitX = location[0]*(UNIT_WIDTH + OFFSET_WIDTH);
		int unitY = location[1]*(UNIT_HEIGHT + OFFSET_HEIGHT); 
		g.drawImage(backgroundImg, unitX, unitY, UNIT_WIDTH, UNIT_HEIGHT, null);
		//draw player, if available

		//draw treasure, if available
		if(treasureNumber!= 0){
			Image treasureImage = ClientUtil.getImage(TREASURE_PATH);
			g.drawImage(treasureImage, unitX+TREASURE_X_OFFSET, unitY + TREASURE_Y_OFFSET, TREASURE_WIDTH, TREASURE_HEIGHT, null);
			String treasureNumberString = "" + treasureNumber;
			g.drawString(treasureNumberString, unitX+TREASURE_NUM_X_OFFSET, unitY+ TREASURE_NUM_Y_OFFSET);
		}
	}
	
	private void printPlayer(IPlayer player, Graphics g){
		SafePoint point = player.getPoint();
		int[] location = new int[] { point.x, point.y };
		int unitX = location[0] * (UNIT_WIDTH + OFFSET_WIDTH);
		int unitY = location[1] * (UNIT_HEIGHT + OFFSET_HEIGHT);
		String playerName = player.getName();
		int characterId = player.getCharacter();
		String playerCharacterPath = CHARACTER_IMAGE_BASE_PATH + characterId
				+ ".png";
		Image playerImage = ClientUtil.getImage(playerCharacterPath);
		g.drawImage(playerImage, unitX + CHARACTER_X_OFFSET, unitY
				+ CHARACTER_Y_OFFSET, CHARACTER_WIDTH, CHARACTER_HEIGHT, null);
		g.drawString(playerName, unitX + CHARACTER_NAME_X_OFFSET, unitY
				+ CHARACTER_NAME_Y_OFFSET);
	}
}

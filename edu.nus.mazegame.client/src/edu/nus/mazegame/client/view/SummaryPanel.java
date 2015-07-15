package edu.nus.mazegame.client.view;

import java.awt.Color;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Group;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import edu.nus.mazegame.model.interf.IPlayer;

public class SummaryPanel extends JPanel {

	/**
	 * Summary Panel 
	 */
	private static final long serialVersionUID = 7438188669206796317L;
	private GroupLayout layout;
	private JLabel [][] labelMap;
	
	private JLabel[][] buildLabelMap(List<IPlayer> playerList){
		final int numberOfPlayer = playerList.size();
		final int rowSize = numberOfPlayer + 1;
		final int columnSize = 2;
		//Create Title Label
		JLabel [] titleLabel = new JLabel[]{new JLabel("player"), new JLabel("score")};
		//fill in data based on the playerList
		JLabel[][] labelMap = new JLabel[rowSize][columnSize];
		labelMap[0] = titleLabel;
		for(int i= 0; i< numberOfPlayer ; i++){
			JLabel [] labelRow = new JLabel[columnSize];
			labelRow[0] = new JLabel(playerList.get(i).getName());
			labelRow[1] = new JLabel("" + playerList.get(i).getScore());
			labelMap[i+1] = labelRow;
 		}
		return labelMap;
	}
	
	
	private void updateLabelMap(List<IPlayer> playerList){
		final int numberOfPlayer = playerList.size();	
		for(int i= 0; i< numberOfPlayer ; i++){
			JLabel [] labelRow = labelMap[i+1];
			labelRow[1].setText(Integer.toString(playerList.get(i).getScore()));
			labelMap[i+1] = labelRow;
 		}
	}
	
	public SummaryPanel(){
		Border outline = BorderFactory.createLineBorder(Color.DARK_GRAY);		
		this.setBorder(outline);
		layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
	}
	
	public void redrawSummaryPanel(List<IPlayer> playerList){
		if(labelMap == null){
			JLabel[][] labelMap = buildLabelMap(playerList);
			this.labelMap = labelMap;
			layout.setHorizontalGroup(createHorizontalGroup(layout, labelMap));
			layout.setVerticalGroup(createVerticalGroup(layout, labelMap));
		
		}else{
			updateLabelMap(playerList);
		}
	}
	
	
	private Group createVerticalGroup(GroupLayout layout, JLabel[][] labelMap ){
		SequentialGroup sequentialGroup = layout.createSequentialGroup();
		for(int row = 0; row < labelMap.length; row++){
			ParallelGroup parallelGroup = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
			sequentialGroup.addGroup(parallelGroup);
			for(int col = 0 ; col < labelMap[0].length; col ++){
				parallelGroup.addComponent(labelMap[row][col]);
			}
		}
		return sequentialGroup;
	}
	
	private Group createHorizontalGroup(GroupLayout layout, JLabel[][] labelMap){
		SequentialGroup sequentialGroup = layout.createSequentialGroup();
		for(int col = 0; col < labelMap[0].length; col++){
			ParallelGroup parallelGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
			sequentialGroup.addGroup(parallelGroup);
			for(int row = 0 ; row < labelMap.length; row ++){
				parallelGroup.addComponent(labelMap[row][col]);
			}
		}
		return sequentialGroup;
	}

}

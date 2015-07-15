package edu.nus.mazegame.client.view;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JFrame;

public class MazeGameFrame extends JFrame {
	/**
	 * Generated Serial Number
	 */
	private static final long serialVersionUID = -3210076651910142128L;
	private ResourceBundle bundle = ResourceBundle
			.getBundle("mazeGameFrame", new Locale("en"));

	public MazeGameFrame() {
		initialize();
	}
	
	public void reset(){
	//	initialize();
		//	removeAll();	
		repaint();
	}
	
	private void initialize(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setTitle(bundle.getString("FRAME_TITLE"));	
		//Menu Is added here!!
		setSize(300, 200);
		setJMenuBar(new MazeGameMenuBar(this));
	}
}

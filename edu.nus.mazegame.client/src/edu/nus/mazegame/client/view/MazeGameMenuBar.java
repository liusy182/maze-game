package edu.nus.mazegame.client.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class MazeGameMenuBar extends JMenuBar {

	/**
	 *  Generated serial Version ID
	 */
	private static final long serialVersionUID = -6207339946721598268L;
	private JFrame owner;
	private ResourceBundle bundle = ResourceBundle
			.getBundle("mazeGameMenuBar", new Locale("en"));
	
	public MazeGameMenuBar(MazeGameFrame mazeGameFrame) {
		this.owner = mazeGameFrame;
		JMenu fileMenu = new JMenu(bundle.getString("FILE_MENU"));
		fileMenu.add(createMenuItem(bundle.getString("JOIN_GAME_MENU_ELEMENT"), new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				JDialog joinExistingGameDialog  = new JoinGameDialog(owner);
				joinExistingGameDialog.setVisible(true);
			}
		}));
		this.add(fileMenu);
	}

	private JMenuItem createMenuItem(String name, ActionListener listener){
		JMenuItem jMenuItem = new JMenuItem(name);
		jMenuItem.addActionListener(listener);
		return jMenuItem;
	}

}

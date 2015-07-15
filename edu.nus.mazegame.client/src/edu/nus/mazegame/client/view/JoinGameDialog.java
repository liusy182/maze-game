/*
 * Created by JFormDesigner on Wed Aug 27 00:22:17 SGT 2014
 */

package edu.nus.mazegame.client.view;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;

import edu.nus.mazegame.client.controller.MazeGameMenuItemHandler;


/**
 * @author hao tang
 */
public class JoinGameDialog extends JDialog {
	/**
	 * Generated Serial ID
	 */
	private static final long serialVersionUID = 4855126564483050427L;
	private final Integer[] characterList = {1,2,3,4,5,6,7,8};
	public JoinGameDialog(Frame owner) {
		super(owner,"New Game Dialog", true);
		initComponents();
	}
	
	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - hao tang
	private JLabel nameFieldLabel;
	private JTextField nameField;
	private JButton joinGameButton;
	private JComboBox<Integer> characterComboBox;
	private JLabel characterComboBoxLabel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	public JoinGameDialog(Dialog owner) {
		super(owner,"New Game Dialog", true);
		initComponents();
	}
	
	public JTextField getNameField() {
		return nameField;
	}

	public JComboBox<Integer> getCharacterComboBox() {
		return characterComboBox;
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - hao tang
		nameFieldLabel = new JLabel();
		nameField = new JTextField();
		joinGameButton = new JButton();
		characterComboBox = new JComboBox<Integer>(characterList);
		characterComboBoxLabel = new JLabel();

		//======== this ========
		setTitle("Join Game");
		Container contentPane = getContentPane();

		//---- label1 ----
		nameFieldLabel.setText("Enter your name...");

		//---- button1 ----
		final JoinGameDialog thisDialog = this;
		
		joinGameButton.setText("join game");
		joinGameButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MazeGameMenuItemHandler.joinGameActionPerformed(e, thisDialog);
			}
		});

		//---- label2 ----
		characterComboBoxLabel.setText("select your character");

		GroupLayout contentPaneLayout = new GroupLayout(contentPane);
		contentPane.setLayout(contentPaneLayout);
		contentPaneLayout.setHorizontalGroup(
			contentPaneLayout.createParallelGroup()
				.addGroup(contentPaneLayout.createSequentialGroup()
					.addGroup(contentPaneLayout.createParallelGroup()
						.addGroup(contentPaneLayout.createSequentialGroup()
							.addGap(140, 140, 140)
							.addComponent(joinGameButton))
						.addGroup(contentPaneLayout.createSequentialGroup()
							.addGap(68, 68, 68)
							.addGroup(contentPaneLayout.createParallelGroup()
								.addComponent(nameFieldLabel, GroupLayout.PREFERRED_SIZE, 113, GroupLayout.PREFERRED_SIZE)
								.addComponent(characterComboBoxLabel))
							.addGap(35, 35, 35)
							.addGroup(contentPaneLayout.createParallelGroup()
								.addComponent(characterComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(nameField, GroupLayout.PREFERRED_SIZE, 74, GroupLayout.PREFERRED_SIZE))))
					.addContainerGap(84, Short.MAX_VALUE))
		);
		contentPaneLayout.setVerticalGroup(
			contentPaneLayout.createParallelGroup()
				.addGroup(contentPaneLayout.createSequentialGroup()
					.addGap(60, 60, 60)
					.addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(nameFieldLabel, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
						.addComponent(nameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(characterComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(characterComboBoxLabel))
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
					.addComponent(joinGameButton)
					.addGap(36, 36, 36))
		);
		pack();
		setLocationRelativeTo(getOwner());

		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}
}

/*
 * Created by JFormDesigner on Tue Aug 26 23:04:10 SGT 2014
 */

package edu.nus.mazegame.client.view;

import java.awt.*;
import javax.swing.*;

/**
 * @author hao tang
 */
public class MessageDialog extends JDialog {
	/**
	 * Generated UID
	 */
	private static final long serialVersionUID = -5308059020135701227L;


	public MessageDialog(Frame owner) {
		super(owner);
		initComponents();
	}

	public MessageDialog(Dialog owner) {
		super(owner);
		initComponents();
	}
	
	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - hao tang
		label1 = new JLabel();

		//======== this ========
		Container contentPane = getContentPane();

		//---- label1 ----
		label1.setText("Waiting for connecting host...");
		label1.setHorizontalAlignment(SwingConstants.CENTER);

		GroupLayout contentPaneLayout = new GroupLayout(contentPane);
		contentPane.setLayout(contentPaneLayout);
		contentPaneLayout.setHorizontalGroup(
			contentPaneLayout.createParallelGroup()
				.addGroup(contentPaneLayout.createSequentialGroup()
					.addGap(42, 42, 42)
					.addComponent(label1, GroupLayout.PREFERRED_SIZE, 286, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(56, Short.MAX_VALUE))
		);
		contentPaneLayout.setVerticalGroup(
			contentPaneLayout.createParallelGroup()
				.addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
					.addContainerGap(74, Short.MAX_VALUE)
					.addComponent(label1)
					.addGap(69, 69, 69))
		);
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - hao tang
	private JLabel label1;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
	public JLabel getLabel1() {
		return label1;
	}
}

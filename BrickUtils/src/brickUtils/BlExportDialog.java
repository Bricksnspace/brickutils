/*
	Copyright 2013-2014 Mario Pascucci <mpascucci@gmail.com>
	This file is part of BrickUtils

	BrickUtils is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	BrickUtils is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with BrickUtils.  If not, see <http://www.gnu.org/licenses/>.

*/


package brickUtils;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


public class BlExportDialog extends JDialog implements ActionListener {

	
	private static final long serialVersionUID = 6241796433157781436L;
	private Container mainPane;
	private JButton okButton;
	private JButton cancelButton;
	private JTextField blList;
	private JCheckBox blQuery;
	private JCheckBox blNotify;
	private JCheckBox blQty;
	private JComboBox<String> blCondition;
	private int userChoice = JOptionPane.CANCEL_OPTION;

	
	BlExportDialog(Frame owner) {
		
		super(owner,"Bricklink export options",true);
		createDialog();
	}
	
	
	private void createDialog() {
		
		setLocationByPlatform(true);
		mainPane = getContentPane();
		mainPane.setLayout(new BorderLayout(2,2));
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		mainPane.add(buttonPane, BorderLayout.SOUTH);

		blQty = new JCheckBox("Include quantity");
		blQty.setSelected(Brick.isBlQty());
		blNotify = new JCheckBox("Enable notification");
		blNotify.setSelected(Brick.isBlNotify());
		blQuery = new JCheckBox("Include in queries");
		blQuery.setSelected(Brick.isBlQueries());
		blList = new JTextField(Brick.getBlListId());
		blCondition = new JComboBox<String>();
		blCondition.addItem("Don't care");
		blCondition.addItem("New");
		blCondition.addItem("Used");
		blCondition.setSelectedIndex(0);

		JPanel pane = new JPanel();
		pane.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.ipady = 2;
		gbc.ipadx = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;

		gbc.gridx = 0;
		gbc.gridy = 0;
		
		// list id
		pane.add(new JLabel("List ID (empty for main):",SwingConstants.RIGHT),gbc);
		gbc.gridx = 1;
		pane.add(blList,gbc);

		// status
		gbc.gridx = 0;
		gbc.gridy += 1;
		pane.add(new JLabel("Part condition:",SwingConstants.RIGHT),gbc);
		
		gbc.gridx = 1;
		pane.add(blCondition,gbc);
		
		// quantity
		gbc.gridy += 1;
		gbc.gridx = 0;
		pane.add(blQty,gbc);
		
		// notify
		gbc.gridx = 1;
		pane.add(blNotify,gbc);
		
		// includes in queries
		gbc.gridx = 0;
		gbc.gridy += 1;
		pane.add(blQuery,gbc);
		
		mainPane.add(pane, BorderLayout.CENTER);
		
		okButton = new JButton("OK");
		buttonPane.add(okButton);
		okButton.addActionListener(this);
		getRootPane().setDefaultButton(okButton);

		cancelButton = new JButton("Cancel");
		buttonPane.add(cancelButton);
		cancelButton.addActionListener(this);
		
		pack();

	}
	
	
	
	public int getResponse() {
		return userChoice;
	}
	
	
	@Override
	public void actionPerformed(ActionEvent ev) {
		
		if (ev.getSource() == okButton) {
			setVisible(false);
			userChoice = JOptionPane.OK_OPTION;
			Brick.setBlQty(blQty.isSelected());
			Brick.setBlNotify(blNotify.isSelected());
			Brick.setBlQueries(blQuery.isSelected());
			Brick.setBlListId(blList.getText());
			switch (blCondition.getSelectedIndex()) {
			case 0:
			case -1:
				Brick.setBlCondition(0);
				break;
			case 1:
				Brick.setBlCondition(1);
				break;
			case 2:
				Brick.setBlCondition(2);
				break;
			}
		}
		else if (ev.getSource() == cancelButton) {
			setVisible(false);
			userChoice = JOptionPane.CANCEL_OPTION;
		}
	}
	
	
	
}

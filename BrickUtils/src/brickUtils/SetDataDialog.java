/*
	Copyright 2013-2014 Mario Pascucci <mpascucci@gmail.com>
	This file is part of BrickUtils.

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
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import bricksnspace.bricklinklib.BlCategoryCombo;
import bricksnspace.bricklinklib.BlSetSearch;
import bricksnspace.bricklinklib.BricklinkCategory;
import bricksnspace.bricklinklib.BricklinkSet;

public class SetDataDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 7775648471110890863L;
	
	private JTextField setid;
	private JComboBox<String> type;
	private JTextField name;
	private BlCategoryCombo category;
	private JTextField year;
	private JTextField notes;
	private JButton okButton;
	private JButton cancelButton;

	private JButton btnBlSetSearch;

	private int userChoice = JOptionPane.CANCEL_OPTION;

	private JCheckBox available;


	public SetDataDialog(Frame owner, String title, boolean modal) {
		super(owner, title, modal);
		
		initialize();
	}

	public SetDataDialog(Dialog owner, String title, boolean modal) {
		super(owner, title, modal);
		
		initialize();
	}

	
	private void initialize() {
		
		setLocationByPlatform(true);
		getContentPane().setLayout(new BorderLayout());
		JPanel contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(4, 4));
		
		JPanel userPane = new JPanel();
		contentPanel.add(userPane, BorderLayout.SOUTH);
		GridBagLayout gbl = new GridBagLayout();
		userPane.setLayout(gbl);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.ipady = 2;
		gbc.ipadx = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;

		// set official #
		JLabel setidLabel = new JLabel("Official set id: ",SwingConstants.RIGHT);
		setid = new JTextField();
		gbc.gridx = 0;
		gbc.gridy = 0;
		userPane.add(setidLabel,gbc);
		gbc.gridx = 1;
		userPane.add(setid,gbc);
		
		// set type
		JLabel typeLabel = new JLabel("Type: ",SwingConstants.RIGHT);
		type = new JComboBox<String>();
		type.addItem("Official set");
		type.addItem("Generic Lot");
		type.addItem("MOC (built)");
		gbc.gridx = 0;
		gbc.gridy = 1;
		userPane.add(typeLabel,gbc);
		gbc.gridx = 1;
		userPane.add(type,gbc);
		type.addActionListener(this);
		
		// set name
		JLabel nameLabel = new JLabel("Set name: ",SwingConstants.RIGHT);
		name = new JTextField();
		gbc.gridx = 0;
		gbc.gridy = 2;
		userPane.add(nameLabel,gbc);
		gbc.gridx = 1;
		userPane.add(name,gbc);
		
		// set category
		JLabel catLabel = new JLabel("Category: ",SwingConstants.RIGHT);
		category = new BlCategoryCombo(BlCategoryCombo.BL_CAT_SET);
		gbc.gridx = 0;
		gbc.gridy = 3;
		userPane.add(catLabel,gbc);
		gbc.gridx = 1;
		userPane.add(category,gbc);
		
		// set year
		JLabel yearLabel = new JLabel("Year: ",SwingConstants.RIGHT);
		year = new JTextField();
		gbc.gridx = 0;
		gbc.gridy = 4;
		userPane.add(yearLabel,gbc);
		gbc.gridx = 1;
		userPane.add(year, gbc);
		
		// set notes
		JLabel notesLabel = new JLabel("Notes: ",SwingConstants.RIGHT);
		notes = new JTextField();
		gbc.gridx = 0;
		gbc.gridy = 5;
		userPane.add(notesLabel, gbc);
		gbc.gridx = 1;
		userPane.add(notes, gbc);
		
		// available for building
		available = new JCheckBox("Set is available for new builds");
		available.setSelected(true);
		gbc.gridx = 1;
		gbc.gridy = 6;
		userPane.add(available, gbc);
		
		
		// search set button
		btnBlSetSearch = new JButton("Search...");
		btnBlSetSearch.addActionListener(this);
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.NONE;
		userPane.add(btnBlSetSearch, gbc);
		
		// ok-cancel panel
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

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
	
	
	public void setData() throws SQLException {
		
		BrickSet bs;
		bs = BrickSet.getCurrent();
		if (bs == null)
			return;
		setid.setText(bs.setid);
		name.setText(bs.name);
		if (bs.type == BrickSet.OFFICIAL_SET) 
			type.setSelectedIndex(0);
		else if (bs.type == BrickSet.GENERIC_LOT)
			type.setSelectedIndex(1);
		else if (bs.type == BrickSet.MOC)
			type.setSelectedIndex(2);
		year.setText(Integer.toString(bs.year));
		notes.setText(bs.notes);
		available.setSelected(bs.available);
		category.selectByCatId(bs.catid);
		
	}
	
	
	public BrickSet getData() throws SQLException {
		
		
		BrickSet bs;
		
		bs = BrickSet.getCurrent();
		if (bs == null) {
			bs = new BrickSet();
		}
		bs.setid = setid.getText();
		bs.name = name.getText();
		switch (type.getSelectedIndex()) {
		case 0:
			bs.type = BrickSet.OFFICIAL_SET;
			break;
		case 1:
			bs.type = BrickSet.GENERIC_LOT;
			break;
		case 2:
			bs.type = BrickSet.MOC;
		}
		try {
			bs.year = Integer.parseInt(year.getText());
		} catch (NumberFormatException e) {
			bs.year = 0;
		}
		bs.category = ((BricklinkCategory)category.getSelectedItem()).getName();
		bs.catid = ((BricklinkCategory)category.getSelectedItem()).getCatid();
		bs.notes = notes.getText();
		bs.available = available.isSelected();
		return bs;
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == btnBlSetSearch) {
			BlSetSearch dlg = new BlSetSearch(this, "Search in Bricklink set database", true);
			dlg.setVisible(true);
			if (dlg.getResponse() == JOptionPane.OK_OPTION) {
				BricklinkSet bs = dlg.getSelected();
				if (bs != null) {
					name.setText(bs.getName());
					setid.setText(bs.getSetid());
					year.setText(Integer.toString(bs.getYear()));
					category.selectByCatId(bs.getCatid());
				}
			}
		}
		else if (e.getSource() == type) {
			if (type.getSelectedIndex() == 2) {
				// it is a built MOC, bricks are "unavailable" by definition
				available.setEnabled(false);
			}
			else {
				available.setEnabled(true);
			}
		}
		else if (e.getSource() == okButton) {
			userChoice = JOptionPane.OK_OPTION;
			setVisible(false);
		}
		else if (e.getSource() == cancelButton) {
			userChoice = JOptionPane.CANCEL_OPTION;
			setVisible(false);
		}
	}

}

/*
	Copyright 2013-2017 Mario Pascucci <mpascucci@gmail.com>
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
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.JTextField;

import bricksnspace.ldrawlib.LDrawLib;
import bricksnspace.ldrawlib.LDrawPart;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LdrSearch extends JDialog implements ActionListener, ListSelectionListener {


	private static final long serialVersionUID = 1490804630522544972L;
	private final JPanel contentPanel = new JPanel();
	private JTable table;
	private LdrTableModel tableModel;
	private JTextField searchText;
	private JPanel userPane;
	private JButton button;
	private JButton okButton;
	private JButton cancelButton;
	private int userChoice = JOptionPane.CLOSED_OPTION;
	private BrickShapePanel brickShape;
	private LDrawLib ldrawlib;
	private JScrollPane scrollPane;
	private JRadioButton onlyDeleted;
	private JRadioButton onlyNew;
	private JCheckBox autoConvert;
	private JLabel isLddOk;
	private JLabel isBlOk;
	private Brick convertedBrick;
	private ImageIcon shapeOk;
	private ImageIcon shapeLike;
	private ImageIcon shapeUnkn;
	private JRadioButton byId;
	private JRadioButton byText;

	/**
	 * Create the dialog.
	 * @param owner SWING parent of dialog
	 * @param title title for dialog
	 * @param modal if dialog is modal
	 * @param ldr a LDrawLib object to get LDraw part shape
	 * @param part if != null, dialog offers a search result for this id on display   
	 */
	public LdrSearch(JFrame owner, String title, boolean modal, LDrawLib ldr) {
		
		super(owner,title,modal);
		
		ldrawlib = ldr;
		
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(4, 4));
		
		scrollPane = new JScrollPane();
		
		contentPanel.add(scrollPane, BorderLayout.CENTER);

		tableModel = new LdrTableModel();
		table = new JTable(tableModel);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.setAutoCreateRowSorter(true);
		table.getSelectionModel().addListSelectionListener(this);
		TableColumnModel tcm = table.getColumnModel();
		tcm.getColumn(0).setPreferredWidth(60);
		tcm.getColumn(1).setPreferredWidth(200);
		tcm.getColumn(2).setPreferredWidth(100);
		tcm.getColumn(3).setPreferredWidth(100);
		tcm.getColumn(4).setPreferredWidth(40);
		//tcm.getColumn(6).setPreferredWidth(50);
		
		userPane = new JPanel();
		contentPanel.add(userPane, BorderLayout.SOUTH);
		GridBagLayout gbl = new GridBagLayout();
		userPane.setLayout(gbl);

		byId = new JRadioButton(" By LDraw ID");

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.ipady = 2;
		gbc.ipadx = 2;

		gbc.gridx = 0;
		gbc.gridy = 0;
		userPane.add(byId, gbc);
		
		byText = new JRadioButton(" By generic text");
		gbc.gridx = 0;
		gbc.gridy = 1;
		userPane.add(byText, gbc);
		
		searchText = new JTextField();
		searchText.setColumns(15);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridheight = 2;
		userPane.add(searchText, gbc);
		searchText.addActionListener(this);
		
		button = new JButton("Search...");
		gbc.gridx = 2;
		gbc.gridy = 0;
		userPane.add(button, gbc);
		button.addActionListener(this);

		gbc.gridx = 3;
		gbc.gridy = 0;
		userPane.add(Box.createHorizontalGlue(),gbc);

		gbc.weightx = 0.0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 4;
		gbc.gridy = 0;
		gbc.gridheight = 9;
		gbc.gridwidth = 1;
		brickShape = new BrickShapePanel(ldrawlib);
		userPane.add(brickShape,gbc);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		shapeOk = new ImageIcon(brickUtils.class.getResource("images/ok.png"));
		shapeLike = new ImageIcon(brickUtils.class.getResource("images/no.png"));
		
		onlyDeleted = new JRadioButton(" Deleted Parts");
		onlyDeleted.setSelected(false);
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridheight = 1;
		userPane.add(onlyDeleted,gbc);
		
		onlyNew = new JRadioButton(" New Parts");
		onlyNew.setSelected(false);
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridheight = 1;
		userPane.add(onlyNew,gbc);
		
		ButtonGroup bgroup = new ButtonGroup();
		bgroup.add(byId);
		bgroup.add(byText);
		bgroup.add(onlyDeleted);
		bgroup.add(onlyNew);
		byId.setSelected(true);
		
		autoConvert = new JCheckBox(" Convert to LDD-Bricklink codes");
		autoConvert.setSelected(false);
		autoConvert.addActionListener(this);
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.gridheight = 1;
		gbc.gridwidth = 2;
		userPane.add(autoConvert,gbc);
		gbc.gridwidth = 1;
		
		JLabel convertedLdd = new JLabel("LDD:");
		convertedLdd.setHorizontalAlignment(JLabel.RIGHT);
		gbc.gridx = 0;
		gbc.gridy = 6;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		userPane.add(convertedLdd,gbc);
		JLabel convertedBl = new JLabel("BLink:");
		convertedBl.setHorizontalAlignment(JLabel.RIGHT);
		gbc.gridx = 0;
		gbc.gridy = 7;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		userPane.add(convertedBl,gbc);

		shapeUnkn = new ImageIcon(brickUtils.class.getResource("images/off.png"));
		
		isLddOk = new JLabel(shapeUnkn);
		isLddOk.setText("-");
		isLddOk.setHorizontalAlignment(JLabel.LEFT);
		gbc.gridx = 1;
		gbc.gridy = 6;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		userPane.add(isLddOk,gbc);
		isBlOk = new JLabel(shapeUnkn);
		isBlOk.setText("-");
		isBlOk.setHorizontalAlignment(JLabel.LEFT);
		gbc.gridx = 1;
		gbc.gridy = 7;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		userPane.add(isBlOk,gbc);

		convertedBrick = new Brick();

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
		
		scrollPane.setPreferredSize(new Dimension(700,200));
		scrollPane.setViewportView(table);
		
		pack();
	}
	
	
	public void setPart(String part) {
			
		if (part != null) {
			searchText.setText(part);
			doSearch();
			if (tableModel.getRowCount() > 0)
				table.setRowSelectionInterval(0, 0);
			else 
				brickShape.setLdrawid("");
		}
		else 
			brickShape.setLdrawid("");
		searchText.requestFocusInWindow();
	}
	

	@Override
	public void actionPerformed(ActionEvent ev) {
		
		if (ev.getSource() == button || ev.getSource() == searchText) {
			table.getRowSorter().setSortKeys(null);
			doSearch();
		}
		else if (ev.getSource() == okButton) {
			userChoice = JOptionPane.OK_OPTION;
			table.getRowSorter().setSortKeys(null);
			setVisible(false);
		}
		else if (ev.getSource() == cancelButton) {
			userChoice = JOptionPane.CANCEL_OPTION;
			table.getRowSorter().setSortKeys(null);
			setVisible(false);
		}
		else if (ev.getSource() == autoConvert) {
			if (autoConvert.isSelected()) {
				int row = table.getSelectedRow();
				if (row < 0)
					return;
				ListSelectionEvent l = new ListSelectionEvent(table, row, row, false);
				valueChanged(l);
			}
			else {
				isLddOk.setIcon(shapeUnkn);
				isLddOk.setText("-");
				isBlOk.setIcon(shapeUnkn);
				isBlOk.setText("-");
			}
		}

	}

	
	public int getResponse() {
		return userChoice;
	}
	
	
	public boolean convertIds() {
		return autoConvert.isSelected();
	}
	
	
	public void setAutoconvert(boolean set) {
		
		autoConvert.setSelected(set);
	}
	
	
	public LDrawPart getSelected() {
		if (table.getSelectedRow() >= 0) {
			return tableModel.getPart(table.convertRowIndexToModel(table.getSelectedRow()));
		}
		return null;
	}

	
	public Brick getConvertedBrick() {
		
		return convertedBrick;
	}
	

	private void doSearch() {
		
		String query;
		
		if (byId.isSelected() && searchText.getText().length() > 0) {
			// search by id with "like"
			try {
				query = "ldrid like '%"+searchText.getText()+"%'";
				ArrayList<LDrawPart> pl = ldrawlib.getLdrDB().get(query,1000);
				tableModel.setParts(pl);
				scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMinimum());
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(this, 
						"Error retrieving LDraw parts from database.\n"+e.getLocalizedMessage(), 
						"Database error", JOptionPane.ERROR_MESSAGE);
				Logger.getGlobal().log(Level.SEVERE,"LDraw search error",e);
				return;
			}
		}
		else if (byText.isSelected() && searchText.getText().length() > 0) {
			ArrayList<LDrawPart> pl;
			try {
				pl = ldrawlib.getLdrDB().getFTS(searchText.getText(),1000);
				tableModel.setParts(pl);
				scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMinimum());
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(this, 
						"Error retrieving LDraw parts from database.\n"+e.getLocalizedMessage(), 
						"Database error", JOptionPane.ERROR_MESSAGE);
				Logger.getGlobal().log(Level.SEVERE,"LDraw search error",e);
			}
		}
//		else if (onlyDeleted.isSelected()) {
//			ArrayList<LDrawPart> pl;
//			try {
//				pl = LDrawPart.get("deleted");
//				tableModel.setParts(pl);
//				scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMinimum());
//			} catch (Exception e) {
//				JOptionPane.showMessageDialog(this, 
//						"Error retrieving LDraw parts from database.\n"+e.getLocalizedMessage(), 
//						"Database error", JOptionPane.ERROR_MESSAGE);
//			}
//
//		}
		else if (onlyNew.isSelected()) {
			ArrayList<LDrawPart> pl;
			try {
				pl = ldrawlib.getLdrDB().getNew();
				tableModel.setParts(pl);
				scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMinimum());
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, 
						"Error retrieving LDraw parts from database.\n"+e.getLocalizedMessage(), 
						"Database error", JOptionPane.ERROR_MESSAGE);
				Logger.getGlobal().log(Level.SEVERE,"LDraw search error",e);
			}
			
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent ev) {
		
		if (ev.getValueIsAdjusting())
			return;
		if (brickShape == null)
			return;
		if (table.getSelectedRow() < 0) {
			brickShape.setLdrawid("");
			return;
		}
		String ldrid = (String) tableModel.getValueAt(table.convertRowIndexToModel(table.getSelectedRow()),0);
		brickShape.setLdrawid(ldrid);
		if (autoConvert.isSelected()) {
			try {
				convertedBrick = Brick.brickByLdrId(ldrid);
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(this, "Problem with database.\nReason: "+
						e.getLocalizedMessage(), "Database error",JOptionPane.ERROR_MESSAGE);
				Logger.getGlobal().log(Level.SEVERE,"LDraw search error",e);
			}
			if (convertedBrick.blID != "") {
				isBlOk.setIcon(shapeOk);
				isBlOk.setText(convertedBrick.ldrawID);
			}
			else {
				isBlOk.setIcon(shapeLike);
				isBlOk.setText("-");
			}
			if (convertedBrick.designID != "") {
				isLddOk.setIcon(shapeOk);
				isLddOk.setText(convertedBrick.designID);
			}
			else {
				isLddOk.setIcon(shapeLike);
				isLddOk.setText("-");
			}
		}

	}
	
}


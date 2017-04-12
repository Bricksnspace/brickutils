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

import bricksnspace.bricklinklib.BLPartTableModel;
import bricksnspace.bricklinklib.BlCategoryCombo;
import bricksnspace.bricklinklib.BricklinkCategory;
import bricksnspace.bricklinklib.BricklinkPart;
import bricksnspace.ldrawlib.LDrawLib;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

public class BlSearch extends JDialog implements ActionListener, ListSelectionListener {


	private static final long serialVersionUID = 1490804630522544972L;
	private final JPanel contentPanel = new JPanel();
	private JTable table;
	private BLPartTableModel tableModel;
	private JTextField searchText;
	private JPanel userPane;
	private JButton button;
	private JButton okButton;
	private JButton cancelButton;
	private int userChoice = JOptionPane.CLOSED_OPTION;
	private BrickShapePanel brickShape;
	private LDrawLib ldrawlib;
	private JScrollPane scrollPane;
	private BlCategoryCombo blcatCombo;
	private JRadioButton catSel;
	private JRadioButton onlyDeleted;
	private JRadioButton onlyNew;
	private ImageIcon shapeOk;
	private ImageIcon shapeLike;
	private GridBagConstraints gbc;
	private JCheckBox autoConvert;
	private JLabel isLddOk;
	private JLabel isLdrOk;
	private Brick convertedBrick;
	private ImageIcon shapeUnkn;
	private JRadioButton byId;
	private JRadioButton byText;


	
	public BlSearch(JFrame owner, String title, boolean modal, LDrawLib ldr) {
		
		super(owner,title,modal);
		
		ldrawlib = ldr;
		
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(4, 4));
		
		scrollPane = new JScrollPane();
		
		contentPanel.add(scrollPane, BorderLayout.CENTER);

		tableModel = new BLPartTableModel();
		table = new JTable(tableModel);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.setAutoCreateRowSorter(true);
		table.getSelectionModel().addListSelectionListener(this);
		TableColumnModel tcm = table.getColumnModel();
		tcm.getColumn(0).setPreferredWidth(50);
		tcm.getColumn(1).setPreferredWidth(90);
		tcm.getColumn(2).setPreferredWidth(350);
		tcm.getColumn(3).setPreferredWidth(150);
		tcm.getColumn(4).setPreferredWidth(50);
		
		userPane = new JPanel();
		contentPanel.add(userPane, BorderLayout.SOUTH);
		GridBagLayout gbl = new GridBagLayout();
		userPane.setLayout(gbl);

		gbc = new GridBagConstraints();
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.ipady = 2;
		gbc.ipadx = 2;
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		byId = new JRadioButton(" By ID");
		userPane.add(byId, gbc);
		gbc.gridy = 1;
		byText = new JRadioButton(" By generic text");
		userPane.add(byText,gbc);
		
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

		shapeOk = new ImageIcon(brickUtils.class.getResource("images/ok.png"));
		shapeLike = new ImageIcon(brickUtils.class.getResource("images/no.png"));
		
		gbc.fill = GridBagConstraints.HORIZONTAL;

		catSel = new JRadioButton(" By Category ");
		catSel.setSelected(false);
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		userPane.add(catSel,gbc);
		
		blcatCombo = new BlCategoryCombo(BlCategoryCombo.BL_CAT_PART);
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.gridheight = 1;
		userPane.add(blcatCombo, gbc);

		onlyDeleted = new JRadioButton(" Deleted Parts");
		onlyDeleted.setSelected(false);
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridheight = 1;
		userPane.add(onlyDeleted,gbc);
		
		onlyNew = new JRadioButton(" New Parts");
		onlyNew.setSelected(false);
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridheight = 1;
		userPane.add(onlyNew,gbc);
		
		ButtonGroup bgroup = new ButtonGroup();
		bgroup.add(byId);
		bgroup.add(byText);
		bgroup.add(catSel);
		bgroup.add(onlyDeleted);
		bgroup.add(onlyNew);
		byId.setSelected(true);
		
		autoConvert = new JCheckBox(" Convert to LDD-LDraw codes");
		autoConvert.addActionListener(this);
		autoConvert.setSelected(false);
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		userPane.add(autoConvert,gbc);
		gbc.gridwidth = 1;
		
		JLabel convertedLdd = new JLabel("LDD:");
		convertedLdd.setHorizontalAlignment(JLabel.RIGHT);
		gbc.gridx = 0;
		gbc.gridy = 6;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		userPane.add(convertedLdd,gbc);
		JLabel convertedLdr = new JLabel("LDraw:");
		convertedLdr.setHorizontalAlignment(JLabel.RIGHT);
		gbc.gridx = 0;
		gbc.gridy = 7;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		userPane.add(convertedLdr,gbc);

		shapeUnkn = new ImageIcon(brickUtils.class.getResource("images/off.png"));
		
		isLddOk = new JLabel(shapeUnkn);
		isLddOk.setText("-");
		isLddOk.setHorizontalAlignment(JLabel.LEFT);
		gbc.gridx = 1;
		gbc.gridy = 6;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		userPane.add(isLddOk,gbc);
		isLdrOk = new JLabel(shapeUnkn);
		isLdrOk.setText("-");
		isLdrOk.setHorizontalAlignment(JLabel.LEFT);
		gbc.gridx = 1;
		gbc.gridy = 7;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		userPane.add(isLdrOk,gbc);

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
		
		scrollPane.setPreferredSize(new Dimension(750,200));
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
		//System.out.println(ev.toString());
		if (ev.getSource() == button || ev.getSource() == searchText
				|| ev.getSource() == catSel) {
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
				isLdrOk.setIcon(shapeUnkn);
				isLdrOk.setText("-");
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
		

	public BricklinkPart getSelected() {
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
				if (catSel.isSelected()) {
					query = "blid like '%"+searchText.getText()+"%' and catid="+
							((BricklinkCategory)blcatCombo.getSelectedItem()).getCatid();
				}
				else {
					query = "blid like '%"+searchText.getText()+"%'";
				}
				ArrayList<BricklinkPart> pl = BricklinkPart.get(query);
				tableModel.setParts(pl);
				scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMinimum());
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(this, 
						"Error retrieving BLink parts from database.\n"+e.getLocalizedMessage(), 
						"Database error", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		else if (byText.isSelected() && searchText.getText().length() > 0) {
			ArrayList<BricklinkPart> pl;
			try {
				if (catSel.isSelected()) {
					pl = BricklinkPart.getFTS(searchText.getText(),"catid="+
							((BricklinkCategory)blcatCombo.getSelectedItem()).getCatid());
				}
				else {
					pl = BricklinkPart.getFTS(searchText.getText(),null);
				}
				tableModel.setParts(pl);
				scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMinimum());
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, 
						"Error retrieving BLink parts from database.\n"+e.getLocalizedMessage(), 
						"Database error", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if (onlyDeleted.isSelected()) {
			ArrayList<BricklinkPart> pl;
			try {
				pl = BricklinkPart.get("deleted");
				tableModel.setParts(pl);
				scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMinimum());
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, 
						"Error retrieving BLink parts from database.\n"+e.getLocalizedMessage(), 
						"Database error", JOptionPane.ERROR_MESSAGE);
			}

		}
		else if (onlyNew.isSelected()) {
			ArrayList<BricklinkPart> pl;
			try {
				pl = BricklinkPart.getNew();
				tableModel.setParts(pl);
				scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMinimum());
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, 
						"Error retrieving BLink parts from database.\n"+e.getLocalizedMessage(), 
						"Database error", JOptionPane.ERROR_MESSAGE);
			}
			
		}
		else if (catSel.isSelected()) {
			// search by category
			try {
				ArrayList<BricklinkPart> pl = BricklinkPart.get("catid="+
						((BricklinkCategory)blcatCombo.getSelectedItem()).getCatid());
				tableModel.setParts(pl);
				scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMinimum());
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(this, 
						"Error retrieving BLink parts from database.\n"+e.getLocalizedMessage(), 
						"Database error", JOptionPane.ERROR_MESSAGE);
				return;
			}

		}
	}

	@Override
	public void valueChanged(ListSelectionEvent ev) {
		
		String blid;
		
		
		if (ev.getValueIsAdjusting())
			return;
		if (brickShape == null)
			return;
		if (table.getSelectedRow() < 0)
			return;
		blid = (String) tableModel.getValueAt(table.convertRowIndexToModel(table.getSelectedRow()),1);
		brickShape.setBlid(blid);
		if (autoConvert.isSelected()) {
			try {
				convertedBrick = Brick.brickByBlinkId(blid);
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(this, "Problem with database.\nReason: "+
						e.getLocalizedMessage(), "Database error",JOptionPane.ERROR_MESSAGE);
			}
			if (convertedBrick.ldrawID != "") {
				isLdrOk.setIcon(shapeOk);
				isLdrOk.setText(convertedBrick.ldrawID);
			}
			else {
				isLdrOk.setIcon(shapeLike);
				isLdrOk.setText("-");
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


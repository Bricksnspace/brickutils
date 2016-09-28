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

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

public class LddSearch extends JDialog implements ActionListener, ListSelectionListener {


	private static final long serialVersionUID = 149080463052344972L;
	private final JPanel contentPanel = new JPanel();
	private JTable table;
	private LddTableModel tableModel;
	private JTextField searchText;
	private JPanel userPane;
	private JButton button;
	private JButton okButton;
	private JButton cancelButton;
	private int userChoice = JOptionPane.CLOSED_OPTION;
	private BrickShapePanel brickShape;
	private LDrawLib ldrawlib;
	private JScrollPane scrollPane;
	private JRadioButton onlyNew;
	private ImageIcon shapeOk;
	private ImageIcon shapeLike;
	private GridBagConstraints gbc;
	private JCheckBox autoConvert;
	private JLabel isBlOk;
	private JLabel isLdrOk;
	private Brick convertedBrick;
	private ImageIcon shapeUnkn;
	private JRadioButton byId;
	private JRadioButton byText;


	
	public LddSearch(JFrame owner, String title, boolean modal, LDrawLib ldr) {
		
		super(owner,title,modal);
		
		ldrawlib = ldr;
		
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(4, 4));
		
		scrollPane = new JScrollPane();
		
		contentPanel.add(scrollPane, BorderLayout.CENTER);

		tableModel = new LddTableModel();
		table = new JTable(tableModel);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.setAutoCreateRowSorter(true);
		table.getSelectionModel().addListSelectionListener(this);
		TableColumnModel tcm = table.getColumnModel();
		tcm.getColumn(0).setPreferredWidth(50);
		tcm.getColumn(5).setPreferredWidth(350);
		
		userPane = new JPanel();
		contentPanel.add(userPane, BorderLayout.SOUTH);
		GridBagLayout gbl = new GridBagLayout();
		userPane.setLayout(gbl);

		byId = new JRadioButton(" By design ID");
		gbc = new GridBagConstraints();
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
		//gbc.insets = new Insets(0, 0, 5, 0);
		gbc.gridx = 0;
		gbc.gridy = 1;
		userPane.add(byText, gbc);
		
		searchText = new JTextField(15);
		//lddid.setColumns(15);
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
		brickShape = new BrickShapePanel(ldrawlib);
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 4;
		gbc.gridy = 0;
		gbc.gridheight = 9;
		gbc.gridwidth = 1;
		userPane.add(brickShape,gbc);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		
		shapeOk = new ImageIcon(brickUtils.class.getResource("images/ok.png"));
		shapeLike = new ImageIcon(brickUtils.class.getResource("images/no.png"));
		
		onlyNew = new JRadioButton(" New Parts");
		onlyNew.setSelected(false);
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridheight = 1;
		userPane.add(onlyNew,gbc);
		
		ButtonGroup bgroup = new ButtonGroup();
		bgroup.add(byId);
		bgroup.add(byText);
		bgroup.add(onlyNew);
		byId.setSelected(true);
		
		autoConvert = new JCheckBox(" Convert to Bricklink-LDraw codes");
		autoConvert.addActionListener(this);
		autoConvert.setSelected(false);
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.gridheight = 1;
		gbc.gridwidth = 2;		
		userPane.add(autoConvert,gbc);
		
		JLabel convertedLdd = new JLabel("Blink:");
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
		
		isBlOk = new JLabel(shapeUnkn);
		isBlOk.setText("-");
		isBlOk.setHorizontalAlignment(JLabel.LEFT);
		gbc.gridx = 1;
		gbc.gridy = 6;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		userPane.add(isBlOk,gbc);
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
		}
		searchText.requestFocusInWindow();
		
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		//System.out.println(ev.toString());
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
				isBlOk.setIcon(shapeUnkn);
				isBlOk.setText("-");
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
		

	public PartMapping getSelected() {
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
				query = "((designid like '%"+searchText.getText()+"%') or (masterid like '%"+searchText.getText()+"%'))" +
						" and (ldd2bl or ldd2dat)";
				ArrayList<PartMapping> pl = PartMapping.get(query);
				tableModel.setParts(pl);
				scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMinimum());
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(this, 
						"Error retrieving LDD parts from database.\n"+e.getLocalizedMessage(), 
						"Database error", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		else if (byText.isSelected() && searchText.getText().length() > 0) {
			ArrayList<PartMapping> pl;
			try {
				pl = PartMapping.getFTS(searchText.getText(),null);
				tableModel.setParts(pl);
				scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMinimum());
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, 
						"Error retrieving LDD parts from database.\n"+e.getLocalizedMessage(), 
						"Database error", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if (onlyNew.isSelected()) {
			ArrayList<PartMapping> pl;
			try {
				pl = PartMapping.getNew();
				tableModel.setParts(pl);
				scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMinimum());
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, 
						"Error retrieving LDD parts from database.\n"+e.getLocalizedMessage(), 
						"Database error", JOptionPane.ERROR_MESSAGE);
			}
			
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent ev) {
		
		String lddid,decorid;
		
		
		if (ev.getValueIsAdjusting())
			return;
		if (brickShape == null)
			return;
		if (table.getSelectedRow() < 0)
			return;
		lddid = (String) tableModel.getValueAt(table.convertRowIndexToModel(table.getSelectedRow()),2);
		decorid = (String) tableModel.getValueAt(table.convertRowIndexToModel(table.getSelectedRow()),3);
		brickShape.setLddid(lddid,decorid);
		if (autoConvert.isSelected()) {
			try {
				convertedBrick = PartMapping.getBrickByDesignId(lddid,decorid);
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
			if (convertedBrick.blID != "") {
				isBlOk.setIcon(shapeOk);
				isBlOk.setText(convertedBrick.blID);
			}
			else {
				isBlOk.setIcon(shapeLike);
				isBlOk.setText("-");
			}
		}

	}
	
}


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
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import bricksnspace.ldrawlib.LDrawLib;




public class CheckBuildableDialog extends JDialog implements ActionListener, ListSelectionListener {

	private static final long serialVersionUID = 714586747434950232L;
	private JPanel contentPanel;
	private JButton closeButton;
	private WorkingTableModel missingBricksModel;
	private WorkingTableModel alternateBricksModel;
	private JTable missingTable;
	private JTable alternateTable;
	private BrickShapePanel missingShape;
	private BrickShapePanel alternateShape;
	private JComboBox<String> brickMode;
	private JButton checkButton;
	private JComboBox<String> colorMode;
	private JCheckBox decoration;
	private JProgressBar percBuild;
	private JTextField missingBricks;
	private JTextField missingParts;
	private JTextField neededBricks;
	private JTextField neededParts;
	private JButton usePart;
	private JButton blExportButton;
	private JButton htmlExportButton;
	private SmartFileChooser fileExport;
	private JFileChooser fileVelocity;
	private Frame topFrame;
	private LDrawLib ldrlib;
	private ImageIcon[] icn;
	private JButton tmplExportButton;


	public CheckBuildableDialog(Frame owner, String title, boolean modal, LDrawLib ldr, 
			SmartFileChooser fe, ImageIcon[] icnImg) 
	{
		
		super(owner, title, modal);
		
		topFrame = owner;
		fileExport = fe;
		fileVelocity = new JFileChooser(".");
		fileVelocity.setDialogTitle("Choose a template");
		FileFilter ff = new FileNameExtensionFilter("Velocity template","vm");
		fileVelocity.setFileFilter(ff);
		ldrlib = ldr;
		icn = icnImg;
		Container mainPane = getContentPane();
		mainPane.setLayout(new BorderLayout());
		contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		mainPane.add(contentPanel, BorderLayout.CENTER);
		
		GridBagLayout gbl = new GridBagLayout();
		contentPanel.setLayout(gbl);
		
		missingBricksModel = new WorkingTableModel();
		missingBricksModel.setSimpleView(true);
		alternateBricksModel = new WorkingTableModel();
		alternateBricksModel.setSimpleView(true);
		
		missingTable = new JTable(missingBricksModel);
		missingTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		missingTable.setAutoCreateRowSorter(true);
		//brickTable.getSelectionModel().addListSelectionListener(this);
		TableColumnModel tcl = missingTable.getColumnModel();
		TableRowSorter<WorkingTableModel>sorterFilter = new TableRowSorter<WorkingTableModel>(missingBricksModel);
		missingTable.setRowSorter(sorterFilter);
		tcl.getColumn(7).setPreferredWidth(350);
		tcl.getColumn(5).setPreferredWidth(45);
		tcl.getColumn(4).setCellRenderer(new BrickColorCellRenderer(true));
		missingTable.getSelectionModel().addListSelectionListener(this);

		alternateTable = new JTable(alternateBricksModel);
		alternateTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		alternateTable.setAutoCreateRowSorter(true);
		//brickTable.getSelectionModel().addListSelectionListener(this);
		tcl = alternateTable.getColumnModel();
		sorterFilter = new TableRowSorter<WorkingTableModel>(alternateBricksModel);
		alternateTable.setRowSorter(sorterFilter);
		tcl.getColumn(7).setPreferredWidth(350);
		tcl.getColumn(5).setPreferredWidth(45);
		tcl.getColumn(4).setCellRenderer(new BrickColorCellRenderer(true));
		alternateTable.getSelectionModel().addListSelectionListener(this);
		
		missingShape = new BrickShapePanel(ldr);
		alternateShape = new BrickShapePanel(ldr);
		
		JScrollPane missingTableScroll = new JScrollPane();
		JScrollPane alternateTableScroll = new JScrollPane();
		
		missingTableScroll.setBorder(BorderFactory.createTitledBorder("Missing bricks"));
		alternateTableScroll.setBorder(BorderFactory.createTitledBorder("Available/suggested bricks"));
		
		missingTableScroll.setViewportView(missingTable);
		alternateTableScroll.setViewportView(alternateTable);
		
		missingTableScroll.setPreferredSize(new Dimension(600,300));
		alternateTableScroll.setPreferredSize(new Dimension(600,300));
		
		missingShape.setBorder(BorderFactory.createTitledBorder("Missing part"));
		alternateShape.setBorder(BorderFactory.createTitledBorder("Alternate part"));
	
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.ipady = 2;
		gbc.ipadx = 2;
		gbc.gridx = 0;
		gbc.gridy = 0;

		gbc.gridwidth = 4;
		gbc.gridheight = 3;
		gbc.weightx = 0.7;
		gbc.weighty = 0.7;
		contentPanel.add(missingTableScroll, gbc);
		
		gbc.gridx = 4;
		gbc.gridwidth = 2;
		gbc.gridheight = 2;
		gbc.weightx = 0.2;
		gbc.weighty = 0.2;
		contentPanel.add(missingShape, gbc);
		
		gbc.gridheight = 1;
		gbc.gridwidth = 3;
		gbc.gridx = 0;
		gbc.gridy += 3;
		JPanel modeButtons = new JPanel(new FlowLayout());
		modeButtons.setBorder(BorderFactory.createTitledBorder("Check mode"));
		contentPanel.add(modeButtons, gbc);
		modeButtons.add(new JLabel("Use:"));
		brickMode = new JComboBox<String>();
		brickMode.addItem("All bricks");
		brickMode.addItem("Only available bricks");
		brickMode.addItem("Only from selected set");
		brickMode.setSelectedIndex(1);
		modeButtons.add(brickMode);
		checkButton = new JButton("Check!");
		modeButtons.add(checkButton);
		checkButton.addActionListener(this);
		modeButtons.add(new JLabel(new ImageIcon(brickUtils.class.getResource("images/go-next.png"))));
		percBuild = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
		percBuild.setStringPainted(true);
		percBuild.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));
		modeButtons.add(percBuild);
		
		gbc.gridheight = 2;
		gbc.gridwidth = 1;
		gbc.gridx = 3;
		usePart = new JButton(new ImageIcon(brickUtils.class.getResource("images/use-part.png")));
		usePart.addActionListener(this);
		contentPanel.add(usePart, gbc);
		
		
		gbc.gridheight = 1;
		gbc.gridwidth = 2;
		gbc.gridx = 4;
		JPanel needs = new JPanel(new FlowLayout());
		needs.setBorder(BorderFactory.createTitledBorder("Needed"));
		neededBricks = new JTextField(4);
		neededBricks.setEditable(false);
		neededParts = new JTextField(4);
		neededParts.setEditable(false);
		needs.add(new JLabel("Parts:"));
		needs.add(neededParts);
		needs.add(new JLabel("Bricks:"));
		needs.add(neededBricks);
		contentPanel.add(needs, gbc);

		gbc.gridheight = 1;
		gbc.gridwidth = 3;
		gbc.gridx = 0;
		gbc.gridy += 1;
		JPanel selectButtons = new JPanel(new FlowLayout());
		selectButtons.setBorder(BorderFactory.createTitledBorder("How to select alternate bricks"));
		contentPanel.add(selectButtons, gbc);
		selectButtons.add(new JLabel("Color mode:"));
		colorMode = new JComboBox<String>();
		colorMode.addItem("Exact color");
		colorMode.addItem("Nearest color");
		colorMode.addItem("Ignore color");
		colorMode.setSelectedIndex(1);
		colorMode.addActionListener(this);
		selectButtons.add(colorMode);
		decoration = new JCheckBox("Ignore molding and decoration");
		decoration.setSelected(false);
		decoration.addActionListener(this);
		selectButtons.add(decoration);
		
		gbc.gridheight = 1;
		gbc.gridwidth = 2;
		gbc.gridx = 4;
		JPanel stats = new JPanel(new FlowLayout());
		stats.setBorder(BorderFactory.createTitledBorder("Missing"));
		missingBricks = new JTextField(4);
		missingBricks.setEditable(false);
		missingParts = new JTextField(4);
		missingParts.setEditable(false);
		stats.add(new JLabel("Parts:"));
		stats.add(missingParts);
		stats.add(new JLabel("Bricks:"));
		stats.add(missingBricks);
		contentPanel.add(stats,gbc);
		
		gbc.gridwidth = 4;
		gbc.gridheight = 3;
		gbc.gridx = 0;
		gbc.gridy += 1;
		gbc.weightx = 0.7;
		gbc.weighty = 0.7;
		contentPanel.add(alternateTableScroll,gbc);
		
		gbc.gridx = 4;
		gbc.gridwidth = 2;
		gbc.gridheight = 2;
		gbc.weightx = 0.2;
		gbc.weighty = 0.2;
		contentPanel.add(alternateShape, gbc);
		
		// Dialog buttons
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		
		htmlExportButton = new JButton("Export to HTML");
		buttonPane.add(htmlExportButton);
		htmlExportButton.addActionListener(this);
		
		blExportButton = new JButton("Export to Blink XML");
		buttonPane.add(blExportButton);
		blExportButton.addActionListener(this);
		
		tmplExportButton = new JButton("Export via template");
		buttonPane.add(tmplExportButton);
		tmplExportButton.addActionListener(this);
		
		closeButton = new JButton("Close");
		buttonPane.add(closeButton);
		closeButton.addActionListener(this);
		getRootPane().setDefaultButton(closeButton);

		pack();

	}


	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == closeButton) {
			setVisible(false);
			missingShape.dispose();
			alternateShape.dispose();
		}
		else if (e.getSource() == checkButton) {
			try {
				missingBricksModel.setParts(Brick.getMissingBricks(brickMode.getSelectedIndex()+1));
				missingParts.setText(Integer.toString(missingBricksModel.getRowCount()));
				int pieces = missingBricksModel.getPartsCount();
				missingBricks.setText(Integer.toString(pieces));
				int i[] = Brick.countWork();
				neededBricks.setText(Integer.toString(i[1]));
				neededParts.setText(Integer.toString(i[0]));
				int perc = (i[1] - pieces) * 100 / i[1];
				percBuild.setValue(perc);
				percBuild.setForeground(Color.getHSBColor(perc/360.0f, 0.8f, 0.8f));
				alternateBricksModel.setParts(null);
			} catch (SQLException e1) {
				JOptionPane.showMessageDialog(this, "Problems with database\n Reason: "+e1.getLocalizedMessage(), "Database error",JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}
		}
		else if (e.getSource() == colorMode || e.getSource() == decoration) {
			if (missingTable.getSelectedRow() < 0)
				return;
			Brick b = missingBricksModel.getBrick(missingTable.convertRowIndexToModel(missingTable.getSelectedRow()));
			try {
				alternateBricksModel.setParts(Brick.getAltBricks(b, colorMode.getSelectedIndex()+1, 
						decoration.isSelected()));
				//System.out.println(alternateBricksModel.getParts());
			} catch (SQLException e1) {
				JOptionPane.showMessageDialog(this, "Problems with database\n Reason: "+e1.getLocalizedMessage(), "Database error",JOptionPane.ERROR_MESSAGE);				
				e1.printStackTrace();
			}
		}
		else if (e.getSource() == usePart) {
			if (missingTable.getSelectedRow() < 0)
				return;
			if (alternateTable.getSelectedRow() < 0)
				return;
			Brick m = missingBricksModel.getBrick(missingTable.convertRowIndexToModel(missingTable.getSelectedRow()));
			Brick a = alternateBricksModel.getBrick(alternateTable.convertRowIndexToModel(alternateTable.getSelectedRow()));
			try {
				if (a.quantity >= m.quantity) {
					// we have enough bricks
					a.quantity = m.quantity;
					int id = a.getWorkId();
					if (id != 0) {
						// there is an identical part in list
						a.workAdd();
						m.deleteWork();
					}
					else {
						// substitution in place
						a.id = m.id;
						a.updateWork();
					}
				}
				else {
					// quantity is less than needed
					// remaining bricks
					m.quantity -= a.quantity;
					m.updateWork();
					a.workAdd();
				}
			}
			catch (SQLException e1) {
				JOptionPane.showMessageDialog(this, "Problems with database\n Reason: "+e1.getLocalizedMessage(), "Database error",JOptionPane.ERROR_MESSAGE);				
				e1.printStackTrace();				
			}
			alternateBricksModel.setParts(null);
			checkButton.doClick();
		}
		else if (e.getSource() == blExportButton) {
			if (missingBricksModel.getRowCount() <= 0) {
				return;
			}
			fileExport.setExtension(".xml");
			BricklinkExporter be = new BricklinkExporter((JFrame)topFrame, missingBricksModel.getParts(), fileExport);
			be.doExport();
		}
		else if (e.getSource() == htmlExportButton) {
			if (missingBricksModel.getRowCount() <= 0) {
				return;
			}
			fileExport.setExtension(".html");
			BrickSet setinfo = new BrickSet();
			setinfo.name = "Missing bricks";
			setinfo.setid = "BrickUtils";
			HTMLExporter he = new HTMLExporter((JFrame)topFrame, missingBricksModel.getParts(), fileExport,setinfo,
					ldrlib, icn);
			he.doExport();
		}
		else if (e.getSource() == tmplExportButton) {
			if (missingBricksModel.getRowCount() <= 0)
				return;
			int res = fileVelocity.showOpenDialog((JFrame)topFrame);
			if (res != JFileChooser.APPROVE_OPTION) {
				return;
			}
			TemplateExportDialog dlg = new TemplateExportDialog((JFrame)topFrame);
			dlg.setVisible(true);
			if (dlg.getResponse() != JOptionPane.OK_OPTION) {
				return;
			}
			// choose output file
			fileExport.setExtension("");
			res = fileExport.showSaveDialog((JFrame)topFrame);
			if (res != JFileChooser.APPROVE_OPTION) {
				return;
			}
			// do template build 
			BrickSet setinfo = new BrickSet();
			setinfo.name = "Missing bricks";
			setinfo.setid = "BrickUtils";
			dlg.doExport(fileVelocity.getSelectedFile().getName(),fileExport.getSelectedFile(),
					missingBricksModel.getParts(),setinfo,ldrlib,icn);
		}
	}

	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		
		Brick b;

		if (e.getValueIsAdjusting())
			return;
		if (e.getSource() == missingTable.getSelectionModel()) {
			if (missingTable.getSelectedRow() < 0)
				return;
			b = missingBricksModel.getBrick(missingTable.convertRowIndexToModel(missingTable.getSelectedRow()));
			if (b.ldrawID.length() > 0) {
				missingShape.setColor(b.color);
				missingShape.setLdrawid(b.ldrawID);
			}
			else if (b.designID.length() > 0) {
				missingShape.setColor(b.color);
				missingShape.setLddid(b.designID,b.decorID);
			}
			else if (b.blID.length() > 0) {
				missingShape.setColor(b.color);
				missingShape.setBlid(b.blID);
			}
			try {
				alternateBricksModel.setParts(Brick.getAltBricks(b, colorMode.getSelectedIndex()+1,
						decoration.isSelected()));
			} catch (SQLException e1) {
				JOptionPane.showMessageDialog(this, "Problems with database\n Reason: "+e1.getLocalizedMessage(), "Database error",JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}
		}
		if (e.getSource() == alternateTable.getSelectionModel()) {
			if (alternateTable.getSelectedRow() < 0)
				return;
			b = alternateBricksModel.getBrick(alternateTable.convertRowIndexToModel(alternateTable.getSelectedRow()));
			if (b.ldrawID.length() > 0) {
				alternateShape.setColor(b.color);
				alternateShape.setLdrawid(b.ldrawID);
			}
			else if (b.designID.length() > 0) {
				alternateShape.setColor(b.color);
				alternateShape.setLddid(b.designID,b.decorID);
			}
			else if (b.blID.length() > 0) {
				alternateShape.setColor(b.color);
				alternateShape.setBlid(b.blID);
			}
		}
	}
	
	

}

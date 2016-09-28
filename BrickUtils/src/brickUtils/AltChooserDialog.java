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
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

public class AltChooserDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = -4804551942185235117L;
	private Container pane;
	private int choosed = -1;
	private WorkingTableModel brickTableModel;
	private JTable brickTable;
	private JScrollPane scrollPane;
	private JButton okButton;
	private JButton cancelButton;


	public AltChooserDialog(Frame owner, String title, boolean modal, ArrayList<Brick> bricks) {

		super(owner, title, modal);
		createDialog(bricks);
	}

	
	public AltChooserDialog(Dialog owner, String title, boolean modal, ArrayList<Brick> bricks) {

		super(owner, title, modal);
		createDialog(bricks);
	}

	
	public AltChooserDialog(Window owner, String title,
			ModalityType modalityType, ArrayList<Brick> bricks) {

		super(owner, title, modalityType);
		createDialog(bricks);
	}

	
	public int getChoosed() {
		
		return choosed;
	}
	
	
	private void createDialog(ArrayList<Brick> bricks) {
		
		// really create the dialog
		setLocationByPlatform(true);
		setPreferredSize(new Dimension(700,300));
		pane = getContentPane();
		brickTableModel = new WorkingTableModel();
		brickTableModel.setSimpleView(true);
		brickTableModel.setParts(bricks);
		brickTable = new JTable(brickTableModel);
		TableColumnModel tcl = brickTable.getColumnModel();
		tcl.getColumn(5).setPreferredWidth(50);
		tcl.getColumn(7).setPreferredWidth(350);
		tcl.getColumn(4).setCellRenderer(new BrickColorCellRenderer(true));
		scrollPane = new JScrollPane();
		scrollPane.setViewportView(brickTable);
		pane.add(scrollPane, BorderLayout.CENTER);
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		pane.add(buttonPane, BorderLayout.SOUTH);

		okButton = new JButton("OK");
		buttonPane.add(okButton);
		okButton.addActionListener(this);
		getRootPane().setDefaultButton(okButton);

		cancelButton = new JButton("Cancel");
		buttonPane.add(cancelButton);
		cancelButton.addActionListener(this);
		
		pack();
	}
	
	@Override
	public void actionPerformed(ActionEvent ev) {
		//System.out.println(ev.toString());
		if (ev.getSource() == okButton) {
			if (brickTable.getSelectedRowCount() == 1) {
				choosed = brickTable.convertRowIndexToModel(brickTable.getSelectedRow());
			}
			setVisible(false);
		}
		else if (ev.getSource() == cancelButton) {
			choosed = -1;
			setVisible(false);
		}
	}

	
}

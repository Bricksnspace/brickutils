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
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;


public class BlImportDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = -1703526546895376328L;
	private Container pane;
	private WorkingTableModel brickTableModel;
	private JTable brickTable;
	private JScrollPane scrollPane;
	private JButton okButton;
	private JButton cancelButton;

	
	private class AltRender extends AbstractCellEditor implements TableCellRenderer,TableCellEditor {

		private static final long serialVersionUID = 1L;
		JButton altButton = new JButton("+");
		JButton altButton2 = new JButton("+");
		JLabel empty = new JLabel(" ");
		BlImportDialog dlg;
		
		public AltRender(BlImportDialog dialog) {
			
			dlg = dialog;
			altButton.setActionCommand("altbrick");
			altButton.addActionListener(dlg);
		}
		
		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			if ((Integer) value != 0) {
				return altButton2;
			}
			return empty;
		}

		
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
        	if ((Integer) value != 0)
        		return altButton;
        	else
        		return empty;
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }

        @Override
        public boolean isCellEditable(EventObject ev) {
            return true;
        }

        @Override
        public boolean shouldSelectCell(EventObject ev) {
            return true;
        }
		
	}
	
	

	public BlImportDialog(Frame owner, String title, boolean modal) throws SQLException {
		super(owner, title, modal);
		createDialog();
	}

	
	public BlImportDialog(Dialog owner, String title, boolean modal) throws SQLException {
		super(owner, title, modal);
		createDialog();
	}

	
	
	private void createDialog() throws SQLException {
		
		// really create the dialog
		setPreferredSize(new Dimension(800,500));
		setLocationByPlatform(true);
		pane = getContentPane();
		pane.setLayout(new BorderLayout(2,2));
		brickTableModel = new WorkingTableModel();
		brickTableModel.setImport(true);
		brickTable = new JTable(brickTableModel);
		scrollPane = new JScrollPane();
		scrollPane.setViewportView(brickTable);
		pane.add(scrollPane, BorderLayout.CENTER);
		brickTableModel.setParts(Brick.getTmp());
		TableColumn cm = brickTable.getColumnModel().getColumn(10);
		AltRender ar = new AltRender(this);
		cm.setCellRenderer(ar);
		cm.setCellEditor(ar);
		brickTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		brickTable.setAutoCreateRowSorter(true);
		//brickTable.getSelectionModel().addListSelectionListener(this);
		TableColumnModel tcl = brickTable.getColumnModel();
		TableRowSorter<WorkingTableModel>sorterFilter = new TableRowSorter<WorkingTableModel>(brickTableModel);
		brickTable.setRowSorter(sorterFilter);
		tcl.getColumn(0).setPreferredWidth(50);
		tcl.getColumn(6).setPreferredWidth(50);
		tcl.getColumn(8).setPreferredWidth(350);
		tcl.getColumn(9).setPreferredWidth(45);
		tcl.getColumn(5).setCellRenderer(new BrickColorCellRenderer(true));
	
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
		
		if (ev.getSource() == okButton) {
			// move all rows from TMP table to WORK table
			setVisible(false);
			try {
				Brick.saveTmpToWork();
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(this, "Problem with database.\nReason: "+
						e.getLocalizedMessage(), "Database error",JOptionPane.ERROR_MESSAGE);
			}
		}
		else if (ev.getSource() == cancelButton) {
			setVisible(false);
		}
		else if (ev.getActionCommand().equalsIgnoreCase("altbrick")) {
			doSelectAltBrick(ev);
		}
	}

	
	public void doSelectAltBrick(ActionEvent ev) {
		
		ArrayList<Brick> b;
		
		@SuppressWarnings("boxing")
		int matchid = (Integer) brickTableModel.getValueAt(
				brickTable.convertRowIndexToModel(brickTable.getSelectedRow()),10);
		if (matchid == 0)
			return;
		int index = brickTable.convertRowIndexToModel(brickTable.getSelectedRow());
		Brick origBrick = brickTableModel.getBrick(index);
		try {
			b = Brick.getAlt(matchid);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Problem with database.\nReason: "+
					e.getLocalizedMessage(), "Database error",JOptionPane.ERROR_MESSAGE);
			return;
		}
		AltChooserDialog dlg = new AltChooserDialog(this, "Choose alternate part", true, b);
		dlg.setVisible(true);
		if (dlg.getChoosed() >= 0) {
			// user select an alternate part
			Brick altBrick = b.get(dlg.getChoosed());
			// it is same part?
			if (altBrick.id == origBrick.id)
				return;
			altBrick.alt = false;
			origBrick.alt = true;
			try {
				origBrick.updateTmp();
				altBrick.updateTmp();
				brickTableModel.setBrick(index, altBrick);
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(this, "Problem with database.\nReason: "+
						e.getLocalizedMessage(), "Database error",JOptionPane.ERROR_MESSAGE);
			}
		}
		
	}
	
	

	

}

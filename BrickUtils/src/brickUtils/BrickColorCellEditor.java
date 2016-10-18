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


import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import bricksnspace.appsettings.AppSettings;

public class BrickColorCellEditor extends AbstractCellEditor implements TableCellEditor,
		ActionListener {


	private static final long serialVersionUID = -86560863469803218L;
	Integer currentColor;
    JButton button;
    ColorChooseDialog dialog;
    BrickShapePanel bsp = null;
    protected static final String EDIT = "edit";

    public BrickColorCellEditor(BrickShapePanel b) {
        button = new JButton();
        button.setActionCommand(EDIT);
        button.addActionListener(this);
        button.setBorderPainted(false);
        bsp = b;
    }

    /**
     * Handles events from the editor button and from
     * the dialog's OK button.
     */
    public void actionPerformed(ActionEvent e) {
        if (EDIT.equals(e.getActionCommand())) {
            //The user has clicked the cell, so
            //bring up the dialog.
        	dialog = new ColorChooseDialog((JFrame)null, "Select Brick Color", true, currentColor,AppSettings.getBool(MySettings.IN_PRODUCTION));
            dialog.setVisible(true);
            if (dialog.getResponse() == JOptionPane.OK_OPTION) {
            	currentColor = dialog.getSelected();
            	if (bsp != null) {
            		bsp.setColor(currentColor);
            	}
            }
            //Make the renderer reappear.
            fireEditingStopped();

        }
    }

    //Implement the one CellEditor method that AbstractCellEditor doesn't.
    public Object getCellEditorValue() {
        return currentColor;
    }

    //Implement the one method defined by TableCellEditor.
    public Component getTableCellEditorComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int row,
                                                 int column) {
        currentColor = (Integer) value;
        return button;
    }		
}

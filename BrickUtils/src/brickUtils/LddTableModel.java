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

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import bricksnspace.brickMapping.PartMapping;

/*
 * table model to display LDD part search results
 */
public class LddTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -3163453575112568L;
	private ArrayList<PartMapping> parts;

	private String[] columnNames = {
			"ID",
			"Master ID",
			"Design ID",
			"Decor. ID",
			"Blink ID",
			"Description",
			"Updated on"
			};
	
	/* 
	 * sets whole data model for table
	 */
	public void setParts(ArrayList<PartMapping> parts) {
		this.parts = parts;
		fireTableDataChanged();
	}

	
	@Override
	public String getColumnName(int col) {
        return columnNames[col];
    }

	

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}
	

	@Override
	public int getRowCount() {
		if (parts != null)
			return parts.size();
		else
			return 0;
	}
	
	@Override
	public boolean isCellEditable(int row, int col) {
        return false;
    }
	
	
	public PartMapping getPart(int idx) {
		return parts.get(idx);
	}
	
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class getColumnClass(int c) {
		switch (c) {
		case 0:
			return Integer.class;
		}
		return String.class;
    }


	
	@Override
	public Object getValueAt(int arg0, int arg1) {
		if (getRowCount() == 0)
			return "";
		//id,blid,name,catid
		switch (arg1) {
		case 0:
			return parts.get(arg0).getMapid();
		case 1:
			return parts.get(arg0).getMasterid();
		case 2:
			return parts.get(arg0).getDesignid();
		case 3:
			return parts.get(arg0).getDecorid();
		case 4:
			return parts.get(arg0).getBlid();
		case 5:
			return parts.get(arg0).getName();
		case 6:
			return parts.get(arg0).getLastmod();
		}
		return null;
	}

}

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

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/*
 * table model to display Ldraw part search results
 */
public class LdrTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -3167873148975112568L;
	private ArrayList<LDrawPart> parts;

	private String[] columnNames = {
			"ID",
			"LDraw part ID",
			"Description",
			"Category",
			"Keywords",
			"Deleted",
			"Official",
			"Updated on"
			};
	
	/* 
	 * sets whole data model for table
	 */
	public void setParts(ArrayList<LDrawPart> parts) {
		this.parts = parts;
		fireTableDataChanged();
	}

	
	@Override
	public String getColumnName(int col) {
        return columnNames[col];
    }

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class getColumnClass(int c) {
		switch (c) {
		case 0:
			return Integer.class;
		case 5:
		case 6:
			return Boolean.class;
		}
		return String.class;
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
	
	public LDrawPart getPart(int idx) {
		return parts.get(idx);
	}
	
	
	
	@Override
	public Object getValueAt(int arg0, int arg1) {
		if (getRowCount() == 0)
			return "";
		//id,ldrid,name,category,keywords
		switch (arg1) {
		case 0:
			return parts.get(arg0).id;
		case 1:
			return parts.get(arg0).ldrid;
		case 2:
			return parts.get(arg0).name;
		case 3:
			return parts.get(arg0).category;
		case 4:
			return parts.get(arg0).keywords;
		case 5:
			return parts.get(arg0).deleted;
		case 6:
			return parts.get(arg0).official;
		case 7:
			return parts.get(arg0).lastmod;

		}
		return null;
	}

}

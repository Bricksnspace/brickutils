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


import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;





class SetTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 3L;
	private ArrayList<BrickSet> parts;

	private static String[] columnNames = {
			"ID",
			"Set #",
			"Type",
			"Name",
			"Category",
			"Year",
			"Notes",
			"Available",
			"Selected",
			"Added"
			};

	
	/* 
	 * sets whole data model for table
	 */
	public void setParts(ArrayList<BrickSet> parts) {
		this.parts = parts;
		fireTableDataChanged();
	}

	
	public ArrayList<BrickSet> getParts() {
		return parts;
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
        if (col == 3 || col == 6 || col == 8) 
            return true;
        else 
            return false;
    }
	
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class getColumnClass(int c) {

		switch (c) {
		case 0:
			return Integer.class;
		case 1:
			return String.class;
		case 2:
			return String.class;
		case 3:
		case 4:
			return String.class;
		case 5:
			return Integer.class;
		case 6:
			return String.class;
		case 7:
		case 8:
			return Boolean.class;
		case 9:
			return String.class;
		}
		return String.class;
    }

	
	@Override
	public String getColumnName(int col) {

        return columnNames[col];
    }

	
    public void setValueAt(Object value, int row, int col) {
    	
    	BrickSet b = new BrickSet(parts.get(row));
    	
		switch (col) {
		case 1:
			b.setid = (String) value;
			break;
		case 2:
			b.type = (Integer) value;
			break;
		case 3:
			b.name = (String) value;
			break;
		case 4:
			b.category = (String) value;
			break;
		case 5:
			b.year = (Integer) value;
			break;
		case 6:
			b.notes = (String) value;
			break;
		case 7:
			// not editable! it become a problem for catalog!
			// b.available = b.type!=BrickSet.MOC?(Boolean) value:false;
			break;
		case 8:
			b.selected = (Boolean) value;
			break;
		}
		
		try {
			b.update();
		} catch (SQLException e) {
			;
		}
		parts.set(row, b);
        fireTableRowsUpdated(row, row);
     }

    
    
    public void deleteRows(int[] list,boolean updCat) throws SQLException {
    	
    	ArrayList<BrickSet> bd = new ArrayList<BrickSet>();
    	
    	// fills list of parts to delete
    	for (int i : list) {
    		bd.add(parts.get(i));
    	}
    	for (BrickSet b : bd) {
    		parts.remove(b);
    		b.delBricks(updCat);
    		b.delete();
    	}
    	fireTableDataChanged();
    }
        
    
    
    public void deleteRow(int idx, boolean updCat) throws SQLException {
    	
    	BrickSet b;
    	
    	b = parts.get(idx);
    	parts.remove(b);
    	b.delBricks(updCat);
    	b.delete();
    	fireTableRowsDeleted(idx, idx);
    	
    }
    
    
    public BrickSet getSet(int index) {
    	
    	return parts.get(index);
    }
    
    
    
	@Override
	public Object getValueAt(int arg0, int arg1) {
		if (getRowCount() == 0)
			return "";
		switch (arg1) {
		case 0:
			return parts.get(arg0).id;
		case 1:
			return parts.get(arg0).setid;
		case 2:
			return (parts.get(arg0).type == BrickSet.OFFICIAL_SET)?"Set":
				(parts.get(arg0).type == BrickSet.GENERIC_LOT)?"Lot":"MOC";
		case 3:
			return parts.get(arg0).name;
		case 4:
			return parts.get(arg0).category;
		case 5:
			return parts.get(arg0).year;
		case 6:
			return parts.get(arg0).notes;
		case 7:
			return parts.get(arg0).available;
		case 8:
			return parts.get(arg0).selected;
		case 9:
			return parts.get(arg0).lastmod;
		}
		return null;
	}



}


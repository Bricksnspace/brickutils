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





class WorkingTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 1L;
	protected ArrayList<Brick> parts;
	private boolean simple = false;
	private boolean isimport = false;
	

	private static String[] columnNames = {
			"ID",
			"Master ID",
			"LDD ID",
			"BLink ID",
			"LDraw ID",
			"Color",
			"Qty",
			"Decor. ID",
			"Description",
			"Extra",
			"Alt."
			};
	
	private static String[] columnNamesSimple = {
		"Master ID",
		"LDD ID",
		"BLink ID",
		"LDraw ID",
		"Color",
		"Qty",
		"Decor. ID",
		"Description",
		};

	/* 
	 * sets whole data model for table
	 */
	public void setParts(ArrayList<Brick> parts) {
		this.parts = parts;
		fireTableDataChanged();
	}

	
	public void setSimpleView(boolean s) {
		
		simple = s;
	}
	
	
	public void setImport(boolean setimport) {
		
		isimport = setimport;
	}
	
	
	public ArrayList<Brick> getParts() {
		return parts;
	}
	
	
	public int getPartsCount() {
		
		int i = 0;
		
		if (parts == null)
			return 0;
		for (Brick b : parts) {
			i += b.quantity;
		}
		return i;
	}
	

	@Override
	public int getColumnCount() {
		if (simple) {
			return columnNamesSimple.length;
		}
		else if (isimport) {
			return columnNames.length;
		}
		return columnNames.length-1;
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
		if (simple) {
			return false;
		}
		if (isimport) {
			if (col == 10) 
				return true;
			return false;
		}
        if (col == 0) 
            return false;
        else 
            return true;
    }
	
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class getColumnClass(int c) {

		if (simple) {
			switch (c) {
			case 0:
			case 1:
			case 2:
			case 3:
				return String.class;
			case 4:
			case 5:
				return Integer.class;
			case 6:
			case 7:
				return String.class;
			}
			return String.class;
		}
		switch (c) {
		case 0:
			return Integer.class;
		case 1:
		case 2:
		case 3:
		case 4:
			return String.class;
		case 5:
		case 6:
			return Integer.class;
		case 7:
		case 8:
			return String.class;
		case 9:
			return Boolean.class;
		case 10:
			return String.class;
		}
		return String.class;
    }

	
	@Override
	public String getColumnName(int col) {
		if (simple) {
			return columnNamesSimple[col];
		}
        return columnNames[col];
    }

	
    @SuppressWarnings("boxing")
	public void setValueAt(Object value, int row, int col) {
    	
    	if (simple) {
    		return;
    	}
    	Brick b = new Brick(parts.get(row));
    	
		switch (col) {
		case 1:
			b.masterID = (String) value;
			break;
		case 2:
			b.designID = (String) value;
			break;
		case 3:
			b.blID = (String) value;
			break;
		case 4:
			b.ldrawID = (String) value;
			break;
		case 5:
			b.color = (Integer) value;
			break;
		case 6:
			b.quantity = (Integer) value;
			break;
		case 7:
			b.decorID = (String) value;
			break;
		case 8:
			b.name = (String) value;
			break;
		case 9:
			b.extra = (Boolean) value;
			break;
		}
		
		try {
			b.updateWork();
		} catch (SQLException e) {
			;
		}
		parts.set(row, b);
        fireTableRowsUpdated(row, row);
     }

    
    
    public int addRow(Brick b) throws SQLException {
    	
    	b.color = 1;
    	int idx;
    	
    	b.id = b.insertWork();
    	parts.add(b);
    	idx = parts.indexOf(b);
    	fireTableRowsInserted(idx,idx);
    	return idx;
    }
	
    
    
    public int dupRow(int index) throws SQLException {
    	
    	Brick b;
    	int idx;
    	
    	b = new Brick(parts.get(index));
    	b.id = b.insertWork();
    	
    	parts.add(b);
    	idx = parts.indexOf(b);
    	fireTableRowsInserted(0,parts.size()-1);
    	return idx;

    }
    
    
    
    public void deleteRows(int[] list) throws SQLException {
    	
    	ArrayList<Brick> bd = new ArrayList<Brick>();
    	
    	// fills list of parts to delete
    	for (int i : list) {
    		bd.add(parts.get(i));
    	}
    	for (Brick b : bd) {
    		parts.remove(b);
    		b.deleteWork();
    	}
    	fireTableDataChanged();
    }
        
    
    
    public void deleteRow(int idx) throws SQLException {
    	
    	Brick b;
    	
    	b = parts.get(idx);
    	parts.remove(b);
    	b.deleteWork();
    	fireTableRowsDeleted(idx, idx);
    	
    }
    
    
    public Brick getBrick(int index) {
    	
    	return parts.get(index);
    }
    
    
    
    /* use only for alternate brick select !!! */
    public void setBrick(int index, Brick b) {
    	
    	parts.set(index, b);
    	fireTableRowsUpdated(index,index);
    }
    
    
    public void changeBrick(int index, Brick b) throws SQLException {
    	
    	Brick old = parts.get(index);
    	// holds color, quantity, ID
    	b.color = old.color;
    	b.quantity = old.quantity;
    	b.id = old.id;
    	b.alt = old.alt;
    	b.matchid = old.matchid;
    	b.extra = old.extra;
    	b.updateWork();
    	parts.set(index, b);
    	fireTableRowsUpdated(index,index);
    }
    
	
	@SuppressWarnings("boxing")
	@Override
	public Object getValueAt(int arg0, int arg1) {
		if (getRowCount() == 0)
			return "";
		if (simple) {
			switch (arg1) {
			case 0:
				return parts.get(arg0).masterID;
			case 1:
				return parts.get(arg0).designID;
			case 2:
				return parts.get(arg0).blID;
			case 3:
				return parts.get(arg0).ldrawID;
			case 4:
				return parts.get(arg0).color;
			case 5:
				return parts.get(arg0).quantity;
			case 6:
				return parts.get(arg0).decorID;
			case 7:
				return parts.get(arg0).name;
			}
			return null;
		}
		switch (arg1) {
		case 0:
			return parts.get(arg0).id;
		case 1:
			return parts.get(arg0).masterID;
		case 2:
			return parts.get(arg0).designID;
		case 3:
			return parts.get(arg0).blID;
		case 4:
			return parts.get(arg0).ldrawID;
		case 5:
			return parts.get(arg0).color;
		case 6:
			return parts.get(arg0).quantity;
		case 7:
			return parts.get(arg0).decorID;
		case 8:
			return parts.get(arg0).name;
		case 9:
			return parts.get(arg0).extra;
		case 10:
			return parts.get(arg0).matchid;
		}
		return null;
	}



}


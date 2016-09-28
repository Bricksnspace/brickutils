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





class CatalogTableModel extends WorkingTableModel {
	
	private static final long serialVersionUID = 2L;
	

	private static String[] columnNames = {
			"ID",
			"Master ID",
			"LDD ID",
			"BLink ID",
			"LDraw ID",
			"Color",
			"Qty",
			"Decor. ID",
			"Description"
			};
	
	/* 
	 * sets whole data model for table
	 */
	public void setParts(ArrayList<Brick> parts) {
		this.parts = parts;
		fireTableDataChanged();
	}

	
	public ArrayList<Brick> getParts() {
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
        if (col == 8) 
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

        return columnNames[col];
    }

	
    @SuppressWarnings("boxing")
	public void setValueAt(Object value, int row, int col) {
    	
    	Brick b = new Brick(parts.get(row));
    	
		switch (col) {
		case 6:
			b.quantity = (Integer) value;
			break;
		case 8:
			b.name = (String) value;
			break;
		}
		
		try {
			b.catalogUpd();
		} catch (SQLException e) {
			;
		}
		parts.set(row, b);
        fireTableRowsUpdated(row, row);
     }

    
    @Override
    public void deleteRows(int[] list) throws SQLException {
    	
    	ArrayList<Brick> bd = new ArrayList<Brick>();
    	
    	// fills list of parts to delete
    	for (int i : list) {
    		bd.add(parts.get(i));
    	}
    	for (Brick b : bd) {
    		if (b.quantity != 0)
    			continue;
    		parts.remove(b);
    		b.deleteCatalog();
    	}
    	fireTableDataChanged();
    }
        
    
    
    @Override
    public int addRow(Brick b) throws SQLException {
    	
    	return 0;
    }
	
    
    @Override
    public int dupRow(int index) throws SQLException {
    	
    	return 0;
    }
    
       

    @Override
    public void deleteRow(int idx) throws SQLException {
    	
    	Brick b;
    	
    	b = parts.get(idx);
    	if (b.quantity != 0)
    		return;
    	parts.remove(b);
    	b.deleteCatalog();
    	fireTableRowsDeleted(idx, idx);
    	
    }
    
    
    
    public Brick getBrick(int index) {
    	
    	return parts.get(index);
    }
    
    
    
	@SuppressWarnings("boxing")
	@Override
	public Object getValueAt(int arg0, int arg1) {
		if (getRowCount() == 0)
			return "";
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
		}
		return null;
	}



}


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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.StartElement;

import bricksnspace.appsettings.AppSettings;



public class BrickSet {

	public int id;
	public String setid;
	public int type;		// moc, lot, official, ecc.
	public String name;
	public String category;
	public int year;
	public int catid;
	public String notes;
	public boolean available;
	public boolean selected;
	public Timestamp lastmod;

	private static BrickDB db;
	private static PreparedStatement insSetPS = null;
	private static PreparedStatement delSetPS = null;
	private static PreparedStatement updSetPS = null;
	private static PreparedStatement insBrickPS = null;
	//private static PreparedStatement updBrickPS = null;
	private static PreparedStatement delBrickPS = null;
	public final static String fieldsSetOrder = "setid,type,name,category,year,catid,notes,available,selected,lastmod";
	public final static String fieldsBrickOrder = "setid,brickid,quantity"; 
	
	public static final int OFFICIAL_SET = 1;
	public static final int GENERIC_LOT = 2;
	public static final int MOC = 3;
	
	public static final String setTable = "myset";
	public static final String brickTable = "brickset";
	
	
	
	
	public BrickSet() {
		id = 0;
		setid = "";
		type = OFFICIAL_SET;
		name = "";
		category = "";
		catid = 0;
		year = 0;
		notes = "";
		available = true;
		selected = false;
	}
	

	public BrickSet(BrickSet set) {
		id = set.id;
		setid = set.setid;
		type = set.type;
		name = set.name;
		category = set.category;
		year = set.year;
		catid = set.catid;
		notes = set.notes;
		available = set.available;
		selected = set.selected;
		lastmod = set.lastmod;
	}
	
	
	@Override
	public String toString() {
		return "BrickSet [id=" + id + ", setid=" + setid + ", type=" + type
				+ ", name=" + name + ", category=" + category + ", year="
				+ year + ", catid=" + catid + ", notes=" + notes
				+ ", available=" + available + ", selected=" + selected
				+ ", lastmod=" + lastmod + "]";
	}

	public int getId() {
		return id;
	}


	public String getSetid() {
		return setid;
	}


	public int getType() {
		return type;
	}


	public String getName() {
		return name;
	}


	public String getCategory() {
		return category;
	}


	public int getYear() {
		return year;
	}


	public int getCatid() {
		return catid;
	}


	public String getNotes() {
		return notes;
	}


	public boolean isAvailable() {
		return available;
	}


	public static void setDb(BrickDB bdb) throws SQLException {

		db = bdb;
		// prepared statement
		insSetPS = db.conn.prepareStatement("INSERT INTO "+setTable+" ("+fieldsSetOrder+
				") VALUES (?,?,?,?,?,?,?,?,?,NOW())"
				);
		updSetPS = db.conn.prepareStatement("UPDATE "+setTable+" SET " +
				"setid=?," +
				"type=?," +
				"name=?," +
				"category=?," +
				"year=?," +
				"catid=?," +
				"notes=?," +
				"available=?," +
				"selected=? " +
				" WHERE id=?");
		delSetPS = db.conn.prepareStatement("DELETE FROM "+setTable+" where id=?");
		
		// brick/set table
		insBrickPS  = db.conn.prepareStatement("INSERT INTO "+brickTable+" ("+fieldsBrickOrder+
				") VALUES (?,?,?)"
				);
//		updBrickPS = db.conn.prepareStatement("UPDATE "+brickTable+" SET " +
//				"setid=?," +
//				"brickid=?," +
//				"quantity=? " +
//				" WHERE id=?");
		delBrickPS = db.conn.prepareStatement("DELETE FROM "+brickTable+" where setid=?");
	}
	

	public static void createTable() throws SQLException {
		
		Statement st;
		
		st = db.conn.createStatement();
		//setid,type,name,category,year,catid,notes,available,selected,lastmod"
		st.execute("DROP TABLE IF EXISTS "+setTable+"; " +
				"CREATE TABLE "+setTable+" (" +
				"id INT PRIMARY KEY AUTO_INCREMENT," +
				"setid VARCHAR(64)," +
				"type INT," +
				"name VARCHAR(255)," +
				"category VARCHAR(255)," +
				"year INT," +
				"catid INT," +
				"notes VARCHAR(255)," +
				"available BOOL," +
				"selected BOOL," +
				"lastmod TIMESTAMP" +
				"); COMMIT ");
		st.execute("DROP TABLE IF EXISTS "+brickTable+"; " +
				"CREATE TABLE "+brickTable+" (" +
				"id INT PRIMARY KEY AUTO_INCREMENT," +
				"setid INT," +
				"brickid INT," +
				"quantity INT" +
				"); COMMIT ");
	}


	public int insert() throws SQLException {
		
		ResultSet rs;
		
		insSetPS.setString(1, setid);
		insSetPS.setInt(2, type);
		insSetPS.setString(3, name);
		insSetPS.setString(4, category);
		insSetPS.setInt(5, year);
		insSetPS.setInt(6, catid);
		insSetPS.setString(7, notes);
		insSetPS.setBoolean(8, available);
		insSetPS.setBoolean(9, selected);
		
		insSetPS.executeUpdate();
		rs = insSetPS.getGeneratedKeys();
		rs.next();
		id = rs.getInt(1);
		return id;
	}

	
	public void update() throws SQLException {
		
		updSetPS.setString(1, setid);
		updSetPS.setInt(2, type);
		updSetPS.setString(3, name);
		updSetPS.setString(4, category);
		updSetPS.setInt(5, year);
		updSetPS.setInt(6, catid);
		updSetPS.setString(7, notes);
		updSetPS.setBoolean(8, available);
		updSetPS.setBoolean(9, selected);
		updSetPS.setInt(10, id);
		
		updSetPS.executeUpdate();
	}
	
	
	public void delete() throws SQLException {
		
		delSetPS.setInt(1, id);
		delSetPS.executeUpdate();
	}
	
	
	public void addBrick(Brick b) throws SQLException {
		
		int catBrickId = 0;
		
		if (type == MOC) {
			// it is a MOC, bricks are subtracted
			b.quantity = -b.quantity;
		}
		// add and retrieve catalog id for brick
		catBrickId = b.catalogAdd();
		insBrickPS.setInt(1, id);
		insBrickPS.setInt(2, catBrickId);
		insBrickPS.setInt(3, b.quantity);
		insBrickPS.executeUpdate();
	}
	
	
	public void delBricks(boolean updateCatalog) throws SQLException {
		
		if (updateCatalog) {
			// also remove bricks from catalog
			PreparedStatement ps = db.conn.prepareStatement("SELECT brickid,quantity FROM "+
					brickTable+" WHERE setid=?");
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int brickid = rs.getInt("brickid");
				int qty = rs.getInt("quantity");
				// remove bricks from catalog
				Brick.catDelSet(brickid, qty);
			}
		}
		// remove bricks from brick-set list
		delBrickPS.setInt(1, id);
		delBrickPS.executeUpdate();
		
	}
	
	
	
	
	
	public static ArrayList<BrickSet> getPS(PreparedStatement ps) throws SQLException {
		
		ResultSet rs = ps.executeQuery();
		ArrayList<BrickSet> bsl = new ArrayList<BrickSet>();
		while (rs.next()) {
			BrickSet bs = new BrickSet();
			bs.id = rs.getInt("id");
			bs.setid = rs.getString("setid");
			bs.type = rs.getInt("type");
			bs.name = rs.getString("name");
			bs.category = rs.getString("category");
			bs.year = rs.getInt("year");
			bs.catid = rs.getInt("catid");
			bs.notes= rs.getString("notes");
			bs.available = rs.getBoolean("available");
			bs.selected = rs.getBoolean("selected");
			bs.lastmod = rs.getTimestamp("lastmod");
			bsl.add(bs);
		}
		return bsl;
	}
	
	
	
	public static ArrayList<BrickSet> getByBrick(int brickid) throws SQLException {
		
		PreparedStatement ps;
		
		ps = db.conn.prepareStatement("SELECT DISTINCT id,"+fieldsSetOrder+" FROM "+setTable+" WHERE " +
				"id IN (SELECT setid FROM "+brickTable+" WHERE brickid=?)");
		ps.setInt(1, brickid);
		return getPS(ps);
	}
	
	
	public static void clearSelection() throws SQLException {
		
		PreparedStatement ps;
		
		ps = db.conn.prepareStatement("UPDATE "+setTable+" SET selected=false");
		ps.execute();
	}
	
	
	public void select() throws SQLException {
		
		PreparedStatement ps;
		
		ps = db.conn.prepareStatement("UPDATE "+setTable+" SET selected=true WHERE id=?");
		ps.setInt(1, id);
		ps.execute();
	}
	
	
	public static ArrayList<BrickSet> get() throws SQLException {
		
		PreparedStatement ps;
		int index = 0;
		
		index = AppSettings.getInt(MySettings.CURRENT_SET);
		ps = db.conn.prepareStatement("SELECT id,"+fieldsSetOrder+" FROM "+setTable+" WHERE id!=?");
		ps.setInt(1, index);
		return getPS(ps);

	}
	
	
	public static BrickSet getById(int id) throws SQLException {
		
		PreparedStatement ps;
		ArrayList<BrickSet> bs;
		
		ps = db.conn.prepareStatement("SELECT id,"+fieldsSetOrder+" FROM "+setTable+" WHERE id=?");
		ps.setInt(1, id);
		bs = getPS(ps);
		if (bs.size() == 0) {
			return null;
		}
		else {
			return bs.get(0);
		}
		
	}
	
	
	public void getBricksWork() throws SQLException {

		PreparedStatement ps;
		
		ps = db.conn.prepareStatement("SELECT "+fieldsBrickOrder+" FROM "+brickTable+" WHERE setid=?");
		ps.setInt(1, id);
		ResultSet rs = ps.executeQuery();
		int id, qty;
		while (rs.next()) {
			id = rs.getInt("brickid");
			qty = rs.getInt("quantity");
			if (type == MOC) {
				qty = -qty;
			}
			Brick.catalog2work(id, qty);
		}
	}
	
	
	
	
	public static BrickSet getCurrent() throws SQLException {

		int index;
		BrickSet bs;
		index = AppSettings.getInt(MySettings.CURRENT_SET);
		if (index == 0) 
			return null;
		bs = BrickSet.getById(index);
		return bs;
	}

	
	
	///////////////////////////////////////////
	// XML I/O helper methods
	///////////////////////////////////////////

	
	
	public BrickSet(StartElement xse) {
		
		try {
			id = Integer.parseInt(xse.getAttributeByName(new QName("id")).getValue());
			setid = xse.getAttributeByName(new QName("setid")).getValue();
			String s = xse.getAttributeByName(new QName("type")).getValue();
			if (s.equals("1"))
				type = OFFICIAL_SET;
			else if (s.equals("2"))
				type = GENERIC_LOT;
			else if (s.equals("3"))
				type = MOC;
			name = xse.getAttributeByName(new QName("name")).getValue();
			category = xse.getAttributeByName(new QName("category")).getValue();
			catid = Integer.parseInt(xse.getAttributeByName(new QName("catid")).getValue());;
			year = Integer.parseInt(xse.getAttributeByName(new QName("year")).getValue());;
			notes = xse.getAttributeByName(new QName("notes")).getValue();
			available = xse.getAttributeByName(new QName("available")).getValue().equals("1");
			selected = xse.getAttributeByName(new QName("selected")).getValue().equals("1");
    	} catch (NumberFormatException | NullPointerException e) {
    		throw new IllegalArgumentException("Error in BrickUtils backup file format. Opening tag:\n"+xse,e);
    	}
	}
	


	public void XMLWrite(XMLStreamWriter xsw) throws XMLStreamException,SQLException {
		
		PreparedStatement ps;
		
		xsw.writeStartElement("set");
		xsw.writeAttribute("id",Integer.toString(id));
		xsw.writeAttribute("setid",setid);
		xsw.writeAttribute("type",Integer.toString(type));
		xsw.writeAttribute("name",name);
		xsw.writeAttribute("category",category);
		xsw.writeAttribute("catid",Integer.toString(catid));
		xsw.writeAttribute("year",Integer.toString(year));
		xsw.writeAttribute("notes",notes);
		xsw.writeAttribute("available",available?"1":"0");
		xsw.writeAttribute("selected",selected?"1":"0");
		xsw.writeCharacters("\n");
		ps = db.conn.prepareStatement("SELECT "+fieldsBrickOrder+" FROM "+brickTable+" WHERE setid=?");
		ps.setInt(1, id);
		ResultSet rs = ps.executeQuery();
		int brickid, qty;
		while (rs.next()) {
			brickid = rs.getInt("brickid");
			qty = rs.getInt("quantity");
			Brick b = Brick.catalogById(brickid);
			b.quantity = qty;
			b.XMLWrite(xsw);
		}
		xsw.writeEndElement();
		xsw.writeCharacters("\n");
	}

	
	

}

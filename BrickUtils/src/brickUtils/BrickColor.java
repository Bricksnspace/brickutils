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

import java.awt.Color;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.StartElement;

import bricksnspace.dbconnector.DBConnector;


/* 
 * A brick "color" in LDD, BL and LDraw catalogs, with helpers for display 
 * color sample and conversions between catalogs 
 */



public class BrickColor {
	public int mapid;
	public int ldd;		// master ID for color
	public int bl;
	public int ldraw;
	public Color color;
	public boolean inProduction;
	public boolean metallic;
	public boolean transparent;
	public boolean glitter;
	public String lddName;
	public int colorGroup;	// index in ColorGroup class
	public String notes;
	public Timestamp lastmod;

	public static final String fieldsOrder = "ldd,bl,ldraw,r,g,b,a,inuse,metal,transparent,glitter,lddname,colgrp,notes,lastmod";
	public static final String table = "colors";
	private static DBConnector db;
	private static PreparedStatement insertPS = null;
	private static PreparedStatement updatePS = null;
	private static PreparedStatement deletePS = null;
	private static HashMap<Integer,BrickColor>colorMap;
	
	@Override
	public String toString() {
		return "BrickColor [mapid=" + mapid +", ldd=" + ldd + ", color=" + color
				+ ", inProduction=" + inProduction + ", metallic=" + metallic + ", transparent=" + transparent 
				+ ", glitter=" + glitter + ", lddName=" + lddName
				+ ", lastmod=" + lastmod + "]";
	}

	
	public BrickColor() {

		color = Color.BLACK;
		inProduction = false;
		metallic = false;
		transparent = false;
		glitter = false;
		lddName = "";
		notes = "";
	}
	
	public BrickColor(BrickColor bc) {
		
		mapid = bc.mapid;
		ldd = bc.ldd;
		bl = bc.bl;
		ldraw = bc.ldraw;
		color = new Color(bc.color.getRed(),
				bc.color.getGreen(),
				bc.color.getBlue(),
				bc.color.getAlpha());
		inProduction = bc.inProduction;
		metallic = bc.metallic;
		transparent = bc.transparent;
		glitter = bc.glitter;
		lddName = bc.lddName;
		colorGroup = bc.colorGroup;
		notes = bc.notes;
		lastmod = bc.lastmod;
	}

	
	
	public BrickColor(StartElement xsr) {
		
		try {
			mapid = Integer.parseInt(xsr.getAttributeByName(new QName("id")).getValue());
			ldd = Integer.parseInt(xsr.getAttributeByName(new QName("ldd")).getValue());
			bl = Integer.parseInt(xsr.getAttributeByName(new QName("bl")).getValue());
			ldraw = Integer.parseInt(xsr.getAttributeByName(new QName("ldraw")).getValue());
			color = new Color(Integer.parseInt(xsr.getAttributeByName(new QName("r")).getValue()),
					Integer.parseInt(xsr.getAttributeByName(new QName("g")).getValue()),
					Integer.parseInt(xsr.getAttributeByName(new QName("b")).getValue()),
					Integer.parseInt(xsr.getAttributeByName(new QName("a")).getValue()));
			inProduction = xsr.getAttributeByName(new QName("inuse")).getValue().equals("1");
			metallic = xsr.getAttributeByName(new QName("metallic")).getValue().equals("1");
			transparent = xsr.getAttributeByName(new QName("transparent")).getValue().equals("1");
			glitter = xsr.getAttributeByName(new QName("glitter")).getValue().equals("1");
			lddName = xsr.getAttributeByName(new QName("lddname")).getValue();
			colorGroup = Integer.parseInt(xsr.getAttributeByName(new QName("group")).getValue());
			notes = xsr.getAttributeByName(new QName("notes")).getValue();
			lastmod = Timestamp.valueOf(xsr.getAttributeByName(new QName("lastmod")).getValue());
		} catch (NumberFormatException | NullPointerException e) {
			throw new IllegalArgumentException("Error in file format. Opening tag:\n"+xsr,e);
		}
	}

	
	
	public static void setDb(DBConnector bdb) throws SQLException {

		db = bdb;
		// prepared statements
		insertPS = db.prepareStatement("INSERT INTO "+table+" " +
				"("+fieldsOrder+") VALUES " +
				"(?,?,?,?,?,?,?,?,?,?,?,?,?,?,NOW())" +
				";",Statement.RETURN_GENERATED_KEYS);
		updatePS = db.prepareStatement("UPDATE "+table+" SET " +
				"ldd=?," +
				"bl=?," +
				"ldraw=?," +
				"r=?," +
				"g=?," +
				"b=?," +
				"a=?," +
				"inuse=?," +
				"metal=?," +
				"transparent=?," +
				"glitter=?," +
				"lddname=?," +
				"colgrp=?," +
				"notes=?," +
				"lastmod=NOW() " +
				"WHERE mapid=?");
		deletePS = db.prepareStatement("DELETE FROM "+table+" " +
				"WHERE mapid=?; COMMIT ");
		colorMap = getAllColor();
		
	}
	
	public static void createTable() throws SQLException {
		
		Statement st;
		
		st = db.createStatement();
		st.execute("DROP TABLE IF EXISTS "+table+"; " +
				"CREATE TABLE "+table+" (" +
				"mapid INT PRIMARY KEY AUTO_INCREMENT," +
				"ldd INT UNIQUE, " +
				"bl INT," +
				"ldraw INT," +
				"r INT," +
				"g INT," +
				"b INT," +
				"a INT," +
				"inuse BOOL," +
				"metal BOOL," +
				"transparent BOOL," +
				"glitter BOOL," +
				"lddname VARCHAR(255)," +
				"colgrp VARCHAR(64)," +
				"notes VARCHAR(255)," +
				"lastmod TIMESTAMP" +
				"); COMMIT ");
	}
	
	
	public int insert() throws SQLException {

		ResultSet rs;
		
		insertPS.setInt(1, ldd);
		insertPS.setInt(2, bl);
		insertPS.setInt(3, ldraw);
		insertPS.setInt(4, color.getRed());
		insertPS.setInt(5, color.getGreen());
		insertPS.setInt(6, color.getBlue());
		insertPS.setInt(7, color.getAlpha());
		insertPS.setBoolean(8, inProduction);
		insertPS.setBoolean(9, metallic);
		insertPS.setBoolean(10, transparent);
		insertPS.setBoolean(11, glitter);
		insertPS.setString(12, lddName);
		insertPS.setInt(13, colorGroup);
		insertPS.setString(14, notes);
		
		insertPS.executeUpdate();
		
		rs = insertPS.getGeneratedKeys();
		rs.next();
		mapid = rs.getInt(1);
		return mapid;
		
	}

	
	public void update() throws SQLException {

		updatePS.setInt(1, ldd);
		updatePS.setInt(2, bl);
		updatePS.setInt(3, ldraw);
		updatePS.setInt(4, color.getRed());
		updatePS.setInt(5, color.getGreen());
		updatePS.setInt(6, color.getBlue());
		updatePS.setInt(7, color.getAlpha());
		updatePS.setBoolean(8, inProduction);
		updatePS.setBoolean(9, metallic);
		updatePS.setBoolean(10, transparent);
		updatePS.setBoolean(11, glitter);
		updatePS.setString(12, lddName);
		updatePS.setInt(13, colorGroup);
		updatePS.setString(14, notes);
		updatePS.setInt(15, mapid);
		
		updatePS.executeUpdate();
		
	}
	
	
	public void delete() throws SQLException {
		
		deletePS.setInt(1, mapid);
		
		deletePS.executeUpdate();
	}
	
	

	
	public static ArrayList<BrickColor> get(String filterExpr) throws SQLException {
		
		ArrayList<BrickColor> brc = new ArrayList<BrickColor>();
		BrickColor bc;
		Statement st;
		ResultSet rs;
		
		if (filterExpr == null) {
			st = db.createStatement();
			rs = st.executeQuery("SELECT mapid,"+fieldsOrder+" FROM "+table+"");
		}
		else {
			st = db.createStatement();
			rs = st.executeQuery("SELECT mapid," + fieldsOrder +
				" FROM "+table+" WHERE " + filterExpr);
		}
		while (rs.next()) {
			// fetch and assign rows to an Array list
			//ldd,bl,ldraw,r,g,b,a,inuse,metal,glitter,lddname,colgrp,notes
			bc = new BrickColor();
			bc.mapid = rs.getInt("mapid");
			bc.ldd = rs.getInt("ldd");
			bc.bl = rs.getInt("bl");
			bc.ldraw = rs.getInt("ldraw");
			bc.color = new Color(rs.getInt("r"),rs.getInt("g"),rs.getInt("b"),rs.getInt("a"));
			bc.inProduction = rs.getBoolean("inuse");
			bc.metallic = rs.getBoolean("metal");
			bc.transparent = rs.getBoolean("transparent");
			bc.glitter = rs.getBoolean("glitter");
			bc.lddName = rs.getString("lddname");
			bc.colorGroup = rs.getInt("colgrp");
			bc.notes = rs.getString("notes");
			bc.lastmod = rs.getTimestamp("lastmod");
			brc.add(bc);
		}
		return brc;
		
	}
	
	
	
	public static HashMap<Integer,BrickColor> getAllColor() throws SQLException {
		
		HashMap<Integer,BrickColor> allColor = new HashMap<Integer,BrickColor>();
		ArrayList<BrickColor> bc;
		
		bc = get(null);
		for (BrickColor b : bc) {
			allColor.put(b.mapid, b);
		}
		BrickColor b = new BrickColor();
		b.mapid = 0;
		b.lddName = "Unknown";
		allColor.put(0, b);
		return allColor;
	}
	
	
	public static ArrayList<BrickColor> getPS(PreparedStatement ps) throws SQLException {
		
		ArrayList<BrickColor> brc = new ArrayList<BrickColor>();
		BrickColor bc;
		ResultSet rs;
		
		rs = ps.executeQuery();
		while (rs.next()) {
			// fetch and assign rows to an Array list
			//ldd,bl,ldraw,r,g,b,a,inuse,metal,glitter,lddname,colgrp,notes
			bc = new BrickColor();
			bc.mapid = rs.getInt("mapid");
			bc.ldd = rs.getInt("ldd");
			bc.bl = rs.getInt("bl");
			bc.ldraw = rs.getInt("ldraw");
			bc.color = new Color(rs.getInt("r"),rs.getInt("g"),rs.getInt("b"),rs.getInt("a"));
			bc.inProduction = rs.getBoolean("inuse");
			bc.metallic = rs.getBoolean("metal");
			bc.transparent = rs.getBoolean("transparent");
			bc.glitter = rs.getBoolean("glitter");
			bc.lddName = rs.getString("lddname");
			bc.colorGroup = rs.getInt("colgrp");
			bc.notes = rs.getString("notes");
			bc.lastmod = rs.getTimestamp("lastmod");
			brc.add(bc);
		}
		return brc;
		
	}
	
	

	public void check() throws SQLException {
		
		Statement st;
		ResultSet rs;
		
		st = db.createStatement();
		rs = st.executeQuery("SELECT mapid,"+fieldsOrder+" FROM "+table+" " +
				"WHERE ((ldd = "+ldd+" AND "+ldd+"!=0) " +
				"OR " +
				"(bl = "+bl+" AND "+bl +"!=0) " +
				"OR " +
				"(ldraw = "+ldraw+" AND "+ldraw+"!=-1)) " +
				"AND " +
				"mapid !="+mapid);
		if (rs.next()) {
			int lddid = rs.getInt("ldd");
			int blid = rs.getInt("bl");
			int ldrawid = rs.getInt("ldraw");
			throw new SQLException(
					"Duplicated color definition. Color:\n" +
					"Ldd="+ldd+" Bl="+bl+" LDraw="+ldraw +"\n" +
					"is already defined as:\n" +
					"Ldd="+lddid+" Bl="+blid+" LDraw="+ldrawid);
		}
	}
	
	
	public static BrickColor getLdrawColor(int id) {
		
		ArrayList<BrickColor> bc;
		
		try {
			bc = get("ldraw="+id);
			if (bc.size() >= 1) {
				return bc.get(0);
			}
			else return null;
		} catch (SQLException e) {
			return null;
		}
	}

	
	

	public String getLddName() {
		return lddName;
	}


	public static Timestamp[] getLastModifyTime() throws SQLException {
		
		PreparedStatement ps;
		ResultSet rs;
		Timestamp last[] = new Timestamp[5];
		
		ps = db.prepareStatement("SELECT FORMATDATETIME(lastmod,'yyyy-MM-dd') as datemod "+
				" FROM "+table+" GROUP BY datemod ORDER BY datemod DESC LIMIT 5");
		rs = ps.executeQuery();
		int i = 0;
		while (rs.next()) {
			last[i] = Timestamp.valueOf(rs.getString("datemod")+" 00:00:01");
			i++;
			if (i >= 5)
				break;
		}
		return last;
	}

	
	public static ArrayList<BrickColor> getModifiedAfter(Timestamp lastmodified) throws SQLException {
		
		PreparedStatement ps;
		ps = db.prepareStatement("SELECT mapid,"+fieldsOrder+" FROM "+table+
				" where lastmod>=?");
		ps.setTimestamp(1, lastmodified);
		return getPS(ps);
	}
    

	public static int getMapByLdd(int lddid) {
		
		ArrayList<BrickColor> bc;
		
		try {
			bc = get("ldd="+lddid);
			//System.out.println(id + " " + bc);
			if (bc.size() >= 1) {
				return bc.get(0).mapid;
			}
			else return -1;
		} catch (SQLException e) {
			return -1;
		}
	}

	
	
	public static int getMapByBl(int blcolor) throws SQLException {
		
		PreparedStatement ps;
		ArrayList<BrickColor> bc;
		
		ps = db.prepareStatement("SELECT mapid,"+fieldsOrder+" FROM "+table+" WHERE bl=?");
		ps.setInt(1,blcolor);
		bc = getPS(ps);
		if (bc.size() != 1) {
			return 0;
		}
		else {
			return bc.get(0).mapid;
		}
	}
	
	
	public static int getMapByLdr(int ldrcolor) throws SQLException {
		
		PreparedStatement ps;
		ArrayList<BrickColor> bc;
		
		ps = db.prepareStatement("SELECT mapid,"+fieldsOrder+" FROM "+table+" WHERE ldraw=?");
		ps.setInt(1,ldrcolor);
		bc = getPS(ps);
		if (bc.size() != 1) {
			return 0;
		}
		else {
			return bc.get(0).mapid;
		}
	}
	
	
	public static BrickColor getColor(int mapid) {
		
		BrickColor bc;
		
		bc = colorMap.get(mapid);
		if (bc == null) 
			return colorMap.get(0);
		else 
			return bc;
	}
	
	
	public static Set<Integer> getColorList() {
		
		return colorMap.keySet();
	}
	

	
	///////////////////////////////////////////
	// statistics
	///////////////////////////////////////////
	
	
	public static int getRules() {

		Statement st;
		ResultSet rs;
		
		try {
			st = db.createStatement();
			rs = st.executeQuery("SELECT COUNT(*) FROM " + table);
			rs.next();
			return rs.getInt(1);
		} catch (SQLException e) {
			return -1;
		}
	}



	///////////////////////////////////////////
	// XML I/O & update helper methods
	///////////////////////////////////////////

	public void XMLWrite(XMLStreamWriter xsw) throws XMLStreamException {
		
		xsw.writeStartElement("colormap");
		xsw.writeAttribute("id",Integer.toString(mapid));
		xsw.writeAttribute("ldd",Integer.toString(ldd)); 
		xsw.writeAttribute("bl",Integer.toString(bl));
		xsw.writeAttribute("ldraw",Integer.toString(ldraw));
		xsw.writeAttribute("r",Integer.toString(color.getRed()));
		xsw.writeAttribute("g",Integer.toString(color.getGreen()));
		xsw.writeAttribute("b",Integer.toString(color.getBlue()));	
		xsw.writeAttribute("a",Integer.toString(color.getAlpha()));
		xsw.writeAttribute("inuse",inProduction?"1":"0");
		xsw.writeAttribute("metallic",metallic?"1":"0");
		xsw.writeAttribute("transparent",transparent?"1":"0");
		xsw.writeAttribute("glitter",transparent?"1":"0");
		xsw.writeAttribute("lddname",lddName);
		xsw.writeAttribute("group",Integer.toString(colorGroup));
		xsw.writeAttribute("notes",notes);
		xsw.writeAttribute("lastmod",lastmod.toString());
		xsw.writeEndElement();
		xsw.writeCharacters("\n");
	}

	
	public void updateMapping() throws SQLException {
		
		PreparedStatement ps;
		ResultSet rs;
		
		ps = db.prepareStatement("SELECT * FROM "+table+" where mapid=?");
		ps.setInt(1, mapid);
		rs = ps.executeQuery();
		if (rs.next()) {
			update();
		}
		else {
			insert();
		}
	}
	


	
	
}

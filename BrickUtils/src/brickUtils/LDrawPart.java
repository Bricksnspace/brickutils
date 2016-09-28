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

public class LDrawPart {

	public int id;
	public String ldrid;
	public String name;
	public String category;
	public String keywords;
	public boolean deleted;
	public boolean official;
	public Timestamp lastmod;	// last modified
	private static PreparedStatement insertPS = null;
	private static PreparedStatement updatePS = null;
	public final static String fieldsOrder = "ldrid,name,category,keywords,deleted,official,lastmod";
	public final static String table = "ldrparts";

	private static BrickDB db;
	

	
	public LDrawPart() {
		;
	}

	@Override
	public String toString() {
		return "LDrawPart [id=" + id + ", ldrid=" + ldrid + ", name=" + name
				+ ", category=" + category + ", keywords=" + keywords
				+ ", deleted=" + deleted + ", official=" + official
				+ ", lastmod=" + lastmod + "]";
	}
	

	public static void setDb(BrickDB bdb) throws SQLException {

		db = bdb;
		insertPS = db.conn.prepareStatement("INSERT INTO "+table+
				" ("+fieldsOrder+") VALUES " +
				"(?,?,?,?,?,?,NOW())" +
				";",Statement.RETURN_GENERATED_KEYS);
		// no update time, part is "lastmod" when inserted first time
		updatePS = db.conn.prepareStatement("UPDATE "+table+" SET " +
				"ldrid=?," +
				"name=?," +
				"category=?," +
				"keywords=?," +
				"deleted=?," +
				"official=? " +
				"WHERE id=?" +
				";");
	}
	
	

	
	public static void createTable() throws SQLException {
		
		Statement st;
		
		st = db.conn.createStatement();
		try {
			// drop old index, if exists
			st.execute("CALL FTL_DROP_INDEX('PUBLIC','LDRPARTS') ");
		}
		catch (SQLException e) {
			;
		}
		st.execute("DROP TABLE IF EXISTS "+table+"; " +
				"CREATE TABLE "+table+" (" +
				"id INT PRIMARY KEY AUTO_INCREMENT, " +
				"ldrid VARCHAR(64)," +
				"name VARCHAR(255)," +
				"category VARCHAR(255)," +
				"keywords VARCHAR(255)," +
				"deleted BOOL," +
				"official BOOL," +
				"lastmod TIMESTAMP" +
				"); COMMIT ");
		
	}

	
	public static void createFTS() throws SQLException {
		
		Statement st;
		
		st = db.conn.createStatement();
		try {
			// drop old index, if exists
			st.execute("CALL FTL_DROP_INDEX('PUBLIC','LDRPARTS') ");
		}
		catch (SQLException e) {
			;
		}
		st.execute("CALL FTL_CREATE_INDEX('PUBLIC','LDRPARTS','LDRID,NAME,CATEGORY,KEYWORDS')");
		
	}

	public static void deleteFTS() {
		Statement st;
		
		try {
			st = db.conn.createStatement();
			// drop old index, if exists
			st.execute("CALL FTL_DROP_INDEX('PUBLIC','LDRPARTS') ");
		}
		catch (SQLException e) {
			;
		}

	}
	
	
	public void update() throws SQLException {
		
		updatePS.setString(1, ldrid);
		updatePS.setString(2, name);
		updatePS.setString(3, category);
		updatePS.setString(4, keywords);
		updatePS.setBoolean(5, deleted);
		updatePS.setBoolean(6, official);
		updatePS.setInt(7, id);

		updatePS.executeUpdate();

	}
	
	
	public static void mark(boolean official) throws SQLException {

		Statement st;
		
		st = db.conn.createStatement();
		if (official) {
			st.execute("UPDATE "+table+" SET deleted=TRUE where official");
		}
		else {
			st.execute("UPDATE "+table+" SET deleted=TRUE where not official");
		}

	}
	

	public void insert() throws SQLException {
		
		insertPS.setString(1, ldrid);
		insertPS.setString(2, name);
		insertPS.setString(3, category);
		insertPS.setString(4, keywords);
		insertPS.setBoolean(5, deleted);
		insertPS.setBoolean(6, official);
		
		insertPS.executeUpdate();
	}

	
	public static ArrayList<LDrawPart> get(String filterExpr) throws SQLException {

		ArrayList<LDrawPart> dat = new ArrayList<LDrawPart>();
		LDrawPart l;
		Statement st;
		ResultSet rs;
		
		if (filterExpr == null) {
			st = db.conn.createStatement();
			rs = st.executeQuery("SELECT id,"+fieldsOrder+" FROM "+table);
		}
		else {
			st = db.conn.createStatement();
			rs = st.executeQuery("SELECT id," + fieldsOrder +
				" FROM "+table+" WHERE " + filterExpr);
		}
		while (rs.next()) {
			// fetch and assign rows to an Array list
			l = new LDrawPart();
			l.id = rs.getInt("id");
			l.ldrid = rs.getString("ldrid");
			l.name = rs.getString("name");
			l.category = rs.getString("category");
			l.keywords = rs.getString("keywords");
			l.deleted = rs.getBoolean("deleted");
			l.official = rs.getBoolean("official");
			l.lastmod = rs.getTimestamp("lastmod");
			dat.add(l);
		}
		return dat;
	}

	

	public static ArrayList<LDrawPart> getPS(PreparedStatement ps) throws SQLException {

		ArrayList<LDrawPart> dat = new ArrayList<LDrawPart>();
		LDrawPart l;
		ResultSet rs;
		
		rs = ps.executeQuery();
		while (rs.next()) {
			// fetch and assign rows to an Array list
			l = new LDrawPart();
			l.id = rs.getInt("id");
			l.ldrid = rs.getString("ldrid");
			l.name = rs.getString("name");
			l.category = rs.getString("category");
			l.keywords = rs.getString("keywords");
			l.deleted = rs.getBoolean("deleted");
			l.official = rs.getBoolean("official");
			l.lastmod = rs.getTimestamp("lastmod");
			dat.add(l);
		}
		return dat;
	}

	
	
	public static ArrayList<LDrawPart> getById(String ldrid) throws SQLException {

		PreparedStatement ps;
		
		ps = db.conn.prepareStatement("SELECT id,"+fieldsOrder+" FROM "+table+" where ldrid=?");
		ps.setString(1, ldrid);
		return getPS(ps);
	}
		

	

	
	public static ArrayList<LDrawPart> getFTS(String filterExpr) throws SQLException {

		ArrayList<LDrawPart> dat = new ArrayList<LDrawPart>();
		LDrawPart l;
		Statement st;
		ResultSet rs;
		
		if (filterExpr != null) {
			st = db.conn.createStatement();
			rs = st.executeQuery("SELECT id,"+fieldsOrder+" FROM FTL_SEARCH_DATA('"+filterExpr+"',0,0) f " +
					"LEFT JOIN "+table+" l on (f.keys[0]=l.id) WHERE f.table='"+table.toUpperCase()+"'");
			while (rs.next()) {
				// fetch and assign rows to an Array list
				l = new LDrawPart();
				l.id = rs.getInt("id");
				l.ldrid = rs.getString("ldrid");
				l.name = rs.getString("name");
				l.category = rs.getString("category");
				l.keywords = rs.getString("keywords");
				l.deleted = rs.getBoolean("deleted");
				l.official = rs.getBoolean("official");
				l.lastmod = rs.getTimestamp("lastmod");
				dat.add(l);
			}
		}
		return dat;
	}

	
	public static ArrayList<LDrawPart> getNew() throws SQLException {
		
		ArrayList<LDrawPart> dat = new ArrayList<LDrawPart>();
		LDrawPart l;
		Statement st;
		ResultSet rs;
		
		st = db.conn.createStatement();
		rs = st.executeQuery("SELECT id,"+fieldsOrder+
				" FROM "+table+" where lastmod > TIMESTAMPADD(MINUTE,-15,SELECT MAX(lastmod) from "+table+")");
		while (rs.next()) {
			// fetch and assign rows to an Array list
			l = new LDrawPart();
			l.id = rs.getInt("id");
			l.ldrid = rs.getString("ldrid");
			l.name = rs.getString("name");
			l.category = rs.getString("category");
			l.keywords = rs.getString("keywords");
			l.deleted = rs.getBoolean("deleted");
			l.official = rs.getBoolean("official");
			l.lastmod = rs.getTimestamp("lastmod");
			dat.add(l);
		}
		return dat;
	}


}

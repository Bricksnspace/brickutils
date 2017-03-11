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

import java.sql.*;
import java.util.ArrayList;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.StartElement;

import bricksnspace.bricklinklib.BricklinkPart;

/*
 * Class for mapping part IDs between catalogs
 * It is an object represent a row in part ID mapping table
 */



public class PartMapping {
	public int mapid;			// mapping id in table (primary key)
    public String masterid;	// the brick "family" ID 
    public String designid;	// LDD design ID
    public String decorid;	// brick decoration id from LDD
    public String name;		// brick name from BL
    public String blid;		// BrickLink ID
    public String ldrawid;		// LDraw catalog ID	
    public boolean ldd2bl;		// converts LDD to BL?
    public boolean bl2ldd;		// converts BL toLDD?
    public boolean ldd2dat;	// converts LDD to LDraw?
    public boolean dat2ldd;	// converts LDraw to LDD?
    public boolean bl2dat;		// converts BL to LDraw?
    public boolean dat2bl;		// converts LDraw to BL?
    public Timestamp lastmod;	// last modified
	public final static String fieldsOrder = "masterid,designid,blid,ldrawid,name,ldd2bl,bl2ldd,ldd2dat,dat2ldd,bl2dat,dat2bl,decorid,lastmod";
	public final static String table = "partmapping";
	private static PreparedStatement deletePS = null;
	private static PreparedStatement updatePS = null;
	private static PreparedStatement insertPS = null;
	private static PreparedStatement selectPS = null;
	private static BrickDB db;

    
    public PartMapping() {
    	masterid = "";
    	designid = "";
    	decorid = "";
    	name = "";
    	blid = "";
    	ldrawid = "";
    	ldd2bl = false;
    	bl2ldd = false;
    	ldd2dat = false;
    	dat2ldd = false;
    	bl2dat = false;
    	dat2bl = false;
    }
    
    /*
     * utility constructor that create a duplicates of a PartMapping Object
     */
    public PartMapping(PartMapping pm) {
    	mapid = pm.mapid;
    	masterid = pm.masterid;
    	designid = pm.designid;
    	decorid = pm.decorid;
    	name = pm.name;
    	blid = pm.blid;
    	ldrawid = pm.ldrawid;
    	ldd2bl = pm.ldd2bl;
    	bl2ldd = pm.bl2ldd;
    	ldd2dat = pm.ldd2dat;
    	dat2ldd = pm.dat2ldd;
    	bl2dat = pm.bl2dat;
    	dat2bl = pm.dat2bl;
    	lastmod = pm.lastmod;
    }
    
    
    
    public PartMapping(StartElement xsr) {

    	try {
	    	mapid = Integer.parseInt(xsr.getAttributeByName(new QName("id")).getValue());
	    	masterid = xsr.getAttributeByName(new QName("masterid")).getValue();
	    	if (masterid == null) 
	    		masterid = "";
	    	designid = xsr.getAttributeByName(new QName("designid")).getValue();
	    	if (designid == null) 
	    		designid = "";
	    	decorid = xsr.getAttributeByName(new QName("decorid")).getValue();
	    	if (decorid == null) 
	    		decorid = "";
	    	name = xsr.getAttributeByName(new QName("name")).getValue();
	    	if (name == null) 
	    		name = "";
	    	blid = xsr.getAttributeByName(new QName("blid")).getValue();
	    	if (blid == null) 
	    		blid = "";
	    	ldrawid = xsr.getAttributeByName(new QName("ldrawid")).getValue();
	    	if (ldrawid == null) 
	    		ldrawid = "";
	    	ldd2bl = xsr.getAttributeByName(new QName("ldd2bl")).getValue().equals("1");
	    	bl2ldd = xsr.getAttributeByName(new QName("bl2ldd")).getValue().equals("1");
	    	ldd2dat = xsr.getAttributeByName(new QName("ldd2dat")).getValue().equals("1");
	    	dat2ldd = xsr.getAttributeByName(new QName("dat2ldd")).getValue().equals("1");
	    	bl2dat = xsr.getAttributeByName(new QName("bl2dat")).getValue().equals("1");
	    	dat2bl = xsr.getAttributeByName(new QName("dat2bl")).getValue().equals("1");
	    	lastmod = Timestamp.valueOf(xsr.getAttributeByName(new QName("lastmod")).getValue());
    	} catch (NumberFormatException | NullPointerException e) {
    		throw new IllegalArgumentException("Error in update file format. Opening tag:\n"+xsr, e);
    	}
    }

    
    
    @Override
    public String toString() {
    	return designid+"|"+blid+"|"+ldrawid+" - "+name;
    }

    
	public static void setDb(BrickDB bdb) throws SQLException {

		db = bdb;
		insertPS = db.conn.prepareStatement("INSERT INTO "+table +
				" ("+fieldsOrder+") " +
				"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,NOW())",Statement.RETURN_GENERATED_KEYS);
		updatePS = db.conn.prepareStatement("UPDATE "+table+" SET " +
				"masterid=?," +
				"designid=?," +
				"blid=?," +
				"ldrawid=?," +
				"name=?," +
				"ldd2bl=?," +
				"bl2ldd=?," +
				"ldd2dat=?," +
				"dat2ldd=?," +
				"bl2dat=?," +
				"dat2bl=?," +
				"decorid=?," +
				"lastmod=NOW() " +
				"WHERE mapid=?");
		deletePS = db.conn.prepareStatement("DELETE FROM "+table+
				" WHERE mapid=?; COMMIT ");
		selectPS  = db.conn.prepareStatement(
				"SELECT mapid," + fieldsOrder +
				" FROM "+table);

	}
	
	

	
	public static void createTable() throws SQLException {
		
		Statement st;
		
		st = db.conn.createStatement();
		try {
			// drop old index, if exists
			st.execute("CALL FTL_DROP_INDEX('PUBLIC','PARTMAPPING') ");
		}
		catch (SQLException e) {
			;
		}
		st.execute("DROP TABLE IF EXISTS "+table+"; " +
				"CREATE TABLE "+table+" (" +
				"mapid INT PRIMARY KEY AUTO_INCREMENT, " +
				"masterid VARCHAR(64)," +
				"designid VARCHAR(64)," +
				"decorid VARCHAR(64)," +
				"blid VARCHAR(64)," +
				"ldrawid VARCHAR(64)," +
				"name VARCHAR(255)," +
				"ldd2bl BOOL," +
				"bl2ldd BOOL," +
				"ldd2dat BOOL," +
				"dat2ldd BOOL," +
				"bl2dat BOOL," +
				"dat2bl BOOL," +
				"lastmod TIMESTAMP); COMMIT ");
	}
	
	
	
	public static void createFTS() throws SQLException {
		
		Statement st;
		
		st = db.conn.createStatement();
		try {
			// drop old index, if exists
			st.execute("CALL FTL_DROP_INDEX('PUBLIC','PARTMAPPING') ");
		}
		catch (SQLException e) {
			;
		}
		st.execute("CALL FTL_CREATE_INDEX('PUBLIC','PARTMAPPING','DESIGNID,DECORID,NAME')");
	}
	
	
	
	public static void deleteFTS() throws SQLException {

		Statement st;
		
		st = db.conn.createStatement();
		try {
			// drop old index, if exists
			st.execute("CALL FTL_DROP_INDEX('PUBLIC','PARTMAPPING') ");
		}
		catch (SQLException e) {
			;
		}
	}
	
	
	
	
	public int insert() throws SQLException {
		
		ResultSet rs;
		
		insertPS.setString(1, masterid);
		insertPS.setString(2, designid);
		insertPS.setString(3, blid);
		insertPS.setString(4, ldrawid);
		insertPS.setString(5, name);
		insertPS.setBoolean(6, ldd2bl);
		insertPS.setBoolean(7, bl2ldd);
		insertPS.setBoolean(8, ldd2dat);
		insertPS.setBoolean(9, dat2ldd);
		insertPS.setBoolean(10, bl2dat);
		insertPS.setBoolean(11, dat2bl);
		insertPS.setString(12, decorid);
		
		insertPS.executeUpdate();
		rs = insertPS.getGeneratedKeys();
		rs.next();
		mapid = rs.getInt(1);
		return mapid;
	}
	
	
	public void update() throws SQLException {
		
		updatePS.setString(1, masterid);
		updatePS.setString(2, designid);
		updatePS.setString(3, blid);
		updatePS.setString(4, ldrawid);
		updatePS.setString(5, name);
		updatePS.setBoolean(6, ldd2bl);
		updatePS.setBoolean(7, bl2ldd);
		updatePS.setBoolean(8, ldd2dat);
		updatePS.setBoolean(9, dat2ldd);
		updatePS.setBoolean(10, bl2dat);
		updatePS.setBoolean(11, dat2bl);
		updatePS.setString(12, decorid);
		updatePS.setInt(13, mapid);
		
		updatePS.executeUpdate();
	}
	
	
	public void delete() throws SQLException {
		
		deletePS.setInt(1, mapid);
		
		deletePS.executeUpdate();
	}
	
	
	
	public static ArrayList<PartMapping> get(String filterExpr) throws SQLException {
		
		ArrayList<PartMapping> pml = new ArrayList<PartMapping>();
		PartMapping pm;
		Statement st;
		ResultSet rs;
		
		if (filterExpr == null)
			rs = selectPS.executeQuery();
		else {
			st = db.conn.createStatement();
			rs = st.executeQuery("SELECT " +
					"mapid," + fieldsOrder + 
					" FROM "+table+" where "+filterExpr);
		}
		while (rs.next()) {
			// fetch and assign rows to a PartMapping Array list
			pm = new PartMapping();
			pm.mapid = rs.getInt("mapid");
			pm.masterid = rs.getString("masterid");
			pm.designid = rs.getString("designid");
			pm.blid = rs.getString("blid");
			pm.ldrawid = rs.getString("ldrawid");
			pm.decorid = rs.getString("decorid");
			pm.name = rs.getString("name");
			pm.ldd2bl = rs.getBoolean("ldd2bl");
			pm.ldd2dat = rs.getBoolean("ldd2dat");
			pm.bl2ldd = rs.getBoolean("bl2ldd");
			pm.bl2dat = rs.getBoolean("bl2dat");
			pm.dat2bl = rs.getBoolean("dat2bl");
			pm.dat2ldd = rs.getBoolean("dat2ldd");
			pm.lastmod = rs.getTimestamp("lastmod");
			pml.add(pm);
		}
		return pml;
	}
	
	
	
	public static ArrayList<PartMapping> getPS(PreparedStatement ps) throws SQLException {
		
		ArrayList<PartMapping> pml = new ArrayList<PartMapping>();
		PartMapping pm;
		ResultSet rs;
		
		rs = ps.executeQuery();
		while (rs.next()) {
			// fetch and assign rows to a PartMapping Array list
			pm = new PartMapping();
			pm.mapid = rs.getInt("mapid");
			pm.masterid = rs.getString("masterid");
			pm.designid = rs.getString("designid");
			pm.blid = rs.getString("blid");
			pm.ldrawid = rs.getString("ldrawid");
			pm.decorid = rs.getString("decorid");
			pm.name = rs.getString("name");
			pm.ldd2bl = rs.getBoolean("ldd2bl");
			pm.ldd2dat = rs.getBoolean("ldd2dat");
			pm.bl2ldd = rs.getBoolean("bl2ldd");
			pm.bl2dat = rs.getBoolean("bl2dat");
			pm.dat2bl = rs.getBoolean("dat2bl");
			pm.dat2ldd = rs.getBoolean("dat2ldd");
			pm.lastmod = rs.getTimestamp("lastmod");
			pml.add(pm);
		}
		return pml;
	}
	
	
	public static ArrayList<PartMapping> getFTS(String filterExpr,String filter) throws SQLException {
		
		PreparedStatement ps;

		//select b.*,f.score from FTL_SEARCH_DATA('words', 0, 0) f left join blparts b on(f.keys[0]=b.id) 
		//                 where f.table='BLPARTS';

		if (filterExpr != null) {
			if (filter == null) {
				ps = db.conn.prepareStatement("SELECT mapid,"+fieldsOrder+" FROM FTL_SEARCH_DATA('"+filterExpr+"',0,0) f " +
						"LEFT JOIN "+table+" b on (f.keys[0]=b.mapid) WHERE f.table='PARTMAPPING'");
			}
			else {
				ps = db.conn.prepareStatement("SELECT mapid,"+fieldsOrder+" FROM FTL_SEARCH_DATA('"+filterExpr+"',0,0) f " +
						"LEFT JOIN "+table+" b on (f.keys[0]=b.mapid) WHERE f.table='PARTMAPPING' AND "+filter);
			}
			return getPS(ps);
		}
				
		return new ArrayList<PartMapping>(1);
	}
	
	
	
	public static ArrayList<PartMapping> getNew() throws SQLException {
		
		PreparedStatement ps;
		
		ps = db.conn.prepareStatement("SELECT mapid,"+fieldsOrder+
				" FROM "+table+" where lastmod > TIMESTAMPADD(MINUTE,-15,SELECT MAX(lastmod) from "+table+")");
		return getPS(ps);
	}
	
	
	
	public static Timestamp[] getLastModifyTime() throws SQLException {
		
		PreparedStatement ps;
		ResultSet rs;
		Timestamp last[] = new Timestamp[5];
		
		ps = db.conn.prepareStatement("SELECT FORMATDATETIME(lastmod,'yyyy-MM-dd') as datemod "+
				" FROM "+table+" GROUP BY datemod ORDER BY datemod DESC LIMIT 5");
		rs = ps.executeQuery();
		int i = 0;
		while (rs.next()) {
			last[i] = Timestamp.valueOf(rs.getString("datemod")+" 00:00:00");
			i++;
			if (i >= 5)
				break;
		}
		return last;
	}

	
	public static ArrayList<PartMapping> getModifiedAfter(Timestamp lastmodified) throws SQLException {
		
		PreparedStatement ps;
		ps = db.conn.prepareStatement("SELECT mapid,"+fieldsOrder+" FROM "+table+
				" where lastmod>=?");
		ps.setTimestamp(1, lastmodified);
		return getPS(ps);
	}
    

	
	
	
	@SuppressWarnings("resource")
	public static Brick getBrickByDesignId(String designid, String decorid) throws SQLException {
		
		Brick b;
		PreparedStatement ps;
		ArrayList<PartMapping> pm;
		ArrayList<BricklinkPart> blp;
		ArrayList<LDrawPart> ldrp;
		
		b = new Brick(designid);
		if (decorid != null && decorid.length() > 0) {
			b.decorID = decorid;
		}
		if (decorid == null || decorid.length() == 0) {
			ps = db.conn.prepareStatement("SELECT mapid,"+fieldsOrder+" FROM "+table+" WHERE designid=? and ldd2dat and decorid=''");
			ps.setString(1, designid);
		}
		else {
			ps = db.conn.prepareStatement("SELECT mapid,"+fieldsOrder+" FROM "+table+" WHERE designid=? and ldd2dat and decorid=?");
			ps.setString(1, designid);
			ps.setString(2, decorid);
		}
		pm = getPS(ps);
		if (pm.size() > 1) {
			throw new SQLException("Internal error: duplicated part mapping in database\n"+pm.get(0));
		}
		else if (pm.size() == 1) {
			b.ldrawID = pm.get(0).ldrawid;
			if (pm.get(0).masterid != "") {
				b.masterID = pm.get(0).masterid;
			}
		}
		// get bl part equivalence
		if (decorid == null || decorid.length() == 0) {
			ps = db.conn.prepareStatement("SELECT mapid,"+fieldsOrder+" FROM "+table+" WHERE designid=? and ldd2bl and decorid=''");
			ps.setString(1, designid);
		}
		else {
			ps = db.conn.prepareStatement("SELECT mapid,"+fieldsOrder+" FROM "+table+" WHERE designid=? and ldd2bl and decorid=?");
			ps.setString(1, designid);
			ps.setString(2, decorid);
		}
		pm = getPS(ps);
		if (pm.size() > 1) {
			throw new SQLException("Internal error: duplicated part mapping in database\n"+pm.get(0));
		}
		else if (pm.size() == 1) {
			b.blID = pm.get(0).blid;
			if (pm.get(0).masterid != "") {
				b.masterID = pm.get(0).masterid;
			}
		}
		// name from blparts or ldrparts
		if (b.blID != "") {
			blp = BricklinkPart.getById(b.blID);
			if (blp.size() > 1) {
				throw new SQLException("Internal error: duplicated Bricklink part in database\n"+blp.get(0));
			}
			else if (blp.size() == 0) {
				// gets name from ldrpart, if any
				if (b.ldrawID != "") {
					ldrp = LDrawPart.getById(b.ldrawID);
					if (ldrp.size() > 1) {
						throw new SQLException("Internal error: duplicated LDraw part in database\n"+ldrp.get(0));
					}
					else if (ldrp.size() == 1) {
						b.name = ldrp.get(0).name;
					}
				}
			}
			else {  // if blp.size() == 1
				b.name = blp.get(0).getName();
			}
		}
		return b;
	}
	
	
	
	public static Brick getBrickByBlinkId(String blid) throws SQLException {
		
		Brick b;
		PreparedStatement ps;
		ArrayList<PartMapping> pm;
		ArrayList<BricklinkPart> blp;
		
		b = new Brick();
		b.blID = blid;
		// gets ldd part equivalence
		ps = db.conn.prepareStatement("SELECT mapid,"+fieldsOrder+" FROM "+table+" WHERE blid=? and bl2ldd");
		ps.setString(1, blid);
		pm = getPS(ps);
		if (pm.size() > 1) {
			throw new SQLException("Internal error: duplicated part mapping in database\n"+pm.get(0));
		}
		else if (pm.size() == 1) {
			b.designID = pm.get(0).designid;
			b.decorID = pm.get(0).decorid;
			if (pm.get(0).masterid != "") {
				b.masterID = pm.get(0).masterid;
			}
			else {
				b.masterID = b.designID;
			}
		}
		// get ldraw part equivalence
		ps = db.conn.prepareStatement("SELECT mapid,"+fieldsOrder+" FROM "+table+" WHERE blid=? and bl2dat");
		ps.setString(1, blid);
		pm = getPS(ps);
		if (pm.size() > 1) {
			throw new SQLException("Internal error: duplicated part mapping in database\n"+pm.get(0));
		}
		else if (pm.size() == 1) {
			b.ldrawID = pm.get(0).ldrawid;
			if (b.masterID == "" && pm.get(0).masterid != "") {
				b.masterID = pm.get(0).masterid;
			}
		}
		// name from blparts or ldrparts
		blp = BricklinkPart.getById(b.blID);
		if (blp.size() > 1) {
			throw new SQLException("Internal error: duplicated Bricklink part in database\n"+blp.get(0));
		}
		else if (blp.size() == 0) {
			// Problem: your BL part are outdate
			b.name = "Problem: part not found in BL DB. Needs an update, maybe?";
		}
		else if (blp.get(0).isDeleted()) {
			// this is a deleted part!
			b.name = "Problem: this is a deleted part in BLink. You have an old dump?";
		}
		else {  // if blp.size() == 1
			b.name = blp.get(0).getName();
		}
		return b;
	}

	
	// fatto: getBrickByLdrId
	public static Brick getBrickByLdrId(String ldr) throws SQLException {
		
		Brick b;
		PreparedStatement ps;
		ArrayList<PartMapping> pm;
		ArrayList<LDrawPart> ldrp;
		
		b = new Brick();
		b.ldrawID = ldr;
		// gets ldd part equivalence
		ps = db.conn.prepareStatement("SELECT mapid,"+fieldsOrder+" FROM "+table+" WHERE ldrawid=? and dat2ldd");
		ps.setString(1, ldr);
		pm = getPS(ps);
		if (pm.size() > 1) {
			throw new SQLException("Internal error: duplicated part mapping in database\n"+pm.get(0));
		}
		else if (pm.size() == 1) {
			b.designID = pm.get(0).designid;
			b.decorID = pm.get(0).decorid;
			if (pm.get(0).masterid != "") {
				b.masterID = pm.get(0).masterid;
			}
			else {
				b.masterID = b.designID;
			}
		}
		// get BL part equivalence
		ps = db.conn.prepareStatement("SELECT mapid,"+fieldsOrder+" FROM "+table+" WHERE ldrawid=? and dat2bl");
		ps.setString(1, ldr);
		pm = getPS(ps);
		if (pm.size() > 1) {
			throw new SQLException("Internal error: duplicated part mapping in database\n"+pm.get(0));
		}
		else if (pm.size() == 1) {
			b.blID = pm.get(0).blid;
			if (b.masterID == "" && pm.get(0).masterid != "") {
				b.masterID = pm.get(0).masterid;
			}
		}
		// name from blparts or ldrparts
		ldrp = LDrawPart.getById(b.ldrawID);
		if (ldrp.size() > 1) {
			throw new SQLException("Internal error: duplicated LDraw part in database\n"+ldrp.get(0));
		}
		else if (ldrp.size() == 0) {
			// Problem: your LDraw library are outdated
			b.name = "Problem: part not found in LDraw part Library. Needs an update, maybe?";
		}
		else if (ldrp.get(0).deleted) {
			// this is a deleted part!
			b.name = "Problem: this is a deleted part. You have an old data file?";
		}
		else {  // if blp.size() == 1
			b.name = ldrp.get(0).name;
		}
		return b;
	}

	
	
	// checks part mappings for part delete/change in BLink/LDraw part lists
	// tags parts with:
	// ##bldelete
	// ##ldrdelete
	
	public static void checkDeletedId() throws SQLException {
		
		Statement st;
		
		st = db.conn.createStatement();
		st.execute("UPDATE "+table+" set name=CONCAT(name,' ##bldelete') " +
				"WHERE blid != '' and blid NOT IN (SELECT blid from blparts WHERE NOT deleted)");
		st.execute("UPDATE "+table+" set name=CONCAT(name,' ##ldrdelete') " +
				"WHERE ldrawid != '' and ldrawid NOT IN (SELECT ldrid from ldrparts WHERE NOT deleted)");
		
	}
	
	
	
	
	public static void cleanup() throws SQLException {
		
		Statement st;
		
		st = db.conn.createStatement();
		// cleanup ##ldrawnew tag
		st.execute("UPDATE "+table+" set name=REPLACE(name,'##ldrawnew')");
		st.execute("UPDATE "+table+" set name=REPLACE(name,'##ldrdelete')");
		st.execute("UPDATE "+table+" set name=REPLACE(name,'##bldelete')");
		st.execute("UPDATE "+table+" set masterid=TRIM(masterid),designid=TRIM(designid),name=TRIM(name)");
		st.execute("UPDATE "+table+" set ldrawid=TRIM(ldrawid),blid=TRIM(blid),decorid=TRIM(decorid)");
	}
	
	
	
	public void check() throws SQLException {
		
		Statement st;
		ResultSet rs;
		
		if (ldd2bl) {
			// it is a ldd to Bricklink mapping, must be unique
			st = db.conn.createStatement();
			rs = st.executeQuery("SELECT * FROM "+table+
					" WHERE designid = '"+designid+"' " +
							"AND " +
					"decorid = '"+decorid+"' " +
							"AND " +
					"ldd2bl " +
					"AND " +
					"mapid != "+Integer.toString(mapid));
			if (rs.next()) {
				String lddid = rs.getString("designid");
				String bl = rs.getString("blid");
				throw new IllegalStateException("Duplicated part\n"+
						"Design ID: "+designid+"->"+
						"BLink ID:  "+blid+"\n"+
						"is already mapped as\n"+
						"Design ID: "+lddid+"->"+
						"BLink ID:  "+bl);
			}
		}
		if (ldd2dat) {
			// it is a ldd to LDraw mapping, must be unique
			st = db.conn.createStatement();
			rs = st.executeQuery("SELECT * FROM "+table+
					" WHERE designid = '"+designid+"' " +
							"AND " +
					"decorid = '"+decorid+"' " +
							"AND " +
					"ldd2dat " +
					"AND " +
					"mapid != "+Integer.toString(mapid));
			if (rs.next()) {
				String lddid = rs.getString("designid");
				String ldraw = rs.getString("ldrawid");
				throw new IllegalStateException("Duplicated part\n"+
						"Design ID: "+designid+"->"+
						"LDraw ID:  "+ldrawid+"\n"+
						"is already mapped as\n"+
						"Design ID: "+lddid+"->"+
						"LDraw ID:  "+ldraw);
			}
		}
		if (bl2ldd) {
			// it is a BL to ldd mapping
			st = db.conn.createStatement();
			rs = st.executeQuery("SELECT * FROM "+table+
					" WHERE blid = '"+blid+"' " +
							"AND " +
					"decorid = '"+decorid+"' " +
							"AND " +
					"bl2ldd " +
					"AND " +
					"mapid != "+Integer.toString(mapid));
			if (rs.next()) {
				String lddid = rs.getString("designid");
				String bl = rs.getString("blid");
				throw new IllegalStateException("Duplicated part\n"+
						"BLink ID: "+blid+"->"+
						"Design ID:  "+designid+"\n"+
						"is already mapped as\n"+
						"BLink ID: "+bl+"\n"+
						"Design ID:  "+lddid);
			}
		}
		if (dat2ldd) {
			// it is a LDraw to ldd mapping
			st = db.conn.createStatement();
			rs = st.executeQuery("SELECT * FROM "+table+
					" WHERE ldrawid = '"+ldrawid+"' " +
							"AND " +
					"decorid = '"+decorid+"' " +
							"AND " +
					"dat2ldd " +
					"AND " +
					"mapid != "+Integer.toString(mapid));
			if (rs.next()) {
				String lddid = rs.getString("designid");
				String ldraw = rs.getString("ldrawid");
				throw new IllegalStateException("Duplicated part\n"+
						"LDRaw ID: "+ldrawid+"->"+
						"Design ID:  "+designid+"\n"+
						"is already mapped as\n"+
						"LDraw ID: "+ldraw+"->"+
						"Design ID:  "+lddid);
			}
		}
		if (bl2dat) {
			// it is a BL to ldraw mapping
			st = db.conn.createStatement();
			rs = st.executeQuery("SELECT * FROM "+table+
					" WHERE blid = '"+blid+"' " +
							"AND " +
					"bl2dat " +
					"AND " +
					"mapid != "+Integer.toString(mapid));
			if (rs.next()) {
				String ldraw = rs.getString("ldrawid");
				String bl = rs.getString("blid");
				throw new IllegalStateException("Duplicated part\n"+
						"BLink ID: "+blid+"->"+
						"LDraw ID:  "+ldrawid+"\n"+
						"is already mapped as\n"+
						"BLink ID: "+bl+"\n"+
						"LDraw ID:  "+ldraw);
			}
		}
		if (dat2bl) {
			// it is a LDraw to bl mapping
			st = db.conn.createStatement();
			rs = st.executeQuery("SELECT * FROM "+table+
					" WHERE ldrawid = '"+ldrawid+"' " +
							"AND " +
					"dat2bl " +
					"AND " +
					"mapid != "+Integer.toString(mapid));
			if (rs.next()) {
				String bl = rs.getString("blid");
				String ldraw = rs.getString("ldrawid");
				throw new IllegalStateException("Duplicated part\n"+
						"LDRaw ID: "+ldrawid+"->"+
						"BLink ID:  "+blid+"\n"+
						"is already mapped as\n"+
						"LDraw ID: "+ldraw+"->"+
						"BLink ID:  "+bl);
			}
		}
		
	}
	
	///////////////////////////////////////////
	// statistics
	///////////////////////////////////////////
	
	
	public static int getRules() {

		Statement st;
		ResultSet rs;
		
		try {
			st = db.conn.createStatement();
			rs = st.executeQuery("SELECT COUNT(*) FROM " + table +
					" WHERE ldd2bl OR ldd2dat OR bl2dat OR bl2ldd OR dat2bl OR dat2ldd");
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
		
		xsw.writeStartElement("partmap");
		xsw.writeAttribute("id",Integer.toString(mapid));
		xsw.writeAttribute("masterid",masterid); 
		xsw.writeAttribute("designid",designid);
		xsw.writeAttribute("decorid",decorid);
		xsw.writeAttribute("name",name);
		xsw.writeAttribute("blid",blid);
		xsw.writeAttribute("ldrawid",ldrawid);	
		xsw.writeAttribute("ldd2bl",ldd2bl?"1":"0");
		xsw.writeAttribute("bl2ldd",bl2ldd?"1":"0");
		xsw.writeAttribute("ldd2dat",ldd2dat?"1":"0");
		xsw.writeAttribute("dat2ldd",dat2ldd?"1":"0");
		xsw.writeAttribute("bl2dat",bl2dat?"1":"0");
		xsw.writeAttribute("dat2bl",dat2bl?"1":"0");
		xsw.writeAttribute("lastmod",lastmod.toString());
		xsw.writeEndElement();
		xsw.writeCharacters("\n");
	}

	
	public void updateMapping() throws SQLException {
		
		PreparedStatement ps;
		ResultSet rs;
		
		ps = db.conn.prepareStatement("SELECT * FROM "+table+" where mapid=?");
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

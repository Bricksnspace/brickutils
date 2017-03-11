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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.imageio.ImageIO;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.StartElement;

import org.apache.commons.codec.binary.Base64;

import bricksnspace.j3dgeom.Matrix3D;
import bricksnspace.ldraw3d.LDRenderedPart;
import bricksnspace.ldraw3d.LDrawGLDisplay;
import bricksnspace.ldrawlib.LDPrimitive;


public class Brick {
	int id;				// index for db and relations
    String masterID;	// the brick "family" ID 
    String designID;	// LDD design ID
    String partNO;		// LEGO® part ID
    String name;		// brick name from BL
    String blID;		// BrickLink ID
    String ldrawID;		// LDraw catalog ID
    int color;			// color index
    String decorID;
    int quantity;		// brick quantity
    boolean extra;		// it is an extra part
    boolean alt;		// it is an alternate part
    int matchid;		// match for alternate part

    /*
     * export in Bricklink format specifiers
     */
	private static boolean blQty = true;
	private static boolean blNotify = false;
	private static boolean blQueries = false;
	private static int blCondition = 0;			// 0=don't care, 1=new, 2=used
	private static String blListId = "";
	
	/* 
	 * export in html format spec
	 */
	private static boolean htmCheck = true;
	private static boolean htmImage = true;
	private static int htmTextSize = 1;		// 0=small 1=default 2=large
	private static int htmImgSize = 100;		// small(75) default(100) large(150) xlarge(200)
	
	// private data for brick image
	private static LDrawGLDisplay shapeView = null;
	private static boolean frontAndRear = true;
	
	
    private static BrickDB db;
	private static PreparedStatement insertTmpPS = null;
	private static PreparedStatement insertTmpIdPS = null;
	private static PreparedStatement updateTmpPS = null;
	private static PreparedStatement updTmpQtyPS = null;
	private static PreparedStatement getTmpIdPS = null;
	private static PreparedStatement insertWorkPS = null;
	private static PreparedStatement updateWorkPS = null;
	private static PreparedStatement deleteWorkPS = null;
	private static PreparedStatement updWorkQtyPS = null;
	private static PreparedStatement getWorkIdPS = null;
	private static PreparedStatement insertCatalogPS = null;
	private static PreparedStatement insertCatalogIdPS = null;
	private static PreparedStatement updateCatalogPS = null;
	private static PreparedStatement deleteCatalogPS = null;
	private static PreparedStatement getCatIdPS = null;
	private static PreparedStatement updCatQtyPS = null;

	public static final String workTable = "workbricks";
	public static final String tmpTable = "tmpbricks";
	public static final String catalogTable = "mybricks";
	public static final String fieldsOrder = "masterid,designid,partno,name,blID,ldrawID,color,decorID,quantity,extra,alt,matchid";

	public static final int COLOR_EXACT = 1;
	public static final int COLOR_NEAR = 2;
	public static final int COLOR_IGNORE = 3;
	
	public static final int BRICK_ALL = 1;
	public static final int BRICK_AVAIL = 2;
	public static final int BRICK_SELECTED = 3;
      
    
    Brick(Brick b) {
    	
    	id = b.id;
    	quantity = b.quantity;
    	designID = b.designID;
    	masterID = b.masterID;
    	partNO = b.partNO;
    	color = b.color;
    	name = b.name;
    	blID = b.blID;
    	ldrawID = b.ldrawID;
    	decorID = b.decorID;
    	extra = b.extra;
    	alt = b.alt;
    	matchid = b.matchid;
    }

    
    
    Brick(String designid, int color) {
    	
    	id = 0;
    	quantity = 0;
    	this.designID = designid;
    	masterID = designid;
    	partNO = "";
    	name = "";
    	blID = "";
    	ldrawID = "";
    	decorID = "";
    	extra = false;
    	this.color = color;
    	alt = false;
    	matchid = 0;
    }
    
    
    
    Brick(String designid) {
    	
    	id = 0;
    	quantity = 0;
    	this.designID = designid;
    	masterID = designid;
    	partNO = "";
    	name = "";
    	blID = "";
    	ldrawID = "";
    	decorID = "";
    	color = 0;
    	extra = false;
    	alt = false;
    	matchid = 0;
    }
    
    
    
    Brick(StartElement xse) {
    	
    	try {
	    	id = Integer.parseInt(xse.getAttributeByName(new QName("id")).getValue());
	    	masterID = xse.getAttributeByName(new QName("masterid")).getValue();
	    	if (masterID == null) 
	    		masterID = "";
	    	designID = xse.getAttributeByName(new QName("designid")).getValue();
	    	if (designID == null) 
	    		designID = "";
	    	decorID = xse.getAttributeByName(new QName("decorid")).getValue();
	    	if (decorID == null) 
	    		decorID = "";
	    	name = xse.getAttributeByName(new QName("name")).getValue();
	    	if (name == null) 
	    		name = "";
	    	blID = xse.getAttributeByName(new QName("blid")).getValue();
	    	if (blID == null) 
	    		blID = "";
	    	ldrawID = xse.getAttributeByName(new QName("ldrawid")).getValue();
	    	if (ldrawID == null) 
	    		ldrawID = "";
	    	quantity = Integer.parseInt(xse.getAttributeByName(new QName("quantity")).getValue());
	    	partNO = xse.getAttributeByName(new QName("partno")).getValue();
	    	color = Integer.parseInt(xse.getAttributeByName(new QName("color")).getValue());
	    	extra = xse.getAttributeByName(new QName("extra")).getValue().equals("1");
	    	alt = xse.getAttributeByName(new QName("alt")).getValue().equals("1");
	    	matchid = Integer.parseInt(xse.getAttributeByName(new QName("matchid")).getValue());
    	} catch (NumberFormatException | NullPointerException e) {
    		throw new IllegalArgumentException("Error in BrickUtils backup file format. Opening tag:\n"+xse,e);
    	}
    }
    
    
    
    Brick() {
    	
    	id = 0;
    	quantity = 0;
    	this.designID = "";
    	masterID = "";
    	partNO = "";
    	name = "";
    	blID = "";
    	ldrawID = "";
    	decorID = "";
    	color = 0;
    	extra = false;
    	alt = false;
    	matchid = 0;
    }



	@Override
	public String toString() {
		return "Brick [id=" + id + ", masterID=" + masterID + ", designID="
				+ designID + ", partNO=" + partNO + ", name=" + name
				+ ", blID=" + blID + ", ldrawID=" + ldrawID + ", color="
				+ color + ", decorID=" + decorID + ", quantity=" + quantity
				+ ", extra=" + extra + ", alt=" + alt + ", matchid=" + matchid
				+ "]";
	}

	
	
	public static void setDb(BrickDB bdb) throws SQLException {

		db = bdb;
		// prepared statements
		// importing table
		insertTmpPS = db.conn.prepareStatement("INSERT INTO "+tmpTable+" ("+fieldsOrder+
				") VALUES (?,?,?,?,?,?,?,?,?,?,?,?)"
				);
		insertTmpIdPS  = db.conn.prepareStatement("INSERT INTO "+tmpTable+" ("+fieldsOrder+
				",id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)"
				);
		updateTmpPS  = db.conn.prepareStatement("UPDATE "+tmpTable+" SET " +
				"masterid=?," +
				"designid=?," +
				"partno=?," +
				"name=?," +
				"blID=?," +
				"ldrawID=?," +
				"color=?," +
				"decorID=?," +
				"quantity=?," +
				"extra=?," +
				"alt=?," +
				"matchid=? " +
				" WHERE id=?"
				);
		
		// working table
		insertWorkPS = db.conn.prepareStatement("INSERT INTO "+workTable+" ("+fieldsOrder+
				") VALUES (?,?,?,?,?,?,?,?,?,?,?,?)"
				);
		updateWorkPS = db.conn.prepareStatement("UPDATE "+workTable+" SET " +
				"masterid=?," +
				"designid=?," +
				"partno=?," +
				"name=?," +
				"blID=?," +
				"ldrawID=?," +
				"color=?," +
				"decorID=?," +
				"quantity=?," +
				"extra=?," +
				"alt=?," +
				"matchid=? " +
				" WHERE id=?"
				);
		deleteWorkPS = db.conn.prepareStatement("DELETE FROM "+workTable+" where id=?");

		// catalog table
		insertCatalogPS = db.conn.prepareStatement("INSERT INTO "+catalogTable+" ("+fieldsOrder+
				") VALUES (?,?,?,?,?,?,?,?,?,?,?,?)"
				);
		insertCatalogIdPS = db.conn.prepareStatement("INSERT INTO "+catalogTable+" ("+fieldsOrder+
				",id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)"
				);
		updateCatalogPS = db.conn.prepareStatement("UPDATE "+catalogTable+" SET " +
				"masterid=?," +
				"designid=?," +
				"partno=?," +
				"name=?," +
				"blID=?," +
				"ldrawID=?," +
				"color=?," +
				"decorID=?," +
				"quantity=?," +
				"extra=?," +
				"alt=?," +
				"matchid=? " +
				" WHERE id=?"
				);
		deleteCatalogPS = db.conn.prepareStatement("DELETE FROM "+catalogTable+" where id=?");
		getCatIdPS = db.conn.prepareStatement("SELECT id,"+fieldsOrder+" FROM "+catalogTable+" WHERE " +
				"masterid=? and " +
				"designid=? and " +
				"partno=? and " +
				"blid=? and " +
				"ldrawid=? and " +
				"color=? and " +
				"decorid=?");
		getTmpIdPS = db.conn.prepareStatement("SELECT id,"+fieldsOrder+" FROM "+tmpTable+" WHERE " +
				"masterid=? and " +
				"designid=? and " +
				"partno=? and " +
				"blid=? and " +
				"ldrawid=? and " +
				"color=? and " +
				"decorid=?");
		getWorkIdPS = db.conn.prepareStatement("SELECT id,"+fieldsOrder+" FROM "+workTable+" WHERE " +
				"masterid=? and " +
				"designid=? and " +
				"partno=? and " +
				"blid=? and " +
				"ldrawid=? and " +
				"color=? and " +
				"decorid=?");
		updCatQtyPS = db.conn.prepareStatement("UPDATE "+catalogTable+" SET " +
				"quantity=quantity+? " +
				" WHERE id=?"
				);
		updTmpQtyPS = db.conn.prepareStatement("UPDATE "+tmpTable+" SET " +
				"quantity=quantity+? " +
				" WHERE id=?"
				);
		updWorkQtyPS = db.conn.prepareStatement("UPDATE "+workTable+" SET " +
				"quantity=quantity+? " +
				" WHERE id=?"
				);
	}

	
	public int getId() {
		return id;
	}



	public String getMasterID() {
		return masterID;
	}



	public String getDesignID() {
		return designID;
	}



	public String getPartNO() {
		return partNO;
	}



	public String getName() {
		return name;
	}



	public String getBlID() {
		return blID;
	}



	public String getLdrawID() {
		return ldrawID;
	}



	public String getDecorID() {
		return decorID;
	}



	public int getQuantity() {
		return quantity;
	}



	public Color getColor() {
		
		return BrickColor.getColor(color).color;
	}
	
	
	public String getHtmlColor() {
		
		return String.format("#%02x%02x%02x", 
				/* red */   getColor().getRed(),
				/* green */ getColor().getGreen(),
				/* blue */  getColor().getBlue());
	}
	
	
	public String getColorName() {
		
		return BrickColor.getColor(color).getLddName();
	}
	
	
	public int getLddColor() {
		
		return BrickColor.getColor(color).ldd;
	}
	
	
	public int getBlColor() {
		
		return BrickColor.getColor(color).bl;
	}
	
	
	
	public int getLdrawColor() {
		
		return BrickColor.getColor(color).ldraw;
	}
	
	
	
	public boolean isExtra() {
		return extra;
	}



	public boolean isDecorated() {
		
		return decorID != null && decorID.trim().length() > 0;
	}
	
	
	
	public static boolean isBlQty() {
		return blQty;
	}



	public static void setBlQty(boolean blQty) {
		Brick.blQty = blQty;
	}



	public static boolean isBlNotify() {
		return blNotify;
	}



	public static void setBlNotify(boolean blNotify) {
		Brick.blNotify = blNotify;
	}



	public static boolean isBlQueries() {
		return blQueries;
	}



	public static void setBlQueries(boolean blQueries) {
		Brick.blQueries = blQueries;
	}



	public static int getBlCondition() {
		return blCondition;
	}



	public static void setBlCondition(int blCondition) {
		Brick.blCondition = blCondition;
	}



	public static String getBlListId() {
		return blListId;
	}



	public static void setBlListId(String blListId) {
		Brick.blListId = blListId;
	}



	public static boolean isHtmCheck() {
		return htmCheck;
	}



	public static void setHtmCheck(boolean htmCheck) {
		Brick.htmCheck = htmCheck;
	}



	public static boolean isHtmImage() {
		return htmImage;
	}



	public static void setHtmImage(boolean htmImage) {
		Brick.htmImage = htmImage;
	}



	public static boolean isFrontAndRear() {
		return frontAndRear;
	}



	public static void setFrontAndRear(boolean frontAndRear) {
		Brick.frontAndRear = frontAndRear;
	}



	public static void setShapeView(LDrawGLDisplay shapeView) {
		Brick.shapeView = shapeView;
	}
	
	
	
	public static LDrawGLDisplay getShapeView() {
		return shapeView;
	}



	public static int getHtmTextSize() {
		return htmTextSize;
	}



	public static void setHtmTextSize(int htmTextSize) {
		Brick.htmTextSize = htmTextSize;
	}



	public static int getHtmImgSize() {
		return htmImgSize;
	}



	public static void setHtmImgSize(int htmImgSize) {
		Brick.htmImgSize = htmImgSize;
	}



	public static void createTmpTable() throws SQLException {
		
		Statement st;
		
		st = db.conn.createStatement();
		st.execute("DROP TABLE IF EXISTS "+tmpTable+"; " +
				"CREATE TEMP TABLE "+tmpTable+" (" +
				"id INT PRIMARY KEY AUTO_INCREMENT," +
				"masterid VARCHAR(64)," +	// the brick "family" ID 
				"designid VARCHAR(64)," +	// LDD design ID
				"partno VARCHAR(64)," +		// LEGO® part ID
				"name VARCHAR(255)," +		// brick name from BL
				"blID VARCHAR(64)," +		// BrickLink ID
				"ldrawID VARCHAR(64)," +	// LDraw catalog ID
				"color INT," +				// color index
				"decorID VARCHAR(64)," +
				"quantity INT," +			// brick quantity
				"extra BOOL," +				// it is an extra part
				"alt BOOL," +				// it is an alternate part
				"matchid INT" +				// match for alternate part
				"); COMMIT ");
	}
	
	
	
	public int insertTmp() throws SQLException {
		
		ResultSet rs;
		
		insertTmpPS.setString(1,masterID);
		insertTmpPS.setString(2,designID);
		insertTmpPS.setString(3,partNO);
		insertTmpPS.setString(4,name);
		insertTmpPS.setString(5,blID);
		insertTmpPS.setString(6,ldrawID);
		insertTmpPS.setInt(7, color);
		insertTmpPS.setString(8,decorID);
		insertTmpPS.setInt(9, quantity);
		insertTmpPS.setBoolean(10, extra);
		insertTmpPS.setBoolean(11, alt);
		insertTmpPS.setInt(12, matchid);
		insertTmpPS.executeUpdate();
		
		rs = insertTmpPS.getGeneratedKeys();
		rs.next();
		id = rs.getInt(1);
		return id;

	}
	
	
	public void insertIdTmp() throws SQLException {
		
		insertTmpIdPS.setString(1,masterID);
		insertTmpIdPS.setString(2,designID);
		insertTmpIdPS.setString(3,partNO);
		insertTmpIdPS.setString(4,name);
		insertTmpIdPS.setString(5,blID);
		insertTmpIdPS.setString(6,ldrawID);
		insertTmpIdPS.setInt(7, color);
		insertTmpIdPS.setString(8,decorID);
		insertTmpIdPS.setInt(9, quantity);
		insertTmpIdPS.setBoolean(10, extra);
		insertTmpIdPS.setBoolean(11, alt);
		insertTmpIdPS.setInt(12, matchid);
		insertTmpIdPS.setInt(13, id);
		insertTmpIdPS.executeUpdate();
		
	}
	
	
	public void updateTmp() throws SQLException {
		
	
		updateTmpPS.setString(1,masterID);
		updateTmpPS.setString(2,designID);
		updateTmpPS.setString(3,partNO);
		updateTmpPS.setString(4,name);
		updateTmpPS.setString(5,blID);
		updateTmpPS.setString(6,ldrawID);
		updateTmpPS.setInt(7, color);
		updateTmpPS.setString(8,decorID);
		updateTmpPS.setInt(9, quantity);
		updateTmpPS.setBoolean(10, extra);
		updateTmpPS.setBoolean(11, alt);
		updateTmpPS.setInt(12, matchid);
		updateTmpPS.setInt(13, id);
		updateTmpPS.executeUpdate();
		
	}
	
	

	
	public int getTmpId() throws SQLException {
		
		ArrayList<Brick> bl;
		
		getTmpIdPS.setString(1, masterID);
		getTmpIdPS.setString(2, designID);
		getTmpIdPS.setString(3, partNO);
		getTmpIdPS.setString(4, blID);
		getTmpIdPS.setString(5, ldrawID);
		getTmpIdPS.setInt(6, color);
		getTmpIdPS.setString(7, decorID);
		bl = getPS(getTmpIdPS);
		if (bl.size() == 0) {
			return 0;			
		}
		else {
			return bl.get(0).id;
		}
	}
	
	

	/* 
	 * add brick to tmp list
	 * if brick exists, add to quantity
	 * else add brick id
	 */
	public int tmpAdd() throws SQLException {
		
		int localId = 0;
		
		localId = getTmpId();
		if (localId == 0) {
			return insertTmp();
		}
		else {
			updTmpQtyPS.setInt(1, quantity);
			updTmpQtyPS.setInt(2, localId);
			updTmpQtyPS.executeUpdate();
			return localId;
		}
	}

	

	
	
	
	public static ArrayList<Brick> getTmpById(int id) throws SQLException {
		
		PreparedStatement ps;
		
		ps = db.conn.prepareStatement("SELECT id,"+fieldsOrder+" FROM "+tmpTable+" WHERE id=?");
		ps.setInt(1, id);
		return getPS(ps);
	}
	

	
	public static ArrayList<Brick> getTmp() throws SQLException {
		
		PreparedStatement ps;
		
		ps = db.conn.prepareStatement("SELECT id,"+fieldsOrder+" FROM "+tmpTable+" WHERE NOT alt");
		return getPS(ps);
	}
	
	
	public static ArrayList<Brick> getRemainTmp() throws SQLException {
		
		PreparedStatement ps;
		
		ps = db.conn.prepareStatement("SELECT id,"+fieldsOrder+" FROM "+tmpTable+" WHERE NOT alt and quantity>0");
		return getPS(ps);
	}
	
	
	public static ArrayList<Brick> getAnomalyTmp() throws SQLException {
		
		PreparedStatement ps;
		
		ps = db.conn.prepareStatement("SELECT id,"+fieldsOrder+" FROM "+tmpTable+" WHERE NOT alt and quantity<0");
		return getPS(ps);
	}
	
	
	

	

	public static ArrayList<Brick> getAlt(int matchid) throws SQLException {
		
		PreparedStatement ps;
		
		ps = db.conn.prepareStatement("SELECT id,"+fieldsOrder+" FROM "+tmpTable+" WHERE matchid=? AND alt");
		ps.setInt(1, matchid);
		return getPS(ps);
	}
	
	
	
	public int insertWork() throws SQLException {
		
		ResultSet rs;
		
		insertWorkPS.setString(1,masterID);
		insertWorkPS.setString(2,designID);
		insertWorkPS.setString(3,partNO);
		insertWorkPS.setString(4,name);
		insertWorkPS.setString(5,blID);
		insertWorkPS.setString(6,ldrawID);
		insertWorkPS.setInt(7, color);
		insertWorkPS.setString(8,decorID);
		insertWorkPS.setInt(9, quantity);
		insertWorkPS.setBoolean(10, extra);
		insertWorkPS.setBoolean(11, alt);
		insertWorkPS.setInt(12, matchid);
		insertWorkPS.executeUpdate();
		
		rs = insertWorkPS.getGeneratedKeys();
		rs.next();
		id = rs.getInt(1);
		return id;
	}
	

	public void deleteWork() throws SQLException {
		
		deleteWorkPS.setInt(1, id);
		deleteWorkPS.executeUpdate();
	}
	
	

	public static void emptyWork() throws SQLException {
		
		PreparedStatement ps = db.conn.prepareStatement("DELETE FROM "+workTable+";" +
				"ALTER TABLE "+workTable+" ALTER COLUMN ID RESTART WITH 1");
		ps.executeUpdate();
	}
	
	
	
	public int getWorkId() throws SQLException {
		
		ArrayList<Brick> bl;
		
		getWorkIdPS.setString(1, masterID);
		getWorkIdPS.setString(2, designID);
		getWorkIdPS.setString(3, partNO);
		getWorkIdPS.setString(4, blID);
		getWorkIdPS.setString(5, ldrawID);
		getWorkIdPS.setInt(6, color);
		getWorkIdPS.setString(7, decorID);
		bl = getPS(getWorkIdPS);
		if (bl.size() == 0) {
			return 0;			
		}
		else {
			return bl.get(0).id;
		}
	}


	/* 
	 * add brick to work list
	 * if brick exists, add to quantity
	 * else add brick id
	 */
	public int workAdd() throws SQLException {
		
		int localId = 0;
		
		localId = getWorkId();
		if (localId == 0) {
			return insertWork();
		}
		else {
			updWorkQtyPS.setInt(1, quantity);
			updWorkQtyPS.setInt(2, localId);
			updWorkQtyPS.executeUpdate();
			return localId;
		}
	}

	

	public void updateWork() throws SQLException {
		
		updateWorkPS.setString(1,masterID);
		updateWorkPS.setString(2,designID);
		updateWorkPS.setString(3,partNO);
		updateWorkPS.setString(4,name);
		updateWorkPS.setString(5,blID);
		updateWorkPS.setString(6,ldrawID);
		updateWorkPS.setInt(7, color);
		updateWorkPS.setString(8,decorID);
		updateWorkPS.setInt(9, quantity);
		updateWorkPS.setBoolean(10, extra);
		updateWorkPS.setBoolean(11, alt);
		updateWorkPS.setInt(12, matchid);
		updateWorkPS.setInt(13, id);
		updateWorkPS.executeUpdate();
	}
	

	public static ArrayList<Brick> getPS(PreparedStatement ps) throws SQLException {
		
		ArrayList<Brick> bl = new ArrayList<Brick>();
		Brick b;
		ResultSet rs;
		
		rs = ps.executeQuery();
		while (rs.next()) {
			// fetch and assign rows to an Array list
			b = new Brick();
			b.id = rs.getInt("id");
			b.masterID = rs.getString("masterid");
			b.designID = rs.getString("designid");
			b.partNO = rs.getString("partno");
			b.name = rs.getString("name");
			b.blID = rs.getString("blid");
			b.ldrawID = rs.getString("ldrawid");
			b.color = rs.getInt("color");
			b.decorID = rs.getString("decorid");
			b.quantity = rs.getInt("quantity");
			b.extra = rs.getBoolean("extra");
			b.alt = rs.getBoolean("alt");
			b.matchid = rs.getInt("matchid");
			bl.add(b);
		}
		return bl;
	}
	
	
	
	public static ArrayList<Brick> getWork() throws SQLException {
		
		PreparedStatement ps;
		
		ps = db.conn.prepareStatement("SELECT id,"+fieldsOrder+" FROM "+workTable);
		return getPS(ps);
	}
	

	// my bricks catalog
	private int catalogIns() throws SQLException {
		
		ResultSet rs;
		
		insertCatalogPS.setString(1,masterID);
		insertCatalogPS.setString(2,designID);
		insertCatalogPS.setString(3,partNO);
		insertCatalogPS.setString(4,name);
		insertCatalogPS.setString(5,blID);
		insertCatalogPS.setString(6,ldrawID);
		insertCatalogPS.setInt(7, color);
		insertCatalogPS.setString(8,decorID);
		insertCatalogPS.setInt(9, quantity);
		insertCatalogPS.setBoolean(10, extra);
		insertCatalogPS.setBoolean(11, alt);
		insertCatalogPS.setInt(12, matchid);
		insertCatalogPS.executeUpdate();
		
		rs = insertCatalogPS.getGeneratedKeys();
		rs.next();
		id = rs.getInt(1);
		return id;
	}
	

	// my bricks catalog
	public void catalogIns(int id) throws SQLException {
		
		insertCatalogIdPS.setString(1,masterID);
		insertCatalogIdPS.setString(2,designID);
		insertCatalogIdPS.setString(3,partNO);
		insertCatalogIdPS.setString(4,name);
		insertCatalogIdPS.setString(5,blID);
		insertCatalogIdPS.setString(6,ldrawID);
		insertCatalogIdPS.setInt(7, color);
		insertCatalogIdPS.setString(8,decorID);
		insertCatalogIdPS.setInt(9, quantity);
		insertCatalogIdPS.setBoolean(10, extra);
		insertCatalogIdPS.setBoolean(11, alt);
		insertCatalogIdPS.setInt(12, matchid);
		insertCatalogIdPS.setInt(13, id);
		insertCatalogIdPS.executeUpdate();
	}
	

	public void deleteCatalog() throws SQLException {
		
		deleteCatalogPS.setInt(1, id);
		deleteCatalogPS.executeUpdate();
	}
	
	
	public void catalogUpd() throws SQLException {
		
		updateCatalogPS.setString(1,masterID);
		updateCatalogPS.setString(2,designID);
		updateCatalogPS.setString(3,partNO);
		updateCatalogPS.setString(4,name);
		updateCatalogPS.setString(5,blID);
		updateCatalogPS.setString(6,ldrawID);
		updateCatalogPS.setInt(7, color);
		updateCatalogPS.setString(8,decorID);
		updateCatalogPS.setInt(9, quantity);
		updateCatalogPS.setBoolean(10, extra);
		updateCatalogPS.setBoolean(11, alt);
		updateCatalogPS.setInt(12, matchid);
		updateCatalogPS.setInt(13, id);
		updateCatalogPS.executeUpdate();
	}
	


	
	public static Brick catalogById(int id) throws SQLException {
		
		PreparedStatement ps;
		
		ps = db.conn.prepareStatement("SELECT id,"+fieldsOrder+" FROM "+catalogTable+" WHERE id=?");
		ps.setInt(1, id);
		ArrayList<Brick> b = getPS(ps);
		if (b.size() == 0)
			return null;
		return b.get(0);
	}
	
	
	public static ArrayList<Brick> catalogGet() throws SQLException {
		
		PreparedStatement ps;
		
		ps = db.conn.prepareStatement("SELECT id,"+fieldsOrder+" FROM "+catalogTable);
		return getPS(ps);
	}
	
	
	
	public int getCatalogId() throws SQLException {
		
		ArrayList<Brick> bl;
		
		getCatIdPS.setString(1, masterID);
		getCatIdPS.setString(2, designID);
		getCatIdPS.setString(3, partNO);
		getCatIdPS.setString(4, blID);
		getCatIdPS.setString(5, ldrawID);
		getCatIdPS.setInt(6, color);
		getCatIdPS.setString(7, decorID);
		bl = getPS(getCatIdPS);
		if (bl.size() == 0) {
			return 0;			
		}
		else {
			return bl.get(0).id;
		}
	}
	
	
	/* 
	 * add brick to catalog
	 * if brick exists, add to quantity
	 * else add brick id
	 */
	public int catalogAdd() throws SQLException {
		
		int localId = 0;
		
		localId = getCatalogId();
		if (localId == 0) {
			return catalogIns();
		}
		else {
			updCatQtyPS.setInt(1, quantity);
			updCatQtyPS.setInt(2, localId);
			updCatQtyPS.executeUpdate();
			return localId;
		}
	}

	
	// move qty brick with ID=id to working table 
	public static void catalog2work(int id, int qty) throws SQLException {
		
		Brick b = catalogById(id);
		b.quantity = qty;
		b.insertWork();
	}
	
	

	public void catalogDel() throws SQLException {
		
		int localId = 0;
		
		localId = getCatalogId();
		if (localId == 0) {
		}
		else {
			updCatQtyPS.setInt(1, -quantity);
			updCatQtyPS.setInt(2, localId);
			updCatQtyPS.executeUpdate();
		}
	}

	
	public static void tmpDelBricks(int brickid, int quantity) throws SQLException {
		
		updTmpQtyPS.setInt(1, -quantity);
		updTmpQtyPS.setInt(2, brickid);
		updTmpQtyPS.executeUpdate();
	}
	
	
	public static void catDelSet(int brickid, int quantity) throws SQLException {
		
		updCatQtyPS.setInt(1, -quantity);
		updCatQtyPS.setInt(2, brickid);
		updCatQtyPS.executeUpdate();
	}
	
	
	public static void saveTmpToWork() throws SQLException {
		
		Statement st;
		
		st = db.conn.createStatement();
		st.executeUpdate("DELETE from "+workTable+"; INSERT INTO "+workTable+" SELECT * FROM "+tmpTable+" WHERE NOT alt");
	}
	
	
	
	public static int[] countWork() throws SQLException {
		
		PreparedStatement ps;
		ResultSet rs;
		
		ps = db.conn.prepareStatement("SELECT count(*) AS parts, SUM(quantity) AS bricks FROM "+ workTable);
		rs = ps.executeQuery();
		rs.next();
		return new int[] {rs.getInt(1),rs.getInt(2)};

	}
	
	
	
	public static int[] countTemp() throws SQLException {
		
		PreparedStatement ps;
		ResultSet rs;
		
		ps = db.conn.prepareStatement("SELECT count(*) AS parts, SUM(quantity) AS bricks FROM "+ tmpTable);
		rs = ps.executeQuery();
		rs.next();
		return new int[] {rs.getInt(1),rs.getInt(2)};

	}
	
	
	
	////////////////////////////////////////////
	// Advanced check buildable functions
	////////////////////////////////////////////
	
	public static ArrayList<Brick> getMissingBricks(int mode) throws SQLException {
		
		PreparedStatement ps = null;
		
		// query for all brick (counting MOC as negative)
		// insert into tmpbricks ( masterid , designid , partno , name , blid , ldrawid , color , decorid , quantity ) 
		// select c.masterid,c.designid,c.partno,c.name,c.blid,c.ldrawid,c.color,c.decorid,s.qty from mybricks as c join 
		// (select brickid,sum(quantity) as qty from brickset group by brickid) as s on (s.brickid=c.id)
		// empties tmp bricks table
		Brick.createTmpTable();
		if (mode == BRICK_ALL) {
			// gets all bricks
			ps = db.conn.prepareStatement("INSERT INTO " + Brick.tmpTable + 
					" (masterid,designid,partno,name,blid,ldrawid,color,decorid,quantity) " +
					"SELECT c.masterid,c.designid,c.partno,c.name,c.blid,c.ldrawid,c.color,c.decorid,s.qty FROM " +
					Brick.catalogTable + " AS c JOIN (SELECT brickid,SUM(quantity) as qty FROM " + BrickSet.brickTable +
					" GROUP BY brickid) AS s ON (s.brickid=c.id)");
		}
		else if (mode == BRICK_AVAIL) {
			// gets only avail bricks
			ps = db.conn.prepareStatement("INSERT INTO " + Brick.tmpTable + 
					" (masterid,designid,partno,name,blid,ldrawid,color,decorid,quantity) " +
					"SELECT c.masterid,c.designid,c.partno,c.name,c.blid,c.ldrawid,c.color,c.decorid,s.qty FROM " +
					Brick.catalogTable + " AS c JOIN (SELECT brickid,SUM(quantity) as qty FROM " + BrickSet.brickTable +
					" WHERE setid IN (SELECT id FROM " + BrickSet.setTable + " WHERE available) " +
					" GROUP BY brickid) AS s ON (s.brickid=c.id)");
		} 
		else if (mode == BRICK_SELECTED) {
			// gets only avail bricks
			ps = db.conn.prepareStatement("INSERT INTO " + Brick.tmpTable + 
					" (masterid,designid,partno,name,blid,ldrawid,color,decorid,quantity) " +
					"SELECT c.masterid,c.designid,c.partno,c.name,c.blid,c.ldrawid,c.color,c.decorid,s.qty FROM " +
					Brick.catalogTable + " AS c JOIN (SELECT brickid,SUM(quantity) as qty FROM " + BrickSet.brickTable +
					" WHERE setid IN (SELECT id FROM " + BrickSet.setTable + " WHERE selected) " +
					" GROUP BY brickid) AS s ON (s.brickid=c.id)");
		}
		ps.executeUpdate();
		ps = db.conn.prepareStatement("SELECT w.id AS id, w.masterid AS masterid, w.designid AS designid," +
				"w.partno AS partno,w.name AS name,w.blid AS blid,w.ldrawid AS ldrawid,w.color AS color,w.decorid AS decorid," +
				"COALESCE((w.quantity-a.quantity),w.quantity) AS quantity, 0 AS matchid, FALSE AS extra," +
				"FALSE AS alt FROM "+Brick.workTable+" AS w LEFT OUTER JOIN "+Brick.tmpTable+" AS a " +
				"ON (w.blid=a.blid AND w.designid=a.designid AND w.ldrawid=a.ldrawid AND w.color=a.color AND w.decorid=a.decorid) " +
				"WHERE COALESCE((w.quantity-a.quantity),w.quantity) > 0");
		return getPS(ps);
		
	}
	
	

	public static ArrayList<Brick> getAltBricks(Brick b, int colormode, boolean ignoreDecoration) throws SQLException {
		
		PreparedStatement ps;
		String decorCondition,colorCondition,query;
		int idx = 1;

		// gets alternate bricks
		if (!ignoreDecoration) {
			decorCondition = " a.designid=? AND a.blid=? AND a.ldrawid=? AND a.decorid=? ";
		}
		else {
			decorCondition = " a.masterid!='' AND a.masterid=? ";
		}
		if (colormode == COLOR_EXACT) {
			colorCondition = " a.color=? ";
		}
		else if (colormode == COLOR_NEAR) {
			colorCondition = " a.color IN (SELECT mapid FROM "+ BrickColor.table +" WHERE colgrp=?) ";
		}
		else {
			colorCondition = " TRUE ";
		}
		
//		query = "SELECT a.id AS id, a.masterid AS masterid, a.designid AS designid," +
//				"a.partno AS partno,a.name AS name,a.blid AS blid,a.ldrawid AS ldrawid,a.color AS color,a.decorid AS decorid," +
//				"COALESCE((a.quantity-w.quantity),a.quantity) AS quantity, 0 AS matchid, FALSE AS extra," +
//				"FALSE AS alt FROM "+Brick.tmpTable+" AS a LEFT OUTER JOIN "+Brick.workTable+" AS w " +
//				"ON (w.blid=a.blid AND w.designid=a.designid AND w.ldrawid=a.ldrawid AND w.decorid=a.decorid AND w.color=a.color) " +
//				"WHERE " +
//				decorCondition + " AND " +
//				colorCondition + " AND " +
//				"COALESCE((a.quantity-w.quantity),a.quantity) > 0";
		query = "SELECT a.id AS id, a.masterid AS masterid, a.designid AS designid," +
				"a.partno AS partno,a.name AS name,a.blid AS blid,a.ldrawid AS ldrawid,a.color AS color,a.decorid AS decorid," +
				"COALESCE((a.quantity-w.quantity),a.quantity) AS quantity, 0 AS matchid, FALSE AS extra," +
				"FALSE AS alt FROM "+Brick.tmpTable+" AS a LEFT OUTER JOIN " +
				"(SELECT masterid,designid,partno,name,blID,ldrawID,color,decorID,SUM(quantity) as quantity FROM "+Brick.workTable+" GROUP BY " +
				"(masterid,designid,partno,name,blID,ldrawID,color,decorID)) AS w " +
				"ON (w.blid=a.blid AND w.designid=a.designid AND w.ldrawid=a.ldrawid AND w.decorid=a.decorid AND w.color=a.color) " +
				"WHERE " +
				decorCondition + " AND " +
				colorCondition + " AND " +
				"COALESCE((a.quantity-w.quantity),a.quantity) > 0";
		ps = db.conn.prepareStatement(query);
		if (!ignoreDecoration) {
			ps.setString(idx, b.designID);
			idx++;
			ps.setString(idx, b.blID);
			idx++;
			ps.setString(idx, b.ldrawID);
			idx++;
			ps.setString(idx, b.decorID);
			idx++;
		}
		else {
			ps.setString(idx, b.masterID);
			idx++;
		}
		if (colormode == COLOR_EXACT) {
			ps.setInt(idx, b.color);
			idx++;
		}
		else if(colormode == COLOR_NEAR) {
			ps.setInt(idx, BrickColor.getColor(b.color).colorGroup);
			idx++;
		}
		//System.out.println(query);
		return getPS(ps);
	}
	
	


	
	
	

	///////////////////////////////////////////
	// I/O helper methods
	///////////////////////////////////////////

	/**
	 * return brick PNG image encoded Base64 (for HTML embedding) 
	 * @return 3D image of this brick base64 encoded
	 * @throws IOException
	 */
	public String getBrickImageBase64Enc() throws IOException {

		if (shapeView == null) {
			throw new IllegalStateException("[getBrickImageBase64Enc] shapeView not initialized");
		}
		BufferedImage image = getBrickImage(shapeView, frontAndRear);
		ByteArrayOutputStream img = new ByteArrayOutputStream();
		ImageIO.write(image, "png", img);
		return Base64.encodeBase64String(img.toByteArray());
	}
	

	/**
	 * generates an image for brick
	 * @param brickShape a BrickShapeGLView "off screen" to generate images
	 * @param frontandback if true generates a front and back view, else only front view
	 * @return 3D image of this brick
	 */
	public BufferedImage getBrickImage(LDrawGLDisplay brickShape, boolean frontandback) {
		
		brickShape.disableAutoRedraw();
		brickShape.clearAllParts();
		LDRenderedPart rendPart = LDRenderedPart.newRenderedPart(
				LDPrimitive.newGlobalPart(ldrawID, BrickColor.getColor(color).ldraw, new Matrix3D()));
		double diagxz;
		float angle = 20f;
		if (rendPart.getSizeZ() > rendPart.getSizeX()) {
			angle = 70;
		}
		diagxz = Math.sqrt(rendPart.getSizeX()*rendPart.getSizeX() +
			rendPart.getSizeZ()*rendPart.getSizeZ());
		double diagxy = Math.sqrt(rendPart.getSizeX()*rendPart.getSizeX() +
				rendPart.getSizeY()*rendPart.getSizeY());
		float diag = (float) Math.max(diagxz,diagxy);
		int size = brickShape.getCanvas().getPreferredSize().width;
		float ratio = (float) (diag/(size-30f));
		brickShape.resetView();
		brickShape.rotateY(angle);
		brickShape.rotateX(-30);
		brickShape.setOrigin(rendPart.getCenterX(), rendPart.getCenterY(), rendPart.getCenterZ());
		brickShape.setZoom(ratio);
		brickShape.enableAutoRedraw();
		brickShape.addRenderedPart(rendPart);
		//brickShape.update();
		BufferedImage img = brickShape.getStaticImage(htmImgSize, htmImgSize);
		if (! frontandback) {
			return img;
		}
		brickShape.rotateX(180);		
		brickShape.addRenderedPart(rendPart);
		BufferedImage imgf = brickShape.getStaticImage(htmImgSize, htmImgSize);
		int sizex = img.getWidth()+imgf.getWidth();
		// image height is max of two
		int sizey = img.getHeight() > imgf.getHeight() ? img.getHeight() : imgf.getHeight();
		BufferedImage tot = new BufferedImage(sizex,sizey,BufferedImage.TYPE_3BYTE_BGR);
		Graphics paint = tot.getGraphics();
		paint.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), null);
		paint.drawImage(imgf, img.getWidth(), 0, imgf.getWidth(), imgf.getHeight(), null);
		return tot;
	}
	
	

	
	
	public void XMLWrite(XMLStreamWriter xsw) throws XMLStreamException {
		
		xsw.writeStartElement("brick");
		xsw.writeAttribute("id",Integer.toString(id));
		xsw.writeAttribute("masterid",masterID);
		xsw.writeAttribute("designid",designID);
		xsw.writeAttribute("partno",partNO);
		xsw.writeAttribute("name",name);
		xsw.writeAttribute("blid",blID);
		xsw.writeAttribute("ldrawid",ldrawID);
		xsw.writeAttribute("color",Integer.toString(color));
		xsw.writeAttribute("decorid",decorID);
		xsw.writeAttribute("quantity",Integer.toString(quantity));
		xsw.writeAttribute("extra",extra?"1":"0");
		xsw.writeAttribute("alt",alt?"1":"0");
		xsw.writeAttribute("matchid",Integer.toString(matchid));
		xsw.writeEndElement();
		xsw.writeCharacters("\n");
	}

	
	public void BlXmlWrite(XMLStreamWriter xsw) throws XMLStreamException, SQLException {
		
        xsw.writeStartElement("ITEM");
		xsw.writeCharacters("\n");
		xsw.writeStartElement("ITEMTYPE");
		xsw.writeCharacters("P");
		xsw.writeEndElement();
		xsw.writeCharacters("\n");
		xsw.writeStartElement("ITEMID");
		xsw.writeCharacters(blID);
		xsw.writeEndElement();
		xsw.writeCharacters("\n");
		xsw.writeStartElement("COLOR");
		xsw.writeCharacters(Integer.toString(BrickColor.getColor(color).bl));
		xsw.writeEndElement();
		xsw.writeCharacters("\n");
        if (blQty) {
    		xsw.writeStartElement("MINQTY");
    		xsw.writeCharacters(Integer.toString(quantity));
    		xsw.writeEndElement();
    		xsw.writeCharacters("\n");
        }
		xsw.writeStartElement("NOTIFY");
        if (blNotify) {
    		xsw.writeCharacters("Y");
        }
        else {
        	xsw.writeCharacters("N");
        }
		xsw.writeEndElement();
		xsw.writeCharacters("\n");
		xsw.writeStartElement("WANTEDSHOW");
        if (blQueries) {
    		xsw.writeCharacters("Y");
        }
        else {
        	xsw.writeCharacters("N");
        }
		xsw.writeEndElement();
		xsw.writeCharacters("\n");
		xsw.writeStartElement("CONDITION");
        switch (blCondition) {
        case 1:
    		xsw.writeCharacters("N");
    		break;
        case 2:
        	xsw.writeCharacters("U");
        }
		xsw.writeEndElement();
		xsw.writeCharacters("\n");
        if (blListId.length() > 0) {
    		xsw.writeStartElement("WANTEDLISTID");
    		xsw.writeCharacters(blListId);
    		xsw.writeEndElement();
    		xsw.writeCharacters("\n");
        }
        xsw.writeEndElement();
		xsw.writeCharacters("\n");
	}
	

	public static int exportBlXml(ArrayList<Brick> bricks, File f) throws XMLStreamException, IOException, SQLException {
		
		XMLOutputFactory output = XMLOutputFactory.newInstance();
		int excluded = 0;

		XMLStreamWriter writer = output.createXMLStreamWriter(new FileOutputStream(f),"UTF-8");
		writer.writeStartDocument("utf-8", "1.0");
		writer.writeCharacters("\n");
		// a comment for exported list
		writer.writeComment("Exported: "+f.getName()+
				" Date: "+DateFormat.getInstance().format(Calendar.getInstance().getTime()));
		writer.writeCharacters("\n");
		// global start tag
		writer.writeStartElement("INVENTORY");
		writer.writeCharacters("\n");
		for (Brick b: bricks) {
			// excludes bricks without blID 
			if (b.blID == null || b.blID.length() == 0) {
				excluded++;
				continue;
			}
			b.BlXmlWrite(writer);
		}
		writer.writeEndElement();
		writer.writeCharacters("\n");
		writer.writeEndDocument();
		writer.flush();
		writer.close();
		return excluded;
	}
	
	
	
}

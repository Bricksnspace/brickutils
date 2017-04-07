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

/* 
 * database communications and operations
 * Used for isolation between program logic and underlying database engine  
 */



import java.sql.*;

import bricksnspace.dbconnector.DBConnector;





public class BrickDB {
	
	protected static DBConnector db;
	private static final String DBVERCONSTANT = "MPBUVERSION";
	private static final int DBVERSION = 1;

	
	
	public BrickDB(DBConnector dbc) throws SQLException {

		if (dbc == null)
			throw new IllegalArgumentException("[BrickMapping] undefined DBConnector");
		db = dbc;
		// checks for new or already populated database
		if (!db.checkTable(PartMapping.table)) {
			createTables();
			db.setDbVersion(DBVERCONSTANT, DBVERSION);
		}
		else { 
			// checks for database upgrade
			if (db.needsUpgrade(DBVERCONSTANT, DBVERSION)) {
				switch (db.getDbVersion(DBVERCONSTANT)) {
				case -1:
					upgradeFromMinus1();
					break;
				}
			}
		}
		createIndexes();
	}
	
	
	
	/**
	 * Contain all operation and queries to upgrade database tables and schema 
	 * to current version
	 * @throws SQLException
	 */
	private static void upgradeFromMinus1() throws SQLException {
		
		db.setDbVersion(DBVERCONSTANT, DBVERSION);
	}

	
	
	

	private void createTables() throws SQLException {

		Statement st;

		st = db.createStatement();
//		try {
//			st.execute("CREATE TABLE IF NOT EXISTS buproperties (name VARCHAR(32), value VARCHAR(255)); COMMIT");
//		} catch (SQLException e) {
//			//  Blocco catch generato automaticamente
//			e.printStackTrace();
//			return;
//		}
		try {
			st.execute("CREATE TABLE IF NOT EXISTS partmapping (" +
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
		} catch (SQLException e) {
			//  Blocco catch generato automaticamente
			e.printStackTrace();
			return;
		}
//		try {
//			st.execute("CREATE TABLE IF NOT EXISTS blparts (" +
//					"id INT PRIMARY KEY AUTO_INCREMENT, " +
//					"blid VARCHAR(64)," +
//					"name VARCHAR(255)," +
//					"catid INT," +
//					"category VARCHAR(64)," +
//					"weight REAL," +
//					"dimx REAL," +
//					"dimy REAL," +
//					"dimz REAL," +
//					"deleted BOOL," +
//					"lastmod TIMESTAMP" +
//					"); COMMIT ");
//		} catch (SQLException e) {
//			// 
//			e.printStackTrace();
//			return;
//		}
//		try {
//			st.execute("CREATE TABLE IF NOT EXISTS ldrparts (" +
//					"id INT PRIMARY KEY AUTO_INCREMENT, " +
//					"ldrid VARCHAR(64)," +
//					"name VARCHAR(255)," +
//					"category VARCHAR(255)," +
//					"keywords VARCHAR(255)," +
//					"deleted BOOL," +
//					"official BOOL," +
//					"lastmod TIMESTAMP" +
//					"); COMMIT ");
//		} catch (SQLException e) {
//			//  Blocco catch generato automaticamente
//			e.printStackTrace();
//			return;
//		}
		try {
			st.execute("CREATE TABLE IF NOT EXISTS colors (" +
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
		} catch (SQLException e) {
			//  Blocco catch generato automaticamente
			e.printStackTrace();
			return;
		}
//		try {
//			st.execute("CREATE TABLE IF NOT EXISTS blcategories (" +
//					"id INT PRIMARY KEY AUTO_INCREMENT," +
//					"catid INT UNIQUE, " +
//					"name VARCHAR(255)" +
//					"); COMMIT ");
//		} catch (SQLException e) {
//			//  Blocco catch generato automaticamente
//			e.printStackTrace();
//			return;
//		}
//		try {
//			st.execute("CREATE TABLE IF NOT EXISTS blsets (" +
//					"id INT PRIMARY KEY AUTO_INCREMENT," +
//					"setid VARCHAR(64)," +
//					"name VARCHAR(255)," +
//					"category VARCHAR(255)," +
//					"catid INT," +
//					"year INT" +
//					"); COMMIT ");
//		} catch (SQLException e) {
//			// 
//			e.printStackTrace();
//			return;
//		}
		try {
			st.execute("CREATE TABLE IF NOT EXISTS "+Brick.workTable+" (" +
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
		} catch (SQLException e) {
			// TODO Blocco catch generato automaticamente
			e.printStackTrace();
		}
		try {
			st.execute("CREATE TEMP TABLE IF NOT EXISTS "+Brick.tmpTable+" (" +
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
		} catch (SQLException e) {
			// TODO Blocco catch generato automaticamente
			e.printStackTrace();
		}
//		try {
////			st.execute("CREATE TABLE IF NOT EXISTS "+BricklinkColor.table+" (" +
////					"id INT PRIMARY KEY AUTO_INCREMENT," +
////					"name VARCHAR(255)," +
////					"rgb VARCHAR(16)," +
////					"type VARCHAR(32)," +
////					"fromy INT," +
////					"toy INT" +
////					"); COMMIT ");
//			BricklinkColor.createTable();
//		} catch (SQLException e) {
//			// 
//			e.printStackTrace();
//		}
		try {
			st.execute("CREATE TABLE IF NOT EXISTS "+BrickSet.setTable+" (" +
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
		} catch (SQLException e) {
			// TODO Blocco catch generato automaticamente
			e.printStackTrace();
		}
		try {
			st.execute("CREATE TABLE IF NOT EXISTS "+BrickSet.brickTable+" (" +
					"id INT PRIMARY KEY AUTO_INCREMENT," +
					"setid INT," +
					"brickid INT," +
					"quantity INT" +
					"); COMMIT ");
		} catch (SQLException e1) {
			// TODO Blocco catch generato automaticamente
			e1.printStackTrace();
		}
		try {
			st.execute("CREATE TABLE IF NOT EXISTS "+Brick.catalogTable+" (" +
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
		} catch (SQLException e) {
			// TODO Blocco catch generato automaticamente
			e.printStackTrace();
		}

	}
	
	
	
	private void createIndexes() throws SQLException {
		
		Statement st = db.createStatement();
		// index for bricklink parts by blid
//		st.executeUpdate("CREATE INDEX IF NOT EXISTS blp_blid ON "+BricklinkPart.table+"(blid)");
//		st.executeUpdate("CREATE INDEX IF NOT EXISTS ldr_ldid ON "+LDrawPart.table+"(ldrid)");
		st.executeUpdate("CREATE INDEX IF NOT EXISTS pm_mapid ON "+PartMapping.table+"(mapid)");
		st.executeUpdate("CREATE INDEX IF NOT EXISTS pm_lddid ON "+PartMapping.table+"(designid)");
		st.executeUpdate("CREATE INDEX IF NOT EXISTS pm_masterid ON "+PartMapping.table+"(masterid)");
		st.executeUpdate("CREATE INDEX IF NOT EXISTS pm_blid ON "+PartMapping.table+"(blid)");
		st.executeUpdate("CREATE INDEX IF NOT EXISTS pm_ldrawid ON "+PartMapping.table+"(ldrawid)");
	}
	
	

	public void prepareForRelease() throws SQLException {
		
		Statement st = db.createStatement(); 
		st.execute("ALTER TABLE "+Brick.workTable+" ALTER COLUMN ID RESTART WITH 1");
		st.execute("ALTER TABLE "+BrickSet.setTable+" ALTER COLUMN ID RESTART WITH 1");
		st.execute("ALTER TABLE "+BrickSet.brickTable+" ALTER COLUMN ID RESTART WITH 1");
		st.execute("ALTER TABLE "+Brick.catalogTable+" ALTER COLUMN ID RESTART WITH 1");
	}

	
	
}




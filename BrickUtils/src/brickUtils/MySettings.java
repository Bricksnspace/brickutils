/**
	Copyright 2016 Mario Pascucci <mpascucci@gmail.com>
	This file is part of BrickUtils

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

/**
 * @author mario
 *
 */
public class MySettings {
	// properties constants
	public static final String IN_PRODUCTION = "currentcolor";		// use in production colors
	public static final String LDR_OFFICIAL_URL = "offldrurl";		// LDraw official part link
	public static final String LDR_UNOFFICIAL_URL = "unoffldrurl";	// LDraw unofficial link
	public static final String MAP_UPDATE = "checkmapupd";			// checks for mapping updates on startup
	public static final String MAP_UPDATE_URL = "mapupdurl";		// URL for mapping updates
	public static final String APP_CHECK_UPDATE = "appupdate";			// app update check on startup
	public static final String APP_UPDATE_URL = "appupdateurl";			// app update URL
	// app private properties
	public static final String UPDATE_SERIAL = "updateserial";		// last update sequence number (1,2,3,...) 
	public static final String CURRENT_SET = "currset";				// id for set in working list 
	public static final String LDR_LIB_PATH = "ldrawlibpath";		// LDraw official lib path
	public static final String LDR_UNOFF_LIB_PATH = "ldrawunofflibpath";	// unofficial lib path
}

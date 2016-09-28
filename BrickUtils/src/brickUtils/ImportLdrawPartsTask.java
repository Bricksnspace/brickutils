/*
	Copyright 2013-2014 Mario Pascucci <mpascucci@gmail.com>
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


import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.SwingWorker;


/*
 * Imports in background parts from Ldraw part catalog
 * storing it in db
 * @see javax.swing.SwingWorker
 */
public class ImportLdrawPartsTask extends SwingWorker<Integer, Void> {

	int lineNo = 0;
	ZipFile ldrzip;
	boolean official;

	/*
	 * @param dbd Brick DB object
	 * @param z a ZipFile point to LDraw part pack
	 * @param official true if it is an official part pack
	 */
	public ImportLdrawPartsTask(ZipFile z,boolean official) {
		
		ldrzip = z;
		lineNo = ldrzip.size();
		this.official = official;
	}
	
	
	@Override
	protected Integer doInBackground() throws Exception {

		int i = 0;
		LDrawPart dat = new LDrawPart();
		ArrayList<LDrawPart> dbdat;
		Enumeration<?> e;
		LineNumberReader lnr;
		boolean firstLine;
		String line;
		String[] l;
		
		setProgress(0);
		// Delete FTS index to speed up imports
		LDrawPart.deleteFTS();
		dat.official = official;
		e = ldrzip.entries();
		LDrawPart.mark(official);
		while (e.hasMoreElements()) {
			ZipEntry ze = (ZipEntry) e.nextElement();
			dat.category = "";
			dat.name = "";
			dat.keywords = "";
			File f = new File(ze.getName());
			dat.ldrid = f.getName();
			if (f.getParent().endsWith("parts")) {
				lnr = new LineNumberReader(new InputStreamReader(ldrzip.getInputStream(ze)));
				firstLine = true;
				while ((line = lnr.readLine()) != null) {
	                if (firstLine) {
	                    dat.name = line.substring(line.indexOf('0')+1).trim();
	                    firstLine = false;
	                    continue;
	                }
	                l = line.trim().split("\\s+");
	                if (l.length > 1 && l[0].equals("0")) {
	                    if (l[1].toUpperCase().equals("!CATEGORY")) {
	                        dat.category = line.substring(line.indexOf("!CATEGORY")+10).trim();
	                        continue;
	                    }
	                    if (l[1].toUpperCase().equals("!KEYWORDS")) {
	                        dat.keywords = line.substring(line.indexOf("!KEYWORDS")+10).trim();
	                    }
	                }
	                else
	                    continue;
				}
				lnr.close();
				dat.deleted = false;
				dbdat = LDrawPart.get("ldrid = '"+dat.ldrid+"'");
				//System.out.println("Parts: "+dbdat.size());
				if (dbdat.size() == 1) {
					dat.id = dbdat.get(0).id;
					if (official) {
						// if update is official -> update
						dat.update();
					}
					else if (!dbdat.get(0).official) {
						// if update is unofficial update only if part is unofficial
						dat.update();
					}
				}
				else {
					dat.insert();
				}
				//System.out.println("LDRID: "+dat.ldrid+" Cat: "+dat.category+" Key: "+dat.keywords+" Descr: "+dat.name);
			}
			i++;
			setProgress((i*100)/lineNo);
		}
		LDrawPart.createFTS();
		try {
			ldrzip.close();
		} catch (IOException ex) {
			;
		}
		return lineNo;
	}
	
	
	public boolean isOfficial() {
		return official;
	}
	
}


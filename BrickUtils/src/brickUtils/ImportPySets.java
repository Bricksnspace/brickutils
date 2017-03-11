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


import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.SwingWorker;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import bricksnspace.bricklinklib.BricklinkSet;


/*
 * Imports in background an XML file contains a pyBrickUtils sets XML dump
 * storing into catalog
 * @see javax.swing.SwingWorker
 */
public class ImportPySets extends SwingWorker<Integer, Void> {

	File pyxml;

	/*
	 * @param blxml an XML from pyBrickUtils 
	 * 
	 */
	public ImportPySets(File blxml) {
		
		this.pyxml = blxml;
	}
	
	
	@Override
	protected Integer doInBackground() throws SQLException, IOException, XMLStreamException {
		
		int i = 0,qty,brickid;
		XMLEvent e;
		boolean isDoc,isSet,isBrick;
		String setNum,tag;
		Brick b;
		BrickSet bs;
		
		LineNumberReader lnr = new LineNumberReader(new FileReader(pyxml));
		String line;
		int lineNo = 0;
		while ((line = lnr.readLine()) != null) {
			if (line.toLowerCase().indexOf("<set ") > -1)
				lineNo++;
		}
		lnr.close();
		XMLInputFactory xmlFact = XMLInputFactory.newInstance();
		xmlFact.setProperty(XMLInputFactory.IS_COALESCING,true);
		XMLEventReader xer = xmlFact.createXMLEventReader(pyxml.getPath(), new FileInputStream(pyxml));
		setProgress(0);
		isDoc = false;
		isSet = false;
		isBrick = false;
		// "zeroes" brick
		qty = 0;
		brickid = -1;
		setNum = "";
		tag = "";
		bs = null;
		while (xer.hasNext()) {
			e = xer.nextEvent();
			switch (e.getEventType()) {
			case XMLEvent.START_DOCUMENT:
				isDoc = true;
				break;
			case XMLEvent.START_ELEMENT:
				tag = e.asStartElement().getName().getLocalPart();
				if (tag == "brick" && isSet) {
					isBrick = true;
					brickid = Integer.parseInt(e.asStartElement().getAttributeByName(new QName("id")).getValue());
					qty = Integer.parseInt(e.asStartElement().getAttributeByName(new QName("num")).getValue());
					ArrayList<Brick> bl = Brick.getTmpById(brickid);
					if (bl.size() != 1) 
						throw new IllegalArgumentException("Internal program error in ImportPySets: wrong brick id");
					b = bl.get(0);
					if (Brick.catalogById(brickid) == null) {
						// insert empty brick ad a placeholder
						b.quantity = 0;
						b.catalogIns(brickid);
					}
					Brick.tmpDelBricks(brickid, qty);
					if (bs == null) 
						throw new IllegalStateException("Internal program error in ImportPySets: no set defined");
					b.quantity = qty;
					bs.addBrick(b);
				}
				else if ((tag == "set") && isDoc) {
					bs = new BrickSet();
					isSet = true;
					bs.notes = "Imported from pyBrickUtils";
					bs.id = Integer.parseInt(e.asStartElement().getAttributeByName(new QName("id")).getValue());
					setNum = e.asStartElement().getAttributeByName(new QName("setid")).getValue();
					if (setNum.startsWith("@")) {
						//it is a moc/lot
						bs.type = BrickSet.GENERIC_LOT;
						bs.name = setNum.substring(1);
						bs.setid = "";
					}
					else {
						bs.type = BrickSet.OFFICIAL_SET;
						bs.setid = setNum;
						ArrayList<BricklinkSet> bls = BricklinkSet.getById(setNum);
						if (bls.size() != 1) {
							System.out.println("Unable to convert: "+setNum);
							// use data you have
							bs.notes = "Unable to find Bricklink set";
						}
						else {
							bs.name = bls.get(0).getName();
							bs.catid = bls.get(0).getCatid();
							bs.category = bls.get(0).getCatname();
							bs.year = bls.get(0).getYear();
							
						}
					}
					bs.insert();
					
				}
				break;
			case XMLEvent.END_ELEMENT:
				tag = e.asEndElement().getName().getLocalPart();
				if (tag == "brick" && isBrick) {
					isBrick = false;
					//System.out.println(b.toString());
					qty = 0;
					brickid = -1;
				}
				else if ((tag == "set") && isDoc) {
					isSet = false;
					bs = null;
					setNum = "";
					i++;
					setProgress((i*100)/lineNo);
				}
				tag = "";
				break;
			}
		}
		try {
			xer.close();
		} catch (XMLStreamException ex) {
			;
		}
		// link remaining bricks to an "anonymous" set
		ArrayList<Brick> br = Brick.getRemainTmp();
		if (br.size() > 0) {
			bs = new BrickSet();
			bs.name = "Remaining bricks";
			bs.type = BrickSet.GENERIC_LOT;
			bs.notes = "Bricks not belongs to any set";
			bs.insert();
			for (Brick brick: br) {
				bs.addBrick(brick);
			}
		}
		// put all "anomaly" brick (i.e. with less-than-zero quantity in working list
		ArrayList<Brick> ba = Brick.getAnomalyTmp();
		if (ba.size() != 0) {
			for (Brick brick:ba) {
				brick.insertWork();
			}
		}
		return lineNo;
	}
	
}


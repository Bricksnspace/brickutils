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

import javax.swing.SwingWorker;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;


/*
 * Imports in background an XML file contains a BLink XML dump
 * storing into an ArrayList<Brick>
 * @see javax.swing.SwingWorker
 */
public class ImportBlXMLTask extends SwingWorker<Integer, Void> {

	File blxml;

	/*
	 * @param dbd Brick DB object
	 * @param blxml an XML from BL website 
	 * 
	 */
	public ImportBlXMLTask(File blxml) {
		
		this.blxml = blxml;
	}
	
	
	@Override
	protected Integer doInBackground() throws SQLException, IOException, XMLStreamException {
		
		int i = 0,qty,color;
		int matchid;
		XMLEvent e;
		boolean isDoc,isList,isItem,counterpart,alt,extra;
		Characters ch;
		String itemType,tag,blid;
		Brick b;
		
		LineNumberReader lnr = new LineNumberReader(new FileReader(blxml));
		String line;
		int lineNo = 0;
		while ((line = lnr.readLine()) != null) {
			if (line.toLowerCase().indexOf("/itemid") > -1)
				lineNo++;
		}
		lnr.close();
		XMLInputFactory xmlFact = XMLInputFactory.newInstance();
		xmlFact.setProperty(XMLInputFactory.IS_COALESCING,true);
		XMLEventReader xer = xmlFact.createXMLEventReader(blxml.getPath(), new FileInputStream(blxml));
		Brick.createTmpTable();
		setProgress(0);
		isDoc = false;
		isList = false;
		isItem = false;
		// "zeroes" brick
		counterpart = false;
		alt = false;
		matchid = 0;
		blid = "";
		qty = 0;
		color = -1;
		extra = false;
		itemType = "";
		tag = "";
		while (xer.hasNext()) {
			e = xer.nextEvent();
			switch (e.getEventType()) {
			case XMLEvent.START_DOCUMENT:
				isDoc = true;
				break;
			case XMLEvent.START_ELEMENT:
				tag = e.asStartElement().getName().getLocalPart();
				if (tag == "ITEM" && isList) {
					isItem = true;
				}
				else if ((tag == "INVENTORY" || tag == "ORDERS") && isDoc) {
					isList = true;
				}
				break;
			case XMLEvent.END_ELEMENT:
				tag = e.asEndElement().getName().getLocalPart();
				if (tag == "ITEM" && isItem) {
					isItem = false;
					if (itemType.equals("P") && !counterpart) {
						b = PartMapping.getBrickByBlinkId(blid);
						b.quantity = qty;
						b.extra = extra;
						b.alt = alt;
						b.matchid = matchid;
						b.color = BrickColor.getMapByBl(color);
						b.insertTmp();
					}
					// "zeroes" brick
					counterpart = false;
					alt = false;
					matchid = 0;
					blid = "";
					qty = 0;
					color = -1;
					extra = false;
					itemType = "";
					tag = "";
					i++;
					setProgress((i*100)/lineNo);
				}
				else if ((tag == "INVENTORY" || tag == "ORDERS") && isDoc) {
					isList = false;
				}
				break;
			case XMLEvent.CHARACTERS:
				ch = e.asCharacters();
				if (!ch.isIgnorableWhiteSpace() && !ch.isWhiteSpace()) {
					if (tag == "ITEMID" && isItem) {
						blid = ch.getData().trim();
					}
					else if (tag == "ITEMTYPE" && isItem) {
						itemType = ch.getData().trim();
					}
					else if ((tag == "QTY" || tag == "MINQTY") && isItem) {
						try {
							qty = Integer.parseInt(ch.getData());
						} catch (NumberFormatException ex) {
							qty = 0;
						}
					}
					else if (tag == "COLOR" && isItem) {
						try {
							color = Integer.parseInt(ch.getData());
						} catch (NumberFormatException ex) {
							color = 0;
						}
					}
					else if (tag == "EXTRA" && isItem) {
						extra = ch.getData().toLowerCase().trim().equals("y");
					}
					else if (tag == "ALTERNATE" && isItem) {
						alt = ch.getData().toLowerCase().trim().equals("y");
					}
					else if (tag == "MATCHID" && isItem) {
						try {
							matchid = Integer.parseInt(ch.getData());
						} catch (NumberFormatException ex) {
							matchid = 0;
						}
					}
					else if (tag == "COUNTERPART" && isItem) {
						counterpart = ch.getData().toLowerCase().trim().equals("y");
					}
				}
			}
		}
		try {
			xer.close();
		} catch (XMLStreamException ex) {
			;
		}

		return lineNo;
	}
	
}


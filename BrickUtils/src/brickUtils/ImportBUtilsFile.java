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
import javax.xml.stream.events.XMLEvent;


/*
 * Imports in background an XML file contains a pyBrickUtils sets XML dump
 * storing into catalog
 * @see javax.swing.SwingWorker
 */
public class ImportBUtilsFile extends SwingWorker<Integer[], Void> {

	File buxml;

	/*
	 * @param blxml an XML from pyBrickUtils 
	 * 
	 */
	public ImportBUtilsFile(File bkxml) {
		
		this.buxml = bkxml;
	}
	
	
	@Override
	protected Integer[] doInBackground() throws SQLException, IOException, XMLStreamException {
		
		int i = 0;
		int sets = 0, bricks = 0;
		XMLEvent e;
		boolean isDoc,isSet,isBrick,isButils;
		String tag;
		Brick b;
		BrickSet bs;
		
		LineNumberReader lnr = new LineNumberReader(new FileReader(buxml));
		String line;
		int lineNo = 0;
		while ((line = lnr.readLine()) != null) {
			if (line.toLowerCase().indexOf("<brick ") > -1)
				lineNo++;
		}
		lnr.close();
		XMLInputFactory xmlFact = XMLInputFactory.newInstance();
		xmlFact.setProperty(XMLInputFactory.IS_COALESCING,true);
		XMLEventReader xer = xmlFact.createXMLEventReader(buxml.getPath(), new FileInputStream(buxml));
		setProgress(0);
		isDoc = false;
		isSet = false;
		isBrick = false;
		isButils = false;
		// "zeroes" brick
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
					b = new Brick(e.asStartElement());
					bricks += b.quantity;
					bs.addBrick(b);
				}
				else if ((tag == "set") && isButils) {
					bs = new BrickSet(e.asStartElement());
					sets++;
					isSet = true;
					bs.insert();
				}
				else if (tag.equals("brickutils2") && isDoc) {
					isButils = true;
				}
				break;
			case XMLEvent.END_ELEMENT:
				tag = e.asEndElement().getName().getLocalPart();
				if (tag == "brick" && isBrick) {
					isBrick = false;
					i++;
					setProgress((i*100)/lineNo);
				}
				else if ((tag == "set") && isButils) {
					isSet = false;
					bs = null;
				}
				else if (tag.equals("brickutils2") && isDoc) {
					isButils = false;
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
		return new Integer[] { bricks,sets};
	}
	
}


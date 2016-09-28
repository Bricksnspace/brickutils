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
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;


/*
 * Imports in background an XML file contains a pyBrickUtils bricks XML dump
 * storing into catalog
 * @see javax.swing.SwingWorker
 */
public class ImportPyBricks extends SwingWorker<Integer, Void> {

	File pyxml;

	/*
	 * @param dbd Brick DB object
	 * @param blxml an XML from BL website 
	 * 
	 */
	public ImportPyBricks(File blxml) {
		
		this.pyxml = blxml;
	}
	
	
	@Override
	protected Integer doInBackground() throws SQLException, IOException, XMLStreamException {
		
		int i = 0,qty,color;
		int id;
		XMLEvent e;
		boolean isDoc,isList,isItem;
		Characters ch;
		String decorated,tag,lddid,name;
		Brick b;
		
		LineNumberReader lnr = new LineNumberReader(new FileReader(pyxml));
		String line;
		int lineNo = 0;
		while ((line = lnr.readLine()) != null) {
			if (line.toLowerCase().indexOf("/brick>") > -1)
				lineNo++;
		}
		lnr.close();
		XMLInputFactory xmlFact = XMLInputFactory.newInstance();
		xmlFact.setProperty(XMLInputFactory.IS_COALESCING,true);
		XMLEventReader xer = xmlFact.createXMLEventReader(pyxml.getPath(), new FileInputStream(pyxml));
		Brick.createTmpTable();
		setProgress(0);
		isDoc = false;
		isList = false;
		isItem = false;
		// "zeroes" brick
		id = 0;
		lddid = "";
		name = "";
		qty = 0;
		color = -1;
		decorated = "";
		tag = "";
		while (xer.hasNext()) {
			e = xer.nextEvent();
			switch (e.getEventType()) {
			case XMLEvent.START_DOCUMENT:
				isDoc = true;
				break;
			case XMLEvent.START_ELEMENT:
				tag = e.asStartElement().getName().getLocalPart();
				if (tag == "brick" && isList) {
					isItem = true;
					id = Integer.parseInt(e.asStartElement().getAttributeByName(new QName("id")).getValue());
					lddid = e.asStartElement().getAttributeByName(new QName("lddid")).getValue();
					color = Integer.parseInt(e.asStartElement().getAttributeByName(new QName("lddcolor")).getValue());
					qty = Integer.parseInt(e.asStartElement().getAttributeByName(new QName("qty")).getValue());
					decorated = e.asStartElement().getAttributeByName(new QName("decorated")).getValue();
				}
				else if ((tag == "bricks") && isDoc) {
					isList = true;
				}
				break;
			case XMLEvent.END_ELEMENT:
				tag = e.asEndElement().getName().getLocalPart();
				if (tag == "brick" && isItem) {
					isItem = false;
					b = PartMapping.getBrickByDesignId(lddid,null);
					b.id = id;
					b.quantity = qty;
					b.color = BrickColor.getMapByLdd(color);
					b.name = name;
					if (decorated.equals("1")) {
						b.decorID = Integer.toString(id);
					}
					//System.out.println(b.toString());
					b.insertIdTmp();
					// "zeroes" brick
					id = 0;
					lddid = "";
					name = "";
					qty = 0;
					color = -1;
					decorated = "";
					tag = "";
					i++;
					setProgress((i*100)/lineNo);
				}
				else if ((tag == "bricks") && isDoc) {
					isList = false;
				}
				break;
			case XMLEvent.CHARACTERS:
				ch = e.asCharacters();
				if (!ch.isIgnorableWhiteSpace() && !ch.isWhiteSpace()) {
					if (tag == "brick" && isItem) {
						name = ch.getData().trim();
					}
				}
				break;
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


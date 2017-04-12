/*
	Copyright 2013-2017 Mario Pascucci <mpascucci@gmail.com>
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
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.sql.SQLException;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.swing.SwingWorker;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import bricksnspace.brickMapping.BrickColor;


/*
 * Imports in background an LDD projext from LXF or LXFML
 * @see javax.swing.SwingWorker
 */
public class ImportLddProjectTask extends SwingWorker<Integer, Void> {

	File lddxml;

	/*
	 * @param blxml an XML from pyBrickUtils 
	 * 
	 */
	public ImportLddProjectTask(File xml) {
		
		this.lddxml = xml;
	}
	
	
	@Override
	protected Integer doInBackground() throws SQLException, IOException, XMLStreamException {
		
		int i = 0;
		int bricks = 0, color, quantity;
		XMLEvent e;
		boolean isDoc,isList,isBrick,isLdd,multipart;
		String tag,material,decoration,designid,partNO;
		Brick b = null;
		ZipFile zf = null;
		
		LineNumberReader lnr;
		try {
			zf = new ZipFile(lddxml);
			lnr = new LineNumberReader(
					new InputStreamReader(zf.getInputStream(zf.getEntry("IMAGE100.LXFML"))));
		} catch (ZipException ex) { 
			lnr = new LineNumberReader(new FileReader(lddxml));
		}
		String line;
		int lineNo = 0;
		while ((line = lnr.readLine()) != null) {
			if (line.toLowerCase().indexOf("<brick ") > -1)
				lineNo++;
		}
		lnr.close();
		zf.close();
		XMLInputFactory xmlFact = XMLInputFactory.newInstance();
		xmlFact.setProperty(XMLInputFactory.IS_COALESCING,true);
		XMLEventReader xer;
		try {
			zf = new ZipFile(lddxml);
			xer = xmlFact.createXMLEventReader(zf.getInputStream(zf.getEntry("IMAGE100.LXFML")));
		} catch (ZipException ex) {
			xer = xmlFact.createXMLEventReader(lddxml.getPath(), new FileInputStream(lddxml));
		}
		setProgress(0);
		isDoc = false;
		isList = false;
		isBrick = false;
		isLdd = false;
		multipart = false;
		// "zeroes" brick
		material = "";
		decoration = "";
		tag = "";
		designid = "";
		partNO = "";
		color = 0;
		quantity = 0;
		Brick.createTmpTable();
		while (xer.hasNext()) {
			e = xer.nextEvent();
			switch (e.getEventType()) {
			case XMLEvent.START_DOCUMENT:
				isDoc = true;
				break;
			case XMLEvent.START_ELEMENT:
				tag = e.asStartElement().getName().getLocalPart();
				if (tag == "Brick" && isList) {
					isBrick = true;
					designid = e.asStartElement().getAttributeByName(new QName("designID")).getValue().trim();
					try {
						partNO = e.asStartElement().getAttributeByName(new QName("itemNos")).getValue().trim();
					} catch (NullPointerException ex) {
						partNO = "";
					}
					quantity = 1;
					bricks++;
				}
				else if (tag.equals("Part") && isBrick) {
					if (!multipart) {
						material = e.asStartElement().getAttributeByName(new QName("materials")).getValue().trim();
						try {
							color = Integer.parseInt(material.split(",")[0]);
						} catch (NumberFormatException e1) {
							color = 0;
						}
						if (color != 0) {
							color = BrickColor.getMapByLdd(color);
						}
						try {
							decoration = e.asStartElement().getAttributeByName(new QName("decoration")).getValue().trim();
						} catch (NullPointerException ex) {
							decoration = "";
						}
						if (decoration.equals("0") || decoration.equals("0,0") ||
								decoration.equals("0,0,0") || decoration.startsWith("0,0,0,0")) 
							decoration = "";
						multipart = true;
					}
				}
				else if ((tag == "Bricks") && isLdd) {
					isList = true;
				}
				else if (tag.equals("LXFML") && isDoc) {
					isLdd = true;
				}
				break;
			case XMLEvent.END_ELEMENT:
				tag = e.asEndElement().getName().getLocalPart();
				if (tag == "Brick" && isBrick) {
					isBrick = false;
					multipart = false;
					i++;
					b = Brick.brickByDesignId(designid, decoration);
					b.color = color;
					b.partNO = partNO;
					b.quantity = quantity;
					b.tmpAdd();
					quantity = 0;
					color = 0;
					decoration = "";
					partNO = "";
					designid = "";
					setProgress((i*100)/lineNo);
				}
				else if ((tag == "Bricks") && isLdd) {
					isList = false;
				}
				else if (tag.equals("LXFML") && isDoc) {
					isLdd = false;
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
		return bricks;
	}
	
}


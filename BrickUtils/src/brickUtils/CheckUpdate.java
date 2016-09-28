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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.SwingWorker;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;


public class CheckUpdate extends SwingWorker<Integer[], Void> {

	private BusyDialog dlg;
	private String updateUrl;
	private int updSerial;

	
	CheckUpdate(BusyDialog busyDialog, int updSer, String updUrl) {
		
		updateUrl = updUrl;
		updSerial = updSer;
		this.dlg = busyDialog;
	}
	
	
	@Override
	protected Integer[] doInBackground() throws SQLException, IOException, XMLStreamException, BrickException {

		int totparts=0, totcolors=0;
		
		dlg.setMsg("Checking update site...");
		while (true) {
			URL updUrl;
			HttpURLConnection connect = null;
			InputStream urlStream = null;
			long size = 0;
			try {
				if (updateUrl.trim().startsWith("file:")) {
					if (updateUrl.endsWith("/"))
						updUrl = new URL(updateUrl+Integer.toString(updSerial));
					else
						updUrl = new URL(updateUrl+"/"+Integer.toString(updSerial));
					File f = new File(updUrl.toURI());
					size = f.length();
					urlStream = new FileInputStream(f);
				}
				else { 
					if (updateUrl.endsWith("/"))
						updUrl = new URL(updateUrl+Integer.toString(updSerial)+"/download");
					else 
						updUrl = new URL(updateUrl+"/"+Integer.toString(updSerial)+"/download");
					HttpURLConnection.setFollowRedirects(false);
					connect = (HttpURLConnection) updUrl.openConnection();
					int res = connect.getResponseCode();
					int tries = 0;
					//System.out.println("HTTP: "+res);
					while (res>=300 && res<400) {
						// it is a redirect
						updUrl = new URL(connect.getHeaderField("Location"));
						//System.out.println(updUrl);
						// get new connection
						connect = (HttpURLConnection) updUrl.openConnection();
						res = connect.getResponseCode();
						//System.out.println("HTTP: "+res);
						tries++;
						if (tries > 4) {
							throw new IOException("Too many redirect, aborted");
						}
					}
					//System.out.println("HTTP: "+res);
					if (res == 404) {
						// end of updates
						break;
					}
					urlStream = connect.getInputStream();
					size = connect.getContentLength();
				}
			}
			catch (IOException ex) {
				// probably no Internet connection is available...
				ex.printStackTrace();
				break;
			} catch (URISyntaxException e) {
				// no update file
				e.printStackTrace();
				break;
			}
			if (size <= 0) {
				// no updates
				break;
			}
			XMLInputFactory xmlFact = XMLInputFactory.newInstance();
			xmlFact.setProperty(XMLInputFactory.IS_COALESCING,true);
			XMLEventReader xer = xmlFact.createXMLEventReader(urlStream);
			boolean isDoc = false;
			boolean isUpdate = false;
			boolean isPart = false;
			boolean isColor = false;
			String tag = "";
			ArrayList<PartMapping> upm = new ArrayList<PartMapping>();
			ArrayList<BrickColor> ubc = new ArrayList<BrickColor>();
			dlg.setMsg("Getting update #"+updSerial+"...");
			while (xer.hasNext()) {
				XMLEvent e = xer.nextEvent();
				switch (e.getEventType()) {
				case XMLEvent.START_DOCUMENT:
					isDoc = true;
					break;
				case XMLEvent.START_ELEMENT:
					tag = e.asStartElement().getName().getLocalPart();
					if (tag == "parts" && isUpdate) {
						isPart = true;
					}
					else if (tag == "update" && isDoc) {
						isUpdate = true;
					}
					else if (tag == "colors" && isUpdate) {
						isColor = true;
					}
					else if (tag == "partmap" && isPart) {
						upm.add(new PartMapping(e.asStartElement()));
					}
					else if (tag == "colormap" && isColor) {
						ubc.add(new BrickColor(e.asStartElement()));
					}
					break;
				}
			}
			xer.close();
			urlStream.close();
			if (upm.size() == 0 && upm.size() == 0) {
				break;
			}
			dlg.setMsg("Apply update #"+updSerial+"...");
			for (PartMapping p : upm) {
				p.updateMapping();
			}
			for (BrickColor b : ubc) {
				b.updateMapping();
			}
			totparts += upm.size();
			totcolors += ubc.size();
			updSerial++;
		}
		return new Integer[] {totparts,totcolors};
	}

	
	public int getNextUpdate() {
		
		return updSerial;
	}
	
	
}

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


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import javax.swing.SwingWorker;



/*
 * Exports a list of Bricks (as ArrayList) in a HTML file
 * @see javax.swing.SwingWorker
 */
public class HtmlExportTask extends SwingWorker<Integer, Void> {

	File html;
	BrickShapeGLView brickShape;
	BrickSet currentSet;
	ArrayList<Brick> bricks;

	/*
	 * @param blxml an XML from pyBrickUtils 
	 * 
	 */
	public HtmlExportTask(ArrayList<Brick> bricks, BrickSet bs, BrickShapeGLView shape, File html) {
		
		this.html = html;
		currentSet = bs;
		this.bricks = bricks;
		brickShape = shape;
	}
	

	@Override
	protected Integer doInBackground() throws IOException {
		
		int numBricks = 0;
		int totalBricks = 0;
		BufferedWriter fw = null;
		String ts = "";
		
		
		totalBricks = bricks.size();
		Brick.setShapeView(brickShape);
		Brick.setFrontAndRear(true);
		setProgress(0);
		// open output stream
		fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(html),"UTF-8"));
		switch (Brick.getHtmTextSize()) {
		case 0:
			ts = "font-size:small;";
			break;
		case 2:
			ts = "font-size:large;";
			break;
		}
		fw.write("<!DOCTYPE xhtml>\n<html>\n" +
				"<head>\n" +
				"<style type='text/css'>\n" +
				"body { font-family:sans-serif;}\n" +
				"table,th,td { empty-cells:show;border-collapse:collapse; border:1px solid gray; "+
				ts+" padding:0.2em; }\n" +
				".color { border:0 solid white }\n" +
				".decor { border:0 solid white; text-align:center; }\n" +
				"img { border:1 solid white; vertical-align:middle }\n" +
				"</style>\n" +
				(currentSet != null ? "<title>"+currentSet.setid+" - "+currentSet.name+"</title>" : "") + 
				"</head>\n");
        fw.write("<body>\n");
        if (currentSet != null) {
        	fw.write("<h2>"+currentSet.setid+" - "+currentSet.name+"</h2>\n");
        }
        fw.write("<table border='1'><thead><tr><th>ID</th><th>LDD ID</th><th>BLink ID</th><th>LDraw ID</th><th>Color</th><th>Qty</th>");
        if (Brick.isHtmCheck() && ! Brick.isHtmImage()) 
            fw.write("<th>Check</th>");
        if ( ! Brick.isHtmCheck() && Brick.isHtmImage())
            fw.write("<th>Brick shape</th>");
        if (Brick.isHtmCheck() && Brick.isHtmImage())
            fw.write("<th>Brick shape/Check</th>");
        fw.write("<th width='30%'>Description</th></tr></thead>\n<tbody>\n");
		for (Brick b : bricks) {
			numBricks ++;
			setProgress(numBricks * 100 / totalBricks);
			/* prints part id, ldd id, blink id, ldraw id */
            fw.write(String.format("  <tr><td>%d</td><td>%s</td><td>%s</td><td>%s</td><td>",
            		b.id,b.designID,b.blID,b.ldrawID));
            		/* color sub-table with ldd color id, color sample, ldd color name */
            fw.write(String.format("<table width='100%%' class='color'><tr><td width='20%%' class='color'>%d</td>",
            		BrickColor.getColor(b.color).ldd));
            fw.write(String.format("<td class='color'><div style='display:inline;border-style:solid;border-width:0 1em 0 1em;border-color:%s'/></td>",
            		/* color in HTML notation #rrggbb */
            		b.getHtmlColor()));
            fw.write(String.format("<td class='color'>%s</td></tr><tr><td colspan='3' class='decor'>%s</td></tr></table></td>",
            		BrickColor.getColor(b.color).lddName,b.isDecorated()?"Decorated":""));
            fw.write(String.format("<td>%d</td>",b.quantity));
            String image64 = "";
            if (Brick.isHtmImage()) {
            	if (b.ldrawID != "") {
            		image64 = String.format("<img width='%d' height='%d' src='data:image/png;base64,", 
            				Brick.getHtmImgSize()*2,Brick.getHtmImgSize()) +
            				b.getBrickImageBase64Enc() +
            				"'/>";
            	}
            	else {
            		image64 = "No brick image ";
            	}
            }
            if (Brick.isHtmCheck() && ! Brick.isHtmImage()) {
            	// only check
            	fw.write("<td style='text-align:center;padding:0.5 em;'><input type='checkbox'></input></td>");
            }
            else if ( ! Brick.isHtmCheck() && Brick.isHtmImage()) {
            	// only image
            	fw.write("<td style='text-align:center;padding:0.5 em;'>");
            	fw.write(image64);
            	fw.write("</td>");
            }
            else if (Brick.isHtmCheck() && Brick.isHtmImage()) {
            	// both
            	fw.write(String.format("<td style='text-align:center;padding:0.5 em;'><label style=''for='%d'><input type='checkbox' id='%d'>",
            			b.id,b.id));
            	fw.write(image64);
            	fw.write("</input></label></td>");            	
            }
            fw.write(String.format("<td>%s</td>",b.name));
			fw.write("</tr>\n");
		}
		fw.write("</tbody></table>\n</body>\n</html>\n");
		// cleanup
		fw.close();
		return numBricks;
	}
	
}
/*
	Copyright 2013-2015 Mario Pascucci <mpascucci@gmail.com>
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


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.SwingWorker;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;


/*
 * Exports a list of Bricks (as ArrayList) in a HTML file
 * @see javax.swing.SwingWorker
 */
public class TemplateExportTask extends SwingWorker<Integer, Void> {

	File html;
	BrickShapeGLView brickShape;
	ExportedSet currentSet;
	ArrayList<ExportedBrick> bricks;
	private String templateFile;
	private VelocityContext velCon;
	private File outputFile;
	private boolean genImg = false;
	private boolean embedImg = false;

	/*
	 * @param blxml an XML from pyBrickUtils 
	 * 
	 */
	public TemplateExportTask(ArrayList<ExportedBrick> bricks, ExportedSet bs, BrickShapeGLView shape, 
			VelocityContext vcon, String template, File output) {

		templateFile = template;
		velCon = vcon;
		currentSet = bs;
		this.bricks = bricks;
		brickShape = shape;
		outputFile = output;
	}
	
	
	public void generateImages(boolean generate) {
		
		genImg = generate;
	}
	
	
	
	public void embedImages(boolean embed) {
		
		embedImg = embed;
	}
	

	@Override
	protected Integer doInBackground() 
			throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
		
		int numBricks = 0;
		int totalBricks = 0;
		
		FileWriter fw = new FileWriter(outputFile);		
		totalBricks = bricks.size();
		setProgress(0);
		// outputs images
		// if images are embedded as Base64 strings, it's up to Velocity 
		// to call method for image strings
		if (embedImg) {
			Brick.setShapeView(brickShape);
			Brick.setFrontAndRear(true);
		}
		if (genImg && !embedImg) {
			// images output directory
			String name = outputFile.getName();
			if (name.lastIndexOf('.') != -1) {
				name = name.substring(0, name.lastIndexOf('.'));
			}
			File dir = new File(outputFile.getParentFile(),name+"_images");
			dir.mkdir();
			velCon.put("imagedir", dir.getName());
			for (ExportedBrick b : bricks) {
				BufferedImage img = b.getBrickImage(brickShape, true);
				ImageIO.write(img, "png", new File(dir,Integer.toString(b.getId())+".png"));
				numBricks++;
				setProgress(numBricks*100/totalBricks);
			}
		}
		else {
			numBricks = totalBricks;
		}
		// Outputs final file
        Velocity.mergeTemplate(templateFile,"utf8",velCon,fw);
        fw.close();
        setProgress(100);
		return numBricks;
	}
	
}
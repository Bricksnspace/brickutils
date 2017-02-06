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



import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.media.opengl.GLException;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import bricksnspace.ldraw3d.LDrawGLDisplay;


public class HTMLExporter {

	
	private JFileChooser fileExport;
	private ArrayList<Brick> bricks;
	private JFrame frame;
	private ImageIcon[] icnImg;
	private BrickSet currentSet;


	HTMLExporter(JFrame owner, ArrayList<Brick> bricks, JFileChooser file, 
			BrickSet setInfo, ImageIcon[] icnImg) {
		
		this.bricks = bricks;
		fileExport = file;
		frame = owner;
		currentSet = setInfo;
		this.icnImg = icnImg;
	}
	
	
	
	void doExport() {
		
		if (bricks.size() == 0)
			return;
		fileExport.setDialogType(JFileChooser.SAVE_DIALOG);
		fileExport.setDialogTitle("Choose a name for HTML file export");
		int retval = fileExport.showSaveDialog(frame);
		if (retval != JFileChooser.APPROVE_OPTION) 
			return;
		HtmlExportDialog dlg = new HtmlExportDialog(frame);
		dlg.setVisible(true);
		retval = dlg.getResponse();
		if (retval != JOptionPane.OK_OPTION)
			return;
		File fname = fileExport.getSelectedFile();
		BusyDialog busyDialog = new BusyDialog(frame,"Export HTML list",true,true,icnImg);
		busyDialog.setLocationRelativeTo(frame);
		LDrawGLDisplay brickShape = null;
		try {
			LDrawGLDisplay.setAntialias(true);
			brickShape = new LDrawGLDisplay();
			brickShape.getCanvas().setPreferredSize(new Dimension(Brick.getHtmImgSize(),Brick.getHtmImgSize()));
			brickShape.update();
		} catch (GLException e1) {
			e1.printStackTrace();
		}
		HtmlExportTask task = new HtmlExportTask(bricks,currentSet, brickShape,fname);
		busyDialog.setTask(task);
		busyDialog.setMsg("Writing HTML list...");
		Timer timer = new Timer(200, busyDialog);
		task.execute();
		timer.start();
		busyDialog.setVisible(true);
		// after completing task return here
		timer.stop();
		busyDialog.dispose();
		try {
			Integer r = task.get(10, TimeUnit.MILLISECONDS);
			JOptionPane.showMessageDialog(frame, "Exported "+r+" bricks to HTML list.","HTML export",JOptionPane.INFORMATION_MESSAGE);
		}
		catch (ExecutionException e) {
			JOptionPane.showMessageDialog(frame, "Unable to export bricks\nReason: "+e.getLocalizedMessage(), 
					"Export error",JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (InterruptedException e) {
			JOptionPane.showMessageDialog(frame, "Task interrupted!\n Reason: "+e.getLocalizedMessage(), "Task interrupted",JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (TimeoutException e) {
			JOptionPane.showMessageDialog(frame, "Timeout retrieving task output\nReason: "+e.getLocalizedMessage(), "Task timeout",JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}



	
	
}

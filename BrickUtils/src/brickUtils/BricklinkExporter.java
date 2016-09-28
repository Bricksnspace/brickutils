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

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.xml.stream.XMLStreamException;




public class BricklinkExporter {

	
	private JFileChooser fileExport;
	private ArrayList<Brick> bricks;
	private JFrame frame;


	BricklinkExporter(JFrame owner, ArrayList<Brick> bricks, JFileChooser file) {
		
		this.bricks = bricks;
		fileExport = file;
		frame = owner;
	}
	
	
	
	void doExport() {
		
		if (bricks.size() == 0)
			return;
		fileExport.setDialogType(JFileChooser.SAVE_DIALOG);
		fileExport.setDialogTitle("Choose a name for Bricklink XML export");
		int retval = fileExport.showSaveDialog(frame);
		if (retval != JFileChooser.APPROVE_OPTION) 
			return;
		BlExportDialog dlg = new BlExportDialog(frame);
		dlg.setVisible(true);
		retval = dlg.getResponse();
		if (retval != JOptionPane.OK_OPTION)
			return;
		int excluded = 0;
		try {
			excluded = Brick.exportBlXml(bricks, fileExport.getSelectedFile());
			JOptionPane.showMessageDialog(frame, "Exported "+bricks.size()+" Bricks.\nOmitting "+excluded+
					" bricks with no Bricklink ID",
					"Brick Export",JOptionPane.INFORMATION_MESSAGE);
		} catch (XMLStreamException e) {
			JOptionPane.showMessageDialog(frame, "Unable to write export file\nReason: "+e.getLocalizedMessage(), 
					"XML I/O error",JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(frame, "Unable to write export file\nReason: "+e.getLocalizedMessage(), 
					"I/O error",JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(frame, "Problems with database\nReason: "+e.getLocalizedMessage(), 
					"Database error",JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}



	
	
}

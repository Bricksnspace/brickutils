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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import bricksnspace.ldraw3d.LDrawGLDisplay;
import bricksnspace.ldrawlib.LDrawException;
import bricksnspace.ldrawlib.LDrawLib;




public class TemplateExportDialog extends JDialog implements ActionListener {

	
	private static final long serialVersionUID = 624132466157781436L;
	private Container mainPane;
	private JButton okButton;
	private JButton cancelButton;
	/* options:
	 * image size (small,default,large,very large) 
	 * text size (small, default, large)
	 * include check
	 * include image
	 */
	private JTextField userLabel1;
	private JTextField userLabel2;
	private JCheckBox includeImg;
	private JCheckBox embedImg;
	//private JCheckBox excludeIncomplete;
	private JCheckBox excludeExtra;
	private JComboBox<String> imgSize;
	private int userChoice = JOptionPane.CANCEL_OPTION;

	// Velocity vars
	private VelocityContext velCon;

	
	TemplateExportDialog(Frame owner) {
		
		super(owner,"Template export options",true);
		Properties p = new Properties();
		p.setProperty("runtime.references.strict", "true");
		Velocity.init(p);
		velCon = new VelocityContext();
		createDialog();
	}
	
	
	private void createDialog() {
		
		// really create the dialog
//		setPreferredSize(new Dimension(800,500));
		setLocationByPlatform(true);
		mainPane = getContentPane();
		mainPane.setLayout(new BorderLayout(2,2));
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		mainPane.add(buttonPane, BorderLayout.SOUTH);

		userLabel1 = new JTextField();
		userLabel2 = new JTextField();
		embedImg = new JCheckBox("Embedded images (see docs)");
		embedImg.setSelected(false);
		excludeExtra = new JCheckBox("Excludes extra parts");
		excludeExtra.setSelected(true);
//		excludeIncomplete = new JCheckBox("Exclude incomplete mappings");
//		excludeIncomplete.setSelected(true);
		includeImg = new JCheckBox("Include brick image");
		includeImg.setSelected(false);
		includeImg.addActionListener(this);
		imgSize = new JComboBox<String>();
		imgSize.addItem("small (75x75px)");
		imgSize.addItem("default (100x100px)");
		imgSize.addItem("large (150x150px)");
		imgSize.addItem("very large (200x200px)");
		switch (Brick.getHtmImgSize()) {
		case 75:
			imgSize.setSelectedIndex(0);
			break;
		case 100:
			imgSize.setSelectedIndex(1);
			break;
		case 150:
			imgSize.setSelectedIndex(2);
			break;
		case 200:
			imgSize.setSelectedIndex(3);
			break;
		default:
			imgSize.setSelectedIndex(1);
		}
		if (!includeImg.isSelected()) {
			imgSize.setEnabled(false);
			embedImg.setEnabled(false);
		}

		JPanel pane = new JPanel();
		pane.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.ipady = 2;
		gbc.ipadx = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;

		gbc.gridx = 0;
		gbc.gridy = 0;

//		// excludes incomplete mappings
//		pane.add(excludeIncomplete,gbc);
//		
		// excludes extra parts
//		gbc.gridy++;
		pane.add(excludeExtra,gbc);
		
		// includes image
		gbc.gridy += 1;
		pane.add(includeImg,gbc);
		
		// embed images
		gbc.gridy += 1;
		pane.add(embedImg,gbc);

		// image size
		gbc.gridx = 0;
		gbc.gridy += 1;
		pane.add(new JLabel("Image size:",SwingConstants.RIGHT),gbc);
		gbc.gridx = 1;
		pane.add(imgSize,gbc);

		// user label 1
		gbc.gridx = 0;
		gbc.gridy += 1;
		pane.add(new JLabel("User string 1:",SwingConstants.RIGHT),gbc);
		gbc.gridx = 1;
		pane.add(userLabel1,gbc);

		// user label 2
		gbc.gridx = 0;
		gbc.gridy += 1;
		pane.add(new JLabel("User string 2:",SwingConstants.RIGHT),gbc);
		gbc.gridx = 1;
		pane.add(userLabel2,gbc);
		
		mainPane.add(pane, BorderLayout.CENTER);
		
		okButton = new JButton("OK");
		buttonPane.add(okButton);
		okButton.addActionListener(this);
		getRootPane().setDefaultButton(okButton);

		cancelButton = new JButton("Cancel");
		buttonPane.add(cancelButton);
		cancelButton.addActionListener(this);
		
		pack();

	}
	
	
	
	public int getResponse() {
		return userChoice;
	}
	
	
	@Override
	public void actionPerformed(ActionEvent ev) {
		
		if (ev.getSource() == okButton) {
			setVisible(false);
			switch (imgSize.getSelectedIndex()) {
			case 0:
				Brick.setHtmImgSize(75);
				break;
			case 1:
				Brick.setHtmImgSize(100);
				break;
			case 2:
				Brick.setHtmImgSize(150);
				break;
			case 3:
				Brick.setHtmImgSize(200);
				break;
			default:
				Brick.setHtmImgSize(100);
			}
			userChoice = JOptionPane.OK_OPTION;
		}
		else if (ev.getSource() == cancelButton) {
			setVisible(false);
			userChoice = JOptionPane.CANCEL_OPTION;
		}
		else if (ev.getSource() == includeImg) {
			imgSize.setEnabled(includeImg.isSelected());
			embedImg.setEnabled(includeImg.isSelected());
		}
	}


	public void doExport(String templateFile, File outputFile,
			ArrayList<Brick> parts, BrickSet currentSet, LDrawLib ldrlib,
			ImageIcon[] icnImg) {
		
		velCon.put("excludeExtra", excludeExtra.isSelected());
		velCon.put("user1", userLabel1.getText());
		velCon.put("user2", userLabel2.getText());
		ExportedSet expSet = new ExportedSet(currentSet);
		velCon.put("currentSet", expSet);
		ArrayList<ExportedBrick> bricks = new ArrayList<ExportedBrick>();
		for (Brick b: parts) {
			bricks.add(new ExportedBrick(b));
		}
		velCon.put("bricks",bricks);
		BusyDialog busyDialog = new BusyDialog((JFrame)this.getOwner(),"Export list",true,true,icnImg);
		busyDialog.setLocationRelativeTo((JFrame)this.getOwner());
		LDrawGLDisplay brickShape = null;
		try {
			LDrawGLDisplay.setAntialias(true);
			brickShape = new LDrawGLDisplay();
			brickShape.getCanvas().setPreferredSize(new Dimension(Brick.getHtmImgSize(),Brick.getHtmImgSize()));
		} catch (LDrawException e1) {
			e1.printStackTrace();
		}
		TemplateExportTask task = new TemplateExportTask(bricks,expSet,
				brickShape,velCon,templateFile,outputFile);
		task.generateImages(includeImg.isSelected());
		task.embedImages(embedImg.isSelected());
		busyDialog.setTask(task);
		busyDialog.setMsg("Writing exported list...");
		Timer timer = new Timer(200, busyDialog);
		task.execute();
		timer.start();
		busyDialog.setVisible(true);
		// after completing task return here
		timer.stop();
		busyDialog.dispose();
		try {
			Integer r = task.get(10, TimeUnit.MILLISECONDS);
			JOptionPane.showMessageDialog((JFrame)this.getOwner(), "Exported "+r+" bricks via template.","Template export",JOptionPane.INFORMATION_MESSAGE);
		}
		catch (ExecutionException e) {
			JOptionPane.showMessageDialog((JFrame)this.getOwner(), "Unable to export bricks\nReason: "+e.getLocalizedMessage(), 
					"Export error",JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (InterruptedException e) {
			JOptionPane.showMessageDialog((JFrame)this.getOwner(), "Task interrupted!\n Reason: "+e.getLocalizedMessage(), "Task interrupted",JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (TimeoutException e) {
			JOptionPane.showMessageDialog((JFrame)this.getOwner(), "Timeout retrieving task output\nReason: "+e.getLocalizedMessage(), "Task timeout",JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	
	
	
}

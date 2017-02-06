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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.media.opengl.GLException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import bricksnspace.j3dgeom.Matrix3D;
import bricksnspace.ldraw3d.LDRenderedPart;
import bricksnspace.ldraw3d.LDrawGLDisplay;
import bricksnspace.ldrawlib.LDPrimitive;
import bricksnspace.ldrawlib.LDrawColor;
import bricksnspace.ldrawlib.LDrawLib;
import bricksnspace.ldrawlib.LDrawPartType;

public class BrickShapePanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 3165286567872362085L;
	private int size = 200;
	private LDrawGLDisplay brickShape = null;
	private LDrawLib ldrlib;
	private int currentColor = LDrawColor.LTGRAY;
	private String currentPart = "3005";
	private JButton animReset;
	private JLabel shapeRightness;
	private ImageIcon shapeOk;
	private ImageIcon shapeLike;
	private ImageIcon shapeUnkn;
	private int currentPartId;

	
	public BrickShapePanel(LDrawLib ldrlib) {
		
		this.ldrlib = ldrlib;
		initialize();
	}
		
	
	
	public BrickShapePanel(LDrawLib ldrlib, int size) {
		
		this.ldrlib = ldrlib;
		this.size = size;
		initialize();
	}
		
	
		
		
	private void initialize() {
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints gbcs = new GridBagConstraints();
		gbcs.weightx = 0.0;
		gbcs.weighty = 0.0;
		gbcs.fill = GridBagConstraints.HORIZONTAL;
		gbcs.insets = new Insets(2, 2, 2, 2);
		gbcs.ipady = 2;
		gbcs.ipadx = 2;
		if (ldrlib == null) {
			JLabel unavailable = new JLabel("LDraw library not available");
			gbcs.fill = GridBagConstraints.BOTH;
			add(unavailable,gbcs);
			brickShape = null;
			return;
		}
		try {
			LDrawGLDisplay.setAntialias(true);
			brickShape = new LDrawGLDisplay();
			brickShape.getCanvas().setPreferredSize(new Dimension(size,size));
			brickShape.update();
		} catch (GLException e) {
			brickShape = null;
			e.printStackTrace();
		}
		gbcs.fill = GridBagConstraints.NONE;
		gbcs.gridx = 0;
		gbcs.gridy = 0;
		gbcs.gridheight = 3;
		gbcs.gridwidth = 3;
		add(brickShape.getCanvas(),gbcs);
//		animUp = new JButton(new ImageIcon(brickUtils.class.getResource("images/go-up.png")));
//		animDown = new JButton(new ImageIcon(brickUtils.class.getResource("images/go-down.png")));
//		animLeft = new JButton(new ImageIcon(brickUtils.class.getResource("images/go-previous.png")));
//		animRight = new JButton(new ImageIcon(brickUtils.class.getResource("images/go-next.png")));
//		animFlipX = new JButton(new ImageIcon(brickUtils.class.getResource("images/object-flip-vertical.png")));
//		animFlipY = new JButton(new ImageIcon(brickUtils.class.getResource("images/object-flip-horizontal.png")));
		animReset = new JButton(new ImageIcon(brickUtils.class.getResource("images/view-refresh.png")));
		shapeOk = new ImageIcon(brickUtils.class.getResource("images/ok.png"));
		shapeLike = new ImageIcon(brickUtils.class.getResource("images/no.png"));
		shapeUnkn = new ImageIcon(brickUtils.class.getResource("images/off.png"));
		
		gbcs.fill = GridBagConstraints.HORIZONTAL;
//		gbcs.gridx = 3;
//		gbcs.gridy = 0;
//		gbcs.gridheight = 1;
//		gbcs.gridwidth = 1;
//		add(animUp,gbcs);
//		gbcs.gridy = 1;
//		gbcs.weighty = 1.0;
//		add(animFlipX,gbcs);
//		gbcs.gridy = 2;
//		gbcs.weighty = 0.0;
//		add(animDown,gbcs);
//		gbcs.gridx = 0;
//		gbcs.gridy = 3;
//		add(animLeft,gbcs);
//		gbcs.gridx = 1;
//		gbcs.weightx = 1.0;
//		add(animFlipY,gbcs);
//		gbcs.gridx = 2;
//		gbcs.weightx = 0.0;
//		add(animRight,gbcs);
		gbcs.gridy = 3;
		gbcs.gridwidth = 2;
		gbcs.gridx = 0;
		gbcs.weightx = 0.9;
		add(animReset,gbcs);
		shapeRightness = new JLabel(shapeUnkn);
		gbcs.fill = GridBagConstraints.NONE;
		gbcs.gridwidth = 1;
		gbcs.gridheight = 1;
		gbcs.gridx = 2;
		gbcs.gridy = 3;
		gbcs.weightx = 0.1;
		add(shapeRightness,gbcs);
//		animUp.addActionListener(this);
//		animDown.addActionListener(this);
//		animLeft.addActionListener(this);
//		animRight.addActionListener(this);
//		animFlipX.addActionListener(this);
//		animFlipY.addActionListener(this);
		animReset.addActionListener(this);

	}

	
	public void shapeOk() {
		
		shapeRightness.setIcon(shapeOk);
	}
	
	
	public void shapeLike() {
		
		shapeRightness.setIcon(shapeLike);
	}
	
	
	
	public void setLdrawid(String ldrid) {
		
		if (brickShape == null)
			return;
		currentPart = ldrid;
		//System.out.println("CurrentPart = '"+ldrid+"'");
		if (ldrlib.checkPart(currentPart) != LDrawPartType.NONE) {
			displayPart();
		}
		else {
			shapeRightness.setIcon(shapeUnkn);
			brickShape.clearAllParts();
			brickShape.resetView();
		}
	}
		
		
	private void displayPart() {
		brickShape.disableAutoRedraw();
		brickShape.clearAllParts();
		LDRenderedPart rendPart = LDRenderedPart.newRenderedPart(
				LDPrimitive.newGlobalPart(currentPart, currentColor, new Matrix3D()));
		currentPartId = rendPart.getId();
		double diagxz;
		float angle = 20f;
		if (rendPart.getSizeZ() > rendPart.getSizeX()) {
			angle = 70;
		}
		diagxz = Math.sqrt(rendPart.getSizeX()*rendPart.getSizeX() +
			rendPart.getSizeZ()*rendPart.getSizeZ());
		double diagxy = Math.sqrt(rendPart.getSizeX()*rendPart.getSizeX() +
				rendPart.getSizeY()*rendPart.getSizeY());
		float diag = (float) Math.max(diagxz,diagxy);
		float ratio = (float) (diag/(size-50f));
		brickShape.resetView();
		brickShape.rotateY(angle);
		brickShape.rotateX(-30);
		brickShape.setOrigin(rendPart.getCenterX(), rendPart.getCenterY(), rendPart.getCenterZ());
		brickShape.setZoom(ratio);
		brickShape.enableAutoRedraw();
		brickShape.addRenderedPart(rendPart);
		shapeRightness.setIcon(shapeOk);
	}
	
	
	
	public void setColor(int colorid) {
		
		currentColor = BrickColor.getColor(colorid).ldraw;
	}
	
	
	
	public void changeColor(int colorid) {
		
		setColor(colorid);
		if (brickShape == null || currentPart == null)
			return;
		brickShape.delRenderedPart(currentPartId);
		LDRenderedPart rendPart = LDRenderedPart.newRenderedPart(
				LDPrimitive.newGlobalPart(currentPart, currentColor, new Matrix3D()));
		currentPartId = rendPart.getId();
		brickShape.addRenderedPart(rendPart);
	}
	
	
	
	public void setBlid(String blid) {

		Brick b;
		
		if (brickShape == null)
			return;
		try {
			b = PartMapping.getBrickByBlinkId(blid);
			currentPart = b.ldrawID;
		} catch (SQLException e) {
			currentPart = "";
		}
		if (currentPart.length() == 0) {
			shapeRightness.setIcon(shapeLike);
			currentPart = blid + ".dat";
		}
		else {
			shapeRightness.setIcon(shapeOk);
		}
		setLdrawid(currentPart);

	}
	
	
	public void setLddid(String designid, String decorid) {

		Brick b;
	
		if (brickShape == null)
			return;
		try {
			b = PartMapping.getBrickByDesignId(designid,decorid);
			currentPart = b.ldrawID;
		} catch (SQLException e) {
			currentPart = "";
		}
		if (currentPart.length() == 0) {
			shapeRightness.setIcon(shapeLike);
			currentPart = designid + ".dat";
		}
		else {
			shapeRightness.setIcon(shapeOk);
		}
		setLdrawid(currentPart);

	}
	
	
	@Override
	public void actionPerformed(ActionEvent ev) {
		
//		if (ev.getSource() == animUp) {
//			brickShape.rotateUp();
//		}
//		else if (ev.getSource() == animDown) {
//			brickShape.rotateDown();
//		}
//		else if (ev.getSource() == animLeft) {
//			brickShape.rotateLeft();
//		}
//		else if (ev.getSource() == animRight) {
//			brickShape.rotateRight();
//		}
//		else if (ev.getSource() == animFlipX) {
//			brickShape.rotateFlipX();
//		}
//		else if (ev.getSource() == animFlipY) {
//			brickShape.rotateFlipY();
//		}
		if (ev.getSource() == animReset) {
			displayPart();
		}
		
	}

	
	public void dispose() {
		
		//brickShape.destroy();
	}
	

}

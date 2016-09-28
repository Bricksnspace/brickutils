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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import bricksnspace.ldrawlib.LDrawLib;

public class BrickShapePanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 3165286567872362085L;
	private BrickShapeGLView brickShape;
	private JButton animUp;
	private JButton animDown;
	private JButton animLeft;
	private JButton animRight;
	private JButton animFlipX;
	private JButton animFlipY;
	private JButton animReset;
	private JLabel shapeRightness;
	private ImageIcon shapeOk;
	private ImageIcon shapeLike;
	private ImageIcon shapeUnkn;

	
	public BrickShapePanel(LDrawLib ldrlib) {
		
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
		brickShape = new BrickShapeGLView(ldrlib,true,200,200);
		brickShape.setLdrawColor(71, false);
		brickShape.setLdrawid("3005.dat");
		gbcs.fill = GridBagConstraints.NONE;
		gbcs.gridx = 0;
		gbcs.gridy = 0;
		gbcs.gridheight = 3;
		gbcs.gridwidth = 3;
		add(brickShape.getCanvas(),gbcs);
		animUp = new JButton(new ImageIcon(brickUtils.class.getResource("images/go-up.png")));
		animDown = new JButton(new ImageIcon(brickUtils.class.getResource("images/go-down.png")));
		animLeft = new JButton(new ImageIcon(brickUtils.class.getResource("images/go-previous.png")));
		animRight = new JButton(new ImageIcon(brickUtils.class.getResource("images/go-next.png")));
		animFlipX = new JButton(new ImageIcon(brickUtils.class.getResource("images/object-flip-vertical.png")));
		animFlipY = new JButton(new ImageIcon(brickUtils.class.getResource("images/object-flip-horizontal.png")));
		animReset = new JButton(new ImageIcon(brickUtils.class.getResource("images/view-refresh.png")));
		shapeOk = new ImageIcon(brickUtils.class.getResource("images/ok.png"));
		shapeLike = new ImageIcon(brickUtils.class.getResource("images/no.png"));
		shapeUnkn = new ImageIcon(brickUtils.class.getResource("images/off.png"));
		
		gbcs.fill = GridBagConstraints.HORIZONTAL;
		gbcs.gridx = 3;
		gbcs.gridy = 0;
		gbcs.gridheight = 1;
		gbcs.gridwidth = 1;
		add(animUp,gbcs);
		gbcs.gridy = 1;
		gbcs.weighty = 1.0;
		add(animFlipX,gbcs);
		gbcs.gridy = 2;
		gbcs.weighty = 0.0;
		add(animDown,gbcs);
		gbcs.gridx = 0;
		gbcs.gridy = 3;
		add(animLeft,gbcs);
		gbcs.gridx = 1;
		gbcs.weightx = 1.0;
		add(animFlipY,gbcs);
		gbcs.gridx = 2;
		gbcs.weightx = 0.0;
		add(animRight,gbcs);
		gbcs.gridy = 4;
		gbcs.gridwidth = 3;
		gbcs.gridx = 0;
		add(animReset,gbcs);
		shapeRightness = new JLabel(shapeUnkn);
		gbcs.gridwidth = 1;
		gbcs.gridheight = 1;
		gbcs.gridx = 3;
		gbcs.gridy = 3;
		add(shapeRightness,gbcs);
		animUp.addActionListener(this);
		animDown.addActionListener(this);
		animLeft.addActionListener(this);
		animRight.addActionListener(this);
		animFlipX.addActionListener(this);
		animFlipY.addActionListener(this);
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
		shapeRightness.setIcon(shapeOk);
		brickShape.setLdrawid(ldrid);
	}
	
	
	
	public void setColor(int colorid) {
		
		if (brickShape == null)
			return;
		brickShape.setColor(colorid,false);
	}
	
	
	
	public void changeColor(int colorid) {
		
		if (brickShape == null)
			return;
		brickShape.setColor(colorid,true);
	}
	
	
	
	public void setBlid(String blid) {

		Brick b;
		String ldrid;
		
		if (brickShape == null)
			return;
		try {
			b = PartMapping.getBrickByBlinkId(blid);
			ldrid = b.ldrawID;
		} catch (SQLException e) {
			ldrid = "";
		}
		if (ldrid.length() == 0) {
			shapeRightness.setIcon(shapeLike);
			ldrid = blid + ".dat";
		}
		else {
			shapeRightness.setIcon(shapeOk);
		}
		brickShape.setLdrawid(ldrid);

	}
	
	
	public void setLddid(String designid, String decorid) {

		Brick b;
		String ldrid;
	
		if (brickShape == null)
			return;
		try {
			b = PartMapping.getBrickByDesignId(designid,decorid);
			ldrid = b.ldrawID;
		} catch (SQLException e) {
			ldrid = "";
		}
		if (ldrid.length() == 0) {
			shapeRightness.setIcon(shapeLike);
			ldrid = designid + ".dat";
		}
		else {
			shapeRightness.setIcon(shapeOk);
		}
		brickShape.setLdrawid(ldrid);

	}
	
	
	@Override
	public void actionPerformed(ActionEvent ev) {
		
		if (ev.getSource() == animUp) {
			brickShape.rotateUp();
		}
		else if (ev.getSource() == animDown) {
			brickShape.rotateDown();
		}
		else if (ev.getSource() == animLeft) {
			brickShape.rotateLeft();
		}
		else if (ev.getSource() == animRight) {
			brickShape.rotateRight();
		}
		else if (ev.getSource() == animFlipX) {
			brickShape.rotateFlipX();
		}
		else if (ev.getSource() == animFlipY) {
			brickShape.rotateFlipY();
		}
		else if (ev.getSource() == animReset) {
			brickShape.rotateReset();
		}
		
	}

	
	public void dispose() {
		
		brickShape.canvas.destroy();
		brickShape.canvas = null;
	}
	

}

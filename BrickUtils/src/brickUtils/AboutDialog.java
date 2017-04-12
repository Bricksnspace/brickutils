/*
	Copyright 2013-2017 Mario Pascucci <mpascucci@gmail.com>
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
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import bricksnspace.appsettings.AppSettings;
import bricksnspace.appsettings.AppVersion;
import bricksnspace.brickMapping.BrickColor;
import bricksnspace.brickMapping.PartMapping;



public class AboutDialog extends JDialog implements ActionListener {

	
	
	private static final long serialVersionUID = -4693987158080226643L;
	private JButton okButton;
	//private URI uri;
	private JButton thanksButton;
//	private JButton otherSoftware;
	
	
	public AboutDialog(JFrame owner, String title, ImageIcon icn) {
		
		super (owner, title, true);
		setLocationByPlatform(true);
		//setPreferredSize(new Dimension(700,300));
		Container pane = getContentPane();

		JPanel body = new JPanel();
		body.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		body.setLayout(new BoxLayout(body, BoxLayout.PAGE_AXIS));
		JLabel prog = new JLabel("BrickUtils",SwingConstants.CENTER);
		prog.setAlignmentX(Component.CENTER_ALIGNMENT);
		Font font = new Font(Font.SANS_SERIF, Font.BOLD, 24);
		prog.setFont(font);
		body.add(prog);
		JLabel img = new JLabel(icn,SwingConstants.CENTER);
		img.setBorder(BorderFactory.createEtchedBorder());
		img.setAlignmentX(Component.CENTER_ALIGNMENT);
		body.add(img);
		prog = new JLabel("Version: "+AppVersion.myVersion(),SwingConstants.CENTER);
		prog.setAlignmentX(Component.CENTER_ALIGNMENT);
		body.add(prog);
		
		body.add(new JSeparator(SwingConstants.HORIZONTAL));
		
//		prog = new JLabel(new ImageIcon(brickUtils.class.getResource("images/rb-icon.png")));
//		prog.setBorder(BorderFactory.createLineBorder(Color.white, 3));
//		prog.setAlignmentX(Component.CENTER_ALIGNMENT);
//		body.add(prog);
//		
//		prog = new JLabel("<html><center>BrickUtils is offered to the AFOL community<br/>by Romabrick people</center></html>",
//				SwingConstants.CENTER);
//		prog.setAlignmentX(Component.CENTER_ALIGNMENT);
//		body.add(prog);
//
//		body.add(new JSeparator(SwingConstants.HORIZONTAL));

		prog = new JLabel("© 2013-2017 Mario Pascucci <mpascucci@gmail.com>",SwingConstants.CENTER);
		prog.setAlignmentX(Component.CENTER_ALIGNMENT);
		body.add(prog);
		
//		JButton urlButton = new JButton();
//	    urlButton.setText("<HTML><FONT color=\"#000099\"><U>http://www.romabrick.it/brickutils/</U></FONT></HTML>");
//	    urlButton.setHorizontalAlignment(SwingConstants.CENTER);
//	    urlButton.setBorderPainted(false);
//	    urlButton.setOpaque(false);
//	    urlButton.setBackground(Color.WHITE);
//	    try {
//			uri = new URI("http://www.romabrick.it/brickutils/");
//		} catch (URISyntaxException e1) {
//			uri = null;
//		}
//	    urlButton.setToolTipText(uri.toString());
//	    urlButton.addActionListener(new ActionListener() {
//			
//	    	// from: http://stackoverflow.com/questions/527719/how-to-add-hyperlink-in-jlabel
//	    	
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				
//				if (Desktop.isDesktopSupported()) {
//					try {
//				        Desktop.getDesktop().browse(uri);
//				    } catch (IOException ex) {}
//				}
//			}
//		});
//		
//	    urlButton.setAlignmentX(Component.CENTER_ALIGNMENT);
//	    body.add(urlButton);
	    
		body.add(new JSeparator(SwingConstants.HORIZONTAL));
		
		prog = new JLabel(PartMapping.countRules()+" part rules",SwingConstants.CENTER);
		prog.setAlignmentX(Component.CENTER_ALIGNMENT);
		body.add(prog);
		prog = new JLabel(BrickColor.countRules()+" color rules",SwingConstants.CENTER);
		prog.setAlignmentX(Component.CENTER_ALIGNMENT);
		body.add(prog);
		
		body.add(new JSeparator(SwingConstants.HORIZONTAL));

		prog = new JLabel("Update serial "+AppSettings.getInt(MySettings.UPDATE_SERIAL),SwingConstants.CENTER);
		prog.setAlignmentX(Component.CENTER_ALIGNMENT);
		body.add(prog);

		body.add(new JSeparator(SwingConstants.HORIZONTAL));
		
		prog = new JLabel("<html><small>BrickUtils is NOT related, linked,<br/>sponsored or supported by LEGO® Group</small></html>",
				SwingConstants.CENTER);
		prog.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		body.add(prog);
		
		pane.add(body,BorderLayout.CENTER);
		
		// ok button
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		pane.add(buttonPane, BorderLayout.SOUTH);

//		otherSoftware = new JButton("More...");
//		buttonPane.add(otherSoftware);
//		otherSoftware.addActionListener(this);
		

		thanksButton = new JButton("Thanks");
		buttonPane.add(thanksButton);
		thanksButton.addActionListener(this);

		okButton = new JButton("OK");
		buttonPane.add(okButton);
		okButton.addActionListener(this);
		getRootPane().setDefaultButton(okButton);
		
		pack();
	}
	
	
	@Override
	public void actionPerformed(ActionEvent ev) {

		if (ev.getSource() == okButton) {
			setVisible(false);
		}
		else if (ev.getSource() == thanksButton) {
			JOptionPane.showMessageDialog(this,
					"Thanks to:\n"
					+ "-H2 Database (http://www.h2database.com/)\n"
					+ "-JOGL (a Java implementation of OpenGL API http://jogamp.org/)\n"
					+ "-LDraw LEGO® bricks part libraries (http://www.ldraw.org/)\n"
					+ "-Apache Commons Codecs (http://commons.apache.org/proper/commons-codec/)\n"
					+ "-Apache Velocity Template Engine (http://velocity.apache.org/)\n"
					+ "-Eclipse (http://www.eclipse.org/eclipse/)\n"
					+ "-LEGO® for inspiring people (http://www.lego.com)",
					"Thanks to...",
					JOptionPane.INFORMATION_MESSAGE, 
					new ImageIcon(brickUtils.class.getResource("images/star.png")));
		}
//		else if (ev.getSource() == otherSoftware) {
//			JOptionPane.showMessageDialog(this,
//					"Other brick-related software:\n"
//					+ "-JBrickBuilder (http://sourceforge.net/projects/jbrickbuilder)\n"
//					+ "  Easy LDraw 3D model editor in Java\n"
//					+ "-BrickMosaic (http://www.romabrick.it/brickmosaic)\n"
//					+ "  Transform your best photos to a mosaic of bricks",
//					"More brick software",
//					JOptionPane.INFORMATION_MESSAGE, 
//					new ImageIcon(brickUtils.class.getResource("images/checked.png"))
//					);
//		}
	}


	
}

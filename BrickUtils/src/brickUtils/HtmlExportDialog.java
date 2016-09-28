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
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;




public class HtmlExportDialog extends JDialog implements ActionListener {

	
	private static final long serialVersionUID = 624173666157781436L;
	private Container mainPane;
	private JButton okButton;
	private JButton cancelButton;
	/* options:
	 * image size (small,default,large,very large) 
	 * text size (small, default, large)
	 * include check
	 * include image
	 */
	private JCheckBox includeImg;
	private JCheckBox includeCheck;
	private JComboBox<String> textSize;
	private JComboBox<String> imgSize;
	private int userChoice = JOptionPane.CANCEL_OPTION;

	
	HtmlExportDialog(Frame owner) {
		
		super(owner,"HTML export options",true);
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

		textSize = new JComboBox<String>();
		textSize.addItem("small");
		textSize.addItem("default");
		textSize.addItem("large");
		textSize.setSelectedIndex(Brick.getHtmTextSize());
		includeCheck = new JCheckBox("Add a checkbox");
		includeCheck.setSelected(Brick.isHtmCheck());
		includeImg = new JCheckBox("Include brick image");
		includeImg.setSelected(Brick.isHtmImage());
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

		// include check
		pane.add(includeCheck,gbc);
		
		// includes image
		gbc.gridy += 1;
		pane.add(includeImg,gbc);

		// text size
		gbc.gridy += 1;
		pane.add(new JLabel("Text size:",SwingConstants.RIGHT),gbc);
		gbc.gridx = 1;
		pane.add(textSize,gbc);

		// image size
		gbc.gridx = 0;
		gbc.gridy += 1;
		pane.add(new JLabel("Image size:",SwingConstants.RIGHT),gbc);
		gbc.gridx = 1;
		pane.add(imgSize,gbc);
		
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
			userChoice = JOptionPane.OK_OPTION;
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
			Brick.setHtmTextSize(textSize.getSelectedIndex());
			Brick.setHtmCheck(includeCheck.isSelected());
			Brick.setHtmImage(includeImg.isSelected());
		}
		else if (ev.getSource() == cancelButton) {
			setVisible(false);
			userChoice = JOptionPane.CANCEL_OPTION;
		}
		else if (ev.getSource() == includeImg) {
			imgSize.setEnabled(includeImg.isSelected());
		}
	}
	
	
	
}

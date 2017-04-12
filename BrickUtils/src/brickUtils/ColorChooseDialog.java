/*
	Copyright 2013-2017 Mario Pascucci <mpascucci@gmail.com>
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

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import bricksnspace.brickMapping.BrickColor;
import bricksnspace.bricklinklib.BricklinkColor;

public class ColorChooseDialog extends JDialog implements ActionListener {

	
	private static final long serialVersionUID = -1936897292092215791L;
	private Container pane;
	private JPanel normPane;
	private JPanel transPane;
	private JPanel metalPane;
	private JPanel[] colGroupPane;
	private ColorGroup colGroup;
	private HashMap<Integer,JRadioButton> buttons;
	private ButtonGroup buttonGroup;
	private JButton cancelButton;
	private int selected = 0;
	private int response = JOptionPane.CANCEL_OPTION;
	private boolean inProd;

	
	public ColorChooseDialog(Frame owner, String title, boolean modal, int sel, boolean inprod) {

		super(owner, title, modal);
		selected = sel;
		inProd = inprod;
		createDialog();
	}

	
	public ColorChooseDialog(Dialog owner, String title, boolean modal, int sel, boolean inprod) {

		super(owner, title, modal);
		selected = sel;
		inProd = inprod;
		createDialog();
	}

	
//	public ColorChooseDialog(Window owner, String title,
//			ModalityType modalityType, int sel) {
//
//		super(owner, title, modalityType);
//		selected = sel;
//		createDialog();
//	}

	
	private void createDialog() {
		
		// really create the dialog
		setLocationByPlatform(true);
		pane = getContentPane();
		pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));
		normPane =  new JPanel(new GridLayout(0, 10,2,2));
		normPane.setBorder(BorderFactory.createTitledBorder("Solid colors"));
		transPane = new JPanel(new GridLayout(0, 10,2,2));
		transPane.setBorder(BorderFactory.createTitledBorder("Transparent colors"));
		metalPane = new JPanel(new GridLayout(0, 10,2,2));
		metalPane.setBorder(BorderFactory.createTitledBorder("Metallic colors"));
		colGroup = new ColorGroup();
		colGroupPane = new JPanel[colGroup.size()];
		for (int i = 0; i< colGroup.size();i++) {
			colGroupPane[i] = new JPanel(new FlowLayout(FlowLayout.LEFT));
		}
		Set<Integer> idx = BrickColor.getColorList();
		buttons = new HashMap<Integer,JRadioButton>();
		buttonGroup = new ButtonGroup();
		for (int i : idx) {
			if (i == 0)
				continue;
			BrickColor b = BrickColor.getColor(i);
			if (inProd) {
				if (!b.isInProduction()) {
					continue;
				}
			}
			else if (b.getBl() == 0 || b.getLdd() == 0)
				continue;
			JRadioButton rb = new JRadioButton();
			rb.setMinimumSize(new Dimension(50,25));
			rb.setPreferredSize(new Dimension(50,25));
			rb.addActionListener(this);
			rb.setBackground(b.getColor());
			if (inProd)
				rb.setToolTipText("Id:"+b.getLdd()+"("+b.getLddName()+") Bl:"+b.getBl()+"("+
						BricklinkColor.getColor(b.getBl()).getName()+")");
			else
				rb.setToolTipText("Id:"+b.getLdd()+"("+b.getLddName()+") Bl:"+b.getBl()+"("+
						BricklinkColor.getColor(b.getBl()).getName()+(b.isInProduction()? ")": ") (discontinued)"));
			rb.setActionCommand("color");
			rb.setName(Integer.toString(b.getMapid()));
			if (b.getMapid() == selected)
				rb.setSelected(true);
			buttons.put(i,rb);
			buttonGroup.add(rb);
			if (b.isMetallic()) {
				metalPane.add(rb);
			}
			else if (b.isTransparent()) {
				transPane.add(rb);
			}
			else {
				colGroupPane[b.getColorGroup()].add(rb);
			}
		}
		// transfer color group
		for (int i = 0; i< colGroup.size();i++) {
			int c = colGroupPane[i].getComponentCount();
			for (int j=0;j<c;j++) {
				//System.out.println("N:"+colGroupPane[i].getComponentCount()+" j:"+j);
				normPane.add(colGroupPane[i].getComponent(0));
			}
		}
		
		pane.add(normPane);
		pane.add(transPane);
		pane.add(metalPane);
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		pane.add(buttonPane);

		cancelButton = new JButton("Cancel");
		buttonPane.add(cancelButton);
		cancelButton.addActionListener(this);
		
		response = JOptionPane.CANCEL_OPTION;
		pack();
	}

	
	public int getResponse() {
		
		return response;
	}
	
	
	public int getSelected() {
		
		return selected;
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() ==  cancelButton) {
			response = JOptionPane.CANCEL_OPTION;
			setVisible(false);
		}
		else if (e.getActionCommand().equalsIgnoreCase("color")) {
			JRadioButton rb = (JRadioButton) e.getSource();
			try {
				selected = Integer.parseInt(rb.getName());
			} catch (NumberFormatException e1) {
				selected = 0;
			}
			response = JOptionPane.OK_OPTION;
			setVisible(false);
		}

	}
	
}

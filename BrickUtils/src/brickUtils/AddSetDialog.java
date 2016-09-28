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

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


/*
 * dialog to collect user preferences on add bricks to catalog as a Set
 */
public class AddSetDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = -4480043594894408382L;
	private JCheckBox addExtraParts;
	private JCheckBox deleteAfterAdd;
	private JButton okButton;
	private JButton cancelButton;
	private int userChoice = JOptionPane.CANCEL_OPTION;
	private JCheckBox deleteSetData;

	
	
	public AddSetDialog(Frame owner, String title, boolean modal) {
		super(owner, title, modal);
		
		initialize();
	}

	
	public AddSetDialog(Dialog owner, String title, boolean modal) {
		super(owner, title, modal);

		initialize();
	}

	
	
	private void initialize() {
		
		setLocationByPlatform(true);
		getContentPane().setLayout(new BorderLayout());
		JPanel contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(4, 4));
		
		JPanel userPane = new JPanel();
		contentPanel.add(userPane, BorderLayout.SOUTH);
		userPane.setLayout(new BoxLayout(userPane, BoxLayout.Y_AXIS));
		
		addExtraParts = new JCheckBox("Add extra parts");
		addExtraParts.setSelected(true);
		userPane.add(addExtraParts);
		deleteAfterAdd = new JCheckBox("Remove parts added to catalog from list");
		userPane.add(deleteAfterAdd);
		deleteSetData = new JCheckBox("Delete set data after add");
		userPane.add(deleteSetData);
		
		// ok-cancel panel
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		okButton = new JButton("OK");
		buttonPane.add(okButton);
		okButton.addActionListener(this);
		getRootPane().setDefaultButton(okButton);

		cancelButton = new JButton("Cancel");
		buttonPane.add(cancelButton);
		cancelButton.addActionListener(this);
		
		pack();


	}
	

	
	public void addToCatalogOnly(boolean b) {
		
		if (b) {
			deleteSetData.setSelected(false);
			deleteSetData.setEnabled(false);
		}
	}
	
	
	
	public int getResponse() {
		return userChoice;
	}
	
	
	
	public boolean addExtra() {
		
		return addExtraParts.isSelected();
	}
	
	
	
	public boolean removeFromList() {
		
		return deleteAfterAdd.isSelected();
	}
	
	
	
	public boolean deleteSetData() {
		
		return deleteSetData.isSelected();
	}


	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == okButton) {
			userChoice = JOptionPane.OK_OPTION;
			setVisible(false);
		}
		else if (e.getSource() == cancelButton) {
			userChoice = JOptionPane.CANCEL_OPTION;
			setVisible(false);
		}
		
	}
	

}

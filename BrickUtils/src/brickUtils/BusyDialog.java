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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.Timer;


public class BusyDialog extends JDialog implements ActionListener {

	@SuppressWarnings("rawtypes")
	private SwingWorker task;
	private static final long serialVersionUID = 5600670512283134040L;
	private boolean progress = false;
	private JLabel msg;
	private JProgressBar pgr;
	private ImageIcon[] animIcn;
	private static int icnFrame = 0;
	private Timer timer;

	
	public BusyDialog(JFrame owner, String title, boolean progress, ImageIcon[] icn) {
		
		super(owner,title,true);
		this.progress = progress;
		animIcn = icn;
		setLocationByPlatform(true);
		getRootPane().setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		msg = new JLabel("...                ");
		if (animIcn != null) { 
			msg.setIcon(animIcn[0]);
		}
		getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(msg);
		if (progress) {
			// add a progress bar
			pgr = new JProgressBar(SwingConstants.HORIZONTAL,0,100);
			pgr.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
			pgr.setMinimum(0);
			pgr.setMaximum(100);
			pgr.setStringPainted(true);
			getContentPane().add(pgr);
		}
		pack();
	}

	
	@SuppressWarnings("rawtypes")
	public void setTask(SwingWorker task) {
		
		this.task = task;
	}
	
	
	
	public void startTask() {

		timer = new Timer(300,this);
		task.execute();
		timer.start();
		setVisible(true);
	}
	
	
	
	public void setMsg(String txt) {
		
		msg.setText(txt);
		pack();
	}
	
	
	public void setIcon(ImageIcon icn) {
		
		msg.setIcon(icn);
		pack();
	}
	
	
	public void setProgress(int val) {
		
		if (progress) {
			if (val == 0) {
				pgr.setIndeterminate(true);
			}
			else {
				pgr.setIndeterminate(false);
				pgr.setValue(val);
			}	
		}
	}
	
	
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == timer) {
			if (task.isDone()) {
				setVisible(false);
				timer.stop();
				return;
			}
			else {
				if (!isVisible()) {
					setVisible(true);
				}
			}
			if (animIcn != null) {
				msg.setIcon(animIcn[icnFrame]);
				icnFrame = (icnFrame +1) % animIcn.length;
			}
			if (progress) {
				int val = task.getProgress();
				if (val == 0) {
					pgr.setIndeterminate(true);
				}
				else {
					pgr.setIndeterminate(false);
					pgr.setValue(val);
				}	
			}
			pack();
		}
	}
	
	

	
	
	
}

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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;

import javax.swing.SwingWorker;

import bricksnspace.appsettings.AppSettings;
import bricksnspace.busydialog.BusyDialog;



public class LDrawLibUpdate extends SwingWorker<Integer[], Void> {

	private BusyDialog dlg;
	
	public LDrawLibUpdate(BusyDialog dialog) {
		
		this.dlg = dialog;
	}
	
	
	@Override
	protected Integer[] doInBackground() throws SQLException, IOException {

		URL updateUrl;
		
		// official lib
		dlg.setMsg("Downloading official part library");
		updateUrl = new URL(AppSettings.get(MySettings.LDR_OFFICIAL_URL));
		File updFile = new File(AppSettings.get(MySettings.LDR_LIB_PATH));
		File tempFile = new File("ldroff.tmp");
		HttpURLConnection.setFollowRedirects(false);
		URLConnection connect = updateUrl.openConnection();
		int ldrLen = connect.getContentLength();
		byte[] buffer = new byte[4096];
		if (ldrLen != 0) {
			FileOutputStream temp = new FileOutputStream(tempFile);
			InputStream ldr = connect.getInputStream();
			int r;
			int total = 0;
			while ((r = ldr.read(buffer)) > 0) {
				temp.write(buffer, 0, r);
				total += r;
				if ((total % 50000) < 4095) {
					setProgress(total/(ldrLen/100));
				}
			}
			temp.close();
			updFile.delete();
			tempFile.renameTo(updFile);
			ldr.close();
		}

		// Unofficial lib
		dlg.setProgress(0);
		dlg.setMsg("Downloading unofficial part library");
		updateUrl = new URL(AppSettings.get(MySettings.LDR_UNOFFICIAL_URL));
		updFile = new File(AppSettings.get(MySettings.LDR_UNOFF_LIB_PATH));
		tempFile = new File("ldrunoff.tmp");
		HttpURLConnection.setFollowRedirects(false);
		connect = updateUrl.openConnection();
		int ldrunLen = connect.getContentLength();
		if (ldrunLen != 0) {
			FileOutputStream temp = new FileOutputStream(tempFile);
			InputStream ldr = connect.getInputStream();
			int r;
			int total = 0;
			while ((r = ldr.read(buffer)) > 0) {
				temp.write(buffer, 0, r);
				total += r;
				if ((total % 50000) < 4095) {
					setProgress(total/(ldrunLen/100));
				}
			}
			temp.close();
			updFile.delete();
			tempFile.renameTo(updFile);
		}
		
		return new Integer[] {ldrLen,ldrunLen};
	}

	
}

/**
 * DialogUtil.java
 *
 * Created by Gan Jianping on 07/01/15.
 * Copyright (c) 2015 GANJP. All rights reserved.
 *
 */
package org.ganjp.glib.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.util.Log;

/**
 * <p>File Utility</p>
 *
 * @author GanJianping
 * @since 1.0
 */
public class FileUtil {
	
	public static String getStringFromInputStream(InputStream is) {
		 
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
 
		String line;
		try {
 
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
 
		return sb.toString();
 
	}
	
	/**
	 * <p>Delete</p>
	 * 
	 * @param file
	 */
	public static void delete(File file) {
			if (file==null) {
				return;
			}
			try {
				if (file.isFile()) {
					file.delete();
					return;
				}
				File[] subs = file.listFiles();
				if (subs.length == 0) {
					file.delete();
				} else {
					for (int i = 0; i < subs.length; i++) {
						delete(subs[i]);
					}
					file.delete();
				}
			} catch (Exception ex) {
				Log.e("FileUtil", ex.getMessage());
			}
	}
}

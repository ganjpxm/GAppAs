/**
 * ConnectionUtil.java
 *
 * Created by Gan Jianping on 07/01/15.
 * Copyright (c) 2015 GANJP. All rights reserved.
 */
package org.ganjp.glib.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.security.MessageDigest;

import org.apache.http.HttpEntity;
import org.apache.http.entity.BufferedHttpEntity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * <p>Connection Util</p>
 *
 * @author GanJianping
 * @since 1.0.0
 */
public abstract class ConnectionUtil {
	/**
	 * <p>Get file name portion of URL</p>
	 * 
	 * @param url
	 * @return
	 */
	public static String getFileName(URL url) {
		String fileName = url.getFile();
		return fileName.substring(fileName.lastIndexOf('/') + 1);
	}

	/**
	 * <p>Get file name portion of URL</p>
	 * 
	 * @param url
	 * @return
	 */
	public static String getFileName(String url) {
		return url.substring(url.lastIndexOf('/') + 1);
	}

	/**
	 * <p>Get url directory portion of URL</p>
	 * 
	 * @param url
	 * @return
	 */
	public static String getDirectory(URL url) {
		String fileName = url.getFile();
		return fileName.substring(0, fileName.lastIndexOf('/') + 1);
	}

	/**
	 * <p>Get url directory portion of URL</p>
	 * 
	 * @param url
	 * @return
	 */
	public static String getDirectory(String url) {
		return url.substring(0, url.lastIndexOf('/') + 1);
	}

	/**
	 * <p>Byte array to String Hex</p>
	 * 
	 * @param inBytes
	 * @return
	 * @throws Exception
	 */
	public static String returnHex(byte[] inBytes) throws Exception {
		String hexString = null;
		for (int i = 0; i < inBytes.length; i++) {
			hexString += Integer.toString((inBytes[i] & 0xff) + 0x100, 16).substring(1);
		}
		return hexString;
	}

	/**
	 * <p>Check file by md5</p>
	 * 
	 * @param md5
     * @param file
	 * @return
	 * @throws Exception
	 */
	public static boolean checksum(String md5, File file) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			InputStream is = new FileInputStream(file);
			byte[] data = new byte[(int) file.length()];
			is.read(data);
			md.update(data);
			is.close();
			byte[] hash = md.digest();
			if (md5 == returnHex(hash))
				return true;
			else
				return false;
		} catch (Exception e) {
			Log.w("----------Converter----------", "checksum Exception");
			return false;
		}
	}

	/**
	 * <p>Process Entity</p>
	 * 
	 * @param entity
	 * @return
	 * @throws IllegalStateException
	 * @throws java.io.IOException
	 */
	public static String processEntity(HttpEntity entity) throws IllegalStateException, IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
		String line;
		StringBuffer result = new StringBuffer();
		while ((line = br.readLine()) != null)
			result.append(line + "\n");
		br.close();
		return result.toString().trim();
	}

	/**
	 * <p>Process File Entity</p>
	 * 
	 * @param entity
	 * @param file
	 * @throws IllegalStateException
	 * @throws java.io.IOException
	 */
	public static void processFileEntity(HttpEntity entity, File file) throws IllegalStateException, IOException {
		BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
		InputStream is = bufHttpEntity.getContent();
		OutputStream os = new FileOutputStream(file);
		byte[] data = new byte[(int) bufHttpEntity.getContentLength()];
		is.read(data);
		os.write(data);
		is.close();
		os.close();
		bufHttpEntity.consumeContent();
	}


	/**
	 * <p>Process Bitmap Entity</p>
	 * 
	 * @param entity
	 * @throws IllegalStateException
	 * @throws java.io.IOException
	 */
	public static Bitmap processBitmapEntity(HttpEntity entity) throws IOException {
		BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
		Bitmap bm = BitmapFactory.decodeStream(bufHttpEntity.getContent());
		bufHttpEntity.consumeContent();
		return bm;
	}

}
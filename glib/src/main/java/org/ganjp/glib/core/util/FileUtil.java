/**
 * DialogUtil.java
 *
 * Created by Gan Jianping on 07/01/15.
 * Copyright (c) 2015 GANJP. All rights reserved.
 *
 */
package org.ganjp.glib.core.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.ganjp.glib.core.base.Const;
import org.ganjp.glib.core.factory.AlbumStorageDirFactory;

/**
 * <p>File Utility</p>
 *
 * @author GanJianping
 * @since 1.0
 */
public class FileUtil {
    private static final String TAG = "[Core][Util]";
    private static final String TAGClass = "FileUtil : ";
    private static AlbumStorageDirFactory mAlbumStorageDirFactory = AlbumStorageDirFactory.getAlbumStorageDirFactory();

    /**
     * <p>createFile</p>
     *
     * @param fileFullPath
     * @throws RuntimeException
     */
    public static File createFile(String fileFullPath) {
        try {
            File file = new File(fileFullPath);
            if (!file.exists()) {
                String tmpFile = fileFullPath.replace('/',File.separatorChar);
                String dir = fileFullPath.substring(0,tmpFile.lastIndexOf(File.separatorChar));
                File dirFile = new File(dir);
                if(!dirFile.exists()){
                    dirFile.mkdirs();
                }
                file.createNewFile();
            }
            return file;
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * <p>Create the directory for the user's public pictures directory</p>
     *
     * @param albumName
     * @return /storage/sdcard0/Pictures
     */
    public static File createExternalPublicPictureDir(String albumName) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e(TAG, TAGClass + "Directory not created");
        }
        return file;
    }

    /**
     * <p>Create External Private File</p>
     *
     * @param context
     * @param albumName
     * @param fileName
     * @throws IOException
     */
    public static File createExternalPrivatePicture(Context context, String albumName, String fileName) throws IOException {
        File albumFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), albumName);//storage/sdcard0/Android/data/com.dbs.ocr/files/Pictures/Cashline
        return createFile(albumFile.getAbsolutePath() + File.separatorChar + fileName);
    }

    /**
     * <p>Get Album Directory</p>
     *
     * @param albumName
     * @return
     */
    public static File createExternalAlbumStorageDir(final String albumName) {
        File storageDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(albumName);
            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        Log.e(TAG, TAGClass + "failed to create directory");
                        return null;
                    }
                }
            }
        } else {
            Log.e(TAG, TAGClass + "External storage is not mounted READ/WRITE.");
        }
        return storageDir;

    }

    /**
     * <p>Create external public image file, the file name is with date</p>
     *
     * @param albumName
     * @return
     * @throws IOException
     */
    public static File createExternalPublicImageFile(final String albumName) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = Const.IMG_PREFIX + timeStamp;
        File albumF = createExternalAlbumStorageDir(albumName);
        File imageF = File.createTempFile(imageFileName, Const.IMG_SUFFIX, albumF);
        return imageF;
    }


    /**
     * <p>extract zip file</p>
     *
     * @param zipFilePath
     * @param outPath
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static void extract(String zipFilePath, String outPath) throws IOException, FileNotFoundException {
        FileInputStream in = new FileInputStream(zipFilePath);
        extract(in, outPath);
    }

    /**
     * <p>extract zip file</p>
     *
     * @param in
     * @param outPath
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static void extract(InputStream in, String outPath) throws IOException, FileNotFoundException {
        CheckedInputStream cis = new CheckedInputStream(in, new Adler32());
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(cis));
        ZipEntry ze = null;
        char separator = File.separatorChar;
        if (!outPath.endsWith(String.valueOf(separator))) {
            outPath = outPath + separator;
        }

        File dir = new File(outPath);
        if ((dir.exists() && !dir.isDirectory()) || !dir.exists())
            dir.mkdir();

        while ((ze = zis.getNextEntry()) != null) {
            String fileName = ze.getName();
            fileName = fileName.replace('/', separator);

            if (fileName.indexOf(separator) >= 0) {
                String path = fileName;
                // make dir
                String supPath = "";
                while (path.indexOf(separator) > 0) {
                    String oneLeverPath = path.substring(0, path.indexOf(separator));
                    File file = new File(outPath + supPath + oneLeverPath);
                    file.mkdir();

                    path = path.substring(path.indexOf(separator) + 1, path.length());
                    supPath = supPath + oneLeverPath + separator;
                }
            }

            if (ze.isDirectory()) {
                dir = new File(ze.getName());
                dir.mkdir();
            } else {
                fileName = outPath + fileName;

                FileOutputStream oneFile = new FileOutputStream(fileName);
                byte[] bBuf = new byte[1024];
                int length = 0;
                while ((length = zis.read(bBuf)) != -1) {
                    oneFile.write(bBuf, 0, length);
                }
                oneFile.close();
            }
        }
    }

    /**
     * <p>doZip</p>
     *
     * @param baseDir
     * @param fileFullName
     * @throws Exception
     */
    public static void doZip(String baseDir, String fileFullName)  throws Exception {
        doZip(baseDir, fileFullName, null);
    }

    /**
     * <p>doZip</p>
     *
     * @param baseDir
     * @param fileFullName
     * @throws Exception
     */
    public static void doZip(String baseDir, String fileFullName, List<String> filteFileNames)  throws Exception {
        List<File> fileList = getSubFiles(new File(baseDir));
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(fileFullName));
        ZipEntry ze = null;
        byte[] buf = new byte[1024];
        int readLen=0;
        for(int i = 0; i <fileList.size(); i++) {
            File f = (File)fileList.get(i);
            if (filteFileNames!=null && filteFileNames.size()>=1 && filteFileNames.contains(f.getName())) {
                continue;
            }
            ze = new ZipEntry(getAbsFileName(baseDir, f));
            ze.setSize(f.length());
            ze.setTime(f.lastModified());
            zos.putNextEntry(ze);
            InputStream is = new BufferedInputStream(new FileInputStream(f));
            while ((readLen=is.read(buf, 0, 1024))!=-1) {
                zos.write(buf, 0, readLen);
            }
            is.close();
        }
        zos.close();
    }

    /**
     * <p>Get all the files or fold in the baseDir</p>
     *
     * @param baseDir File
     * @return
     */
    public static List<File> getSubFiles(File baseDir){
        List<File> ret = new ArrayList<File>();
        File[] tmp=baseDir.listFiles();
        for (int i = 0; i <tmp.length; i++) {
            if(tmp[i].isFile())
                ret.add(tmp[i]);
            if(tmp[i].isDirectory())
                ret.addAll(getSubFiles(tmp[i]));
        }
        return ret;
    }

    /**
     * <p>getAbsFileName</p>
     *
     * @param baseDir
     * @param realFileName
     * @return
     */
    private static String getAbsFileName(String baseDir, File realFileName){
        File realFile = realFileName;
        File base = new File(baseDir);
        String ret = realFile.getName();
        while (true) {
            realFile = realFile.getParentFile();
            if(realFile==null)
                break;
            if(realFile.equals(base))
                break;
            else
                ret=realFile.getName() +"/"+ret;
        }
        return ret;
    }

    /**
     * <p>copy</p>
     *
     * @param fins
     * @param destine
     */
    public static void copy(InputStream fins, File destine) {
        try {
            if (fins == null)
                return;
            destine.getParentFile().mkdirs();
            FileOutputStream fos = new FileOutputStream(destine);
            byte[] buf = new byte[1024];
            int readLen;
            while ((readLen = fins.read(buf, 0, buf.length)) > 0) {
                fos.write(buf, 0, readLen);
            }
            fos.flush();
            fos.close();
            fins.close();
        } catch (Exception ex) {
            Log.e("FileUtil", ex.getMessage());
        }
    }

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

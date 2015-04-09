package org.ganjp.glib.core.factory;

import java.io.File;

import android.os.Build;

public abstract class AlbumStorageDirFactory {
	public abstract File getAlbumStorageDir(String albumName);
	
	public static AlbumStorageDirFactory getAlbumStorageDirFactory () {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			return new FroyoAlbumDirFactory();
		} else {
			return new BaseAlbumDirFactory();
		}
	}
}

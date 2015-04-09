/**
 * ImageUtil.java
 *
 * Created by Gan Jianping on 07/01/15.
 * Copyright (c) 2015 GANJP. All rights reserved.
 */
package org.ganjp.glib.core.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.Log;
import android.widget.ImageView;

/**
 * <p>Image Utility</p>
 * 
 * @author Gan Jianping
 * @since 1.0.0
 */
public class ImageUtil {
	
	/**
	 * <p>Set image to imageView</p>
	 * 
	 * @param imageView
	 * @param imagePath
	 */
	public static void setImgSmall(ImageView imageView, String imagePath) {
		File imgFile = new File(imagePath);
		if(imgFile.exists()){
            BitmapFactory.Options options = new BitmapFactory.Options(); 
            options.inSampleSize = 8; 
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options); 
            imageView.setImageBitmap(bitmap);
		}
	}
	
	/**
	 * <p>Set image to imageView</p>
	 * 
	 * @param imageView
	 * @param imagePath
	 */
	public static void setImgNormal(ImageView imageView, String imagePath) {
		File imgFile = new File(imagePath);
		if(imgFile.exists()){
            BitmapFactory.Options options = new BitmapFactory.Options(); 
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options); 
            imageView.setImageBitmap(bitmap);
		}
	}
	
    /**
     * <p>Save bitmap to file name<p>
     * 
     * @param bitmap
     * @param fileName
     * @throws java.io.IOException
     */
	public static void saveBitmap(Bitmap bitmap, String fileName) {
        File file = new File(fileName);
        FileOutputStream out;
        try{
            out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out))  {
            	out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e)  {
        	e.printStackTrace(); 
        } finally {
        	if(bitmap != null && !bitmap.isRecycled()){  
                bitmap.recycle();  
                bitmap = null;  
	        }  
	        System.gc();
        }
    }
	
	/**
	 * <p>Scale the image base on min width or height with FIT</p>
	 * <pre>
	 *   ImageUtil.scaleAndSave(mCurrentPhotoPath, Const.IMG_RESOLUCTION_OCR, ScalingLogic.FIT);
	 * </pre>
	 * http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
	 * 
	 * @param imgPath
	 * @param minWidthOrHeight
	 */
	public static void scaleFitAndSave(String imgPath, int minWidthOrHeight) {
		try{
			BitmapFactory.Options opts = new BitmapFactory.Options();  
		    opts.inSampleSize = 4;  
		    Bitmap unscaledBitmap = BitmapFactory.decodeFile(imgPath, opts); 
		    
	        Bitmap scaledBitmap = createScaledBitmap(unscaledBitmap, minWidthOrHeight, minWidthOrHeight, ScalingLogic.FIT);
        	unscaledBitmap.recycle();  
        	unscaledBitmap = null;  
	        System.gc();
	        saveBitmap(scaledBitmap, imgPath);
		} catch (Exception e)  {
        	e.printStackTrace(); 
        }
	}
	
	/**
     * ScalingLogic defines how scaling should be carried out if source and
     * destination image has different aspect ratio.
     *
     * CROP: Scales the image the minimum amount while making sure that at least
     * one of the two dimensions fit inside the requested destination area.
     * Parts of the source image will be cropped to realize this.
     *
     * FIT: Scales the image the minimum amount while making sure both
     * dimensions fit inside the requested destination area. The resulting
     * destination dimensions might be adjusted to a smaller size than
     * requested.
     */
    public static enum ScalingLogic {
        CROP, FIT
    }
    
	/**
     * <p>Utility function for creating a scaled version of an existing bitmap</p>
     *
     * @param unscaledBitmap Bitmap to scale
     * @param dstWidth Wanted width of destination bitmap
     * @param dstHeight Wanted height of destination bitmap
     * @param scalingLogic Logic to use to avoid image stretching
     * @return New scaled bitmap object
     */
    public static Bitmap createScaledBitmap(Bitmap unscaledBitmap, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {
        Rect srcRect = calculateSrcRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), dstWidth, dstHeight, scalingLogic);
        Rect dstRect = calculateDstRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), dstWidth, dstHeight, scalingLogic);
        Bitmap scaledBitmap = Bitmap.createBitmap(dstRect.width(), dstRect.height(), Config.ARGB_8888);
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.drawBitmap(unscaledBitmap, srcRect, dstRect, new Paint(Paint.FILTER_BITMAP_FLAG));
        return scaledBitmap;
    }
	
    /**
     * <p>Calculates destination rectangle for scaling bitmap</p>
     *
     * @param srcWidth Width of source image
     * @param srcHeight Height of source image
     * @param dstWidth Width of destination area
     * @param dstHeight Height of destination area
     * @param scalingLogic Logic to use to avoid image stretching
     * @return Optimal destination rectangle
     */
    public static Rect calculateDstRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {
        if (scalingLogic == ScalingLogic.FIT) {
            final float srcAspect = (float)srcWidth / (float)srcHeight;
            final float dstAspect = (float)dstWidth / (float)dstHeight;

            if (srcAspect > dstAspect) {
                return new Rect(0, 0, dstWidth, (int)(dstWidth / srcAspect));
            } else {
                return new Rect(0, 0, (int)(dstHeight * srcAspect), dstHeight);
            }
        } else {
            return new Rect(0, 0, dstWidth, dstHeight);
        }
    }
    
    /**
     * <p>Calculates source rectangle for scaling bitmap</p>
     *
     * @param srcWidth Width of source image
     * @param srcHeight Height of source image
     * @param dstWidth Width of destination area
     * @param dstHeight Height of destination area
     * @param scalingLogic Logic to use to avoid image stretching
     * @return Optimal source rectangle
     */
    public static Rect calculateSrcRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {
        if (scalingLogic == ScalingLogic.CROP) {
            final float srcAspect = (float)srcWidth / (float)srcHeight;
            final float dstAspect = (float)dstWidth / (float)dstHeight;

            if (srcAspect > dstAspect) {
                final int srcRectWidth = (int)(srcHeight * dstAspect);
                final int srcRectLeft = (srcWidth - srcRectWidth) / 2;
                return new Rect(srcRectLeft, 0, srcRectLeft + srcRectWidth, srcHeight);
            } else {
                final int srcRectHeight = (int)(srcWidth / dstAspect);
                final int scrRectTop = (int)(srcHeight - srcRectHeight) / 2;
                return new Rect(0, scrRectTop, srcWidth, scrRectTop + srcRectHeight);
            }
        } else {
            return new Rect(0, 0, srcWidth, srcHeight);
        }
    }

}

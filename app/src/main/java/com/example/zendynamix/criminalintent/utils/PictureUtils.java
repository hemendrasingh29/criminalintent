package com.example.zendynamix.criminalintent.utils;

/**
 * Created by zendynamix on 7/1/2016.
 */
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

/**
 * Created by zendynamix on 6/30/2016.
 */
public class PictureUtils {
    public static Bitmap getscaledBitmap(String path, Activity activity){
        Point size= new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return getScaleBitmap(path,size.x,size.y);
    }
    public static Bitmap getScaleBitmap(String path, int destWidth, int destHeight) {
        // read in dimension of image on disk
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        float srcWidth = options.outWidth;
        float srcheight = options.outHeight;
        // figure out how much to scale down
        int inSimpleSize = 1;
        if (srcheight > destHeight || srcWidth > destWidth) {
            if (srcWidth > srcheight) {
                inSimpleSize = Math.round(srcheight / destHeight);
            } else {
                inSimpleSize = Math.round(srcWidth / srcWidth);
            }
        }
        options = new BitmapFactory.Options();
        options.inSampleSize = inSimpleSize;
        // Read In and create final bitmap
        return BitmapFactory.decodeFile(path, options);
    }

}


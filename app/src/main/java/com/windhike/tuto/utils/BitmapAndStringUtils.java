package com.windhike.tuto.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;

/**
 * Created by Administrator on 2017/7/2 0002.
 */

public class BitmapAndStringUtils {
    public static String convertIconToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

    }

    public static Bitmap convertStringToIcon(String st) {
        byte[] bitmapArray = Base64.decode(st, Base64.DEFAULT);
        return byteToBitmap(bitmapArray);
    }

    public static Bitmap byteToBitmap(byte[] imgByte) {
        InputStream input = null;
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            input = new ByteArrayInputStream(imgByte);
            SoftReference<Bitmap> softRef = new SoftReference(BitmapFactory.decodeStream(
                    input, null, options));
            bitmap = softRef.get();
            System.gc();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }
}
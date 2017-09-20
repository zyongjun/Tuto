package com.windhike.annotation.common;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Build;
import android.os.Build.VERSION;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.windhike.annotation.configsapp.Configs;
import com.windhike.annotation.datastorage.ExternalStorage;
import com.windhike.annotation.model.DeviceObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class Utilities {
    public static void recycleImageView(ImageView imgImage, boolean isCallGC) {
        if (imgImage != null) {
            Drawable drawable = imgImage.getDrawable();
            if (drawable instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                if (bitmap != null) {
                    bitmap.recycle();
                    imgImage.setImageBitmap(null);
                    imgImage.setImageDrawable(null);
                }
            }
            if (isCallGC) {
                System.gc();
            }
        }
    }

    public static void recycleView(View view) {
        if (view != null) {
            Drawable drawable = view.getBackground();
            if (drawable instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                if (bitmap != null) {
                    bitmap.recycle();
                    view.setBackgroundDrawable(null);
                }
            }
            System.gc();
        }
    }

    public static void recycleBitmap(Bitmap bmp) {
        if (bmp != null) {
            bmp.recycle();
        }
        System.gc();
    }

    public static String formatIntegerWithDecimal(int number) {
        return new StringBuilder(String.valueOf(new DecimalFormat("#,###").format((long) number))).toString();
    }

    public static String formatDate(String strDate) {
        String result = "";
        if (strDate != null) {
            String temp = strDate;
            try {
                if (strDate.length() > 10) {
                    result = new SimpleDateFormat("EEEE,MMMM dd,yyyy").format(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(strDate.substring(0, 10)));
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (ParseException e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    public static boolean isNetworkAvailable(Context mContext) {
        NetworkInfo i = ((ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return i != null && i.isConnected() && i.isAvailable();
    }

    public static final void sendEmail(Context context, String body, String title) {
        Intent i = new Intent("android.intent.action.SEND");
        i.setType("message/rfc822");
        i.putExtra("android.intent.extra.SUBJECT", title);
        i.putExtra("android.intent.extra.TEXT", body);
        try {
            context.startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_LONG).show();
        }
    }

    public static DeviceObject getDeviceObject(Activity mActivity) {
        DeviceObject deviceObj = new DeviceObject();
        try {
            DisplayMetrics metrics = new DisplayMetrics();
            mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            deviceObj.setmSystemVersion(VERSION.RELEASE);
            deviceObj.setmModel(Build.MODEL);
            deviceObj.setmResolution(metrics.widthPixels + " X " + metrics.heightPixels);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deviceObj;
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        Log.e("width", "width ====== " + width + " height === " + height);
        float scaleWidth = ((float) newWidth) / ((float) width);
        float scaleHeight = ((float) newHeight) / ((float) height);
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
    }

    public static Bitmap decodeFile(boolean FlgScale, File f) {
        Exception e;
        if (FlgScale) {
            try {
                InputStream in = new FileInputStream(f);
                InputStream in2;
                try {
                    Bitmap b;
                    Options o = new Options();
                    o.inJustDecodeBounds = true;
                    Bitmap bitmap = BitmapFactory.decodeStream(in, null, o);
                    in.close();
                    if (bitmap != null) {
                        bitmap.recycle();
                    }
                    int scale = 1;
                    while (((double) (o.outWidth * o.outHeight)) * (1.0d / Math.pow((double) scale, 2.0d)) > ((double) 150000)) {
                        scale++;
                    }
                    LogUtils.LogError("getBitmap", "scale = " + scale + ", orig-width: " + o.outWidth + ", orig-height: " + o.outHeight);
                    in2 = new FileInputStream(f);
                    if (scale > 1) {
                        scale--;
                        o = new Options();
                        o.inSampleSize = scale;
                        b = BitmapFactory.decodeStream(in2, null, o);
                        int height = b.getHeight();
                        int width = b.getWidth();
                        LogUtils.LogError("getBitmap", "1th scale operation dimenions - width: " + width + ",height: " + height);
                        double y = Math.sqrt(((double) 150000) / (((double) width) / ((double) height)));
                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) ((y / ((double) height)) * ((double) width)), (int) y, true);
                        b.recycle();
                        b = scaledBitmap;
                        System.gc();
                    } else {
                        b = BitmapFactory.decodeStream(in2);
                    }
                    in2.close();
                    LogUtils.LogError("getBitmap", "bitmap size - width: " + b.getWidth() + ", height: " + b.getHeight());
                    return b;
                } catch (Exception e2) {
                    e = e2;
                    in2 = in;
                    try {
                        LogUtils.LogError("getBitmap", e.getMessage());
                        return null;
                    } catch (Exception e3) {
                        e3.printStackTrace();
                        return null;
                    }
                }
            } catch (Exception e4) {
                LogUtils.LogError("getBitmap", e4.getMessage());
                return null;
            }
        }
        Options o2 = new Options();
        o2.inSampleSize = 1;
        try {
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    public static int calculateInSampleSize(Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        if (height <= reqHeight && width <= reqWidth) {
            return 1;
        }
        int heightRatio = Math.round(((float) height) / ((float) reqHeight));
        int widthRatio = Math.round(((float) width) / ((float) reqWidth));
        if (heightRatio < widthRatio) {
            return heightRatio;
        }
        return widthRatio;
    }

    public static String getImageDrawNameEncript(String folderName) {
        return UUID.nameUUIDFromBytes(folderName.getBytes()).toString();
    }

    public static String getImageDrawFileNamePath(String folderProjectName, String filename) {
        try {
            folderProjectName = AESHelper.encrypt(Configs.ENCRYPT_KEY, folderProjectName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ExternalStorage.getRootPathSDCard() + Configs.ROOT_FOLDER_NAME + "/" + folderProjectName + "/" + getImageDrawNameEncript(filename);
    }

    public static void createFolderProject(String folderProjectName) {
        try {
            folderProjectName = AESHelper.encrypt(Configs.ENCRYPT_KEY, folderProjectName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtils.LogError("createFolderProject", "createFolderProject   ==== " + folderProjectName);
        if (!ExternalStorage.isForderExited(Configs.ROOT_FOLDER_NAME+"/" + folderProjectName)) {
            ExternalStorage.CreateForder(Configs.ROOT_FOLDER_NAME+"/" + folderProjectName);
        }
    }

    public static void copyFileUsingFileStreams(File source, File dest) throws Throwable {
        Throwable th;
        InputStream input = null;
        OutputStream output = null;
        try {
            InputStream input2 = new FileInputStream(source);
            try {
                OutputStream output2 = new FileOutputStream(dest);
                try {
                    byte[] buf = new byte[1024];
                    while (true) {
                        int bytesRead = input2.read(buf);
                        if (bytesRead <= 0) {
                            input2.close();
                            output2.close();
                            return;
                        }
                        output2.write(buf, 0, bytesRead);
                    }
                } catch (Throwable th2) {
                    th = th2;
                    output = output2;
                    input = input2;
                }
            } catch (Throwable th3) {
                th = th3;
                input = input2;
                input.close();
                output.close();
                throw th;
            }
        } catch (Throwable th4) {
            th = th4;
            input.close();
            output.close();
            throw th;
        }
    }

    public static Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        } else {
            canvas.drawColor(-1);
        }
        view.draw(canvas);
        return returnedBitmap;
    }

    public static void saveToSdcardPNG(File file, Bitmap bitmap) {
            try {
                OutputStream outStream = new FileOutputStream(file);
                bitmap.compress(CompressFormat.PNG, 100, outStream);
                outStream.flush();
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

    }

    public static int convertDpToPixel(Context mContext, float dp) {
        return (int) (dp * (((float) mContext.getResources().getDisplayMetrics().densityDpi) / 160.0f));
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static int checkDeviceAutoRotateBitmap(String photoPath) {
        try {
            switch (new ExifInterface(photoPath).getAttributeInt("Orientation", 1)) {
                case 3:
                    LogUtils.LogError("ORIENTATION_ROTATE_180", "ORIENTATION_ROTATE_180");
                    return 180;
                case 6:
                    LogUtils.LogError("ORIENTATION_ROTATE_90", "ORIENTATION_ROTATE_90");
                    return 90;
                default:
                    LogUtils.LogError("checkDeviceAutoRotateBitmap", "default default default default");
                    return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static Bitmap scaleToActualAspectRatioForImageLoader(Bitmap bitmap, int deviceWidth, int deviceHeight) {
        if (bitmap == null) {
            return null;
        }
        int scaleHeight;
        int scaleWidth;
        int bitmapHeight = bitmap.getHeight();
        int bitmapWidth = bitmap.getWidth();
        if (deviceWidth / deviceHeight >= bitmapWidth / bitmapHeight) {
            scaleHeight = deviceHeight;
            scaleWidth = (scaleHeight * bitmapWidth) / bitmapHeight;
        } else {
            scaleWidth = deviceWidth;
            scaleHeight = (scaleWidth * bitmapHeight) / bitmapWidth;
        }
        Bitmap bitmapCopy = null;
        try {
            return Bitmap.createScaledBitmap(bitmap, scaleWidth, scaleHeight, true);
        } catch (Exception e) {
            e.printStackTrace();
            return bitmapCopy;
        }
    }

    private static final String TAG = "Utilities";
    public static Bitmap scaleToActualAspectRatio(Bitmap bitmap, int deviceWidth, int deviceHeight) {

        if (bitmap == null) {
            return null;
        }
        Log.e(TAG, "setMainBitmap: dw:"+deviceWidth+"--dh:"+deviceHeight+"====bw:"+bitmap.getWidth()+"==bh:"+bitmap.getHeight() );
        int scaleHeight;
        int scaleWidth;
        int bitmapHeight = bitmap.getHeight();
        int bitmapWidth = bitmap.getWidth();

        if (deviceWidth / deviceHeight >= bitmapWidth / bitmapHeight) {
            scaleHeight = deviceHeight;
            scaleWidth = (scaleHeight * bitmapWidth) / bitmapHeight;
        } else {
            scaleWidth = deviceWidth;
            scaleHeight = (scaleWidth * bitmapHeight) / bitmapWidth;
        }
        try {
            Bitmap bitmapCopy = Bitmap.createScaledBitmap(bitmap, scaleWidth, scaleHeight, true);
            if (bitmap.getWidth() == bitmapCopy.getWidth() || bitmap.getHeight() == bitmapCopy.getHeight()) {
                return bitmapCopy;
            }
            bitmap.recycle();
            return bitmapCopy;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static int scalePX(int dp_size, Context context) {
        return (int) ((((float) dp_size) * context.getResources().getDisplayMetrics().density) + Configs.SLIDING_MENU_WIDTH);
    }

    public static boolean isConnectingToInternet(Context mContext) {
        ConnectivityManager connectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo state : info) {
                    if (state.getState() == State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static String encodeFileToBase64Binary(String fileName) throws IOException {
        String encodedString = new String(Base64.encodeToString(loadFile(new File(fileName)), Base64.DEFAULT));
        LogUtils.LogError("encodeFileToBase64Binary", "encodeFileToBase64Binary ==== " + encodedString);
        return encodedString;
    }

    public static String encodeFileToBase64Binary(byte[] input) throws IOException {
        String encodedString = new String(Base64.encodeToString(input, Base64.DEFAULT));
        LogUtils.LogError("encodeFileToBase64Binary", "encodeFileToBase64Binary ==== " + encodedString);
        return encodedString;
    }

    public static byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        byte[] bytes = new byte[((int) file.length())];
        int offset = 0;
        while (offset < bytes.length) {
            int numRead = is.read(bytes, offset, bytes.length - offset);
            if (numRead < 0) {
                break;
            }
            offset += numRead;
        }
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }
        is.close();
        return bytes;
    }

    public static String getCurrentHour() {
        return new SimpleDateFormat("yyyy-MM-dd_'T'_hh-mm-ss").format(new Date());
    }

    public static String getCurrentUpdatedExisttingSession() {
        String[] dataDate = new SimpleDateFormat("dd:MMMM:yyyy").format(Calendar.getInstance().getTime()).split(":");
        return dataDate[1] + " " + dataDate[0] + ", " + dataDate[2];
    }

    public static String getFileSize(String fileName) {
        float ret = (float) getFileSizeInBytes(fileName);
        LogUtils.LogError("ret", "ret ==== " + ret);
        String fileSize = "";
        DecimalFormat df = new DecimalFormat("#0.#");
        if (ret < 1048576.0f) {
            return df.format((double) (ret / 1024.0f)) + " KB";
        }
        if (ret < 1.07374182E9f) {
            return df.format((double) (ret / 1048576.0f)) + " MB";
        }
        if (ret < 0.0f) {
            return df.format((double) (ret / 1.07374182E9f)) + " GB";
        }
        return fileSize;
    }

    public static String getFileSize(float filesize) {
        float ret = filesize;
        LogUtils.LogError("ret", "ret ==== " + ret);
        String fileSize = "";
        DecimalFormat df = new DecimalFormat("#0.#");
        if (ret < 1048576.0f) {
            return df.format((double) (ret / 1024.0f)) + " KB";
        }
        if (ret < 1.07374182E9f) {
            return df.format((double) (ret / 1048576.0f)) + " MB";
        }
        if (ret < 0.0f) {
            return df.format((double) (ret / 1.07374182E9f)) + " GB";
        }
        return fileSize;
    }

    public static long getFileSizeInBytes(String fileName) {
        long ret = 0;
        File f = new File(fileName);
        if (f.isFile()) {
            return f.length();
        }
        if (f.isDirectory()) {
            File[] contents = f.listFiles();
            for (int i = 0; i < contents.length; i++) {
                if (contents[i].isFile()) {
                    ret += contents[i].length();
                } else if (contents[i].isDirectory()) {
                    ret += getFileSizeInBytes(contents[i].getPath());
                }
            }
        }
        return ret;
    }

    public static boolean checkExistingSession(Context mContext) {
        ArrayList<String> listProjectName = new ArrayList();
        try {
            if (filterDataFiles(mContext.getFilesDir().listFiles()).length > 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static File[] filterDataFiles(File[] fs) {
        if (fs == null || fs.length == 0) {
            return new File[0];
        }
        int i;
        ArrayList<File> res = new ArrayList();
        for (File file : fs) {
            if (getFileExtension(file.getName()).equalsIgnoreCase("dat")) {
                res.add(file);
            }
        }
        File[] array = new File[res.size()];
        for (i = 0; i < res.size(); i++) {
            array[i] = res.get(i);
        }
        return array;
    }

    public static String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(46);
        return dotIndex == -1 ? "" : fileName.substring(dotIndex + 1);
    }


    public static String getFileName(String filePath) {
        String[] getTempFileName = filePath.split("/");
        String fileName = getTempFileName[getTempFileName.length - 1];
        return fileName.substring(0, fileName.length() - 4).trim();
    }

    public static String encryptFileName(String fileName) {
        String fileNameEncrypt = fileName;
        try {
            fileNameEncrypt = AESHelper.encrypt(Configs.ENCRYPT_KEY, fileNameEncrypt);
        } catch (Exception e) {
        }
        return fileNameEncrypt;
    }
}
package com.windhike.annotation.reuse;

import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * author: zyongjun on 2017/7/1 0001.
 * email: zhyongjun@windhike.cn
 */

public class FileUtils {
    public static File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        try {
            File image = File.createTempFile(imageFileName,
                    ".jpg",
                    Environment.getExternalStorageDirectory());
            return image;
        } catch (IOException e) {

        }
        return null;
    }

    public static InputStream getFileInputStream(String path) {
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(new File(path));
        } catch (FileNotFoundException var3) {
            var3.printStackTrace();
        }

        return fileInputStream;
    }

    public static byte[] getByteFromUri(Uri uri) {
        InputStream input = getFileInputStream(uri.getPath());

        Object var3;
        try {
            int count = 0;

            while(true) {
                if(count == 0) {
                    count = input.available();
                    if(count != 0) {
                        continue;
                    }
                }

                byte[] bytes = new byte[count];
                input.read(bytes);
                byte[] var4 = bytes;
                return var4;
            }
        } catch (Exception var14) {
            var3 = null;
        } finally {
            if(input != null) {
                try {
                    input.close();
                } catch (IOException var13) {
                    ;
                }
            }

        }

        return (byte[])var3;
    }

}

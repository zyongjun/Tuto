package com.windhike.annotation.datastorage;

import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.StatFs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ExternalStorage {
    public static ExternalStorage _obj;
    public Object objParcel;

    public static synchronized ExternalStorage getObj() {
        ExternalStorage externalStorage;
        synchronized (ExternalStorage.class) {
            if (_obj == null) {
                _obj = new ExternalStorage();
            }
            externalStorage = _obj;
        }
        return externalStorage;
    }

    public static synchronized boolean externalMemoryAvailable() {
        boolean equals;
        synchronized (ExternalStorage.class) {
            equals = Environment.getExternalStorageState().equals("mounted");
        }
        return equals;
    }

    public static synchronized long getAvailableExternalMemorySize() {
        long availableBlocks;
        synchronized (ExternalStorage.class) {
            if (externalMemoryAvailable()) {
                StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
                availableBlocks = ((long) stat.getAvailableBlocks()) * ((long) stat.getBlockSize());
            } else {
                availableBlocks = -1;
            }
        }
        return availableBlocks;
    }

    public static synchronized long getTotalExternalMemorySize() {
        long blockCount;
        synchronized (ExternalStorage.class) {
            if (externalMemoryAvailable()) {
                StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
                blockCount = ((long) stat.getBlockCount()) * ((long) stat.getBlockSize());
            } else {
                blockCount = -1;
            }
        }
        return blockCount;
    }

    public static synchronized boolean staticisExternalStorageNotEnoughSpace(long bytesFile) {
        boolean z;
        synchronized (ExternalStorage.class) {
            if (externalMemoryAvailable()) {
                StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
                int blockCount = stat.getBlockCount();
                int blockAvailable = stat.getAvailableBlocks();
                int blockSize = stat.getBlockSize();
                z = blockCount - blockAvailable > 0 && bytesFile > (((long) blockCount) * ((long) blockSize)) - (((long) (blockCount - blockAvailable)) * ((long) blockSize));
            } else {
                z = false;
            }
        }
        return z;
    }

    public static synchronized boolean isFileExited(String path) {
        boolean z;
        synchronized (ExternalStorage.class) {
            File file = new File(new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().getAbsolutePath())).append(File.separator).append(path).toString());
            z = file.exists() && file.isFile() && file.length() > 0;
        }
        return z;
    }

    public static synchronized boolean isForderExited(String path) {
        boolean z;
        synchronized (ExternalStorage.class) {
            File root = new File(new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().getAbsolutePath())).append(File.separator).append(path).toString());
            z = root.exists() && root.isDirectory();
        }
        return z;
    }

    public static synchronized boolean CreateForder(String path) {
        boolean z = false;
        synchronized (ExternalStorage.class) {
            try {
                if (externalMemoryAvailable()) {
                    if (!isForderExited(path)) {
                        new File(new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().getAbsolutePath())).append(File.separator).append(path).toString()).mkdirs();
                        z = true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return z;
    }

    public static synchronized boolean isDeleteFile(String path) {
        boolean z;
        synchronized (ExternalStorage.class) {
            if (isFileExited(path)) {
                new File(new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().getAbsolutePath())).append(File.separator).append(path).toString()).delete();
                z = true;
            } else {
                z = false;
            }
        }
        return z;
    }

    public static synchronized boolean isDeleteAllFileInForder(String path) {
        boolean z = false;
        synchronized (ExternalStorage.class) {
            File dir = new File(new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().getAbsolutePath())).append(File.separator).append(path).toString());
            if (dir.isDirectory()) {
                try {
                    File[] fs = dir.listFiles();
                    if (fs == null || fs.length == 0) {
                        z = true;
                    } else {
                        int i = 0;
                        while (i < fs.length) {
                            try {
                                fs[i].delete();
                                i++;
                            } catch (Exception e) {
                            }
                        }
                        z = true;
                    }
                } catch (Exception e2) {
                }
            }
        }
        return z;
    }

    public static synchronized boolean isDeleteForder(String path) {
        boolean delete;
        synchronized (ExternalStorage.class) {
            if (isForderExited(path)) {
                File dir = new File(new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().getAbsolutePath())).append(File.separator).append(path).toString());
                if (dir.isDirectory()) {
                    String[] list = dir.list();
                    for (String file : list) {
                        supportDeleteRecursive(new File(dir, file));
                    }
                }
                delete = dir.delete();
            } else {
                delete = false;
            }
        }
        return delete;
    }

    public static synchronized boolean supportDeleteRecursive(File dir) {
        boolean delete;
        synchronized (ExternalStorage.class) {
            if (dir.exists()) {
                if (dir.isDirectory()) {
                    String[] list = dir.list();
                    for (String file : list) {
                        supportDeleteRecursive(new File(dir, file));
                    }
                }
                delete = dir.delete();
            } else {
                delete = false;
            }
        }
        return delete;
    }

    public static synchronized boolean WriteFileToSDcard(String path, InputStream is) throws IOException {
        boolean z = false;
        synchronized (ExternalStorage.class) {
            File file = new File(new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().getAbsolutePath())).append(File.separator).append(path).toString());
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fileOutput = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            while (true) {
                int bufferLength = is.read(buffer);
                if (bufferLength <= 0) {
                    break;
                }
                try {
                    fileOutput.write(buffer, 0, bufferLength);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            fileOutput.close();
            if (!true || file.length() <= 0) {
                if (isFileExited(path)) {
                    isDeleteFile(path);
                }
            } else {
                z = true;
            }
        }
        return z;
    }

    public static synchronized InputStream readFileFromSDcard(String path) {
        InputStream inputStream;
        synchronized (ExternalStorage.class) {
            try {
                File file = new File(new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().getAbsolutePath())).append(File.separator).append(path).toString());
                if (!file.exists()) {
                    inputStream = null;
                } else if (file.length() == 0) {
                    file.delete();
                    inputStream = null;
                } else {
                    inputStream = new FileInputStream(file);
                }
            } catch (Exception e) {
                e.printStackTrace();
                inputStream = null;
            }
        }
        return inputStream;
    }

    public static synchronized boolean WriteFileParcelToSDcard(String path, Parcelable objParcel) {
        boolean z = false;
        synchronized (ExternalStorage.class) {
            try {
                File file = new File(new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().getAbsolutePath())).append(File.separator).append(path).toString());
                if (file.exists()) {
                    file.delete();
                }
                FileOutputStream fileOutput = new FileOutputStream(file);
                Parcel p = Parcel.obtain();
                objParcel.writeToParcel(p, 0);
                fileOutput.write(p.marshall());
                fileOutput.flush();
                fileOutput.close();
                z = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return z;
    }

    public static synchronized Parcel readFileParcelFromSDcard(String path) {
        Parcel parcel;
        synchronized (ExternalStorage.class) {
            try {
                File file = new File(new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().getAbsolutePath())).append(File.separator).append(path).toString());
                if (!file.exists()) {
                    parcel = null;
                } else if (file.length() == 0) {
                    file.delete();
                    parcel = null;
                } else {
                    FileInputStream fis = new FileInputStream(file);
                    byte[] data = new byte[((int) file.length())];
                    fis.read(data);
                    fis.close();
                    parcel = Parcel.obtain();
                    parcel.unmarshall(data, 0, data.length);
                    parcel.setDataPosition(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
                parcel = null;
            }
        }
        return parcel;
    }

    public static synchronized String getRootPathSDCard() {
        String stringBuilder;
        synchronized (ExternalStorage.class) {
            stringBuilder = new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().getAbsolutePath())).append(File.separator).toString();
        }
        return stringBuilder;
    }
}


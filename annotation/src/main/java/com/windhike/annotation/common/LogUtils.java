package com.windhike.annotation.common;

import android.util.Log;

public class LogUtils {
    public static final String ERROR_TAG = "ERROR_PIXNOTES";
    public static final String INFO_TAG = "INFO_PIXNOTES";
    public static final boolean IS_ALLOW_LOG = true;

    public static void LogError(String msg) {
        Log.e(ERROR_TAG, msg);
    }

    public static void LogInfo(String msg) {
        Log.i(INFO_TAG, msg);
    }

    public static void LogError(String tag, String msg) {
        Log.e(tag, msg);
    }

    public static void LogInfo(String tag, String msg) {
        Log.i(tag, msg);
    }
}

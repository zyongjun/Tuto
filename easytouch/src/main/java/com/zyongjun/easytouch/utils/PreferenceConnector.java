package com.zyongjun.easytouch.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * author: gzzyj on 2017/2/9.
 * email:zhyongjun@windhike.cn
 */
public class PreferenceConnector {
    public static final String KEY_FLOAT_OPENED = "KEY_FLOAT_OPENED";
    public static final int MODE = Context.MODE_PRIVATE;
    public static final String PREF_NAME = "PEOPLE_PREFERENCES";

    public static void clearAllValue(Context context) {
        getEditor(context).clear().commit();
    }

    public static SharedPreferences.Editor getEditor(Context context) {
        return getPreferences(context).edit();
    }

    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, MODE);
    }

    public static boolean readBoolean(Context paramContext, String key, boolean value) {
        return getPreferences(paramContext).getBoolean(key, value);
    }

    public static float readFloat(Context context, String key, float value) {
        return getPreferences(context).getFloat(key, value);
    }

    public static int readInteger(Context context, String key, int value) {
        return getPreferences(context).getInt(key, value);
    }

    public static long readLong(Context context, String key, long value) {
        return getPreferences(context).getLong(key, value);
    }

    public static String readString(Context context, String key, String value) {
        return getPreferences(context).getString(key, value);
    }

    public static void removeValueForKey(Context context, String key) {
        getEditor(context).remove(key).commit();
    }

    public static void writeBoolean(Context context, String key, boolean value) {
        getEditor(context).putBoolean(key, value).commit();
    }

    public static void writeFloat(Context context, String key, float value) {
        getEditor(context).putFloat(key, value).commit();
    }

    public static void writeInteger(Context context, String key, int value) {
        getEditor(context).putInt(key, value).commit();
    }

    public static void writeLong(Context context, String key, long value) {
        getEditor(context).putLong(key, value).commit();
    }

    public static void writeString(Context context, String key, String value) {
        getEditor(context).putString(key, value).commit();
    }


}


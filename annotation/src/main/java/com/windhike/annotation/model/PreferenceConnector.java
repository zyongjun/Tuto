package com.windhike.annotation.model;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * author: gzzyj on 2017/2/9.
 * email:zhyongjun@windhike.cn
 */
public class PreferenceConnector {
    public static final String KEY_FLOAT_OPENED = "KEY_FLOAT_OPENED";
    public static final int MODE = 0;
    public static final String PREF_NAME = "PEOPLE_PREFERENCES";

    public static void clearAllValue(Context paramContext)
    {
        getEditor(paramContext).clear().commit();
    }

    public static SharedPreferences.Editor getEditor(Context paramContext)
    {
        return getPreferences(paramContext).edit();
    }

    public static SharedPreferences getPreferences(Context paramContext)
    {
        return paramContext.getSharedPreferences("PEOPLE_PREFERENCES", 0);
    }

    public static boolean readBoolean(Context paramContext, String paramString, boolean paramBoolean)
    {
        return getPreferences(paramContext).getBoolean(paramString, paramBoolean);
    }

    public static float readFloat(Context paramContext, String paramString, float paramFloat)
    {
        return getPreferences(paramContext).getFloat(paramString, paramFloat);
    }

    public static int readInteger(Context paramContext, String paramString, int paramInt)
    {
        return getPreferences(paramContext).getInt(paramString, paramInt);
    }

    public static long readLong(Context paramContext, String paramString, long paramLong)
    {
        return getPreferences(paramContext).getLong(paramString, paramLong);
    }

    public static String readString(Context paramContext, String paramString1, String paramString2)
    {
        return getPreferences(paramContext).getString(paramString1, paramString2);
    }

    public static void removeValueForKey(Context paramContext, String paramString)
    {
        getEditor(paramContext).remove(paramString).commit();
    }

    public static void writeBoolean(Context paramContext, String paramString, boolean paramBoolean)
    {
        getEditor(paramContext).putBoolean(paramString, paramBoolean).commit();
    }

    public static void writeFloat(Context paramContext, String paramString, float paramFloat)
    {
        getEditor(paramContext).putFloat(paramString, paramFloat).commit();
    }

    public static void writeInteger(Context paramContext, String paramString, int paramInt)
    {
        getEditor(paramContext).putInt(paramString, paramInt).commit();
    }

    public static void writeLong(Context paramContext, String paramString, long paramLong)
    {
        getEditor(paramContext).putLong(paramString, paramLong).commit();
    }

    public static void writeString(Context paramContext, String paramString1, String paramString2)
    {
        getEditor(paramContext).putString(paramString1, paramString2).commit();
    }


}


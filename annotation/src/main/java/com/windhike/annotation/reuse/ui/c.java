package com.windhike.annotation.reuse.ui;

import android.app.AppOpsManager;
import android.content.Context;
import android.os.Binder;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Environment;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class c {
    private static final String[] MEIZUBOARD = new String[]{"m9", "M9", "mx", "MX"};
    private static boolean aHD = false;
    private static boolean aHE = false;
    private static String aHF;
    private static String sMiuiInternalStorage;
    private static String sMiuiVersion;
    private static String sMiuiVersionCode;

    static {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(new File(Environment.getRootDirectory(), "build.prop"));
            Properties properties = new Properties();
            properties.load(fileInputStream);
            sMiuiVersionCode = properties.getProperty("ro.miui.ui.version.code", null);
            sMiuiVersion = properties.getProperty("ro.miui.ui.version.name", null);
            sMiuiInternalStorage = properties.getProperty("ro.miui.internal.storage", null);
            aHF = properties.getProperty("ro.build.display.id", null);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                MethodUtil.close(fileInputStream);
            }
        }
    }

    public static boolean isTablet(Context context) {
        if (aHD) {
            return aHE;
        }
        boolean z;
        if ((context.getResources().getConfiguration().screenLayout & 15) >= 3) {
            z = true;
        } else {
            z = false;
        }
        aHE = z;
        aHD = true;
        return aHE;
    }

    public static boolean isMIUI() {
        return (MethodUtil.isNullOrEmpty(sMiuiVersionCode) && MethodUtil.isNullOrEmpty(sMiuiVersion) && MethodUtil.isNullOrEmpty(sMiuiInternalStorage)) ? false : true;
    }

    public static boolean xL() {
        boolean z;
        if (!(aHF == null || aHF.equals(""))) {
            Matcher matcher = Pattern.compile("(\\d+\\.){2}\\d").matcher(aHF);
            if (matcher.find()) {
                String group = matcher.group();
                if (!(group == null || group.equals(""))) {
                    String[] split = group.split("\\.");
                    if (split.length == 3) {
                        int i = 0;
                        if (Integer.valueOf(split[0]).intValue() < 5) {
                            i = 0;
                        } else if (Integer.valueOf(split[0]).intValue() > 5) {
                            z = true;
                        } else if (Integer.valueOf(split[1]).intValue() < 2) {
                            i = 0;
                        } else if (Integer.valueOf(split[1]).intValue() > 2) {
                            z = true;
                        } else if (Integer.valueOf(split[2]).intValue() < 4) {
                            i = 0;
                        } else if (Integer.valueOf(split[2]).intValue() >= 5) {
                            z = true;
                        }
                        if (isMeizu() || i == 0) {
                            return false;
                        }
                        return true;
                    }
                }
            }
        }
        z = true;
        if (isMeizu()) {
        }
        return false;
    }

    public static boolean isMeizu() {
        boolean z;
        String[] strArr = MEIZUBOARD;
        String str = Build.BOARD;
        if (str != null) {
            for (Object equals : strArr) {
                if (str.equals(equals)) {
                    z = true;
                    break;
                }
            }
        }
        z = false;
        if (!z) {
            if (!Pattern.compile("flyme").matcher(Build.DISPLAY.toLowerCase()).find()) {
                return false;
            }
        }
        return true;
    }

    public static boolean isZUKZ1() {
        String str = Build.MODEL;
        if (str == null) {
            return false;
        }
        return str.toLowerCase().contains("zuk z1");
    }

    public static boolean xM() {
        String str = Build.MODEL;
        if (str == null) {
            return false;
        }
        return str.toLowerCase().contains("zte c2016");
    }

    public static boolean xN() {
        return isMIUI() && sMiuiVersion != null && sMiuiVersion.equalsIgnoreCase("V8");
    }

    public static boolean aK(Context context) {
        if (VERSION.SDK_INT >= 19) {
            return o(context, 24);
        }
        try {
            if ((context.getApplicationInfo().flags & 134217728) == 134217728) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean o(Context context, int i) {
        if (VERSION.SDK_INT >= 19) {
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            try {
                return ((Integer) appOpsManager.getClass().getDeclaredMethod("checkOp", new Class[]{Integer.TYPE, Integer.TYPE, String.class}).invoke(appOpsManager, new Object[]{Integer.valueOf(24), Integer.valueOf(Binder.getCallingUid()), context.getPackageName()})).intValue() == 0;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}

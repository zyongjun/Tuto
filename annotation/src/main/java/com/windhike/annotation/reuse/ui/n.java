package com.windhike.annotation.reuse.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build.VERSION;
import android.support.annotation.ColorInt;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import com.windhike.annotation.reuse.DisplayUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;

public final class n {

    private static final float OVER_SCROLL_FRICTION = 0.5f;
    private static int aHQ = -1;
    private static int aHR = 0;
    public static float sVirtualDensity = -1.0f;
    public static float sVirtualDensityDpi = -1.0f;

    public static void i(Activity activity) {
        a(activity, 1073741824);
    }

    @TargetApi(19)
    public static void a(Activity activity, @ColorInt int i) {
        if (VERSION.SDK_INT >= 19) {
            if (c.isMeizu() || c.isMIUI()) {
//                WindowManager.LayoutParams.FLAG_FULLSCREEN
                activity.getWindow().setFlags(FLAG_TRANSLUCENT_STATUS, FLAG_TRANSLUCENT_STATUS);
            } else if (VERSION.SDK_INT >= 21) {
                Window window = activity.getWindow();
                window.getDecorView().setSystemUiVisibility(1280);
                if (VERSION.SDK_INT >= 23) {
                    int i2;
                    if (c.isZUKZ1() || c.xM()) {
                        i2 = 0;
                    } else {
                        i2 = 1;
                    }
                    if (i2 != 0) {
                        window.clearFlags(FLAG_TRANSLUCENT_STATUS);
                        window.addFlags(Integer.MIN_VALUE);
                        window.setStatusBarColor(0);
                        return;
                    }
                }
                window.clearFlags(FLAG_TRANSLUCENT_STATUS);
                window.addFlags(Integer.MIN_VALUE);
                window.setStatusBarColor(i);
            }
        }
    }

    public static void j(Activity activity) {
        if (!c.xM()) {
            Window window;
            if (aHR != 0) {
                int i = aHR;
                if (i == 1) {
                    a(activity.getWindow(), true);
                } else if (i == 2) {
                    b(activity.getWindow(), true);
                } else if (i == 3) {
                    window = activity.getWindow();
                    window.getDecorView().setSystemUiVisibility(a(window, 8192));
                }
            } else if (VERSION.SDK_INT < 19) {
            } else {
                if (a(activity.getWindow(), true)) {
                    aHR = 1;
                } else if (b(activity.getWindow(), true)) {
                    aHR = 2;
                } else if (VERSION.SDK_INT >= 23) {
                    window = activity.getWindow();
                    window.getDecorView().setSystemUiVisibility(a(window, 8192));
                    aHR = 3;
                }
            }
        }
    }

    public static void k(Activity activity) {
        if (aHR != 0) {
            if (aHR == 1) {
                a(activity.getWindow(), false);
            } else if (aHR == 2) {
                b(activity.getWindow(), false);
            } else if (aHR == 3) {
                Window window = activity.getWindow();
                window.getDecorView().setSystemUiVisibility(a(window, 256));
            }
        }
    }

    private static int a(Window window, int i) {
        return a(window, a(window, a(window, a(window, a(window, a(window, i, 1024), 4), 2), 4096), 1024), 512);
    }

    public static int a(Window window, int i, int i2) {
        if ((window.getDecorView().getSystemUiVisibility() & i2) == i2) {
            return i | i2;
        }
        return i;
    }

    private static boolean a(Window window, boolean z) {
        if (window != null) {
            Class cls = window.getClass();
            try {
                Class cls2 = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                int i = cls2.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE").getInt(cls2);
                Method method = cls.getMethod("setExtraFlags", new Class[]{Integer.TYPE, Integer.TYPE});
                if (z) {
                    method.invoke(window, new Object[]{Integer.valueOf(i), Integer.valueOf(i)});
                    return true;
                }
                method.invoke(window, new Object[]{Integer.valueOf(0), Integer.valueOf(i)});
                return true;
            } catch (Exception e) {
            }
        }
        return false;
    }

    private static boolean b(Window window, boolean z) {
        if (window != null) {
            try {
                LayoutParams attributes = window.getAttributes();
                Field declaredField = LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field declaredField2 = LayoutParams.class.getDeclaredField("meizuFlags");
                declaredField.setAccessible(true);
                declaredField2.setAccessible(true);
                int i = declaredField.getInt(null);
                int i2 = declaredField2.getInt(attributes);
                if (z) {
                    i |= i2;
                } else {
                    i = (i ^ -1) & i2;
                }
                declaredField2.setInt(attributes, i);
                window.setAttributes(attributes);
                return true;
            } catch (Exception e) {
            }
        }
        return false;
    }

    public static int aN(Context context) {
//        Object newInstance;
//        Throwable th;
//        Field field = null;
//        if (aHQ == -1) {
//            Field field2;
//            try {
//                Class cls = Class.forName("com.android.internal.R$dimen");
//                newInstance = cls.newInstance();
//                try {
//                    if (c.isMeizu()) {
//                        field2 = cls.getField("status_bar_height_large");
//                        if (field2 == null) {
//                            try {
//                                field2 = cls.getField("status_bar_height");
//                            } catch (Throwable th2) {
//                                Throwable th3 = th2;
//                                field = field2;
//                                th = th3;
//                                th.printStackTrace();
//                                field2 = field;
//                                aHQ = context.getResources().getDimensionPixelSize(Integer.parseInt(field2.get(newInstance).toString()));
//                                if (!c.isTablet(context)) {
//                                }
//                                if (sVirtualDensity == -1.0f) {
//                                    aHQ = DisplayUtil.dp2px(context, 25);
//                                } else {
//                                    aHQ = (int) ((25.0f * sVirtualDensity) + OVER_SCROLL_FRICTION);
//                                }
//                                return aHQ;
//                            }
//                        }
//                        if (!(field2 == null || newInstance == null)) {
//                            aHQ = context.getResources().getDimensionPixelSize(Integer.parseInt(field2.get(newInstance).toString()));
//                        }
//                        if (!c.isTablet(context) && aHQ > DisplayUtil.dp2px(context, 25)) {
//                            aHQ = 0;
//                        } else if (aHQ <= 0 || aHQ > DisplayUtil.dp2px(context, 50)) {
//                            if (sVirtualDensity == -1.0f) {
//                                aHQ = DisplayUtil.dp2px(context, 25);
//                            } else {
//                                aHQ = (int) ((25.0f * sVirtualDensity) + OVER_SCROLL_FRICTION);
//                            }
//                        }
//                    }
//                } catch (Throwable th4) {
//                    th = th4;
//                    th.printStackTrace();
//                    field2 = field;
//                    aHQ = context.getResources().getDimensionPixelSize(Integer.parseInt(field2.get(newInstance).toString()));
//                    if (!c.isTablet(context)) {
//                    }
//                    if (sVirtualDensity == -1.0f) {
//                        aHQ = (int) ((25.0f * sVirtualDensity) + OVER_SCROLL_FRICTION);
//                    } else {
//                        aHQ = DisplayUtil.dp2px(context, 25);
//                    }
//                    return aHQ;
//                }
//                field2 = field;
//                if (field2 == null) {
//                    field2 = cls.getField("status_bar_height");
//                }
//            } catch (Throwable th5) {
//                th = th5;
//                newInstance = field;
//                th.printStackTrace();
//                field2 = field;
//                aHQ = context.getResources().getDimensionPixelSize(Integer.parseInt(field2.get(newInstance).toString()));
//                if (!c.isTablet(context)) {
//                }
//                if (sVirtualDensity == -1.0f) {
//                    aHQ = DisplayUtil.dp2px(context, 25);
//                } else {
//                    aHQ = (int) ((25.0f * sVirtualDensity) + OVER_SCROLL_FRICTION);
//                }
//                return aHQ;
//            }
//            try {
//                aHQ = context.getResources().getDimensionPixelSize(Integer.parseInt(field2.get(newInstance).toString()));
//            } catch (Throwable th6) {
//                th6.printStackTrace();
//            }
//            if (!c.isTablet(context)) {
//            }
//            if (sVirtualDensity == -1.0f) {
//                aHQ = (int) ((25.0f * sVirtualDensity) + OVER_SCROLL_FRICTION);
//            } else {
//                aHQ = DisplayUtil.dp2px(context, 25);
//            }
//        }
        return aHQ;
    }
}

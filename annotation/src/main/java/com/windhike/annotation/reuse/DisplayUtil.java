package com.windhike.annotation.reuse;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

public class DisplayUtil {

	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static float px2pxBaseOnDensity(Context context, float pxValue, float density) {
		float dpValue = pxValue / density + 0.5f;
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue,
				context.getResources().getDisplayMetrics());
	}

	public static int px2pxWithOtherDensity(Context context, float pxValue, float density) {
		return dip2px(context,pxValue / density + 0.5f);
	}

	public static final float DENSITY = Resources.getSystem().getDisplayMetrics().density;
	private static Boolean aHL = null;
	public static float sDensity = 0.0f;

	public static int dip2px(Context context, float dipValue) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue,
				context.getResources().getDisplayMetrics());
	}
	public static DisplayMetrics getDisplayMetrics(Context context) {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		((WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
		return displayMetrics;
	}

	public static int dpToPx(int i) {
		return (int) ((((float) i) * DENSITY) + 0.5f);
	}

	public static float getDensity(Context context) {
		if (sDensity == 0.0f) {
			sDensity = getDisplayMetrics(context).density;
		}
		return sDensity;
	}

	public static int getScreenWidth(Context context) {
		return getDisplayMetrics(context).widthPixels;
	}

	public static int getScreenHeight(Context context) {
		return getDisplayMetrics(context).heightPixels;
	}

	private static int[] getRealScreenSize(Context context) {
		int intValue;
		int[] iArr = new int[2];
		Display defaultDisplay = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		defaultDisplay.getMetrics(displayMetrics);
		int i = displayMetrics.widthPixels;
		int i2 = displayMetrics.heightPixels;
		try {
			i = ((Integer) Display.class.getMethod("getRawWidth", new Class[0]).invoke(defaultDisplay, new Object[0])).intValue();
			intValue = ((Integer) Display.class.getMethod("getRawHeight", new Class[0]).invoke(defaultDisplay, new Object[0])).intValue();
		} catch (Exception e) {
			i = i;
			intValue = i2;
		}
		try {
			Point point = new Point();
			Display.class.getMethod("getRealSize", new Class[]{Point.class}).invoke(defaultDisplay, new Object[]{point});
			i = point.x;
			intValue = point.y;
		} catch (Exception e2) {
		}
		iArr[0] = i;
		iArr[1] = intValue;
		return iArr;
	}

	public static int dp2px(Context context, int i) {
		return (int) (((double) (getDensity(context) * ((float) i))) + 0.5d);
	}

	public static int p(Context context, int i) {
		return (int) (((double) (((float) i) / getDensity(context))) + 0.5d);
	}

	public static int aL(Context context) {
		try {
			Class cls = Class.forName("com.android.internal.R$dimen");
			return context.getResources().getDimensionPixelSize(Integer.parseInt(cls.getField("status_bar_height").get(cls.newInstance()).toString()));
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static int aM(Context context) {
		return getRealScreenSize(context)[1] - getScreenHeight(context);
	}
}


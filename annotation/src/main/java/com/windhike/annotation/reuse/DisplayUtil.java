package com.windhike.annotation.reuse;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class DisplayUtil {

	public static int dip2px(Context context, float dipValue) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue,
				context.getResources().getDisplayMetrics());
	}

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

}
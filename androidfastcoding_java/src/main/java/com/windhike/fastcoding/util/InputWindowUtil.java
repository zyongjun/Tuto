package com.windhike.fastcoding.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * 
 * @Description:键盘显示工具类
 * @author bob
 * @created 2015-10-9 上午9:16:36
 * @version 1
 */
public class InputWindowUtil {

	/**
	 * 如果输入法在窗口上已经显示，则隐藏，反之则显示
	 * @param context
	 */
	public static void changeInputWindow(Context context){
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	/**
	 * 强制显示键盘,其中view为聚焦的view组件
	 * @param context
	 * @param view
	 */
	public static void forceShowInputWindow(Context context, View view){
		InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
	}
	
	/**
	 * 强制隐藏键盘
	 * @param context
	 * @param view
	 */
	public static void forceHideInputWindow(Context context, View view){
		//先关闭键盘，在销毁activity
		InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);  
		try {
			Thread.sleep(150);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void setInputMethodHide(Activity activity) {
		InputMethodManager imm = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null && activity != null
				&& activity.getCurrentFocus() != null) {
			imm.hideSoftInputFromWindow(activity.getCurrentFocus()
					.getWindowToken(), 0);
		}
	}
	
	/**
	 * 获取输入法打开的状态
	 * @param context
	 * @return true，则表示输入法打开
	 */
	public static boolean getInputWindowStatus(Context context){
		InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		return imm.isActive();
	}
}

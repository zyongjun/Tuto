package com.zyongjun.easytouch.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import com.zyongjun.easytouch.R;
import com.zyongjun.easytouch.service.DrawMenuService.HolderSwitchCallback;

/**
 * author:gzzyj on 2017/8/7 0007.
 * email:zhyongjun@windhike.cn
 */
public class MenuHolder extends BaseTouchViewHolder{
    private Context mContext;
    private FloatSettingView mFloatSettingView;
    private HolderSwitchCallback mCallback;
    public MenuHolder(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public void initView() {
        if (mFloatSettingView == null) {
            mFloatSettingView = new FloatSettingView(mContext);
        }
        mFloatSettingView.refreshUI();
        mWindowManager.addView(mFloatSettingView,getViewLayoutParams());
    }

    public void setMenuCallback(HolderSwitchCallback callback) {
        mCallback = callback;
        mFloatSettingView.setSettingCallback(mCallback);
    }

    @Override
    public WindowManager.LayoutParams getViewLayoutParams() {
        WindowManager.LayoutParams windowLayoutParams = new WindowManager.LayoutParams();
        windowLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        windowLayoutParams.format = PixelFormat.RGBA_8888;
        windowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        windowLayoutParams.windowAnimations= R.style.IconViewAnimator;
        DisplayMetrics metrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(metrics);
        windowLayoutParams.alpha = 0.6f;
        windowLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        windowLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        windowLayoutParams.gravity = Gravity.CENTER_VERTICAL;
        return windowLayoutParams;
    }

    @Override
    public View getRootView() {
        return mFloatSettingView;
    }

    @Override
    public void onDestory() {
        if(mFloatSettingView!=null&& ViewCompat.isAttachedToWindow(mFloatSettingView)) {
            mWindowManager.removeView(mFloatSettingView);
        }
    }
}

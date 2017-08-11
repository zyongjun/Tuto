package com.zyongjun.easytouch.view;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;

/**
 * author:gzzyj on 2017/8/7 0007.
 * email:zhyongjun@windhike.cn
 */

public class MenuHolder extends BaseTouchViewHolder{
    private Context mContext;
    private FloatSettingView mFloatSettingView;
    public MenuHolder(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public void initView() {
        mFloatSettingView = new FloatSettingView(mContext);
        mWindowManager.addView(mFloatSettingView,getViewLayoutParams());
    }

    @Override
    public WindowManager.LayoutParams getViewLayoutParams() {
        return null;
    }

    @Override
    public View getRootView() {
        return mFloatSettingView;
    }

    @Override
    public void onDestory() {
        mWindowManager.removeView(mFloatSettingView);
    }
}

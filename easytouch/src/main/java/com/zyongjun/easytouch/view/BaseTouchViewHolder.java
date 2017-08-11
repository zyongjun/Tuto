package com.zyongjun.easytouch.view;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;

/**
 * author:gzzyj on 2017/8/7 0007.
 * email:zhyongjun@windhike.cn
 */

public abstract class BaseTouchViewHolder {
    protected   Context mContext;
    protected  WindowManager mWindowManager;
    public BaseTouchViewHolder(Context context) {
        mContext = context.getApplicationContext();
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
    }

    public abstract void initView();

    public abstract WindowManager.LayoutParams getViewLayoutParams();

    public abstract View getRootView();

    public abstract void onDestory();
}

package com.zyongjun.easytouch.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.View;
import com.zyongjun.easytouch.view.IconHolder;
import com.zyongjun.easytouch.view.MenuHolder;

/**
 * author:gzzyj on 2017/8/7 0007.
 * email:zhyongjun@windhike.cn
 */

public class DrawMenuService extends Service{
    private IconHolder mIconHolder;
    private MenuHolder mMenuHolder;

    @Override
    public void onCreate() {
        super.onCreate();
//        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mIconHolder = new IconHolder(this);
        mMenuHolder = new MenuHolder(this);
        mIconHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIconHolder.onDestory();
                mMenuHolder.initView();
            }
        });
        mIconHolder.initView();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIconHolder.onDestory();
        mMenuHolder.onDestory();
    }
}

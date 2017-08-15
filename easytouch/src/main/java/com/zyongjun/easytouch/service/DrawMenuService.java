package com.zyongjun.easytouch.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;
import com.zyongjun.easytouch.view.FloatSettingView;
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
        mIconHolder = new IconHolder(this);
        mMenuHolder = new MenuHolder(this);
        mIconHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIconHolder.onDestory();
                mMenuHolder.initView();
                mMenuHolder.setMenuCallback(mDrawingSettingCallback);
            }
        });
        mIconHolder.initView();
    }

    private FloatSettingView.OnDrawingSettingCallback mDrawingSettingCallback = new FloatSettingView.OnDrawingSettingCallback() {
        @Override
        public void onShapeChanged(int drawingShape) {
            Toast.makeText(getBaseContext(),"onShapeChanged",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onColorChanged(int index) {
            Toast.makeText(getBaseContext(),"onColorChanged",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void switchZoomMode(boolean isSwitchToZoomMode) {
            Toast.makeText(getBaseContext(),"switchZoomMode",Toast.LENGTH_SHORT).show();
        }

        @Override
        public View.OnClickListener obtainMenuOutsideListener() {
            return new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMenuHolder.onDestory();
                    mIconHolder.initView();
                    Toast.makeText(getBaseContext(),"obtainMenuOutsideListener",Toast.LENGTH_SHORT).show();
                }
            };
        }

        @Override
        public void switchToIconMode() {
            mMenuHolder.onDestory();
            mIconHolder.initView();
        }
    };

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

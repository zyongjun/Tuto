package com.zyongjun.easytouch.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.View;
import com.zyongjun.easytouch.view.ColorHolder;
import com.zyongjun.easytouch.view.IconHolder;
import com.zyongjun.easytouch.view.MenuHolder;

/**
 * author:gzzyj on 2017/8/7 0007.
 * email:zhyongjun@windhike.cn
 */
public class DrawMenuService extends Service{
    private IconHolder mIconHolder;
    private MenuHolder mMenuHolder;
    private ColorHolder mColorHolder;

    @Override
    public void onCreate() {
        super.onCreate();
        mIconHolder = new IconHolder(this);
        mMenuHolder = new MenuHolder(this);
        mColorHolder = new ColorHolder(this);
        mIconHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIconHolder.onDestory();
                mMenuHolder.initView();
                mMenuHolder.setMenuCallback(mMenuSwitchHolderCallback);
            }
        });
        mIconHolder.initView();
    }

    private HolderSwitchCallback mMenuSwitchHolderCallback = new HolderSwitchCallback(){

        @Override
        public View.OnClickListener obtainMenuColorPickerListener() {
            return new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMenuHolder.onDestory();
                    mColorHolder.initView();
                    mColorHolder.setMenuCallback(mMenuSwitchHolderCallback);
                }
            };
        }

        @Override
        public View.OnClickListener obtainMenuOutsideListener() {
            return new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMenuHolder.onDestory();
                    mColorHolder.onDestory();
                    mIconHolder.initView();
                }
            };
        }

        @Override
        public void switchToIconMode() {
            mColorHolder.onDestory();
            mMenuHolder.onDestory();
            mIconHolder.initView();
        }
    };


    public interface HolderSwitchCallback{
        View.OnClickListener obtainMenuColorPickerListener();
        View.OnClickListener obtainMenuOutsideListener();
        void switchToIconMode();
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
        mColorHolder.onDestory();
    }
}

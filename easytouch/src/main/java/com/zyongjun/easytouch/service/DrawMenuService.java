package com.zyongjun.easytouch.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;

import com.umeng.analytics.MobclickAgent;
import com.windhike.fastcoding.BaseApplication;
import com.windhike.fastcoding.CommonFragmentActivity;
import com.zyongjun.easytouch.screenshot.NewScreenShotUtilImpl;
import com.zyongjun.easytouch.screenshot.OldScreenShotUtilImpl;
import com.zyongjun.easytouch.screenshot.ScreenShotUtil;
import com.zyongjun.easytouch.view.ColorHolder;
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
    private ColorHolder mColorHolder;
    private LocalBroadcastManager mLocalManager;
    private ScreenShotUtil mScreenShot;

    @Override
    public void onCreate() {
        super.onCreate();
        mLocalManager = LocalBroadcastManager.getInstance(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(FloatSettingView.ACTION_CAPTURE_FINISHED);
        mLocalManager.registerReceiver(mReceiver, filter);
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
                    MobclickAgent.onEvent(DrawMenuService.this,"menu_color");
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
                    switchToIconMode();
                }
            };
        }

        @Override
        public void switchToIconMode() {
            mColorHolder.onDestory();
            mMenuHolder.onDestory();
            mIconHolder.initView();
        }

        @Override
        public void onCaptureRequest() {
            mIconHolder.onDestory();
            mColorHolder.onDestory();
            mMenuHolder.onDestory();
            Context context = DrawMenuService.this;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP&&(mScreenShot==null||NewScreenShotUtilImpl.data==null)){
                Bundle bundle = new Bundle();
                bundle.putBoolean(CommonFragmentActivity.BUNDLE_KEY_TRANSLUCENT,true);
                bundle.putBoolean(CommonFragmentActivity.BUNDLE_KEY_FULLSCREEN,false);
                if(!((BaseApplication)getApplication()).isForground) {
                    CommonFragmentActivity.start(context, "com.windhike.tuto.fragment.TranslucentFragment",
                            bundle, Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                }else{
                    CommonFragmentActivity.start(context, "com.windhike.tuto.fragment.TranslucentFragment",
                            bundle);
                }
            }else{
                if(mScreenShot == null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mScreenShot = new NewScreenShotUtilImpl(context);
                    } else {
                        mScreenShot = OldScreenShotUtilImpl.getInstance(context);
                    }
                }
                mScreenShot.startScreenshot();
            }
        }
    };
    private static final String TAG = "DrawMenuService";
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            final String action = intent.getAction();
            if (FloatSettingView.ACTION_CAPTURE_FINISHED.equals(action)) {
                mMenuSwitchHolderCallback.switchToIconMode();
            }
        }
    };

    public interface HolderSwitchCallback{
        View.OnClickListener obtainMenuColorPickerListener();
        View.OnClickListener obtainMenuOutsideListener();
        void switchToIconMode();
        void onCaptureRequest();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocalManager.unregisterReceiver(mReceiver);
        mIconHolder.onDestory();
        mMenuHolder.onDestory();
        mColorHolder.onDestory();
    }
}

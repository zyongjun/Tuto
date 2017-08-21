package com.zyongjun.easytouch.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
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
//    private LocalBroadcastManager mBroadcastmanager;

    @Override
    public void onCreate() {
        super.onCreate();
//        mBroadcastmanager = LocalBroadcastManager.getInstance(this);
        mIconHolder = new IconHolder(this);
        mMenuHolder = new MenuHolder(this);
        mIconHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIconHolder.onDestory();
                mMenuHolder.initView();
                mMenuHolder.setMenuCallback(mSwitchHolderCallback);
            }
        });
        mIconHolder.initView();
    }

    private HolderSwitchCallback mSwitchHolderCallback = new HolderSwitchCallback(){
//        @Override
//        public void onShapeChanged(int drawingShape) {
//            Intent intent = new Intent(FloatSettingView.ACTION_SHAPE_CHANGED);
//            intent.putExtra(FloatSettingView.ACTION_SHAPE_CHANGED,drawingShape);
//            mBroadcastmanager.sendBroadcast(intent);
//            Toast.makeText(getBaseContext(),"onShapeChanged",Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onColorChanged(int index) {
//            Intent intent = new Intent(FloatSettingView.ACTION_COLOR_CHANGED);
//            intent.putExtra(FloatSettingView.ACTION_COLOR_CHANGED,index);
//            mBroadcastmanager.sendBroadcast(intent);
//            Toast.makeText(getBaseContext(),"onColorChanged",Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void switchZoomMode(boolean isSwitchToZoomMode) {
//            Intent intent = new Intent(FloatSettingView.ACTION_SCALE_MODE_CHANGED);
//            intent.putExtra(FloatSettingView.ACTION_SCALE_MODE_CHANGED,isSwitchToZoomMode);
//            mBroadcastmanager.sendBroadcast(intent);
//            Toast.makeText(getBaseContext(),"switchZoomMode",Toast.LENGTH_SHORT).show();
//        }

        @Override
        public View.OnClickListener obtainMenuOutsideListener() {
            return new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMenuHolder.onDestory();
                    mIconHolder.initView();
                }
            };
        }

        @Override
        public void switchToIconMode() {
            mMenuHolder.onDestory();
            mIconHolder.initView();
        }
    };

    public interface HolderSwitchCallback{
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
    }
}

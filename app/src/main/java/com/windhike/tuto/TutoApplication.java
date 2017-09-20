package com.windhike.tuto;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import com.windhike.annotation.configsapp.AnnotationInitialize;
import com.windhike.annotation.model.PreferenceConnector;
import com.windhike.fastcoding.BaseApplication;
import com.zyongjun.easytouch.view.FloatSettingView;

/**
 * author: zyongjun on 2017/6/30 0030.
 * email: zhyongjun@windhike.cn
 */
public class TutoApplication extends BaseApplication{
    private static TutoApplication INSTANCE;
    private LocalBroadcastManager mLocalBroadcastManager;
    public static TutoApplication getInstance(){
        return INSTANCE;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        PreferenceConnector.writeBoolean(this, FloatSettingView.KEY_DRAWING_NOW,false);
        AnnotationInitialize.getInstance().initialize(this);
        FlavorConfig.getInstance().init(this);

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(FloatSettingView.ACTION_LAUCH);
        mLocalBroadcastManager.registerReceiver(mReceiver,filter);
    }


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (FloatSettingView.ACTION_LAUCH.equals(action)) {
                Intent intentStart = new Intent(context, WelcomeActivity.class);
                intentStart.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentStart);
            }
        }
    };

    @Override
    public void onTerminate() {
        mLocalBroadcastManager.unregisterReceiver(mReceiver);
        PreferenceConnector.writeBoolean(this, FloatSettingView.KEY_DRAWING_NOW,false);
        super.onTerminate();
    }
}

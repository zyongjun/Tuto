package com.windhike.tuto;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.WindowManager;
//import com.facebook.stetho.Stetho;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
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

    public void configScreenSize(WindowManager windowManager) {
        AnnotationInitialize.getInstance().configScreenMetrics(windowManager);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        PreferenceConnector.writeBoolean(this, FloatSettingView.KEY_DRAWING_NOW,false);
//        Stetho.initializeWithDefaults(this);
        AnnotationInitialize.getInstance().initialize(this);
        configUmeng();
        registerWX(this);

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(FloatSettingView.ACTION_LAUCH);
        mLocalBroadcastManager.registerReceiver(mReceiver,filter);
    }
    private void registerWX(Application context) {
        final IWXAPI api = WXAPIFactory.createWXAPI(context, null);
        api.registerApp(getString(R.string.wx_appid));
    }

    private void configUmeng() {
        MobclickAgent.setDebugMode( true );
        String channel =  "test";;
        MobclickAgent.UMAnalyticsConfig config = new MobclickAgent.UMAnalyticsConfig(this,
                "5966e53caed1793fed000288",channel,
                 MobclickAgent.EScenarioType.E_UM_ANALYTICS_OEM,true);

        MobclickAgent. startWithConfigure(config);
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

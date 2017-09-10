package com.windhike.tuto;

import android.app.Application;
import android.view.WindowManager;
//import com.facebook.stetho.Stetho;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import com.windhike.annotation.configsapp.AnnotationInitialize;
import com.windhike.annotation.model.PreferenceConnector;
import com.zyongjun.easytouch.view.FloatSettingView;

/**
 * author: zyongjun on 2017/6/30 0030.
 * email: zhyongjun@windhike.cn
 */

public class TutoApplication extends Application{
    private static TutoApplication INSTANCE;
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
//        Stetho.initializeWithDefaults(this);
        AnnotationInitialize.getInstance().initialize(this);
        configUmeng();
        registerWX(this);
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

    @Override
    public void onTerminate() {
        super.onTerminate();
        PreferenceConnector.writeBoolean(this, FloatSettingView.KEY_DRAWING_NOW,false);
    }
}

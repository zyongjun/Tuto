package com.windhike.tuto;

import android.app.Application;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import com.facebook.stetho.Stetho;
import com.umeng.analytics.MobclickAgent;
import com.windhike.annotation.configsapp.AnnotationInitialize;

/**
 * author: zyongjun on 2017/6/30 0030.
 * email: zhyongjun@windhike.cn
 */

public class TutoApplication extends Application{
    private static TutoApplication INSTANCE;
    private DisplayMetrics mDisplayMetrics;

    public static TutoApplication getInstance(){
        return INSTANCE;
    }

    public void configScreenSize(WindowManager windowManager) {
        AnnotationInitialize.getInstance().configScreenMetrics(windowManager);
        mDisplayMetrics = AnnotationInitialize.getInstance().getDisplayMetrics();
    }

    public DisplayMetrics getDisplayMetrics() {
        return mDisplayMetrics;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        Stetho.initializeWithDefaults(this);
        AnnotationInitialize.getInstance().initialize(this);
//        configUmeng();
    }

    private static final String TAG = "TutoApplication";
    private void configUmeng() {
        MobclickAgent.setDebugMode( true );
        String channel =  "test";;
        MobclickAgent.UMAnalyticsConfig config = new MobclickAgent.UMAnalyticsConfig(this,
                "5966e53caed1793fed000288",channel,
                 MobclickAgent.EScenarioType.E_UM_ANALYTICS_OEM,true);

        MobclickAgent. startWithConfigure(config);
    }

}

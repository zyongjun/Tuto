package com.windhike.tuto;

import android.app.Application;
import android.util.DisplayMetrics;
import android.view.WindowManager;
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
        AnnotationInitialize.getInstance().initialize(this);
    }
}

package com.windhike.annotation.configsapp;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * author: zyongjun on 2017/6/30 0030.
 * email: zhyongjun@windhike.cn
 */

public class AnnotationInitialize {
    private Context context;
    private DisplayMetrics mDisplayMetrics;

    public static AnnotationInitialize getInstance(){
        return Holder.INSTANCE;
    }

    public void initialize(Context context) {
        this.context = context.getApplicationContext();
    }

    public void configScreenMetrics(WindowManager manager) {
        mDisplayMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(mDisplayMetrics);
    }

    public DisplayMetrics getDisplayMetrics(){
        return mDisplayMetrics;
    }

    public Context getContext() {
        return this.context;
    }

    private static class Holder{
        static final AnnotationInitialize INSTANCE = new AnnotationInitialize();
    }
}

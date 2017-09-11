package com.zyongjun.easytouch.screenshot;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.windhike.fastcoding.BaseApplication;
import com.windhike.fastcoding.CommonFragmentActivity;
import com.windhike.fastcoding.FadeFragmentActivity;
import com.zyongjun.easytouch.view.FloatSettingView;
import rx.Observer;

/**
 * Created by Aria on 2017/7/24.
 */

public abstract class ScreenShotUtil {
    protected Context context;

    public ScreenShotUtil(Context context) {
        this.context = context;
         mManager = LocalBroadcastManager.getInstance(context);
    }

    public abstract void startScreenshot();
    public abstract boolean isSupportScreenshot();
    public abstract void setHandler(Handler handler);
    private LocalBroadcastManager mManager;

    private static final String TAG = "ScreenShotUtil";
    public Observer<String> getShotObserver() {
        return new Observer<String>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                mManager.sendBroadcast(new Intent(FloatSettingView.ACTION_CAPTURE_FINISHED));
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
//                Toast.makeText(context, context.getString(R.string.msg_screenshot_fail), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(String path) {
                if("".equals(path)){
                    startScreenshot();
                }else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        NewScreenShotUtilImpl.setData(null);
                    }
                    mManager.sendBroadcast(new Intent(FloatSettingView.ACTION_CAPTURE_FINISHED));
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(CommonFragmentActivity.BUNDLE_KEY_TRANSLUCENT,true);
                    bundle.putBoolean(CommonFragmentActivity.BUNDLE_KEY_FULLSCREEN,true);
                    bundle.putString("KEY_ANNOTATION_DRAW_NEW_PATH", path);
//                    if(((BaseApplication)context.getApplicationContext()).isForground()){
                        FadeFragmentActivity.start(context, "com.windhike.tuto.fragment.AnnotationFragment",
                                bundle);
//                    }else {
//                        FadeFragmentActivity.start(context, "com.windhike.tuto.fragment.AnnotationFragment",
//                                bundle, Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    }
                }
            }
        };
    }
}

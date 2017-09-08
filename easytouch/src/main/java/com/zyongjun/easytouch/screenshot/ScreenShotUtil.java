package com.zyongjun.easytouch.screenshot;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import com.windhike.fastcoding.CommonFragmentActivity;
import rx.Observer;

/**
 * Created by Aria on 2017/7/24.
 */

public abstract class ScreenShotUtil {
    protected Context context;

    public ScreenShotUtil(Context context) {
        this.context = context;
    }

    public abstract void startScreenshot();
    public abstract boolean isSupportScreenshot();
    public abstract void setHandler(Handler handler);

    public Observer<String> getShotObserver() {
        return new Observer<String>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
//                Toast.makeText(context, context.getString(R.string.msg_screenshot_fail), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(String path) {
                if("".equals(path)){
                    startScreenshot();
                }else {
                    Bundle bundle = new Bundle();
//                  bundle.putInt(KEY_TYPE, 2);
                    bundle.putString("KEY_ANNOTATION_DRAW_NEW_PATH", path);
                    CommonFragmentActivity.start(context, "com.windhike.tuto.fragment.AnnotationFragment", bundle);
                }
            }
        };
    }
}

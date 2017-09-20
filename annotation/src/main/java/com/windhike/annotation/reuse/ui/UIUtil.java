package com.windhike.annotation.reuse.ui;

import android.os.Build;
import android.view.Window;

/**
 * Created by Administrator on 2017/7/11 0011.
 */

public class UIUtil {


    public static void requestApplyInsets(Window window) {
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            window.getDecorView().requestFitSystemWindows();
        } else if (Build.VERSION.SDK_INT >= 21) {
            window.getDecorView().requestApplyInsets();
        }
    }

}

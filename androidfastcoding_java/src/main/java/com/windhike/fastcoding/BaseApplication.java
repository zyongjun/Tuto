package com.windhike.fastcoding;

import android.app.Application;

/**
 * author:gzzyj on 2017/9/11 0011.
 * email:zhyongjun@windhike.cn
 */

public abstract class BaseApplication extends Application{
    public boolean isForground;

    public boolean isForground() {
        return isForground;
    }

    public void setForground(boolean isForground) {
        this.isForground = isForground;
    }

}

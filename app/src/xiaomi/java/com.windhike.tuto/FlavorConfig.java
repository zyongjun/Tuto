package com.windhike.tuto;

import android.app.Application;
import android.content.Context;

/**
 * author:gzzyj on 2017/9/20 0020.
 * email:zhyongjun@windhike.cn
 */

public class FlavorConfig extends FlavorSetting{

    public static FlavorConfig getInstance() {
        return Holder.INSTANCE;
    }
    private static final class Holder{
        private static final FlavorConfig INSTANCE = new FlavorConfig();
    }

    @Override
    public void init(Application context) {
//        super.init(context);
        configUmeng(context,"xiaomi");
        registerWX(context);
    }
}

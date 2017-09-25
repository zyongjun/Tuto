package com.windhike.tuto;

import android.app.Application;
import android.content.Context;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import com.xiaomi.market.sdk.XiaomiUpdateAgent;

/**
 * author:gzzyj on 2017/9/20 0020.
 * email:zhyongjun@windhike.cn
 */

public abstract class FlavorSetting {

    public void init(Application context) {
        configUmeng(context,"tencent");
        registerWX(context);

    }

    protected void configUmeng(Context context,String channel) {
        MobclickAgent.setDebugMode( true );
        MobclickAgent.UMAnalyticsConfig config = new MobclickAgent.UMAnalyticsConfig(context,
                "5966e53caed1793fed000288",channel,
                MobclickAgent.EScenarioType.E_UM_ANALYTICS_OEM,true);
        MobclickAgent. startWithConfigure(config);
    }

    protected void registerWX(Application context) {
        final IWXAPI api = WXAPIFactory.createWXAPI(context, null);
        api.registerApp(context.getString(R.string.wx_appid));
    }
}

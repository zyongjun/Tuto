package com.windhike.tuto.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import com.windhike.tuto.R;
import com.windhike.tuto.TutoApplication;

/**
 * author:gzzyj on 2017/8/22 0022.
 * email:zhyongjun@windhike.cn
 */

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private IWXAPI api;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, getString(R.string.wx_appid), false);
        api.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        if (baseResp instanceof SendAuth.Resp) {
            handleAuthResp(baseResp);
        } else {
            handleShareResp(baseResp);
        }
    }

    private void handleShareResp(BaseResp baseResp) {
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
//                ShareObject object = ApplicationDelegate.getInstance().getShareObject();
//                logShare(object);
                MobclickAgent.onEvent(this,"share_success");
                showToastAndBack(getString(R.string.toast_share_succ));
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                MobclickAgent.onEvent(this,"share_cancel");
                showToastAndBack(getString(R.string.toast_share_cancel));
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                showToastAndBack(getString(R.string.toast_share_autoerr));
                break;
            default:
                MobclickAgent.onEvent(this,"share_fail");
                showToastAndBack(getString(R.string.toast_share_back));
                break;
        }
    }

    private void showToastAndBack(String message) {
        Toast.makeText(TutoApplication.getInstance(),message,Toast.LENGTH_SHORT).show();
        finish();
    }

    private void handleAuthResp(BaseResp resp) {
        if (resp.errCode != BaseResp.ErrCode.ERR_OK) {
            if (resp.errCode == BaseResp.ErrCode.ERR_USER_CANCEL) {
                showToastAndBack(getString(R.string.label_auth_user_cancel));
            } else {
                showToastAndBack(getString(R.string.label_auth_error));
            }

        } else {
            SendAuth.Resp sendResp = (SendAuth.Resp) resp;
            String code = sendResp.code;
        }
        finish();
    }
}

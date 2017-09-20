package com.windhike.tuto;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.windhike.fastcoding.CommonFragmentActivity;
import com.windhike.tuto.fragment.WelcomeFragment;

/**
 * author:gzzyj on 2017/7/14 0014.
 * email:zhyongjun@windhike.cn
 */

public class WelcomeActivity extends CommonFragmentActivity {

    @Override
    protected void onLayoutCreateBefore(@Nullable Bundle savedInstanceState) {
        super.onLayoutCreateBefore(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TutoApplication.getInstance().configScreenSize(getWindowManager());
        replaceFragment(new WelcomeFragment());
    }

    protected int getLayoutId() {
        return R.layout.activity_fragment_container;
    }

    /** 开屏页一定要禁止用户对返回按钮的控制，否则将可能导致用户手动退出了App而广告无法正常曝光和计费 */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

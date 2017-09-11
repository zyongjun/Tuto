package com.windhike.fastcoding;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import com.umeng.analytics.MobclickAgent;
import com.windhike.fastcoding.base.BaseFragment;


/**
 * author:gzzyj on 2017/7/14 0014.
 * email:zhyongjun@windhike.cn
 */
public class BaseFragmentActivity extends AppCompatActivity{

    protected int getLayoutId() {
        return R.layout.activity_fragment_container;
    }


    protected void replaceFragment(BaseFragment fragment){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.content_container, fragment, fragment.getTransactionTag());
        ft.commitAllowingStateLoss();
        fm.executePendingTransactions();
    }

    protected void replaceFragment(Fragment fragment){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.content_container, fragment);
        ft.commitAllowingStateLoss();
        fm.executePendingTransactions();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((BaseApplication)getApplication()).setForground(true);
        MobclickAgent.onResume(this);       //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        ((BaseApplication)getApplication()).setForground(false);
        MobclickAgent.onPause(this);
    }

}

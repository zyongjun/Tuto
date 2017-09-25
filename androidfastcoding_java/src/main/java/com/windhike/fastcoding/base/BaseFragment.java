package com.windhike.fastcoding.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.umeng.analytics.MobclickAgent;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * author:gzzyj on 2017/7/14 0014.
 * email:zhyongjun@windhike.cn
 */

public abstract class BaseFragment extends Fragment{
    protected View mRootView;
    private Unbinder unbinder;

    public String getTransactionTag() {
        return getClass().getSimpleName();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(getLayouId(),container,false);
            unbinder = ButterKnife.bind(this, mRootView);
            initView();
        }else{
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            parent.removeAllViews();
        }
        return mRootView;
    }

    public void initView() {

    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getSimpleName());
    }

    public abstract int getLayouId();

    public boolean onBackPressed() {
        if (getActivity() != null) {
            getActivity().finish();
            return true;
        }
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

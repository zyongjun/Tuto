package com.windhike.tuto;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * author:gzzyj on 2017/7/14 0014.
 * email:zhyongjun@windhike.cn
 */

public class BaseFragment extends Fragment{
    private View mRootView;

    public String getTransactionTag() {
        return getClass().getSimpleName();
    }

    public View onCustomCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        return null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = onCustomCreateView(inflater,container,savedInstanceState);
        }else{
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            parent.removeAllViews();
        }
        return mRootView;
    }

    public boolean onBackPressed() {
        if (getActivity() != null) {
            getActivity().finish();
            return true;
        }
        return false;
    }
}

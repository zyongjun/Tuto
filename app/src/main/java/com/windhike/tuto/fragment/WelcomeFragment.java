package com.windhike.tuto.fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qq.e.ads.splash.SplashADListener;
import com.windhike.tuto.AnnotationPicturesActivity;
import com.windhike.tuto.BaseFragment;
import com.windhike.tuto.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * author:gzzyj on 2017/7/14 0014.
 * email:zhyongjun@windhike.cn
 */

public class WelcomeFragment extends BasePermissionFragment implements SplashADListener {
    @BindView(R.id.tvLogo)
    TextView tvLogo;
    @BindView(R.id.llLogo)
    LinearLayout llLogo;
    private Handler mHandler = new Handler();

    @Override
    public View onCustomCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_welcome, container, false);
        ButterKnife.bind(this, rootView);
        llLogo.setAlpha(0);
        llLogo.animate().alpha(1).setDuration(3000).start();
//        checkAndRequestPermission(Manifest.permission.CAMERA,Manifest.permission.READ_PHONE_STATE);
        checkAndRequestPermission(Manifest.permission.READ_PHONE_STATE);
//        lauchTask().observeOn(AndroidSchedulers.mainThread())
//                .doOnCompleted(new Action0() {
//                    @Override
//                    public void call() {
//
//                    }
//                }).subscribe(new Action1<Intent>() {
//                    @Override
//                    public void call(Intent intent) {
//
//                    }
//                }, new Action1<Throwable>() {
//                    @Override
//                    public void call(Throwable throwable) {
//
//                    }
//                });
        return rootView;
    }

    @Override
    protected void onGranted() {
        super.onGranted();
        mHandler.postDelayed(mSplashRunnable,3000);
    }

    private Runnable mSplashRunnable = new Runnable() {
        @Override
        public void run() {
            getActivity().startActivity(new Intent(getActivity(), AnnotationPicturesActivity.class));
            getActivity().finish();
        }
    };

    public Observable<Intent> lauchTask() {
        return Observable.create(new Observable.OnSubscribe<Intent>() {
            @Override
            public void call(Subscriber<? super Intent> subscriber) {

            }
        });
    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacks(mSplashRunnable);
        super.onDestroy();
    }

    @Override
    public void onADDismissed() {

    }

    @Override
    public void onNoAD(int i) {

    }

    @Override
    public void onADPresent() {

    }

    @Override
    public void onADClicked() {

    }

    @Override
    public void onADTick(long l) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}

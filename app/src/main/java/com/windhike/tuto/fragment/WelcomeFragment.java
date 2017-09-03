package com.windhike.tuto.fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Handler;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.qq.e.ads.splash.SplashADListener;
import com.windhike.fastcoding.CommonFragmentActivity;
import com.windhike.tuto.R;
import butterknife.BindView;
import rx.Observable;
import rx.Subscriber;

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
    public int getLayouId() {
        return R.layout.fragment_welcome;
    }

    @Override
    public void initView() {
        super.initView();
        llLogo.setAlpha(0);
        llLogo.animate().alpha(1).setDuration(3000).start();
//        checkAndRequestPermission(Manifest.permission.CAMERA,Manifest.permission.READ_PHONE_STATE);
        checkAndRequestPermission(Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.ACCESS_FINE_LOCATION);
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
    }

    @Override
    protected void onGranted() {
        super.onGranted();
        //,Manifest.permission.SYSTEM_ALERT_WINDOW 不弹窗
        mHandler.postDelayed(mSplashRunnable,3000);
    }

    private Runnable mSplashRunnable = new Runnable() {
        @Override
        public void run() {
//            getActivity().startActivity(new Intent(getActivity(), AnnotationPicturesActivity.class));
            CommonFragmentActivity.start(getActivity(),MainPageFragment.class.getName(),null);
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

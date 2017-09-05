package com.windhike.tuto.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.windhike.fastcoding.CommonFragmentActivity;
import com.windhike.tuto.BuildConfig;
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
    @BindView(R.id.splash_container)
    FrameLayout splash_container;
    @BindView(R.id.skip_view)
    TextView skipView;
    @BindView(R.id.splash_holder)
    View splashHolder;
    private Handler mHandler = new Handler();
    private SplashAD splashAD;

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
//        fetchSplashAD(getActivity(), splash_container, skipView, BuildConfig.ad_appid, BuildConfig.ad_splashposid, this, 0);
    }

    private void fetchSplashAD(Activity activity, ViewGroup adContainer, View skipContainer,
                               String appId, String posId, SplashADListener adListener, int fetchDelay) {
        splashAD = new SplashAD(activity, adContainer, skipContainer, appId, posId, adListener, fetchDelay);
    }

    private Runnable mSplashRunnable = new Runnable() {
        @Override
        public void run() {
//            getActivity().startActivity(new Intent(getActivity(), AnnotationPicturesActivity.class));
            onNoAD(0);
        }
    };

    private void next() {
//        if (canJump) {
//            onNoAD(0);
//        } else {
//            canJump = true;
//        }
    }

    /** 开屏页一定要禁止用户对返回按钮的控制，否则将可能导致用户手动退出了App而广告无法正常曝光和计费 */
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        if (canJump) {
//            next();
//        }
//        canJump = true;
//    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        canJump = false;
//    }

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
        next();
    }

    @Override
    public void onNoAD(int i) {
        CommonFragmentActivity.start(getActivity(),MainPageFragment.class.getName(),null);
        getActivity().finish();
    }

    @Override
    public void onADPresent() {
        splashHolder.setVisibility(View.INVISIBLE); // 广告展示后一定要把预设的开屏图片隐藏起来
    }

    @Override
    public void onADClicked() {

    }

    private static final String SKIP_TEXT = "点击跳过 %d";
    public boolean canJump;
    @Override
    public void onADTick(long millisUntilFinished) {
        skipView.setText(String.format(SKIP_TEXT, Math.round(millisUntilFinished / 1000f)));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}

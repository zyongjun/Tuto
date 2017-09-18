package com.zyongjun.easytouch.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import com.zyongjun.easytouch.R;
import com.zyongjun.easytouch.utils.Utils;

/**
 * author:gzzyj on 2017/8/7 0007.
 * email:zhyongjun@windhike.cn
 */
public class IconHolder extends BaseTouchViewHolder{
    private Button mLauchIcon;
    private int iconViewX = 50, iconViewY = 50;
    private float startX = 0, startY = 0;
    private float startRawX = 0, startRawY = 0;
    private View.OnClickListener mClickListener;

    public IconHolder(Context context) {
        super(context);
    }

    public void setOnClickListener(View.OnClickListener clickListener) {
        mClickListener = clickListener;
    }

    @Override
    public void initView() {
        if (mLauchIcon != null) {
//            mWindowManager.removeViewImmediate(mLauchIcon);
            mWindowManager.addView(mLauchIcon,getViewLayoutParams());
            return;
        }
        mLauchIcon = new Button(mContext);
        mLauchIcon.setBackgroundResource(R.drawable.selector_btn_launcher);
        mLauchIcon.setOnClickListener(mClickListener);
        mLauchIcon.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float rawX = event.getRawX();
                float rawY = event.getRawY();
                int sumX = (int) (rawX - startRawX);
                int sumY = (int) (event.getRawY() - startRawY);
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        startRawX = event.getRawX();
                        startRawY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        mWindowManager.updateViewLayout(mLauchIcon,getViewLayoutParams());

                        startX = 0;
                        startY = 0;
                        startRawY = 0;
                        startRawX = 0;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (sumX < -10 || sumX > 10 || sumY < -10 || sumY > 10){
                            iconViewX = mMetrics.widthPixels-(int) (rawX-startX);
                            iconViewY = (int)(rawY-startY)-mMetrics.heightPixels/2;
                            mWindowManager.updateViewLayout(mLauchIcon,getViewLayoutParams());
                        }
                        break;
                }
                return false;
            }
        });

        mWindowManager.getDefaultDisplay().getMetrics(mMetrics);
        mWindowManager.addView(mLauchIcon,getViewLayoutParams());
    }

    private DisplayMetrics mMetrics = new DisplayMetrics();;


    private static final int ICON_SIZE = 38;
    @Override
    public WindowManager.LayoutParams getViewLayoutParams() {
        WindowManager.LayoutParams windowLayoutParams = new WindowManager.LayoutParams();
        windowLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        windowLayoutParams.format = PixelFormat.RGBA_8888;
        windowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        windowLayoutParams.x = iconViewX;
        windowLayoutParams.y = iconViewY;
        windowLayoutParams.windowAnimations=R.style.IconViewAnimator;
        DisplayMetrics metrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(metrics);
        windowLayoutParams.width = Utils.dip2px(mContext,ICON_SIZE);
        windowLayoutParams.height = Utils.dip2px(mContext,ICON_SIZE);
        windowLayoutParams.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
        return windowLayoutParams;
    }

    @Override
    public View getRootView() {
        return mLauchIcon;
    }

    private static final String TAG = "IconHolder";
    @Override
    public void onDestory() {
        if (mLauchIcon != null&& ViewCompat.isAttachedToWindow(mLauchIcon)) {
            mWindowManager.removeViewImmediate(mLauchIcon);
        }
    }
}

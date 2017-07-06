package com.windhike.tuto;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.windhike.tuto.reuse.ToolbarBuilder;

import java.util.Map;

/**
 * author: zyongjun on 2017/6/29 0029.
 * email: zhyongjun@windhike.cn
 */

public abstract class BaseAty extends AppCompatActivity {

    protected Handler handler;
    protected Bundle bundle;
    protected Context mContext;
    protected Map<String, String> params;

    protected AlertDialog alertDialog;
    protected AnimationDrawable anim;
    protected DisplayMetrics metric;
    protected int mScreenWidth;
    protected int mScreenHeight;
    protected TutoApplication app;

    private ToolbarBuilder mToolbarBuilder;

    public void setOnClickRight(ToolbarBuilder.OnClickRight onClickRight) {
        mToolbarBuilder.withRightClick(onClickRight);
    }

    public void setOnClickLeft(ToolbarBuilder.OnClickLeft onClickLeft) {
        mToolbarBuilder.withLeftClick(onClickLeft);
    }

    public ToolbarBuilder getToolbarBuilder(){
        return mToolbarBuilder;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mContext = this;
        if (mScreenWidth == 0) {
            metric = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metric);
            mScreenWidth = metric.widthPixels;
            mScreenHeight = metric.heightPixels;
        }
        app = (TutoApplication)getApplication();
        mToolbarBuilder = new ToolbarBuilder(this);
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public void setTitleCustomer(boolean isLeft, boolean isRight,
                                 String left, String title, String right, int imbResId){
        setTitleCustomer(isLeft,isRight,left,title,right,imbResId);
    }

    public void setTitleCustomer(boolean isLeft, boolean isRight,
                                 String left, String title, String right, boolean isShowBack){
        mToolbarBuilder.showLeft(isLeft).withLeft(left)
                .showRight(isRight).withRight(right)
                .withTitle(title)
                .withBack(isShowBack)
                .show();
    }



    protected void startJump(Class<? extends Activity> cls) {
        startJump(cls, null);
    }

    protected void startJump(Class<? extends Activity> cls, Bundle bundle) {
        Intent intent = new Intent(this, cls);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    protected void sendBroadCast(String action, Bundle bundle) {
        Intent intent = new Intent(action);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        /** SDK 4.2系统添加多用户支持 **/
		/*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			sendBroadcastAsUser(intent, new UserHandle(null));
			return;
		}*/
        sendBroadcast(intent);
    }

    public void showWaitingDialog(Context context) {
        showOpDialog(context, "正在加载...",false);
    }

    public void showOpDialog(Context context, String message,boolean isCancel) {
        alertDialog = new android.app.AlertDialog.Builder(context).create();
        alertDialog.setCanceledOnTouchOutside(isCancel);
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setBackgroundDrawableResource(R.color.transparent);
        window.setContentView(R.layout.view_loading);
        ImageView imgLoading =  (ImageView) window.findViewById(R.id.imgLoading);
        anim= (AnimationDrawable) imgLoading.getBackground();
        TextView txtHint = (TextView) window.findViewById(R.id.txtHint);
        txtHint.setText(message);
        anim.start();
    }

    public void dismissOpDialog() {
        if (alertDialog == null || !alertDialog.isShowing()) {
            return;
        }
        alertDialog.dismiss();
        if(anim!=null){
            anim.stop();
        }
        alertDialog = null;
    }

    public Context getViewContext() {
        return this;
    }

    public void hideViewByIds(int... ids) {
        for (int id : ids) {
            findViewById(id).setVisibility(View.GONE);
        }
    }

    public void showViewByIds(int... ids) {
        for (int id : ids) {
            findViewById(id).setVisibility(View.VISIBLE);
        }
    }
}
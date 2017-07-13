package com.windhike.tuto;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import com.windhike.tuto.reuse.ToolbarBuilder;

/**
 * author: zyongjun on 2017/6/29 0029.
 * email: zhyongjun@windhike.cn
 */

public abstract class BaseAty extends AppCompatActivity {

    protected Context mContext;

    protected AlertDialog alertDialog;
    protected AnimationDrawable anim;
    protected DisplayMetrics metric;
    protected int mScreenWidth;
    protected int mScreenHeight;
    protected TutoApplication app;

    private ToolbarBuilder mToolbarBuilder;

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
//        n.i(this);
//        getWindow().getDecorView().setFitsSystemWindows(true);
//        UIUtil.requestApplyInsets(getWindow());
        getWindow().getDecorView().setBackgroundColor(ResourcesCompat.getColor(getResources(),R.color.black,null));
//        n.j(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
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
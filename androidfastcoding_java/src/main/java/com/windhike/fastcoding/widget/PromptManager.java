package com.windhike.fastcoding.widget;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.windhike.fastcoding.R;
import com.windhike.fastcoding.util.UIUtil;

/**
 * 温馨提示窗口管理工具类：根据给定消息内容回调
 */
public class PromptManager implements View.OnClickListener {

    private Dialog dialog;
    private OnClickBtnCallback callback;
    private Context mContext;
    private int msgTextGravity = Integer.MIN_VALUE;

    private PromptManager(Context context) {
        mContext = context;
        initDialog();
    }

    public void setMsgTextGravity(int gravity){
        msgTextGravity = gravity;
    }

    private void initDialog(){
        if(dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
        dialog = null;
        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.setContentView(R.layout.prompt_dialog);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setBackgroundDrawableResource(R.color.translucence);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//        lp.width = TutoApplication.getInstance().getDisplayMetrics().widthPixels*3/4;
        lp.width = UIUtil.DeviceInfo.getDeviceScreenWidth()*3/4;
    }

    public static PromptManager getInstance(Context context) {
        return new PromptManager(context);
    }

    public void setContentView(int layout){
        dialog.setContentView(layout);
    }

    public interface OnClickBtnCallback {
        /**
         *  右边点击操作
         */
        public void confirmClick();

        /**
         * 左边点击操作
         */
        public void cancelClick();
    }

    public void showDialog(String title, String msg, String cancel, String confirm, OnClickBtnCallback callback) {
        show(title, msg, cancel, confirm, callback);
    }

    protected void show(String title, String msg, String cancel, String confirm, OnClickBtnCallback callback){
        if (dialog != null && callback != null) {
            this.callback = callback;

            TextView positiveBtn = (TextView) dialog.findViewById(R.id.tv_dialog_confirm);
            TextView negativeBtn = (TextView) dialog.findViewById(R.id.tv_dialog_cancel);
            TextView titleText = (TextView) dialog.findViewById(R.id.tv_title);
            TextView msgText = (TextView) dialog.findViewById(R.id.tv_msg);
            if(msgTextGravity != Integer.MIN_VALUE){
                msgText.setGravity(msgTextGravity);
            }
            if (!TextUtils.isEmpty(title)) {
                titleText.setVisibility(View.VISIBLE);
                titleText.setText(title);
                if(TextUtils.isEmpty(msg)){
                    int padding = mContext.getResources().getDimensionPixelSize(R.dimen.d_15);
                    titleText.setPadding(padding, padding, padding, padding);
                }
            } else {
                titleText.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(msg)) {
                msgText.setVisibility(View.VISIBLE);
                msgText.setText(msg);
            } else {
                msgText.setText("");
                msgText.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(confirm)) {
                positiveBtn.setText(confirm);
                positiveBtn.setVisibility(View.VISIBLE);
            }else{
                positiveBtn.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(cancel)) {
                negativeBtn.setText(cancel);
            }

            // 设置点击监听
            positiveBtn.setOnClickListener(this);
            negativeBtn.setOnClickListener(this);
            dialog.show();
        }
    }

    /**
     * 根据 消息内容显示对话框（默认按钮为 取消 - 确认）
     * @param msg
     * @param callback
     */
    public void showDialog(String msg, OnClickBtnCallback callback) {
        showDialog(null, msg, null, null, callback);
    }


    /**
     * 隐藏消息提示框
     */
    public void closeDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    /**
     * 点击监听回调
     * @param view
     */
    @Override
    public void onClick(View view) {
        if (callback != null) {
            int id = view.getId();
            if (id == R.id.tv_dialog_confirm) {
                callback.confirmClick();
            }else if(id ==R.id.tv_dialog_cancel) {
                callback.cancelClick();
            }
            closeDialog();
        }
    }

}

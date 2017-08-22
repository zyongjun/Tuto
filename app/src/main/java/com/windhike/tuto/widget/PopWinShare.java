package com.windhike.tuto.widget;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import com.windhike.tuto.R;
import java.io.ByteArrayOutputStream;

/**
 * author:gzzyj on 2017/8/22 0022.
 * email:zhyongjun@windhike.cn
 */

public class PopWinShare extends PopupWindow implements View.OnClickListener{
    protected View mView;
    protected View mRootView;
    protected IWXAPI wxApi;
    protected Activity mActivity;
    private Handler mHandler = new Handler();
    private ShareCallback mShareCallback;
    public interface ShareCallback {
        void onClose();
        Bitmap getCurrentBitmap();
    }

    public PopWinShare(Activity paramActivity, View rootView) {
        super(paramActivity);
        mActivity = paramActivity;
        mRootView = rootView;
        //窗口布局
        mView = LayoutInflater.from(paramActivity).inflate(R.layout.popup_share_menu_list_layout, null);
        //微信分享按钮
        mView.findViewById(R.id.share_btn_wx).setOnClickListener(this);
        //微信朋友圈分享按钮
        mView.findViewById(R.id.share_btn_pyx).setOnClickListener(this);

        mView.findViewById(R.id.share_btn_close).setOnClickListener(this);
        //设置每个子布局的事件监听器
        mView.findViewById(R.id.share_layout_btn).setOnClickListener(this);
//        mView.findViewById(R.id.share_btn_multi_photo).setOnClickListener(this);
//        mView.setOnClickListener(this);
        mView.setAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.share_view_push_in));

        setContentView(mView);
        //设置宽度
        setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        //设置高度
        setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        //设置显示隐藏动画
        setAnimationStyle(R.style.ShareAnimStyle);
        //设置背景透明
        setBackgroundDrawable(mActivity.getResources().getDrawable(R.color.color_share_pop_bg));
        setClippingEnabled(false);

        initWXAPI();
    }

    public void setShareCallback(ShareCallback listener) {
        mShareCallback = listener;
    }


//    public View getRootView() {
//        return mRootView;
//    }

    public void showShareWin() {
        if (mRootView != null) {
            //设置默认获取焦点
            setFocusable(true);
            //以某个控件的x和y的偏移量位置开始显示窗口
            showAtLocation(mRootView, Gravity.BOTTOM, 0, 0);
            //如果窗口存在，则更新
            update();
        }
    }

    private void initWXAPI() {
        wxApi = WXAPIFactory.createWXAPI(mRootView.getContext(), mRootView.getContext().getString(R.string.wx_appid), true);
        wxApi.registerApp(mRootView.getContext().getString(R.string.wx_appid));
    }

    protected void wechatShare(final boolean isShareToWx) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (wxApi != null) {
                    if (!wxApi.isWXAppInstalled()) {
                        Toast.makeText(mRootView.getContext(), R.string.toast_share_wx_nosetup, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!wxApi.isWXAppSupportAPI()) {
                        Toast.makeText(mRootView.getContext(), R.string.toast_share_wx_update, Toast.LENGTH_SHORT).show();
                        return;
                    }
//                    WXWebpageObject webpage = new WXWebpageObject();
//                    webpage.webpageUrl = mShareObject.webUrl;
                    Bitmap bitmap = mShareCallback.getCurrentBitmap();
                    if (bitmap == null || bitmap.isRecycled()) {
                        return;
                    }
                    WXImageObject imageObject = new WXImageObject(bitmap);
                    WXMediaMessage msg = new WXMediaMessage(imageObject);
//                    msg.title = mShareObject.title;
//                    msg.description = mShareObject.desc;
//                    Bitmap bitmap = null;
//                    if(thumb!=null) {
//                        if (thumb.getByteCount() > 32 * 1024) {//缩略图最大32K
//                            bitmap = ImageUtil.resizeImage(thumb, thumb.getWidth() / 2, thumb.getHeight() / 2);
//                            msg.setThumbImage(bitmap);
//                        } else {
//                            msg.setThumbImage(thumb);
//                        }
//                    }
                    Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap,30,30,true);
                    bitmap.recycle();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumbBmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    msg.thumbData = baos.toByteArray();
                    thumbBmp.recycle();
                    SendMessageToWX.Req req = new SendMessageToWX.Req();
                    req.transaction = String.valueOf(System.currentTimeMillis());
                    req.message = msg;
                    req.scene =  isShareToWx?SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
                    wxApi.sendReq(req);
//                    ApplicationDelegate.getInstance().addShareObject(mShareObject);

                    closePopWin();
//                    if (!TextUtils.isEmpty(mShareObject.cpType)) {
//                        MobclickAgent.onEvent(mActivity, mShareObject.cpType);
//                    }
//                    if (thumb != null && !thumb.isRecycled()) {
//                        thumb.recycle();
//                    }
                    if (bitmap != null && !bitmap.isRecycled()) {
                        bitmap.recycle();
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.share_btn_wx:
                MobclickAgent.onEvent(mRootView.getContext(),"share_friends");
//                shareToWX();
                wechatShare(true);
                break;
            case R.id.share_btn_pyx:
                MobclickAgent.onEvent(mRootView.getContext(),"share_timeline");
//                shareToTimeline();
                wechatShare(false);
                break;
            case R.id.share_btn_close:
                closePopWin();
                break;
            case R.id.share_layout_btn:
                break;
//            case R.id.share_btn_multi_photo:
//                shareMultiPhotos();
//                break;
            default:
                closePopWin();
                break;
        }
    }

//    public void shareToWX() {
//        downloadImage(ShareObject.SHARE_WX_FRIEND);
//    }

//    public void shareToTimeline() {
//        downloadImage(ShareObject.SHARE_WX_TIMELINE);
//    }

//    private static final String TAG = "PopWinShare";
//    protected void shareMultiPhotos() {
//        closePopWin();
//        String[] urls = mShareObject.imgUrls;
//        String[] compositeImageUrls = mShareObject.compositeImageUrls;
////        if (urls == null || compositeImageUrls == null || urls.length != compositeImageUrls.length) {
//        if (urls == null || compositeImageUrls == null) {
////            ApplicationDelegate.showToast("图片信息不对称，分享失败");
//            ApplicationDelegate.showToast("无二维码合成图片，分享失败");
//            return;
//        }
//        MultiPhotoShareFragment.enter(mActivity,mShareObject);
//    }

    public void closePopWin() {
        if (isShowing()) {
            Animation outAnim = AnimationUtils.loadAnimation(mActivity, R.anim.share_view_push_out);
            outAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    new Handler().post(new Runnable() {//加Handler解决空指针问题

                        @Override
                        public void run() {
                            try {
                                if (isShowing()) {
                                    dismiss();
                                }
                            } catch (Exception e) {
                            }
                            if (mShareCallback != null) {
                                mShareCallback.onClose();
                            }
                        }
                    });

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mView.startAnimation(outAnim);
        }
    }

    @Override
    public void update() {
        super.update();
        mView.setAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.share_view_push_in));
    }
}

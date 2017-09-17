package com.windhike.tuto;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.windhike.annotation.common.Utilities;
import com.windhike.annotation.model.ImageDrawObject;
import com.windhike.annotation.model.ListenerEventUpdate;
import com.windhike.annotation.reuse.DisplayUtil;
import com.windhike.fastcoding.util.UIUtil;
import com.windhike.tuto.widget.DrawingView;
import com.windhike.tuto.utils.BaseAnnotationPreviewPresenter;
import com.windhike.tuto.view.IAnnotationEditView;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AnnotationPreviewActivity extends BaseAty implements IAnnotationEditView {
    public static final String KEY_PROJECT_DRAW_INDEX ="KEY_REMARK_ID";
    public static final String KEY_SHARE_ANNOTATION = "KEY_SHARE_ANNOTATION";
    public static final String KEY_SHARE_BY_SELF = "KEY_SHARE_SELF";
    public static final String KEY_SHARE_CONVERSATION = "KEY_SHARE_CONVERSATION";
    public static final int REQUEST_SEND = 130;
    private BaseAnnotationPreviewPresenter mPresenter;

    @BindView(R.id.drawingView)
    DrawingView drawingView;
    @BindView(R.id.drawingContent)
    RelativeLayout drawingContent;
    @BindView(R.id.tv_scale)
    TextView tvScale;
    @BindView(R.id.ll_center)
    View llCenter;

    public static Intent getAnnotationPreviewIntent(Context context, int index) {
        Intent intent = new Intent(context, AnnotationPreviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_PROJECT_DRAW_INDEX,index);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annotation_preview);
        ButterKnife.bind(this);
        mPresenter = BaseAnnotationPreviewPresenter.create(getIntent().getExtras());
        mPresenter.attach(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.initProject();
    }

    @Override
    protected void onDestroy() {
        mPresenter.detach();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }
        if (requestCode == REQUEST_SEND) {
            finish();
        }
    }

    @Override
    public void loadDrawObject() {
        if(drawingContent!=null){
                ArrayList<ImageDrawObject> imageDrawObjects = mPresenter.getManagerObject().getListChooseDrawObject();
                String mImageEditPath = imageDrawObjects.get(mPresenter.getDrawObjectIndex()).getEditImagePath();
                String mImageDisplayPath = imageDrawObjects.get(mPresenter.getDrawObjectIndex()).getOriginalImagePath();
                boolean flagLoadListRootShape = false;
                if ((mImageEditPath != null && mImageEditPath.length() > 0) || imageDrawObjects.get(0).getListRootShape().size() > 0) {
                    flagLoadListRootShape = true;
                }
                if (new File(mImageDisplayPath).exists()) {
                    Bitmap currentLoadingBitmap = Utilities.scaleToActualAspectRatio(BitmapFactory.decodeFile(mImageDisplayPath),
                            UIUtil.DeviceInfo.getDeviceWidth(),
                            UIUtil.DeviceInfo.getDeviceHeight()- UIUtil.dpToPx(110));
                    drawingView.setEdit(true);
                    drawingView.setListener(updateListener);
                    drawingView.setwMainView(UIUtil.DeviceInfo.getDeviceWidth());
                    drawingView.sethMainView(UIUtil.DeviceInfo.getDeviceHeight()- UIUtil.dpToPx(110));
                    drawingView.setMainBitmap(currentLoadingBitmap);
                    if (flagLoadListRootShape) {
                        drawingView.loadListRootShape(imageDrawObjects.get(mPresenter.getDrawObjectIndex()).getListRootShape(), imageDrawObjects.get(mPresenter.getDrawObjectIndex()).getListRootShape().size());
                    } else {
                        drawingView.clearAllRootShape();
                    }
                    drawingView.changeDrawingObjectPosition();
                }

                drawingView.postInvalidate();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tvScale.setVisibility(View.VISIBLE);
                        tvScale.setText(String.format("%s%%",(int)(drawingView.getScaleFactor()*100)));
                        drawingView.setEnableScaleMode(true);
                    }
                },500);
        }

    }

    private Handler mHandler = new Handler() ;

    private ListenerEventUpdate updateListener = new ListenerEventUpdate(){

        @Override
        public void onScale(float scaleFactor) {
            tvScale.setText(String.format("%s%%",(int)(scaleFactor*100)));
        }

        @Override
        public void isOnOff_Undo_Redo(boolean isOffUndo, boolean isOffRedo) {

        }

    };

//    @Override
//    public void onErrorTips() {
//        if(!isFinishing()) {
//            Toast.makeText(this,"加载失败",Toast.LENGTH_SHORT).show();
//        }
//    }

}

package com.windhike.tuto;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.windhike.annotation.common.Utilities;
import com.windhike.annotation.configsapp.Configs;
import com.windhike.annotation.model.AudioPlayManager;
import com.windhike.annotation.model.ImageDrawObject;
import com.windhike.annotation.model.ListenerEventUpdate;
import com.windhike.annotation.model.PreferenceConnector;
import com.windhike.annotation.reuse.DisplayUtil;
import com.windhike.annotation.view.CustomerShapeView;
import com.windhike.fastcoding.util.UIUtil;
import com.windhike.tuto.widget.DrawingView;
import com.windhike.tuto.audio.AnnotationAudioRecordManager;
import com.windhike.tuto.event.AnnotationChangedEvent;
import com.windhike.tuto.event.AudioRecordStopEvent;
import com.windhike.fastcoding.widget.PromptManager;
import com.windhike.fastcoding.rx.SchedulersTransFormer;
import com.windhike.tuto.reuse.ToolbarBuilder;
import com.windhike.tuto.utils.AnnotationEditNewDrawPresenter;
import com.windhike.tuto.utils.AnnotationEditPresenter;
import com.windhike.tuto.utils.AnnotationEditSendPresenter;
import com.windhike.tuto.utils.BaseAnnotationEditPresenter;
import com.windhike.tuto.view.IAnnotationEditView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.io.File;
import java.util.ArrayList;
import java.util.UUID;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ibos.ui.widget.DrawingMenuDialog;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import rx.Observable;
import rx.Observer;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * author: gzzyj on 2017/3/2.
 * email:zhyongjun@windhike.cn
 */

public class AnnotationProjectEditActivity extends BaseAty implements View.OnClickListener, IAnnotationEditView {
    //0 exist 1 converstion 2 newdraw
    private static final String KEY_TYPE = "KEY_TYPE";
    @BindView(R.id.fl_reset)
    View vResetContainer;
    @BindView(R.id.tvReset)
    TextView tvReset;
    @BindView(R.id.fl_restore)
    View vRestoreContainer;
    @BindView(R.id.tvRestore)
    TextView tvRestore;
    @BindView(R.id.txtRight)
    TextView txtRight;
    @BindView(R.id.drawingView)
    DrawingView drawingView;
    @BindView(R.id.drawingContent)
    RelativeLayout drawingContent;
    @BindView(R.id.ll_drawshape)
    View llDrawShape;
    @BindView(R.id.tvDrawShape)
    TextView tvDrawShape;
    @BindView(R.id.tvDrawShapeIndicate)
    TextView tvDrawShapeIndicate;
    @BindView(R.id.ll_drawtalk)
    View llDrawTalk;
    @BindView(R.id.tvDrawTalk)
    ImageView tvDrawTalk;
    @BindView(R.id.ll_drawtext)
    View llDrawText;
    @BindView(R.id.tvDrawText)
    TextView tvDrawText;
    @BindView(R.id.ll_scale)
    View llScale;
    @BindView(R.id.tvDrawScale)
    TextView tvDrawScale;
    @BindView(R.id.activity_annotation_edit)
    LinearLayout mRootView;
    @BindView(R.id.tv_scale)
    TextView tvScale;
    @BindView(R.id.ll_center)
    View llCenter;
    private BaseAnnotationEditPresenter mPresenter;

    public static Intent getAnnotationProjectIntent(Context context, int drawIndex) {
        Intent intent = new Intent(context, AnnotationProjectEditActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Configs.KEY_ANNOTATION_DRAW_INDEX, drawIndex);
        intent.putExtras(bundle);
        return intent;
    }

    //image gallery to new annotation
    public static Intent getAnnotationConversationProjectIntent(Context context, String path) {
        Intent intent = new Intent(context, AnnotationProjectEditActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_TYPE, 1);
        bundle.putString(Configs.KEY_ANNOTATION_DRAW_NEW_PATH, path);
        intent.putExtras(bundle);
        return intent;
    }

    public static Intent getAnnotationNewDrawIntent(Context context, String path) {
        Intent intent = new Intent(context, AnnotationProjectEditActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_TYPE, 2);
        bundle.putString(Configs.KEY_ANNOTATION_DRAW_NEW_PATH, path);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annotation_edit);
        TutoApplication.getInstance().configScreenSize(getWindowManager());
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        int type = getIntent().getIntExtra(KEY_TYPE, 0);
        if (type == 1) {
            mPresenter = new AnnotationEditSendPresenter(getIntent().getExtras());
        } else if (type == 0) {
            mPresenter = new AnnotationEditPresenter(getIntent().getExtras());
        } else if (type == 2) {
            mPresenter = new AnnotationEditNewDrawPresenter(getIntent().getExtras());
        }
        mPresenter.attach(this);
        getToolbarBuilder().showLeft(true)
                .withLeft("取消")
                .withLeftClick(new ToolbarBuilder.OnClickLeft() {
                    @Override
                    public void onClickLeft() {
                        onBackPressed();
                    }
                })
                .withBack(false)
                .showRight(true)
                .withRightClick(new ToolbarBuilder.OnClickRight() {
                    @Override
                    public void onClickRight() {
                        showOpDialog(AnnotationProjectEditActivity.this, "保存中", false);
                        saveImageReportAndSync();
                    }
                })
                .withRight(type == 1 || type == 3 ? "发送" : "保存")
                .show();
        llDrawShape.setOnClickListener(this);
        tvDrawShape.setSelected(true);
        tvDrawShapeIndicate.setSelected(true);
        if (type == 0 || type == 3) {
            drawingContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    drawingContent.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    loadDrawObject();
                }
            });
        }
        vResetContainer.setOnClickListener(this);
        tvReset.setOnClickListener(this);
        vRestoreContainer.setOnClickListener(this);
        tvRestore.setOnClickListener(this);
        llDrawText.setOnClickListener(this);
        llDrawTalk.setOnTouchListener(speakTouchListener);
        llScale.setOnClickListener(this);
        mOffsetLimit = 70.0f * getResources().getDisplayMetrics().density;

    }

    private float mVoiceLastX, mVoiceLastY;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(AudioRecordStopEvent event) {
        if (drawingView != null) {
            float xPiece = (drawingView.getwMainView() - drawingView.getxMainBitmap()) / 8f;
            float yPiece = (drawingView.gethMainView() - drawingView.getyMainBitmap()) / 8f;
            if (mVoiceLastX == 0 && mVoiceLastY == 0) {
                mVoiceLastX = drawingView.getxMainBitmap() + xPiece;
                mVoiceLastY = drawingView.getyMainBitmap() + yPiece;
            } else {
                mVoiceLastX += xPiece;
            }
            if (mVoiceLastX < xPiece || mVoiceLastX >= xPiece * 6) {
                mVoiceLastX = xPiece;
                mVoiceLastY += yPiece;
                if (mVoiceLastY < yPiece || mVoiceLastY >= yPiece * 6) {
                    mVoiceLastY = yPiece;
                }
            }
            drawingView.createVoice(mVoiceLastX, mVoiceLastY, event.getAudioPath());
        }
    }

    float mOffsetLimit;
    float lastTouchY;
    boolean upDirection;
    private View.OnTouchListener speakTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event != null) {
//                if (Build.VERSION.SDK_INT < 23 || v.getContext().checkSelfPermission("android.permission.RECORD_AUDIO") == PackageManager.PERMISSION_GRANTED || RongContext.getInstance().getRequestPermissionListener() == null) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    AudioPlayManager.getInstance().stopPlay();
                    AnnotationAudioRecordManager.getInstance().startRecord(llCenter);
                    lastTouchY = event.getY();
                    upDirection = false;
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (lastTouchY - event.getY() > mOffsetLimit && !upDirection) {
                        AnnotationAudioRecordManager.getInstance().willCancelRecord();
                        upDirection = true;
                    } else if (event.getY() - lastTouchY > (-mOffsetLimit) && upDirection) {
                        AnnotationAudioRecordManager.getInstance().continueRecord();
                        upDirection = false;
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    AnnotationAudioRecordManager.getInstance().stopRecord();
                }
//                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    RongContext.getInstance().getRequestPermissionListener().onPermissionRequest(new String[]{"android.permission.RECORD_AUDIO"}, 100);
//                }
            }

            return true;
        }
    };

    private void filterShapeInCurrentBitmap() {
        if (mPresenter.getManagerObject() != null) {
            int indexCurrentDraw = mPresenter.getDrawObjectIndex();
            drawingView.clearFocusAllShape();
            ArrayList<CustomerShapeView> listShapeCurrent = drawingView.getListRootShape();
            ArrayList<ImageDrawObject> imageDrawObjects = mPresenter.getManagerObject().getListChooseDrawObject();
            if (imageDrawObjects.size() > 0 && indexCurrentDraw <= imageDrawObjects.size() - 1) {
                imageDrawObjects.get(indexCurrentDraw).getListRootShape().clear();
                imageDrawObjects.get(indexCurrentDraw).getListRootShape().addAll(listShapeCurrent);
                imageDrawObjects.get(indexCurrentDraw).set_undoRedoCurrentIndex(drawingView.get_undoRedoCurrentIndex());
            }
            mPresenter.saveManageImageObject();
        }
    }

    @Override
    public void onBackPressed() {
        if (isShowCirform) {
            cancelEditCirform();
        } else {
            super.onBackPressed();
        }
    }

    private boolean isShowCirform = true;

    public void cancelEditCirform() {
        PromptManager.getInstance(this).showDialog("提示","确定放弃对该图片的所有批注","取消","确定", new PromptManager.OnClickBtnCallback() {
            @Override
            public void confirmClick() {
                                isShowCirform = false;
                mPresenter.exitWithoutSave();
                finish();
            }

            @Override
            public void cancelClick() {

            }
        });
    }

    private void showShapeSettingMenu() {
        DrawingMenuDialog drawingMenuDialog = new DrawingMenuDialog(this, new Function1<View, Unit>() {
            @Override
            public Unit invoke(View view) {
                switch (view.getId()) {
                    case R.id.ivPen:
                        drawingView.setmModeCreateObjPaint(0);
                        tvDrawShape.setBackgroundResource(R.drawable.tab_anno_free_selector);
                        break;
                    case R.id.ivRectange:
                        drawingView.setmModeCreateObjPaint(2);
                        tvDrawShape.setBackgroundResource(R.drawable.tab_anno_rectange_selector);
                        break;
                    case R.id.ivCircle:
                        drawingView.setmModeCreateObjPaint(3);
                        tvDrawShape.setBackgroundResource(R.drawable.tab_anno_circle_selector);
                        break;
                    case R.id.ivArrow:
                        drawingView.setmModeCreateObjPaint(1);
                        tvDrawShape.setBackgroundResource(R.drawable.tab_anno_arrow_selector);
                        break;
                    case R.id.ivEraser:
                        drawingView.setmModeCreateObjPaint(7);
                        tvDrawShape.setBackgroundResource(R.drawable.tab_anno_eraser_selector);
                        break;
                    case R.id.ivMask:
                        drawingView.setmModeCreateObjPaint(4);
                        tvDrawShape.setBackgroundResource(R.drawable.tab_anno_mask_selector);
                        break;
                    case R.id.ivColorOne:
                        drawingView.setCurrentPaint(0, 1, "");
                        break;
                    case R.id.ivColorSecond:
                        drawingView.setCurrentPaint(1, 1, "");
                        break;
                    case R.id.ivColorThird:
                        drawingView.setCurrentPaint(2, 1, "");
                        break;
                    case R.id.ivColorFour:
                        drawingView.setCurrentPaint(3, 1, "");
                        break;
                    case R.id.ivColorFive:
                        drawingView.setCurrentPaint(4, 1, "");
                        break;
                    case R.id.ivColorSix:
                        drawingView.setCurrentPaint(5, 1, "");
                        break;
                }
                return null;
            }
        });
        drawingMenuDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                tvDrawShapeIndicate.setRotation(0);
            }
        });
        drawingMenuDialog.show();
    }

    private static final int REQUEST_CODE_EDIT_TEXT = 180;
    private static final int REQUEST_CODE_CREATE_TEXT = 181;

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.fl_reset:
            case R.id.tvReset:
                actionUndo();
                break;
            case R.id.fl_restore:
            case R.id.tvRestore:
                actionRedo();
                break;
            case R.id.ll_drawshape:
                tvScale.setVisibility(View.GONE);
                tvDrawScale.setBackgroundResource(R.mipmap.ic_draw_scale);
                drawingView.setEnableScaleMode(false);
                if (tvDrawShape.isSelected()) {
                    tvDrawShapeIndicate.setRotation(180);
                    showShapeSettingMenu();
                } else {
                    tvDrawShape.setSelected(true);
                    tvDrawShapeIndicate.setSelected(true);
                }
                break;
            case R.id.ll_drawtalk:
                tvScale.setVisibility(View.GONE);
                tvDrawScale.setBackgroundResource(R.mipmap.ic_draw_scale);
                drawingView.setEnableScaleMode(false);
                tvDrawShape.setSelected(true);
                tvDrawShapeIndicate.setSelected(true);
                break;
            case R.id.ll_drawtext:
                tvDrawShape.setSelected(true);
                tvDrawShapeIndicate.setSelected(true);
                tvScale.setVisibility(View.GONE);
                tvDrawScale.setBackgroundResource(R.mipmap.ic_draw_scale);
                drawingView.setEnableScaleMode(false);
                startActivityForResult(AnnotationTextEditActivity.getTextEditIntent(this, ""), REQUEST_CODE_CREATE_TEXT);
                break;
            case R.id.ll_scale:
                tvScale.setVisibility(View.VISIBLE);
                tvScale.setText(String.format("%s%%", (int) (drawingView.getScaleFactor() * 100)));
                drawingView.setEnableScaleMode(true);
                tvDrawScale.setBackgroundResource(R.mipmap.ic_draw_scale_selected);
                tvDrawShape.setSelected(false);
                tvDrawShapeIndicate.setSelected(false);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }
        if (requestCode == REQUEST_CODE_CREATE_TEXT) {
            String editStr = data.getStringExtra(Intent.EXTRA_RETURN_RESULT);
            if (!TextUtils.isEmpty(editStr)) {
                drawingView.createText((float) drawingView.getwMainView() / 3, (float) drawingView.gethMainView() / 2, editStr);
                drawingView.isWaittingUp = true;
                drawingView.postInvalidate();
            }
        } else if (requestCode == REQUEST_CODE_EDIT_TEXT) {
            String editStr = data.getStringExtra(Intent.EXTRA_RETURN_RESULT);
            if (!TextUtils.isEmpty(editStr)) {
                if (drawingView.getCurrentTextObject() != null) {
                    if (!drawingView.getCurrentTextObject().getText().equals(editStr)) {
                        drawingView.updateRealTextObject(editStr);
//                        drawingView.postInvalidate();
                    } else {
                        drawingView.getCurrentTextObject().isVisible = true;
                        drawingView.getCurrentTextObject().isFocus = false;
                    }
                }
            } else {
                drawingView.deleteCurrentShape();
            }
            drawingView.isWaittingUp = true;
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (drawingView != null) {
                        drawingView.postInvalidate();
                    }
                }
            }, 100);

        }
    }

    private Handler mHandler = new Handler();

    private void saveImageReportAndSync() {
        Observable.just(mPresenter.getProjectName())
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<String, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(String s) {
                        Bitmap bmCurrentDraw = drawingView.scaleBitmapAfterZoomInOut();
                        if (bmCurrentDraw != null) {
                            int index = mPresenter.getDrawObjectIndex();
                            if (index <= mPresenter.getManagerObject().getListChooseDrawObject().size() - 1) {
                                File fileEdit = new File(Utilities.getImageDrawFileNamePath(mPresenter.getProjectName(), String.format("%s%s", String.valueOf((mPresenter.getManagerObject().getListChooseDrawObject().get(index)).getOriginalImageGalleryPath()), Configs.FLAG_EDIT_FILE_NAME)));
                                if (fileEdit.exists()) {
                                    fileEdit.delete();
                                }
                                Utilities.saveToSdcardPNG(fileEdit, bmCurrentDraw);
                                //save thumbnail
                                String mUUID_PicturePath = String.format("thumbnail%s", UUID.randomUUID().toString());
                                String imagePathSDCard = Utilities.getImageDrawFileNamePath(mPresenter.getProjectName(), String.valueOf(mUUID_PicturePath) + Configs.FLAG_THUMBNAIL_FILE_NAME);
                                File filedest = new File(imagePathSDCard);
                                try {
                                    if (filedest.exists()) {
                                        filedest.delete();
                                    }
                                    Bitmap bitMapSourceSave = Utilities.scaleToActualAspectRatio(bmCurrentDraw,
                                            UIUtil.DeviceInfo.getDeviceWidth(), UIUtil.dpToPx(150));
                                    Utilities.saveToSdcardPNG(filedest, bitMapSourceSave);
                                    mPresenter.getManagerObject().getListChooseDrawObject().get(index).setEditImagePath(filedest.getAbsolutePath());
                                    bitMapSourceSave.recycle();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                mPresenter.getManagerObject().getListChooseDrawObject().get(index).getListRootShape().clear();
                                mPresenter.getManagerObject().getListChooseDrawObject().get(index).getListRootShape().addAll(drawingView.getListRootShape());
                                filterShapeInCurrentBitmap();

                                bmCurrentDraw.recycle();
                                return Observable.just(1);
                            }
                            bmCurrentDraw.recycle();
                        }
                        return null;
                    }
                }).compose(SchedulersTransFormer.<Integer>applyExecutorSchedulers())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (TextUtils.isEmpty(mPresenter.getManagerObject().getListChooseDrawObject().get(mPresenter.getDrawObjectIndex()).getRemarkId())) {
                            mPresenter.deleteProject();
                        }
                        e.printStackTrace();
                        dismissOpDialog();
                        finish();
                        Toast.makeText(TutoApplication.getInstance(), "保存失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(Integer result) {
                        dismissOpDialog();
                        isShowCirform = false;
                        mPresenter.finish();
                        EventBus.getDefault().post(new AnnotationChangedEvent("update on edit finished"));
                    }
                });
    }

    @Override
    protected void onDestroy() {
        PreferenceConnector.writeInteger(TutoApplication.getInstance(), Configs.FLAG_MODE_DRAWING, 0);
        PreferenceConnector.writeInteger(TutoApplication.getInstance(), Configs.FLAG_MODE_COLOR, 1);
        EventBus.getDefault().unregister(this);
        mPresenter.detach();
        super.onDestroy();
    }

    private boolean isEdit = true;

    public void actionUndo() {
        if (isEdit && drawingView != null) {
            drawingView.actionUndo();
        }
    }

    public void actionRedo() {
        if (isEdit && drawingView != null) {
            drawingView.actionRedo();
        }
    }

    @Override
    public void loadDrawObject() {
        if (mPresenter.getManagerObject() != null) {
            ArrayList<ImageDrawObject> imageDrawObjects = mPresenter.getManagerObject().getListChooseDrawObject();
            String mImageEditPath = imageDrawObjects.get(mPresenter.getDrawObjectIndex()).getEditImagePath();
            String mImageDisplayPath = imageDrawObjects.get(mPresenter.getDrawObjectIndex()).getOriginalImagePath();
            boolean flagLoadListRootShape = false;
            if ((mImageEditPath != null && mImageEditPath.length() > 0) || imageDrawObjects.get(mPresenter.getDrawObjectIndex()).getListRootShape().size() > 0) {
                flagLoadListRootShape = true;
            }
            if (new File(mImageDisplayPath).exists()) {
                Bitmap currentLoadingBitmap = Utilities.scaleToActualAspectRatio(BitmapFactory.decodeFile(mImageDisplayPath),
                        UIUtil.DeviceInfo.getDeviceWidth(),
                        UIUtil.DeviceInfo.getDeviceHeight() - UIUtil.dpToPx(110));
                drawingView.setEdit(isEdit);
                drawingView.setListener(updateListener);
                drawingView.setwMainView(UIUtil.DeviceInfo.getDeviceWidth());
                drawingView.sethMainView(UIUtil.DeviceInfo.getDeviceHeight() - UIUtil.dpToPx(110));
                drawingView.setMainBitmap(currentLoadingBitmap);
                if (flagLoadListRootShape) {
                    drawingView.loadListRootShape(imageDrawObjects.get(mPresenter.getDrawObjectIndex()).getListRootShape(), imageDrawObjects.get(mPresenter.getDrawObjectIndex()).getListRootShape().size());
                } else {
                    drawingView.clearAllRootShape();
                }
                drawingView.changeDrawingObjectPosition();
            }
        } else {
            Toast.makeText(TutoApplication.getInstance(), "加载失败", Toast.LENGTH_SHORT).show();
        }
    }

    private ListenerEventUpdate updateListener = new ListenerEventUpdate() {

        @Override
        public void onScale(float scaleFactor) {
            tvScale.setText(String.format("%s%%", (int) (scaleFactor * 100)));
        }

        @Override
        public void isOnOff_Undo_Redo(boolean isOffUndo, boolean isOffRedo) {
            tvReset.setBackgroundResource(R.mipmap.ic_reset);
            tvReset.setEnabled(isOffUndo);
            tvReset.setAlpha(isOffUndo ? 1f : 0.5f);
            tvRestore.setBackgroundResource(R.mipmap.ic_restore);
            tvRestore.setEnabled(isOffRedo);
            tvRestore.setAlpha(isOffRedo ? 1f : 0.5f);
        }

//        @Override
//        public void clickOnEditCurrentText(String text) {
//            startActivityForResult(AnnotationTextEditActivity.getTextEditIntent(AnnotationProjectEditActivity.this, text), REQUEST_CODE_EDIT_TEXT);
//        }
    };
}

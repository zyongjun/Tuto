package com.windhike.tuto;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.windhike.annotation.common.Utilities;
import com.windhike.annotation.configsapp.Configs;
import com.windhike.annotation.model.ImageDrawObject;
import com.windhike.annotation.model.ListenerEventUpdate;
import com.windhike.annotation.model.ManagerImageObject;
import com.windhike.annotation.model.PreferenceConnector;
import com.windhike.annotation.view.CustomerShapeView;
import com.windhike.fastcoding.CommonFragmentActivity;
import com.windhike.fastcoding.rx.SchedulersTransFormer;
import com.windhike.fastcoding.util.UIUtil;
import com.windhike.tuto.widget.DrawingView;
import com.windhike.tuto.widget.PopWinShare;
import com.zyongjun.easytouch.view.FloatSettingView;
import java.io.File;
import java.util.ArrayList;
import java.util.UUID;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.Observer;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import static com.windhike.tuto.fragment.AnnotationListFragment.ACTION_ANNOTATION_CHANGED;

/**
 * author:gzzyj on 2017/9/17 0017.
 * email:zhyongjun@windhike.cn
 */

public class AnnotationActivity extends CommonFragmentActivity{
    private static final String KEY_TYPE = "KEY_TYPE";
    @BindView(R.id.rlRoot)
    View vRoot;
    @BindView(R.id.drawingView)
    DrawingView drawingView;
    @BindView(R.id.drawingContent)
    RelativeLayout drawingContent;
    @BindView(R.id.ll_center)
    View llCenter;
    @BindView(R.id.tv_scale)
    TextView tvScale;
    @BindView(R.id.etEditTmp)
    EditText etEditTmp;
    private ManagerImageObject mManagerImageObject;
    private int mDrawObjectIndex;
    private String mNewDrawPath;
    private LocalBroadcastManager mLocalBroadcastManager;

    public static Intent obtainExistIntent(Context context, int drawIndex) {
        Intent intent = new Intent(context,AnnotationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Configs.KEY_ANNOTATION_DRAW_INDEX, drawIndex);
        bundle.putBoolean(CommonFragmentActivity.BUNDLE_KEY_TRANSLUCENT,true);
        bundle.putBoolean(CommonFragmentActivity.BUNDLE_KEY_FULLSCREEN,true);
        intent.putExtras(bundle);
        return intent;
    }


    public static Intent obtainNewDrawIntent(Context context, String path) {
        Intent intent = new Intent(context,AnnotationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_TYPE, 2);
        bundle.putString(Configs.KEY_ANNOTATION_DRAW_NEW_PATH, path);
        bundle.putBoolean(CommonFragmentActivity.BUNDLE_KEY_TRANSLUCENT,true);
        bundle.putBoolean(CommonFragmentActivity.BUNDLE_KEY_FULLSCREEN,true);
        intent.putExtras(bundle);
        return intent;
    }


    @Override
    public void onResume() {
        super.onResume();
        PreferenceConnector.writeBoolean(this, FloatSettingView.KEY_DRAWING_NOW,true);
    }

    @Override
    public void onStop() {
        super.onStop();
        PreferenceConnector.writeBoolean(this,FloatSettingView.KEY_DRAWING_NOW,false);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_annotation;
    }

    @Override
    protected void onLayoutCreateBefore(@Nullable Bundle savedInstanceState) {
        super.onLayoutCreateBefore(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            final String action = intent.getAction();
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (action.equals(FloatSettingView.ACTION_COLOR_CHANGED)) {
                        int colorIndex = intent.getIntExtra(action, 0);
                        drawingView.setCurrentColor(colorIndex);
                    } else if (action.equals(FloatSettingView.ACTION_SCALE_MODE_CHANGED)) {
                        boolean isScaleMode = intent.getBooleanExtra(action, true);
                        switchToZoomMode(isScaleMode);
                    } else if (action.equals(FloatSettingView.ACTION_SHAPE_CHANGED)) {
                        int shape = intent.getIntExtra(action, 0);
                        drawingView.setmModeCreateObjPaint(shape);
                    } else if (action.equals(FloatSettingView.ACTION_RESET_OPERATION)) {
                        drawingView.actionUndo();
                    } else if (action.equals(FloatSettingView.ACTION_RESTORE_OPERATION)) {
                        drawingView.actionRedo();
                    } else if (action.equals(FloatSettingView.ACTION_SAVE_ANNOTAION)) {
                        saveImageReport();
                    } else if (action.equals(FloatSettingView.ACTION_SHARE_ANNOTATION)) {
                        if(!isFinishing()) {
                            shareAnnotation();
                        }
                    } else if (action.equals(FloatSettingView.ACTION_CLOSE_DRAWING_PAGE)) {
                        if (!isFinishing()) {
                            finish();
                        }
                    } else if (action.equals(FloatSettingView.ACTION_EDIT_TEXT_MODE)) {
                        drawingView.setEditTextMode(true);
                        switchToZoomMode(false);
                    }
                }
            });

        }
    };

    public void shareAnnotation() {
        PopWinShare popWinShare = new PopWinShare(this,vRoot);
        popWinShare.setShareCallback(new PopWinShare.ShareCallback() {
            @Override
            public void onClose() {

            }

            @Override
            public Bitmap getCurrentBitmap() {
                return drawingView.scaleBitmapAfterZoomInOut();
            }
        });
        popWinShare.showShareWin();
    }

    private Handler mHandler = new Handler();

    public Observable<Integer> getSaveImageObservable() {
        return Observable.just(getProjectName())
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<String, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(String s) {
                        Bitmap bmCurrentDraw = drawingView.scaleBitmapAfterZoomInOut();
                        if (bmCurrentDraw != null) {
                            if (mDrawObjectIndex <= mManagerImageObject.getListChooseDrawObject().size() - 1) {
                                ImageDrawObject imageDrawObject = mManagerImageObject.getListChooseDrawObject().get(mDrawObjectIndex);
                                File fileEdit = new File(Utilities.getImageDrawFileNamePath(getProjectName(), String.format("%s%s", String.valueOf((imageDrawObject).getOriginalImageGalleryPath()), Configs.FLAG_EDIT_FILE_NAME)));
                                if (fileEdit.exists()) {
                                    fileEdit.delete();
                                }
                                Utilities.saveToSdcardPNG(fileEdit, bmCurrentDraw);
                                //save thumbnail
                                String mUUID_PicturePath = String.format("thumbnail%s", UUID.randomUUID().toString());
                                String imagePathSDCard = Utilities.getImageDrawFileNamePath(getProjectName(), String.valueOf(mUUID_PicturePath) + Configs.FLAG_THUMBNAIL_FILE_NAME);
                                File filedest = new File(imagePathSDCard);
                                try {
                                    if (filedest.exists()) {
                                        filedest.delete();
                                    }
                                    Bitmap bitMapSourceSave = Utilities.scaleToActualAspectRatio(bmCurrentDraw,
                                            UIUtil.DeviceInfo.getDeviceWidth(), UIUtil.dpToPx(150));
                                    Utilities.saveToSdcardPNG(filedest, bitMapSourceSave);
                                    imageDrawObject.setEditImagePath(filedest.getAbsolutePath());
                                    bitMapSourceSave.recycle();
                                } catch (Exception e) {
                                    imageDrawObject.setEditImagePath(filedest.getAbsolutePath());
                                    e.printStackTrace();
                                }
                                imageDrawObject.getListRootShape().clear();
                                imageDrawObject.getListRootShape().addAll(drawingView.getListRootShape());
                                filterShapeInCurrentBitmap();

                                bmCurrentDraw.recycle();
                                return Observable.just(1);
                            }
                            bmCurrentDraw.recycle();
                        }
                        return null;
                    }
                });
    }

    private void saveImageReport() {
        getSaveImageObservable().compose(SchedulersTransFormer.<Integer>applyExecutorSchedulers())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onCompleted() {
                        Toast.makeText(TutoApplication.getInstance(), "保存成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        finish();
                        Toast.makeText(TutoApplication.getInstance(), "保存失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(Integer result) {
//                        EventBus.getDefault().post(new AnnotationChangedEvent("update on edit finished"));
                        Intent intent = new Intent();
                        intent.setAction(ACTION_ANNOTATION_CHANGED);
                        mLocalBroadcastManager.sendBroadcast(intent);
                    }
                });
    }

    private void filterShapeInCurrentBitmap() {
        if (mManagerImageObject != null) {
            int indexCurrentDraw = mDrawObjectIndex;
            drawingView.clearFocusAllShape();
            ArrayList<CustomerShapeView> listShapeCurrent = drawingView.getListRootShape();
            ArrayList<ImageDrawObject> imageDrawObjects = mManagerImageObject.getListChooseDrawObject();
            if (imageDrawObjects.size() > 0 && indexCurrentDraw <= imageDrawObjects.size() - 1) {
                imageDrawObjects.get(indexCurrentDraw).getListRootShape().clear();
                imageDrawObjects.get(indexCurrentDraw).getListRootShape().addAll(listShapeCurrent);
                imageDrawObjects.get(indexCurrentDraw).set_undoRedoCurrentIndex(drawingView.get_undoRedoCurrentIndex());
            }
            mManagerImageObject.writeToFile(this,getProjectName());
        }
    }

    private Unbinder unbinder;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        unbinder = ButterKnife.bind(this);
        if (getIntent() != null) {
            mDrawObjectIndex = getIntent().getIntExtra(Configs.KEY_ANNOTATION_DRAW_INDEX, -1);
            mNewDrawPath = getIntent().getStringExtra(Configs.KEY_ANNOTATION_DRAW_NEW_PATH);
        }
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(FloatSettingView.ACTION_COLOR_CHANGED);
        filter.addAction(FloatSettingView.ACTION_SCALE_MODE_CHANGED);
        filter.addAction(FloatSettingView.ACTION_SHAPE_CHANGED);
        filter.addAction(FloatSettingView.ACTION_RESET_OPERATION);
        filter.addAction(FloatSettingView.ACTION_RESTORE_OPERATION);
        filter.addAction(FloatSettingView.ACTION_SHARE_ANNOTATION);
        filter.addAction(FloatSettingView.ACTION_SAVE_ANNOTAION);
        filter.addAction(FloatSettingView.ACTION_CLOSE_DRAWING_PAGE);
        filter.addAction(FloatSettingView.ACTION_EDIT_TEXT_MODE);
        mLocalBroadcastManager.registerReceiver(mReceiver, filter);
        initProject();
    }

    public void loadDrawObject() {
        if (mManagerImageObject != null) {
            ArrayList<ImageDrawObject> imageDrawObjects = mManagerImageObject.getListChooseDrawObject();
            String mImageEditPath = imageDrawObjects.get(mDrawObjectIndex).getEditImagePath();
            String mImageDisplayPath = imageDrawObjects.get(mDrawObjectIndex).getOriginalImagePath();
            boolean flagLoadListRootShape = false;
            if ((mImageEditPath != null && mImageEditPath.length() > 0) || imageDrawObjects.get(mDrawObjectIndex).getListRootShape().size() > 0) {
                flagLoadListRootShape = true;
            }
            if (new File(mImageDisplayPath).exists()) {
                Bitmap currentLoadingBitmap = Utilities.scaleToActualAspectRatio(BitmapFactory.decodeFile(mImageDisplayPath),
                        UIUtil.DeviceInfo.getDeviceWidth(),
                        UIUtil.DeviceInfo.getDeviceHeight());
                drawingView.setEdit(true);
                drawingView.setEditText(etEditTmp);
                drawingView.setListener(updateListener);
                drawingView.setwMainView(UIUtil.DeviceInfo.getDeviceWidth());
                drawingView.sethMainView(UIUtil.DeviceInfo.getDeviceHeight());
                drawingView.setMainBitmap(currentLoadingBitmap);
                if (flagLoadListRootShape) {
                    drawingView.loadListRootShape(imageDrawObjects.get(mDrawObjectIndex).getListRootShape(), imageDrawObjects.get(mDrawObjectIndex).getListRootShape().size());
                } else {
                    drawingView.clearAllRootShape();
                }
                drawingView.changeDrawingObjectPosition();
                drawingView.postInvalidate();
                switchToZoomMode(true);
            }
        } else {
            Toast.makeText(TutoApplication.getInstance(), "加载失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void switchToZoomMode(boolean isZoomMode) {
        if (isZoomMode) {
            tvScale.setVisibility(View.VISIBLE);
            tvScale.setText(String.format("%s%%", (int) (drawingView.getScaleFactor() * 100)));
            drawingView.setEnableScaleMode(true);
        } else {
            tvScale.setVisibility(View.GONE);
            drawingView.setEnableScaleMode(false);
        }
    }

    public String getProjectName() {
        return Configs.ANNOTATION_IBOS;
    }
    public void initProject() {
        this.mManagerImageObject = ManagerImageObject.readFromFile(this, Utilities.encryptFileName(getProjectName()));
        if (this.mManagerImageObject == null) {
            mManagerImageObject = new ManagerImageObject();
        }
        ImageDrawObject imageDraw = new ImageDrawObject();
        if(!TextUtils.isEmpty(mNewDrawPath)) {
            imageDraw.setOriginalImagePath(mNewDrawPath);
            mManagerImageObject.getListChooseDrawObject().add(imageDraw);
        }
        if (mDrawObjectIndex == -1) {
            mDrawObjectIndex = mManagerImageObject.getListChooseDrawObject().size() - 1;
        }
        if(mNewDrawPath==null) {
            ViewCompat.setTransitionName(drawingContent, String.format("%d_annotation", mDrawObjectIndex));
        }else{
            ViewCompat.setTransitionName(drawingContent, String.format("%s_image", mNewDrawPath));
        }
        loadDrawObject();
        if(mNewDrawPath!=null) {
            copyImageFormPath(imageDraw);
        }
    }

    public void copyImageFormPath(ImageDrawObject imageDrawObject) {
        Observable.just(imageDrawObject)
                .map(new Func1<ImageDrawObject, String>() {
                    @Override
                    public String call(ImageDrawObject imageDrawObject) {
                        String mUUID_PicturePath = new StringBuilder(String.valueOf(mNewDrawPath)).append(UUID.randomUUID().toString()).toString();
                        imageDrawObject.setOriginalImageGalleryPath(mUUID_PicturePath);
                        Utilities.createFolderProject(getProjectName());
                        File fileSource = new File(mNewDrawPath);
                        String imagePathSDCard = Utilities.getImageDrawFileNamePath(getProjectName(), new StringBuilder(String.valueOf(mUUID_PicturePath)).append(Configs.FLAG_ORIGINAL_FILE_NAME).toString());
                        File filedest = new File(imagePathSDCard);
                        try {
                            if (filedest.exists()) {
                                filedest.delete();
                            }
                            int angle = Utilities.checkDeviceAutoRotateBitmap(mNewDrawPath);
                            Bitmap bitMapSource;

                            if (angle > 0) {
                                bitMapSource = Utilities.decodeFile(true, fileSource);
                                Bitmap bitMapRotate = Utilities.RotateBitmap(bitMapSource, (float) angle);
                                Bitmap bitMapRotateSave = Utilities.scaleToActualAspectRatio(bitMapRotate,
                                        UIUtil.DeviceInfo.getDeviceWidth(), UIUtil.DeviceInfo.getDeviceHeight());
                                Utilities.saveToSdcardPNG(filedest, bitMapRotateSave);
                                bitMapRotate.recycle();
                                bitMapSource.recycle();
                                bitMapRotateSave.recycle();
                            } else {
                                bitMapSource = Utilities.decodeFile(true, fileSource);
                                Bitmap bitMapSourceSave = Utilities.scaleToActualAspectRatio(bitMapSource,
                                        UIUtil.DeviceInfo.getDeviceScreenWidth(),
                                        UIUtil.DeviceInfo.getDeviceHeight());
                                Utilities.saveToSdcardPNG(filedest, bitMapSourceSave);
                                bitMapSource.recycle();
                                bitMapSourceSave.recycle();
                            }
                            return filedest.getAbsolutePath();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }).compose(SchedulersTransFormer.<String>applyExecutorSchedulers())
                .subscribe();
    }


    private ListenerEventUpdate updateListener = new ListenerEventUpdate() {

        @Override
        public void onScale(float scaleFactor) {
            tvScale.setText(String.format("%s%%", (int) (scaleFactor * 100)));
        }

        @Override
        public void isOnOff_Undo_Redo(boolean isOffUndo, boolean isOffRedo) {
        }

        private static final int REQUEST_CODE_EDIT_TEXT = 180;
//        private static final int REQUEST_CODE_CREATE_TEXT = 181;

    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocalBroadcastManager.unregisterReceiver(mReceiver);
        unbinder.unbind();
    }
}

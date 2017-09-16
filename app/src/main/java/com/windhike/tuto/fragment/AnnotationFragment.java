package com.windhike.tuto.fragment;

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
import android.text.TextUtils;
import android.view.View;
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
import com.windhike.fastcoding.util.UIUtil;
import com.windhike.tuto.widget.DrawingView;
import com.windhike.fastcoding.base.BaseFragment;
import com.windhike.tuto.AnnotationTextEditActivity;
import com.windhike.fastcoding.CommonFragmentActivity;
import com.windhike.tuto.R;
import com.windhike.tuto.TutoApplication;
import com.windhike.tuto.event.AnnotationChangedEvent;
import com.windhike.fastcoding.rx.SchedulersTransFormer;
import com.windhike.tuto.widget.PopWinShare;
import com.zyongjun.easytouch.view.FloatSettingView;
import org.greenrobot.eventbus.EventBus;
import java.io.File;
import java.util.ArrayList;
import java.util.UUID;
import butterknife.BindView;
import rx.Observable;
import rx.Observer;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * author:gzzyj on 2017/7/26 0026.
 * email:zhyongjun@windhike.cn
 */
public class AnnotationFragment extends BaseFragment {
    private static final String KEY_TYPE = "KEY_TYPE";
    @BindView(R.id.drawingView)
    DrawingView drawingView;
    @BindView(R.id.drawingContent)
    RelativeLayout drawingContent;
    @BindView(R.id.ll_center)
    View llCenter;
    @BindView(R.id.tv_scale)
    TextView tvScale;
    @BindView(R.id.activity_annotation_edit)
    RelativeLayout activityAnnotationEdit;
    @BindView(R.id.etEditTmp)
    EditText etEditTmp;
    private ManagerImageObject mManagerImageObject;
    private int mDrawObjectIndex;
    private String mNewDrawPath;
    private LocalBroadcastManager mLocalBroadcastManager;

    public static void startExist(Context context, int drawIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(Configs.KEY_ANNOTATION_DRAW_INDEX, drawIndex);
        bundle.putBoolean(CommonFragmentActivity.BUNDLE_KEY_TRANSLUCENT,true);
        bundle.putBoolean(CommonFragmentActivity.BUNDLE_KEY_FULLSCREEN,true);
        CommonFragmentActivity.start(context, AnnotationFragment.class.getName(), bundle);
    }

    public static void startNewDraw(Context context, String path) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_TYPE, 2);
        bundle.putString(Configs.KEY_ANNOTATION_DRAW_NEW_PATH, path);
        bundle.putBoolean(CommonFragmentActivity.BUNDLE_KEY_TRANSLUCENT,true);
        bundle.putBoolean(CommonFragmentActivity.BUNDLE_KEY_FULLSCREEN,true);
        CommonFragmentActivity.start(context, AnnotationFragment.class.getName(), bundle);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDrawObjectIndex = getArguments().getInt(Configs.KEY_ANNOTATION_DRAW_INDEX, -1);
            mNewDrawPath = getArguments().getString(Configs.KEY_ANNOTATION_DRAW_NEW_PATH);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        PreferenceConnector.writeBoolean(getContext(),FloatSettingView.KEY_DRAWING_NOW,true);
    }

    @Override
    public void onStop() {
        super.onStop();
        PreferenceConnector.writeBoolean(getContext(),FloatSettingView.KEY_DRAWING_NOW,false);
    }

    @Override
    public int getLayouId() {
        return R.layout.fragment_annotation;
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
                        if(!getActivity().isFinishing()) {
                            shareAnnotation();
                        }
                    } else if (action.equals(FloatSettingView.ACTION_CLOSE_DRAWING_PAGE)) {
                        if (!getActivity().isFinishing()) {
                            getActivity().finish();
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
        PopWinShare popWinShare = new PopWinShare(getActivity(),mRootView);
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
                        getActivity().finish();
                        Toast.makeText(TutoApplication.getInstance(), "保存失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(Integer result) {
                        EventBus.getDefault().post(new AnnotationChangedEvent("update on edit finished"));
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
            mManagerImageObject.writeToFile(getActivity(),getProjectName());
        }
    }

    @Override
    public void initView() {
        super.initView();
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
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
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        this.mManagerImageObject = ManagerImageObject.readFromFile(getContext(), Utilities.encryptFileName(getProjectName()));
        if (this.mManagerImageObject == null) {
            mManagerImageObject = new ManagerImageObject();
        }
        copyImageFormPath(mNewDrawPath);

    }

    public void copyImageFormPath(String picturePath) {
        final ImageDrawObject imageDraw = new ImageDrawObject();
        Observable.just(picturePath)
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String path) {
                        if (TextUtils.isEmpty(path)) {
                            return "";
                        }
                        String mUUID_PicturePath = new StringBuilder(String.valueOf(path)).append(UUID.randomUUID().toString()).toString();
                        imageDraw.setOriginalImageGalleryPath(mUUID_PicturePath);
                        Utilities.createFolderProject(getProjectName());
                        File fileSource = new File(path);
                        String imagePathSDCard = Utilities.getImageDrawFileNamePath(getProjectName(), new StringBuilder(String.valueOf(mUUID_PicturePath)).append(Configs.FLAG_ORIGINAL_FILE_NAME).toString());
                        File filedest = new File(imagePathSDCard);
                        try {
                            if (filedest.exists()) {
                                filedest.delete();
                            }
                            int angle = Utilities.checkDeviceAutoRotateBitmap(path);
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
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        onBackPressed();
                    }

                    @Override
                    public void onNext(String path) {
                        if(!TextUtils.isEmpty(path)) {
                            imageDraw.setOriginalImagePath(path);
                            mManagerImageObject.getListChooseDrawObject().add(imageDraw);
                        }
                        if (mDrawObjectIndex == -1) {
                            mDrawObjectIndex = mManagerImageObject.getListChooseDrawObject().size() - 1;
                        }
                        loadDrawObject();
                    }
                });


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

        @Override
        public void clickOnEditCurrentText(String text) {
            startActivityForResult(AnnotationTextEditActivity.getTextEditIntent(getContext(), text), REQUEST_CODE_EDIT_TEXT);
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mLocalBroadcastManager.unregisterReceiver(mReceiver);
    }
}

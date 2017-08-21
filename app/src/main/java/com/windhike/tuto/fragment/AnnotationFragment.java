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
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.windhike.annotation.common.Utilities;
import com.windhike.annotation.configsapp.Configs;
import com.windhike.annotation.model.ImageDrawObject;
import com.windhike.annotation.model.ListenerEventUpdate;
import com.windhike.annotation.model.ManagerImageObject;
import com.windhike.annotation.reuse.DisplayUtil;
import com.windhike.annotation.view.CustomerShapeView;
import com.windhike.annotation.view.DrawingView;
import com.windhike.tuto.AnnotationTextEditActivity;
import com.windhike.tuto.BaseFragment;
import com.windhike.tuto.CommonFragmentActivity;
import com.windhike.tuto.R;
import com.windhike.tuto.TutoApplication;
import com.windhike.tuto.event.AnnotationChangedEvent;
import com.windhike.tuto.reuse.SchedulersTransFormer;
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
public class AnnotationFragment extends BaseFragment{
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
    private ManagerImageObject mManagerImageObject;
    private int mDrawObjectIndex;
    private String mNewDrawPath;
    private LocalBroadcastManager mLocalBroadcastManager;

    public static void startExist(Context context, int drawIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(Configs.KEY_ANNOTATION_DRAW_INDEX, drawIndex);
        CommonFragmentActivity.start(context, AnnotationFragment.class.getName(), bundle);
    }

    public static void startNewDraw(Context context, String path) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_TYPE, 2);
        bundle.putString(Configs.KEY_ANNOTATION_DRAW_NEW_PATH, path);
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
                        Log.e(TAG, "run: ==========color=======" + colorIndex);
//                        onColorChanged(colorIndex);
                    } else if (action.equals(FloatSettingView.ACTION_SCALE_MODE_CHANGED)) {
                        boolean isScaleMode = intent.getBooleanExtra(action, true);
                        Log.e(TAG, "run: =====isScaleMode======" + isScaleMode);
                        switchToZoomMode(isScaleMode);
                    } else if (action.equals(FloatSettingView.ACTION_SHAPE_CHANGED)) {
                        int shape = intent.getIntExtra(action, 0);
                            Log.e(TAG, "run: =======shape======="+shape );
                        drawingView.setmModeCreateObjPaint(shape);
                    } else if (action.equals(FloatSettingView.ACTION_RESET_OPERATION)) {
                        drawingView.actionUndo();
                    } else if (action.equals(FloatSettingView.ACTION_RESTORE_OPERATION)) {
                        drawingView.actionRedo();
                    } else if (action.equals(FloatSettingView.ACTION_SAVE_ANNOTAION)) {
                        saveImageReport();
                    }
                }
            });

        }
    };
    public static final String TAG = "AnnotationFragment";

    private Handler mHandler = new Handler();

    private void saveImageReport() {
        Observable.just(getProjectName())
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
                                            TutoApplication.getInstance().getDisplayMetrics().widthPixels, DisplayUtil.dip2px(TutoApplication.getInstance(), 150));
                                    Utilities.saveToSdcardPNG(filedest, bitMapSourceSave);
                                    imageDrawObject.setEditImagePath(filedest.getAbsolutePath());
                                    bitMapSourceSave.recycle();
                                } catch (Exception e) {
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
                }).compose(SchedulersTransFormer.<Integer>applyExecutorSchedulers())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onCompleted() {
                        getActivity().finish();
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
        filter.addAction(FloatSettingView.ACTION_SAVE_ANNOTAION);
        mLocalBroadcastManager.registerReceiver(mReceiver, filter);
        initProject();
        drawingContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                drawingContent.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                loadDrawObject();
            }
        });
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
                        TutoApplication.getInstance().getDisplayMetrics().widthPixels,
                        TutoApplication.getInstance().getDisplayMetrics().heightPixels);
                drawingView.setEdit(true);
                drawingView.setListener(updateListener);
                drawingView.setwMainView(TutoApplication.getInstance().getDisplayMetrics().widthPixels);
                drawingView.sethMainView(TutoApplication.getInstance().getDisplayMetrics().heightPixels);
                drawingView.setMainBitmap(currentLoadingBitmap);
                if (flagLoadListRootShape) {
                    drawingView.loadListRootShape(imageDrawObjects.get(mDrawObjectIndex).getListRootShape(), imageDrawObjects.get(mDrawObjectIndex).getListRootShape().size());
                } else {
                    drawingView.clearAllRootShape();
                }
                drawingView.changeDrawingObjectPosition();
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
        if (mNewDrawPath != null) {
            copyImageFormPath(mNewDrawPath);
        }
        if (mDrawObjectIndex == -1) {
            mDrawObjectIndex = mManagerImageObject.getListChooseDrawObject().size() - 1;
        }
    }

    public void copyImageFormPath(String picturePath) {
        String mUUID_PicturePath = new StringBuilder(String.valueOf(picturePath)).append(UUID.randomUUID().toString()).toString();
        ImageDrawObject imageDraw = new ImageDrawObject();
        imageDraw.setOriginalImageGalleryPath(mUUID_PicturePath);
        Utilities.createFolderProject(getProjectName());
        File fileSource = new File(picturePath);
        String imagePathSDCard = Utilities.getImageDrawFileNamePath(getProjectName(), new StringBuilder(String.valueOf(mUUID_PicturePath)).append(Configs.FLAG_ORIGINAL_FILE_NAME).toString());
        File filedest = new File(imagePathSDCard);
        try {
            if (filedest.exists()) {
                filedest.delete();
            }
            int angle = Utilities.checkDeviceAutoRotateBitmap(picturePath);
            Bitmap bitMapSource;
            if (angle > 0) {
                bitMapSource = Utilities.decodeFile(true, fileSource);
                Bitmap bitMapRotate = Utilities.RotateBitmap(bitMapSource, (float) angle);
                Bitmap bitMapRotateSave = Utilities.scaleToActualAspectRatio(bitMapRotate,
                        TutoApplication.getInstance().getDisplayMetrics().widthPixels, TutoApplication.getInstance().getDisplayMetrics().heightPixels);
                Utilities.saveToSdcardPNG(filedest, bitMapRotateSave);
                bitMapRotate.recycle();
                bitMapSource.recycle();
                bitMapRotateSave.recycle();
            } else {
                bitMapSource = Utilities.decodeFile(true, fileSource);
                Bitmap bitMapSourceSave = Utilities.scaleToActualAspectRatio(bitMapSource,
                        TutoApplication.getInstance().getDisplayMetrics().widthPixels,
                        TutoApplication.getInstance().getDisplayMetrics().heightPixels);
                Utilities.saveToSdcardPNG(filedest, bitMapSourceSave);
                bitMapSource.recycle();
                bitMapSourceSave.recycle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        imageDraw.setOriginalImagePath(filedest.getAbsolutePath());
        mManagerImageObject.getListChooseDrawObject().add(imageDraw);
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
        private static final int REQUEST_CODE_CREATE_TEXT = 181;

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

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
import com.windhike.annotation.view.DrawingView;
import com.windhike.tuto.AnnotationTextEditActivity;
import com.windhike.tuto.BaseFragment;
import com.windhike.tuto.CommonFragmentActivity;
import com.windhike.tuto.R;
import com.windhike.tuto.TutoApplication;
import com.zyongjun.easytouch.view.FloatSettingView;
import java.io.File;
import java.util.ArrayList;
import java.util.UUID;
import butterknife.BindView;

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
                    }
                }
            });

        }
    };
    public static final String TAG = "AnnotationFragment";

    private Handler mHandler = new Handler();

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
//            tvScale.setBackgroundResource(R.mipmap.ic_draw_scale_selected);
        } else {
            tvScale.setVisibility(View.GONE);
            drawingView.setEnableScaleMode(false);
        }
//        tvDrawShape.setSelected(false);
//        tvDrawShapeIndicate.setSelected(false);
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
//            mFloatControlView.unDoRedo(isOffUndo, isOffRedo);
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

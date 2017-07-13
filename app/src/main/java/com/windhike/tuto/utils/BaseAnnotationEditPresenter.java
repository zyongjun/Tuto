package com.windhike.tuto.utils;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.windhike.annotation.configsapp.Configs;
import com.windhike.annotation.model.ManagerImageObject;
import com.windhike.annotation.model.PreferenceConnector;
import com.windhike.mvputils.BasePresenter;
import com.windhike.tuto.TutoApplication;
import com.windhike.tuto.view.IAnnotationEditView;

/**
 * author: gzzyj on 2017/3/23.
 * email:zhyongjun@windhike.cn
 */

public abstract class BaseAnnotationEditPresenter extends BasePresenter<IAnnotationEditView> {
    protected ManagerImageObject mManagerObject;
    protected int mDrawObjectIndex;
    private static final String TAG = "BaseAnnotationEditPrese";

    public BaseAnnotationEditPresenter(Bundle bundle) {
        mDrawObjectIndex = bundle.getInt(Configs.KEY_ANNOTATION_DRAW_INDEX,0);
        Log.e(TAG, "BaseAnnotationEditPresenter:--------editindex :"+mDrawObjectIndex );
    }

    public String getProjectName() {
        return Configs.ANNOTATION_IBOS;
    }

    public int getDrawObjectIndex() {
        return mDrawObjectIndex;
    }

    @Override
    public void attach(IAnnotationEditView view) {
        super.attach(view);

    }

    public void saveSynctime(long time) {
        PreferenceConnector.writeLong(TutoApplication.getInstance(),Configs.FLAG_SYSNC_ANNOTATION_TIME,time);
    }

    public void saveManageImageObject() {
        mManagerObject.writeToFile(mView.getViewContext(),getProjectName());
    }

    public void deleteProject() {
//        try {
//            String storeprojectNameDelete = AESHelper.encrypt(Configs.ENCRYPT_KEY, Configs.ANNOTATION_IBOS);
//            File[] fs = Utilities.filterDataFiles(mView.getViewContext().getFilesDir().listFiles());
//            for (File f : fs) {
//                try {
//                    String projectName = Utilities.getFileName(f.getAbsoluteFile().getAbsolutePath());
//                    if (storeprojectNameDelete.equals(projectName)) {
//                        f.delete();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            ExternalStorage.getObj();
//            ExternalStorage.isDeleteForder("iAnnotation/" + storeprojectNameDelete);
//        } catch (Exception e1) {
//            e1.printStackTrace();
//        }
    }

    public void exitWithoutSave() {

    }

    public int getOperationType() {
        int annotationAction = DrawingShapeTransformer.ADD_ANNOTATION;
        if (!TextUtils.isEmpty(getManagerObject().getListChooseDrawObject().get(mDrawObjectIndex).getRemarkId())) {
            annotationAction = DrawingShapeTransformer.UPDATE_ANNOTATION;
        }
        return annotationAction;
    }

    public ManagerImageObject getManagerObject(){
        return mManagerObject;
    }

    public void finish() {
        if(mView!=null) {
            Activity activity = (Activity) mView.getViewContext();
            activity.finish();
            Toast.makeText(TutoApplication.getInstance(), "保存成功", Toast.LENGTH_SHORT).show();
        }
    }
}

package com.windhike.tuto.utils;

import android.graphics.Bitmap;
import android.os.Bundle;

import com.windhike.annotation.common.Utilities;
import com.windhike.annotation.configsapp.Configs;
import com.windhike.annotation.model.ImageDrawObject;
import com.windhike.annotation.model.ManagerImageObject;
import com.windhike.annotation.reuse.DisplayUtil;
import com.windhike.fastcoding.util.UIUtil;
import com.windhike.tuto.TutoApplication;
import com.windhike.tuto.view.IAnnotationEditView;

import java.io.File;
import java.util.UUID;

/**
 * author: gzzyj on 2017/3/2.
 * email:zhyongjun@windhike.cn
 */

public class AnnotationEditNewDrawPresenter extends BaseAnnotationEditPresenter {
    private String path;
    public AnnotationEditNewDrawPresenter(Bundle bundle) {
        super(bundle);
        path = bundle.getString(Configs.KEY_ANNOTATION_DRAW_NEW_PATH);
    }

    @Override
    public void attach(IAnnotationEditView view) {
        super.attach(view);
        mView.showWaitingDialog(mView.getViewContext());
        initProject();
        mView.loadDrawObject();
        mView.dismissOpDialog();

    }

    @Override
    public void deleteProject() {
        super.deleteProject();
        if(mManagerObject != null) {
            mManagerObject.getListChooseDrawObject().remove(mDrawObjectIndex);
            mManagerObject.writeToFile(mView.getViewContext(), getProjectName());
        }
    }

    @Override
    public void exitWithoutSave() {
        deleteProject();
    }

    @Override
    public String getProjectName() {
        return Configs.ANNOTATION_IBOS;
//        return Configs.ANNOTATION_IBOS_TEMP;
    }

    public void initProject() {
        this.mManagerObject = ManagerImageObject.readFromFile(mView.getViewContext(), Utilities.encryptFileName(getProjectName()));
        if (this.mManagerObject == null) {
            mManagerObject = new ManagerImageObject();
        }
        copyImageFormPath(path);
        mDrawObjectIndex = mManagerObject.getListChooseDrawObject().size() - 1;
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
                        UIUtil.DeviceInfo.getDeviceWidth(), UIUtil.DeviceInfo.getDeviceHeight());
                Utilities.saveToSdcardPNG(filedest, bitMapRotateSave);
                bitMapRotate.recycle();
                bitMapSource.recycle();
                bitMapRotateSave.recycle();
            } else {
                bitMapSource = Utilities.decodeFile(true, fileSource);
                Bitmap bitMapSourceSave = Utilities.scaleToActualAspectRatio(bitMapSource,
                        UIUtil.DeviceInfo.getDeviceWidth(),
                        UIUtil.DeviceInfo.getDeviceHeight()- UIUtil.dpToPx(110));
                Utilities.saveToSdcardPNG(filedest, bitMapSourceSave);
                bitMapSource.recycle();
                bitMapSourceSave.recycle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        imageDraw.setOriginalImagePath(filedest.getAbsolutePath());
        this.mManagerObject.getListChooseDrawObject().add(imageDraw);
    }

}

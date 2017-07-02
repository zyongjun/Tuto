package com.windhike.tuto.utils;

import android.os.Bundle;

import com.windhike.annotation.common.Utilities;
import com.windhike.annotation.model.ManagerImageObject;
import com.windhike.tuto.view.IAnnotationEditView;


/**
 * author: gzzyj on 2017/3/2.
 * email:zhyongjun@windhike.cn
 */

public class AnnotationEditPresenter extends BaseAnnotationEditPresenter {

    public AnnotationEditPresenter(Bundle bundle) {
        super(bundle);
    }

    @Override
    public void attach(IAnnotationEditView view) {
        super.attach(view);
        mManagerObject = ManagerImageObject.readFromFile(mView.getViewContext(), Utilities.encryptFileName(getProjectName()));
    }

    @Override
    public void deleteProject() {
        super.deleteProject();
    }
}

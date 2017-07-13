package com.windhike.tuto.utils;

import android.app.Activity;
import android.view.View;

import com.windhike.tuto.AnnotationPreviewActivity;
import com.windhike.tuto.AnnotationProjectEditActivity;
import com.windhike.tuto.R;
import com.windhike.tuto.view.IBaseRecordsView;

/**
 * author: gzzyj on 2017/3/2.
 * email:zhyongjun@windhike.cn
 */

public class AnnotationRecordsPresenter extends BaseAnnotationRecordsPresenter<AnnotationRecordsPresenter.IAnnotationRecordsView> {


    public AnnotationRecordsPresenter() {
        registViewTemplate(0,AnnotationRecordHolder.class);
    }

    @Override
    public View.OnClickListener itemClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag(R.id.position_id);
                try {
                    Activity activity = (Activity)mView.getViewContext();
                    activity.startActivity(AnnotationProjectEditActivity.getAnnotationProjectIntent(activity,position));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    public void initHead() {
        mView.getToolbarBuilder().showLeft(false)
                .withBack(true)
                .withTitle("批注记录")
                .showRight(false)
                .show();
    }

    public interface IAnnotationRecordsView extends IBaseRecordsView {

    }
}

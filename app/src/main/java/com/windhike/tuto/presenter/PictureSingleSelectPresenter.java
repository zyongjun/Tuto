package com.windhike.tuto.presenter;

import android.app.Activity;
import android.content.Intent;
import android.widget.BaseAdapter;

import com.windhike.tuto.view.IBaseSelectPictureView;

/**
 * author: zyongjun on 2017/7/1 0001.
 * email: zhyongjun@windhike.cn
 */

public class PictureSingleSelectPresenter extends BasePictureSelectPresenter<PictureSingleSelectPresenter.ISelectSinglePictureView>{
    public interface ISelectSinglePictureView extends IBaseSelectPictureView {
        void initSingleSelectCurrentFolderUI(PictureSingleSelectPresenter presenter, BaseAdapter adapter);
        void hideBottomUI();
    }

    @Override
    public void onPictureClick(int position) {
        Activity activity = (Activity) mView.getViewContext();
        Intent preview = new Intent();
        preview.putExtra(Intent.EXTRA_RETURN_RESULT, mCurrentStateList.get(position));
        activity.setResult(Activity.RESULT_OK, preview);
        activity.finish();
    }

    @Override
    public void initCurrentFolderUI() {
        mView.initSingleSelectCurrentFolderUI(this,new PictureSingleSelectAdapter(this));
    }

    @Override
    public void initBottomUI() {
        mView.hideBottomUI();
    }
}

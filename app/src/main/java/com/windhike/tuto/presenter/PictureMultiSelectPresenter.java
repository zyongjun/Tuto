package com.windhike.tuto.presenter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.widget.CompoundButton;
import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.widget.CompoundButton;

import com.windhike.annotation.model.SaveSelectState;
import com.windhike.tuto.view.IBaseSelectPictureView;
import java.util.ArrayList;

/**
 * author: zyongjun on 2017/7/1 0001.
 * email: zhyongjun@windhike.cn
 */

public class PictureMultiSelectPresenter extends BasePictureSelectPresenter<PictureMultiSelectPresenter.ISelectMultiPictureView>{
    private static final String KEY_SEND_ORIGIN = "sendOrigin";
    private int mSelectCount;

    public void setSelectCount(int selectCount) {
        mSelectCount = selectCount;
    }

    public int getSelectCount() {
        return mSelectCount;
    }

    @Override
    public void initCurrentFolderUI() {
        mSelectCount = 0;
        mView.initMultiCurrentFolderUI(this);
    }

    public interface ISelectMultiPictureView extends IBaseSelectPictureView {
        void initMultiCurrentFolderUI(PictureMultiSelectPresenter presenter);
        void onPictureSelected(PictureMultiSelectPresenter presenter,int position,boolean isChecked,CompoundButton buttonView);
        void updateBottomUI(PictureMultiSelectPresenter presenter);
    }

    public void onPictureSelected(int position,boolean isChecked,CompoundButton buttonView){
        mView.onPictureSelected(this,position,isChecked,buttonView);
    }

    public void caculateSelectCount() {
        int selectCount = 0;
        for (SaveSelectState saveSelectState : getCurrentStateList()) {
            if(saveSelectState.isSelect()){
                selectCount ++;
            }
        }
        setSelectCount(selectCount);
    }

    @Override
    public void onPictureClick(int position) {
//        mIsSendOriginOnPreview = false;
//        Intent preview = new Intent(mView.getViewContext(), PreviewSelectPictureAty.class);
//        preview.putParcelableArrayListExtra("ArrayList", (ArrayList<? extends Parcelable>) getCurrentStateList());
//        preview.putExtra("preview_position",position);
//        preview.putExtra("select_size",getSelectCount());
//        ((Activity)mView.getViewContext()).startActivityForResult(preview, CODE_REQUEST_PREVIEW_PICTURES);
    }

    @Override
    public void sendSelectImages(boolean sendOrigin) {
        ArrayList<Uri> selectList = new ArrayList<>();
        for (SaveSelectState saveSelectState : getCurrentStateList()) {
            if(saveSelectState.isSelect()){
                selectList.add(Uri.parse(String.format("file://%s", saveSelectState.getPath())));
            }
        }
        Activity activity = (Activity) mView.getViewContext();
        if(selectList.size()>0){
            Intent preview = new Intent();
            preview.putExtra(KEY_SEND_ORIGIN, sendOrigin);
            preview.putExtra(Intent.EXTRA_RETURN_RESULT, selectList);
            activity.setResult(Activity.RESULT_OK, preview);
            activity.finish();
        } else {
//            Tools.openToastShort(IBOSApp.getInstance(),activity.getString(io.rong.imkit.R.string.rc_notice_select_one_picture_at_last));
        }
    }

    private static final int RESULT_CODE_SELECT_ANNOTATION = 10;
    @Override
    public void clickOnAnnotation() {
        ArrayList<SaveSelectState> selectList = new ArrayList<>();
        for (SaveSelectState saveSelectState : getCurrentStateList()) {
            if(saveSelectState.isSelect()){
                selectList.add(saveSelectState);
            }
        }
        if(selectList.size()==1) {
            Activity activity = (Activity) mView.getViewContext();
            Intent intent = new Intent();
            intent.putExtra("BACK_RESULT", selectList);
            activity.setResult(RESULT_CODE_SELECT_ANNOTATION, intent);
            activity.finish();
        }
    }
}

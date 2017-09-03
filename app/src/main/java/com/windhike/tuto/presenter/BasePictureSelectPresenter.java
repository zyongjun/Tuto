package com.windhike.tuto.presenter;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.windhike.annotation.model.SaveSelectState;
import com.windhike.mvputils.BasePresenter;
import com.windhike.tuto.model.ImageFolder;
import com.windhike.fastcoding.rx.SchedulersTransFormer;
import com.windhike.tuto.reuse.ToolbarBuilder;
import com.windhike.tuto.view.IBaseSelectPictureView;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;

/**
 * author: zyongjun on 2017/7/1 0001.
 * email: zhyongjun@windhike.cn
 */

public abstract class BasePictureSelectPresenter<T extends IBaseSelectPictureView> extends BasePresenter<T> {
    private static final int EVENT_UPDATE_BOTTOM_UI = 3;
    protected static final int CODE_REQUEST_PREVIEW_PICTURES = 2;
    private static final int CODE_REQUEST_CHANGE_PICTURE_DIRS = 1;
    protected List<SaveSelectState> mPictureStateList = new ArrayList<>();
    protected List<SaveSelectState> mCurrentStateList = new ArrayList<>();
    protected List<ImageFolder> mImageFolderList = new ArrayList<>();
    private int mSelectedImageFolder;
    protected boolean mIsSendOriginOnPreview;
    public void initPictureAndFolders() {
        mView.showWaitingDialog(mView.getViewContext());
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                try {
                    mPictureStateList.clear();
                    mPictureStateList.addAll(getThumb());
                    mImageFolderList.clear();
                    mImageFolderList.addAll(getImageFolders());
                    Collections.reverse(mPictureStateList);
                    if(!subscriber.isUnsubscribed()) {
                        subscriber.onNext(1);
                        subscriber.onCompleted();
                    }
                } catch (Exception e) {
                    if(!subscriber.isUnsubscribed()) {
                        subscriber.onError(new Exception("error happened on fetch galleries"));
                    }
                }
            }
        }).compose(SchedulersTransFormer.<Integer>applyIoSchedulers())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onCompleted() {
                        if (mView != null) {
                            mView.dismissOpDialog();
                            initCurrentFolderData();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.dismissOpDialog();
                            initCurrentFolderData();
                        }
                    }

                    @Override
                    public void onNext(Integer integer) {
                    }
                });

    }

    public void initHead() {
        mView.getToolbarBuilder()
                .showLeft(false)
                .withBack(true)
                .withLeftClick(new ToolbarBuilder.OnClickLeft() {
                    @Override
                    public void onClickLeft() {
//                        Intent intent = new Intent(mView.getViewContext(), CameraRollAty.class);
//                        Bundle bundle = new Bundle();
//                        bundle.putSerializable("folders", (Serializable)getImageFolderList());
//                        intent.putExtras(bundle);
//                        ((Activity)mView.getViewContext()).startActivityForResult(intent,CODE_REQUEST_CHANGE_PICTURE_DIRS);
//                        ((Activity)mView.getViewContext()).overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                    }
                }).withRightClick(new ToolbarBuilder.OnClickRight() {
            @Override
            public void onClickRight() {
                ((Activity)mView.getViewContext()).setResult(Activity.RESULT_CANCELED);
                ((Activity)mView.getViewContext()).finish();
            }
        }).show();

    }

    public void initBottomUI() {

    }

    public void setSendOriginOnPreview(boolean isSendOriginOnPreview) {
        mIsSendOriginOnPreview = isSendOriginOnPreview;
    }

    public boolean isSendOriginOnPreview() {
        return mIsSendOriginOnPreview;
    }

    public void updateSelectedImageFolder(int selectedImageFolderIndex) {
        mSelectedImageFolder = selectedImageFolderIndex;
    }

    public int getSelectedImageFolderIndex() {
        return mSelectedImageFolder;
    }

    public void initCurrentFolderData(){
        if(mCurrentStateList != null){
            mCurrentStateList.clear();
        }

        ImageFolder folder = mImageFolderList.get(mSelectedImageFolder);
        String folderPath = folder.getTopImagePath();
        if(mSelectedImageFolder == 0) {
            mCurrentStateList = new ArrayList<>();
            mCurrentStateList.addAll(mPictureStateList);
        }else{
            for (SaveSelectState saveSelectState : mPictureStateList) {
                saveSelectState.setIsSelect(false);
                if(folderPath.equals(saveSelectState.getFolderPath())){
                    mCurrentStateList.add(saveSelectState);
                }
            }
        }
        initCurrentFolderUI();
    }

    public void caculateSelectCount() {

    }

    public void onSelectedFolderResult(int resultCode) {
        if(resultCode < getImageFolderList().size() && resultCode > 0) {
            if(getSelectedImageFolderIndex() != resultCode-1) {
                updateSelectedImageFolder(resultCode-1);
                initCurrentFolderData();
                mView.notifyDataSetChanged();
                mView.sendEmptyMessage(EVENT_UPDATE_BOTTOM_UI);
            }
        }
    }

    public ImageFolder getCurrentFolder() {
        return mImageFolderList.get(mSelectedImageFolder);
    }

    public void initCurrentFolderUI() {

    }

    public void onPictureClick(int position) {

    }

    public List<SaveSelectState> getCurrentStateList() {
        return mCurrentStateList;
    }

    public void setCurrentStateList(List<SaveSelectState> currentStateList) {
        mCurrentStateList = currentStateList;
    }

    public List<ImageFolder> getImageFolderList() {
        return mImageFolderList;
    }

    public List<SaveSelectState> getThumb() {
        ArrayList<SaveSelectState> uris = new ArrayList();
        Cursor cursor = MediaStore.Images.Media.query(mView.getViewContext().getContentResolver(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{"_id",MediaStore.Images.Media.DATA},null, MediaStore.Images.ImageColumns.DATE_TAKEN);
        if(cursor != null) {
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    Uri uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cursor.getString(0));
                    String path = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));
                    uris.add(new SaveSelectState(uri,false,path));
                } while(cursor.moveToNext());
            }
            cursor.close();
        }
        return uris;
    }

    public List<ImageFolder> getImageFolders(){
        List<ImageFolder> imageFolders = new ArrayList<>();
        ImageFolder allImageFolder = new ImageFolder();
        allImageFolder.setFolderName("所有照片");
        int size = mPictureStateList.size();
        allImageFolder.setImageCounts(size);
        if(size > 0) {
            allImageFolder.setAblumPath(mPictureStateList.get(0).getPath());
        }
        imageFolders.add(allImageFolder);
        String parentName = null;
        String tmpParentName = null;
        int tmpCount = 0;
//        String ignore = "0";
        ImageFolder folder = null;
        for (SaveSelectState info : mPictureStateList) {
            File newFile = new File(info.getPath());
            File file = newFile.getParentFile();
            if(file!=null&&file.exists()){
                parentName = file.getName();
                info.setFolderPath(file.getAbsolutePath());
//                if(!ignore.equals(parentName)){
                if (!parentName.equals(tmpParentName)) {
                    tmpParentName = parentName;
                    folder = new ImageFolder();
                    folder.setTopImagePath(file.getAbsolutePath());
                    folder.setFolderName(parentName);
                    tmpCount = 1;
                    if (file.list() == null) {
                        folder.setImageCounts(0);
                    }else {
                        folder.setImageCounts(file.list().length);
                    }
                    folder.setAblumPath(info.getPath());
                    imageFolders.add(folder);
                    continue;
                }
                tmpCount ++;
                folder.setImageCounts(tmpCount);
//                }
            }
        }
        return  imageFolders;
    }

    public void sendSelectImages(boolean sendOrigin) {

    }

    public void clickOnAnnotation() {

    }
}

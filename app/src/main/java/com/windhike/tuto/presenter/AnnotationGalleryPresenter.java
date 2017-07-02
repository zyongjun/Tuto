package com.windhike.tuto.presenter;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import com.windhike.annotation.configsapp.Configs;
import com.windhike.annotation.model.SaveSelectState;
import com.windhike.mvputils.BaseRecyclerPresenter;
import com.windhike.tuto.AnnotationProjectEditActivity;
import com.windhike.tuto.R;
import com.windhike.tuto.reuse.IBaseView;
import com.windhike.tuto.widget.PreviewHolder;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;

/**
 * author: gzzyj on 2017/3/22.
 * email:zhyongjun@windhike.cn
 */

public class AnnotationGalleryPresenter extends BaseRecyclerPresenter<AnnotationGalleryPresenter.IAnnotationGalleryView> {
    ArrayList<SaveSelectState> mPictures = new ArrayList<>();
    public AnnotationGalleryPresenter() {
        registViewTemplate(1,PreviewHolder.class);
    }

    public ArrayList<SaveSelectState> getPictures() {
        return mPictures;
    }

    public void initPictures() {
        if (mView == null) {
            return;
        }
        mView.showWaitingDialog(mView.getViewContext());

        Observable.create(new Observable.OnSubscribe<List<SaveSelectState>>() {
            @Override
            public void call(Subscriber<? super List<SaveSelectState>> subscriber) {
                mPictures.clear();
                List<SaveSelectState> list = getThumb();
                Collections.reverse(list);
                if(!subscriber.isUnsubscribed()){
                    subscriber.onNext(list);
                }
            }
        }).subscribe(new Observer<List<SaveSelectState>>() {
            @Override
            public void onCompleted() {
                if (mView != null) {
                    mView.dismissOpDialog();
                }
            }

            @Override
            public void onError(Throwable e) {
                if (mView != null) {
                    mView.dismissOpDialog();
                }
            }

            @Override
            public void onNext(List<SaveSelectState> saveSelectStates) {
                mPictures.addAll(saveSelectStates);
                if (mView != null) {
                    mView.dismissOpDialog();
                    mView.notifyDataChanged();
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return mPictures.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    public View.OnClickListener obtainItemClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag(R.id.position_id);
//                Bundle bundle = new Bundle();
//                bundle.putParcelable(AnnotationPreviewFragment.KEY_IMAGE_STATE,getPictures().get(position));
//                AnnotationSelectPicturePresenter presenter = new AnnotationSelectPicturePresenter(bundle);
//                presenter.attach(mView);
//                presenter.initProject();
                Activity activity = (Activity) mView.getViewContext();
                activity.startActivity(AnnotationProjectEditActivity.getAnnotationNewDrawIntent(activity,mPictures.get(position).getPath()));
//                presenter.detach();
            }
        };
    }

    private List<SaveSelectState> getThumb() {
        ArrayList<SaveSelectState> uris = new ArrayList();
        Cursor cursor = MediaStore.Images.Media.query(mView.getViewContext().getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{"_id", MediaStore.Images.Media.DATA},null, MediaStore.Images.ImageColumns.DATE_TAKEN+" DESC");
        if(cursor != null) {
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    Uri uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cursor.getString(0));
                    String path = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));
                    File fileSource = new File(path);
                    if (!path.contains(Configs.ROOT_FOLDER_NAME) && fileSource.exists() && fileSource.length() > 0) {
                        uris.add(new SaveSelectState(uri, false, path));
                    }
                    if (uris.size() > 15) {
                        break;
                    }
                } while(cursor.moveToNext());
            }
            cursor.close();
        }
        Collections.reverse(uris);
        return uris;
    }

    public interface IAnnotationGalleryView extends IBaseView {
        void notifyDataChanged();
    }


}

package com.windhike.tuto.fragment;

import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;

import com.windhike.annotation.model.SaveSelectState;
import com.windhike.fastcoding.base.BaseFragment;
import com.windhike.tuto.AnnotationActivity;
import com.windhike.tuto.R;
import com.windhike.tuto.presenter.AlbumAdapter;
import com.windhike.fastcoding.rx.SchedulersTransFormer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import butterknife.BindView;
import rx.Observable;
import rx.Subscriber;

/**
 * author:gzzyj on 2017/7/20 0020.
 * email:zhyongjun@windhike.cn
 */
public class AlbumListFragment extends BaseFragment implements AlbumAdapter.AlbumCallback{
    @BindView(R.id.recycerView)
    RecyclerView recycerView;
    private AlbumAdapter mAdapter;

    @Override
    public int getLayouId() {
        return R.layout.layout_recycler;
    }

    @Override
    public void initView() {
        super.initView();
        GridLayoutManager manager = new GridLayoutManager(getActivity(),2);
        recycerView.setLayoutManager(manager);
        mAdapter = new AlbumAdapter(this);
        recycerView.setAdapter(mAdapter);
        getActivity().getContentResolver().registerContentObserver(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true,observer);
        loadData();
    }

    private ContentObserver observer = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            loadData();
        }
    };

    public void loadData() {
        Observable.create(new Observable.OnSubscribe<List<SaveSelectState>>() {
            @Override
            public void call(Subscriber<? super List<SaveSelectState>> subscriber) {
                List<SaveSelectState> photos = getThumb();
                Log.e(TAG, "call: ====="+photos.size() );
                subscriber.onNext(photos);
                subscriber.onCompleted();
            }
        }).compose(SchedulersTransFormer.<List<SaveSelectState>>applyIoSchedulers()).subscribe(new Subscriber<List<SaveSelectState>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<SaveSelectState> saveSelectStates) {
                mAdapter.updateData(saveSelectStates);
            }
        });
    }

    @Override
    public void onDestroyView() {
        getActivity().getContentResolver().unregisterContentObserver(observer);
        super.onDestroyView();
    }

    private static final String TAG = "AlbumListFragment";
    public List<SaveSelectState> getThumb() {
        ArrayList<SaveSelectState> uris = new ArrayList();
        Cursor cursor = MediaStore.Images.Media.query(getActivity().getContentResolver(),
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
            Collections.reverse(uris);
        }
        return uris;
    }

    @Override
    public void onItemClick(View shareView,String path) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                    new Pair(shareView, String.format("%s_image", path))
            );
            setSharedElementReturnTransition(TransitionInflater.from(
                    getActivity()).inflateTransition(R.transition.change_image_transion));
            setExitTransition(TransitionInflater.from(
                    getActivity()).inflateTransition(android.R.transition.fade));
            getActivity().startActivity(AnnotationActivity.obtainNewDrawIntent(getActivity(),path),options.toBundle());
        }else {
            getActivity().startActivity(AnnotationActivity.obtainNewDrawIntent(getActivity(),path));
        }
    }
}



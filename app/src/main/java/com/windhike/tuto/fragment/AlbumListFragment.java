package com.windhike.tuto.fragment;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import com.windhike.annotation.model.SaveSelectState;
import com.windhike.tuto.BaseFragment;
import com.windhike.tuto.R;
import com.windhike.tuto.presenter.AlbumAdapter;
import com.windhike.tuto.reuse.SchedulersTransFormer;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import rx.Observable;
import rx.Subscriber;

/**
 * author:gzzyj on 2017/7/20 0020.
 * email:zhyongjun@windhike.cn
 */
public class AlbumListFragment extends BaseFragment{
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
        mAdapter = new AlbumAdapter();
        recycerView.setAdapter(mAdapter);
        loadData();
    }

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
        }
        return uris;
    }
}



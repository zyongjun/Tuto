package com.windhike.tuto.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.windhike.annotation.common.Utilities;
import com.windhike.annotation.configsapp.Configs;
import com.windhike.annotation.model.ImageDrawObject;
import com.windhike.annotation.model.ManagerImageObject;
import com.windhike.tuto.BaseFragment;
import com.windhike.tuto.R;
import com.windhike.tuto.TutoApplication;
import com.windhike.tuto.event.AnnotationChangedEvent;
import com.windhike.tuto.presenter.AnnotationAdapter;
import com.windhike.tuto.reuse.SchedulersTransFormer;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.List;
import butterknife.BindView;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * author:gzzyj on 2017/7/20 0020.
 * email:zhyongjun@windhike.cn
 */
public class AnnotationListFragment extends BaseFragment {
    private AnnotationAdapter mAdapter;
    @BindView(R.id.recycerView)
    RecyclerView recycerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void initView() {
        super.initView();
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
        recycerView.setLayoutManager(manager);
        mAdapter = new AnnotationAdapter();
        recycerView.setAdapter(mAdapter);
        loadData();
    }

    @Subscribe(threadMode= ThreadMode.MAIN)
    public void onAnnotationChanged(AnnotationChangedEvent event) {
        loadData();
    }

    public void loadData() {
        Observable.create(new Observable.OnSubscribe<List<ImageDrawObject>>() {
            @Override
            public void call(Subscriber<? super List<ImageDrawObject>> subscriber) {
                ManagerImageObject managerImageObjects = ManagerImageObject.readFromFile(TutoApplication.getInstance(), Utilities.encryptFileName(Configs.ANNOTATION_IBOS));
                if(managerImageObjects!=null){
                    subscriber.onNext(managerImageObjects.getListChooseDrawObject());
                }
                subscriber.onCompleted();

            }
        }).compose(SchedulersTransFormer.<List<ImageDrawObject>>applyIoSchedulers()).doOnNext(new Action1<List<ImageDrawObject>>() {
            @Override
            public void call(List<ImageDrawObject> imageDrawObjects) {
                mAdapter.updateData(imageDrawObjects);
            }
        }).subscribe();
    }

    @Override
    public int getLayouId() {
        return R.layout.layout_recycler;
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }
}

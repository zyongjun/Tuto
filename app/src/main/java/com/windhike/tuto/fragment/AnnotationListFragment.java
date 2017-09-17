package com.windhike.tuto.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionInflater;
import android.view.View;
import com.windhike.annotation.common.Utilities;
import com.windhike.annotation.configsapp.Configs;
import com.windhike.annotation.model.ImageDrawObject;
import com.windhike.annotation.model.ManagerImageObject;
import com.windhike.fastcoding.base.BaseFragment;
import com.windhike.tuto.AnnotationActivity;
import com.windhike.tuto.R;
import com.windhike.tuto.TutoApplication;
import com.windhike.tuto.event.AnnotationChangedEvent;
import com.windhike.tuto.presenter.AnnotationAdapter;
import com.windhike.fastcoding.rx.SchedulersTransFormer;
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
public class AnnotationListFragment extends BaseFragment implements AnnotationAdapter.AnnotationCallback {
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
        mAdapter = new AnnotationAdapter(this);
        recycerView.setAdapter(mAdapter);
        loadData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAnnotationChanged(AnnotationChangedEvent event) {
        loadData();
    }

    private ManagerImageObject mManagerImageObject;

    public void loadData() {
        Observable.create(new Observable.OnSubscribe<List<ImageDrawObject>>() {
            @Override
            public void call(Subscriber<? super List<ImageDrawObject>> subscriber) {
                mManagerImageObject = ManagerImageObject.readFromFile(TutoApplication.getInstance(), Utilities.encryptFileName(Configs.ANNOTATION_IBOS));
                if (mManagerImageObject != null) {
                    subscriber.onNext(mManagerImageObject.getListChooseDrawObject());
                }
                subscriber.onCompleted();

            }
        }).compose(SchedulersTransFormer.<List<ImageDrawObject>>applyIoSchedulers()).doOnNext(new Action1<List<ImageDrawObject>>() {
            @Override
            public void call(List<ImageDrawObject> imageDrawObjects) {
                if (mAdapter != null) {
                    mAdapter.updateData(imageDrawObjects);
                }
            }
        }).subscribe();
    }

    @Override
    public int getLayouId() {
        return R.layout.layout_recycler;
    }

    @Override
    public void deleteProject(int position) {
        mManagerImageObject.getListChooseDrawObject().remove(position);
        mManagerImageObject.writeToFile(TutoApplication.getInstance(), Configs.ANNOTATION_IBOS);
        mAdapter.remove(position);
    }

    @Override
    public void viewAnnotation(View imageView, int position) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                    new Pair(imageView, String.format("%d_annotation", position))
            );
            setSharedElementReturnTransition(TransitionInflater.from(
                    getActivity()).inflateTransition(R.transition.change_image_transion));
            setExitTransition(TransitionInflater.from(
                    getActivity()).inflateTransition(android.R.transition.fade));
            getActivity().startActivity(AnnotationActivity.obtainExistIntent(getActivity(),position),options.toBundle());
        }else {
            getActivity().startActivity(AnnotationActivity.obtainExistIntent(getActivity(),position));
        }
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }
}

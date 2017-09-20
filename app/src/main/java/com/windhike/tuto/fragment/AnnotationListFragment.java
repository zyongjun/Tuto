package com.windhike.tuto.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.LocalBroadcastManager;
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
import com.windhike.tuto.presenter.AnnotationAdapter;
import com.windhike.fastcoding.rx.SchedulersTransFormer;
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
    public static final String ACTION_ANNOTATION_CHANGED = "ACTION_ANNOTATION_CHANGED";
    private AnnotationAdapter mAdapter;
    @BindView(R.id.recycerView)
    RecyclerView recycerView;
    @BindView(R.id.llEmptyAnnotation)
    View  mEmptyView;
    private LocalBroadcastManager mLocalBroadcastManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
    }

    @Override
    public void initView() {
        super.initView();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_ANNOTATION_CHANGED);
        mLocalBroadcastManager.registerReceiver(mReceiver,intentFilter);
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
        recycerView.setLayoutManager(manager);
        mAdapter = new AnnotationAdapter(this);
        recycerView.setAdapter(mAdapter);
        loadData();
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(mEmptyView!=null) {
                loadData();
            }
        }
    };;

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
                mEmptyView.setVisibility(imageDrawObjects.size()<1?View.VISIBLE:View.GONE);
            }
        }).subscribe();
    }

    @Override
    public int getLayouId() {
        return R.layout.fragment_annotationlist;
    }

    @Override
    public void deleteProject(int position) {
        mManagerImageObject.getListChooseDrawObject().remove(position);
        mManagerImageObject.writeToFile(TutoApplication.getInstance(), Configs.ANNOTATION_IBOS);
        mAdapter.remove(position);
        mEmptyView.setVisibility(mAdapter.getItemCount()<1?View.VISIBLE:View.GONE);
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
        mLocalBroadcastManager.unregisterReceiver(mReceiver);
        super.onDestroyView();
    }
}

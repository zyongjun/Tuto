package com.windhike.tuto.widget;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.windhike.recyclerutils.BindRecyclerHolder;
import com.windhike.recyclerutils.HolderLayout;
import com.windhike.recyclerutils.RecyclerAdapter;
import com.windhike.recyclerutils.RecyclerHolder;
import com.windhike.tuto.R;
import com.windhike.tuto.presenter.AnnotationGalleryPresenter;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * author: gzzyj on 2017/3/22.
 * email:zhyongjun@windhike.cn
 */

@HolderLayout(R.layout.fgt_annotation_picture_preview)
public class PreviewHolder extends RecyclerHolder<AnnotationGalleryPresenter> {
    @BindView(R.id.rc_icon)
    ImageView ivPreview;
    public PreviewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void bindView(int position, AnnotationGalleryPresenter presenter) {
        super.bindView(position, presenter);
        itemView.setTag(R.id.position_id,position);
        itemView.setOnClickListener(presenter.obtainItemClickListener());
        Glide.with(itemView.getContext()).load(presenter.getPictures().get(position).getUri().toString().trim()).into(ivPreview);
    }
}
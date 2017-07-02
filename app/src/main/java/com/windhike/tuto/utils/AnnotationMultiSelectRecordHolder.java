package com.windhike.tuto.utils;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.windhike.annotation.model.ImageDrawObject;
import com.windhike.recyclerutils.BindRecyclerHolder;
import com.windhike.recyclerutils.HolderLayout;
import com.windhike.tuto.R;
import butterknife.Bind;

/**
 * author: zyongjun on 2017/3/8 0008.
 * email: zhyongjun@windhike.cn
 */
@HolderLayout(R.layout.item_annotation_multi_select_record)
public class AnnotationMultiSelectRecordHolder extends BindRecyclerHolder<AnnotationSelectRecordsPresenter> {
    @Bind(R.id.iv_record)
    ImageView ivRecord;
    @Bind(R.id.tv_datetime)
    TextView tvDatetime;
    @Bind(R.id.rl_select)
    View rlSelect;

    public AnnotationMultiSelectRecordHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindView(int position, AnnotationSelectRecordsPresenter presenter) {
        super.bindView(position, presenter);
        int cachePosition =presenter.getManagerImageObjects().getListChooseDrawObject().size()-1-position;
        ImageDrawObject drawObject = presenter.getManagerImageObjects().getListChooseDrawObject().get(cachePosition);
        if (presenter.getSelectList().get(cachePosition)) {
            rlSelect.setVisibility(View.VISIBLE);
        }else {
            rlSelect.setVisibility(View.GONE);
        }
        tvDatetime.setText(drawObject.getmDate());
        Glide.with(itemView.getContext()).load(drawObject.getEditImagePath()).into(ivRecord);
        itemView.setTag(R.id.position_id,cachePosition);
        itemView.setOnClickListener(presenter.itemClickListener());
    }
}

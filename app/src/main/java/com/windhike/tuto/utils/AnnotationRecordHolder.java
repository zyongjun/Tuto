package com.windhike.tuto.utils;

import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.windhike.annotation.model.ImageDrawObject;
import com.windhike.recyclerutils.BindRecyclerHolder;
import com.windhike.recyclerutils.HolderLayout;
import com.windhike.tuto.R;

import butterknife.Bind;

/**
 * author: zyongjun on 2017/3/8 0008.
 * email: zhyongjun@windhike.cn
 */
@HolderLayout(R.layout.item_annotation_record)
public class AnnotationRecordHolder extends BindRecyclerHolder<AnnotationRecordsPresenter> {
    @Bind(R.id.iv_record)
    ImageView ivRecord;
    @Bind(R.id.tv_datetime)
    TextView tvDatetime;
    @Bind(R.id.iv_delete)
    ImageView ivDelete;
    @Bind(R.id.iv_preview)
    ImageView ivShare;

    public AnnotationRecordHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindView(int position, AnnotationRecordsPresenter presenter) {
        super.bindView(position, presenter);
        int cachePosition =presenter.getManagerImageObjects().getListChooseDrawObject().size()-1-position;
        ImageDrawObject drawObject = presenter.getManagerImageObjects().getListChooseDrawObject().get(cachePosition);
        ivDelete.setTag(R.id.position_id,cachePosition);
        ivDelete.setOnClickListener(presenter.deleteProjectListener());
        ivRecord.setTag(R.id.position_id,cachePosition);
        ivRecord.setOnClickListener(presenter.itemClickListener());
        tvDatetime.setText(drawObject.getmDate());

        ivRecord.setImageBitmap(BitmapFactory.decodeFile(drawObject.getEditImagePath()));
//        LoadImageUtil.displayFromSDCard(,ivRecord);
        ivShare.setTag(R.id.position_id,cachePosition);
        ivShare.setOnClickListener(presenter.shareClickListener());
//        Bitmap bmp= BitmapFactory.decodeFile(editPath);
//        ThumbnailUtils.extractThumbnail(bmp,100,100)
//        itemView.setTag(R.id.position_id,position);
//        itemView.setOnClickListener(presenter.itemClickListener());
    }
}

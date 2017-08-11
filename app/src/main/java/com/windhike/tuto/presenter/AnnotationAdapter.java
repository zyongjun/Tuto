package com.windhike.tuto.presenter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import com.windhike.annotation.model.ImageDrawObject;
import com.windhike.tuto.R;
import com.windhike.tuto.fragment.AnnotationFragment;
import java.util.ArrayList;
import java.util.List;

/**
 * author:gzzyj on 2017/7/26 0026.
 * email:zhyongjun@windhike.cn
 */

public class AnnotationAdapter extends RecyclerView.Adapter<ImageHolder>{
    private List<ImageDrawObject> annotationList = new ArrayList<>();

    @Override
    public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_album_item,parent,false));
    }

    public void updateData(List<ImageDrawObject> annotationList) {
        this.annotationList.clear();
        if (annotationList != null) {
            this.annotationList.addAll(annotationList);
        }
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ImageHolder holder, int position) {
        ImageDrawObject drawObject = annotationList.get(position);
//        ivRecord.setImageBitmap(BitmapFactory.decodeFile(drawObject.getEditImagePath()));
        Glide.with(holder.itemView.getContext()).load(drawObject.getEditImagePath()).into(holder.icon);
        holder.itemView.setTag(R.id.position_id,position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                v.getContext().startActivity(AnnotationProjectEditActivity.startExist(v.getContext(), (Integer) v.getTag(R.id.position_id)));
                AnnotationFragment.startExist(v.getContext(),(Integer) v.getTag(R.id.position_id));
            }
        });
    }

    @Override
    public int getItemCount() {
        return annotationList.size();
    }
}

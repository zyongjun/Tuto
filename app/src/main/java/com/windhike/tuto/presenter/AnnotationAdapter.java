package com.windhike.tuto.presenter;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.windhike.annotation.model.ImageDrawObject;
import com.windhike.tuto.EventTracker;
import com.windhike.tuto.R;
import java.util.ArrayList;
import java.util.List;

/**
 * author:gzzyj on 2017/7/26 0026.
 * email:zhyongjun@windhike.cn
 */

public class AnnotationAdapter extends RecyclerView.Adapter<AnnotationAdapter.AnnotationHolder>{
    private List<ImageDrawObject> annotationList = new ArrayList<>();
    private AnnotationCallback mDeleteCallback;
    public AnnotationAdapter(AnnotationCallback annotationCallback) {
        mDeleteCallback = annotationCallback;
    }
    public interface AnnotationCallback {
        void deleteProject(int position);
        void viewAnnotation(View imageView,int position);
    }
    @Override
    public AnnotationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AnnotationHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_annotation_item,parent,false));
    }

    public void remove(int position) {
        annotationList.remove(position);
        notifyDataSetChanged();
    }

    public void updateData(List<ImageDrawObject> annotationList) {
        this.annotationList.clear();
        if (annotationList != null) {
            this.annotationList.addAll(annotationList);
        }
        notifyDataSetChanged();
    }

    private static final String PATTERN_ANNOTATION_SHARENAME = "position_%d";
    @Override
    public void onBindViewHolder(AnnotationHolder holder, int position) {
        ImageDrawObject drawObject = annotationList.get(position);
//        Log.e(TAG, "onBindViewHolder: "+drawObject.getEditImagePath());
        Glide.with(holder.itemView.getContext()).load(drawObject.getEditImagePath()).into(holder.icon);
        ViewCompat.setTransitionName(holder.icon, String.format(PATTERN_ANNOTATION_SHARENAME, position));
        holder.icon.setTag(R.id.position_id,position);
        holder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (Integer) v.getTag(R.id.position_id);
                EventTracker.INSTANCE.trackClickAnno(position);
                mDeleteCallback.viewAnnotation(v,position);
            }
        });
        holder.vDelete.setTag(R.id.position_id, position);
        holder.vDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeleteCallback.deleteProject((Integer) v.getTag(R.id.position_id));
            }
        });
    }

    @Override
    public int getItemCount() {
        return annotationList.size();
    }

    static final class AnnotationHolder extends RecyclerView.ViewHolder{
       ImageView icon;
       ImageView ivDelete;
       View vDelete;

        public AnnotationHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.rc_icon);
            vDelete = itemView.findViewById(R.id.llDelete);
            ivDelete= (ImageView) itemView.findViewById(R.id.ivDelete);
        }
    }
}

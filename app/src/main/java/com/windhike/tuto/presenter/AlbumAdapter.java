package com.windhike.tuto.presenter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import com.windhike.annotation.model.SaveSelectState;
import com.windhike.tuto.R;
import com.windhike.tuto.fragment.AnnotationFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * author:gzzyj on 2017/7/26 0026.
 * email:zhyongjun@windhike.cn
 */

public class AlbumAdapter extends RecyclerView.Adapter<ImageHolder>{
    private List<SaveSelectState> mPhotoList = new ArrayList<>();

    @Override
    public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_album_item,parent,false));
    }

    public void updateData(List<SaveSelectState> photos){
        mPhotoList.addAll(photos);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ImageHolder holder, int position) {
        final SaveSelectState saveSelectState = mPhotoList.get(position);
        Glide.with(holder.itemView.getContext()).load(saveSelectState.getUri()).into(holder.icon);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnnotationFragment.startNewDraw(v.getContext(),saveSelectState.getPath());
//                v.getContext().startActivity(AnnotationProjectEditActivity.startNewDraw(v.getContext(),saveSelectState.getPath()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPhotoList.size();
    }

}

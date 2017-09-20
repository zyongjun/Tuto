package com.windhike.tuto.presenter;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import com.windhike.annotation.model.SaveSelectState;
import com.windhike.tuto.R;
import java.util.ArrayList;
import java.util.List;

/**
 * author:gzzyj on 2017/7/26 0026.
 * email:zhyongjun@windhike.cn
 */

public class AlbumAdapter extends RecyclerView.Adapter<ImageHolder>{
    private List<SaveSelectState> mPhotoList = new ArrayList<>();
    private AlbumCallback albumCallback;

    public interface AlbumCallback{
        void onItemClick(View shareView,String path);
    }

    public AlbumAdapter(AlbumCallback albumCallback) {
        this.albumCallback = albumCallback;
    }

    @Override
    public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_album_item,parent,false));
    }

    public void updateData(List<SaveSelectState> photos){
        mPhotoList.clear();
        mPhotoList.addAll(photos);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ImageHolder holder, int position) {
        final SaveSelectState saveSelectState = mPhotoList.get(position);
        Glide.with(holder.itemView.getContext()).load(saveSelectState.getUri()).into(holder.icon);
        ViewCompat.setTransitionName(holder.icon, String.format("%s_image", saveSelectState.getPath()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                albumCallback.onItemClick(v,saveSelectState.getPath());
//                AnnotationFragment.obtainNewDrawIntent(v.getContext(),saveSelectState.getPath());
//                v.getContext().startActivity(AnnotationProjectEditActivity.obtainNewDrawIntent(v.getContext(),saveSelectState.getPath()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPhotoList.size();
    }

}

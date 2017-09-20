package com.windhike.tuto.presenter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import com.windhike.tuto.R;

/**
 * author:gzzyj on 2017/7/26 0026.
 * email:zhyongjun@windhike.cn
 */

public class ImageHolder extends RecyclerView.ViewHolder{
    ImageView icon;
    public ImageHolder(View itemView) {
        super(itemView);
        icon = (ImageView) itemView.findViewById(R.id.rc_icon);
    }
}

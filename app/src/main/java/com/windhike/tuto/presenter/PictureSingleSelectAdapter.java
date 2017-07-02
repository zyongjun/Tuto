package com.windhike.tuto.presenter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.windhike.annotation.model.SaveSelectState;
import com.windhike.tuto.R;

/**
 * author: zyongjun on 2017/7/1 0001.
 * email: zhyongjun@windhike.cn
 */

public class PictureSingleSelectAdapter extends BaseAdapter {
    private PictureSingleSelectPresenter mPresenter;

    public PictureSingleSelectAdapter(PictureSingleSelectPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public int getCount() {
        return mPresenter.getCurrentStateList().size();
    }

    @Override
    public Object getItem(int position) {
        return mPresenter.getCurrentStateList().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView != null && convertView.getTag() != null) {
            holder = (ViewHolder)convertView.getTag();
        } else {
            convertView = View.inflate(parent.getContext(), R.layout.rc_item_select_single_picture,null);
            holder = new ViewHolder();
            holder.icon = (ImageView) convertView.findViewById(R.id.rc_icon);
            convertView.setTag(holder);
        }

        SaveSelectState saveSelectState = mPresenter.getCurrentStateList().get(position);
//        ((ViewGroup)holder.icon.getParent()).setAlpha(saveSelectState.isSelect()?0.5f:1f);
//        holder.icon.setResource(saveSelectState.getUri());
        Glide.with(parent.getContext()).load(saveSelectState.getUri()).into(holder.icon);
        holder.icon.setTag(position);
        holder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onPictureClick((Integer) v.getTag());
            }
        });
        return convertView;
    }

    private final class ViewHolder {
        ImageView icon;
        ViewHolder() {}
    }
}

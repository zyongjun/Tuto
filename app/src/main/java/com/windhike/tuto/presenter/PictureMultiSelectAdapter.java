package com.windhike.tuto.presenter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import com.windhike.annotation.model.SaveSelectState;
import com.windhike.tuto.R;

/**
 * Created by Administrator on 2017/7/2 0002.
 */

public class PictureMultiSelectAdapter extends BaseAdapter {
    PictureMultiSelectPresenter mPresenter;

    public PictureMultiSelectAdapter(PictureMultiSelectPresenter presenter) {
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
        return (long)position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView != null && convertView.getTag() != null) {
            holder = (ViewHolder)convertView.getTag();
            holder.checkBox.setOnCheckedChangeListener(null);
        } else {
            convertView = View.inflate(parent.getContext(), R.layout.rc_item_select_picture,null);
            holder = new ViewHolder();
            holder.icon = (ImageView)convertView.findViewById(R.id.rc_icon);
            holder.checkBox = (CheckBox)convertView.findViewById(R.id.rc_checkbox);
            holder.vBound = convertView.findViewById(R.id.ll_cbbound);
            convertView.setTag(holder);
        }

        holder.vBound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) ((ViewGroup)v).getChildAt(0);
                cb.setChecked(!cb.isChecked());
            }
        });
        SaveSelectState saveSelectState = mPresenter.getCurrentStateList().get(position);
        holder.checkBox.setChecked(saveSelectState.isSelect());
        ((ViewGroup)holder.icon.getParent()).setAlpha(saveSelectState.isSelect()?0.5f:1f);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPresenter.onPictureSelected(position,isChecked,buttonView);
            }
        });

//        holder.icon.setResource(saveSelectState.getUri());
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
        CheckBox checkBox;
        View vBound;

        ViewHolder() {
        }
    }
}
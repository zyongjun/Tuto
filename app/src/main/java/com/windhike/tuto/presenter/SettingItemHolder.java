package com.windhike.tuto.presenter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.sevenheaven.iosswitch.ShSwitchView;
import com.windhike.tuto.R;

/**
 * author:gzzyj on 2017/8/8 0008.
 * email:zhyongjun@windhike.cn
 */

public class SettingItemHolder extends RecyclerView.ViewHolder{
    ShSwitchView switchView;
    TextView tvName;
    public SettingItemHolder(View itemView) {
        super(itemView);
        tvName = (TextView) itemView.findViewById(R.id.floatwindow_textview);
        switchView = (ShSwitchView) itemView.findViewById(R.id.floatWindowSwitch);
    }
}

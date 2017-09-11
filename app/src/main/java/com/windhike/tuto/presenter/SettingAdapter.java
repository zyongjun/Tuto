package com.windhike.tuto.presenter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.sevenheaven.iosswitch.ShSwitchView;
import com.windhike.annotation.model.PreferenceConnector;
import com.windhike.tuto.R;
import com.windhike.tuto.TutoApplication;
import com.windhike.tuto.model.SettingItem;
import com.windhike.fastcoding.widget.PromptManager;
import com.zyongjun.easytouch.service.DrawMenuService;
import java.util.ArrayList;
import java.util.List;

/**
 * author:gzzyj on 2017/8/8 0008.
 * email:zhyongjun@windhike.cn
 */
public class SettingAdapter extends RecyclerView.Adapter<SettingItemHolder>{
    private Context mContext;
    private List<SettingItem> mSettingList = new ArrayList<>();
    private boolean isFloatOpened;

    public SettingAdapter(Context context) {
        mContext = context;
        SettingItem item = new SettingItem();
        item.name= context.getString(R.string.open_draw_tools);
        item.isSelect = true;
        mSettingList.add(item);
        isFloatOpened = PreferenceConnector.readBoolean(context,PreferenceConnector.KEY_FLOAT_OPENED,false);
    }

    @Override
    public SettingItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SettingItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_setting,parent,false));
    }

    @Override
    public void onBindViewHolder(final SettingItemHolder holder, int position) {
        SettingItem item = mSettingList.get(position);
        holder.tvName.setText(item.name);
        if(position == 0) {
            holder.switchView.setOn(isFloatOpened);
            holder.switchView.setOnSwitchStateChangeListener(new ShSwitchView.OnSwitchStateChangeListener() {
                @Override
                public void onSwitchStateChange(boolean isOn) {
                    Context context = holder.itemView.getContext();
                    if (isOn) {
                        if (checkFloatWindowPermission()) {
                            PreferenceConnector.writeBoolean(context, PreferenceConnector.KEY_FLOAT_OPENED, true);
                            context.startService(new Intent(context, DrawMenuService.class));
                        } else {
                            holder.switchView.setOn(false);
                            isFloatOpened = false;
                            showAppSettingDialog();
                        }
                    } else {
                        isFloatOpened = false;
                        context.stopService(new Intent(context, DrawMenuService.class));
                        PreferenceConnector.writeBoolean(context, PreferenceConnector.KEY_FLOAT_OPENED, false);
                    }

                }
            });
        }
    }

    public boolean checkFloatWindowPermission(){
        boolean hasPermission = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //23后的悬浮窗权限属于危险权限 需要手动开启
            if(!Settings.canDrawOverlays(mContext)) {
                hasPermission = false;
            }
        }else if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.SYSTEM_ALERT_WINDOW) == PackageManager.PERMISSION_DENIED){
            hasPermission = false;
        }
        return hasPermission;
    }

    private void showAppSettingDialog() {
        String title = "权限申请";
        String confirm = "去设置";
        String cancel = "取消";
        String appName = TutoApplication.getInstance().getString(R.string.app_name);
        StringBuilder sb = new StringBuilder(String.format("在设置-应用-%s-权限中开启",appName));
        sb.append("允许绘制工具出现在其他应用上");
        sb.append(",以正常使用本应用");
        PromptManager.getInstance(mContext).showDialog(title, sb.toString(), cancel, confirm, new PromptManager.OnClickBtnCallback() {
            @Override
            public void confirmClick() {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse(String.format("package:%s", mContext.getPackageName())));
                mContext.startActivity(intent);
            }

            @Override
            public void cancelClick() {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mSettingList.size();
    }
}

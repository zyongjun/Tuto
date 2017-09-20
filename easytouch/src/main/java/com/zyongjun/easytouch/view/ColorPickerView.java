package com.zyongjun.easytouch.view;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.zyongjun.easytouch.R;
import com.zyongjun.easytouch.service.DrawMenuService.HolderSwitchCallback;
import com.zyongjun.easytouch.utils.PreferenceConnector;

/**
 * author:gzzyj on 2017/7/30 0030.
 * email:zhyongjun@windhike.cn
 */
public class ColorPickerView extends RelativeLayout implements View.OnClickListener{
    private View mRootView;
    private TextView vColor0;
    private TextView vColor1;
    private TextView vColor2;
    private TextView vColor3;
    private TextView vColor4;
    private TextView vColor5;
    private TextView vColor6;
    private TextView vColor7;
    private TextView vColor8;
    private TextView vColor9;
    private TextView vColor10;
    private TextView vColor11;
    private TextView vColor12;
    private TextView vColor13;
    private TextView vColor14;
    private TextView vColor15;

    public ColorPickerView(@NonNull Context context) {
        super(context);
        init();
    }

    public ColorPickerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ColorPickerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mBroadcastmanager = LocalBroadcastManager.getInstance(getContext());
        removeAllViews();
        setBackgroundResource(android.R.color.transparent);
        mRootView = LayoutInflater.from(getContext()).inflate(R.layout.view_color_control, this, false);
        LayoutParams rootParams = (LayoutParams) mRootView.getLayoutParams();
        rootParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        vColor0 = (TextView) mRootView.findViewById(R.id.vColor0);
        vColor0.setSelected(true);
        vColor0.setOnClickListener(this);
        vColor1 = (TextView) mRootView.findViewById(R.id.vColor1);
        vColor1.setOnClickListener(this);
        vColor2 = (TextView) mRootView.findViewById(R.id.vColor2);
        vColor2.setOnClickListener(this);
        vColor3 = (TextView)mRootView. findViewById(R.id.vColor3);
        vColor3.setOnClickListener(this);
        vColor4 = (TextView) mRootView.findViewById(R.id.vColor4);
        vColor4.setOnClickListener(this);
        vColor5 = (TextView) mRootView.findViewById(R.id.vColor5);
        vColor5.setOnClickListener(this);
        vColor6 = (TextView) mRootView.findViewById(R.id.vColor6);
        vColor6.setOnClickListener(this);
        vColor7 = ((TextView) mRootView.findViewById(R.id.vColor7));
        vColor7.setOnClickListener(this);
        vColor8 = (TextView)mRootView.findViewById(R.id.vColor8);
        vColor8.setOnClickListener(this);
        vColor9 = (TextView)mRootView.findViewById(R.id.vColor9);
        vColor9.setOnClickListener(this);
        vColor10 = (TextView)mRootView.findViewById(R.id.vColor10);
        vColor10.setOnClickListener(this);
        vColor11 = (TextView)mRootView.findViewById(R.id.vColor11);
        vColor11.setOnClickListener(this);
        vColor12 = (TextView)mRootView.findViewById(R.id.vColor12);
        vColor12.setOnClickListener(this);
        vColor13 = (TextView)mRootView.findViewById(R.id.vColor13);
        vColor13.setOnClickListener(this);
        vColor14 = (TextView)mRootView.findViewById(R.id.vColor14);
        vColor14.setOnClickListener(this);
        vColor15 = (TextView)mRootView.findViewById(R.id.vColor15);
        vColor15.setOnClickListener(this);
        handleSelectState();
        addView(mRootView,rootParams);
    }

    public static final String ACTION_COLOR_CHANGED = "ACTION_COLOR_CHANGED";
    public static final String ACTION_SCALE_MODE_CHANGED = "ACTION_SCALE_MODE_CHANGED";

    private LocalBroadcastManager mBroadcastmanager;

    private HolderSwitchCallback mSettingCallback;

    public void onColorChanged(int index) {
        PreferenceConnector.writeInteger(getContext(), KEY_COLOR_SELECTED, index);
        Intent intent = new Intent(ACTION_COLOR_CHANGED);
        intent.putExtra(ACTION_COLOR_CHANGED,index);
        mBroadcastmanager.sendBroadcast(intent);
    }

    public void switchZoomMode(boolean isSwitchToZoomMode) {
        Intent intent = new Intent(ACTION_SCALE_MODE_CHANGED);
        intent.putExtra(ACTION_SCALE_MODE_CHANGED,isSwitchToZoomMode);
        mBroadcastmanager.sendBroadcast(intent);
    }

    public void setSettingCallback(HolderSwitchCallback callback) {
        this.mSettingCallback = callback;
        if (callback != null) {
            setOnClickListener(callback.obtainMenuOutsideListener());
        }
    }
    public static final String KEY_COLOR_SELECTED = "KEY_COLOR_SELECTED";
    private void handleSelectState() {
        int colorIndex = PreferenceConnector.readInteger(getContext(),KEY_COLOR_SELECTED,0);
        resetAllSelectedState();
        switch (colorIndex) {
            case 0:
                vColor0.setSelected(true);
                break;
            case 1:
                vColor1.setSelected(true);
                break;
            case 2:
                vColor2.setSelected(true);
                break;
            case 3:
                vColor3.setSelected(true);
                break;
            case 4:
                vColor4.setSelected(true);
                break;
            case 5:
                vColor5.setSelected(true);
                break;
            case 6:
                vColor6.setSelected(true);
                break;
            case 7:
                vColor7.setSelected(true);
                break;
            case 8:
                vColor8.setSelected(true);
                break;
            case 9:
                vColor9.setSelected(true);
                break;
            case 10:
                vColor10.setSelected(true);
                break;
            case 11:
                vColor11.setSelected(true);
                break;
            case 12:
                vColor12.setSelected(true);
                break;
            case 13:
                vColor13.setSelected(true);
                break;
            case 14:
                vColor14.setSelected(true);
                break;
            case 15:
                vColor15.setSelected(true);
                break;
        }
    }

    private void resetAllSelectedState() {
        vColor0.setSelected(false);
        vColor1.setSelected(false);
        vColor2.setSelected(false);
        vColor3.setSelected(false);
        vColor4.setSelected(false);
        vColor5.setSelected(false);
        vColor6.setSelected(false);
        vColor7.setSelected(false);
        vColor8.setSelected(false);
        vColor9.setSelected(false);
        vColor10.setSelected(false);
        vColor11.setSelected(false);
        vColor12.setSelected(false);
        vColor13.setSelected(false);
        vColor14.setSelected(false);
        vColor15.setSelected(false);
    }

    private int getClickIndex(int id) {
        int index = 0;
        if (id == R.id.vColor0) {
            index = 0;
        } else if (id == R.id.vColor1) {
            index = 1;
        } else if (id == R.id.vColor2) {
            index = 2;
        } else if (id == R.id.vColor3) {
            index = 3;
        } else if (id == R.id.vColor4) {
            index = 4;
        } else if (id == R.id.vColor5) {
            index = 5;
        } else if (id == R.id.vColor6) {
            index = 6;
        } else if (id == R.id.vColor7) {
            index = 7;
        } else if (id == R.id.vColor8) {
            index = 8;
        } else if (id == R.id.vColor9) {
            index = 9;
        } else if (id == R.id.vColor10) {
            index = 10;
        } else if (id == R.id.vColor11) {
            index = 11;
        } else if (id == R.id.vColor12) {
            index = 12;
        } else if (id == R.id.vColor13) {
            index = 13;
        } else if (id == R.id.vColor14) {
            index = 14;
        } else if (id == R.id.vColor15) {
            index = 15;
        }
        return index;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (mSettingCallback != null) {
            mSettingCallback.switchToIconMode();
        }
        resetAllSelectedState();
        v.setSelected(true);
        switchZoomMode(false);
        onColorChanged(getClickIndex(id));
    }
}

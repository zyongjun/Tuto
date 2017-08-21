package com.zyongjun.easytouch.view;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.IntDef;
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
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * author:gzzyj on 2017/7/30 0030.
 * email:zhyongjun@windhike.cn
 */
public class FloatSettingView extends RelativeLayout implements View.OnClickListener{
    private View mRootView;
    TextView vScale;
    TextView vFreeHand;
    TextView vCircle;
    TextView vArrow;
    TextView vSquare;
    TextView vTalk;
    TextView vText;
    TextView vEraser;
    TextView vMask;
    TextView tvReset;
    TextView tvRestore;
    TextView vColorIndicate;

    public FloatSettingView(@NonNull Context context) {
        super(context);
        init();
    }

    public FloatSettingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FloatSettingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mBroadcastmanager = LocalBroadcastManager.getInstance(getContext());
        removeAllViews();
        setBackgroundResource(android.R.color.transparent);

        mRootView = LayoutInflater.from(getContext()).inflate(R.layout.view_annotation_control, this, false);
        LayoutParams rootParams = (LayoutParams) mRootView.getLayoutParams();
        rootParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        vScale = (TextView) mRootView.findViewById(R.id.vScale);
        vScale.setSelected(true);
        vScale.setOnClickListener(this);
        vFreeHand = (TextView) mRootView.findViewById(R.id.vFreeHand);
        vFreeHand.setOnClickListener(this);
        vCircle = (TextView) mRootView.findViewById(R.id.vCircle);
        vCircle.setOnClickListener(this);
        vArrow = (TextView)mRootView. findViewById(R.id.vArrow);
        vArrow.setOnClickListener(this);
        vSquare = (TextView) mRootView.findViewById(R.id.vSquare);
        vSquare.setOnClickListener(this);
        vTalk = (TextView) mRootView.findViewById(R.id.vTalk);
        vTalk.setOnClickListener(this);
        vText = (TextView) mRootView.findViewById(R.id.vText);
//        vText.setOnClickListener(this);
        vEraser = ((TextView) mRootView.findViewById(R.id.vEraser));
        vEraser.setOnClickListener(this);
        vMask = (TextView)mRootView.findViewById(R.id.vMask);
        vMask.setOnClickListener(this);
        tvReset = (TextView)mRootView.findViewById(R.id.tvReset);
        tvReset.setOnClickListener(this);
        tvRestore = (TextView)mRootView.findViewById(R.id.tvRestore);
        tvRestore.setOnClickListener(this);
        vColorIndicate = (TextView)mRootView.findViewById(R.id.vColorIndicate);
        addView(mRootView,rootParams);
    }

    public static final int SHAPE_FREE_HAND = 0;
    public static final int SHAPE_ARROW = 1;
    public static final int SHAPE_SQUARE = 2;
    public static final int SHAPE_CIRCLE = 3;
    public static final int SHAPE_MASK = 4;
    public static final int SHAPE_ERASER = 7;

    public static final String ACTION_SHAPE_CHANGED = "ACTION_SHAPE_CHANGED";
    public static final String ACTION_COLOR_CHANGED = "ACTION_COLOR_CHANGED";
    public static final String ACTION_SCALE_MODE_CHANGED = "ACTION_SCALE_MODE_CHANGED";
    public static final String ACTION_RESET_OPERATION = "ACTION_RESET_OPERATION";
    public static final String ACTION_RESTORE_OPERATION = "ACTION_RESTORE_OPERATION";

    @IntDef({SHAPE_FREE_HAND, SHAPE_ARROW, SHAPE_SQUARE,SHAPE_CIRCLE,SHAPE_MASK,SHAPE_ERASER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DrawingShape{}

    private int mDrawingShape;
    private LocalBroadcastManager mBroadcastmanager;

    public void setDrawingShape(@DrawingShape int drawingShape) {
        mDrawingShape = drawingShape;
    }
    private HolderSwitchCallback mSettingCallback;

    public void onShapeChanged(int drawingShape) {
        Intent intent = new Intent(FloatSettingView.ACTION_SHAPE_CHANGED);
        intent.putExtra(FloatSettingView.ACTION_SHAPE_CHANGED,drawingShape);
        mBroadcastmanager.sendBroadcast(intent);
        Toast.makeText(getContext().getApplicationContext(),"onShapeChanged",Toast.LENGTH_SHORT).show();
    }

    public void onColorChanged(int index) {
        Intent intent = new Intent(FloatSettingView.ACTION_COLOR_CHANGED);
        intent.putExtra(FloatSettingView.ACTION_COLOR_CHANGED,index);
        mBroadcastmanager.sendBroadcast(intent);
        Toast.makeText(getContext().getApplicationContext(),"onColorChanged",Toast.LENGTH_SHORT).show();
    }

    public void switchZoomMode(boolean isSwitchToZoomMode) {
        Intent intent = new Intent(FloatSettingView.ACTION_SCALE_MODE_CHANGED);
        intent.putExtra(FloatSettingView.ACTION_SCALE_MODE_CHANGED,isSwitchToZoomMode);
        mBroadcastmanager.sendBroadcast(intent);
        Toast.makeText(getContext().getApplicationContext(),"switchZoomMode",Toast.LENGTH_SHORT).show();
    }

    public void unDo() {
        switchZoomMode(false);
        Intent intent = new Intent(FloatSettingView.ACTION_RESET_OPERATION);
        mBroadcastmanager.sendBroadcast(intent);
//        Toast.makeText(getContext().getApplicationContext(),"undo",Toast.LENGTH_SHORT).show();
    }
    public void reDo() {
        switchZoomMode(false);
        Intent intent = new Intent(FloatSettingView.ACTION_RESTORE_OPERATION);
        mBroadcastmanager.sendBroadcast(intent);
//        Toast.makeText(getContext().getApplicationContext(),"redo",Toast.LENGTH_SHORT).show();
    }

    public void setSettingCallback(HolderSwitchCallback callback) {
        this.mSettingCallback = callback;
        if (callback != null) {
            setOnClickListener(callback.obtainMenuOutsideListener());
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (mSettingCallback != null) {
            mSettingCallback.switchToIconMode();
        }
        if (id == R.id.tvReset) {
            unDo();
            return;
        }
        if (id == R.id.tvRestore) {
            reDo();
            return;
        }

        vFreeHand.setSelected(false);
        vCircle.setSelected(false);
        vArrow.setSelected(false);
        vSquare.setSelected(false);
        vTalk.setSelected(false);
        vMask.setSelected(false);
        vEraser.setSelected(false);
        vScale.setSelected(false);
        v.setSelected(true);
        if (id == R.id.vScale) {
            switchZoomMode(true);
            return;
        }

        if (id == R.id.vFreeHand) {
            mDrawingShape = 0;
        } else if (id == R.id.vCircle) {
            mDrawingShape = 3;
        } else if (id == R.id.vArrow) {
            mDrawingShape = 1;
        } else if (id == R.id.vSquare) {
            mDrawingShape = 2;
        } else if (id == R.id.vTalk) {
        } else if (id == R.id.vMask) {
            mDrawingShape = 4;

        } else if (id == R.id.vEraser) {
            mDrawingShape = 7;

        }
        switchZoomMode(false);
        onShapeChanged(mDrawingShape);
    }
}

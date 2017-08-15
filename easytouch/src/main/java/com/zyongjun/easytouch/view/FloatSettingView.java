package com.zyongjun.easytouch.view;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.zyongjun.easytouch.R;
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
        removeAllViews();
        setBackgroundResource(android.R.color.transparent);

        mRootView = LayoutInflater.from(getContext()).inflate(R.layout.view_annotation_control, this, false);
        LayoutParams rootParams = (LayoutParams) mRootView.getLayoutParams();
        rootParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        vScale = (TextView) mRootView.findViewById(R.id.vScale);
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
        tvRestore = (TextView)mRootView.findViewById(R.id.tvRestore);
        vColorIndicate = (TextView)mRootView.findViewById(R.id.vColorIndicate);
        addView(mRootView,rootParams);
    }

    public void unDoRedo(boolean isOffUndo, boolean isOffRedo) {
        tvReset.setBackgroundResource(R.mipmap.ic_reset);
        tvReset.setEnabled(isOffUndo);
        tvReset.setAlpha(isOffUndo ? 1f : 0.5f);
        tvRestore.setBackgroundResource(R.mipmap.ic_restore);
        tvRestore.setEnabled(isOffRedo);
        tvRestore.setAlpha(isOffRedo ? 1f : 0.5f);
    }
    public static final int SHAPE_FREE_HAND = 0;
    public static final int SHAPE_ARROW = 1;
    public static final int SHAPE_SQUARE = 2;
    public static final int SHAPE_CIRCLE = 3;
    public static final int SHAPE_MASK = 4;
    public static final int SHAPE_ERASER = 7;

    @IntDef({SHAPE_FREE_HAND, SHAPE_ARROW, SHAPE_SQUARE,SHAPE_CIRCLE,SHAPE_MASK,SHAPE_ERASER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DrawingShape{}

    private int mDrawingShape;
    public void setDrawingShape(@DrawingShape int drawingShape) {
        mDrawingShape = drawingShape;
    }
    private OnDrawingSettingCallback mSettingCallback;
    public interface OnDrawingSettingCallback{
        void onShapeChanged(@DrawingShape int drawingShape);
        void onColorChanged(int index);
        void switchZoomMode(boolean isSwitchToZoomMode);
        OnClickListener obtainMenuOutsideListener();
        void switchToIconMode();
    }

    public void setSettingCallback(OnDrawingSettingCallback callback) {
        this.mSettingCallback = callback;
        if (callback != null) {
            setOnClickListener(callback.obtainMenuOutsideListener());
        }
    }

    @Override
    public void onClick(View v) {
        if (mSettingCallback != null) {
            mSettingCallback.switchToIconMode();
        }
        int id = v.getId();
        if (id == R.id.vScale) {
            v.setSelected(true);
            if (mSettingCallback == null) {
                return;
            }
            mSettingCallback.switchZoomMode(true);
            return;
        }
        vFreeHand.setSelected(false);
        vCircle.setSelected(false);
        vArrow.setSelected(false);
        vSquare.setSelected(false);
        vTalk.setSelected(false);
        vMask.setSelected(false);
        vEraser.setSelected(false);
        v.setSelected(true);
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
        if (mSettingCallback == null) {
            return;
        }
        mSettingCallback.switchZoomMode(false);
        mSettingCallback.onShapeChanged(mDrawingShape);

    }
}

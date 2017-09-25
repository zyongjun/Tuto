package com.zyongjun.easytouch.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.zyongjun.easytouch.R;
import com.zyongjun.easytouch.service.DrawMenuService.HolderSwitchCallback;
import com.zyongjun.easytouch.utils.PreferenceConnector;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import static com.zyongjun.easytouch.view.ColorPickerView.KEY_COLOR_SELECTED;

/**
 * author:gzzyj on 2017/7/30 0030.
 * email:zhyongjun@windhike.cn
 */
public class FloatSettingView extends RelativeLayout implements View.OnClickListener {
    private View mRootView;
    TextView vScale;
    TextView vFreeHand;
    TextView vCircle;
    TextView vArrow;
    TextView vSquare;
    TextView vText;
    TextView vEraser;
    TextView vMask;
    TextView tvReset;
    TextView tvRestore;
    TextView vColor;
    TextView tvShare;
    ImageView tvSave;
    private TextView vOpenOrClose;
    private TextView vHome;

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
        vArrow = (TextView) mRootView.findViewById(R.id.vArrow);
        vArrow.setOnClickListener(this);
        vSquare = (TextView) mRootView.findViewById(R.id.vSquare);
        vSquare.setOnClickListener(this);
        vText = (TextView) mRootView.findViewById(R.id.vText);
        vText.setOnClickListener(this);
        vEraser = ((TextView) mRootView.findViewById(R.id.vEraser));
        vEraser.setOnClickListener(this);
        vMask = (TextView) mRootView.findViewById(R.id.vMask);
        vMask.setOnClickListener(this);
        tvReset = (TextView) mRootView.findViewById(R.id.tvReset);
        tvReset.setOnClickListener(this);
        tvRestore = (TextView) mRootView.findViewById(R.id.tvRestore);
        tvRestore.setOnClickListener(this);
        vColor = (TextView) mRootView.findViewById(R.id.vColor);
        tvShare = (TextView) mRootView.findViewById(R.id.vShare);
        tvShare.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSettingCallback != null) {
                    mSettingCallback.switchToIconMode();
                }
                onShareAnnotation();
            }
        });
        tvSave = (ImageView) mRootView.findViewById(R.id.vSaveee);
        tvSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getContext(),"menu_save");
                if (mSettingCallback != null) {
                    mSettingCallback.switchToIconMode();
                }
                onSaveAnnotation();
            }
        });

        vOpenOrClose = (TextView)mRootView.findViewById(R.id.vOpenOrClose);
        vOpenOrClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                PreferenceConnector.writeBoolean(getContext(),KEY_DRAWING_NOW,!v.isSelected());
                if (mSettingCallback != null) {
                    if (v.isSelected()) {
                        MobclickAgent.onEvent(getContext(),"menu_new_screen");
                        mSettingCallback.onCaptureRequest();
                    }else{
                        MobclickAgent.onEvent(getContext(),"icon_start_main");
                        closeDrawingPage();
                        mSettingCallback.switchToIconMode();
                    }
                }

            }
        });
        vHome = (TextView)mRootView.findViewById(R.id.vHome);
        vHome.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getContext(),"icon_start_main");
                if (mSettingCallback != null) {
                    mSettingCallback.switchToIconMode();
                }
                lauch();
            }
        });
        TypedArray attr = getResources().obtainTypedArray(R.array.draw_bgres);
        int size = attr.length();
        colorDrawableArray = new int[size];
        for (int i = 0; i < size; i++) {
            colorDrawableArray[i] = attr.getResourceId(i,0);
        }
        attr.recycle();
        refreshUI();
        addView(mRootView, rootParams);
    }

    private int[] colorDrawableArray;

    private void initColorState() {
        vColor.setSelected(true);
        vColor.setBackgroundResource(getBgColorSelected());
    }

    public static final String KEY_DRAWING_NOW = "KEY_DRAWING_NOW";
    private void initDrawingOpenState() {
        boolean isDrawing = PreferenceConnector.readBoolean(getContext(),KEY_DRAWING_NOW,false);
        vOpenOrClose.setSelected(!isDrawing);
    }

    public void refreshUI() {
        initColorState();
        initDrawingOpenState();
    }

    private int getBgColorSelected() {
        int colorIndex = PreferenceConnector.readInteger(getContext(), KEY_COLOR_SELECTED, 0);
        return colorDrawableArray[colorIndex];
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

    public static final String ACTION_SHARE_ANNOTATION = "ACTION_SHARE_ANNOTATION";
    public static final String ACTION_SAVE_ANNOTAION = "ACTION_SAVE_ANNOTAION";

    public static final String ACTION_OPEN_EMPTY_DRAWING_PAGE = "ACTION_OPEN_EMPTY_DRAWING_PAGE";
    public static final String ACTION_CLOSE_DRAWING_PAGE = "ACTION_CLOSE_DRAWING_PAGE";
    public static final String ACTION_CLOSE_APPUI_PAGE = "ACTION_CLOSE_APPUI_PAGE";
    public static final String ACTION_CAPTURE_FINISHED = "ACTION_CAPTURE_FINISHED";
    public static final String ACTION_LAUCH = "ACTION_LAUCH";
    public static final String ACTION_EDIT_TEXT_MODE = "ACTION_EDIT_TEXT_MODE";


    @IntDef({SHAPE_FREE_HAND, SHAPE_ARROW, SHAPE_SQUARE, SHAPE_CIRCLE, SHAPE_MASK, SHAPE_ERASER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DrawingShape {
    }

    private int mDrawingShape;
    private LocalBroadcastManager mBroadcastmanager;

    private HolderSwitchCallback mSettingCallback;


    public void saveToSdcardPNG(File file, Bitmap bitmap) {
        try {
            OutputStream outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void onEditTextMode() {
        Intent intent = new Intent(ACTION_EDIT_TEXT_MODE);
        mBroadcastmanager.sendBroadcast(intent);
    }

    public void lauch() {
        Intent intent = new Intent(ACTION_LAUCH);
        mBroadcastmanager.sendBroadcast(intent);
    }

    public void closeDrawingPage() {
        Intent intent = new Intent(ACTION_CLOSE_DRAWING_PAGE);
        mBroadcastmanager.sendBroadcast(intent);
    }

    public void onShareAnnotation() {
        MobclickAgent.onEvent(getContext(),"menu_share");
        Intent intent = new Intent(ACTION_SHARE_ANNOTATION);
        mBroadcastmanager.sendBroadcast(intent);
    }

    public void onSaveAnnotation() {
        MobclickAgent.onEvent(getContext(),"menu_save");
        Intent intent = new Intent(ACTION_SAVE_ANNOTAION);
        mBroadcastmanager.sendBroadcast(intent);
    }

    public void onShapeChanged(int drawingShape) {
        Intent intent = new Intent(ACTION_SHAPE_CHANGED);
        intent.putExtra(ACTION_SHAPE_CHANGED, drawingShape);
        mBroadcastmanager.sendBroadcast(intent);
    }

    public void switchZoomMode(boolean isSwitchToZoomMode) {
        Intent intent = new Intent(ACTION_SCALE_MODE_CHANGED);
        intent.putExtra(ACTION_SCALE_MODE_CHANGED, isSwitchToZoomMode);
        mBroadcastmanager.sendBroadcast(intent);
    }

    public void unDo() {
        switchZoomMode(false);
        Intent intent = new Intent(ACTION_RESET_OPERATION);
        mBroadcastmanager.sendBroadcast(intent);
    }

    public void reDo() {
        switchZoomMode(false);
        Intent intent = new Intent(ACTION_RESTORE_OPERATION);
        mBroadcastmanager.sendBroadcast(intent);
    }

    public void setSettingCallback(HolderSwitchCallback callback) {
        this.mSettingCallback = callback;
        if (callback != null) {
            setOnClickListener(callback.obtainMenuOutsideListener());
            vColor.setOnClickListener(callback.obtainMenuColorPickerListener());
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (mSettingCallback != null) {
            mSettingCallback.switchToIconMode();
        }
        if (id == R.id.tvReset) {
            MobclickAgent.onEvent(getContext(),"menu_reset");
            unDo();
            return;
        }
        if (id == R.id.tvRestore) {
            MobclickAgent.onEvent(getContext(),"menu_restore");
            reDo();
            return;
        }

        vFreeHand.setSelected(false);
        vCircle.setSelected(false);
        vArrow.setSelected(false);
        vSquare.setSelected(false);
        vMask.setSelected(false);
        vEraser.setSelected(false);
        vScale.setSelected(false);
        v.setSelected(true);
        if (id == R.id.vScale) {
            MobclickAgent.onEvent(getContext(),"menu_zoom");
            switchZoomMode(true);
            return;
        }

        if (id == R.id.vFreeHand) {
            MobclickAgent.onEvent(getContext(),"menu_free");
            mDrawingShape = 0;
        } else if (id == R.id.vCircle) {
            MobclickAgent.onEvent(getContext(),"menu_circle");
            mDrawingShape = 3;
        } else if (id == R.id.vArrow) {
            MobclickAgent.onEvent(getContext(),"menu_arrow");
            mDrawingShape = 1;
        } else if (id == R.id.vSquare) {
            MobclickAgent.onEvent(getContext(),"menu_rect");
            mDrawingShape = 2;
        } else if (id == R.id.vMask) {
            MobclickAgent.onEvent(getContext(),"menu_mask");
            mDrawingShape = 4;
        } else if (id == R.id.vEraser) {
            MobclickAgent.onEvent(getContext(),"menu_erraser");
            mDrawingShape = 7;
        } else if (id == R.id.vText) {
            MobclickAgent.onEvent(getContext(),"menu_text");
            onEditTextMode();
            return;
        }
        switchZoomMode(false);
        onShapeChanged(mDrawingShape);
    }
}

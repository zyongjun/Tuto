package com.windhike.tuto.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.internal.view.SupportMenu;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.umeng.analytics.MobclickAgent;
import com.windhike.annotation.common.DrawUtils;
import com.windhike.annotation.configsapp.AnnotationInitialize;
import com.windhike.annotation.configsapp.Configs;
import com.windhike.annotation.model.BlurManager;
import com.windhike.annotation.model.FlaotPoint;
import com.windhike.annotation.model.ListenerEventUpdate;
import com.windhike.annotation.model.PreferenceConnector;
import com.windhike.annotation.reuse.DisplayUtil;
import com.windhike.annotation.view.ArrowObject;
import com.windhike.annotation.view.BlurObject;
import com.windhike.annotation.view.CustomerShapeView;
import com.windhike.annotation.view.DotObject;
import com.windhike.annotation.view.EllipseObject;
import com.windhike.annotation.view.EraserObject;
import com.windhike.annotation.view.FreeStyleObject;
import com.windhike.annotation.view.RecordObject;
import com.windhike.annotation.view.RectangleObject;
import com.windhike.annotation.view.TextObject;
import com.windhike.fastcoding.util.InputWindowUtil;
import com.windhike.fastcoding.util.UIUtil;
import com.windhike.tuto.R;
import java.util.ArrayList;
import java.util.Iterator;
import static com.windhike.annotation.configsapp.Configs.ShapeAction.AddNewShape;
import static com.windhike.annotation.configsapp.Configs.ShapeAction.DeleteShape;
import static com.windhike.annotation.configsapp.Configs.ShapeAction.MoveOrResizeShape;
import static com.windhike.annotation.configsapp.Configs.ShapeAction.MoveShapeToTop;
import static com.zyongjun.easytouch.view.ColorPickerView.KEY_COLOR_SELECTED;

public class DrawingView extends RelativeLayout {
    private static float MAX_ZOOM = 5.0f;
    private static float MIN_ZOOM = 1.0f;
    private BlurManager _blurManager;
    private int _undoRedoCurrentIndex = -1;
    private int _undoRedoFirstIndex = -1;
    private int[] arrayColor;
    private float[] arrayStrokeWith = Configs.LIST_STROKE_WIDTH;
    private float[] arrayTextSize = Configs.LIST_TEXT_SIZE;
    float deltaScale = 0.0f;
    private int distanceZigZag = 0;
    private int hMainView;
    float increasedScale = 1.0f;
    private int indexCurrentTouchOnShape = 0;
    private boolean isDrawVirtualBodyArrow = false;
    private boolean isEdit;
    private boolean isFingerMove = false;
    private boolean isNotDrawWhenCloseMenu = false;
    private boolean isTouchOnPointFocusShape = false;
    private boolean isTouchOnShape = false;
    public boolean isWaittingUp = false;
    private ArrayList<Integer> listIndexCornerPath = new ArrayList();
    private ArrayList<FlaotPoint> listIntersection = new ArrayList();
    private ArrayList<FlaotPoint> listPoint = new ArrayList();
    private ArrayList<FlaotPoint> listPointArrow = new ArrayList();
    private ArrayList<CustomerShapeView> listRootShape;
    private ListenerEventUpdate listener;
    private Bitmap mBitmapVoicePrepare;
    private Context mContext;
    private Paint mCurrentSettingPaint;
    private CustomerShapeView mCurrentShape;
    private Configs.DrawingState mDrawingState = Configs.DrawingState.Idle;
    private GestureDetector mGestureDetector;
    private int mIndexDotCurrentShape = 0;
    private Paint mMainBitmapPaint;
    private int mModeCreateObjPaint = 0;
    private Paint mPaintVirtualDraw;
    private Path mPath;
    private Path mPathVirtualDraw;
    private ScaleGestureDetector mScaleDetector;
    private float mTranslateX = 0.0f;
    private float mTranslateY = 0.0f;
    private BlurObject mVirtualBlurObj;
    private float mXStart;
    private float mXStartVirtual;
    private float mYStart;
    private float mYStartVirtual;
    private Bitmap mainBitmap;
    private float mtailArrowX;
    private float mtailArrowY;
    private float mtipArrowX;
    private float mtipArrowY;
    float newTranX = 0.0f;
    float newTranY = 0.0f;
    float oldScale = 1.0f;
    float oldTranlateX = 0.0f;
    float oldTranlateY = 0.0f;
    public float scaleFactor = 1.0f;
    private float startX = 0.0f;
    private float startY = 0.0f;
    private int wMainView;
    private int xMainBitmap;
    private int yMainBitmap;
    public static final float HUE_YELLOW = 60f;
    public static final float HUE_ORANGE = 30.0f;

    Canvas mForeCanvas;
    Bitmap converBitmap;
    private Paint mEraserPaint;


    public float getScaleFactor() {
        return scaleFactor;
    }

    class MyGestureDetector extends SimpleOnGestureListener {

        MyGestureDetector() {
        }

        public boolean onDoubleTap(MotionEvent e) {
            return false;
        }

        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        public void onShowPress(MotionEvent e) {
        }

        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        public void onLongPress(MotionEvent e) {
            if (isTouchOnShape && listener != null) {
            }else if (DrawingView.this.listener != null && DrawingView.this.isEdit&&!DrawingView.this.isScaleMode) {
                float[] newTouch = getAbsolutePosition(e.getX(), e.getY());
                float x = newTouch[0];
                float y = newTouch[1];
                if (!isFingerMove) {
                    clearFocusAllShape();
                    isTouchOnShape = checkFocusOnShape(x, y, false);
                    if (isTouchOnShape) {
                        jumpShapeToTopLayer();
                        if (mCurrentShape.mType==8||mCurrentShape.mType==6) {//voice or text
                            isLongPressOnVoiceOrText = true;
                        resetData();
                        postInvalidate();
                    }
                    }
                }
            }
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > Configs.SHOW_MENU_EDGE_WIDTH && Math.abs(velocityX) > Configs.SHOW_MENU_EDGE_WIDTH) {
                        if (diffX > 0.0f) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                    }
                } else if (Math.abs(diffY) > Configs.SHOW_MENU_EDGE_WIDTH && Math.abs(velocityY) > Configs.SHOW_MENU_EDGE_WIDTH) {
                    if (diffY > 0.0f) {
                        onSwipeBottom();
                    } else {
                        onSwipeTop();
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return false;
        }

        public boolean onDown(MotionEvent e) {
            return false;
        }
    }
    private boolean isLongPressOnVoiceOrText;
    private class ScaleListener extends SimpleOnScaleGestureListener {
        private ScaleListener() {
        }

        public boolean onScale(ScaleGestureDetector detector) {
            if(isScaleMode) {
                if (mDrawingState == Configs.DrawingState.Pinch && mainBitmap != null && isEdit) {
                    scaleFactor *= detector.getScaleFactor();
                    listener.onScale(scaleFactor);
                    scaleFactor = Math.max(DrawingView.MIN_ZOOM, Math.min(scaleFactor, DrawingView.MAX_ZOOM));
                }
            }
            return true;
        }

        public boolean onScaleBegin(ScaleGestureDetector detector) {
            if(isScaleMode) {
                if (mDrawingState != Configs.DrawingState.Pinch) {
                    return true;
                }
                deltaScale = oldScale - 1.0f;
                if (deltaScale < 0.0f) {
                    deltaScale = 0.0f;
                }
            }
            return super.onScaleBegin(detector);
        }

        public void onScaleEnd(ScaleGestureDetector detector) {
            if(isScaleMode) {
                listener.onScale(scaleFactor);
                if (mDrawingState == Configs.DrawingState.Pinch) {
                    oldScale = scaleFactor;
                    super.onScaleEnd(detector);
                }
            }
        }
    }
    int slop;
    public DrawingView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        slop = ViewConfiguration.get(getContext()).getScaledPagingTouchSlop();
        this.mContext = context;
        this.mBitmapVoicePrepare = BitmapFactory.decodeResource(AnnotationInitialize.getInstance().getContext().getResources(), R.mipmap.ic_anno_voice_prepare);
        this.mMainBitmapPaint = new Paint();
        this.mPath = new Path();
        this.mPathVirtualDraw = new Path();
        int indexStrokeWith = 1;
        arrayColor = getResources().getIntArray(R.array.draw_colors);
        int indexColor = PreferenceConnector.readInteger(this.mContext, KEY_COLOR_SELECTED, 0);
        PreferenceConnector.writeInteger(this.mContext, Configs.FLAG_INDEX_STROKEWIDTH, indexStrokeWith);
        setLayerType(1, null);
        this.mCurrentSettingPaint = new Paint();
        this.mCurrentSettingPaint.setAntiAlias(true);
        this.mCurrentSettingPaint.setDither(true);
        this.mCurrentSettingPaint.setColor(this.arrayColor[indexColor]);
        this.mCurrentSettingPaint.setStyle(Style.STROKE);
        this.mCurrentSettingPaint.setStrokeJoin(Join.ROUND);
        this.mCurrentSettingPaint.setStrokeWidth(this.arrayStrokeWith[indexStrokeWith]);
        this.mCurrentSettingPaint.setTextSize(this.arrayTextSize[indexStrokeWith]);
        this.mCurrentSettingPaint.setPathEffect(new CornerPathEffect(25.0f));
        this.mPaintVirtualDraw = new Paint();
        this.mPaintVirtualDraw.setAntiAlias(true);
        this.mPaintVirtualDraw.setDither(true);
        this.mPaintVirtualDraw.setColor(this.arrayColor[indexColor]);
        this.mPaintVirtualDraw.setStyle(Style.STROKE);
        this.mPaintVirtualDraw.setStrokeJoin(Join.ROUND);
        this.mPaintVirtualDraw.setStrokeWidth(this.arrayStrokeWith[indexStrokeWith]);
        this.mPaintVirtualDraw.setTextSize(this.arrayTextSize[indexStrokeWith]);
        this.listRootShape = new ArrayList();

        mEraserPaint = new Paint();
        mEraserPaint.setAntiAlias(true);
        mEraserPaint.setDither(true);
        mEraserPaint.setColor(Color.RED);
        mEraserPaint.setStrokeWidth(30);
        mEraserPaint.setStyle(Paint.Style.STROKE);
        mEraserPaint.setStrokeJoin(Paint.Join.ROUND);
        mEraserPaint.setStrokeCap(Paint.Cap.SQUARE);
        mEraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        Paint mPaintVirtualRect = new Paint();
        mPaintVirtualRect.setColor(ResourcesCompat.getColor(context.getResources(),R.color.default_read,null));
        mPaintVirtualRect.setStyle(Style.FILL);
        Paint mPaintCircle = new Paint();
        mPaintCircle.setColor(ResourcesCompat.getColor(context.getResources(),R.color.default_read,null));
        mPaintCircle.setStyle(Style.STROKE);
        mPaintCircle.setStrokeWidth(Configs.SHOW_MENU_EDGE_WIDTH);
        Paint shadowPaint = new Paint();
        shadowPaint.setAntiAlias(true);
        shadowPaint.setColor(SupportMenu.CATEGORY_MASK);
        shadowPaint.setTextSize(45.0f);
        shadowPaint.setStrokeWidth(2.0f);
        shadowPaint.setStyle(Style.STROKE);
        this.mGestureDetector = new GestureDetector(new MyGestureDetector());
        this.mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        this._blurManager = new BlurManager(context);
    }

    public void setCurrentColor(int indexColor) {
        setCurrentPaint(indexColor,1,"");
    }

    public void setCurrentPaint(int indexColor, int indexStrokeWidth, String textValue) {
        int color = arrayColor[indexColor];
        float stroke = arrayStrokeWith[indexStrokeWidth];
        if (textValue.equalsIgnoreCase("") && color == mCurrentSettingPaint.getColor() && stroke == mCurrentSettingPaint.getStrokeWidth()) {
            return;
        }
        mCurrentSettingPaint.setColor(arrayColor[indexColor]);
        mCurrentSettingPaint.setStrokeWidth(arrayStrokeWith[indexStrokeWidth]);
        mCurrentSettingPaint.setTextSize(arrayTextSize[indexStrokeWidth]);
        mPaintVirtualDraw.setColor(arrayColor[indexColor]);
        mPaintVirtualDraw.setStrokeWidth(arrayStrokeWith[indexStrokeWidth]);
        mPaintVirtualDraw.setTextSize(arrayTextSize[indexStrokeWidth]);
        if (mCurrentShape == null) {
            return;
        }
        switch (this.mCurrentShape.mType) {
            case 1:
            case 2:
            case 4:
            case 5:
            case 6:
                mCurrentShape.mPaint.setColor(arrayColor[indexColor]);
                mCurrentShape.mPaint.setStrokeWidth(arrayStrokeWith[indexStrokeWidth]);
                break;
            case 3:
                mCurrentShape.mPaint.setColor(arrayColor[indexColor]);
                mCurrentShape.mPaint.setStrokeWidth(arrayStrokeWith[indexStrokeWidth]);
                ((ArrowObject)mCurrentShape).initNewPointPositionMoveShape();
                break;
        }
        mCurrentShape._action = Configs.ShapeAction.ChangeTextColorOrStroke;
        showShapeAndFocus(mCurrentShape, false, false);
        clearRedoSharpFromIndex(_undoRedoCurrentIndex);
        addNewSharp(mCurrentShape);
        mCurrentShape = mCurrentShape.copyCustomerView();
        showShapeAndFocus(mCurrentShape, true, true);
        mCurrentShape._action = MoveShapeToTop;
        postInvalidate();
    }

    public void setListener(ListenerEventUpdate listener) {
        this.listener = listener;
    }

    public void resetAllData() {
        clearFocusAllShape();
        if (this.mainBitmap != null) {
            this.mainBitmap.recycle();
            this.mainBitmap = null;
        }
        if (this.mBitmapVoicePrepare != null) {
            mBitmapVoicePrepare.recycle();
            mBitmapVoicePrepare = null;
        }
        if (this._blurManager != null) {
            this._blurManager.cleanData();
        }
        if (this.listIndexCornerPath != null) {
            listIndexCornerPath.clear();
        }
        if (this.listPoint != null) {
            listPoint.clear();
        }
        if (this.listIntersection != null) {
            listIntersection.clear();
        }
        if (this.listRootShape != null) {
            for (int i = 0; i < this.listRootShape.size(); i++) {
                CustomerShapeView shape = this.listRootShape.get(i);
                if(shape!=null) {
                    shape.resetData();
                }
            }
            listRootShape.clear();
        }
        if (this.listPointArrow != null) {
            listPointArrow.clear();
        }
        this.indexCurrentTouchOnShape = 0;
        this._undoRedoCurrentIndex = -1;
        this._undoRedoFirstIndex = -1;
        checkHeaderStatus();
        System.gc();
    }

    public void loadListRootShape(ArrayList<CustomerShapeView> listRootShape, int undoRedoCurrentIndex) {
        clearAllRootShape();
        this.listRootShape.addAll(listRootShape);
        this._undoRedoCurrentIndex = undoRedoCurrentIndex;
        this._undoRedoFirstIndex = 0;
        checkHeaderStatus();
    }

    void addNewSharp(CustomerShapeView shape) {
        this.listRootShape.add(shape);
        this._undoRedoCurrentIndex = this.listRootShape.size();
        checkHeaderStatus();
    }

    void clearRedoSharpFromIndex(int index) {
        if (index <= this.listRootShape.size() - 1) {
            ArrayList<CustomerShapeView> tempList = new ArrayList();
            for (int i = 0; i < index; i++) {
                tempList.add(this.listRootShape.get(i));
            }
            this.listRootShape.clear();
            this.listRootShape = null;
            this.listRootShape = tempList;
            this._undoRedoCurrentIndex = this.listRootShape.size();
        }
    }

    public void clearAllRootShape() {
        listRootShape.clear();
        this._undoRedoCurrentIndex = this.listRootShape.size();
        this._undoRedoFirstIndex = this.listRootShape.size();
        this.mCurrentShape = null;
        checkHeaderStatus();
    }
    private boolean isScaleMode = false;

    public void setEnableScaleMode(boolean enableScaleMode) {
        this.isScaleMode = enableScaleMode;
    }
    public Bitmap scaleBitmapAfterZoomInOut() {
        if (this.mainBitmap == null) {
            return null;
        }
        Bitmap scaledBitmap = Bitmap.createBitmap((int) (((float) this.mainBitmap.getWidth()) * this.scaleFactor), (int) (((float) this.mainBitmap.getHeight()) * this.scaleFactor), Config.ARGB_8888);
        float scaleX = this.scaleFactor;
        float scaleY = this.scaleFactor;
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX, scaleY, 0.0f, 0.0f);
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        if (this.mainBitmap != null) {
            canvas.drawBitmap(this.mainBitmap, 0, 0, this.mMainBitmapPaint);
        }
        canvas.drawBitmap(converBitmap, -xMainBitmap, -yMainBitmap, null);
        return scaledBitmap;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (this.mDrawingState == Configs.DrawingState.Pinch) {
            if (((double) (this.scaleFactor / this.oldScale)) >= 0.99d || this.oldScale <= 1.0f || this.deltaScale <= 0.0f || ((double) this.scaleFactor) >= 1.02d) {
                if (this.mScaleDetector.isInProgress()) {
                    this.increasedScale = this.scaleFactor / this.oldScale;
                    this.newTranX = (-(this.increasedScale - 1.0f)) * this.mScaleDetector.getFocusX();
                    this.newTranY = (-(this.increasedScale - 1.0f)) * this.mScaleDetector.getFocusY();
                } else {
                    this.oldTranlateX = this.newTranX + this.oldTranlateX;
                    this.oldTranlateY = this.newTranY + this.oldTranlateY;
                    this.oldScale = this.scaleFactor;
                    this.newTranX = 0.0f;
                    this.newTranY = 0.0f;
                }
            } else if (this.mScaleDetector.isInProgress()) {
                if (this.scaleFactor == 1.0f || (this.oldTranlateX == 0.0f && this.oldTranlateY == 0.0f)) {
                    this.oldTranlateX = 0.0f;
                    this.oldTranlateY = 0.0f;
                    this.scaleFactor = 1.0f;
                }
                this.oldTranlateX = (((this.scaleFactor - this.oldScale) + this.deltaScale) / this.deltaScale) * this.oldTranlateX;
                this.oldTranlateY = (((this.scaleFactor - this.oldScale) + this.deltaScale) / this.deltaScale) * this.oldTranlateY;
            } else {
                this.newTranX = 0.0f;
                this.newTranY = 0.0f;
            }
        }
        float verifiedX = verifyCoordinateValue(this.oldTranlateX + this.newTranX, (((float) this.wMainView) * this.scaleFactor) - ((float) this.wMainView));
        float verifiedY = verifyCoordinateValue(this.oldTranlateY + this.newTranY, (((float) this.hMainView) * this.scaleFactor) - ((float) this.hMainView));
        canvas.translate(verifiedX, verifiedY);
        canvas.scale(this.scaleFactor, this.scaleFactor);
        if(mainBitmap!=null)
        canvas.drawBitmap(mainBitmap, xMainBitmap, yMainBitmap, null);
        // converting bitmap into mutable bitmap
        converBitmap = Bitmap.createBitmap(wMainView, hMainView, Config.ARGB_8888);
        mForeCanvas = new Canvas(converBitmap);
        mForeCanvas.drawColor(Color.TRANSPARENT);
        if (this.mainBitmap != null) {
            mForeCanvas.clipRect(this.xMainBitmap, this.yMainBitmap, this.xMainBitmap + this.mainBitmap.getWidth(), this.yMainBitmap + this.mainBitmap.getHeight());
        }

        for (int i = 0; i < this.listRootShape.size(); i++) {
            CustomerShapeView shape = this.listRootShape.get(i);
            if (shape != null) {
                processBitmap(shape);
                if (shape.isVisible) {
                    shape.onDraw(mForeCanvas);
                }
            }
        }
        if (this.mCurrentShape != null && this.mCurrentShape.isVisible) {
            processBitmap(this.mCurrentShape);
            this.mCurrentShape.onDraw(mForeCanvas);
        }
        if (this.mModeCreateObjPaint == 0 || this.mModeCreateObjPaint == 5||this.mModeCreateObjPaint == 7) {
            mForeCanvas.drawPath(this.mPath, mModeCreateObjPaint == 7?mEraserPaint:this.mCurrentSettingPaint);
        } else if (this.mModeCreateObjPaint == 2) {
            mForeCanvas.drawPath(this.mPathVirtualDraw, this.mPaintVirtualDraw);
        } else if (this.mModeCreateObjPaint == 1 && this.isDrawVirtualBodyArrow) {
            mForeCanvas.drawPath(this.mPathVirtualDraw, this.mPaintVirtualDraw);
        } else if (this.mModeCreateObjPaint == 3) {
            RectF mVirtualRectFCircle = new RectF();
            this.mPathVirtualDraw.computeBounds(mVirtualRectFCircle, true);
            mForeCanvas.drawOval(mVirtualRectFCircle, this.mPaintVirtualDraw);
        } else if (this.mModeCreateObjPaint == 4 && this.mVirtualBlurObj != null) {
            this.mVirtualBlurObj.onDraw(mForeCanvas);
        }
        canvas.drawBitmap(converBitmap, 0, 0, null);
        this.mTranslateX = -verifiedX;
        this.mTranslateY = -verifiedY;
        canvas.restore();
    }

    private void virtualBlurObj(float x1, float y1, float x2, float y2) {
        this.mPathVirtualDraw.rewind();
        if (x1 < x2 && y1 > y2) {
            this.mPathVirtualDraw.addRect(x1, y2, x2, y1, Direction.CW);
        }
        if (x1 < x2 && y1 < y2) {
            this.mPathVirtualDraw.addRect(x1, y1, x2, y2, Direction.CW);
        }
        if (x1 > x2 && y1 > y2) {
            this.mPathVirtualDraw.addRect(x2, y2, x1, y1, Direction.CW);
        }
        if (x1 > x2 && y1 < y2) {
            this.mPathVirtualDraw.addRect(x2, y1, x1, y2, Direction.CW);
        }
        RectF rectF = new RectF();
        this.mPathVirtualDraw.computeBounds(rectF, true);
        if (this.mVirtualBlurObj == null) {
            this.mVirtualBlurObj = new BlurObject(this.mContext, this._blurManager, 4, (float) this.xMainBitmap, (float) this.yMainBitmap, rectF.left, rectF.top, rectF.right, rectF.bottom, this.mCurrentSettingPaint, new FlaotPoint((float) this.xMainBitmap, (float) this.yMainBitmap), (float) this.mainBitmap.getWidth(), (float) this.mainBitmap.getHeight());
            this.mVirtualBlurObj.isFocus = false;
            this.mVirtualBlurObj.isVisible = true;
            return;
        }
        this.mVirtualBlurObj.resizeBlurBitmap(rectF.left, rectF.top, rectF.right, rectF.bottom);
    }

    private void virtualCircle(float x1, float y1, float x2, float y2) {
        this.mPathVirtualDraw.rewind();
        if (x1 < x2 && y1 > y2) {
            this.mPathVirtualDraw.addRect(x1, y2, x2, y1, Direction.CW);
        }
        if (x1 < x2 && y1 < y2) {
            this.mPathVirtualDraw.addRect(x1, y1, x2, y2, Direction.CW);
        }
        if (x1 > x2 && y1 > y2) {
            this.mPathVirtualDraw.addRect(x2, y2, x1, y1, Direction.CW);
        }
        if (x1 > x2 && y1 < y2) {
            this.mPathVirtualDraw.addRect(x2, y1, x1, y2, Direction.CW);
        }
    }

    private void virtualRectPath(float x1, float y1, float x2, float y2) {
        this.mPathVirtualDraw.rewind();
        if (x1 < x2 && y1 > y2) {
            this.mPathVirtualDraw.addRect(x1, y2, x2, y1, Direction.CW);
        }
        if (x1 < x2 && y1 < y2) {
            this.mPathVirtualDraw.addRect(x1, y1, x2, y2, Direction.CW);
        }
        if (x1 > x2 && y1 > y2) {
            this.mPathVirtualDraw.addRect(x2, y2, x1, y1, Direction.CW);
        }
        if (x1 > x2 && y1 < y2) {
            this.mPathVirtualDraw.addRect(x2, y1, x1, y2, Direction.CW);
        }
    }

    private void virtualArrowPath(float tailX, float tailY, float tipX, float tipY) {
        this.isDrawVirtualBodyArrow = true;
        this.mPathVirtualDraw.reset();
        int arrowLength = 60;
        for (int i = 0; i < Configs.LIST_STROKE_WIDTH.length; i++) {
            if (this.mPaintVirtualDraw.getStrokeWidth() == Configs.LIST_STROKE_WIDTH[i]) {
                if (i == 0) {
                    arrowLength = 50;
                    break;
                } else if (i == 1) {
                    arrowLength = 60;
                    break;
                } else if (i == 2) {
                    arrowLength = 70;
                    break;
                }
            }
        }
        double theta = Math.atan2((double) (tipY - tailY), (double) (tipX - tailX));
        int deltaLength = 15;
        double x1 = tipX - arrowLength * Math.cos(theta+ Math.toRadians(45.0d));
        double y1 = tipY - arrowLength * Math.sin(theta+ Math.toRadians(45.0d));
        double x2 = tipX - (arrowLength-deltaLength)* Math.cos(theta+ Math.toRadians(30d));
        double y2 = tipY - (arrowLength - deltaLength)* Math.sin(theta+ Math.toRadians(30d));
        float x3 = (float) (tipX - (arrowLength-deltaLength)* Math.cos(theta+ Math.toRadians(-30d)));
        float y3 = (float) (tipY -(arrowLength-deltaLength)* Math.sin(theta+ Math.toRadians(-30d)));
        float x4 = (float) (tipX - (arrowLength)* Math.cos(theta+ Math.toRadians(-45d)));
        float y4 = (float) (tipY - (arrowLength)* Math.sin(theta+ Math.toRadians(-45d)));
        this.mPathVirtualDraw.moveTo(tipX, tipY);
        this.mPathVirtualDraw.quadTo(tipX, tipY, (float) x1, (float) y1);
        this.mPathVirtualDraw.quadTo((float) x1, (float) y1, (float) x2, (float) y2);
        this.mPathVirtualDraw.quadTo((float) x2, (float) y2, tailX, tailY);
        this.mPathVirtualDraw.quadTo(tailX,tailY,x3,y3);
        this.mPathVirtualDraw.quadTo(x3,y3,x4,y4);
        this.mPathVirtualDraw.quadTo(x4,y4,tipX,tipY);
        this.mtailArrowX = tailX;
        this.mtailArrowY = tailY;
        this.mtipArrowX = tipX;
        this.mtipArrowY = tipY;
    }

    private float verifyCoordinateValue(float value, float max) {
        if (value > 0.0f) {
            return 0.0f;
        }
        if (value < (-max)) {
            return -max;
        }
        return value;
    }

    private void processBitmap(CustomerShapeView shape) {
        if (shape != null && shape.mType == 4) {
            if (((BlurObject) shape).getBlurManager() == null) {
                ((BlurObject) shape).setBlurManager(this._blurManager);
                ((BlurObject) shape).setxMargin((float) this.xMainBitmap);
                ((BlurObject) shape).setyMargin((float) this.yMainBitmap);
            }
            if (shape.isVisible) {
                ((BlurObject) shape).showBlurObj();
            } else {
                ((BlurObject) shape).hideBlurObj();
            }
        }
    }

    public void resetData() {
        this.mPath.reset();
        this.mPathVirtualDraw.reset();
        listIndexCornerPath.clear();
        listPoint.clear();
        listIntersection.clear();
        listPointArrow.clear();
        isTouchOnTextEdit = false;
        isTouchOnEditTextSession = false;
        this.isTouchOnShape = false;
        this.isTouchOnPointFocusShape = false;
        this.mXStart = 0.0f;
        this.mYStart = 0.0f;
        this.distanceZigZag = 0;
        this.isFingerMove = false;
        this.mXStartVirtual = 0.0f;
        this.mYStartVirtual = 0.0f;
        this.isDrawVirtualBodyArrow = false;
        if (this.mVirtualBlurObj != null) {
            this.mVirtualBlurObj.hideBlurObj();
            this.mVirtualBlurObj = null;
        }
    }

    public void changeDrawingObjectPosition() {
        FlaotPoint oldBitmapMarin;
        FlaotPoint newBitmapMargin = new FlaotPoint(0,0);
        Iterator it = this.listRootShape.iterator();
        while (it.hasNext()) {
            float scale;
            CustomerShapeView v = (CustomerShapeView) it.next();
            if (v != null) {
                oldBitmapMarin = v.mOldFlaotPointMarginBitmap;
                scale = 1.0f;
                if (v.mType != 6) {
                    v.scaleAndChangePosition(oldBitmapMarin, newBitmapMargin, scale);
                } else {
                    v.scaleAndChangePositionForTextObject(oldBitmapMarin, newBitmapMargin, scale);
                }
                v.mWdithBitamp = (float) UIUtil.DeviceInfo.getDeviceScreenWidth();
                v.mHeighBitmap = (float) UIUtil.DeviceInfo.getDeviceScreenHeight()-DisplayUtil.dip2px(AnnotationInitialize.getInstance().getContext(),110);
            }
        }
        if (this.mCurrentShape != null) {
            oldBitmapMarin = this.mCurrentShape.mOldFlaotPointMarginBitmap;
            float scale = 1.0f;
            if (this.mCurrentShape.mType != 6) {
                this.mCurrentShape.scaleAndChangePosition(oldBitmapMarin, newBitmapMargin, scale);
            } else {
                this.mCurrentShape.scaleAndChangePositionForTextObject(oldBitmapMarin, newBitmapMargin, scale);
            }
            this.mCurrentShape.mWdithBitamp = (float)UIUtil.DeviceInfo.getDeviceScreenWidth();
//            this.mCurrentShape.mHeighBitmap = (float)UIUtil.DeviceInfo.getDeviceScreenHeight()- UIUtil.dpToPx(110);
            this.mCurrentShape.mHeighBitmap = (float)UIUtil.DeviceInfo.getDeviceScreenHeight();
        }
    }

    private boolean FocusOnText(int indexShapeInList, float xTouch, float yTouch, boolean isDown) {
        boolean flag = false;
        TextObject textObj = (TextObject) this.listRootShape.get(indexShapeInList);
        if (!DrawUtils.checkTouchPointInSideRectangleShape(textObj.x, textObj.y, textObj.w, textObj.h, xTouch, yTouch)) {
            return false;
        }
        if (this.indexCurrentTouchOnShape != indexShapeInList && this.indexCurrentTouchOnShape < this.listRootShape.size()) {
            if (this.listRootShape.get(this.indexCurrentTouchOnShape).isFocus) {
                this.isTouchOnPointFocusShape = checkTouchOnPointFocusShape((float) ((int) xTouch), (float) ((int) yTouch));
                if (this.isTouchOnPointFocusShape) {
                    return true;
                }
            }
            this.listRootShape.get(this.indexCurrentTouchOnShape).isFocus = false;
        }
        this.indexCurrentTouchOnShape = indexShapeInList;
        if (textObj.isFocus) {
            flag = true;
        } else if (!isDown) {
            flag = true;
            textObj.isFocus = true;
        }
        return flag;
    }

    private boolean FocusOnRecord(int indexShapeInList, float xTouch, float yTouch, boolean isDown) {
        boolean flag = false;
        RecordObject recordObject = (RecordObject) this.listRootShape.get(indexShapeInList);
        if (!DrawUtils.checkTouchPointInSideRectangleShape(recordObject.x, recordObject.y, recordObject.w+recordObject.x, recordObject.h+recordObject.y, xTouch, yTouch)) {
            return false;
        }
        if (this.indexCurrentTouchOnShape != indexShapeInList && this.indexCurrentTouchOnShape < this.listRootShape.size()) {
            if (this.listRootShape.get(this.indexCurrentTouchOnShape).isFocus) {
                this.isTouchOnPointFocusShape = checkTouchOnPointFocusShape(xTouch,yTouch);
                if (this.isTouchOnPointFocusShape) {
                    return true;
                }
            }
            this.listRootShape.get(this.indexCurrentTouchOnShape).isFocus = false;
        }
        this.indexCurrentTouchOnShape = indexShapeInList;
        if (recordObject.isFocus) {
            flag = true;
        } else if (!isDown) {
            flag = true;
            recordObject.isFocus = true;
        }
        return flag;
    }

    public boolean checkFocusOnShape(float xTouch, float yTouch, boolean isDown) {
        for (int i = this.listRootShape.size() - 1; i >= 0; i--) {
            if (this.listRootShape.get(i).isVisible) {
                switch (this.listRootShape.get(i).mType) {
                    case 6:
                        if (!isScaleMode&&FocusOnText(i, xTouch, yTouch, isDown)) {
                            return true;
                        }
                        break;
                    case 8:
                        if(FocusOnRecord(i,xTouch,yTouch,isDown)){
                           return true;
                        }
                        break;
                }
            }
        }
        return false;
    }

    boolean isTouchOnTextEdit;
    boolean isTouchOnEditTextSession;
    private void touch_start(float x, float y) {
        if (this.mainBitmap != null && isEdit) {
            if (listener!=null&&indexCurrentTouchOnShape < listRootShape.size() && mCurrentShape != null&&mCurrentShape.mType==6&&mCurrentShape.isFocus&&!isScaleMode){
                int touchTextPoint = checkTouchOnFocusTextArrea(x, y);
                if (touchTextPoint == TOUCH_ON_TEXT_DEL) {
                    isWaittingUp=true;
                    isTouchOnShape = true;
                    deleteCurrentShape();
                    return;
                } else if (touchTextPoint == TOUCH_ON_TEXT_EDIT) {
                    isTouchOnTextEdit = true;
                    isTouchOnEditTextSession = true;
                    isEditTextMode= true;
                    String edit = ((TextObject)mCurrentShape).getText();
                    mCurrentShape.isVisible = false;
                    mCurrentShape.isFocus =false;
                    editText.setTextColor(mCurrentSettingPaint.getColor());
                    editText.setText(edit);
                    editText.setVisibility(VISIBLE);
                    InputWindowUtil.forceShowInputWindow(mContext,editText);
                    editText.setX(mCurrentShape.x);
                    editText.setY(mCurrentShape.y);

//                    listener.clickOnEditCurrentText(((TextObject)mCurrentShape).getText());
                    return;
                }
            }

            clearFocusAllShape();
            isTouchOnShape = checkFocusOnShape(x, y, false);
            if (isTouchOnShape) {
                jumpShapeToTopLayer();
                this.isTouchOnShape = true;
                this.mXStart = x;
                this.mYStart = y;
            } else {
                if (isScaleMode) {
                    return;
                }
                resetData();
                this.mXStart = x;
                this.mYStart = y;
                this.mPath.moveTo(x, y);
                this.mXStartVirtual = x;
                this.mYStartVirtual = y;
            }
        }
    }

    private void touch_move(float x, float y) {
        this.isFingerMove = true;
        if (!this.isWaittingUp && this.mainBitmap != null && this.isEdit && !this.isNotDrawWhenCloseMenu) {
           if (this.isTouchOnPointFocusShape) {
            } else if (this.isTouchOnShape) {
                MoveShape(x, y);
            } else {
                float dx = Math.abs(x - this.mXStart);
                float dy = Math.abs(y - this.mYStart);
                if (checkTouchMovingToDraw(x, y, this.mXStart, this.mYStart)) {
                    this.mPath.quadTo(this.mXStart, this.mYStart, (this.mXStart + x) / 2.0f, (this.mYStart + y) / 2.0f);
                    this.mXStart = x;
                    this.mYStart = y;
                    this.distanceZigZag = (int) (this.distanceZigZag + dx);
                }
                if (this.mModeCreateObjPaint == 1) {
                    virtualArrowPath(this.mXStartVirtual, this.mYStartVirtual, x, y);
                } else if (this.mModeCreateObjPaint == 2) {
                    virtualRectPath(this.mXStartVirtual, this.mYStartVirtual, x, y);
                } else if (this.mModeCreateObjPaint == 3) {
                    virtualCircle(this.mXStartVirtual, this.mYStartVirtual, x, y);
                } else if (this.mModeCreateObjPaint == 4) {
                    virtualBlurObj(this.mXStartVirtual, this.mYStartVirtual, x, y);
                }
            }
        }
    }

    boolean checkTouchMovingToDraw(float x1, float y1, float x2, float y2) {
        return Math.abs(x1 - x2) >= slop || Math.abs(y1 - y2) >= slop;
    }

    private void touch_up(float x, float y) {
        if (isLongPressOnVoiceOrText) {
            isLongPressOnVoiceOrText = false;
            this.mCurrentShape._action = MoveOrResizeShape;
            showShapeAndFocus(this.mCurrentShape, false, false);
            clearRedoSharpFromIndex(this._undoRedoCurrentIndex);
            addNewSharp(this.mCurrentShape);
            this.mCurrentShape = this.mCurrentShape.copyCustomerView();
            showShapeAndFocus(this.mCurrentShape, true, true);
            this.mCurrentShape._action = MoveShapeToTop;
            invalidate();
            return;
        }
        if (this.isWaittingUp) {
            this.isWaittingUp = false;
        } else if (this.mainBitmap != null && this.isEdit) {
            if (this.isNotDrawWhenCloseMenu) {
                this.isNotDrawWhenCloseMenu = false;
            } else {
                if (this.isTouchOnShape || this.isTouchOnPointFocusShape) {
                    if (isCheckDeleteCurrentShape(x, y)) {
                        deleteCurrentShape();
                    } else if (this.mCurrentShape != null && this.mCurrentShape._action == MoveOrResizeShape) {
                        this.mCurrentShape._action = MoveOrResizeShape;
                        showShapeAndFocus(this.mCurrentShape, false, false);
                        clearRedoSharpFromIndex(this._undoRedoCurrentIndex);
                        addNewSharp(this.mCurrentShape);
                        this.mCurrentShape = this.mCurrentShape.copyCustomerView();
                        showShapeAndFocus(this.mCurrentShape, true, true);
                        this.mCurrentShape._action = MoveShapeToTop;
                    }
                    return;
                }
                if (this.mModeCreateObjPaint == 1) {
                    createArrow(this.mtailArrowX, this.mtailArrowY, this.mtipArrowX, this.mtipArrowY);
                } else if (this.mModeCreateObjPaint == 2) {
                    createRectangle(this.mPathVirtualDraw);
                } else if (this.mModeCreateObjPaint == 3) {
                    createCircle(this.mPathVirtualDraw);
                } else if (this.mModeCreateObjPaint == 4) {
                    createZigZag(this.mPathVirtualDraw);
                } else if (this.mModeCreateObjPaint == 0) {
                    createFreeStyleDraw();
                }else if(this.mModeCreateObjPaint == 7){
                    createEraserDraw();
                }
                checkHeaderStatus();
                resetData();
                this.indexCurrentTouchOnShape = this.listRootShape.size() - 1;
                if (this.indexCurrentTouchOnShape < 0) {
                    this.indexCurrentTouchOnShape = 0;
                }
                jumpShapeToTopLayer();
                clearFocusAllShape();
            }
        }
    }

    public boolean TouchOnPointTextFocus(float x, float y) {
        TextObject textObj = (TextObject)mCurrentShape;
        if (!textObj.isFocus) {
            return false;
        }
        for (int i = 0; i < textObj.getmListDot().size(); i++) {
            if (DrawUtils.checkTouchPointInSideDotPoint(textObj.getmListDot().get(i), x, y)) {
                this.mIndexDotCurrentShape = i;
                return true;
            }
        }
        return false;
    }

    private static final int TOUCH_ON_TEXT_DEL = 1;
    private static final int TOUCH_ON_TEXT_EDIT = 2;
    private static final int TOUCH_ON_TEXT_NONE = 0;
    public int checkTouchOnFocusTextArrea(float x,float y) {
        int result = TOUCH_ON_TEXT_NONE;
        TextObject textObj = (TextObject)mCurrentShape;
        if (textObj.isFocus) {
            DotObject dotDel = textObj.getmListDot().get(1);
            DotObject dotEdit = textObj.getmListDot().get(2);
            float helpWidth = textObj.getClickBitmapWidth();
            RectF delRect = new RectF(dotDel.getmX()-helpWidth,dotDel.getmY()-helpWidth,dotDel.getmX()+helpWidth,dotDel.getmY()+helpWidth);
            if(delRect.contains(x,y)){
                return 1;
            }
            RectF editRect = new RectF(dotEdit.getmX()-helpWidth,dotEdit.getmY()-helpWidth,dotEdit.getmX()+helpWidth,dotEdit.getmY()+helpWidth);
            if (editRect.contains(x, y)) {
                return 2;
            }
        }
        return result;
    }

    public boolean checkTouchOnPointFocusShape(float x, float y) {
        if (indexCurrentTouchOnShape >= listRootShape.size() || mCurrentShape == null) {
            return false;
        }
        switch (this.mCurrentShape.mType) {
            case 6:
                return TouchOnPointTextFocus(x, y);
            default:
                return false;
        }
    }


    public void jumpShapeToTopLayer() {
        clearFocusAllShape();
        if (indexCurrentTouchOnShape < listRootShape.size() && indexCurrentTouchOnShape > -1) {
            CustomerShapeView currentTouchShape = listRootShape.get(indexCurrentTouchOnShape);
            if (currentTouchShape == null || !(currentTouchShape instanceof TextObject) || currentTouchShape.isVisible) {
                showShapeAndFocus(currentTouchShape, false, false);
                if (!(this.mCurrentShape == null || !this.mCurrentShape.isFocus || this.mCurrentShape._shapeId == currentTouchShape._shapeId)) {
                    CustomerShapeView previousShape = getPreviousShape(this.mCurrentShape._shapeId);
                    if (previousShape != null) {
                        showShapeAndFocus(previousShape, true, false);
                    }
                }
                currentTouchShape.mContext = getContext();
                mCurrentShape = currentTouchShape.copyCustomerView();
                mCurrentShape._shapeId = currentTouchShape._shapeId;
                mCurrentShape._action = MoveShapeToTop;
                showShapeAndFocus(mCurrentShape, true, true);
            }
        }
    }

    private void showShapeAndFocus(CustomerShapeView shape, boolean isVisible, boolean isFocus) {
        if (shape != null) {
            shape.isFocus = isFocus;
            shape.isVisible = isVisible;
        }
    }

    public void deleteCurrentShape() {
        if (this.isTouchOnShape && this.indexCurrentTouchOnShape < this.listRootShape.size()) {
            mCurrentShape.isVisible = false;
            mCurrentShape.isFocus = false;
            mCurrentShape._action = DeleteShape;
            addNewSharp(mCurrentShape);
            mCurrentShape = null;
            indexCurrentTouchOnShape = 0;
            isTouchOnShape = false;
            isTouchOnPointFocusShape = false;
            checkHeaderStatus();
            postInvalidate();
        }
    }

    public boolean isCheckDeleteCurrentShape(float x, float y) {
        return y >= ((float) (this.yMainBitmap + this.mainBitmap.getHeight())) || ((float) this.yMainBitmap) >= y || x <= ((float) this.xMainBitmap) || x >= ((float) (this.xMainBitmap + this.mainBitmap.getWidth()));
    }

    public void clearFocusAllShape() {
        for (int i = 0; i < listRootShape.size(); i++) {
            if (listRootShape.get(i) != null) {
                listRootShape.get(i).isFocus = false;
            }
        }
        if (mCurrentShape != null) {
            showShapeAndFocus(getPreviousShape(mCurrentShape._shapeId), true, false);
            mCurrentShape = null;
        }
        postInvalidate();
    }

    private void moveRectangle(float x, float y) {
        float detaX = this.mXStart - x;
        float detaY = this.mYStart - y;
        RectangleObject rectangle = (RectangleObject) this.mCurrentShape;
        if (Math.abs(detaX)>0|| Math.abs(detaY)>0) {
            if (Math.abs(detaX)>0) {
                rectangle.x -= detaX;
                rectangle.w -= detaX;
                for (int i = 0; i < rectangle.getmListDot().size(); i++) {
                    rectangle.getmListDot().get(i).setmX(rectangle.getmListDot().get(i).getmX() - detaX);
                }
            }
            if (Math.abs(detaY)>0) {
                rectangle.y -= detaY;
                rectangle.h -= detaY;
                for (int i = 0; i < rectangle.getmListDot().size(); i++) {
                    rectangle.getmListDot().get(i).setmY(rectangle.getmListDot().get(i).getmY() - detaY);
                }
            }
            rectangle.initNewVirtualPoint();
            this.mXStart = x;
            this.mYStart = y;
        }
    }

    private void moveBlur(float x, float y) {
        float detaX = this.mXStart - x;
        float detaY = this.mYStart - y;
        BlurObject blur = (BlurObject) mCurrentShape;
        if (Math.abs(detaX)>0 || Math.abs(detaY)>0) {
            boolean flagReloadBlurBitmap = false;
            if (Math.abs(detaX)>0) {
                blur.x -= detaX;
                blur.w -= detaX;
                for (int i = 0; i < blur.getmListDot().size(); i++) {
                    blur.getmListDot().get(i).setmX(blur.getmListDot().get(i).getmX() - detaX);
                }
                flagReloadBlurBitmap = true;
            }
            if (Math.abs(detaY)>0) {
                blur.y -= detaY;
                blur.h -= detaY;
                for (int i = 0; i < blur.getmListDot().size(); i++) {
                    blur.getmListDot().get(i).setmY(blur.getmListDot().get(i).getmY() - detaY);
                }
                flagReloadBlurBitmap = true;
            }
            if (flagReloadBlurBitmap) {
                blur.initNewPointPosition(blur.x, blur.y, blur.w, blur.h);
                blur.resizeBlurBitmap(blur.x, blur.y, blur.w, blur.h);
            }
            this.mXStart = x;
            this.mYStart = y;
        }
    }

    private void moveFreeStyleShape(float x, float y) {
        float detaX = this.mXStart - x;
        float detaY = this.mYStart - y;
        FreeStyleObject freeStyle = (FreeStyleObject)mCurrentShape;
        if (Math.abs(detaX)>0 || Math.abs(detaY)>0) {
            freeStyle.initNewPointPositionMoveShape(detaX, detaY);
            this.mXStart = x;
            this.mYStart = y;
        }
    }

    private void moveText(float x, float y) {
        float detaX = this.mXStart - x;
        float detaY = this.mYStart - y;
        TextObject textObj = (TextObject)mCurrentShape;
        if (Math.abs(detaX)>0 || Math.abs(detaY)>0) {
            textObj.x -= detaX;
            textObj.w -= detaX;
            textObj.y -= detaY;
            textObj.h -= detaY;
            for (int i = 0; i < textObj.getmListDot().size(); i++) {
                textObj.getmListDot().get(i).setmX(textObj.getmListDot().get(i).getmX() - detaX);
                textObj.getmListDot().get(i).setmY(textObj.getmListDot().get(i).getmY() - detaY);
            }
            textObj.moveText();
            this.mXStart = x;
            this.mYStart = y;
        }
    }

    private void moveRecord(float x, float y) {
        float detaX = this.mXStart - x;
        float detaY = this.mYStart - y;
        RecordObject recordObj = (RecordObject) mCurrentShape;
        if (Math.abs(detaX)>0 || Math.abs(detaY)>0) {
            recordObj.x = x;
            recordObj.y = y;
            recordObj.move();
            this.mXStart = x;
            this.mYStart = y;
        }
    }

    private void moveArrow(float x, float y) {
        float detaX = this.mXStart - x;
        float detaY = this.mYStart - y;
        ArrowObject arrow = (ArrowObject)mCurrentShape;
        if (Math.abs(detaX)>0 || Math.abs(detaY)>0) {
            boolean flagReloadBlurBitmap = false;
            if (Math.abs(detaX)>0) {
                arrow.setTailX(arrow.getTailX() - detaX);
                arrow.setTipX(arrow.getTipX() - detaX);
                for (int i = 0; i < arrow.getmListDot().size(); i++) {
                    arrow.getmListDot().get(i).setmX(arrow.getmListDot().get(i).getmX() - detaX);
                }
                flagReloadBlurBitmap = true;
            }
            if (Math.abs(detaY)>0) {
                arrow.setTailY(arrow.getTailY() - detaY);
                arrow.setTipY(arrow.getTipY() - detaY);
                for (int i = 0; i < arrow.getmListDot().size(); i++) {
                    arrow.getmListDot().get(i).setmY(arrow.getmListDot().get(i).getmY() - detaY);
                }
                flagReloadBlurBitmap = true;
            }
            if (flagReloadBlurBitmap) {
                arrow.initNewPointPositionMoveShape();
            }
            this.mXStart = x;
            this.mYStart = y;
        }
    }

    private void moveEllipse(float x, float y) {
        float detaX = this.mXStart - x;
        float detaY = this.mYStart - y;
        EllipseObject ellipse = (EllipseObject) this.mCurrentShape;
        if (Math.abs(detaX)>0 || Math.abs(detaY)>0) {
            RectF rectF;
            if (Math.abs(detaX) >0) {
                rectF = ellipse.getRectF();
                rectF.left -= detaX;
                ellipse.x -= detaX;
                rectF = ellipse.getRectF();
                rectF.right -= detaX;
                ellipse.w -= detaX;
                for (int i = 0; i < ellipse.getmListDot().size(); i++) {
                    ellipse.getmListDot().get(i).setmX(ellipse.getmListDot().get(i).getmX() - detaX);
                }
            }
            if (Math.abs(detaY)> 0) {
                rectF = ellipse.getRectF();
                rectF.top -= detaY;
                ellipse.y -= detaY;
                rectF = ellipse.getRectF();
                rectF.bottom -= detaY;
                ellipse.h -= detaY;
                for (int i = 0; i < ellipse.getmListDot().size(); i++) {
                    ellipse.getmListDot().get(i).setmY(ellipse.getmListDot().get(i).getmY() - detaY);
                }
            }
            ellipse.initNewVirtualPoint();
            this.mXStart = x;
            this.mYStart = y;
        }
    }

    public void MoveShape(float x, float y) {
        if (mCurrentShape != null) {
            mCurrentShape._action = MoveOrResizeShape;
            switch (mCurrentShape.mType) {
//                case 1:
//                    moveRectangle(x, y);
//                    break;
//                case 2:
//                    moveEllipse(x, y);
//                    break;
//                case 3:
//                    moveArrow(x, y);
//                    break;
//                case 4:
//                    moveBlur(x, y);
//                    break;
//                case 5:
//                    moveFreeStyleShape(x, y);
//                    break;
                case 6:
                    moveText(x, y);
                    break;
//                case 8:
//                    moveRecord(x,y);
//                    break;
            }
        }
    }

    public void resizeText(float x, float y) {
        TextObject text = (TextObject) this.mCurrentShape;
        float detaX = this.mXStart - x;
        float detaY = this.mYStart - y;
        float tempWidth = text.w;
        float tempX = text.x;
        float f;
        float maxWidthText;
        switch (mIndexDotCurrentShape) {
            case 0:
                moveText(x, y);
                return;
            case 1:
                moveText(x, y);
                return;
            case 2:
                tempWidth -= ((float) text.alphaFrame) + detaX;
                if (tempWidth - tempX >= (text.getmMinWidthText() + ((float) text.alphaFrame)) + 2.0f) {
                    f = tempWidth - tempX;
                    maxWidthText = text.getMaxWidthText();
                    text.getClass();
                    if (f <= ((maxWidthText + 8.0f) + ((float) text.alphaFrame)) - 2.0f) {
                        text.w -= detaX;
                        break;
                    }
                    return;
                }
                return;
            case 3:
                tempX -= ((float) text.alphaFrame) + detaX;
                if (tempWidth - tempX >= (text.getmMinWidthText() + ((float) text.alphaFrame)) + 2.0f) {
                    f = tempWidth - tempX;
                    maxWidthText = text.getMaxWidthText();
                    text.getClass();
                    if (f <= ((maxWidthText + 8.0f) + ((float) text.alphaFrame)) - 2.0f) {
                        text.x -= detaX;
                        break;
                    }
                    return;
                }
                return;
        }
        text.h = text.y + text.detectHeighBound();
        text.resize_Or_Move_Object(text.x, text.y, text.w, text.h);
    }

    public void resizeRectangle(float x, float y) {
        RectangleObject rectangle = (RectangleObject) this.mCurrentShape;
        float detaX = this.mXStart - x;
        float detaY = this.mYStart - y;
        switch (mIndexDotCurrentShape) {
            case 0:
                rectangle.x -= detaX;
                rectangle.y -= detaY;
                break;
            case 1:
                rectangle.y -= detaY;
                break;
            case 2:
                rectangle.w -= detaX;
                rectangle.y -= detaY;
                break;
            case 3:
                rectangle.w -= detaX;
                break;
            case 4:
                rectangle.w -= detaX;
                rectangle.h -= detaY;
                break;
            case 5:
                rectangle.h -= detaY;
                break;
            case 6:
                rectangle.x -= detaX;
                rectangle.h -= detaY;
                break;
            case 7:
                rectangle.x -= detaX;
                break;
        }
        if (rectangle.x < 0.0f) {
            rectangle.x = 0.0f;
        }
        if (rectangle.y < 0.0f) {
            rectangle.y = 0.0f;
        }
        if (rectangle.w > ((float) this.wMainView)) {
            rectangle.w = (float) this.wMainView;
        }
        if (rectangle.h > ((float) this.hMainView)) {
            rectangle.h = (float) this.hMainView;
        }
        if (rectangle.x > rectangle.w - ((float) DrawUtils.ALPHA_RESIZE)) {
            rectangle.x = rectangle.w - ((float) DrawUtils.ALPHA_RESIZE);
        }
        if (rectangle.y > rectangle.h - ((float) DrawUtils.ALPHA_RESIZE)) {
            rectangle.y = rectangle.h - ((float) DrawUtils.ALPHA_RESIZE);
        }
        if (rectangle.h - ((float) DrawUtils.ALPHA_RESIZE) < rectangle.y) {
            rectangle.h = rectangle.y + ((float) DrawUtils.ALPHA_RESIZE);
        }
        if (rectangle.w - ((float) DrawUtils.ALPHA_RESIZE) < rectangle.x) {
            rectangle.w = rectangle.x + ((float) DrawUtils.ALPHA_RESIZE);
        }
        rectangle.initNewVirtualPoint();
        rectangle.resize_Or_Move_Object(rectangle.x, rectangle.y, rectangle.w, rectangle.h);
    }

    public void resizeBlur(float x, float y) {
        BlurObject blur = (BlurObject) mCurrentShape;
        float detaX = this.mXStart - x;
        float detaY = this.mYStart - y;
        switch (this.mIndexDotCurrentShape) {
            case 0:
                blur.x -= detaX;
                blur.y -= detaY;
                break;
            case 1:
                blur.y -= detaY;
                break;
            case 2:
                blur.w -= detaX;
                blur.y -= detaY;
                break;
            case 3:
                blur.w -= detaX;
                break;
            case 4:
                blur.w -= detaX;
                blur.h -= detaY;
                break;
            case 5:
                blur.h -= detaY;
                break;
            case 6:
                blur.x -= detaX;
                blur.h -= detaY;
                break;
            case 7:
                blur.x -= detaX;
                break;
        }
        if (blur.x < 0.0f) {
            blur.x = 0.0f;
        }
        if (blur.y < 0.0f) {
            blur.y = 0.0f;
        }
        if (blur.w > ((float) this.wMainView)) {
            blur.w = (float) this.wMainView;
        }
        if (blur.h > ((float) this.hMainView)) {
            blur.h = (float) this.hMainView;
        }
        if (blur.x > blur.w - ((float) DrawUtils.ALPHA_RESIZE)) {
            blur.x = blur.w - ((float) DrawUtils.ALPHA_RESIZE);
        }
        if (blur.y > blur.h - ((float) DrawUtils.ALPHA_RESIZE)) {
            blur.y = blur.h - ((float) DrawUtils.ALPHA_RESIZE);
        }
        if (blur.h - ((float) DrawUtils.ALPHA_RESIZE) < blur.y) {
            blur.h = blur.y + ((float) DrawUtils.ALPHA_RESIZE);
        }
        if (blur.w - ((float) DrawUtils.ALPHA_RESIZE) < blur.x) {
            blur.w = blur.x + ((float) DrawUtils.ALPHA_RESIZE);
        }
        blur.initNewPointPosition(blur.x, blur.y, blur.w, blur.h);
        blur.resizeBlurBitmap(blur.x, blur.y, blur.w, blur.h);
    }

    public void resizeArrow(float x, float y) {
        ((ArrowObject)this.mCurrentShape).initNewPointPosition(this.mIndexDotCurrentShape, x, y);
    }

    public void resizeFreeStyleObj(float x, float y) {
        FreeStyleObject freeStyleObj = (FreeStyleObject) this.mCurrentShape;
        float detaX = mXStart - x;
        float detaY = mYStart - y;
        switch (mIndexDotCurrentShape) {
            case 0:
                freeStyleObj.x -= detaX;
                freeStyleObj.y -= detaY;
                break;
            case 1:
                freeStyleObj.y -= detaY;
                break;
            case 2:
                freeStyleObj.w -= detaX;
                freeStyleObj.y -= detaY;
                break;
            case 3:
                freeStyleObj.w -= detaX;
                break;
            case 4:
                freeStyleObj.w -= detaX;
                freeStyleObj.h -= detaY;
                break;
            case 5:
                freeStyleObj.h -= detaY;
                break;
            case 6:
                freeStyleObj.x -= detaX;
                freeStyleObj.h -= detaY;
                break;
            case 7:
                freeStyleObj.x -= detaX;
                break;
        }
        if (freeStyleObj.x < 0.0f) {
            freeStyleObj.x = 0.0f;
        }
        if (freeStyleObj.y < 0.0f) {
            freeStyleObj.y = 0.0f;
        }
        if (freeStyleObj.w > ((float) this.wMainView)) {
            freeStyleObj.w = (float) this.wMainView;
        }
        if (freeStyleObj.h > ((float) this.hMainView)) {
            freeStyleObj.h = (float) this.hMainView;
        }
        if (freeStyleObj.x > freeStyleObj.w - ((float) DrawUtils.ALPHA_RESIZE)) {
            freeStyleObj.x = freeStyleObj.w - ((float) DrawUtils.ALPHA_RESIZE);
        }
        if (freeStyleObj.y > freeStyleObj.h - ((float) DrawUtils.ALPHA_RESIZE)) {
            freeStyleObj.y = freeStyleObj.h - ((float) DrawUtils.ALPHA_RESIZE);
        }
        if (freeStyleObj.h - ((float) DrawUtils.ALPHA_RESIZE) < freeStyleObj.y) {
            freeStyleObj.h = freeStyleObj.y + ((float) DrawUtils.ALPHA_RESIZE);
        }
        if (freeStyleObj.w - ((float) DrawUtils.ALPHA_RESIZE) < freeStyleObj.x) {
            freeStyleObj.w = freeStyleObj.x + ((float) DrawUtils.ALPHA_RESIZE);
        }
        freeStyleObj.resizePath(freeStyleObj.x, freeStyleObj.y, freeStyleObj.w, freeStyleObj.h);
    }

    public void resizeEllipse(float x, float y) {
        EllipseObject ellipse = (EllipseObject) mCurrentShape;
        float detaX = mXStart - x;
        float detaY = mYStart - y;
        RectF rectF;
        switch (this.mIndexDotCurrentShape) {
            case 0:
                rectF = ellipse.getRectF();
                rectF.left -= detaX;
                rectF = ellipse.getRectF();
                rectF.top -= detaY;
                break;
            case 1:
                rectF = ellipse.getRectF();
                rectF.top -= detaY;
                break;
            case 2:
                rectF = ellipse.getRectF();
                rectF.right -= detaX;
                rectF = ellipse.getRectF();
                rectF.top -= detaY;
                break;
            case 3:
                rectF = ellipse.getRectF();
                rectF.right -= detaX;
                break;
            case 4:
                rectF = ellipse.getRectF();
                rectF.right -= detaX;
                rectF = ellipse.getRectF();
                rectF.bottom -= detaY;
                break;
            case 5:
                rectF = ellipse.getRectF();
                rectF.bottom -= detaY;
                break;
            case 6:
                rectF = ellipse.getRectF();
                rectF.left -= detaX;
                rectF = ellipse.getRectF();
                rectF.bottom -= detaY;
                break;
            case 7:
                rectF = ellipse.getRectF();
                rectF.left -= detaX;
                break;
        }
        if (ellipse.getRectF().left < 0.0f) {
            ellipse.getRectF().left = 0.0f;
        }
        if (ellipse.getRectF().top < 0.0f) {
            ellipse.getRectF().top = 0.0f;
        }
        if (ellipse.getRectF().right > ((float)wMainView)) {
            ellipse.getRectF().right = (float)wMainView;
        }
        if (ellipse.getRectF().bottom > ((float)hMainView)) {
            ellipse.getRectF().bottom = (float)hMainView;
        }
        if (ellipse.getRectF().left > ellipse.getRectF().right - ((float) DrawUtils.ALPHA_RESIZE)) {
            ellipse.getRectF().left = ellipse.getRectF().right - ((float) DrawUtils.ALPHA_RESIZE);
        }
        if (ellipse.getRectF().top > ellipse.getRectF().bottom - ((float) DrawUtils.ALPHA_RESIZE)) {
            ellipse.getRectF().top = ellipse.getRectF().bottom - ((float) DrawUtils.ALPHA_RESIZE);
        }
        if (ellipse.getRectF().bottom - ((float) DrawUtils.ALPHA_RESIZE) < ellipse.getRectF().top) {
            ellipse.getRectF().bottom = ellipse.getRectF().top + ((float) DrawUtils.ALPHA_RESIZE);
        }
        if (ellipse.getRectF().right - ((float) DrawUtils.ALPHA_RESIZE) < ellipse.getRectF().left) {
            ellipse.getRectF().right = ellipse.getRectF().left + ((float) DrawUtils.ALPHA_RESIZE);
        }
        ellipse.initNewPointPosition(ellipse.getRectF().left, ellipse.getRectF().top, ellipse.getRectF().right, ellipse.getRectF().bottom);
    }

    private void resizeCurrentShapeFocus(float x, float y) {
        if (mCurrentShape != null) {
            mCurrentShape._action = MoveOrResizeShape;
            switch (mCurrentShape.mType) {
                case 1:
                    resizeRectangle(x, y);
                    break;
                case 2:
                    resizeEllipse(x, y);
                    break;
                case 3:
                    resizeArrow(x, y);
                    break;
                case 4:
                    resizeBlur(x, y);
                    break;
                case 5:
                    resizeFreeStyleObj(x, y);
                    break;
                case 6:
                    resizeText(x, y);
                    break;
            }
            this.mXStart = x;
            this.mYStart = y;
        }
    }

    public void getCollectionPointsCornerPath() {
        FlaotPoint[] points = getListPoints();
        for (int i = 0; i < this.listIndexCornerPath.size(); i++) {
            int index = listIndexCornerPath.get(i);
            if (index < points.length) {
                listPoint.add(points[index]);
            } else if (index > 0) {
                listPoint.add(points[index - 1]);
            }
        }
    }

    public void actionUndo() {
        _undoRedoCurrentIndex--;
        if (_undoRedoCurrentIndex < 0) {
            _undoRedoCurrentIndex = 0;
        }
        if (_undoRedoCurrentIndex != -1 && _undoRedoCurrentIndex < listRootShape.size() && listRootShape.size() > 0) {
            CustomerShapeView shape = listRootShape.get(_undoRedoCurrentIndex);
            switch (shape._action) {
                case AddNewShape://1:
                    showShapeAndFocus(shape, false, false);
                    break;
                case MoveOrResizeShape://2:
                    showShapeAndFocus(shape, false, false);
                    showShapeAndFocus(getPreviousShape(shape._shapeId), true, false);
                    break;
                case DeleteShape://5:
                    showShapeAndFocus(shape, false, false);
                    showShapeAndFocus(getPreviousShape(shape._shapeId), true, false);
                    break;
                case ChangeBlurOpacity://6:
                    showShapeAndFocus(shape, false, false);
                    showShapeAndFocus(getPreviousShape(shape._shapeId), true, false);
                    break;
                case ChangeTextColorOrStroke://7:
                    showShapeAndFocus(shape, false, false);
                    showShapeAndFocus(getPreviousShape(shape._shapeId), true, false);
                    break;
            }
            if (this.mCurrentShape != null && this.mCurrentShape._shapeId == shape._shapeId) {
                this.mCurrentShape = null;
            }
        }
        checkHeaderStatus();
        invalidate();
    }

    public void actionRedo() {
        if (_undoRedoCurrentIndex != -1 && _undoRedoCurrentIndex < listRootShape.size() && listRootShape.size() > 0) {
            CustomerShapeView shape = listRootShape.get(_undoRedoCurrentIndex);
            switch (shape._action) {
                case AddNewShape://1
                    showShapeAndFocus(shape, true, false);
                    break;
                case MoveOrResizeShape://2
                    showShapeAndFocus(shape, true, false);
                    showShapeAndFocus(getPreviousShape(shape._shapeId), false, false);
                    break;
                case DeleteShape://5:
                    showShapeAndFocus(shape, false, false);
                    showShapeAndFocus(getPreviousShape(shape._shapeId), false, false);
                    break;
                case ChangeBlurOpacity://6:
                    showShapeAndFocus(shape, true, false);
                    showShapeAndFocus(getPreviousShape(shape._shapeId), false, false);
                    break;
                case ChangeTextColorOrStroke://7:
                    showShapeAndFocus(shape, true, false);
                    showShapeAndFocus(getPreviousShape(shape._shapeId), false, false);
                    break;
            }
            if (mCurrentShape != null && mCurrentShape._shapeId == shape._shapeId) {
                mCurrentShape = null;
            }
        }
        _undoRedoCurrentIndex++;
        checkHeaderStatus();
        invalidate();
    }

    public void checkHeaderStatus() {
        if (this.listener != null) {
            this.listener.isOnOff_Undo_Redo(enableUndo(), enableRedo());
        }
    }

    boolean enableUndo() {
        return !(_undoRedoCurrentIndex == -1 || _undoRedoCurrentIndex <= _undoRedoFirstIndex);
    }

    boolean enableRedo() {
        return !(_undoRedoCurrentIndex == -1 || listRootShape == null || _undoRedoCurrentIndex >= listRootShape.size());
    }

    protected CustomerShapeView getPreviousShape(int shapeId) {
        for (int i = _undoRedoCurrentIndex - 1; i >= 0; i--) {
            CustomerShapeView shape = listRootShape.get(i);
            if (shapeId == shape._shapeId) {
                return shape;
            }
        }
        return null;
    }

    public void getListIndexPointCornerPath() {
        float length = new PathMeasure(this.mPath, false).getLength();
        if (listIndexCornerPath.size() == 0) {
            listIndexCornerPath.add(Integer.valueOf((int) length));
            return;
        }
        int tempLength = (int) length;
        if ((listIndexCornerPath.get(listIndexCornerPath.size() - 1))!= tempLength) {
            listIndexCornerPath.add(tempLength);
        }
    }

    public FlaotPoint[] getListPoints() {
        PathMeasure pm = new PathMeasure(mPath, false);
        float length = pm.getLength();
        FlaotPoint[] pointArray = new FlaotPoint[((int) length)];
        float speed = length / ((float) ((int) length));
        int counter = 0;
        float[] aCoordinates = new float[]{0.0f, 0.0f};
        for (float distance = 0.0f; distance < length && counter < ((int) length); distance += speed) {
            pm.getPosTan(distance, aCoordinates, null);
            pointArray[counter] = new FlaotPoint(aCoordinates[0], aCoordinates[1]);
            counter++;
        }
        return pointArray;
    }

    public float[] getAbsolutePosition(float Ax, float Ay) {
        float x = (this.mTranslateX + Ax) / this.scaleFactor;
        float y = (this.mTranslateY + Ay) / this.scaleFactor;
        return new float[]{x, y};
    }

    public float getAbsoluteXPosition(float Ax) {
        return (this.mTranslateX + Ax) / this.scaleFactor;
    }

    protected float[] getViewPortPosition(float Ax, float Ay) {
        float x = scaleFactor * Ax - mTranslateX;
        float y = scaleFactor * Ay - mTranslateY;
        return new float[]{x, y};
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt((x * x) + (y * y));
    }

    private float[] midPoint(MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        return new float[]{x / 2.0f, y / 2.0f};
    }

    float mStartX, mStartY;
    float mUpX,mUpY;
    private static final String TAG = "DrawingView";
    @Override
    public boolean onTouchEvent(MotionEvent event) {
            float viewTouchX = event.getX();
            float viewTouchY = event.getY();
            float[] newTouch = getAbsolutePosition(viewTouchX, viewTouchY);
            float xTouch = newTouch[0];
            float yTouch = newTouch[1];
            this.mGestureDetector.onTouchEvent(event);
            this.mScaleDetector.onTouchEvent(event);
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    isTouchOnTextEdit = false;
                    isTouchOnEditTextSession = false;
                    this.startX = event.getX();
                    this.startY = event.getY();
                    mStartX = event.getX();
                    mStartY = event.getY();
                    this.mDrawingState = Configs.DrawingState.ListenForNewTouchPoint;
                    touch_start(xTouch, yTouch);
                    break;
                case MotionEvent.ACTION_UP:
                    mUpX = event.getX();
                    mUpY = event.getY();
                    if (isScaleMode) {
                        break;
                    }
                    if (Math.abs(mUpX - mStartX) < slop && Math.abs(mUpY - mStartY) < slop) {

                        /**
                         * if isedittext mode:
                         * 1. invisible or visbile but text is empty -> click trans edittext position show inputwindow
                         * 2. visbile and txt is not empty -> click hide edittext and hide inputwindow and new textobj
                         */

                        if (isEditTextMode) {
                            if (isTouchOnTextEdit) {
                                if (isTouchOnEditTextSession) {

                                    break;
                                } else {
                                    isTouchOnTextEdit = false;
                                    isEditTextMode = false;
                                    String text = editText.getText().toString();
                                    if (!TextUtils.isEmpty(text)) {
                                        if (getCurrentTextObject() != null) {
                                            if (!getCurrentTextObject().getText().equals(text)) {
                                                updateRealTextObject(text);
                                            } else {
                                                getCurrentTextObject().isVisible = true;
                                                getCurrentTextObject().isFocus = false;
                                            }
                                        }
                                    } else {
                                        deleteCurrentShape();
                                    }
                                    InputWindowUtil.forceHideInputWindow(getContext(), editText);
                                    editText.setText("");
                                    editText.setVisibility(GONE);
                                    invalidate();
                                    break;
                                }
                            }
                            if(editText.getVisibility()== View.GONE||(TextUtils.isEmpty(editText.getText().toString()))) {
                                editText.setX(mUpX);
                                editText.setY(mUpY);
                                editText.setTextColor(mCurrentSettingPaint.getColor());
                                editText.setVisibility(VISIBLE);
                                editText.requestFocus();
                                InputWindowUtil.forceShowInputWindow(getContext(), editText);
                            }else{
                                isEditTextMode = false;
                                InputWindowUtil.forceHideInputWindow(getContext(),editText);
                                createText(editText.getX(),editText.getY(),editText.getText().toString());
                                editText.setText("");
                                editText.setVisibility(GONE);
                                isWaittingUp = true;
                            }
                            break;
                        }
                    }


                    if (this.mDrawingState == Configs.DrawingState.DrawingOrMove) {
                        touch_up(xTouch, yTouch);
                    }
                    this.mDrawingState = Configs.DrawingState.Idle;
                    break;
                case MotionEvent.ACTION_MOVE:
                    switch (mDrawingState) {
                        case ListenForNewTouchPoint://2
                            if (isScaleMode) {
                                break;
                            }
//                            if (checkTouchMovingToDraw(xTouch, yTouch, this.mXStart, this.mYStart)) {
                                this.mDrawingState = Configs.DrawingState.DrawingOrMove;
                                touch_move(xTouch, yTouch);
//                            }
                            break;
                        case DrawingOrMove://3:
                            if (isScaleMode) {
                                break;
                            }
                            touch_move(xTouch, yTouch);
                            break;
                        case Pinch://4:
                            break;
                        case Pan://5:
                            float distanceX = event.getX() - this.startX;
                            float distanceY = event.getY() - this.startY;
                            if (Math.sqrt((double) ((distanceX * distanceX) + (distanceY * distanceY))) > 2.0d) {
                                this.oldTranlateX += distanceX;
                                this.oldTranlateY += distanceY;
                            }
                            this.startX = event.getX();
                            this.startY = event.getY();
                            break;
                        default:
                            break;
                    }
                case MotionEvent.ACTION_POINTER_DOWN:
                    if (this.mDrawingState != Configs.DrawingState.DrawingOrMove) {
                        if (event.getPointerCount() == 1&&isScaleMode) {
                            this.mDrawingState = Configs.DrawingState.Pan;
                            break;
                        }
                        this.mDrawingState = Configs.DrawingState.Pinch;
                        break;
                    }
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    if (this.mDrawingState == Configs.DrawingState.Pinch || this.mDrawingState == Configs.DrawingState.Pan) {
                        this.mDrawingState = Configs.DrawingState.DisableAction;
                        break;
                    }
            }
            if (this.mDrawingState != Configs.DrawingState.DisableAction) {
                invalidate();
            }
        return true;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.wMainView = w;
        this.hMainView = h;
    }

    public double angleBetween2Lines(FlaotPoint point1, FlaotPoint point2, FlaotPoint point3) {
        double line1_x2 = (double) point2.getX();
        double line1_y2 = (double) point2.getY();
        return Math.atan2(((double) point1.getY()) - line1_y2, ((double) point1.getX()) - line1_x2) - Math.atan2(line1_y2 - ((double) point3.getY()), line1_x2 - ((double) point3.getX()));
    }

    public double distance(FlaotPoint A, FlaotPoint B) {
        return Math.sqrt((double) (((B.getY() - A.getY()) * (B.getY() - A.getY())) + ((B.getX() - A.getX()) * (B.getX() - A.getX()))));
    }

    private void createArrow(float mtailArrowX, float mtailArrowY, float mtipArrowX, float mtipArrowY) {
        ArrowObject arrow = new ArrowObject(this.mContext, 3, mtailArrowX, mtailArrowY, mtipArrowX, mtipArrowY, this.mCurrentSettingPaint, new FlaotPoint((float) this.xMainBitmap, (float) this.yMainBitmap), (float) this.mainBitmap.getWidth(), (float) this.mainBitmap.getHeight());
        clearRedoSharpFromIndex(this._undoRedoCurrentIndex);
        arrow._shapeId = this.listRootShape.size();
        arrow._action = AddNewShape;
        arrow.isFocus = false;
        arrow.isVisible = true;
        addNewSharp(arrow);
    }


    private void createZigZag(Path mPathVirtualDraw) {
        RectF rectF = new RectF();
        mPathVirtualDraw.computeBounds(rectF, true);
        BlurObject blurObject = new BlurObject(this.mContext, this._blurManager, 4, (float) this.xMainBitmap, (float) this.yMainBitmap, rectF.left, rectF.top, rectF.right, rectF.bottom, this.mCurrentSettingPaint, new FlaotPoint((float) this.xMainBitmap, (float) this.yMainBitmap), (float) this.mainBitmap.getWidth(), (float) this.mainBitmap.getHeight());
        clearRedoSharpFromIndex(this._undoRedoCurrentIndex);
        blurObject._shapeId = this.listRootShape.size();
        blurObject._action = AddNewShape;
        blurObject.isFocus = false;
        blurObject.isVisible = true;
        addNewSharp(blurObject);
    }

    private void createCircle(Path mPathVirtualDraw) {
        RectF rectF = new RectF();
        mPathVirtualDraw.computeBounds(rectF, true);
        EllipseObject ellipseObject = new EllipseObject(this.mContext, 2, rectF.left, rectF.top, rectF.left + (rectF.right - rectF.left), rectF.top + (rectF.bottom - rectF.top), this.mCurrentSettingPaint, new FlaotPoint((float) this.xMainBitmap, (float) this.yMainBitmap), (float) this.mainBitmap.getWidth(), (float) this.mainBitmap.getHeight());
        clearRedoSharpFromIndex(this._undoRedoCurrentIndex);
        ellipseObject._shapeId = this.listRootShape.size();
        ellipseObject._action = AddNewShape;
        ellipseObject.isFocus = false;
        ellipseObject.isVisible = true;
        addNewSharp(ellipseObject);
    }

    private void createRectangle() {
        RectF rectF = new RectF();
        this.mPath.computeBounds(rectF, true);
        RectangleObject rectangle = new RectangleObject(this.mContext, 1, rectF.left, rectF.top, rectF.right, rectF.bottom, this.mCurrentSettingPaint, new FlaotPoint((float) this.xMainBitmap, (float) this.yMainBitmap), (float) this.mainBitmap.getWidth(), (float) this.mainBitmap.getHeight());
        clearRedoSharpFromIndex(this._undoRedoCurrentIndex);
        rectangle._shapeId = this.listRootShape.size();
        rectangle._action = AddNewShape;
        rectangle.isFocus = false;
        rectangle.isVisible = true;
        addNewSharp(rectangle);
    }

    private void createRectangle(Path mPathVirtualDraw) {
        RectF rectF = new RectF();
        mPathVirtualDraw.computeBounds(rectF, true);
        RectangleObject rectangle = new RectangleObject(this.mContext, 1, rectF.left, rectF.top, rectF.right, rectF.bottom, this.mCurrentSettingPaint, new FlaotPoint((float) this.xMainBitmap, (float) this.yMainBitmap), (float) this.mainBitmap.getWidth(), (float) this.mainBitmap.getHeight());
        clearRedoSharpFromIndex(this._undoRedoCurrentIndex);
        rectangle._shapeId = this.listRootShape.size();
        rectangle._action = AddNewShape;
        rectangle.isFocus = false;
        rectangle.isVisible = true;
        addNewSharp(rectangle);
    }

    private void createFreeStyleDraw() {
        RectF rectF = new RectF();
        this.mPath.computeBounds(rectF, true);
        float distanceX = Math.abs(rectF.right - rectF.left);
        float distanceY = Math.abs(rectF.top - rectF.bottom);
        if (distanceX >= HUE_ORANGE || distanceY >= HUE_ORANGE) {
            FreeStyleObject freeStyleObject = new FreeStyleObject(this.mContext, 5, rectF.left, rectF.top, rectF.left + (rectF.right - rectF.left), rectF.top + (rectF.bottom - rectF.top), this.mPath, this.mCurrentSettingPaint, new FlaotPoint((float) this.xMainBitmap, (float) this.yMainBitmap), (float) this.mainBitmap.getWidth(), (float) this.mainBitmap.getHeight());
            clearRedoSharpFromIndex(this._undoRedoCurrentIndex);
            freeStyleObject._shapeId = this.listRootShape.size();
            freeStyleObject._action = AddNewShape;
            freeStyleObject.isFocus = false;
            freeStyleObject.isVisible = true;
            addNewSharp(freeStyleObject);
        }
    }

    private void createEraserDraw() {
        RectF rectF = new RectF();
        this.mPath.computeBounds(rectF, true);
        float distanceX = Math.abs(rectF.right - rectF.left);
        float distanceY = Math.abs(rectF.top - rectF.bottom);
        if (distanceX >= HUE_ORANGE || distanceY >= HUE_ORANGE) {
            EraserObject eraser = new EraserObject(this.mContext, 7, rectF.left, rectF.top, rectF.left + (rectF.right - rectF.left), rectF.top + (rectF.bottom - rectF.top), this.mPath, this.mCurrentSettingPaint, new FlaotPoint((float) this.xMainBitmap, (float) this.yMainBitmap), (float) this.mainBitmap.getWidth(), (float) this.mainBitmap.getHeight());
            clearRedoSharpFromIndex(this._undoRedoCurrentIndex);
            eraser._shapeId = this.listRootShape.size();
            eraser._action = AddNewShape;
            eraser.isFocus = false;
            eraser.isVisible = true;
            addNewSharp(eraser);
        }
    }

    public void createVoice(float x, float y,Uri audioPath) {
        int width = mBitmapVoicePrepare.getWidth();
        int height = mBitmapVoicePrepare.getHeight();
        RecordObject textObj = new RecordObject(this.mContext, 8, x, y, x + ((float) width), y + ((float) height), this.mCurrentSettingPaint, false, new FlaotPoint((float) this.xMainBitmap, (float) this.yMainBitmap), (float) this.mainBitmap.getWidth(), (float) this.mainBitmap.getHeight(),audioPath);
        clearRedoSharpFromIndex(this._undoRedoCurrentIndex);
        textObj._shapeId = this.listRootShape.size();
        textObj._action = AddNewShape;
        textObj.isFocus = false;
        textObj.isVisible = true;
        addNewSharp(textObj);
        postInvalidate();
    }

    public void createText(float x, float y, String text) {
        MobclickAgent.onEvent(getContext(),"create_text");
        Rect mRectText = new Rect();
        this.mCurrentSettingPaint.getTextBounds(text, 0, text.length(), mRectText);
        int width = mRectText.left + mRectText.width();
        int height = mRectText.bottom + mRectText.height();
        TextObject textObj = new TextObject(this.mContext, 6, text, x, y, x + ((float) width), y + ((float) height), this.mCurrentSettingPaint, false, new FlaotPoint((float) this.xMainBitmap, (float) this.yMainBitmap), UIUtil.DeviceInfo.getDeviceScreenWidth(), UIUtil.DeviceInfo.getDeviceScreenHeight()- UIUtil.dpToPx(110));
        textObj.setColor(mCurrentSettingPaint.getColor());
        clearRedoSharpFromIndex(this._undoRedoCurrentIndex);
        textObj._shapeId = this.listRootShape.size();
        textObj._action = AddNewShape;
        textObj.isFocus = false;
        textObj.isVisible = true;
        addNewSharp(textObj);
        postInvalidate();
    }

    public TextObject getCurrentTextObject() {
        if (this.mCurrentShape == null || !(this.mCurrentShape instanceof TextObject)) {
            return null;
        }

        return (TextObject) this.mCurrentShape;
    }

    public void updateRealTextObject(String update) {
        if (this.mCurrentShape == null || !(this.mCurrentShape instanceof TextObject)) {
            return;
        }
        this.mCurrentShape._action = DeleteShape;
        showShapeAndFocus(this.mCurrentShape, false, false);
        clearRedoSharpFromIndex(this._undoRedoCurrentIndex);
        this.mCurrentShape = this.mCurrentShape.copyCustomerView();
        ((TextObject)mCurrentShape).initNewText(update);
        addNewSharp(this.mCurrentShape);
        this.mCurrentShape._action = AddNewShape;
        showShapeAndFocus(this.mCurrentShape, true, true);

    }

    private void onSwipeRight() {
    }

    private void onSwipeLeft() {
    }

    private void onSwipeTop() {
    }

    private void onSwipeBottom() {
    }

    public int getwMainView() {
        return this.wMainView;
    }

    public void setwMainView(int wMainView) {
        this.wMainView = wMainView;
    }

    public int gethMainView() {
        return hMainView;
    }

    public void sethMainView(int hMainView) {
        this.hMainView = hMainView;
    }

    public ArrayList<CustomerShapeView> getListRootShape() {
        return this.listRootShape;
    }

    public void setMainBitmap(Bitmap mainBitmap) {
        this.mainBitmap = mainBitmap;
        this._blurManager.setMainBitmap(this.mainBitmap);
        this.xMainBitmap = (wMainView - this.mainBitmap.getWidth()) / 2;
        this.yMainBitmap = (hMainView- this.mainBitmap.getHeight()) / 2;
    }

    public FlaotPoint getBitmapMargin() {
        return new FlaotPoint((float) this.xMainBitmap, (float) this.yMainBitmap);
    }

    public void setEdit(boolean isEdit) {
        this.isEdit = isEdit;
        isEditTextMode = false;
    }

    private boolean isEditTextMode;
    private EditText editText;
    public void setEditTextMode(boolean isEditTextMode) {
        if (this.isEditTextMode == isEditTextMode) {
            return;
        }
        if(isEditTextMode) {
            isEdit = true;
        }
        this.isEditTextMode = isEditTextMode;
    }

    public void setEditText(EditText editText) {
        this.editText = editText;
    }

    public int getxMainBitmap() {
        return this.xMainBitmap;
    }

    public int getyMainBitmap() {
        return this.yMainBitmap;
    }

    public int get_undoRedoCurrentIndex() {
        return this._undoRedoCurrentIndex;
    }

    public void setmModeCreateObjPaint(int mModeCreateObjPaint) {
        this.mModeCreateObjPaint = mModeCreateObjPaint;
        this.mPaintVirtualDraw.setStyle(Style.STROKE);
    }

}
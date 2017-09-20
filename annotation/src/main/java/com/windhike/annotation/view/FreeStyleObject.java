package com.windhike.annotation.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;

import com.windhike.annotation.model.FlaotPoint;

import java.util.ArrayList;

public class FreeStyleObject extends CustomerShapeView {
    public static final Parcelable.Creator<FreeStyleObject> CREATOR = new Parcelable.Creator<FreeStyleObject>() {
        public FreeStyleObject createFromParcel(Parcel in) {
            FreeStyleObject mFreeStyleObject = new FreeStyleObject();
            try {
                mFreeStyleObject.init(in);
                return mFreeStyleObject;
            } catch (Throwable th) {
                return null;
            }
        }

        public FreeStyleObject[] newArray(int size) {
            return new FreeStyleObject[size];
        }
    };
    private RectF RectFBodyPath;
    private int alphaResize = 40;
    private FlaotPoint[] arrayPointPathFirstCreate;
    private int distanceTouchPoint = 30;
    private ArrayList<FlaotPoint> listPointBody = new ArrayList();
    private ArrayList<FlaotPoint> listPointMainPath = new ArrayList();
//    private Paint mPaintFocus;
    public Path mPathFreeStyle;

    public FreeStyleObject(){}
    public FreeStyleObject(Context mContext, int mType, float x, float y, float w, float h, Path mPath, Paint mPaint, FlaotPoint mOldFlaotPointMarginBitmap, float mWdithBitamp, float mHeighBitmap) {
        super(mContext, mType, x, y, w, h, mPaint, mOldFlaotPointMarginBitmap, mWdithBitamp, mHeighBitmap);
        this.mType = mType;
        if (this.mPathFreeStyle == null) {
            this.mPathFreeStyle = new Path(mPath);
        }
        this.mPaint = new Paint(mPaint);
//        this.mPaint.setPathEffect(new CornerPathEffect(0.0f));
        this.RectFBodyPath = new RectF();
        this.mPathFreeStyle.computeBounds(this.RectFBodyPath, true);
        this.listPointBody.clear();
        this.listPointMainPath.clear();
        FlaotPoint[] arrayPointPath = getListPoints();
        if (arrayPointPath != null && arrayPointPath.length > 0) {
            int distance = this.distanceTouchPoint;
            int mainPointBody = arrayPointPath.length / distance;
            for (int i = 0; i < mainPointBody; i++) {
                this.listPointBody.add(arrayPointPath[i * distance]);
            }
            if (this.listPointBody.size() > 0) {
                this.listPointBody.add(arrayPointPath[this.listPointBody.size() - 1]);
            }
        }
//        this.mPaintFocus = new Paint(mPaint);
//        this.mPaintFocus.setStrokeWidth(this.mPaintFocus.getStrokeWidth());
//        this.mPaintFocus.setColor(IBOSApp.getInstance().getResources().getColor(R.color.red));
//        this.mPaintFocus.setPathEffect(new CornerPathEffect(0.0f));
    }

    public void resetData() {
        super.resetData();
        if (this.mPathFreeStyle != null) {
            this.mPathFreeStyle.reset();
            this.mPathFreeStyle = null;
        }
        if (this.listPointBody != null) {
            this.listPointBody.clear();
        }
        if (this.listPointMainPath != null) {
            this.listPointMainPath.clear();
        }
        this.RectFBodyPath = null;
//        this.mPaintFocus = null;
        this.arrayPointPathFirstCreate = null;
    }

    public FlaotPoint[] getListPoints() {
        PathMeasure pm = new PathMeasure(this.mPathFreeStyle, false);
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

    public FreeStyleObject copyCustomerView() {
        FreeStyleObject freeStyle = new FreeStyleObject(this.mContext, this.mType, this.x, this.y, this.w, this.h, this.mPathFreeStyle, this.mPaint, this.mOldFlaotPointMarginBitmap.copyFlaotPoint(), this.mWdithBitamp, this.mHeighBitmap);
        freeStyle._shapeId = this._shapeId;
        return freeStyle;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeInt(this.mType);
        parcel.writeInt(this.mPaint.getColor());
        parcel.writeInt((int) this.mPaint.getStrokeWidth());
        this.listPointMainPath.clear();
        FlaotPoint[] arrayPointPath = getListPoints();
        if (arrayPointPath != null && arrayPointPath.length > 0) {
            for (int i = 0; i < arrayPointPath.length; i++) {
                this.listPointMainPath.add(new FlaotPoint(arrayPointPath[i].getX(), arrayPointPath[i].getY()));
            }
        }
        parcel.writeList(this.listPointMainPath);
    }

    public void scaleAndChangePosition(FlaotPoint oldBitmapMarin, FlaotPoint newBitmapMargin, float scale) {
        this.x = ((this.x - oldBitmapMarin.getX()) * scale) + newBitmapMargin.getX();
        this.y = ((this.y - oldBitmapMarin.getY()) * scale) + newBitmapMargin.getY();
        this.w = ((this.w - oldBitmapMarin.getX()) * scale) + newBitmapMargin.getX();
        this.h = ((this.h - oldBitmapMarin.getY()) * scale) + newBitmapMargin.getY();
        if (this.arrayPointPathFirstCreate == null) {
            this.arrayPointPathFirstCreate = getListPoints();
        }
        float mXStart = 0.0f;
        float mYStart = 0.0f;
        if (this.arrayPointPathFirstCreate != null && this.arrayPointPathFirstCreate.length > 0) {
            this.mPathFreeStyle.reset();
            for (int i = 0; i < this.arrayPointPathFirstCreate.length; i++) {
                FlaotPoint point = this.arrayPointPathFirstCreate[i];
                float mNewY = ((point.getY() - oldBitmapMarin.getY()) * scale) + newBitmapMargin.getY();
                this.arrayPointPathFirstCreate[i].setX(((point.getX() - oldBitmapMarin.getX()) * scale) + newBitmapMargin.getX());
                this.arrayPointPathFirstCreate[i].setY(mNewY);
                if (i == 0) {
                    this.mPathFreeStyle.moveTo(this.arrayPointPathFirstCreate[i].getX(), this.arrayPointPathFirstCreate[i].getY());
                    mXStart = this.arrayPointPathFirstCreate[i].getX();
                    mYStart = this.arrayPointPathFirstCreate[i].getY();
                } else {
                    this.mPathFreeStyle.quadTo(mXStart, mYStart, this.arrayPointPathFirstCreate[i].getX(), this.arrayPointPathFirstCreate[i].getY());
                    mXStart = this.arrayPointPathFirstCreate[i].getX();
                    mYStart = this.arrayPointPathFirstCreate[i].getY();
                }
            }
        }
        this.mPathFreeStyle.computeBounds(this.RectFBodyPath, true);
        resize_Or_Move_Object(this.RectFBodyPath.left, this.RectFBodyPath.top, this.RectFBodyPath.right, this.RectFBodyPath.bottom);
        this.listPointBody.clear();
        this.listPointMainPath.clear();
        FlaotPoint[] arrayPointPath = getListPoints();
        if (arrayPointPath != null && arrayPointPath.length > 0) {
            int distance = this.distanceTouchPoint;
            int mainPointBody = arrayPointPath.length / distance;
            for (int i = 0; i < mainPointBody; i++) {
                this.listPointBody.add(arrayPointPath[i * distance]);
            }
            if (this.listPointBody.size() > 0) {
                this.listPointBody.add(arrayPointPath[this.listPointBody.size() - 1]);
            }
        }
    }

    public void create() {

    }

    public void init(Parcel parcel) {
        super.init(parcel);
        this.mType = parcel.readInt();
        this.mColor = parcel.readInt();
        this.mStrokeWith = (float) parcel.readInt();
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        this.mPaint.setDither(true);
        this.mPaint.setColor(this.mColor);
        this.mPaint.setStrokeWidth(this.mStrokeWith);
        this.mPaint.setStyle(Style.STROKE);
        this.mPaint.setStrokeJoin(Join.ROUND);
        this.listPointMainPath = parcel.readArrayList(FlaotPoint.class.getClassLoader());
        this.mPathFreeStyle = new Path();
        this.mPathFreeStyle.moveTo(this.listPointMainPath.get(0).getX(), this.listPointMainPath.get(0).getY());
        for (int i = 1; i < this.listPointMainPath.size(); i++) {
            this.mPathFreeStyle.lineTo(this.listPointMainPath.get(i).getX(),this.listPointMainPath.get(i).getY());
        }
        this.RectFBodyPath = new RectF();
        this.mPathFreeStyle.computeBounds(this.RectFBodyPath, true);
        this.x = this.RectFBodyPath.left;
        this.y = this.RectFBodyPath.top;
        this.w = this.RectFBodyPath.left + (this.RectFBodyPath.right - this.RectFBodyPath.left);
        this.h = this.RectFBodyPath.top + (this.RectFBodyPath.bottom - this.RectFBodyPath.top);
        this.listPointBody.clear();
        int distance = this.distanceTouchPoint;
        int mainPointBody = this.listPointMainPath.size() / distance;
        for (int i = 0; i < mainPointBody; i++) {
            this.listPointBody.add(this.listPointMainPath.get(i * distance));
        }
//        this.mPaintFocus = new Paint(this.mPaint);
//        this.mPaintFocus.setStrokeWidth(this.mPaintFocus.getStrokeWidth());
//        this.mPaintFocus.setColor(-1);
//        this.mPaintFocus.setPathEffect(new CornerPathEffect(0.0f));
        createListPoint();
    }

    public void initNewPointPositionMoveShape(float detaX, float detaY) {
        Matrix translateMatrix = new Matrix();
        translateMatrix.setTranslate(-detaX, -detaY);
        this.mPathFreeStyle.transform(translateMatrix);
        this.RectFBodyPath = new RectF();
        this.mPathFreeStyle.computeBounds(this.RectFBodyPath, true);
        this.listPointBody.clear();
        this.listPointMainPath.clear();
        FlaotPoint[] arrayPointPath = getListPoints();
        if (arrayPointPath != null && arrayPointPath.length > 0) {
            int distance = this.distanceTouchPoint;
            int mainPointBody = arrayPointPath.length / distance;
            for (int i = 0; i < mainPointBody; i++) {
                this.listPointBody.add(arrayPointPath[i * distance]);
            }
            if (this.listPointBody.size() > 0) {
                this.listPointBody.add(arrayPointPath[this.listPointBody.size() - 1]);
            }
        }
        this.x = this.RectFBodyPath.left;
        this.y = this.RectFBodyPath.top;
        this.w = this.RectFBodyPath.right;
        this.h = this.RectFBodyPath.bottom;
        resize_Or_Move_Object(this.x, this.y, this.w, this.h);
    }

    public void resizePath(float x, float y, float w, float h) {
        RectF RectFBodyPath = new RectF();
        this.mPathFreeStyle.computeBounds(RectFBodyPath, true);
        float oldWidth = RectFBodyPath.right - RectFBodyPath.left;
        float oldHeight = RectFBodyPath.bottom - RectFBodyPath.top;
        float oldx = RectFBodyPath.left;
        float oldy = RectFBodyPath.top;
        if (this.arrayPointPathFirstCreate == null) {
            this.arrayPointPathFirstCreate = getListPoints();
        }
        float mXStart = 0.0f;
        float mYStart = 0.0f;
        if (this.arrayPointPathFirstCreate != null && this.arrayPointPathFirstCreate.length > 0) {
            this.mPathFreeStyle.reset();
            for (int i = 0; i < this.arrayPointPathFirstCreate.length; i++) {
                FlaotPoint point = this.arrayPointPathFirstCreate[i];
                float mNewY = y + (((h - y) * (point.getY() - oldy)) / oldHeight);
                this.arrayPointPathFirstCreate[i].setX(x + (((w - x) * (point.getX() - oldx)) / oldWidth));
                this.arrayPointPathFirstCreate[i].setY(mNewY);
                if (i == 0) {
                    this.mPathFreeStyle.moveTo(this.arrayPointPathFirstCreate[i].getX(), this.arrayPointPathFirstCreate[i].getY());
                    mXStart = this.arrayPointPathFirstCreate[i].getX();
                    mYStart = this.arrayPointPathFirstCreate[i].getY();
                } else {
                    this.mPathFreeStyle.quadTo(mXStart, mYStart, this.arrayPointPathFirstCreate[i].getX(), this.arrayPointPathFirstCreate[i].getY());
                    mXStart = this.arrayPointPathFirstCreate[i].getX();
                    mYStart = this.arrayPointPathFirstCreate[i].getY();
                }
            }
        }
        this.mPathFreeStyle.computeBounds(this.RectFBodyPath, true);
        resize_Or_Move_Object(RectFBodyPath.left, RectFBodyPath.top, RectFBodyPath.right, RectFBodyPath.bottom);
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        if (this.isFocus) {
//            makeEffects(this.mEffects, this.mPhase);
//            this.mPhase += 1.0f;
//            this.mDashLine.setPathEffect(this.mEffects[2]);
//            this.mBorderBodyArrow.setPathEffect(this.mEffects[2]);
//            if (this.mPhase > 2.14748365E9f) {
//                this.mPhase = 0.0f;
//            }
//            canvas.drawPath(this.mPathFreeStyle, this.mBorderBodyArrow);
//        }
        canvas.drawPath(this.mPathFreeStyle, this.mPaint);
        this.mPathFreeStyle.computeBounds(this.RectFBodyPath, true);
//        if (this.isFocus) {
//            for (int i = 0; i < this.mListDot.size(); i++) {
//                this.mListDot.get(i).onDraw(canvas);
//            }
//        }
    }

    public RectF getRectFBodyPath() {
        return this.RectFBodyPath;
    }

    public void setRectFBodyPath(RectF rectFBodyPath) {
        this.RectFBodyPath = rectFBodyPath;
    }

    public ArrayList<FlaotPoint> getListPointBody() {
        return this.listPointBody;
    }

    public void setListPointBody(ArrayList<FlaotPoint> listPointBody) {
        this.listPointBody = listPointBody;
    }

    public int getAlphaResize() {
        return this.alphaResize;
    }

    public void setAlphaResize(int alphaResize) {
        this.alphaResize = alphaResize;
    }

    public ArrayList<DotObject> getmListDot() {
        return this.mListDot;
    }

    public void setmListDot(ArrayList<DotObject> mListDot) {
        this.mListDot = mListDot;
    }
}
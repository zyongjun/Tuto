package com.windhike.annotation.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PathMeasure;
import android.os.Parcel;
import android.os.Parcelable;

import com.windhike.annotation.common.LogUtils;
import com.windhike.annotation.model.FlaotPoint;
import java.util.ArrayList;


public class RectangleObject extends CustomerShapeView {
    public static final Parcelable.Creator<RectangleObject> CREATOR = new Parcelable.Creator<RectangleObject>() {
        public RectangleObject createFromParcel(Parcel in) {
            RectangleObject mRectangleObj = new RectangleObject();
            try {
                mRectangleObj.init(in);
                return mRectangleObj;
            } catch (Throwable th) {
                LogUtils.LogError("Edition.createFromParcel", "Failed to init Edition from parcel");
                return null;
            }
        }

        public RectangleObject[] newArray(int size) {
            return new RectangleObject[size];
        }
    };
    private int alphaResize = 30;
    private int distanceTouchPoint = 15;
    private ArrayList<FlaotPoint> listPointBody = new ArrayList();
    private Paint mPaintDebug;
    private Path mPath;

    public RectangleObject(){}
    public RectangleObject(Context mContext, int mType, float x, float y, float w, float h, Paint mPaint, FlaotPoint mOldFlaotPointMarginBitmap, float mWdithBitamp, float mHeighBitmap) {
        super(mContext, mType, x, y, w, h, mPaint, mOldFlaotPointMarginBitmap, mWdithBitamp, mHeighBitmap);
        this.mType = mType;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.mPaint = new Paint(mPaint);
        this.mPaint.setPathEffect(new CornerPathEffect(0.0f));
        this.mPath = new Path();
        this.mPath.addRect(x, y, w, h, Direction.CCW);
        this.listPointBody.clear();
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

    private FlaotPoint[] getListPoints() {
        PathMeasure pm = new PathMeasure(this.mPath, false);
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

    public void resetData() {
        super.resetData();
        if (this.mPaintDebug != null) {
            this.mPaintDebug = null;
        }
        if (this.listPointBody != null) {
            this.listPointBody.clear();
        }
    }

    public RectangleObject copyCustomerView() {
        RectangleObject rectangle = new RectangleObject(this.mContext, this.mType, this.x, this.y, this.w, this.h, this.mPaint, this.mOldFlaotPointMarginBitmap.copyFlaotPoint(), this.mWdithBitamp, this.mHeighBitmap);
        rectangle._shapeId = this._shapeId;
        return rectangle;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeInt(this.mType);
        parcel.writeFloat(this.x);
        parcel.writeFloat(this.y);
        parcel.writeFloat(this.w);
        parcel.writeFloat(this.h);
        parcel.writeInt(this.mPaint.getColor());
        parcel.writeInt((int) this.mPaint.getStrokeWidth());
    }

    public void init(Parcel parcel) {
        super.init(parcel);
        this.mType = parcel.readInt();
        this.x = parcel.readFloat();
        this.y = parcel.readFloat();
        this.w = parcel.readFloat();
        this.h = parcel.readFloat();
        this.mColor = parcel.readInt();
        this.mStrokeWith = (float) parcel.readInt();
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        this.mPaint.setDither(true);
        this.mPaint.setColor(this.mColor);
        this.mPaint.setStrokeWidth(this.mStrokeWith);
        this.mPaint.setStyle(Style.STROKE);
        this.mPaint.setStrokeJoin(Join.ROUND);
        this.mPaint.setStrokeCap(Cap.ROUND);
        createListPoint();
        if (this.mPath == null) {
            this.mPath = new Path();
        }
        this.mPath.reset();
        this.mPath.addRect(this.x, this.y, this.w, this.h, Direction.CCW);
        if (this.listPointBody == null) {
            this.listPointBody = new ArrayList();
        }
        this.listPointBody.clear();
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

    public void initNewVirtualPoint() {
        if (this.mPath == null) {
            this.mPath = new Path();
        }
        this.mPath.reset();
        this.mPath.addRect(this.x, this.y, this.w, this.h, Direction.CCW);
        if (this.listPointBody == null) {
            this.listPointBody = new ArrayList();
        }
        this.listPointBody.clear();
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

    public void scaleAndChangePosition(FlaotPoint oldBitmapMarin, FlaotPoint newBitmapMargin, float scale) {
        super.scaleAndChangePosition(oldBitmapMarin, newBitmapMargin, scale);
        initNewVirtualPoint();
        resize_Or_Move_Object(this.x, this.y, this.w, this.h);
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(this.x, this.y, this.w, this.h, this.mPaint);
//        if (this.isFocus) {
//            makeEffects(this.mEffects, this.mPhase);
//            this.mPhase += 1.0f;
//            this.mDashLine.setPathEffect(this.mEffects[2]);
//            if (this.mPhase > 2.14748365E9f) {
//                this.mPhase = 0.0f;
//            }
//            Canvas canvas2 = canvas;
//            canvas2.drawRect(this.x - 5, this.y - 5, 5 + this.w, 5 + this.h, this.mDashLine);
//            canvas2 = canvas;
//            canvas2.drawRect(5 + this.x,5 + this.y, this.w - 5, this.h - 5, this.mDashLine);
//            for (int i = 0; i < this.mListDot.size(); i++) {
//                this.mListDot.get(i).onDraw(canvas);
//            }
//        }
    }

    public ArrayList<DotObject> getmListDot() {
        return this.mListDot;
    }

    public void setmListDot(ArrayList<DotObject> mListDot) {
        this.mListDot = mListDot;
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
}

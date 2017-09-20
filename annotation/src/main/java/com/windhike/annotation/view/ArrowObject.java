package com.windhike.annotation.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.windhike.annotation.configsapp.Configs;
import com.windhike.annotation.model.FlaotPoint;

import java.util.ArrayList;

/**
 * author: gzzyj on 2017/2/15.
 * email:zhyongjun@windhike.cn
 */
public class ArrowObject extends CustomerShapeView {
    public static final Parcelable.Creator<ArrowObject> CREATOR = new Parcelable.Creator<ArrowObject>() {
        public ArrowObject createFromParcel(Parcel in) {
            ArrowObject arrowObj = new ArrowObject();
            try {
                arrowObj.init(in);
                return arrowObj;
            } catch (Throwable th) {
                Log.e("ArrowObject", "Failed to init Edition from parcel");
                return null;
            }
        }

        public ArrowObject[] newArray(int size) {
            return new ArrowObject[size];
        }
    };
    private final int FIXDOT = 2;
    private int alphaResize = 45;
    private Path arrowBodyPath;
    private RectF arrowBodyRectF;
    private int distance = 20;
    private ArrayList<FlaotPoint> listPointBody = new ArrayList();
    private ArrayList<DotObject> mListDot;
    private float tailX;
    private float tailY;
    private float tipX;
    private float tipY;
    public ArrowObject(){}
    public ArrowObject(Context mContext, int mType, float tailX, float tailY, float tipX, float tipY, Paint mPaint, FlaotPoint mOldFlaotPointMarginBitmap, float mWdithBitamp, float mHeighBitmap) {
        super(mContext, mType, tailX, tailY, tipX, tipY, mPaint, mOldFlaotPointMarginBitmap, mWdithBitamp, mHeighBitmap);
        this.mType = mType;
        this.tailX = tailX;
        this.tailY = tailY;
        this.tipX = tipX;
        this.tipY = tipY;
        this.mPaint = new Paint(mPaint);
        this.mPaint.setPathEffect(new CornerPathEffect(0.0f));
        this.mPaint.setStyle(Style.STROKE);
        this.listPointBody = new ArrayList();
        this.arrowBodyPath = new Path();
        logicDrawArrow(tailX, tailY, tipX, tipY);
        createListDotPoint(tailX, tailY, tipX, tipY);
        this.arrowBodyRectF = new RectF();
        this.arrowBodyPath.computeBounds(this.arrowBodyRectF, true);
        this.listPointBody.clear();
        FlaotPoint[] points = getListPoints();
        int mainPointBody = points.length / this.distance;
        for (int i = 0; i < mainPointBody; i++) {
            this.listPointBody.add(points[this.distance * i]);
            if (i == mainPointBody - 1) {
                this.listPointBody.add(points[points.length - 1]);
            }
        }
    }

    public void resetData() {
        super.resetData();
        if (this.arrowBodyPath != null) {
            this.arrowBodyPath.reset();
            this.arrowBodyPath = null;
        }
        if (this.arrowBodyRectF != null) {
            this.arrowBodyRectF = null;
        }
        if (this.listPointBody != null) {
            this.listPointBody.clear();
        }
    }

    public float getTailX() {
        return this.tailX;
    }

    public void setTailX(float tailX) {
        this.tailX = tailX;
    }

    public float getTailY() {
        return this.tailY;
    }

    public void setTailY(float tailY) {
        this.tailY = tailY;
    }

    public float getTipX() {
        return this.tipX;
    }

    public void setTipX(float tipX) {
        this.tipX = tipX;
    }

    public float getTipY() {
        return this.tipY;
    }

    public void setTipY(float tipY) {
        this.tipY = tipY;
    }

    public RectF getArrowBodyRectF() {
        return this.arrowBodyRectF;
    }

    public void setArrowBodyRectF(RectF arrowBodyRectF) {
        this.arrowBodyRectF = arrowBodyRectF;
    }

    public int getAlphaResize() {
        return this.alphaResize;
    }

    public void setAlphaResize(int alphaResize) {
        this.alphaResize = alphaResize;
    }

    public ArrayList<FlaotPoint> getListPointBody() {
        return this.listPointBody;
    }

    public void setListPointBody(ArrayList<FlaotPoint> listPointBody) {
        this.listPointBody = listPointBody;
    }

    public FlaotPoint[] getListPoints() {
        PathMeasure pm = new PathMeasure(this.arrowBodyPath, false);
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

    private void createListDotPoint(float tailX, float tailY, float tipX, float tipY) {
        this.mListDot = new ArrayList();
        for (int i = 0; i < 2; i++) {
            DotObject dot = null;
            switch (i) {
                case 0:
                    dot = new DotObject(i, tailX, tailY);
                    dot.setmY_vitural_touch(dot.getmY() - (dot.getmH() / 2.0f));
                    break;
                case 1:
                    dot = new DotObject(i, tipX, tipY);
                    dot.setmY_vitural_touch(dot.getmY() - (dot.getmH() / 2.0f));
                    break;
                default:
                    break;
            }
            this.mListDot.add(dot);
        }
    }

    public ArrowObject copyCustomerView() {
        ArrowObject arrow = new ArrowObject(this.mContext, this.mType, this.tailX, this.tailY, this.tipX, this.tipY, this.mPaint, this.mOldFlaotPointMarginBitmap.copyFlaotPoint(), this.mWdithBitamp, this.mHeighBitmap);
        arrow._shapeId = this._shapeId;
        return arrow;
    }

    public void scaleAndChangePosition(FlaotPoint oldBitmapMarin, FlaotPoint newBitmapMargin, float scale) {
        this.tailX = ((this.tailX - oldBitmapMarin.getX()) * scale) + newBitmapMargin.getX();
        this.tailY = ((this.tailY - oldBitmapMarin.getY()) * scale) + newBitmapMargin.getY();
        this.tipX = ((this.tipX - oldBitmapMarin.getX()) * scale) + newBitmapMargin.getX();
        this.tipY = ((this.tipY - oldBitmapMarin.getY()) * scale) + newBitmapMargin.getY();
        if (this.listPointBody == null) {
            this.listPointBody = new ArrayList();
        }
        this.listPointBody.clear();
        this.arrowBodyPath = new Path();
        logicDrawArrow(this.tailX, this.tailY, this.tipX, this.tipY);
        createListDotPoint(this.tailX, this.tailY, this.tipX, this.tipY);
        this.arrowBodyRectF = new RectF();
        this.arrowBodyPath.computeBounds(this.arrowBodyRectF, true);
        FlaotPoint[] points = getListPoints();
        int mainPointBody = points.length / this.distance;
        for (int i = 0; i < mainPointBody; i++) {
            this.listPointBody.add(points[this.distance * i]);
            if (i == mainPointBody - 1) {
                this.listPointBody.add(points[points.length - 1]);
            }
        }
    }

    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeInt(this.mType);
        parcel.writeFloat(this.tailX);
        parcel.writeFloat(this.tailY);
        parcel.writeFloat(this.tipX);
        parcel.writeFloat(this.tipY);
        parcel.writeInt(this.mPaint.getColor());
        parcel.writeInt((int) this.mPaint.getStrokeWidth());
    }

    public void init(Parcel parcel) {
        super.init(parcel);
        this.mType = parcel.readInt();
        this.tailX = parcel.readFloat();
        this.tailY = parcel.readFloat();
        this.tipX = parcel.readFloat();
        this.tipY = parcel.readFloat();
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
        this.arrowBodyPath = new Path();
        logicDrawArrow(this.tailX, this.tailY, this.tipX, this.tipY);
        createListDotPoint(this.tailX, this.tailY, this.tipX, this.tipY);
        this.arrowBodyRectF = new RectF();
        this.arrowBodyPath.computeBounds(this.arrowBodyRectF, true);
        this.listPointBody.removeAll(this.listPointBody);
        FlaotPoint[] points = getListPoints();
        int mainPointBody = points.length / this.distance;
        for (int i = 0; i < mainPointBody; i++) {
            this.listPointBody.add(points[this.distance * i]);
            if (i == mainPointBody - 1) {
                this.listPointBody.add(points[points.length - 1]);
            }
        }
    }

    public void logicDrawArrow(float tailX, float tailY, float tipX, float tipY) {
        this.arrowBodyPath.reset();
        int arrowLength = 60;
        for (int i = 0; i < Configs.LIST_STROKE_WIDTH.length; i++) {
            if (this.mPaint.getStrokeWidth() == Configs.LIST_STROKE_WIDTH[i]) {
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
        this.arrowBodyPath.moveTo(tipX, tipY);
        this.arrowBodyPath.quadTo(tipX, tipY, (float) x1, (float) y1);
        this.arrowBodyPath.quadTo((float) x1, (float) y1, (float) x2, (float) y2);
        this.arrowBodyPath.quadTo((float) x2, (float) y2, tailX, tailY);
        this.arrowBodyPath.quadTo(tailX,tailY,x3,y3);
        this.arrowBodyPath.quadTo(x3,y3,x4,y4);
        this.arrowBodyPath.quadTo(x4,y4,tipX,tipY);
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(this.arrowBodyPath, this.mPaint);
//        if (this.isFocus) {
//            makeEffects(this.mEffects, this.mPhase);
//            this.mPhase += 1.0f;
//            this.mDashLine.setPathEffect(this.mEffects[2]);
//            this.mBorderBodyArrow.setPathEffect(this.mEffects[2]);
//            if (this.mPhase > 2.14748365E9f) {
//                this.mPhase = 0.0f;
//            }
//            canvas.drawLine(this.tailX, this.tailY, this.xEndPoint, this.yEndPoint, this.mBorderBodyArrow);
//        }
//        canvas.drawLine(this.tailX, this.tailY, this.xEndPoint, this.yEndPoint, this.mPaint);
//        if (this.isFocus) {
//            canvas.drawPath(this.arrowHeadrPath, this.mDashLine);
//            for (int i = 0; i < this.mListDot.size(); i++) {
//                this.mListDot.get(i).onDraw(canvas);
//            }
//        }
    }

    public void initNewPointPositionMoveShape() {
        int i;
        logicDrawArrow(this.tailX, this.tailY, this.tipX, this.tipY);
        this.arrowBodyRectF = null;
        this.arrowBodyRectF = new RectF();
        this.arrowBodyPath.computeBounds(this.arrowBodyRectF, true);
        this.listPointBody.removeAll(this.listPointBody);
        FlaotPoint[] points = getListPoints();
        int mainPointBody = points.length / this.distance;
        for (i = 0; i < mainPointBody; i++) {
            this.listPointBody.add(points[this.distance * i]);
            if (i == mainPointBody - 1) {
                this.listPointBody.add(points[points.length - 1]);
            }
        }
        for (i = 0; i < this.mListDot.size(); i++) {
            this.mListDot.get(i).setmY_vitural_touch(this.mListDot.get(i).getmY() - (this.mListDot.get(i).getmH() / 2.0f));
        }
    }

    public void initNewPointPosition(int indexDotPoint, float x, float y) {
        if (indexDotPoint == 0) {
            this.tailX = x;
            this.tailY = y;
        } else {
            this.tipX = x;
            this.tipY = y;
        }
        logicDrawArrow(this.tailX, this.tailY, this.tipX, this.tipY);
        this.mListDot.get(indexDotPoint).setmX(x);
        this.mListDot.get(indexDotPoint).setmY(y);
        this.mListDot.get(indexDotPoint).setmY_vitural_touch(this.mListDot.get(indexDotPoint).getmY() - (this.mListDot.get(indexDotPoint).getmH() / 2.0f));
        this.arrowBodyRectF = new RectF();
        this.arrowBodyPath.computeBounds(this.arrowBodyRectF, true);
        this.listPointBody.removeAll(this.listPointBody);
        FlaotPoint[] points = getListPoints();
        int mainPointBody = points.length / this.distance;
        for (int i = 0; i < mainPointBody; i++) {
            this.listPointBody.add(points[this.distance * i]);
            if (i == mainPointBody - 1) {
                this.listPointBody.add(points[points.length - 1]);
            }
        }
    }

    public ArrayList<DotObject> getmListDot() {
        return this.mListDot;
    }

    public void setmListDot(ArrayList<DotObject> mListDot) {
        this.mListDot = mListDot;
    }
}
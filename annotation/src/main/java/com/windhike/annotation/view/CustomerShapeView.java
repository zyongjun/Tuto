package com.windhike.annotation.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ComposePathEffect;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;

import com.windhike.annotation.R;
import com.windhike.annotation.configsapp.Configs;
import com.windhike.annotation.model.FlaotPoint;
import com.windhike.annotation.reuse.ui.UIUtil;

import java.util.ArrayList;

public class CustomerShapeView implements Parcelable {
    public int FIXDOT = 8;
    public Configs.ShapeAction _action;
    public int _shapeId;
    public float h;
    public boolean isFocus;
    public boolean isVisible;
    public Paint mBorderBodyArrow;
    protected int mColor = 0;
    public Context mContext;
    public Paint mDashLine;
    public PathEffect[] mEffects;
    public float mHeighBitmap;
    public ArrayList<DotObject> mListDot;
    public FlaotPoint mOldFlaotPointMarginBitmap;
    public Paint mPaint;
    public Path mPath;
    public float mPhase;
    protected float mStrokeWith = 0.0f;
    protected float mTextSize = 0.0f;
    public int mType;
    public float mWdithBitamp;
    public float w;
    public float x;
    public float y;

    public CustomerShapeView(){}

    public CustomerShapeView(Context mContext, int mType, float x, float y, float w, float h, Paint mPaint, FlaotPoint mOldFlaotPointMarginBitmap, float mWdithBitamp, float mHeighBitmap) {
        Log.e(getClass().getName(), "CustomerShapeView() called with: mContext = [" + mContext + "], mType = [" + mType + "], x = [" + x + "], y = [" + y + "], w = [" + w + "], h = [" + h + "], mPaint = [" + mPaint + "], mOldFlaotPointMarginBitmap = [" + mOldFlaotPointMarginBitmap + "], mWdithBitamp = [" + mWdithBitamp + "], mHeighBitmap = [" + mHeighBitmap + "]");
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.mPaint = new Paint(mPaint);
        this.mPaint.setPathEffect(new CornerPathEffect(0.0f));
        this.mContext = mContext;
        this._action = Configs.ShapeAction.AddNewShape;
        this.mOldFlaotPointMarginBitmap = mOldFlaotPointMarginBitmap;
        this.mOldFlaotPointMarginBitmap = new FlaotPoint(0,0);
        this.mWdithBitamp = mWdithBitamp;
        this.mHeighBitmap = mHeighBitmap;
        createListPoint();
        this.mDashLine = new Paint(1);
        this.mDashLine.setColor(ResourcesCompat.getColor(mContext.getResources(), R.color.default_read, null));
        this.mDashLine.setStyle(Style.STROKE);
        this.mDashLine.setStrokeWidth(3.0f);
        this.mEffects = new PathEffect[3];
        makeEffects(this.mEffects, this.mPhase);
        this.mDashLine.setPathEffect(this.mEffects[2]);
        this.mBorderBodyArrow = new Paint(this.mDashLine);
        this.mBorderBodyArrow.setStyle(Style.STROKE);
        this.mBorderBodyArrow.setStrokeWidth(mPaint.getStrokeWidth() + Configs.SHOW_MENU_EDGE_WIDTH);
    }

    protected CustomerShapeView(Parcel in) {
        FIXDOT = in.readInt();
        _shapeId = in.readInt();
        h = in.readFloat();
        isFocus = in.readByte() != 0;
        isVisible = in.readByte() != 0;
        mColor = in.readInt();
        mHeighBitmap = in.readFloat();
        mOldFlaotPointMarginBitmap = in.readParcelable(FlaotPoint.class.getClassLoader());
        mOldFlaotPointMarginBitmap = new FlaotPoint(0,0);
        mPhase = in.readFloat();
        mStrokeWith = in.readFloat();
        mTextSize = in.readFloat();
        mType = in.readInt();
        mWdithBitamp = in.readFloat();
        w = in.readFloat();
        x = in.readFloat();
        y = in.readFloat();
    }

    public static final Creator<CustomerShapeView> CREATOR = new Creator<CustomerShapeView>() {
        @Override
        public CustomerShapeView createFromParcel(Parcel in) {
            return new CustomerShapeView(in);
        }

        @Override
        public CustomerShapeView[] newArray(int size) {
            return new CustomerShapeView[size];
        }
    };

    public void makeEffects(PathEffect[] e, float phase) {
        e[0] = new CornerPathEffect(Configs.SHOW_MENU_EDGE_WIDTH);
        e[1] = new DashPathEffect(new float[]{Configs.SHOW_MENU_EDGE_WIDTH, Configs.SHOW_MENU_EDGE_WIDTH, Configs.SHOW_MENU_EDGE_WIDTH, Configs.SHOW_MENU_EDGE_WIDTH}, phase);
        e[2] = new ComposePathEffect(e[1], e[0]);
    }

    public void textOutOfScreen(float distanceWidth) {
        for (int i = 0; i < this.mListDot.size(); i++) {
            this.mListDot.get(i).setmX(this.mListDot.get(i).getmX() - distanceWidth);
        }
    }

    public void resize_Or_Move_Object(float x, float y, float w, float h) {
        for (int i = 0; i < this.mListDot.size(); i++) {
            float xNewPosition = 0.0f;
            float yNewPosition = 0.0f;
            switch (i) {
                case 0:
                    xNewPosition = x - 5.0f;
                    yNewPosition = y - Configs.SHOW_MENU_EDGE_WIDTH;
                    break;
                case 1:
                    xNewPosition = x + ((w - x) / 2.0f);
                    yNewPosition = y - Configs.SHOW_MENU_EDGE_WIDTH;
                    break;
                case 2:
                    xNewPosition = w + 5.0f;
                    yNewPosition = y - Configs.SHOW_MENU_EDGE_WIDTH;
                    break;
                case 3:
                    yNewPosition = y + ((h - y) / 2.0f);
                    xNewPosition = w + Configs.SHOW_MENU_EDGE_WIDTH;
                    break;
                case 4:
                    xNewPosition = w + 5.0f;
                    yNewPosition = h + Configs.SHOW_MENU_EDGE_WIDTH;
                    break;
                case 5:
                    xNewPosition = x + ((w - x) / 2.0f);
                    yNewPosition = h + Configs.SHOW_MENU_EDGE_WIDTH;
                    break;
                case 6:
                    xNewPosition = x - 5.0f;
                    yNewPosition = h + Configs.SHOW_MENU_EDGE_WIDTH;
                    break;
                case 7:
                    xNewPosition = x - Configs.SHOW_MENU_EDGE_WIDTH;
                    yNewPosition = y + ((h - y) / 2.0f);
                    break;
                default:
                    break;
            }
            this.mListDot.get(i).setmX(xNewPosition);
            this.mListDot.get(i).setmY(yNewPosition);
        }
    }

    public void createListPoint() {
        if (this.mListDot != null) {
            mListDot.clear();
        }
        this.mListDot = new ArrayList();
        for (int i = 0; i < 8; i++) {
            DotObject dot = null;
            float mYdot;
            switch (i) {
                case 0:
                    dot = new DotObject(i, this.x, this.y);
                    mYdot = dot.getmY() - Configs.SHOW_MENU_EDGE_WIDTH;
                    dot.setmX(dot.getmX() - 5.0f);
                    dot.setmY(mYdot);
                    break;
                case 1:
                    dot = new DotObject(i, this.x + ((this.w - this.x) / 2.0f), this.y);
                    dot.setmY(dot.getmY() - Configs.SHOW_MENU_EDGE_WIDTH);
                    break;
                case 2:
                    dot = new DotObject(i, this.w, this.y);
                    mYdot = dot.getmY() - Configs.SHOW_MENU_EDGE_WIDTH;
                    dot.setmX(dot.getmX() + 5.0f);
                    dot.setmY(mYdot);
                    break;
                case 3:
                    dot = new DotObject(i, this.w, this.y + ((this.h - this.y) / 2.0f));
                    dot.setmX(dot.getmX() + Configs.SHOW_MENU_EDGE_WIDTH);
                    break;
                case 4:
                    dot = new DotObject(i, this.w, this.h);
                    dot.setmX(dot.getmX() + 5.0f);
                    dot.setmY(dot.getmY() + Configs.SHOW_MENU_EDGE_WIDTH);
                    break;
                case 5:
                    dot = new DotObject(i, this.x + ((this.w - this.x) / 2.0f), this.h);
                    dot.setmY(dot.getmY() + Configs.SHOW_MENU_EDGE_WIDTH);
                    break;
                case 6:
                    dot = new DotObject(i, this.x, this.h);
                    dot.setmX(dot.getmX() - 5.0f);
                    dot.setmY(dot.getmY() + Configs.SHOW_MENU_EDGE_WIDTH);
                    break;
                case 7:
                    dot = new DotObject(i, this.x, this.y + ((this.h - this.y) / 2.0f));
                    dot.setmX(dot.getmX() - Configs.SHOW_MENU_EDGE_WIDTH);
                    break;
                default:
                    break;
            }
            this.mListDot.add(dot);
        }
    }

    public CustomerShapeView copyCustomerView() {
        return null;
    }

    public void onDraw(Canvas canvas) {
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this._shapeId);
        dest.writeInt(this.isVisible ? 1 : 0);
        int action = 0;
        switch (_action) {
            case AddNewShape:
                action = 0;
                break;
            case MoveOrResizeShape:
                action = 1;
                break;
            case MoveShapeToTop://4:
                action = 2;
                break;
            case DeleteShape://5:
                action = 3;
                break;
            case ChangeBlurOpacity://6:
                action = 4;
                break;
            case ChangeTextColorOrStroke://7:
                action = 5;
                break;
        }
        dest.writeInt(action);
        dest.writeParcelable(this.mOldFlaotPointMarginBitmap, flags);
        dest.writeFloat(this.mWdithBitamp);
        dest.writeFloat(this.mHeighBitmap);
    }

    public void scaleAndChangePosition(FlaotPoint oldBitmapMarin, FlaotPoint newBitmapMargin, float scale) {
        this.x = ((this.x - oldBitmapMarin.getX()) * scale) + newBitmapMargin.getX();
        this.y = ((this.y - oldBitmapMarin.getY()) * scale) + newBitmapMargin.getY();
        this.w = ((this.w - oldBitmapMarin.getX()) * scale) + newBitmapMargin.getX();
        this.h = ((this.h - oldBitmapMarin.getY()) * scale) + newBitmapMargin.getY();
        if (this.mListDot != null) {
            this.mListDot.clear();
            createListPoint();
        }
    }

    public void scaleAndChangePositionForTextObject(FlaotPoint oldBitmapMarin, FlaotPoint newBitmapMargin, float scale) {
        this.x = ((this.x - oldBitmapMarin.getX()) * scale) + newBitmapMargin.getX();
        this.y = ((this.y - oldBitmapMarin.getY()) * scale) + newBitmapMargin.getY();
    }

    public void init(Parcel parcel) {
        this._shapeId = parcel.readInt();
        this.isVisible = parcel.readInt() != 0;
        switch (parcel.readInt()) {
            case 0:
                this._action = Configs.ShapeAction.AddNewShape;
                break;
            case 1:
                this._action = Configs.ShapeAction.MoveOrResizeShape;
                break;
            case 2:
                this._action = Configs.ShapeAction.MoveShapeToTop;
                break;
            case 3:
                this._action = Configs.ShapeAction.DeleteShape;
                break;
            case 4:
                this._action = Configs.ShapeAction.ChangeBlurOpacity;
                break;
            case 5:
                this._action = Configs.ShapeAction.ChangeTextColorOrStroke;
                break;
        }
        this.mOldFlaotPointMarginBitmap = parcel.readParcelable(FlaotPoint.class.getClassLoader());
        this.mWdithBitamp = parcel.readFloat();
        this.mHeighBitmap = parcel.readFloat();
    }

    public int getColor() {
        return this.mColor;
    }

    public void setColor(int mColor) {
        this.mColor = mColor;
    }

    public float getStrokeWith() {
        return this.mStrokeWith;
    }

    public void setStrokeWith(float mStrokeWith) {
        this.mStrokeWith = mStrokeWith;
    }

    public Paint getmPaint() {
        return this.mPaint;
    }

    public void setmPaint(Paint mPaint) {
        this.mPaint = mPaint;
    }

    public void resetData() {
        if (this.mPaint != null) {
            this.mPaint = null;
        }
        if (this.mPath != null) {
            this.mPath.reset();
            this.mPath = null;
        }
        if (this.mListDot != null) {
            mListDot.clear();
            this.mListDot = null;
        }
        if (this.mOldFlaotPointMarginBitmap != null) {
            this.mOldFlaotPointMarginBitmap = null;
        }
    }

    @Override
    public String toString() {
        return "CustomerShapeView{" +
                "h=" + h +
                ", w=" + w +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}

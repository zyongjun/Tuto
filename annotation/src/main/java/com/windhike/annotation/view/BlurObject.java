package com.windhike.annotation.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.PathEffect;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.windhike.annotation.configsapp.Configs;
import com.windhike.annotation.model.BlurManager;
import com.windhike.annotation.model.FlaotPoint;
import com.windhike.annotation.model.PreferenceConnector;

import java.util.ArrayList;

public class BlurObject extends CustomerShapeView {

    public static final Parcelable.Creator<BlurObject> CREATOR = new Parcelable.Creator<BlurObject>() {
        public BlurObject createFromParcel(Parcel in) {
            BlurObject blurObj = new BlurObject();
            try {
                blurObj.init(in);
                return blurObj;
            } catch (Throwable th) {
                return null;
            }
        }

        public BlurObject[] newArray(int size) {
            return new BlurObject[size];
        }
    };
    private Bitmap _blurBitmap;
    private BlurManager blurManager;
    private int mOpacity;
    private float translateX;
    private float translateY;
    private float xMargin;
    private float yMargin;

    public BlurObject(){}

    private static final String TAG = "BlurObject";
    public BlurObject(Context mContext, BlurManager manager, int mType, float xMargin, float yMargin, float x, float y, float w, float h, Paint mPaint, FlaotPoint mOldFlaotPointMarginBitmap, float mWdithBitamp, float mHeighBitmap) {
        super(mContext, mType, x, y, w, h, mPaint, mOldFlaotPointMarginBitmap, mWdithBitamp, mHeighBitmap);
        this.blurManager = manager;
        this.mType = mType;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.mPaint = new Paint(mPaint);
        this.xMargin = xMargin;
        this.yMargin = yMargin;
        Log.e(TAG, "BlurObject: ===x:"+x+",y:"+y+",w:"+w+",h:"+h+",xmargin:"+xMargin+",yMargin:"+yMargin );
        this.mOpacity = PreferenceConnector.readInteger(mContext, Configs.FLAG_BLUR_OPACTIY, 20);
        initialBlur();
    }



    private void initialBlur() {
        this.mPaint.setPathEffect(new CornerPathEffect(0.0f));
        reRenderBlurObject();
    }

    public void hideBlurObj() {
        if (this._blurBitmap != null) {
            this._blurBitmap.recycle();
            this._blurBitmap = null;
        }
    }

    public void resetData() {
        if (this._blurBitmap != null) {
            this._blurBitmap.recycle();
            this._blurBitmap = null;
        }
        if (this.blurManager != null) {
            this.blurManager.cleanData();
        }
        if (this.mListDot != null) {
            this.mListDot.clear();
        }
    }

    public void showBlurObj() {
        reRenderBlurObject();
    }

    public BlurObject copyCustomerView() {
        BlurObject blur = new BlurObject();
        blur._shapeId = this._shapeId;
        blur.mContext = this.mContext;
        blur.blurManager = this.blurManager;
        blur.x = this.x;
        blur.y = this.y;
        blur.w = this.w;
        blur.h = this.h;
        blur.xMargin = this.xMargin;
        blur.yMargin = this.yMargin;
        blur.mPaint = this.mPaint;
        blur.mType = this.mType;
        blur.createListPoint();
        blur.mOpacity = this.mOpacity;
        blur.mOldFlaotPointMarginBitmap = this.mOldFlaotPointMarginBitmap.copyFlaotPoint();
        blur.mWdithBitamp = this.mWdithBitamp;
        blur.mHeighBitmap = this.mHeighBitmap;
        blur.mDashLine = new Paint(1);
        blur.mDashLine.setColor(Color.parseColor("#D21A24"));
        blur.mDashLine.setStyle(Style.STROKE);
        blur.mDashLine.setStrokeWidth(3.0f);
        blur.mEffects = new PathEffect[3];
        blur.makeEffects(this.mEffects, this.mPhase);
        blur.mDashLine.setPathEffect(this.mEffects[2]);
        return blur;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeInt(this.mType);
        parcel.writeFloat(this.x);
        parcel.writeFloat(this.y);
        parcel.writeFloat(this.w);
        parcel.writeFloat(this.h);
        parcel.writeInt(this.mOpacity);
    }

    public void init(Parcel parcel) {
        super.init(parcel);
        this.mType = parcel.readInt();
        this.x = parcel.readFloat();
        this.y = parcel.readFloat();
        this.w = parcel.readFloat();
        this.h = parcel.readFloat();
        this.mOpacity = parcel.readInt();
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        this.mPaint.setDither(true);
        this.mPaint.setStyle(Style.STROKE);
        this.mPaint.setStrokeJoin(Join.ROUND);
        this.mPaint.setStrokeCap(Cap.ROUND);
        this.mEffects = new PathEffect[3];
        makeEffects(this.mEffects, this.mPhase);
        createListPoint();
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this._blurBitmap != null) {
            canvas.drawBitmap(this._blurBitmap, this.x + this.translateX, this.y + this.translateY, this.mPaint);
        }
        if (this.isFocus) {
            makeEffects(this.mEffects, this.mPhase);
            this.mPhase += 1.0f;
            this.mDashLine.setPathEffect(this.mEffects[2]);
            if (this.mPhase > 2.14748365E9f) {
                this.mPhase = 0.0f;
            }
            Canvas canvas2 = canvas;
            canvas2.drawRect((this.x + this.translateX) - 2, (this.y + this.translateY) - 2, 2 + this.w, 2 + this.h, this.mDashLine);
            for (int i = 0; i < this.mListDot.size(); i++) {
                this.mListDot.get(i).onDraw(canvas);
            }
        }
    }

    public void scaleAndChangePosition(FlaotPoint oldBitmapMarin, FlaotPoint newBitmapMargin, float scale) {
        super.scaleAndChangePosition(oldBitmapMarin, newBitmapMargin, scale);
        this.xMargin = newBitmapMargin.getX();
        this.yMargin = newBitmapMargin.getY();
        hideBlurObj();
    }

    public void reRenderBlurObject() {
        if (blurManager!=null&&(this._blurBitmap == null || this._blurBitmap.isRecycled())) {
            createBitmap(this.x, this.y, this.w, this.h);
        }
    }

    private void createBitmap(float x, float y, float w, float h) {
        this.translateY = 0.0f;
        this.translateX = 0.0f;
        if (x < this.xMargin) {
            this.translateX = this.xMargin - x;
            x = this.xMargin;
        }
        if (y < this.yMargin) {
            this.translateY = this.yMargin - y;
            y = this.yMargin;
        }
//        Bitmap largeBlurBitmap = this.blurManager.getBlurBitmapWithOpacity(this.mOpacity);
//        Bitmap largeBlurBitmap = Bitmap.createBitmap(blurManager.getMainBitmap(),(int)x,(int)y,(int)w,(int)h);
        if (w - this.xMargin > ((float) blurManager.getMainBitmap().getWidth())) {
            w = ((float) blurManager.getMainBitmap().getWidth()) + this.xMargin;
        }
        if (h - this.yMargin > ((float) blurManager.getMainBitmap().getHeight())) {
            h = ((float) blurManager.getMainBitmap().getHeight()) + this.yMargin;
        }
        try {
//            this._blurBitmap = Bitmap.createBitmap(largeBlurBitmap, (int) (x - this.xMargin), (int) (y - this.yMargin), (int) (w - x), (int) (h - y));
//            Log.e(TAG, "createBitmap: ====blur==========x="+ (int) (x - this.xMargin)+",y:"+(int) (y - this.yMargin)+"--w:"+(int) (w - x)+"--h:"+(int) (h - y));
            this._blurBitmap = getMosaicsBitmaps(Bitmap.createBitmap(blurManager.getMainBitmap(), (int) (x - this.xMargin), (int) (y - this.yMargin), (int) (w - x), (int) (h - y)),0.04f);
            this.x = x;
            this.y = y;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public Bitmap getMosaicsBitmaps(Bitmap bmp, double precent) {
        int bmpW = bmp.getWidth();
        int bmpH = bmp.getHeight();
        int[] pixels = new int[bmpH * bmpW];
        bmp.getPixels(pixels, 0, bmpW, 0, 0, bmpW, bmpH);
        int raw = (int) (bmpW * precent);
        int unit;
        if (raw == 0) {
            unit = bmpW;
        } else {
            unit = bmpW / raw; //原来的unit*unit像素点合成一个，使用原左上角的值
        }
        if (unit >= bmpW || unit >= bmpH) {
            return getMosaicsBitmap(bmp, precent);
        }
        for (int i = 0; i < bmpH; ) {
            for (int j = 0; j < bmpW; ) {
                int leftTopPoint = i * bmpW + j;
                for (int k = 0; k < unit; k++) {
                    for (int m = 0; m < unit; m++) {
                        int point = (i + k) * bmpW + (j + m);
                        if (point < pixels.length) {
                            pixels[point] = pixels[leftTopPoint];
                        }
                    }
                }
                j += unit;
            }
            i += unit;
        }
        return Bitmap.createBitmap(pixels, bmpW, bmpH, Bitmap.Config.ARGB_8888);
    }

    public Bitmap getMosaicsBitmap(Bitmap bmp, double precent) {
        int bmpW = bmp.getWidth();
        int bmpH = bmp.getHeight();
        Bitmap resultBmp = Bitmap.createBitmap(bmpW, bmpH, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(resultBmp);
        Paint paint = new Paint();
        double unit;
        if (precent == 0) {
            unit = bmpW;
        } else {
            unit = 1 / precent;
        }
        double resultBmpW = bmpW / unit;
        double resultBmpH = bmpH / unit;
        for (int i = 0; i < resultBmpH; i++) {
            for (int j = 0; j < resultBmpW; j++) {
                int pickPointX = (int) (unit * (j + 0.5));
                int pickPointY = (int) (unit * (i + 0.5));
                int color;
                if (pickPointX >= bmpW || pickPointY >= bmpH) {
                    color = bmp.getPixel(bmpW / 2, bmpH / 2);
                } else {
                    color = bmp.getPixel(pickPointX, pickPointY);
                }
                paint.setColor(color);
                canvas.drawRect((int) (unit * j), (int) (unit * i), (int) (unit * (j + 1)), (int) (unit * (i + 1)), paint);
            }
        }
        canvas.setBitmap(null);
        return resultBmp;
    }

    public void resizeBlurBitmap(float x, float y, float w, float h) {
        if (this._blurBitmap != null) {
            this._blurBitmap.recycle();
            this._blurBitmap = null;
        }
        createBitmap(x, y, w, h);
    }

    public void setOpacity(int opacity) {
        this.mOpacity = opacity;
        reRenderBlurObject();
    }

    public int getOpacity() {
        return this.mOpacity;
    }

    public void initNewPointPosition(float x, float y, float w, float h) {
        resize_Or_Move_Object(x, y, w, h);
    }

    public ArrayList<DotObject> getmListDot() {
        return this.mListDot;
    }

    public void setmListDot(ArrayList<DotObject> mListDot) {
        this.mListDot = mListDot;
    }

    public BlurManager getBlurManager() {
        return this.blurManager;
    }

    public void setBlurManager(BlurManager blurManager) {
        this.blurManager = blurManager;
    }

    public float getxMargin() {
        return this.xMargin;
    }

    public void setxMargin(float xMargin) {
        this.xMargin = xMargin;
    }

    public float getyMargin() {
        return this.yMargin;
    }

    public void setyMargin(float yMargin) {
        this.yMargin = yMargin;
    }


}

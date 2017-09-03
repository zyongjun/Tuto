package com.windhike.annotation.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.support.v4.view.ViewCompat;
import com.windhike.annotation.configsapp.Configs;

public class DotObject {
    private float mH;
    private int mIdDot;
    private Paint mPaint;
    private Paint mPaintCircle;
    private float mW;
    private float mX;
    private float mX_vitural_touch;
    private float mY;
    private float mY_vitural_touch;
    public static final float HUE_YELLOW = 60f;
    public static final float HUE_ORANGE = 30.0f;

    public DotObject(int idDot, float x, float y) {
        this.mIdDot = idDot;
        this.mX = x;
        this.mY = y;
        this.mW = HUE_YELLOW;
        this.mH = HUE_YELLOW;
        calculateTouchPoint();
        this.mPaint = new Paint();
        this.mPaint.setColor(ViewCompat.MEASURED_STATE_MASK);
        this.mPaint.setStyle(Style.STROKE);
        this.mPaint.setStrokeWidth(3.0f);
        this.mPaintCircle = new Paint();
        this.mPaintCircle.setColor(-1);
        this.mPaintCircle.setStyle(Style.FILL);
    }

    private void calculateTouchPoint() {
        if (this.mIdDot == 0) {
            this.mX_vitural_touch = this.mX - (this.mW / 2.0f);
            this.mY_vitural_touch = (this.mY - this.mH) + ((float) 20);
        } else if (this.mIdDot == 1) {
            this.mX_vitural_touch = this.mX - (this.mW / 2.0f);
            this.mY_vitural_touch = (this.mY - this.mH) + ((float) 20);
        } else if (this.mIdDot == 2) {
            this.mX_vitural_touch = this.mX - (this.mW / 2.0f);
            this.mY_vitural_touch = (this.mY - this.mH) + ((float) 20);
        } else if (this.mIdDot == 3) {
            this.mX_vitural_touch = this.mX - ((float) 20);
            this.mY_vitural_touch = this.mY - (this.mH / 2.0f);
        } else if (this.mIdDot == 4) {
            this.mX_vitural_touch = this.mX - (this.mW / 2.0f);
            this.mY_vitural_touch = this.mY - ((float) 20);
        } else if (this.mIdDot == 5) {
            this.mX_vitural_touch = this.mX - (this.mW / 2.0f);
            this.mY_vitural_touch = this.mY - ((float) 20);
        } else if (this.mIdDot == 6) {
            this.mX_vitural_touch = this.mX - (this.mW / 2.0f);
            this.mY_vitural_touch = this.mY - ((float) 20);
        } else if (this.mIdDot == 7) {
            this.mX_vitural_touch = (this.mX - this.mW) + ((float) 20);
            this.mY_vitural_touch = this.mY - (this.mH / 2.0f);
        }
    }

    public void onDraw(Canvas canvas) {
        canvas.drawCircle(this.mX, this.mY, Configs.SHOW_MENU_EDGE_WIDTH, this.mPaintCircle);
        canvas.drawCircle(this.mX, this.mY, Configs.SHOW_MENU_EDGE_WIDTH, this.mPaint);
    }

    public float getmX() {
        return this.mX;
    }

    public void setmX(float mX) {
        this.mX = mX;
        calculateTouchPoint();
    }

    public float getmY() {
        return this.mY;
    }

    public void setmY(float mY) {
        this.mY = mY;
        calculateTouchPoint();
    }

    public float getmW() {
        return this.mW;
    }

    public void setmW(float mW) {
        this.mW = mW;
    }

    public float getmH() {
        return this.mH;
    }

    public void setmH(float mH) {
        this.mH = mH;
    }

    public Paint getmPaint() {
        return this.mPaint;
    }

    public void setmPaint(Paint mPaint) {
        this.mPaint = mPaint;
    }

    public int getmIdDot() {
        return this.mIdDot;
    }

    public void setmIdDot(int mIdDot) {
        this.mIdDot = mIdDot;
    }

    public float getmX_vitural_touch() {
        return this.mX_vitural_touch;
    }

    public void setmX_vitural_touch(float mX_vitural_touch) {
        this.mX_vitural_touch = mX_vitural_touch;
    }

    public float getmY_vitural_touch() {
        return this.mY_vitural_touch;
    }

    public void setmY_vitural_touch(float mY_vitural_touch) {
        this.mY_vitural_touch = mY_vitural_touch;
    }
}

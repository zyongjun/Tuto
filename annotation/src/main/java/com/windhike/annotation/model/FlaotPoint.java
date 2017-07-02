package com.windhike.annotation.model;

/**
 * author: zyongjun on 2017/2/12 0012.
 * email: zhyongjun@windhike.cn
 */

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class FlaotPoint
        implements Parcelable
{
    private static final String TAG = "FlaotPoint";
    public static final Creator CREATOR = new Parcelable.Creator()
    {
        public FlaotPoint createFromParcel(Parcel paramAnonymousParcel)
        {
            FlaotPoint localFlaotPoint = new FlaotPoint();
            try
            {
                localFlaotPoint.init(paramAnonymousParcel);
                return localFlaotPoint;
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                Log.e(TAG, "createFromParcel: Failed to init Edition from parcel");
            }
            return null;
        }

        public FlaotPoint[] newArray(int size)
        {
            return new FlaotPoint[size];
        }
    };
    private float x;
    private float y;

    public FlaotPoint() {}

    public FlaotPoint(float paramFloat1, float paramFloat2)
    {
        this.x = paramFloat1;
        this.y = paramFloat2;
    }

    public FlaotPoint copyFlaotPoint()
    {
        return new FlaotPoint(this.x, this.y);
    }

    public int describeContents()
    {
        return 0;
    }

    public float getX()
    {
        return this.x;
    }

    public float getY()
    {
        return this.y;
    }

    public void init(Parcel paramParcel)
    {
        this.x = paramParcel.readFloat();
        this.y = paramParcel.readFloat();
    }

    public void setX(float paramFloat)
    {
        this.x = paramFloat;
    }

    public void setY(float paramFloat)
    {
        this.y = paramFloat;
    }

    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
        paramParcel.writeFloat(this.x);
        paramParcel.writeFloat(this.y);
    }

    @Override
    public String toString() {
        return "FlaotPoint{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}

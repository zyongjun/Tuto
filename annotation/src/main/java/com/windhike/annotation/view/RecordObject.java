package com.windhike.annotation.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewCompat;

import com.windhike.annotation.R;
import com.windhike.annotation.configsapp.AnnotationInitialize;
import com.windhike.annotation.configsapp.Configs;
import com.windhike.annotation.model.FlaotPoint;

import java.util.ArrayList;

public class RecordObject extends CustomerShapeView {
    public static final Parcelable.Creator<RecordObject> CREATOR = new Parcelable.Creator<RecordObject>() {
        public RecordObject createFromParcel(Parcel in) {
            RecordObject textObj = new RecordObject();
            try {
                textObj.init(in);
                return textObj;
            } catch (Throwable th) {
                th.printStackTrace();
                return null;
            }
        }

        public RecordObject[] newArray(int size) {
            return new RecordObject[size];
        }
    };
    private final int FIXDOT = 4;
    public int alphaFrame = 10;
    private int alphaWidth = 10;
    private Paint mPaintBorder;
    private Paint mPaintFrame;
    public final int margin = 8;
    private Bitmap mBitmapPrepare;
    private Bitmap mBitmapPause;
    private Bitmap mBitmapDelete;
    private Uri audioPath;
    //0 prepare 1 playing //2 delete
    public int state;

    public Uri getAudioPath() {
        return audioPath;
    }

    public RecordObject(){}
    public RecordObject(Context mContext, int mType, float x, float y, float w, float h, Paint mPaint, boolean isGetCurrent_Width_Heigh, FlaotPoint mOldFlaotPointMarginBitmap, float mWdithBitamp, float mHeighBitmap, Uri audioPath) {
        super(mContext, mType, x, y, w, h, mPaint, mOldFlaotPointMarginBitmap, mWdithBitamp, mHeighBitmap);
        this.audioPath = audioPath;
        this.mType = mType;
        this.x = x;
        this.y = y;
        mBitmapPrepare = BitmapFactory.decodeResource(AnnotationInitialize.getInstance().getContext().getResources(), R.mipmap.ic_anno_voice_prepare);
        mBitmapPause = BitmapFactory.decodeResource(AnnotationInitialize.getInstance().getContext().getResources(), R.mipmap.ic_anno_voice_pause);
        mBitmapDelete = BitmapFactory.decodeResource(AnnotationInitialize.getInstance().getContext().getResources(), R.mipmap.ic_anno_voice_delete);

        this.mPaint = new Paint(mPaint);
        this.mPaint.setPathEffect(new CornerPathEffect(0.0f));
        this.mPaint.setStyle(Style.FILL);
        if (isGetCurrent_Width_Heigh) {
            this.w = w;
            this.h = h;
        } else {
            this.w = mBitmapPrepare.getWidth();
            this.h = mBitmapPrepare.getHeight();
        }
        createListPoint();
        this.mPaintFrame = new Paint();
        this.mPaintFrame.setColor(ViewCompat.MEASURED_STATE_MASK);
        this.mPaintFrame.setStyle(Style.STROKE);
        this.mPaintFrame.setStrokeWidth(3.0f);
        this.mPaintBorder = new Paint(this.mPaint);
        this.mPaintBorder.setPathEffect(new CornerPathEffect(0.0f));
        this.mPaintBorder.setStyle(Style.FILL_AND_STROKE);
        changeColorBorder();
    }

    public void resetData() {
        super.resetData();
        if (this.mPaintFrame != null) {
            this.mPaintFrame = null;
        }
        if (this.mPaintBorder != null) {
            this.mPaintBorder = null;
        }
    }

    public void changeColorBorder() {
        if (this.mPaint.getColor() == Configs.LIST_COLOR[5]) {
            this.mPaintBorder.setColor(ViewCompat.MEASURED_STATE_MASK);
        } else {
            this.mPaintBorder.setColor(-1);
        }
    }

    public void resize_Or_Move_Object(float x, float y, float w, float h) {
        for (int i = 0; i < this.mListDot.size(); i++) {
            float xNewPosition = 0.0f;
            float yNewPosition = 0.0f;
            switch (i) {
                case 0:
                    xNewPosition = (x - 5.0f) - ((float) this.alphaFrame);
                    yNewPosition = (y - Configs.SHOW_MENU_EDGE_WIDTH) - ((float) this.alphaFrame);
                    break;
                case 1:
                    xNewPosition = w + 5.0f;
                    yNewPosition = (y - Configs.SHOW_MENU_EDGE_WIDTH) - ((float) this.alphaFrame);
                    break;
                case 2:
                    xNewPosition = w + 5.0f;
                    yNewPosition = (h + Configs.SHOW_MENU_EDGE_WIDTH) + ((float) this.alphaFrame);
                    break;
                case 3:
                    xNewPosition = (x - 5.0f) - ((float) this.alphaFrame);
                    yNewPosition = (h + Configs.SHOW_MENU_EDGE_WIDTH) + ((float) this.alphaFrame);
                    break;
                default:
                    break;
            }
            this.mListDot.get(i).setmX(xNewPosition);
            this.mListDot.get(i).setmY(yNewPosition);
        }
    }

    public void move() {

    }

    public RecordObject copyCustomerView() {
        RecordObject textObj = new RecordObject(this.mContext, this.mType,this.x, this.y, this.w, this.h, this.mPaint, true, this.mOldFlaotPointMarginBitmap.copyFlaotPoint(), this.mWdithBitamp, this.mHeighBitmap,audioPath);
        textObj._shapeId = this._shapeId;
        return textObj;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeInt(this.mType);
        parcel.writeFloat(this.x);
        parcel.writeFloat(this.y);
        parcel.writeFloat(this.w);
        parcel.writeFloat(this.h);
        parcel.writeInt(this.mPaint.getColor());
        parcel.writeFloat(this.mPaint.getStrokeWidth());
        parcel.writeFloat(this.mPaint.getTextSize());
        parcel.writeParcelable(audioPath,flags);
        parcel.writeInt(0);
    }

    public void init(Parcel parcel) {
        super.init(parcel);
        this.mType = parcel.readInt();
        this.x = parcel.readFloat();
        this.y = parcel.readFloat();
        this.w = parcel.readFloat();
        this.h = parcel.readFloat();

        this.mColor = parcel.readInt();
        this.mStrokeWith = parcel.readFloat();
        this.mTextSize = parcel.readFloat();
        this.audioPath = parcel.readParcelable(Uri.class.getClassLoader());
         parcel.readInt();
        this.state = 0;
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        this.mPaint.setDither(true);
        this.mPaint.setColor(this.mColor);
        this.mPaint.setStrokeWidth(this.mStrokeWith);
        this.mPaint.setTextSize(this.mTextSize);
        this.mPaint.setStyle(Style.FILL);
        this.mPaint.setStrokeJoin(Join.ROUND);
        this.mPaint.setStrokeCap(Cap.ROUND);
        createListPoint();
        this.mPaintFrame = new Paint();
        this.mPaintFrame.setColor(ViewCompat.MEASURED_STATE_MASK);
        this.mPaintFrame.setStyle(Style.STROKE);
        this.mPaintFrame.setStrokeWidth(3.0f);
        this.mPaintBorder = new Paint(this.mPaint);
        this.mPaintBorder.setPathEffect(new CornerPathEffect(0.0f));
        this.mPaintBorder.setStyle(Style.FILL_AND_STROKE);
        changeColorBorder();
        mBitmapPrepare = BitmapFactory.decodeResource(AnnotationInitialize.getInstance().getContext().getResources(), R.mipmap.ic_anno_voice_prepare);
        mBitmapPause = BitmapFactory.decodeResource(AnnotationInitialize.getInstance().getContext().getResources(), R.mipmap.ic_anno_voice_pause);
        mBitmapDelete = BitmapFactory.decodeResource(AnnotationInitialize.getInstance().getContext().getResources(), R.mipmap.ic_anno_voice_delete);
    }

    public void initNewText() {
        this.w = mBitmapPrepare.getWidth();
        this.h = mBitmapPrepare.getHeight();
    }

    public void scaleAndChangePositionForTextObject(FlaotPoint oldBitmapMarin, FlaotPoint newBitmapMargin, float scale) {
        super.scaleAndChangePositionForTextObject(oldBitmapMarin, newBitmapMargin, scale);
        initNewText();
//        if (this.mListDot != null) {
//            this.mListDot.clear();
//            createListPoint();
//        }
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(state==0&&mBitmapPrepare != null){
            canvas.drawBitmap(mBitmapPrepare,x,y,null);
        } else if (state == 1 && mBitmapPause != null) {
            canvas.drawBitmap(mBitmapPause,x,y,null);
        }else if(mBitmapDelete!=null){
            canvas.drawBitmap(mBitmapDelete,x,y,null);
        }
    }

    public ArrayList<DotObject> getmListDot() {
        return this.mListDot;
    }

    public void setmListDot(ArrayList<DotObject> mListDot) {
        this.mListDot = mListDot;
    }

    public int getAlphaWidth() {
        return this.alphaWidth;
    }

    public void setAlphaWidth(int alphaWidth) {
        this.alphaWidth = alphaWidth;
    }

    @Override
    public String toString() {
        return "RecordObject{" +
                "audioPath=" + audioPath +
                '}';
    }
}
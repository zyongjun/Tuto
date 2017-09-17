package com.windhike.annotation.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Parcel;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import com.windhike.annotation.R;
import com.windhike.annotation.common.LogUtils;
import com.windhike.annotation.configsapp.AnnotationInitialize;
import com.windhike.annotation.configsapp.Configs;
import com.windhike.annotation.model.FlaotPoint;
import java.util.ArrayList;
import java.util.List;

public class TextObject extends CustomerShapeView {
    public static final Creator<TextObject> CREATOR = new Creator<TextObject>() {
        public TextObject createFromParcel(Parcel in) {
            TextObject textObj = new TextObject();
            try {
                textObj.init(in);
                return textObj;
            } catch (Throwable th) {
                LogUtils.LogError("Edition.createFromParcel", "Failed to init Edition from parcel");
                return null;
            }
        }

        public TextObject[] newArray(int size) {
            return new TextObject[size];
        }
    };
    private final int FIXDOT = 4;
    public int alphaFrame = 10;
    private int alphaWidth = 10;
    private String[] arrayText;
    private int line = 0;
    private float mMinHeightText;
    private float mMinWidthText;
    private Paint mPaintBorder;
    private Paint mPaintFrame;
    public final int margin = 8;
    private String text;
    private static final String TAG = "TextObject";
    public TextObject(){}
    public TextObject(Context mContext, int mType, String text, float x, float y, float w, float h, Paint mPaint, boolean isGetCurrent_Width_Heigh, FlaotPoint mOldFlaotPointMarginBitmap, float mWdithBitamp, float mHeighBitmap) {
        super(mContext, mType, x, y, w, h, mPaint, mOldFlaotPointMarginBitmap, mWdithBitamp, mHeighBitmap);
        Log.e(TAG, "TextObject() called with: mContext = [" + mContext + "], mType = [" + mType + "], text = [" + text + "], x = [" + x + "], y = [" + y + "], w = [" + w + "], h = [" + h + "], mPaint = [" + mPaint + "], isGetCurrent_Width_Heigh = [" + isGetCurrent_Width_Heigh + "], mOldFlaotPointMarginBitmap = [" + mOldFlaotPointMarginBitmap + "], mWdithBitamp = [" + mWdithBitamp + "], mHeighBitmap = [" + mHeighBitmap + "]");
        this.mType = mType;
        this.text = text;
        this.x = x;
        this.y = y;
        this.mPaint = new Paint(mPaint);
        this.mPaint.setPathEffect(new CornerPathEffect(0.0f));
        this.mPaint.setStyle(Style.FILL);
        this.mPaint.setTextSize(mContext.getResources().getDimensionPixelSize(R.dimen.s_16));
        this.mMinWidthText = getMinWidthText(text);
        this.mMinHeightText = getMinHeighText();
        if (isGetCurrent_Width_Heigh) {
            this.w = w;
            this.h = h;
        } else {
            this.w = (this.x + this.mMinWidthText) + ((float) this.alphaWidth);
            this.h = this.y + detectHeighBound();
        }
        createListPoint();
        this.mPaintFrame = new Paint();
        this.mPaintFrame.setColor(Color.TRANSPARENT);
        this.mPaintFrame.setStyle(Style.FILL);
        this.mPaintBorder = new Paint(this.mPaint);
        this.mPaintBorder.setPathEffect(new CornerPathEffect(0.0f));
        this.mPaintBorder.setStyle(Style.FILL_AND_STROKE);
        changeColorBorder();
        edit = BitmapFactory.decodeResource(mContext.getResources(),R.mipmap.anno_txt_edit);
        del = BitmapFactory.decodeResource(mContext.getResources(),R.mipmap.anno_txt_del);
        clickBitmapWidth = edit.getWidth();
    }

    private int clickBitmapWidth;

    public float getClickBitmapWidth() {
        return clickBitmapWidth;
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

    public void setTextColor_Stroke_Size(int color, float stroke, float size) {
        this.mPaint.setColor(color);
        this.mPaint.setStrokeWidth(stroke);
        this.mPaint.setTextSize(size);
        this.mPaintBorder = null;
        this.mPaintBorder = new Paint(this.mPaint);
        this.mPaintBorder.setPathEffect(new CornerPathEffect(0.0f));
        this.mPaintBorder.setStyle(Style.FILL_AND_STROKE);
        changeColorBorder();
    }

    public int getIndexTextSize() {
        float textSizeIndex = this.mPaint.getTextSize();
        for (int i = 0; i < Configs.LIST_TEXT_SIZE.length; i++) {
            if (Configs.LIST_TEXT_SIZE[i] == textSizeIndex) {
                return i;
            }
        }
        return 0;
    }

    public int getIndexColor() {
        float colorIndex = (float) this.mPaint.getColor();
        for (int i = 0; i < Configs.LIST_TEXT_SIZE.length; i++) {
            if (((float) Configs.LIST_COLOR[i]) == colorIndex) {
                return i;
            }
        }
        return 0;
    }

    public void createListPoint() {
        if (this.mListDot != null) {
            this.mListDot.clear();
        }
        this.mListDot = new ArrayList();
        for (int i = 0; i < 4; i++) {
            DotObject dot = null;
            float mYdot;
            switch (i) {
                case 0:
                    dot = new DotObject(i, this.x, this.y);
                    mYdot = (dot.getmY() - Configs.SHOW_MENU_EDGE_WIDTH) - ((float) this.alphaFrame);
                    dot.setmX((dot.getmX() - 5.0f) - ((float) this.alphaFrame));
                    dot.setmY(mYdot);
                    break;
                case 1:
                    dot = new DotObject(i, this.w, this.y);
                    mYdot = (dot.getmY() - Configs.SHOW_MENU_EDGE_WIDTH) - ((float) this.alphaFrame);
                    dot.setmX(dot.getmX() + 5.0f);
                    dot.setmY(mYdot);
                    break;
                case 2:
                    dot = new DotObject(i, this.w, this.h);
                    dot.setmX(dot.getmX() + 5.0f);
                    dot.setmY((dot.getmY() + Configs.SHOW_MENU_EDGE_WIDTH) + ((float) this.alphaFrame));
                    break;
                case 3:
                    dot = new DotObject(i, this.x, this.h);
                    dot.setmX((dot.getmX() - 5.0f) - ((float) this.alphaFrame));
                    dot.setmY((dot.getmY() + Configs.SHOW_MENU_EDGE_WIDTH) + ((float) this.alphaFrame));
                    break;
                default:
                    break;
            }
            this.mListDot.add(dot);
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

    public float detectHeighBound() {
        this.line = 0;
        float countWidthOneLine = 0.0f;
        String mCurrentString = "";
        for (int i=0;i < this.arrayText.length;i++) {
            float currentWidth;
            if (i < this.arrayText.length - 1) {
                currentWidth = this.mPaint.measureText(this.arrayText[i]) + 8.0f;
                mCurrentString = new StringBuilder(String.valueOf(mCurrentString)).append(this.arrayText[i]).append(" ").toString();
            } else {
                currentWidth = this.mPaint.measureText(this.arrayText[i]);
                mCurrentString = new StringBuilder(String.valueOf(mCurrentString)).append(this.arrayText[i]).toString();
            }
            countWidthOneLine += currentWidth;
            float mCountNextTextForBreakLine = countWidthOneLine;
            if (i < this.arrayText.length - 1) {
                mCountNextTextForBreakLine += this.mPaint.measureText(this.arrayText[i + 1]);
            }
            if (mCountNextTextForBreakLine >= this.w - this.x && i <= this.arrayText.length - 1) {
                this.line++;
                countWidthOneLine = 0.0f;
                mCurrentString = "";
            }
        }
        return (((float) this.line) * this.mMinHeightText) + this.mMinHeightText;
    }

    public float getMinHeighText() {
        return this.mPaint.descent() - this.mPaint.ascent();
    }

    public float getMinWidthText(String text) {
        float minWidth = 0.0f;
        List<String> strings = new ArrayList<>();
        int index = 0;
        int length = text.length();
        while (index < length) {
            String lineText = text.substring(index, Math.min(index + 13,length));
            if(lineText.contains("\\r\\n")||lineText.contains("\\n")){
                lineText = text.substring(index,index+lineText.indexOf("\\n"));
            }
            strings.add(lineText);
            index += lineText.length();
        }
        arrayText = strings.toArray(new String[strings.size()]);
        for (String measureText : this.arrayText) {
            float currentWidth = this.mPaint.measureText(measureText);
            if (minWidth < currentWidth) {
                minWidth = currentWidth;
            }
        }
        return minWidth;
    }

    public float getMaxWidthText() {
        return this.mPaint.measureText(this.text);
    }

    public TextObject copyCustomerView() {
        TextObject textObj = new TextObject(this.mContext, this.mType, this.text, this.x, this.y, this.w, this.h, this.mPaint, true, this.mOldFlaotPointMarginBitmap.copyFlaotPoint(), this.mWdithBitamp, this.mHeighBitmap);
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
        parcel.writeString(this.text);
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
        this.text = parcel.readString();
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        this.mPaint.setDither(true);
        this.mPaint.setColor(mColor);
        this.mPaint.setStrokeWidth(this.mStrokeWith);
        this.mPaint.setTextSize(mTextSize);
        this.mPaint.setStyle(Style.FILL);
        this.mMinWidthText = getMinWidthText(this.text);
        this.mMinHeightText = getMinHeighText();
        createListPoint();
        this.mPaintFrame = new Paint();
        this.mPaintFrame.setColor(Color.TRANSPARENT);
        this.mPaintFrame.setStyle(Style.FILL);
//        this.mPaintFrame.setStrokeWidth(3.0f);
        this.mPaintBorder = new Paint(this.mPaint);
        this.mPaintBorder.setPathEffect(new CornerPathEffect(0.0f));
        this.mPaintBorder.setStyle(Style.FILL_AND_STROKE);
        changeColorBorder();
        edit = BitmapFactory.decodeResource(AnnotationInitialize.getInstance().getContext().getResources(),R.mipmap.anno_txt_edit);
        del = BitmapFactory.decodeResource(AnnotationInitialize.getInstance().getContext().getResources(),R.mipmap.anno_txt_del);
        clickBitmapWidth = edit.getWidth();
    }

    public void moveText() {
    }

    public void initNewText(String textValue) {
        this.text = textValue;
        this.mMinWidthText = getMinWidthText(this.text);
        this.mMinHeightText = getMinHeighText();
        this.w = (this.x + this.mMinWidthText) + ((float) this.alphaWidth);
        this.h = this.y + detectHeighBound();
        createListPoint();
    }

    public void detectWidthAndHeighText() {
    }

    public void scaleAndChangePositionForTextObject(FlaotPoint oldBitmapMarin, FlaotPoint newBitmapMargin, float scale) {
        super.scaleAndChangePositionForTextObject(oldBitmapMarin, newBitmapMargin, scale);
        initNewText(this.text);
        if (this.mListDot != null) {
            this.mListDot.clear();
            createListPoint();
        }
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        RectF rect = new RectF((int)mListDot.get(0).getmX(),(int)mListDot.get(1).getmY(),(int)mListDot.get(2).getmX(),(int)mListDot.get(3).getmY());
        canvas.drawRoundRect(rect,7f,7f,mPaintFrame);
        line = 0;
        float countWidthOneLine = 0.0f;
        String mCurrentString = "";
        for (int i = 0;i < this.arrayText.length;i++) {
            float currentWidth;
            if (i < this.arrayText.length - 1) {
                currentWidth = this.mPaint.measureText(this.arrayText[i]) + 8.0f;
                mCurrentString = new StringBuilder(mCurrentString).append(this.arrayText[i]).append(" ").toString();
            } else {
                currentWidth = this.mPaint.measureText(this.arrayText[i]);
                mCurrentString = new StringBuilder(mCurrentString).append(this.arrayText[i]).toString();
            }
            countWidthOneLine += currentWidth;
            float mCountNextTextForBreakLine = countWidthOneLine;
            if (i < this.arrayText.length - 1) {
                mCountNextTextForBreakLine += this.mPaint.measureText(this.arrayText[i + 1]);
            }
            if (mCountNextTextForBreakLine < this.w - this.x || i > this.arrayText.length - 1) {
                canvas.drawText(mCurrentString, this.x, ((this.y + (this.mMinHeightText / 2.0f)) + (this.mMinHeightText / 4.0f)) + (((float) line) * this.mMinHeightText), this.mPaint);
            } else {
                canvas.drawText(mCurrentString, this.x, ((this.y + (this.mMinHeightText / 2.0f)) + (this.mMinHeightText / 4.0f)) + (((float) line) * this.mMinHeightText), this.mPaint);
                line++;
                countWidthOneLine = 0.0f;
                mCurrentString = "";
            }
        }


        if (this.isFocus) {
//            for (int i = 0; i < this.mListDot.size(); i++) {
//                this.mListDot.get(i).onDraw(canvas);
//            }
            int bw = edit.getWidth();
            int bh = edit.getHeight();
            canvas.drawBitmap(edit,mListDot.get(2).getmX()-bw/2f,mListDot.get(2).getmY()-bh/2f,null);
            canvas.drawBitmap(del,mListDot.get(1).getmX()-bw/2f,mListDot.get(1).getmY()-bh/2f,null);
        }
    }
    private Bitmap edit;
    private Bitmap del;

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ArrayList<DotObject> getmListDot() {
        return this.mListDot;
    }

    public void setmListDot(ArrayList<DotObject> mListDot) {
        this.mListDot = mListDot;
    }

    public float getmMinWidthText() {
        return this.mMinWidthText;
    }

    public void setmMinWidthText(float mMinWidthText) {
        this.mMinWidthText = mMinWidthText;
    }

    public float getmMinHeightText() {
        return this.mMinHeightText;
    }

    public void setmMinHeightText(float mMinHeightText) {
        this.mMinHeightText = mMinHeightText;
    }

    public int getAlphaWidth() {
        return this.alphaWidth;
    }

    public void setAlphaWidth(int alphaWidth) {
        this.alphaWidth = alphaWidth;
    }

    @Override
    public String toString() {
        return "TextObject{" +
                "line=" + line +
                ", margin=" + margin +
                ", text='" + text + '\'' +
                '}';
    }
}
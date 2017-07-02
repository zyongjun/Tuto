package com.windhike.annotation.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.windhike.annotation.view.CustomerShapeView;

import java.util.ArrayList;

public class ImageDrawObject
        implements Parcelable
{
    private static final String TAG = "ImageDrawObject";
    public static final Creator CREATOR = new Parcelable.Creator()
    {
        public ImageDrawObject createFromParcel(Parcel paramAnonymousParcel)
        {
            ImageDrawObject localImageDrawObject = new ImageDrawObject();
            try
            {
                localImageDrawObject.init(paramAnonymousParcel);
                return localImageDrawObject;
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                Log.e(TAG, "ImageDrawObject  Failed to init Edition from parcel");
            }
            return null;
        }

        public ImageDrawObject[] newArray(int size)
        {
            return new ImageDrawObject[size];
        }
    };
    private String EditImagePath = "";
    private int _undoRedoCurrentIndex;
    private int isSelect = 0;
    private ArrayList<CustomerShapeView> listRootShape = new ArrayList();
    private String mDescription = "";
//    private String imageDrawedPath = "";
    private String originalImageGalleryPath = "";
    private String originalImagePath = "";
    private String remarkId = "";
    private String url = "";
    private String mDate = "";

    public String getRemarkId() {
        return remarkId;
    }

    public void setRemarkId(String remarkId) {
        this.remarkId = remarkId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getmDate() {
        return mDate;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    public int describeContents()
    {
        return 0;
    }

    public void init(Parcel parcel) {
        this.isSelect = parcel.readInt();
        this.originalImageGalleryPath = parcel.readString();
        this.originalImagePath = parcel.readString();
        this.EditImagePath = parcel.readString();
        this.listRootShape = parcel.readArrayList(CustomerShapeView.class.getClassLoader());
        this.mDescription = parcel.readString();
//        this.imageDrawedPath = parcel.readString();
//        this._undoRedoCurrentIndex = parcel.readInt();
//        this._undoRedoCurrentIndex = listRootShape.size();
        this.remarkId = parcel.readString();
        this.url = parcel.readString();
        this.mDate = parcel.readString();
    }

    public void resetData() {
        for (int i = 0; i < this.listRootShape.size(); i++) {
            CustomerShapeView shape = this.listRootShape.get(i);
            if (shape != null && shape.mType == 4) {
                shape.resetData();
            } else if (shape != null && shape.mType == 1) {
                shape.resetData();
            } else if (shape != null && shape.mType == 2) {
                shape.resetData();
            } else if (shape != null && shape.mType == 3) {
                shape.resetData();
            } else if (shape != null && shape.mType == 5) {
                shape.resetData();
            } else if (shape != null && shape.mType == 6) {
                shape.resetData();
            }
        }
        this.listRootShape.clear();
    }

    public int get_undoRedoCurrentIndex() {
        return this._undoRedoCurrentIndex;
    }

    public void set_undoRedoCurrentIndex(int _undoRedoCurrentIndex) {
        this._undoRedoCurrentIndex = _undoRedoCurrentIndex;
    }

    public String getOriginalImagePath() {
        return this.originalImagePath;
    }

    public void setOriginalImagePath(String originalImagePath) {
        this.originalImagePath = originalImagePath;
    }

    public String getEditImagePath() {
        return this.EditImagePath;
    }

    public void setEditImagePath(String editImagePath) {
        this.EditImagePath = editImagePath;
    }

    public ArrayList<CustomerShapeView> getListRootShape() {
        return this.listRootShape;
    }

    public void setListRootShape(ArrayList<CustomerShapeView> listRootShape) {
        this.listRootShape = listRootShape;
    }

    public String getOriginalImageGalleryPath() {
        return this.originalImageGalleryPath;
    }

    public void setOriginalImageGalleryPath(String originalImageGalleryPath) {
        this.originalImageGalleryPath = originalImageGalleryPath;
    }

    public int getIsSelect() {
        return this.isSelect;
    }

    public void setIsSelect(int isSelect) {
        this.isSelect = isSelect;
    }

    public String getmDescription() {
        return this.mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }


    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.isSelect);
        parcel.writeString(this.originalImageGalleryPath);
        parcel.writeString(this.originalImagePath);
        parcel.writeString(this.EditImagePath);
        parcel.writeList(this.listRootShape);
        parcel.writeString(this.mDescription);
//        parcel.writeString(this.imageDrawedPath);
//        parcel.writeInt(this._undoRedoCurrentIndex);
        parcel.writeString(this.remarkId);
        parcel.writeString(this.url);
        parcel.writeString(mDate);
    }
}

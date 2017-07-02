package com.windhike.annotation.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * author: zyongjun on 2017/6/30 0030.
 * email: zhyongjun@windhike.cn
 */

public class SaveSelectState implements Parcelable {
    private Uri uri;
    private String path;
    private boolean isSelect;
    private String folderPath;

    public SaveSelectState(Uri mUri, boolean isSelect,String path) {
        this.uri = mUri;
        this.isSelect = isSelect;
        this.path = path;
    }

    protected SaveSelectState(Parcel in) {
        uri = in.readParcelable(Uri.class.getClassLoader());
        path = in.readString();
        isSelect = in.readByte() != 0;
        folderPath = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(uri, flags);
        dest.writeString(path);
        dest.writeByte((byte) (isSelect ? 1 : 0));
        dest.writeString(folderPath);
    }

    public static final Creator<SaveSelectState> CREATOR = new Creator<SaveSelectState>() {
        @Override
        public SaveSelectState createFromParcel(Parcel in) {
            return new SaveSelectState(in);
        }

        @Override
        public SaveSelectState[] newArray(int size) {
            return new SaveSelectState[size];
        }
    };

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public Uri getUri() {
        return this.uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public boolean isSelect() {
        return this.isSelect;
    }

    public void setIsSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

}
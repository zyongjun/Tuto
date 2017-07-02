package com.windhike.tuto.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * author: zyongjun on 2017/3/13 0013.
 * email: zhyongjun@windhike.cn
 */
public class AnnotationModel implements Parcelable {
    private String id;
    private String remarkid;
    private String url;
    private String uid;
    private long createtime;
    private long updatetime;
    private String isdel;
    private String thumbnail;


    public AnnotationModel() {

    }

    public String getId() {
        return this.id;
    }

    protected AnnotationModel(Parcel in) {
        id = in.readString();
        remarkid = in.readString();
        url = in.readString();
        uid = in.readString();
        createtime = in.readLong();
        updatetime = in.readLong();
        isdel = in.readString();
        thumbnail = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(remarkid);
        dest.writeString(url);
        dest.writeString(uid);
        dest.writeLong(createtime);
        dest.writeLong(updatetime);
        dest.writeString(isdel);
        dest.writeString(thumbnail);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AnnotationModel> CREATOR = new Creator<AnnotationModel>() {
        @Override
        public AnnotationModel createFromParcel(Parcel in) {
            return new AnnotationModel(in);
        }

        @Override
        public AnnotationModel[] newArray(int size) {
            return new AnnotationModel[size];
        }
    };

    public void setId(String id) {
        this.id = id;
    }

    public String getIsdel() {
        return isdel;
    }

    public void setIsdel(String isdel) {
        this.isdel = isdel;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getRemarkid() {
        return remarkid;
    }

    public void setRemarkid(String remarkid) {
        this.remarkid = remarkid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }

    public long getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(long updatetime) {
        this.updatetime = updatetime;
    }

    @Override
    public String toString() {
        return "AnnotationModel{" +
                "id='" + id + '\'' +
                ", remarkid='" + remarkid + '\'' +
                ", url='" + url + '\'' +
                ", uid='" + uid + '\'' +
                ", createtime=" + createtime +
                ", updatetime=" + updatetime +
                ", isdel='" + isdel + '\'' +
                ", thumbnail='" + thumbnail.length() + '\'' +
                '}';
    }
}

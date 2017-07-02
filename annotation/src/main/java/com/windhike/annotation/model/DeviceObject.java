package com.windhike.annotation.model;

/**
 * author: zyongjun on 2017/2/12 0012.
 * email: zhyongjun@windhike.cn
 */

public class DeviceObject {
    private String mModel = "";
    private String mResolution="";
    private String mSystemVersion="";

    public DeviceObject() {

    }

    public String getmSystemVersion() {
        return this.mSystemVersion;
    }

    public void setmSystemVersion(String mSystemVersion) {
        this.mSystemVersion = mSystemVersion;
    }

    public String getmModel() {
        return this.mModel;
    }

    public void setmModel(String mModel) {
        this.mModel = mModel;
    }

    public String getmResolution() {
        return this.mResolution;
    }

    public void setmResolution(String mResolution) {
        this.mResolution = mResolution;
    }
}
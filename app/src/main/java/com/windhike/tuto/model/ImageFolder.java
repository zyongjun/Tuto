package com.windhike.tuto.model;

import java.io.Serializable;

/**
 * author: zyongjun on 2017/7/1 0001.
 * email: zhyongjun@windhike.cn
 */

public class ImageFolder implements Serializable {

    private String topImagePath;
    private String folderName;
    private int imageCounts;
    private String ablumPath;

    public String getTopImagePath() {
        return topImagePath;
    }
    public void setTopImagePath(String topImagePath) {
        this.topImagePath = topImagePath;
    }
    public String getFolderName() {
        return folderName;
    }
    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
    public int getImageCounts() {
        return imageCounts;
    }
    public void setImageCounts(int imageCounts) {
        this.imageCounts = imageCounts;
    }

    public String getAblumPath() {
        return ablumPath;
    }

    public void setAblumPath(String ablumPath) {
        this.ablumPath = ablumPath;
    }

    @Override
    public String toString() {
        return "imagefolder:[path:"+topImagePath+"][name:"+folderName+"][count:"+imageCounts+"][ablumpath:"+ablumPath+"]";
    }
}

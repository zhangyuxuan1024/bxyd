package net.iclassmate.bxyd.bean.netdisk;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by xydbj on 2016.9.22.
 */
public class VideoInfo implements Serializable{
    private String displayName;
    private String path;
    private String allTime;
    private String allSize;
    private String imgThumb;
    private Bitmap bitmapThumb;
    private boolean isSelected;

    @Override
    public String toString() {
        return "VideoInfo{" +
                "displayName='" + displayName + '\'' +
                ", path='" + path + '\'' +
                ", allTime='" + allTime + '\'' +
                ", allSize='" + allSize + '\'' +
                ", imgThumb='" + imgThumb + '\'' +
                ", bitmapThumb=" + bitmapThumb +
                ", isSelected=" + isSelected +
                '}';
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public Bitmap getBitmapThumb() {
        return bitmapThumb;
    }

    public void setBitmapThumb(Bitmap bitmapThumb) {
        this.bitmapThumb = bitmapThumb;
    }

    public String getAllTime() {
        return allTime;
    }

    public void setAllTime(String allTime) {
        this.allTime = allTime;
    }

    public String getAllSize() {
        return allSize;
    }

    public void setAllSize(String allSize) {
        this.allSize = allSize;
    }

    public String getImgThumb() {
        return imgThumb;
    }

    public void setImgThumb(String imgThumb) {
        this.imgThumb = imgThumb;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

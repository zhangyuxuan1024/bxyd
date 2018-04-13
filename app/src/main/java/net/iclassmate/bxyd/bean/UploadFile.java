package net.iclassmate.bxyd.bean;

import android.graphics.Bitmap;

/**
 * Created by xydbj on 2016.10.20.
 */
public class UploadFile {
    private String fileName;
    private String fileId;
    private String fileIcon;
    private Bitmap fileBitmapIcon;

    public Bitmap getFileBitmapIcon() {
        return fileBitmapIcon;
    }

    public void setFileBitmapIcon(Bitmap fileBitmapIcon) {
        this.fileBitmapIcon = fileBitmapIcon;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileIcon() {
        return fileIcon;
    }

    public void setFileIcon(String fileIcon) {
        this.fileIcon = fileIcon;
    }

    @Override
    public String toString() {
        return "UploadFile{" +
                "fileName='" + fileName + '\'' +
                ", fileId='" + fileId + '\'' +
                ", fileIcon='" + fileIcon + '\'' +
                ", fileBitmapIcon=" + fileBitmapIcon +
                '}';
    }
}
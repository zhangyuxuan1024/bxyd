package net.iclassmate.bxyd.bean.netdisk;

import java.io.Serializable;

/**
 * Created by xydbj on 2016.6.27.
 */
public class FileLabel implements Serializable{
    private String createTime;
    private Extend extend;
    private String fileId;
    private String ownerId;
    private String spaceId;
    private String tagIcon;
    private String tagId;
    private String tagName;
    private String tagType;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Extend getExtend() {
        return extend;
    }

    public void setExtend(Extend extend) {
        this.extend = extend;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }

    public String getTagIcon() {
        return tagIcon;
    }

    public void setTagIcon(String tagIcon) {
        this.tagIcon = tagIcon;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTagType() {
        return tagType;
    }

    public void setTagType(String tagType) {
        this.tagType = tagType;
    }

    @Override
    public String toString() {
        return "FileLabel{" +
                "createTime='" + createTime + '\'' +
                ", extend=" + extend +
                ", fileId='" + fileId + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", spaceId='" + spaceId + '\'' +
                ", tagIcon='" + tagIcon + '\'' +
                ", tagId='" + tagId + '\'' +
                ", tagName='" + tagName + '\'' +
                ", tagType='" + tagType + '\'' +
                '}';
    }
}

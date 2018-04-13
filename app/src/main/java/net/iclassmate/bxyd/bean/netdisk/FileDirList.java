package net.iclassmate.bxyd.bean.netdisk;


import java.io.Serializable;

/**
 * Created by xydbj on 2016.6.27.
 */
public class FileDirList implements Serializable{
    private String auth;
    private String createTime;
    private FileLabel fileLabel;
    private String fileType;
    private String fullPath;
    private String id;
    private String label;
    private String parentId;
    private String saveUuid;
    private String scale;
    private String seq;
    private String shortName;
    private String ossPath;
    private String size;
    private String spaceUuid;
    private int type;
    private String updateTime;
    private String userUuid;
    private boolean isCheck;

    public String getOssPath() {
        return ossPath;
    }

    public void setOssPath(String ossPath) {
        this.ossPath = ossPath;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setIsCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public FileLabel getFileLabel() {
        return fileLabel;
    }

    public void setFileLabel(FileLabel fileLabel) {
        this.fileLabel = fileLabel;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getSaveUuid() {
        return saveUuid;
    }

    public void setSaveUuid(String saveUuid) {
        this.saveUuid = saveUuid;
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSpaceUuid() {
        return spaceUuid;
    }

    public void setSpaceUuid(String spaceUuid) {
        this.spaceUuid = spaceUuid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    @Override
    public String toString() {
        return "FileDirList{" +
                "auth='" + auth + '\'' +
                ", createTime='" + createTime + '\'' +
                ", fileLabel=" + fileLabel +
                ", fileType='" + fileType + '\'' +
                ", fullPath='" + fullPath + '\'' +
                ", id='" + id + '\'' +
                ", label='" + label + '\'' +
                ", parentId='" + parentId + '\'' +
                ", saveUuid='" + saveUuid + '\'' +
                ", scale='" + scale + '\'' +
                ", seq='" + seq + '\'' +
                ", shortName='" + shortName + '\'' +
                ", ossPath='" + ossPath + '\'' +
                ", size='" + size + '\'' +
                ", spaceUuid='" + spaceUuid + '\'' +
                ", type=" + type +
                ", updateTime='" + updateTime + '\'' +
                ", userUuid='" + userUuid + '\'' +
                ", isCheck=" + isCheck +
                '}';
    }
}
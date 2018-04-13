package net.iclassmate.bxyd.bean.attention;

import java.io.Serializable;

/**
 * Created by xydbj on 2016.7.2.
 */
public class Attention implements Serializable {
    private String userName;//显示的数据
    private String userIcon;//显示的数据的头像
    private String userPinyin;//显示数据拼音的首字母
    private String uuid;
    private String subSpaceId;
    private int index;
    private int statetype;
    private String ownerId;
    private boolean isDownload;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isDownload() {
        return isDownload;
    }

    public void setIsDownload(boolean isDownload) {
        this.isDownload = isDownload;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public int getStatetype() {
        return statetype;
    }

    public void setStatetype(int statetype) {
        this.statetype = statetype;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSubSpaceId() {
        return subSpaceId;
    }

    public void setSubSpaceId(String subSpaceId) {
        this.subSpaceId = subSpaceId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public String getUserPinyin() {
        return userPinyin;
    }

    public void setUserPinyin(String userPinyin) {
        this.userPinyin = userPinyin;
    }

    @Override
    public String toString() {
        return "Attention{" +
                "userName='" + userName + '\'' +
                ", userIcon='" + userIcon + '\'' +
                ", userPinyin='" + userPinyin + '\'' +
                ", uuid='" + uuid + '\'' +
                ", subSpaceId='" + subSpaceId + '\'' +
                ", index=" + index +
                ", statetype=" + statetype +
                ", ownerId='" + ownerId + '\'' +
                ", isDownload=" + isDownload +
                ", type='" + type + '\'' +
                '}';
    }
}
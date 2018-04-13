package net.iclassmate.bxyd.bean.contacts;

/**
 * Created by xyd on 2016/7/5.
 */
public class GroupInfo {
 /*   "sessionId": "2f1c386a65934e979c392939aaeb91ae",
            "sessionName": "移动端副",
            "author": "5c3057471a7544e788f9eac484774fa4",
            "sessionType": 1,
            "updateTime": null,
            "spaceId": null,
            "sessionIcon": null*/
    private String sessionId;
    private String sessionName;
    private String author;
    private int sessionType;
    private String updateTime;
    private String spaceId;
    private String sessionIcon;
    private String code;    //机构号

    public GroupInfo(String sessionId, String sessionName, String author, int sessionType, String updateTime, String spaceId, String sessionIcon) {
        this.sessionId = sessionId;
        this.sessionName = sessionName;
        this.author = author;
        this.sessionType = sessionType;
        this.updateTime = updateTime;
        this.spaceId = spaceId;
        this.sessionIcon = sessionIcon;
    }

    public GroupInfo(String sessionId, String sessionName, String author, int sessionType, String sessionIcon) {
        this.sessionId = sessionId;
        this.sessionName = sessionName;
        this.author = author;
        this.sessionType = sessionType;
        this.sessionIcon = sessionIcon;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getSessionType() {
        return sessionType;
    }

    public void setSessionType(int sessionType) {
        this.sessionType = sessionType;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }

    public String getSessionIcon() {
        return sessionIcon;
    }

    public void setSessionIcon(String sessionIcon) {
        this.sessionIcon = sessionIcon;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "GroupInfo{" +
                "sessionId='" + sessionId + '\'' +
                ", sessionName='" + sessionName + '\'' +
                ", author='" + author + '\'' +
                ", sessionType=" + sessionType +
                ", updateTime='" + updateTime + '\'' +
                ", spaceId='" + spaceId + '\'' +
                ", sessionIcon='" + sessionIcon + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
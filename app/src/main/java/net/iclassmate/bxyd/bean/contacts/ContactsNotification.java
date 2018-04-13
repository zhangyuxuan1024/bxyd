package net.iclassmate.bxyd.bean.contacts;

import java.io.Serializable;

/**
 * Created by xyd on 2016/6/23.
 */
public class ContactsNotification implements Serializable {
  /*  mContactNotificationMessage.getSourceUserId();
    mContactNotificationMessage.getTargetUserId();
    mContactNotificationMessage.getMessage();
    mContactNotificationMessage.getExtra();*/
    private String sourceUserId;
    private String targetUserId;
    private String message;
    private String extra;//对方的name

    private int type;//是否添加好友，1是，2否

    public ContactsNotification() {
    }

    public ContactsNotification(String sourceUserId, String targetUserId, String message, String extra) {
        this.sourceUserId = sourceUserId;
        this.targetUserId = targetUserId;
        this.message = message;
        this.extra = extra;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSourceUserId() {
        return sourceUserId;
    }

    public void setSourceUserId(String sourceUserId) {
        this.sourceUserId = sourceUserId;
    }

    public String getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(String targetUserId) {
        this.targetUserId = targetUserId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}

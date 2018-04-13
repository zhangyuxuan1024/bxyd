package net.iclassmate.bxyd.bean.message;

import java.io.Serializable;

/**
 * Created by xydbj on 2016/8/16.
 */
public class MessageState implements Serializable {
    //type 1 为本  type 2 图片
    private int type;
    private int msgId;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }
}

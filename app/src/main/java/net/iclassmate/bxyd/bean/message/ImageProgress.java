package net.iclassmate.bxyd.bean.message;

import java.io.Serializable;

/**
 * Created by xydbj on 2016/8/16.
 */
public class ImageProgress implements Serializable {
    private int msgId;
    private int progress;

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}

package net.iclassmate.bxyd.bean.message;

import io.rong.imlib.model.Message;

/**
 * Created by xyd on 2016/6/14.
 */
public class MessageInfo {
    private String targetId;
    private Message message;
    public MessageInfo(){

    }

    public MessageInfo(String targetId ,Message message){
        this.targetId=targetId;
        this.message=message;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}

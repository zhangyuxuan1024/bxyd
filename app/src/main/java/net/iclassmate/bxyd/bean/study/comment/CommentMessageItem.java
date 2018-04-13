package net.iclassmate.bxyd.bean.study.comment;

import net.iclassmate.bxyd.bean.study.CreateBy;
import net.iclassmate.bxyd.bean.study.Parserable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by xydbj on 2016/7/7.
 */
public class CommentMessageItem implements Serializable, Parserable {
    /**
     * content : Hjgj
     * createdOn : 1467873312000
     * id : a594056b1a1e48f0b828344df23468a2
     */

    private String content;
    private String createdOn;
    private String id;
    private CreateBy createBy;
    private CreateBy replyTo;

    @Override
    public void parserJson(JSONObject json) {
        if (json != null) {
            try {
                content = json.getString("content");
                createdOn = json.getString("createdOn");
                id = json.getString("id");
                createBy = new CreateBy();
                JSONObject jsonObject = json.optJSONObject("createdBy");
                if (jsonObject != null) {
                    createBy.parserJson(jsonObject);
                    setCreateBy(createBy);
                }
                JSONObject jsonObject1 = json.optJSONObject("replyTo");
                replyTo = new CreateBy();
                if (jsonObject1 != null) {
                    replyTo.parserJson(jsonObject1);
                    setReplyTo(replyTo);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public CreateBy getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(CreateBy replyTo) {
        this.replyTo = replyTo;
    }

    public CreateBy getCreateBy() {
        return createBy;
    }

    public void setCreateBy(CreateBy createBy) {
        this.createBy = createBy;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getId() {
        return id;
    }
}

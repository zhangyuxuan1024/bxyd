package net.iclassmate.bxyd.bean.study.comment;

import net.iclassmate.bxyd.bean.study.CreateBy;
import net.iclassmate.bxyd.bean.study.Parserable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by xydbj on 2016/7/5.
 */
public class CommentList implements Serializable, Parserable {
    private String content;
    private String createdOn;
    private String id;
    private CreateBy createdBy;

    @Override
    public void parserJson(JSONObject json) {
        if (json != null) {
            try {
                content = json.getString("content");
                createdOn = json.getString("createdOn");
                id = json.getString("id");
                createdBy = new CreateBy();
                JSONObject jsonObject = json.optJSONObject("createdBy");
                if (jsonObject != null) {
                    createdBy.parserJson(jsonObject);
                    setCreatedBy(createdBy);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public void setId(String id) {
        this.id = id;
    }

    public CreateBy getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(CreateBy createdBy) {
        this.createdBy = createdBy;
    }
}

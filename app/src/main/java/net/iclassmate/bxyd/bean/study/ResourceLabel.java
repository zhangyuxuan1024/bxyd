package net.iclassmate.bxyd.bean.study;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by xydbj on 2016/6/21.
 */
public class ResourceLabel implements Serializable, Parserable {
    /**
     * createTime : 2016-06-21 14:35:41.0
     * extend : {}
     * fileId : 14c217550159421487f64ebf658be329
     * ownerId : 5c3057471a7544e788f9eac484774fa4
     * spaceId : c881f6ea9b564d32877333947b24ec9b
     * tagIcon : tagicon tag-photo
     * tagName : 相片
     * tagType : 3
     */

    private String createTime;
    private String fileId;
    private String ownerId;
    private String spaceId;
    private String tagIcon;
    private String tagName;
    private String tagType;

    @Override
    public void parserJson(JSONObject json) {
        if (json != null) {
            try {
                createTime = json.getString("createTime");
                fileId = json.getString("fileId");
                ownerId = json.getString("ownerId");
                spaceId = json.getString("spaceId");
                tagIcon = json.getString("tagIcon");
                tagName = json.getString("tagName");
                tagType = json.getString("tagType");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }

    public void setTagIcon(String tagIcon) {
        this.tagIcon = tagIcon;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public void setTagType(String tagType) {
        this.tagType = tagType;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getFileId() {
        return fileId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getSpaceId() {
        return spaceId;
    }

    public String getTagIcon() {
        return tagIcon;
    }

    public String getTagName() {
        return tagName;
    }

    public String getTagType() {
        return tagType;
    }
}

package net.iclassmate.bxyd.bean.study;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by xydbj on 2016/6/24.
 */
public class FileLabel implements Serializable, Parserable {
    /**
     * createTime : 2016-06-21 14:35:45.0
     * extend : {}
     * fileId : f5bb09cf7a6446df90de0293dc21d25b
     * ownerId : 5c3057471a7544e788f9eac484774fa4
     * spaceId : c881f6ea9b564d32877333947b24ec9b
     * tagIcon : tagicon tag-photo
     * tagId : 0f959491e65b4e37b947be6b10da584a
     * tagName : 相片
     * tagType : 3
     */
    private String authorName;
    private String createTime;
    private Extend extend;
    private String fileId;
    private String ownerId;
    private String spaceId;
    private String tagIcon;
    private String tagId;
    private String tagName;
    private String tagType;

    @Override
    public void parserJson(JSONObject json) {
        if (json != null) {
            try {
                authorName = json.getString("authorName");
                createTime = json.getString("createTime");
                fileId = json.getString("fileId");
                ownerId = json.getString("ownerId");
                spaceId = json.getString("spaceId");
                tagIcon = json.getString("tagIcon");
                tagId = json.getString("tagId");
                tagName = json.getString("tagName");
                tagType = json.getString("tagType");
                extend = new Extend();
                JSONObject jsonObject = json.optJSONObject("extend");
                if (jsonObject != null) {
                    extend.parserJson(jsonObject);
                }
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

    public void setTagId(String tagId) {
        this.tagId = tagId;
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

    public String getTagId() {
        return tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public String getTagType() {
        return tagType;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Extend getExtend() {
        return extend;
    }

    public void setExtend(Extend extend) {
        this.extend = extend;
    }
}

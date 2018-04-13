package net.iclassmate.bxyd.bean.message;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by xydbj on 2016/10/20.
 */
public class Auth implements Serializable {
    /**
     * content : 多弗朗明哥预加入空间，请审批
     * createTime : 1476967202000
     * from : service
     * objectId : 04c3a31c16fc483fa8865ff941b7861e
     * spaceId : 2203bda1b8914704bc843223b92f10a2
     * spaceName : 第三空间
     * subType : joinSpace
     * type : auth
     * userId : 1222c2b50b9d48d09be61ef58ccc1ef6
     * userName : 多弗朗明哥
     */

    private String content;
    private long createTime;
    private String from;
    private String objectId;
    private String spaceId;
    private String spaceName;
    private String subType;
    private String type;
    private String userId;
    private String userName;

    public void parserJson(JSONObject object) {
        if (object != null) {
            content = object.optString("content");
            createTime = object.optLong("createTime");
            from = object.optString("from");
            objectId = object.optString("objectId");
            spaceId = object.optString("spaceId");
            spaceName = object.optString("spaceName");
            subType = object.optString("subType");
            type = object.optString("type");
            userId = object.optString("userId");
            userName = object.optString("userName");
        }
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }

    public void setSpaceName(String spaceName) {
        this.spaceName = spaceName;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public long getCreateTime() {
        return createTime;
    }

    public String getFrom() {
        return from;
    }

    public String getObjectId() {
        return objectId;
    }

    public String getSpaceId() {
        return spaceId;
    }

    public String getSpaceName() {
        return spaceName;
    }

    public String getSubType() {
        return subType;
    }

    public String getType() {
        return type;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }
}
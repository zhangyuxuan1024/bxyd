package net.iclassmate.bxyd.bean.study.fri;

import net.iclassmate.bxyd.bean.study.Parserable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by xydbj on 2016/7/22.
 */
public class Fri implements Serializable, Parserable {
    /**
     * friendId : 282bd4f2068540f88a211f262b6193bb
     * groupId : 0
     * remark :
     * userId : 5c3057471a7544e788f9eac484774fa4
     * userName : 的话说分开
     */

    private String friendId;
    private String groupId;
    private String remark;
    private String userId;
    private String userName;
    private boolean check;
    private char key;

    @Override
    public void parserJson(JSONObject json) {
        if (json != null) {
            try {
                friendId = json.getString("friendId");
                groupId = json.getString("groupId");
                remark = json.getString("remark");
                userId = json.getString("userId");
                userName = json.getString("userName");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFriendId() {
        return friendId;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getRemark() {
        return remark;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public char getKey() {
        return key;
    }

    public void setKey(char key) {
        this.key = key;
    }
}

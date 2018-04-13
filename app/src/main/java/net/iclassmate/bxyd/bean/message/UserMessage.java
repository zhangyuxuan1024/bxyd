package net.iclassmate.bxyd.bean.message;

import net.iclassmate.bxyd.bean.study.Parserable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by xydbj on 2016/8/2.
 */
public class UserMessage implements Serializable, Parserable {
    private String id;
    private String spaceId;
    private UserInfo userInfo;


    @Override
    public void parserJson(JSONObject json) {
        if (json != null) {
            try {
                id = json.getString("id");
                spaceId = json.getString("spaceId");
                userInfo = new UserInfo();
                JSONObject jsonObject = json.getJSONObject("userInfo");
                if (jsonObject != null) {
                    userInfo.parserJson(jsonObject);
                    setUserInfo(userInfo);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public String getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public String toString() {
        return "UserMessage{" +
                "id='" + id + '\'' +
                ", spaceId='" + spaceId + '\'' +
                ", userInfo=" + userInfo +
                '}';
    }
}
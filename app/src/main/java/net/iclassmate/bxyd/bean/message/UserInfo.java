package net.iclassmate.bxyd.bean.message;

import net.iclassmate.bxyd.bean.study.Parserable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by xydbj on 2016/8/2.
 */
public class UserInfo implements Serializable, Parserable {
    /**
     * capacity : 100
     * fsRoot : 53f7a60dccae4a6092b2cdd1be2ec08e
     * name : 兰付
     * phone : 18823456789
     * ryToken : GrGhHnFt3TQirL2ffQQbfXBCNtWRky9pVYYQnDySzuTdIP+opQuhfNGTn0QXsPDSol5EeUuCZkVa+N8pX36UH3NojZcETw1A4KsY7aENTVcLZZGUKuns/58TxiUHobgRUIRCaBajs4U=
     * searchMe : 1
     * userCode : 697139
     * userType : 1
     */
    private String taggetId;
    private String capacity;
    private String fsRoot;
    private String name;
    private String phone;
    private String ryToken;
    private String searchMe;
    private String userCode;
    private String userType;
    private String userIcon;

    @Override
    public void parserJson(JSONObject json) {
        if (json != null) {
            try {
                name = json.getString("name");
                userCode = json.getString("userCode");
                capacity = json.getString("capacity");
                fsRoot = json.optString("fsRoot");
                phone = json.getString("phone");
                ryToken = json.getString("ryToken");
                searchMe = json.getString("searchMe");
                userType = json.getString("userType");
                userIcon = json.optString("icon");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String getTaggetId() {
        return taggetId;
    }

    public void setTaggetId(String taggetId) {
        this.taggetId = taggetId;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getFsRoot() {
        return fsRoot;
    }

    public void setFsRoot(String fsRoot) {
        this.fsRoot = fsRoot;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRyToken() {
        return ryToken;
    }

    public void setRyToken(String ryToken) {
        this.ryToken = ryToken;
    }

    public String getSearchMe() {
        return searchMe;
    }

    public void setSearchMe(String searchMe) {
        this.searchMe = searchMe;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "taggetId='" + taggetId + '\'' +
                ", capacity='" + capacity + '\'' +
                ", fsRoot='" + fsRoot + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", ryToken='" + ryToken + '\'' +
                ", searchMe='" + searchMe + '\'' +
                ", userCode='" + userCode + '\'' +
                ", userType='" + userType + '\'' +
                ", userIcon='" + userIcon + '\'' +
                '}';
    }
}

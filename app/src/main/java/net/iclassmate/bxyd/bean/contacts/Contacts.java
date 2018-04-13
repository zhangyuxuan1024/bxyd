package net.iclassmate.bxyd.bean.contacts;

import net.iclassmate.bxyd.bean.message.UserInfo;
import net.iclassmate.bxyd.bean.study.Parserable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by xyd on 2016/6/21.
 */
public class Contacts implements Serializable, Parserable{
    public String name;     //在手通讯录中的名称
    public String number;   //手机号
    public boolean isXYD;   //是否是xyd空间用户
    public String id;       //心意答userID
    public String spaceId;  //心意答空间id
    public boolean isFriender;  //是否是当前登陆用户的心意答朋友
    public char sortKey;    //首字母
    public boolean isHead;  //是否该字母第一位
    public net.iclassmate.bxyd.bean.message.UserInfo userInfo;

    public Contacts(){
        super();
    }

    public Contacts(String name,String number){
        this.name=name;
        this.number=number;
    }



    @Override
    public void parserJson(JSONObject json) {
        if (json != null) {
            try {
                id = json.getString("id");
                spaceId = json.getString("spaceId");
                userInfo = new net.iclassmate.bxyd.bean.message.UserInfo();
                JSONObject jsonObject = json.optJSONObject("userInfo");
                if (jsonObject != null) {
                    userInfo.parserJson(jsonObject);
                    setUserInfo(userInfo);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public String toString() {
        return "Contacts{" +
                "name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", isXYD=" + isXYD +
                ", id='" + id + '\'' +
                ", spaceId='" + spaceId + '\'' +
                ", isFriender=" + isFriender +
                ", userInfo=" + userInfo +
                '}';
    }
}
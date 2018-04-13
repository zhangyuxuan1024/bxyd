package net.iclassmate.bxyd.bean;

import net.iclassmate.bxyd.bean.owner.UserInfo;

/**
 * Created by xydbj on 2016.8.4.
 */
public class Owner {
    private UserInfo userInfo;

    @Override
    public String toString() {
        return "Owner{" +
                "userInfo=" + userInfo +
                '}';
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}

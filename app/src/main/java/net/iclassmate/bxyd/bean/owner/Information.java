package net.iclassmate.bxyd.bean.owner;

/**
 * Created by xydbj on 2016.7.19.
 */
public class Information {
    private String id;
    private UserInfo userInfo;


    @Override
    public String toString() {
        return "Information{" +
                "id='" + id + '\'' +
                ", userInfo=" + userInfo +
                '}';
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

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}

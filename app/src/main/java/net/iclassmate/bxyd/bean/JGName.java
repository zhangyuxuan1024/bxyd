package net.iclassmate.bxyd.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xydbj on 2016.8.18.
 */
public class JGName implements Serializable {
    private String name;
    private List<String> userId;
    private List<String> adminName;

    @Override
    public String toString() {
        return "JGName{" +
                "name='" + name + '\'' +
                ", userId=" + userId +
                ", adminName=" + adminName +
                '}';
    }

    public List<String> getAdminName() {
        return adminName;
    }

    public void setAdminName(List<String> adminName) {
        this.adminName = adminName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getUserId() {
        return userId;
    }

    public void setUserId(List<String> userId) {
        this.userId = userId;
    }
}

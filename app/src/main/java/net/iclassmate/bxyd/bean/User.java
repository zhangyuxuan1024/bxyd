package net.iclassmate.bxyd.bean;

/**
 * Created by xyd on 2016/6/7.
 */
public class User {
    private String name;
    private String phone;
    private String userCode;

    public User() {
        super();
    }

    public User(String name,String phone, String userCode) {
        super();
        this.name = name;
        this.phone = phone;
        this.userCode = userCode;
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

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }
}
package net.iclassmate.bxyd.bean.contacts;

/**
 * Created by xyd on 2016/6/22.
 * 用户信息（添加好友）
 */
public class UserInfo {
    private String area;
    private String gender;
    private String city;
    private String edulevel;
    private String ryToken;
    private String icon;
    private String remark;
    private String userCode;
    private String province;
    private String phone;
    private String searchMe;
    private String name;
    private String userType;
    private String dateBirth;
    private String email;

    public UserInfo(){

    }

/*    public UserInfo(String icon, String phone, String name) {
        this.icon = icon;
        this.phone = phone;
        this.name = name;
    }*/

    public UserInfo(String icon,String userCode,String name){
        this.icon=icon;
        this.userCode=userCode;
        this.name=name;
    }

    public UserInfo(String area, String gender, String city, String edulevel, String ryToken, String icon, String remark, String userCode, String province, String phone, String searchMe, String name, String userType, String dateBirth, String email) {
        this.area = area;
        this.gender = gender;
        this.city = city;
        this.edulevel = edulevel;
        this.ryToken = ryToken;
        this.icon = icon;
        this.remark = remark;
        this.userCode = userCode;
        this.province = province;
        this.phone = phone;
        this.searchMe = searchMe;
        this.name = name;
        this.userType = userType;
        this.dateBirth = dateBirth;
        this.email = email;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEdulevel() {
        return edulevel;
    }

    public void setEdulevel(String edulevel) {
        this.edulevel = edulevel;
    }

    public String getRyToken() {
        return ryToken;
    }

    public void setRyToken(String ryToken) {
        this.ryToken = ryToken;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSearchMe() {
        return searchMe;
    }

    public void setSearchMe(String searchMe) {
        this.searchMe = searchMe;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getDateBirth() {
        return dateBirth;
    }

    public void setDateBirth(String dateBirth) {
        this.dateBirth = dateBirth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

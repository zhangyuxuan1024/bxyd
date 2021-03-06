package net.iclassmate.bxyd.bean.owner;

/**
 * Created by xydbj on 2016.7.19.
 */
public class UserInfo {
    private String Introduction;
    private String area;
    private String city;
    private String province;
    private String capacity;
    private String dateBirth;
    private String eduLevel;
    private String email;
    private String fsRoot;
    private String gender;
    private String icon;
    private String name;
    private String phone;
    private String remark;
    private String ryToken;
    private int searchMe;
    private String userCode;
    private int userType;
    private String tradeType;

    @Override
    public String toString() {
        return "UserInfo{" +
                "Introduction='" + Introduction + '\'' +
                ", area='" + area + '\'' +
                ", city='" + city + '\'' +
                ", province='" + province + '\'' +
                ", capacity='" + capacity + '\'' +
                ", dateBirth='" + dateBirth + '\'' +
                ", eduLevel='" + eduLevel + '\'' +
                ", email='" + email + '\'' +
                ", fsRoot='" + fsRoot + '\'' +
                ", gender='" + gender + '\'' +
                ", icon='" + icon + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", remark='" + remark + '\'' +
                ", ryToken='" + ryToken + '\'' +
                ", searchMe=" + searchMe +
                ", userCode='" + userCode + '\'' +
                ", userType=" + userType +
                ", tradeType='" + tradeType + '\'' +
                '}';
    }

    public String getIntroduction() {
        return Introduction;
    }

    public void setIntroduction(String introduction) {
        Introduction = introduction;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getDateBirth() {
        return dateBirth;
    }

    public void setDateBirth(String dateBirth) {
        this.dateBirth = dateBirth;
    }

    public String getEduLevel() {
        return eduLevel;
    }

    public void setEduLevel(String eduLevel) {
        this.eduLevel = eduLevel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFsRoot() {
        return fsRoot;
    }

    public void setFsRoot(String fsRoot) {
        this.fsRoot = fsRoot;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRyToken() {
        return ryToken;
    }

    public void setRyToken(String ryToken) {
        this.ryToken = ryToken;
    }

    public int getSearchMe() {
        return searchMe;
    }

    public void setSearchMe(int searchMe) {
        this.searchMe = searchMe;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }
}

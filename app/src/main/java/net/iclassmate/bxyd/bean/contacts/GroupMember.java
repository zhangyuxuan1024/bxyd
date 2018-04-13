package net.iclassmate.bxyd.bean.contacts;

import android.text.TextUtils;

/**
 * 群聊、群组、机构成员
 * Created by xyd on 2016/8/5.
 */
public class GroupMember implements java.io.Serializable{
    private String userId;
    private String userName;
    private String remark;
    private String userCode;    //用户号
    private String icon;
    private String spaceId;
    private String groupId;     //该成员所在空间或机构的某一分类

    public GroupMember(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    public GroupMember(FriendInfo friendInfo){
        this.userId = friendInfo.getFriendId();
        this.userName = friendInfo.getUserName();
        this.remark = friendInfo.getRemark();
        this.userCode = friendInfo.getFriendCode();
        if(friendInfo.getIcon() != null && !TextUtils.isEmpty(friendInfo.getIcon()))
        {
            this.icon = friendInfo.getIcon();
        }else
        {

        }
    }

    //用于创建群组和机构成员
    public GroupMember(String userName, String userId, String spaceId, String groupId) {
        this.userName = userName;
        this.userId = userId;
        this.spaceId = spaceId;
        this.groupId = groupId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "GroupMember{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", remark='" + remark + '\'' +
                ", userCode='" + userCode + '\'' +
                ", icon='" + icon + '\'' +
                '}';
    }
}
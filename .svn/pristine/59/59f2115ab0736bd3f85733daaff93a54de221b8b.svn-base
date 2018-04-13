package net.iclassmate.bxyd.bean.contacts;

import android.text.TextUtils;

/**
 * Created by xyd on 2016/6/24.
 */
public class FriendInfo {
   /* "userId": "e2119fd4c71e4755a8f16fd778b04c64",
            "friendId": "fb27ade5bc4b4d8ca75f7dd58ce2c990",
            "groupId": null,
            "remark": "lxw",
            "userName": null*/
   public boolean check;
    private String userId;
    private String friendId;
    private String groupId;
    private String remark;
    private String userName;
    private String icon;
    private String friendCode;  //朋友用户号
    private String groupSpaceId;    //相当于空间成员中的groupId

    public char sortKey;
    public boolean isHead;

    private boolean isMember = false;   //是否为成员

    public FriendInfo(){

    }

    public FriendInfo(GroupMember groupMember)
    {
        this.friendId = groupMember.getUserId();
        this.userName = groupMember.getUserName();
        this.friendCode = groupMember.getUserCode();
        this.remark = groupMember.getRemark();
        if(null != groupMember.getGroupId() && !TextUtils.isEmpty(groupMember.getGroupId())){
            this.groupSpaceId = groupMember.getGroupId();
        }
        if(null != groupMember.getIcon() && !TextUtils.isEmpty(groupMember.getIcon())){
            this.icon = groupMember.getIcon();
        }
    }

    public FriendInfo(String userId, String friendId, String groupId, String remark, String userName) {
        this.userId = userId;
        this.friendId = friendId;
        this.groupId = groupId;
        this.remark = remark;
        this.userName = userName;
//        this.icon=icon;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFriendCode() {
        return friendCode;
    }

    public void setFriendCode(String friendCode) {
        this.friendCode = friendCode;
    }

    public boolean isMember() {
        return isMember;
    }

    public void setMember(boolean member) {
        isMember = member;
    }

    public String getGroupSpaceId() {
        return groupSpaceId;
    }

    public void setGroupSpaceId(String groupSpaceId) {
        this.groupSpaceId = groupSpaceId;
    }

    @Override
    public String toString() {
        return "FriendInfo{" +
                "check=" + check +
                ", userId='" + userId + '\'' +
                ", friendId='" + friendId + '\'' +
                ", groupId='" + groupId + '\'' +
                ", remark='" + remark + '\'' +
                ", userName='" + userName + '\'' +
                ", icon='" + icon + '\'' +
                ", sortKey=" + sortKey +
                ", isHead=" + isHead +
                ", isMember=" + isMember +
                '}';
    }
}

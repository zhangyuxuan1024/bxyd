package net.iclassmate.bxyd.bean.attention;

/**
 * Created by xydbj on 2016.7.14.
 */
public class Attention_Responses {
    private String createTime;
    private String mainSpaceId;
    private String noteName;
    private String relationGroupId;
    private String relationGroupName;
    private SubSpace subSpace;
    private String icon;
    private String subSpaceId;
    private String uuid;

    @Override
    public String toString() {
        return "Attention_Responses{" +
                "createTime='" + createTime + '\'' +
                ", mainSpaceId='" + mainSpaceId + '\'' +
                ", noteName='" + noteName + '\'' +
                ", relationGroupId='" + relationGroupId + '\'' +
                ", relationGroupName='" + relationGroupName + '\'' +
                ", subSpace=" + subSpace +
                ", icon='" + icon + '\'' +
                ", subSpaceId='" + subSpaceId + '\'' +
                ", uuid='" + uuid + '\'' +
                '}';
    }

    public SubSpace getSubSpace() {
        return subSpace;
    }

    public void setSubSpace(SubSpace subSpace) {
        this.subSpace = subSpace;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getMainSpaceId() {
        return mainSpaceId;
    }

    public void setMainSpaceId(String mainSpaceId) {
        this.mainSpaceId = mainSpaceId;
    }

    public String getNoteName() {
        return noteName;
    }

    public void setNoteName(String noteName) {
        this.noteName = noteName;
    }

    public String getRelationGroupId() {
        return relationGroupId;
    }

    public void setRelationGroupId(String relationGroupId) {
        this.relationGroupId = relationGroupId;
    }

    public String getRelationGroupName() {
        return relationGroupName;
    }

    public void setRelationGroupName(String relationGroupName) {
        this.relationGroupName = relationGroupName;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getSubSpaceId() {
        return subSpaceId;
    }

    public void setSubSpaceId(String subSpaceId) {
        this.subSpaceId = subSpaceId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}

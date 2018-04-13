package net.iclassmate.bxyd.bean.contacts;

/**
 * 群组空间和机构空间
 * Created by xyd on 2016/10/22.
 */
public class GroupSpaces {
    private String id;
    private String name;
    private String ownerId; //所有者
    private String type;    //类型   群组：group   机构org

    public GroupSpaces(String id, String name, String ownerId, String type) {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "GroupSpaces{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
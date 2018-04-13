package net.iclassmate.bxyd.bean;

/**
 * Created by xydbj on 2016.7.8.
 */
public class UserInFo {
    private String uuid;
    private long usedSpace;
    private Owner owner;

    public long getUsedSpace() {
        return usedSpace;
    }

    public void setUsedSpace(long usedSpace) {
        this.usedSpace = usedSpace;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return "UserInFo{" +
                "uuid='" + uuid + '\'' +
                ", usedSpace=" + usedSpace +
                ", owner=" + owner +
                '}';
    }
}

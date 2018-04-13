package net.iclassmate.bxyd.bean.attention;

/**
 * Created by xydbj on 2016.7.14.
 */
public class SubSpace {
    private String icon;
    private String ownerId;
    private String type;

    @Override
    public String toString() {
        return "SubSpace{" +
                "icon='" + icon + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
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
}

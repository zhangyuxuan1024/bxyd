package net.iclassmate.bxyd.bean.netdisk;

/**
 * Created by xydbj on 2016.7.5.
 */
public class QunZuDisk {
    private String qz_name;
    private String qz_icon;

    public String getQz_name() {
        return qz_name;
    }

    public void setQz_name(String qz_name) {
        this.qz_name = qz_name;
    }

    public String getQz_icon() {
        return qz_icon;
    }

    public void setQz_icon(String qz_icon) {
        this.qz_icon = qz_icon;
    }

    @Override
    public String toString() {
        return "QunZuDisk{" +
                "qz_name='" + qz_name + '\'' +
                ", qz_icon='" + qz_icon + '\'' +
                '}';
    }
}

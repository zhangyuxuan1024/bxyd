package net.iclassmate.bxyd.bean.netdisk;

/**
 * Created by xydbj on 2016.7.5.
 */
public class JiGouDisk {
    private String jg_name;
    private String jg_icon;

    public String getJg_name() {
        return jg_name;
    }

    public void setJg_name(String jg_name) {
        this.jg_name = jg_name;
    }

    public String getJg_icon() {
        return jg_icon;
    }

    public void setJg_icon(String jg_icon) {
        this.jg_icon = jg_icon;
    }

    @Override
    public String toString() {
        return "JiGouDisk{" +
                "jg_name='" + jg_name + '\'' +
                ", jg_icon='" + jg_icon + '\'' +
                '}';
    }
}
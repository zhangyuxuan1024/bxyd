package net.iclassmate.bxyd.bean.index;

import java.io.Serializable;

/**
 * Created by xydbj on 2016/11/16.
 */
public class PersonInfo implements Serializable {
    private String name;
    private String phone;
    private boolean maleSelect;
    private String id;

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

    public boolean isMaleSelect() {
        return maleSelect;
    }

    public void setMaleSelect(boolean maleSelect) {
        this.maleSelect = maleSelect;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
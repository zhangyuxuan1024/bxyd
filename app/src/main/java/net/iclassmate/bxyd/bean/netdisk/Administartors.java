package net.iclassmate.bxyd.bean.netdisk;

import java.util.List;

/**
 * Created by xydbj on 2016.7.9.
 */
public class Administartors {
    private List<list> listList;

    @Override
    public String toString() {
        return "Administartors{" +
                "listList=" + listList +
                '}';
    }

    public List<list> getListList() {
        return listList;
    }

    public void setListList(List<list> listList) {
        this.listList = listList;
    }
}

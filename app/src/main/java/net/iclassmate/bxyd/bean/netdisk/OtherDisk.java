package net.iclassmate.bxyd.bean.netdisk;

import java.util.List;

/**
 * Created by xydbj on 2016.7.9.
 */
public class OtherDisk {
    private int pageSize;
    private List<Responses> responsesList;
    private int startPage;
    private int total;

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<Responses> getResponsesList() {
        return responsesList;
    }

    public void setResponsesList(List<Responses> responsesList) {
        this.responsesList = responsesList;
    }

    public int getStartPage() {
        return startPage;
    }

    public void setStartPage(int startPage) {
        this.startPage = startPage;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "OtherDisk{" +
                "pageSize=" + pageSize +
                ", responsesList=" + responsesList +
                ", startPage=" + startPage +
                ", total=" + total +
                '}';
    }
}

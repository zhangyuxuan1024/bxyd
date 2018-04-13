package net.iclassmate.bxyd.bean.attention;

import java.util.List;

/**
 * Created by xydbj on 2016.7.14.
 */
public class Attention_All {
    private int pageSize;
    private List<Attention_Responses> attention_responsesList;
    private int startPage;
    private int total;

    @Override
    public String toString() {
        return "Attention_All{" +
                "pageSize=" + pageSize +
                ", attention_responsesList=" + attention_responsesList +
                ", startPage=" + startPage +
                ", total=" + total +
                '}';
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<Attention_Responses> getAttention_responsesList() {
        return attention_responsesList;
    }

    public void setAttention_responsesList(List<Attention_Responses> attention_responsesList) {
        this.attention_responsesList = attention_responsesList;
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
}

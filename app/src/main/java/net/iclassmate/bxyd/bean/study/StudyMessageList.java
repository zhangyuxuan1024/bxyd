package net.iclassmate.bxyd.bean.study;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xydbj on 2016/6/21.
 */
public class StudyMessageList implements Serializable, Parserable {
    private int endRow;
    private List<StudyMessageItem> list;
    private int page;
    private int pageSize;
    private int pages;
    private int startRow;
    private int total;

    public void parserJson(JSONObject json) {
        if (json != null) {
            try {
                endRow = json.getInt("endRow");
                page = json.getInt("page");
                pageSize = json.getInt("pageSize");
                pages = json.getInt("pages");
                startRow = json.getInt("startRow");
                total = json.getInt("total");
                List<StudyMessageItem> list = new ArrayList<>();
                JSONArray array = json.getJSONArray("list");
                for (int i = 0; i < array.length(); i++) {
                    StudyMessageItem item = new StudyMessageItem();
                    item.parserJson(array.getJSONObject(i));
                    list.add(item);
                }
                setList(list);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public int getEndRow() {
        return endRow;
    }

    public void setEndRow(int endRow) {
        this.endRow = endRow;
    }

    public List<StudyMessageItem> getList() {
        return list;
    }

    public void setList(List<StudyMessageItem> list) {
        this.list = list;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
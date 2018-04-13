package net.iclassmate.bxyd.bean.study;

import net.iclassmate.bxyd.bean.study.comment.CommentMessageItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xydbj on 2016/6/15.
 */
public class CommentMessage implements Serializable, Parserable {
    /**
     * endRow : 30
     * page : 1
     * pageSize : 30
     * pages : 1
     * startRow : 0
     * total : 7
     */

    private int endRow;
    private int page;
    private int pageSize;
    private int pages;
    private int startRow;
    private int total;
    private List<CommentMessageItem> list;

    @Override
    public void parserJson(JSONObject json) {
        if (json != null) {
            try {
                endRow = json.getInt("endRow");
                page = json.getInt("page");
                pageSize = json.getInt("pageSize");
                pages = json.getInt("pages");
                startRow = json.getInt("startRow");
                total = json.getInt("total");
                list = new ArrayList<>();
                JSONArray array = json.optJSONArray("list");
                if (array != null && array.length() > 0) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jsonObject = array.optJSONObject(i);
                        if (jsonObject != null) {
                            CommentMessageItem item = new CommentMessageItem();
                            item.parserJson(jsonObject);
                            list.add(item);
                        }
                    }
                    setList(list);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public List<CommentMessageItem> getList() {
        return list;
    }

    public void setList(List<CommentMessageItem> list) {
        this.list = list;
    }

    public void setEndRow(int endRow) {
        this.endRow = endRow;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getEndRow() {
        return endRow;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getPages() {
        return pages;
    }

    public int getStartRow() {
        return startRow;
    }

    public int getTotal() {
        return total;
    }
}

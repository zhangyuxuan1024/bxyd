package net.iclassmate.bxyd.bean.study.comment;

import net.iclassmate.bxyd.bean.study.Parserable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xydbj on 2016/7/5.
 */
public class Comment implements Serializable, Parserable {
    private int endRow;
    private int page;
    private int pageSize;
    private int pages;
    private int startRow;
    private int total;
    private List<CommentList> commentList;

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
                commentList = new ArrayList();
                JSONArray array = json.optJSONArray("list");
                if (array != null && array.length() > 0) {
                    for (int i = 0; i < array.length(); i++) {
                        CommentList c = new CommentList();
                        JSONObject jsonObject = array.optJSONObject(i);
                        if (jsonObject != null) {
                            c.parserJson(jsonObject);
                            commentList.add(c);
                        }
                    }
                    setCommentList(commentList);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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

    public List<CommentList> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<CommentList> commentList) {
        this.commentList = commentList;
    }
}
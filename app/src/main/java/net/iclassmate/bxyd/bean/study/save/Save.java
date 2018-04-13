package net.iclassmate.bxyd.bean.study.save;

import net.iclassmate.bxyd.bean.study.Parserable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xydbj on 2016/7/9.
 */
public class Save implements Serializable, Parserable {
    private int endRow;
    private int page;
    private int pageSize;
    private int pages;
    private int startRow;
    private int total;
    private List<BulletinItem> bulletinList;

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
                bulletinList = new ArrayList<>();
                JSONArray array = json.optJSONArray("list");
                if (array != null && array.length() > 0) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jsonObject = array.optJSONObject(i);
                        BulletinItem bulletin = new BulletinItem();
                        if (jsonObject != null) {
                            JSONObject jsonObject1 = jsonObject.optJSONObject("bulletin");
                            if (jsonObject1 != null) {
                                bulletin.parserJson(jsonObject);
                                bulletinList.add(bulletin);
                            }
                        }
                    }
                    setBulletinList(bulletinList);
                }
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

    public List<BulletinItem> getBulletinList() {
        return bulletinList;
    }

    public void setBulletinList(List<BulletinItem> bulletinList) {
        this.bulletinList = bulletinList;
    }
}

package net.iclassmate.bxyd.bean.study.group;

import net.iclassmate.bxyd.bean.study.Parserable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xydbj on 2016/7/23.
 */
public class Group implements Serializable, Parserable {
    private int resultCode;
    private String resultMarks;
    private List<GroupItem> list;

    @Override
    public void parserJson(JSONObject json) {
        if (json != null) {
            try {
                resultCode = json.getInt("resultCode");
                resultMarks = json.getString("resultMarks");
                List<GroupItem> list = new ArrayList<>();
                JSONArray array = json.getJSONArray("sessionList");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.optJSONObject(i);
                    if (object != null) {
                        GroupItem item = new GroupItem();
                        item.parserJson(object);
                        list.add(item);
                    }
                }
                setList(list);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMarks() {
        return resultMarks;
    }

    public void setResultMarks(String resultMarks) {
        this.resultMarks = resultMarks;
    }

    public List<GroupItem> getList() {
        return list;
    }

    public void setList(List<GroupItem> list) {
        this.list = list;
    }
}
package net.iclassmate.bxyd.bean.study.fri;

import net.iclassmate.bxyd.bean.study.Parserable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xydbj on 2016/7/22.
 */
public class FriList implements Serializable {
    private List<Fri> list;

    public void parserJson(JSONArray array) {
        if (array != null) {
            list = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.optJSONObject(i);
                if (object != null) {
                    Fri fri = new Fri();
                    fri.parserJson(object);
                    list.add(fri);
                }
            }
            setList(list);
        }
    }

    public List<Fri> getList() {
        return list;
    }

    public void setList(List<Fri> list) {
        this.list = list;
    }
}

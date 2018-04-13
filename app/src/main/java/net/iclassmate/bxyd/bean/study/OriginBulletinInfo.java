package net.iclassmate.bxyd.bean.study;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xydbj on 2016/6/23.
 */
public class OriginBulletinInfo implements Serializable, Parserable {
    private String content;
    private CreateBy createBy;
    private String createdOn;
    private String id;
    private List<Resources> list;

    @Override
    public void parserJson(JSONObject json) {
        if (json != null) {
            try {
                content = json.getString("content");
                createBy = new CreateBy();
                JSONObject object = json.optJSONObject("createdBy");
                if (object != null) {
                    createBy.parserJson(object);
                    setCreateBy(createBy);
                }
                createdOn = json.getString("createdOn");
                id = json.getString("id");
                list = new ArrayList<>();
                JSONArray array = json.optJSONArray("resources");
                if (array != null && array.length() > 0) {
                    for (int i = 0; i < array.length(); i++) {
                        Resources resources = new Resources();
                        JSONObject jsonObject = array.optJSONObject(i);
                        if (jsonObject != null) {
                            resources.parserJson(jsonObject);
                        } else {
                            resources.setId("-1");
                            resources.setImage("-1");
                            resources.setType("图片");
                        }
                        list.add(resources);
                    }
                    setList(list);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public CreateBy getCreateBy() {
        return createBy;
    }

    public void setCreateBy(CreateBy createBy) {
        this.createBy = createBy;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Resources> getList() {
        return list;
    }

    public void setList(List<Resources> list) {
        this.list = list;
    }
}
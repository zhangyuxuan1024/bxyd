package net.iclassmate.bxyd.bean.study;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by xydbj on 2016/6/21.
 */
public class CreateBy implements Serializable, Parserable {
    /**
     * id : 5c3057471a7544e788f9eac484774fa4
     * name : 移动端主
     * type : person
     */
    private String avatar;
    private String id;
    private String name;
    private String type;

    @Override
    public void parserJson(JSONObject json) {
        if (json != null) {
            try {
                avatar = json.optString("avatar");
                if (avatar != null) {
                    setAvatar(avatar);
                }
                id = json.getString("id");
                name = json.getString("name");
                type = json.getString("type");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
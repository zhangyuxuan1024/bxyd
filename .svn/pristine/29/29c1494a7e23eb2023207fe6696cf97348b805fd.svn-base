package net.iclassmate.bxyd.bean.study;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by xydbj on 2016/6/21.
 */
public class Resources implements Serializable, Parserable {
    private String id;
    private String image;
    private String name;
    private int size;
    private String type;
    private ResourceLabel label;

    @Override
    public void parserJson(JSONObject json) {
        if (json != null) {
            try {
                id = json.getString("id");
                image = json.getString("image");
                name = json.getString("name");
                type = json.getString("type");
                size = json.getInt("size");
                label = new ResourceLabel();
                JSONObject jsonObject = json.optJSONObject("label");
                if (jsonObject != null) {
                    label.parserJson(jsonObject);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String parserJson2(JSONObject json) {
        if (json != null) {
            try {
                image = json.getString("image");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public ResourceLabel getLabel() {
        return label;
    }

    public void setLabel(ResourceLabel label) {
        this.label = label;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "Resources{" +
                "id='" + id + '\'' +
                ", image='" + image + '\'' +
                ", name='" + name + '\'' +
                ", size=" + size +
                ", type='" + type + '\'' +
                ", label=" + label +
                '}';
    }
}

package net.iclassmate.bxyd.bean.index;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by xydbj on 2016/11/14.
 */
public class Recommend implements Serializable {
    /**
     * id : ts00001
     * name : test1
     * organizer : 心意答
     * address : 北京市海淀区
     * price : 50
     * start_time : null
     * end_time : null
     * imageUrl : null
     * jumpUrl : null
     */

    private String id;
    private String name;
    private String organizer;
    private String address;
    private double price;
    private long start_time;
    private long end_time;
    private String imageUrl;
    private String jumpUrl;
    private int type;

    public void parserJson(JSONObject json) {
        if (json != null) {
            try {
                id = json.getString("id");
                name = json.getString("name");
                organizer = json.getString("organizer");
                address = json.getString("address");
                price = json.optDouble("price");
                start_time = json.optLong("startTime");
                end_time = json.optLong("endTime");
                imageUrl = json.optString("imageUrl");
                jumpUrl = json.optString("jumpUrl");
                type = json.optInt("type");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public long getStart_time() {
        return start_time;
    }

    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }

    public long getEnd_time() {
        return end_time;
    }

    public void setEnd_time(long end_time) {
        this.end_time = end_time;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getJumpUrl() {
        return jumpUrl;
    }

    public void setJumpUrl(String jumpUrl) {
        this.jumpUrl = jumpUrl;
    }
}

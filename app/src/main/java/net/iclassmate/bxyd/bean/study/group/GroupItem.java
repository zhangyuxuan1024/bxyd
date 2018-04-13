package net.iclassmate.bxyd.bean.study.group;

import net.iclassmate.bxyd.bean.study.Parserable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by xydbj on 2016/7/23.
 */
public class GroupItem implements Serializable, Parserable {
    /**
     * author : 5c3057471a7544e788f9eac484774fa4
     * sessionIcon :
     * sessionId : d0cc32fb74c14d5bb5d6092022675c34
     * sessionName : 13888888888(2人)
     * sessionType : 2
     * updated : false
     */

    private String author;
    private String sessionIcon;
    private String sessionId;
    private String sessionName;
    private int sessionType;
    private boolean updated;
    private String spaceId;

    @Override
    public void parserJson(JSONObject json) {
        if (json != null) {
            try {
                author = json.getString("author");
                sessionIcon = json.getString("sessionIcon");
                sessionId = json.getString("sessionId");
                sessionName = json.getString("sessionName");
                sessionType = json.getInt("sessionType");
                updated = json.getBoolean("updated");
                spaceId = json.optString("spaceId");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setSessionIcon(String sessionIcon) {
        this.sessionIcon = sessionIcon;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public void setSessionType(int sessionType) {
        this.sessionType = sessionType;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public String getAuthor() {
        return author;
    }

    public String getSessionIcon() {
        return sessionIcon;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getSessionName() {
        return sessionName;
    }

    public int getSessionType() {
        return sessionType;
    }

    public boolean isUpdated() {
        return updated;
    }

    public String getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }
}

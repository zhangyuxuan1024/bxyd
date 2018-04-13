package net.iclassmate.bxyd.bean.study.save;

import net.iclassmate.bxyd.bean.study.Parserable;
import net.iclassmate.bxyd.bean.study.StudyMessageItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by xydbj on 2016/7/9.
 */
public class BulletinItem implements Serializable, Parserable {
    private StudyMessageItem studyMessageItem;
    private String createdOn;
    private String id;

    @Override
    public void parserJson(JSONObject json) {
        if (json != null) {
            try {
                studyMessageItem = new StudyMessageItem();
                JSONObject jsonObject = json.optJSONObject("bulletin");
                if (jsonObject != null) {
                    studyMessageItem.parserJson(jsonObject);
                    setStudyMessageItem(studyMessageItem);
                }
                createdOn = json.getString("createdOn");
                id = json.getString("id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public StudyMessageItem getStudyMessageItem() {
        return studyMessageItem;
    }

    public void setStudyMessageItem(StudyMessageItem studyMessageItem) {
        this.studyMessageItem = studyMessageItem;
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
}
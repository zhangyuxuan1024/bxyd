package net.iclassmate.bxyd.bean.study;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by xydbj on 2016/7/8.
 */
public class Extend implements Serializable, Parserable {
    private String grade;
    private String intro;
    private String subject;

    @Override
    public void parserJson(JSONObject json) {
        if (json != null) {
            try {
                grade = json.optString("grade");
                intro = json.getString("intro");
                subject = json.getString("subject");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}

package net.iclassmate.bxyd.bean.netdisk;

import java.io.Serializable;

/**
 * Created by xydbj on 2016.6.27.
 */
public class Extend implements Serializable{
    private String contact;
    private String intro;
    private String grade;
    private String subject;

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        return "Extend{" +
                "contact='" + contact + '\'' +
                ", intro='" + intro + '\'' +
                ", grade='" + grade + '\'' +
                ", subject='" + subject + '\'' +
                '}';
    }
}

package net.iclassmate.bxyd.bean.study;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xydbj on 2016/6/6.
 */
public class StudyMessage implements Serializable {
    private String headUrl;
    private String name;
    private String time;
    private String content;
    private List<ImageType> picList;
    private int comment;
    private int like;
    private boolean isLike;

    public boolean isLike() {
        return isLike;
    }

    public void setIsLike(boolean isLike) {
        this.isLike = isLike;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<ImageType> getPicList() {
        return picList;
    }

    public void setPicList(List<ImageType> picList) {
        this.picList = picList;
    }

    public int getComment() {
        return comment;
    }

    public void setComment(int comment) {
        this.comment = comment;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
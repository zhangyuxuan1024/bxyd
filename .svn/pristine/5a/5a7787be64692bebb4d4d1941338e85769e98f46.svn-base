package net.iclassmate.bxyd.bean.study.save;

import net.iclassmate.bxyd.bean.study.CreateBy;
import net.iclassmate.bxyd.bean.study.OriginBulletinInfo;
import net.iclassmate.bxyd.bean.study.Parserable;
import net.iclassmate.bxyd.bean.study.Resources;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xydbj on 2016/7/9.
 */
public class Bulletin implements Serializable, Parserable {
    private String bulletinType;
    private boolean commentable;
    private int commented;
    private String content;
    private CreateBy createdBy;
    private String createdOn;
    private boolean downloadable;
    private int downloaded;
    private int explored;
    private int favored;
    private boolean forwardable;
    private int forwarded;
    private String id;
    private int liked;
    private OriginBulletinInfo originBulletinInfo;
    private int reported;
    private List<Resources> list;
    private String sectionId;
    private int shared;
    private String spaceId;
    private boolean clickLiked;

    @Override
    public void parserJson(JSONObject json) {
        if (json != null) {
            try {
                bulletinType = json.getString("bulletinType");
                commentable = json.getBoolean("commentable");
                commented = json.getInt("commented");
                content = json.getString("content");
                createdBy = new CreateBy();
                JSONObject jsonObject = json.optJSONObject("createdBy");
                if (jsonObject != null) {
                    createdBy.parserJson(jsonObject);
                    setCreatedBy(createdBy);
                }
                createdOn = json.getString("createdOn");
                downloadable = json.getBoolean("downloadable");
                downloaded = json.getInt("downloaded");
                explored = json.getInt("explored");
                favored = json.getInt("favored");
                forwardable = json.getBoolean("forwardable");
                forwarded = json.getInt("forwarded");
                id = json.getString("id");
                liked = json.getInt("liked");
                reported = json.getInt("reported");
                list = new ArrayList<>();
                JSONArray array = json.optJSONArray("resources");
                if (array != null && array.length() > 0) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jsonObject1 = array.optJSONObject(i);
                        Resources resources = new Resources();
                        if (jsonObject1 != null) {
                            resources.parserJson(jsonObject1);
                            list.add(resources);
                        }
                    }
                    setList(list);
                }
                sectionId = json.getString("sectionId");
                shared = json.getInt("shared");
                spaceId = json.getString("spaceId");
                originBulletinInfo = new OriginBulletinInfo();
                JSONObject jsonObject1 = json.optJSONObject("originBulletinInfo");
                if (jsonObject1 != null) {
                    originBulletinInfo.parserJson(jsonObject1);
                    setOriginBulletinInfo(originBulletinInfo);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String getBulletinType() {
        return bulletinType;
    }

    public void setBulletinType(String bulletinType) {
        this.bulletinType = bulletinType;
    }

    public boolean isCommentable() {
        return commentable;
    }

    public void setCommentable(boolean commentable) {
        this.commentable = commentable;
    }

    public int getCommented() {
        return commented;
    }

    public void setCommented(int commented) {
        this.commented = commented;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public CreateBy getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(CreateBy createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public boolean isDownloadable() {
        return downloadable;
    }

    public void setDownloadable(boolean downloadable) {
        this.downloadable = downloadable;
    }

    public int getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(int downloaded) {
        this.downloaded = downloaded;
    }

    public int getExplored() {
        return explored;
    }

    public void setExplored(int explored) {
        this.explored = explored;
    }

    public int getFavored() {
        return favored;
    }

    public void setFavored(int favored) {
        this.favored = favored;
    }

    public boolean isForwardable() {
        return forwardable;
    }

    public void setForwardable(boolean forwardable) {
        this.forwardable = forwardable;
    }

    public int getForwarded() {
        return forwarded;
    }

    public void setForwarded(int forwarded) {
        this.forwarded = forwarded;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getLiked() {
        return liked;
    }

    public void setLiked(int liked) {
        this.liked = liked;
    }

    public int getReported() {
        return reported;
    }

    public void setReported(int reported) {
        this.reported = reported;
    }

    public List<Resources> getList() {
        return list;
    }

    public void setList(List<Resources> list) {
        this.list = list;
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public int getShared() {
        return shared;
    }

    public void setShared(int shared) {
        this.shared = shared;
    }

    public String getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }

    public boolean isClickLiked() {
        return clickLiked;
    }

    public void setClickLiked(boolean clickLiked) {
        this.clickLiked = clickLiked;
    }

    public OriginBulletinInfo getOriginBulletinInfo() {
        return originBulletinInfo;
    }

    public void setOriginBulletinInfo(OriginBulletinInfo originBulletinInfo) {
        this.originBulletinInfo = originBulletinInfo;
    }
}

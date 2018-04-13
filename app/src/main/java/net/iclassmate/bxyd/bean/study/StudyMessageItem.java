package net.iclassmate.bxyd.bean.study;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xydbj on 2016/6/21.
 */
public class StudyMessageItem implements Serializable, Parserable {
    private String bulletinType;
    private boolean commentable;
    private int commented;
    private String content;
    private CreateBy createBy;
    private String createdOn;
    private boolean downloadable;
    private int downloaded;
    private int explored;
    private int favored;
    private boolean forwardable;
    private int forwarded;
    private String id;
    private int liked;
    private int reported;
    private List<Resources> list;
    private String sectionId;
    private String shared;
    private String spaceId;
    private OriginBulletinInfo originBulletinInfo;
    private boolean isClickLiked;

    @Override
    public void parserJson(JSONObject json) {
        if (json != null) {
            try {
                bulletinType = json.getString("bulletinType");
                commentable = json.getBoolean("commentable");
                commented = json.getInt("commented");
                content = json.getString("content");
                CreateBy createBy = new CreateBy();
                JSONObject jsonObject2 = json.optJSONObject("createdBy");
                if (jsonObject2 != null) {
                    createBy.parserJson(jsonObject2);
                    setCreateBy(createBy);
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
                        setList(list);
                    }
                }
                sectionId = json.getString("sectionId");
                shared = json.getString("shared");
                spaceId = json.getString("spaceId");
                originBulletinInfo = new OriginBulletinInfo();
                JSONObject object = json.optJSONObject("originBulletinInfo");
                if (object != null) {
                    originBulletinInfo.parserJson(object);
                    setOriginBulletinInfo(originBulletinInfo);
                }
                isClickLiked = json.getBoolean("likedFlag");
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

    public String getShared() {
        return shared;
    }

    public void setShared(String shared) {
        this.shared = shared;
    }

    public String getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }

    public boolean isClickLiked() {
        return isClickLiked;
    }

    public void setIsClickLiked(boolean isClickLiked) {
        this.isClickLiked = isClickLiked;
    }

    public OriginBulletinInfo getOriginBulletinInfo() {
        return originBulletinInfo;
    }

    public void setOriginBulletinInfo(OriginBulletinInfo originBulletinInfo) {
        this.originBulletinInfo = originBulletinInfo;
    }
}
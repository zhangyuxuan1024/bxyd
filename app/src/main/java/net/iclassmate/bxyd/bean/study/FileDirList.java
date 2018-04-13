package net.iclassmate.bxyd.bean.study;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by xydbj on 2016/6/24.
 */
public class FileDirList implements Serializable, Parserable {
    private String auth;
    private String createTime;
    private FileLabel fileLabel;
    private String fileType;
    private String fullPath;
    private String id;
    private String label;
    private String parentId;
    private String saveUuid;
    private String scale;
    private String seq;
    private String shortName;
    private int size;
    private String spaceUuid;
    private int type;
    private String updateTime;
    private String userUuid;
    private boolean isCheck;

    @Override
    public void parserJson(JSONObject json) {
        if (json != null) {
            try {
                auth = json.getString("auth");
                createTime = json.getString("createTime");
                fileLabel = new FileLabel();
                JSONObject jsonObject = json.optJSONObject("fileLabel");
                if (jsonObject != null) {
                    fileLabel.parserJson(jsonObject);
                    setFileLabel(fileLabel);
                }
                fileType = json.getString("fileType");
                fullPath = json.getString("fullPath");
                id = json.getString("id");
                label = json.getString("label");
                parentId = json.getString("parentId");
                saveUuid = json.getString("saveUuid");
                scale = json.getString("scale");
                seq = json.getString("seq");
                shortName = json.getString("shortName");
                size = json.getInt("size");
                spaceUuid = json.getString("spaceUuid");
                type = json.getInt("type");
                updateTime = json.getString("updateTime");
                userUuid = json.getString("userUuid");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setIsCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public FileLabel getFileLabel() {
        return fileLabel;
    }

    public void setFileLabel(FileLabel fileLabel) {
        this.fileLabel = fileLabel;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getSaveUuid() {
        return saveUuid;
    }

    public void setSaveUuid(String saveUuid) {
        this.saveUuid = saveUuid;
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getSpaceUuid() {
        return spaceUuid;
    }

    public void setSpaceUuid(String spaceUuid) {
        this.spaceUuid = spaceUuid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }
}

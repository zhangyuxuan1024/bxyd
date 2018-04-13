package net.iclassmate.bxyd.bean.owner;

/**
 * Created by xydbj on 2016.8.3.
 */
public class Update {
    private String version;
    private String url;
    private String updateDesc;
    private String size;
    private long createTime;

    @Override
    public String toString() {
        return "Update{" +
                "version='" + version + '\'' +
                ", url='" + url + '\'' +
                ", updateDesc='" + updateDesc + '\'' +
                ", size='" + size + '\'' +
                ", createTime=" + createTime +
                '}';
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUpdateDesc() {
        return updateDesc;
    }

    public void setUpdateDesc(String updateDesc) {
        this.updateDesc = updateDesc;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}

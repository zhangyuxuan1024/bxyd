package net.iclassmate.bxyd.bean.study;

/**
 * Created by xydbj on 2016/6/22.
 */
public class ImageType {
    private int type;
    private String path;
    private String name;
    public static int IMG_TYPE_PIC = 0;
    public static int IMG_TYPE_OTHER = 1;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

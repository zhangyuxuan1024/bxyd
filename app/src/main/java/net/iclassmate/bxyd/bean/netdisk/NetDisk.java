package net.iclassmate.bxyd.bean.netdisk;

import java.util.List;

/**
 * Created by xydbj on 2016.6.27.
 */
public class NetDisk {
    private List<FileDirList> fileDirLists;

    public List<FileDirList> getFileDirLists() {
        return fileDirLists;
    }

    public void setFileDirLists(List<FileDirList> fileDirLists) {
        this.fileDirLists = fileDirLists;
    }

    @Override
    public String toString() {
        return "NetDisk{" +
                "fileDirLists=" + fileDirLists +
                '}';
    }
}
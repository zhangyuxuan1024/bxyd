package net.iclassmate.bxyd.bean.contacts;

import java.util.ArrayList;

/**
 * Created by xyd on 2016/7/5.
 */
public class Group {
  /*  "resultCode": 0,
            "resultMarks": "成功",*/
    private int resultCode;
    private String resultMarks;
    private ArrayList<GroupInfo> list;

    public Group(int resultCode, String resultMarks, ArrayList<GroupInfo> list) {
        this.resultCode = resultCode;
        this.resultMarks = resultMarks;
        this.list = list;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMarks() {
        return resultMarks;
    }

    public void setResultMarks(String resultMarks) {
        this.resultMarks = resultMarks;
    }

    public ArrayList<GroupInfo> getList() {
        return list;
    }

    public void setList(ArrayList<GroupInfo> list) {
        this.list = list;
    }
}

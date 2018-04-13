package net.iclassmate.bxyd.bean.contacts;

/**
 * Created by xyd on 2016/7/8.
 */
public class AddGroupInfo {
   /* {
        "author": "string",
            "resultCode": 0,
            "resultMarks": "string",
            "sessionId": "string"
    }*/
    private String author;
    private int resultCode;
    private String resultMarks;
    private String sessionId;

    public AddGroupInfo(String author, int resultCode, String resultMarks, String sessionId) {
        this.author = author;
        this.resultCode = resultCode;
        this.resultMarks = resultMarks;
        this.sessionId = sessionId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
package net.iclassmate.bxyd.bean;

/**
 * Created by xydbj on 2016/10/22.
 */
public class LoginResult {
    private int code;
    private String result;

    public LoginResult(int code, String result) {
        this.code = code;
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
package net.iclassmate.bxyd.bean.index;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by xydbj on 2016/11/18.
 */
public class PayParameter implements Serializable {
    /**
     * appId : wx5305ba8c2d88d463
     * partnerId : 1317102701
     * prepayId : wx2016111810210328964ac38a0061631427
     * nonceStr : 4LZgZ6W3M3FpTiXw
     * timestamp : 2016年11月18日10时21分03秒
     * sign : F5A7683CBF77FF93955145B4AF88F689
     */

    private String orderId;
    private String appId;
    private String partnerId;
    private String prepayId;
    private String nonceStr;
    private String timestamp;
    private String sign;

    public void parserJson(JSONObject json) {
        if (json != null) {
            try {
                appId = json.getString("appId");
                partnerId = json.getString("partnerId");
                prepayId = json.getString("prepayId");
                nonceStr = json.getString("nonceStr");
                timestamp = json.getString("timestamp");
                sign = json.getString("sign");
                orderId = json.optString("orderId");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public void setPrepayId(String prepayId) {
        this.prepayId = prepayId;
    }

    public void setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getAppId() {
        return appId;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public String getPrepayId() {
        return prepayId;
    }

    public String getNonceStr() {
        return nonceStr;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getSign() {
        return sign;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}

package net.iclassmate.bxyd.bean.index;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by xydbj on 2016/11/17.
 */
public class Order implements Serializable {
    /**
     * adsAddress : 北京市海淀区
     * adsName : test4
     * id : 1479353808472247
     * joinAdsTime : 1479340800000
     * paymentWay : 1
     * price : 0.01
     * userNumber : 1
     */

    private String adsAddress;
    private String adsName;
    private String id;
    private long joinAdsTime;
    private int paymentWay;
    private double price;
    private int userNumber;

    public void parserJson(JSONObject json) {
        if (json != null) {
            try {
                adsAddress = json.getString("adsAddress");
                adsName = json.getString("adsName");
                id = json.getString("id");
                joinAdsTime = json.getLong("joinAdsTime");
                paymentWay = json.getInt("paymentWay");
                price = json.getDouble("price");
                userNumber = json.getInt("userNumber");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void setAdsAddress(String adsAddress) {
        this.adsAddress = adsAddress;
    }

    public void setAdsName(String adsName) {
        this.adsName = adsName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setJoinAdsTime(long joinAdsTime) {
        this.joinAdsTime = joinAdsTime;
    }

    public void setPaymentWay(int paymentWay) {
        this.paymentWay = paymentWay;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setUserNumber(int userNumber) {
        this.userNumber = userNumber;
    }

    public String getAdsAddress() {
        return adsAddress;
    }

    public String getAdsName() {
        return adsName;
    }

    public String getId() {
        return id;
    }

    public long getJoinAdsTime() {
        return joinAdsTime;
    }

    public int getPaymentWay() {
        return paymentWay;
    }

    public double getPrice() {
        return price;
    }

    public int getUserNumber() {
        return userNumber;
    }
}

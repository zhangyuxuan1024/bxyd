package net.iclassmate.bxyd.bean.area;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by xydbj on 2016.6.22.
 */
public class Province {
    private String province_name;
    private String province_code;
    private List<City> cityList;

    public String getProvince_name() {
        return province_name;
    }

    public void setProvince_name(String province_name) {
        this.province_name = province_name;
    }

    public List<City> getCityList() {
        return cityList;
    }

    public void setCityList(List<City> cityList) {
        this.cityList = cityList;
    }

    public String getProvince_code() {
        return province_code;
    }

    public void setProvince_code(String province_code) {
        this.province_code = province_code;
    }

    @Override
    public String toString() {
        return "Province{" +
                "province_name='" + province_name + '\'' +
                ", province_code='" + province_code + '\'' +
                ", cityList=" + cityList +
                '}';
    }
}

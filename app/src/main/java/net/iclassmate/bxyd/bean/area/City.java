package net.iclassmate.bxyd.bean.area;

import java.util.List;

/**
 * Created by xydbj on 2016.6.22.
 */
public class City {
    private String city_name;
    private String city_code;
    private List<County> countyList;

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getCity_code() {
        return city_code;
    }

    public void setCity_code(String city_code) {
        this.city_code = city_code;
    }

    public List<County> getCountyList() {
        return countyList;
    }

    public void setCountyList(List<County> countyList) {
        this.countyList = countyList;
    }

    @Override
    public String toString() {
        return "City{" +
                "city_name='" + city_name + '\'' +
                ", city_code='" + city_code + '\'' +
                ", countyList=" + countyList +
                '}';
    }
}

package net.iclassmate.bxyd.bean.area;

/**
 * Created by xydbj on 2016.6.22.
 */
public class County {
    private String county_name;
    private String county_code;

    public String getCounty_name() {
        return county_name;
    }

    public void setCounty_name(String county_name) {
        this.county_name = county_name;
    }

    public String getCounty_code() {
        return county_code;
    }

    public void setCounty_code(String county_code) {
        this.county_code = county_code;
    }

    @Override
    public String toString() {
        return "County{" +
                "county_name='" + county_name + '\'' +
                ", county_code='" + county_code + '\'' +
                '}';
    }
}

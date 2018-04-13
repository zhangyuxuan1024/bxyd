package net.iclassmate.bxyd.bean.index;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xydbj on 2016/11/14.
 */
public class Index implements Serializable {
    private List<Banner> listBanner;
    private List<Recommend> listRecommend;

    public void parserJson(JSONObject json) {
        if (json != null) {
            JSONArray array1 = json.optJSONArray("banner");
            if (array1 != null) {
                listBanner = new ArrayList<>();
                for (int i = 0; i < array1.length(); i++) {
                    JSONObject json1 = array1.optJSONObject(i);
                    if (json1 != null) {
                        Banner banner = new Banner();
                        banner.parserJson(json1);
                        listBanner.add(banner);
                    }
                }
                setListBanner(listBanner);
            }

            JSONArray array2 = json.optJSONArray("recommend");
            if (array2 != null) {
                listRecommend = new ArrayList<>();
                for (int i = 0; i < array2.length(); i++) {
                    JSONObject json2 = array2.optJSONObject(i);
                    if (json2 != null) {
                        Recommend recommend = new Recommend();
                        recommend.parserJson(json2);
                        listRecommend.add(recommend);
                    }
                }
                setListRecommend(listRecommend);
            }
        }
    }

    public List<Banner> getListBanner() {
        return listBanner;
    }

    public void setListBanner(List<Banner> listBanner) {
        this.listBanner = listBanner;
    }

    public List<Recommend> getListRecommend() {
        return listRecommend;
    }

    public void setListRecommend(List<Recommend> listRecommend) {
        this.listRecommend = listRecommend;
    }
}

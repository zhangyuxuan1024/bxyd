package net.iclassmate.bxyd.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.stx.xhb.xbanner.XBanner;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.adapter.IndexAdapter;
import net.iclassmate.bxyd.bean.index.Banner;
import net.iclassmate.bxyd.bean.index.Index;
import net.iclassmate.bxyd.bean.index.Recommend;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.ui.activitys.index.WebBxActivity;
import net.iclassmate.bxyd.utils.FileUtils;
import net.iclassmate.bxyd.utils.NetWorkUtils;
import net.iclassmate.bxyd.view.pullrefreshview.PullToRefreshLayout;
import net.iclassmate.bxyd.view.pullrefreshview.PullableScrollView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class IndexFragment extends Fragment implements XBanner.XBannerAdapter, XBanner.OnItemClickListener, AdapterView.OnItemClickListener, View.OnTouchListener {
    private Context mContext;
    private XBanner xBanner;
    private List<Banner> list;
    private List<Recommend> data;
    private IndexAdapter adapter;
    private ListView listView;
    private Index indexPage;

    private PullToRefreshLayout pull;
    private PullableScrollView pullableScrollView;
    private long last_touch_time;
    private boolean loadfinish;
    private String filename;

    private ImageView img_anim;
    private AnimationDrawable anim;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    loadfinish = true;
//                    pull.refreshFinish(PullToRefreshLayout.SUCCEED);
//                    pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);

                    anim.stop();
                    img_anim.setVisibility(View.GONE);

                    String ret = (String) msg.obj;
                    Log.i("info", "首页原始数据：" + ret);
                    try {
                        if (NetWorkUtils.isNetworkAvailable(mContext)) {
                            FileUtils.write2Sd(ret, filename);
                        }

                        JSONObject json = new JSONObject(ret);
                        indexPage.parserJson(json);
                        List<Banner> listBanner = indexPage.getListBanner();
                        if (listBanner != null) {
                            list.clear();
                            list.addAll(listBanner);
                            xBanner.setData(list);
                        }
                        List<Recommend> listRecommend = indexPage.getListRecommend();
                        if (listRecommend != null) {
                            data.clear();
                            data.addAll(listRecommend);
                            adapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 404:
                    Toast.makeText(mContext, getString(R.string.con_out_time), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_index, container, false);
        init(view);
        loadData();
        return view;
    }

    private void loadData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!NetWorkUtils.isNetworkAvailable(mContext)) {
                    String s = FileUtils.read2Sd(filename);
                    Message message = new Message();
                    message.what = 1;
                    message.obj = s;
                    mHandler.sendMessage(message);
                    return;
                }
                String url = Constant.INDEX_INFO;
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        Message message = new Message();
                        message.what = 1;
                        message.obj = response.body().string();
                        if (loadfinish) {
                            mHandler.sendMessage(message);
                        } else {
                            mHandler.sendMessageDelayed(message, 1500);
                        }
                    } else {
                        mHandler.sendEmptyMessage(2);
                    }
                    if (response != null) {
                        response.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(2);
                } catch (Exception e) {
                    mHandler.sendEmptyMessage(2);
                }
            }
        }).start();
    }

    private void init(View view) {
        mContext = getActivity();
        img_anim = (ImageView) view.findViewById(R.id.img_anim);
        img_anim.setVisibility(View.VISIBLE);
        anim = (AnimationDrawable) img_anim.getBackground();
        anim.start();

        xBanner = (XBanner) view.findViewById(R.id.main_xBanner);
        list = new ArrayList<>();
        xBanner.setmAdapter(this);
        xBanner.setOnItemClickListener(this);

        data = new ArrayList<>();
        adapter = new IndexAdapter(mContext, data);
        listView = (ListView) view.findViewById(R.id.listview_index_main);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        indexPage = new Index();

//        pull = (PullToRefreshLayout) view.findViewById(R.id.index_bx_pulltorefresh);
//        pullableScrollView = (PullableScrollView) view.findViewById(R.id.index_bx_pullscor);
//        pull.setOnRefreshListener(new MyListener());
//        pull.setOnTouchListener(this);
        loadfinish = true;
        filename = "bx_index.dat";
    }


    @Override
    public void loadBanner(XBanner xBanner, View view, int i) {
        Banner banner = list.get(i);
        Picasso.with(mContext).load(banner.getImageUrl())
                .placeholder(R.mipmap.img_moren_banner).error(R.mipmap.img_moren_banner)
                .config(Bitmap.Config.RGB_565)
                .into((ImageView) view);
    }

    @Override
    public void onItemClick(XBanner xBanner, int i) {
        Banner banner = list.get(i);
        String url = banner.getJumpUrl();
        int type = banner.getType();
        if (url == null || url.equals("") || !url.startsWith("http")) {
            return;
        }
        if (!NetWorkUtils.isNetworkAvailable(mContext)) {
            if (System.currentTimeMillis() - last_touch_time < 3000){
                return;
            }
            Toast.makeText(getActivity(),R.string.alert_msg_check_net,Toast.LENGTH_SHORT).show();
            last_touch_time = System.currentTimeMillis();
            return;
        }
        Intent intent = new Intent(mContext, WebBxActivity.class);
        if (type == 1){
            intent.putExtra("title", "活动详情");
        }else if (type == 2){
            intent.putExtra("title", "详情");
        }
        intent.putExtra("back", "首页");
        intent.putExtra("url", url);
        intent.putExtra("type", "banner");
        intent.putExtra("data", banner);
        intent.putExtra("type2",type);
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Recommend recommend = data.get(position);
        String url = recommend.getJumpUrl();
        int type = recommend.getType();
        if (url == null || url.equals("") || !url.startsWith("http")) {
            return;
        }
        if (!NetWorkUtils.isNetworkAvailable(mContext)) {
            if (System.currentTimeMillis() - last_touch_time < 3000){
                return;
            }
            Toast.makeText(getActivity(),R.string.alert_msg_check_net,Toast.LENGTH_SHORT).show();
            last_touch_time = System.currentTimeMillis();
            return;
        }
        Intent intent = new Intent(mContext, WebBxActivity.class);
        if (type == 1){
            intent.putExtra("title", "活动详情");
        }else if (type == 2){
            intent.putExtra("title", "详情");
        }
        intent.putExtra("back", "首页");
        intent.putExtra("url", url);
        intent.putExtra("data", recommend);
        intent.putExtra("type", "recommend");
        intent.putExtra("type2",type);
        startActivity(intent);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
//        if (NetWorkUtils.isNetworkAvailable(UIUtils.getContext()) && System.currentTimeMillis() - last_touch_time > 3000 && loadfinish) {
//            if (pull.getCurrentState() == PullToRefreshLayout.REFRESHING
//                    || pull.getCurrentState() == PullToRefreshLayout.LOADING) {
//                loadfinish = false;
//                loadData();
//                last_touch_time = System.currentTimeMillis();
//            }
//        }
        return true;
    }
}

package net.iclassmate.bxyd.ui.activitys.owner;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.adapter.owner.AttentionAdapter;
import net.iclassmate.bxyd.bean.attention.Attention;
import net.iclassmate.bxyd.bean.attention.Attention_All;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.utils.CharacterParser;
import net.iclassmate.bxyd.utils.JsonUtils;
import net.iclassmate.bxyd.utils.PinyinComparator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by xydbj on 2016.6.18.
 */
public class AttentionActivity extends Activity implements SectionIndexer, View.OnClickListener {
    private ListView sortListView;
    //    private SideBar sideBar;//右侧的ABCDEF......XYZ#
    private TextView dialog, title, tvNofriends, attention_tv_back;
    private ImageView attention_iv_back, attention_iv_loading;
    private AttentionAdapter attentionAdapter;
    private SharedPreferences sharedPreferences;
    private LinearLayout titleLayout;

    private AnimationDrawable anim;

    /**
     * 上次第一个可见元素，用于滚动时记录标识。
     */
    private int lastFirstVisibleItem = -1;
    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    private List<Attention> attentionList;

    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention);

        initViews();

        IntentFilter filter = new IntentFilter(UnfollowActivity.action);
        registerReceiver(broadcastReceiver, filter);
    }

    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<Attention> list = (List<Attention>) intent.getSerializableExtra("list");
            if (list != null) {
                attentionAdapter.updateListView(list);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    private void initViews() {
        attention_tv_back = (TextView) findViewById(R.id.attention_tv_back);
        attention_iv_back = (ImageView) findViewById(R.id.attention_iv_back);

        attention_tv_back.setOnClickListener(this);
        attention_iv_back.setOnClickListener(this);
        titleLayout = (LinearLayout) findViewById(R.id.title_layout);
        title = (TextView) this.findViewById(R.id.title_layout_catalog);

        attention_iv_loading = (ImageView) findViewById(R.id.attention_iv_loading);
        anim = (AnimationDrawable) attention_iv_loading.getBackground();
        anim.start();

        // 实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();

        sortListView = (ListView) findViewById(R.id.country_lvcountry);
        attentionList = new ArrayList<>();
        attentionAdapter = new AttentionAdapter(this, attentionList);
        sortListView.setAdapter(attentionAdapter);

        getSpaceId();
    }

    public void addData(int length) {
        // 根据a-z进行排序源数据
        Collections.sort(attentionList, pinyinComparator);
        if (length != 1) {
            sortListView.setOnScrollListener(scrollListener);
        }
        attentionAdapter.updateListView(attentionList);
    }

    public OnScrollListener scrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            int section = getSectionForPosition(firstVisibleItem);
            int nextSection = getSectionForPosition(firstVisibleItem + 1);
            int nextSecPosition = getPositionForSection(+nextSection);
            if (firstVisibleItem != lastFirstVisibleItem) {
                MarginLayoutParams params = (MarginLayoutParams) titleLayout.getLayoutParams();
                params.topMargin = 0;
                titleLayout.setLayoutParams(params);
                title.setText(attentionList.get(getPositionForSection(section)).getUserPinyin());
            }
            if (nextSecPosition == firstVisibleItem + 1) {
                View childView = view.getChildAt(0);
                if (childView != null) {
                    int titleHeight = titleLayout.getHeight();
                    int bottom = childView.getBottom();
                    MarginLayoutParams params = (MarginLayoutParams) titleLayout.getLayoutParams();
                    if (bottom < titleHeight) {
                        float pushedDistance = bottom - titleHeight;
                        params.topMargin = (int) pushedDistance;
                        titleLayout.setLayoutParams(params);
                    } else {
                        if (params.topMargin != 0) {
                            params.topMargin = 0;
                            titleLayout.setLayoutParams(params);
                        }
                    }
                }
            }
            lastFirstVisibleItem = firstVisibleItem;
        }
    };

    /**
     * 为ListView填充数据,这就是那个添加的拼音
     *
     * @param date
     * @return
     */
    private List<Attention> filledData(String[] date) {
        List<Attention> attentionList = new ArrayList<Attention>();
        for (int i = 0; i < date.length; i++) {
            Attention attention = new Attention();
            attention.setUserName(date[i]);
            // 汉字转换成拼音
            String pinyin = characterParser.getSelling(date[i]);
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                attention.setUserPinyin(sortString.toUpperCase());
            } else {
                attention.setUserPinyin("#");
            }

            attentionList.add(attention);
        }
        return attentionList;

    }

    private List<Attention> filledData(List<Attention> attentionlist) {
        List<Attention> attentionList = new ArrayList<Attention>();

        for (int i = 0; i < attentionlist.size(); i++) {
            Attention attention = new Attention();
            attention.setUserName(attentionlist.get(i).getUserName());
            // 汉字转换成拼音
            String pinyin = characterParser.getSelling(attentionlist.get(i).getUserName());
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                attention.setUserPinyin(sortString.toUpperCase());
            } else {
                attention.setUserPinyin("#");
            }
            attention.setUuid(attentionlist.get(i).getUuid());
            attention.setSubSpaceId(attentionlist.get(i).getSubSpaceId());
            attention.setUserIcon(attentionlist.get(i).getUserIcon());
            attention.setOwnerId(attentionlist.get(i).getOwnerId());
            attention.setType(attentionlist.get(i).getType());
            attentionList.add(attention);
        }
        return attentionList;

    }

    @Override
    public Object[] getSections() {
        return null;
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    @Override
    public int getPositionForSection(int position) {
        for (int i = 0; i < attentionList.size(); i++) {
            String sortStr = attentionList.get(i).getUserPinyin();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == position) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    @Override
    public int getSectionForPosition(int position) {
        return attentionList.get(position).getUserPinyin().charAt(0);
    }

    public void getSpaceId() {
        sharedPreferences = AttentionActivity.this.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String spaceId = sharedPreferences.getString(Constant.ID_SPACE, "");
        Log.i("info", "获取关注列表的spaceId：" + spaceId);
        getData(spaceId);
    }

    public void getData(final String spaceId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                execute(spaceId);
            }
        }).start();
    }

    public void execute(String spaceId) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(Constant.GETATTENTION + spaceId + "/related/type/concern?getSubSpaceDetail=true&page=1&page-size=30")
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("info", "获取关注列表，网络请求数据失败：" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("info", "获取关注列表，网络请求数据成功");
                Message msg = new Message();
                msg.what = 1;
                msg.obj = response.body().string();
                mHandler.sendMessage(msg);
            }
        });
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    String json = (String) msg.obj;
                    Attention_All attention_all = JsonUtils.StartAttentionJson(json);
                    Log.i("info", "获取关注列表，解析的数据成功：" + attention_all.toString());
                    List<Attention> attentionlist = new ArrayList<>();
                    if (attention_all.getAttention_responsesList() == null || attention_all.getAttention_responsesList().equals("") || attention_all.getAttention_responsesList().equals("null")) {
                        attention_iv_loading.setBackgroundResource(R.mipmap.ic_no_result);
                        attention_iv_loading.setVisibility(View.VISIBLE);
                    }else{
                        for (int i = 0; i < attention_all.getAttention_responsesList().size(); i++) {
                            Attention attention = new Attention();
                            attention.setUserName(  attention_all.getAttention_responsesList().get(i).getNoteName());
                            attention.setSubSpaceId(attention_all.getAttention_responsesList().get(i).getSubSpaceId());
                            attention.setUuid(      attention_all.getAttention_responsesList().get(i).getUuid());
                            attention.setUserIcon(  attention_all.getAttention_responsesList().get(i).getSubSpace().getIcon());
                            attention.setOwnerId(   attention_all.getAttention_responsesList().get(i).getSubSpace().getOwnerId());
                            attention.setType(      attention_all.getAttention_responsesList().get(i).getSubSpace().getType());
                            attentionlist.add(attention);
                        }
                        attentionList = filledData(attentionlist);
                        Log.i("info","-----"+attentionList.toString());
                        if (attentionList.size() == 0 || attentionList.equals("") || attentionList.equals("null")) {
                            attention_iv_loading.setBackgroundResource(R.mipmap.ic_no_result);
                            attention_iv_loading.setVisibility(View.VISIBLE);
                        } else {
                            attention_iv_loading.setVisibility(View.INVISIBLE);
                            anim.stop();
                            addData(attentionList.size());
                        }
                    }
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.attention_tv_back:
            case R.id.attention_iv_back:
                finish();
                break;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("AttentionActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("AttentionActivity");
        MobclickAgent.onPause(this);
    }
}

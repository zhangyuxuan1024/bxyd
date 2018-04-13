package net.iclassmate.bxyd.ui.activitys.constacts;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.bean.message.UserMessage;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.ui.activitys.information.FriendInformationActivity;
import net.iclassmate.bxyd.utils.HttpManager;
import net.iclassmate.bxyd.utils.JsonUtils;
import net.iclassmate.bxyd.utils.UIUtils;
import net.iclassmate.bxyd.view.Loading;
import net.iclassmate.bxyd.view.TitleBar;
import net.iclassmate.bxyd.view.study.ShapeImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FriendResActivity extends FragmentActivity implements TitleBar.TitleOnClickListener {
    private String type, key, mId;
    private String result;
    private Context mContext;
    private TitleBar titleBar;
    private ListView mResult;
    private ImageView mNoResult;
    private Loading loading;
    private HttpManager httpManager;
    private MyAdapter myAdapter;
    private List<UserMessage> findUserInfos;
    private SharedPreferences sp;
    private static final int FIND_USER_SUCCESS = 0;
    private static final int FIND_USER_FAIL = 1;
    private boolean isFirstFunction, isAccurateFound;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case FIND_USER_SUCCESS:
                    loading.hideLoading(true);
                    if (result != null) {
                        if (isAccurateFound) {//当用数字精确查找时
                            try {
                                JSONArray array = null;
                                if (isFirstFunction) {
                                    array = new JSONArray(result);
                                } else {
                                    JSONObject jsonObject = new JSONObject(result);
                                    array = jsonObject.optJSONArray("list");
                                }
                                for (int i = 0; i < array.length() && array != null; i++) {
                                    JSONObject json = array.optJSONObject(i);
                                    if (json != null) {
                                        UserMessage message = new UserMessage();
                                        message.parserJson(json);
                                        findUserInfos.add(message);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (findUserInfos.size() == 0) {
                                mNoResult.setVisibility(View.VISIBLE);
                                mResult.setVisibility(View.GONE);
                            } else {
                                if (myAdapter != null) {
                                    myAdapter.notifyDataSetChanged();
                                }
                            }
                        } else {//当用文字模糊查询时
                            if (type.equals("person") || type.equals("org")) {
                                try {
                                    JSONArray array = null;
                                    if (isFirstFunction) {
                                        array = new JSONArray(result);
                                    } else {
                                        JSONObject jsonObject = new JSONObject(result);
                                        array = jsonObject.optJSONArray("list");
                                    }
                                    for (int i = 0; i < array.length() && array != null; i++) {
                                        JSONObject json = array.optJSONObject(i);
                                        if (json != null) {
                                            UserMessage message = new UserMessage();
                                            message.parserJson(json);
                                            findUserInfos.add(message);
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (findUserInfos.size() == 0) {
                                    mNoResult.setVisibility(View.VISIBLE);
                                    mResult.setVisibility(View.GONE);
                                } else {
                                    if (myAdapter != null) {
                                        myAdapter.notifyDataSetChanged();
                                    }
                                }
                            } else if (type.equals("group")) {
//                                JSONArray array = null;
//                                JSONObject jsonObject = null;
//                                try {
//                                    jsonObject = new JSONObject(result);
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                                array = jsonObject.optJSONArray("responses");
//                                for (int i = 0; i < array.length() && array != null; i++) {
//                                    JSONObject json = array.optJSONObject(i);
//                                    if (json != null) {
//                                        UserMessage message = new UserMessage();
//                                        message.parserJson(json);
//                                        findUserInfos.add(message);
//                                    }
//                                }
//                                Log.i("info", "(未解析)用文字模糊查询群组的findUserInfos：" + result.toString());
                                findUserInfos = JsonUtils.StartUserMessageJson(result);
                                Log.i("info", "(已解析)用文字模糊查询群组的findUserInfos：" + findUserInfos.toString());

                                if (findUserInfos.size() == 0) {
                                    mNoResult.setVisibility(View.VISIBLE);
                                    mResult.setVisibility(View.GONE);
                                } else {
                                    if (myAdapter != null) {
                                        myAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        }
                    }
                    result = null;
                    break;
                case FIND_USER_FAIL:
                    loading.hideLoading(true);
//                    Toast.makeText(UIUtils.getContext(), "搜索好友失败，请稍候再试", Toast.LENGTH_SHORT).show();
                    mNoResult.setVisibility(View.VISIBLE);
                    mResult.setVisibility(View.GONE);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_res);
        mContext = this;
        initView();
        initData();
        initListener();
    }

    private void initView() {
        titleBar = (TitleBar) findViewById(R.id.friend_result_title_bar);
        titleBar.setTitle("搜索结果");
        titleBar.setLeftIcon(R.mipmap.ic_fanhui, "返回");

        mResult = (ListView) findViewById(R.id.friend_result_lv);
        httpManager = new HttpManager();
        mNoResult = (ImageView) findViewById(R.id.search_no_result);
        loading = new Loading(findViewById(R.id.loading_layout), (LinearLayout) findViewById(R.id.search_result_layout));

        sp = mContext.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        findUserInfos = new ArrayList<>();
        myAdapter = new MyAdapter();
        mResult.setAdapter(myAdapter);
    }

    private void initData() {
        type = getIntent().getStringExtra("type");
        key = getIntent().getStringExtra("key");
//        Log.i("info", "进入搜索界面传入的参数：key=" + key + "，type=" + type);
        loading.showLoading(true);
        final boolean isNum = isNum(key);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isNum) {//用用户号或者数字查询
                    isAccurateFound = true;
                    result = httpManager.searchInfo(key, type);
//                    Log.i("info", "当精确查找时，result=" + result);
                } else {
                    if (type.equals("person")) {
//                        result = httpManager.searchInfoByName(key);
                        result = httpManager.searchInfoByName(key, 1);
//                        Log.i("info", "当点击person时的result：" + result);
                    } else if (type.equals("group")) {
                        result = httpManager.searchGroupByName(key);
//                        Log.i("info", "当点击group时的result：" + result);
                    } else if (type.equals("org")) {
                        result = httpManager.searchInfoByName(key, 0);
//                        Log.i("info", "当点击org时的result：" + result);
                    } else {
                        result = "404";
                    }
                }
                isFirstFunction = isNum;
                if (result == null || result.equals("404")) {
                    mHandler.sendEmptyMessage(FIND_USER_FAIL);
                } else {
                    mHandler.sendEmptyMessage(FIND_USER_SUCCESS);
                }
            }
        }).start();
    }

    private void initListener() {
        titleBar.setTitleClickListener(this);
        mResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                           @Override
                                           public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                                               mId = findUserInfos.get(position).getId();
                                               Intent toInfor = new Intent(UIUtils.getContext(), FriendInformationActivity.class);
                                               toInfor.putExtra("from", "FriendResActivity");
                                               //机构 个人
                                               toInfor.putExtra("id", findUserInfos.get(position).getId());
                                               toInfor.putExtra("spaceId", findUserInfos.get(position).getSpaceId());
                                               //群组
                                               toInfor.putExtra("uuid", findUserInfos.get(position).getUserInfo().getTaggetId());

                                               toInfor.putExtra("icon", findUserInfos.get(position).getUserInfo().getUserIcon());
                                               toInfor.putExtra("type", type);
                                               toInfor.putExtra("isFriend", false);
                                               toInfor.putExtra("name", findUserInfos.get(position).getUserInfo().getName());
                                               toInfor.putExtra("code", findUserInfos.get(position).getUserInfo().getUserCode());

//                                               Log.i("info", "id=" + findUserInfos.get(position).getId());
//                                               Log.i("info", "spaceId=" + findUserInfos.get(position).getSpaceId());
//                                               Log.i("info", "uuid=" + findUserInfos.get(position).getUserInfo().getTaggetId());
                                               startActivity(toInfor);
                                           }
                                       }
        );
    }


    /*
    * 6，11，18位数字调用第一个接口
      别的位数或者汉字的话用模糊
      true 执行第一种查询
      fasle执行第二种
    * */
    private boolean isNum(String key) {
        boolean ret = true;
        int len = key.length();
        if (len == 6 || len == 11 || len == 18) {
            ret = true;
        } else {
            ret = false;
            return ret;
        }

        for (int i = 0; i < key.length(); i++) {
            char a = key.charAt(i);
            if (a < '0' || a > '9') {
                ret = false;
                break;
            }
        }
        return ret;
    }

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return findUserInfos.size();
        }

        @Override
        public Object getItem(int i) {
            return findUserInfos.get(i);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        class ViewHolder {
            TextView tvName, tvCode;
            ShapeImageView imageIcon;
        }


        @Override
        public View getView(int i, View v, ViewGroup vg) {
            ViewHolder holder = null;
            if (v == null) {
                v = LayoutInflater.from(mContext).inflate(R.layout.user_item, null);
                holder = new ViewHolder();
                holder.tvName = (TextView) v.findViewById(R.id.user_name);
                holder.imageIcon = (ShapeImageView) v.findViewById(R.id.user_icon);
                holder.tvCode = (TextView) v.findViewById(R.id.user_code);
                v.setTag(holder);
            } else {
                holder = (ViewHolder) v.getTag();
            }

            UserMessage message = findUserInfos.get(i);
            if (message != null && message.getUserInfo() != null) {
                holder.tvName.setText(message.getUserInfo().getName());
                holder.tvCode.setText(message.getUserInfo().getUserCode());
            }
            int utype = 0;
            if (type.equals("person")) {
                utype = 0;
                holder.imageIcon.setBackgroundResource(R.mipmap.ic_geren_xuanren);
            } else if (type.equals("group")) {
                utype = 1;
                holder.imageIcon.setBackgroundResource(R.mipmap.ic_qunzu_xuanren);
            } else if (type.equals("org")) {
                utype = 2;
                holder.imageIcon.setBackgroundResource(R.mipmap.ic_jigoumoren_wangpan);
            }
            holder.imageIcon.setTag(findUserInfos.get(i).getId());
            if (type.equals("person") || type.equals("org")) {
                setHeadIcon(findUserInfos.get(i).getId(), utype, holder.imageIcon);
            } else if (type.equals("group")) {
                Picasso.with(FriendResActivity.this).load(findUserInfos.get(i).getUserInfo().getUserIcon()).placeholder(R.mipmap.ic_qunzu_xuanren).error(R.mipmap.ic_qunzu_xuanren).config(Bitmap.Config.RGB_565).into(holder.imageIcon);
            }

            return v;
        }

        private void setHeadIcon(final String userId, final int type, final ShapeImageView icon) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final String url = httpManager.getUserIconUrl(userId);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            String tag = (String) icon.getTag();
                            if (tag.equals(userId)) {
                                setImage(url, type, icon);
                            }
                        }
                    });
                }
            }).start();
        }

        private void setImage(String iconUrl, int type, ShapeImageView icon) {
            //ChatType  0 单聊，1 群聊，2 空间(群组)
            if (type == 0) {
                if (iconUrl == null || iconUrl.equals("")) {
                    icon.setImageResource(R.mipmap.ic_geren_xuanren);
                } else {
                    Picasso.with(mContext).load(iconUrl).resize((int) mContext.getResources().getDimension(R.dimen.view_43),
                            (int) mContext.getResources().getDimension(R.dimen.view_43))
                            .placeholder(R.mipmap.ic_geren_xuanren).error(R.mipmap.ic_geren_xuanren).config(Bitmap.Config.RGB_565).into(icon);
                }
            } else if (type == 2) {
                if (iconUrl == null || iconUrl.equals("")) {
                    icon.setImageResource(R.mipmap.ic_jigoumoren_wangpan);
                } else {
                    Picasso.with(mContext).load(iconUrl).resize((int) mContext.getResources().getDimension(R.dimen.view_43),
                            (int) mContext.getResources().getDimension(R.dimen.view_43))
                            .placeholder(R.mipmap.ic_jigoumoren_wangpan).error(R.mipmap.ic_jigoumoren_wangpan).config(Bitmap.Config.RGB_565).into(icon);
                }
            } else if (type == 1) {
                if (iconUrl == null || iconUrl.equals("")) {
                    icon.setImageResource(R.mipmap.ic_qunzu_xuanren);
                } else {
                    Picasso.with(mContext).load(iconUrl).resize((int) mContext.getResources().getDimension(R.dimen.view_43),
                            (int) mContext.getResources().getDimension(R.dimen.view_43))
                            .placeholder(R.mipmap.ic_qunzu_xuanren).error(R.mipmap.ic_qunzu_xuanren).config(Bitmap.Config.RGB_565).into(icon);
                }
            }
        }
    }

    @Override
    public void leftClick() {
        finish();
    }

    @Override
    public void rightClick() {

    }

    @Override
    public void titleClick() {

    }

    @Override
    public void innerleftClick() {

    }

    @Override
    public void innerRightClick() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("FriendResActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("FriendResActivity");
        MobclickAgent.onPause(this);
    }
}

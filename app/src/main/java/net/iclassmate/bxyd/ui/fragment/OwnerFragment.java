package net.iclassmate.bxyd.ui.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.adapter.owner.OwnerAdapter;
import net.iclassmate.bxyd.bean.owner.Information;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.ui.activitys.index.WebBxActivity;
import net.iclassmate.bxyd.ui.activitys.owner.CutActivity;
import net.iclassmate.bxyd.ui.activitys.owner.HomePageActivity;
import net.iclassmate.bxyd.ui.activitys.owner.JiGouInformationActivity;
import net.iclassmate.bxyd.ui.activitys.owner.OwnerInformationActivity;
import net.iclassmate.bxyd.ui.activitys.owner.OwnerSaveActivity;
import net.iclassmate.bxyd.ui.activitys.owner.SettingActivity;
import net.iclassmate.bxyd.ui.activitys.study.StudySpaceActivity;
import net.iclassmate.bxyd.utils.JsonUtils;
import net.iclassmate.bxyd.utils.NetWorkUtils;
import net.iclassmate.bxyd.view.CircleImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class OwnerFragment extends Fragment implements View.OnClickListener {
    private View view;
    private ListView lv;

    private int[] img = {R.mipmap.ic_homepage, R.mipmap.ic_follow, R.mipmap.ic_collect, R.mipmap.ic_dingdan_wode, R.mipmap.ic_set};
    private String[] str = {"我的主页", "我的关注", "我的收藏", "我的订单", "设置"};

    private int[] jimg = {R.mipmap.ic_homepage, R.mipmap.ic_follow, R.mipmap.ic_collect, R.mipmap.ic_dingdan_wode, R.mipmap.ic_set};
    private String[] jstr = {"我的主页", "我的关注", "我的收藏", "我的订单", "设置"};

    private List<Map<String, Object>> mapList;
    private OwnerAdapter ownerAdapter;
    private ImageView owner_iv_ownerinformation;
    private CircleImageView owner_iv_head;
    private TextView owner_tv_name;
    private SharedPreferences sharedPreferences;
    private String userId, userType;
    private Information info;
    private byte[] b;
    private int cut_byte;

    public OwnerFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_owner, container, false);
        initView();
        userId = sharedPreferences.getString(Constant.ID_USER, "");

        userType = sharedPreferences.getString(Constant.ID_USERTYPE, "");
        if (userType == null || userType.equals("")) {
            userType = "1";
        }
        mapList = getData();
        ownerAdapter = new OwnerAdapter(getActivity(), mapList);
        lv.setAdapter(ownerAdapter);
        lv.setOnItemClickListener(itemClickListener);

        IntentFilter filter = new IntentFilter(CutActivity.action);
        getActivity().registerReceiver(broadcastReceiver, filter);

        getUserNameAndIcon(userId);
        return view;
    }

    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int type = intent.getIntExtra("type", 0);
            if (type == 101) {
                String newName = intent.getStringExtra("newName");
                owner_tv_name.setText(newName);
            } else {
                b = intent.getByteArrayExtra("byte");
                cut_byte = intent.getIntExtra("cut_byte", 1);
//            Log.i("info", "修改之后头像的byte[]：" + b);
                if (!"".equals(b) || !"null".equals(b) || b != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                    owner_iv_head.setImageBitmap(bitmap);
                }
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    public List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;
        if (userType.contains("0")) {
            for (int i = 0; i < jimg.length; i++) {
                map = new HashMap<String, Object>();
                map.put("img", jimg[i]);
                map.put("text", jstr[i]);
                list.add(map);
            }
        } else if (userType.contains("1")) {
            for (int i = 0; i < img.length; i++) {
                map = new HashMap<String, Object>();
                map.put("img", img[i]);
                map.put("text", str[i]);
                list.add(map);
            }
        }
        return list;
    }

    public void initView() {
        lv = (ListView) view.findViewById(R.id.owner_lv);
        owner_iv_ownerinformation = (ImageView) view.findViewById(R.id.owner_iv_ownerinformation);
        owner_tv_name = (TextView) view.findViewById(R.id.owner_tv_name);
        owner_iv_head = (CircleImageView) view.findViewById(R.id.owner_iv_head);

        owner_iv_ownerinformation.setOnClickListener(ivClickListener);
        owner_iv_head.setOnClickListener(ivClickListener);

        sharedPreferences = getActivity().getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString(Constant.USER_NAME, "");
        owner_tv_name.setText(userName);
    }

    public ImageView.OnClickListener ivClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!NetWorkUtils.isNetworkAvailable(getActivity())) {
                Toast.makeText(getActivity(), "请检查网络连接!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (userType.equals("") || userId.equals("") || info == null) {
                Toast.makeText(getActivity(), "请重新登录APP", Toast.LENGTH_SHORT).show();
                return;
            }
            if (userType.contains("0")) {
                Intent intent = new Intent(getActivity(), JiGouInformationActivity.class);
                if (cut_byte == 1) {
                    if (b != null || !"".equals(b) || !"null".equals(b)) {
                        intent.putExtra("byte", b);
                        intent.putExtra("type", 1);
                    }
                } else {
                    intent.putExtra("icon", info.getUserInfo().getIcon());
                    intent.putExtra("type", 2);
                }
                startActivity(intent);
            } else if (userType.contains("1")) {
                Intent intent = new Intent(getActivity(), OwnerInformationActivity.class);
                if (cut_byte == 1) {
                    if (b != null || !"".equals(b) || !"null".equals(b)) {
                        intent.putExtra("byte", b);
                        intent.putExtra("type", 1);
                    }
                } else {
                    intent.putExtra("icon", info.getUserInfo().getIcon());
                    intent.putExtra("type", 2);
                }
                startActivity(intent);
            }
        }
    };

    public ListView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = null;
            switch (position) {
                case 0:
                    intent = new Intent(getActivity(), HomePageActivity.class);
                    startActivity(intent);
                    break;
                case 1:
                    intent = new Intent(getActivity(), StudySpaceActivity.class);
                    startActivity(intent);
                    break;
                case 2:
                    Intent intent2 = new Intent(getActivity(), OwnerSaveActivity.class);
                    startActivity(intent2);
                    break;
                case 3:
                    Intent intent3 = new Intent(getActivity(), WebBxActivity.class);
                    intent3.putExtra("title", "活动订单");
                    intent3.putExtra("back", "我的");
                    String uid = sharedPreferences.getString(Constant.ID_USER, "");
                    String url = String.format(Constant.BX_OWNER_ACTIVITY, uid);
                    intent3.putExtra("url", url);
                    intent3.putExtra("type", "owner");
                    startActivity(intent3);
                    break;
                case 4:
                    Intent intent4 = new Intent(getActivity(), SettingActivity.class);
                    startActivity(intent4);
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {

    }

    public void getUserNameAndIcon(final String userId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                execute(userId);
            }
        }).start();
    }

    public void execute(String userId) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(Constant.GETUSERINFO_URL + userId + "?needIcon=true")
                .build();
        Log.i("info", "获取用户信息url:" + Constant.GETUSERINFO_URL + userId + "?needIcon=true");
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("info", "获取用户信息失败：" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message msg = new Message();
                msg.obj = response.body().string();
                msg.what = response.code();
                mHandler.sendMessage(msg);
            }
        });
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 200:
                    String information = (String) msg.obj;
                    info = JsonUtils.StartInformationJson(information);
                    if (info.getUserInfo().getName() != null && !info.getUserInfo().getName().equals("") && !info.getUserInfo().getName().equals("null")) {
                        owner_tv_name.setText(info.getUserInfo().getName());
                    } else {
                        owner_tv_name.setText("");
                    }
                    Log.i("info", "用户头像的地址：" + info.getUserInfo().getIcon());
                    String userType = sharedPreferences.getString(Constant.ID_USERTYPE, "0");
                    String userIcon = info.getUserInfo().getIcon();
                    if (!userIcon.equals("") && !userIcon.equals("null") && userIcon != null) {
                        userIcon = userIcon.substring(0, userIcon.indexOf("@"));
                    }
                    if (userType.equals("0")) {
                        Picasso.with(getActivity())
                                .load(userIcon)
                                .placeholder(R.mipmap.ic_head_jigou_wode)
                                .into(owner_iv_head);
                    } else if (userType.equals("1")) {
                        Picasso.with(getActivity())
                                .load(userIcon)
                                .placeholder(R.mipmap.ic_head_wode)
                                .into(owner_iv_head);
                    }
                    break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("OwnerFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("OwnerFragment");
    }
}
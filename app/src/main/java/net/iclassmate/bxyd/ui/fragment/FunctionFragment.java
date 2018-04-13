package net.iclassmate.bxyd.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.adapter.teachlearn.JiGouDiskAdapter;
import net.iclassmate.bxyd.bean.JGName;
import net.iclassmate.bxyd.bean.UserInFo;
import net.iclassmate.bxyd.bean.netdisk.OtherDisk;
import net.iclassmate.bxyd.bean.netdisk.Responses;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.ui.activitys.teachlearn.NetDisk2Activity;
import net.iclassmate.bxyd.utils.JsonUtils;
import net.iclassmate.bxyd.view.FullListView;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class FunctionFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    //    private ImageView iv_souweike,iv_saoyisao,iv_wangpan,iv_introduce;
    private View mView;
    private TextView fragment_function_usedSpace;
    private RelativeLayout fragment_function_rl_mynetdisk;
    private List<Object> JGlist;
    private List<Object> QZlist;
    private List<Responses> responsesList;
    private List<JGName> admin, adminqz;
    private JiGouDiskAdapter jadapter, qadapter;
    private FullListView jg_flv, qz_flv;
    private SharedPreferences sharedPreferences;
    private String UserId, spaceId;
    private long usedSpace;
    private OtherDisk otherDisk;
    private RelativeLayout fragment_function_qz, fragment_function_jg;
    private SwipeRefreshLayout srl;
    private static final int REFRESH_COMPLETE = 0x10;
    private boolean haveSpace;


    public FunctionFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_function, container, false);
        initView();
        return mView;
    }

    //从SharedPreference中取出需要的UserId；
    public void getUserId() {
        sharedPreferences = getActivity().getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        UserId = sharedPreferences.getString(Constant.ID_USER, "");
        Log.i("info", "用户的UserId=" + UserId);
        openThread(UserId);
    }

    //开启工作线程，通过UserId获取SpaceId;
    public void openThread(final String userId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    getSpaceId(userId);
                    getOtherDisk();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //get请求，通过url+UserId拼接的接口okhttp请求spaceId;
    public void getSpaceId(String userId) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Constant.GETSPACEID_URL + userId)
                .get()
                .build();
        Log.i("info", "请求spaceId的URL=" + Constant.GETSPACEID_URL + userId);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
//                Log.i("info", "请求SpaceId失败：" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Message message = new Message();
                    message.what = 1;
                    message.obj = response.body().string();
                    mHandler.sendMessage(message);
                }
            }
        });
    }

    //通过Handler接收工作线程传来的数据（SpaceId）
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_COMPLETE:
                    try {
                        if (spaceId != null) {
                            responsesList.clear();
                            JGlist.clear();
                            QZlist.clear();
                            getSpaceId(UserId);
                            getOtherDisk();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    srl.setRefreshing(false);
                    break;
                case 2:
                    String string = (String) msg.obj;
                    otherDisk = JsonUtils.StartOtherDiskJson(string);
//                    Log.i("info", "获取所有网盘解析数据：" + otherDisk.toString());
                    responsesList = otherDisk.getResponsesList();
                    if (null == responsesList) {
                        return;
                    }
                    for (int i = 0; i < responsesList.size(); i++) {
                        String type = responsesList.get(i).getType();
                        if (type.equals("group")) {
                            QZlist.add(responsesList.get(i));
                            qadapter.setType(2);

                            JGName jname = new JGName();
                            jname.setName(responsesList.get(i).getName());
                            List<String> AdminUserId = new ArrayList<>();
                            List<String> AdminName = new ArrayList<>();
                            for (int j = 0; j < responsesList.get(i).getAdministartors().getListList().size(); j++) {
                                AdminUserId.add(responsesList.get(i).getAdministartors().getListList().get(j).getUserId());
                                AdminName.add(responsesList.get(i).getAdministartors().getListList().get(j).getUserName());
                            }
                            jname.setAdminName(AdminName);
                            jname.setUserId(AdminUserId);
                            adminqz.add(jname);

                        } else if (type.equals("org")) {
                            JGlist.add(responsesList.get(i));
                            jadapter.setType(1);

                            JGName jname = new JGName();
                            jname.setName(responsesList.get(i).getName());
                            List<String> AdminUserId = new ArrayList<>();
                            List<String> AdminName = new ArrayList<>();
                            for (int j = 0; j < responsesList.get(i).getAdministartors().getListList().size(); j++) {
                                AdminUserId.add(responsesList.get(i).getAdministartors().getListList().get(j).getUserId());
                                AdminName.add(responsesList.get(i).getAdministartors().getListList().get(j).getUserName());
                            }
                            jname.setAdminName(AdminName);
                            jname.setUserId(AdminUserId);
                            admin.add(jname);
                        }
                    }
                    if (JGlist.size() == 0) {
                        fragment_function_jg.setVisibility(View.GONE);
                    }
                    if (QZlist.size() == 0) {
                        fragment_function_qz.setVisibility(View.GONE);
                    }
//                    Log.i("info", "机构网盘的size:" + JGlist.size());
//                    Log.i("info", "群组网盘的size:" + QZlist.size());
//                    Log.i("info", "机构管理员的信息:" + admin.toString());
//                    Log.i("info", "群组管理员的信息:" + adminqz.toString());
                    jadapter.notifyDataSetChanged();
                    qadapter.notifyDataSetChanged();
                    break;
                case 1:
                    String str = (String) msg.obj;
                    UserInFo userInFo = JsonUtils.StartUserInfoJson(str);
                    spaceId = userInFo.getUuid();
                    usedSpace = userInFo.getUsedSpace();
                    if (spaceId != null) {
                        haveSpace = true;
                    }
                    String usedSize = getFileTime(usedSpace);
                    Log.i("info", "用户的spaceId=" + spaceId + ",使用的usedSpace=" + usedSpace + ";转换的userSize=" + usedSize);
                    fragment_function_usedSpace.setText("已使用" + usedSize + "\t共100GB容量");
                    sharedPreferences.edit().putString(Constant.ID_SPACE, spaceId).apply();
                    break;
            }
        }
    };

    public String getFileTime(long time) {
        DecimalFormat df = new DecimalFormat("#.00");
        if (time < 1024 && time >= 0) {
            return time + "B";
        } else if (time >= 1024 && time < 1024 * 1024) {
            return df.format(time / 1024.0) + "KB";
        } else if (time >= 1024 * 1024 && time < 1024 * 1024 * 1024) {
            return df.format(time / 1024.0 / 1024.0) + "MB";
        } else if (time >= 1024 * 1024 * 1024) {
            return df.format(time / 1024.0 / 1024.0 / 1024.0) + "GB";
        } else {
            return " ";
        }
    }

    //get请求，通过url+UserId+page+(page-size)拼接接口请求机构网盘，群组网盘信息
    public void getOtherDisk() throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Constant.GETOTHERDISK_URL + "page=1&page-size=9999&userid=" + UserId)
                .get()
                .build();
        Log.i("info", "请求机构，群组网盘的Url=" + Constant.GETOTHERDISK_URL + "page=1&page-size=9999&userid=" + UserId);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("info", "请求机构，群组网盘数据失败=" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Message message = new Message();
                    message.what = 2;
                    message.obj = response.body().string();
                    mHandler.sendMessage(message);
                }
            }
        });
    }


    //初始化View
    public void initView() {
        srl = (SwipeRefreshLayout) mView.findViewById(R.id.fragment_function_srl);
        srl.setOnRefreshListener(this);
        srl.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        fragment_function_usedSpace = (TextView) mView.findViewById(R.id.fragment_function_usedSpace);
        fragment_function_jg = (RelativeLayout) mView.findViewById(R.id.fragment_function_jg);
        fragment_function_qz = (RelativeLayout) mView.findViewById(R.id.fragment_function_qz);
        jg_flv = (FullListView) mView.findViewById(R.id.fragment_function_jg_flv);
        qz_flv = (FullListView) mView.findViewById(R.id.fragment_function_qz_flv);
        fragment_function_rl_mynetdisk = (RelativeLayout) mView.findViewById(R.id.fragment_function_rl_mynetdisk);

        JGlist = new ArrayList<Object>();
        QZlist = new ArrayList<Object>();
        responsesList = new ArrayList<>();
        admin = new ArrayList<>();
        adminqz = new ArrayList<>();
        jadapter = new JiGouDiskAdapter(getActivity(), JGlist, admin, adminqz);
        qadapter = new JiGouDiskAdapter(getActivity(), QZlist, admin, adminqz);

        jg_flv.setAdapter(jadapter);
        qz_flv.setAdapter(qadapter);

        fragment_function_rl_mynetdisk.setOnClickListener(this);
        jadapter.setImgClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isAdmin = false;
                boolean isJgDisk = false;
                Responses response = (Responses) v.getTag();
                String spaceId = response.getUuid();
                if (!haveSpace) {
                    Log.i("info", "spaceId为空，无法请求到网盘数据");
                    Toast.makeText(getActivity(), "服务器繁忙，请稍后重试...", Toast.LENGTH_SHORT).show();
                    return;
                }
                kk:
                for (int i = 0; i < admin.size(); i++) {
                    if (response.getName().equals(admin.get(i).getName())) {
                        for (int j = 0; j < admin.get(i).getUserId().size(); j++) {
                            if (admin.get(i).getUserId().contains(UserId)) {
                                isAdmin = true;
                                isJgDisk = true;
                                break kk;
                            }
                        }
                    }
                }
                Intent intent = new Intent(getActivity(), NetDisk2Activity.class);
                intent.putExtra("spaceId", spaceId);
                intent.putExtra("isAdmin", isAdmin);
                intent.putExtra("isJgDisk", isJgDisk);
                intent.putExtra("type", "org");
                intent.putExtra("ShortName", response.getName());
                startActivity(intent);
            }
        });
        qadapter.setImgClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isAdmin = false;
                Responses response = (Responses) v.getTag();
                String spaceId = response.getUuid();
                if (/*spaceId == null || spaceId.equals("") || spaceId.equals("null")*/!haveSpace) {
                    Log.i("info", "spaceId为空，无法请求到网盘数据");
                    Toast.makeText(getActivity(), "服务器繁忙，请稍后重试...", Toast.LENGTH_SHORT).show();
                    return;
                }
                kk:
                for (int i = 0; i < adminqz.size(); i++) {
                    if (response.getName().equals(adminqz.get(i).getName())) {
                        for (int j = 0; j < adminqz.get(i).getUserId().size(); j++) {
                            if (adminqz.get(i).getUserId().contains(UserId)) {
                                isAdmin = true;
                                break kk;
                            }
                        }
                    }
                }
                Intent intent = new Intent(getActivity(), NetDisk2Activity.class);
                intent.putExtra("spaceId", spaceId);
                intent.putExtra("isAdmin", isAdmin);
                intent.putExtra("isJgDisk", true);
                intent.putExtra("type", "group");
                intent.putExtra("ShortName", response.getName());
                startActivity(intent);
            }
        });

        getUserId();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_function_rl_mynetdisk:
                if (/*spaceId == null || spaceId.equals("") || spaceId.equals("null")*/!haveSpace) {
                    Log.i("info", "spaceId为空，无法请求到网盘数据");
                    Toast.makeText(getActivity(), "服务器繁忙，请稍后重试...", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent_mynetdisk = new Intent(getActivity(), NetDisk2Activity.class);
                    intent_mynetdisk.putExtra("spaceId", spaceId);
                    intent_mynetdisk.putExtra("isAdmin", true);
                    intent_mynetdisk.putExtra("isJgDisk", true);
                    intent_mynetdisk.putExtra("type", "owner");
                    intent_mynetdisk.putExtra("ShortName", "我的网盘");
                    startActivity(intent_mynetdisk);
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("FunctionFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("FunctionFragment");
    }

    @Override
    public void onRefresh() {
        mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 3500);
    }
}
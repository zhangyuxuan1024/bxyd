package net.iclassmate.bxyd.ui.activitys;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.jauker.widget.BadgeView;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import net.iclassmate.bxyd.BuildConfig;
import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.adapter.MainFragmentAdapter;
import net.iclassmate.bxyd.bean.owner.Update;
import net.iclassmate.bxyd.bean.study.FileDirList;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.ui.activitys.chat.SelectContactsActivity;
import net.iclassmate.bxyd.ui.activitys.constacts.AddFriendActivity;
import net.iclassmate.bxyd.ui.activitys.constacts.ContactsActivity;
import net.iclassmate.bxyd.ui.activitys.owner.CheckActivity;
import net.iclassmate.bxyd.ui.activitys.study.Scan2Activity;
import net.iclassmate.bxyd.ui.fragment.FunctionFragment;
import net.iclassmate.bxyd.ui.fragment.IndexFragment;
import net.iclassmate.bxyd.ui.fragment.MessageFragment;
import net.iclassmate.bxyd.ui.fragment.OwnerFragment;
import net.iclassmate.bxyd.ui.fragment.StudySpaceFragment;
import net.iclassmate.bxyd.utils.DataCallback;
import net.iclassmate.bxyd.utils.JsonUtils;
import net.iclassmate.bxyd.utils.UIUtils;
import net.iclassmate.bxyd.view.MainViewPager;
import net.iclassmate.bxyd.view.PopMenu;
import net.iclassmate.bxyd.view.TitleBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.rong.imlib.RongIMClient;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends FragmentActivity implements View.OnClickListener, ViewPager.OnPageChangeListener, DataCallback {
    private Context mContext;
    private List<Fragment> fragments;
    private ImageView image_index, image_message, image_space, image_function, image_owner, img_tongxunlu_guide, img_iknow;
    private BadgeView badgeView;
    private MainViewPager main_viewPager;
    private MainFragmentAdapter mainFragmentAdapter;
    private TitleBar titleBar;
    private PopMenu popMenu;
    private String nowversionName;

    public static final int REQ_CHAT = 1;

    private String spaceid, userid;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int unreadcount;

    private IWXAPI api;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(mContext, "您的该帐号，已经在其他客户端登录！您已被迫下线，请及时修改密码！", Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    String str = (String) msg.obj;
                    if (str == null || str.equals("") || str.equals("null")) {
                    } else {
                        Update update = JsonUtils.StartUpdateJson(str);
//                        Log.i("info", "获取最新版本：" + update.getVersion() + ",str=" + str);
                        if (null != update && !update.getVersion().equals(nowversionName)) {
                            Intent intent_update = new Intent(MainActivity.this, CheckActivity.class);
                            intent_update.putExtra("version", update.getVersion());
                            intent_update.putExtra("size", update.getSize());
                            intent_update.putExtra("description", update.getUpdateDesc());
                            intent_update.putExtra("url", update.getUrl());
                            startActivity(intent_update);
                        } else {

                        }
                    }
                    break;
                //查找文件夹
                case 3:
                    String ret = (String) msg.obj;
                    findFile(ret);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        mContext = this;
        nowversionName = "V" + BuildConfig.VERSION_NAME;
        initView();
        RongIMClient.getInstance().setConnectionStatusListener(new MyConnectionStatusListener());
        checkVersionInfo(nowversionName);

        //查找和创建文件夹
        sharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCES, MODE_PRIVATE);
        spaceid = sharedPreferences.getString(Constant.ID_SPACE, "");
        userid = sharedPreferences.getString(Constant.ID_USER, "");
        if (spaceid != null && !spaceid.equals("")) {
            findAndcreateFile();
        }
    }

    private void initView() {
        api = WXAPIFactory.createWXAPI(this, Constant.PAY_WX_ID, false);
        api.registerApp(Constant.PAY_WX_ID);


        img_iknow = (ImageView) findViewById(R.id.img_iknow);
        img_iknow.setOnClickListener(this);
        img_tongxunlu_guide = (ImageView) findViewById(R.id.img_tongxunlu_guide);
        img_tongxunlu_guide.setOnClickListener(this);
        image_index = (ImageView) findViewById(R.id.main_iv_index);
        image_message = (ImageView) findViewById(R.id.main_iv_message);
        image_space = (ImageView) findViewById(R.id.main_iv_space);
        image_function = (ImageView) findViewById(R.id.main_iv_function);
        image_owner = (ImageView) findViewById(R.id.main_iv_owner);
        main_viewPager = (MainViewPager) findViewById(R.id.main_ViewPager);
        titleBar = (TitleBar) findViewById(R.id.main_title_bar);
        badgeView = (BadgeView) findViewById(R.id.badgeview_message);
        badgeView.setBadgeCount(unreadcount);
        badgeView.setOnClickListener(this);
        popMenu = new PopMenu(this);
//        popMenu.addItems(new String[]{"发起聊天"});
//        popMenu.addIcons(new int[]{R.mipmap.ic_liaotian});
        String[] items = {"发起聊天", "添加好友"};
        int[] icons = {R.mipmap.ic_liaotian, R.mipmap.ic_jiahaoyou_xiaoxi};
        popMenu.addItems(items);
        popMenu.addIcons(icons);
        popMenu.setOnItemClickListener(popmenuItemClickListener);

        main_viewPager.setNoScroll(true);
        mainTvSelected(0);

        image_index.setOnClickListener(this);
        image_message.setOnClickListener(this);
        image_space.setOnClickListener(this);
        image_function.setOnClickListener(this);
        image_owner.setOnClickListener(this);


        initFragments();
    }

    private void initFragments() {
        fragments = new ArrayList<Fragment>();
        fragments.add(new IndexFragment());
        MessageFragment messageFragment = new MessageFragment();
        messageFragment.setDataCallback(this);
        fragments.add(messageFragment);
        fragments.add(new FunctionFragment());
        fragments.add(new StudySpaceFragment());
        fragments.add(new OwnerFragment());
        mainFragmentAdapter = new MainFragmentAdapter(getSupportFragmentManager(), fragments);
        main_viewPager.setAdapter(mainFragmentAdapter);
        main_viewPager.setOnPageChangeListener(this);
        main_viewPager.setOffscreenPageLimit(4);
        main_viewPager.setCurrentItem(0);
    }

    public void mainTvSelected(int index) {
        image_index.setSelected(false);
        image_message.setSelected(false);
        image_space.setSelected(false);
        image_function.setSelected(false);
        image_owner.setSelected(false);
        if (index == 0) {
            image_index.setSelected(true);
            titleBar.setRightIcon(R.mipmap.ic_saoyisao_snow);
            titleBar.setInnerRightIconEmpty();
            titleBar.setLeftVisibility(View.INVISIBLE);
            titleBar.setTitle(getResources().getString(R.string.main_title_index));
            titleBar.setTitleClickListener(new TitleBar.TitleOnClickListener() {

                @Override
                public void leftClick() {

                }

                @Override
                public void rightClick() {
                    if (Build.VERSION.SDK_INT < 23) {
                        Intent intent2 = new Intent(MainActivity.this, Scan2Activity.class);
                        startActivity(intent2);
                    } else {
                        AndPermission.with(MainActivity.this)
                                .requestCode(1012)
                                .permission(Manifest.permission.CAMERA)
                                .send();
                    }
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
            });
        } else if (index == 1) {
            image_message.setSelected(true);
            titleBar.setTitle(getResources().getString(R.string.main_title_message));
            titleBar.setRightIcon(R.mipmap.ic_jiahao);
            titleBar.setInnerRightIcon(R.mipmap.ic_tongxunlu);
//            titleBar.setRightIcon(R.mipmap.ic_tongxunlu);
            titleBar.setTitleClickListener(new TitleBar.TitleOnClickListener() {
                @Override
                public void leftClick() {

                }

                @Override
                public void rightClick() {
                    popMenu.showAsDropDown(titleBar.getRightIcon());
//                    Intent toContacts = new Intent(UIUtils.getContext(), ContactsActivity.class);
//                    startActivityForResult(toContacts, REQ_CHAT);
                }

                @Override
                public void titleClick() {

                }

                @Override
                public void innerleftClick() {

                }

                @Override
                public void innerRightClick() {
                    Intent toContacts = new Intent(UIUtils.getContext(), ContactsActivity.class);
                    startActivityForResult(toContacts, REQ_CHAT);
                }
            });
        } else if (index == 2) {
            image_function.setSelected(true);
            titleBar.setTitle(getResources().getString(R.string.main_title_function_2));
            titleBar.setRightIconEmpty();
            titleBar.setInnerRightIconEmpty();
        } else if (index == 3) {
            image_space.setSelected(true);
            titleBar.setTitle(getResources().getString(R.string.main_title_space_2));
            titleBar.setRightIconEmpty();
            titleBar.setInnerRightIconEmpty();
        } else if (index == 4) {
            image_owner.setSelected(true);
            titleBar.setTitle(getResources().getString(R.string.main_title_owner));
            titleBar.setRightIconEmpty();
            titleBar.setInnerRightIconEmpty();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        AndPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults, listener);
    }

    private PermissionListener listener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode) {
            if (requestCode == 1012) {
                Intent intent2 = new Intent(MainActivity.this, Scan2Activity.class);
                startActivity(intent2);
            }
        }

        @Override
        public void onFailed(int requestCode) {
            if (requestCode == 1012) {
                Toast.makeText(MainActivity.this, "您未开放相机权限", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onClick(View v) {
        int id = v.getId();
        int index = 1;
        switch (id) {
            case R.id.img_iknow:
                img_iknow.setVisibility(View.GONE);
                img_tongxunlu_guide.setVisibility(View.GONE);
                index = 1;
                break;
            case R.id.main_iv_index:
                index = 0;
                break;
            case R.id.badgeview_message:
            case R.id.main_iv_message:
                boolean isFirstLogin = sharedPreferences.getBoolean("isFirstLogin", true);
                if (isFirstLogin == true) {
                    editor = sharedPreferences.edit();
                    img_iknow.setVisibility(View.VISIBLE);
                    img_tongxunlu_guide.setVisibility(View.VISIBLE);
                    editor.putBoolean("isFirstLogin", false);
                    editor.apply();
                } else if (isFirstLogin == false) {
                    img_iknow.setVisibility(View.GONE);
                    img_tongxunlu_guide.setVisibility(View.GONE);
                }
                index = 1;
                break;
            case R.id.main_iv_function:
                index = 2;
                break;
            case R.id.main_iv_space:
                index = 3;
                break;
            case R.id.main_iv_owner:
                index = 4;
                break;
        }
        main_viewPager.setCurrentItem(index);
        mainTvSelected(index);
    }

    public void setBadgeView(int count) {
        unreadcount = count;
        if (badgeView != null) {
            badgeView.setBadgeCount(count);
        }
    }


    // 弹出菜单监听器
    AdapterView.OnItemClickListener popmenuItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0) {
                Intent intent = new Intent(UIUtils.getContext(), SelectContactsActivity.class);
                intent.putExtra("from", "newGroup");
                startActivityForResult(intent, REQ_CHAT);
            } else if (position == 1) {
                Intent intent = new Intent(UIUtils.getContext(), AddFriendActivity.class);
                startActivity(intent);
            }
            popMenu.dismiss();
        }
    };

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mainTvSelected(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int index = main_viewPager.getCurrentItem();
        if (index == 1) {
            if (resultCode == 1) {
                if (requestCode == REQ_CHAT) {
                    Fragment fragment1 = fragments.get(1);
                    if (fragment1 instanceof MessageFragment) {
                        MessageFragment fragment = (MessageFragment) fragment1;
                        fragment.getData(true);
                    }
                }
            } else if (resultCode == 2) {

            }
        } else {
            if (resultCode == RESULT_OK) {
                if (requestCode == 1011) {
                    Bundle bundle = data.getExtras();
                    if (bundle == null) {
                        return;
                    }
                    if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                        String result = bundle.getString(CodeUtils.RESULT_STRING);
                        Toast.makeText(MainActivity.this, "扫描的结果是：" + result, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    @Override
    public void sendData(Object object) {
        if (object != null && object instanceof Integer) {
            int count = (int) object;
            badgeView.setBadgeCount(count);
        }
    }


    private class MyConnectionStatusListener implements RongIMClient.ConnectionStatusListener {
        @Override
        public void onChanged(ConnectionStatus connectionStatus) {
            //  Log.i("login", "状态码=" + connectionStatus);
            switch (connectionStatus) {
                case CONNECTED://连接成功。
                    break;
                case DISCONNECTED://断开连接。
                    break;
                case CONNECTING://连接中。
                    break;
                case NETWORK_UNAVAILABLE://网络不可用。
                    break;
                case KICKED_OFFLINE_BY_OTHER_CLIENT://用户账户在其他设备登录，本机会被踢掉线
                    RongIMClient.getInstance().disconnect();
                    RongIMClient.getInstance().logout();
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    mContext.startActivity(intent);
                    finish();
                    mHandler.sendEmptyMessage(1);
                    break;
            }
        }
    }

    public void checkVersionInfo(final String nowversionName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                execute2(nowversionName);
            }
        }).start();
    }

    public void execute2(String nowversionName) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(Constant.UPDATE_URL + nowversionName + "?product=" + Constant.UPDATE_VERSIONNAME)
                .build();
//        Log.i("info", "请求新版本的url:" + Constant.UPDATE_URL + nowversionName + "?product="+Constant.UPDATE_VERSIONNAMW);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
//                Log.i("info", "检测版本失败：" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Message msg = new Message();
                    msg.obj = response.body().string();
                    msg.what = 2;
                    mHandler.sendMessage(msg);
                }
            }
        });
    }

    private void findAndcreateFile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = String.format(Constant.STUDY_FIND_FILE, spaceid, "我的缓存");
//                Log.i("info", "查看文件是否存在路径=" + url);
                final Request request = new Request.Builder()
                        .url(url)
                        .build();

                int cacheSize = 10 * 1024 * 1024;
                OkHttpClient.Builder builder = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS);
                OkHttpClient client = builder.build();
                client.newCall(request).enqueue(new Callback() {
                                                    @Override
                                                    public void onFailure(Call call, IOException e) {
//                                                        Log.i("info", "请求数据失败" + e.getMessage());
                                                        // mHandler.sendEmptyMessage(404);
                                                    }

                                                    @Override
                                                    public void onResponse(Call call, final Response response) throws IOException {
                                                        if (response.isSuccessful()) {
//                                                            Log.i("info", "请求数据,返回成功！");
                                                            Message message = new Message();
                                                            message.what = 3;
                                                            message.obj = response.body().string();
                                                            mHandler.sendMessage(message);
//                                                            Log.i("info", "查看文件夹=" + response.code());
                                                        } else {
                                                            //mHandler.sendEmptyMessage(404);
                                                        }
                                                        response.close();
                                                    }

                                                }

                );
            }
        }).start();
    }

    //查找文件夹
    private void findFile(String ret) {
        boolean isFind = false;
        try {
            JSONObject json = new JSONObject(ret);
            JSONArray array = json.getJSONArray("fileDirList");
            for (int i = 0; i < array.length(); i++) {
                FileDirList fileDirList = new FileDirList();
                JSONObject jsonObject = array.optJSONObject(i);
                if (jsonObject != null) {
                    fileDirList.parserJson(jsonObject);
                    if (fileDirList.getType() == 1) {
                        isFind = true;
                        break;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            isFind = false;
        }

        if (!isFind) {
            createFile();
        }
    }

    //创建文件夹
    private void createFile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                String json = "";
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("fullPath", "");
                    jsonObject.put("shortName", getResources().getString(R.string.my_cache));
                    jsonObject.put("userId", userid);
                    jsonObject.put("spaceId", spaceid);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                json = jsonObject.toString();
//                Log.i("info", "创建文件夹参数=" + json);
                RequestBody body = RequestBody.create(JSON, json);
                OkHttpClient.Builder builder = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS);
                OkHttpClient client = builder.build();
                Request request = new Request.Builder()
                        .url(Constant.STUDY_CREATE_FILE)
                        .post(body)
                        .build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    Message message = new Message();
                    message.what = 4;
                    message.arg1 = response.code();
                    message.obj = response.body().string();
                    mHandler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}

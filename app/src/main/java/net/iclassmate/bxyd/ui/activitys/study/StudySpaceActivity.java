package net.iclassmate.bxyd.ui.activitys.study;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.adapter.study.StudySpaceAdapter;
import net.iclassmate.bxyd.bean.study.OriginBulletinInfo;
import net.iclassmate.bxyd.bean.study.Resources;
import net.iclassmate.bxyd.bean.study.StudyMessageItem;
import net.iclassmate.bxyd.bean.study.StudyMessageList;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.ui.activitys.owner.AttentionActivity;
import net.iclassmate.bxyd.ui.activitys.owner.HomePageActivity;
import net.iclassmate.bxyd.utils.NetWorkUtils;
import net.iclassmate.bxyd.utils.OpenFile;
import net.iclassmate.bxyd.utils.UIUtils;
import net.iclassmate.bxyd.view.FullListView;
import net.iclassmate.bxyd.view.pullrefreshview.MyListener;
import net.iclassmate.bxyd.view.pullrefreshview.PullToRefreshLayout;
import net.iclassmate.bxyd.view.pullrefreshview.PullableScrollView;
import net.iclassmate.bxyd.view.study.ShapeImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class StudySpaceActivity extends FragmentActivity implements View.OnClickListener, View.OnTouchListener {
    private StudySpaceAdapter adapter;
    private FullListView listView;
    private List<StudyMessageItem> listMessage;
    private Context mContext;
    private StudyMessageList smlist;
    private OkHttpClient client;

    private TextView tv_back, tv_title;
    private ImageView img_back, img_right, img_anim, img_release;
    private AnimationDrawable anim;
    public static final int REQ_RELEASE = 1;
    public static final int REQ_COMMENT_COUNT = 2;
    private static final int REQ_DEL = 3;
    private String spaceid, userid;
    private int pages, pageSize, total;
    private SharedPreferences sharedPreferences;
    private PullToRefreshLayout pull;
    private PullableScrollView pullableScrollView;

    private long last_touch_time;
    private boolean loadfinish;
    private int lastY;
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            anim.stop();
            img_anim.setBackgroundColor(Color.parseColor("#efefef"));
            if (what == 1) {
                img_anim.setVisibility(View.GONE);
                String result = (String) msg.obj;
                loadfinish = true;
                try {
                    result = removeBOM(result);
                    JSONObject json = new JSONObject(result);
                    smlist = new StudyMessageList();
                    smlist.parserJson(json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (smlist == null || smlist.getList() == null || smlist.getList().size() < 1) {
                    if (pages == 1 && listMessage.size() < 1) {
                        img_anim.setBackgroundColor(Color.parseColor("#efefef"));
                        img_anim.setVisibility(View.VISIBLE);
                        if (smlist == null || smlist.getList() == null) {
                            img_anim.setImageResource(R.mipmap.img_yemianjiazaishibai);
                        } else {
                            img_anim.setImageResource(R.mipmap.ic_noresult_zhuyedongtai);
                        }
                        pull.refreshFinish(PullToRefreshLayout.SUCCEED);
                        pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                        return;
                    }
                }
                List<StudyMessageItem> smlistList = smlist.getList();
                if (pages == 1) {
                    listMessage.clear();
                    pull.refreshFinish(PullToRefreshLayout.SUCCEED);
                } else {
                    pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
                listMessage.addAll(smlistList);
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                total = smlist.getTotal();
            } else if (what == 2) {
                int code = msg.arg1;
                if (code == 200) {
                    String ret = (String) msg.obj;
                    try {
                        JSONObject object = new JSONObject(ret);
                        spaceid = object.optString("uuid");
                        if (spaceid != null && !spaceid.equals("")) {
                            sharedPreferences.edit().putString(Constant.ID_SPACE, spaceid).commit();
                            loadData();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                loadFail(false);
            } else if (what == 404) {
                loadFail(true);
                //监听Scrollview是否停止
            } else if (what == 201) {
                if (lastY == pullableScrollView.getScrollY()) {
                    adapter.notifyDataSetChanged();
                } else {
                    if (lastY == 0) {
                        adapter.notifyDataSetChanged();
                    }
                    lastY = pullableScrollView.getScrollY();
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_space);
        Intent intent = getIntent();
        spaceid = intent.getStringExtra(Constant.ID_SPACE);
        userid = intent.getStringExtra(Constant.ID_USER);
        sharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        if (spaceid == null || spaceid.equals("")) {
            spaceid = sharedPreferences.getString(Constant.ID_SPACE, "");
            userid = sharedPreferences.getString(Constant.ID_USER, "");
        }
        initView();
        initEvent();
        if (spaceid == null || spaceid.equals("") || spaceid.equals("-1")) {
            loadSpaceId();
        } else {
            loadData();
        }
    }

    private void loadData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    execute(userid, spaceid, pages, pageSize);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initView() {
        pull = (PullToRefreshLayout) findViewById(R.id.pull);
        pullableScrollView = (PullableScrollView) findViewById(R.id.study_space_scrollview);
        pull.setOnRefreshListener(new MyListener());
        pull.setOnTouchListener(this);

        listView = (FullListView) findViewById(R.id.study_space_listview);
        listMessage = new ArrayList<>();

        tv_back = (TextView) findViewById(R.id.study_message_left_tv);
        tv_back.setOnClickListener(this);
        tv_back.setText("我的");
        img_back = (ImageView) findViewById(R.id.study_message_back);
        img_back.setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.study_message_title_tv);
        tv_title.setText("我的关注");
        img_right = (ImageView) findViewById(R.id.study_message_right_icon);
        img_right.setImageResource(R.mipmap.ic_guanzhu_zhuyedongtai);
        img_right.setOnClickListener(this);
        img_right.setVisibility(View.VISIBLE);
        img_release = (ImageView) findViewById(R.id.img_study_release);
        img_release.setOnClickListener(this);
        img_anim = (ImageView) findViewById(R.id.img_anim);
        anim = (AnimationDrawable) img_anim.getBackground();
        anim.start();
        mContext = this;
        pages = 1;
        pageSize = 20;
        loadfinish = true;

        adapter = new StudySpaceAdapter(mContext, listMessage);
        listView.setAdapter(adapter);
        adapter.setIsCanClickLike(true);
        adapter.setUserType(1);

        adapter.setImgClickComent(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = (int) view.getTag();
                StudyMessageItem message = listMessage.get(index);
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("msg", message);
                intent.putExtra("index", index);
                startActivityForResult(intent, REQ_COMMENT_COUNT);
            }
        });
        adapter.setImgClickLike(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = (int) view.getTag();
                StudyMessageItem message = listMessage.get(index);
                int like = message.getLiked();
                boolean isLike = message.isClickLiked();
                isLike = !isLike;
                if (isLike) {
                    Toast.makeText(mContext, "已点赞", Toast.LENGTH_SHORT).show();
                    like++;
                } else {
                    Toast.makeText(mContext, "取消点赞", Toast.LENGTH_SHORT).show();
                    like--;
                }
                addLiked(message.getId());
                message.setIsClickLiked(isLike);
                message.setLiked(like);
                listMessage.set(index, message);
                adapter.notifyDataSetChanged();
            }
        });
        adapter.setImgClickShare(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = (int) view.getTag();
                StudyMessageItem message = listMessage.get(index);
                Intent intent = new Intent(mContext, StudyWindowActivity.class);
                List<String> list = new ArrayList<String>();
                String uid = sharedPreferences.getString(Constant.ID_USER, "");
                String uid2 = message.getCreateBy().getId();
                String uid3 = "";
                String type = message.getBulletinType();
                if (type.equalsIgnoreCase("FORWARD")) {
                    if (message.getOriginBulletinInfo() != null && message.getOriginBulletinInfo().getCreateBy() != null) {
                        uid3 = message.getOriginBulletinInfo().getCreateBy().getId();
                    }
                }
                if (!uid.equals(uid2) && !uid.equals(uid3)) {
                    if (type != null) {
                        if (type != null) {
                            type = type.toUpperCase();
                            boolean add = true;
                            if (type.equals("FORWARD")) {
                                if (message.getOriginBulletinInfo() == null || message.getOriginBulletinInfo().getCreateBy() == null) {
                                    add = false;
                                }
                            }
                            if (add) {
                                list.add("转发到主页");
                            }
                            list.add("转发给好友");
                            list.add("收藏");
                            list.add("举报");
                        }
                    }
                } else {
                    list.add("转发给好友");
                    list.add("删除动态");
                }
                intent.putStringArrayListExtra("list", (ArrayList<String>) list);
                intent.putExtra("msg", message);
                startActivityForResult(intent, REQ_RELEASE);
            }
        });
        adapter.setImgClickHead(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = (int) view.getTag();
                StudyMessageItem item = listMessage.get(index);
                Intent intent = new Intent(mContext, HomePageActivity.class);
                intent.putExtra(Constant.ID_USER, item.getCreateBy().getId());
                intent.putExtra(Constant.ID_SPACE, item.getSpaceId());
                startActivityForResult(intent, REQ_DEL);
            }
        });
        adapter.setImgClickHomePage(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = (int) v.getTag();
                StudyMessageItem item = listMessage.get(index);
                OriginBulletinInfo info = item.getOriginBulletinInfo();
                if (info != null && info.getCreateBy() != null) {
                    Intent intent = new Intent(mContext, HomePageActivity.class);
                    intent.putExtra(Constant.ID_USER, info.getCreateBy().getId());
                    intent.putExtra(Constant.ID_SPACE, "-1");
                    startActivityForResult(intent, REQ_RELEASE);
                }
            }
        });
        adapter.setGridClick(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int index = (int) adapterView.getTag();
                StudyMessageItem item = listMessage.get(index);
                if (item != null) {
                    String type = getFileType(item, i);
                    if (type.equals("图片")) {
                        openPic(item, i);
                    } else {
                        openFile(item, i);
                    }
                }
            }
        });

    }

    //点赞
    private void addLiked(final String bulletinId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                String json = "";
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("bulletinId", bulletinId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                json = jsonObject.toString();
                RequestBody body = RequestBody.create(JSON, json);
                OkHttpClient.Builder builder = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS);
                client = builder.build();
                Request request = new Request.Builder()
                        .url(String.format(Constant.STUDY_ADD_LIKE, userid))
                        .post(body)
                        .build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    Message message = new Message();
                    message.what = 3;
                    message.arg1 = response.code();
                    message.obj = response.body().string();
                    mHandler.sendMessage(message);
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void openPic(StudyMessageItem item, int index) {
        String type = item.getBulletinType().toUpperCase();
        List<Object> list = new ArrayList<>();
        if (type.equals("ORIGIN")) {
            if (item.getList() != null && item.getList().size() > 0) {
                List<Resources> list1 = item.getList();
                list.addAll(list1);
            }
        } else if (type.equals("FORWARD")) {
            if (item.getOriginBulletinInfo() != null && item.getOriginBulletinInfo().getList() != null
                    && item.getOriginBulletinInfo().getList().size() > 0) {
                List<Resources> list1 = item.getOriginBulletinInfo().getList();
                list.addAll(list1);
            }
        }

        Intent intent = new Intent(mContext, LookPicActivity.class);
        intent.putExtra("type", 1);
        intent.putExtra("index", index);
        intent.putExtra("list", (Serializable) list);
        startActivity(intent);
    }

    //打开文件
    private void openFile(StudyMessageItem item, int index) {
        String id = "", type = "", name = "";
        type = item.getBulletinType().toUpperCase();
        if (type.equals("ORIGIN")) {
            if (item.getList() != null && item.getList().size() > index) {
                id = item.getList().get(index).getId();
                type = item.getList().get(index).getType();
                name = item.getList().get(index).getName().toLowerCase();
            }
        } else if (type.equals("FORWARD")) {
            if (item.getOriginBulletinInfo() != null && item.getOriginBulletinInfo().getList() != null && item.getOriginBulletinInfo().getList().size() > index) {
                id = item.getOriginBulletinInfo().getList().get(index).getId();
                type = item.getOriginBulletinInfo().getList().get(index).getType();
                name = item.getOriginBulletinInfo().getList().get(index).getName().toLowerCase();
            }
        }
        if (id == "" || type == "") {
            return;
        }
        OpenFile.openFile(id, name, 1, mContext);
    }

    private String getFileType(StudyMessageItem item, int index) {
        String type = item.getBulletinType().toUpperCase();
        if (type.equals("ORIGIN")) {
            if (item.getList() != null && item.getList().size() > index) {
                type = item.getList().get(index).getType();
            }
        } else if (type.equals("FORWARD")) {
            if (item.getOriginBulletinInfo() != null && item.getOriginBulletinInfo().getList() != null
                    && item.getOriginBulletinInfo().getList().size() > index) {
                type = item.getOriginBulletinInfo().getList().get(index).getType();
            }
        }
        return type;
    }

    private void initEvent() {
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.study_message_linear:
//                intent = new Intent(mContext, MessageAlertActivity.class);
//                startActivity(intent);
                break;
            case R.id.study_message_left_tv:
            case R.id.study_message_back:
                this.finish();
                break;
            case R.id.study_message_right_icon:
                intent = new Intent(mContext, AttentionActivity.class);
                startActivity(intent);
                break;
            case R.id.img_study_release:
                intent = new Intent(mContext, ReleaseActivity.class);
                startActivityForResult(intent, REQ_RELEASE);
                break;
            case R.id.study_user_img:
                intent = new Intent(mContext, HomePageActivity.class);
                String sid = sharedPreferences.getString(Constant.ID_SPACE, "");
                String uid = sharedPreferences.getString(Constant.ID_USER, "");
                intent.putExtra(Constant.ID_SPACE, sid);
                intent.putExtra(Constant.ID_USER, uid);
                startActivityForResult(intent, REQ_DEL);
                break;
        }
    }

    public void execute(String userid, String spaceId, int page, int page_size) {
        String url = String.format(Constant.STUDY_RELEASE_LIST, userid, spaceId, page, page_size);
//        Log.i("info", "请求学习圈主页路径=" + url);
        final Request request = new Request.Builder()
                .url(url)
                .build();

        //client = new OkHttpClient();
        int cacheSize = 10 * 1024 * 1024;
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .cache(new Cache(getSDPath(), cacheSize));
        client = builder.build();
        client.newCall(request).enqueue(new Callback() {
                                            @Override
                                            public void onFailure(Call call, IOException e) {
                                                //Log.i("info", "请求数据失败" + e.getMessage());
                                                mHandler.sendEmptyMessage(404);
                                            }

                                            @Override
                                            public void onResponse(Call call, final Response response) throws IOException {
                                                if (response.isSuccessful()) {
                                                    //Log.i("info", "请求数据,返回成功！");
                                                    Message message = new Message();
                                                    message.what = 1;
                                                    message.obj = response.body().string();
                                                    mHandler.sendMessage(message);
                                                } else {
                                                    mHandler.sendEmptyMessage(404);
                                                }
                                                response.close();
                                            }
                                        }

        );
    }

    public File getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        File file = null;
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
            file = new File(sdDir + "/" + Constant.APP_DIR_NAME);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
        return file;
    }

    public String removeBOM(String data) {
        if (TextUtils.isEmpty(data)) {
            return data;
        }
        if (data.startsWith("\ufeff")) {
            return data.substring(1);
        } else {
            return data;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_RELEASE) {
                pages = 1;
                loadData();
            } else if (requestCode == REQ_COMMENT_COUNT) {
                Bundle bundle = data.getExtras();
                boolean load = bundle.getBoolean("load");
                if (load) {
                    int index = bundle.getInt("index");
                    int count = bundle.getInt("count");
                    int liked = bundle.getInt("liked");
                    boolean flag = bundle.getBoolean("click");
                    StudyMessageItem item = listMessage.get(index);
                    item.setCommented(count);
                    item.setLiked(liked);
                    item.setIsClickLiked(flag);
                    listMessage.set(index, item);
                    adapter.notifyDataSetChanged();
                }
            } else if (requestCode == REQ_DEL) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    boolean isdel = bundle.getBoolean("del", false);
                    if (isdel) {
                        img_anim.setVisibility(View.VISIBLE);
                        anim.start();
                        pages = 1;
                        loadData();
                    }
                }
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (NetWorkUtils.isNetworkAvailable(UIUtils.getContext()) && System.currentTimeMillis() - last_touch_time > 3000 && loadfinish) {
            if (pull.getCurrentState() == PullToRefreshLayout.REFRESHING) {
                pages = 1;
                loadData();
                loadfinish = false;
                last_touch_time = System.currentTimeMillis();
            } else if (pull.getCurrentState() == PullToRefreshLayout.LOADING) {
                if (listMessage.size() == 0) {
                    pages = 1;
                    loadData();
                    loadfinish = false;
                    last_touch_time = System.currentTimeMillis();
                } else if (listMessage.size() >= total) {
                    pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                } else if (listMessage.size() < total) {
                    pages++;
                    loadData();
                    loadfinish = false;
                    last_touch_time = System.currentTimeMillis();
                }
            }
        }
        return true;
    }

    //获取spaceid
    private void loadSpaceId() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(Constant.GETSPACEID_URL + userid)
                        .get()
                        .build();
//                Log.i("info", "spaceid=" + Constant.GETSPACEID_URL + userid);
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //mHandler.sendEmptyMessage(404);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            Message message = new Message();
                            message.what = 2;
                            message.arg1 = response.code();
                            message.obj = response.body().string();
                            mHandler.sendMessage(message);
                        } else {
                            mHandler.sendEmptyMessage(404);
                        }
                        response.close();
                    }
                });
            }
        }).start();
    }

    //加载失败
    private void loadFail(boolean flag) {
        if (flag || spaceid == null || spaceid.equals("")) {
            pull.refreshFinish(PullToRefreshLayout.FAIL);
            pull.loadmoreFinish(PullToRefreshLayout.FAIL);
            img_anim.setBackgroundColor(Color.parseColor("#efefef"));
            img_anim.setVisibility(View.VISIBLE);
            img_anim.setImageResource(R.mipmap.img_yemianjiazaishibai);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            listView = null;
            adapter = null;
            listMessage.clear();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("StudySpaceActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("StudySpaceActivity");
        MobclickAgent.onPause(this);
    }
}



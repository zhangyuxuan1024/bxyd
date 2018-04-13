package net.iclassmate.bxyd.ui.activitys.study;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.adapter.study.CommentAdapter;
import net.iclassmate.bxyd.adapter.study.StudySpaceAdapter;
import net.iclassmate.bxyd.bean.study.CommentMessage;
import net.iclassmate.bxyd.bean.study.CreateBy;
import net.iclassmate.bxyd.bean.study.Resources;
import net.iclassmate.bxyd.bean.study.StudyMessageItem;
import net.iclassmate.bxyd.bean.study.comment.CommentMessageItem;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.ui.activitys.owner.HomePageActivity;
import net.iclassmate.bxyd.utils.OpenFile;
import net.iclassmate.bxyd.view.FullListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CommentActivity extends FragmentActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private StudyMessageItem msg;
    private StudySpaceAdapter adapter;
    private FullListView listView;
    private TextView tv_back, tv_title;
    private ImageView img_back;
    private FullListView listComView;
    private CommentAdapter commentAdapter;

    private List<CommentMessageItem> listMes;
    private List<CommentMessageItem> listCom;
    private List<CommentMessageItem> listTar;
    private List<CommentMessageItem> listGreat;

    private TextView tv_com, tv_tar, tv_great;
    private ImageView img_com, img_like, img_anim, img_right;
    private AnimationDrawable anim;
    private int cur_type;
    private Context mContext;
    private boolean isClickLike;
    private OkHttpClient client;
    private CommentMessage comment;
    public static int REQ_COMMET = 1;
    public static int REQ_REPLY = 2;
    private static final int REQ_RELEASE = 3;
    private String userId;
    private int index, cur_liked, type_user;
    //判断评论是否加载成功
    private boolean isLoaded;
    private SharedPreferences sharedPreferences;
    private long last_click_time;
    private LinearLayout linearLayout_container;

    private List<StudyMessageItem> list;
    private ImageView img_no_exist;
    private View view_bottom;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg1) {
            int what = msg1.what;
            anim.stop();
            img_no_exist.setVisibility(View.GONE);
            img_anim.setBackgroundColor(Color.WHITE);
            img_anim.setVisibility(View.GONE);
            //请求评论列表
            if (what == 1) {
                String result = (String) msg1.obj;
                try {
                    JSONObject json = new JSONObject(result);
                    comment.parserJson(json);
                    setData(cur_type);
                    isLoaded = true;
                    img_anim.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }//请求动态详情
            } else if (what == 5) {
                linearLayout_container.setVisibility(View.VISIBLE);
                String result = (String) msg1.obj;
                try {
                    JSONObject object = new JSONObject(result);
                    msg.parserJson(object);
                    initView();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (what == 404) {
                img_anim.setVisibility(View.VISIBLE);
                img_anim.setImageResource(R.mipmap.img_jiazaishibai);
            } else if (what == 403) {
                img_no_exist.setVisibility(View.VISIBLE);
                view_bottom.setVisibility(View.GONE);
                img_right.setVisibility(View.INVISIBLE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        final Intent intent = getIntent();
        msg = (StudyMessageItem) intent.getSerializableExtra("msg");
        index = intent.getIntExtra("index", 0);
        list = new ArrayList<>();
        adapter = new StudySpaceAdapter(this, list);
        adapter.setMsgType(1);

        listView = (FullListView) findViewById(R.id.study_sapce_msg_listview);
        listView.setAdapter(adapter);

        tv_back = (TextView) findViewById(R.id.study_message_left_tv);
        tv_back.setOnClickListener(this);
        tv_back.setText("返回");
        img_back = (ImageView) findViewById(R.id.study_message_back);
        img_back.setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.study_message_title_tv);
        tv_title.setVisibility(View.INVISIBLE);
        img_right = (ImageView) findViewById(R.id.study_message_right_icon);
        img_no_exist = (ImageView) findViewById(R.id.img_no_exist);
        img_no_exist.setVisibility(View.GONE);
        view_bottom = findViewById(R.id.layout_bottom);
        view_bottom.setVisibility(View.VISIBLE);

        listMes = new ArrayList<>();
        listCom = new ArrayList<>();
        listTar = new ArrayList<>();
        listGreat = new ArrayList<>();
        listComView = (FullListView) findViewById(R.id.comment_listview);
        commentAdapter = new CommentAdapter(this, listMes);
        listComView.setAdapter(commentAdapter);
        listComView.setOnItemClickListener(this);

        tv_com = (TextView) findViewById(R.id.comment_tv_com);
        tv_tar = (TextView) findViewById(R.id.comment_tv_tra);
        tv_great = (TextView) findViewById(R.id.comment_tv_great);
        img_com = (ImageView) findViewById(R.id.comment_com_img);
        img_like = (ImageView) findViewById(R.id.comment_like_img);

        img_com.setOnClickListener(this);
        img_like.setOnClickListener(this);
        img_anim = (ImageView) findViewById(R.id.img_anim);
        anim = (AnimationDrawable) img_anim.getBackground();
        anim.start();
        linearLayout_container = (LinearLayout) findViewById(R.id.save_container_linear);
        linearLayout_container.setVisibility(View.INVISIBLE);
        mContext = this;
        sharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        userId = sharedPreferences.getString(Constant.ID_USER, "");
        String type = sharedPreferences.getString(Constant.ID_USERTYPE, "1");
        try {
            type_user = Integer.parseInt(type);
        } catch (Exception e) {
            type_user = 1;
        }
        adapter.setUserType(type_user);

        comment = new CommentMessage();
        cur_type = 0;
        isLoaded = false;
        if (msg != null) {
            anim.stop();
            img_anim.setVisibility(View.GONE);
            linearLayout_container.setVisibility(View.VISIBLE);
            initView();
        } else {
            msg = new StudyMessageItem();
            String bullId = intent.getStringExtra("id");
            img_right.setVisibility(View.VISIBLE);
            img_right.setImageResource(R.mipmap.ic_xiaobaidian);
            img_right.setOnClickListener(this);
            loadData(bullId);
        }

        adapter.setGridClick(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (msg != null) {
                    String type = getFileType(msg, i);
                    if (type.equals("图片")) {
                        openPic(msg, i);
                    } else {
                        openFile(msg, i);
                    }
                }
            }
        });

        commentAdapter.setOnHeadClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag instanceof CommentMessageItem) {
                    CommentMessageItem item = (CommentMessageItem) tag;
                    Intent intent1 = new Intent(mContext, HomePageActivity.class);
                    intent1.putExtra(Constant.ID_USER, item.getCreateBy().getId());
                    intent1.putExtra(Constant.ID_SPACE, "-1");
                    intent.putExtra(Constant.HOME_PAGE_TITLE, item.getCreateBy().getName());
                    intent.putExtra(Constant.ID_USERTYPE, Constant.TYPE_PRIVATE);
                    startActivity(intent1);
                }
            }
        });
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

    //加载发布详情
    private void loadData(final String bullId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = String.format(Constant.MESSAGE_GET_BULLETIN_DETIAL, userId, bullId);
//                Log.i("info", "发布详情=" + url);
                Request request = new Request.Builder()
                        .url(url)
                        .build();

                OkHttpClient.Builder builder = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS);
                client = builder.build();
                client.newCall(request).enqueue(new Callback() {
                                                    @Override
                                                    public void onFailure(Call call, IOException e) {
//                                                Log.i("info", "请求数据失败" + e.getMessage());
                                                        mHandler.sendEmptyMessage(404);
                                                    }

                                                    @Override
                                                    public void onResponse(Call call, final Response response) throws IOException {
                                                        int code = response.code();
                                                        if (response.isSuccessful()) {
                                                            Message message = new Message();
                                                            message.what = 5;
                                                            message.obj = response.body().string();
                                                            mHandler.sendMessage(message);
                                                        } else if (code == 404) {
                                                            mHandler.sendEmptyMessage(403);
                                                        } else {
                                                            mHandler.sendEmptyMessage(404);
                                                        }
                                                    }
                                                }

                );
            }
        }).start();
    }

    private void initView() {
        isClickLike = msg.isClickLiked();
        if (isClickLike) {
            img_like.setImageResource(R.mipmap.ic_great_clicked);
        } else {
            img_like.setImageResource(R.mipmap.ic_great);
        }
        cur_liked = msg.getLiked();
        tv_com.setText("评论" + msg.getCommented());
        tv_tar.setText("转发" + msg.getForwarded());
        tv_great.setText("赞" + msg.getLiked());

        list.add(msg);
        adapter.notifyDataSetChanged();

        loadComentData();
    }

    private void loadComentData() {
        final String url = String.format(Constant.STUDY_GET_COMMENT, msg.getId(), 1, 0);
//        Log.i("info", "评论列表=" + url);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    execute(url);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void execute(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        //client = new OkHttpClient();
        int cacheSize = 10 * 1024 * 1024;
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS);
        client = builder.build();
        client.newCall(request).enqueue(new Callback() {
                                            @Override
                                            public void onFailure(Call call, IOException e) {
//                                                Log.i("info", "请求数据失败" + e.getMessage());
                                                //mHandler.sendEmptyMessage(404);
                                            }

                                            @Override
                                            public void onResponse(Call call, final Response response) throws IOException {
                                                if (response.isSuccessful()) {
//                                                    Log.i("info", "请求数据,返回成功！");
                                                    Message message = new Message();
                                                    message.what = 1;
                                                    message.obj = response.body().string();
                                                    mHandler.sendMessage(message);
                                                } else {
                                                    // mHandler.sendEmptyMessage(404);
                                                }
                                                response.close();
                                            }
                                        }

        );
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.study_message_back:
            case R.id.study_message_left_tv:
                close();
                break;
            case R.id.comment_tv_com:
                setData(0);
                break;
            case R.id.comment_tv_tra:
                setData(1);
                break;
            case R.id.comment_tv_great:
                setData(2);
                break;
            case R.id.comment_com_img:
                if (msg != null && msg.getCreateBy() != null) {
                    intent = new Intent(mContext, ReplyActivity.class);
                    intent.putExtra("reply", "评论" + msg.getCreateBy().getName());
                    intent.putExtra("msg", msg);
                    startActivityForResult(intent, REQ_COMMET);
                }
                break;
            case R.id.comment_like_img:
                if (System.currentTimeMillis() - last_click_time < 3000) {
                    return;
                }
                last_click_time = System.currentTimeMillis();

                isClickLike = !isClickLike;
                if (isClickLike) {
                    img_like.setImageResource(R.mipmap.ic_great_clicked);
                    Toast.makeText(mContext, "已点赞", Toast.LENGTH_SHORT).show();
                    addLiked(msg.getId());
                    cur_liked++;
                    tv_great.setText("赞" + cur_liked);
                } else {
                    img_like.setImageResource(R.mipmap.ic_great);
                    Toast.makeText(mContext, "取消点赞", Toast.LENGTH_SHORT).show();
                    addLiked(msg.getId());
                    cur_liked--;
                    tv_great.setText("赞" + cur_liked);
                }
                break;
            case R.id.study_message_right_icon:
                intent = new Intent(mContext, StudyWindowActivity.class);
                List<String> list = new ArrayList<String>();
                String uid = sharedPreferences.getString(Constant.ID_USER, "");
                String uid2 = msg.getCreateBy().getId();
                String uid3 = "";
                String type = msg.getBulletinType();
                if (type.equalsIgnoreCase("FORWARD")) {
                    if (msg.getOriginBulletinInfo() != null && msg.getOriginBulletinInfo().getCreateBy() != null) {
                        uid3 = msg.getOriginBulletinInfo().getCreateBy().getId();
                    }
                }
                if (!uid.equals(uid2) && !uid.equals(uid3)) {
                    if (type != null) {
                        if (type != null) {
                            type = type.toUpperCase();
                            boolean add = true;
                            if (type.equals("FORWARD")) {
                                if (msg.getOriginBulletinInfo() == null || msg.getOriginBulletinInfo().getCreateBy() == null) {
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
                intent.putExtra("msg", msg);
                startActivityForResult(intent, REQ_RELEASE);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        close();
    }

    private void setData(int type) {
        cur_type = type;
        listMes.clear();
        listCom.clear();
        listTar.clear();
        listGreat.clear();

        if (comment != null && comment.getList() != null && comment.getList().size() > 0) {
            List<CommentMessageItem> list = comment.getList();
            for (int i = 0; i < list.size(); i++) {
                CommentMessageItem item = list.get(i);
                String rid = item.getReplyTo().getId();
                String anmae = item.getCreateBy().getName();
                String rname = item.getReplyTo().getName();
                if (msg.getCreateBy() != null && rid != null && !rid.equals(msg.getCreateBy().getId())) {
                    String str = anmae + "回复" + rname;
                    if (!item.getCreateBy().getName().contains(str)) {
                        item.getCreateBy().setName(str);
                        list.set(i, item);
                    }
                }
            }
            listCom.addAll(list);
        }
        for (int i = 0; i < 5; i++) {
            CommentMessageItem cm = new CommentMessageItem();
            CreateBy by = new CreateBy();
            cm.setCreateBy(by);
            cm.getCreateBy().setName("李四");
            cm.setContent("不错哦，转走了！");
            cm.setCreatedOn(System.currentTimeMillis() + "");
            listTar.add(cm);
        }

        for (int i = 0; i < 10; i++) {
            CommentMessageItem cm = new CommentMessageItem();
            CreateBy by = new CreateBy();
            cm.setCreateBy(by);
            cm.getCreateBy().setName("王五");
            cm.setContent("");
            cm.setCreatedOn(System.currentTimeMillis() + "");
            listGreat.add(cm);
        }

        if (type == 0) {
            listMes.addAll(listCom);
        } else if (type == 1) {
            listMes.addAll(listTar);
        } else if (type == 2) {
            listMes.addAll(listGreat);
        }
        commentAdapter.notifyDataSetChanged();
        tv_com.setText("评论" + listCom.size());
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (cur_type == 0) {
            Intent intent = new Intent(mContext, StudyWindowActivity.class);
            List<String> list = new ArrayList<>();
            CommentMessageItem item = listMes.get(i);
            String id = item.getCreateBy().getId();
            if (msg.getCreateBy().getId().equals(userId)) {
                if (id.equals(userId)) {
                    list.add("删除");
                } else {
                    list.add("回复");
                    list.add("删除");
                }
            } else {
                if (id.equals(userId)) {
                    list.add("删除");
                } else {
                    list.add("回复");
                }
            }
            intent.putExtra("msg", msg);
            intent.putExtra("del", item);
            intent.putExtra("rid", item.getCreateBy().getId());
            intent.putStringArrayListExtra("list", (ArrayList<String>) list);
            startActivityForResult(intent, REQ_COMMET);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_COMMET) {
                isLoaded = false;
                if (msg != null) {
                    loadComentData();
                }
            }
        }
    }

    private void close() {
        Intent intent = new Intent();
        intent.putExtra("load", isLoaded);
        intent.putExtra("index", index);
        intent.putExtra("count", listCom.size());
        intent.putExtra("liked", cur_liked);
        intent.putExtra("click", isClickLike);
        setResult(RESULT_OK, intent);
        this.finish();
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
                        .url(String.format(Constant.STUDY_ADD_LIKE, userId))
                        .post(body)
                        .build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    Message message = new Message();
                    message.what = 2;
                    message.arg1 = response.code();
                    message.obj = response.body().string();
                    mHandler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        ).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("CommentActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("CommentActivity");
        MobclickAgent.onPause(this);
    }
}
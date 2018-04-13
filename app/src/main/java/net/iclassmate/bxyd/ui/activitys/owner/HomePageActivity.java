package net.iclassmate.bxyd.ui.activitys.owner;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.adapter.study.StudySpaceAdapter;
import net.iclassmate.bxyd.bean.study.OriginBulletinInfo;
import net.iclassmate.bxyd.bean.study.Resources;
import net.iclassmate.bxyd.bean.study.StudyMessageItem;
import net.iclassmate.bxyd.bean.study.StudyMessageList;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.ui.activitys.study.CommentActivity;
import net.iclassmate.bxyd.ui.activitys.study.LookPicActivity;
import net.iclassmate.bxyd.ui.activitys.study.ReleaseActivity;
import net.iclassmate.bxyd.ui.activitys.study.StudyWindowActivity;
import net.iclassmate.bxyd.utils.HttpManager;
import net.iclassmate.bxyd.utils.NetWorkUtils;
import net.iclassmate.bxyd.utils.OpenFile;
import net.iclassmate.bxyd.utils.UIUtils;
import net.iclassmate.bxyd.view.FullListView;
import net.iclassmate.bxyd.view.pullrefreshview.MyListener;
import net.iclassmate.bxyd.view.pullrefreshview.PullToRefreshLayout;
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

public class HomePageActivity extends FragmentActivity implements View.OnClickListener, View.OnTouchListener {
    private LinearLayout message_linear;
    private StudySpaceAdapter adapter;
    private FullListView listView;
    private List<StudyMessageItem> listMessage;
    private Context mContext;
    private StudyMessageList smlist;
    private OkHttpClient client;

    private TextView tv_back, tv_title;
    private ImageView img_back, img_right, img_anim, img_care, img_bg, img_organization, img_release;
    private ShapeImageView img_user;
    private AnimationDrawable anim;
    public static final int REQ_RELEASE = 1;
    public static final int REQ_COMMENT_COUNT = 2;
    private static final int REQ_DEL = 3;
    //当前点击的项
    private int cur_index;

    private String spaceid, userid, title;
    private SharedPreferences sharedPreferences;
    private boolean isdel, isCare, isCareCanClick;
    private int pages, pageSize, total;
    private PullToRefreshLayout pull;
    private long last_click_time;
    private int type_home;
    private HttpManager httpManager;
    private boolean visitHomepage = true;   //是否允许别人进入自己主页
    private boolean isHasCare;//是否添加关注

    private long last_touch_time;
    private boolean loadfinish;
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
                        img_anim.setVisibility(View.VISIBLE);
                        if (smlist == null || smlist.getList() == null) {
                            img_anim.setImageResource(R.mipmap.img_yemianjiazaishibai);
                        } else {
                            img_anim.setImageResource(R.mipmap.ic_noresult_zhuyedongtai);
                        }
                        pull.refreshFinish(PullToRefreshLayout.SUCCEED);
                        pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                        if (type_home == Constant.TYPE_GROUP) {
                            img_anim.setVisibility(View.INVISIBLE);
                        }
                        return;
                    }
                    if (pages == 1 && listMessage.size() < 1) {
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
                adapter.notifyDataSetChanged();
                total = smlist.getTotal();
                if (title == null || title.equals("")) {
                    tv_title.setText(listMessage.get(0).getCreateBy().getName());
                }
                if (type_home == Constant.TYPE_GROUP) {
                    img_anim.setVisibility(View.INVISIBLE);
                }
            } else if (what == 2) {
                int code = msg.arg1;
                if (code == 200) {
                    String ret = (String) msg.obj;
                    try {
                        JSONObject object = new JSONObject(ret);
                        spaceid = object.getString("uuid");

                        String uid = sharedPreferences.getString(Constant.ID_USER, "");
                        String sid = sharedPreferences.getString(Constant.ID_SPACE, "");
                        if (!uid.equals(userid)) {
                            getSpaceRelation(sid, spaceid);
                            img_right.setVisibility(View.INVISIBLE);
                            if (visitHomepage) {
                                img_care.setVisibility(View.VISIBLE);
                            } else {
                                img_care.setVisibility(View.INVISIBLE);
                            }
                        }
                        loadData();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                loadFail(false);
            } else if (what == 3) {
                int code = msg.arg1;
                if (code == 200) {
                    String ret = (String) msg.obj;
                    try {
                        JSONObject object = new JSONObject(ret);
                        isCare = object.getBoolean("isRelated");
                        if (isCare) {
                            img_care.setImageResource(R.mipmap.bt_quxiaoguanzhu_zhuye);
                        } else {
                            img_care.setImageResource(R.mipmap.bt_jiaguanzhu_zhuye);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if (what == 4) {
                int code = msg.arg1;
                String ret = (String) msg.obj;
                if (code == 200) {
                    Toast.makeText(mContext, "已关注！", Toast.LENGTH_SHORT).show();
                    img_care.setImageResource(R.mipmap.bt_quxiaoguanzhu_zhuye);
                    isCare = true;
                } else if (code == 400) {
                    Toast.makeText(mContext, "本空间设置权限不许关注！", Toast.LENGTH_SHORT).show();
                }
            } else if (what == 5) {
                isCareCanClick = true;
                int code = msg.arg1;
                String ret = (String) msg.obj;
                if (code == 200) {
                    Toast.makeText(mContext, "已取消关注！", Toast.LENGTH_SHORT).show();
                    img_care.setImageResource(R.mipmap.bt_jiaguanzhu_zhuye);
                    isCare = false;
                }
            } else if (what == 404) {
                loadFail(true);
            }

            if (type_home == Constant.TYPE_GROUP) {
                img_anim.setVisibility(View.INVISIBLE);
            }
        }
    };

    private void loadFail(boolean flag) {
        if (flag || spaceid == null || spaceid.equals("")) {
            img_anim.setBackgroundColor(Color.parseColor("#efefef"));
            img_anim.setVisibility(View.VISIBLE);
            if (type_home == Constant.TYPE_PRIVATE) {
                img_anim.setImageResource(R.mipmap.img_yemianjiazaishibai);
            }
        }
        if (type_home == Constant.TYPE_GROUP) {
            img_anim.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        loadfinish = true;

        Intent intent = getIntent();
        spaceid = intent.getStringExtra(Constant.ID_SPACE);
        userid = intent.getStringExtra(Constant.ID_USER);
        type_home = intent.getIntExtra(Constant.ID_USERTYPE, 1);
        title = intent.getStringExtra(Constant.HOME_PAGE_TITLE);
        visitHomepage = intent.getBooleanExtra("visitHomepage", true);
        isHasCare = intent.getBooleanExtra(Constant.IS_CONCERN, true);
        sharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        if (type_home == 1) {
            if (spaceid == null || spaceid.equals("") || userid == null || userid.equals("")) {
                spaceid = sharedPreferences.getString(Constant.ID_SPACE, "");
                userid = sharedPreferences.getString(Constant.ID_USER, "");
                String type = sharedPreferences.getString(Constant.ID_USERTYPE, "1");
                try {
                    type_home = Integer.parseInt(type);
                } catch (Exception e) {
                    type_home = 1;
                }
            }
        } else if (type_home == 2) {
            userid = sharedPreferences.getString(Constant.ID_USER, "");
        }

        initView();
        initEvent();
        String uid = sharedPreferences.getString(Constant.ID_USER, "");
        String sid = sharedPreferences.getString(Constant.ID_SPACE, "");
        if (isHasCare) {
            img_care.setImageResource(R.mipmap.bt_quxiaoguanzhu_zhuye);
        } else {
            img_care.setImageResource(R.mipmap.bt_jiaguanzhu_zhuye);
        }
        if (type_home == 1 || type_home == 0) {
            if (!uid.equals(userid)) {
                img_right.setVisibility(View.INVISIBLE);
                tv_back.setText("返回");
                if (visitHomepage) {
                    img_care.setVisibility(View.VISIBLE);
                } else {
                    img_care.setVisibility(View.INVISIBLE);
                }
            } else {
                img_right.setVisibility(View.VISIBLE);
                img_care.setVisibility(View.INVISIBLE);
                tv_back.setText("我的");
            }
            if (type_home == Constant.TYPE_GROUP) {
                img_anim.setVisibility(View.INVISIBLE);
            }
        } else if (type_home == 2) {
            img_right.setVisibility(View.VISIBLE);
            tv_back.setText("返回");
            if (visitHomepage) {
                img_care.setVisibility(View.VISIBLE);
            } else {
                img_care.setVisibility(View.INVISIBLE);
            }
        }
        if (!spaceid.equals("-1") && !spaceid.equals("")) {
            if (!uid.equals(userid)) {
                getSpaceRelation(sid, spaceid);
            }
            loadData();
        } else {
            loadSpaceId();
        }

    }

    private void loadSpaceId() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(Constant.GETSPACEID_URL + userid)
                        .get()
                        .build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //mHandler.sendEmptyMessage(404);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Message message = new Message();
                        message.what = 2;
                        message.arg1 = response.code();
                        message.obj = response.body().string();
                        mHandler.sendMessage(message);
                        if (response != null) {
                            response.close();
                        }
                    }
                });
            }
        }).start();
    }

    private void loadData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    execute(spaceid, pages, pageSize);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initView() {
        pull = (PullToRefreshLayout) findViewById(R.id.pull);
        pull.setOnRefreshListener(new MyListener());
        pull.setOnTouchListener(this);
        httpManager = new HttpManager();

        mContext = this;
        message_linear = (LinearLayout) findViewById(R.id.study_message_linear);
        //message_linear.setVisibility(View.GONE);
        listView = (FullListView) findViewById(R.id.study_sapce_listview);
        listMessage = new ArrayList<>();

        tv_back = (TextView) findViewById(R.id.study_message_left_tv);
        tv_back.setOnClickListener(this);
        img_back = (ImageView) findViewById(R.id.study_message_back);
        img_back.setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.study_message_title_tv);
        tv_title.setText("");
        img_right = (ImageView) findViewById(R.id.study_message_right_icon);
        img_right.setImageResource(R.mipmap.ic_edit);
        img_right.setOnClickListener(this);
        img_right.setVisibility(View.VISIBLE);
        img_release = (ImageView) findViewById(R.id.img_study_release);
        img_release.setOnClickListener(this);

        img_anim = (ImageView) findViewById(R.id.img_anim);
        anim = (AnimationDrawable) img_anim.getBackground();
        anim.start();
        img_user = (ShapeImageView) findViewById(R.id.study_user_img);
        img_bg = (ImageView) findViewById(R.id.img_home_title_bg);
        img_organization = (ImageView) findViewById(R.id.img_organization);

        //设置头像
        if (type_home == 0 || type_home == 1) {
            setHeadIcon(userid, type_home);
        } else if (type_home == 2) {
            setHeadIcon(spaceid, type_home);
        }
        if (title != null && !title.equals("")) {
            tv_title.setText(title);
        }

        isdel = false;
        isCareCanClick = true;
        pages = 1;
        pageSize = 20;
        img_care = (ImageView) findViewById(R.id.study_sapce_jia_img);
        img_care.setVisibility(View.INVISIBLE);
        img_care.setOnClickListener(this);

        adapter = new StudySpaceAdapter(mContext, listMessage);
        listView.setAdapter(adapter);
        adapter.setIsCanClickLike(true);
        adapter.setUserType(type_home);

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
                if (System.currentTimeMillis() - last_click_time < 3000) {
                    return;
                }
                last_click_time = System.currentTimeMillis();

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
                addLike(message.getId());
                message.setIsClickLiked(isLike);
                message.setLiked(like);
                listMessage.set(index, message);
                adapter.notifyDataSetChanged();
            }
        });
        adapter.setImgClickShare(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (int) view.getTag();
                StudyMessageItem item = listMessage.get(position);
                String id = item.getCreateBy().getId();
                Intent intent = new Intent(mContext, StudyWindowActivity.class);
                List<String> list = new ArrayList<String>();
                String uid = sharedPreferences.getString(Constant.ID_USER, "");
                if (uid != null && !uid.equals("") && id != null && !id.equals("")) {
                    if (uid.equals(id)) {
                        list.add("转发给好友");
                        list.add("删除动态");
                    } else {
                        list.add("转发到主页");
                        list.add("转发给好友");
                        list.add("收藏");
                        list.add("举报");
                    }
                }
                intent.putStringArrayListExtra("list", (ArrayList<String>) list);
                int index = (int) view.getTag();
                StudyMessageItem message = listMessage.get(index);
                intent.putExtra("msg", message);
                cur_index = index;
                startActivityForResult(intent, REQ_RELEASE);
            }
        });
        adapter.setImgClickHead(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                int index = (int) view.getTag();
//                Intent intent = new Intent(mContext, HomePageActivity.class);
//                StudyMessageItem item = listMessage.get(index);
//                intent.putExtra(Constant.ID_USER, item.getCreateBy().getId());
//                intent.putExtra(Constant.ID_SPACE, item.getSpaceId());
//                startActivity(intent);
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
                    startActivityForResult(intent, REQ_DEL);
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

    //点赞和取消点赞
    private void addLike(final String bullId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                httpManager.addLiked(bullId, userid);
            }
        }).start();
    }

    //设置头像
    private void setHeadIcon(final String userId, final int type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String url = httpManager.getUserIconUrl(userId);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        setImage(url, type);
                    }
                });
            }
        }).start();
    }

    private void setImage(String url, int type) {
        if (url == null || url.equals("")) {
            url = "null";
        }

        if (type == 0) {
            Picasso.with(mContext).load(url)
                    .placeholder(R.mipmap.ic_jigou_zhuye).config(Bitmap.Config.RGB_565).resize(106, 106).into(img_user);
            img_bg.setImageResource(R.mipmap.img_bierenzhuye);
        } else if (type == 1) {
            Picasso.with(mContext).load(url)
                    .placeholder(R.mipmap.ic_head_wode).config(Bitmap.Config.RGB_565).resize(106, 106).into(img_user);
            img_bg.setImageResource(R.mipmap.img_bg2);
        } else if (type == 2) {
            Picasso.with(mContext).load(url)
                    .placeholder(R.mipmap.ic_qunzu_xuanren).config(Bitmap.Config.RGB_565).resize(106, 106).into(img_user);
            img_bg.setImageResource(R.mipmap.img_wodezhuye);
        }
        img_organization.setVisibility(View.GONE);
    }

    private void getSpaceRelation(final String sid, final String spaceid) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(String.format(Constant.STUDY_SELECT_SPACE_RELATIONSHIP, sid, spaceid))
                        .build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        Message msg = new Message();
                        msg.what = 3;
                        msg.arg1 = response.code();
                        msg.obj = response.body().string();
                        mHandler.sendMessage(msg);
                    } else {
                        //mHandler.sendEmptyMessage(404);
                    }
                    if (response != null) {
                        response.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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

    //打开所有图片
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
//        Log.i("info", type);
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

    private void initEvent() {
        //message_linear.setOnClickListener(this);
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
                close();
                break;
            case R.id.study_message_right_icon:
                intent = new Intent(mContext, ReleaseActivity.class);
                intent.putExtra("sid", spaceid);
                startActivityForResult(intent, REQ_RELEASE);
                break;
            case R.id.img_study_release:
                intent = new Intent(mContext, ReleaseActivity.class);
                startActivityForResult(intent, REQ_RELEASE);
                break;
            case R.id.study_sapce_jia_img:
                if (!isCareCanClick && System.currentTimeMillis() - last_click_time < 3000) {
                    return;
                }
                isCareCanClick = false;
                last_click_time = System.currentTimeMillis();
                if (!isCare) {
//                    img_care.setImageResource(R.mipmap.bt_quxiaoguanzhu_zhuye);
                    addCare();
                } else {
                    cancelCare();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        close();
    }

    private void close() {
        Intent intent = new Intent();
        intent.putExtra("del", isdel);
        setResult(RESULT_OK, intent);
        this.finish();
    }

    private void cancelCare() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sid = sharedPreferences.getString(Constant.ID_SPACE, "");
                OkHttpClient.Builder builder = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS);
                client = builder.build();
                Request request = new Request.Builder()
                        .url(String.format(Constant.STUDY_CANCEL_CARE, sid, spaceid, "concern"))
                        .delete()
                        .build();
                //Log.i("info", "取消关注参数=" + String.format(Constant.STUDY_CANCEL_CARE, sid, spaceid, "concern"));
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    Message message = new Message();
                    message.what = 5;
                    message.arg1 = response.code();
                    message.obj = response.body().string();
                    mHandler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                    //mHandler.sendEmptyMessage(400);
                }
                if (response != null) {
                    response.close();
                }
            }
        }).start();
    }

    private void addCare() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                String json = "";
                JSONObject jsonObject = new JSONObject();
                try {
                    String sid = sharedPreferences.getString(Constant.ID_SPACE, "");
                    jsonObject.put("mainSpaceId", sid);
                    jsonObject.put("subSpaceId", spaceid);
                    jsonObject.put("relationType", "concern");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                json = jsonObject.toString();
                //Log.i("info", "添加关注参数=" + json);
                RequestBody body = RequestBody.create(JSON, json);
                OkHttpClient.Builder builder = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS);
                client = builder.build();
                Request request = new Request.Builder()
                        .url(Constant.STUDY_ADD_CARE)
                        .post(body)
                        .build();
//                Log.i("info", "查看是否关注=" + Constant.STUDY_ADD_CARE);
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
                    //mHandler.sendEmptyMessage(400);
                }
                if (response != null) {
                    response.close();
                }
            }
        }).start();
    }

    public void execute(String spaceId, int page, int page_size) {
        String url = String.format(Constant.STUDY_MY_PAGE_ID, spaceId, userid, page, page_size);
//        Log.i("info", "请求个人主页路径=" + url);
        final Request request = new Request.Builder()
                .url(url)
                .build();

        //client = new OkHttpClient();
        int cacheSize = 10 * 1024 * 1024;
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .cache(new Cache(getSDPath(), cacheSize));
        client = builder.build();
        client.newCall(request).enqueue(new Callback() {
                                            @Override
                                            public void onFailure(Call call, IOException e) {
                                                mHandler.sendEmptyMessage(404);
                                            }

                                            @Override
                                            public void onResponse(Call call, final Response response) throws IOException {
                                                if (response.isSuccessful()) {
                                                    Message message = new Message();
                                                    message.what = 1;
                                                    message.obj = response.body().string();
                                                    mHandler.sendMessage(message);
                                                } else {
                                                    mHandler.sendEmptyMessage(404);
                                                }
                                                if (response != null) {
                                                    response.close();
                                                }
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
                Bundle bundle = data.getExtras();
                boolean del = false;
                if (bundle != null) {
                    del = bundle.getBoolean("del", false);
                    isdel = del;
                }
                if (!del) {
                    loadData();
                } else {
                    if (cur_index != -1) {
                        listMessage.remove(cur_index);
                        adapter.notifyDataSetChanged();
                        if (listMessage.size() == 0) {
                            img_anim.setVisibility(View.VISIBLE);
                            img_anim.setImageResource(R.mipmap.ic_noresult_zhuyedongtai);
                        }
                    }
                }
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

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("HomePageActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("HomePageActivity");
        MobclickAgent.onPause(this);
    }
}



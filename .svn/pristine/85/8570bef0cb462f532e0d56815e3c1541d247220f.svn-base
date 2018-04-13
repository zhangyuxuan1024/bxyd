package net.iclassmate.bxyd.ui.activitys.owner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import net.iclassmate.bxyd.adapter.owner.SaveAdapter;
import net.iclassmate.bxyd.bean.study.OriginBulletinInfo;
import net.iclassmate.bxyd.bean.study.Resources;
import net.iclassmate.bxyd.bean.study.StudyMessageItem;
import net.iclassmate.bxyd.bean.study.save.BulletinItem;
import net.iclassmate.bxyd.bean.study.save.Save;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.ui.activitys.study.CommentActivity;
import net.iclassmate.bxyd.ui.activitys.study.LookPicActivity;
import net.iclassmate.bxyd.ui.activitys.study.ReleaseActivity;
import net.iclassmate.bxyd.ui.activitys.study.StudyWindowActivity;
import net.iclassmate.bxyd.ui.activitys.study.openfile.OpenAudioActivity;
import net.iclassmate.bxyd.ui.activitys.study.openfile.OpenFailActivity;
import net.iclassmate.bxyd.ui.activitys.study.openfile.OpenPicActivity;
import net.iclassmate.bxyd.ui.activitys.study.openfile.OpenTextFileActivity;
import net.iclassmate.bxyd.ui.activitys.study.openfile.OpenVideoActivity;
import net.iclassmate.bxyd.utils.HttpManager;
import net.iclassmate.bxyd.utils.NetWorkUtils;
import net.iclassmate.bxyd.utils.OpenFile;
import net.iclassmate.bxyd.utils.UIUtils;
import net.iclassmate.bxyd.view.FullListView;
import net.iclassmate.bxyd.view.pullrefreshview.MyListener;
import net.iclassmate.bxyd.view.pullrefreshview.PullToRefreshLayout;

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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OwnerSaveActivity extends FragmentActivity implements View.OnClickListener, View.OnTouchListener {
    private SaveAdapter adapter;
    private FullListView listView;
    private List<BulletinItem> listMessage;
    private Context mContext;
    private Save save;
    private OkHttpClient client;

    private TextView tv_back, tv_title;
    private ImageView img_back, img_right, img_anim;
    private AnimationDrawable anim;
    public static final int REQ_RELEASE = 1;
    public static final int REQ_COMMENT_COUNT = 2;
    private static final int REQ_DEL = 3;
    //当前点击的条目，用户删除
    private int cur_click;

    private String spaceid, userid;
    private int type_user;
    private SharedPreferences sharedPreferences;
    private int pages, pageSize, total;
    private PullToRefreshLayout pull;
    private HttpManager httpManager;

    private long last_click_time;
    private long last_touch_time;
    private boolean loadfinish;
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            anim.stop();
            img_anim.setBackgroundColor(Color.WHITE);
            if (what == 1) {
                loadfinish = true;
                img_anim.setVisibility(View.GONE);
                String result = (String) msg.obj;
                try {
                    result = removeBOM(result);
                    JSONObject json = new JSONObject(result);
                    save = new Save();
                    save.parserJson(json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (save == null || save.getBulletinList() == null || save.getBulletinList().size() < 1) {
                    if (pages == 1 && listMessage.size() < 1) {
                        img_anim.setBackgroundColor(Color.WHITE);
                        img_anim.setVisibility(View.VISIBLE);
                        if (save == null || save.getBulletinList() == null) {
                            img_anim.setImageResource(R.mipmap.img_yemianjiazaishibai);
                        } else {
                            img_anim.setImageResource(R.mipmap.ic_noresult_shoucang);
                        }
                        pull.refreshFinish(PullToRefreshLayout.SUCCEED);
                        pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                        return;
                    }
                }
                List<BulletinItem> bulletinList = save.getBulletinList();
                if (pages == 1) {
                    listMessage.clear();
                    pull.refreshFinish(PullToRefreshLayout.SUCCEED);
                } else {
                    pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
                listMessage.addAll(bulletinList);
                adapter.notifyDataSetChanged();
                total = save.getTotal();
            } else if (what == 404) {
                pull.refreshFinish(PullToRefreshLayout.FAIL);
                pull.loadmoreFinish(PullToRefreshLayout.FAIL);
                img_anim.setBackgroundColor(Color.WHITE);
                img_anim.setVisibility(View.VISIBLE);
                img_anim.setImageResource(R.mipmap.img_yemianjiazaishibai);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_save);

        Intent intent = getIntent();
        spaceid = intent.getStringExtra(Constant.ID_SPACE);
        userid = intent.getStringExtra(Constant.ID_USER);
        type_user = intent.getIntExtra(Constant.ID_USERTYPE, 1);
        sharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        if (spaceid == null || spaceid.equals("") || userid == null || userid.equals("")) {
            spaceid = sharedPreferences.getString(Constant.ID_SPACE, "");
            userid = sharedPreferences.getString(Constant.ID_USER, "");
            String type = sharedPreferences.getString(Constant.ID_USERTYPE, "1");
            try {
                type_user = Integer.parseInt(type);
            } catch (Exception e) {
                type_user = 1;
            }
        }

        initView();
        initEvent();
        loadData();
    }

    private void loadData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    execute(spaceid, userid, pages, pageSize);
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

        listView = (FullListView) findViewById(R.id.study_sapce_listview);
        listMessage = new ArrayList<>();

        tv_back = (TextView) findViewById(R.id.study_message_left_tv);
        tv_back.setOnClickListener(this);
        tv_back.setText("我的");
        img_back = (ImageView) findViewById(R.id.study_message_back);
        img_back.setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.study_message_title_tv);
        tv_title.setText("收藏");
        img_right = (ImageView) findViewById(R.id.study_message_right_icon);
        img_right.setImageResource(R.mipmap.ic_edit);
        img_right.setOnClickListener(this);
        img_right.setVisibility(View.INVISIBLE);
        img_anim = (ImageView) findViewById(R.id.img_anim);
        anim = (AnimationDrawable) img_anim.getBackground();
        anim.start();

        pages = 1;
        pageSize = 20;
        mContext = this;
        loadfinish = true;
        httpManager = new HttpManager();

        adapter = new SaveAdapter(mContext, listMessage);
        listView.setAdapter(adapter);
        adapter.setIsCanClickLike(true);
        adapter.setImgClickComent(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = (int) view.getTag();
                StudyMessageItem message = listMessage.get(index).getStudyMessageItem();
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
                BulletinItem item = listMessage.get(index);
                StudyMessageItem message = item.getStudyMessageItem();
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
                message.setLiked(like);
                message.setIsClickLiked(isLike);
                addLike(message.getId());
                listMessage.set(index, item);
                adapter.notifyDataSetChanged();
            }
        });
        adapter.setImgClickShare(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = (int) view.getTag();
                cur_click = index;
                BulletinItem item = listMessage.get(index);
                StudyMessageItem message = item.getStudyMessageItem();
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
                if (type != null) {
                    type = type.toUpperCase();
                    boolean add = true;
                    if (type.equals("FORWARD")) {
                        if (message.getOriginBulletinInfo() == null || message.getOriginBulletinInfo().getCreateBy() == null) {
                            add = false;
                        }
                    }
                    if (add && !uid.equals(uid2) && !uid.equals(uid3)) {
                        list.add("转发到主页");
                    }
                    list.add("转发给好友");
                    list.add("取消收藏");
                    list.add("举报");
                }
                intent.putStringArrayListExtra("list", (ArrayList<String>) list);
                intent.putExtra("msg", message);
                intent.putExtra("saveId", item.getId());
                startActivityForResult(intent, REQ_RELEASE);
            }
        });
        adapter.setImgClickHead(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = (int) view.getTag();
                Intent intent = new Intent(mContext, HomePageActivity.class);
                BulletinItem bulletinItem = listMessage.get(index);
                StudyMessageItem item = bulletinItem.getStudyMessageItem();
                if (item == null || item.getCreateBy() == null) {
                    return;
                }
                intent.putExtra(Constant.ID_USER, item.getCreateBy().getId());
                intent.putExtra(Constant.ID_SPACE, item.getSpaceId());
                startActivity(intent);
            }
        });
        adapter.setImgClickHomePage(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = (int) v.getTag();
                BulletinItem bulletinItem = listMessage.get(index);
                StudyMessageItem item = bulletinItem.getStudyMessageItem();
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
                StudyMessageItem item = listMessage.get(index).getStudyMessageItem();
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


    //获取文件类型
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
        if (type.equals("ORIGIN")) {
            if (item.getList() != null && item.getList().size() > index) {
                id = item.getList().get(index).getId();
                type = item.getList().get(index).getType();
                name = item.getList().get(index).getName().toLowerCase();
            }
        } else if (type.equals("FORWARD")) {
            if (item.getOriginBulletinInfo() != null && item.getOriginBulletinInfo().getList() != null
                    && item.getOriginBulletinInfo().getList().size() > index) {
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
                intent = new Intent(mContext, ReleaseActivity.class);
                startActivityForResult(intent, REQ_RELEASE);
                break;
        }
    }

    public void execute(String spaceId, String userid, int page, int page_size) {
        String url = String.format(Constant.STUDY_SAVE_LIST_ID, spaceId, userid, page, page_size);
//        Log.i("info", "请求收藏路径=" + url);
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
//                                                Log.i("info", "请求数据失败" + e.getMessage());
                                                mHandler.sendEmptyMessage(404);
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
                                                    mHandler.sendEmptyMessage(404);
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
                boolean del = data.getBooleanExtra("del", false);
                if (del) {
                    if (listMessage.size() > cur_click) {
                        listMessage.remove(cur_click);
                        adapter.notifyDataSetChanged();
                    }
                    if (listMessage.size() == 0) {
                        img_anim.setBackgroundColor(Color.WHITE);
                        img_anim.setVisibility(View.VISIBLE);
                        img_anim.setImageResource(R.mipmap.ic_noresult_zhuyedongtai);
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
                    BulletinItem item = listMessage.get(index);
                    StudyMessageItem msg = item.getStudyMessageItem();
                    msg.setCommented(count);
                    msg.setLiked(liked);
                    msg.setIsClickLiked(flag);
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
        MobclickAgent.onPageStart("OwnerSaveActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("OwnerSaveActivity");
        MobclickAgent.onPause(this);
    }
}



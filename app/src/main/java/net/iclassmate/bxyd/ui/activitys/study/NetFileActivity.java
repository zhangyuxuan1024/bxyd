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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.adapter.study.NetFileAdapter;
import net.iclassmate.bxyd.bean.study.FileDirList;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.constant.StudyConstance;
import net.iclassmate.bxyd.utils.NetWorkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetFileActivity extends FragmentActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private List<FileDirList> list;
    private List<Object> listSelectAll;
    private List<String> ids;
    private GridView gridView;
    private NetFileAdapter adapter;

    private TextView tv_back, tv_title, tv_cancel, tv_sure;
    private ImageView img_back, img_anim;
    private AnimationDrawable anim;
    private Context mContext;
    private OkHttpClient client;
    private int select_count;
    private String fullPath;
    private String title;
    private boolean isCanSelect;
    public static final int RET_MY = 1;
    public boolean isroot;

    private String spaceid;
    private SharedPreferences sharedPreferences;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            anim.stop();
            img_anim.setVisibility(View.GONE);
            if (what == 1) {
                //Toast.makeText(mContext, "数据请求成功", Toast.LENGTH_SHORT).show();
                String ret = (String) msg.obj;
                ids.clear();
                try {
                    JSONObject json = new JSONObject(ret);
                    JSONArray array = json.getJSONArray("fileDirList");
                    for (int i = 0; i < array.length(); i++) {
                        FileDirList fileDirList = new FileDirList();
                        fileDirList.parserJson(array.getJSONObject(i));
                        list.add(fileDirList);
                        ids.add(fileDirList.getId());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (listSelectAll != null) {
                    for (int i = 0; i < listSelectAll.size(); i++) {
                        Object object = listSelectAll.get(i);
                        if (object instanceof FileDirList) {
                            FileDirList fileDirList = (FileDirList) object;
                            String id = fileDirList.getId();
                            if (ids.contains(id)) {
                                int index = ids.indexOf(id);
                                fileDirList.setIsCheck(true);
                                list.set(index, fileDirList);
                            }
                        }
                    }
                }
                setSelectTitle();
                adapter.notifyDataSetChanged();
                if (list.size() < 1) {
                    img_anim.setVisibility(View.VISIBLE);
                    img_anim.setBackgroundColor(Color.parseColor("#efefef"));
                    img_anim.setImageResource(R.mipmap.img_meiwenjian);
                }
            } else if (what == 404) {
                img_anim.setVisibility(View.VISIBLE);
                img_anim.setBackgroundColor(Color.parseColor("#efefef"));
                img_anim.setImageResource(R.mipmap.img_jiazaishibai);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_file);
        mContext = this;
        Intent intent = getIntent();
        select_count = intent.getIntExtra("count", 0);
        fullPath = intent.getStringExtra("fullPath");
        title = intent.getStringExtra("title");
        listSelectAll = (List<Object>) intent.getSerializableExtra("file");

        spaceid = intent.getStringExtra(Constant.ID_SPACE);
        sharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        if (spaceid == null || spaceid.equals("")) {
            spaceid = sharedPreferences.getString(Constant.ID_SPACE, "");
        }
        init();
        loadData();
    }

    private void init() {
        isCanSelect = true;
        gridView = (GridView) findViewById(R.id.netfile_gridview);
        list = new ArrayList<>();
        ids = new ArrayList<>();

        adapter = new NetFileAdapter(this, list);
        gridView.setAdapter(adapter);
        tv_back = (TextView) findViewById(R.id.study_message_left_tv);
        img_back = (ImageView) findViewById(R.id.study_message_back);
        tv_title = (TextView) findViewById(R.id.study_message_title_tv);
        tv_back.setOnClickListener(this);
        img_back.setOnClickListener(this);
        tv_title.setText("网盘文件");
        tv_back.setText("返回");
        tv_cancel = (TextView) findViewById(R.id.tv_net_cancel);
        tv_sure = (TextView) findViewById(R.id.tv_net_sure);
        tv_cancel.setOnClickListener(this);
        tv_sure.setOnClickListener(this);

        img_anim = (ImageView) findViewById(R.id.img_anim);
        if (!NetWorkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "请检查网络", Toast.LENGTH_LONG).show();
            img_anim.setVisibility(View.GONE);
            return;
        }

        anim = (AnimationDrawable) img_anim.getBackground();
        anim.start();

        if (fullPath == null) {
            fullPath = "";
            isroot = true;
            tv_back.setVisibility(View.INVISIBLE);
            img_back.setVisibility(View.INVISIBLE);
        } else {
            isroot = false;
            tv_back.setVisibility(View.VISIBLE);
            img_back.setVisibility(View.VISIBLE);
        }

        if (title != null && !title.equals("")) {
            tv_title.setText(title);
        }

        adapter.setImgCheckClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = (int) view.getTag();
                FileDirList file = list.get(index);
                boolean flag = !file.isCheck();
                if (flag && !isCanSelect) {
                    Toast.makeText(mContext, "最多选择9个文件", Toast.LENGTH_SHORT).show();
                    return;
                }
                file.setIsCheck(flag);
                list.set(index, file);

                if (flag) {
                    listSelectAll.add(file);
                } else {
                    for (int i = 0; i < listSelectAll.size(); i++) {
                        Object object = listSelectAll.get(i);
                        if (object instanceof FileDirList) {
                            FileDirList fileDirList = (FileDirList) object;
                            String id = file.getId();
                            if (id.equals(fileDirList.getId())) {
                                listSelectAll.remove(i);
                                break;
                            }
                        }
                    }
                }

                setSelectTitle();
                adapter.notifyDataSetChanged();
            }
        });
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.study_message_left_tv:
                this.finish();
                break;
            case R.id.study_message_back:
                this.finish();
                break;
            case R.id.tv_net_cancel:
                cancleFile();
                break;
            case R.id.tv_net_sure:
                Intent intent = new Intent();
                intent.putExtra("type", StudyConstance.FILE_NET_FILE);
                intent.putExtra("list", (Serializable) listSelectAll);
                intent.putExtra("file", (Serializable) listSelectAll);
                setResult(RESULT_OK, intent);
                this.finish();
                break;
        }
    }

    //取消操作
    private void cancleFile() {
        if (isroot) {
            this.finish();
        } else {
            Intent intent = new Intent();
            setResult(StudyConstance.CANCEL_FILE, intent);
            this.finish();
        }
    }

    private void loadData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {// spaceId  "622bf101182d48ff83ba9d6879e47bd6"
                    execute(spaceid, fullPath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        adapter.notifyDataSetChanged();
    }

    private void execute(String spaceid, String path) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        int cacheSize = 10 * 1024 * 1024;
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS);
        client = builder.build();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("fullPath", path);
            jsonObject.put("spaceId", spaceid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json = jsonObject.toString();
//        Log.i("info", "获取网盘文件请求参数=" + json);
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(Constant.STUDY_SELECT_NETFILE)
                .post(body)
                .build();
//        Log.i("info", "获取网盘文件路径=" + Constant.STUDY_SELECT_NETFILE);
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
//                Log.i("info", "数据  网盘文件=" + response.body().string());
                Message message = new Message();
                message.what = 1;
                message.obj = response.body().string();
                mHandler.sendMessage(message);
            } else {
                Message message = new Message();
                message.what = 404;
                mHandler.sendMessage(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setSelectTitle() {
        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            FileDirList file1 = list.get(i);
            if (file1.isCheck()) {
                count++;
            }
        }
        if (listSelectAll.size() >= 9) {
            isCanSelect = false;
        } else {
            isCanSelect = true;
        }
        if (listSelectAll.size() > 0) {
            tv_sure.setText("确定(" + listSelectAll.size() + ")");
            tv_sure.setClickable(true);
            tv_sure.setTextColor(0xff65caff);
        } else {
            tv_sure.setText("确定");
            tv_sure.setClickable(false);
            tv_sure.setTextColor(0x7765caff);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        FileDirList fileDirList = list.get(i);
        int type = fileDirList.getType();
        if (type == 1) {
            Intent intent = new Intent(mContext, NetFileActivity.class);
            intent.putExtra("fullPath", fileDirList.getFullPath());
            intent.putExtra("title", fileDirList.getShortName());
            intent.putExtra("file", (Serializable) listSelectAll);
            startActivityForResult(intent, RET_MY);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            if (requestCode == RET_MY) {
                listSelectAll = (List<Object>) bundle.getSerializable("file");
                setSelectTitle();
            }
        } else if (resultCode == StudyConstance.CANCEL_FILE) {
            if (requestCode == RET_MY) {
                Intent intent = new Intent();
                setResult(StudyConstance.CANCEL_FILE, intent);
                this.finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("NetFileActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("NetFileActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
package net.iclassmate.bxyd.ui.activitys.study;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.adapter.study.StudyTraFriAdapter;
import net.iclassmate.bxyd.bean.study.StudyMessageItem;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.view.FullListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TraStudyActivity extends FragmentActivity implements View.OnClickListener {
    private StudyMessageItem msg;
    private StudyTraFriAdapter adapter;
    private FullListView listView;
    private TextView tv_back, tv_release;
    private Context mContext;
    private EditText editText;
    private OkHttpClient client;

    private String spaceid, userid;
    private SharedPreferences sharedPreferences;
    private long last_clcik_time;
    private boolean istra;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            if (what == 1) {
                try {
                    int code = (int) msg.obj;
//                    Log.i("info", "返回数据=" + code);
                    if (code == 200) {
                        Toast.makeText(mContext, "已转发到主页！", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    } else if (code == 400) {
                        Toast.makeText(TraStudyActivity.this, "所转发的原创不允许转发！", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(TraStudyActivity.this, "转发主页失败！", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(TraStudyActivity.this, "转发主页失败！", Toast.LENGTH_SHORT).show();
                }
                istra = false;
            } else if (what == 404) {
                Toast.makeText(TraStudyActivity.this, "转发主页失败！", Toast.LENGTH_SHORT).show();
                istra = false;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tra_study);
        Intent intent = getIntent();
        msg = (StudyMessageItem) intent.getSerializableExtra("msg");
        List<StudyMessageItem> list = new ArrayList<>();
        list.add(msg);
        adapter = new StudyTraFriAdapter(this, list);
        adapter.setMsgType(1);

        listView = (FullListView) findViewById(R.id.study_sapce_msg_listview);
        listView.setAdapter(adapter);

        tv_back = (TextView) findViewById(R.id.tv_comment_back);
        tv_back.setOnClickListener(this);
        tv_release = (TextView) findViewById(R.id.tv_comment_release);
        tv_release.setOnClickListener(this);
        editText = (EditText) findViewById(R.id.tv_forawrd);
        mContext = this;

        sharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        userid = sharedPreferences.getString(Constant.ID_USER, "");
        spaceid = sharedPreferences.getString(Constant.ID_SPACE, "");
        istra = false;

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 255) {
                    s = s.subSequence(0, 255);
                    editText.setText(s);
                    editText.setSelection(s.length());
                    if (System.currentTimeMillis() - last_clcik_time > 3000) {
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.out_max_num), Toast.LENGTH_SHORT).show();
                        last_clcik_time = System.currentTimeMillis();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_comment_back:
                this.finish();
                break;
            case R.id.tv_comment_release:
                if (System.currentTimeMillis() - last_clcik_time < 3000 && istra) {
                    return;
                }
                last_clcik_time = System.currentTimeMillis();

                istra = true;
                forward(userid, spaceid);
                break;
        }
    }

    private void forward(final String userId, final String spaceId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                execute(userId, spaceId);
            }
        }).start();
    }

    private void execute(String userId, String spaceId) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String json = "";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("commentable", true);
            jsonObject.put("downloadable", true);
            jsonObject.put("forwardable", true);
            jsonObject.put("content", editText.getText().toString());
            jsonObject.put("sectionId", "sectionId");
            jsonObject.put("spaceId", spaceId);
            String type = msg.getBulletinType().toUpperCase();
            if (type.equals("ORIGIN")) {
                jsonObject.put("originBulletinId", msg.getId());
            } else if (type.equals("FORWARD")) {
                jsonObject.put("originBulletinId", msg.getOriginBulletinInfo().getId());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        json = jsonObject.toString();
//        Log.i("info", "发布动态参数=" + json);
        RequestBody body = RequestBody.create(JSON, json);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS);
        client = builder.build();
        Request request = new Request.Builder()
                .url(String.format(Constant.STUDY_FORWARD, userId))
                .post(body)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            Message message = new Message();
            message.what = 1;
            message.obj = response.code();
            mHandler.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(404);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("TraStudyActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("TraStudyActivity");
        MobclickAgent.onPause(this);
    }
}
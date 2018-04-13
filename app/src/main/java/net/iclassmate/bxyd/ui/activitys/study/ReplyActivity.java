package net.iclassmate.bxyd.ui.activitys.study;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.bean.study.StudyMessageItem;
import net.iclassmate.bxyd.constant.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReplyActivity extends FragmentActivity implements View.OnClickListener, TextWatcher {
    private EditText ed_reply;
    private String reply;
    private TextView tv_back, tv_send;
    private Context mContext;
    private StudyMessageItem msg;
    private OkHttpClient client;

    private String userid;
    private String rid;
    private SharedPreferences sharedPreferences;
    private boolean isreply;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            if (what == 1) {
                try {
                    int code = (int) msg.obj;
                    //Log.i("info", "返回数据=" + code);
                    if (code == 200) {
                        Toast.makeText(mContext, "评论成功！", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                        isreply = true;
                    } else {
                        Toast.makeText(mContext, "评论失败！", Toast.LENGTH_SHORT).show();
                        isreply = false;
                    }
                } catch (Exception e) {
                    Toast.makeText(mContext, "评论失败！", Toast.LENGTH_SHORT).show();
                    isreply = false;
                }
            } else if (what == 404) {
                Toast.makeText(mContext, "评论失败！", Toast.LENGTH_SHORT).show();
                isreply = false;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        Intent intent = getIntent();
        reply = intent.getStringExtra("reply");
        msg = (StudyMessageItem) intent.getSerializableExtra("msg");
        rid = intent.getStringExtra("rid");
        initView();
        initEvent();
    }

    private void initView() {
        ed_reply = (EditText) findViewById(R.id.comment_reply_et);
        ed_reply.setHint(reply);
        tv_back = (TextView) findViewById(R.id.tv_reply_back);
        tv_send = (TextView) findViewById(R.id.tv_reply_release);
        tv_send.setText("发送");

        sharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        userid = sharedPreferences.getString(Constant.ID_USER, "");
        mContext = this;
        ed_reply.addTextChangedListener(this);
        isreply = false;
    }

    private void initEvent() {
        tv_back.setOnClickListener(this);
        tv_send.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_reply_back:
                this.finish();
                break;
            case R.id.tv_reply_release:
                if (ed_reply.getText().toString().trim().equals("")) {
                    Toast.makeText(mContext, "内容不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (isreply) {
                        return;
                    }
                    reply(userid);
                    isreply = true;
                }
                break;
        }
    }

    private void reply(final String userId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                execute(userId);
            }
        }).start();
    }

    private void execute(String userId) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String json = "";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("bulletinId", msg.getId());
            jsonObject.put("content", ed_reply.getText().toString());
            if (rid != null && !rid.equals("")) {
                jsonObject.put("replyTo", rid);
            } else {
                jsonObject.put("replyTo", msg.getCreateBy().getId());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        json = jsonObject.toString();
//        Log.i("info", "评论参数=" + json);
        RequestBody body = RequestBody.create(JSON, json);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS);
        client = builder.build();
        Request request = new Request.Builder()
                .url(String.format(Constant.STUDY_RELEASE_COMMENT, userId))
                .post(body)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                Message message = new Message();
                message.what = 1;
                message.obj = response.code();
                mHandler.sendMessage(message);
            } else {
                mHandler.sendEmptyMessage(404);
            }
        } catch (IOException e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(404);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() > 255) {
            String ret = s.toString();
            ret = ret.substring(0, 255);
            ed_reply.setText(ret);

            CharSequence text = ed_reply.getText();
            if (text instanceof Spannable) {
                Spannable spanText = (Spannable) text;
                Selection.setSelection(spanText, text.length());
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("ReplyActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("ReplyActivity");
        MobclickAgent.onPause(this);
    }
}
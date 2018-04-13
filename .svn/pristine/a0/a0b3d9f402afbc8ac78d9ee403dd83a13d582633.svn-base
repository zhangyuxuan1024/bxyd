package net.iclassmate.bxyd.ui.activitys.owner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.bean.attention.Attention;
import net.iclassmate.bxyd.constant.Constant;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by xydbj on 2016.7.14.
 */
public class UnfollowActivity extends Activity implements View.OnClickListener {
    private TextView unfollow_cancel, unfollow_sure;
    private List<Attention> attentionList;
    private String uuid;
    private int index;
    public static final String action = "net.iclassmate.bxyd.ui.activitys.owner";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unfollow);
        Intent intent = getIntent();
        uuid = intent.getStringExtra("uuid");
        attentionList = (List<Attention>) intent.getSerializableExtra("list");
        index = intent.getIntExtra("index", 0);
        Log.i("info", "取消关注，传递的数据：uuid=" + uuid + ",index=" + index + ",attentionList=" + attentionList.toString());
        initView();
    }

    public void initView() {
        unfollow_cancel = (TextView) findViewById(R.id.unfollow_cancel);
        unfollow_sure = (TextView) findViewById(R.id.unfollow_sure);

        unfollow_cancel.setOnClickListener(this);
        unfollow_sure.setOnClickListener(this);

    }

    public void sendRequest(final String uuid) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                execute(uuid);
            }
        }).start();
    }

    public void execute(String uuid) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .delete()
                .url(Constant.UNFOLLOWURL + uuid)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("info", "取消关注失败：" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message msg = new Message();
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
                    Toast.makeText(UnfollowActivity.this, "操作成功!", Toast.LENGTH_LONG).show();
                    for (int i = 0; i < attentionList.size(); i++) {
                        if (i == index) {
                            attentionList.remove(index);
                        }
                    }
                    Intent intent = new Intent(action);
                    intent.putExtra("list",(Serializable) attentionList);
                    sendBroadcast(intent);
                    UnfollowActivity.this.finish();
                    break;
                case 204:
                case 401:
                case 403:
                    Toast.makeText(UnfollowActivity.this, "操作失败，请检查！", Toast.LENGTH_LONG).show();
                    finish();
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.unfollow_cancel:
                finish();
                break;
            case R.id.unfollow_sure:
                sendRequest(uuid);
                break;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("UnfollowActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("UnfollowActivity");
        MobclickAgent.onPause(this);
    }
}

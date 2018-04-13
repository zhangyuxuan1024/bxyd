package net.iclassmate.bxyd.ui.activitys.owner;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.constant.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by xydbj on 2016.6.18.
 */
public class BoundNowPhoneNumActivity extends Activity implements View.OnClickListener {
    private SharedPreferences sharedPreferences;
    private ImageView boundnowphonenum_iv_newphone,boundnowphonenum_iv_back;
    private TextView boundnowphonenum_tv_nowphone,boundnowphonenum_tv_back;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boundnowphonenum);
        initView();

        IntentFilter filter = new IntentFilter(BoundPhoneNumActivity.action);
        registerReceiver(receiver,filter);

    }
    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getPhoneNumber(userId);
        }
    };

    public void initView() {
        boundnowphonenum_tv_nowphone = (TextView) findViewById(R.id.boundnowphonenum_tv_nowphone);
        boundnowphonenum_iv_newphone = (ImageView) findViewById(R.id.boundnowphonenum_iv_newphone);
        boundnowphonenum_tv_back = (TextView) findViewById(R.id.boundnowphonenum_tv_back);
        boundnowphonenum_iv_back = (ImageView) findViewById(R.id.boundnowphonenum_iv_back);
        boundnowphonenum_iv_newphone.setOnClickListener(this);
        boundnowphonenum_tv_back.setOnClickListener(this);
        boundnowphonenum_iv_back.setOnClickListener(this);
        getUserId();
    }

    public void getUserId() {
        sharedPreferences = BoundNowPhoneNumActivity.this.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        userId = sharedPreferences.getString(Constant.ID_USER, "");
        Log.i("info", "绑定新手机号时，获取的userId：" + userId);
        getPhoneNumber(userId);
    }
    //通过userId,获取用户手机号
    public void getPhoneNumber(final String string) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                execute(string);
            }
        }).start();
    }

    public void execute(String userId) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(Constant.CHANGEPHONE_URL + userId + "?needIcon=true")
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("info", "获取手机号失败:" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message msg = new Message();
                msg.what = 1;
                msg.obj = response.body().string();
                mHandler.sendMessage(msg);
            }
        });
    }
    public Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    String json = (String) msg.obj;
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(json);
                        JSONObject jsonObject1 = jsonObject.getJSONObject("userInfo");
                        String phone = jsonObject1.getString("phone");
                        boundnowphonenum_tv_nowphone.setText(phone);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.boundnowphonenum_iv_newphone:
                Intent intent = new Intent(BoundNowPhoneNumActivity.this, BoundPhoneNumActivity.class);
                startActivity(intent);
                break;
            case R.id.boundnowphonenum_tv_back:
            case R.id.boundnowphonenum_iv_back:
                finish();
                break;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("BoundNowPhoneNumActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("BoundNowPhoneNumActivity");
        MobclickAgent.onPause(this);
    }
}

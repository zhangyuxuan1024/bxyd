package net.iclassmate.bxyd.ui.activitys.owner;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.constant.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by xydbj on 2016.6.13.
 */
public class FeedBackActivity extends Activity implements OnClickListener {
    private ImageView feedback_iv_back, feedback_submit;
    private TextView feedback_tv_back;
    private EditText feedback_et;
    private SharedPreferences sharedPreferences;
    private String userId,description,version;
    public static final int clientType = 2;// 类型,1: Windows 2:Android 3:IOS = ['1', '2', '3'],

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        initView();
    }

    public void initView() {
        feedback_iv_back = (ImageView) findViewById(R.id.feedback_iv_back);
        feedback_tv_back = (TextView) findViewById(R.id.feedback_tv_back);
        feedback_submit = (ImageView) findViewById(R.id.feedback_submit);
        feedback_et = (EditText) findViewById(R.id.feedback_et);

        feedback_iv_back.setOnClickListener(this);
        feedback_tv_back.setOnClickListener(this);
        feedback_submit.setOnClickListener(this);

        sharedPreferences = FeedBackActivity.this.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        getParamete();
    }
    public void getParamete(){
        //获取userId
        userId = sharedPreferences.getString(Constant.ID_USER,"");
        if (userId == null) {
            return;
        }
        //获取当前版本
        version = getVersionInfo();
    }
    public String getVersionInfo(){
        String versionName = null;
        PackageManager pm =getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(),0);
            versionName = "V"+packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public void sendData(final int clientType, final String description, final String userId, final String version) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                execute(clientType, description, userId, version);
            }
        }).start();
    }

    public void execute(int clientType, String description, String userId, String version) {
        MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
        OkHttpClient okHttpClient = new OkHttpClient();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("clientType", clientType);
            jsonObject.put("description", description);
            jsonObject.put("userId", userId);
            jsonObject.put("version", version);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json = jsonObject.toString();
        RequestBody body = RequestBody.create(mediaType, json);
        Request request = new Request.Builder()
                .post(body)
                .url(Constant.FEEDBACK_URL)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("info", "提交意见反馈失败:" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message msg = new Message();
                msg.what = response.code();
                mHandler.sendMessage(msg);
            }
        });

    }
    public Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 200:
                    Toast.makeText(FeedBackActivity.this,"谢谢您宝贵的意见！",Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                default:
                    Toast.makeText(FeedBackActivity.this,"提交失败，我也不知道为什么！",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.feedback_iv_back:
            case R.id.feedback_tv_back:
                finish();
                break;
            case R.id.feedback_submit:
                description = feedback_et.getText().toString().trim();
                if (!feedback_et.getText().toString().trim().equals("") || !feedback_et.getText().toString().trim().equals("null") || feedback_et.getText().toString().trim().length() != 0){
                    Log.i("info","clientType="+clientType);
                    Log.i("info","description="+description);
                    Log.i("info","userId="+userId);
                    Log.i("info","version="+version);
                    sendData(clientType, description, userId,version);
                } else{
                    Toast.makeText(FeedBackActivity.this,"你想表达什么？",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("FeedBackActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("FeedBackActivity");
        MobclickAgent.onPause(this);
    }
}
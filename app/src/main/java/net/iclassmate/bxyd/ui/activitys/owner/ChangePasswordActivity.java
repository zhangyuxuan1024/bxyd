package net.iclassmate.bxyd.ui.activitys.owner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.utils.NetWorkUtils;
import net.iclassmate.bxyd.utils.UIUtils;

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
 * Created by xydbj on 2016.6.17.
 */
public class ChangePasswordActivity extends Activity implements View.OnClickListener {
    private EditText changepassword_et_oldpassword, changepassword_et_newpassword, changepassword_et_confirm;
    private TextView changepassword_tv_back;
    private ImageView changepassword_iv_submit,changepassword_iv_back;
    private SharedPreferences sharedPreferences;
    private String userId, oldpassword, newpassword, confirmpassword;

    private long last_click_time;
    private boolean isclickregist;

    public ChangePasswordActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);
        initView();
    }

    public void initView() {
        changepassword_tv_back = (TextView) findViewById(R.id.changepassword_tv_back);
        changepassword_iv_back = (ImageView) findViewById(R.id.changepassword_iv_back);
        changepassword_et_oldpassword = (EditText) findViewById(R.id.changepassword_et_oldpassword);
        changepassword_et_newpassword = (EditText) findViewById(R.id.changepassword_et_newpassword);
        changepassword_et_confirm = (EditText) findViewById(R.id.changepassword_et_confirm);
        changepassword_iv_submit = (ImageView) findViewById(R.id.changepassword_iv_submit);
        changepassword_iv_submit.setOnClickListener(this);

        changepassword_iv_back.setOnClickListener(this);
        changepassword_tv_back.setOnClickListener(this);

        isclickregist = true;
    }

    public void getParamete() {
        //获取用户的UserId；
        sharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        userId = sharedPreferences.getString(Constant.ID_USER, "");
        Log.i("info", "修改用户密码时，获取的UserId=" + userId);
        //获取用户的原始密码：
        oldpassword = changepassword_et_oldpassword.getText().toString().trim();
        Log.i("info", "修改用户密码时，获取的oldpassword=" + oldpassword);
        //获取用户的新密码：
        newpassword = changepassword_et_newpassword.getText().toString().trim();
        Log.i("info", "修改用户密码时，获取的newpassword=" + newpassword);
        //获取用户重新输入的密码：
        confirmpassword = changepassword_et_confirm.getText().toString().trim();
        Log.i("info", "修改用户密码时，获取的confirmpassword=" + confirmpassword);

        isclickregist = true;
        if (!newpassword.equals(confirmpassword)){
            Toast.makeText(ChangePasswordActivity.this, "两次输入密码不一致，请重新输入！", Toast.LENGTH_SHORT).show();
            return;
        }
        submitData(oldpassword, newpassword, userId);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.changepassword_iv_submit:

                if (!isclickregist || System.currentTimeMillis() - last_click_time < 3000) {
                    return;
                }
                last_click_time = System.currentTimeMillis();
                isclickregist = false;

                getParamete();
                break;
            case R.id.changepassword_iv_back:
            case R.id.changepassword_tv_back:
                finish();
                break;
        }
    }

    public void submitData(final String oldpassword, final String newpassword, final String userId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    execute(oldpassword, newpassword, userId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void execute(String oldpassword, String newpassword, String userId) throws IOException {
        MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("application/json;charset=utf-8");
        OkHttpClient okHttpClient = new OkHttpClient();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("oldPassWord", oldpassword);
            jsonObject.put("passWord", newpassword);
            jsonObject.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json = jsonObject.toString();
        Log.i("info", "获取密码时，封装的json串=" + json);
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_MARKDOWN, json);
        Request request = new Request.Builder()
                .url(Constant.CHANGEPASSWORD_URL)
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("info", "修改用户密码失败：" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message msg = new Message();
                msg.what = 1;
                msg.obj = response.body().string();
                msg.arg1 = response.code();
                mHandler.sendMessage(msg);
            }
        });
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (!NetWorkUtils.isNetworkAvailable(UIUtils.getContext())) {
                        Toast.makeText(ChangePasswordActivity.this, "请检查您的网络链接！", Toast.LENGTH_SHORT).show();
                        isclickregist = true;
                    } else {
                        if (oldpassword.equals("") || newpassword.equals("") || confirmpassword.equals("")) {
                            Toast.makeText(ChangePasswordActivity.this, "请确保填写信息完整，不能为空！", Toast.LENGTH_SHORT).show();
                            isclickregist = true;
                        } else {
                            if (newpassword.length() >= 6 && newpassword.length() <= 12) {
                                if (newpassword.equals(confirmpassword)) {
                                    Intent intent = new Intent(ChangePasswordActivity.this, SettingActivity.class);
                                    String result_code = msg.arg1 + "";
                                    if(result_code.equals("400")){
                                        String result_body = (String) msg.obj;
                                        JSONObject jsonObject = null;
                                        try {
                                            jsonObject = new JSONObject(result_body);
                                            String reason = jsonObject.getString("reason");
                                            Log.i("info", "修改密码，原始密码填写错误：" + reason);
                                            Toast.makeText(ChangePasswordActivity.this, reason, Toast.LENGTH_SHORT).show();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }else if (result_code.equals("200")){
                                        intent.putExtra("password", result_code);
                                        Log.i("info", "获取密码，未跳转之前，返回的result_code=" + result_code);
                                        setResult(RESULT_OK, intent);
                                        finish();
                                    }
                                } else {
                                    isclickregist = true;
                                    Toast.makeText(ChangePasswordActivity.this, "两次输入密码不一致，请重新输入！", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                isclickregist = true;
                                Toast.makeText(ChangePasswordActivity.this, "请输入6-12位字符组合！", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    break;
            }
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("ChangePasswordActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("ChangePasswordActivity");
        MobclickAgent.onPause(this);
    }
}

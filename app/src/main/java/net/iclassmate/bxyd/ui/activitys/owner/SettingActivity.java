package net.iclassmate.bxyd.ui.activitys.owner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.bean.owner.Update;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.ui.activitys.LoginActivity;
import net.iclassmate.bxyd.utils.JsonUtils;

import java.io.IOException;

import io.rong.imlib.RongIMClient;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by xydbj on 2016.6.13.
 */
public class SettingActivity extends Activity implements View.OnClickListener {

    private ImageView owner_setting_iv_back, setting_iv_tuichuzhanghao, setting_iv_new;
    private TextView owner_setting_tv_back;
    private RelativeLayout setting_rl_msg, setting_rl_changepassword, setting_rl_bangphone, setting_rl_privacy, setting_rl_update, setting_rl_aboutus, setting_rl_feedback;
    private ToggleButton setting_tb_message;

    private SharedPreferences sharedPreferences;

    private boolean flag, updateflag, isSound;

    private static final int REQUEST_CODE = 1;
    private String nowversionName;
    private Update update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        sharedPreferences = SettingActivity.this.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        isSound = sharedPreferences.getBoolean(Constant.HAS_RING, true);
        Log.i("info", "每次进入SettingActivity时的isSound=" + isSound);

        initView();
        nowversionName = getNowVersionInfo();
        Log.i("info", "获取当前版本：" + nowversionName);
        checkVersionInfo(nowversionName);
    }

    public void initView() {
        setting_iv_new = (ImageView) findViewById(R.id.setting_iv_new);
        setting_tb_message = (ToggleButton) findViewById(R.id.setting_tb_message);
        setting_rl_msg = (RelativeLayout) findViewById(R.id.setting_rl_msg);
        setting_rl_changepassword = (RelativeLayout) findViewById(R.id.setting_rl_changepassword);
        setting_rl_bangphone = (RelativeLayout) findViewById(R.id.setting_rl_bangphone);
        setting_rl_privacy = (RelativeLayout) findViewById(R.id.setting_rl_privacy);
        setting_rl_update = (RelativeLayout) findViewById(R.id.setting_rl_update);
        setting_rl_aboutus = (RelativeLayout) findViewById(R.id.setting_rl_aboutus);
        setting_rl_feedback = (RelativeLayout) findViewById(R.id.setting_rl_feedback);
        setting_iv_tuichuzhanghao = (ImageView) findViewById(R.id.setting_iv_tuichuzhanghao);
        owner_setting_iv_back = (ImageView) findViewById(R.id.owner_setting_iv_back);
        owner_setting_tv_back = (TextView) findViewById(R.id.owner_setting_tv_back);

        setting_tb_message.setChecked(isSound);
        setting_tb_message.setOnCheckedChangeListener(onCheckedChangeListener);
        setting_rl_msg.setOnClickListener(this);
        setting_rl_changepassword.setOnClickListener(this);
        setting_rl_bangphone.setOnClickListener(this);
        setting_rl_privacy.setOnClickListener(this);
        setting_rl_update.setOnClickListener(this);
        setting_rl_aboutus.setOnClickListener(this);
        setting_rl_feedback.setOnClickListener(this);
        owner_setting_tv_back.setOnClickListener(this);
        owner_setting_iv_back.setOnClickListener(this);
        setting_iv_tuichuzhanghao.setOnClickListener(this);
    }

    public CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isSound) {
                Toast.makeText(getApplicationContext(), "消息提醒关闭", Toast.LENGTH_SHORT).show();
                Log.i("info", "点击按钮，isSound=" + isSound);
                sharedPreferences.edit().putBoolean(Constant.HAS_RING, false).apply();
            } else {
                Toast.makeText(getApplicationContext(), "消息提醒打开", Toast.LENGTH_SHORT).show();
                Log.i("info", "点击按钮，isSound=" + isSound);
                sharedPreferences.edit().putBoolean(Constant.HAS_RING, true).apply();
            }
            isSound = !isSound;
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_rl_msg:
                break;
            case R.id.setting_rl_changepassword:
                Intent intent_changepassword = new Intent(SettingActivity.this, ChangePasswordActivity.class);
                startActivityForResult(intent_changepassword, REQUEST_CODE);
                break;
            case R.id.setting_rl_bangphone:
                Intent intent_bangphone = new Intent(SettingActivity.this, BoundNowPhoneNumActivity.class);
                startActivity(intent_bangphone);
                break;
            case R.id.setting_rl_privacy:
                Intent intent_privacy = new Intent(SettingActivity.this, PrivacySettingActivity.class);
                startActivity(intent_privacy);
                break;
            case R.id.setting_rl_update:
                if (updateflag) {
                    Intent intent_update = new Intent(SettingActivity.this, CheckActivity.class);
                    intent_update.putExtra("version", update.getVersion());
                    intent_update.putExtra("size", update.getSize());
                    intent_update.putExtra("description", update.getUpdateDesc());
                    intent_update.putExtra("url", update.getUrl());
                    startActivity(intent_update);
                } else {
                    Toast.makeText(SettingActivity.this, "当前版本已是最新版本！", Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.setting_rl_aboutus:
                Intent intent_aboutus = new Intent(SettingActivity.this, AboutUsActivity.class);
                startActivity(intent_aboutus);
                break;
            case R.id.setting_rl_feedback:
                Intent intent_feedback = new Intent(SettingActivity.this, FeedBackActivity.class);
                startActivity(intent_feedback);
                break;
            case R.id.setting_iv_tuichuzhanghao:
                getAuthToken();
                break;
            //退出
            case R.id.owner_setting_tv_back:
            case R.id.owner_setting_iv_back:
                SettingActivity.this.finish();
                break;
        }
    }

    public void checkVersionInfo(final String nowversionName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                execute2(nowversionName);
            }
        }).start();
    }

    public void execute2(String nowversionName) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(Constant.UPDATE_URL + nowversionName + "?product=" + Constant.UPDATE_VERSIONNAME)
                .build();
//        Log.i("info", "请求新版本的url:" + Constant.UPDATE_URL + nowversionName + "?product=" + Constant.UPDATE_VERSIONNAMW);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
//                Log.i("info", "检测版本失败：" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Message msg = new Message();
                    msg.obj = response.body().string();
                    msg.what = 1;
                    mHandler.sendMessage(msg);
                }
            }
        });
    }

    public String getNowVersionInfo() {
        String versionName = null;
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            versionName = "V" + packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String result_code = data.getStringExtra("password");
//                Log.i("info", "修改用户密码返回的result_code=" + result_code);
                Toast.makeText(SettingActivity.this, "您的密码已被修改，请牢记！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getAuthToken() {
        String authToken = sharedPreferences.getString(Constant.AUTHTOKEN, "");
        flag = true;
        if (authToken != null) {
            Log.i("info", "注销用户，拿到的authToken:" + authToken);
            logout(authToken);
            RongIMClient.getInstance().disconnect();
            RongIMClient.getInstance().logout();
        } else {
            Toast.makeText(SettingActivity.this, "注销失败!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (flag) {
            sharedPreferences.edit().clear();
            sharedPreferences.edit().commit();
        }
        super.onDestroy();
    }

    public void logout(final String authToken) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                execute(authToken);
            }
        }).start();
    }

    public void execute(String authToken) {
//        MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("application/json;charset=utf-8");
        OkHttpClient okHttpClient = new OkHttpClient();
//        RequestBody body = RequestBody.create(MEDIA_TYPE_MARKDOWN,authToken);
        Request request = new Request.Builder()
                .addHeader("authCode", authToken)
                .get()
                .url(Constant.LOGOUT_URL)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("info", "注销用户失败：" + e.getMessage());
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
                case 1:
                    String str = (String) msg.obj;
                    if (str == null || str.equals("") || str.equals("null")) {
                        updateflag = false;
                    } else {
                        update = JsonUtils.StartUpdateJson(str);
                        Log.i("info", "获取最新版本：" + update.getVersion());
                        if (update != null && !update.getVersion().equals(nowversionName)) {
                            setting_iv_new.setVisibility(View.VISIBLE);
                            updateflag = true;
                        } else {
                            updateflag = false;
                        }
                    }
                    break;
                case 200:
                    Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    Toast.makeText(SettingActivity.this, "退出成功！", Toast.LENGTH_SHORT).show();
                    SettingActivity.this.finish();
                    break;
                case 401:
                case 402:
                case 403:
                    Toast.makeText(SettingActivity.this, "退出失败！", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SettingActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SettingActivity");
        MobclickAgent.onPause(this);
    }
}
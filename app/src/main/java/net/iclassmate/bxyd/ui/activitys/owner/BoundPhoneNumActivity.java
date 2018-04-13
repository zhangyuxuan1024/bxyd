
package net.iclassmate.bxyd.ui.activitys.owner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.utils.TimeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class BoundPhoneNumActivity extends Activity implements View.OnClickListener, OnTouchListener {
    private EditText boundphonenum_et_phone, boundphonenum_et_password, boundphonenum_et_verification;
    private ImageView boundphonenum_iv_submit, boundphonenum_iv_phone_cross, boundphonenum_iv_password_cross, boundphonenum_iv_back;
    private TextView boundphonenum_tv_back;
    private String phone, password, verification, userId;
    private Button boundphonenum_iv_huoqu_pressed;
    private SharedPreferences sharedPreferences;
    private boolean isRegister;

    private long last_click_time;
    private boolean isclickregist;
    public static final String action = "BoundPhoneNumActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boundphonenum);
        initView();
    }

    //初始化各种View
    public void initView() {
        boundphonenum_et_phone = (EditText) findViewById(R.id.boundphonenum_et_phone);
        boundphonenum_et_password = (EditText) findViewById(R.id.boundphonenum_et_password);
        boundphonenum_et_verification = (EditText) findViewById(R.id.boundphonenum_et_verification);
        boundphonenum_iv_huoqu_pressed = (Button) findViewById(R.id.boundphonenum_iv_huoqu_pressed);
        boundphonenum_iv_submit = (ImageView) findViewById(R.id.boundphonenum_iv_submit);
        boundphonenum_iv_password_cross = (ImageView) findViewById(R.id.boundphonenum_iv_password_cross);
        boundphonenum_iv_phone_cross = (ImageView) findViewById(R.id.boundphonenum_iv_phone_cross);
        boundphonenum_tv_back = (TextView) findViewById(R.id.boundphonenum_tv_back);
        boundphonenum_iv_back = (ImageView) findViewById(R.id.boundphonenum_iv_back);

        boundphonenum_et_phone.addTextChangedListener(phonetextchange);
        boundphonenum_et_password.addTextChangedListener(passwordtextchange);

        boundphonenum_iv_huoqu_pressed.setOnClickListener(this);
        boundphonenum_iv_submit.setOnClickListener(this);
        boundphonenum_iv_password_cross.setOnClickListener(this);
        boundphonenum_iv_phone_cross.setOnClickListener(this);
        boundphonenum_tv_back.setOnClickListener(this);
        boundphonenum_iv_back.setOnClickListener(this);

        boundphonenum_et_phone.setOnTouchListener(this);
        boundphonenum_et_password.setOnTouchListener(this);
        boundphonenum_et_verification.setOnTouchListener(this);

        isclickregist = true;

        getData();
    }

    public TextWatcher passwordtextchange = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (boundphonenum_et_password.getText().toString() != null && !boundphonenum_et_password.getText().toString().equals("")) {
                boundphonenum_iv_password_cross.setVisibility(View.VISIBLE);
            } else {
                boundphonenum_iv_password_cross.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    public TextWatcher phonetextchange = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (boundphonenum_et_phone.getText().toString() != null && !boundphonenum_et_phone.getText().toString().equals("")) {
                boundphonenum_iv_phone_cross.setVisibility(View.VISIBLE);
                if (s.length() == 11) {
                    boundphonenum_iv_huoqu_pressed.setBackgroundResource(R.mipmap.bt_huoqu);
                } else {
                    boundphonenum_iv_huoqu_pressed.setBackgroundResource(R.mipmap.bt_huoqu_pressed);
                }
            } else {
                boundphonenum_iv_phone_cross.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    //得到用户填写的手机号，密码，验证码
    public void getData() {
        sharedPreferences = BoundPhoneNumActivity.this.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        userId = sharedPreferences.getString(Constant.ID_USER, "");
    }

    //判断手机号是否是可用
    public boolean isAvailablePhone(String phonenum) {
        String regExp = "^(13|15|17|18)\\d{9}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(phonenum);
        return m.find();
    }

    //判断手机号是否被注册
    public void PhoneIsRegister(final String phone) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                execute(phone);
            }
        }).start();
    }

    //发送手机验证码
    public void sendVerification(final String phone) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                execute2(phone);
            }
        }).start();
    }

    //发送手机号，密码，验证码到服务器,从而绑定新手机号
    public void BoundPhone(final String phone, final String password, final String verification, final String userId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                execute3(phone, password, verification, userId);
            }
        }).start();
    }

    public void execute3(final String phone, String password, String verification, String userId) {
        MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("application/json;charset=utf-8");
        OkHttpClient okHttpClient = new OkHttpClient();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("phone", phone);
            jsonObject.put("pwd", password);
            jsonObject.put("userId", userId);
            jsonObject.put("verification", verification);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json = jsonObject.toString();
        Log.i("info", "绑定新手机号发送的json:" + json);
        RequestBody body = RequestBody.create(MEDIA_TYPE_MARKDOWN, json);
        Request request = new Request.Builder()
                .post(body)
                .url(Constant.CHANGEPHONE)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("info", "绑定新手机号，失败：" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message msg = new Message();
                msg.what = response.code();
                Log.i("info", "绑定新手机号，返回的code：" + response.code());
                msg.obj = response.body().string();
                mHandler.sendMessage(msg);
                if (response.isSuccessful()) {
                    sharedPreferences.edit().putString(Constant.USER_PHONE, phone).apply();
                }
            }
        });
    }

    public void execute2(String phone) {
        MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("application/json;charset=utf-8");
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody body = RequestBody.create(MEDIA_TYPE_MARKDOWN, phone);
        Request request = new Request.Builder()
                .put(body)
                .url(Constant.VERIFICATION_URL + "/" + phone)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("info", "绑定新手机号，获取验证码失败：" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int result_code = response.code();
                Log.i("info", "绑定新手机号，获取验证码result_code：" + result_code);
                Message msg = new Message();
                msg.what = 1;
                mHandler.sendMessage(msg);
            }
        });
    }

    public void execute(String phone) {
        MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("application/json;charset=utf-8");
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_MARKDOWN, phone);

        Request request = new Request.Builder()
                .post(requestBody)
                .url(Constant.PHONEISREGISTER_URL + phone)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("info", "判断手机号是否被注册，失败：" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message msg = new Message();
                String str = response.body().string();
                Log.i("info", "判断手机号是否被注册，返回的json串：" + str);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(str);
                    boolean isRegister = jsonObject.getBoolean("exist");
                    msg.obj = isRegister;
                    msg.what = 2;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mHandler.sendMessage(msg);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.boundphonenum_iv_phone_cross:
                boundphonenum_et_phone.setText("");
                break;
            case R.id.boundphonenum_iv_password_cross:
                boundphonenum_et_password.setText("");
                break;
            //点击获取验证码：
            case R.id.boundphonenum_iv_huoqu_pressed:

                boolean isAvailablePhone = isAvailablePhone(boundphonenum_et_phone.getText().toString().trim());
                if (isAvailablePhone) {
                    //判断手机号是否已被注册
                    PhoneIsRegister(boundphonenum_et_phone.getText().toString().trim());
                    Log.i("info", "点击获取验证码时的isRegister=" + isRegister);

                } else {
                    Toast.makeText(BoundPhoneNumActivity.this, "请输入正确的手机号！", Toast.LENGTH_SHORT).show();
                }
                break;
            //点击提交信息，绑定新手机号：
            case R.id.boundphonenum_iv_submit:
                Log.i("info", "点击提交按钮，isclickregist=" + isclickregist);
                if (!isclickregist || System.currentTimeMillis() - last_click_time < 3000) {
                    return;
                }
                last_click_time = System.currentTimeMillis();
                isclickregist = false;

                if (boundphonenum_et_phone.getText().toString().trim() == null || boundphonenum_et_password.getText().toString().trim() == null || boundphonenum_et_verification.getText().toString().trim() == null
                        || boundphonenum_et_phone.getText().toString().trim().equals("") || boundphonenum_et_password.getText().toString().trim().equals("") || boundphonenum_et_verification.getText().toString().trim().equals("")
                        || boundphonenum_et_phone.getText().toString().trim().equals("null") || boundphonenum_et_password.getText().toString().trim().equals("null") || boundphonenum_et_verification.getText().toString().trim().equals("null")) {

                    isclickregist = true;
                    Toast.makeText(BoundPhoneNumActivity.this, "请完善信息", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i("info", "phone=" + boundphonenum_et_phone.getText().toString().trim() + ",password=" + boundphonenum_et_password.getText().toString().trim() + ",verification=" + boundphonenum_et_verification.getText().toString().trim() + ",userId=" + userId);
                    BoundPhone(boundphonenum_et_phone.getText().toString().trim(), boundphonenum_et_password.getText().toString().trim(), boundphonenum_et_verification.getText().toString().trim(), userId);
                    isclickregist = true;
                }
                break;
            case R.id.boundphonenum_tv_back:
            case R.id.boundphonenum_iv_back:
                finish();
                break;
        }
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    boundphonenum_iv_huoqu_pressed.setBackgroundResource(R.mipmap.ic_chongshi);
                    boundphonenum_iv_huoqu_pressed.setFocusable(false);
                    TimeUtils time = new TimeUtils(boundphonenum_iv_huoqu_pressed);
                    time.RunTimer();
                    break;
                case 2:
                    isRegister = (boolean) msg.obj;
                    Log.i("info", "isRegister=" + isRegister);
                    if (isRegister == false) {
                        //向新手机号发送验证码：
                        sendVerification(boundphonenum_et_phone.getText().toString().trim());
                    } else if (isRegister == true) {
                        Toast.makeText(BoundPhoneNumActivity.this, "手机号已被注册", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 200:
                    Toast.makeText(BoundPhoneNumActivity.this, "绑定新手机号成功!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(action);
                    sendBroadcast(intent);

                    finish();
                    break;
                case 400:
                    String str = (String) msg.obj;
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(str);
                        String reason = jsonObject.getString("reason");
                        Toast.makeText(BoundPhoneNumActivity.this, reason, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.boundphonenum_et_phone:
                if (boundphonenum_et_phone.getText().length() != 0) {
                    boundphonenum_iv_phone_cross.setVisibility(View.VISIBLE);
                }
                boundphonenum_iv_password_cross.setVisibility(View.INVISIBLE);
                break;
            case R.id.boundphonenum_et_password:
                if (boundphonenum_et_password.getText().length() != 0) {
                    boundphonenum_iv_password_cross.setVisibility(View.VISIBLE);
                }
                boundphonenum_iv_phone_cross.setVisibility(View.INVISIBLE);
                break;
            case R.id.boundphonenum_et_verification:
                boundphonenum_iv_phone_cross.setVisibility(View.INVISIBLE);
                boundphonenum_iv_password_cross.setVisibility(View.INVISIBLE);
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("BoundPhoneNumActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("BoundPhoneNumActivity");
        MobclickAgent.onPause(this);
    }
}

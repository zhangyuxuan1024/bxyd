package net.iclassmate.bxyd.ui.activitys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.utils.HttpManager;
import net.iclassmate.bxyd.utils.NetWorkUtils;
import net.iclassmate.bxyd.utils.TimeUtils;
import net.iclassmate.bxyd.utils.UIUtils;
import net.iclassmate.bxyd.view.TitleBar;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgetPwActivity extends Activity implements TitleBar.TitleOnClickListener, View.OnClickListener, View.OnTouchListener {
    private TitleBar titleBar;
    private Context mContext;
    private int resultCode;
    private int responseCode;
    private HttpManager httpManager;
    private TextView code_success;
    private boolean isTimeRun;
    private Button btn_forgetPW_code, btn_forgetPw;
    private EditText et_phone, et_code, et_pwd, et_affrim;
    private Button phone_delete, code_delete, pwd_delete, affrim_delete;
    private String phone, code, pwd, affrim, register_phone;
    public static final int RECEIVE_CODE = 1;
    public static final int RESET_PASSWORD = 2;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case RECEIVE_CODE:
                    if (responseCode == 200) {
                        isTimeRun = true;
                        btn_forgetPW_code.setBackgroundResource(R.mipmap.ic_chongshi);
                        TimeUtils time = new TimeUtils(btn_forgetPW_code);
                        time.RunTimer();
                        code_success.setVisibility(View.VISIBLE);
                    } else if (responseCode == 201) {
                        Toast.makeText(UIUtils.getContext(), "手机号已经被使用", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UIUtils.getContext(), "获取验证码失败，请稍候再试", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case RESET_PASSWORD:
                    if (resultCode == 200) {
                        Toast.makeText(UIUtils.getContext(), "重置密码成功，请重新登录", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.putExtra("uname", et_phone.getText().toString().trim());
                        intent.putExtra("upass", et_pwd.getText().toString().trim());
                        setResult(RESULT_OK, intent);
                        finish();
                    } else if (resultCode == 400) {
                        Toast.makeText(UIUtils.getContext(), "手机验证码错误", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UIUtils.getContext(), "重置密码失败，请稍候再试", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 3:
                    String ret = (String) msg.obj;
                    if (ret.contains(",")) {
                        String[] data = ret.split(",");
                        try {
                            int code = Integer.parseInt(data[0]);
                            if (code == 200) {
                                String json = data[1];
                                JSONObject object = new JSONObject(json);
                                boolean exist = object.getBoolean("exist");
                                if (!exist) {
                                    Toast.makeText(UIUtils.getContext(), "手机号未注册", Toast.LENGTH_SHORT).show();
                                } else {
                                    final String phone = data[2];
                                    final String verificationUrl = Constant.VERIFICATION_URL + "/" + phone;
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            responseCode = httpManager.httpUrlConnectionPut(verificationUrl, phone);
                                            mHandler.sendEmptyMessage(RECEIVE_CODE);
                                        }
                                    }).start();
                                }
                            }
                        } catch (Exception e) {

                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_forget_pw);
        mContext = this;
        initView();
        initListener();
        initData();
    }

    private void initView() {
        titleBar = (TitleBar) findViewById(R.id.forgetPw_title_bar);
        titleBar.setLeftIcon(R.mipmap.ic_fanhui);
        titleBar.setTitle("找回密码");
        titleBar.setTitleClickListener(this);

        httpManager = new HttpManager();
        code_success = (TextView) findViewById(R.id.forgetPw_code_success);
        et_affrim = (EditText) findViewById(R.id.forgetPw_affrim);
        et_code = (EditText) findViewById(R.id.forgetPw_code);
        et_phone = (EditText) findViewById(R.id.forgetPw_phoneNumber);
        et_pwd = (EditText) findViewById(R.id.forgetPw_password);
        phone_delete = (Button) findViewById(R.id.forgetPw_phone_delete);
        code_delete = (Button) findViewById(R.id.forgetPw_code_delete);
        pwd_delete = (Button) findViewById(R.id.forgetPw_password_delete);
        affrim_delete = (Button) findViewById(R.id.forgetPw_affrim_delete);
        btn_forgetPw = (Button) findViewById(R.id.forgetPw_btn);
        btn_forgetPW_code = (Button) findViewById(R.id.forgetPw_code_btn);
        //出现EditText删除按钮
        initDeleteIcon();
    }

    private void initDeleteIcon() {
        et_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    phone_delete.setVisibility(View.VISIBLE);
                    if (!isTimeRun) {
                        if (s.length() == 11) {
                            btn_forgetPW_code.setBackgroundResource(R.drawable.huoqu_selector);
                        } else {
                            btn_forgetPW_code.setBackgroundResource(R.mipmap.bt_huoqu_moren);
                        }
                    }
                } else {
                    phone_delete.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    code_delete.setVisibility(View.VISIBLE);
                } else {
                    code_delete.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    pwd_delete.setVisibility(View.VISIBLE);
                } else {
                    pwd_delete.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_affrim.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    affrim_delete.setVisibility(View.VISIBLE);
                } else {
                    affrim_delete.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initListener() {
        et_phone.setOnTouchListener(this);
        et_code.setOnTouchListener(this);
        et_pwd.setOnTouchListener(this);
        et_affrim.setOnTouchListener(this);

        btn_forgetPW_code.setOnClickListener(this);
        btn_forgetPw.setOnClickListener(this);
        phone_delete.setOnClickListener(this);
        code_delete.setOnClickListener(this);
        pwd_delete.setOnClickListener(this);
        affrim_delete.setOnClickListener(this);
    }

    private void initData() {

    }

    @Override
    public void leftClick() {
        finish();
    }

    @Override
    public void rightClick() {

    }

    @Override
    public void titleClick() {

    }

    @Override
    public void innerleftClick() {

    }

    @Override
    public void innerRightClick() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.forgetPw_btn:
                //{"name":"","phone":"","password":"","verification":""}
                register_phone = et_phone.getText().toString().trim();
                code = et_code.getText().toString().trim();
                pwd = et_pwd.getText().toString().trim();
                affrim = et_affrim.getText().toString().trim();
                if (!NetWorkUtils.isNetworkAvailable(UIUtils.getContext())) {
                    Toast.makeText(UIUtils.getContext(), mContext.getResources().getString(R.string.alert_msg_check_net), Toast.LENGTH_SHORT).show();
                } else {
                    if (register_phone.equals("") || code.equals("") || pwd.equals("") || affrim.equals("")) {
                        Toast.makeText(UIUtils.getContext(), "请确保填写信息完整，不能为空", Toast.LENGTH_SHORT).show();
                    } else {
                        if (code.length() == 6) {
                            if (pwd.length() >= 6 && pwd.length() <= 12) {
                                if (pwd.equals(affrim)) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            resultCode = httpManager.resetPassword(register_phone, affrim, code);
                                            mHandler.sendEmptyMessage(RESET_PASSWORD);
                                        }
                                    }).start();
                                } else {
                                    Toast.makeText(UIUtils.getContext(), "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(UIUtils.getContext(), "请输入6-12位的密码", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(UIUtils.getContext(), "验证码不正确", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
            case R.id.forgetPw_code_btn:
                phone = et_phone.getText().toString().trim();
                if (!NetWorkUtils.isNetworkAvailable(UIUtils.getContext())) {
                    Toast.makeText(UIUtils.getContext(), mContext.getResources().getString(R.string.alert_msg_check_net), Toast.LENGTH_SHORT).show();
                } else {
                    final String value = phone;
                    Pattern p = Pattern.compile("^((13[0-9])|(14[0-9])|(15[^4,\\D])|(18[0-9])|(17[0-9]))\\d{8}$");
                    Matcher m = p.matcher(value);
                    if (!m.matches()) {
                        Toast.makeText(UIUtils.getContext(), "输入手机号不正确", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String ret = httpManager.isRegister(phone);
                            ret = ret + "," + phone;
                            Message message = new Message();
                            message.what = 3;
                            message.obj = ret;
                            mHandler.sendMessage(message);
                        }
                    }).start();
                }

                break;
            case R.id.forgetPw_phone_delete:
                et_phone.setText("");
                break;
            case R.id.forgetPw_affrim_delete:
                et_affrim.setText("");
                break;
            case R.id.forgetPw_code_delete:
                et_code.setText("");
                break;
            case R.id.forgetPw_password_delete:
                et_pwd.setText("");
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {

            case R.id.forgetPw_phoneNumber:
                if (et_phone.getText().length() != 0) {
                    phone_delete.setVisibility(View.VISIBLE);
                }
                pwd_delete.setVisibility(View.INVISIBLE);
                code_delete.setVisibility(View.INVISIBLE);
                affrim_delete.setVisibility(View.INVISIBLE);
                code_success.setVisibility(View.INVISIBLE);
                break;
            case R.id.forgetPw_code:
                if (et_code.getText().length() != 0) {
                    code_delete.setVisibility(View.VISIBLE);
                }
                phone_delete.setVisibility(View.INVISIBLE);
                pwd_delete.setVisibility(View.INVISIBLE);
                affrim_delete.setVisibility(View.INVISIBLE);
                break;
            case R.id.forgetPw_password:
                if (et_pwd.getText().length() != 0) {
                    pwd_delete.setVisibility(View.VISIBLE);
                }
                phone_delete.setVisibility(View.INVISIBLE);
                code_delete.setVisibility(View.INVISIBLE);
                affrim_delete.setVisibility(View.INVISIBLE);
                code_success.setVisibility(View.INVISIBLE);
                break;
            case R.id.forgetPw_affrim:
                if (et_affrim.getText().length() != 0) {
                    affrim_delete.setVisibility(View.VISIBLE);
                }
                phone_delete.setVisibility(View.INVISIBLE);
                pwd_delete.setVisibility(View.INVISIBLE);
                code_delete.setVisibility(View.INVISIBLE);
                code_success.setVisibility(View.INVISIBLE);
                break;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("ForgetPwActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("ForgetPwActivity");
        MobclickAgent.onPause(this);
    }
}
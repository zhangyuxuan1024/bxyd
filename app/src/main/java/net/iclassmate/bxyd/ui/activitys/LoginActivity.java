package net.iclassmate.bxyd.ui.activitys;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.application.MyApplication;
import net.iclassmate.bxyd.bean.LoginResult;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.utils.DataCallback;
import net.iclassmate.bxyd.utils.HttpManager;
import net.iclassmate.bxyd.utils.NetWorkUtils;
import net.iclassmate.bxyd.utils.UIUtils;
import net.iclassmate.bxyd.view.MyScrollView;
import net.iclassmate.bxyd.view.emotion.Keyboard;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imlib.RongIMClient;


public class LoginActivity extends Activity implements View.OnClickListener, DataCallback, View.OnTouchListener,
        View.OnLayoutChangeListener, MyScrollView.ScrollViewListener {
    private Button mLoginButton;
    private Context mContext;
    private EditText et_login_userCode, et_login_password;
    private Handler mHandler = new Handler();
    private ImageView mImageBackground, logo, user_clean, pass_clean;
    private TextView mRegister, mForgetPw;
    private View loadingbar;
    Drawable drawable;

    private LinearLayout activityRootView;
    private FrameLayout frameLayout_img_bg;
    private LinearLayout.LayoutParams params;

    private String userName, password;

    private HttpManager httpManager;
    private String token = null;
    private SharedPreferences sp, sharedPreferences;
    public static final int REQ_REGIST = 1;

    private long last_clicl_time;

    private MyScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        initView();
    }

    private void initView() {
        mLoginButton = (Button) findViewById(R.id.bt_login);
        et_login_password = (EditText) findViewById(R.id.login_password);
        et_login_userCode = (EditText) findViewById(R.id.login_userCode);
        mImageBackground = (ImageView) findViewById(R.id.login_bg);
        mRegister = (TextView) findViewById(R.id.login_register);
        mForgetPw = (TextView) findViewById(R.id.login_forget_password);
        activityRootView = (LinearLayout) this.findViewById(R.id.layout);
        logo = (ImageView) findViewById(R.id.login_logo);
        sp = this.getSharedPreferences(Keyboard.SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        sharedPreferences = this.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(Constant.LOGIN_TOKEN, "").apply();
        loadingbar = findViewById(R.id.loadingbar);

        user_clean = (ImageView) findViewById(R.id.img_user_clean);
        pass_clean = (ImageView) findViewById(R.id.img_pass_clean);
        httpManager = new HttpManager(this);
        frameLayout_img_bg = (FrameLayout) findViewById(R.id.root_layout);
        params = (LinearLayout.LayoutParams) frameLayout_img_bg.getLayoutParams();

        mLoginButton.setOnClickListener(this);
        mRegister.setOnClickListener(this);
        mForgetPw.setOnClickListener(this);
        user_clean.setOnClickListener(this);
        pass_clean.setOnClickListener(this);
        et_login_password.setOnTouchListener(this);
        et_login_userCode.setOnTouchListener(this);

        scrollView = (MyScrollView) findViewById(R.id.login_scrollview);
        scrollView.setScrollViewListener(this);
        setText();

        userName = sharedPreferences.getString(Constant.LOGIN_NUMBER, "");
        password = sharedPreferences.getString(Constant.LOGIN_PASS, "");
        if (!userName.equals("") && !password.equals("")) {
            et_login_userCode.setText(userName);
            et_login_password.setText(password);
            user_clean.setVisibility(View.VISIBLE);
            pass_clean.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //添加layout大小发生改变监听器
        this.activityRootView.addOnLayoutChangeListener(this);

        MobclickAgent.onPageStart("LoginActivity");
        MobclickAgent.onResume(this);
    }

    private void initSoftInputHeight() {
        Rect r = new Rect();
        /**
         * decorView是window中的最顶层view，可以从window中通过getDecorView获取到decorView。
         * 通过decorView获取到程序显示的区域，包括标题栏，但不包括状态栏。
         */
        this.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        //获取屏幕的高度
        int screenHeight = this.getWindow().getDecorView().getRootView().getHeight();
        //计算软件盘的高度
        int softInputHeight = screenHeight - r.bottom;

        /**
         * 某些Android版本下，没有显示软键盘时减出来的高度总是144，而不是零，
         * 这是因为高度是包括了虚拟按键栏的(例如华为系列)，所以在API Level高于20时，
         * 我们需要减去底部虚拟按键栏的高度（如果有的话）
         */
        if (Build.VERSION.SDK_INT >= 20) {
            // When SDK Level >= 20 (Android L), the softInputHeight will contain the height of softButtonsBar (if has)
            softInputHeight = softInputHeight - getSoftButtonsBarHeight();
        }

        if (softInputHeight < 0) {
//	            LogUtils.w("EmotionKeyboard--Warning: value of softInputHeight is below zero!");
        }
        //存一份到本地
        if (softInputHeight > 0) {
            sp.edit().putInt(Keyboard.SHARE_PREFERENCE_SOFT_INPUT_HEIGHT, softInputHeight).apply();
        }
        //Log.e("LoginActivity", "--------" + softInputHeight);
    }

    /**
     * 底部虚拟按键栏的高度
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private int getSoftButtonsBarHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        //这个方法获取可能不是真实屏幕的高度
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        //获取当前屏幕的真实高度
        this.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_login:
                if (System.currentTimeMillis() - last_clicl_time < 3000) {
                    return;
                }
                last_clicl_time = System.currentTimeMillis();
                login();
                break;
            case R.id.login_register:
                Intent i2 = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(i2, REQ_REGIST);
                break;
            case R.id.login_forget_password:
                Intent i3 = new Intent(LoginActivity.this, ForgetPwActivity.class);
                startActivityForResult(i3, REQ_REGIST);
                break;
            case R.id.img_user_clean:
                et_login_userCode.setText("");
                user_clean.setVisibility(View.INVISIBLE);
            case R.id.img_pass_clean:
                et_login_password.setText("");
                pass_clean.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.login_userCode:
                if (et_login_userCode.getText().toString().length() > 0) {
                    user_clean.setVisibility(View.VISIBLE);
                } else {
                    user_clean.setVisibility(View.GONE);
                }
                pass_clean.setVisibility(View.GONE);
                break;
            case R.id.login_password:
                if (et_login_password.getText().toString().length() > 0) {
                    pass_clean.setVisibility(View.VISIBLE);
                } else {
                    pass_clean.setVisibility(View.GONE);
                }
                user_clean.setVisibility(View.GONE);
                break;
        }
        return false;
    }

    private void login() {
        //请求本地服务器  得到token
        userName = et_login_userCode.getText().toString().trim();
        password = et_login_password.getText().toString().trim();

        if (!NetWorkUtils.isNetworkAvailable(UIUtils.getContext())) {
            Toast.makeText(UIUtils.getContext(), "请检查您的网络连接！", Toast.LENGTH_SHORT).show();
        } else {
            if (userName.equals("") || password.equals("")) {
                Toast.makeText(UIUtils.getContext(), "帐号或密码不能为空", Toast.LENGTH_SHORT).show();
            } else {
                if (password.length() <= 12 && password.length() >= 6) {
                    loadingbar.setVisibility(View.VISIBLE);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            httpManager.loginGetToken(userName, password);
                        }
                    }).start();
                } else {
                    Toast.makeText(UIUtils.getContext(), "密码错误", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    /**
     * 连接融云服务器
     *
     * @param token
     */
    private void connect(String token) {
        if (getApplicationInfo().packageName.equals(MyApplication.getCurProcessName(getApplicationContext()))) {
            /**
             * IMKit SDK调用第二步,建立与服务器的连接
             */
            RongIMClient.connect(token, new RongIMClient.ConnectCallback() {
                /**
                 * Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App Server 重新请求一个新的 Token
                 */
                @Override
                public void onTokenIncorrect() {
                    loadingbar.setVisibility(View.INVISIBLE);
                    Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                }

                /**
                 * 连接融云成功
                 * @param userid 当前 token
                 */
                @Override
                public void onSuccess(String userid) {
                    sharedPreferences.edit().putString(Constant.LOGIN_NUMBER, userName).commit();
                    sharedPreferences.edit().putString(Constant.LOGIN_PASS, password).commit();
                    loadingbar.setVisibility(View.INVISIBLE);
                    MobclickAgent.onProfileSignIn(userid);
                    Intent i1 = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i1);
                    finish();
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                }

                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    loadingbar.setVisibility(View.INVISIBLE);
                    Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, final int bottom, int oldLeft, int oldTop, int oldRight, final int oldBottom) {
        if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > 100)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            logo.clearAnimation();
                            scrollView.smoothScrollTo(0, oldBottom - bottom + 60);
                            AlphaAnimation animation = new AlphaAnimation(1.0f, 0.0f);
                            animation.setDuration(200);
                            animation.setFillAfter(true);
                            logo.setAnimation(animation);
                            animation.start();
                        }
                    });
                }
            }).start();

            //本地记录软件盘的高度
            initSoftInputHeight();
        } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > 100)) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    logo.clearAnimation();
                    scrollView.smoothScrollTo(0, 0);
                    AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
                    animation.setDuration(200);
                    animation.setFillAfter(true);
                    logo.setAnimation(animation);
                    animation.start();
                }
            });
        }
    }

    @Override
    public void sendData(Object object) {
        String result = object.toString();
        if (!NetWorkUtils.isNetworkAvailable(UIUtils.getContext())) {
            Toast.makeText(UIUtils.getContext(), "请检查您的网络连接！", Toast.LENGTH_SHORT).show();
            loadingbar.setVisibility(View.INVISIBLE);
            return;
        }
        if (result.equals("404")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!NetWorkUtils.isNetworkAvailable(mContext)) {
                        Toast.makeText(UIUtils.getContext(), "请检查您的网络连接！", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UIUtils.getContext(), "服务器繁忙，请稍候再试", Toast.LENGTH_SHORT).show();
                    }
                    loadingbar.setVisibility(View.INVISIBLE);
                }
            });

        } else if (result.equals("400")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(UIUtils.getContext(), "帐号或密码错误", Toast.LENGTH_SHORT).show();
                    loadingbar.setVisibility(View.INVISIBLE);
                }
            });

        } else if (object instanceof LoginResult) {
            LoginResult loginResult = (LoginResult) object;
            int code = loginResult.getCode();
            String ret = loginResult.getResult();
            if (code == 200) {
                JSONObject json = null;
                try {
                    json = new JSONObject(ret);
                    String authtoken = json.getString("authToken");
                    boolean hasFriends = json.getBoolean("hasFriends");
                    String icon = json.getString("icon");
                    String name = json.getString("name");
                    token = json.getString("ryToken");
                    String spaceId = json.getString("spaceId");
                    String userCode = json.getString("userCode");
                    String userId = json.getString("userId");
                    String userType = json.optString("userType");
                    String phone = json.getString("phone");
//                    Log.i("info", "登陆返回=" + ret);
                    sharedPreferences.edit().putString(Constant.AUTHTOKEN, authtoken).apply();
                    sharedPreferences.edit().putBoolean(Constant.HAS_FRIEND, hasFriends).apply();
                    sharedPreferences.edit().putString(Constant.USER_ICON, icon).apply();
                    sharedPreferences.edit().putString(Constant.USER_NAME, name).apply();
                    sharedPreferences.edit().putString(Constant.LOGIN_TOKEN, token).apply();
                    sharedPreferences.edit().putString(Constant.ID_SPACE, spaceId).apply();
                    sharedPreferences.edit().putString(Constant.USER_CODE, userCode).apply();
                    sharedPreferences.edit().putString(Constant.ID_USER, userId).apply();
                    sharedPreferences.edit().putString(Constant.ID_USERTYPE, userType).apply();
                    sharedPreferences.edit().putString(Constant.USER_PHONE, phone).apply();
                    //连接融云服务器
                    connect(token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (code == 8036) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UIUtils.getContext(), "帐号或密码错误", Toast.LENGTH_SHORT).show();
                        loadingbar.setVisibility(View.INVISIBLE);
                    }
                });
            } else if (code == 8044) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UIUtils.getContext(), "账号还未激活", Toast.LENGTH_SHORT).show();
                        loadingbar.setVisibility(View.INVISIBLE);
                    }
                });
            } else if (code == 8999) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UIUtils.getContext(), "用户不存在", Toast.LENGTH_SHORT).show();
                        loadingbar.setVisibility(View.INVISIBLE);
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UIUtils.getContext(), "帐号或密码错误", Toast.LENGTH_SHORT).show();
                        loadingbar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        } else {
            JSONObject json = null;
            try {
                json = new JSONObject(result);
                token = json.getString("ryToken");
                String userId = json.getString("userId");
                String name = json.getString("name");
                String authtoken = json.getString("authToken");
                String userType = json.optString("userType");
//                Log.i("info", "登陆返回=" + result);
                sharedPreferences.edit().putString(Constant.AUTHTOKEN, authtoken).apply();
                sharedPreferences.edit().putString(Constant.USER_NAME, name).apply();
                sharedPreferences.edit().putString(Constant.ID_USER, userId).apply();
                sharedPreferences.edit().putString(Constant.ID_USERTYPE, userType).apply();
                sharedPreferences.edit().putString(Constant.LOGIN_TOKEN, token).apply();
                //连接融云服务器
                connect(token);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void setText() {
        et_login_userCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                et_login_password.setText("");
                if (s.length() > 0) {
                    user_clean.setVisibility(View.VISIBLE);
                } else {
                    user_clean.setVisibility(View.INVISIBLE);
                }
                pass_clean.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_login_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    pass_clean.setVisibility(View.VISIBLE);
                } else {
                    pass_clean.setVisibility(View.INVISIBLE);
                }
                user_clean.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_REGIST) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    userName = bundle.getString("uname");
                    password = bundle.getString("upass");
                    et_login_userCode.setText(userName);
                    et_login_password.setText(password);
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageStart("LoginActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    public void onScrollChanged(MyScrollView scrollView, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        //滑动到顶部
        if (scrollY == 0) {
            logo.clearAnimation();
            AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
            animation.setDuration(200);
            animation.setFillAfter(true);
            logo.setAnimation(animation);
            animation.start();
        }

        //滑动到底部
        View contentView = scrollView.getChildAt(0);
        if (contentView.getMeasuredHeight() <= scrollView.getScrollY() + scrollView.getHeight()) {
            logo.clearAnimation();
            AlphaAnimation animation = new AlphaAnimation(1.0f, 0.0f);
            animation.setDuration(200);
            animation.setFillAfter(true);
            logo.setAnimation(animation);
            animation.start();
        }
    }
}
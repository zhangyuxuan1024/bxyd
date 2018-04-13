package net.iclassmate.bxyd.ui.activitys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.application.MyApplication;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.utils.DataCallback;
import net.iclassmate.bxyd.utils.HttpManager;
import net.iclassmate.bxyd.utils.JsonUtils;
import net.iclassmate.bxyd.utils.UIUtils;

import java.util.List;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Group;

public class WelcomeActivity extends Activity implements DataCallback {
    private RelativeLayout welcomeRelativeLayout;
    private Boolean isFirst;
    private ImageView iv_welcome;
    private Context mContext;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private HttpManager httpManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);
        MobclickAgent.openActivityDurationTrack(false);
        mContext = this;
        initView();
    }

    private void initView() {
        welcomeRelativeLayout = (RelativeLayout) findViewById(R.id.layout_welcome);
        iv_welcome = (ImageView) findViewById(R.id.iv_welcome);
        sharedPreferences = mContext.getSharedPreferences(
                Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        //设置透明度动画
        setAlphaAnimation();

        httpManager = new HttpManager(this);
    }


    private void setAlphaAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.3f, 1.0f);
        alphaAnimation.setDuration(3000);
        welcomeRelativeLayout.setAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub
                isFirst = sharedPreferences.getBoolean(Constant.FIRST_LOGIN, true);
                if (isFirst) {
                    editor = sharedPreferences.edit();
                    editor.putBoolean(Constant.FIRST_LOGIN, false);
                    editor.apply();
                    Intent intent = new Intent(WelcomeActivity.this, GuideActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    String token = sharedPreferences.getString(Constant.LOGIN_TOKEN, "");
                    if (token != null && !token.equals("")) {
                        //连接融云服务器
                        connect(token);
                    } else {
                        Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
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
//                    loadingbar.setVisibility(View.INVISIBLE);
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

                /**
                 * 连接融云成功
                 * @param userid 当前 token
                 */
                @Override
                public void onSuccess(String userid) {
                    //refreshGroup(userid);
                    Toast.makeText(mContext, "登录成功", Toast.LENGTH_SHORT).show();
                    Intent i1 = new Intent(mContext, MainActivity.class);
                    startActivity(i1);
                    finish();
                }

                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    Toast.makeText(mContext, "登录失败", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                    finish();
//                    Intent i1 = new Intent(mContext, MainActivity.class);
//                    startActivity(i1);
//                    finish();
                }
            });
        }
    }

    /**
     * 刷新群组列表
     *
     * @param userid
     */
    private void refreshGroup(final String userid) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = httpManager.findAllGroup(userid);
                if (result.equals("404")) {

                } else {
                    List<Group> groupList = JsonUtils.jsonSysnGroup(result);
                    RongIMClient.getInstance().syncGroup(groupList, new RongIMClient.OperationCallback() {
                        @Override
                        public void onSuccess() {
                            //Toast.makeText(UIUtils.getContext(), "刷新群组成功", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {

                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void sendData(Object object) {
        String result = object.toString();
        if (result.equals("404")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(UIUtils.getContext(), "服务器繁忙，请稍候再试", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onPageStart("WelcomeActivity");
        //统计时长
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPageEnd("WelcomeActivity");
        MobclickAgent.onPause(this);
    }
}

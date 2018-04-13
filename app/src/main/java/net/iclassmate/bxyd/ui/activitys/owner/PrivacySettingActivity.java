package net.iclassmate.bxyd.ui.activitys.owner;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.bean.owner.SpaceInfo;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.utils.JsonUtils;

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
 * Created by xydbj on 2016.6.18.
 */
public class PrivacySettingActivity extends Activity implements View.OnClickListener {

    private ImageView privacysetting_iv_back, privacy_loading;
    private TextView privacysetting_tv_back;
    private ToggleButton tb_attention_me, tb_search_me, tb_my_resourse;
    private LinearLayout privacy_ll;

    private SharedPreferences sharedPreferences;
    private AnimationDrawable anim;
    private String spaceId, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacysetting);
        initView();
        getSpaceId();
    }

    public void initView() {
        privacy_loading = (ImageView) findViewById(R.id.privacy_loading);
        privacysetting_iv_back = (ImageView) findViewById(R.id.privacysetting_iv_back);
        privacysetting_tv_back = (TextView) findViewById(R.id.privacysetting_tv_back);
        tb_attention_me = (ToggleButton) findViewById(R.id.tb_attention_me);
        tb_search_me = (ToggleButton) findViewById(R.id.tb_search_me);
        tb_my_resourse = (ToggleButton) findViewById(R.id.tb_my_resourse);
        privacy_ll = (LinearLayout) findViewById(R.id.privacy_ll);

        privacysetting_iv_back.setOnClickListener(this);
        privacysetting_tv_back.setOnClickListener(this);
        tb_attention_me.setOnCheckedChangeListener(attention_me);
        tb_search_me.setOnCheckedChangeListener(search_me);
        tb_my_resourse.setOnCheckedChangeListener(my_resourse);

        sharedPreferences = PrivacySettingActivity.this.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        anim = (AnimationDrawable) privacy_loading.getBackground();
        anim.start();
    }

    public void setState(SpaceInfo spaceInfo) {
        tb_attention_me.setChecked(spaceInfo.getAuthority().isFocusMe());
        tb_search_me.setChecked(spaceInfo.getAuthority().isSearchMe());
        tb_my_resourse.setChecked(spaceInfo.getAuthority().isSearchMyresource());
    }

    public CompoundButton.OnCheckedChangeListener my_resourse = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            sendDate(spaceId, isChecked, 3);
        }
    };
    public CompoundButton.OnCheckedChangeListener search_me = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            sendDate(spaceId, isChecked, 2);
        }
    };
    public CompoundButton.OnCheckedChangeListener attention_me = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            sendDate(spaceId, isChecked, 1);
        }
    };

    public void getSpaceId() {
        userId = sharedPreferences.getString(Constant.ID_USER, "");
        spaceId = sharedPreferences.getString(Constant.ID_SPACE, "");
        Log.i("info", "隐私权限的spaceId:" + spaceId);
        if (spaceId == null) {
            return;
        }
        getDate(spaceId);
    }

    public void getDate(final String spaceId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                execute(spaceId);
            }
        }).start();
    }

    public void execute(String spaceId) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(Constant.PRIVACY_URL + spaceId + "?getOwner=false")
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("info", "隐私设置获取信息失败：" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message msg = new Message();
                String str = response.body().string();
                msg.obj = str;
                msg.what = 200;
                mHandler.sendMessage(msg);
            }
        });
    }

    public void sendDate(final String spaceId, final boolean flag, final int type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                execute2(spaceId, flag, type);
            }
        }).start();
    }

    public void execute2(String spaceId, boolean flag, int type) {
        MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("application/json;charset=utf-8");
        OkHttpClient okHttpClient = new OkHttpClient();
        String json = null;
        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject jsonObject1 = new JSONObject();
            if (type == 1) {
                jsonObject.put("focusMe", flag);
            } else if (type == 2) {
                jsonObject.put("searchMe", flag);
            } else if (type == 3) {
                jsonObject.put("searchMyresource", flag);
            }
            jsonObject1.put("authority", jsonObject);
            json = jsonObject1.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MEDIA_TYPE_MARKDOWN, json);
        Request request = new Request.Builder()
                .put(body)
                .url(Constant.PRIVACY_URL + spaceId + "?userid=" + userId)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("info", "隐私设置上传信息失败：" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message msg = new Message();
                msg.arg1 = response.code();
                msg.what = 1;
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
                    int index = msg.arg1;
                    Log.i("info", "修改状态返回的code:" + index);
                    break;
                case 200:
                    String str = (String) msg.obj;
                    SpaceInfo spaceInfo = JsonUtils.StartSpaceInfoJson(str);
                    Log.i("info", "获取隐私权限：" + spaceInfo.toString());
                    setState(spaceInfo);
                    anim.stop();
                    privacy_loading.setVisibility(View.INVISIBLE);
                    privacy_ll.setVisibility(View.VISIBLE);
                    break;
                default:
                    Log.i("info", "网络获取的隐私权限有异常");
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.privacysetting_tv_back:
            case R.id.privacysetting_iv_back:
                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("PrivacySettingActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("PrivacySettingActivity");
        MobclickAgent.onPause(this);
    }
}

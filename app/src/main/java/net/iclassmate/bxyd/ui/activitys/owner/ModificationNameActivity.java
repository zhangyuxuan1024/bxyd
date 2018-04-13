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
import android.view.View;
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
 * Created by xydbj on 2016.6.22.
 */
public class ModificationNameActivity extends Activity implements View.OnClickListener {
    private EditText modificationname_et;
    private ImageView modificationname_rl_iv_x, modificationname_iv_back;
    private TextView modificationname_tv_back, modificationname_tv_sure;
    private String uesrName, userId;
    private SharedPreferences sharedPreferences;
    public static final String acion = "CutActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificationname);
        Intent intent = getIntent();
        uesrName = intent.getStringExtra("userName");
        init();
    }

    public void init() {
        modificationname_et = (EditText) findViewById(R.id.modificationname_et);
        modificationname_rl_iv_x = (ImageView) findViewById(R.id.modificationname_iv_x);
        modificationname_iv_back = (ImageView) findViewById(R.id.modificationname_iv_back);
        modificationname_tv_back = (TextView) findViewById(R.id.modificationname_tv_back);
        modificationname_tv_sure = (TextView) findViewById(R.id.modificationname_tv_sure);
        modificationname_iv_back.setOnClickListener(this);
        modificationname_tv_back.setOnClickListener(this);
        modificationname_tv_sure.setOnClickListener(this);
        modificationname_rl_iv_x.setOnClickListener(onClickListener);
        modificationname_et.addTextChangedListener(textWatcher);
        modificationname_et.setText(uesrName);
        sharedPreferences = ModificationNameActivity.this.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        getUserId();
    }

    public View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            modificationname_et.setText("");
        }
    };
    public TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (modificationname_et.getText().toString() != null && !modificationname_et.getText().toString().equals("")) {
                modificationname_rl_iv_x.setVisibility(View.VISIBLE);
            } else {
                modificationname_rl_iv_x.setVisibility(View.INVISIBLE);
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.modificationname_iv_back:
            case R.id.modificationname_tv_back:
                ModificationNameActivity.this.finish();
                break;
            case R.id.modificationname_tv_sure:
                changeInformation(userId, modificationname_et.getText().toString());
                Intent intent_sure = new Intent();
                intent_sure.putExtra("userName--", modificationname_et.getText().toString());
                setResult(RESULT_OK, intent_sure);

                Intent intent = new Intent(acion);
                intent.putExtra("type",101);
                intent.putExtra("newName",modificationname_et.getText().toString());
                sendBroadcast(intent);

                finish();
                break;
        }
    }

    public void getUserId() {
        userId = sharedPreferences.getString(Constant.ID_USER, "");
        if (userId == null) {
            return;
        }
    }

    public void changeInformation(final String userId, final String newuserName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                execute(userId, newuserName);
            }
        }).start();
    }

    public void execute(String userId, String newuserName) {
        MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("application/json;charset=utf-8");
        OkHttpClient okHttpClient = new OkHttpClient();
        JSONObject object = null;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", newuserName);

            object = new JSONObject();
            object.put("userId", userId);
            object.put("userInfo", jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String json = object.toString();

        RequestBody body = RequestBody.create(MEDIA_TYPE_MARKDOWN, json);

        Request request = new Request.Builder()
                .put(body)
                .url(Constant.CHANGEINFO_URL)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("info", "修改用户信息失败:" + e.getMessage());
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
                    Toast.makeText(ModificationNameActivity.this,"修改成功",Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(ModificationNameActivity.this,"修改失败",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("ModificationNameActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("ModificationNameActivity");
        MobclickAgent.onPause(this);
    }
}
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
public class DescriptionActivity extends Activity implements View.OnClickListener {
    private EditText description_et;
    private ImageView description_iv_x,description_iv_back;
    private TextView description_tv_back,description_tv_sure;
    private String description,userId;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        Intent intent = getIntent();
        description = intent.getStringExtra("description");
        init();
    }
    public void init(){
        description_et = (EditText) findViewById(R.id.description_et);
        description_iv_x = (ImageView) findViewById(R.id.description_iv_x);
        description_iv_back = (ImageView) findViewById(R.id.description_iv_back);
        description_tv_back = (TextView) findViewById(R.id.description_tv_back);
        description_tv_sure = (TextView) findViewById(R.id.description_tv_sure);
        description_tv_sure.setOnClickListener(this);
        description_tv_back.setOnClickListener(this);
        description_iv_back.setOnClickListener(this);
        description_et.addTextChangedListener(textWatcher);
        description_iv_x.setOnClickListener(onClickListener);
        description_et.setText(description);

        sharedPreferences = DescriptionActivity.this.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        getUserId();
    }
    public TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(description_et.getText().toString() != null && !description_et.getText().toString().equals("")){
                description_iv_x.setVisibility(View.VISIBLE);
            } else {
                description_iv_x.setVisibility(View.INVISIBLE);
            }
        }
    };
    public View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            description_et.setText("");
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.description_iv_back:
                finish();
                break;
            case R.id.description_tv_back:
                finish();
                break;
            case R.id.description_tv_sure:
                changeInformation(userId,description_et.getText().toString());
                Intent intent = new Intent();
                intent.putExtra("description",description_et.getText().toString());
                setResult(RESULT_OK,intent);
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

    public void changeInformation(final String userId, final String description) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                execute(userId, description);
            }
        }).start();
    }

    public void execute(String userId, String description) {
        MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("application/json;charset=utf-8");
        OkHttpClient okHttpClient = new OkHttpClient();
        JSONObject object = null;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Introduction", description);
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
                    Toast.makeText(DescriptionActivity.this, "修改成功", Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(DescriptionActivity.this,"修改失败",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("DescriptionActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("DescriptionActivity");
        MobclickAgent.onPause(this);
    }
}

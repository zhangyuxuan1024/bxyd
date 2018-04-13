package net.iclassmate.bxyd.ui.activitys.owner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.view.clip.ClipImageLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by xydbj on 2016.7.21.
 */
public class CutActivity extends Activity implements View.OnClickListener {
    private TextView cutbitmap_tv_back, cutbitmap_tv_sure;
    private ImageView cutbitmap_iv_back;
    private ClipImageLayout cutbitmap_image;
    private SharedPreferences sharedPreferences;
    private String userId;
    private byte[] datas;
    public static final String action = "CutActivity";
    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cutbitmap);
        initView();
        userId = sharedPreferences.getString(Constant.ID_USER, "");
    }

    public void initView() {
        pb = (ProgressBar) findViewById(R.id.cutbitmap_pb);
        cutbitmap_tv_back = (TextView) findViewById(R.id.cutbitmap_tv_back);
        cutbitmap_tv_sure = (TextView) findViewById(R.id.cutbitmap_tv_sure);
        cutbitmap_iv_back = (ImageView) findViewById(R.id.cutbitmap_iv_back);
        cutbitmap_image = (ClipImageLayout) findViewById(R.id.cutbitmap_image);

        cutbitmap_tv_back.setOnClickListener(this);
        cutbitmap_iv_back.setOnClickListener(this);
        cutbitmap_tv_sure.setOnClickListener(this);

        sharedPreferences = CutActivity.this.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cutbitmap_tv_back:
            case R.id.cutbitmap_iv_back:
                finish();
                break;
            case R.id.cutbitmap_tv_sure:
                pb.setVisibility(View.VISIBLE);
                Bitmap bitmap = cutbitmap_image.clip();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                datas = baos.toByteArray();
                String encode = Base64.encodeToString(datas, Base64.DEFAULT);

                if (datas != null || !"".equals(datas) || !"".equals("null")) {
                    Intent intent_icon = new Intent(action);
                    intent_icon.putExtra("byte", datas);
                    intent_icon.putExtra("cut_type", 1);
                    sendBroadcast(intent_icon);
                }

                uploadIcon(userId, encode);
                break;
        }
    }

    public void uploadIcon(final String userId, final String encode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                execute(userId, encode);
            }
        }).start();
    }

    public void execute(String userId, String encode) {
        MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
        OkHttpClient okHttpClient = new OkHttpClient();
        String json = "data:image/jpeg;base64," + encode;
        RequestBody body = RequestBody.create(mediaType, json);
        Request request = new Request.Builder()
                .put(body)
                .url(Constant.CUTICON_URL + userId + "/icon")
                .build();
        Log.i("info", "修改用户头像的url:" + Constant.CUTICON_URL + userId + "/icon");
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("info", "修改头像失败：" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message msg = new Message();
                msg.what = response.code();
                Log.i("info", "修改头像返回的code:" + response.code());
                mHandler.sendMessage(msg);
            }
        });
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 200:
                    Intent intent = new Intent(CutActivity.this, ImageIconActivity.class);
                    intent.putExtra("bitmap", datas);
                    startActivity(intent);
                    Toast.makeText(CutActivity.this, "修改头像成功", Toast.LENGTH_LONG).show();
                    finish();
                    break;
                default:
                    Toast.makeText(CutActivity.this, "修改头像失败", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("CutActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("CutActivity");
        MobclickAgent.onPause(this);
    }
}

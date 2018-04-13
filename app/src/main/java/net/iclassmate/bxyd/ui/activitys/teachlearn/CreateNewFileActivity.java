package net.iclassmate.bxyd.ui.activitys.teachlearn;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
 * Created by xydbj on 2016.9.20.
 */
public class CreateNewFileActivity extends Activity implements OnClickListener {

    private String fullPath, spaceId, userId, fileName;
    private EditText et_fileName;
    private TextView tv_cancel, tv_sure;
    private int code;
    public static final String action = "CreateNewFileActivity";
    private static final int REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createnewfile);

        Intent intent = getIntent();
        fullPath = intent.getStringExtra("fullPath");
        if (fullPath == null || fullPath.equals("") || fullPath.equals("null")) {
            fullPath = "";
        }
        spaceId = intent.getStringExtra("spaceId");
        userId = intent.getStringExtra("userId");

        initView();
    }

    public void initView() {
        tv_cancel = (TextView) findViewById(R.id.createnewfile_cancel);
        tv_sure = (TextView) findViewById(R.id.createnewfile_sure);
        et_fileName = (EditText) findViewById(R.id.createnewfile_filename);
        tv_cancel.setOnClickListener(this);
        tv_sure.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.createnewfile_cancel:
                Intent intent = new Intent(CreateNewFileActivity.this,ExitCreateFileActivity.class);
                startActivityForResult(intent,REQUEST_CODE);
                break;
            case R.id.createnewfile_sure:
                if (et_fileName.getText().toString().trim().contains(".") ||
                        et_fileName.getText().toString().trim().contains("\\") ||
                        et_fileName.getText().toString().trim().contains("/") ||
                        et_fileName.getText().toString().trim().contains(":") ||
                        et_fileName.getText().toString().trim().contains(" ") ||
                        et_fileName.getText().toString().trim().contains("*") ||
                        et_fileName.getText().toString().trim().contains("?") ||
                        et_fileName.getText().toString().trim().contains("<") ||
                        et_fileName.getText().toString().trim().contains(">") ||
                        et_fileName.getText().toString().trim().contains("|")) {
                    Toast.makeText(CreateNewFileActivity.this, "含有非法字符", Toast.LENGTH_SHORT).show();
                } else if (et_fileName.getText().toString().trim().equals("")){
                    Toast.makeText(CreateNewFileActivity.this, "文件夹名称不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    fileName = et_fileName.getText().toString().trim();
                    Log.i("info", "新建文件夹时,fullPath=" + fullPath + ",spaceId=" + spaceId + ",userId=" + userId + ",fileName=" + fileName);
                    createFile(fullPath, fileName, spaceId, userId);
//                    Intent intent = new Intent();
//                    setResult(RESULT_OK, intent);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE){
            finish();
        }
    }

    public void createFile(final String fullPath, final String fileName, final String spaceId, final String userId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                execute(fullPath, fileName, spaceId, userId);
            }
        }).start();
    }

    public void execute(String fullPath, String fileName, String spaceId, String userId) {
        MediaType type = MediaType.parse("application/json;charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("fullPath", fullPath);
            jsonObject.put("shortName", fileName);
            jsonObject.put("spaceId", spaceId);
            jsonObject.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json = jsonObject.toString();
        Log.i("info", "创建文件夹的json：" + json);
        RequestBody body = RequestBody.create(type, json);
        Request request = new Request.Builder()
                .url(Constant.STUDY_CREATE_FILE)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("info", "创建文件夹失败：" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                code = response.code();
                Log.i("info", "创建文件夹返回的code：" + code);
                mHandler.sendEmptyMessage(code);
            }
        });
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 200:
                    Intent intent1 = new Intent(action);
                    sendBroadcast(intent1);
                    CreateNewFileActivity.this.finish();
                    break;
                case 500:
                    Toast.makeText(CreateNewFileActivity.this, "含有非法字符", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(CreateNewFileActivity.this, "文件夹名重复，请重新输入", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}

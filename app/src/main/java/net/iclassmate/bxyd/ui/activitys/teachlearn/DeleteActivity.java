package net.iclassmate.bxyd.ui.activitys.teachlearn;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.bean.netdisk.FileDirList;
import net.iclassmate.bxyd.constant.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by xydbj on 2016.7.6.
 */
public class DeleteActivity extends Activity implements View.OnClickListener {
    private TextView tv_cancel, tv_sure, deletedialog_tv_title;
    private List<FileDirList> listSelected;
    private String[] ids;
    private String[] spaceIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deletedialog);
        initView();

    }

    public void getData(List<FileDirList> list) {
        ids = new String[list.size()];
        spaceIds = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            spaceIds[i] = list.get(i).getSpaceUuid();
            ids[i] = list.get(i).getId();
        }
        openThread(ids, spaceIds);
    }

    public void initView() {
        if (getIntent().getStringExtra("from") != null && !getIntent().getStringExtra("from").equals("null") && !getIntent().getStringExtra("from").equals("")) {
            if (getIntent().getStringExtra("from").equals("SelectSortActivity")) {
                deletedialog_tv_title = (TextView) findViewById(R.id.deletedialog_tv_title);
                deletedialog_tv_title.setText("是否确认删除群成员");
            }
        } else {
            listSelected = new ArrayList<FileDirList>();
        }
        tv_cancel = (TextView) findViewById(R.id.deletedialog_cancel);
        tv_sure = (TextView) findViewById(R.id.deletedialog_sure);


        tv_cancel.setOnClickListener(this);
        tv_sure.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.deletedialog_cancel:
                if (getIntent().getStringExtra("from") != null && !getIntent().getStringExtra("from").equals("null") && !getIntent().getStringExtra("from").equals("")) {
                    if (getIntent().getStringExtra("from").equals("SelectSortActivity")){
                        Intent intent = getIntent();
                        intent.putExtra("YesNo", "No");
                        setResult(15, intent);
                    }
                }
                finish();
                Toast.makeText(this, "取消", Toast.LENGTH_SHORT).show();
                break;
            case R.id.deletedialog_sure:
                Intent intent = getIntent();
                if (getIntent().getStringExtra("from") != null && !getIntent().getStringExtra("from").equals("null") && !getIntent().getStringExtra("from").equals("")) {
                    if (getIntent().getStringExtra("from").equals("SelectSortActivity")) {
                        intent.putExtra("YesNo", "Yes");
                        setResult(15, intent);
                    }
                } else {
                    listSelected = (List<FileDirList>) intent.getSerializableExtra("listSelected");
                    getData(listSelected);
                    setResult(RESULT_OK, intent);
                }
                finish();
                break;
        }
    }

    public void openThread(final String[] ids, final String[] spaceIds) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    execute(ids, spaceIds);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void execute(String[] ids, String[] spaceIds) throws IOException {
        MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("application/json;charset=utf-8");
        OkHttpClient okHttpClient = new OkHttpClient();

        JSONObject jsonObject = null;
        try {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < listSelected.size(); i++) {
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("id", ids[i]);
                jsonObject1.put("spaceId", spaceIds[i]);
                jsonArray.put(jsonObject1);
            }
            jsonObject = new JSONObject();
            jsonObject.put("requests", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i("info", "异常=" + e.getMessage());
        }
        String json = jsonObject.toString();
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_MARKDOWN, json);
        Request request = new Request.Builder()
                .url(Constant.DELETE_NETDISK_URL)
                .post(requestBody)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            Message message = new Message();
            message.what = 1;
            message.obj = response.code();
            mHandler.sendMessage(message);
        } else {
            Message message = new Message();
            message.what = 404;
            mHandler.sendMessage(message);
            Log.i("info", "失败");
        }
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Toast.makeText(DeleteActivity.this, "删除成功!", Toast.LENGTH_SHORT).show();
                    break;
                case 404:
                    Toast.makeText(DeleteActivity.this, "有问题，删除失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("DeleteActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("DeleteActivity");
        MobclickAgent.onPause(this);
    }
}















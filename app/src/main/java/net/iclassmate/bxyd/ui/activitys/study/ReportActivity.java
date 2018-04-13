package net.iclassmate.bxyd.ui.activitys.study;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.bean.study.StudyMessageItem;
import net.iclassmate.bxyd.constant.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReportActivity extends FragmentActivity implements View.OnClickListener, TextWatcher {
    private ImageView img_report, ra, rb, rc, rd;
    private TextView tv_back, tv_a, tv_b, tv_c, tv_d;
    private LinearLayout linear_a, linear_b, linear_c, linear_d;
    private boolean isreport;
    private EditText et;
    private int last_click;
    private long last_click_time;
    private StudyMessageItem message;
    private OkHttpClient client;
    private String selectText;
    private Context mContext;

    private boolean ischeck;
    private String id;
    private int type;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            if (what == 1) {
                try {
                    int code = (int) msg.obj;
                    //Log.i("info", "返回数据=" + code);
                    if (code == 200) {
                        Toast.makeText(mContext, "举报成功！", Toast.LENGTH_SHORT).show();
                        finish();
                    } else if (code == 400) {
                        Toast.makeText(mContext, "不能重复举报！", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(mContext, "举报失败！", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(mContext, "举报失败！", Toast.LENGTH_SHORT).show();
                }
            } else if (what == 404) {
                Toast.makeText(mContext, "举报失败！", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Intent intent = getIntent();
        message = (StudyMessageItem) intent.getSerializableExtra("msg");
        id = intent.getStringExtra("id");
        type = intent.getIntExtra("type", 0);
        init();
    }

    private void init() {
        img_report = (ImageView) findViewById(R.id.study_img_report_pic);
        img_report.setOnClickListener(this);
        ra = (ImageView) findViewById(R.id.report_a);
        ra.setOnClickListener(this);
        rb = (ImageView) findViewById(R.id.report_b);
        rb.setOnClickListener(this);
        rc = (ImageView) findViewById(R.id.report_c);
        rc.setOnClickListener(this);
        rd = (ImageView) findViewById(R.id.report_d);
        rd.setOnClickListener(this);
        tv_back = (TextView) findViewById(R.id.tv_report_back);
        tv_back.setOnClickListener(this);
        et = (EditText) findViewById(R.id.report_input_et);
        tv_a = (TextView) findViewById(R.id.tv_a);
        tv_a.setOnClickListener(this);
        tv_b = (TextView) findViewById(R.id.tv_b);
        tv_b.setOnClickListener(this);
        tv_c = (TextView) findViewById(R.id.tv_c);
        tv_c.setOnClickListener(this);
        tv_d = (TextView) findViewById(R.id.tv_d);
        tv_d.setOnClickListener(this);
        linear_a = (LinearLayout) findViewById(R.id.linear_a);
        linear_a.setOnClickListener(this);
        linear_b = (LinearLayout) findViewById(R.id.linear_b);
        linear_b.setOnClickListener(this);
        linear_c = (LinearLayout) findViewById(R.id.linear_c);
        linear_c.setOnClickListener(this);
        linear_d = (LinearLayout) findViewById(R.id.linear_d);
        linear_d.setOnClickListener(this);
        isreport = false;
        setReportImg();

        last_click = -1;
        ischeck = false;
        mContext = this;
        et.addTextChangedListener(this);
        selectText = "";
    }

    private void setReportImg() {
        String text = et.getText().toString().trim();
        if (!text.equals("")) {
            isreport = true;
        }
        if (isreport || ischeck) {
            img_report.setClickable(true);
            img_report.setImageResource(R.mipmap.bt_report);
        } else {
            img_report.setClickable(false);
            img_report.setImageResource(R.mipmap.bt_report_pressed);
        }
    }

    @Override
    public void onClick(View view) {
        int index = -1;
        switch (view.getId()) {
            case R.id.study_img_report_pic:
                if (System.currentTimeMillis() - last_click_time >= 3000) {
                    String uid = "";
                    if (message != null) {
                        uid = message.getCreateBy().getId();
                        report(uid);
                    } else if (id != null && !id.equals("")) {
                        uid = id;
                        report(uid, type);
                    }
                    last_click_time = System.currentTimeMillis();
                }
                break;
            case R.id.report_a:
            case R.id.tv_a:
            case R.id.linear_a:
                index = 0;
                setImage(index);
                break;
            case R.id.report_b:
            case R.id.tv_b:
            case R.id.linear_b:
                index = 1;
                setImage(index);
                break;
            case R.id.report_c:
            case R.id.tv_c:
            case R.id.linear_c:
                index = 2;
                setImage(index);
                break;
            case R.id.report_d:
            case R.id.tv_d:
            case R.id.linear_d:
                index = 3;
                setImage(index);
                break;
            case R.id.tv_report_back:
                this.finish();
                break;
        }
    }

    private void report(final String userId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                execute(userId);
            }
        }).start();
    }

    private void report(final String rid, final int type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                execute(rid, type);
            }
        }).start();
    }

    private void execute(String userId) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String json = "";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("reason", et.getText().toString() + "," + selectText);
            if (message != null) {
                jsonObject.put("bulletinId", message.getId());
            } else if (id != null && id.equals("")) {
                jsonObject.put("bulletinId", id);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        json = jsonObject.toString();
        //Log.i("info", "举报参数=" + json);
        RequestBody body = RequestBody.create(JSON, json);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS);
        client = builder.build();
        Request request = new Request.Builder()
                .url(String.format(Constant.STUDY_REPORT, userId))
                .post(body)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            Message message = new Message();
            message.what = 1;
            message.obj = response.code();
            mHandler.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(404);
        }
    }

    /*
    {
  "informerId": "string",
  "reason": "string",
  "reportedId": "string",
  "type": 1
}
    * */
    private void execute(String rid, int type) {
        SharedPreferences sharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCES, MODE_PRIVATE);

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String json = "";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("reason", et.getText().toString() + "," + selectText);
            jsonObject.put("informerId", sharedPreferences.getString(Constant.ID_USER, ""));
            jsonObject.put("reportedId", rid);
            jsonObject.put("type", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        json = jsonObject.toString();
        //Log.i("info", "举报参数=" + json);
        RequestBody body = RequestBody.create(JSON, json);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS);
        client = builder.build();
        Request request = new Request.Builder()
                .url(Constant.MESSAGE_REPORT)
                .post(body)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            Message message = new Message();
            message.what = 1;
            message.obj = response.code();
            mHandler.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(404);
        }
    }

    private void setImage(int index) {
        if (index == last_click) {
            ischeck = !ischeck;
        } else {
            ischeck = true;
        }
        last_click = index;
        ra.setImageResource(R.mipmap.ic_circle);
        rb.setImageResource(R.mipmap.ic_circle);
        rc.setImageResource(R.mipmap.ic_circle);
        rd.setImageResource(R.mipmap.ic_circle);
        selectText = "";
        switch (index) {
            case 0:
                if (ischeck) {
                    ra.setImageResource(R.mipmap.ic_blue);
                } else {
                    ra.setImageResource(R.mipmap.ic_circle);
                }
                break;
            case 1:
                if (ischeck) {
                    rb.setImageResource(R.mipmap.ic_blue);
                } else {
                    rb.setImageResource(R.mipmap.ic_circle);
                }
                break;
            case 2:
                if (ischeck) {
                    rc.setImageResource(R.mipmap.ic_blue);
                } else {
                    rc.setImageResource(R.mipmap.ic_circle);
                }
                break;
            case 3:
                if (ischeck) {
                    rd.setImageResource(R.mipmap.ic_blue);
                } else {
                    rd.setImageResource(R.mipmap.ic_circle);
                }
                break;
        }
        selectText = tv_d.getText().toString();
        setReportImg();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!s.toString().trim().equals("")) {
            isreport = true;
        } else {
            isreport = false;
        }
        if (s.length() > 255) {
            String ret = s.toString();
            ret = ret.substring(0, 255);
            et.setText(ret);

            CharSequence text = et.getText();
            if (text instanceof Spannable) {
                Spannable spanText = (Spannable) text;
                Selection.setSelection(spanText, text.length());
            }
        }
        setReportImg();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("ReportActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("ReportActivity");
        MobclickAgent.onPause(this);
    }
}

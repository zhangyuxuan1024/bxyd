package net.iclassmate.bxyd.ui.activitys.study;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;

/**
 * Created by xydbj on 2016.8.12.
 */
public class IsWifiActivity extends Activity implements View.OnClickListener {
    private TextView iswifi_cancel, iswifi_sure, iswifi_content;
    private String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iswifi);
        Intent intent = getIntent();
        content = intent.getStringExtra("content");
        initView();
    }

    public void initView() {
        iswifi_cancel = (TextView) findViewById(R.id.iswifi_cancel);
        iswifi_sure = (TextView) findViewById(R.id.iswifi_sure);
        iswifi_sure.setOnClickListener(this);
        iswifi_cancel.setOnClickListener(this);
        iswifi_content = (TextView) findViewById(R.id.iswifi_conetnt);
        if (content != null && !content.equals("")) {
            iswifi_content.setText(content);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iswifi_cancel:
                finish();
                break;
            case R.id.iswifi_sure:
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("IsWifiActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("IsWifiActivity");
        MobclickAgent.onPause(this);
    }
}
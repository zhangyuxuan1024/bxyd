package net.iclassmate.bxyd.ui.activitys.owner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;

/**
 * Created by xydbj on 2016.8.2.
 */
public class CheckActivity extends Activity implements View.OnClickListener {
    private TextView dialog_later,dialog_now,dialog_latest_version,dialog_latest_version_size,dialog_content;
    private String version,size,description,url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
        initView();

        Intent intent = getIntent();
        version = intent.getStringExtra("version");
        size = intent.getStringExtra("size");
        description = intent.getStringExtra("description");
        url = intent.getStringExtra("url");

        dialog_latest_version.setText(version);
        dialog_latest_version_size.setText(size+"M");
        dialog_content.setText(description);
    }
    public void initView(){
        dialog_later = (TextView) findViewById(R.id.dialog_later);
        dialog_now = (TextView) findViewById(R.id.dialog_now);
        dialog_latest_version = (TextView) findViewById(R.id.dialog_latest_version);
        dialog_latest_version_size = (TextView) findViewById(R.id.dialog_latest_version_size);
        dialog_content = (TextView) findViewById(R.id.dialog_content);

        dialog_later.setOnClickListener(this);
        dialog_now.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dialog_later:
                finish();
                break;
            case R.id.dialog_now:
                Intent intent = new Intent(CheckActivity.this,UpdateActivity.class);
                intent.putExtra("size",size);
                intent.putExtra("url",url);
                startActivity(intent);
                this.finish();
                break;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("CheckActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("CheckActivity");
        MobclickAgent.onPause(this);
    }
}
package net.iclassmate.bxyd.ui.activitys.balloon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;

/**
 * 弹出提示框
 * Created by xyd on 2016/8/17.
 */
public class BalloonActivity extends Activity implements View.OnClickListener {
    private TextView balloon_title, balloon_cancel, balloon_sure;    //标题，取消，确认

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balloon);

        initView();
        initData();
    }

    public void initView() {
        balloon_title = (TextView) findViewById(R.id.balloon_title);
        balloon_cancel = (TextView) findViewById(R.id.balloon_cancel);
        balloon_sure = (TextView) findViewById(R.id.balloon_sure);
    }

    public void initData(){
        String title = getIntent().getStringExtra("title");

        if(title != null && !TextUtils.isEmpty(title)){
            balloon_title.setText(title);
        }

        balloon_cancel.setOnClickListener(this);
        balloon_sure.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.balloon_cancel:
                finish();
                break;

            case R.id.balloon_sure:
                Intent intent = getIntent();
                setResult(16, intent);
                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("BalloonActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("BalloonActivity");
        MobclickAgent.onPause(this);
    }
}
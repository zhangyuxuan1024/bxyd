package net.iclassmate.bxyd.ui.activitys.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;

public class ClearRecordActivity extends FragmentActivity implements View.OnClickListener{

    private TextView clear_cancel,clear_sure;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_clear_record);
        mContext=this;
        initView();
        initListener();
        initData();
    }

    private void initView() {
        clear_cancel= (TextView) findViewById(R.id.clear_cancel);
        clear_sure= (TextView) findViewById(R.id.clear_sure);
    }

    private void initListener() {
        clear_cancel.setOnClickListener(this);
        clear_sure.setOnClickListener(this);
    }

    private void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.clear_cancel:
                finish();
                break;
            case R.id.clear_sure:
                Intent intent = getIntent();
                setResult(16, intent);
                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("ClearRecordActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("ClearRecordActivity");
        MobclickAgent.onPause(this);
    }
}

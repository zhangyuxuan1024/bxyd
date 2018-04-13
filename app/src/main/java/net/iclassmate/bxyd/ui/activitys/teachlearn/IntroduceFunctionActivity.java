package net.iclassmate.bxyd.ui.activitys.teachlearn;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;

/**
 * Created by xydbj on 2016.6.4.
 */
public class IntroduceFunctionActivity extends Activity implements View.OnClickListener {
    private ImageView iv_back,iv_example;
    private TextView tv_back;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce_function);
        initView();
    }

    public void initView(){
        iv_back = (ImageView) findViewById(R.id.introduce_function_iv_back);
        iv_example = (ImageView) findViewById(R.id.introduce_function_iv_example);
        tv_back = (TextView) findViewById(R.id.introduce_function_tv_back);
        iv_back.setOnClickListener(this);
        tv_back.setOnClickListener(this);
        iv_example.setImageResource(R.mipmap.introduce);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.introduce_function_iv_back:
                IntroduceFunctionActivity.this.finish();
                break;
            case R.id.introduce_function_tv_back:
                IntroduceFunctionActivity.this.finish();
                break;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("IntroduceFunctionActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("IntroduceFunctionActivity");
        MobclickAgent.onPause(this);
    }
}
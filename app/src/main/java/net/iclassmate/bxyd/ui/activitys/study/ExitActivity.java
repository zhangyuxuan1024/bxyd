package net.iclassmate.bxyd.ui.activitys.study;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;

public class ExitActivity extends FragmentActivity implements View.OnClickListener {
    private TextView tv_cancel, tv_sure;
    public static final int RET_EXIT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exit);

        tv_cancel = (TextView) findViewById(R.id.exit_cancel);
        tv_sure = (TextView) findViewById(R.id.exit_sure);
        tv_cancel.setOnClickListener(this);
        tv_sure.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.exit_cancel:
                this.finish();
                break;
            case R.id.exit_sure:
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                this.finish();
                break;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("ExitActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("ExitActivity");
        MobclickAgent.onPause(this);
    }
}

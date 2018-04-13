package net.iclassmate.bxyd.ui.activitys.study.openfile;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.iclassmate.bxyd.R;

public class TxtActivity extends FragmentActivity implements View.OnClickListener {
    private String text;
    private TextView tv_result;
    private ImageView img_back;
    private TextView tv_back, tv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_txt);
        init();
    }

    private void init() {
        Intent intent = getIntent();
        text = intent.getStringExtra("text");

        tv_result = (TextView) findViewById(R.id.tv_result);
        img_back = (ImageView) findViewById(R.id.study_message_back);
        tv_back = (TextView) findViewById(R.id.study_message_left_tv);
        tv_title = (TextView) findViewById(R.id.study_message_title_tv);
        tv_title.setText("提示");
        img_back.setOnClickListener(this);
        tv_back.setOnClickListener(this);
        tv_result.setText(text);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.study_message_back:
            case R.id.study_message_left_tv:
                finish();
                break;
        }
    }
}

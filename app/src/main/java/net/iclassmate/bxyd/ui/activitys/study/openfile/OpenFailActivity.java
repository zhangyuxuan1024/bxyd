package net.iclassmate.bxyd.ui.activitys.study.openfile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.ui.activitys.study.StudyWindowActivity;

import java.util.ArrayList;
import java.util.List;

public class OpenFailActivity extends FragmentActivity implements View.OnClickListener {
    private TextView tv_back;
    private ImageView img_back;
    private TextView tv_title;
    private ImageView img_right;
    private Context mContext;
    private static final int RET_DEL = 1;
    private String id;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_fail);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        type = intent.getIntExtra("type", 0);
        init();
    }

    private void init() {
        tv_back = (TextView) findViewById(R.id.study_message_left_tv);
        tv_back.setOnClickListener(this);
        img_back = (ImageView) findViewById(R.id.study_message_back);
        img_back.setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.study_message_title_tv);
        tv_title.setVisibility(View.INVISIBLE);
        tv_back.setText("返回");

        img_right = (ImageView) findViewById(R.id.study_message_right_icon);
        img_right.setVisibility(View.VISIBLE);
        img_right.setOnClickListener(this);
        mContext = this;

        if (type == 1) {
            img_right.setImageResource(R.mipmap.ic_xiaobaidian);
        } else if (type == 2) {
            img_right.setImageResource(R.drawable.img_study_del);
        } else if (type == 3) {
            img_right.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.study_message_left_tv:
            case R.id.study_message_back:
                this.finish();
                break;
            case R.id.study_message_right_icon:
                if (type == 1) {
                    Intent intent = new Intent(mContext, StudyWindowActivity.class);
                    List<String> list = new ArrayList<String>();
                    list.add("保存到网盘");
                    intent.putStringArrayListExtra("list", (ArrayList<String>) list);
                    intent.putExtra("saveid", id);
                    startActivity(intent);
                } else if (type == 2) {
                    Intent intent = new Intent(mContext, StudyWindowActivity.class);
                    List<String> list = new ArrayList<String>();
                    list.add("是否确认删除文件删除");
                    intent.putStringArrayListExtra("list", (ArrayList<String>) list);
                    startActivityForResult(intent, RET_DEL);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RET_DEL) {
                Intent intent = new Intent();
                intent.putExtra("id", id);
                setResult(RESULT_OK, intent);
                this.finish();
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("OpenFailActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("OpenFailActivity");
        MobclickAgent.onPause(this);
    }
}

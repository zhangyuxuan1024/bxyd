package net.iclassmate.bxyd.ui.activitys.study;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.adapter.study.StudySpaceAdapter;
import net.iclassmate.bxyd.bean.study.StudyMessage;
import net.iclassmate.bxyd.view.FullListView;

import java.util.ArrayList;
import java.util.List;

public class SendFriActivity extends FragmentActivity implements View.OnClickListener {
    private StudyMessage msg;
    private StudySpaceAdapter adapter;
    private FullListView listView;
    private TextView tv_back, tv_release;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_fri);
        Intent intent = getIntent();
        msg = (StudyMessage) intent.getSerializableExtra("msg");
        List<StudyMessage> list = new ArrayList<>();
        list.add(msg);
        //adapter = new StudySpaceAdapter(this, list);
        adapter.setMsgType(1);

        listView = (FullListView) findViewById(R.id.study_sapce_msg_listview);
        listView.setAdapter(adapter);

        tv_back = (TextView) findViewById(R.id.tv_comment_back);
        tv_back.setOnClickListener(this);
        tv_release = (TextView) findViewById(R.id.tv_comment_release);
        tv_release.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_comment_back:
                this.finish();
                break;
            case R.id.tv_comment_release:
                Toast.makeText(SendFriActivity.this, "已发布！", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SendFriActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SendFriActivity");
        MobclickAgent.onPause(this);
    }
}

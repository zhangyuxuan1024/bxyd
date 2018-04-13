package net.iclassmate.bxyd.ui.activitys.study;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.adapter.study.MessageAdapter;
import net.iclassmate.bxyd.bean.study.NoticMessage;

import java.util.ArrayList;
import java.util.List;

public class MessageAlertActivity extends FragmentActivity implements View.OnClickListener {
    private ImageView image_back;
    private TextView tv_left, tv_title, tv_right;
    private ListView listView;
    private MessageAdapter messageAdapter;
    private List<NoticMessage> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_message_alert);
        initView();
        initEvent();
        loadData();
    }

    private void loadData() {
        for (int i = 0; i < 25; i++) {
            NoticMessage message = new NoticMessage();
            message.setName("小明");
            message.setContent("转发了此条消息!");
            message.setTime("下午9:29");
            message.setPath_left("http://i04.pictn.sogoucdn.com/bdfc02332b1f52c0");
            message.setPath_right("http://i04.pictn.sogoucdn.com/bdfc02332b1f52c0");
            list.add(message);
        }
        messageAdapter.notifyDataSetChanged();
    }

    private void initView() {
        image_back = (ImageView) findViewById(R.id.study_message_back);
        tv_left = (TextView) findViewById(R.id.study_message_left_tv);
        tv_title = (TextView) findViewById(R.id.study_message_title_tv);
        tv_right = (TextView) findViewById(R.id.study_message_right_tv);
        listView = (ListView) findViewById(R.id.study_message_listview);

        tv_left.setText("学习圈");
        tv_right.setText("学习圈");
        tv_title.setText("消息提醒");

        list = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, list);
        listView.setAdapter(messageAdapter);
    }

    private void initEvent() {
        image_back.setOnClickListener(this);
        tv_left.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.study_message_back:
            case R.id.study_message_left_tv:
                this.finish();
                break;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("MessageAlertActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MessageAlertActivity");
        MobclickAgent.onPause(this);
    }
}

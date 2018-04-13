package net.iclassmate.bxyd.ui.activitys.chat;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.adapter.ChatRecordAdapter;
import net.iclassmate.bxyd.adapter.MessagesAdapter;
import net.iclassmate.bxyd.utils.NetWorkUtils;
import net.iclassmate.bxyd.utils.UIUtils;
import net.iclassmate.bxyd.utils.UTCTime;
import net.iclassmate.bxyd.view.TitleBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.ImageMessage;
import io.rong.message.TextMessage;

/**
 * 聊天记录展示页面
 * Created by xyd on 2016/9/1.
 */
public class RecordInfoActivity extends Activity implements TitleBar.TitleOnClickListener{

    private ListView lv;
    private TitleBar titleBar;
    private ImageView iv_record_info_loading;   //动画
    private ImageView record_info_noresult_iv;

    private String friendId, targetId, from, time;
    private String demand;  //按哪种需求查询聊天记录。 time：按时间查询；member：按成员查询
    private Conversation.ConversationType conversationType;
    private List<Message> list;
    private ChatRecordAdapter adapter;  //用于成员查询
    private MessagesAdapter messagesAdapter; //用于时间查询
    private AnimationDrawable anim;

    private static final int NO_INTERNET = 0;    //没网络
    private static final int FIND_RECORD_MEMBER_SUCCEED = 1; //获取聊天记录成功(成员)
    private static final int FIND_RECORD_TIME_SUCCEED = 2;   //获取聊天记录成功(时间)

    Handler handler = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case NO_INTERNET:
                    Toast.makeText(UIUtils.getContext(), "您没有连接网络，请连接后重试！", Toast.LENGTH_SHORT).show();
                    break;

                case FIND_RECORD_MEMBER_SUCCEED:
                    if(list.size() <= 0){
                        record_info_noresult_iv.setVisibility(View.VISIBLE);
                    } else {
                        record_info_noresult_iv.setVisibility(View.GONE);
                    }
                    iv_record_info_loading.setVisibility(View.GONE);
                    anim.stop();
                    adapter = new ChatRecordAdapter(RecordInfoActivity.this, list);
                    lv.setAdapter(adapter);
                    break;

                case FIND_RECORD_TIME_SUCCEED:
                    if(list.size() <= 0){
                        record_info_noresult_iv.setVisibility(View.VISIBLE);
                    } else {
                        record_info_noresult_iv.setVisibility(View.GONE);
                    }
                    iv_record_info_loading.setVisibility(View.GONE);
                    anim.stop();
                    lv.setDivider(null);
                    Collections.reverse(list);
                    messagesAdapter = new MessagesAdapter(list, RecordInfoActivity.this);
                    lv.setAdapter(messagesAdapter);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_info);

        initView();
        initData();
        initListener();
    }

    public void initView(){
        lv = (ListView)findViewById(R.id.record_info_lv);
        titleBar = (TitleBar) findViewById(R.id.record_info_titlebar);
        iv_record_info_loading = (ImageView)findViewById(R.id.iv_record_info_loading);
        record_info_noresult_iv = (ImageView)findViewById(R.id.record_info_noresult_iv);
        anim = (AnimationDrawable) iv_record_info_loading.getBackground();
    }

    public void initData(){
        titleBar.setLeftIcon(R.mipmap.ic_fanhui, "返回");
        titleBar.setTitle("按群成员查找");
        from = getIntent().getStringExtra("from");
        friendId = getIntent().getStringExtra("friendId");
        targetId = getIntent().getStringExtra("targetId");
        demand = getIntent().getStringExtra("demand");

        iv_record_info_loading.setVisibility(View.VISIBLE);
        anim.start();
        list = new ArrayList<>();

        if(from.equals("person")){
            conversationType = Conversation.ConversationType.PRIVATE;
        } else if (from.equals("group")){
            conversationType = Conversation.ConversationType.GROUP;
            if(demand.equals("member")) {   //按成员查询
                titleBar.setTitle("按群成员查找");
                initMemberMessage();
            } else if(demand.equals("time")){   //按时间日期查找
                time = getIntent().getStringExtra("time");
                titleBar.setTitle(time);
                initTimeMessage();
            }
        }
    }

    public void initListener(){
        titleBar.setTitleClickListener(this);
    }

    @Override
    public void leftClick() {
        finish();
    }

    @Override
    public void rightClick() {

    }

    @Override
    public void titleClick() {

    }

    @Override
    public void innerleftClick() {

    }

    @Override
    public void innerRightClick() {

    }

    /**
     * 按成员获取聊天记录
     */
    public void initMemberMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(NetWorkUtils.isNetworkAvailable(UIUtils.getContext())){
                    RongIMClient.getInstance().getLatestMessages(conversationType, targetId, 500, new RongIMClient.ResultCallback<List<Message>>() {
                        @Override
                        public void onSuccess(List<Message> messageList) {
                            if(messageList != null && messageList.size() > 0){
                                for(Message message : messageList){
                                    if(message.getSenderUserId().equals(friendId)) {
                                        if (message.getContent() instanceof TextMessage) {
                                            TextMessage textMessage = (TextMessage) message.getContent();
                                            String info = textMessage.getContent();
                                            try {
                                                JSONObject json = new JSONObject(info);
                                                int messageType = json.optInt("MessageType");
                                                if (messageType == 0) {
                                                    list.add(message);
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        } else if (message.getContent() instanceof ImageMessage) {
                                            list.add(message);
                                        }
                                    }
                                }
                            }
                            handler.sendEmptyMessage(FIND_RECORD_MEMBER_SUCCEED);
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {

                        }
                    });
                } else{
                    handler.sendEmptyMessage(NO_INTERNET);
                }
            }
        }).start();
    }

    /**
     * 获取聊天记录
     */
    public void initTimeMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(NetWorkUtils.isNetworkAvailable(UIUtils.getContext())){
                    RongIMClient.getInstance().getLatestMessages(conversationType, targetId, 500, new RongIMClient.ResultCallback<List<Message>>() {
                        @Override
                        public void onSuccess(List<Message> messageList) {
                            if(messageList != null){
                                for(Message message : messageList){
                                    String messageTime = UTCTime.getTime(message.getReceivedTime());
                                    if(messageTime.contains(time)) {
                                        if (message.getContent() instanceof TextMessage) {
                                            TextMessage textMessage = (TextMessage) message.getContent();
                                            String info = textMessage.getContent();
                                            try {
                                                JSONObject json = new JSONObject(info);
                                                int messageType = json.optInt("MessageType");
                                                if (messageType == 0) {
                                                    list.add(message);
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        } else if (message.getContent() instanceof ImageMessage) {
                                            list.add(message);
                                        }
                                    }
                                }
                            }
                            handler.sendEmptyMessage(FIND_RECORD_TIME_SUCCEED);
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {

                        }
                    });
                } else{
                    handler.sendEmptyMessage(NO_INTERNET);
                }
            }
        }).start();
    }
}

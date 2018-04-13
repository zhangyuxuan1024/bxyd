package net.iclassmate.bxyd.ui.activitys.chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.adapter.ChatRecordAdapter;
import net.iclassmate.bxyd.bean.contacts.GroupMember;
import net.iclassmate.bxyd.ui.activitys.constacts.SelectSortActivity;
import net.iclassmate.bxyd.ui.activitys.other.RecordCalendarActivity;
import net.iclassmate.bxyd.utils.NetWorkUtils;
import net.iclassmate.bxyd.utils.UIUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;

public class ChatRecordActivity extends FragmentActivity implements View.OnClickListener{

    private Context mContext;
    private Button btn_date, chat_record_member, chat_record_byDate, chat_record_byDate2;
    private TextView tv_cancel;
    private EditText record_search;
    private LinearLayout record_ll;
    private ListView lv;
    private ImageView iv_record_loading;    //动画
    private ImageView record_noresult_iv;

    private Conversation.ConversationType conversationType;
    private int sessionType;
    private String from, targetId, author, name;
    private List<Message> list;
    private ChatRecordAdapter adapter;
    private AnimationDrawable anim;
    private List<GroupMember> listMember;

    private static final int NO_INTERNET = 0;    //没网络
    private static final int FIND_RECORD_SUCCEED = 1; //获取聊天记录成功

    Handler handler = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case NO_INTERNET:
                    Toast.makeText(UIUtils.getContext(), "您没有连接网络，请连接后重试！", Toast.LENGTH_SHORT).show();
                    break;

                case FIND_RECORD_SUCCEED:
                    if(list.size() <= 0){
                        record_noresult_iv.setVisibility(View.VISIBLE);
                    } else {
                        record_noresult_iv.setVisibility(View.GONE);
                    }
                    iv_record_loading.setVisibility(View.GONE);
                    anim.stop();
                    chat_record_byDate2.setVisibility(View.GONE);
                    record_ll.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat_record);
        mContext=this;
        initView();
        initData();
        initListener();
    }

    private void initView() {
        btn_date= (Button) findViewById(R.id.chat_record_byDate);
        tv_cancel= (TextView) findViewById(R.id.chat_record_cancel);
        record_search= (EditText) findViewById(R.id.chat_record_search);
        lv = (ListView)findViewById(R.id.record_lv);
        iv_record_loading = (ImageView)findViewById(R.id.iv_record_loading);
        record_ll = (LinearLayout) findViewById(R.id.record_ll);
        chat_record_member = (Button)findViewById(R.id.chat_record_member);
        chat_record_byDate = (Button)findViewById(R.id.chat_record_byDate);
        record_noresult_iv = (ImageView)findViewById(R.id.record_noresult_iv);
        chat_record_byDate2 = (Button)findViewById(R.id.chat_record_byDate2);
        anim = (AnimationDrawable) iv_record_loading.getBackground();

        list = new ArrayList<Message>();
        adapter = new ChatRecordAdapter(this, list);
        lv.setAdapter(adapter);
    }

    private void initData() {
        from = getIntent().getStringExtra("from");
        targetId = getIntent().getStringExtra("targetId");
        author = getIntent().getStringExtra("author");
        name = getIntent().getStringExtra("name");
        sessionType = getIntent().getIntExtra("sessionType", 0);
        if(from.equals("person")){
            sessionType = 1;
            conversationType = Conversation.ConversationType.PRIVATE;
            chat_record_byDate2.setVisibility(View.VISIBLE);
            record_ll.setVisibility(View.GONE);
        } else if(from.equals("group")){
            conversationType = Conversation.ConversationType.GROUP;
            chat_record_byDate2.setVisibility(View.GONE);
            record_ll.setVisibility(View.VISIBLE);
        }

//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Message message = list.get(position);
//                Intent intent = null;
//                if(from.equals("person")){
//                    intent = new Intent(UIUtils.getContext(), ChatActivity.class);
//                } else if(from.equals("group")){
//                    if(sessionType == 2){
//                        intent = new Intent(UIUtils.getContext(), DiscussionActivity.class);
//                    }else if(sessionType == 3 || sessionType == 4){
//                        intent = new Intent(UIUtils.getContext(), GroupChatActivity.class);
//                    }
//                }
//                intent.putExtra("targetId", targetId);
//                intent.putExtra("from", "ChatRecordActivity");
//                intent.putExtra("author", author);
//                intent.putExtra("name", name);
//                intent.putExtra("messageId", message.getMessageId());
////                    intent.putExtra("demand", "time");
//                startActivity(intent);
//            }
//        });
    }

    private void initListener() {
        btn_date.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        chat_record_member.setOnClickListener(this);
        chat_record_byDate.setOnClickListener(this);
        chat_record_byDate2.setOnClickListener(this);
        record_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                System.out.println("-1-onTextChanged-->" + record_search.getText().toString() + "<--");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                System.out.println("-2-onTextChanged-->" + record_search.getText().toString() + "<--");
                if(record_search.getText().toString().length() > 0){
                    tv_cancel.setText("确定");
                } else{
                    if(from.equals("person")){
                        chat_record_byDate2.setVisibility(View.VISIBLE);
                    }else{
                        record_ll.setVisibility(View.VISIBLE);
                    }
                    record_noresult_iv.setVisibility(View.GONE);
                    tv_cancel.setText("取消");
                    list.clear();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
//                System.out.println("-3-onTextChanged-->" + record_search.getText().toString() + "<--");

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.chat_record_byDate:   //日期按钮(group)
                Intent toDate=new Intent(UIUtils.getContext(), RecordCalendarActivity.class);
                toDate.putExtra("targetId", targetId);
                toDate.putExtra("from", from);
                toDate.putExtra("author", author);
                toDate.putExtra("name", name);
                toDate.putExtra("sessionType", sessionType);
                startActivity(toDate);
                break;
            case R.id.chat_record_cancel:
                if(tv_cancel.getText().equals("确定")){
                    iv_record_loading.setVisibility(View.VISIBLE);
                    anim.start();
                    initMessage();
                } else if(tv_cancel.getText().equals("取消")) {
                    finish();
                }
                break;
            case R.id.chat_record_member:   //成员按钮
                listMember = (ArrayList) getIntent().getSerializableExtra("listMember");
                Intent intent = new Intent(ChatRecordActivity.this, SelectSortActivity.class);
                intent.putExtra("targetId", targetId);
                intent.putExtra("groupList", (Serializable) listMember);
                intent.putExtra("from", "ChatRecordActivity");
                intent.putExtra("visible", false);
                startActivity(intent);
                break;
            case R.id.chat_record_byDate2:  //日期按钮(person)
                Intent toDate2=new Intent(UIUtils.getContext(), RecordCalendarActivity.class);
                toDate2.putExtra("targetId", targetId);
                toDate2.putExtra("from", from);
                toDate2.putExtra("author", author);
                toDate2.putExtra("name", name);
                toDate2.putExtra("sessionType", sessionType);
                startActivity(toDate2);
                break;
        }
    }

    /**
     * 获取聊天记录
     */
    public void initMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(NetWorkUtils.isNetworkAvailable(UIUtils.getContext())){
                    RongIMClient.getInstance().getLatestMessages(conversationType, targetId, 500, new RongIMClient.ResultCallback<List<Message>>() {
                        @Override
                        public void onSuccess(List<Message> messageList) {
                            list.clear();
                            if(messageList != null){
                                for(Message message : messageList){
                                    if (message.getContent() instanceof TextMessage) {
                                        TextMessage textMessage = (TextMessage) message.getContent();
                                        String info = textMessage.getContent();
                                        try {
                                            JSONObject json = new JSONObject(info);
                                            int messageType = json.optInt("MessageType");
                                            if (messageType == 0) {
                                                String filename = json.optString("FileName");
                                                if(filename == null || filename.equals("")){
                                                    String content = json.optString("Content");
                                                    if(content.contains(record_search.getText().toString())){
                                                        list.add(message);
                                                        Log.i("ChatRecordActivity", "获取的聊天记录内容："+content+","+list.size());
                                                    }
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                            handler.sendEmptyMessage(FIND_RECORD_SUCCEED);
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

//    /**
//     * 获取聊天记录
//     */
//    public void initMessage(){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if(NetWorkUtils.isNetworkAvailable(UIUtils.getContext())){
//                    RongIMClient.getInstance().getLatestMessages(conversationType, targetId, 500, new RongIMClient.ResultCallback<List<Message>>() {
//                        @Override
//                        public void onSuccess(List<Message> messageList) {
//                            if(messageList != null){
//                                for(Message message : messageList){
//                                    if (message.getContent() instanceof TextMessage) {
//                                        TextMessage textMessage = (TextMessage) message.getContent();
//                                        String info = textMessage.getContent();
//                                        try {
//                                            JSONObject json = new JSONObject(info);
//                                            int messageType = json.optInt("MessageType");
//                                            if (messageType == 0) {
//                                                list.add(message);
//                                            }
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//                                    } else if (message.getContent() instanceof ImageMessage) {
//                                        list.add(message);
//                                    }
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onError(RongIMClient.ErrorCode errorCode) {
//
//                        }
//                    });
//                } else{
//                    handler.sendEmptyMessage(NO_INTERNET);
//                }
//            }
//        }).start();
//    }



    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("ChatRecordActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("ChatRecordActivity");
        MobclickAgent.onPause(this);
    }
}
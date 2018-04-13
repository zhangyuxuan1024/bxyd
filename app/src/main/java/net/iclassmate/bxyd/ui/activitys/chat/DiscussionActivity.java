package net.iclassmate.bxyd.ui.activitys.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.bean.contacts.GroupMember;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.ui.fragment.emotion.EmotionDisFragment;
import net.iclassmate.bxyd.utils.HttpManager;
import net.iclassmate.bxyd.utils.NetWorkUtils;
import net.iclassmate.bxyd.utils.SendLastMessage;
import net.iclassmate.bxyd.utils.UIUtils;
import net.iclassmate.bxyd.utils.UTCTime;
import net.iclassmate.bxyd.view.TitleBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.ImageMessage;
import io.rong.message.TextMessage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//群聊
public class DiscussionActivity extends FragmentActivity implements TitleBar.TitleOnClickListener, SendLastMessage {
    private Context mContext;
    private TitleBar titleBar;
    private ListView messageBody;
    private String from, targetId, sessionName, author, sessionIcon;
    private String time;
    private LinearLayout ll_discussion;
    private RelativeLayout dis_noLoading;   //动画
    private ImageView dis_info_loading;     //动画
    private AnimationDrawable anim;
    private Conversation message;
    private Conversation.ConversationType type;
    private EmotionDisFragment emotionDisFragment;
    private SharedPreferences sp;
    private String result_group;
    private HttpManager httpManager;
    private Boolean get_group_name; //是否获取群组名称和群主ID
    private ArrayList<GroupMember> list;
    private Boolean isInternet = false; //是否有网络
    private int sessionType = 2;    //1单聊   2群聊   3群组
    private List<Message> timeList; //根据时间日期获取聊天记录
    private int messageId;
    public static final int RESULT_GROUP_NAME_SUCCEED = 0;
    public static final int RESULT_GROUP_NAME_FAIL = 1;
    public static final int FIND_GROUP_MEMBER_SUCCEED = 2;  //获取群组成员成功
    private static final int FIND_GROUP_INFO_SUCCEED = 3;   //获取群信息成功
    public static final int FIND_GROUP_MEMBER_FAIL = 6;  //获取群组成员失败
    private static final int NO_INTERNET = 4;   //没网
    private static final int FIND_RECORD_TIME_SUCCEED = 5;  //根据时间日期获取聊天记录成功

    //最后一条消息的id
    private int last_message_id;

    private long last_click_time;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case RESULT_GROUP_NAME_SUCCEED:
                    //修改标题
                    try {
                        JSONObject object = new JSONObject(sessionName);
                        sessionName = object.getString("sessionName");
                        author = object.getString("author");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    Log.i("TAG", "当前群组的名称是：" + sessionName);
                    titleBar.setTitle(sessionName);
                    break;

                case FIND_GROUP_MEMBER_SUCCEED:
                    Intent intent = new Intent(UIUtils.getContext(), ChatInformationActivity.class);
                    intent.putExtra("from", "group");
                    intent.putExtra("targetId", targetId);
                    intent.putExtra("sessionName", sessionName);
                    intent.putExtra("author", author);
                    intent.putExtra("isInternet", isInternet);
                    intent.putExtra("sessionType", sessionType);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("list", list);
                    intent.putExtra("bundle", bundle);
                    if(anim.isRunning()){
                        dis_noLoading.setVisibility(View.INVISIBLE);
                        anim.stop();
                    }
                    startActivityForResult(intent, 17);
                    break;

                case FIND_GROUP_MEMBER_FAIL://获取群组成员失败
                    break;

                case FIND_GROUP_INFO_SUCCEED:
                    if(anim.isRunning()){
                        dis_noLoading.setVisibility(View.INVISIBLE);
                        anim.stop();
                    }
                    Toast.makeText(UIUtils.getContext(), "获取信息失败！", Toast.LENGTH_SHORT).show();
                    break;

                case NO_INTERNET:
                    if(anim.isRunning()){
                        dis_noLoading.setVisibility(View.INVISIBLE);
                        anim.stop();
                    }
                    Toast.makeText(UIUtils.getContext(), "您没有连接网络，请连接后重试！", Toast.LENGTH_SHORT).show();
                    break;

                case FIND_RECORD_TIME_SUCCEED:
//                    Log.i("TAG", "cccccccccccccccccccccccccc:" + timeList.size());
                    emotionDisFragment.updateAdapter(timeList);
                    break;
            }
        }
    };

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent.getStringExtra("name") && !TextUtils.isEmpty(intent.getStringExtra("name"))) {
                sessionName = intent.getStringExtra("name");
                titleBar.setTitle(sessionName);
            } else if (null != intent.getParcelableExtra("message")) {
                Message message = intent.getParcelableExtra("message");
                emotionDisFragment.messageList.add(message);
                if (null != emotionDisFragment.messagesAdapter) {
                    emotionDisFragment.messagesAdapter.notifyDataSetChanged();
                }
            } else if(null != intent.getStringExtra("updateName") && intent.getStringExtra("updateName").equals("updateName")){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sessionName = null;
                        sessionName = httpManager.findSessionName(targetId);
                        if (sessionName == null || sessionName.equals("404")) {
//                        Log.i("TAG", "获取群组名称失败");
                            handler.sendEmptyMessage(RESULT_GROUP_NAME_FAIL);
                        } else {
//                        Log.i("TAG", "获取群组名称成功");
                            handler.sendEmptyMessage(RESULT_GROUP_NAME_SUCCEED);
                        }
                    }
                }).start();
            }
        }
    };

    /**
     * 软键盘和表情图的隐藏
     *
     * @param view 跟布局
     * @author LvZhangFeng
     * @time 2016年8月6日17点
     */
    public void setupUI(View view) {
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    emotionDisFragment.mkeyboard.hideEmotionLayout(false);
                    emotionDisFragment.mkeyboard.hideSoftInput();
                    return false;
                }
            });
        }

        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        from = getIntent().getStringExtra("from");
        if (from.equals("RecordCalendarActivity")) {
            get_group_name = false;
            sessionName = getIntent().getStringExtra("name");
            targetId = getIntent().getStringExtra("targetId");
            author = getIntent().getStringExtra("author");
            time = getIntent().getStringExtra("time");
            titleBar.setTitle(sessionName);
            initTimeMessage();
        } else if (from.equals("ChatRecordActivity")) {
            get_group_name = false;
            sessionName = getIntent().getStringExtra("name");
            targetId = getIntent().getStringExtra("targetId");
            author = getIntent().getStringExtra("author");
            messageId = getIntent().getIntExtra("messageId", -1);
            titleBar.setTitle(sessionName);
            getHistoryMessages();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_discussion);
        mContext = this;
        if (!NetWorkUtils.isNetworkAvailable(mContext)) {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.alert_msg_check_net), Toast.LENGTH_SHORT).show();
        }
        initView();
        registerBoradcastReceiver();
        setupUI(ll_discussion);
        initData();
        userAddGroup();
        initEmotionMainFragment();
        emotionDisFragment.setSendLastMessage(this);
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (from.equals("RecordCalendarActivity")){
//            initTimeMessage();
//        }else if(from.equals("ChatRecordActivity")){
//            getHistoryMessages();
//        }
//    }

    private void initView() {
        titleBar = (TitleBar) findViewById(R.id.discussion_title_bar);
        titleBar.setLeftIcon(R.mipmap.ic_fanhui, "消息");
        titleBar.setRightIcon(R.mipmap.ic_qunxiangqing);
        titleBar.setTitleClickListener(this);
//        titleBar.setRightVisibility(View.INVISIBLE);
        ll_discussion = (LinearLayout) findViewById(R.id.ll_discussion);

        messageBody = (ListView) findViewById(R.id.discussion_messagebody);

        dis_noLoading = (RelativeLayout) findViewById(R.id.dis_noLoading);
        dis_info_loading = (ImageView) findViewById(R.id.dis_info_loading);
        anim = (AnimationDrawable) dis_info_loading.getBackground();
    }

    /**
     * 把当前用户加入到该群中，这样才能群聊
     * 2016年10月25日   林动改接口了，不用加入了
     */
    public void userAddGroup() {
        sp = this.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        httpManager = new HttpManager();
//        String userId = sp.getString(Constant.ID_USER, "");
//        String name = sp.getString("name", "");
//        final List<String> userIdList = new ArrayList<>();
//        final List<String> userNameList = new ArrayList<>();
//        userIdList.add(userId);
//        userNameList.add(name);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                result_group = "";
//                try {
//                    result_group = httpManager.addGroup(targetId, sessionName, userIdList, userNameList);
//                    if (result_group == null || result_group.equals("404")) {
//                        Log.i("TAG", "user加入群组失败");
//                    } else {
//                        Log.i("TAG", "user加入群组成功" + result_group.toString());
//                    }
//                } catch (Exception e) {
//
//                }
//            }
//        }).start();

        //获取群聊名称
        if (get_group_name) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sessionName = null;
                    sessionName = httpManager.findSessionName(targetId);
                    if (sessionName == null || sessionName.equals("404")) {
//                        Log.i("TAG", "获取群组名称失败");
                        handler.sendEmptyMessage(RESULT_GROUP_NAME_FAIL);
                    } else {
//                        Log.i("TAG", "获取群组名称成功");
                        handler.sendEmptyMessage(RESULT_GROUP_NAME_SUCCEED);
                    }
                }
            }).start();
        }
    }

    private void initData() {
        type = Conversation.ConversationType.GROUP;
        from = getIntent().getStringExtra("from");
//        Log.i("info", "from=" + from);
        if (from.equals("MessageFragment")) {
            get_group_name = true;
            message = getIntent().getParcelableExtra("message");
            targetId = message.getTargetId();
//            Log.i("info", "tid=" + targetId);
//            titleBar.setTitle(discussion.getName());
            MessageContent latestMessage = message.getLatestMessage();
            if (latestMessage instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) latestMessage;
//                titleBar.setTitle(textMessage.getUserInfo().getName());

//                Log.i("TAG", "从MessageFragment进入到群聊，title=" + textMessage.getUserInfo().getName());
            }
        } else if (from.equals("SelectContactsActivity")) {
            get_group_name = false;
            targetId = getIntent().getStringExtra("sessionId");
            sessionName = getIntent().getStringExtra("sessionName");
            author = getIntent().getStringExtra("author");
            titleBar.setTitle(sessionName);
        } else if (from.equals("FriendInformationActivity")) {
            get_group_name = false;
            sessionName = getIntent().getStringExtra("sessionName");
            targetId = getIntent().getStringExtra("sessionId");
            author = getIntent().getStringExtra("author");
            sessionIcon = getIntent().getStringExtra("sessionIcon");
            titleBar.setTitle(sessionName);
        } else if (from.equals("RecordCalendarActivity")) {
            get_group_name = false;
            sessionName = getIntent().getStringExtra("name");
            targetId = getIntent().getStringExtra("targetId");
            author = getIntent().getStringExtra("author");
            time = getIntent().getStringExtra("time");
            titleBar.setTitle(sessionName);
            initTimeMessage();
        } else if (from.equals("ChatRecordActivity")) {
            get_group_name = false;
            sessionName = getIntent().getStringExtra("name");
            targetId = getIntent().getStringExtra("targetId");
            author = getIntent().getStringExtra("author");
            messageId = getIntent().getIntExtra("messageId", -1);
//            time = getIntent().getStringExtra("time");
            titleBar.setTitle(sessionName);
//            getHistoryMessages();
        }
        messageBody.setSelection(messageBody.getBottom());

        Log.i("TAG", "当前群聊id："+targetId);
        if (targetId != null && !targetId.equals("")) {
            setTitleBar();
        }
    }

    private void initEmotionMainFragment() {
        //构建传递参数
        Bundle bundle = new Bundle();
        //绑定主内容编辑框
        bundle.putBoolean(EmotionDisFragment.BIND_TO_EDITTEXT, true);
        //隐藏控件
        bundle.putBoolean(EmotionDisFragment.HIDE_BAR_EDITTEXT_AND_BTN, false);
        //传递消息参数

        bundle.putString("targetId", targetId);
        bundle.putSerializable("type", type);

        //替换fragment
        //创建修改实例
        emotionDisFragment = EmotionDisFragment.newInstance(EmotionDisFragment.class, bundle);
        emotionDisFragment.bindToContentView(messageBody);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (time != null && !TextUtils.isEmpty(time)) {
            emotionDisFragment.setSessionIdTime(targetId, sessionName, time);
        } else {
            emotionDisFragment.setSessionId(targetId, sessionName);
        }
        transaction.replace(R.id.discussion_fl_emotionview_main, emotionDisFragment);
        transaction.addToBackStack(null);
        //提交修改
        transaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 17)    //清除聊天记录
        {
            emotionDisFragment.messageList.clear();
            emotionDisFragment.messagesAdapter.notifyDataSetChanged();
        } else if (resultCode == 16) {   //退群了，退出当前聊天页面
            //发送广播
            Intent intent = new Intent(EmotionDisFragment.NEW_MESSAGE);
            intent.putExtra("new_message", "exit_group");
            intent.putExtra("targetId", targetId);
            this.sendBroadcast(intent);
            if(from != null && from.equals("FriendInformationActivity")) {
                this.setResult(RESULT_OK);
            }
            finish();
        }
    }

    @Override
    public void leftClick() {
        close();
    }

    @Override
    public void onBackPressed() {
        close();
    }

    private void close() {
        Intent intent = new Intent();
        setResult(1, intent);
        intent.putExtra("update", true);
        finish();
    }

    @Override
    public void rightClick() {
        if (System.currentTimeMillis() - last_click_time < 3000) {
            return;
        }
        dis_noLoading.setVisibility(View.VISIBLE);
        anim.start();
        last_click_time = System.currentTimeMillis();

        list = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (NetWorkUtils.isNetworkAvailable(UIUtils.getContext())) {
                    //获取群信息
                    String result = httpManager.findGroupInfo(targetId);
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        sessionType = jsonObject.getInt("sessionType");
//                    handler.sendEmptyMessage(FIND_GROUP_INFO_SUCCEED);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //获取群成员
                    isInternet = true;
                    String userId = sp.getString(Constant.ID_USER, "");
                    list = httpManager.findGroupRemarks(userId, targetId);

                    if (null != list || list.size() != 0) {
                        if (userId.equals(author)) {
                            GroupMember groupMember = new GroupMember("add", "");
                            list.add(groupMember);
                            if(list.size() > 1) {
                                GroupMember groupMember2 = new GroupMember("exit", "");
                                list.add(groupMember2);
                            }
                        } else {
                            GroupMember groupMember = new GroupMember("add", "");
                            list.add(groupMember);
                        }
                        handler.sendEmptyMessage(FIND_GROUP_MEMBER_SUCCEED);
                    }else{
                        handler.sendEmptyMessage(FIND_GROUP_MEMBER_FAIL);
                    }
                } else {
                    isInternet = false;
                    handler.sendEmptyMessage(NO_INTERNET);
                }
            }
        }).start();

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

    @Override
    public void sendMessageId(int msgId) {
        last_message_id = msgId;
    }

    //设置标题
    private void setTitleBar() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = String.format(Constant.MESSAGE_GET_SESSION_NAME, targetId);
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            final String result = response.body().string();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    String sname = result;
                                    try {
                                        JSONObject object = new JSONObject(sname);
                                        sname = object.optString("sessionName");
                                        if (sname == null) {
                                            titleBar.setTitle(sname);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                });
            }
        }).start();
    }

    //注册广播
    public void registerBoradcastReceiver() {
        IntentFilter intentFilter = new IntentFilter(ChatInformationActivity.UPDATE_NAME);
        this.registerReceiver(broadcastReceiver, intentFilter);
    }

    /**
     * 获取群组、群聊信息
     *
     * @param targetId
     */
    public void getGroupInfo(final String targetId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = httpManager.findGroupInfo(targetId);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    sessionType = jsonObject.getInt("sessionType");
//                    handler.sendEmptyMessage(FIND_GROUP_INFO_SUCCEED);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 根据时间日期获取聊天记录
     */
    public void initTimeMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (NetWorkUtils.isNetworkAvailable(UIUtils.getContext())) {
                    RongIMClient.getInstance().getLatestMessages(type, targetId, 500, new RongIMClient.ResultCallback<List<Message>>() {
                        @Override
                        public void onSuccess(List<Message> messageList) {
                            if (messageList != null) {
                                timeList = new ArrayList<Message>();
                                for (Message message : messageList) {
                                    String messageTime = UTCTime.getTime(message.getReceivedTime());
                                    if (messageTime.contains(time)) {
                                        if (message.getContent() instanceof TextMessage) {
                                            TextMessage textMessage = (TextMessage) message.getContent();
                                            String info = textMessage.getContent();
                                            try {
                                                JSONObject json = new JSONObject(info);
                                                int messageType = json.optInt("MessageType");
                                                if (messageType == 0) {
                                                    timeList.add(message);
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        } else if (message.getContent() instanceof ImageMessage) {
                                            timeList.add(message);
                                        }
                                    }
                                }
                                handler.sendEmptyMessage(FIND_RECORD_TIME_SUCCEED);
                            }
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {

                        }
                    });
                } else {
                    handler.sendEmptyMessage(NO_INTERNET);
                }
            }
        }).start();
    }

    /**
     * 获取某条消息之后的聊天记录
     */
    public void getHistoryMessages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                RongIMClient.getInstance().getHistoryMessages(type, targetId, null, messageId, 300, RongCommonDefine.GetMessageDirection.BEHIND, new RongIMClient.ResultCallback<List<Message>>() {
//                    @Override
//                    public void onSuccess(List<Message> messageList) {
//                        timeList = new ArrayList<Message>();
////                        if(messageList != null && messageList.size() > 0) {
//                            timeList.addAll(messageList);
//                            Log.i("ChatFileActivity", "qwewqeeqwe获取的数据是：" + timeList.toString());
////                        }
//                        handler.sendEmptyMessage(FIND_RECORD_TIME_SUCCEED);
//                    }
//
//                    @Override
//                    public void onError(RongIMClient.ErrorCode errorCode) {
//                        Log.i("ChatFileActivity", "vvvvvv获取的数据是失败的：");
//                    }
//                });
                timeList = new ArrayList<Message>();
                timeList.addAll(emotionDisFragment.messageList);
                for (int i = 0; i < emotionDisFragment.messageList.size(); i++) {
                    int u = emotionDisFragment.messageList.get(i).getMessageId();
                    if (messageId != emotionDisFragment.messageList.get(i).getMessageId()) {
                        Message message = emotionDisFragment.messageList.get(i);
                        boolean b = timeList.remove(message);
                    } else if (messageId == emotionDisFragment.messageList.get(i).getMessageId()) {
                        break;
                    }
                }
                handler.sendEmptyMessageAtTime(FIND_RECORD_TIME_SUCCEED, 1000);
            }
//                if (NetWorkUtils.isNetworkAvailable(UIUtils.getContext())) {
//                    RongIMClient.getInstance().getLatestMessages(type, targetId, 500, new RongIMClient.ResultCallback<List<Message>>() {
//                        @Override
//                        public void onSuccess(List<Message> messageList) {
//                            if (messageList != null) {
//                                timeList = new ArrayList<Message>();
//                                boolean isAdd = false;
//                                timeList.addAll(messageList);
//                                for (Message message : timeList) {
//                                    int  i = message.getMessageId();
//                                    if(message.getMessageId() != messageId){
//                                        timeList.remove(message);
//                                    }else{
//                                        break;
//                                    }
//                                }
//                                handler.sendEmptyMessage(FIND_RECORD_TIME_SUCCEED);
//                            }
//                        }
//
//                        @Override
//                        public void onError(RongIMClient.ErrorCode errorCode) {
//
//                        }
//                    });
//                } else {
//                    handler.sendEmptyMessage(NO_INTERNET);
//                }
//            }
        }).start();

    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
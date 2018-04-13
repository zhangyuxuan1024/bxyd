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
import net.iclassmate.bxyd.bean.owner.SpaceInfo;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.ui.fragment.emotion.EmotionGroupFragment;
import net.iclassmate.bxyd.utils.HttpManager;
import net.iclassmate.bxyd.utils.JsonUtils;
import net.iclassmate.bxyd.utils.NetWorkUtils;
import net.iclassmate.bxyd.utils.SendLastMessage;
import net.iclassmate.bxyd.utils.UIUtils;
import net.iclassmate.bxyd.utils.UTCTime;
import net.iclassmate.bxyd.view.TitleBar;

import org.json.JSONArray;
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

//群组
public class GroupChatActivity extends FragmentActivity implements TitleBar.TitleOnClickListener, SendLastMessage {
    private Context mContext;
    private TitleBar titleBar;
    private ListView messageBody;
    private String from, targetId, sessionName, author, sessionIcon;
    private String time;
    private LinearLayout ll_discussion;
    private RelativeLayout discussion_noLoading;    //动画
    private ImageView iv_record_info_loading;       //动画
    private AnimationDrawable anim;
    private Conversation message;
    private Conversation.ConversationType type;
    private EmotionGroupFragment emotionGroupFragment;
    private SharedPreferences sp;
    private String result_group;
    private HttpManager httpManager;
    private Boolean get_group_name; //是否获取群组名称和群主ID
    private ArrayList<GroupMember> list;
    private Boolean isInternet = false; //是否有网络
    private int sessionType = 3;    //1单聊   2群聊   3群组   4机构
    private List<Message> timeList; //根据时间日期获取聊天记录
    private boolean focusMe;  //是否允许关注我
    private String ownerId; //如果是机构，就是创建者id，用于获取机构头像
    private ArrayList<String> administratorsList;    //管理员
    public static final int RESULT_GROUP_NAME_SUCCEED = 0;
    public static final int RESULT_GROUP_NAME_FAIL = 1;
    public static final int FIND_GROUP_MEMBER_SUCCEED = 2;  //获取群组成员成功
    public static final int FIND_GROUP_MEMBER_FAIL = 6;  //获取群组成员失败
    private static final int FIND_GROUP_INFO_SUCCEED = 3;   //获取群信息成功
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
//                    try {
//                        JSONObject object = new JSONObject(sessionName);
//                        sessionName = object.getString("sessionName");
//                        author = object.getString("author");
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    Log.i("TAG", "当前群组的名称是：" + sessionName);
                    titleBar.setTitle(sessionName);
                    break;

                case FIND_GROUP_MEMBER_SUCCEED:
                    if (anim.isRunning()) {
                        discussion_noLoading.setVisibility(View.INVISIBLE);
                        anim.stop();
                    }
                    Intent intent = new Intent(UIUtils.getContext(), ChatInformationActivity.class);
                    intent.putExtra("from", "group");
                    intent.putExtra("targetId", targetId);
                    intent.putExtra("sessionName", sessionName);
                    intent.putExtra("author", author);
                    intent.putExtra("isInternet", isInternet);
                    intent.putExtra("sessionType", sessionType);
                    intent.putStringArrayListExtra("administratorsList", administratorsList);  //管理员id
                    intent.putExtra("focusMe", focusMe);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("list", list);
                    intent.putExtra("bundle", bundle);
                    if (anim.isRunning()) {
                        discussion_noLoading.setVisibility(View.INVISIBLE);
                        anim.stop();
                    }
                    startActivityForResult(intent, 17);
                    break;

                case FIND_GROUP_MEMBER_FAIL:
                    if (anim.isRunning()) {
                        discussion_noLoading.setVisibility(View.INVISIBLE);
                        anim.stop();
                    }
                    Toast.makeText(UIUtils.getContext(), "获取信息失败！", Toast.LENGTH_SHORT).show();
                    break;

                case FIND_GROUP_INFO_SUCCEED:
                    break;

                case NO_INTERNET:
                    if (anim.isRunning()) {
                        discussion_noLoading.setVisibility(View.INVISIBLE);
                        anim.stop();
                    }
                    Toast.makeText(UIUtils.getContext(), "您没有连接网络，请连接后重试！", Toast.LENGTH_SHORT).show();
                    break;

                case FIND_RECORD_TIME_SUCCEED:
                    emotionGroupFragment.updateAdapter(timeList);
                    break;
            }
        }
    };

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("name") != null && !TextUtils.isEmpty(intent.getStringExtra("name"))) {
                sessionName = intent.getStringExtra("name");
                titleBar.setTitle(sessionName);
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
                    emotionGroupFragment.mkeyboard.hideEmotionLayout(false);
                    emotionGroupFragment.mkeyboard.hideSoftInput();
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
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_group_chat);
        mContext = this;
        if (!NetWorkUtils.isNetworkAvailable(mContext)) {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.alert_msg_check_net), Toast.LENGTH_SHORT).show();
        }
        initView();
        registerBoradcastReceiver();
        setupUI(ll_discussion);
        initData();
        userAddGroup();
        findSpaceInfo();
        initEmotionMainFragment();
        emotionGroupFragment.setSendLastMessage(this);
    }

    private void initView() {
        titleBar = (TitleBar) findViewById(R.id.discussion_title_bar);
        titleBar.setLeftIcon(R.mipmap.ic_fanhui, "消息");
        titleBar.setRightIcon(R.mipmap.ic_qunxiangqing);
        titleBar.setTitleClickListener(this);
//        titleBar.setRightVisibility(View.INVISIBLE);
        ll_discussion = (LinearLayout) findViewById(R.id.ll_discussion);

        messageBody = (ListView) findViewById(R.id.discussion_messagebody);
        discussion_noLoading = (RelativeLayout) findViewById(R.id.discussion_noLoading);
        iv_record_info_loading = (ImageView) findViewById(R.id.iv_record_info_loading);
        anim = (AnimationDrawable) iv_record_info_loading.getBackground();
    }

    /**
     * 把当前用户加入到该群中，这样才能群聊
     */
    public void userAddGroup() {
        sp = this.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
//        String userId = sp.getString(Constant.ID_USER, "");
//        String name = sp.getString("name", "");
//        final List<String> userIdList = new ArrayList<>();
//        final List<String> userNameList = new ArrayList<>();
//        userIdList.add(userId);
//        userNameList.add(name);
        httpManager = new HttpManager();
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
//        if (get_group_name) {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    sessionName = null;
//                    sessionName = httpManager.findSessionName(targetId);
//                    if (sessionName.equals("404")) {
//                        Log.i("TAG", "获取群组名称失败");
//                        handler.sendEmptyMessage(RESULT_GROUP_NAME_FAIL);
//                    } else {
//                        Log.i("TAG", "获取群组名称成功");
//                        handler.sendEmptyMessage(RESULT_GROUP_NAME_SUCCEED);
//                    }
//                }
//            }).start();
//        }
    }

    private void initData() {
        type = Conversation.ConversationType.GROUP;
        from = getIntent().getStringExtra("from");
        Log.i("info", "from=" + from);
        if (from.equals("MessageFragment")) {
            get_group_name = true;
            message = getIntent().getParcelableExtra("message");
            targetId = message.getTargetId();
//            titleBar.setTitle(discussion.getName());
            MessageContent latestMessage = message.getLatestMessage();
            if (latestMessage instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) latestMessage;
//                titleBar.setTitle(textMessage.getUserInfo().getName());

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
        }
        messageBody.setSelection(messageBody.getBottom());

//        Log.i("info", "tid=" + targetId);
        if (targetId != null && !targetId.equals("")) {
            setTitleBar();
        }
    }

    private void initEmotionMainFragment() {
        //构建传递参数
        Bundle bundle = new Bundle();
        //绑定主内容编辑框
        bundle.putBoolean(EmotionGroupFragment.BIND_TO_EDITTEXT, true);
        //隐藏控件
        bundle.putBoolean(EmotionGroupFragment.HIDE_BAR_EDITTEXT_AND_BTN, false);
        //传递消息参数

        bundle.putString("targetId", targetId);
        bundle.putSerializable("type", type);

        //替换fragment
        //创建修改实例
        emotionGroupFragment = EmotionGroupFragment.newInstance(EmotionGroupFragment.class, bundle);
        emotionGroupFragment.bindToContentView(messageBody);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (time != null && !TextUtils.isEmpty(time)) {
            emotionGroupFragment.setSessionIdTime(targetId, sessionName, time);
        } else {
            emotionGroupFragment.setSessionId(targetId, sessionName);
        }
        transaction.replace(R.id.discussion_fl_emotionview_main, emotionGroupFragment);
        transaction.addToBackStack(null);
        //提交修改
        transaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 17)    //清除聊天记录
        {
            emotionGroupFragment.messageList.clear();
            emotionGroupFragment.messagesAdapter.notifyDataSetChanged();
        } else if (resultCode == 16) {   //退群了，退出当前聊天页面
            //发送广播
            Intent intent = new Intent(EmotionGroupFragment.NEW_MESSAGE);
            intent.putExtra("new_message", "exit_group");
            intent.putExtra("targetId", targetId);
            this.sendBroadcast(intent);
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
        discussion_noLoading.setVisibility(View.VISIBLE);
        anim.start();
        last_click_time = System.currentTimeMillis();
        list = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (NetWorkUtils.isNetworkAvailable(UIUtils.getContext())) {
                    //获取群信息
//                    String result = httpManager.findGroupInfo(targetId);
//                    try {
//                        JSONObject jsonObject = new JSONObject(result);
//                        sessionType = jsonObject.getInt("sessionType");
////                    handler.sendEmptyMessage(FIND_GROUP_INFO_SUCCEED);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }

                    //获取群成员
                    isInternet = true;
                    String userId = sp.getString(Constant.ID_USER, "");
                    list = httpManager.findGroupRemarks2(targetId, userId);

                    if (null != list || list.size() != 0) {
                        Log.i("TAG", "nnnnnnnnnnnnnn:"+administratorsList.toString());
                        if (administratorsList.contains(userId)) {
                            Log.i("TAG", "mmmmmmmmmmmmmmmmmm:"+administratorsList.toString());
                            GroupMember groupMember = new GroupMember("add", "");
                            list.add(groupMember);
                            if (list.size() > 1) {
                                GroupMember groupMember2 = new GroupMember("exit", "");
                                list.add(groupMember2);
                            }
                        } else {
//                            GroupMember groupMember = new GroupMember("add", "");
//                            list.add(groupMember);
                        }
                        handler.sendEmptyMessage(FIND_GROUP_MEMBER_SUCCEED);
                    } else {
                        handler.sendEmptyMessage(FIND_GROUP_MEMBER_FAIL);
                    }
                } else {
                    handler.sendEmptyMessage(NO_INTERNET);
                    isInternet = false;
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
     * 查看空间信息（获取群组空间管理员）
     */
    public void findSpaceInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (NetWorkUtils.isNetworkAvailable(UIUtils.getContext())) {
                    String result = httpManager.findSpaceInfo3(false, targetId);
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        author = jsonObject.optString("ownerId");
                        String type = jsonObject.optString("type");
                        sessionName = jsonObject.optString("name");
                        if (type != null && !TextUtils.isEmpty(type) && type.equals("group")) {
                            sessionType = 3;
                        } else if (type != null && !TextUtils.isEmpty(type) && type.equals("org")) {
                            sessionType = 4;
                        }
                        SpaceInfo spaceInfo = null;
                        spaceInfo = JsonUtils.StartSpaceInfoJson(result);
                        focusMe = spaceInfo.getAuthority().isFocusMe();

                        JSONObject jsonObject2 = jsonObject.getJSONObject("administrators");
                        JSONArray jsonArray1 = jsonObject2.getJSONArray("list");
                        administratorsList = new ArrayList<String>();
                        for (int j = 0; j < jsonArray1.length(); j++) {
                            JSONObject jsonObject3 = jsonArray1.getJSONObject(j);
                            administratorsList.add(jsonObject3.getString("userId"));
                        }
                        Log.i("TAG","管理员管理员管理员："+administratorsList.toString());
                        handler.sendEmptyMessage(RESULT_GROUP_NAME_SUCCEED);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
                    RongIMClient.getInstance().getLatestMessages(type, targetId, 200, new RongIMClient.ResultCallback<List<Message>>() {
                        @Override
                        public void onSuccess(List<Message> messageList) {
                            if (messageList != null) {
                                timeList = new ArrayList<Message>();
                                for (Message message : messageList) {
                                    String messageTime = UTCTime.getTime(message.getReceivedTime());
                                    Log.i("TAG","通过时间获取聊天记录：messageTime="+messageTime+",time"+time);
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
                                Log.i("TAG","通过时间获取聊天记录：timeList="+timeList.size()+",time"+time);
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
     * 根据时间日期获取聊天记录
     */
    public void initTimeMessage2() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Log.i("TAG","cccccc通过时间获取聊天记录：y="+emotionGroupFragment);
                if(null != emotionGroupFragment.messageList) {
                    int y = 0;
                    for(int i = 0; i < emotionGroupFragment.messageList.size(); i++){
                        String messageTime = UTCTime.getTime(message.getReceivedTime());
                        if(messageTime.contains(time)){
                            y = i;
                            break;
                        }
                    }
                    Log.i("TAG","cccccc通过时间获取聊天记录：y="+y);
                }
            }
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

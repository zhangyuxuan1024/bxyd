package net.iclassmate.bxyd.ui.activitys;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.adapter.AddFriendAdapter;
import net.iclassmate.bxyd.bean.message.Auth;
import net.iclassmate.bxyd.bean.message.RMessage;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.utils.DataCallback;
import net.iclassmate.bxyd.utils.HttpManager;
import net.iclassmate.bxyd.utils.NetWorkUtils;
import net.iclassmate.bxyd.utils.UIUtils;
import net.iclassmate.bxyd.view.TitleBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.message.TextMessage;

/**
 * 待办界面
 */
public class BacklogActivity extends FragmentActivity implements TitleBar.TitleOnClickListener, DataCallback {
    private Context mContext;
    private TitleBar titleBar;
    private ArrayList<Message> lists;
    private SwipeMenuListView mSwipeMenuListView;
    private AddFriendAdapter addFriendAdapter;
    private HttpManager httpManager;
    private ImageView img;

    private SharedPreferences sharedPreferences;
    private int cur_position;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    int code = msg.arg1;
                    Message message = (Message) msg.obj;
                    RMessage rMessage = new RMessage(message);
                    if (code == 200) {
                        RongIMClient.getInstance().setMessageSentStatus(message.getMessageId(), Message.SentStatus.RECEIVED);
                        message.setSentStatus(Message.SentStatus.RECEIVED);
                        if (lists == null) {
                            return;
                        }
                        if (cur_position >= 0 && cur_position < lists.size()) {
                            lists.set(cur_position, message);
                            addFriendAdapter.notifyDataSetChanged();
                            if (rMessage.getAuth() == null) {
                                sendAgreeMessage(message);
                            } else {
                                sendJoinMessage(message);
                            }
                        }
                    } else {
                        if (rMessage.getAuth() == null) {
                            if (code == 8043) {
                                RongIMClient.getInstance().setMessageSentStatus(message.getMessageId(), Message.SentStatus.RECEIVED);
                                message.setSentStatus(Message.SentStatus.RECEIVED);
                                if (lists == null) {
                                    return;
                                }
                                if (cur_position >= 0 && cur_position < lists.size()) {
                                    lists.set(cur_position, message);
                                    addFriendAdapter.notifyDataSetChanged();
                                }
                                Toast.makeText(mContext, "不能重复添加好友", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mContext, "申请添加好友失败", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(mContext, "申请加入空间失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
            }
        }
    };

    private void sendJoinMessage(Message message1) {
        RMessage rMessage = new RMessage(message1);
        Auth auth = rMessage.getAuth();
        if (auth == null) {
            return;
        }
        JSONObject json = new JSONObject();
        try {
            json.put("MessageType", 11);
            json.put("ContentType", 1);
            json.put("ChatType", 2);
            json.put("Content", "欢迎" + auth.getUserName() + "加入" + auth.getSpaceName());
            json.put("FontSize", 14);
            json.put("FontStyle", 0);
            json.put("FontColor", 0);
            json.put("BulletinID", "");
            json.put("BulletinContent", "");
            json.put("requestName", "");
            json.put("requestRemark", "");
            json.put("requestGroupId", "");
            json.put("FileID", "");
            json.put("FileName", "");
            json.put("CreateTime", System.currentTimeMillis());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String messageBody = json.toString();
        //Log.d("EmotionMainFragment", "发送内容" + messageBody);
        if (messageBody.equals("")) {
            Toast.makeText(UIUtils.getContext(), "发送内容不能为空", Toast.LENGTH_SHORT).show();
        } else {
            UserInfo userInfo = new UserInfo(sharedPreferences.getString(Constant.ID_USER, ""), sharedPreferences.getString(Constant.USER_NAME, ""), null);
            final TextMessage textMessage = TextMessage.obtain(messageBody);
            textMessage.setUserInfo(userInfo);

            //添加消息
            final Message message = Message.obtain(message1.getTargetId(), message1.getConversationType(), textMessage);
            message.setMessageDirection(Message.MessageDirection.SEND);
            message.setSenderUserId(sharedPreferences.getString(Constant.ID_USER, ""));
            RongIMClient.getInstance().sendMessage(Conversation.ConversationType.GROUP, auth.getSpaceId(),
                    textMessage, null, null, new RongIMClient.SendMessageCallback() {
                        @Override
                        public void onSuccess(Integer integer) {

                        }

                        @Override
                        public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {

                        }
                    }, new RongIMClient.ResultCallback<Message>() {
                        @Override
                        public void onSuccess(Message message) {

                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {

                        }
                    });
        }
    }

    private void sendAgreeMessage(Message messageSend) {
        JSONObject json = new JSONObject();
        try {
            json.put("MessageType", 4);
            json.put("ContentType", 1);
            json.put("ChatType", 0);
            json.put("Content", "添加好友成功，开始讨论吧");
            json.put("FontSize", 14);
            json.put("FontStyle", 0);
            json.put("FontColor", 0);
            json.put("BulletinID", "");
            json.put("BulletinContent", "");
            json.put("requestName", "");
            json.put("requestRemark", "");
            json.put("requestGroupId", "");
            json.put("FileID", "");
            json.put("FileName", "");
            json.put("CreateTime", System.currentTimeMillis());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String messageBody = json.toString();
        //Log.d("EmotionMainFragment", "发送内容" + messageBody);
        if (messageBody.equals("")) {
            Toast.makeText(UIUtils.getContext(), "发送内容不能为空", Toast.LENGTH_SHORT).show();
        } else {
            UserInfo userInfo = new UserInfo(sharedPreferences.getString(Constant.ID_USER, ""), sharedPreferences.getString("name", ""), null);
            final TextMessage textMessage = TextMessage.obtain(messageBody);
            textMessage.setUserInfo(userInfo);

            //添加消息
            final Message message = Message.obtain(messageSend.getTargetId(), messageSend.getConversationType(), textMessage);
            message.setMessageDirection(Message.MessageDirection.SEND);
            message.setSenderUserId(sharedPreferences.getString(Constant.ID_USER, ""));
            RongIMClient.getInstance().sendMessage(messageSend.getConversationType(), messageSend.getTargetId(),
                    textMessage, null, null, new RongIMClient.SendMessageCallback() {
                        @Override
                        public void onSuccess(Integer integer) {

                        }

                        @Override
                        public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {

                        }
                    }, new RongIMClient.ResultCallback<Message>() {
                        @Override
                        public void onSuccess(Message message) {

                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {

                        }
                    });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_backlog);
        mContext = this;
        sharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCES, MODE_PRIVATE);

        initView();
        initData();
        initLinstener();
    }

    private void initView() {
        titleBar = (TitleBar) findViewById(R.id.backlog_title_bar);
        titleBar.setTitle("待办");
        titleBar.setLeftIcon(R.mipmap.ic_fanhui, "消息");
        httpManager = new HttpManager();
        mSwipeMenuListView = (SwipeMenuListView) findViewById(R.id.add_contacts_listView);
        initListView();
        img = (ImageView) findViewById(R.id.img_anim);
    }

    private void initListView() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        mContext.getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(244, 53, 49)));
                deleteItem.setWidth(UIUtils.dip2px(73));
                deleteItem.setIcon(R.mipmap.wenzi_shanchu);
                menu.addMenuItem(deleteItem);
            }
        };
        mSwipeMenuListView.setMenuCreator(creator);
        mSwipeMenuListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(final int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        Message message = lists.get(position);
                        lists.remove(position);
                        addFriendAdapter.notifyDataSetChanged();
                        if (lists.size() == 0) {
                            img.setVisibility(View.VISIBLE);
                            img.setImageResource(R.mipmap.ic_no_result);
                        }
                        RongIMClient.getInstance().removeConversation(message.getConversationType(), message.getTargetId(), new RongIMClient.ResultCallback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean aBoolean) {

                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {

                            }
                        });
                        break;
                    default:
                        break;
                }
            }
        });
        // listview item click event
        mSwipeMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

            }
        });
    }

    private void initData() {
        lists = (ArrayList<Message>) getIntent().getSerializableExtra("contactsNotifications");
        //初始化或刷新适配器
        addFriendAdapter = new AddFriendAdapter(lists, mContext, httpManager, BacklogActivity.this, this);
        mSwipeMenuListView.setAdapter(addFriendAdapter);
        addFriendAdapter.setBtnAgreeClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetWorkUtils.isNetworkAvailable(mContext)) {
                    Toast.makeText(mContext, getResources().getString(R.string.alert_msg_check_net), Toast.LENGTH_SHORT).show();
                } else {
                    int position = (int) v.getTag();
                    cur_position = position;
                    final Message message = lists.get(position);
                    RMessage rMessage = new RMessage(message);
                    final Auth auth = rMessage.getAuth();
                    final String uid = sharedPreferences.getString(Constant.ID_USER, "");
                    final String name = sharedPreferences.getString(Constant.USER_NAME, "");
                    String requestName = "";
                    if (auth == null) {
                        if (message.getContent() instanceof TextMessage) {
                            TextMessage textMessage = (TextMessage) message.getContent();
                            String info = textMessage.getContent();
                            try {
                                JSONObject json = new JSONObject(info);
                                requestName = json.getString("requestName");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            return;
                        }
                    }

                    final String finalRequestName = requestName;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int code = -1;
                            if (auth == null) {
                                code = httpManager.agreeFriendRequest(uid, message.getTargetId(), name, finalRequestName);
                            } else {
                                String objectId = auth.getObjectId();
                                if (objectId != null && !objectId.equals("")) {
                                    code = httpManager.reqJoinGroup(auth.getObjectId(), uid);
                                }
                            }
                            android.os.Message msg = new android.os.Message();
                            msg.what = 1;
                            msg.arg1 = code;
                            msg.obj = message;
                            mHandler.sendMessage(msg);
                        }
                    }).start();
                }
            }
        });

        if (lists.size() == 0) {
            img.setVisibility(View.VISIBLE);
            img.setImageResource(R.mipmap.ic_noresult_xiaoxi);
        } else {
            img.setVisibility(View.GONE);
            for (int i = 0; i < lists.size(); i++) {
                Message message = lists.get(i);
                RongIMClient.getInstance().setMessageSentStatus(message.getMessageId(), Message.SentStatus.READ);
            }
        }
    }

    private void initLinstener() {
        titleBar.setTitleClickListener(this);
    }

    private void initOrRefreshAdapter() {
        addFriendAdapter.notifyDataSetChanged();
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
        intent.putExtra("contactsNotifications", lists);
        this.finish();
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

    @Override
    public void sendData(Object object) {
        int position = (int) object;
//        ContactsNotification info=lists.get(position);
//        info.setType(2);
//        lists.set(position,info);
        Message message = lists.get(position);
        message.setExtra("isAgreed");
        lists.set(position, message);
        sendMessage();
        addFriendAdapter.notifyDataSetChanged();
    }

    private void sendMessage() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("BacklogActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("BacklogActivity");
        MobclickAgent.onPause(this);
    }
}

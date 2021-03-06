package net.iclassmate.bxyd.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.jauker.widget.BadgeView;
import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.adapter.MessageFragmentAdapter;
import net.iclassmate.bxyd.bean.message.Auth;
import net.iclassmate.bxyd.bean.message.RMessage;
import net.iclassmate.bxyd.bean.message.SpaceMessage;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.rongCloud.RongCloudContext;
import net.iclassmate.bxyd.ui.activitys.BacklogActivity;
import net.iclassmate.bxyd.ui.activitys.chat.ChatActivity;
import net.iclassmate.bxyd.ui.activitys.chat.DiscussionActivity;
import net.iclassmate.bxyd.ui.activitys.chat.GroupChatActivity;
import net.iclassmate.bxyd.ui.activitys.chat.ReceiverMessage;
import net.iclassmate.bxyd.ui.activitys.constacts.AddFriendActivity;
import net.iclassmate.bxyd.ui.activitys.constacts.PhoneContactActivity;
import net.iclassmate.bxyd.ui.fragment.emotion.EmotionMainFragment;
import net.iclassmate.bxyd.utils.DataCallback;
import net.iclassmate.bxyd.utils.HttpManager;
import net.iclassmate.bxyd.utils.MessageCallback;
import net.iclassmate.bxyd.utils.NetWorkUtils;
import net.iclassmate.bxyd.utils.NoticeUtills;
import net.iclassmate.bxyd.utils.PopWindowUtils;
import net.iclassmate.bxyd.utils.UIUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.CommandNotificationMessage;
import io.rong.message.ImageMessage;
import io.rong.message.TextMessage;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends Fragment implements MessageCallback, DataCallback, View.OnClickListener {
    private Context mContext;
    private String targetId;
    boolean isFirst = true;
    boolean isInitCoversation = true;
    private List<Conversation> messageList;
    private List<String> targetList;
    private List<String> noticeIdList;
    private List<String> isTopList;
    private TextView mAddFriendName;
    private SharedPreferences msharedPreferences;
    public final static int RECEIVE_MESSAGE_FR = 0;
    public final static int RECEIVE_DISCUSSION_NOTIFICATION = 1;
    public final static int RECEIVE_DISCUSSION_MESSAGE = 2;
    private SwipeMenuListView mSwipeMenuListView;
    private MessageFragmentAdapter messageFragmentAdapter;
    private ArrayList<Message> contactsNotifications;
    private int isTopNum = 0;
    private int isTopNum2 = 0;

    //点击聊天界面
    public static final int REQ_OPEN = 1;
    //点击待办界面
    public static final int REQ_OPEN_WAIT = 2;
    //当前点击的tagid
    private String cur_click_id;
    //待办图标
    private ImageView img_wait;
    //数字提醒
    private BadgeView badgeView;
    private int wait_unread;
    //当前点击的tid
    private String cur_click_tid;

    //接收广播
    private ReceiverMessage receiverMessage;

    private ImageView img_no_result;
    private long last_click_time;
    //是否有声音
    private boolean ishasring, cur_ishasing;
    private boolean hasFriend;
    private HttpManager httpManager;
    private View view_no_friend;
    private ImageView img_add_friend;
    //上次消息响铃的时间
    private long last_ring_time;

    private DataCallback dataCallback;
    private View headView;

    public void setDataCallback(DataCallback dataCallback) {
        this.dataCallback = dataCallback;
    }

    public MessageFragment() {
    }


    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                //接收消息
                case 0:
                    initOrRefresAdapter(messageList);
                    view_no_friend.setVisibility(View.INVISIBLE);
                    img_no_result.setVisibility(View.GONE);
                    msharedPreferences.edit().putBoolean(Constant.HAS_FRIEND, true).commit();
                    break;
                case RECEIVE_DISCUSSION_MESSAGE:
                    initOrRefresAdapter(messageList);
                    view_no_friend.setVisibility(View.INVISIBLE);
                    img_no_result.setVisibility(View.GONE);
                    msharedPreferences.edit().putBoolean(Constant.HAS_FRIEND, true).commit();
                    break;
                case RECEIVE_DISCUSSION_NOTIFICATION:
                    initOrRefresAdapter(messageList);
                    view_no_friend.setVisibility(View.INVISIBLE);
                    img_no_result.setVisibility(View.GONE);
                    msharedPreferences.edit().putBoolean(Constant.HAS_FRIEND, true).commit();
                    break;
                case 8:
                    if (messageList.size() == 0 && mSwipeMenuListView.getHeaderViewsCount() == 0 && hasFriend) {
                        img_no_result.setImageResource(R.mipmap.ic_noresult_xiaoxi);
                        img_no_result.setVisibility(View.VISIBLE);
                    } else {
                        img_no_result.setVisibility(View.GONE);
                    }
                    break;
                case 4:
                    img_no_result.setVisibility(View.GONE);
                    mAddFriendName.setVisibility(View.VISIBLE);
                    Object object = msg.obj;
                    if (object instanceof String) {
                        String requestName = (String) msg.obj;
                        mAddFriendName.setText(requestName + "申请加你为好友");
                    } else if (object instanceof Auth) {
                        Auth auth = (Auth) msg.obj;
                        mAddFriendName.setText(auth.getUserName() + "申请加入" + auth.getSpaceName() + "空间");
                    }
                    badgeView.setVisibility(View.VISIBLE);
                    waitNumShow();
                    addHeadView();
                    break;
                //隐藏待办
                case 6:
                    addHeadView();
                    if (messageList.size() == 0 && mSwipeMenuListView.getHeaderViewsCount() == 0) {
                        img_no_result.setImageResource(R.mipmap.ic_noresult_xiaoxi);
                        img_no_result.setVisibility(View.VISIBLE);
                    }
                    break;
                case 7:
                    Auth auth = (Auth) msg.obj;
                    String spaceId = auth.getSpaceId();
                    String userId = auth.getUserId();
                    if (contactsNotifications != null && contactsNotifications.size() > 0) {
                        for (int i = 0; i < contactsNotifications.size(); i++) {
                            Message message = contactsNotifications.get(i);
                            RMessage rMessage = new RMessage(message);
                            if (rMessage.getAuth() != null && rMessage.getAuth().getSpaceId().equals(spaceId) && rMessage.getAuth().getUserId().equals(userId)) {
                                message.setSentStatus(Message.SentStatus.RECEIVED);
                                contactsNotifications.set(i, message);
                                break;
                            }
                        }
                    }
                    addHeadView();
                    break;
                default:
                    break;
            }
            sendUnMessCount();
            if (mSwipeMenuListView.getHeaderViewsCount() == 0) {
                noticeIdList.clear();
            }
        }
    };

    private void sendUnMessCount() {
        if (messageList != null) {
            int count = 0;
            for (int i = 0; i < messageList.size(); i++) {
                Conversation conversation = messageList.get(i);
                count += conversation.getUnreadMessageCount();
            }
            count = count + wait_unread;
            dataCallback.sendData(count);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBoradcastReceiver();
    }

    @Override
    public void onAttach(Context context) {
        dataCallback = (DataCallback) context;
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        View view = inflater.inflate(R.layout.fragment_message, null, false);
        httpManager = new HttpManager();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("message_copy");
        initView(view);
        hasFriend = msharedPreferences.getBoolean(Constant.HAS_FRIEND, true);
        if (hasFriend) {
            view_no_friend.setVisibility(View.INVISIBLE);
            if (messageList.size() == 0 && mSwipeMenuListView.getHeaderViewsCount() == 0) {
                img_no_result.setVisibility(View.VISIBLE);
                img_no_result.setImageResource(R.mipmap.ic_noresult_xiaoxi);
            } else {
                img_no_result.setVisibility(View.GONE);
            }
        } else {
            view_no_friend.setVisibility(View.VISIBLE);
            img_no_result.setVisibility(View.GONE);
            if (messageList != null && messageFragmentAdapter != null) {
                messageList.clear();
                messageFragmentAdapter.notifyDataSetChanged();
            }
        }
        initMessages();
        return view;
    }

    private void initOrRefresAdapter(List<Conversation> messageList) {
        if (messageFragmentAdapter == null) {
            messageFragmentAdapter = new MessageFragmentAdapter(messageList, mContext);
            mSwipeMenuListView.setAdapter(messageFragmentAdapter);
        } else {
            messageFragmentAdapter.notifyDataSetChanged();
        }
    }

    private void initMessages() {
        RongCloudContext.getInstance().init(mContext, this);
        //获取服务器消息
        RongCloudContext.getInstance().registerReceiveMessageListener();
        if (isInitCoversation) {
            isInitCoversation = false;
            getConversation();
        }
    }

    //获取本地存储消息
    private void getConversation() {
        isTopNum = 0;
        if (!NetWorkUtils.isNetworkAvailable(mContext)) {
            img_no_result.setImageResource(R.mipmap.img_yemianjiazaishibai);
            img_no_result.setVisibility(View.VISIBLE);
            return;
        } else {
            img_no_result.setVisibility(View.GONE);
        }

        //获取本地存储消息
        RongIMClient.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                targetList.clear();
                messageList.clear();
                isTopList.clear();

                String result = "";
                if (conversations != null && conversations.size() > 0) {
                    for (int i = 0; i < conversations.size(); i++) {
                        Conversation conversation = conversations.get(i);
                        String tid = conversation.getTargetId();
                        if (conversation.isTop()) {
                            isTopNum++;
                            isTopList.add(tid);
                        }
                        if (tid != null && !tid.equals("") && !tid.contains("@abc@") && !targetList.contains(tid)) {
                            targetList.add(tid);
                            addConversation(conversation);
                        } else {
                            MessageContent message = conversation.getLatestMessage();
                            if (message == null) {
                                return;
                            }
                            RMessage rMessage = new RMessage(message);
                            Auth auth = rMessage.getAuth();
                            if (auth != null) {
                                String subType = auth.getSubType();
                                if (subType != null && subType.equals("joinSpace")) {
                                    String type = auth.getType();
                                    if (type != null && type.equals("auth")) {
                                        noticeIdList.add(auth.getObjectId());
                                        Message message1 = new Message();
                                        message1.setContent(message);
                                        message1.setConversationType(conversation.getConversationType());
                                        message1.setSentTime(conversation.getSentTime());
                                        message1.setReceivedTime(conversation.getReceivedTime());
                                        contactsNotifications.add(message1);

                                        android.os.Message msg = new android.os.Message();
                                        msg.what = 4;
                                        msg.obj = auth;
                                        handler.sendMessage(msg);
                                    } else if (type != null && type.equals("memberChange")) {
                                        android.os.Message msg = new android.os.Message();
                                        msg.what = 7;
                                        msg.obj = auth;
                                        handler.sendMessage(msg);
                                    }
                                }
                            }
                        }
                    }
                    messageFragmentAdapter.notifyDataSetChanged();
                    waitNumShow();
                    sendUnMessCount();

                    int index = mSwipeMenuListView.indexOfChild(headView);
                    if (contactsNotifications.size() > 0) {
                        if (index == -1) {
                            mSwipeMenuListView.addHeaderView(headView);
                        }
                    } else {
                        mSwipeMenuListView.removeHeaderView(headView);
                    }
                    handler.sendEmptyMessage(8);
                } else {
                    handler.sendEmptyMessage(8);
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                handler.sendEmptyMessage(8);
            }
        });

        isTopNum2 = isTopNum;
    }

    //添加本地会话
    private void addConversation(final Conversation conversation) {
        MessageContent message = conversation.getLatestMessage();
        if (message instanceof TextMessage || message instanceof SpaceMessage) {
            String info = null;
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                info = textMessage.getContent();
            } else if (message instanceof SpaceMessage) {
                SpaceMessage spaceMessage = (SpaceMessage) message;
                info = spaceMessage.getContent();
            }
            int messageType = -1, contentType = -1;
            String requestName = null;
            try {
                if (info != null && !info.equals("")) {
                    JSONObject json = new JSONObject(info);
                    messageType = json.optInt("MessageType");
                    requestName = json.optString("requestName");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //普通消息
            if (messageType == 0 || messageType == 4 || messageType == 10 || messageType == 11) {
                messageList.add(conversation);
                //添加好友通知
            } else if (messageType == 1) {
                if (requestName != null) {
                    String id = conversation.getTargetId();
                    String uid = msharedPreferences.getString(Constant.ID_USER, "");
                    String name = msharedPreferences.getString(Constant.USER_NAME, "");
                    if (!id.equals(uid) && !noticeIdList.contains(id) && !name.equals(requestName)) {
                        mAddFriendName.setVisibility(View.VISIBLE);
                        noticeIdList.add(conversation.getTargetId());
                        Message message1 = new Message();
                        message1.setTargetId(conversation.getTargetId());
                        message1.setContent(message);
                        message1.setConversationType(conversation.getConversationType());
                        message1.setSentTime(conversation.getSentTime());
                        message1.setReceivedTime(conversation.getReceivedTime());
                        message1.setSenderUserId(conversation.getSenderUserId());
                        contactsNotifications.add(message1);
                        android.os.Message msg = new android.os.Message();
                        msg.what = 4;
                        msg.obj = requestName;
                        handler.sendMessage(msg);
                    }
                }
            } else if (messageType == 7) {
                String targetId = conversation.getTargetId();
                for (int i = 0; i < messageList.size(); i++) {
                    Conversation conversation1 = messageList.get(i);
                    if (conversation1.getTargetId().equals(targetId)) {
                        messageList.remove(i);
                        RongIMClient.getInstance().removeConversation(conversation.getConversationType(), targetId, new RongIMClient.ResultCallback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean aBoolean) {
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {
//                                Log.i("MessageFragment","从会话列表中移除某一会话失败"+errorCode.getValue());
                            }
                        });
                        RongIMClient.getInstance().clearMessages(conversation.getConversationType(), targetId, new RongIMClient.ResultCallback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean aBoolean) {
//                                Log.i("MessageFragment","清空指定类型，targetId 的某一会话所有聊天消息记录成功");
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {
//                                Log.i("MessageFragment","清空指定类型，targetId 的某一会话所有聊天消息记录失败");
                            }
                        });
                        break;
                    }
                }
            }
        } else if (message instanceof ImageMessage) {
            messageList.add(conversation);
        } else if (message instanceof CommandNotificationMessage) {
            RMessage rMessage = new RMessage(message);
            if (rMessage.getExtra() != null) {
                if (rMessage.getExtra().getCmd() == 3 || rMessage.getExtra().getCmd() == 6) {
                    String gid = rMessage.getExtra().getGroupid();
                    RongIMClient.getInstance().clearMessages(conversation.getConversationType(), gid, new RongIMClient.ResultCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean aBoolean) {

                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {

                        }
                    });
                }
            }
        }
//        messageFragmentAdapter.notifyDataSetChanged();
    }

    private void initView(View view) {
        mSwipeMenuListView = (SwipeMenuListView) view.findViewById(R.id.fragment_message_listView);
        headView = LayoutInflater.from(mContext).inflate(R.layout.list_head, null);
        mAddFriendName = (TextView) headView.findViewById(R.id.friend_request_message);
        msharedPreferences = mContext.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
//        mSwipeMenuListView.addHeaderView(headView);
        img_wait = (ImageView) headView.findViewById(R.id.backlog_list_icon);
        badgeView = new BadgeView(mContext);
        img_no_result = (ImageView) view.findViewById(R.id.img_no_result);
        img_no_result.setVisibility(View.GONE);
        view_no_friend = view.findViewById(R.id.view_no_friend);
        view_no_friend.setVisibility(View.INVISIBLE);
        img_add_friend = (ImageView) view.findViewById(R.id.img_add_no_friend);
        img_add_friend.setOnClickListener(this);
        contactsNotifications = new ArrayList<>();
        messageList = new ArrayList<>();
        targetList = new ArrayList<>();
        noticeIdList = new ArrayList<>();
        isTopList = new ArrayList<>();
        messageFragmentAdapter = new MessageFragmentAdapter(messageList, mContext);
        mSwipeMenuListView.setAdapter(messageFragmentAdapter);
        initListView();
        registerBoradcastReceiver();
        //刷新适配器，显示headview
        initOrRefresAdapter(messageList);
    }

    private void initListView() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity().getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(244, 53, 49)));
                deleteItem.setWidth(UIUtils.dip2px(60));
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
                        //左滑，删除消息
                        final Conversation con = messageList.get(position);
                        RongIMClient.getInstance().removeConversation(con.getConversationType(), con.getTargetId(), new RongIMClient.ResultCallback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean aBoolean) {
                                if (aBoolean) {
                                    messageList.remove(position);
                                    messageFragmentAdapter.notifyDataSetChanged();
                                    sendUnMessCount();
                                    if (messageList.size() == 0 && contactsNotifications.size() == 0) {
                                        img_no_result.setVisibility(View.VISIBLE);
                                        img_no_result.setImageResource(R.mipmap.ic_noresult_xiaoxi);
                                    }
                                    String id = con.getTargetId();
                                    if (targetList.contains(id)) {
                                        targetList.remove(id);
                                    }
                                }
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
        // 点击，进入详情界面
        mSwipeMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (System.currentTimeMillis() - last_click_time < 1200) {
                    return;
                }
                last_click_time = System.currentTimeMillis();
                if (!NetWorkUtils.isNetworkAvailable(mContext)) {
                    Toast.makeText(mContext, getString(R.string.alert_msg_check_net), Toast.LENGTH_SHORT).show();
                    return;
                }
                int cur_position = position - mSwipeMenuListView.getHeaderViewsCount();
                if (position < mSwipeMenuListView.getHeaderViewsCount()) {
                    Intent backlog = new Intent(getActivity(), BacklogActivity.class);
                    backlog.putExtra("contactsNotifications", contactsNotifications);
                    startActivityForResult(backlog, REQ_OPEN_WAIT);
                    badgeView.setVisibility(View.INVISIBLE);
                    wait_unread = 0;
                } else {
                    Conversation con = messageList.get(cur_position);
                    if (con == null) {
                        messageList.remove(cur_position);
                        return;
                    }
                    cur_click_id = con.getTargetId();

                    MessageContent latestMessage = con.getLatestMessage();
                    RMessage rMessage = new RMessage(latestMessage);
                    int chatType = rMessage.getChatType();
                    // 0 单聊，1 群聊，2 空间(群组)
                    if (chatType == 0) {
                        Intent startChat = new Intent(mContext, ChatActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("message", con);
                        startChat.putExtras(bundle);
                        startChat.putExtra("from", "MessageFragment");
                        startActivityForResult(startChat, REQ_OPEN);
                    } else if (chatType == 1) {
                        Intent startDiscussion = new Intent(mContext, DiscussionActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("message", con);
                        startDiscussion.putExtra("from", "MessageFragment");
                        startDiscussion.putExtras(bundle);
                        startActivityForResult(startDiscussion, REQ_OPEN);
                    } else if (chatType == 2) {
                        Intent startDiscussion = new Intent(mContext, GroupChatActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("message", con);
                        startDiscussion.putExtra("from", "MessageFragment");
                        startDiscussion.putExtras(bundle);
                        startActivityForResult(startDiscussion, REQ_OPEN);
                    }

                    if (con.getUnreadMessageCount() > 0) {
                        con.setUnreadMessageCount(0);
                        messageList.set(cur_position, con);
                        messageFragmentAdapter.notifyDataSetChanged();
                    }
                }
                sendUnMessCount();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        initMessages();
        MobclickAgent.onPageStart("MessageFragment");
    }

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(EmotionMainFragment.NEW_MESSAGE);
        // 注册广播
        getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String isTop = intent.getStringExtra("isTop");
            String updateName = intent.getStringExtra("UPDATE_NAME");
            String deleteMessages = intent.getStringExtra("deleteMessages");
            String new_message = intent.getStringExtra("new_message");
            if (new_message != null && new_message.equals("exit_group")) {   //退群
                String targetId = intent.getStringExtra("targetId");
                for (int i = 0; i < messageList.size(); i++) {
                    Conversation conversation = messageList.get(i);
                    if (conversation.getTargetId().equals(targetId)) {
                        messageList.remove(i);
                        messageFragmentAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            } else if (updateName != null && updateName.equals("UPDATE_NAME")) {
//                Log.i("MessageFragment", "从会话列表中移除某一会话成功111");
                if (targetList.contains(intent.getStringExtra("targetId"))) {
                    getConversation();
                }
            } else if (deleteMessages != null && deleteMessages.equals("deleteMessages")) {
                getConversation();
            } else if (isTop != null && isTop.equals("isTop")) {
                getConversation();
            } else if (new_message != null && new_message.equals("message")) {
                Message message = intent.getExtras().getParcelable("message");
                if (message != null) {
                    getMessage(message);
                    if (message.getMessageDirection() == Message.MessageDirection.SEND) {
                        for (int i = 0; i < messageList.size(); i++) {
                            Conversation conversation = messageList.get(i);
                            if (conversation.getTargetId().equals(message.getTargetId())) {
                                conversation.setUnreadMessageCount(0);
                                messageList.set(i, conversation);
                                break;
                            }
                        }
                        RongIMClient.getInstance().clearMessagesUnreadStatus(message.getConversationType(), message.getTargetId(), new RongIMClient.ResultCallback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean aBoolean) {

                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {

                            }
                        });
                    }
                }
            } else if (new_message != null && new_message.equals("delfri")) {
                String tid = intent.getStringExtra("tid");
                if (tid != null && !tid.equals("")) {
                    for (int i = 0; i < messageList.size(); i++) {
                        Conversation conversation = messageList.get(i);
                        if (conversation.getTargetId().equals(tid)) {
                            messageList.remove(i);
                            messageFragmentAdapter.notifyDataSetChanged();
                            RongIMClient.getInstance().removeConversation(conversation.getConversationType(), conversation.getTargetId());
                        }
                    }
                }
            }
        }
    };

    @Override
    public void getMessage(Message message) {
        targetId = message.getTargetId();
        //是否有响铃
        ishasring = msharedPreferences.getBoolean(Constant.HAS_RING, true);
        cur_ishasing = msharedPreferences.getBoolean(Constant.HAS_RING + message.getTargetId(), false);
        ishasring = !cur_ishasing && ishasring;

        if (isTopList.contains(message.getTargetId())) {
            isTopNum2 = 0;
        } else {
            isTopNum2 = isTopNum;
        }

        if (message != null && targetId != null && !targetId.equals("")) {
            int messageId = message.getMessageId();
            msharedPreferences.edit().putString("targetId", targetId).apply();

            if (message.getMessageDirection() == Message.MessageDirection.SEND) {
                RongIMClient.getInstance().clearMessagesUnreadStatus(message.getConversationType(), message.getTargetId(), new RongIMClient.ResultCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {

                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {

                    }
                });
            }

            if (message.getContent() instanceof TextMessage || message.getContent() instanceof SpaceMessage) {
                String info = null;
                if (message.getContent() instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message.getContent();
                    info = textMessage.getContent();
                } else if (message.getContent() instanceof SpaceMessage) {
                    SpaceMessage spaceMessage = (SpaceMessage) message.getContent();
                    info = spaceMessage.getContent();
                }
                int messageType = -1;
                String requestName = null;
                try {
                    if (info != null && !info.equals("")) {
                        JSONObject json = new JSONObject(info);
                        messageType = json.optInt("MessageType");
                        requestName = json.optString("requestName");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //普通消息
                if (messageType == 0 || messageType == 4 || messageType == 10 || messageType == 11) {
                    String id = message.getTargetId();
                    updateFriend(id);
                    boolean isexist = false;
                    int index = 0;
                    Conversation conversation = null;
                    for (int i = 0; i < messageList.size(); i++) {
                        conversation = messageList.get(i);
                        if (conversation.getTargetId().equals(id)) {
                            isexist = true;
                            index = i;
                            break;
                        }
                    }
                    if (isexist) {
                        int count = conversation.getUnreadMessageCount();
                        count++;
                        if (messageId == 0) {
                            conversation.setUnreadMessageCount(0);
                        } else if (message.getMessageDirection() == Message.MessageDirection.RECEIVE) {
                            if (message.getSentStatus() == Message.SentStatus.READ) {
                                conversation.setUnreadMessageCount(0);
                                RongIMClient.getInstance().clearMessagesUnreadStatus(conversation.getConversationType(), conversation.getTargetId());
                            } else {
                                conversation.setUnreadMessageCount(count);
                            }
                        }

                        conversation.setLatestMessageId(message.getMessageId());
                        conversation.setReceivedTime(System.currentTimeMillis());
                        conversation.setLatestMessage(message.getContent());
                        messageList.remove(index);
                        messageList.add(isTopNum2, conversation);
                    } else {
                        Conversation con = traMessage2Con(message);
                        if (messageId == 0) {
                            con.setUnreadMessageCount(0);
                        } else if (message.getMessageDirection() == Message.MessageDirection.RECEIVE) {
                            con.setUnreadMessageCount(1);
                        }
                        messageList.add(isTopNum2, con);
                        targetList.add(con.getTargetId());
                    }
                    handler.sendEmptyMessage(0);
                    if (message.getMessageDirection() == Message.MessageDirection.RECEIVE && System.currentTimeMillis() - last_ring_time > 1000) {
                        NoticeUtills.soundRing(mContext, ishasring, ishasring);
                        last_ring_time = System.currentTimeMillis();
                    }
                    //添加好友通知
                } else if (messageType == 1) {
                    if (requestName != null && !requestName.equals("") && !requestName.equals("null")) {
                        String id = message.getTargetId();
                        String uid = msharedPreferences.getString(Constant.ID_USER, "");
                        boolean isadd = false;
                        if (noticeIdList.contains(id)) {
                            isadd = true;
                            for (int i = 0; i < contactsNotifications.size(); i++) {
                                Message message1 = contactsNotifications.get(i);
                                if (message1.getTargetId().equals(id)) {
                                    contactsNotifications.remove(i);
                                    break;
                                }
                            }
                        } else {
                            noticeIdList.add(id);
                        }
                        contactsNotifications.add(0, message);
                        if (message.getMessageDirection() == Message.MessageDirection.RECEIVE) {
                            android.os.Message msg = new android.os.Message();
                            msg.what = 4;
                            msg.obj = requestName;
                            handler.sendMessage(msg);
                            if (message.getMessageDirection() == Message.MessageDirection.RECEIVE && System.currentTimeMillis() - last_ring_time > 1000 && !isadd) {
                                NoticeUtills.soundRing(mContext, ishasring, ishasring);
                                last_ring_time = System.currentTimeMillis();
                            }
                        }
                    }
                } else if (messageType == 7) {
                    String targetId = message.getTargetId();
                    for (int i = 0; i < messageList.size(); i++) {
                        Conversation conversation1 = messageList.get(i);
                        if (conversation1.getTargetId().equals(targetId)) {
                            messageList.remove(i);
                            RongIMClient.getInstance().removeConversation(message.getConversationType(), targetId, new RongIMClient.ResultCallback<Boolean>() {
                                @Override
                                public void onSuccess(Boolean aBoolean) {
                                }

                                @Override
                                public void onError(RongIMClient.ErrorCode errorCode) {

                                }
                            });
                            RongIMClient.getInstance().clearMessages(message.getConversationType(), targetId);
                            PopWindowUtils.delMessage(message);
                            break;
                        }
                    }
                    handler.sendEmptyMessage(0);
                }
            } else if (message.getContent() instanceof ImageMessage) {
                ImageMessage imageMessage = (ImageMessage) message.getContent();
                String id = message.getTargetId();
                if (id != null && !id.equals("")) {
                    boolean isexist = false;
                    int index = 0;
                    Conversation conversation = null;
                    for (int i = 0; i < messageList.size(); i++) {
                        conversation = messageList.get(i);
                        if (conversation.getTargetId().equals(message.getTargetId())) {
                            index = i;
                            isexist = true;
                            break;
                        }
                    }
                    if (isexist) {
                        int count = conversation.getUnreadMessageCount();
                        count++;
                        if (message.getSentStatus() == Message.SentStatus.READ
                                && message.getMessageDirection() == Message.MessageDirection.RECEIVE) {
                            conversation.setUnreadMessageCount(0);
                            RongIMClient.getInstance().clearMessagesUnreadStatus(conversation.getConversationType(), conversation.getTargetId());
                        } else {
                            conversation.setUnreadMessageCount(count);
                        }
                        conversation.setLatestMessageId(message.getMessageId());
                        conversation.setReceivedTime(System.currentTimeMillis());
                        conversation.setLatestMessage(message.getContent());
                        messageList.remove(index);
                        messageList.add(isTopNum2, conversation);
                    } else {
                        Conversation con = traMessage2Con(message);
                        messageList.add(isTopNum2, con);
                    }
                }
                handler.sendEmptyMessage(0);
                if (message.getMessageDirection() == Message.MessageDirection.RECEIVE && System.currentTimeMillis() - last_ring_time > 1000) {
                    NoticeUtills.soundRing(mContext, ishasring, ishasring);
                    last_ring_time = System.currentTimeMillis();
                }
            } else if (message.getContent() instanceof CommandNotificationMessage) {
                RMessage rMessage = new RMessage(message);
                if (rMessage.getExtra() != null) {
                    if (rMessage.getExtra().getCmd() == 3 || rMessage.getExtra().getCmd() == 6) {
                        String gid = rMessage.getExtra().getGroupid();
                        RongIMClient.getInstance().clearMessages(message.getConversationType(), gid);
                        for (int i = 0; i < messageList.size(); i++) {
                            Conversation conversation = messageList.get(i);
                            if (conversation.getTargetId().equals(gid)) {
                                messageList.remove(i);
                                break;
                            }
                        }
                    }
                }
            }
        } else if (targetId == null || targetId.equals("")) {
            if (message != null) {
                RMessage rMessage = new RMessage(message);
                Auth auth = rMessage.getAuth();
                if (auth != null) {
                    String subType = auth.getSubType();
                    if (subType != null && subType.equals("joinSpace")) {
                        String type = auth.getType();
                        if (type != null && type.equals("auth")) {
                            String id = auth.getObjectId();
                            if (noticeIdList.contains(id)) {
                                for (int i = 0; i < contactsNotifications.size(); i++) {
                                    Message message1 = contactsNotifications.get(i);
                                    RMessage rMessage1 = new RMessage(message1);
                                    if (rMessage1.getAuth() != null) {
                                        String id2 = rMessage1.getAuth().getObjectId();
                                        if (id2.equals(id)) {
                                            contactsNotifications.remove(i);
                                            break;
                                        }
                                    }
                                }
                            } else {
                                noticeIdList.add(id);
                            }
                            contactsNotifications.add(0, message);
                            android.os.Message msg = new android.os.Message();
                            msg.what = 4;
                            msg.obj = auth;
                            handler.sendMessage(msg);
                            if (System.currentTimeMillis() - last_ring_time > 1000) {
                                NoticeUtills.soundRing(mContext, ishasring, ishasring);
                                last_ring_time = System.currentTimeMillis();
                            }
                        } else if (type != null && type.equals("memberChange")) {
                            android.os.Message msg = new android.os.Message();
                            msg.what = 7;
                            msg.obj = auth;
                            handler.sendMessage(msg);
                        }
                    }
                }
            }
        }
    }

    //通知手机联系人页面刷新好友
    public void updateFriend(String targetId) {
        Intent intent = new Intent(PhoneContactActivity.UPDATE_FRIEND);
        intent.putExtra("targetId", targetId);
        getContext().sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBroadcastReceiver);
//        mContext.unregisterReceiver(receiverMessage);
    }

    public Conversation traMessage2Con(Message msg) {
        Conversation con = new Conversation();
        con.setTargetId(msg.getTargetId());
        con.setSentTime(msg.getSentTime());
        con.setReceivedTime(System.currentTimeMillis());
        con.setConversationType(msg.getConversationType());
        con.setLatestMessage(msg.getContent());
        con.setLatestMessageId(msg.getMessageId());
        con.setUnreadMessageCount(1);
        return con;
    }

    //待办  BadgeView  数字提醒
    private void waitNumShow() {
        if (contactsNotifications != null && contactsNotifications.size() > 0) {
            badgeView.setTargetView(img_wait);
            badgeView.setGravity(Gravity.TOP | Gravity.RIGHT);
            int len = contactsNotifications.size();
            for (int i = 0; i < contactsNotifications.size(); i++) {
                Message message = contactsNotifications.get(i);
                if (message.getSentStatus() == Message.SentStatus.READ || message.getSentStatus() == Message.SentStatus.RECEIVED) {
                    len--;
                }
            }
            badgeView.setBadgeCount(len);
            wait_unread = len;
        }
    }

    public void getData(boolean update) {
        if (update && NetWorkUtils.isNetworkAvailable(UIUtils.getContext())) {
            if (messageList == null || targetList == null || messageFragmentAdapter == null) {
                return;
            }
            targetList.clear();
            isInitCoversation = true;
            initMessages();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            if (requestCode == REQ_OPEN) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    boolean update = bundle.getBoolean("update");
                    if (update) {
                        getData(update);
                    }
                }
            } else if (requestCode == REQ_OPEN_WAIT) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    contactsNotifications.clear();
                    List<Message> list = (List<Message>) bundle.getSerializable("contactsNotifications");
                    contactsNotifications.addAll(list);
                    int count = bundle.getInt("count");
                    if (count < 1) {
                        contactsNotifications.clear();
                        handler.sendEmptyMessage(6);
                    }
                }
            }
        } else if (resultCode == 2) {
            if (requestCode == REQ_OPEN) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    String tid = bundle.getString("tid");
                    if (tid != null || !tid.equals("")) {
                        for (int i = 0; i < messageList.size(); i++) {
                            Conversation conversation = messageList.get(i);
                            if (conversation.getTargetId().equals(tid)) {
                                messageList.remove(i);
                                RongIMClient.getInstance().removeConversation(conversation.getConversationType(), conversation.getTargetId());
                                messageFragmentAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MessageFragment");
    }

    @Override
    public void sendData(Object object) {
        if (object instanceof Message) {
            Message message = (Message) object;
            if (message.getTargetId().equals(targetId)) {
                boolean ishave = false;
                Conversation con = traMessage2Con(message);
                con.setUnreadMessageCount(0);
                for (int i = 0; i < messageList.size(); i++) {
                    Conversation conversation = messageList.get(i);
                    if (conversation.getTargetId().equals(message.getTargetId())) {
                        ishave = true;
                        messageList.set(i, con);
                        break;
                    }
                }
                if (!ishave) {
                    messageList.add(0, con);
                }

                if (message.getMessageDirection() == Message.MessageDirection.SEND) {
                    RongIMClient.getInstance().clearMessagesUnreadStatus(message.getConversationType(), message.getTargetId());
                }

                messageFragmentAdapter.notifyDataSetChanged();
            }
        } else if (object instanceof String) {
            String json = (String) object;
            try {
                JSONObject jsonObject = new JSONObject(json);
                boolean del = jsonObject.optBoolean("del");
                if (del) {
                    String tid = jsonObject.optString("tid");
                    if (tid != null || !tid.equals("")) {
                        for (int i = 0; i < messageList.size(); i++) {
                            Conversation conversation = messageList.get(i);
                            if (conversation.getTargetId().equals(tid)) {
                                messageList.remove(i);
                                RongIMClient.getInstance().removeConversation(conversation.getConversationType(), conversation.getTargetId());
                                messageFragmentAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_add_no_friend:
                Intent intent = new Intent(mContext, AddFriendActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void addHeadView() {
        boolean isadd = true;
        int index = mSwipeMenuListView.indexOfChild(headView);
        if (contactsNotifications == null || contactsNotifications.size() < 1) {
            isadd = false;
        } else {
            int count = contactsNotifications.size();
            for (int i = 0; i < contactsNotifications.size(); i++) {
                Message message = contactsNotifications.get(i);
                if (message.getSentStatus() == Message.SentStatus.RECEIVED) {
                    count--;
                }
            }
            if (count == 0) {
                isadd = false;
                if (index > -1) {
                    mSwipeMenuListView.removeHeaderView(headView);
                    return;
                }
            }
        }
        if (isadd) {
            if (index == -1) {
                try {
                    int count = mSwipeMenuListView.getHeaderViewsCount();
                    if (count == 0) {
                        mSwipeMenuListView.addHeaderView(headView);
                    }
                } catch (Exception e) {

                }
            }
        } else {
            if (index > -1) {
                mSwipeMenuListView.removeHeaderView(headView);
            }
        }
    }
}

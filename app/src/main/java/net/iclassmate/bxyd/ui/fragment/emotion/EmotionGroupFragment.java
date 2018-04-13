package net.iclassmate.bxyd.ui.fragment.emotion;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.adapter.MessagesAdapter;
import net.iclassmate.bxyd.adapter.emotion.NoHorizontalScrollerVPAdapter;
import net.iclassmate.bxyd.bean.ImageModel;
import net.iclassmate.bxyd.bean.message.ImageProgress;
import net.iclassmate.bxyd.bean.message.RMessage;
import net.iclassmate.bxyd.bean.message.SpaceMessage;
import net.iclassmate.bxyd.bean.study.FileDirList;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.rongCloud.RongCloudContext;
import net.iclassmate.bxyd.ui.activitys.chat.ChatInformationActivity;
import net.iclassmate.bxyd.ui.activitys.chat.ReceiverMessage;
import net.iclassmate.bxyd.ui.activitys.information.FriendInformationActivity;
import net.iclassmate.bxyd.ui.activitys.study.AlbumActivity;
import net.iclassmate.bxyd.ui.activitys.study.CommentActivity;
import net.iclassmate.bxyd.ui.activitys.study.IsWifiActivity;
import net.iclassmate.bxyd.ui.activitys.study.NetFileActivity;
import net.iclassmate.bxyd.utils.DataCallback;
import net.iclassmate.bxyd.utils.FileUtils;
import net.iclassmate.bxyd.utils.HttpManager;
import net.iclassmate.bxyd.utils.MessageCallback;
import net.iclassmate.bxyd.utils.NetWorkUtils;
import net.iclassmate.bxyd.utils.OpenFile;
import net.iclassmate.bxyd.utils.PopWindowUtils;
import net.iclassmate.bxyd.utils.SendLastMessage;
import net.iclassmate.bxyd.utils.UIUtils;
import net.iclassmate.bxyd.utils.emotion.EmotionUtils;
import net.iclassmate.bxyd.utils.emotion.GlobalOnItemClickManagerUtils;
import net.iclassmate.bxyd.view.emotion.Keyboard;
import net.iclassmate.bxyd.view.emotion.NoHorizontalScrollerViewPager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ImageMessage;
import io.rong.message.TextMessage;

/**
 * Created by xyd on 2016/6/30.
 */
public class EmotionGroupFragment extends BaseFragment implements View.OnClickListener, MessageCallback, DataCallback {
    //是否绑定当前Bar的编辑框的flag
    public static final String BIND_TO_EDITTEXT = "bind_to_edittext";
    //是否隐藏bar上的编辑框和发生按钮
    public static final String HIDE_BAR_EDITTEXT_AND_BTN = "hide bar's editText and btn";
    public Keyboard mkeyboard;
    private EditText bar_edit_text;
    private Button bar_btn_send, bar_btn_more, bar_btn_voice;
    //更多界面中的Button
    private Button more_btn_camera, more_btn_pic, more_btn_file, more_btn_collect;
    //需要绑定的内容view
    private ListView contentView;
    //不可横向滚动的ViewPager
    private NoHorizontalScrollerViewPager viewPager;
    //是否绑定当前Bar的编辑框,默认true,即绑定。
    //false,则表示绑定contentView,此时外部提供的contentView必定也是EditText
    private boolean isBindToBarEditText = true;
    //是否隐藏bar上的编辑框和发生按钮,默认不隐藏
    private boolean isHidenBarEditTextAndBtn = false;
    List<Fragment> fragments = new ArrayList<>();
    private String targetId;
    private Conversation.ConversationType type;
    private SharedPreferences sp;
    public final static int RECEIVE_MESSAGE = 0;
    public final static int SEND_MESSAGE = 1;
    public final static int SEND_FAIL = 2;
    public static final String NEW_MESSAGE = "NEW_MESSAGE";
    public MessagesAdapter messagesAdapter;
    public List<Message> messageList;
    private String camera_photo_name;
    public static final int RESULT_CAMERA = 2;
    public static final int RESULT_PIC = 3;
    public static final int RESULT_NET_FILE = 4;
    private static final int NO_INTERNET = 5;   //没网
    public static final int REQ_WIFI = 6;
    public final static int RE_SEND = 7;
    private List<Object> listSelectAll;
    private String groupName;
    private long time;
    private Context mContext;
    private String timeDate;

    //发送最后一条消息的id
    private SendLastMessage sendLastMessage;

    //复制的内容
    private String copy_content;
    //显示当前图片的进行
    private List<ImageProgress> progressList;

    private ReceiverMessage receiverMessage;

    private int chatType = 2;
    private long last_time;

    //要打开视频的id和名字
    private String videoid, videoname;

    private boolean resend;
    private HttpManager httpManager;

    public void setSendLastMessage(SendLastMessage sendLastMessage) {
        this.sendLastMessage = sendLastMessage;
    }

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case RECEIVE_MESSAGE:
                case SEND_MESSAGE:
                    Object obj = msg.obj;
                    if (obj != null && obj instanceof Message) {
                        Message message = (Message) obj;
                        messageList.add(message);
                    }
                    initOrRefresAdapter(messageList);
                    break;
                case SEND_FAIL:
                    Message message = (Message) msg.obj;
                    sendMessageFail(message);
                    break;
                case NO_INTERNET:
                    Toast.makeText(UIUtils.getContext(), "您当前没有连接网络，请链接在重试！", Toast.LENGTH_SHORT).show();
                    break;
                case RE_SEND:
                    Toast.makeText(mContext, getString(R.string.resend_sucess), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }

            if (msg.what == RECEIVE_MESSAGE || msg.what == SEND_MESSAGE || msg.what == SEND_FAIL) {
                if (messagesAdapter != null && messagesAdapter.getCount() > 1) {
                    contentView.setSelection(messagesAdapter.getCount());
                    if (messageList != null) {
                        int len = messageList.size();
                        Message message = messageList.get(len - 1);
                        if (message.getMessageDirection() == Message.MessageDirection.RECEIVE) {
                            Intent intent = new Intent(NEW_MESSAGE);
                            intent.putExtra("new_message", "message");
                            Bundle bundle = new Bundle();
                            message.setSentStatus(Message.SentStatus.READ);
                            bundle.putParcelable("message", message);
                            intent.putExtras(bundle);
                            mContext.sendBroadcast(intent);
                        }
                        RongIMClient.ConnectionStatusListener.ConnectionStatus currentConnectionStatus = RongIMClient.getInstance().getCurrentConnectionStatus();
                        boolean state = false;
                        if (currentConnectionStatus == RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED ||
                                currentConnectionStatus == RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTING) {
                            state = true;
                        }
                        if (resend && NetWorkUtils.isNetworkAvailable(mContext)
                                && message.getMessageDirection() == Message.MessageDirection.SEND
                                && message.getSentStatus() != Message.SentStatus.FAILED && state
                                && message.getContent() instanceof TextMessage) {
                            if (msg.what == RECEIVE_MESSAGE || msg.what == SEND_MESSAGE) {
                                Toast.makeText(mContext, getString(R.string.resend_sucess), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
            if (messagesAdapter.getCount() > 1) {
                contentView.setSelection(messagesAdapter.getCount());
            }
            resend = false;
        }
    };

    private void initOrRefresAdapter(final List<Message> messageList) {
        if (messagesAdapter == null) {
            messagesAdapter = new MessagesAdapter(messageList, mContext);
            if (contentView == null) {
                return;
            }
            contentView.setAdapter(messagesAdapter);
            messagesAdapter.setIsShowName(false);
            if (messagesAdapter.getCount() > 1) {
                contentView.setSelection(messagesAdapter.getCount());
            }
            messagesAdapter.setOnHeadClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = (String) v.getTag();
                    final Intent queryInfo = new Intent(UIUtils.getContext(), FriendInformationActivity.class);
                    String uid = sp.getString(Constant.ID_USER, "");
                    String type = sp.getString(Constant.ID_USERTYPE, "1");
                    if (id.equals("-1") || id.equals(uid) || id.equals(uid + "1")) {
                        queryInfo.putExtra("from", "EmotionMainFragment");
                        queryInfo.putExtra("type", "person");
                        queryInfo.putExtra("name", sp.getString(Constant.USER_NAME, ""));
                        queryInfo.putExtra("code", sp.getString(Constant.USER_NAME, ""));
                        queryInfo.putExtra("friendId", uid);
                        if (type.equals("1")) {
                            queryInfo.putExtra("type", "person");
                        } else if (type.equals("0")) {
                            queryInfo.putExtra("type", "org");
                        }
                        startActivity(queryInfo);
                    } else {
                        id = id.substring(0, id.length() - 1);
                        queryInfo.putExtra("from", "EmotionMainFragment2");
                        queryInfo.putExtra("isFriend", true);
                        queryInfo.putExtra("type", "person");
                        queryInfo.putExtra("friendId", id);
                        final String finalId = id;
                        if (!NetWorkUtils.isNetworkAvailable(mContext)) {
                            Toast.makeText(mContext, getString(R.string.alert_msg_check_net), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String result = httpManager.findSpaceInfo2(false, finalId);
                                try {
                                    JSONObject jsonObject = new JSONObject(result);
                                    String type2 = "person";
                                    if (jsonObject.getString("type") != null && !TextUtils.isEmpty(jsonObject.getString("type"))) {
                                        type2 = jsonObject.getString("type");
                                    }
                                    final String finalType = type2;
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            queryInfo.putExtra("type", finalType);
                                            startActivity(queryInfo);
                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                }
            });

            messagesAdapter.setOnMesgClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OpenFile.openPic(v, mContext, messageList);
                }
            });

            messagesAdapter.setOnMesgLongClick(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Object tag = v.getTag();
                    int messageType = -1;
                    int contentType = -1;
                    if (tag instanceof Message) {
                        Message messageTag = (Message) tag;
                        int tid = messageTag.getMessageId();
                        Message msg = null;
                        long msg_time = 0;
                        for (int i = 0; i < messageList.size(); i++) {
                            Message message = messageList.get(i);
                            if (message.getMessageId() == tid && (message.getContent() instanceof TextMessage
                                    || message.getContent() instanceof SpaceMessage)) {
                                String info = null;
                                if (message.getContent() instanceof TextMessage) {
                                    TextMessage text = (TextMessage) message.getContent();
                                    info = text.getContent();
                                } else if (message.getContent() instanceof SpaceMessage) {
                                    SpaceMessage spaceMessage = (SpaceMessage) message.getContent();
                                    info = spaceMessage.getContent();
                                }
                                JSONObject json = null;
                                try {
                                    json = new JSONObject(info);
                                    copy_content = json.getString("Content");
                                    msg_time = json.optLong("CreateTime");
                                    messageType = json.optInt("MessageType");
                                    contentType = json.optInt("ContentType");
                                    if (msg_time < 1) {
                                        long t1 = message.getSentTime();
                                        long t2 = message.getReceivedTime();
                                        if (t1 > t2) {
                                            msg_time = t1;
                                        } else {
                                            msg_time = t2;
                                        }
                                    }
                                    msg = messageList.get(i);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                break;
                            } else if (message.getContent() instanceof ImageMessage) {
                                if (message.getMessageId() == tid) {
                                    if (message.getMessageId() == tid) {
                                        msg = message;
                                        long t = message.getReceivedTime();
                                        msg_time = message.getSentTime();
                                        if (t > msg_time) {
                                            msg_time = t;
                                        }
                                        break;
                                    }
                                }
                            }
                        }

                        if (msg != null) {
                            if (System.currentTimeMillis() - msg_time < 1000 * 59 * 2 && msg.getMessageDirection() == Message.MessageDirection.SEND) {
                                if (copy_content != null && copy_content.trim().length() > 0 && messageType == 0 && contentType == 1) {
                                    PopWindowUtils.showPopupWindowCopyRevoke(v, mContext, copy_content, Constant.MESSAGE_TYPE_WORD, msg);
                                } else {
                                    PopWindowUtils.showPopupWindowCopyRevoke(v, mContext, copy_content, Constant.MESSAGE_TYPE_FILE, msg);
                                }
                            } else {
                                if (copy_content != null && copy_content.trim().length() > 0 && messageType == 0 && contentType == 1) {
                                    PopWindowUtils.showPopupWindowCopy(v, msg, copy_content, Constant.MESSAGE_TYPE_WORD, mContext);
                                } else {
                                    PopWindowUtils.showPopupWindowCopy(v, msg, copy_content, Constant.MESSAGE_TYPE_FILE, mContext);
                                }
                            }
                        }
                    }
                    return true;
                }
            });

            messagesAdapter.setOnclickSendMessage(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message message = (Message) v.getTag();
                    sendMessage(message);
                }
            });
            //打开文件
            messagesAdapter.setOnclickOpenFile(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message message = (Message) v.getTag();
                    if (message.getContent() instanceof TextMessage || message.getContent() instanceof SpaceMessage) {
                        String info = null;
                        if (message.getContent() instanceof TextMessage) {
                            TextMessage textMessage = (TextMessage) message.getContent();
                            info = textMessage.getContent();
                        } else if (message.getContent() instanceof SpaceMessage) {
                            SpaceMessage spaceMessage = (SpaceMessage) message.getContent();
                            info = spaceMessage.getContent();
                        }
                        JSONObject json = null;
                        try {
                            json = new JSONObject(info);
                            String filename = json.optString("FileName");
                            String fileId = json.optString("FileID");
                            int contentType = json.optInt("ContentType");
                            String bulletinContent = json.optString("BulletinContent");
                            if (contentType == 8 || contentType == 9) {
                                Intent intent = new Intent(mContext, CommentActivity.class);
                                String bulletinID = "";
                                if (bulletinContent != null && !bulletinContent.equals("")) {
                                    JSONObject object = new JSONObject(bulletinContent);
                                    bulletinID = object.optString("bulletinId");
                                }
                                intent.putExtra("id", bulletinID);
                                startActivity(intent);
                            } else {
                                //是视频并且处于非wifi状态下
                                if (!NetWorkUtils.isNetworkAvailable(mContext)) {
                                    Toast.makeText(mContext, mContext.getResources().getString(R.string.alert_msg_check_net), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (OpenFile.isVideo(filename) && !FileUtils.isWifiActive(mContext)) {
                                    videoid = fileId;
                                    videoname = filename;
                                    Intent intent = new Intent(mContext, IsWifiActivity.class);
                                    startActivityForResult(intent, REQ_WIFI);
                                } else {
                                    OpenFile.openFile(fileId, filename, 1, mContext);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } else {
            messagesAdapter.notifyDataSetChanged();
            if (messagesAdapter.getCount() > 1) {
                contentView.setSelection(messagesAdapter.getCount());
            }
        }
        if (messageList != null && messageList.size() > 0) {
            int len = messageList.size();
            sendLastMessage.sendMessageId(messageList.get(len - 1).getMessageId());
        }
    }

    /**
     * 创建与Fragment对象关联的View视图时调用
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_emotion, container, false);
        mContext = getActivity();
        httpManager = new HttpManager();

        isHidenBarEditTextAndBtn = args.getBoolean(EmotionMainFragment.HIDE_BAR_EDITTEXT_AND_BTN);
        sp = mContext.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        messageList = new ArrayList<>();
        listSelectAll = new ArrayList<>();
        progressList = new ArrayList<>();

        initView(rootView);

        registerBoradcastReceiver();    //用于添加家人消息

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("message_copy");
        receiverMessage = new ReceiverMessage(this);
        mContext.registerReceiver(receiverMessage, intentFilter);

        IntentFilter intentFilter1 = new IntentFilter();
        intentFilter1.addAction("message_revoke");
        mContext.registerReceiver(receiverMessage, intentFilter1);

        Message message = new Message();
        targetId = args.getString("targetId");
//            type= (Conversation.ConversationType) args.getSerializable("type");
        type = Conversation.ConversationType.GROUP;
        message.setConversationType(type);
        message.setTargetId(targetId);

        //获取判断绑定对象的参数
        isBindToBarEditText = args.getBoolean(EmotionMainFragment.BIND_TO_EDITTEXT);

        mkeyboard = Keyboard.with(getActivity())
                .setView(rootView.findViewById(R.id.ll_emotion_layout), rootView.findViewById(R.id.ll_more_layout))
                .bindToContent(contentView)
                .bindToButton(rootView.findViewById(R.id.chat_emotion), rootView.findViewById(R.id.chat_more))
                .bindToEditText(((EditText) rootView.findViewById(R.id.chat_message)))
                .build();
        initListener();
        initDatas();
        //创建全局监听
        GlobalOnItemClickManagerUtils globalOnItemClickManager = GlobalOnItemClickManagerUtils.getInstance(mContext);

        //绑定当前Bar的编辑框
        globalOnItemClickManager.attachToEditText(bar_edit_text);


        return rootView;
    }

    /**
     * 绑定内容view
     *
     * @param contentView
     * @return
     */
    public void bindToContentView(ListView contentView) {
        this.contentView = contentView;
    }

    /**
     * 初始化view控件
     */
    protected void initView(View rootView) {
        viewPager = (NoHorizontalScrollerViewPager) rootView.findViewById(R.id.vp_emotionview_layout);
        bar_edit_text = (EditText) rootView.findViewById(R.id.chat_message);
        bar_btn_send = (Button) rootView.findViewById(R.id.send_message);
        bar_btn_more = (Button) rootView.findViewById(R.id.chat_more);
        bar_btn_voice = (Button) rootView.findViewById(R.id.chat_voice_message);
        more_btn_pic = (Button) rootView.findViewById(R.id.chat_pic);
        more_btn_file = (Button) rootView.findViewById(R.id.chat_file);
        more_btn_collect = (Button) rootView.findViewById(R.id.chat_collect);
        more_btn_camera = (Button) rootView.findViewById(R.id.chat_camera);

        bar_edit_text.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int type = sp.getInt(Constant.MESSAGE_TYPE, -1);
                if (type >= 0) {
                    PopWindowUtils.showPopupWindowPaste(v, mContext, bar_edit_text, targetId, Constant.CHAT_TYPE_GROUP);
                }
                return true;
            }
        });

        bar_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    bar_btn_more.setVisibility(View.GONE);
                    bar_btn_send.setVisibility(View.VISIBLE);
                } else {
                    bar_btn_more.setVisibility(View.VISIBLE);
                    bar_btn_send.setVisibility(View.GONE);
                }

                if (s.length() > 500) {
                    s = s.subSequence(0, 500);
                    bar_edit_text.setText(s);
                    bar_edit_text.setSelection(s.length());
                    if (System.currentTimeMillis() - last_time > 3000) {
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.out_max_num), Toast.LENGTH_SHORT).show();
                        last_time = System.currentTimeMillis();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * 初始化监听器
     */
    protected void initListener() {
        bar_btn_send.setOnClickListener(this);
        more_btn_camera.setOnClickListener(this);
        more_btn_collect.setOnClickListener(this);
        more_btn_file.setOnClickListener(this);
        more_btn_pic.setOnClickListener(this);
    }

    /**
     * 数据操作,这里是测试数据，请自行更换数据
     */
    protected void initDatas() {
        replaceFragment();
        List<ImageModel> list = new ArrayList<>();
        for (int i = 0; i < fragments.size(); i++) {
            if (i == 0) {
                ImageModel model1 = new ImageModel();
                model1.icon = getResources().getDrawable(R.drawable.ic_emotion);
                model1.flag = "经典笑脸";
                model1.isSelected = true;
                list.add(model1);
            } else {
                ImageModel model = new ImageModel();
                model.icon = getResources().getDrawable(R.drawable.ic_plus);
                model.flag = "其他笑脸" + i;
                model.isSelected = false;
                list.add(model);
            }
        }
        initMessage();
    }

    private void initMessage() {
        RongCloudContext.getInstance().init(mContext, this);
        RongCloudContext.getInstance().registerReceiveMessageListener();
        RongIMClient.getInstance().clearMessagesUnreadStatus(Conversation.ConversationType.GROUP, targetId);
//        Log.i("EmotionDisFragment", "type=" + type + ",targerId=" + targetId);
        /**
         * 获取指定类型，targetId 的最新消息记录。通常在进入会话后，调用此接口拉取该会话的最近聊天记录。
         * @param conversationType - 会话类型。
         * @param targetId - 目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
         * @param count - 要获取的消息数量。
         * @param callback - 获取最新消息记录的回调，按照时间顺序从新到旧排列。
         */
        RongIMClient.getInstance().getLatestMessages(Conversation.ConversationType.GROUP, targetId, 200, new RongIMClient.ResultCallback<List<Message>>() {
            @Override
            public void onSuccess(List<Message> messages) {
                if (messages != null) {
                    Collections.reverse(messages);
                    List<Message> list = new ArrayList<Message>();
                    for (int i = 0; i < messages.size(); i++) {
                        Message message = messages.get(i);
                        if (message.getContent() instanceof TextMessage) {
                            TextMessage textMessage = (TextMessage) message.getContent();
                            String info = textMessage.getContent();
                            try {
                                JSONObject json = new JSONObject(info);
                                int messageType = json.optInt("MessageType");
                                if (messageType == 0 || messageType == 10 || messageType == 11) {
                                    if (messageType == 10) {
                                        long fileID = json.optLong("FileID");
                                        for (int j = list.size() - 1; j >= 0; j--) {
                                            Message message1 = list.get(j);
                                            RMessage rMessage = new RMessage(message1);
                                            if (rMessage.getCreateTime() == fileID) {
                                                list.remove(j);
                                                PopWindowUtils.delMessage(message1);
                                                break;
                                            }
                                        }
                                    }
                                    list.add(message);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else if (message.getContent() instanceof ImageMessage) {
                            list.add(message);
                        }
                    }
                    messageList = list;
                    initOrRefresAdapter(messageList);
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                Log.i("TAG", "获取群聊聊天记录失败");
            }
        });
    }

    private void replaceFragment() {
        //创建fragment的工厂类
        FragmentFactory factory = FragmentFactory.getSingleFactoryInstance();
        //创建修改实例
        EmotiomComplateFragment f1 = (EmotiomComplateFragment) factory.getFragment(EmotionUtils.EMOTION_CLASSIC_TYPE);
        fragments.add(f1);

        NoHorizontalScrollerVPAdapter adapter = new NoHorizontalScrollerVPAdapter(getActivity().getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_message:
                //Toast.makeText(getActivity(), "发送", Toast.LENGTH_SHORT).show();
                String content = bar_edit_text.getText().toString().trim();
                sendMessage(content, null, false);
                break;
            case R.id.chat_camera:
                //Toast.makeText(getActivity(), "拍照", Toast.LENGTH_SHORT).show();
                takePhoto();
                break;
            case R.id.chat_pic:
                openPic();
                break;
            case R.id.chat_file:
                openNetFile();
                break;
            case R.id.chat_collect:
                break;
        }
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String path = Environment.getExternalStorageDirectory() + "/" + Constant.APP_DIR_NAME;
        File f = new File(path);
        if (!f.exists()) {
            f.mkdirs();
        }
        camera_photo_name = System.currentTimeMillis() + ".jpg";
        File file = new File(path, camera_photo_name);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, RESULT_CAMERA);
    }

    private void openPic() {
        listSelectAll.clear();
        Intent intent = new Intent(UIUtils.getContext(), AlbumActivity.class);
        intent.putExtra("pic", (Serializable) listSelectAll);
        intent.putExtra("send", true);
        intent.putExtra("chatType", chatType);
        intent.putExtra("tid", targetId);
        startActivityForResult(intent, RESULT_PIC);
    }

    private void openNetFile() {
        listSelectAll.clear();
        Intent intent = new Intent(UIUtils.getContext(), NetFileActivity.class);
        intent.putExtra("file", (Serializable) listSelectAll);
        startActivityForResult(intent, RESULT_NET_FILE);
    }

    public void setSessionId(String sessionId, String groupName) {
        targetId = sessionId;
        this.groupName = groupName;
    }

    public void setSessionIdTime(String sessionId, String groupName, String timeDate) {
        targetId = sessionId;
        this.groupName = groupName;
        this.timeDate = timeDate;
    }

    //展示聊天记录
    public void updateAdapter(List<Message> messageList) {
//        this.messageList.clear();
//        this.messageList.addAll(messageList);
        if (messageList != null) {
            initOrRefresAdapter(messageList);
        }
    }

    private void sendMessage(String content, Message msg, final boolean flag) {
        resend = flag;
        RMessage rMessage = null;
        if (flag && msg != null) {
            rMessage = new RMessage(msg);
        }
        JSONObject json = new JSONObject();
        try {
            if (flag) {
                json.put("ContentType", rMessage.getContentType());
                json.put("BulletinID", rMessage.getBulletinID());
                json.put("BulletinContent", rMessage.getBulletinContent());
                json.put("requestName", rMessage.getRequestName());
                json.put("requestRemark", rMessage.getRequestRemark());
                json.put("requestGroupId", rMessage.getRequestGroupId());
                json.put("FileID", rMessage.getFileID());
                json.put("FileName", rMessage.getFileName());
            } else {
                json.put("ContentType", 1);
                json.put("BulletinID", "");
                json.put("BulletinContent", "");
                json.put("requestName", "");
                json.put("requestRemark", "");
                json.put("requestGroupId", "");
                json.put("FileID", "");
                json.put("FileName", "");
            }
            json.put("MessageType", 0);
            json.put("ChatType", chatType);
            json.put("Content", content);
            json.put("FontSize", 11);
            json.put("FontStyle", 0);
            json.put("FontColor", 0);
            json.put("CreateTime", System.currentTimeMillis());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String messageBody = json.toString();
        //Log.d("EmotionMainFragment", "发送内容" + messageBody);
        if (messageBody.equals("")) {
            Toast.makeText(UIUtils.getContext(), "发送内容不能为空", Toast.LENGTH_SHORT).show();
        } else {
            String icon = sp.getString(Constant.USER_ICON, "");
            Uri uri = Uri.parse(icon);
            UserInfo userInfo = new UserInfo(sp.getString(Constant.ID_USER, ""), sp.getString(Constant.USER_NAME, ""), uri);
            final TextMessage textMessage = TextMessage.obtain(messageBody);
            textMessage.setUserInfo(userInfo);

            //添加消息
            final Message message = Message.obtain(targetId, type, textMessage);
            message.setMessageDirection(Message.MessageDirection.SEND);
            message.setSenderUserId(sp.getString(Constant.ID_USER, ""));
            bar_edit_text.setText("");
            RongIMClient.getInstance().sendMessage(type, targetId,
                    textMessage, null, null, new RongIMClient.SendMessageCallback() {
                        @Override
                        public void onSuccess(Integer integer) {

                        }

                        @Override
                        public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {
                            android.os.Message msg = new android.os.Message();
                            msg.obj = message;
                            msg.what = SEND_FAIL;
                            handler.sendMessage(msg);
                        }
                    }, new RongIMClient.ResultCallback<Message>() {
                        @Override
                        public void onSuccess(Message message) {
                            if (NetWorkUtils.isNetworkAvailable(mContext)) {
                                android.os.Message msg = new android.os.Message();
                                msg.what = SEND_MESSAGE;
                                msg.obj = message;
                                handler.sendMessage(msg);
                            } else {
                                android.os.Message msg = new android.os.Message();
                                msg.obj = message;
                                msg.what = SEND_FAIL;
                                handler.sendMessage(msg);
                            }
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {
                            android.os.Message msg = new android.os.Message();
                            msg.obj = message;
                            msg.what = SEND_FAIL;
                            handler.sendMessage(msg);
                        }
                    });
        }

    }

    @Override
    public void getMessage(final Message message) {
        sendLastMessage.sendMessageId(message.getMessageId());
        if (message.getContent() instanceof TextMessage || message.getContent() instanceof ImageMessage) {
            int messageType = 0;
            if (message.getTargetId().equals(targetId)) {
                if (message.getContent() instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message.getContent();
                    String info = textMessage.getContent();
                    try {
                        JSONObject json = new JSONObject(info);
                        messageType = json.optInt("MessageType");
                        if (messageType == 10) {
                            long fileID = json.optLong("FileID");
                            for (int i = messageList.size() - 1; i >= 0; i--) {
                                Message message1 = messageList.get(i);
                                RMessage rMessage = new RMessage(message1);
                                if (rMessage.getCreateTime() == fileID) {
                                    messageList.remove(i);
                                    PopWindowUtils.delMessage(message1);
                                    break;
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (messageType == 0 || messageType == 10 || messageType == 11) {
                    android.os.Message msg = new android.os.Message();
                    msg.what = RECEIVE_MESSAGE;
                    msg.obj = message;
                    handler.sendMessage(msg);
                }
            } else {
                //发送广播
                Intent intent = new Intent(NEW_MESSAGE);
                intent.putExtra("new_message", "message");
                Bundle bundle = new Bundle();
                bundle.putParcelable("message", message);
                intent.putExtras(bundle);
                mContext.sendBroadcast(intent);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            //照相
            if (requestCode == RESULT_CAMERA) {
                String sdStatus = Environment.getExternalStorageState();
                if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
                    Log.i("info", "SD card is not avaiable writeable right now.");
                    return;
                }
                FileOutputStream b = null;
                String path = Environment.getExternalStorageDirectory() + "/" + Constant.APP_DIR_NAME;
                File file = new File(path);
                if (!file.exists()) {
                    file.mkdirs();
                }

                String filename = path + "/" + camera_photo_name;
                try {
                    Bitmap bitmap = compressImageFromFile(filename);
                    b = new FileOutputStream(filename);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 60, b);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    Toast.makeText(mContext, "获取照片失败,请重新拍照", Toast.LENGTH_SHORT).show();
                    return;
                } catch (OutOfMemoryError error) {
                    Toast.makeText(mContext, "获取照片失败,请重新拍照", Toast.LENGTH_SHORT).show();
                    return;
                }
                mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(filename))));
                listSelectAll.clear();
                listSelectAll.add(filename);
                sendImageMessage(listSelectAll, false);
            }//手机相册
            else if (requestCode == RESULT_PIC) {
                Bundle bundle = data.getExtras();
                int type = data.getIntExtra("type", -1);
                listSelectAll = (List<Object>) bundle.getSerializable("list");
                sendImageMessage(listSelectAll, false);
            } else if (requestCode == RESULT_NET_FILE) {
                Bundle bundle = data.getExtras();
                int type = data.getIntExtra("type", -1);
                listSelectAll = (List<Object>) bundle.getSerializable("list");
                sendNetFile(listSelectAll);
            } else if (requestCode == REQ_WIFI) {
                OpenFile.openFile(videoid, videoname, 1, mContext);
            }
        }
    }

    private Bitmap compressImageFromFile(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, null);

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        float hh = width;
        float ww = height;
        int be = 1;
        if (w > h && w > ww) {
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;

        newOpts.inPurgeable = true;
        newOpts.inInputShareable = true;

        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return bitmap;

    }

    //发送图片
    private void sendImageMessage(List<Object> listSelectAll, final boolean flag) {
        resend = flag;
        for (int i = 0; i < listSelectAll.size(); i++) {
            final ImageMessage imageMessage = ImageMessage.obtain(Uri.parse("file://" + listSelectAll.get(i)), Uri.parse("file://" + listSelectAll.get(i)));

            JSONObject json = new JSONObject();
            try {
                json.put("type", 1);
                json.put("fileid", "");
                json.put("name", listSelectAll.get(i).toString());
                long size = 0;
                try {
                    File file = new File(listSelectAll.get(i).toString());
                    size = file.getTotalSpace();
                } catch (Exception e) {

                }
                json.put("size", size);
                json.put("CRC", "");
                json.put("memo", "");
                long t = System.currentTimeMillis() + i;
                json.put("createTime", t);
                json.put("chatType", chatType);
                json.put("uri", imageMessage.getRemoteUri());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final String messageBody = json.toString();
            String icon = sp.getString(Constant.USER_ICON, "");
            Uri uri = Uri.parse(icon);
            UserInfo userInfo = new UserInfo(sp.getString(Constant.ID_USER, ""), sp.getString(Constant.USER_NAME, ""), uri);
            imageMessage.setUserInfo(userInfo);
            imageMessage.setExtra(messageBody);
            RongIMClient.getInstance().sendImageMessage(type, targetId, imageMessage, null, null, new RongIMClient.SendImageMessageCallback() {
                @Override
                public void onAttached(Message message) {
                    if (NetWorkUtils.isNetworkAvailable(mContext)) {
                        android.os.Message msg = new android.os.Message();
                        msg.what = SEND_MESSAGE;
                        msg.obj = message;
                        handler.sendMessage(msg);
                    } else {
                        android.os.Message message1 = new android.os.Message();
                        message1.what = SEND_FAIL;
                        message1.obj = message;
                        handler.sendMessage(message1);
                    }
                }

                @Override
                public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                    android.os.Message message1 = new android.os.Message();
                    message1.what = SEND_FAIL;
                    message1.obj = message;
                    handler.sendMessage(message1);
                }

                @Override
                public void onSuccess(Message message) {
                    if (flag) {
                        handler.sendEmptyMessage(RE_SEND);
                    }
                }

                @Override
                public void onProgress(Message message, int i) {
                    sendPicProgress(message, i);
                }
            });
        }
    }

    //发送网盘文件
    private void sendNetFile(List<Object> listSelectAll) {
        for (int i = 0; i < listSelectAll.size(); i++) {
            Object o = listSelectAll.get(i);
            if (o instanceof FileDirList) {
                FileDirList file = (FileDirList) o;
                JSONObject json = new JSONObject();
                try {
                    json.put("MessageType", 0);
                    json.put("ContentType", 11);
                    json.put("ChatType", chatType);
                    json.put("Content", "[文件]");
                    json.put("FontSize", 14);
                    json.put("FontStyle", 0);
                    json.put("FontColor", 0);
                    json.put("BulletinID", "");
                    json.put("BulletinContent", "");
                    json.put("requestName", "");
                    json.put("requestRemark", "");
                    json.put("requestGroupId", "");
                    json.put("FileID", file.getId());
                    json.put("FileName", file.getShortName());
                    json.put("FileSize", file.getSize());
                    json.put("CreateTime", System.currentTimeMillis());
                    final String messageBody = json.toString();
                    if (messageBody.equals("")) {
                        Toast.makeText(UIUtils.getContext(), "发送内容不能为空", Toast.LENGTH_SHORT).show();
                    } else {
                        String icon = sp.getString(Constant.USER_ICON, "");
                        Uri uri = Uri.parse(icon);
                        UserInfo userInfo = new UserInfo(sp.getString(Constant.ID_USER, ""), sp.getString(Constant.USER_NAME, ""), uri);
                        final TextMessage textMessage = TextMessage.obtain(messageBody);
                        textMessage.setUserInfo(userInfo);

                        final Message message = Message.obtain(targetId, type, textMessage);
                        message.setMessageDirection(Message.MessageDirection.SEND);
                        message.setSenderUserId(sp.getString(Constant.ID_USER, ""));
                        RongIMClient.getInstance().sendMessage(type, targetId,
                                textMessage, null, null, new RongIMClient.SendMessageCallback() {
                                    @Override
                                    public void onSuccess(Integer integer) {
                                        //发送广播
                                        Intent intent = new Intent(NEW_MESSAGE);
                                        intent.putExtra("new_message", "message");
                                        Bundle bundle = new Bundle();
                                        bundle.putParcelable("message", message);
                                        intent.putExtras(bundle);
                                        mContext.sendBroadcast(intent);
                                        handler.sendEmptyMessage(SEND_MESSAGE);
                                        bar_edit_text.setText("");
                                    }

                                    @Override
                                    public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {
                                        android.os.Message message1 = new android.os.Message();
                                        message1.what = SEND_FAIL;
                                        message1.obj = message;
                                        handler.sendMessage(message1);
                                    }
                                }, new RongIMClient.ResultCallback<Message>() {
                                    @Override
                                    public void onError(RongIMClient.ErrorCode errorCode) {
                                        android.os.Message message1 = new android.os.Message();
                                        message1.what = SEND_FAIL;
                                        message1.obj = message;
                                        handler.sendMessage(message1);
                                    }

                                    @Override
                                    public void onSuccess(Message message) {
                                        android.os.Message msg = new android.os.Message();
                                        msg.what = SEND_MESSAGE;
                                        msg.obj = message;
                                        handler.sendMessage(msg);
                                    }

                                });
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //重新发送文本
    private void sendMessage(Message message) {
        RMessage rMessage = new RMessage(message);
        int[] ids = new int[]{message.getMessageId()};
        RongIMClient.getInstance().deleteMessages(ids);
        messageList.remove(message);
        if (message.getContent() instanceof TextMessage || message.getContent() instanceof SpaceMessage) {
            if (message.getContent() instanceof TextMessage) {
                String content = rMessage.getContent();
                sendMessage(content, message, true);
            } else if (message.getContent() instanceof SpaceMessage) {
                String content = rMessage.getContent();
                sendMessage(content, message, true);
            }
        } else if (message.getContent() instanceof ImageMessage) {
            String name = rMessage.getName();
            if (name != null) {
                List<Object> list = new ArrayList<>();
                list.add(name);
                sendImageMessage(list, true);
            }
        }
    }

    public void sendRongIMClient(final Message message, final MessageContent msgContent) {
        RongIMClient.getInstance().sendMessage(message.getConversationType(), message.getTargetId(),
                msgContent, null, null, new RongIMClient.SendMessageCallback() {
                    @Override
                    public void onSuccess(Integer integer) {
                        Message message = Message.obtain(targetId, type, msgContent);
                        message.setMessageDirection(Message.MessageDirection.SEND);
                        message.setSenderUserId(sp.getString(Constant.ID_USER, ""));

                        //发送广播
                        Intent intent = new Intent(NEW_MESSAGE);
                        intent.putExtra("new_message", "message");
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("message", message);
                        intent.putExtras(bundle);
                        mContext.sendBroadcast(intent);
                    }

                    @Override
                    public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {
                        android.os.Message message1 = new android.os.Message();
                        message1.what = SEND_FAIL;
                        message1.obj = message;
                        handler.sendMessage(message1);
                    }
                }, null);
    }

    //发送消息失败
    private void sendMessageFail(Message message) {
        RongIMClient.getInstance().setMessageSentStatus(message.getMessageId(), Message.SentStatus.FAILED);
        RMessage rMessage = new RMessage(message);

        boolean ishave = false;
        for (int i = messageList.size() - 1; i >= 0; i--) {
            Message message1 = messageList.get(i);
            RMessage rMessage1 = new RMessage(message1);
            if (rMessage.getCreateTime() == rMessage1.getCreateTime()) {
                message.setSentStatus(Message.SentStatus.FAILED);
                messageList.set(i, message);
                ishave = true;
                break;
            }
        }

        if (!ishave) {
            message.setSentStatus(Message.SentStatus.FAILED);
            messageList.add(message);
        }
        handler.sendEmptyMessage(SEND_MESSAGE);
    }

    //发送图片进度显示
    private void sendPicProgress(Message message, int i) {
        int id = message.getMessageId();
        boolean isHave = false;
        if (progressList != null) {
            for (int j = 0; j < progressList.size(); j++) {
                ImageProgress imageProgress = progressList.get(j);
                if (imageProgress.getMsgId() == id) {
                    imageProgress.setProgress(i);
                    progressList.set(j, imageProgress);
                    isHave = true;
                    break;
                }
            }
            if (!isHave) {
                ImageProgress imageProgress = new ImageProgress();
                imageProgress.setMsgId(id);
                imageProgress.setProgress(i);
                progressList.add(imageProgress);
            }
            messagesAdapter.setProgressList(progressList);
            messagesAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("EmotionDisFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("EmotionDisFragment");
    }

    @Override
    public void sendData(Object object) {
        if (object instanceof Message) {
            Message message = (Message) object;
            if (message.getContent() instanceof TextMessage && message.getMessageDirection() == Message.MessageDirection.SEND
                    && message.getConversationType() == Conversation.ConversationType.GROUP) {
                TextMessage textMessage = (TextMessage) message.getContent();
                String info = textMessage.getContent();
                try {
                    JSONObject json = new JSONObject(info);
                    int messageType = json.optInt("MessageType");
                    if (messageType == 10) {
                        long fileID = json.optLong("FileID");
                        if (fileID >= 0) {
                            for (int i = messageList.size() - 1; i >= 0; i--) {
                                Message message1 = messageList.get(i);
                                RMessage rMessage = new RMessage(message1);
                                if (rMessage.getCreateTime() == fileID) {
                                    messageList.remove(i);
                                    PopWindowUtils.delMessage(message1);
                                    android.os.Message msg = new android.os.Message();
                                    msg.what = RECEIVE_MESSAGE;
                                    msg.obj = message;
                                    handler.sendMessage(msg);
                                    break;
                                }
                            }
                        }
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (message.getMessageDirection() == Message.MessageDirection.SEND && message.getTargetId().equals(targetId)
                    && message.getConversationType() == Conversation.ConversationType.GROUP) {
                messageList.add(message);
                initOrRefresAdapter(messageList);
                if (messagesAdapter.getCount() > 1) {
                    contentView.setSelection(messagesAdapter.getCount());
                }
            }
        }
    }

    private void sendRecorver2MessageFragment(Message message) {
        Intent intent = new Intent(NEW_MESSAGE);
        intent.putExtra("new_message", "message");
        Bundle bundle = new Bundle();
        message.setSentStatus(Message.SentStatus.READ);
        bundle.putParcelable("message", message);
        intent.putExtras(bundle);
        mContext.sendBroadcast(intent);
    }

    //注册广播
    public void registerBoradcastReceiver() {
        IntentFilter intentFilter = new IntentFilter(ChatInformationActivity.UPDATE_MESSAGE);
        mContext.registerReceiver(broadcastReceiver, intentFilter);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getParcelableExtra("message") != null) {
                Message message = intent.getParcelableExtra("message");
                if (null != messageList) {
                    messageList.add(message);
                    initOrRefresAdapter(messageList);
                }
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext.unregisterReceiver(receiverMessage);
        mContext.unregisterReceiver(broadcastReceiver);
    }
}
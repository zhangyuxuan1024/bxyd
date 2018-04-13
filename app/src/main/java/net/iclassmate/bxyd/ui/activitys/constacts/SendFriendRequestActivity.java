package net.iclassmate.bxyd.ui.activitys.constacts;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.ui.fragment.emotion.EmotionDisFragment;
import net.iclassmate.bxyd.utils.DataCallback;
import net.iclassmate.bxyd.utils.HttpManager;
import net.iclassmate.bxyd.utils.NetWorkUtils;
import net.iclassmate.bxyd.utils.UIUtils;
import net.iclassmate.bxyd.view.TitleBar;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ImageMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;

public class SendFriendRequestActivity extends FragmentActivity implements View.OnClickListener, TitleBar.TitleOnClickListener, DataCallback {
    private Context mcontext;
    private String userId, oppositeId, name, from, friendId, type2;
    private SharedPreferences sp;
    private EditText sendRequest_et;
    private Button delete;
    private TextView tv;
    private TitleBar titleBar;
    private Conversation.ConversationType type = Conversation.ConversationType.PRIVATE;
    private int sessionType;    //1单聊   2群聊   3群组   4机构

    private static final int NO_INTERNET = 0;             //没网
    private static final int UPDATE_NAME_SUCCEED = 1;     //修改备注名/群组名成功
    private static final int UPDATE_NAME_FAIL = 2;       //修改备注名失败
    public static final String action = "net.iclassmate.bxyd.ui.activitys.constacts";
    private Context mContext;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case NO_INTERNET:
                    Toast.makeText(UIUtils.getContext(), mContext.getResources().getString(R.string.alert_msg_check_net), Toast.LENGTH_SHORT).show();
                    break;

                case UPDATE_NAME_SUCCEED:
                    Intent sendIntent = new Intent(EmotionDisFragment.NEW_MESSAGE);
                    sendIntent.putExtra("UPDATE_NAME", "UPDATE_NAME");
                    sendIntent.putExtra("targetId", friendId);
                    sendIntent.putExtra("name", sendRequest_et.getText().toString());
//                    sendIntent.putExtras(sendIntent);
                    SendFriendRequestActivity.this.sendBroadcast(sendIntent);
                    //刷新GroupListActivity中展示的数据
                    castGroupList();

                    //把修改号的名称传给FriendInformationActivity
                    Toast.makeText(UIUtils.getContext(), "修改成功", Toast.LENGTH_SHORT).show();
                    Intent intent = getIntent();
                    intent.putExtra("remarksName", sendRequest_et.getText().toString());
                    intent.putExtra("targetId", friendId);
                    SendFriendRequestActivity.this.setResult(11, intent);
                    SendFriendRequestActivity.this.finish();
                    break;

                case UPDATE_NAME_FAIL:
                    Toast.makeText(UIUtils.getContext(), "修改失败，请重试！", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_send_friend_request);
        mcontext = this;
        mContext = this;

        initView();
        initData();
        initLinstener();
    }


    private void initView() {
        sp = this.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        titleBar = (TitleBar) findViewById(R.id.send_request_title_bar);
        titleBar.setLeftIcon(R.mipmap.ic_fanhui, "返回");
        titleBar.setRightIcon("确定");

        sendRequest_et = (EditText) findViewById(R.id.send_request_et);
        delete = (Button) findViewById(R.id.send_request_delete);
        tv = (TextView) super.findViewById(R.id.tv);
        String userName = sp.getString(Constant.USER_NAME, "");
        if (userName != null && !userName.equals("")) {
            sendRequest_et.setText("我是" + userName);
            delete.setVisibility(View.VISIBLE);
        }

        from = getIntent().getStringExtra("from");
        if (from != null && from.equals("FriendInformationActivity")) {
            sessionType = getIntent().getIntExtra("sessionType", 2);
            type2 = getIntent().getStringExtra("type");
            friendId = getIntent().getStringExtra("friendId");
            if (type2.equals("person")) {
                tv.setText("备注名");
                sendRequest_et.setText(getIntent().getStringExtra("friendName"));
            } else {
                tv.setText("群组名称");
                sendRequest_et.setText(getIntent().getStringExtra("sessionName"));
            }
        }
    }

    private void initData() {
        userId = sp.getString(Constant.ID_USER, "");
        name = sp.getString("name", "");
        oppositeId = getIntent().getStringExtra("oppositeId");
    }

    private void initLinstener() {
        titleBar.setTitleClickListener(this);
        delete.setOnClickListener(this);

        sendRequest_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    delete.setVisibility(View.VISIBLE);
                } else {
                    delete.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_request_delete:
                sendRequest_et.setText("");
                break;
        }
    }

    @Override
    public void leftClick() {
        finish();
    }

    @Override
    public void rightClick() {
        if (from != null && from.equals("FriendInformationActivity"))    //修改备注名
        {
            if (sendRequest_et.getText().toString().length() <= 0 || sendRequest_et.getText().toString().equals(getIntent().getStringExtra("friendName"))) {
//                String str = null;
//                if (type2.equals("person")) {
//                    str = "备注名不能为空，请重新输入！";
//                } else {
//                    str = "群组名称不能为空，请重新输入！";
//                }
//                Toast.makeText(UIUtils.getContext(), str, Toast.LENGTH_SHORT).show();
                SendFriendRequestActivity.this.finish();
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (NetWorkUtils.isNetworkAvailable(UIUtils.getContext())) {
                            int result = 404;
                            HttpManager httpManager = new HttpManager();
                            if (type2.equals("person")) {
                                result = httpManager.remarksName(friendId, sendRequest_et.getText().toString(), userId);
                            } else {
                                if (sessionType == 2) {
                                    result = httpManager.updateGroupName(friendId, sendRequest_et.getText().toString(), 1);
                                } else if (sessionType == 3 || sessionType == 4) {
                                    result = httpManager.updateSpaceName(friendId, sendRequest_et.getText().toString());
                                }
                            }
                            if (result == 200) {
                                handler.sendEmptyMessage(UPDATE_NAME_SUCCEED);
                            } else {
                                handler.sendEmptyMessage(UPDATE_NAME_FAIL);
                            }
                        } else {
                            handler.sendEmptyMessage(NO_INTERNET);
                        }
                    }
                }).start();
            }
        } else {     //发送好友请求
            String sendMessage = sendRequest_et.getText().toString().trim();
            sendMessage(sendMessage);
        }
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

    }

    //请求添加好友
    private void sendMessage(final String messageBody) {
        JSONObject json = new JSONObject();
        try {
            json.put("MessageType", 1);
            json.put("ContentType", 1);
            json.put("ChatType", 0);
            json.put("Content", messageBody);
            json.put("FontSize", 14);
            json.put("FontStyle", 0);
            json.put("FontColor", 0);
            json.put("BulletinID", "");
            json.put("BulletinContent", "");
            json.put("requestName", name);
            json.put("requestRemark", "");
            json.put("requestGroupId", "");
            json.put("FileID", "");
            json.put("FileName", "");
            json.put("CreateTime", System.currentTimeMillis());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String message = json.toString();
//        Log.i("info", "请求添加好友=" + message);
        UserInfo userInfo = new UserInfo(userId, name, null);
        final TextMessage textMessage = TextMessage.obtain(message);
        textMessage.setUserInfo(userInfo);

        RongIMClient.getInstance().sendMessage(type, oppositeId,
                textMessage, null, null, new RongIMClient.SendMessageCallback() {
                    @Override
                    public void onSuccess(Integer integer) {

//                            Message message = Message.obtain(oppositeId, Conversation.ConversationType.CUSTOMER_SERVICE, TextMessage.obtain(messageBody));
//                            message.setSenderUserId(userId);
                        Intent intent = getIntent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                    @Override
                    public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {
//                        Log.d("SendFriendRequest", "发送失败");
                        if (!NetWorkUtils.isNetworkAvailable(mContext)) {
                            Toast.makeText(mContext, mContext.getResources().getString(R.string.alert_msg_check_net), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UIUtils.getContext(), "添加好友失败，请稍候再试", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new RongIMClient.ResultCallback<Message>() {
                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {

                    }

                    @Override
                    public void onSuccess(Message message) {
                        MessageContent messageContent = message.getContent();
                        if (messageContent instanceof TextMessage) {//文本消息
                            Log.e("SendFriendRequest", "onReceived-TextMessage:" + ((TextMessage) messageContent).getContent());
                        } else if (messageContent instanceof ImageMessage) {//图片消息
                        } else if (messageContent instanceof VoiceMessage) {//语音消息
                        }
                    }
                });
//        }
    }

    /**
     * 刷新GroupListActivity中展示的数据
     */
    public void castGroupList() {
        Intent intent = new Intent(action);
        intent.putExtra("updateName", sendRequest_et.getText().toString());
        sendBroadcast(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SendFriendRequestActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SendFriendRequestActivity");
        MobclickAgent.onPause(this);
    }
}
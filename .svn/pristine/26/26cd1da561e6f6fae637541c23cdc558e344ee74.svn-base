package net.iclassmate.bxyd.ui.activitys.study;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.adapter.study.TraFriAdapter;
import net.iclassmate.bxyd.bean.study.Resources;
import net.iclassmate.bxyd.bean.study.StudyMessageItem;
import net.iclassmate.bxyd.bean.study.group.Group;
import net.iclassmate.bxyd.bean.study.group.GroupItem;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.utils.NetWorkUtils;
import net.iclassmate.bxyd.utils.UIUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;
import io.rong.message.TextMessage;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SelectGroupActivity extends FragmentActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private TextView tv_back;
    private ListView listView;
    private String userid;
    private ImageView img_anim;
    private AnimationDrawable anim;
    private SharedPreferences sharedPreferences;
    private List<Object> list;
    private TraFriAdapter adapter;

    private Context mContext;
    private StudyMessageItem messageItem;
    public static final String NEW_MESSAGE = "NEW_MESSAGE";
    private long last_click_time;
    private int chatType;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            anim.stop();
            img_anim.setVisibility(View.GONE);
            img_anim.setBackgroundColor(Color.parseColor("#efefef"));
            switch (what) {
                case 1:
                    int code = msg.arg1;
                    String ret = (String) msg.obj;
                    if (code == 200) {
                        Group group = new Group();
                        try {
                            JSONObject json = new JSONObject(ret);
                            group.parserJson(json);
                            List<GroupItem> list1 = group.getList();
                            if (list1.size() == 0) {
                                img_anim.setVisibility(View.VISIBLE);
                                img_anim.setImageResource(R.mipmap.ic_no_result);
                            } else {
                                list.addAll(list1);
                                adapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        img_anim.setVisibility(View.VISIBLE);
                        img_anim.setImageResource(R.mipmap.img_jiazaishibai);
                    }
                    break;
                case 404:
                    img_anim.setVisibility(View.VISIBLE);
                    img_anim.setImageResource(R.mipmap.img_jiazaishibai);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_group);
        sharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCES, MODE_PRIVATE);
        userid = sharedPreferences.getString(Constant.ID_USER, "");

        init();
        loadData();
    }

    private void init() {
        mContext = this;
        Intent intent = getIntent();
        messageItem = (StudyMessageItem) intent.getSerializableExtra("msg");

        tv_back = (TextView) findViewById(R.id.tra_group_back);
        tv_back.setOnClickListener(this);
        img_anim = (ImageView) findViewById(R.id.img_anim);
        anim = (AnimationDrawable) img_anim.getBackground();
        anim.start();
        listView = (ListView) findViewById(R.id.tra_group_listview);
        list = new ArrayList<>();
        adapter = new TraFriAdapter(this, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    private void loadData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String url = String.format(Constant.MESSAGE_GET_CHAT_GROUP, userid);
                Request request = new Request.Builder().url(url).build();
//                Log.i("info", "群组列表=" + url);
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        Message msg = new Message();
                        msg.what = 1;
                        msg.arg1 = response.code();
                        msg.obj = response.body().string();
                        mHandler.sendMessage(msg);
                    } else {
                        mHandler.sendEmptyMessage(404);
                    }
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(404);
                }
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tra_group_back:
                this.finish();
                break;
        }
    }

    //转发消息到群组
    private void sendMessage2Group(final String targetId) {
        if (messageItem == null) {
            return;
        }
        String content = messageItem.getContent();
        String name = messageItem.getCreateBy().getName();
        JSONObject json = new JSONObject();
        JSONObject jsonBull = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonBull.put("author", messageItem.getCreateBy().getName());
            jsonBull.put("bulletinId", messageItem.getId());
            jsonBull.put("bulletinUrl", "");
            jsonBull.put("content", content);
            String bulletinType = messageItem.getBulletinType();
            if (bulletinType == null) {
                return;
            }
            bulletinType = bulletinType.toUpperCase();
            List<Resources> list = new ArrayList<>();
            if (bulletinType.equals("ORIGIN")) {
                List<Resources> list1 = messageItem.getList();
                list.addAll(list1);
            } else if (bulletinType.equals("FORWARD")) {
                List<Resources> list1 = messageItem.getOriginBulletinInfo().getList();
                list.addAll(list1);
            }
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    Resources resources = list.get(i);
                    JSONObject object = new JSONObject();
                    object.put("id", resources.getId());
                    object.put("shortName", resources.getName());
                    object.put("size", resources.getSize());
                    jsonArray.put(object);
                }
                jsonBull.put("files", jsonArray);
            }

            json.put("MessageType", 0);
            json.put("ContentType", 8);
            json.put("ChatType", chatType);
            json.put("Content", "[好友动态]");
            json.put("FontSize", 14);
            json.put("FontStyle", 0);
            json.put("FontColor", 0);
            json.put("BulletinID", messageItem.getId());
            json.put("BulletinContent", jsonBull);
            json.put("requestName", name);
            json.put("requestRemark", "");
            json.put("requestGroupId", "");
            json.put("FileID", "");
            json.put("FileName", "");
            json.put("CreateTime", System.currentTimeMillis());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String messageBody = json.toString();
        if (messageBody.equals("")) {
            Toast.makeText(UIUtils.getContext(), "发送内容不能为空", Toast.LENGTH_SHORT).show();
        } else {
            String icon = sharedPreferences.getString(Constant.USER_ICON, "");
            Uri uri = Uri.parse(icon);
            UserInfo userInfo = new UserInfo(sharedPreferences.getString(Constant.ID_USER, ""),
                    sharedPreferences.getString(Constant.USER_NAME, ""), uri);
            final TextMessage textMessage = TextMessage.obtain(messageBody);
            textMessage.setUserInfo(userInfo);

            final Conversation.ConversationType type = Conversation.ConversationType.GROUP;
            //添加消息
            final io.rong.imlib.model.Message message = io.rong.imlib.model.Message.obtain(targetId, type, textMessage);
            message.setMessageDirection(io.rong.imlib.model.Message.MessageDirection.SEND);
            message.setSenderUserId(sharedPreferences.getString(Constant.ID_USER, ""));

            RongIMClient.getInstance().sendMessage(type, targetId,
                    textMessage, null, null, new RongIMClient.SendMessageCallback() {
                        @Override
                        public void onSuccess(Integer integer) {
                            io.rong.imlib.model.Message message = io.rong.imlib.model.Message.obtain(targetId, type, textMessage);
                            message.setMessageDirection(io.rong.imlib.model.Message.MessageDirection.SEND);
                            message.setSenderUserId(sharedPreferences.getString(Constant.ID_USER, ""));

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

                        }
                    }, new RongIMClient.ResultCallback<io.rong.imlib.model.Message>() {
                        @Override
                        public void onSuccess(io.rong.imlib.model.Message message) {

                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {
                        }
                    });
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (System.currentTimeMillis() - last_click_time < 3000) {
            return;
        }
        last_click_time = System.currentTimeMillis();

        if (NetWorkUtils.isNetworkAvailable(UIUtils.getContext())) {
            Object o = list.get(position);
            if (o instanceof GroupItem) {
                GroupItem item = (GroupItem) o;
                if (item.getSessionType() == 2) {
                    chatType = 1;
                } else if (item.getSessionType() == 3) {
                    chatType = 2;
                }
                sendMessage2Group(item.getSessionId());
                Toast.makeText(mContext, "已转发至群组", Toast.LENGTH_SHORT).show();
                this.finish();
            }
        } else {
            Toast.makeText(UIUtils.getContext(), "您当前没有链接网络", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SelectGroupActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SelectGroupActivity");
        MobclickAgent.onPause(this);
    }
}

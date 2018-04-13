package net.iclassmate.bxyd.ui.activitys.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.adapter.GroupMemberAdapter;
import net.iclassmate.bxyd.bean.contacts.GroupMember;
import net.iclassmate.bxyd.bean.message.Extra;
import net.iclassmate.bxyd.bean.owner.SpaceInfo;
import net.iclassmate.bxyd.bean.study.StudyMessageList;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.ui.activitys.balloon.BalloonActivity;
import net.iclassmate.bxyd.ui.activitys.constacts.SelectSortActivity;
import net.iclassmate.bxyd.ui.activitys.constacts.SendFriendRequestActivity;
import net.iclassmate.bxyd.ui.activitys.information.FriendInformationActivity;
import net.iclassmate.bxyd.ui.activitys.owner.HomePageActivity;
import net.iclassmate.bxyd.ui.activitys.study.ReportActivity;
import net.iclassmate.bxyd.ui.fragment.emotion.EmotionDisFragment;
import net.iclassmate.bxyd.utils.HttpManager;
import net.iclassmate.bxyd.utils.NetWorkUtils;
import net.iclassmate.bxyd.utils.RongIMClientUtils;
import net.iclassmate.bxyd.utils.UIUtils;
import net.iclassmate.bxyd.view.FullGridView;
import net.iclassmate.bxyd.view.TitleBar;
import net.iclassmate.bxyd.view.study.ShapeImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

public class ChatInformationActivity extends Activity implements TitleBar.TitleOnClickListener, View.OnClickListener {

    private Context mContext;
    private TitleBar titleBar;

    private RelativeLayout chatFile, chatRecord, chatClear, report, rl_chat_information, rl_group_information, chat_name_rl, group_member_rl;
    private ShapeImageView chat_icon;
    private TextView chat_name, chat_name_tv;
    private FullGridView gv;
    private ImageView chat_information_xiangqing;
    private ToggleButton tb_top, tb_disturb;
    private Button group_exit_btn;   //退群

    private String from, name, iconUrl;
    private String author, targetId, result, resultCode;
    private Boolean isInternet = false;   // 是否后网络
    private SharedPreferences sp;

    //群组
    private LinearLayout groups_ll, groups_info_resources;
    private RelativeLayout groups_file, groups_studyspace;
    private ShapeImageView groups_icon;
    private TextView groups_name, groups_update_name, groups_update_name1;
    private ImageView groups_add_care;
    private boolean isCare = false;
    private StudyMessageList smlist;
    private List<String> urlLisst;
    private ArrayList<String> administratorsList;   //管理员

    private Conversation.ConversationType conversationType;

    private HttpManager httpManager;
    private static final int NO_INTERNET = 0;    //没网络
    private static final int FIND_FRIENDINFO_SUCCEED = 1;  //通过好友ID获取备注名和用户号成功
    private static final int FIND_FRIENDINFO_FAIL = 2;     //通过好友ID获取备注名和用户号失败
    private static final int FIND_GROUP_INFO_SUCCEED = 3;   //获取群信息成功
    private static final int EXIT_DISCUSSION_SUCCEED = 4;        //退群聊
    private static final int EXIT_GROUP_SUCCEED = 10;        //退群组
    private static final int SPACE_RELATION_SUCCEED = 5;    //获取空间关系
    private static final int ADD_CARE_SUCCEED = 6;          //关注空间
    private static final int CANCEL_CARE_SUCCEED = 7;       //取消关注空间
    private final static int FINDE_CRICLE_FRIEND_SUCCEED = 8;   //查看朋友圈中的发布列表成功
    private static final int FINDE_SPACE_SUCCEED = 9;   //获取用户自己的空间信息
    private final static int USER_SELECT_SPACE_RELATIONSHIP_ATTENTION = 20; //当前好友与某一空间是否关注


    public static final String UPDATE_NAME = "update_name"; //修改群名称
    public static final String UPDATE_MESSAGE = "update_message"; //修改群名称

    private List<GroupMember> list;
    private GroupMemberAdapter adapter;
    private boolean isTop = false;
    private boolean isClearMessage = false;
    private int sessionType;    //1单聊   2群聊   3群组   4机构
    private boolean requestTop = true;  //是否网络请求置顶情况
    private boolean focusMe;  //是否允许关注我
    private boolean isConcern;  //是否已关注某一空间
    private String type;    //成员的类型

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case NO_INTERNET:
                    Toast.makeText(UIUtils.getContext(), "您没有连接网络，请连接后重试！", Toast.LENGTH_SHORT).show();
                    break;

                case FIND_FRIENDINFO_SUCCEED:
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        chat_name.setText(jsonObject.getString("remark"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case EXIT_DISCUSSION_SUCCEED:
                    if (result != null && !result.equals("404")) {
                        if (result.equals("100")) {
//                                Log.i("TAG", "user退出群失败，没有网络");
                            Toast.makeText(UIUtils.getContext(), "您没有连接网络，请连接后再重试！", Toast.LENGTH_SHORT).show();
                        } else {
                            //删除会话
                            delConversation(targetId);

//                                Log.i("TAG", "user退出群成功");
                            Toast.makeText(UIUtils.getContext(), "退群成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.putExtra("exit", "exit_group");
                            ChatInformationActivity.this.setResult(16, intent);
                            ChatInformationActivity.this.finish();
                        }

                    } else {
//                            Log.i("TAG", "user退出群失败");
                        Toast.makeText(UIUtils.getContext(), "退群失败", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case SPACE_RELATION_SUCCEED:
                    if (resultCode.equals("404")) {
//                            Log.i("ChatInformationActivity", "判断是否关注当前群组失败");
                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(resultCode);
                            isCare = jsonObject.getBoolean("isRelated");
                            if (isCare) {
                                groups_add_care.setImageResource(R.mipmap.bt_open);
                            } else {
                                groups_add_care.setImageResource(R.mipmap.bt_close);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case ADD_CARE_SUCCEED:  //添加关注
                    if (resultCode.equals("404")) {
//                            Log.i("ChatInformationActivity", "添加关注失败");
                    } else {
                        groups_add_care.setImageResource(R.mipmap.bt_open);
                        isCare = true;
                    }
                    break;
                case CANCEL_CARE_SUCCEED:   //取消关注
                    if (resultCode.equals("404")) {
//                            Log.i("ChatInformationActivity", "取消关注失败");
                    } else {
                        groups_add_care.setImageResource(R.mipmap.bt_close);
                        isCare = false;
                    }
                    break;
                case FINDE_CRICLE_FRIEND_SUCCEED:   //查看朋友圈中的发布列表成功
                    if (urlLisst != null && urlLisst.size() > 0) {
                        for (int i = 0; i < urlLisst.size(); i++) {
                            String url = urlLisst.get(i);
                            if (url != null && !TextUtils.isEmpty(url) && url.contains("http://")) {
                                View view = LayoutInflater.from(ChatInformationActivity.this).inflate(R.layout.finde_cricle_friend_item, null);
                                ImageView finde_cricle_friend_iv = (ImageView) view.findViewById(R.id.finde_cricle_friend_iv);
                                Picasso.with(mContext).load(url).config(Bitmap.Config.RGB_565).into(finde_cricle_friend_iv);
                                groups_info_resources.addView(view);
                            }
                        }
                    }
                    break;
                case FINDE_SPACE_SUCCEED:   //获取用户自己的空间信息
                    Intent startSelect = (Intent) msg.obj;
                    startActivityForResult(startSelect, 15);
                    break;
                case USER_SELECT_SPACE_RELATIONSHIP_ATTENTION:  //当前好友与某一空间是否关注
                    Intent startHome = new Intent(mContext, HomePageActivity.class);
                    startHome.putExtra(Constant.ID_USER, targetId);
                    startHome.putExtra(Constant.ID_SPACE, targetId);
                    startHome.putExtra("visitHomepage", focusMe);
                    startHome.putExtra(Constant.IS_CONCERN, isConcern);
                    startHome.putExtra(Constant.HOME_PAGE_TITLE, name);
                    if (sessionType == 1) { //个人
                        startHome.putExtra(Constant.ID_USERTYPE, Constant.TYPE_PRIVATE);
                    } else if (sessionType == 3) {   //空间
                        startHome.putExtra(Constant.ID_USERTYPE, Constant.TYPE_SPACE);
                    } else { //机构
                        startHome.putExtra(Constant.ID_USER, author);
                        startHome.putExtra(Constant.ID_USERTYPE, Constant.TYPE_GROUP);
                    }
                    Log.i("TAG", "进入主页 friendId:" + targetId + ",spaceid:" + targetId + ",sessionType:" + sessionType + ",是否允许关注:" + focusMe + ",名称:" + name + ",是否关注该空间:" + isConcern);
                    startActivity(startHome);
                    break;
                case EXIT_GROUP_SUCCEED:
                    String result = (String) msg.obj;
                    if (null != result && !result.equals("404")) {
                        if (result.equals("100")) {
//                                Log.i("TAG", "user退出群失败，没有网络");
                            Toast.makeText(UIUtils.getContext(), "您没有连接网络，请连接后再重试！", Toast.LENGTH_SHORT).show();
                        } else {
                            //删除会话
                            delConversation(targetId);

//                                Log.i("TAG", "user退出群成功");
                            Toast.makeText(UIUtils.getContext(), "退群成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.putExtra("exit", "exit_group");
                            ChatInformationActivity.this.setResult(16, intent);
                            ChatInformationActivity.this.finish();
                        }
                    } else {
//                            Log.i("TAG", "user退出群失败");
                        Toast.makeText(UIUtils.getContext(), "退群失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        initDatas();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat_information);
        mContext = this;
        sp = this.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        httpManager = new HttpManager();
        initView();
    }

    private void initView() {

        titleBar = (TitleBar) findViewById(R.id.chat_information_title_bar);
        titleBar.setLeftIcon(R.mipmap.ic_fanhui, "返回");
        titleBar.setTitle("聊天信息");

        chatFile = (RelativeLayout) findViewById(R.id.chat_file);
        chatClear = (RelativeLayout) findViewById(R.id.clear_chat_record);
        chatRecord = (RelativeLayout) findViewById(R.id.chat_record);
        report = (RelativeLayout) findViewById(R.id.report);
        rl_chat_information = (RelativeLayout) findViewById(R.id.rl_chat_information);
        rl_group_information = (RelativeLayout) findViewById(R.id.rl_group_information);
        chat_icon = (ShapeImageView) findViewById(R.id.chat_information_icon);
        chat_name = (TextView) findViewById(R.id.chat_information_name);
        gv = (FullGridView) findViewById(R.id.group_information_gv);
        chat_information_xiangqing = (ImageView) findViewById(R.id.chat_information_xiangqing);
        tb_top = (ToggleButton) findViewById(R.id.tb_top);
        tb_disturb = (ToggleButton) findViewById(R.id.tb_disturb);
        chat_name_rl = (RelativeLayout) findViewById(R.id.chat_name_rl);
        group_member_rl = (RelativeLayout) findViewById(R.id.group_member_rl);
        chat_name_tv = (TextView) findViewById(R.id.chat_name_tv);
        group_exit_btn = (Button) findViewById(R.id.group_exit_btn);

        groups_ll = (LinearLayout) findViewById(R.id.chat_groups);
        groups_file = (RelativeLayout) findViewById(R.id.groups_file);
        groups_studyspace = (RelativeLayout) findViewById(R.id.groups_studyspace);
        groups_icon = (ShapeImageView) findViewById(R.id.groups_icon);
        groups_name = (TextView) findViewById(R.id.groups_name);
        groups_add_care = (ImageView) findViewById(R.id.groups_add_care);
        groups_update_name = (TextView) findViewById(R.id.groups_update_name);
        groups_update_name1 = (TextView) findViewById(R.id.groups_update_name1);
        groups_info_resources = (LinearLayout) findViewById(R.id.groups_info_resources);

        initDatas();

        titleBar.setTitleClickListener(this);
        chatFile.setOnClickListener(this);
        chatRecord.setOnClickListener(this);
        chatClear.setOnClickListener(this);
        report.setOnClickListener(this);
        chat_information_xiangqing.setOnClickListener(this);
        chat_name_rl.setOnClickListener(this);
        group_member_rl.setOnClickListener(this);
        groups_file.setOnClickListener(this);
        groups_studyspace.setOnClickListener(this);
        groups_add_care.setOnClickListener(this);
        chat_icon.setOnClickListener(this);

//        chatFile.setVisibility(View.GONE);  //聊天文件

        //置顶对话
        tb_top.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setConversationTop(isChecked);
//                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
//                Date curDate = new Date(System.currentTimeMillis());
//                String str = formatter.format(curDate);
//                Toast.makeText(UIUtils.getContext(),"当前时间："+str,Toast.LENGTH_SHORT).show();
            }
        });

        //设置消息免打扰
        tb_disturb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sp.edit().putBoolean(Constant.HAS_RING + targetId, isChecked).apply();
//                Log.i("TAG", "消息免打扰："+targetId+"////"+isChecked);
            }
        });

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (list.get(position).getUserId().equals("add")) {
                    ArrayList<String> idList = new ArrayList<String>();
                    for (GroupMember groupMember : list) {
                        idList.add(groupMember.getUserId());
                    }
                    Intent startSelect = new Intent(UIUtils.getContext(), SelectContactsActivity.class);
                    startSelect.putExtra("from", from);
                    startSelect.putExtra("sessionName", name);
                    startSelect.putExtra("targetId", targetId);
                    startSelect.putExtra("type", "ChatInformationActivity");
                    startSelect.putExtra("sessionType", sessionType);
                    startSelect.putStringArrayListExtra("idList", idList);  //成员id
                    if (sessionType == 2) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i < list.size(); i++) {
                            if (i < 6) {
                                if (null != list.get(i).getUserName() && !TextUtils.isEmpty(list.get(i).getUserName())) {
                                    stringBuilder.append(list.get(i).getUserName() + "、");
                                }
                            } else {
                                break;
                            }
                        }
                        startSelect.putExtra("discussionName", stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1));
//                        Log.i("TAG", "sssssssssssssssssss:"+stringBuilder.toString().substring(0, stringBuilder.toString().length()-1));
                    }
                    startActivityForResult(startSelect, 13);
                } else if (list.get(position).getUserId().equals("exit")) {
                    Intent startSelect = new Intent(UIUtils.getContext(), SelectSortActivity.class);
                    startSelect.putExtra("from", from);
                    startSelect.putExtra("sessionName", name);
                    startSelect.putExtra("targetId", targetId);
                    startSelect.putExtra("author", author);
                    startSelect.putExtra("sessionType", sessionType);
                    startSelect.putStringArrayListExtra("administratorsList", administratorsList);  //管理员id
                    startSelect.putExtra("exit", "exit");
                    startSelect.putExtra("groupList", (Serializable) list);
                    startActivityForResult(startSelect, 14);
                } else {
                    final Intent startSelect = new Intent(UIUtils.getContext(), FriendInformationActivity.class);
                    final GroupMember groupMember = list.get(position);
                    startSelect.putExtra("from", "ChatInformationActivity");
                    if (groupMember.getUserId().equals(sp.getString(Constant.ID_USER, ""))) {
                        startSelect.putExtra("from", "EmotionMainFragment");
                    }
                    startSelect.putExtra("targetId", groupMember.getUserId());
//                    startActivityForResult(startSelect, 15);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (NetWorkUtils.isNetworkAvailable(UIUtils.getContext())) {
                                String result = httpManager.findSpaceInfo2(false, groupMember.getUserId());
                                try {
                                    JSONObject jsonObject = new JSONObject(result);
                                    type = "person";
                                    SpaceInfo spaceInfo = null;
                                    if (jsonObject.getString("type") != null && !TextUtils.isEmpty(jsonObject.getString("type"))) {
                                        type = jsonObject.getString("type");
//                                        spaceInfo = JsonUtils.StartSpaceInfoJson(result);
                                    }
                                    Log.i("ContactsActivity", "是否为机构是否为机构是否为机构是否为机构：" + type);
                                    startSelect.putExtra("type", type);
                                    Message msg = new Message();
                                    msg.obj = startSelect;
                                    msg.what = FINDE_SPACE_SUCCEED;
                                    handler.sendMessage(msg);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                handler.sendEmptyMessage(NO_INTERNET);
                            }
                        }
                    }).start();
                }
            }
        });

        group_exit_btn.setOnClickListener(this);
        //获取当前消息免打扰状态
        tb_disturb.setChecked(sp.getBoolean(Constant.HAS_RING + targetId, false));
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, final Intent data) {
        if (resultCode == 13) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (NetWorkUtils.isNetworkAvailable(UIUtils.getContext())) {
                        sendCommend(Conversation.ConversationType.GROUP, "", resultCode);    //发送添加添加成员通知
                        io.rong.imlib.model.Message message = sendTxtMsg(Conversation.ConversationType.GROUP, data.getStringExtra("selectName"), resultCode);
                        Intent intent = new Intent(UPDATE_MESSAGE);
                        intent.putExtra("message", message);
                        ChatInformationActivity.this.sendBroadcast(intent);
                    } else {
                        handler.sendEmptyMessage(NO_INTERNET);
                    }
                }
            }).start();

            //更新群聊默认名称（如果出现群聊加人报错，就注释if）
            if (sessionType == 2) {
                String discussionName = data.getStringExtra("discussionName");
//                Log.i("TAG", "+++++++++++++++:" + discussionName);
                Intent intent = new Intent(UPDATE_NAME);
                intent.putExtra("updateName", "updateName");
                ChatInformationActivity.this.sendBroadcast(intent);
            }

            ChatInformationActivity.this.finish();
        } else if (resultCode == 14) {
            if (data.getStringExtra("ExitGroup").equals("ExitSucceed")) {
                //更新群聊默认名称（如果出现群聊踢人报错，就注释if）
                if (sessionType == 2) {
                    if (null != data.getStringExtra("discussionName")) {
                        String discussionName = data.getStringExtra("discussionName");
//                        Log.i("TAG", "----------------:" + data.getStringExtra("discussionName"));
                        Intent intent = new Intent(UPDATE_NAME);
                        intent.putExtra("updateName", "updateName");
                        ChatInformationActivity.this.sendBroadcast(intent);
                    }
                }

                ChatInformationActivity.this.finish();
            }
        } else if (requestCode == 16 && resultCode == 16) {
            clearMessages();
        } else if (requestCode == 18 && resultCode == 11) {   //修改名称
            chat_name_tv.setText(data.getStringExtra("remarksName"));
            name = data.getStringExtra("remarksName");
            Intent intent = new Intent(UPDATE_NAME);
            intent.putExtra("name", data.getStringExtra("remarksName"));

            new Thread(new Runnable() {
                @Override
                public void run() {
                    sendCommend(Conversation.ConversationType.GROUP, "", 15);   //修改名称成功，发送同步消息
                    io.rong.imlib.model.Message message = sendTxtMsg(Conversation.ConversationType.GROUP, "", 15);
                    Intent intent = new Intent(UPDATE_MESSAGE);
                    intent.putExtra("message", message);
                    ChatInformationActivity.this.sendBroadcast(intent);
                }
            }).start();

            ChatInformationActivity.this.sendBroadcast(intent);
        } else if (requestCode == 19 && resultCode == 11) {
            groups_name.setText(data.getStringExtra("remarksName"));
            name = data.getStringExtra("remarksName");
            groups_update_name.setText(data.getStringExtra("remarksName"));
            Intent intent = new Intent(UPDATE_NAME);
            intent.putExtra("name", data.getStringExtra("remarksName"));

            new Thread(new Runnable() {
                @Override
                public void run() {
                    sendCommend(Conversation.ConversationType.GROUP, "", 15);
                    io.rong.imlib.model.Message message = sendTxtMsg(Conversation.ConversationType.GROUP, "", 15);
                    Intent intent = new Intent(UPDATE_MESSAGE);
                    intent.putExtra("message", message);
                    ChatInformationActivity.this.sendBroadcast(intent);
                }
            }).start();

            ChatInformationActivity.this.sendBroadcast(intent);
        } else if (requestCode == 20 && resultCode == 16) {    //退群聊
//            Log.i("TAG","2222222222222222222222222222222222");
            exitDiscussion();
        } else if (requestCode == 21 && resultCode == 16) {    //退群主或机构
//            Log.i("TAG","111111111111111111111111111111111111");
            exitSpace();
        }
    }

    private void initDatas() {
        from = getIntent().getStringExtra("from");
        if (from.equals("person")) {
            sessionType = 1;
            conversationType = Conversation.ConversationType.PRIVATE;
            name = getIntent().getStringExtra("name");
            iconUrl = getIntent().getStringExtra("iconUrl");
            targetId = getIntent().getStringExtra("targetId");
            rl_chat_information.setVisibility(View.VISIBLE);
            groups_ll.setVisibility(View.GONE);
            setHeadIcon(targetId);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (NetWorkUtils.isNetworkAvailable(UIUtils.getContext())) {
                        //获取用户名和用户号（备注名）
                        result = httpManager.getUserRemarkName(sp.getString(Constant.ID_USER, ""), targetId);
                        if (result.equals("404")) {
                            handler.sendEmptyMessage(FIND_FRIENDINFO_FAIL);
                        } else {
                            handler.sendEmptyMessage(FIND_FRIENDINFO_SUCCEED);
                        }
                    } else {
                        handler.sendEmptyMessage(NO_INTERNET);
                    }
                }
            }).start();

            if (!TextUtils.isEmpty(iconUrl)) {
                Picasso.with(mContext).load(iconUrl).into(chat_icon);
            }
        } else if (from.equals("group")) {
            conversationType = Conversation.ConversationType.GROUP;
            sessionType = getIntent().getIntExtra("sessionType", 2);
            author = getIntent().getStringExtra("author");
            administratorsList = getIntent().getStringArrayListExtra("administratorsList");
            rl_chat_information.setVisibility(View.GONE);
            rl_group_information.setVisibility(View.VISIBLE);
            chat_information_xiangqing.setVisibility(View.GONE);
            chat_name_rl.setVisibility(View.VISIBLE);
            group_member_rl.setVisibility(View.VISIBLE);
            group_exit_btn.setVisibility(View.VISIBLE);
            if (sessionType == 3) {
                groups_update_name1.setText("修改空间名称");
                setHeadIconGroup(targetId);
            } else if (sessionType == 4) {
                groups_file.setVisibility(View.GONE);
                setHeadIconGroup(author);
            }

            targetId = getIntent().getStringExtra("targetId");
            name = getIntent().getStringExtra("sessionName");
            author = getIntent().getStringExtra("author");
            isInternet = getIntent().getBooleanExtra("isInternet", false);
            if (sessionType == 3 || sessionType == 4) {
//                getSpaceRelation();   //是否关注
                focusMe = getIntent().getBooleanExtra("focusMe", true);
                chat_name_rl.setVisibility(View.GONE);
                groups_name.setText(name);
//                setHeadIconGroup(targetId);
                groups_update_name.setText(name);
                group_exit_btn.setBackgroundResource(R.drawable.friend_info_exit_group_selector);
            } else {
                groups_ll.setVisibility(View.GONE);
            }
            chat_name_tv.setText(name);
            list = new ArrayList<GroupMember>();
            Bundle b = getIntent().getBundleExtra("bundle");
            list = (ArrayList<GroupMember>) b.getSerializable("list");
            Log.i("TAG", "list:" + list.size() + "个");
            adapter = new GroupMemberAdapter(list, ChatInformationActivity.this);
            gv.setAdapter(adapter);

            if (sessionType == 3 || sessionType == 4) {
                getCircleFriend();
            }
        }

        /**
         * 根据不同会话类型的目标 Id，回调方式获取某一会话信息
         */
        if (requestTop) {
            if (NetWorkUtils.isNetworkAvailable(UIUtils.getContext())) {
                RongIMClient.getInstance().getConversation(conversationType, targetId, new RongIMClient.ResultCallback<Conversation>() {
                    @Override
                    public void onSuccess(Conversation conversation) {
                        if (null != conversation) {
                            isTop = conversation.isTop();
                            Log.i("ChatInformationActivity", "根据不同会话类型的目标 Id，回调方式获取会话信息成功" + isTop);
                            tb_top.setChecked(isTop);
                            requestTop = false;
                        }
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        Log.i("ChatInformationActivity", "根据不同会话类型的目标 Id，回调方式获取会话信息失败");
                    }
                });
            } else {
                Toast.makeText(UIUtils.getContext(), "您当前没有链接网络", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 置顶对话
     *
     * @param isTop 是否置顶，true置顶，false不置顶
     * @author LvZhanFeng
     */
    public void setConversationTop(final boolean isTop) {
        if (NetWorkUtils.isNetworkAvailable(UIUtils.getContext())) {
            RongIMClient.getInstance().setConversationToTop(conversationType, targetId, isTop, new RongIMClient.ResultCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
//                    Toast.makeText(UIUtils.getContext(), "设置成功", Toast.LENGTH_SHORT).show();
                    if (isTop) {
                        Intent intent = new Intent(EmotionDisFragment.NEW_MESSAGE);
                        intent.putExtra("isTop", "isTop");
                        ChatInformationActivity.this.sendBroadcast(intent);
                    }
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    Toast.makeText(UIUtils.getContext(), "设置失败，请重新设置！", Toast.LENGTH_SHORT).show();
                    tb_top.setChecked(!isTop);
                }
            });
        } else {
            tb_top.setChecked(!isTop);
            handler.sendEmptyMessage(NO_INTERNET);
        }
    }

    /**
     * 清空指定类型，targetId 的某一会话所有聊天消息记录
     */
    private void clearMessages() {
        if (NetWorkUtils.isNetworkAvailable(UIUtils.getContext())) {
            RongIMClient.getInstance().clearMessages(conversationType, targetId, new RongIMClient.ResultCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
                    Toast.makeText(UIUtils.getContext(), "聊天记录已清除", Toast.LENGTH_SHORT).show();
                    isClearMessage = true;
                    Intent intent = new Intent(EmotionDisFragment.NEW_MESSAGE);
                    intent.putExtra("deleteMessages", "deleteMessages");
                    intent.putExtras(intent);
                    ChatInformationActivity.this.sendBroadcast(intent);
                    ChatInformationActivity.this.setResult(17, intent);
                    ChatInformationActivity.this.finish();
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    Toast.makeText(UIUtils.getContext(), "聊天记录清除失败，请重试！", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            handler.sendEmptyMessage(NO_INTERNET);
        }
    }

    @Override
    public void leftClick() {
        if (isClearMessage) {
            Intent intent = getIntent();
            setResult(17, intent);
        }
        requestTop = true;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chat_file:    //聊天文件
                Intent startFile = new Intent(UIUtils.getContext(), ChatFileActivity.class);
                startFile.putExtra("from", from);
                startFile.putExtra("targetId", targetId);
                startActivity(startFile);
                break;
            case R.id.clear_chat_record:    //清除聊天记录
                Intent clearRecord = new Intent(UIUtils.getContext(), BalloonActivity.class);
                startActivityForResult(clearRecord, 16);
                break;
            case R.id.chat_record:  //聊天记录
                Intent startRecord = new Intent(UIUtils.getContext(), ChatRecordActivity.class);
                startRecord.putExtra("targetId", targetId);
                startRecord.putExtra("from", from);
                startRecord.putExtra("author", author);
                startRecord.putExtra("name", name);
                startRecord.putExtra("listMember", (Serializable) list);
                startRecord.putExtra("sessionType", sessionType);
                startActivity(startRecord);
                break;
            case R.id.report:   //举报
                Intent intent = new Intent(ChatInformationActivity.this, ReportActivity.class);
                intent.putExtra("id", targetId);
                if (sessionType == 1) {
                    intent.putExtra("type", 1);
                } else if (sessionType == 2 || sessionType == 3) {
                    intent.putExtra("type", 2);
                }
                startActivity(intent);
//                Toast.makeText(UIUtils.getContext(),"点击举报",Toast.LENGTH_SHORT).show();
                break;
            case R.id.chat_information_xiangqing:   //创建群聊
                Intent startSelect = new Intent(UIUtils.getContext(), SelectContactsActivity.class);
                startSelect.putExtra("from", from);
                startSelect.putExtra("type", "ChatInformationActivity");
                startSelect.putExtra("friendName", chat_name.getText().toString());
                startSelect.putExtra("friendId", targetId);
                startActivity(startSelect);
                break;
            case R.id.chat_name_rl:  //修改群名称
                Intent startUpdate = new Intent(UIUtils.getContext(), SendFriendRequestActivity.class);
                startUpdate.putExtra("from", "FriendInformationActivity");
                startUpdate.putExtra("friendId", targetId);
                startUpdate.putExtra("type", "group");
                startUpdate.putExtra("sessionName", name);
                startUpdate.putExtra("sessionType", sessionType);
                startActivityForResult(startUpdate, 18);
                break;
            case R.id.groups_file: //修改群组名称
                String userID = sp.getString(Constant.ID_USER, "");
                if (author.equals(userID)) {
                    Intent startUpdategroups = new Intent(UIUtils.getContext(), SendFriendRequestActivity.class);
                    startUpdategroups.putExtra("from", "FriendInformationActivity");
                    startUpdategroups.putExtra("friendId", targetId);
                    startUpdategroups.putExtra("type", "group");
                    startUpdategroups.putExtra("sessionName", name);
                    startUpdategroups.putExtra("sessionType", sessionType);
                    startActivityForResult(startUpdategroups, 19);
                } else {
                    Toast.makeText(UIUtils.getContext(), "您不是群主，没有权限", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.group_member_rl://查看全部群成员
                Intent startMember = new Intent(UIUtils.getContext(), SelectSortActivity.class);
                startMember.putExtra("groupList", (Serializable) list);
                startMember.putExtra("targetName", name);
                startMember.putExtra("visible", false);
                startActivity(startMember);
                break;
            case R.id.group_exit_btn:   //退群
//                if(NetWorkUtils.isNetworkAvailable(UIUtils.getContext())) {
//                    sendCommend(Conversation.ConversationType.GROUP, "", 14);    //发送退出命令消息
//
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            String userId = sp.getString(Constant.ID_USER, "");
//                            String name = sp.getString("name", "");
//                            List<String> userIdList = new ArrayList<>();
//                            List<String> userNameList = new ArrayList<>();
//                            userIdList.add(userId);
//                            userNameList.add(name);
//                            result = httpManager.exitGroup(targetId, userIdList, userNameList);
//                            handler.sendEmptyMessage(EXIT_DISCUSSION_SUCCEED);
//                        }
//                    }).start();
//                } else {
//                    handler.sendEmptyMessage(NO_INTERNET);
//                }
                Intent exitRecord = new Intent(UIUtils.getContext(), BalloonActivity.class);
                if (sessionType == 2) {
                    exitRecord.putExtra("title", "退出群聊？");
                    startActivityForResult(exitRecord, 20);
                } else if (sessionType == 3 || sessionType == 4) {
                    exitRecord.putExtra("title", "退出空间？");
                    startActivityForResult(exitRecord, 21);
                }
                break;
            case R.id.groups_studyspace:    //主页
//                Intent startHome = new Intent(mContext, HomePageActivity.class);
//                startHome.putExtra(Constant.ID_USER, targetId);
//                startHome.putExtra(Constant.ID_SPACE, targetId);
//                startHome.putExtra("visitHomepage", focusMe);
//                startHome.putExtra(Constant.HOME_PAGE_TITLE, name);
//                if (sessionType == 1) {
//                    startHome.putExtra(Constant.ID_USERTYPE, Constant.TYPE_PRIVATE);
//                }else if (sessionType == 3) {
//                    startHome.putExtra(Constant.ID_USERTYPE, Constant.TYPE_SPACE);
//                }else {
//                    startHome.putExtra(Constant.ID_USERTYPE, Constant.TYPE_GROUP);
//                }
//                Log.i("TAG", "进入主页 friendId:"+targetId+",spaceid:"+targetId+",sessionType:"+sessionType+",是否允许关注:"+focusMe+",名称:"+name);
//                startActivity(startHome);
                userSelectSpaceRelationship();
                break;
            case R.id.groups_add_care:      //关注/取消关注
                if (isCare) {
                    cancelCare();
                } else {
                    addCare();
                }
                break;
            case R.id.chat_information_icon: //单人头像
                final Intent startSelect2 = new Intent(UIUtils.getContext(), FriendInformationActivity.class);
                startSelect2.putExtra("from", "ChatInformationActivity");
//                startSelect2.putExtra("type", "person");
                startSelect2.putExtra("targetId", targetId);
//                startActivityForResult(startSelect2, 15);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (NetWorkUtils.isNetworkAvailable(UIUtils.getContext())) {
                            String result = httpManager.findSpaceInfo2(false, targetId);
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                type = "person";
                                SpaceInfo spaceInfo = null;
                                if (null != jsonObject.getString("type") && !TextUtils.isEmpty(jsonObject.getString("type"))) {
                                    type = jsonObject.getString("type");
//                                        spaceInfo = JsonUtils.StartSpaceInfoJson(result);
                                }
                                Log.i("ContactsActivity", "是否为机构是否为机构是否为机构是否为机构：" + type);
                                startSelect2.putExtra("type", type);
                                Message msg = new Message();
                                msg.obj = startSelect2;
                                msg.what = FINDE_SPACE_SUCCEED;
                                handler.sendMessage(msg);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            handler.sendEmptyMessage(NO_INTERNET);
                        }
                    }
                }).start();
                break;
        }
    }

    //退出群聊
    public void exitDiscussion() {
        if (NetWorkUtils.isNetworkAvailable(UIUtils.getContext())) {
            sendCommend(Conversation.ConversationType.GROUP, "", 14);    //发送退出命令消息

            new Thread(new Runnable() {
                @Override
                public void run() {
                    String userId = sp.getString(Constant.ID_USER, "");
                    String name = sp.getString("name", "");
                    List<String> userIdList = new ArrayList<>();
                    List<String> userNameList = new ArrayList<>();
                    userIdList.add(userId);
                    userNameList.add(name);
                    result = httpManager.exitGroup(targetId, userIdList, userNameList);
                    handler.sendEmptyMessage(EXIT_DISCUSSION_SUCCEED);
                }
            }).start();
        } else {
            handler.sendEmptyMessage(NO_INTERNET);
        }
    }

    //退出群组
    public void exitSpace() {
        if (NetWorkUtils.isNetworkAvailable(UIUtils.getContext())) {
            sendCommend(Conversation.ConversationType.GROUP, "", 14);    //发送退出命令消息

            new Thread(new Runnable() {
                @Override
                public void run() {
                    String userId = sp.getString(Constant.ID_USER, "");
                    String groupId = null;
                    for (GroupMember groupMember : list) {
                        if (userId.equals(groupMember.getUserId())) {
                            groupId = groupMember.getGroupId();
                        }
                    }
                    String result = null;
                    if (null != groupId && !TextUtils.isEmpty(groupId)) {
                        result = httpManager.exitSpace1(userId, groupId);
                    }
                    Message msg = new Message();
                    msg.what = EXIT_GROUP_SUCCEED;
                    msg.obj = result;
//                    handler.sendEmptyMessage(EXIT_DISCUSSION_SUCCEED);
                    handler.sendMessage(msg);
                }
            }).start();
        } else {
            handler.sendEmptyMessage(NO_INTERNET);
        }
    }

    //删除会话
    private void delConversation(final String fid) {
        RongIMClient.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                if (null != conversations && conversations.size() > 0) {
                    for (int i = 0; i < conversations.size(); i++) {
                        Conversation conversation = conversations.get(i);
                        String id = conversation.getTargetId();
                        if (fid.equals(id)) {
                            //从会话列表中移除某一会话，但是不删除会话内的消息
                            RongIMClient.getInstance().removeConversation(conversation.getConversationType(), id, new RongIMClient.ResultCallback<Boolean>() {
                                @Override
                                public void onSuccess(Boolean aBoolean) {
//                                    Log.i("ChatInFormationActivity","从会话列表中移除某一会话成功");
                                }

                                @Override
                                public void onError(RongIMClient.ErrorCode errorCode) {
//                                    Log.i("ChatInFormationActivity","从会话列表中移除某一会话失败");
                                }
                            });
                            break;
                        }
                    }
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }

    //判断是否关注学习圈
    public void getSpaceRelation() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (NetWorkUtils.isNetworkAvailable(UIUtils.getContext())) {
                    String mainSpaceId = sp.getString(Constant.ID_SPACE, "");
                    resultCode = httpManager.getSpaceRelation(mainSpaceId, targetId);
                    handler.sendEmptyMessage(SPACE_RELATION_SUCCEED);
                }
            }
        }).start();
    }

    //关注学习圈（空间）
    public void addCare() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (NetWorkUtils.isNetworkAvailable(UIUtils.getContext())) {
                    String mainSpaceId = sp.getString(Constant.ID_SPACE, "");
                    resultCode = httpManager.addCare(mainSpaceId, targetId);
                    handler.sendEmptyMessage(ADD_CARE_SUCCEED);
                } else {
                    handler.sendEmptyMessage(NO_INTERNET);
                }
            }
        }).start();
    }

    //取消关注学习圈（空间）
    public void cancelCare() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (NetWorkUtils.isNetworkAvailable(UIUtils.getContext())) {
                    String mainSpaceId = sp.getString(Constant.ID_SPACE, "");
                    resultCode = httpManager.cancelCare(mainSpaceId, targetId);
                    handler.sendEmptyMessage(CANCEL_CARE_SUCCEED);
                } else {
                    handler.sendEmptyMessage(NO_INTERNET);
                }
            }
        }).start();
    }

    //设置头像(个人)
    private void setHeadIcon(final String userId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String url = httpManager.getUserIconUrl(userId);
                if (null != url && !TextUtils.isEmpty(url)) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Picasso.with(mContext).load(url).resize((int) mContext.getResources().getDimension(R.dimen.view_43),
                                    (int) mContext.getResources().getDimension(R.dimen.view_43))
                                    .placeholder(R.mipmap.ic_geren_xuanren).error(R.mipmap.ic_geren_xuanren).config(Bitmap.Config.RGB_565).into(chat_icon);
                        }
                    });
                }
            }
        }).start();
    }

    //设置头像（空间，机构）
    private void setHeadIconGroup(final String userId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String url = httpManager.getUserIconUrl(userId);
                if (null != url && !TextUtils.isEmpty(url)) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Picasso.with(mContext).load(url).resize((int) mContext.getResources().getDimension(R.dimen.view_43),
                                    (int) mContext.getResources().getDimension(R.dimen.view_43))
                                    .placeholder(R.mipmap.ic_geren_xuanren).error(R.mipmap.ic_geren_xuanren).config(Bitmap.Config.RGB_565).into(groups_icon);
                        }
                    });
                }
            }
        }).start();
    }

    //查看朋友圈中的发布列表
    public void getCircleFriend() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (NetWorkUtils.isNetworkAvailable(UIUtils.getContext())) {
                    String result = httpManager.findHomepageList(targetId, 1, 1);
                    if (result != null && !TextUtils.isEmpty(result) && !result.equals("404")) {
                        result = removeBOM(result);

                        JSONObject json = null;
                        try {
                            json = new JSONObject(result);
                            smlist = new StudyMessageList();
                            smlist.parserJson(json);
                            urlLisst = new ArrayList<String>();
                            if (smlist != null && smlist.getList() != null & smlist.getList().size() > 0) {
                                if (smlist.getList().get(0).getList() != null && smlist.getList().get(0).getList().size() > 0) {
                                    for (int i = 0; i < smlist.getList().get(0).getList().size(); i++) {
                                        String url = smlist.getList().get(0).getList().get(i).getImage();
                                        if (url != null && !TextUtils.isEmpty(url)) {
                                            urlLisst.add(url);
                                        }
                                    }
                                }
                            }
                            Log.i("info", "获取的动态222: " + urlLisst.toString());
                            handler.sendEmptyMessage(FINDE_CRICLE_FRIEND_SUCCEED);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    //判断用户与一个空间有哪些关系（用于跳转空间主页）
    private void userSelectSpaceRelationship() {
        if (NetWorkUtils.isNetworkAvailable(UIUtils.getContext())) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String result = httpManager.userSelectSpaceRelationship(sp.getString(Constant.ID_USER, ""), targetId);
                    if (null != result && !result.equals("404")) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            isConcern = jsonObject.optBoolean("isConcern");
                            handler.sendEmptyMessage(USER_SELECT_SPACE_RELATIONSHIP_ATTENTION);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        isConcern = true;
                        handler.sendEmptyMessage(USER_SELECT_SPACE_RELATIONSHIP_ATTENTION);
                    }
                }
            }).start();
        } else {
            handler.sendEmptyMessage(NO_INTERNET);
        }
    }

    public String removeBOM(String data) {
        if (TextUtils.isEmpty(data)) {
            return data;
        }
        if (data.startsWith("\ufeff")) {
            return data.substring(1);
        } else {
            return data;
        }
    }

    /**
     * 邀请好友加入群组、群聊后给群之前的成员发送命令消息
     *
     * @param content    消息内容
     * @param resultCode 用于区别CommandNotificationMessage的名称
     */
    public void sendCommend(Conversation.ConversationType type, final String content, final int resultCode) {
        Extra.Operation operation = null;
        if (resultCode == 13) {  //加人
            if (sessionType == 2) {
                operation = Extra.Operation.SYNCH_GROUP;
            } else if (sessionType == 3 || sessionType == 4) {
                operation = Extra.Operation.SYNCH_SPACE;
            }
        } else if (resultCode == 14) {    //踢人
            if (sessionType == 2) {
                operation = Extra.Operation.KICK_OUT_GROUP;
            } else if (sessionType == 3 || sessionType == 4) {
                operation = Extra.Operation.KICK_OUT_SPACE;
            }
        } else if (resultCode == 15) {    //同步
            if (sessionType == 2) {
                operation = Extra.Operation.SYNCH_GROUP;
            } else if (sessionType == 3 || sessionType == 4) {
                operation = Extra.Operation.SYNCH_SPACE;
            }
        }
        RongIMClientUtils.sendCommend(operation, targetId, name, type, content);
    }

    /**
     * 发送通知消息
     *
     * @param type
     * @param userName   加入群的人亲戚
     * @param resultCode
     */
    public io.rong.imlib.model.Message sendTxtMsg(Conversation.ConversationType type, String userName, int resultCode) {
        String content = null;
        int chatType = 1;
        if (resultCode == 13) {
//                    Log.i("TAG", "sessionType="+sessionType);
            if (sessionType == 2) {
                content = "欢迎 " + userName + "加入群聊";
            } else if (sessionType == 3 || sessionType == 4) {
                content = "欢迎 " + userName + "加入群组";
                chatType = 2;
            }
        } else if (resultCode == 15) {   //修改群名称（群头像）
            if (sessionType == 2) {
                content = sp.getString("name", "") + " 修改群聊名为 " + name;
                chatType = 1;
            } else if (sessionType == 3 || sessionType == 4) {
                content = sp.getString("name", "") + " 修改群组名为 " + name;
                chatType = 2;
            }
        }
        return RongIMClientUtils.sendTxtMsg(type, 11, chatType, content, targetId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("ChatInformationActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("ChatInformationActivity");
        MobclickAgent.onPause(this);
    }
}

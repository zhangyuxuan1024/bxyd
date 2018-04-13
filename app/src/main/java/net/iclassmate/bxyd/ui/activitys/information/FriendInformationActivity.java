package net.iclassmate.bxyd.ui.activitys.information;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.bean.contacts.FindUserInfo;
import net.iclassmate.bxyd.bean.message.UserMessage;
import net.iclassmate.bxyd.bean.owner.SpaceInfo;
import net.iclassmate.bxyd.bean.study.StudyMessageList;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.ui.activitys.chat.ChatActivity;
import net.iclassmate.bxyd.ui.activitys.chat.GroupChatActivity;
import net.iclassmate.bxyd.ui.activitys.constacts.ContactsActivity;
import net.iclassmate.bxyd.ui.activitys.constacts.PhoneContactActivity;
import net.iclassmate.bxyd.ui.activitys.constacts.SendFriendRequestActivity;
import net.iclassmate.bxyd.ui.activitys.owner.HomePageActivity;
import net.iclassmate.bxyd.ui.activitys.study.IsWifiActivity;
import net.iclassmate.bxyd.utils.HttpManager;
import net.iclassmate.bxyd.utils.JsonUtils;
import net.iclassmate.bxyd.utils.NetWorkUtils;
import net.iclassmate.bxyd.utils.UIUtils;
import net.iclassmate.bxyd.view.Loading;
import net.iclassmate.bxyd.view.TitleBar;
import net.iclassmate.bxyd.view.study.ShapeImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;
import io.rong.message.TextMessage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FriendInformationActivity extends FragmentActivity implements TitleBar.TitleOnClickListener, View.OnClickListener {
    private Context mContext;
    private TitleBar titleBar;
    private String friendId, userId, iconUrl, spaceId;
    private String type;
    private boolean isFriend;
    private RelativeLayout study_space, friend_chat_file;
    private Button chat, delete, person, groupOrorg;
    private TextView name, userCode, tv_remarks_name, friend_info_name2, friend_info_studyspace_tv, tv_remarks_name2;
    private ImageView gender, icon, img_add_care;
    private Loading loading;
    private HttpManager httpManager;
    private String result;
    //    private FindUserInfo findUserInfo;
    private FindUserInfo findUserInfo;
    private String sessionName, sessionIcon, author, userName, targetUserId, trueFriendName;
    private LinearLayout mBtn, ll_add_cancel_care, friend_info_resources;
    private int function;   //判断当前机构   0：个人    1：机构    2：群组
    public final static int GET_FRIENDINFO_SUCCEED = 1;
    public final static int GET_FRIENDINFO_FAIL = 2;
    public static final int EXIT_GROUP_SUCCEED = 9;
    public static final int EXIT_GROUP_FAIL = 10;
    public static final int CLOSE_ACTIVTY = 12;
    private final static int FINDE_SPACE_SUCCEED = 13;   //查看空间信息成功
    private final static int FINDE_SPACE_FAIL = 14;   //查看空间信息失败
    private static final int NO_INTERNET = 15;    //没网络
    private static final int REQ_DEL_FRI = 6;
    private final static int FINDE_CRICLE_FRIEND_SUCCEED = 16;   //查看朋友圈中的发布列表成功
    private final static int FINDE_CRICLE_FRIEND_FAIL = 17;   //查看朋友圈中的发布列表失败
    private final static int WHETHER_IN_THE_SPACE = 18;   //该用户是否在某一空间中（包括群组空间和机构空间）
    private final static int WHETHER_IN_THE_SPACE_FALL = 19;   //该用户是否在某一空间中（包括群组空间和机构空间）失败
    private final static int USER_SELECT_SPACE_RELATIONSHIP_ATTENTION = 20; //当前好友与某一空间是否关注
    private final static int REQ_ADD_FRI = 21;//申请添加好友

    private SharedPreferences sp;
    private OkHttpClient client;
    private String from, spaceid, uCode, userinfo;
    private boolean iscare;
    private long last_click;
    private boolean searchMyresource;  //是否允许别人搜到我发布的资源
    private boolean focusMe;  //是否允许关注我
    private boolean inTheOrg = false;   //该用户是否在某一机构中
    private boolean isConcern;  //是否已关注某一空间
    private StudyMessageList smlist;
    private List<String> urlLisst;

    //    private ArrayList<GroupInfo> groupList;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_FRIENDINFO_FAIL:
                    loading.hideLoading(true);
                    Toast.makeText(UIUtils.getContext(), "获取好友信息失败", Toast.LENGTH_SHORT).show();
                    break;
                case GET_FRIENDINFO_SUCCEED:
                    loading.hideLoading(true);
//                    Log.i("info", "好友信息=" + result);
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        trueFriendName = jsonObject.getString("userName");
                        if (jsonObject.getString("remark") != null && !TextUtils.isEmpty(jsonObject.getString("remark")) && !jsonObject.getString("remark").equals("null")) {
                            if (trueFriendName.equals(jsonObject.getString("remark"))) {
                                sessionName = trueFriendName;
                            } else {
                                sessionName = jsonObject.getString("remark");
                                tv_remarks_name2.setText(sessionName);
                            }
                        }
                        uCode = jsonObject.getString("userCode");
                        name.setText(sessionName);
                        userCode.setText("用户号：" + uCode);
                        if (null != type && type.equals("org")) {
                            userCode.setText("机构号：" + uCode);
                        }
                        setHeadIcon(friendId, 0);
                    } catch (Exception e) {
//                        Toast.makeText(UIUtils.getContext(), "获取好友信息失败", Toast.LENGTH_SHORT).show();
                    }

                    if (type != null) {
                        if (type.equals("group")) {
                            icon.setBackgroundResource(R.mipmap.ic_touxiang_qunmingpian);
                        } else if (type.equals("org")) {
                            icon.setBackgroundResource(R.mipmap.ic_touxiang_jigoumingpian);
                        }
                    }
                    break;
                //删除好友
                case 3:
                    loading.hideLoading(true);
                    int ret = msg.arg1;
                    if (ret == 200) {
                        Toast.makeText(mContext, "好友已删除", Toast.LENGTH_SHORT).show();
                        if (from.equals("PhoneContactActivity")) {
                            Intent intent = getIntent();
                            setResult(PhoneContactActivity.DELETE_FRIENDER, intent);
                        } else if (from.equals("ContactsActivity")) {
                            Intent intent = getIntent();
                            setResult(1, intent);
                        }
                        finish();
                    }
                    break;
                //判断2人是否是好友
                case 4:
                    loading.hideLoading(true);
                    initData();
                    break;
                //判断是否关注
                case 7:
                    loading.hideLoading(true);
                    int code = msg.arg1;
                    if (code == 200) {
                        String result = (String) msg.obj;
                        try {
                            JSONObject object = new JSONObject(result);
                            iscare = object.getBoolean("isRelated");
                            if (iscare) {
                                img_add_care.setImageResource(R.mipmap.bt_open);
                            } else {
                                img_add_care.setImageResource(R.mipmap.bt_close);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                //获取spaceId
                case 8:
                    loading.hideLoading(true);
                    code = msg.arg1;
                    if (code == 200) {
                        String result = (String) msg.obj;
                        JSONObject object = null;
                        try {
                            object = new JSONObject(result);
                            spaceid = object.getString("uuid");
                            Log.i("info", "获取spaceid: " + spaceid + ",frienid:" + friendId);
                            getCircleFriend();
                            String sid = sp.getString(Constant.ID_SPACE, "");
                            getSpaceRelation(sid, spaceid); //判断是否关注
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case EXIT_GROUP_SUCCEED:    //退群成功
                    loading.hideLoading(true);
                    if (!result.equals("404")) {
                        if (result.equals("100")) {
//                            Log.i("TAG", "user退出群组失败，没有网络");
                            Toast.makeText(UIUtils.getContext(), mContext.getResources().getString(R.string.alert_msg_check_net), Toast.LENGTH_SHORT).show();
                        } else {
                            //删除会话
                            delConversation(friendId, Conversation.ConversationType.GROUP);

//                            Log.i("TAG", "user退出群组成功");
                            Toast.makeText(UIUtils.getContext(), "退群成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.putExtra("exit", "exit_group");
                            FriendInformationActivity.this.setResult(RESULT_OK, intent);
                            FriendInformationActivity.this.finish();
                        }

                    } else {
//                        Log.i("TAG", "user退出群组失败");
                        Toast.makeText(UIUtils.getContext(), "退群失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 11:
                    loading.hideLoading(true);
                    try {
                        JSONObject jsonObject = new JSONObject(userinfo);
                        UserMessage userMessage = new UserMessage();
                        userMessage.parserJson(jsonObject);
                        sessionName = userMessage.getUserInfo().getName();
                        uCode = userMessage.getUserInfo().getUserCode();
                        name.setText(sessionName);
                        friend_info_name2.setText(sessionName);
                        tv_remarks_name2.setText(sessionName);
                        if (type.equals("person")) {
                            userCode.setText("用户号：" + uCode);
                        } else if (type.equals("org")) {
                            userCode.setText("机构号：" + uCode);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    loading.hideLoading(true);
                    break;
                case CLOSE_ACTIVTY:
                    loading.hideLoading(true);
                    finish();
                    break;
                case 101:
                    Toast.makeText(mContext, "您的请求已发送成功，请等候群主/管理员验证。", Toast.LENGTH_SHORT).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(3000);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            handler.sendEmptyMessage(CLOSE_ACTIVTY);
                        }
                    }).start();
                    break;
                case FINDE_SPACE_SUCCEED:   //查看空间信息成功
                    loading.hideLoading(true);
//                    Intent intent = (Intent) msg.obj;
//                    startActivity(intent);
                    if (searchMyresource) {
                        study_space.setVisibility(View.VISIBLE);
                    } else {
//                        study_space.setVisibility(View.GONE);
                    }
                    break;
                case FINDE_SPACE_FAIL:   //查看空间信息失败
                    loading.hideLoading(true);
                    Toast.makeText(UIUtils.getContext(), "连接网络超时，请重试！", Toast.LENGTH_SHORT).show();
                    break;
                case NO_INTERNET:    //没网络
                    loading.hideLoading(true);
                    Toast.makeText(UIUtils.getContext(), "您没有连接网络，请连接后重试！", Toast.LENGTH_SHORT).show();
                    break;
                case FINDE_CRICLE_FRIEND_SUCCEED:   //查看朋友圈中的发布列表成功
                    loading.hideLoading(true);
                    if (urlLisst != null && urlLisst.size() > 0) {
                        for (int i = 0; i < urlLisst.size(); i++) {
                            String url = urlLisst.get(i);
                            if (url != null && !TextUtils.isEmpty(url) && url.contains("http://")) {
                                View view = LayoutInflater.from(FriendInformationActivity.this).inflate(R.layout.finde_cricle_friend_item, null);
                                ImageView finde_cricle_friend_iv = (ImageView) view.findViewById(R.id.finde_cricle_friend_iv);
                                Picasso.with(mContext).load(url).config(Bitmap.Config.RGB_565).into(finde_cricle_friend_iv);
                                friend_info_resources.addView(view);
                            }
                        }
                    }
                    break;
                case WHETHER_IN_THE_SPACE:    //该用户是否在某一空间中
                    loading.hideLoading(true);
                    if (inTheOrg) {   //true 在
                        mBtn.setVisibility(View.VISIBLE);
                        groupOrorg.setVisibility(View.GONE);
                        delete.setBackgroundResource(R.drawable.friend_info_exit_group_selector);
                    } else {          //false 不在
                        mBtn.setVisibility(View.GONE);
                        groupOrorg.setVisibility(View.VISIBLE);
                    }
                    break;
                case WHETHER_IN_THE_SPACE_FALL://该用户是否在某一空间中失败
                    loading.hideLoading(true);
                    mBtn.setVisibility(View.GONE);
                    groupOrorg.setVisibility(View.VISIBLE);
                    break;
                case USER_SELECT_SPACE_RELATIONSHIP_ATTENTION:  //当前好友与某一空间是否关注
                    loading.hideLoading(true);
                    Intent intent = new Intent(mContext, HomePageActivity.class);
                    // Log.i("TAG", "进入主页 friendId:" + friendId + ",spaceid:" + spaceid + ",type:" + type + ",是否允许关注:" + focusMe + ",名称:" + sessionName + ",是否关注该空间:" + isConcern);
                    intent.putExtra("visitHomepage", focusMe);
                    intent.putExtra(Constant.IS_CONCERN, isConcern);
                    intent.putExtra(Constant.HOME_PAGE_TITLE, sessionName);
                    if (type.equals("person")) {
                        intent.putExtra(Constant.ID_USERTYPE, Constant.TYPE_PRIVATE);
                        intent.putExtra(Constant.ID_USER, friendId);
                        intent.putExtra(Constant.ID_SPACE, spaceid);
                    } else if (type.equals("org")) {
                        intent.putExtra(Constant.ID_USERTYPE, Constant.TYPE_GROUP);
                        if (from.equals("GroupListActivity")) {
                            intent.putExtra(Constant.ID_USER, author);
                        } else {
                            intent.putExtra(Constant.ID_USER, friendId);
                        }

                        intent.putExtra(Constant.ID_SPACE, spaceid);
                    } else if (type.equals("group")) {
                        intent.putExtra(Constant.ID_USERTYPE, Constant.TYPE_SPACE);
                        intent.putExtra(Constant.ID_USER, userId);
                        intent.putExtra(Constant.ID_SPACE, friendId);
                        intent.putExtra(Constant.HOME_PAGE_TITLE, sessionName);
                    }
                    loading.hideLoading(true);
                    startActivity(intent);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_friend_information);

        mContext = this;
        sp = mContext.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        userId = sp.getString(Constant.ID_USER, "");
        spaceId = sp.getString(Constant.ID_SPACE, "");
        userName = sp.getString("name", "");
        initView();

        from = getIntent().getStringExtra("from");
        type = getIntent().getStringExtra("type");
        if (from.equals("ContactsActivity")) {
            function = 0;
            isFriend = false;
            friendId = getIntent().getStringExtra("friendId");
//            isFriend();
            initData();
            setHeadIcon(friendId, 0);
        } else if (from.equals("GroupListActivity")) {
            function = 2;
            friendId = getIntent().getStringExtra("sessionId");
            sessionName = getIntent().getStringExtra("sessionName");
            sessionIcon = getIntent().getStringExtra("sessionIcon");
            author = getIntent().getStringExtra("author");
            uCode = getIntent().getStringExtra("code");   //机构
            if (getIntent().getIntExtra("sessionType", 3) != 0 && getIntent().getIntExtra("sessionType", 3) == 4) {
                type = "org";
                userCode.setText("机构号：" + uCode);
            }
            spaceid = friendId;
            getCircleFriend();
        } else if (from.equals("FriendResActivity")) {
            function = 0;
            sessionName = getIntent().getStringExtra("name");
            String iconUrl = getIntent().getStringExtra("icon");
            uCode = getIntent().getStringExtra("code");
            name.setText(sessionName);
            friend_info_name2.setText(sessionName);
            tv_remarks_name2.setText(sessionName);
            userCode.setText("机构号：" + uCode);
            if (getIntent().getStringExtra("type") != null && getIntent().getStringExtra("type").equals("org")) {
                friend_info_studyspace_tv.setText("机构主页");
                function = 2;
                friendId = getIntent().getStringExtra("id");
                spaceid = getIntent().getStringExtra("spaceId");
                setHeadIcon(friendId, 1);
                getCircleFriend();
//                loading.showLoading(true);
                isJoin();
            } else if (getIntent().getStringExtra("type") != null && getIntent().getStringExtra("type").equals("group")) {
//                Log.i("info", "群组主页的头像url:" + iconUrl);
                friend_info_studyspace_tv.setText("空间主页");
                Picasso.with(this).load(iconUrl).placeholder(R.mipmap.ic_qunzu_xuanren).error(R.mipmap.ic_qunzu_xuanren).config(Bitmap.Config.RGB_565).into(icon);
                userCode.setVisibility(View.INVISIBLE);
                function = 2;
                friendId = getIntent().getStringExtra("uuid");
                spaceid = friendId;
                setHeadIcon(friendId, 2);
                getCircleFriend();
                isJoin();
                loading.showLoading(true);
            } else if (type != null && !TextUtils.isEmpty(type) && type.equals("person")) {
                friendId = getIntent().getStringExtra("id");
                spaceid = getIntent().getStringExtra("spaceId");
                function = 0;
//                isFriend();
            }
            Log.i("info", "搜索页传来的，type:" + type + ",friendId:" + friendId + ",spaceId:" + spaceid);
        } else if (from.equals("EmotionMainFragment")) {
            function = 0;
            type = getIntent().getStringExtra("type");
            String uid = sp.getString(Constant.ID_USER, "");
            sessionName = "-1";
            uCode = "-1";
            friendId = uid;
            getMyInfo(uid, false);
//            isFriend();
            setHeadIcon(uid, 0);
        } else if (from.equals("EmotionMainFragment2")) {
            function = 0;
            type = getIntent().getStringExtra("type");
            friendId = getIntent().getStringExtra("friendId");
//            isFriend();
            setHeadIcon(friendId, 0);
//            getMyInfo(friendId, false);
//            Log.i("info", "聊天界面，type:" + type+",friendId:"+friendId+",spaceId:"+spaceid);
        } else if (from.equals("ChatInformationActivity")) {
            function = 0;
            type = getIntent().getStringExtra("type");
            friendId = getIntent().getStringExtra("targetId");
            if (type.equals("org")) {
                friend_info_studyspace_tv.setText("机构主页");
            }
//            isFriend();
            setHeadIcon(friendId, 0);
//            getMyInfo(friendId, false);
        } else if (from.equals("PhoneContactActivity")) {
            function = 0;
            type = getIntent().getStringExtra("type");
            friendId = getIntent().getStringExtra("id");
            uCode = getIntent().getStringExtra("code");
            sessionName = getIntent().getStringExtra("name");
            name.setText(sessionName);
            friend_info_name2.setText(sessionName);
            tv_remarks_name2.setText(sessionName);
            userCode.setText("用户号：" + uCode);
//            isFriend();
        }
        if (function == 0) {
            loadSpaceId();//获取spaceid
        }
        initLinstener();
        if (null != type && !type.equals("group") && !from.equals("GroupListActivity")) {
            getSpaceInfo();
        } else {
            findSpaceInfo();
        }
        if (null != type && type.equals("group")) {
            function = 2;
        } else if (null != type && !type.equals("group") && !from.equals("GroupListActivity") && !from.equals("FriendResActivity")) {
            function = 0;
        }
        if (null != type && type.equals("org")) {
            friend_chat_file.setVisibility(View.GONE);
        }
        switch (function) {
            case 0:     //个人
                if (getIntent().getStringExtra("type") != null && getIntent().getStringExtra("type").equals("org")) {
                    friend_info_studyspace_tv.setText("机构主页");
                }
                isFriend();
                break;

            case 1:     //机构
                break;

            case 2:     //群组
                if (null != type && type.equals("group")) {
                    userCode.setVisibility(View.INVISIBLE);
                    name.setVisibility(View.GONE);
                    friend_info_name2.setVisibility(View.VISIBLE);
                    friend_info_name2.setText(sessionName);
                }
                name.setText(sessionName);
                friend_info_studyspace_tv.setText("空间主页");
                if (userId.equals(author))   //判断当前登录用户是否为该群群主
                {
                    friend_chat_file.setVisibility(View.VISIBLE);
                    tv_remarks_name.setText("修改群组名称");
                } else {
                    friend_chat_file.setVisibility(View.GONE);
                }
                Picasso.with(mContext).load(sessionIcon).resize((int) mContext.getResources().getDimension(R.dimen.view_43),
                        (int) mContext.getResources().getDimension(R.dimen.view_43))
                        .placeholder(R.mipmap.ic_qunzu_xuanren).error(R.mipmap.ic_qunzu_xuanren).config(Bitmap.Config.RGB_565).into(icon);
                titleBar.setTitle("空间资料");

//                loading.hideLoading(true);
                name.setText(sessionName);
                friend_info_name2.setText(sessionName);
                tv_remarks_name2.setText(sessionName);
                ll_add_cancel_care.setVisibility(View.GONE);
                gender.setVisibility(View.INVISIBLE);
                delete.setBackgroundResource(R.drawable.friend_info_exit_group_selector);
                break;
        }
    }

    private void initView() {
        titleBar = (TitleBar) findViewById(R.id.friend_info_title_bar);
        titleBar.setTitle("好友资料");
        titleBar.setLeftIcon(R.mipmap.ic_fanhui, "返回");

        study_space = (RelativeLayout) findViewById(R.id.friend_info_studyspace);
        chat = (Button) findViewById(R.id.friend_info_chat_btn);
        delete = (Button) findViewById(R.id.friend_info_delete_friend);
        name = (TextView) findViewById(R.id.friend_info_name);
        userCode = (TextView) findViewById(R.id.friend_info_userCode);
        icon = (ShapeImageView) findViewById(R.id.friend_info_icon);
        gender = (ImageView) findViewById(R.id.friend_info_gender);
        mBtn = (LinearLayout) findViewById(R.id.friend_btn_layout);
        person = (Button) findViewById(R.id.person_btn);
        groupOrorg = (Button) findViewById(R.id.group_org_btn);
        img_add_care = (ImageView) findViewById(R.id.add_cancel_care);
        ll_add_cancel_care = (LinearLayout) findViewById(R.id.ll_add_cancel_care);
        friend_chat_file = (RelativeLayout) super.findViewById(R.id.friend_chat_file);
        tv_remarks_name = (TextView) super.findViewById(R.id.tv_remarks_name);
        tv_remarks_name2 = (TextView) super.findViewById(R.id.tv_remarks_name2);
        friend_info_name2 = (TextView) super.findViewById(R.id.friend_info_name2);
        friend_info_studyspace_tv = (TextView) findViewById(R.id.friend_info_studyspace_tv);
        friend_info_resources = (LinearLayout) findViewById(R.id.friend_info_resources);
        img_add_care.setOnClickListener(this);

        httpManager = new HttpManager();
        loading = new Loading(findViewById(R.id.loading_layout), (LinearLayout) findViewById(R.id.friend_info_noLoading));
        loading.showLoading(true);

        study_space.setOnClickListener(this);
        delete.setOnClickListener(this);
        friend_chat_file.setOnClickListener(this);
    }

    private void initData() {
        if (!from.equals("ContactsActivity")) {
            if (isFriend) {
                titleBar.setTitle("好友资料");
                setHeadIcon(friendId, 0);
            } else {
                if (function == 0) {
                    getMyInfo(friendId, false);
                }
                friend_chat_file.setVisibility(View.GONE);
                int utype = 0;
                if (type.equals("person")) {
                    utype = 0;
                    titleBar.setTitle("个人资料");
                    mBtn.setVisibility(View.GONE);
                    groupOrorg.setVisibility(View.GONE);
                    person.setVisibility(View.GONE);
                    if (from.equals("FriendResActivity") || from.equals("PhoneContactActivity") || from.equals("EmotionMainFragment2") || from.equals("ChatInformationActivity")) {
                        String id = sp.getString(Constant.ID_USER, "");
                        if (id.equals(friendId)) {
                            if (isFriend) {
                                titleBar.setTitle("好友资料");
                            } else {
                                titleBar.setTitle("个人资料");
                            }
                            mBtn.setVisibility(View.GONE);
                            groupOrorg.setVisibility(View.GONE);
                            person.setVisibility(View.GONE);
                        } else {
                            if (isFriend) {
                                titleBar.setTitle("好友资料");
                            } else {
                                titleBar.setTitle("个人资料");
                            }
                            mBtn.setVisibility(View.GONE);
                            groupOrorg.setVisibility(View.GONE);
                            person.setVisibility(View.VISIBLE);
                        }
                    }
                } else if (type.equals("group")) {
                    utype = 1;
                    titleBar.setTitle("群组资料");
                    mBtn.setVisibility(View.GONE);
                    person.setVisibility(View.GONE);
                    groupOrorg.setVisibility(View.VISIBLE);
                } else if (type.equals("org")) {
                    utype = 2;
                    titleBar.setTitle("机构资料");
                    mBtn.setVisibility(View.GONE);
                    person.setVisibility(View.GONE);
//                    groupOrorg.setVisibility(View.VISIBLE);
                    person.setVisibility(View.VISIBLE);
                    groupOrorg.setVisibility(View.GONE);
                }
                setHeadIcon(friendId, utype);
            }
        }

        if (uCode != null && sessionName != null && !uCode.equals("") && !sessionName.equals("")) {
            loading.hideLoading(true);
        }

        if (isFriend) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    result = httpManager.getUserRemarkName(userId, friendId);
                    if (result.equals("404")) {
                        handler.sendEmptyMessage(GET_FRIENDINFO_FAIL);
                    } else {
                        handler.sendEmptyMessage(GET_FRIENDINFO_SUCCEED);
                    }
                }
            }).start();
        }
    }


    //设置头像
    private void setHeadIcon(final String userId, final int type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String url = httpManager.getUserIconUrl(userId);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        setImage(url, type, icon);
                    }
                });
            }
        }).start();
    }

    private void setImage(String iconUrl, int type, ImageView icon) {
        //ChatType  0 单聊，1 机构，2 空间(群组)
        if (iconUrl == null || iconUrl.equals("")) {
            iconUrl = "null";
        }
        if (type == 0) {
            Picasso.with(mContext).load(iconUrl).resize((int) mContext.getResources().getDimension(R.dimen.view_43),
                    (int) mContext.getResources().getDimension(R.dimen.view_43))
                    .placeholder(R.mipmap.ic_geren_xuanren).error(R.mipmap.ic_geren_xuanren).config(Bitmap.Config.RGB_565).into(icon);
        } else if (type == 1) {
            Picasso.with(mContext).load(iconUrl).resize((int) mContext.getResources().getDimension(R.dimen.view_43),
                    (int) mContext.getResources().getDimension(R.dimen.view_43))
                    .placeholder(R.mipmap.ic_jigoumoren_wangpan).error(R.mipmap.ic_jigoumoren_wangpan).config(Bitmap.Config.RGB_565).into(icon);
        } else if (type == 2) {
            Picasso.with(mContext).load(iconUrl).resize((int) mContext.getResources().getDimension(R.dimen.view_43),
                    (int) mContext.getResources().getDimension(R.dimen.view_43))
                    .placeholder(R.mipmap.ic_qunzu_xuanren).error(R.mipmap.ic_qunzu_xuanren).config(Bitmap.Config.RGB_565).into(icon);
        }
    }

    private void initLinstener() {
        titleBar.setTitleClickListener(this);
        study_space.setOnClickListener(this);
        chat.setOnClickListener(this);
        delete.setOnClickListener(this);
        person.setOnClickListener(this);
        groupOrorg.setOnClickListener(this);
    }

    @Override
    public void leftClick() {
        close();
    }

    private void close() {
        Intent intent = new Intent();
        if (function == 0) {
            setResult(RESULT_OK, intent);
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        close();
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
            case R.id.friend_info_chat_btn:
                if (function == 0) {
                    Intent startChat = new Intent(UIUtils.getContext(), ChatActivity.class);
                    startChat.putExtra("from", "FriendInformationActivity");
                    startChat.putExtra("targetId", friendId);
                    startChat.putExtra("type", Conversation.ConversationType.PRIVATE);
                    startChat.putExtra("name", sessionName);
                    startChat.putExtra("iconUrl", iconUrl);
                    startActivity(startChat);
                } else if (function == 2) {
                    Intent intent = new Intent(UIUtils.getContext(), GroupChatActivity.class);
                    intent.putExtra("from", "FriendInformationActivity");
                    intent.putExtra("sessionName", sessionName);
                    intent.putExtra("sessionId", spaceid);
                    intent.putExtra("author", author);
                    intent.putExtra("sessionIcon", sessionIcon);
                    startActivity(intent);
                }
                finish();
                break;
            case R.id.friend_info_delete_friend:
                if (function == 0) {
                    Intent intent = new Intent(mContext, IsWifiActivity.class);
                    intent.putExtra("content", "删除好友？");
                    startActivityForResult(intent, REQ_DEL_FRI);
                } else if (function == 2) {
//                    进入主页 friendId:5b41f54348da4eaea45cd0deb61bad8b,spaceid:586c5bdebde84ef39bb73777c9fe34be,type:org,是否允许关注:true,名称:null
//                    进入主页 friendId:586c5bdebde84ef39bb73777c9fe34be,spaceid:586c5bdebde84ef39bb73777c9fe34be,type:org,是否允许关注:false,名称:心意答中学
                    Intent intent = new Intent(mContext, IsWifiActivity.class);
                    intent.putExtra("content", "退出空间？");
                    startActivityForResult(intent, 10);
                }

                break;
            case R.id.friend_info_studyspace:   //进入主页
//                loading.hideLoading(false);
//                Intent intent = new Intent(mContext, HomePageActivity.class);
////                Log.i("TAG", "进入主页 friendId:" + friendId + ",spaceid:" + spaceid + ",type:" + type + ",是否允许关注:" + focusMe + ",名称:" + sessionName);
//                intent.putExtra("visitHomepage", focusMe);
//                intent.putExtra(Constant.HOME_PAGE_TITLE, sessionName);
//                if (type.equals("person")) {
//                    intent.putExtra(Constant.ID_USERTYPE, Constant.TYPE_PRIVATE);
//                    intent.putExtra(Constant.ID_USER, friendId);
//                    intent.putExtra(Constant.ID_SPACE, spaceid);
//                } else if (type.equals("org")) {
//                    intent.putExtra(Constant.ID_USERTYPE, Constant.TYPE_GROUP);
//                    if (from.equals("GroupListActivity")) {
//                        intent.putExtra(Constant.ID_USER, author);
//                    } else {
//                        intent.putExtra(Constant.ID_USER, friendId);
//                    }
//
//                    intent.putExtra(Constant.ID_SPACE, spaceid);
//                } else if (type.equals("group")) {
//                    intent.putExtra(Constant.ID_USERTYPE, Constant.TYPE_SPACE);
//                    intent.putExtra(Constant.ID_USER, userId);
//                    intent.putExtra(Constant.ID_SPACE, friendId);
//                    intent.putExtra(Constant.HOME_PAGE_TITLE, sessionName);
//                }
//                startActivity(intent);
                loading.showLoading(true);
                userSelectSpaceRelationship();
                break;
            case R.id.person_btn:
                if (type.equals("org")) {
                    Toast.makeText(UIUtils.getContext(), "暂不开放个人添加机构好友", Toast.LENGTH_SHORT).show();
                } else {
                    Intent toSendRe = new Intent(UIUtils.getContext(), SendFriendRequestActivity.class);
                    toSendRe.putExtra("oppositeId", friendId);
                    startActivityForResult(toSendRe, REQ_ADD_FRI);
                }
                break;
            case R.id.group_org_btn:
//                Log.i("info", "申请加入机构或群组：userId=" + userId + ";spaceId=" + spaceId + ";targetId=" + targetId);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        execute_join(userId, spaceId, spaceid);
                    }
                }).start();
                break;
            case R.id.add_cancel_care:
                if (spaceid.equals("") || System.currentTimeMillis() - last_click < 3000) {
                    return;
                }
                last_click = System.currentTimeMillis();
                if (iscare) {
                    cancelCare();
                    img_add_care.setImageResource(R.mipmap.bt_close);
                    Toast.makeText(mContext, "已取消关注", Toast.LENGTH_SHORT).show();
                } else {
                    addCare();
                    img_add_care.setImageResource(R.mipmap.bt_open);
                    Toast.makeText(mContext, "已添加关注", Toast.LENGTH_SHORT).show();
                }
                iscare = !iscare;
                break;
            case R.id.friend_chat_file: //修改备注名或群组名称
                Intent startUpdate = new Intent(UIUtils.getContext(), SendFriendRequestActivity.class);
                startUpdate.putExtra("from", "FriendInformationActivity");
                startUpdate.putExtra("friendId", friendId);
                switch (function) {
                    case 0:     //个人
                        startUpdate.putExtra("friendName", name.getText().toString());
                        startUpdate.putExtra("sessionType", 1);
                        startUpdate.putExtra("type", type);
                        break;

                    case 1:     //机构
                        break;

                    case 2:     //群组
                        startUpdate.putExtra("type", type);
                        if (type.equals("group")) {
                            startUpdate.putExtra("sessionType", 3);
                        } else if (type.equals("org")) {
                            startUpdate.putExtra("sessionType", 4);
                        }
                        startUpdate.putExtra("sessionName", sessionName);
                        break;
                }
                startActivityForResult(startUpdate, 11);
                break;
        }
    }

    //查看空间信息（人）
    public void getSpaceInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (NetWorkUtils.isNetworkAvailable(UIUtils.getContext())) {
                    String result = httpManager.findSpaceInfo(false, friendId, type);
                    if (result != null && !TextUtils.isEmpty(result) && !result.equals("404")) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            SpaceInfo spaceInfo = null;
                            spaceInfo = JsonUtils.StartSpaceInfoJson(result);
                            if (jsonObject.getString("type") != null && !TextUtils.isEmpty(jsonObject.getString("type"))) {
                                type = jsonObject.getString("type");
                                searchMyresource = spaceInfo.getAuthority().isSearchMyresource();
                                focusMe = spaceInfo.getAuthority().isFocusMe();
                            }
                            handler.sendEmptyMessage(FINDE_SPACE_SUCCEED);
//                            Log.i("ContactsActivity", "是否为机构是否为机构是否为机构是否为机构1111：" + type+"///////////"+searchMyresource);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        handler.sendEmptyMessage(FINDE_SPACE_FAIL);
                    }
                } else {
                    handler.sendEmptyMessage(NO_INTERNET);
                }
            }
        }).start();
    }

    /**
     * 查看空间信息（获取群组空间管理员，空间）
     */
    public void findSpaceInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (NetWorkUtils.isNetworkAvailable(UIUtils.getContext())) {
                    String result = httpManager.findSpaceInfo3(false, friendId);
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        author = jsonObject.optString("ownerId");
                        type = jsonObject.optString("type");
                        sessionName = jsonObject.optString("name");
                        SpaceInfo spaceInfo = null;
                        spaceInfo = JsonUtils.StartSpaceInfoJson(result);
                        focusMe = spaceInfo.getAuthority().isFocusMe();
                        searchMyresource = spaceInfo.getAuthority().isSearchMyresource();
                        handler.sendEmptyMessage(FINDE_SPACE_SUCCEED);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    //查看主页中的发布列表
    public void getCircleFriend() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (NetWorkUtils.isNetworkAvailable(UIUtils.getContext())) {
                    String result = httpManager.findHomepageList(spaceid, 1, 1);
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

//                            Log.i("info", "获取的动态222: " + urlLisst.toString());
                            handler.sendEmptyMessage(FINDE_CRICLE_FRIEND_SUCCEED);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
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

    //zyx   申请加入机构或者群组
    public void execute_join(String userId, String myspaceId, String targetspaceId) {
        OkHttpClient client = new OkHttpClient();
        String url = String.format(Constant.JOIN_ORG_GROUP, targetspaceId, userId, myspaceId);
        Log.i("info", "申请加入机构或者群组的url:" + url);

        RequestBody body = new FormBody.Builder()
                .add("memberId", userId)
                .add("spaceId", targetspaceId)
                .add("myGroupId", myspaceId)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("info", "网络请求失败：" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int code = response.code();
                String body = response.body().string();
                Log.i("info", "点击申请加入之后：code=" + code + "，body=" + body);
                handler.sendEmptyMessage(101);
            }
        });
    }

    //发送删除好友消息
    private void sendDelMessage() {
//        Log.i("info", "发送删除好友消息");
        String uname = sp.getString(Constant.USER_NAME, "");
        String uid = sp.getString(Constant.ID_USER, "");
        JSONObject json = new JSONObject();
        try {
            json.put("MessageType", 7);
            json.put("ContentType", 1);
            json.put("ChatType", 0);
            json.put("Content", "");
            json.put("FontSize", 14);
            json.put("FontStyle", 0);
            json.put("FontColor", 0);
            json.put("BulletinID", "");
            json.put("BulletinContent", "");
            json.put("requestName", uname);
            json.put("requestRemark", "");
            json.put("requestGroupId", "");
            json.put("FileID", "");
            json.put("FileName", "");
            json.put("CreateTime", System.currentTimeMillis());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String message = json.toString();

        UserInfo userInfo = new UserInfo(uid, uname, null);
        final TextMessage textMessage = TextMessage.obtain(message);
        textMessage.setUserInfo(userInfo);

        RongIMClient.getInstance().sendMessage(Conversation.ConversationType.PRIVATE, friendId,
                textMessage, null, null, new RongIMClient.SendMessageCallback() {
                    @Override
                    public void onSuccess(Integer integer) {

                    }

                    @Override
                    public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {

                    }
                }, new RongIMClient.ResultCallback<io.rong.imlib.model.Message>() {
                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {

                    }

                    @Override
                    public void onSuccess(io.rong.imlib.model.Message message) {

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 11) {
            sessionName = data.getStringExtra("remarksName");
//            uName = sessionName;
            name.setText(sessionName);
            friend_info_name2.setText(sessionName);
            tv_remarks_name2.setText(sessionName);

            Intent intent = new Intent(ContactsActivity.UPDATE_FRIEND_NAME);
            intent.putExtra("targetId", data.getStringExtra("targetId"));
            intent.putExtra("targetName", sessionName);
            FriendInformationActivity.this.sendBroadcast(intent);
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_DEL_FRI) {
                String uid = sp.getString(Constant.ID_USER, "");
                if (friendId != null && !friendId.equals("")) {
                    delFri(uid, friendId);
                    delConversation(friendId, Conversation.ConversationType.PRIVATE);
                    sendDelMessage();
                }
            } else if (requestCode == REQ_ADD_FRI) {
                finish();
            }
        }

        if (requestCode == 10 && resultCode == RESULT_OK) {   //退空间
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String userId = sp.getString(Constant.ID_USER, "");
                    String name = sp.getString("name", "");
                    List<String> userIdList = new ArrayList<>();
                    List<String> userNameList = new ArrayList<>();
                    userIdList.add(userId);
                    userNameList.add(name);
                    result = httpManager.exitSpace2(userId, spaceid);
                    handler.sendEmptyMessage(EXIT_GROUP_SUCCEED);
                }
            }).start();
        }
    }

    //删除会话
    private void delConversation(final String fid, final Conversation.ConversationType type) {
        RongIMClient.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                if (conversations != null && conversations.size() > 0) {
                    for (int i = 0; i < conversations.size(); i++) {
                        Conversation conversation = conversations.get(i);
                        String id = conversation.getTargetId();
                        if (fid.equals(id) && conversation.getConversationType() == type) {
                            RongIMClient.getInstance().removeConversation(conversation.getConversationType(), id, new RongIMClient.ResultCallback<Boolean>() {
                                @Override
                                public void onSuccess(Boolean aBoolean) {
                                }

                                @Override
                                public void onError(RongIMClient.ErrorCode errorCode) {

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

        Conversation.ConversationType conversationType;
        if (function == 0) {
            conversationType = Conversation.ConversationType.PRIVATE;
        } else {
            return;
        }
        //清空聊天信息
        RongIMClient.getInstance().clearMessages(conversationType, fid);
    }

    //删除好友
    private void delFri(final String userId, final String friendId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int ret = httpManager.delFri(userId, friendId);
                Message msg = new Message();
                msg.what = 3;
                msg.arg1 = ret;
                handler.sendMessage(msg);
            }
        }).start();
    }

    //是否为好友
    private void isFriend() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                result = httpManager.isFriend(sp.getString(Constant.ID_USER, ""), friendId);
                if (result != null && !result.equals("404")) {
                    if (result.equals("true")) {
                        isFriend = true;
                    } else if (result.equals("false")) {
                        isFriend = false;
                    }
                    handler.sendEmptyMessage(4);
                }
            }
        }).start();
    }

    //取消关注
    private void cancelCare() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sid = sp.getString(Constant.ID_SPACE, "");
                OkHttpClient.Builder builder = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS);
                client = builder.build();
                Request request = new Request.Builder()
                        .url(String.format(Constant.STUDY_CANCEL_CARE, sid, spaceid, "concern"))
                        .delete()
                        .build();
                //Log.i("info", "取消关注参数=" + String.format(Constant.STUDY_CANCEL_CARE, sid, spaceid, "concern"));
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    Message message = new Message();
                    message.what = 5;
                    message.arg1 = response.code();
                    message.obj = response.body().string();
                    handler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                    //mHandler.sendEmptyMessage(400);
                }
            }
        }).start();
    }

    //添加关注
    private void addCare() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                String json = "";
                JSONObject jsonObject = new JSONObject();
                try {
                    String sid = sp.getString(Constant.ID_SPACE, "");
                    jsonObject.put("mainSpaceId", sid);
                    jsonObject.put("subSpaceId", spaceid);
                    jsonObject.put("relationType", "concern");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                json = jsonObject.toString();
                //Log.i("info", "添加关注参数=" + json);
                RequestBody body = RequestBody.create(JSON, json);
                OkHttpClient.Builder builder = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS);
                client = builder.build();
                Request request = new Request.Builder()
                        .url(Constant.STUDY_ADD_CARE)
                        .post(body)
                        .build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    Message message = new Message();
                    message.what = 6;
                    message.arg1 = response.code();
                    message.obj = response.body().string();
                    handler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                    //mHandler.sendEmptyMessage(400);
                }
            }
        }).start();
    }

    //判断是否关注
    private void getSpaceRelation(final String sid, final String spaceid) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(String.format(Constant.STUDY_SELECT_SPACE_RELATIONSHIP, sid, spaceid))
                        .build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        Message msg = new Message();
                        msg.what = 7;
                        msg.arg1 = response.code();
                        msg.obj = response.body().string();
                        handler.sendMessage(msg);
                    } else {
                        //mHandler.sendEmptyMessage(404);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
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
                    String result = httpManager.userSelectSpaceRelationship(userId, spaceid);
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

    //获取spaceid
    private void loadSpaceId() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(Constant.GETSPACEID_URL + friendId)
                        .get()
                        .build();
                Log.i("info", "获取spaceid " + Constant.GETSPACEID_URL + friendId);
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //mHandler.sendEmptyMessage(404);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Message message = new Message();
                        message.what = 8;
                        message.arg1 = response.code();
                        message.obj = response.body().string();
                        handler.sendMessage(message);
                    }
                });
            }
        }).start();
    }

    //根据用户ID查找用户信息
    public void getMyInfo(final String userId, final boolean needicon) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                userinfo = httpManager.getUserInfo(userId, needicon);
                handler.sendEmptyMessage(11);
            }
        }).start();
    }

    //判断是否加入某一空间
    public void isJoin() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (NetWorkUtils.isNetworkAvailable(UIUtils.getContext())) {
                    String result = httpManager.isJoin(userId, spaceid);
                    if (result != null && !result.equals("404")) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            inTheOrg = jsonObject.optBoolean("isJoin");
                            Log.i("info", "判断是否加入某一空间=" + jsonObject.optBoolean("isJoin"));
                            handler.sendEmptyMessage(WHETHER_IN_THE_SPACE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        handler.sendEmptyMessage(WHETHER_IN_THE_SPACE_FALL);
                    }
                } else {
                    handler.sendEmptyMessage(NO_INTERNET);
                }
            }
        }).start();
    }

    //查看朋友圈中的发布列表(移动端)

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("FriendInformationActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("FriendInformationActivity");
        MobclickAgent.onPause(this);
    }
}

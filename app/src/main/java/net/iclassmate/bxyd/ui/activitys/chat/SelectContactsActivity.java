package net.iclassmate.bxyd.ui.activitys.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.adapter.SeleContAdapter;
import net.iclassmate.bxyd.bean.contacts.FriendInfo;
import net.iclassmate.bxyd.bean.message.Extra;
import net.iclassmate.bxyd.bean.study.FileDirList;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.ui.activitys.constacts.GroupListActivity;
import net.iclassmate.bxyd.ui.fragment.emotion.EmotionDisFragment;
import net.iclassmate.bxyd.utils.FileUtils;
import net.iclassmate.bxyd.utils.HttpManager;
import net.iclassmate.bxyd.utils.JsonUtils;
import net.iclassmate.bxyd.utils.NetWorkUtils;
import net.iclassmate.bxyd.utils.RongIMClientUtils;
import net.iclassmate.bxyd.utils.UIUtils;
import net.iclassmate.bxyd.view.Loading;
import net.iclassmate.bxyd.view.TitleBar;
import net.iclassmate.bxyd.view.study.ShapeImageView;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ImageMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;

public class SelectContactsActivity extends Activity implements TitleBar.TitleOnClickListener, View.OnClickListener {
    private Context mContext;
    private TitleBar titleBar;
    private ListView listView;
    private List<Object> list;
    private List<FriendInfo> listMsg;
    private List<FriendInfo> listSelect;
    private List<String> userIdList;
    private List<String> userNameList;
    private List<Group> groupList;
    private List<FileDirList> FBlistSelected;
    private SeleContAdapter adapter;
    private LinearLayout linear_select;
    private RelativeLayout relative_group;
    private HorizontalScrollView horizontalScrollView;

    private SharedPreferences sp;
    private char sortKey;
    private String userId, name;
    private String groupName;
    private String result;
    private FriendInfo friendInfo;
    private HttpManager httpManager;
    private Loading loading;
    private String from;
    private String selectName;

    private String targetId;
    private Conversation.ConversationType type;
    private List<String> idList;
    private int sessionType; //1单聊   2群聊   3群组   4机构
    private String discussionName;
    public static final String NEW_MESSAGE = "NEW_MESSAGE";

    public final static int FIND_FRIENDS_SUCCEED = 1;
    public final static int FIND_FRIENDS_FAIL = 2;
    public final static int CREAT_GROUP_SUCCEED = 3;
    public final static int CREAT_GROUP_FAIL = 4;
    private static final int NO_INTERNET = 0;    //没网络
    private final static int ADD_GROUP_SUCCEED = 5;
    private final static int ADD_GROUP_FAIL = 6;
    public final static int SEND_MESSAGE = 7;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SEND_MESSAGE:

                    break;
                case FIND_FRIENDS_SUCCEED:
                    Log.i("TAG", "获取的好友列表：" + result);
                    loading.hideLoading(false);
                    listMsg = JsonUtils.jsonFriendInfo(result);
                    if (listMsg == null) {
                        return;
                    }
                    for (int j = 0; j < listMsg.size(); j++) {
                        FriendInfo friendInfo = listMsg.get(j);
                        if(idList != null && idList.contains(friendInfo.getFriendId())){
                            friendInfo.setMember(true);
                        }
                        if(null != getIntent().getStringExtra("friendId") && getIntent().getStringExtra("friendId").equals(friendInfo.getFriendId())){
                            friendInfo.setMember(true);
                        }
                        String remark = friendInfo.getRemark();
                        if (remark == null || remark.equals("") || remark.equals("null")) {
                            remark = friendInfo.getUserName();
                        }
                        char ch = ' ';
                        if (isEnglish(remark)) {
                            //字母
                            ch = remark.toUpperCase().charAt(0);
                        } else {
                            //中文
                            char[] c = remark.toCharArray();
                            HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
                            // UPPERCASE：大写  (ZHONG)LOWERCASE：小写  (zhong)
                            format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
                            format.setToneType(HanyuPinyinToneType.WITH_TONE_MARK);
                            format.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);
                            try {
                                if (c != null && c.length > 0) {
                                    String[] pinyin = PinyinHelper.toHanyuPinyinStringArray(c[0], format);
                                    if (pinyin.length != 0) {
                                        char[] cc = pinyin[0].toCharArray();
                                        ch = cc[0];
                                    }
                                } else {
                                    friendInfo.sortKey = '#';
                                }
                            } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                                badHanyuPinyinOutputFormatCombination.printStackTrace();
                            }
                        }
                        if (ch >= 65 && ch <= 90) {//大写字母
                            friendInfo.sortKey = ch;
                        } else {
                            friendInfo.sortKey = '#';
                        }

                        if (sortKey != friendInfo.sortKey) {
                            sortKey = friendInfo.sortKey;
                            friendInfo.isHead = true;
                        }
                        for (int k = j - 1; k >= 0; k--) {
                            if (ch == listMsg.get(k).sortKey) {
                                friendInfo.isHead = false;
                            }
                        }

                        listMsg.set(j, friendInfo);

                    }
                    for (int j = 0; j < listMsg.size(); j++) {
                        for (int k = j + 1; k < listMsg.size(); k++) {
                            FriendInfo f1 = listMsg.get(j);
                            FriendInfo f2 = listMsg.get(k);
                            if (f1.sortKey > f2.sortKey) {
                                listMsg.set(k, f1);
                                listMsg.set(j, f2);
                            }
                        }
                        FriendInfo message = listMsg.get(j);
                        if (!list.contains(message)) {
                            list.add(message);
                        }
                    }
//                    adapter.notifyDataSetChanged();
//                    listView.setAdapter(adapter);
                    break;
                case FIND_FRIENDS_FAIL:
                    loading.hideLoading(false);
                    Toast.makeText(UIUtils.getContext(), "好友列表加载失败", Toast.LENGTH_SHORT).show();
                    break;
                case CREAT_GROUP_SUCCEED:
//                    Log.i("TAG", "服务器返回群聊：" + result);
                    try {
                        JSONObject object = new JSONObject(result);
                        String author = object.getString("author");
                        final String sessionId = object.getString("sessionId");
                        //发送广播
                       /* Intent intent=new Intent(EmotionDisFragment.NEW_MESSAGE);
                        Bundle bundle=new Bundle();
                        bundle.putParcelable("message",message);
                        intent.putExtras(bundle);
                        sendBroadcast(intent);*/
                        Group group = new Group(sessionId, groupName, null);
                        groupList.add(group);
//                        sendCmd(sessionId);
                        //发送创建群聊同步通知
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                RongIMClientUtils.sendCommend(Extra.Operation.SYNCH_GROUP, sessionId, groupName, Conversation.ConversationType.GROUP, "");
                            }
                        }).start();

                        Intent startGroup = new Intent(UIUtils.getContext(), DiscussionActivity.class);
//                        bundle.putParcelable("discussion",discussion);
                        startGroup.putExtra("sessionId", sessionId);
                        startGroup.putExtra("sessionName", groupName);
                        startGroup.putExtra("author", author);
                        startGroup.putExtra("from", "SelectContactsActivity");
                        startActivity(startGroup);
                        if (from.equals("person")) {
                            finish();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    loading.hideLoading(false);
                    break;
                case CREAT_GROUP_FAIL:
                    break;

                case NO_INTERNET:
                    loading.hideLoading(false);
                    Toast.makeText(UIUtils.getContext(), "您没有连接网络，请连接后重试！", Toast.LENGTH_SHORT).show();
                    break;

                case ADD_GROUP_SUCCEED: //加入群组成功
                    loading.hideLoading(false);
                    Toast.makeText(UIUtils.getContext(), "加入群成功！", Toast.LENGTH_SHORT).show();
                    //改群聊默认名称
                    if(sessionType == 2){
                        discussionName = discussionName.substring(0, discussionName.length()-1);
                        if(NetWorkUtils.isNetworkAvailable(UIUtils.getContext())) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    httpManager.updateGroupName(targetId, discussionName, 0);
                                }
                            }).start();
                        }
                    }
                    Intent intent = new Intent();
                    intent.putExtra("selectName", selectName);
                    if(sessionType == 2){
                        intent.putExtra("discussionName", discussionName);
                    }
                    SelectContactsActivity.this.setResult(13, intent);
                    SelectContactsActivity.this.finish();
                    break;

                case ADD_GROUP_FAIL:    //加入群组失败
                    loading.hideLoading(false);
                    Toast.makeText(UIUtils.getContext(), "加入群失败，请重试！", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void sendCmd(final String sessionId) {
        UserInfo userInfo = new UserInfo(sessionId, groupName, null);
        final TextMessage textMessage = TextMessage.obtain("");
        textMessage.setUserInfo(userInfo);
        RongIMClient.getInstance().sendMessage(Conversation.ConversationType.PRIVATE, sessionId, textMessage, null, null, new RongIMClient.SendMessageCallback() {
            @Override
            public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {
                Toast.makeText(UIUtils.getContext(), "失败" + errorCode, Toast.LENGTH_SHORT).show();
//                Log.i("TAG", "sendCmd失败");
            }

            @Override
            public void onSuccess(Integer integer) {
                Toast.makeText(UIUtils.getContext(), "成功", Toast.LENGTH_SHORT).show();
//                Log.i("TAG", "sendCmd成功");
            }
        }, new RongIMClient.ResultCallback<io.rong.imlib.model.Message>() {
            @Override
            public void onSuccess(io.rong.imlib.model.Message message) {
                Intent intent = new Intent(EmotionDisFragment.NEW_MESSAGE);
                Bundle bundle = new Bundle();
                bundle.putParcelable("message", message);
                intent.putExtras(bundle);
                sendBroadcast(intent);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }


    /**
     * 是否是英文
     *
     * @param charaString
     * @return
     */
    public static boolean isEnglish(String charaString) {
        boolean result = false;
        if (charaString != null && charaString.length() > 0) {
            char ch = charaString.charAt(0);
            if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
                result = true;
            }
        }
        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contacts);
        mContext = this;
        Intent intent = getIntent();
        initView();
        FBlistSelected = (List<FileDirList>) intent.getSerializableExtra("NetFile");
        initData();
        initListener();

        Intent intent1 = getIntent();
        String from = intent1.getStringExtra("from");
        if (from.equals("NetFile")) {
            relative_group.setVisibility(View.VISIBLE);
        }
    }

    private void initView() {
        titleBar = (TitleBar) findViewById(R.id.select_contacts_title_bar);

        titleBar.setLeftIcon(R.mipmap.ic_fanhui, "取消");
        titleBar.setRightIcon("确定");
        titleBar.setTitle("选择联系人");
        titleBar.setTitleClickListener(this);
        listView = (ListView) findViewById(R.id.sele_cont_listview);
        linear_select = (LinearLayout) findViewById(R.id.sele_cont_container_linear);
        relative_group = (RelativeLayout) findViewById(R.id.sele_cont_group);
        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.sele_cont_horizontalscrollview);
        loading = new Loading(findViewById(R.id.loading_layout), (RelativeLayout) findViewById(R.id.sele_noLoading));

        httpManager = new HttpManager();
        sp = mContext.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        type = Conversation.ConversationType.PRIVATE;

        list = new ArrayList<>();
        listMsg = new ArrayList<>();
        listSelect = new ArrayList<>();
        FBlistSelected = new ArrayList<>();
        groupList = new ArrayList<>();
        adapter = new SeleContAdapter(this, list, true);
        listView.setAdapter(adapter);
        adapter.setImgCheckImg(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = (int) view.getTag();
                Object object = list.get(index);
                if (idList == null) {
                    idList = new ArrayList<String>();
                }
                if (object instanceof FriendInfo) {
                    FriendInfo message = (FriendInfo) object;
                    if (!idList.contains(message.getFriendId()) && !message.isMember()) {
                        message.check = !message.check;
                        list.set(index, message);
                        adapter.notifyDataSetChanged();
                        getSelectCount();
                    }
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object object = list.get(position);
                if (idList == null) {
                    idList = new ArrayList<String>();
                }
                if (object instanceof FriendInfo) {
                    FriendInfo message = (FriendInfo) object;
                    if (!idList.contains(message.getFriendId()) && !message.isMember()) {
                        message.check = !message.check;
                        list.set(position, message);
                        adapter.notifyDataSetChanged();
                        getSelectCount();
                    }
                }
            }
        });
    }

    private void initData() {
        userId = sp.getString(Constant.ID_USER, "");
        name = sp.getString("name", "");
        if (getIntent().getStringExtra("type") != null && !TextUtils.isEmpty(getIntent().getStringExtra("type")) && getIntent().getStringExtra("type").equals("ChatInformationActivity")) {
            relative_group.setVisibility(View.GONE);
            titleBar.setTitle("增加聊天对象");
            idList = getIntent().getStringArrayListExtra("idList");
            discussionName = getIntent().getStringExtra("discussionName");
            sessionType = getIntent().getIntExtra("sessionType", 2);
            targetId = getIntent().getStringExtra("targetId");
        }
        loading.showLoading(false);
        if (!userId.equals("")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    result = httpManager.findAllFriends(userId);
                    if (result == null || result.equals("404")) {
                        mHandler.sendEmptyMessage(FIND_FRIENDS_FAIL);
                    } else {
                        mHandler.sendEmptyMessage(FIND_FRIENDS_SUCCEED);
                    }
                }
            }).start();
        }
    }

    private void initListener() {
        relative_group.setOnClickListener(this);
    }

    private void getSelectCount() {
        listSelect.clear();
        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            Object object = list.get(i);
            if (object instanceof FriendInfo) {
                FriendInfo message = (FriendInfo) object;
                if (message.check) {
                    count++;
                    listSelect.add(message);
                }
            }
        }
        if (count == 0) {
            titleBar.setRightIcon("确定");
            linear_select.setVisibility(View.GONE);
        } else if (count > 0) {
            titleBar.setRightIcon("确定(" + count + ")");
            linear_select.setVisibility(View.VISIBLE);
        }
        linear_select.removeAllViews();
        for (int i = 0; i < listSelect.size(); i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.tra_fri_select_linear_item, null);
            ShapeImageView img = (ShapeImageView) view.findViewById(R.id.tra_fri_select_img);
            setUImage(img, listSelect.get(i).getFriendId());
            linear_select.addView(view);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                horizontalScrollView.smoothScrollBy(Integer.MAX_VALUE / 4, 0);
            }
        }, 200);
    }


    private void setUImage(final ShapeImageView img, final String friendId) {
        img.setTag(friendId);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String url = httpManager.getUserIconUrl(friendId);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        String tag = (String) img.getTag();
                        if (url != null && !TextUtils.isEmpty(url)) {
                            if (tag.equals(friendId)) {
                                Picasso.with(mContext).load(url).resize((int) getResources().getDimension(R.dimen.view_34),
                                        (int) getResources().getDimension(R.dimen.view_34))
                                        .placeholder(R.mipmap.ic_head_wode).error(R.mipmap.ic_head_wode).config(Bitmap.Config.RGB_565).into(img);
                            }
                        }
                    }
                });
            }
        }).start();
    }


    @Override
    public void leftClick() {
        close();
    }

    private void close() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        close();
    }

    @Override
    public void rightClick() {
        loading.showLoading(false);
        Log.i("SelectContactsActivity", "头像url：" + listSelect.toString());
        if (listSelect.size() >= 1) {
            from = getIntent().getStringExtra("from");
            Boolean create = false;
            userIdList = new ArrayList<String>();
            userNameList = new ArrayList<>();

            for (int i = 0; i < listSelect.size(); i++) {
                userIdList.add(listSelect.get(i).getFriendId());
                userNameList.add(listSelect.get(i).getUserName());
            }
//            Log.i("TAG", "创建群组的人数是：" + userNameList.size());
//            groupName = listSelect.get(0).getUserName() + "、" + listSelect.get(1).getUserName() + "...(" + listSelect.size() + ")";
            if (from.equals("newGroup")) {    //创建新群组
                if (listSelect.size() == 1) {      //选一个人时跳到单聊
                    Intent toSingleChat = new Intent(UIUtils.getContext(), ChatActivity.class);
                    toSingleChat.putExtra("from", "SelectContactsActivity");
                    toSingleChat.putExtra("targetId", listSelect.get(0).getFriendId());
                    toSingleChat.putExtra("type", Conversation.ConversationType.PRIVATE);
                    toSingleChat.putExtra("name", sp.getString("name", ""));
                    startActivity(toSingleChat);
                    finish();
                } else {//选了一人以上时创建群组
                    userNameList.add(0, name);
                    userIdList.add(0, userId);
                    groupName = name + "、" + listSelect.get(0).getUserName() + "、" + listSelect.get(1).getUserName() + "...(" + listSelect.size() + 1 + ")";
                    create = true;
                }
            } else if (from.equals("person")) { //由单聊通过加人变为群聊
                userNameList.add(0, name);
                userIdList.add(0, userId);
                String friendId = getIntent().getStringExtra("friendId");
                String friendName = getIntent().getStringExtra("friendName");
                userNameList.add(1,friendName);
                userIdList.add(friendId);
//                groupName = friendName + "、" + name + "、" + listSelect.get(0).getUserName() + "...(" + userNameList.size() + ")";
                create = true;
            } else if (from.equals("group")) {  //已有的群组加人
//                Log.i("TAG", "群添加人，sessionType=" + sessionType);
                create = false;
                userId = getIntent().getStringExtra("targetId");
                name = getIntent().getStringExtra("sessionName");
                int sessionType = getIntent().getIntExtra("sessionType", 2);//1单聊   2群聊   3群组   4机构
                if(sessionType == 2) {  //群聊
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (NetWorkUtils.isNetworkAvailable(UIUtils.getContext())) {
                                result = null;
                                result = httpManager.addGroup(userId, name, userIdList, userNameList);
                                if (result.equals("404")) {
                                    mHandler.sendEmptyMessage(ADD_GROUP_FAIL);
                                } else {
                                    StringBuilder stringBuilder = new StringBuilder();
                                    StringBuilder stringBuilder2 = new StringBuilder();
                                    for (int i = 0; i < listSelect.size(); i++) {
                                        stringBuilder.append(listSelect.get(i).getUserName() + " ");
                                        if(i < 2){
                                            stringBuilder2.append(listSelect.get(i).getUserName()+"、");
                                        }
                                    }
                                    selectName = stringBuilder.toString();
                                    discussionName = discussionName + "、" + stringBuilder2.toString();
                                    mHandler.sendEmptyMessage(ADD_GROUP_SUCCEED);
                                }
                            } else {
                                mHandler.sendEmptyMessage(NO_INTERNET);
                            }
                        }
                    }).start();
                }else if(sessionType == 3 || sessionType == 4){ //群组或机构
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (NetWorkUtils.isNetworkAvailable(UIUtils.getContext())) {
                                result = null;
                                result = httpManager.addSpaces(userId, userIdList);
                                if (result.equals("404")) {
                                    mHandler.sendEmptyMessage(ADD_GROUP_FAIL);
                                } else {
                                    StringBuilder stringBuilder = new StringBuilder();
                                    for (int i = 0; i < listSelect.size(); i++) {
                                        stringBuilder.append(listSelect.get(i).getUserName() + " ");
                                    }
                                    selectName = stringBuilder.toString();
                                    mHandler.sendEmptyMessage(ADD_GROUP_SUCCEED);
                                }
                            } else {
                                mHandler.sendEmptyMessage(NO_INTERNET);
                            }
                        }
                    }).start();
                }
            } else if (from.equals("NetFile")) {

                create = false;
                for (int i = 0; i < listSelect.size(); i++) {
                    final int finalI = i;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            sendNetFile(FBlistSelected, listSelect.get(finalI).getFriendId());
                        }
                    }).start();
                }
                loading.hideLoading(false);
                Toast.makeText(SelectContactsActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                SelectContactsActivity.this.finish();
            }

            //创建群聊
            if (create) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        StringBuilder stringBuilder = new StringBuilder();
                        for(String str : userNameList){
                            if(str != null && !TextUtils.isEmpty(str) && !str.equals("null")) {
                                stringBuilder.append(str);
                                stringBuilder.append("、");
                            }
                            if(stringBuilder.toString().length() >= 20){
                                break;
                            }
                        }
                        groupName = stringBuilder.toString();
                        result = null;
                        result = httpManager.creatGroup(userId, "", groupName, userIdList, userNameList);
                        Log.i("TAG", "创建群聊的返回码：" + result);
                        if (result.equals("404")) {
                            mHandler.sendEmptyMessage(CREAT_GROUP_FAIL);
                        } else {
                            mHandler.sendEmptyMessage(CREAT_GROUP_SUCCEED);
                        }
                    }
                }).start();
            }
        } else {
            Toast.makeText(UIUtils.getContext(), "请选择好友", Toast.LENGTH_SHORT).show();
            loading.hideLoading(false);
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
    public void onClick(View v) {
        if (v.getId() == R.id.sele_cont_group) {
            Intent toGroup = new Intent(UIUtils.getContext(), GroupListActivity.class);
            if (getIntent().getStringExtra("from").equals("NetFile")){
                toGroup.putExtra("FBlistSelected", (Serializable) FBlistSelected);
                toGroup.putExtra("flag","fromNetFile");
                Log.i("info", "SelectedContactsActivity中发送到空间群组的文件：" + FBlistSelected.size());
            }
            startActivity(toGroup);
            finish();
        }
    }

    public void sendNetFile(List<FileDirList> FBlistSelected, final String friendId) {
//        Log.i("info", "走网盘发送文件");
//        Log.i("info", "目标Id:" + friendId);
        for (int i = 0; i < FBlistSelected.size(); i++) {
            FileDirList fileDirList = FBlistSelected.get(i);
            JSONObject json = new JSONObject();
            try {
                int contentType = FileUtils.getContentType(fileDirList.getShortName());
                json.put("MessageType", 0);
                json.put("ContentType", 11);
                json.put("ChatType", 0);// 0 单聊，1 群组，2 空间
                json.put("Content", "[文件]");
                json.put("FontSize", 14);
                json.put("FontStyle", 0);
                json.put("FontColor", 0);
                json.put("BulletinID", "");
                json.put("BulletinContent", "");
                json.put("requestName", "");
                json.put("requestRemark", "");
                json.put("requestGroupId", "");
                json.put("FileID", fileDirList.getId());
                json.put("FileSize", fileDirList.getSize());
                json.put("FileName", fileDirList.getShortName());
                json.put("CreateTime", System.currentTimeMillis());
                final String messagebody = json.toString();
                if (messagebody.equals("")) {
                    Toast.makeText(UIUtils.getContext(), "发送内容不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    String icon = sp.getString(Constant.USER_ICON, "");
                    Uri uri = Uri.parse(icon);
                    UserInfo userInfo = new UserInfo(sp.getString(Constant.ID_USER, ""), sp.getString(Constant.USER_NAME, ""), uri);
                    final TextMessage textMessage = TextMessage.obtain(messagebody);
                    textMessage.setUserInfo(userInfo);
                    RongIMClient.getInstance().sendMessage(type, friendId, textMessage, null, null, new RongIMClient.SendMessageCallback() {

                        @Override
                        public void onSuccess(Integer integer) {
                            io.rong.imlib.model.Message message = io.rong.imlib.model.Message.obtain(friendId, type, textMessage);
                            message.setMessageDirection(io.rong.imlib.model.Message.MessageDirection.SEND);
                            message.setSenderUserId(sp.getString(Constant.ID_USER, ""));

                            //发送广播
                            Intent intent = new Intent(NEW_MESSAGE);
                            intent.putExtra("new_message", "message");
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("message", message);
                            intent.putExtras(bundle);
                            sendBroadcast(intent);
                            Log.i("info", "发送成功" + message.getMessageDirection());
                            mHandler.sendEmptyMessage(SEND_MESSAGE);
                        }

                        @Override
                        public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {
                            Log.i("info", "发送失败");
                        }
                    }, new RongIMClient.ResultCallback<io.rong.imlib.model.Message>() {

                        @Override
                        public void onSuccess(io.rong.imlib.model.Message message) {
                            MessageContent messageContent = message.getContent();
                            if (messageContent instanceof TextMessage) {//文本消息
                                Log.i("info", "onReceived-TextMessage:" + ((TextMessage) messageContent).getContent());
                            } else if (messageContent instanceof ImageMessage) {//图片消息

                            } else if (messageContent instanceof VoiceMessage) {//语音消息

                            }
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {

                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
//    //发送网盘文件
//    private void sendNetFile(List<Object> listSelectAll) {
//        for (int i = 0; i < listSelectAll.size(); i++) {
//            Object o = listSelectAll.get(i);
//            if (o instanceof FileDirList) {
//                FileDirList file = (FileDirList) o;
//                JSONObject json = new JSONObject();
//                try {
//                    json.put("MessageType", 0);
//                    json.put("ContentType", 11);
//                    json.put("ChatType", 0);
//                    json.put("Content", "[文件]");
//                    json.put("FontSize", 14);
//                    json.put("FontStyle", 0);
//                    json.put("FontColor", 0);
//                    json.put("BulletinID", "");
//                    json.put("BulletinContent", "");
//                    json.put("requestName", "");
//                    json.put("requestRemark", "");
//                    json.put("requestGroupId", "");
//                    json.put("FileID", file.getId());
//                    json.put("FileName", file.getShortName());
//                    json.put("FileSize", file.getSize());
//                    json.put("CreateTime", System.currentTimeMillis());
//                    final String messageBody = json.toString();
//                    if (messageBody.equals("")) {
//                        Toast.makeText(UIUtils.getContext(), "发送内容不能为空", Toast.LENGTH_SHORT).show();
//                    } else {
//                        UserInfo userInfo = new UserInfo(sp.getString(Constant.ID_USER, ""), sp.getString("name", ""), null);
//                        final TextMessage textMessage = TextMessage.obtain(messageBody);
//                        textMessage.setUserInfo(userInfo);
//
//                        final io.rong.imlib.model.Message message = io.rong.imlib.model.Message.obtain(targetId, type, textMessage);
//                        message.setMessageDirection(io.rong.imlib.model.Message.MessageDirection.SEND);
//                        message.setSenderUserId(sp.getString(Constant.ID_USER, ""));
//                        RongIMClient.getInstance().sendMessage(type, targetId,
//                                textMessage, null, null, new RongIMClient.SendMessageCallback() {
//                                    @Override
//                                    public void onSuccess(Integer integer) {
//                                        //发送广播
//                                        Intent intent = new Intent(NEW_MESSAGE);
//                                        intent.putExtra("new_message", "message");
//                                        Bundle bundle = new Bundle();
//                                        bundle.putParcelable("message", message);
//                                        intent.putExtras(bundle);
//                                        mContext.sendBroadcast(intent);
//                                        handler.sendEmptyMessage(SEND_MESSAGE);
//                                        bar_edit_text.setText("");
//                                    }
//
//                                    @Override
//                                    public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {
//                                        android.os.Message message1 = new android.os.Message();
//                                        message1.what = SEND_FAIL;
//                                        message1.obj = message;
//                                        handler.sendMessage(message1);
//                                    }
//                                }, new RongIMClient.ResultCallback<io.rong.imlib.model.Message>() {
//                                    @Override
//                                    public void onError(RongIMClient.ErrorCode errorCode) {
//                                        android.os.Message message1 = new android.os.Message();
//                                        message1.what = SEND_FAIL;
//                                        message1.obj = message;
//                                        handler.sendMessage(message1);
//                                    }
//
//                                    @Override
//                                    public void onSuccess(io.rong.imlib.model.Message message) {
//                                        messageList.add(message);
//                                        handler.sendEmptyMessage(SEND_MESSAGE);
//                                    }
//
//                                });
//                    }
//
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SelectContactsActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SelectContactsActivity");
        MobclickAgent.onPause(this);
    }
}

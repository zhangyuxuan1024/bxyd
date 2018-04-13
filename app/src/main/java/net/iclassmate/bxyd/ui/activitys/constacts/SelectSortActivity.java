package net.iclassmate.bxyd.ui.activitys.constacts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.adapter.teachlearn.SeleSortAdapter;
import net.iclassmate.bxyd.bean.contacts.FriendInfo;
import net.iclassmate.bxyd.bean.contacts.GroupMember;
import net.iclassmate.bxyd.bean.message.Extra;
import net.iclassmate.bxyd.bean.owner.SpaceInfo;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.ui.activitys.chat.ChatInformationActivity;
import net.iclassmate.bxyd.ui.activitys.chat.RecordInfoActivity;
import net.iclassmate.bxyd.ui.activitys.chat.SelectContactsActivity;
import net.iclassmate.bxyd.ui.activitys.information.FriendInformationActivity;
import net.iclassmate.bxyd.ui.activitys.teachlearn.DeleteActivity;
import net.iclassmate.bxyd.utils.HttpManager;
import net.iclassmate.bxyd.utils.JsonUtils;
import net.iclassmate.bxyd.utils.NetWorkUtils;
import net.iclassmate.bxyd.utils.RongIMClientUtils;
import net.iclassmate.bxyd.utils.UIUtils;
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

import java.util.ArrayList;
import java.util.List;

import io.rong.imlib.model.Conversation;


/**
 * 群组踢人界面
 * Created by xyd on 2016/8/10.
 */
public class SelectSortActivity extends Activity implements TitleBar.TitleOnClickListener, View.OnClickListener
{
    private TitleBar titleBar;
    private ListView listView;
    private List<Object> list;
    private List<FriendInfo> listMsg;
    private List<FriendInfo> listSelect;
    private ArrayList<String> userIdList;
    private ArrayList<String> userNameList;
    private ArrayList<String> groupIdList;
    private List<GroupMember> groupList;
    private LinearLayout linear_select;
    private RelativeLayout relative_group;
    private HorizontalScrollView horizontalScrollView;
    private ImageView iv_select_loading;

    private AnimationDrawable anim;
    private char sortKey;
    private HttpManager httpManager;
//    private Loading loading;
    private SeleSortAdapter adapter;
    private String result;
    private String selectName;
    private String author, targetId, from, targetName;
    private int numList;
    private String type;
    private String discussionName;  //群聊踢人时同步更新名称
    private int sessionType;    //1单聊   2群聊   3群组   4机构
    private boolean visible;    //成员左边选择按钮是否显示   false不显示，true显示
    private SharedPreferences sp;
    private ArrayList<String> administratorsList;   //管理员id

    private static final int NO_INTERNET = 0;   //没网
    private static final int EXIT_GROUP_MEMBER_SUCCEED = 1; //群主踢人成功
    private static final int EXIT_GROUP_MEMBER_FAIL = 2; //群主踢人失败
    private static final int FINDE_SPACE_SUCCEED = 3;   //获取用户自己的空间信息

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case NO_INTERNET:
                    Toast.makeText(UIUtils.getContext(),"您当前没有连接网络，请连接后重试！",Toast.LENGTH_SHORT).show();
                    break;

                case EXIT_GROUP_MEMBER_SUCCEED:
                    Toast.makeText(UIUtils.getContext(),"这"+listSelect.size()+"人被您踢出了群聊！",Toast.LENGTH_SHORT).show();

                    Intent intent = getIntent();
                    intent.putExtra("ExitGroup","ExitSucceed");

                    sendKickMsg();
                    discussionUpdateName(); //同步更新群聊名称

                    intent.putExtra("discussionName",discussionName);
                    setResult(14, intent);
                    SelectSortActivity.this.finish();
                    break;

                case EXIT_GROUP_MEMBER_FAIL:
                    break;

                case FINDE_SPACE_SUCCEED:
                    Intent intent2 = (Intent)msg.obj;
                    intent2.putExtra("type", type);
                    startActivity(intent2);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_sort);

        initView();
        initData();
    }

    private void initView() {
        sp = UIUtils.getContext().getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        titleBar = (TitleBar) findViewById(R.id.select_sort_title_bar);

        titleBar.setLeftIcon(R.mipmap.ic_fanhui, "返回");
        titleBar.setRightIcon("删除");
        titleBar.setTitle("删除成员");
        titleBar.setTitleClickListener(this);
        listView = (ListView) findViewById(R.id.sele_sort_listview);
        linear_select = (LinearLayout) findViewById(R.id.sele_sort_container_linear);
        relative_group = (RelativeLayout) findViewById(R.id.sele_sort_group);
        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.sele_sort_horizontalscrollview);
        iv_select_loading = (ImageView)findViewById(R.id.iv_select_loading);

        anim = (AnimationDrawable) iv_select_loading.getBackground();
        visible = getIntent().getBooleanExtra("visible", true);

        httpManager = new HttpManager();
        sp = SelectSortActivity.this.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        list = new ArrayList<>();
        listMsg = new ArrayList<>();
        listSelect = new ArrayList<>();
        groupList = new ArrayList<>();
        adapter = new SeleSortAdapter(SelectSortActivity.this, list, visible);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FriendInfo friendInfo = (FriendInfo) list.get(position);
                final String friendId = friendInfo.getFriendId();
                String uName = friendInfo.getUserName();
                String uCode = friendInfo.getFriendCode();
                Log.i("TAG", "啊十分大撒旦发射点发射点:"+getIntent().getStringExtra("from"));
                if(getIntent().getStringExtra("from") == null || getIntent().getStringExtra("from").equals("")) {
                    final Intent intent = new Intent(SelectSortActivity.this, FriendInformationActivity.class);
                    intent.putExtra("targetId", friendId);
                    intent.putExtra("sessionName", uName);
                    intent.putExtra("code", uCode);
                    intent.putExtra("from", "ChatInformationActivity");
                    if(friendId.equals(sp.getString(Constant.ID_USER, ""))){
                        intent.putExtra("from", "EmotionMainFragment");
                    }
//                    startActivity(intent);

//                    numList = position;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if(NetWorkUtils.isNetworkAvailable(UIUtils.getContext())) {
                                String result = httpManager.findSpaceInfo2(false, friendId);
                                try {
                                    JSONObject jsonObject = new JSONObject(result);
                                    type = "person";
                                    SpaceInfo spaceInfo = null;
                                    if(jsonObject.getString("type") != null && !TextUtils.isEmpty(jsonObject.getString("type")))
                                    {
                                        type = jsonObject.getString("type");
                                        spaceInfo = JsonUtils.StartSpaceInfoJson(result);
                                        intent.putExtra("type", type);
                                    }
                                    Log.i("ContactsActivity", "是否为机构是否为机构是否为机构是否为机构：" + type);
                                    Message msg = new Message();
                                    intent.putExtra("visitHomepage", spaceInfo.getAuthority().isFocusMe());
                                    msg.obj = intent;
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
                } else if(getIntent().getStringExtra("from") != null && getIntent().getStringExtra("from").equals("ChatRecordActivity")){    //跳转聊天记录页
                    Intent intent = new Intent(SelectSortActivity.this, RecordInfoActivity.class);
                    intent.putExtra("targetId", targetId);
                    intent.putExtra("friendId", friendId);
                    intent.putExtra("from", "group");
                    intent.putExtra("demand", "member");    //按成员查询
                    startActivity(intent);
                } else if(getIntent().getStringExtra("exit") != null && getIntent().getStringExtra("exit").equals("exit")){ //踢人
                    Object object = list.get(position);
                    if (object instanceof FriendInfo) {
                        FriendInfo message = (FriendInfo) object;
                        if(!friendInfo.isMember()) {
                            message.check = !message.check;
                            list.set(position, message);
                            adapter.notifyDataSetChanged();
                            getSelectCount();
                        }
                    }
                }
            }
        });
    }

    public void initData()
    {
//        loading.showLoading(false);
        iv_select_loading.setVisibility(View.VISIBLE);
        anim.start();
        author = getIntent().getStringExtra("author");
        targetId = getIntent().getStringExtra("targetId");
        groupList = (ArrayList)getIntent().getSerializableExtra("groupList");
        targetName = getIntent().getStringExtra("groupName");
        sessionType = getIntent().getIntExtra("sessionType", 2);
        administratorsList = getIntent().getStringArrayListExtra("administratorsList");

        if(!visible){
            titleBar.setRightIcon("");
            titleBar.setTitle("群成员");
        } else {
            adapter.setImgCheckImg(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = (int) view.getTag();
                    Object object = list.get(index);
                    if (object instanceof FriendInfo) {
                        FriendInfo message = (FriendInfo) object;
                        message.check = !message.check;
                        list.set(index, message);
                        adapter.notifyDataSetChanged();
                        getSelectCount();
                    }
                }
            });
        }
        Log.e("SelectSortActivity", "群组成员groupList：" + groupList.size() + "个" + groupList.toString());
        String userId = sp.getString(Constant.ID_USER, "");

        for(int i = 0; i < groupList.size(); i++)
        {
            GroupMember groupMember = groupList.get(i);
            if(visible){
                if(groupMember.getUserId().equals(userId)){
                    continue;
                }
            }
            if(groupMember.getUserId().equals("add") || groupMember.getUserId().equals("exit"))
            {
                continue;
            }else
            {
                FriendInfo friendInfo = new FriendInfo(groupList.get(i));
                listMsg.add(friendInfo);
            }
        }
        initSort();
    }

    /**
     * 按大写字母排序
     */
    public void initSort()
    {
        Log.e("SelectSortActivity", "群组成员listMsg：" + listMsg.size()+"个"+listMsg.toString());
        for (int j = 0; j < listMsg.size(); j++) {
            Log.e("----------------", j + "");
            FriendInfo friendInfo = listMsg.get(j);
            if(getIntent().getStringExtra("exit") != null && getIntent().getStringExtra("exit").equals("exit")){
                if(null != author && sp.getString(Constant.ID_USER,"").equals(author)){
                    if(friendInfo.getFriendId().equals(author)){
                        friendInfo.setMember(true);
                    }
                }else{
                    sheZhe(friendInfo);
                }
            }
            String remark = friendInfo.getRemark();
            if (remark == null || remark.equals("")) {
                remark = friendInfo.getUserName();
            }
            char ch = ' ';
            if (SelectContactsActivity.isEnglish(remark)) {
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
                    String[] pinyin = PinyinHelper.toHanyuPinyinStringArray(c[0], format);
                    if (pinyin.length != 0) {
                        char[] cc = pinyin[0].toCharArray();
                        ch = cc[0];
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
        adapter.notifyDataSetChanged();
        anim.stop();
        iv_select_loading.setVisibility(View.GONE);
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
            titleBar.setRightIcon("删除");
            linear_select.setVisibility(View.GONE);
        } else if (count > 0) {
            titleBar.setRightIcon("删除(" + count + ")");
            linear_select.setVisibility(View.VISIBLE);
        }
        linear_select.removeAllViews();
        for (int i = 0; i < listSelect.size(); i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.tra_fri_select_linear_item, null);
            ShapeImageView img = (ShapeImageView) view.findViewById(R.id.tra_fri_select_img);
            setUImage(img, listSelect.get(i).getFriendId());
//            Picasso.with(this).load(url).resize(106, 106).into(img);
//            view.setTag(url);
         /*   if (url != null && !url.equals("")) {
                String icon = BitmapUtils.getImageUrl(url);
                Bitmap bitmap = BitmapUtils.stringtoBitmap(icon);
                img.setImageBitmap(bitmap);
            }*/
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
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        String tag = (String) img.getTag();
                        if (url != null && !TextUtils.isEmpty(url)) {
                            if (tag.equals(friendId)) {
                                Picasso.with(SelectSortActivity.this).load(url).resize((int) getResources().getDimension(R.dimen.view_34),
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 15)
        {
            if(data.getStringExtra("YesNo").equals("Yes")){
                exitGroupMember();
            }
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void leftClick() {
        close();
    }

    private void close()
    {
        Intent intent = getIntent();
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void rightClick() {
        if(listSelect.size() > 0)
        {
            Intent intent = new Intent(UIUtils.getContext(), DeleteActivity.class);
            intent.putExtra("from", "SelectSortActivity");
            startActivityForResult(intent, 15);
        }else
        {
            Toast.makeText(UIUtils.getContext(),"请选择要踢出群的人",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 群主踢人
     * @author LvZhanFeng
     * @time 2016/8/12
     */
    private void exitGroupMember()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(NetWorkUtils.isNetworkAvailable(UIUtils.getContext()))
                {
                    result = null;
                    userIdList = new ArrayList<String>();
                    userNameList = new ArrayList<String>();
                    groupIdList = new ArrayList<String>();
                    StringBuilder stringBuilder = new StringBuilder();
                    for(FriendInfo friendInfo : listSelect)
                    {
                        String userId = friendInfo.getFriendId();
                        String userName = friendInfo.getUserName();
                        String groupId = friendInfo.getGroupSpaceId();
                        userIdList.add(userId);
                        userNameList.add(userName);
                        groupIdList.add(groupId);
                        stringBuilder.append(userName + " ");
                    }
                    selectName = stringBuilder.toString();
                    if(sessionType == 2) {
                        result = httpManager.exitGroup(targetId, userIdList, userNameList);
                    }else if(sessionType == 3 || sessionType == 4){
                        result = httpManager.exitSpaces(groupIdList, userIdList);
                    }
                    if(result.equals("404")){
                        handler.sendEmptyMessage(EXIT_GROUP_MEMBER_FAIL);
                    }else{
                        handler.sendEmptyMessage(EXIT_GROUP_MEMBER_SUCCEED);
                    }
                }else
                {
                    handler.sendEmptyMessage(NO_INTERNET);
                }
            }
        }).start();
    }

    /**
     * 同步更新群聊名称
     */
    public void discussionUpdateName(){
        boolean b = listMsg.removeAll(listSelect);
        if(null != listMsg && listMsg.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < listMsg.size(); i++) {
                if (i < 4) {
                    stringBuilder.append(listMsg.get(i).getUserName() + "、");
                }
            }
            discussionName = sp.getString("name", "") + "、" + stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1);
        }else{
            discussionName = sp.getString("name", "");
        }
//                    Log.i("TAG", "zzzzzzzzzzzzzzzzzzzzzzz:" + discussionName);
        if(sessionType == 2) {
            if(NetWorkUtils.isNetworkAvailable(UIUtils.getContext())) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        httpManager.updateGroupName(targetId, discussionName, 0);
                    }
                }).start();

            }
        }
    }

    /**
     * T人成功时发送CmdDtf和TxtMsg消息
     */
    public void sendKickMsg(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(NetWorkUtils.isNetworkAvailable(UIUtils.getContext())){
                    sendPrivateCmdDtf();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                if(NetWorkUtils.isNetworkAvailable(UIUtils.getContext())){
                    sendGroupCmdDtf();
                    io.rong.imlib.model.Message message = sendTxtMsg();
                    Intent intent = new Intent(ChatInformationActivity.UPDATE_MESSAGE);
                    intent.putExtra("message", message);
                    SelectSortActivity.this.sendBroadcast(intent);
                }
            }
        }).start();
    }

    /**
     * 给被T出群的人发命令通知
     */
    public void sendPrivateCmdDtf(){
        int i = 0;
        for(FriendInfo friendInfo : listSelect)
        {
            i++;
            String userId = friendInfo.getFriendId();
            String userName = friendInfo.getUserName();
            if(i % 6 == 0){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if(sessionType == 2) {
                RongIMClientUtils.sendCommend(Extra.Operation.KICK_OUT_GROUP, userId, userName, Conversation.ConversationType.PRIVATE, "");
            } else if (sessionType == 3 || sessionType == 4){
                RongIMClientUtils.sendCommend(Extra.Operation.KICK_OUT_SPACE, userId, userName, Conversation.ConversationType.PRIVATE, "");
            }
        }
    }

    /**
     * 发送T人的命令消息
     */
    public void sendGroupCmdDtf(){
        if(sessionType == 2) {
            RongIMClientUtils.sendCommend(Extra.Operation.SYNCH_GROUP, targetId, targetName, Conversation.ConversationType.GROUP, "");
            RongIMClientUtils.sendCommend(Extra.Operation.KICK_OUT_GROUP, targetId, targetName, Conversation.ConversationType.GROUP, "");
        } else if (sessionType == 3 || sessionType == 4){
            RongIMClientUtils.sendCommend(Extra.Operation.SYNCH_SPACE, targetId, targetName, Conversation.ConversationType.GROUP, "");
            RongIMClientUtils.sendCommend(Extra.Operation.KICK_OUT_SPACE, targetId, targetName, Conversation.ConversationType.GROUP, "");
        }
    }

    /**
     * 发送T人消息通知
     */
    public io.rong.imlib.model.Message sendTxtMsg(){
        String content = null;
        int chatType = 1;
        if(sessionType == 2){
            content = selectName + "被T出群聊 ";
            chatType = 1;
        } else if (sessionType == 3 || sessionType == 4){
            content = selectName + "被T出群组 ";
            chatType = 2;
        }
        return RongIMClientUtils.sendTxtMsg(Conversation.ConversationType.GROUP, 11, chatType, content, targetId);
    }

    public void sheZhe(FriendInfo friendInfo){
        if(null != administratorsList){
            if(administratorsList.contains(friendInfo.getFriendId())){
                friendInfo.setMember(true);
            }
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
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SelectSortActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SelectSortActivity");
        MobclickAgent.onPause(this);
    }
}


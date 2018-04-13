package net.iclassmate.bxyd.ui.activitys.constacts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.adapter.GroupSpaceListAdapter;
import net.iclassmate.bxyd.bean.contacts.Group;
import net.iclassmate.bxyd.bean.contacts.GroupInfo;
import net.iclassmate.bxyd.bean.study.FileDirList;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.ui.activitys.information.FriendInformationActivity;
import net.iclassmate.bxyd.utils.HttpManager;
import net.iclassmate.bxyd.utils.JsonUtils;
import net.iclassmate.bxyd.utils.NetWorkUtils;
import net.iclassmate.bxyd.utils.RongIMClientUtils;
import net.iclassmate.bxyd.utils.UIUtils;
import net.iclassmate.bxyd.view.TitleBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.rong.imlib.model.Conversation;

public class GroupListActivity extends FragmentActivity implements TitleBar.TitleOnClickListener {
    private Context mContext;
    private TitleBar titleBar;
    private ListView groupLv;
    private String userId;
    private String result;
    private Group group;
    private GroupInfo groupInfo;
    private TextView count;
    private GroupSpaceListAdapter groupListAdapter;
    private HttpManager httpManager;
    private SharedPreferences sp;
    private int positionList;
    private boolean isTop;
    private int orgSpaceNum; //获取机构数量
    private int groupSpaceNum;//获取群组数量
    public static final int REQUEST_SUCCESS = 0;
    public static final int REQUEST_FAIL = 1;
    private List<FileDirList> FBlistSelected;
    private String from;
    public static final String NEW_MESSAGE = "NEW_MESSAGE";

    private AnimationDrawable anim;
    private ImageView img_anim;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            anim.stop();
            img_anim.setVisibility(View.GONE);
            switch (msg.what) {
                case REQUEST_SUCCESS:
//                    Log.i("TAG", "群聊列表："+result);
                    group = JsonUtils.jsonGroupInfo2(result);
                    try {
                        JSONObject object = new JSONObject(result);
//                        Log.i("TAG", "获取的群组机构：" + group.getList().toString());
                        orgSpaceNum = object.getInt("orgSpaceNum"); //获取机构数量
                        groupSpaceNum = object.getInt("groupSpaceNum");//获取群组数量
                        int size = group.getList().size();
                        count.setText(orgSpaceNum + "个机构  " + groupSpaceNum + "个群组");
                        if (groupListAdapter == null) {
                            groupListAdapter = new GroupSpaceListAdapter(mContext, group);
                            groupLv.setAdapter(groupListAdapter);
                        } else {
                            groupListAdapter.notifyDataSetChanged();
                        }
                        if (size <= 0) {
                            img_anim.setVisibility(View.VISIBLE);
                            img_anim.setBackgroundColor(Color.parseColor("#f5f5f5"));
                            img_anim.setImageResource(R.mipmap.img_meiyouqunzu);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                case REQUEST_FAIL:
                    img_anim.setVisibility(View.VISIBLE);
                    img_anim.setBackgroundColor(Color.parseColor("#f5f5f5"));
                    img_anim.setImageResource(R.mipmap.img_jiazaishibai);
                    break;
                case 1011:
                    io.rong.imlib.model.Message message = (io.rong.imlib.model.Message) msg.obj;
                    //发送广播
                    Intent intent = new Intent(NEW_MESSAGE);
                    intent.putExtra("new_message", "message");
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("message", message);
                    intent.putExtras(bundle);
                    sendBroadcast(intent);
                    Toast.makeText(GroupListActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        mContext = this;

        initView();
        Intent intent = getIntent();
        FBlistSelected = (List<FileDirList>) intent.getSerializableExtra("FBlistSelected");
        from = intent.getStringExtra("flag");
        initListener();
        initData();
        initGroupListener();

        //注册广播
        IntentFilter filter = new IntentFilter(SendFriendRequestActivity.action);
        registerReceiver(broadcastreceiver, filter);
    }

    private void initView() {
        FBlistSelected = new ArrayList<>();
        titleBar = (TitleBar) findViewById(R.id.groupList_title_bar);
        titleBar.setTitle("空间");
        titleBar.setLeftIcon(R.mipmap.ic_fanhui, "返回");
        groupLv = (ListView) findViewById(R.id.group_lv);
        httpManager = new HttpManager();
        sp = mContext.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        RelativeLayout footViewLayout = (RelativeLayout) LayoutInflater.from(
                GroupListActivity.this).inflate(R.layout.footer_textview, null);
        count = (TextView) footViewLayout.findViewById(R.id.groups_count);
        groupLv.addFooterView(footViewLayout);

        img_anim = (ImageView) findViewById(R.id.img_anim);
        anim = (AnimationDrawable) img_anim.getBackground();
        anim.start();
    }

    private void initListener() {
        titleBar.setTitleClickListener(this);
    }

    private void initData() {
        userId = sp.getString(Constant.ID_USER, "");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    result = httpManager.findAllGroupAndSpace(userId);
                    if (result != null && !result.equals("404")) {
                        mHandler.sendEmptyMessage(REQUEST_SUCCESS);
                    } else {
                        mHandler.sendEmptyMessage(REQUEST_FAIL);
                    }
                } catch (Exception e) {

                }
            }
        }).start();
    }

    private void initGroupListener() {
        groupLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (NetWorkUtils.isNetworkAvailable(UIUtils.getContext())) {
                    if (from != null && from.equals("fromNetFile")) {
                        Log.i("info", "网盘文件发送到空间群组");
                        //发消息
                        io.rong.imlib.model.Message message = RongIMClientUtils.sendNetFile(FBlistSelected, 2, Conversation.ConversationType.GROUP, group.getList().get(position).getSessionId(), sp);
                        if (message != null) {
                            Message msg = new Message();
                            msg.obj = message;
                            msg.what = 1011;
                            mHandler.sendMessage(msg);
                        } else {
                            Log.i("info", "message=" + message);
                        }

                    } else {
                        positionList = position;
                        ArrayList<GroupInfo> list = group.getList();
                        if (list == null || list.size() <= position) {
                            return;
                        }
                        groupInfo = group.getList().get(position);

                        Intent intent = new Intent(UIUtils.getContext(), FriendInformationActivity.class);
                        intent.putExtra("from", "GroupListActivity");
                        intent.putExtra("type", "group");
                        intent.putExtra("sessionId", groupInfo.getSessionId());
                        intent.putExtra("sessionName", groupInfo.getSessionName());
                        intent.putExtra("author", groupInfo.getAuthor());
                        intent.putExtra("sessionIcon", groupInfo.getSessionIcon());
                        intent.putExtra("sessionType", groupInfo.getSessionType());
                        if (groupInfo.getSessionType() == 4) {
                            intent.putExtra("code", groupInfo.getCode());
                        }
                        startActivityForResult(intent, 10);
                    }
                } else {
                    Toast.makeText(UIUtils.getContext(), "您当前没有链接网络", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 10 && group != null) {
                group.getList().remove(positionList);
                groupListAdapter.notifyDataSetChanged();
                if (groupInfo.getSessionType() == 3) {
                    groupSpaceNum--;
                } else if (groupInfo.getSessionType() == 4) {
                    orgSpaceNum--;
                }
                count.setText(orgSpaceNum + "个机构  " + groupSpaceNum + "个群组");
                Intent intent = new Intent(ContactsActivity.UPDATE_FRIEND_NAME);
                intent.putExtra("groupSpaceNum", groupSpaceNum);
                intent.putExtra("orgSpaceNum", orgSpaceNum);
                intent.putExtra("updateNum", "updateNum");
//                setResult(10, intent);
                sendBroadcast(intent);
            }
        }
    }

    public BroadcastReceiver broadcastreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String groupName = intent.getStringExtra("updateName");
            group.getList().get(positionList).setSessionName(groupName);
            groupListAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastreceiver);  //移除广播接收
    }

    @Override
    public void leftClick() {
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
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("GroupListActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("GroupListActivity");
        MobclickAgent.onPause(this);
    }
}
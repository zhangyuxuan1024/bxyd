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
import android.text.TextUtils;
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
import net.iclassmate.bxyd.adapter.GroupListAdapter;
import net.iclassmate.bxyd.bean.contacts.Group;
import net.iclassmate.bxyd.bean.contacts.GroupInfo;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.ui.activitys.chat.DiscussionActivity;
import net.iclassmate.bxyd.utils.HttpManager;
import net.iclassmate.bxyd.utils.JsonUtils;
import net.iclassmate.bxyd.utils.NetWorkUtils;
import net.iclassmate.bxyd.utils.UIUtils;
import net.iclassmate.bxyd.view.TitleBar;

import java.util.ArrayList;

/**
 * 群聊List界面
 */

public class DiscussionListActivity extends FragmentActivity implements TitleBar.TitleOnClickListener {
    private Context mContext;
    private TitleBar titleBar;
    private ListView groupLv;
    private String userId;
    private String result;
    private Group group;
    private GroupInfo groupInfo;
    private TextView count;
    private GroupListAdapter groupListAdapter;
    private HttpManager httpManager;
    private SharedPreferences sp;
    private int positionList;
    private boolean isTop;
    private int sessionType = 2;
    public static final int REQUEST_SUCCESS = 0;
    public static final int REQUEST_FAIL = 1;

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
                    group = JsonUtils.jsonGroupInfo(result);
                    int size = group.getList().size();
                    count.setText(size + "个群聊");
                    if (groupListAdapter == null) {
                        groupListAdapter = new GroupListAdapter(mContext, group);
                        groupLv.setAdapter(groupListAdapter);
                    } else {
                        groupListAdapter.notifyDataSetChanged();
                    }
                    if (size <= 0) {
                        img_anim.setVisibility(View.VISIBLE);
                        img_anim.setBackgroundColor(Color.parseColor("#f5f5f5"));
                        img_anim.setImageResource(R.mipmap.img_meiyouqunzu);
                    }
                    break;
                case REQUEST_FAIL:
                    img_anim.setVisibility(View.VISIBLE);
                    img_anim.setBackgroundColor(Color.parseColor("#f5f5f5"));
                    img_anim.setImageResource(R.mipmap.img_jiazaishibai);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion_list);
        mContext = this;
        initView();
        initListener();
        initData();
        initGroupListener();

        //注册广播
        IntentFilter filter = new IntentFilter(SendFriendRequestActivity.action);
        registerReceiver(broadcastreceiver, filter);
    }

    private void initView() {
        titleBar = (TitleBar) findViewById(R.id.discussionList_title_bar);
        titleBar.setTitle("群聊");
        titleBar.setLeftIcon(R.mipmap.ic_fanhui, "返回");
        groupLv = (ListView) findViewById(R.id.discussion_lv);
        httpManager = new HttpManager();
        sp = mContext.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        RelativeLayout footViewLayout = (RelativeLayout) LayoutInflater.from(
                DiscussionListActivity.this).inflate(R.layout.footer_textview, null);
        count = (TextView) footViewLayout.findViewById(R.id.groups_count);
        groupLv.addFooterView(footViewLayout);

        img_anim = (ImageView) findViewById(R.id.img_ani_discussion);
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
                    result = httpManager.findAllDiscussion(userId);
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
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (NetWorkUtils.isNetworkAvailable(UIUtils.getContext())) {
                    positionList = position;
                    ArrayList<GroupInfo> list = group.getList();
                    if (list == null || list.size() <= position) {
                        return;
                    }
                    groupInfo = group.getList().get(position);
                    int sessionType = groupInfo.getSessionType();
                    Intent intent = new Intent(UIUtils.getContext(), DiscussionActivity.class);
                    intent.putExtra("from", "FriendInformationActivity");
                    intent.putExtra("sessionId", groupInfo.getSessionId());
                    intent.putExtra("sessionName", groupInfo.getSessionName());
                    intent.putExtra("author", groupInfo.getAuthor());
                    intent.putExtra("sessionIcon", groupInfo.getSessionIcon());
//                    startActivity(intent);
                    startActivityForResult(intent, 10);
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
                count.setText(group.getList().size() + "个群聊");
            }
        }
    }

    public BroadcastReceiver broadcastreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String groupName = intent.getStringExtra("updateName");
            if(groupName != null && !TextUtils.isEmpty(groupName)) {
                group.getList().get(positionList).setSessionName(groupName);
                groupListAdapter.notifyDataSetChanged();
            }
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

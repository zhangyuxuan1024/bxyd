package net.iclassmate.bxyd.ui.activitys.constacts;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.BaseInputConnection;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.bean.contacts.FriendInfo;
import net.iclassmate.bxyd.bean.contacts.Group;
import net.iclassmate.bxyd.bean.owner.SpaceInfo;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.ui.activitys.information.FriendInformationActivity;
import net.iclassmate.bxyd.utils.FileUtils;
import net.iclassmate.bxyd.utils.HttpManager;
import net.iclassmate.bxyd.utils.JsonUtils;
import net.iclassmate.bxyd.utils.NetWorkUtils;
import net.iclassmate.bxyd.utils.UIUtils;
import net.iclassmate.bxyd.view.IndexBar;
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

import java.io.File;
import java.util.ArrayList;

/**
 * 通讯录界面
 */
public class ContactsActivity extends Activity implements TitleBar.TitleOnClickListener, View.OnClickListener {
    private TitleBar titleBar;
    private Context mContext;
    private char sortKey;
    private IndexBar bar;
    private ListView lv;
    private TextView tvShow, tv_organization, tv_group, tv_discussion;
    private View view_no_friend;
    private ImageView img_no_result, img_no_friend, img_add_friend;
    private String userId;
    private String result, resultGroup;
    private Loading loading;
    private RelativeLayout groupNext, discussionNext;
    private HttpManager httpManager;
    private SharedPreferences sp;
    private int numList;
    private String type, spaceId;
    private MyAdapter adapter;
    public static final String UPDATE_FRIEND_NAME = "update_friend_name";
    public final static int FINDE_FRIENDS_SUCCEED = 1;
    public final static int FINDE_FRIENDS_FAIL = 2;
    public final static int FINDE_SPACE_SUCCEED = 4;    //获取空间信息成功（用于判断此好友是否为机构）
    public final static int FINDE_SPACE_FALL = 7;       //获取空间信息失败
    private static final int NO_INTERNET = 0;    //没网络
    public static final int REQUEST_DISCUSSION_SUCCESS = 5; //获取用户的群组会话列表（群聊）成功
    public static final int REQUEST_DISCUSSION_FAIL = 6;    //获取用户的群组会话列表（群聊）失败
    private ArrayList<FriendInfo> friendInfos;
    private Group group;
    private int orgSpaceNum; //获取机构数量
    private int groupSpaceNum; //获取群组数量
    private int position;   //ListView下标

    private LruCache<String, Bitmap> bitmapLruCache;
    public static final int REQ_FRI = 1;
    private String sdcachefilename;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case NO_INTERNET:
                    loading.hideLoading(false);
                    Toast.makeText(UIUtils.getContext(), getResources().getString(R.string.alert_msg_check_net), Toast.LENGTH_SHORT).show();
                    break;
                case FINDE_FRIENDS_SUCCEED:
                    loading.hideLoading(false);
                    friendInfos = JsonUtils.jsonFriendInfo(result);
                    if (friendInfos == null) {
                        return;
                    }
                    FileUtils.write2Sd(result, sdcachefilename);
                    for (int i = 0; i < friendInfos.size(); i++) {
                        FriendInfo friendInfo1 = friendInfos.get(i);
                        if (friendInfo1 == null || friendInfo1.getUserName() == null
                                || friendInfo1.getUserName().trim().equals("") || friendInfo1.getRemark() == null) {
                            friendInfos.remove(i);
                        }
                    }

                    if (friendInfos.size() == 0) {
                        sp.edit().putBoolean(Constant.HAS_FRIEND, false).commit();
                        return;
                    }

                    for (int j = 0; j < friendInfos.size(); j++) {
                        FriendInfo friendInfo = friendInfos.get(j);
                        String remark = friendInfo.getRemark();
                        if (remark == null || remark.equals("") || remark.equals("null")) {
                            remark = friendInfo.getUserName();
                        }
                        remark = remark.toUpperCase();

                        char ch = ' ';
                        if (remark != null && remark.length() > 0) {
                            ch = remark.toUpperCase().charAt(0);
                            //字母
                            if (ch >= 'A' && ch <= 'Z') {
                                friendInfo.sortKey = ch;
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
                                    if (pinyin.length > 0) {
                                        char[] cc = pinyin[0].toCharArray();
                                        ch = cc[0];
                                        friendInfo.sortKey = ch;
                                    } else {
                                        friendInfo.sortKey = '#';
                                    }
                                } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                                    badHanyuPinyinOutputFormatCombination.printStackTrace();
                                }
                            }
                        } else {
                            friendInfo.sortKey = '#';
                        }
                        friendInfos.set(j, friendInfo);
                    }

                    //排序
                    for (int i = 0; i < friendInfos.size() - 1; i++) {
                        for (int j = 0; j < friendInfos.size() - 1 - i; j++) {
                            FriendInfo friendInfo1 = friendInfos.get(j);
                            FriendInfo friendInfo2 = friendInfos.get(j + 1);
                            if (friendInfo1.sortKey > friendInfo2.sortKey) {
                                friendInfos.set(j, friendInfo2);
                                friendInfos.set(j + 1, friendInfo1);
                            }
                        }
                    }

                    //添加字母
                    FriendInfo friendInfo = friendInfos.get(0);
                    friendInfo.isHead = true;
                    friendInfos.set(0, friendInfo);
                    for (int i = 1; i < friendInfos.size(); i++) {
                        FriendInfo friendInfo1 = friendInfos.get(i - 1);
                        FriendInfo friendInfo2 = friendInfos.get(i);
                        if (friendInfo2.sortKey > friendInfo1.sortKey) {
                            friendInfo2.isHead = true;
                        } else {
                            friendInfo2.isHead = false;
                        }
                        friendInfos.set(i, friendInfo2);
                    }

//                    lv.setAdapter(new MyAdapter());
                    adapter.notifyDataSetChanged();
                    break;
                case FINDE_FRIENDS_FAIL:
                    loading.hideLoading(false);
//                    Toast.makeText(UIUtils.getContext(), "好友列表加载失败", Toast.LENGTH_SHORT).show();
                    break;
                //获取群组个数
                case 3:
                    try {
                        JSONObject object = new JSONObject(resultGroup);
                        orgSpaceNum = object.getInt("orgSpaceNum"); //获取机构数量
                        groupSpaceNum = object.getInt("groupSpaceNum");//获取群组数量
                        tv_organization.setText(orgSpaceNum + "个机构");
                        tv_group.setText(groupSpaceNum + "个群组");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case FINDE_SPACE_SUCCEED:   //获取空间信息成功
                    SpaceInfo spaceInfo = (SpaceInfo) msg.obj;
                    Intent queryInfo = new Intent(UIUtils.getContext(), FriendInformationActivity.class);
                    queryInfo.putExtra("from", "ContactsActivity");
                    queryInfo.putExtra("friendId", friendInfos.get(numList).getFriendId());
                    queryInfo.putExtra("type", type);
                    queryInfo.putExtra("spaceId", spaceId);
                    queryInfo.putExtra("visitHomepage", spaceInfo.getAuthority().isVisitHomepage());    //是否允许别人进入自己主页
                    loading.hideLoading(false);
                    startActivityForResult(queryInfo, REQ_FRI);
                    break;

                case REQUEST_DISCUSSION_SUCCESS:    //获取用户的群组会话列表（群聊）
                    int size = group.getList().size();
                    tv_discussion.setText(size + "个群聊");
                    break;

                case FINDE_SPACE_FALL:  //获取空间信息失败
                    loading.hideLoading(false);
                    Toast.makeText(UIUtils.getContext(), "获取信息失败，请重试！", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        mContext = this;
        sdcachefilename = "xydspace_txl.dat";
//        initPhoneNumber();
        registerBroadcastReceiver();
        initView();
        initCache();
        initData();
        initLinstener();
    }

    private void initView() {
        titleBar = (TitleBar) findViewById(R.id.contacts_title_bar);
        bar = (IndexBar) findViewById(R.id.contacts_lb);
        tvShow = (TextView) findViewById(R.id.contacts_tvShow);
        groupNext = (RelativeLayout) findViewById(R.id.contacts_group_layout);
        discussionNext = (RelativeLayout) findViewById(R.id.contacts_discussion_layout);
        httpManager = new HttpManager();
        titleBar.setLeftIcon(R.mipmap.ic_fanhui, "消息");
        titleBar.setTitle("通讯录");
//        titleBar.setInnerRightIcon(R.mipmap.ic_jiahaoyou);
        titleBar.setRightIcon(R.mipmap.ic_jiahaoyou);
        sp = mContext.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        loading = new Loading(findViewById(R.id.loading_layout), (RelativeLayout) findViewById(R.id.cont_noLoading));
        lv = getListView();

        tv_organization = (TextView) findViewById(R.id.contacts_organization_count);
        tv_group = (TextView) findViewById(R.id.contacts_group_count);
        tv_discussion = (TextView) findViewById(R.id.contacts_discussion_count);

        view_no_friend = findViewById(R.id.view_no_friend);
        img_no_result = (ImageView) findViewById(R.id.img_no_result);
        img_no_friend = (ImageView) findViewById(R.id.img_no_friend);
        img_add_friend = (ImageView) findViewById(R.id.img_add_no_friend);
        img_no_friend.setImageResource(R.mipmap.img_meiyouhaoyou);
        img_add_friend.setOnClickListener(this);
        if (!NetWorkUtils.isNetworkAvailable(mContext)) {
            img_no_result.setVisibility(View.VISIBLE);
        } else {
            img_no_result.setVisibility(View.INVISIBLE);
        }
        boolean hasfriend = sp.getBoolean(Constant.HAS_FRIEND, true);
        if (!hasfriend) {
            view_no_friend.setVisibility(View.VISIBLE);
        } else {
            view_no_friend.setVisibility(View.INVISIBLE);
        }
    }

    private void initCache() {
        int maxSize = (int) Runtime.getRuntime().maxMemory();
        maxSize = maxSize / 8;
        bitmapLruCache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
    }

    private void initData() {
        userId = sp.getString(Constant.ID_USER, "");
        loading.showLoading(false);

        friendInfos = new ArrayList<>();
        adapter = new MyAdapter();
        lv.setAdapter(adapter);

        if (!NetWorkUtils.isNetworkAvailable(UIUtils.getContext())) {
            Toast.makeText(mContext, getResources().getString(R.string.alert_msg_check_net), Toast.LENGTH_SHORT).show();
            loading.hideLoading(false);
            return;
        }
        //请好友列表
        if (!userId.equals("")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    result = FileUtils.read2SdTXL(sdcachefilename, mContext);
                    if (result == null || result.equals("")) {
                        result = httpManager.findAllFriends(userId);
                    }
                    if (result != null && !result.equals("404")) {
                        handler.sendEmptyMessage(FINDE_FRIENDS_SUCCEED);
                    } else {
                        handler.sendEmptyMessage(FINDE_FRIENDS_FAIL);
                    }
                }
            }).start();
        }

        //获取群组数量
        new Thread(new Runnable() {
            @Override
            public void run() {
                resultGroup = httpManager.findAllGroupAndSpace(userId);
                if (resultGroup != null && !resultGroup.equals("404")) {
                    handler.sendEmptyMessage(3);
                }
            }
        }).start();

        //获取群聊数量
        userId = sp.getString(Constant.ID_USER, "");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = httpManager.findAllDiscussion(userId);
                if (result != null && !result.equals("404")) {
                    group = JsonUtils.jsonGroupInfo(result);
                    handler.sendEmptyMessage(REQUEST_DISCUSSION_SUCCESS);
                } else {
                    handler.sendEmptyMessage(REQUEST_DISCUSSION_FAIL);
                }
            }
        }).start();
    }

    private void initLinstener() {
        titleBar.setTitleClickListener(this);
        groupNext.setOnClickListener(this);
        discussionNext.setOnClickListener(this);
        bar.setOnIndexSelectedListener(new IndexBar.OnIndexSelectedListener() {
            @Override
            public void indexSelected(char index) {
                tvShow.setVisibility(View.GONE);
            }

            @Override
            public void indexChange(char index) {
                tvShow.setVisibility(View.VISIBLE);
                tvShow.setText(index + "");
                if (friendInfos == null) {
                    return;
                }
                for (int i = 0; i < friendInfos.size(); i++) {
                    if (friendInfos.get(i).isHead && friendInfos.get(i).sortKey == index) {
                        lv.setSelection(i);
                        return;
                    }
                }
                for (int i = 0; i < friendInfos.size(); i++) {
                    if (friendInfos.get(i).isHead && friendInfos.get(i).sortKey > index) {
                        lv.setSelection(i);
                        return;
                    }
                }
                lv.setSelection(friendInfos.size() - 1);
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                loading.showLoading(false);
                numList = position;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (NetWorkUtils.isNetworkAvailable(UIUtils.getContext())) {
                            String result = httpManager.findSpaceInfo2(false, friendInfos.get(position).getFriendId());
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                type = "person";
                                SpaceInfo spaceInfo = null;
                                if (jsonObject.getString("type") != null && !TextUtils.isEmpty(jsonObject.getString("type"))) {
                                    type = jsonObject.getString("type");
                                    spaceInfo = JsonUtils.StartSpaceInfoJson(result);
                                    spaceId = jsonObject.getString("uuid");
                                }
//                                Log.i("ContactsActivity", "是否为机构是否为机构是否为机构是否为机构：" + type + " " + spaceInfo.getAuthority().isVisitHomepage() + " " + spaceId);
                                if (spaceInfo != null) {
                                    Message message = new Message();
                                    message.obj = spaceInfo;
                                    message.what = FINDE_SPACE_SUCCEED;
                                    handler.sendMessage(message);
                                }else {
                                    handler.sendEmptyMessage(FINDE_SPACE_FALL);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (NullPointerException e) {
                            }
                        } else {
                            handler.sendEmptyMessage(NO_INTERNET);
                        }
                    }
                }).start();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_FRI) {
//                if (friendInfos != null) {
//                    friendInfos.clear();
//                }
//                initData();
            }//删除好友
        } else if (requestCode == 1) {
            String filepath = FileUtils.getSdCardPath();
            File file = new File(filepath, sdcachefilename);
            if (file.exists()) {
                file.delete();
            }
            initData();
        }
        if (requestCode == 10 && resultCode == 10) {
            groupSpaceNum = data.getIntExtra("groupSpaceNum", groupSpaceNum);
            orgSpaceNum = data.getIntExtra("orgSpaceNum", orgSpaceNum);
            tv_organization.setText(orgSpaceNum + "个机构");
            tv_group.setText(groupSpaceNum + "个群组");
        }
    }

    @Override
    public void leftClick() {
        close();
    }

    @Override
    public void onBackPressed() {
        close();
    }

    private void close() {
        Intent intent = new Intent();
        setResult(1, intent);
        finish();
    }

    @Override
    public void rightClick() {
        Intent intent = new Intent(ContactsActivity.this, AddFriendActivity.class);
        startActivityForResult(intent, REQ_FRI);
    }

    @Override
    public void titleClick() {

    }

    @Override
    public void innerleftClick() {

    }

    @Override
    public void innerRightClick() {
        Intent intent = new Intent(ContactsActivity.this, AddFriendActivity.class);
        startActivityForResult(intent, REQ_FRI);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.contacts_group_layout:
                Intent intent = new Intent(ContactsActivity.this, GroupListActivity.class);
                startActivityForResult(intent, 10);
                break;
            case R.id.contacts_discussion_layout:
                Intent intent2 = new Intent(ContactsActivity.this, DiscussionListActivity.class);
                startActivity(intent2);
                break;
            case R.id.img_add_no_friend:
                Intent intent3 = new Intent(mContext, AddFriendActivity.class);
                startActivity(intent3);
                break;
        }

    }

    public void registerBroadcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(UPDATE_FRIEND_NAME);
        // 注册广播
        ContactsActivity.this.registerReceiver(broadcastReceiver, myIntentFilter);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String targetId = intent.getStringExtra("targetId");
            String targetName = intent.getStringExtra("targetName");
            if (targetId != null && !TextUtils.isEmpty(targetId)) {
                for (FriendInfo friendInfo : friendInfos) {
                    if (friendInfo.getFriendId() != null && !TextUtils.isEmpty(friendInfo.getFriendId()) && friendInfo.getFriendId().equals(targetId)) {
                        if (targetName != null && !TextUtils.isEmpty(targetName)) {
                            friendInfo.setRemark(targetName);
                            adapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }
            if (intent.getStringExtra("updateNum") != null && !TextUtils.isEmpty(intent.getStringExtra("updateNum")) && intent.getStringExtra("updateNum").equals("updateNum")) {
                groupSpaceNum = intent.getIntExtra("groupSpaceNum", groupSpaceNum);
                orgSpaceNum = intent.getIntExtra("orgSpaceNum", orgSpaceNum);
                tv_organization.setText(orgSpaceNum + "个机构");
                tv_group.setText(groupSpaceNum + "个群组");
            }
        }
    };

    public ListView getListView() {
        return (ListView) findViewById(R.id.contacts_lv);
    }

    class MyAdapter extends BaseAdapter {
        private String image;

        @Override
        public int getCount() {
            int ret = 0;
            if (friendInfos != null) {
                ret = friendInfos.size();
            }
            return ret;
        }

        @Override
        public Object getItem(int i) {
            return friendInfos.get(i);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }


        class ViewHolder {
            TextView tvName;
            ImageView imageIcon;
        }


        @Override
        public View getView(int i, View v, ViewGroup parent) {
            ViewHolder holder = null;
            if (v == null) {
                v = LayoutInflater.from(mContext).inflate(R.layout.contacts_item, parent, false);
                holder = new ViewHolder();
                holder.tvName = (TextView) v.findViewById(R.id.contacts_person_name);
                holder.imageIcon = (ShapeImageView) v.findViewById(R.id.contacts_person_icon);
                v.setTag(holder);
            } else {
                holder = (ViewHolder) v.getTag();
            }

            String name = friendInfos.get(i).getRemark().trim();
            if (name != null && !TextUtils.isEmpty(name) && !name.equals("null") && !name.equals("NULL")) {
                holder.tvName.setText(name);
            } else {
                name = friendInfos.get(i).getUserName();
                holder.tvName.setText(name);
            }

            String id = friendInfos.get(i).getFriendId();
            Bitmap bitmap = bitmapLruCache.get(id);
            if (bitmap == null) {
                bitmap = FileUtils.read2SdBitmap(id, mContext);
                if (bitmap != null) {
                    holder.imageIcon.setImageBitmap(bitmap);
                    bitmapLruCache.put(id, bitmap);
                } else {
                    holder.imageIcon.setTag(id);
                    getUserIcon(holder.imageIcon, id);
                }
            } else {
                holder.imageIcon.setImageBitmap(bitmap);
            }

            if (friendInfos.get(i).isHead) {
                v.findViewById(R.id.llShowIndex).setVisibility(View.VISIBLE);
                ((TextView) v.findViewById(R.id.tvIndex)).setText("" + friendInfos.get(i).sortKey);
            } else {
                v.findViewById(R.id.llShowIndex).setVisibility(View.GONE);
            }
            return v;
        }

        private void getUserIcon(final ImageView imageIcon, final String userId) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final String url = httpManager.getUserIconUrl(userId);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            String tag = (String) imageIcon.getTag();
                            if (tag.equals(userId)) {
                                setImage(userId, url, imageIcon);
                            }
                        }
                    });
                }
            }).start();
        }

        public void setImage(final String uid, final String url, final ImageView img) {
            if (!NetWorkUtils.isNetworkAvailable(mContext) || url == null || url.equals("") || !url.contains("http")) {
                img.setImageResource(R.mipmap.ic_head_wode);
                Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_head_wode);
                bitmapLruCache.put(uid, bitmap);
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Bitmap bitmap = httpManager.getBitmap(url, true);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            String tag = (String) img.getTag();
                            if (tag.equals(uid)) {
                                if (bitmap != null) {
                                    img.setImageBitmap(bitmap);
                                    bitmapLruCache.put(uid, bitmap);
                                    FileUtils.writeBitmap2sd(bitmap, uid);
                                } else {
                                    img.setImageResource(R.mipmap.ic_head_wode);
                                }
                            }
                        }
                    });
                }
            }).start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("ContactsActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("ContactsActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ContactsActivity.this.unregisterReceiver(broadcastReceiver);
    }
}


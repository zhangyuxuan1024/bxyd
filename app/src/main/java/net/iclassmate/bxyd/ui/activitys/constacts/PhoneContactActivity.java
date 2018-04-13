package net.iclassmate.bxyd.ui.activitys.constacts;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.bean.contacts.Contacts;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.ui.activitys.information.FriendInformationActivity;
import net.iclassmate.bxyd.utils.HttpManager;
import net.iclassmate.bxyd.utils.NetWorkUtils;
import net.iclassmate.bxyd.utils.UIUtils;
import net.iclassmate.bxyd.view.IndexBar;
import net.iclassmate.bxyd.view.TitleBar;
import net.iclassmate.bxyd.view.study.ShapeImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.rong.imlib.model.Conversation;

public class PhoneContactActivity extends Activity  implements TitleBar.TitleOnClickListener{


    private TitleBar titleBar;
    private Context mContext;
    private IndexBar bar;
    private ListView lv;
    private TextView tvShow;
    private ImageView iv_contacts_loading;  //动画

    private MyAdapter adapter;
    private HttpManager httpManager;
    private SharedPreferences sp;
    private AnimationDrawable anim;
    private String userId, name;
    private int listNum;
    private boolean isTow = true;
    private Conversation.ConversationType conversationType = Conversation.ConversationType.PRIVATE;

    public static final int DELETE_FRIENDER = 19;   //删除好友

    String[] PHONES_PROJECTION = new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER, "sort_key"};

    public static final String UPDATE_FRIEND = "update_friend"; //刷新朋友
    private static final int NO_INTERNET = 0;    //没网络
    private static final int FIND_PHONE_SUCCEED = 1; //获取手机通讯录且判断数据成功

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case NO_INTERNET:
                    Toast.makeText(UIUtils.getContext(), "您没有连接网络，请连接后重试！", Toast.LENGTH_SHORT).show();
                    break;

                case FIND_PHONE_SUCCEED:
                    Log.i("PhoneContactActivity", "获取的联系人数据："+beans.toString());
                    iv_contacts_loading.setVisibility(View.GONE);
                    anim.stop();
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_contact);
        mContext = this;
        registerBoradcastReceiver();
        initView();
        initPhoneNumber();
//        initView();
        initListener();
    }

    private void initPhoneNumber() {
        iv_contacts_loading.setVisibility(View.VISIBLE);
        anim.start();
        sp = mContext.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        userId = sp.getString(Constant.ID_USER, "");
        name = sp.getString("name", "");
        getContacts();
        if(beans.size() <= 0){
            if(isTow) {
                Toast.makeText(UIUtils.getContext(), "您手机通讯录中没有联系人", Toast.LENGTH_SHORT).show();
            }
        } else {
            if(NetWorkUtils.isNetworkAvailable(UIUtils.getContext())) {
                searchInfo(beans);
            } else {
                handler.sendEmptyMessage(NO_INTERNET);
            }
        }
    }

    private void initView() {
        titleBar = (TitleBar) findViewById(R.id.phonecontacts_title_bar);
        bar = (IndexBar) findViewById(R.id.phone_contacts_lb);
        tvShow = (TextView) findViewById(R.id.phone_contacts_tvShow);
        iv_contacts_loading = (ImageView)findViewById(R.id.iv_contacts_loading);
        anim = (AnimationDrawable) iv_contacts_loading.getBackground();

        titleBar.setLeftIcon("取消");
        titleBar.setTitle("手机联系人");
        titleBar.setTitleClickListener(this);


        lv = getListView();
        adapter = new MyAdapter();
        lv.setAdapter(adapter);
        bar.setOnIndexSelectedListener(new IndexBar.OnIndexSelectedListener() {
            @Override
            public void indexSelected(char index) {
                tvShow.setVisibility(View.GONE);
            }

            @Override
            public void indexChange(char index) {
                tvShow.setVisibility(View.VISIBLE);
                tvShow.setText(index + "");
                for (int i = 0; i < beans.size(); i++) {
                    if (beans.get(i).isHead && beans.get(i).sortKey == index) {
                        lv.setSelection(i);
                        return;
                    }
                }
                for (int i = 0; i < beans.size(); i++) {
                    if (beans.get(i).isHead && beans.get(i).sortKey > index) {
                        lv.setSelection(i);
                        return;
                    }
                }
                lv.setSelection(beans.size() - 1);
            }
        });
    }

    public void initListener(){
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(beans.get(position).isXYD){
                    listNum = position;
                    Intent intent = new Intent(PhoneContactActivity.this, FriendInformationActivity.class);
                    intent.putExtra("id", beans.get(position).id);
                    intent.putExtra("from", "PhoneContactActivity");
                    intent.putExtra("type", "person");
                    intent.putExtra("code", beans.get(position).userInfo.getUserCode());
                    intent.putExtra("name", beans.get(position).userInfo.getName());
                    startActivityForResult(intent, 19);
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == DELETE_FRIENDER){
            beans.get(listNum).isFriender = false;
            adapter.notifyDataSetChanged();
        }
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

    /**
     * 判断电话号码是否符合格式.
     *
     * @param inputText the input text
     * @return true, if is phone
     */
    public boolean isPhone(String inputText) {
        Pattern p = Pattern.compile("^((14[0-9])|(13[0-9])|(15[0-9])|(18[0-9])|(17[0-9]))\\d{8}$");
        Matcher m = p.matcher(inputText);
        return m.matches();
    }

    /**
     * 获取手机通讯录中的手机号
     */
    public void getContacts(){
//        TelephonyManager tm = (TelephonyManager) this
//                .getSystemService(TELEPHONY_SERVICE);
//        String tel = tm.getLine1Number();
        try {
            ContentResolver resolver = getContentResolver();
            // 获取手机联系人
            Cursor c = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION, null,
                    null, "sort_key COLLATE LOCALIZED asc");
            Contacts bean = null;
            char sortKey = ' ';
            if (c != null && c.getCount() > 0) {
                while (c.moveToNext()) {
                    if (isPhone(c.getString(1).replace(" ", ""))) {
                        bean = new Contacts();
                        bean.name = c.getString(0);
                        bean.number = c.getString(1).replace(" ", "");
                        char ch = c.getString(2).toUpperCase().charAt(0);
                        if (ch >= 65 && ch <= 90) {
                            bean.sortKey = ch;
                        } else {
                            bean.sortKey = '#';
                        }
                        if (sortKey != bean.sortKey) {
                            sortKey = bean.sortKey;
                            bean.isHead = true;
                        }
//                Log.i("PhoneContactActivity", bean.name + ":" + bean.number);
                        beans.add(bean);
                    }
                }
            } else {
                isTow = false;
                Toast.makeText(UIUtils.getContext(), "请检查读取联系人权限是否开启", Toast.LENGTH_SHORT).show();
                if (anim.isRunning()) {
                    iv_contacts_loading.setVisibility(View.GONE);
                    anim.stop();
                }
            }
            c.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 通过手机号获取xyd数据
     * @param beans
     */
    public void searchInfo(final List<Contacts> beans){
        new Thread(new Runnable() {
            @Override
            public void run() {
                httpManager = new HttpManager();
                for(Contacts contacts : beans) {
                    String result = httpManager.searchInfo(contacts.number, "person");
                    if (result.equals("404")) {
                        contacts.isXYD = false;
                    } else {
                        if(result == null || TextUtils.isEmpty(result) || result.length() < 5){
                            contacts.isXYD = false;       //不是心意答用户
                        } else {
                            contacts.isXYD = true;
                            try {
                                JSONArray jsonArray = new JSONArray(result);
                                for(int i = 0; i < jsonArray.length(); i++){
                                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                                    if(jsonObject != null){
                                        contacts.parserJson(jsonObject);
                                        contacts.isFriender = isFriend(contacts.id);
                                    } else {
                                        contacts.isXYD = false;
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                handler.sendEmptyMessage(FIND_PHONE_SUCCEED);
            }
        }).start();
    }

    /**
     * 判断好友关系
     * @param userBId
     * @return
     */
    public boolean isFriend(String userBId){
        boolean isFriend = false;
        String result2 = httpManager.isFriend(userId, userBId);
        if (result2 != null && !result2.equals("404")) {
            if (result2.equals("true")) {
                isFriend = true;
            } else if (result2.equals("false")) {
                isFriend = false;
            }
        }
        return isFriend;
    }

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(UPDATE_FRIEND);
        // 注册广播
        PhoneContactActivity.this.registerReceiver(broadcastReceiver, myIntentFilter);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String targetId = intent.getStringExtra("targetId");
            if(targetId != null && !TextUtils.isEmpty(targetId)){
                for(Contacts contacts : beans){
                    if(contacts.id != null && !TextUtils.isEmpty(contacts.id) && contacts.id.equals(targetId)){
                        contacts.isFriender = true;
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PhoneContactActivity.this.unregisterReceiver(broadcastReceiver);
    }

    //适配器
    List<Contacts> beans = new ArrayList<Contacts>();

    public ListView getListView() {
        return (ListView) findViewById(R.id.phone_contacts_lv);
    }

    class MyAdapter extends BaseAdapter {
        LayoutInflater inflater;

        {
            inflater = LayoutInflater.from(PhoneContactActivity.this);
        }

        @Override
        public int getCount() {
            return beans.size();
        }

        @Override
        public Object getItem(int i) {
            return beans.get(i);
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        TextView name, xyd_name, name2, status;
        ShapeImageView imageIcon;
        Button btn_contacts;

        @Override
        public View getView(int i, View v, ViewGroup vg) {
            v = inflater.inflate(R.layout.item_contacts_phone, null);
            name = (TextView) v.findViewById(R.id.tv_contacts_person_name);
            name2 = (TextView) v.findViewById(R.id.tv_contacts_person_name2);
            xyd_name = (TextView) v.findViewById(R.id.contacts_xyd_name);
            imageIcon = (ShapeImageView) v.findViewById(R.id.iv_contacts_person_icon);
            status = (TextView) v.findViewById(R.id.tv_contacts_status);
            btn_contacts = (Button) v.findViewById(R.id.btn_contacts);

            final Contacts contacts = beans.get(i);
            if(contacts.isXYD){
                name.setText(contacts.name);
                xyd_name.setText("心意答空间：" + contacts.userInfo.getName());
//                String iconUrl = String.format(Constant.STUDY_GET_USER_PIC, contacts.id);
//                Log.i("PhoneContact", "任务的头像:"+iconUrl);
                Picasso.with(mContext).load(contacts.userInfo.getUserIcon()).resize((int) mContext.getResources().getDimension(R.dimen.view_43),
                        (int) mContext.getResources().getDimension(R.dimen.view_43)).placeholder(R.mipmap.ic_geren_xuanren)
                        .error(R.mipmap.ic_geren_xuanren).into(imageIcon);
                if(contacts.isFriender){
                    btn_contacts.setVisibility(View.GONE);
                    status.setVisibility(View.VISIBLE);
                    status.setText("已添加");
                }
            } else {
                name.setVisibility(View.GONE);
                xyd_name.setVisibility(View.GONE);
                name2.setVisibility(View.VISIBLE);
                name2.setText(beans.get(i).name);
                btn_contacts.setVisibility(View.GONE);
                status.setVisibility(View.VISIBLE);
                status.setText("未注册");
            }

            if (contacts.isHead) {
                v.findViewById(R.id.llShowIndex).setVisibility(View.VISIBLE);
                ((TextView) v.findViewById(R.id.tvIndex)).setText("" + contacts.sortKey);
            }

            btn_contacts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(UIUtils.getContext(), "邀请", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PhoneContactActivity.this, SendFriendRequestActivity.class);
                    intent.putExtra("name", sp.getString("name", ""));
                    intent.putExtra("oppositeId", contacts.id);
                    startActivity(intent);
                }
            });
            return v;
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("PhoneContactActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("PhoneContactActivity");
        MobclickAgent.onPause(this);
    }
}
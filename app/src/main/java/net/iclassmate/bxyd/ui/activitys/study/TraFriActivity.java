package net.iclassmate.bxyd.ui.activitys.study;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.adapter.study.TraFriAdapter;
import net.iclassmate.bxyd.bean.study.Resources;
import net.iclassmate.bxyd.bean.study.StudyMessageItem;
import net.iclassmate.bxyd.bean.study.fri.Fri;
import net.iclassmate.bxyd.bean.study.fri.FriList;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.utils.HttpManager;
import net.iclassmate.bxyd.utils.UIUtils;
import net.iclassmate.bxyd.view.study.ShapeImageView;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

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

public class TraFriActivity extends FragmentActivity implements View.OnClickListener {
    private TextView tv_back, tv_sure, tv_title;
    private ImageView img_anim;
    private AnimationDrawable anim;
    private ListView listView;
    private List<Object> list;
    private List<Fri> listMsg;
    private List<String> listStr;
    private TraFriAdapter adapter;
    private List<Fri> listSelect;
    private LinearLayout linear_select, linear_tra_group;
    private HorizontalScrollView horizontalScrollView;
    private Context mContext;
    private SharedPreferences sharedPreferences;
    private String userid;
    private StudyMessageItem message;
    public static final String NEW_MESSAGE = "NEW_MESSAGE";
    private HttpManager httpManger;
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int waht = msg.what;
            anim.stop();
            img_anim.setBackgroundColor(Color.parseColor("#f5f5f9"));
            if (waht == 1) {
                int code = msg.arg1;
                if (code == 200) {
                    img_anim.setVisibility(View.GONE);
                    String ret = (String) msg.obj;
                    try {
                        JSONArray array = new JSONArray(ret);
                        FriList friList = new FriList();
                        friList.parserJson(array);
                        if (friList != null && friList.getList() != null) {
                            listMsg.addAll(friList.getList());
                            soreList();
                        } else {
                            img_anim.setVisibility(View.VISIBLE);
                            img_anim.setImageResource(R.mipmap.ic_no_result);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        img_anim.setVisibility(View.VISIBLE);
                        img_anim.setImageResource(R.mipmap.img_jiazaishibai);
                    }
                }
            } else if (waht == 2) {
                Fri fri = (Fri) msg.obj;
                sendMessage2Fri(fri.getFriendId());
                int index = msg.arg1;
                if (index == listSelect.size() - 1) {
                    tv_sure.setClickable(true);
                    Toast.makeText(mContext, "已转发给好友", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else if (waht == 404) {
                img_anim.setVisibility(View.VISIBLE);
                img_anim.setImageResource(R.mipmap.img_jiazaishibai);
            }
            adapter.notifyDataSetChanged();
        }
    };

    private void soreList() {
        for (int i = 0; i < listMsg.size(); i++) {
            Fri fri = listMsg.get(i);
            if (fri.getUserName() == null || fri.getUserName().equals("")) {
                listMsg.remove(i);
            }
        }
        char ch = ' ';
        if (listMsg != null && listMsg.size() > 0) {
            //获取名字首字母
            for (int i = 0; i < listMsg.size(); i++) {
                Fri fri = listMsg.get(i);
                String name = fri.getRemark();
                if (name == null || name.equals("") || name.equals("null")) {
                    name = fri.getUserName();
                }
                if (name != null && name.length() > 0) {
                    String ret = name.toUpperCase();
                    ch = ret.charAt(0);
                    if (ch >= 'A' && ch <= 'Z') {
                        fri.setKey(ch);
                        listMsg.set(i, fri);
                    } else {
                        //中文
                        char[] c = name.toCharArray();
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
                                ret = ch + "".toUpperCase();
                                ch = ret.charAt(0);
                                if (ch >= 'A' && ch <= 'Z') {
                                    fri.setKey(ch);
                                    listMsg.set(i, fri);
                                } else {
                                    fri.setKey('#');
                                    listMsg.set(i, fri);
                                }
                            }
                        } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                            badHanyuPinyinOutputFormatCombination.printStackTrace();
                        }
                    }
                }
            }

            //对名字首字母进行排序
            for (int i = 0; i < listMsg.size() - 1; i++) {
                for (int j = 0; j < listMsg.size() - 1 - i; j++) {
                    Fri fri1 = listMsg.get(j);
                    Fri fri2 = listMsg.get(j + 1);
                    if (fri1.getKey() > fri2.getKey()) {
                        listMsg.set(j, fri2);
                        listMsg.set(j + 1, fri1);
                    }
                }
            }

            //添加数据到list集合
            ch = ' ';
            for (int i = 0; i < listMsg.size(); i++) {
                if (ch != listMsg.get(i).getKey()) {
                    ch = listMsg.get(i).getKey();
                    list.add(ch + "");
                    list.add(listMsg.get(i));
                } else if (ch == listMsg.get(i).getKey()) {
                    list.add(listMsg.get(i));
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tra_fri);

        sharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCES, MODE_PRIVATE);
        userid = sharedPreferences.getString(Constant.ID_USER, "");
        Intent intent = getIntent();
        message = (StudyMessageItem) intent.getSerializableExtra("msg");

        httpManger = new HttpManager();
        initView();
        initEvnet();
        loadData();
    }

    private void initView() {
        mContext = this;
        tv_back = (TextView) findViewById(R.id.tra_fri_back);
        tv_sure = (TextView) findViewById(R.id.tra_fri_sure);
        listView = (ListView) findViewById(R.id.tra_fri_listview);
        linear_select = (LinearLayout) findViewById(R.id.tra_fri_container_linear);
        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.tra_fri_horizontalscrollview);
        tv_title = (TextView) findViewById(R.id.tra_fri_title);
        tv_title.setText("选择联系人");
        linear_tra_group = (LinearLayout) findViewById(R.id.tra_select_group);
        linear_tra_group.setOnClickListener(this);
        img_anim = (ImageView) findViewById(R.id.img_anim);
        anim = (AnimationDrawable) img_anim.getBackground();
        anim.start();

        list = new ArrayList<>();
        listMsg = new ArrayList<>();
        listStr = new ArrayList<>();
        listSelect = new ArrayList<>();
        adapter = new TraFriAdapter(this, list);
        listView.setAdapter(adapter);
        adapter.setImgCheckImg(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = (int) view.getTag();
                Object object = list.get(index);
                if (object instanceof Fri) {
                    Fri fri = (Fri) object;
                    fri.setCheck(!fri.isCheck());
                    list.set(index, fri);
                    adapter.notifyDataSetChanged();
                    getSelectCount();
                }
            }
        });
    }

    private void initEvnet() {
        tv_back.setOnClickListener(this);
        tv_sure.setOnClickListener(this);
    }

    private void loadData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(String.format(Constant.STUDY_GET_FRI_LIST, userid)).build();
//                Log.i("info", "好友列表=" + String.format(Constant.STUDY_GET_FRI_LIST, userid));
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
                } catch (IOException e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(404);
                }
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tra_fri_back:
                this.finish();
                break;
            case R.id.tra_fri_sure:
                if (listSelect.size() == 0) {
                    Toast.makeText(this, "请选择好友", Toast.LENGTH_SHORT).show();
                    return;
                }
                tv_sure.setClickable(false);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (int i = 0; i < listSelect.size(); i++) {
                                Fri fri = listSelect.get(i);
                                Message message = new Message();
                                message.what = 2;
                                message.arg1 = i;
                                message.obj = fri;
                                mHandler.sendMessage(message);

                                Thread.sleep(200);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            case R.id.tra_select_group:
                Intent intent = new Intent(mContext, SelectGroupActivity.class);
                intent.putExtra("msg", message);
                startActivity(intent);
                break;
        }
    }


    private void getSelectCount() {
        listSelect.clear();
        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            Object object = list.get(i);
            if (object instanceof Fri) {
                Fri message = (Fri) object;
                if (message.isCheck()) {
                    count++;
                    listSelect.add(message);
                }
            }
        }
        if (count == 0) {
            tv_sure.setText("确定");
            linear_select.setVisibility(View.GONE);
        } else if (count > 0) {
            tv_sure.setText("确定(" + count + ")");
            linear_select.setVisibility(View.VISIBLE);
        }
        linear_select.removeAllViews();
        for (int i = 0; i < listSelect.size(); i++) {
            String url = String.format(Constant.STUDY_GET_USER_PIC, listSelect.get(i).getFriendId());
            View view = LayoutInflater.from(this).inflate(R.layout.tra_fri_select_linear_item, null);
            ShapeImageView img = (ShapeImageView) view.findViewById(R.id.tra_fri_select_img);
            setUImage(listSelect.get(i).getFriendId(), img);
            view.setTag(url);
            linear_select.addView(view);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                horizontalScrollView.smoothScrollBy(Integer.MAX_VALUE / 4, 0);
            }
        }, 200);
    }

    public void setUImage(final String uid, final ShapeImageView img) {
        img.setTag(uid);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String url = httpManger.getUserIconUrl(uid);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        String tag = (String) img.getTag();
                        if (tag.equals(uid)) {
                            if (url == null || url.equals("")) {
                                img.setImageResource(R.mipmap.ic_head_wode);
                            } else {
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

    //转发给好友
    private void sendMessage2Fri(final String targetId) {
        if (message == null) {
            return;
        }
        String content = message.getContent();
        String name = message.getCreateBy().getName();
        JSONObject json = new JSONObject();
        JSONObject jsonBull = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonBull.put("author", message.getCreateBy().getName());
            jsonBull.put("bulletinId", message.getId());
            jsonBull.put("bulletinUrl", "");
            jsonBull.put("content", content);
            String bulletinType = message.getBulletinType();
            if (bulletinType == null) {
                return;
            }
            bulletinType = bulletinType.toUpperCase();
            List<Resources> list = new ArrayList<>();
            if (bulletinType.equals("ORIGIN")) {
                List<Resources> list1 = message.getList();
                if (list1 != null) {
                    list.addAll(list1);
                }
            } else if (bulletinType.equals("FORWARD")) {
                if (message.getOriginBulletinInfo() != null) {
                    List<Resources> list1 = message.getOriginBulletinInfo().getList();
                    if (list1 != null) {
                        list.addAll(list1);
                    }
                }
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
            json.put("ChatType", 0);
            json.put("Content", "[好友动态]");
            json.put("FontSize", 14);
            json.put("FontStyle", 0);
            json.put("FontColor", 0);
            json.put("BulletinID", message.getId());
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

            final Conversation.ConversationType type = Conversation.ConversationType.PRIVATE;
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
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("TranFriActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("TranFriActivity");
        MobclickAgent.onPause(this);
    }

}
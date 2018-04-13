package net.iclassmate.bxyd.ui.activitys.study.openfile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.adapter.study.AlbumOpenAdapter;
import net.iclassmate.bxyd.adapter.study.MyViewPagerAdapter;
import net.iclassmate.bxyd.bean.study.ImageState;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.utils.NetWorkUtils;
import net.iclassmate.bxyd.view.study.PhotoViewPager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.sql.Connection;
import java.util.Collections;
import java.util.List;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ImageMessage;

public class OpenAlbumPicActivity extends Activity implements View.OnClickListener {
    private List<ImageState> imageList;
    private int index, chatType;
    private String tid;
    private List<Object> selectList;
    private PhotoViewPager viewPager;
    private AlbumOpenAdapter adapter;
    private Context mContext;

    private TextView tv_back, tv_sure;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_album_pic);

        initData();
        initView();
    }

    private void initData() {
        Intent intent = getIntent();
        imageList = (List<ImageState>) intent.getSerializableExtra("all");
        index = intent.getIntExtra("index", 0);
        selectList = (List<Object>) intent.getSerializableExtra("list");
        chatType = intent.getIntExtra("chatType", 0);
        tid = intent.getStringExtra("tid");

        sharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCES, MODE_PRIVATE);
    }

    private void initView() {
        mContext = this;
        viewPager = (PhotoViewPager) findViewById(R.id.album_viewpager);
        tv_back = (TextView) findViewById(R.id.tv_pic_back);
        tv_sure = (TextView) findViewById(R.id.tv_pic_release);
        tv_back.setOnClickListener(this);
        tv_sure.setOnClickListener(this);
        if (imageList != null) {
            Collections.reverse(imageList);
            adapter = new AlbumOpenAdapter(mContext, imageList);
            viewPager.setAdapter(adapter);
            if (index >= 0) {
                viewPager.setCurrentItem(index);
            }
            adapter.setOnCheckListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    ImageState state = imageList.get(position);
                    int count = 0;
                    for (int i = 0; i < imageList.size(); i++) {
                        ImageState state1 = imageList.get(i);
                        if (state1.check) {
                            count++;
                        }
                    }
                    if (!state.check) {
                        count++;
                    }
                    if (count > 9) {
                        Toast.makeText(mContext, "最多选择9个图片", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    state.check = !state.check;
                    imageList.set(position, state);
                    View view = viewPager.findViewWithTag(position);
                    ImageView imgCheck = (ImageView) view.findViewById(R.id.img_check);
                    if (state.check) {
                        imgCheck.setImageResource(R.mipmap.ic_checked);
                    } else {
                        imgCheck.setImageResource(R.mipmap.ic_unchecked);
                    }
                    setTitle();
                }
            });
        }
        setTitle();
    }

    private void setTitle() {
        if (selectList != null) {
            selectList.clear();
            for (int i = 0; i < imageList.size(); i++) {
                ImageState state = imageList.get(i);
                if (state.check) {
                    selectList.add(state.path);
                }
            }
        }
        int count = selectList.size();
        if (count > 0) {
            tv_sure.setText("确定(" + count + ")");
            tv_sure.setClickable(true);
            tv_sure.setTextColor(0xff65caff);
        } else {
            tv_sure.setText("确定");
            tv_sure.setClickable(false);
            tv_sure.setTextColor(0x7765caff);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_pic_back:
                this.finish();
                break;
            case R.id.tv_pic_release:
                sendImageMessage(selectList);
                Intent intent = new Intent();
                setResult(1, intent);
                finish();
                break;
        }
    }

    //发送图片
    private void sendImageMessage(List<Object> listSelectAll) {
        for (int i = 0; i < listSelectAll.size(); i++) {
            UserInfo userInfo = new UserInfo(sharedPreferences.getString(Constant.ID_USER, ""), sharedPreferences.getString(Constant.USER_NAME, ""), null);
            final ImageMessage imageMessage = ImageMessage.obtain(Uri.parse("file://" + listSelectAll.get(i)), Uri.parse("file://" + listSelectAll.get(i)));

            JSONObject json = new JSONObject();
            try {
                json.put("type", chatType);
                json.put("fileid", "");
                json.put("name", listSelectAll.get(i).toString());
                long size = 0;
                try {
                    File file = new File(listSelectAll.get(i).toString());
                    size = file.getTotalSpace();
                } catch (Exception e) {

                }
                json.put("size", size);
                json.put("CRC", "");
                json.put("memo", "");
                long t = System.currentTimeMillis() + i;
                json.put("createTime", t);
                json.put("chatType", chatType);
                json.put("uri", imageMessage.getRemoteUri());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final String messageBody = json.toString();
            imageMessage.setUserInfo(userInfo);
            imageMessage.setExtra(messageBody);
            Conversation.ConversationType type = null;
            if (chatType == 0) {
                type = Conversation.ConversationType.PRIVATE;
            } else if (chatType == 1 || chatType == 2) {
                type = Conversation.ConversationType.GROUP;
            }
            RongIMClient.getInstance().sendImageMessage(type, tid, imageMessage, null, null, new RongIMClient.SendImageMessageCallback() {
                @Override
                public void onAttached(Message message) {

                }

                @Override
                public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                    Intent intent = new Intent();
                    intent.setAction("message_copy");
                    intent.putExtra("new_message", "message");
                    Bundle bundle = new Bundle();
                    message.setSentStatus(Message.SentStatus.FAILED);
                    bundle.putParcelable("message", message);
                    intent.putExtras(bundle);
                    mContext.sendBroadcast(intent);
                }

                @Override
                public void onSuccess(Message message) {
                    Intent intent = new Intent();
                    intent.setAction("message_copy");
                    intent.putExtra("new_message", "message");
                    Bundle bundle = new Bundle();
                    if (!NetWorkUtils.isNetworkAvailable(mContext)) {
                        message.setSentStatus(Message.SentStatus.FAILED);
                    }
                    bundle.putParcelable("message", message);
                    intent.putExtras(bundle);
                    mContext.sendBroadcast(intent);
                }

                @Override
                public void onProgress(Message message, int i) {

                }
            });
        }

    }
}

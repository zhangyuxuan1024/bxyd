package net.iclassmate.bxyd.ui.activitys.chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.adapter.teachlearn.ChatFileAdapter;
import net.iclassmate.bxyd.bean.message.MessageFile;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.ui.activitys.study.IsWifiActivity;
import net.iclassmate.bxyd.utils.FileUtils;
import net.iclassmate.bxyd.utils.HttpManager;
import net.iclassmate.bxyd.utils.NetWorkUtils;
import net.iclassmate.bxyd.utils.OpenFile;
import net.iclassmate.bxyd.utils.UIUtils;
import net.iclassmate.bxyd.view.TitleBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.ImageMessage;
import io.rong.message.TextMessage;

public class ChatFileActivity extends FragmentActivity implements TitleBar.TitleOnClickListener, View.OnClickListener{

    private Context mContext;
    private TitleBar titleBar;
    private RelativeLayout loading_layout;   //动画
    private ImageView animation_view;   //动画
    private ImageView chat_file_wu;

//    private SwipeRefreshLayout swipeRefreshLayout;
//    private RecyclerView recyclerView;
    private GridView gv;
    private ChatFileAdapter adapter;

    private AnimationDrawable anim;

    private int oldestMessageId = -1;    //最后一条消息的 Id，获取此消息之前的 count 条消息,没有消息第一次调用应设置为:-1
    private String targetId, from;
    private String rightIcon;   //右上角文字按钮名称
    private Conversation.ConversationType conversationType;
    private List<MessageFile> list;
    private HttpManager httpManager;
    //要打开视频的id和名字
    private String videoid, videoname;

    private static final int NO_INTERNET = 0;    //没网络
    private static final int FIND_RECORD_FILE_SUCCEED = 1; //获取聊天记录生成文件成功
    private static final int FIND_RECORD_FILE_NULL = 3; //没有聊天文件
    private static final int REQ_WIFI = 2;

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case NO_INTERNET:
                    if(anim.isRunning()){
                        loading_layout.setVisibility(View.GONE);
                        anim.stop();
                    }
                    Toast.makeText(UIUtils.getContext(), "您没有连接网络，请连接后重试！", Toast.LENGTH_SHORT).show();
                    break;

                case FIND_RECORD_FILE_SUCCEED:  //获取聊天记录生成文件成功
                    if(anim.isRunning()){
                        loading_layout.setVisibility(View.GONE);
                        anim.stop();
                    }
                    if(null != list && list.size() > 0) {
                        adapter.notifyDataSetChanged();
                    }else{
                        chat_file_wu.setVisibility(View.VISIBLE);
                    }
                    break;
                case FIND_RECORD_FILE_NULL: //没有聊天文件
                    if(anim.isRunning()){
                        loading_layout.setVisibility(View.GONE);
                        anim.stop();
                    }
                    chat_file_wu.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat_file);
        mContext=this;
        initView();
        initData();
        initListener();
    }

    private void initView() {
        titleBar= (TitleBar) findViewById(R.id.chat_file_bar);
        titleBar.setTitle("聊天文件");
        titleBar.setLeftIcon(R.mipmap.ic_fanhui,"返回");
//        rightIcon = "选择";
        titleBar.setRightIcon(rightIcon);

//        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.chat_file_srl);
//        recyclerView = (RecyclerView) findViewById(R.id.chat_file_recycler);
//        swipeRefreshLayout.setRefreshing(false);
//        swipeRefreshLayout.setColorSchemeColors(R.color.chat_file_back,R.color.chat_file_back,R.color.chat_file_back,R.color.chat_file_back);
//        recyclerView.setLayoutManager(new GridLayoutManager(this,4));
        gv = (GridView) findViewById(R.id.chat_file_gv);
        chat_file_wu = (ImageView) findViewById(R.id.chat_file_wu);


        loading_layout = (RelativeLayout) findViewById(R.id.loading_layout);
        animation_view = (ImageView) findViewById(R.id.animation_view);
    }

    private void initListener() {
        titleBar.setTitleClickListener(this);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MessageFile messageFile = list.get(position);
                if(messageFile.getObjectName().equals("RC:TxtMsg")){
                    //是视频并且处于非wifi状态下
                    if (!NetWorkUtils.isNetworkAvailable(mContext)) {
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.alert_msg_check_net), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (OpenFile.isVideo(messageFile.getFileName()) && !FileUtils.isWifiActive(mContext)) {
                        videoid = messageFile.getFileId();
                        videoname = messageFile.getFileName();
                        Intent intent = new Intent(mContext, IsWifiActivity.class);
                        startActivityForResult(intent, REQ_WIFI);
                    } else {
                        OpenFile.openFile(messageFile.getFileId(), messageFile.getFileName(), 3, mContext);
                    }
                }else if(messageFile.getObjectName().equals("RC:ImgMsg")){
                    List<Message> list = new ArrayList<Message>();
                    list.add(messageFile.getMessage());
                    view.setTag(messageFile.getMessage());
                    OpenFile.openPic(view, mContext, list);
                }
            }
        });

        adapter.setOnClickIsSeleck(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.i("TAG", "ttttttttttttt:"+(int)v.getTag());
//                Log.i("TAG", "yyyyy:"+list.get((int)v.getTag()).toString());
                list.get((int)v.getTag()).setCheck(!list.get((int)v.getTag()).isCheck());
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void initData() {
        anim = (AnimationDrawable) animation_view.getBackground();
        loading_layout.setVisibility(View.VISIBLE);
        anim.start();

        httpManager = new HttpManager();
        list = new ArrayList<>();
        adapter = new ChatFileAdapter(this, list);
        gv.setAdapter(adapter);

        from = getIntent().getStringExtra("from");
        targetId = getIntent().getStringExtra("targetId");
        if(from.equals("person")){
            conversationType = Conversation.ConversationType.PRIVATE;
        } else if(from.equals("group")){
            conversationType = Conversation.ConversationType.GROUP;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                if(NetWorkUtils.isNetworkAvailable(UIUtils.getContext()))
                {
                    getHistoryMessages();
//                    Log.i("TAG","11聊天文件："+list.toString());
//                    handler.sendEmptyMessage(FIND_RECORD_FILE_SUCCEED);
                }else
                {
                    handler.sendEmptyMessage(NO_INTERNET);
                }
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == REQ_WIFI){
                OpenFile.openFile(videoid, videoname, 3, mContext);
            }
        }
    }

    @Override
    public void leftClick() {
        finish();
    }

    @Override
    public void rightClick() {
        if(rightIcon.equals("选择")){
            for(MessageFile messageFile : list){
                messageFile.setVisibility(true);
            }
            rightIcon = "取消";
            titleBar.setRightIcon(rightIcon);
            adapter.notifyDataSetChanged();
        }else if(rightIcon.equals("取消")){
            for(MessageFile messageFile : list){
                messageFile.setVisibility(false);
            }
            rightIcon = "选择";
            titleBar.setRightIcon(rightIcon);
            adapter.notifyDataSetChanged();
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

    }

    /**
     * 获取聊天记录
     */
    public void getHistoryMessages(){
        RongIMClient.getInstance().getHistoryMessages(conversationType, targetId, oldestMessageId, 50, new RongIMClient.ResultCallback<List<Message>>() {
            @Override
            public void onSuccess(List<Message> messageList) {
                if(messageList != null && messageList.size() > 0) {
                    messageFileData(messageList);
                    Log.i("ChatFileActivity", "获取的数据是：" + list.toString());
//                    Log.i("TAG","11聊天文件："+list.toString());
                    handler.sendEmptyMessage(FIND_RECORD_FILE_SUCCEED);
                }else{
                    handler.sendEmptyMessage(FIND_RECORD_FILE_NULL);
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }

    /**
     * 获取聊天记录中的聊天文件
     * @param messageList
     */
    public void messageFileData(List<Message> messageList){
        Log.i("TAG","messageList大小:"+messageList.size());
        for(Message message : messageList){
            if(message.getObjectName().equals("RC:TxtMsg")){
                TextMessage textMessage = (TextMessage) message.getContent();
                String info = textMessage.getContent();
                try {
                    JSONObject jsonObject = new JSONObject(info);
                    String fileID = null;
                    String fileName = null;
                    long time = 0;
                    String url = null;
                    int contentTpye = jsonObject.getInt("ContentType");
                    fileID = jsonObject.getString("FileID");
                    fileName = jsonObject.getString("FileName");
                    time = jsonObject.getLong("CreateTime");
                    if(null != fileName && !TextUtils.isEmpty(fileName)){
                        contentTpye = FileUtils.getContentType(fileName);
                    }
                    messageFileData2(contentTpye, message, fileID, fileName, time, url);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if(message.getObjectName().equals("RC:ImgMsg")){
                ImageMessage imageMessage = (ImageMessage)message.getContent();
                Uri uri = imageMessage.getThumUri();
                String str = imageMessage.getExtra();
                long time = 0;
                String fileName = null;
                try {
                    JSONObject jsonObject = new JSONObject(str);
                    time = jsonObject.getLong("createTime");
                    fileName = jsonObject.getString("name").toString();
                    list.add(new MessageFile("RC:ImgMsg", time, uri, fileName, message.getMessageId(), message));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("ChatFileActivity","图片消息，时间："+time+",uri："+uri+",fileName："+fileName+",message："+message);
            }
        }
    }

    //用于区分文件的类型
    public void messageFileData2(int contentTpye, Message message, String fileID, String fileName, long time, String url)
    {
        switch (contentTpye){
            case 1: //普通消息
//                Log.i("ChatFileActivity","普通消息");
                break;

            case 2: //图片文件
                list.add(new MessageFile("RC:TxtMsg", time, fileID, fileName, 2, message.getMessageId(), message));
                Log.i("ChatFileActivity","网盘消息--图片，fileID:"+fileID+",fileName:"+fileName+",time:"+time);
                break;

            case 3: //音频文件
                list.add(new MessageFile("RC:TxtMsg", time, fileID, fileName, 3, message.getMessageId(), message));
//                Log.i("ChatFileActivity","网盘消息--音频，fileID:"+fileID+",fileName:"+fileName+",time:"+time);
                break;

            case 4: //视频文件
                list.add(new MessageFile("RC:TxtMsg", time, fileID, fileName, 4, message.getMessageId(), message));
                Log.i("ChatFileActivity","网盘消息--视频，fileID:"+fileID+",fileName:"+fileName+",time:"+time);
                break;

            case 11: //文本文件
                if(null != fileName && !TextUtils.isEmpty(fileName)){
                    contentTpye = FileUtils.getContentType(fileName);
                }
                messageFileData3(contentTpye, message, fileID, fileName, time, url);
                break;
        }
    }

    //用于区分网盘文件类型
    public void messageFileData3(int contentTpye, Message message, String fileID, String fileName, long time, String url) {
        switch (FileUtils.getContentType(fileName)) {
            case 1: //普通消息
//                Log.i("ChatFileActivity", "普通消息");
                break;

            case 2: //图片文件
                String.format(Constant.MESSAGE_GET_FILE_DETIAL, fileID);
                list.add(new MessageFile("RC:TxtMsg", time, fileID, fileName, 2,message.getMessageId(), message));
                Log.i("ChatFileActivity", "网盘消息--图片，fileID:" + fileID + ",fileName:" + fileName + ",time:" + time);
                break;

            case 3: //音频文件
                list.add(new MessageFile("RC:TxtMsg", time, fileID, fileName, 3, message.getMessageId(), message));
//                Log.i("ChatFileActivity", "网盘消息--音频，fileID:" + fileID + ",fileName:" + fileName + ",time:" + time);
                break;

            case 4: //视频文件
                list.add(new MessageFile("RC:TxtMsg", time, fileID, fileName, 4, message.getMessageId(), message));
                Log.i("ChatFileActivity", "网盘消息--视频，fileID:" + fileID + ",fileName:" + fileName + ",time:" + time);
                break;

            case 11: //文本文件
                list.add(new MessageFile("RC:TxtMsg", time, fileID, fileName, contentTpye, message.getMessageId(), message));
                Log.i("ChatFileActivity", "网盘消息--文本，fileID:" + fileID + ",fileName:" + fileName + ",time:" + time);
                break;
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("ChatFileActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("ChatFileActivity");
        MobclickAgent.onPause(this);
    }

}

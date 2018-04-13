package net.iclassmate.bxyd.ui.activitys.chat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.bean.message.RMessage;
import net.iclassmate.bxyd.bean.message.SpaceMessage;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.utils.BitmapUtils;
import net.iclassmate.bxyd.utils.FileUtils;
import net.iclassmate.bxyd.utils.HttpManager;
import net.iclassmate.bxyd.utils.NetWorkUtils;
import net.iclassmate.bxyd.utils.UIUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ImageMessage;
import io.rong.message.TextMessage;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SendCopyMessageActivity extends FragmentActivity implements View.OnClickListener {
    private int messageId;
    private String targetId;
    private int chatType;
    private Message msgMain;
    private Context mContext;
    private TextView tv_cancel, tv_send;
    private SharedPreferences sharedPreferences;

    private FrameLayout frameLayout;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    String filename = (String) msg.obj;
                    sendMessage1(filename);
                    break;
            }
        }
    };
    private HttpManager httpManager;

    int resend = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_copy_message);
        mContext = this;
        sharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCES, MODE_PRIVATE);
        httpManager = new HttpManager();
        initView();
        initData();
    }

    private void initView() {
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        tv_send = (TextView) findViewById(R.id.tv_send);
        tv_cancel.setOnClickListener(this);
        tv_send.setOnClickListener(this);
        frameLayout = (FrameLayout) findViewById(R.id.message_view_container);
    }

    private void initData() {
        Intent intent = getIntent();
        messageId = intent.getIntExtra(Constant.MESSAGE_ID, -1);
        targetId = intent.getStringExtra("tid");
        chatType = intent.getIntExtra(Constant.CHAT_TYPE, 0);
//        Log.i("info", "消息id=" + messageId + ",tid=" + targetId + ",chatType=" + chatType);
        RongIMClient.getInstance().getMessage(messageId, new RongIMClient.ResultCallback<Message>() {
            @Override
            public void onSuccess(Message message) {
                msgMain = message;
                setView(msgMain);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                msgMain = null;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                finish();
                break;
            case R.id.tv_send:
                sendMessage1("");
                break;
        }
    }

    private void sendMessage1(String filename) {
        if (msgMain == null) {
            Toast.makeText(mContext, "粘贴消息失败，请稍后重试！", Toast.LENGTH_SHORT).show();
        } else {
            if (messageId > 0 && targetId != null) {
                if (chatType == Constant.CHAT_TYPE_PRIVATE) {
                    sendMessage(msgMain, filename, Conversation.ConversationType.PRIVATE);
                } else if (chatType == Constant.CHAT_TYPE_GROUP || chatType == Constant.CHAT_TYPE_GROUPCHAT) {
                    sendMessage(msgMain, filename, Conversation.ConversationType.GROUP);
                }
            }
        }
        finish();
    }

    //发送文本
    private void sendMessage(final Message message, String filename, final Conversation.ConversationType type) {
        if (message.getContent() instanceof TextMessage || message.getContent() instanceof SpaceMessage) {
            Message tvMessage = null;
            if (message.getContent() instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message.getContent();
                tvMessage = Message.obtain(message.getTargetId(), message.getConversationType(), textMessage);
                sendRongIMClient(message, textMessage, type);
            } else if (message.getContent() instanceof SpaceMessage) {
                SpaceMessage spaceMessage = (SpaceMessage) message.getContent();
                tvMessage = Message.obtain(message.getTargetId(), message.getConversationType(), spaceMessage);
                sendRongIMClient(message, spaceMessage, type);
            }
        } else if (message.getContent() instanceof ImageMessage) {
            RMessage rMessage = new RMessage(message);
            String icon = sharedPreferences.getString(Constant.USER_ICON, "");
            Uri uri = Uri.parse(icon);
            UserInfo userInfo = new UserInfo(sharedPreferences.getString(Constant.ID_USER, ""), sharedPreferences.getString(Constant.USER_NAME, ""), uri);
            final ImageMessage imageMessage = ImageMessage.obtain();
            JSONObject json = new JSONObject();
            try {
                json.put("type", 1);
                json.put("fileid", "");
//                json.put("name", FileUtils.getFileNameFullPath(rMessage.getName()));
                json.put("name", rMessage.getName());
                json.put("size", rMessage.getSize());
                json.put("CRC", "");
                json.put("memo", "");
                json.put("createTime", System.currentTimeMillis());
                json.put("chatType", chatType);
                json.put("uri", imageMessage.getRemoteUri());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final String messageBody = json.toString();
            imageMessage.setUserInfo(userInfo);
            imageMessage.setExtra(messageBody);
            ImageMessage im = (ImageMessage) message.getContent();
            imageMessage.setThumUri(im.getThumUri());
            imageMessage.setRemoteUri(im.getRemoteUri());
            imageMessage.setBase64(im.getBase64());
            imageMessage.setIsFull(true);
            Uri localUri = im.getLocalUri();
            File file = null;
            String name = rMessage.getName();
            if (name.contains("/")) {
                int index = name.lastIndexOf("/");
                name = name.substring(index + 1, name.length());
            }
            try {
                file = new File(localUri.getPath());
            } catch (NullPointerException e) {
                if (filename != null && !filename.equals("")) {
                    localUri = Uri.parse("file://" + filename);
                } else if (filename != null && filename.equals("")) {
                    String path = FileUtils.getSdCardPath();
                    File file1 = new File(path, name);
                    filename = file1.getAbsolutePath();
                    if (file1.exists()) {
                        localUri = Uri.parse("file://" + filename);
                    } else {
                        downImage(name, im.getRemoteUri());
                        return;
                    }
                } else if (file == null && resend-- > 0) {
                    downImage(name, im.getRemoteUri());
                    return;
                }
            }
            imageMessage.setLocalUri(localUri);
            RongIMClient.getInstance().sendImageMessage(type, targetId, imageMessage, null, null, new RongIMClient.SendImageMessageCallback() {
                @Override
                public void onAttached(Message message) {
                    Intent intent = new Intent();
                    intent.setAction("message_copy");
                    intent.putExtra("new_message", "message");
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("message", message);
                    intent.putExtras(bundle);
                    mContext.sendBroadcast(intent);
                }

                @Override
                public void onError(Message message, RongIMClient.ErrorCode errorCode) {

                }

                @Override
                public void onSuccess(Message message) {

                }

                @Override
                public void onProgress(Message message, int i) {
                }
            });

            /*
            RMessage rMessage = new RMessage(message);
            ImageMessage imageMessage = (ImageMessage) message.getContent();
            JSONObject json = new JSONObject();
            try {
                json.put("MessageType", 0);
                json.put("ContentType", 2);
                json.put("ChatType", chatType);
                json.put("Content", "[图片]");
                json.put("FontSize", 14);
                json.put("FontStyle", 0);
                json.put("FontColor", 0);
                json.put("BulletinID", "");
                json.put("BulletinContent", "");
                json.put("requestName", sharedPreferences.getString(Constant.USER_NAME, ""));
                json.put("requestRemark", "");
                json.put("requestGroupId", "");
                json.put("FileID", imageMessage.getRemoteUri());
                json.put("FileName", System.currentTimeMillis() + ".png");
                json.put("CreateTime", System.currentTimeMillis());
                json.put("FileSize", rMessage.getSize());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final String messageBody = json.toString();
            if (messageBody.equals("")) {
                Toast.makeText(UIUtils.getContext(), "发送内容不能为空", Toast.LENGTH_SHORT).show();
            } else {
                UserInfo userInfo = new UserInfo(sharedPreferences.getString(Constant.ID_USER, ""), sharedPreferences.getString(Constant.USER_NAME, ""), null);
                final TextMessage textMessage = TextMessage.obtain(messageBody);
                textMessage.setUserInfo(userInfo);

                //添加消息
                final Message message2 = Message.obtain(targetId, type, textMessage);
                message2.setMessageDirection(Message.MessageDirection.SEND);
                message2.setSenderUserId(sharedPreferences.getString(Constant.ID_USER, ""));

                RongIMClient.getInstance().sendMessage(type, targetId,
                        textMessage, null, null, new RongIMClient.SendMessageCallback() {
                            @Override
                            public void onSuccess(Integer integer) {
                                Message message = Message.obtain(targetId, type, textMessage);
                                message.setMessageDirection(Message.MessageDirection.SEND);
                                message.setSenderUserId(sharedPreferences.getString(Constant.ID_USER, ""));
                            }

                            @Override
                            public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {
//                                android.os.Message message1 = new android.os.Message();
//                                message1.what = SEND_FAIL;
//                                message1.obj = message;
//                                handler.sendMessage(message1);
                            }
                        }, new RongIMClient.ResultCallback<Message>() {
                            @Override
                            public void onSuccess(Message message) {
                                //发送广播
                                Intent intent = new Intent();
                                intent.setAction("message_copy");
                                intent.putExtra("new_message", "message");
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("message", message);
                                intent.putExtras(bundle);
                                mContext.sendBroadcast(intent);
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {
                            }
                        });
            }
            */
        }
    }

    private void downImage(final String filenmae, final Uri remoteUri) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                OkHttpClient client = builder.build();
                String url = remoteUri.toString();
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        InputStream stream = response.body().byteStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(stream);
                        FileUtils.writeBitmap2sd(bitmap, filenmae);
                        String path = FileUtils.getSdCardPath();
                        File file = new File(path, filenmae);
                        android.os.Message message = new android.os.Message();
                        message.what = 1;
                        message.obj = file.getAbsolutePath();
                        mHandler.sendMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void sendRongIMClient(final Message message, final MessageContent msgContent, final Conversation.ConversationType type) {
        String name = sharedPreferences.getString(Constant.USER_NAME, "");
        JSONObject json = new JSONObject();
        try {
            RMessage rMessage = new RMessage(message);
            json.put("MessageType", rMessage.getMessageType());
            json.put("ContentType", rMessage.getContentType());
            json.put("ChatType", chatType);
            json.put("Content", rMessage.getContent());
            json.put("FontSize", 14);
            json.put("FontStyle", 0);
            json.put("FontColor", 0);
            json.put("BulletinID", "");
            json.put("BulletinContent", "");
            json.put("requestName", name);
            json.put("requestRemark", "");
            json.put("requestGroupId", "");
            json.put("FileID", rMessage.getFileID());
            json.put("FileName", rMessage.getFileName());
            json.put("CreateTime", System.currentTimeMillis());
            json.put("FileSize", rMessage.getSize());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String messageBody = json.toString();

        String icon = sharedPreferences.getString(Constant.USER_ICON, "");
        Uri uri = Uri.parse(icon);
        final UserInfo userInfo = new UserInfo(sharedPreferences.getString(Constant.ID_USER, ""), sharedPreferences.getString(Constant.USER_NAME, ""), uri);
        final TextMessage textMessage = TextMessage.obtain(messageBody);
        textMessage.setUserInfo(userInfo);

        //添加消息
        final Message message2 = Message.obtain(message.getTargetId(), message.getConversationType(), textMessage);
        message2.setMessageDirection(Message.MessageDirection.SEND);
        message2.setSenderUserId(sharedPreferences.getString(Constant.ID_USER, ""));

        RongIMClient.getInstance().sendMessage(type, targetId,
                textMessage, null, null, new RongIMClient.SendMessageCallback() {
                    @Override
                    public void onSuccess(Integer integer) {
                        Message message = Message.obtain(targetId, type, msgContent);
                        message.setMessageDirection(Message.MessageDirection.SEND);
                        message.setSenderUserId(sharedPreferences.getString(Constant.ID_USER, ""));
                    }

                    @Override
                    public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {
//                        android.os.Message message1 = new android.os.Message();
//                        message1.what = SEND_FAIL;
//                        message1.obj = message;
//                        handler.sendMessage(message1);
                    }
                }, new RongIMClient.ResultCallback<Message>() {
                    @Override
                    public void onSuccess(Message message) {
                        //发送广播
                        Intent intent = new Intent();
                        intent.setAction("message_copy");
                        intent.putExtra("new_message", "message");
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("message", message);
                        intent.putExtras(bundle);
                        mContext.sendBroadcast(intent);
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {

                    }
                });
    }

    public void setView(Message message) {
        if (message.getContent() instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message.getContent();
            String info = textMessage.getContent();
            try {
                JSONObject json = new JSONObject(info);
                int contentType = json.getInt("ContentType");
                String filename = json.getString("FileName");
                String fileid = json.getString("FileID");
                String bulletinContent = json.optString("BulletinContent");
                String name = "", content = "";
                name = json.getString("requestName");
                if (contentType == 11 || contentType == 10) {
                    contentType = FileUtils.getContentType(filename);
                } else if (contentType == 8 || contentType == 9) {
                    JSONObject object = new JSONObject(bulletinContent);
                    content = object.optString("content");
                    if (name == null) {
                        name = "";
                    }
                }
                View view = null;
                ImageView img_pic;
                TextView tv, tv_content;
                switch (contentType) {
                    //图片
                    case 2:
                        view = LayoutInflater.from(mContext).inflate(R.layout.paste_img_show, null);
                        img_pic = (ImageView) view.findViewById(R.id.message_pic_img);
                        String url = "";
                        if (fileid.contains("http")) {
                            url = fileid;
                        } else {
                            url = String.format(Constant.STUDY_OPEN_FILE, fileid);
                        }
                        Picasso.with(mContext).load(url).placeholder(R.mipmap.img_morentupian).config(Bitmap.Config.RGB_565).
                                error(R.mipmap.img_jiazaishibai).into(img_pic);
                        frameLayout.addView(view);
                        break;
                    //音频
                    case 3:
                        view = LayoutInflater.from(mContext).inflate(R.layout.paste_file_message, null);
                        img_pic = (ImageView) view.findViewById(R.id.img_file);
                        img_pic.setImageResource(R.mipmap.ic_yinpin02);
                        tv = (TextView) view.findViewById(R.id.tv_file);
                        tv.setText(filename);
                        frameLayout.addView(view);
                        break;
                    case 4:   //视频
                        view = LayoutInflater.from(mContext).inflate(R.layout.paste_video_message, null);
                        img_pic = (ImageView) view.findViewById(R.id.img_message_pic);
                        url = String.format(Constant.MESSAGE_GET_FILE_DETIAL, fileid);

//                        Log.i("info", "复制视频=" + url);
                        img_pic.setImageResource(R.mipmap.img_morentupian);
                        setVideoScale(url, img_pic);

                        tv = (TextView) view.findViewById(R.id.tv_video_name);
                        tv.setText(filename);
                        frameLayout.addView(view);
                        break;
                    //动态
                    case 8:
                    case 9:
                        view = LayoutInflater.from(mContext).inflate(R.layout.paste_file_message, null);
                        img_pic = (ImageView) view.findViewById(R.id.img_file);
                        img_pic.setImageResource(R.mipmap.img_lianjie_liaotianchuang);
                        tv = (TextView) view.findViewById(R.id.tv_file);
                        tv.setText(name);
                        tv_content = (TextView) view.findViewById(R.id.tv_file_content);
                        tv_content.setText(content);
                        tv_content.setVisibility(View.VISIBLE);
                        frameLayout.addView(view);
                        break;
                    //文件
                    case 11:
                        view = LayoutInflater.from(mContext).inflate(R.layout.paste_file_message, null);
                        img_pic = (ImageView) view.findViewById(R.id.img_file);
                        FileUtils.setImage(img_pic, filename, "", mContext);
                        tv = (TextView) view.findViewById(R.id.tv_file);
                        tv.setText(filename);
                        frameLayout.addView(view);
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (message.getContent() instanceof ImageMessage) {
            ImageMessage imageMessage = (ImageMessage) message.getContent();
            View view = LayoutInflater.from(mContext).inflate(R.layout.paste_img_show, null);
            ImageView img_pic = (ImageView) view.findViewById(R.id.message_pic_img);
            Uri uri = imageMessage.getRemoteUri();
            Picasso.with(mContext).load(uri).placeholder(R.mipmap.img_morentupian).config(Bitmap.Config.RGB_565).
                    error(R.mipmap.img_jiazaishibai).into(img_pic);
            frameLayout.addView(view);
        }
    }

    private void setVideoScale(final String url, final ImageView img) {
        img.setTag(url);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String result = httpManager.getFileInfo(url);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        String tag = (String) img.getTag();
                        if (tag.equals(url)) {
                            if (result != null && !result.equals("") && !result.equals("404")) {
                                String path = "";
                                try {
                                    JSONObject json = new JSONObject(result);
                                    path = json.getString("scale");
                                    if (!path.contains("base64")) {
                                        if (path == null || path.equals("")) {
                                            img.setImageResource(R.mipmap.img_morentupian);
                                        } else {
                                            if (path.contains("@")) {
                                                path = path.substring(0, path.lastIndexOf("@"));
                                            }
//                                            Log.i("info", "路径=" + path);
                                            Picasso.with(mContext).load(path).placeholder(R.mipmap.img_morentupian).config(Bitmap.Config.RGB_565).into(img);
                                        }
                                    } else {
                                        path = path.substring(path.indexOf("base64") + 7);
                                        Bitmap bm = BitmapUtils.stringtoBitmap(path);
                                        img.setImageBitmap(bm);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                img.setImageResource(R.mipmap.img_morentupian);
                            }
                        }
                    }
                });
            }
        }).start();
    }
}
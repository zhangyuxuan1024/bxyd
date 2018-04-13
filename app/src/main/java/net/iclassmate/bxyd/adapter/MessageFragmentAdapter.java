package net.iclassmate.bxyd.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jauker.widget.BadgeView;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.bean.message.RMessage;
import net.iclassmate.bxyd.bean.message.SpaceMessage;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.utils.ChatMessageUtil;
import net.iclassmate.bxyd.utils.FileUtils;
import net.iclassmate.bxyd.utils.HttpManager;
import net.iclassmate.bxyd.utils.LoadImageSd;
import net.iclassmate.bxyd.utils.NetWorkUtils;
import net.iclassmate.bxyd.view.study.ShapeImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.ImageMessage;
import io.rong.message.TextMessage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by xyd on 2016/6/1.
 */
public class MessageFragmentAdapter extends BaseAdapter {
    private List<Conversation> messageList;
    private Context mContext;
    private String id;
    private String name;
    private SharedPreferences sp;
    private int emotion_map_type = 0x0001;
    private Handler mHandler = new Handler();
    private LruCache<String, String> lruCache;
    private LruCache<String, Bitmap> bitmapLruCache;
    private HttpManager httpManager;

    private static final String NAME = "name";
    private static final String PIC = "pic";
    private String userid;

    public MessageFragmentAdapter(List<Conversation> messageList, Context context) {
        this.messageList = messageList;
        this.mContext = context;
        sp = mContext.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        userid = sp.getString(Constant.ID_USER, "");
        httpManager = new HttpManager();
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int mCacheSize = maxMemory / 4;

        lruCache = new LruCache<String, String>(mCacheSize) {
            @Override
            protected int sizeOf(String key, String value) {
                return value.getBytes().length;
            }
        };
        bitmapLruCache = new LruCache<String, Bitmap>(mCacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
    }

    @Override
    public int getCount() {
        int ret = 0;
        if (messageList != null) {
            ret = messageList.size();
        }
        return ret;
    }

    @Override
    public Object getItem(int position) {
        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.chat_list_view, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.chat_list_name);
            holder.messageTextView = (TextView) convertView.findViewById(R.id.chat_list_message);
            holder.icon = (ShapeImageView) convertView.findViewById(R.id.chat_list_icon);
            holder.tv_time = (TextView) convertView.findViewById(R.id.chat_list_time);
            holder.badgeView = (BadgeView) convertView.findViewById(R.id.chat_badgeview);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Conversation msg = messageList.get(position);
        MessageContent message = msg.getLatestMessage();
        RMessage rMessage = new RMessage(message);

        holder.icon.setTag(msg.getTargetId()+PIC);
        holder.name.setTag(msg.getTargetId() + NAME);
        String url = lruCache.get(msg.getTargetId() + PIC);
        if (url == null || url.equals("") || url.equals("null")) {
            Bitmap bitmap = FileUtils.read2SdBitmap(msg.getTargetId(), mContext);
            if (bitmap != null) {
                holder.icon.setImageBitmap(bitmap);
                lruCache.put(msg.getTargetId() + PIC, msg.getTargetId() + PIC);
                bitmapLruCache.put(msg.getTargetId() + PIC, bitmap);
            } else {
                setHeadIcon(msg.getTargetId(), rMessage.getChatType(), holder.icon, holder.name);
            }
        } else {
            Bitmap bitmap = bitmapLruCache.get(url);
            if (bitmap != null) {
                holder.icon.setImageBitmap(bitmap);
            } else {
                setImage(url, msg.getTargetId(), rMessage.getChatType(), holder.icon);
            }
        }

        String name = lruCache.get(msg.getTargetId() + NAME);
        if (name == null || name.equals("") || name.equals("null") || name.contains("http") || name.equals("404")) {
            //获取个人备注名
            getUserName(rMessage.getChatType(), msg.getTargetId(), holder.name);
        } else {
            holder.name.setText(name);
        }

        final ViewHolder finalHolder = holder;
        final ViewHolder finalHolder1 = holder;
        final ViewHolder finalHolder2 = holder;
        int latestMessageId = msg.getLatestMessageId();
        if (latestMessageId == 0) {
            MessageContent latestMessage = msg.getLatestMessage();
            if (latestMessage instanceof TextMessage || latestMessage instanceof SpaceMessage) {
                String info = null;
                if (latestMessage instanceof TextMessage) {
                    info = ((TextMessage) latestMessage).getContent();
                } else if (latestMessage instanceof SpaceMessage) {
                    info = ((SpaceMessage) latestMessage).getContent();
                }
                if (info != null && !info.equals("")) {
                    rMessage = new RMessage(latestMessage);
                    String content = rMessage.getContent();
                    String filename = rMessage.getFileName();
                    long time = rMessage.getCreateTime();
                    finalHolder1.tv_time.setText(FileUtils.getTime(time + ""));
                    int contentType = rMessage.getContentType();
                    if (filename != null && !filename.equals("") && contentType == 11) {
                        contentType = FileUtils.getContentType(filename);
                    }
                    ChatMessageUtil chat = new ChatMessageUtil();
                    chat.setMessageView(contentType, finalHolder2.messageTextView, content, mContext);
                }
            } else if (latestMessage instanceof ImageMessage) {
                finalHolder.messageTextView.setText("[图片]");
                long time = 0;
                long t1 = msg.getReceivedTime();
                long t2 = msg.getSentTime();
                if (t1 > t2) {
                    time = t1;
                } else {
                    time = t2;
                }
                if (time > 0) {
                    finalHolder1.tv_time.setText(FileUtils.getTime(time + ""));
                } else {
                    finalHolder1.tv_time.setText(FileUtils.getTime(System.currentTimeMillis() + ""));
                }
            }
        } else {
            RongIMClient.getInstance().getMessage(latestMessageId, new RongIMClient.ResultCallback<Message>() {
                @Override
                public void onSuccess(Message message) {
                    if (message.getContent() instanceof TextMessage || message.getContent() instanceof SpaceMessage) {
                        String info = null;
                        if (message.getContent() instanceof TextMessage) {
                            TextMessage textMessage = (TextMessage) message.getContent();
                            info = textMessage.getContent();
                        } else if (message.getContent() instanceof SpaceMessage) {
                            SpaceMessage spaceMessage = (SpaceMessage) message.getContent();
                            info = spaceMessage.getContent();
                        }
                        if (info != null && !info.equals("")) {
                            try {
                                JSONObject json = new JSONObject(info);
                                String content = json.optString("Content");
                                String filename = json.optString("FileName");
                                long time = json.optLong("CreateTime");
                                int messageType = json.optInt("MessageType");

                                if (time > 0) {
                                    finalHolder1.tv_time.setText(FileUtils.getTime(time + ""));
                                } else {
                                    finalHolder1.tv_time.setText(FileUtils.getTime(System.currentTimeMillis() + ""));
                                }
                                if (messageType == 0 || messageType == 11) {
                                    int contentType = json.optInt("ContentType");
                                    if (filename != null && !filename.equals("") && contentType == 11) {
                                        contentType = FileUtils.getContentType(filename);
                                    }
                                    ChatMessageUtil chat = new ChatMessageUtil();
                                    chat.setMessageView(contentType, finalHolder2.messageTextView, content, mContext);
                                } else if (messageType == 10) {
                                    String requestName = json.getString("requestName");
                                    String name = sp.getString(Constant.USER_NAME, "");
                                    if (requestName != null) {
                                        if (requestName.equals(name)) {
                                            finalHolder2.messageTextView.setText("你" + mContext.getResources().getString(R.string.revoke_message));
                                        } else {
                                            finalHolder2.messageTextView.setText(requestName + mContext.getResources().getString(R.string.revoke_message));
                                        }
                                    }
                                } else if (messageType == 4) {
                                    if (content != null) {
                                        finalHolder2.messageTextView.setText(content);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (message.getContent() instanceof ImageMessage) {
                        finalHolder.messageTextView.setText("[图片]");
                        long time = 0;
                        long t1 = message.getReceivedTime();
                        long t2 = message.getSentTime();
                        if (t1 > t2) {
                            time = t1;
                        } else {
                            time = t2;
                        }
                        if (time > 0) {
                            finalHolder1.tv_time.setText(FileUtils.getTime(time + ""));
                        } else {
                            finalHolder1.tv_time.setText(FileUtils.getTime(System.currentTimeMillis() + ""));
                        }
                    }
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                }
            });
        }
        holder.badgeView.setBadgeCount(msg.getUnreadMessageCount());
        return convertView;
    }


    private void setHeadIcon(final String sid, final int type, final ShapeImageView icon, final TextView tvName) {
        if (!NetWorkUtils.isNetworkAvailable(mContext) || type == 1) {
            setImage(sid + PIC, sid, type, icon);
            return;
        }

        //获取头像的网址
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String result = httpManager.getChatMessageInfo(userid, sid, type);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        String tag = (String) icon.getTag();
                        if (tag.equals(sid + PIC)) {
                            if (result != null && !result.equals("") && !result.equals("404")) {
                                try {
                                    JSONObject json = new JSONObject(result);
                                    String type1 = json.optString("type");
                                    String icon1 = json.optString("icon");
                                    String remark = json.optString("remark");
                                    if (icon1 != null && !icon1.equals("")) {
                                        lruCache.put(tag, icon1);
                                    } else {
                                        icon1 = "null";
                                    }
                                    int t = type;
                                    if (type1 != null && !type1.equals("")) {
                                        if (type1.equals("person")) {
                                            t = 0;
                                        } else if (type1.equals("group")) {
                                            t = 2;
                                        } else if (type1.equals("org")) {
                                            t = 3;
                                        }
                                    }
                                    setImage(icon1, sid, t, icon);
                                    String tag1 = (String) tvName.getTag();
                                    if (remark != null && !remark.equals("") && remark.equals("null") && tag1 != null && tag1.equals(sid + NAME)) {
                                        lruCache.put(sid + NAME, remark);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                setImage(sid + PIC, sid, type, icon);
                            }
                        }
                    }
                });
            }
        }).start();
    }

    private void setImage(String iconUrl, final String uid, final int type, final ShapeImageView icon) {
        //ChatType  0 单聊，1 群聊，2 空间(群组)
        if (iconUrl == null || iconUrl.equals("") || !iconUrl.contains("http")) {
            Bitmap bitmap = null;
            if (type == 0) {
                icon.setImageResource(R.mipmap.moren_geren_xiaoxi);
                bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.moren_geren_xiaoxi);
            } else if (type == 1) {
                icon.setImageResource(R.mipmap.ic_qunliao);
                bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_qunliao);
            } else if (type == 2) {
                icon.setImageResource(R.mipmap.ic_qunzu_guanzhu);
                bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_qunzu_guanzhu);
            } else if (type == 3) {
                icon.setImageResource(R.mipmap.ic_jigou_guanzhu);
                bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_jigou_guanzhu);
            }
            if (bitmap != null) {
                lruCache.put(uid + PIC, uid + PIC);
                bitmapLruCache.put(uid + PIC, bitmap);
            }
            return;
        }

        //获取头像图片
        final String finalIconUrl = iconUrl;
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap bitmap = httpManager.getBitmap(finalIconUrl, true);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        String tag = (String) icon.getTag();
                        if (tag.equals(uid + PIC)) {
                            if (bitmap != null) {
                                icon.setImageBitmap(bitmap);
                                lruCache.put(uid + PIC, finalIconUrl);
                                bitmapLruCache.put(finalIconUrl, bitmap);
                                FileUtils.writeBitmap2sd(bitmap, uid);
                            } else {
                                if (type == 0) {
                                    icon.setImageResource(R.mipmap.moren_geren_xiaoxi);
                                } else if (type == 1) {
                                    icon.setImageResource(R.mipmap.ic_qunliao);
                                } else if (type == 2) {
                                    icon.setImageResource(R.mipmap.ic_qunzu_guanzhu);
                                } else if (type == 3) {
                                    icon.setImageResource(R.mipmap.ic_jigou_guanzhu);
                                }
                            }
                        }
                    }
                });

            }
        }).start();
    }

    //type用于区分单聊还是群聊以获取会话名称  整型， 0 单聊，1 群聊，2 空间(群组)
    private void getUserName(final int type, final String targetId, final TextView textView) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "";
                String uid = sp.getString(Constant.ID_USER, "");
                if (type == 0) {
                    url = String.format(Constant.MESSAGE_FIND_REMARK_NAME, uid, targetId);
                } else if (type == 1) {
                    url = String.format(Constant.MESSAGE_GET_SESSION_NAME, targetId);
                } else if (type == 2) {
                    url = String.format(Constant.MESSAGE_GET_ORG_INFO, targetId, uid);
                } else {
                    return;
                }
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            final String result = response.body().string();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    String tag = (String) textView.getTag();
                                    if (tag.equals(targetId + NAME)) {
                                        String sname = result;
                                        if (type == 0) {
                                            try {
                                                JSONObject object = new JSONObject(sname);
                                                sname = object.optString("remark");
                                                if (sname == null || sname.equals("") || sname.equals("null")) {
                                                    sname = object.optString("userName");
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        } else if (type == 1) {
                                            try {
                                                JSONObject object = new JSONObject(sname);
                                                sname = object.optString("sessionName");
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        } else if (type == 2) {
                                            try {
                                                JSONObject object = new JSONObject(sname);
                                                sname = object.optString("name");
                                                if (sname == null) {
                                                    sname = "";
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        if (sname != null && !sname.equals("null")) {
                                            textView.setText(sname);
                                            lruCache.put(tag, sname);
                                        }
                                    }
                                }
                            });
                        }
                    }
                });
            }
        }).start();
    }


    public void clean() {
        if (messageList == null || messageList.size() < 1) {
            return;
        }
        for (int i = 0; i < messageList.size(); i++) {
            Conversation conversation = messageList.get(i);
            String targetId = conversation.getTargetId();
            String ret = lruCache.get(targetId);
            if (ret != null) {
                lruCache.remove(targetId);
            }
        }
    }

    public void writeFileData(String fileName, String message) {
        try {
            File file = new File(Environment.getExternalStorageDirectory()
                    .getAbsolutePath(), fileName);
            FileOutputStream fout = new FileOutputStream(file);
            System.out.println(message);
            byte[] bytes = message.getBytes();
            fout.write(bytes);
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ViewHolder {
        ShapeImageView icon;
        public TextView messageTextView, name, tv_time;
        BadgeView badgeView;
    }

}

package net.iclassmate.bxyd.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.text.SpannableString;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.bean.message.ImageProgress;
import net.iclassmate.bxyd.bean.message.RMessage;
import net.iclassmate.bxyd.bean.message.SpaceMessage;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.utils.BitmapUtils;
import net.iclassmate.bxyd.utils.FileUtils;
import net.iclassmate.bxyd.utils.HttpManager;
import net.iclassmate.bxyd.utils.NetWorkUtils;
import net.iclassmate.bxyd.utils.emotion.SpanStringUtils;
import net.iclassmate.bxyd.view.study.ShapeImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import io.rong.imlib.model.Message;
import io.rong.message.ImageMessage;
import io.rong.message.RecallNotificationMessage;
import io.rong.message.TextMessage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by xyd on 2016/5/31.
 */
public class MessagesAdapter extends BaseAdapter {
    private List<Message> messageList;
    private Context mContext;
    private boolean isShowName;
    //图片发送进度
    private List<ImageProgress> progressList;

    //设置picassion图片宽度
    private int targetWidth;

    final static int VIEW_TYPE_FROM = 0;
    final static int VIEW_TYPE_TO = 1;
    private int emotion_map_type = 0x0001;
    public String friendName;
    private HttpManager httpManager;
    private Handler mHandler = new Handler();

    private View.OnClickListener onHeadClick;
    private View.OnClickListener onMesgClick;
    private View.OnLongClickListener onMesgLongClick;
    private View.OnClickListener onclickSendMessage;
    private View.OnClickListener onclickOpenFile;

    private SharedPreferences sharedPreferences;
    private LruCache<String, String> lruCache;
    private LruCache<String, Bitmap> bitmapLruCache;

    public void setOnHeadClick(View.OnClickListener onHeadClick) {
        this.onHeadClick = onHeadClick;
    }

    public void setOnMesgClick(View.OnClickListener onMesgClick) {
        this.onMesgClick = onMesgClick;
    }

    public void setOnMesgLongClick(View.OnLongClickListener onMesgLongClick) {
        this.onMesgLongClick = onMesgLongClick;
    }

    public List<ImageProgress> getProgressList() {
        return progressList;
    }

    public void setProgressList(List<ImageProgress> progressList) {
        this.progressList = progressList;
    }

    public void setOnclickSendMessage(View.OnClickListener onclickSendMessage) {
        this.onclickSendMessage = onclickSendMessage;
    }

    public void setOnclickOpenFile(View.OnClickListener onclickOpenFile) {
        this.onclickOpenFile = onclickOpenFile;
    }

    public MessagesAdapter(List<Message> messageList, Context context) {
        this.messageList = messageList;
        this.mContext = context;
        httpManager = new HttpManager();
        sharedPreferences = context.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        int maxSize = (int) Runtime.getRuntime().maxMemory();
        maxSize = maxSize / 4;
        //存放头像路径 key tid
        //存放用户名  key  tid+"2"
        lruCache = new LruCache<String, String>(maxSize) {
            @Override
            protected int sizeOf(String key, String value) {
                return value.getBytes().length;
            }
        };

        bitmapLruCache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
    }

    public boolean isShowName() {
        return isShowName;
    }

    public void setIsShowName(boolean isShowName) {
        this.isShowName = isShowName;
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
    public int getViewTypeCount() {
        return 9;
    }

    @Override
    public int getItemViewType(int position) {
        int ret = 0;
        Message message = messageList.get(position);
        if (message.getMessageDirection() == Message.MessageDirection.SEND) {
            if (message.getContent() instanceof TextMessage || message.getContent() instanceof SpaceMessage) {
                String info = null;
                if (message.getContent() instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message.getContent();
                    info = textMessage.getContent();
                } else if (message.getContent() instanceof SpaceMessage) {
                    SpaceMessage spaceMessage = (SpaceMessage) message.getContent();
                    info = spaceMessage.getContent();
                }
                JSONObject json = null;
                try {
                    json = new JSONObject(info);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String filename = json.optString("FileName");
                int contentType = json.optInt("ContentType");
                String fileId = json.optString("FileID");
                int messageType = json.optInt("MessageType");
                if (messageType == 0) {
                    if (filename != null && !filename.equals("")) {
                        if (contentType == 10 || contentType == 11) {
                            contentType = FileUtils.getContentType(filename);
                        }
                    }
                    //文字，图片
                    if (contentType == 0 || contentType == 1 || contentType == 2) {
                        ret = 0;
                    } else if (contentType == 4) {
                        //视频
                        ret = 2;
                    } else if (contentType == 8 || contentType == 9) {
                        //发动态
                        ret = 7;
                    } else {
                        //文件
                        ret = 1;
                    }
                } else if (messageType == 4 || messageType == 10 || messageType == 11) {
                    ret = 6;
                }
            } else if (message.getContent() instanceof ImageMessage) {
                ret = 0;
            }
        } else if (message.getMessageDirection() == Message.MessageDirection.RECEIVE) {
            if (message.getContent() instanceof TextMessage || message.getContent() instanceof SpaceMessage) {
                String info = null;
                if (message.getContent() instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message.getContent();
                    info = textMessage.getContent();
                } else if (message.getContent() instanceof SpaceMessage) {
                    SpaceMessage spaceMessage = (SpaceMessage) message.getContent();
                    info = spaceMessage.getContent();
                }
                JSONObject json = null;
                try {
                    json = new JSONObject(info);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String filename = json.optString("FileName");
                int contentType = json.optInt("ContentType");
                String fileId = json.optString("FileID");
                int messageType = json.optInt("MessageType");
                if (messageType == 0) {
                    if (filename != null && !filename.equals("")) {
                        if (contentType == 10 || contentType == 11) {
                            contentType = FileUtils.getContentType(filename);
                        }
                    }
                    if (contentType == 0 || contentType == 1 || contentType == 2) {
                        //文字，图片
                        ret = 3;
                    } else if (contentType == 4) {
                        //视频
                        ret = 5;
                    } else if (contentType == 8 || contentType == 9) {
                        //发动态
                        ret = 8;
                    } else {
                        //文件
                        ret = 4;
                    }
                } else if (messageType == 4 || messageType == 10 || messageType == 11) {
                    ret = 6;
                }
            } else if (message.getContent() instanceof ImageMessage) {
                ret = 3;
            }
        }
        return ret;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = messageList.get(position);
        int type = getItemViewType(position);
        try {
            switch (type) {
                //发文字，图片
                case 0:
                    convertView = getSendView(convertView, parent, position);
                    break;
                //发文件
                case 1:
                    convertView = getSendFileView(convertView, parent, position, 0);
                    break;
                //发视频
                case 2:
                    convertView = getSendVideoView(convertView, parent, position);
                    break;
                //收文字，图片
                case 3:
                    convertView = getFromView(convertView, parent, message, position);
                    break;
                //收文件
                case 4:
                    convertView = getReceiveFileView(convertView, parent, position, 0);
                    break;
                //收视频
                case 5:
                    convertView = getReceiveVideoView(convertView, parent, position);
                    break;
                case 6:
                    //显示系统消息
                    convertView = getSystemView(convertView, parent, position);
                    break;
                //发动态
                case 7:
                    convertView = getSendFileView(convertView, parent, position, 1);
                    break;
                //收动态
                case 8:
                    convertView = getReceiveFileView(convertView, parent, position, 1);
                    break;
                default:
                    break;
            }
        } catch (NullPointerException e) {
        }
        return convertView;
    }

    private View getSendView(View convertView, ViewGroup parent, int position) {
        SendViewHolder sendViewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.send_view, null);
            sendViewHolder = new SendViewHolder();
            sendViewHolder.messageTextView = (TextView) convertView.findViewById(R.id.send_message);
            sendViewHolder.icon = (ShapeImageView) convertView.findViewById(R.id.send_icon);
            sendViewHolder.messageImage = (ImageView) convertView.findViewById(R.id.send_Imagemessage);
            sendViewHolder.tv_progress = (TextView) convertView.findViewById(R.id.send_pro_tv);
            sendViewHolder.img_tv = (ImageView) convertView.findViewById(R.id.img_send_tv_fail);
            sendViewHolder.img_pic = (ImageView) convertView.findViewById(R.id.img_send_img_fail);
            sendViewHolder.tv_time = (TextView) convertView.findViewById(R.id.message_time);
            convertView.setTag(sendViewHolder);
        } else {
            sendViewHolder = (SendViewHolder) convertView.getTag();
        }
        Message message = messageList.get(position);

        sendViewHolder.tv_time.setVisibility(View.GONE);
        setTime(position, sendViewHolder.tv_time);
        //设置头像
        setUImage(sendViewHolder.icon, message.getSenderUserId());

        sendViewHolder.img_tv.setVisibility(View.GONE);
        sendViewHolder.img_pic.setVisibility(View.GONE);

        if (message.getContent() instanceof TextMessage || message.getContent() instanceof SpaceMessage) {
            sendViewHolder.messageImage.setVisibility(View.GONE);
            sendViewHolder.tv_progress.setVisibility(View.GONE);
            sendViewHolder.messageTextView.setVisibility(View.VISIBLE);
            Message.SentStatus sentStatus = message.getSentStatus();
            String info = null;
            if (message.getContent() instanceof TextMessage) {
                TextMessage text = (TextMessage) message.getContent();
                info = text.getContent();
            } else if (message.getContent() instanceof SpaceMessage) {
                SpaceMessage spaceMessage = (SpaceMessage) message.getContent();
                info = spaceMessage.getContent();
            }
            try {
                JSONObject json = new JSONObject(info);
                String content = json.getString("Content");
                String filename = json.optString("FileName");
                int contentType = json.optInt("ContentType");
                String fileId = json.optString("FileID");
                if (filename != null && !filename.equals("")) {
                    contentType = FileUtils.getContentType(filename);
                }
                if (fileId == null) {
                    fileId = "";
                }
                if (contentType == 0 || contentType == 1) {
                    if (sentStatus == Message.SentStatus.FAILED) {
                        sendViewHolder.img_tv.setVisibility(View.VISIBLE);
                    } else {
                        sendViewHolder.img_tv.setVisibility(View.GONE);
                    }
                    SpannableString string = SpanStringUtils.getEmotionContent(emotion_map_type, mContext, sendViewHolder.messageTextView, content);
//                    String ret = SpanStringUtils.getString(string);
                    sendViewHolder.messageTextView.setText(string);
                } else if (contentType == 2) {
                    if (sentStatus == Message.SentStatus.FAILED) {
                        sendViewHolder.img_pic.setVisibility(View.VISIBLE);
                    } else {
                        sendViewHolder.img_pic.setVisibility(View.GONE);
                    }
                    sendViewHolder.messageTextView.setVisibility(View.GONE);
                    sendViewHolder.messageImage.setVisibility(View.VISIBLE);
                    String url = "";
                    if (fileId.contains("http")) {
                        url = fileId;
                    } else {
                        url = String.format(Constant.STUDY_OPEN_FILE, fileId);
                    }
                    targetWidth = (int) mContext.getResources().getDimension(R.dimen.view_90);
                    Picasso.with(mContext).load(url).placeholder(R.mipmap.img_morentupian).error(R.mipmap.img_morentupian)
                            .config(Bitmap.Config.RGB_565)
                            .into(sendViewHolder.messageImage);
                    sendViewHolder.messageImage.setTag(message);
                    sendViewHolder.messageImage.setOnClickListener(onMesgClick);
                } else {
                    sendViewHolder.messageTextView.setText("[文件]");
                }
//                ChatMessageUtil chat = new ChatMessageUtil();
//                chat.setMessageView(contentType, sendViewHolder.messageTextView, content, mContext);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (message.getContent() instanceof ImageMessage) {
            sendViewHolder.messageTextView.setVisibility(View.GONE);
            sendViewHolder.messageImage.setVisibility(View.VISIBLE);
            int id = message.getMessageId();
            Message.SentStatus sentStatus = message.getSentStatus();
            if (sentStatus == Message.SentStatus.FAILED) {
                sendViewHolder.img_pic.setVisibility(View.VISIBLE);
            } else {
                sendViewHolder.img_pic.setVisibility(View.GONE);
            }
            ImageProgress imageProgress = null;
            if (progressList != null) {
                for (int i = 0; i < progressList.size(); i++) {
                    imageProgress = progressList.get(i);
                    if (imageProgress.getMsgId() == id) {
                        break;
                    }
                    imageProgress = null;
                }
                if (imageProgress != null) {
                    sendViewHolder.tv_progress.setVisibility(View.VISIBLE);
                    sendViewHolder.tv_progress.setText(imageProgress.getProgress() + "%");
                    if (imageProgress.getProgress() >= 98) {
                        sendViewHolder.tv_progress.setVisibility(View.GONE);
                    }
                }
            }
            ImageMessage imageMessage = (ImageMessage) message.getContent();
            Uri uri = imageMessage.getThumUri();
            Picasso.with(mContext).load(uri).config(Bitmap.Config.RGB_565).placeholder(R.mipmap.img_morentupian)
                    .error(R.mipmap.img_morentupian).into(sendViewHolder.messageImage);

            sendViewHolder.messageImage.setTag(message);
            sendViewHolder.messageImage.setOnClickListener(onMesgClick);
        } else {
            Message.SentStatus sentStatus = message.getSentStatus();
            if (sentStatus == Message.SentStatus.FAILED) {
                sendViewHolder.img_tv.setVisibility(View.VISIBLE);
            }
            sendViewHolder.messageTextView.setText("[动态]");
        }
        sendViewHolder.icon.setOnClickListener(onHeadClick);

        sendViewHolder.messageTextView.setTag(message);
        sendViewHolder.messageTextView.setOnLongClickListener(onMesgLongClick);

        sendViewHolder.messageImage.setTag(message);
        sendViewHolder.messageImage.setOnLongClickListener(onMesgLongClick);

        sendViewHolder.img_tv.setTag(message);
        sendViewHolder.img_tv.setOnClickListener(onclickSendMessage);

        sendViewHolder.img_pic.setTag(message);
        sendViewHolder.img_pic.setOnClickListener(onclickSendMessage);
        return convertView;
    }


    private void getHeadIcon(final String uid, final ImageView img) {
        if (!NetWorkUtils.isNetworkAvailable(mContext)) {
            setImage("null", uid, img);
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String url = httpManager.getUserIconUrl(uid);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        String tag = (String) img.getTag();
                        if (tag.equals(uid + "1")) {
                            if (url != null) {
                                lruCache.put(uid, url);
                                setImage(url, uid, img);
                            }
                        }
                    }
                });
            }
        }).start();
    }

    private View getFromView(View convertView, ViewGroup parent, Message message, int position) {
        FromViewHolder fromViewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.receive_view, null);
            fromViewHolder = new FromViewHolder();
            fromViewHolder.nameTextView = (TextView) convertView.findViewById(R.id.receive_name);
            fromViewHolder.messageTextView = (TextView) convertView.findViewById(R.id.receive_message);
            fromViewHolder.icon = (ShapeImageView) convertView.findViewById(R.id.receive_icon);
            fromViewHolder.messageImage = (ImageView) convertView.findViewById(R.id.receive_Imagemessage);
            fromViewHolder.tv_time = (TextView) convertView.findViewById(R.id.message_time);
            convertView.setTag(fromViewHolder);
        } else {
            fromViewHolder = (FromViewHolder) convertView.getTag();
        }

        //设置时间
        setTime(position, fromViewHolder.tv_time);
        //设置用户头像
        setUImage(fromViewHolder.icon, message.getSenderUserId());
        if (message.getContent().getUserInfo() != null) {
            friendName = message.getContent().getUserInfo().getName();
            if (NetWorkUtils.isNetworkAvailable(mContext)) {
                String name = lruCache.get(message.getSenderUserId() + "2");
                if (name == null || name.equals("") || name.equals("null")) {
                    setUserName(fromViewHolder.nameTextView, message.getSenderUserId(), message.getContent().getUserInfo().getName());
                } else {
                    fromViewHolder.nameTextView.setText(name);
                }
            } else {
                fromViewHolder.nameTextView.setText(message.getContent().getUserInfo().getName());
            }
        }
        if (message.getContent() instanceof TextMessage || message.getContent() instanceof SpaceMessage) {
            fromViewHolder.messageTextView.setVisibility(View.VISIBLE);
            fromViewHolder.messageImage.setVisibility(View.GONE);
            String info = null;
            if (message.getContent() instanceof TextMessage) {
                TextMessage text = (TextMessage) message.getContent();
                info = text.getContent();
            } else if (message.getContent() instanceof SpaceMessage) {
                SpaceMessage spaceMessage = (SpaceMessage) message.getContent();
                info = spaceMessage.getContent();
            }
            try {
                JSONObject json = new JSONObject(info);
                String content = json.getString("Content");
                String filename = json.optString("FileName");
                int contentType = json.optInt("ContentType");
                String fileId = json.optString("FileID");
                if (filename != null && !filename.equals("")) {
                    contentType = FileUtils.getContentType(filename);
                }
                if (fileId == null) {
                    fileId = "";
                }
                if (contentType == 0 || contentType == 1) {
                    SpannableString string = SpanStringUtils.getEmotionContent(emotion_map_type, mContext, fromViewHolder.messageTextView, content);
//                    String ret = SpanStringUtils.getString(string);
                    fromViewHolder.messageTextView.setText(string);
                } else if (contentType == 2) {
                    fromViewHolder.messageTextView.setVisibility(View.GONE);
                    fromViewHolder.messageImage.setVisibility(View.VISIBLE);
                    String url = "";
                    if (url.contains("http")) {
                        url = fileId;
                    } else {
                        url = String.format(Constant.STUDY_OPEN_FILE, fileId);
                    }
                    Picasso.with(mContext).load(url).placeholder(R.mipmap.img_morentupian).error(R.mipmap.img_morentupian)
                            .config(Bitmap.Config.RGB_565)
                            .into(fromViewHolder.messageImage);
                    fromViewHolder.messageImage.setTag(message);
                    fromViewHolder.messageImage.setOnClickListener(onMesgClick);
                } else if (message.getContent() instanceof RecallNotificationMessage) {

                } else {
                    fromViewHolder.messageTextView.setText("[消息]");
                }
//                ChatMessageUtil chat = new ChatMessageUtil();
//                chat.setMessageView(contentType, fromViewHolder.messageTextView, content, mContext, message);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (message.getContent() instanceof ImageMessage) {
            fromViewHolder.messageTextView.setVisibility(View.GONE);
            fromViewHolder.messageImage.setVisibility(View.VISIBLE);
            ImageMessage imageMessage = (ImageMessage) message.getContent();
            Uri uri = imageMessage.getThumUri();
            Picasso.with(mContext).load(uri).placeholder(R.mipmap.img_morentupian).into(fromViewHolder.messageImage);

            fromViewHolder.messageImage.setTag(message);
            fromViewHolder.messageImage.setOnClickListener(onMesgClick);
        } else {
            fromViewHolder.messageTextView.setText("[动态]");
        }

        if (!isShowName) {
            fromViewHolder.nameTextView.setVisibility(View.VISIBLE);
        } else {
            fromViewHolder.nameTextView.setVisibility(View.GONE);
        }
        RMessage rMessage = new RMessage(message);
        if (rMessage.getChatType() == 0) {
            fromViewHolder.nameTextView.setVisibility(View.GONE);
        }
        fromViewHolder.icon.setOnClickListener(onHeadClick);

        fromViewHolder.messageTextView.setTag(message);
        fromViewHolder.messageTextView.setOnLongClickListener(onMesgLongClick);
        fromViewHolder.messageImage.setOnLongClickListener(onMesgLongClick);
        return convertView;
    }

    private void setTime(int position, TextView tv) {
        tv.setVisibility(View.GONE);
        try {
            long time = 0;
            if (position == 0) {
                Message msg = messageList.get(0);
                RMessage rMessage = new RMessage(msg);
                time = rMessage.getCreateTime();
                if (time > 0) {
                    tv.setVisibility(View.VISIBLE);
                    tv.setText(FileUtils.getMessageTime(time + ""));
                }
            } else if (position < messageList.size()) {
                Message message1 = messageList.get(position - 1);
                Message message2 = messageList.get(position);
                RMessage rMessage1 = new RMessage(message1);
                RMessage rMessage2 = new RMessage(message2);
                long t1 = 0, t2 = 0;
                t1 = rMessage1.getCreateTime();
                t2 = rMessage2.getCreateTime();
                if (t2 - t1 >= 3 * 1000 * 60) {
                    tv.setVisibility(View.VISIBLE);
                    tv.setText(FileUtils.getMessageTime(t2 + ""));
                }
            }
        } catch (Exception e) {
            tv.setVisibility(View.GONE);
        }
    }

    //发送文件 type=0 文件 type 1 动态
    private View getSendFileView(View convertView, ViewGroup parent, int position, int type) {
        SendViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.message_send_file, null);
            holder = new SendViewHolder();
            holder.icon = (ImageView) convertView.findViewById(R.id.send_icon);
            holder.messageImage = (ImageView) convertView.findViewById(R.id.send_pic_message);
            holder.messageTextView = (TextView) convertView.findViewById(R.id.send_tv_message);
            holder.linearLayout = (LinearLayout) convertView.findViewById(R.id.send_file_message);
            holder.tv_content = (TextView) convertView.findViewById(R.id.send_tv_content);
            holder.tv_time = (TextView) convertView.findViewById(R.id.message_time);
            holder.img_tv = (ImageView) convertView.findViewById(R.id.img_send_tv_fail);
            convertView.setTag(holder);
        } else {
            holder = (SendViewHolder) convertView.getTag();
        }

        setTime(position, holder.tv_time);

        Message message = messageList.get(position);
        if (message.getSentStatus() == Message.SentStatus.FAILED) {
            holder.img_tv.setVisibility(View.VISIBLE);
        } else {
            holder.img_tv.setVisibility(View.INVISIBLE);
        }
        //设置用户头像
        setUImage(holder.icon, message.getSenderUserId());

        if (message.getContent() instanceof TextMessage || message.getContent() instanceof SpaceMessage) {
            String info = null;
            if (message.getContent() instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message.getContent();
                info = textMessage.getContent();
            } else if (message.getContent() instanceof SpaceMessage) {
                SpaceMessage spaceMessage = (SpaceMessage) message.getContent();
                info = spaceMessage.getContent();
            }
            try {
                JSONObject json = new JSONObject(info);
                String filename = json.optString("FileName");
                String fileId = json.optString("FileID");
                String name = json.optString("requestName");
                if (type == 0) {
                    String url = String.format(Constant.STUDY_OPEN_FILE, fileId);
                    FileUtils.setImageInAdapter(holder.messageImage, filename, url, mContext);
//                    filename = FileUtils.getFileName(filename);
                    holder.messageTextView.setText(filename);
                    holder.tv_content.setVisibility(View.GONE);
                } else if (type == 1) {
                    holder.messageImage.setImageResource(R.mipmap.img_lianjie_liaotianchuang);
                    String bulletinContent = json.optString("BulletinContent");
                    JSONObject jsonObject = null;
                    String content = "";
                    if (bulletinContent != null && !bulletinContent.equals("")) {
                        jsonObject = new JSONObject(bulletinContent);
                        content = jsonObject.optString("content");
                        name = jsonObject.optString("author");
                    }
                    if (name != null && !name.equals("")) {
                        holder.messageTextView.setText(name);
                    }
                    if (content != null) {
                        holder.tv_content.setText(content);
                        holder.tv_content.setVisibility(View.VISIBLE);
                    }
                    //holder.linearLayout.setBackgroundResource(R.mipmap.img_qipao_lianjie);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            holder.linearLayout.setTag(message);
            holder.linearLayout.setOnClickListener(onclickOpenFile);
            holder.linearLayout.setOnLongClickListener(onMesgLongClick);

            holder.icon.setOnClickListener(onHeadClick);

            holder.img_tv.setTag(message);
            holder.img_tv.setOnClickListener(onclickSendMessage);
        }
        return convertView;
    }

    //接收文件 type = 0 文件 type 1 动态
    private View getReceiveFileView(View convertView, ViewGroup parent, int position, int type) {
        FromViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.message_receive_file, null);
            holder = new FromViewHolder();
            holder.icon = (ImageView) convertView.findViewById(R.id.receive_icon);
            holder.nameTextView = (TextView) convertView.findViewById(R.id.receive_name);
            holder.messageImage = (ImageView) convertView.findViewById(R.id.receive_pic_message);
            holder.messageTextView = (TextView) convertView.findViewById(R.id.receiver_tv_message);
            holder.tv_content = (TextView) convertView.findViewById(R.id.receive_tv_content);
            holder.linearLayot = (LinearLayout) convertView.findViewById(R.id.receive_file_message);
            holder.tv_time = (TextView) convertView.findViewById(R.id.message_time);
            convertView.setTag(holder);
        } else {
            holder = (FromViewHolder) convertView.getTag();
        }
        setTime(position, holder.tv_time);

        Message message = messageList.get(position);
        RMessage rMessage = new RMessage(message);
        if (rMessage.getChatType() == 0) {
            holder.nameTextView.setVisibility(View.GONE);
        }
        //设置用户头像
        setUImage(holder.icon, message.getSenderUserId());

        if (message.getContent().getUserInfo() != null) {
            friendName = message.getContent().getUserInfo().getName();
            if (NetWorkUtils.isNetworkAvailable(mContext)) {
                String name = lruCache.get(message.getSenderUserId() + "2");
                if (name == null || name.equals("")) {
                    setUserName(holder.nameTextView, message.getSenderUserId(), message.getContent().getUserInfo().getName());
                } else {
                    holder.nameTextView.setText(name);
                }
            } else {
                holder.nameTextView.setText(message.getContent().getUserInfo().getName());
            }
        }
        if (message.getContent() instanceof TextMessage || message.getContent() instanceof SpaceMessage) {
            String info = null;
            if (message.getContent() instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message.getContent();
                info = textMessage.getContent();
            } else if (message.getContent() instanceof SpaceMessage) {
                SpaceMessage spaceMessage = (SpaceMessage) message.getContent();
                info = spaceMessage.getContent();
            }
            try {
                JSONObject json = new JSONObject(info);
                String filename = json.optString("FileName");
                String fileId = json.optString("FileID");
                String name = json.optString("requestName");
                if (type == 0) {
                    String url = String.format(Constant.STUDY_OPEN_FILE, fileId);
                    FileUtils.setImageInAdapter(holder.messageImage, filename, url, mContext);
//                    filename = FileUtils.getFileName(filename);
                    holder.messageTextView.setText(filename);
                    holder.tv_content.setVisibility(View.GONE);
                } else if (type == 1) {
                    holder.messageImage.setImageResource(R.mipmap.img_lianjie_liaotianchuang);
                    String bulletinContent = json.optString("BulletinContent");
//                    Log.i("bull", "bull=" + bulletinContent);
                    JSONObject object = null;
                    String content = "";
                    if (bulletinContent != null) {
                        object = new JSONObject(bulletinContent);
                        content = object.optString("content");
                        name = object.optString("author");
                    }
                    if (name != null && !name.equals("") && !name.equals("null")) {
                        holder.messageTextView.setVisibility(View.VISIBLE);
                        holder.messageTextView.setText(name);
                    } else {
                        holder.messageTextView.setVisibility(View.GONE);
                    }
                    if (content != null) {
                        holder.tv_content.setText(content);
                        holder.tv_content.setVisibility(View.VISIBLE);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            holder.linearLayot.setTag(message);
            holder.linearLayot.setOnClickListener(onclickOpenFile);
            holder.linearLayot.setOnLongClickListener(onMesgLongClick);

            holder.icon.setOnClickListener(onHeadClick);
        }
        return convertView;
    }

    //发送视频文件
    private View getSendVideoView(View convertView, ViewGroup parent, int position) {
        SendViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.message_send_video, null);
            holder = new SendViewHolder();
            holder.icon = (ImageView) convertView.findViewById(R.id.send_icon);
            holder.messageImage = (ImageView) convertView.findViewById(R.id.send_message_pic);
            holder.img_tv = (ImageView) convertView.findViewById(R.id.img_send_tv_fail);
            holder.messageTextView = (TextView) convertView.findViewById(R.id.send_video_name);
            holder.frameLayout = (FrameLayout) convertView.findViewById(R.id.send_message_frame);
            holder.tv_time = (TextView) convertView.findViewById(R.id.message_time);
            convertView.setTag(holder);
        } else {
            holder = (SendViewHolder) convertView.getTag();
        }
        setTime(position, holder.tv_time);

        Message message = messageList.get(position);
        if (message.getSentStatus() == Message.SentStatus.FAILED) {
            holder.img_tv.setVisibility(View.VISIBLE);
        } else {
            holder.img_tv.setVisibility(View.INVISIBLE);
        }
        //设置用户头像
        setUImage(holder.icon, message.getSenderUserId());

        if (message.getContent() instanceof TextMessage || message.getContent() instanceof SpaceMessage) {
            String info = null;
            if (message.getContent() instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message.getContent();
                info = textMessage.getContent();
            } else if (message.getContent() instanceof SpaceMessage) {
                SpaceMessage spaceMessage = (SpaceMessage) message.getContent();
                info = spaceMessage.getContent();
            }
            try {
                JSONObject json = new JSONObject(info);
                String filename = json.optString("FileName");
                String fileId = json.optString("FileID");
                holder.messageTextView.setText(filename);
                String url = String.format(Constant.MESSAGE_GET_FILE_DETIAL, fileId);
                holder.messageImage.setTag(url);
                setVideoScale(url, holder.messageImage);

                holder.frameLayout.setTag(message);
                holder.frameLayout.setOnClickListener(onclickOpenFile);
                holder.frameLayout.setOnLongClickListener(onMesgLongClick);

                holder.icon.setOnClickListener(onHeadClick);

                holder.img_tv.setTag(message);
                holder.img_tv.setOnClickListener(onclickSendMessage);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return convertView;
    }

    //接收视频文件
    private View getReceiveVideoView(View convertView, ViewGroup parent, int position) {
        FromViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.message_receive_video, null);
            holder = new FromViewHolder();
            holder.icon = (ImageView) convertView.findViewById(R.id.receive_icon);
            holder.messageImage = (ImageView) convertView.findViewById(R.id.receive_message_pic);
            holder.nameTextView = (TextView) convertView.findViewById(R.id.receive_name);
            holder.messageTextView = (TextView) convertView.findViewById(R.id.receive_video_name);
            holder.frameLayout = (FrameLayout) convertView.findViewById(R.id.receive_message_frame);
            holder.tv_time = (TextView) convertView.findViewById(R.id.message_time);
            convertView.setTag(holder);
        } else {
            holder = (FromViewHolder) convertView.getTag();
        }
        setTime(position, holder.tv_time);

        Message message = messageList.get(position);
        RMessage rMessage = new RMessage(message);
        if (rMessage.getChatType() == 0) {
            holder.nameTextView.setVisibility(View.GONE);
        }
        //设置用户头像
        setUImage(holder.icon, message.getSenderUserId());

        if (message.getContent().getUserInfo() != null) {
            friendName = message.getContent().getUserInfo().getName();
            if (NetWorkUtils.isNetworkAvailable(mContext)) {
                String name = lruCache.get(message.getSenderUserId() + "2");
                if (name == null || name.equals("")) {
                    setUserName(holder.nameTextView, message.getSenderUserId(), message.getContent().getUserInfo().getName());
                } else {
                    holder.nameTextView.setText(name);
                }
            } else {
                holder.nameTextView.setText(message.getContent().getUserInfo().getName());
            }
        }
        if (message.getContent() instanceof TextMessage || message.getContent() instanceof SpaceMessage) {
            String info = null;
            if (message.getContent() instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message.getContent();
                info = textMessage.getContent();
            } else if (message.getContent() instanceof SpaceMessage) {
                SpaceMessage spaceMessage = (SpaceMessage) message.getContent();
                info = spaceMessage.getContent();
            }
            try {
                JSONObject json = new JSONObject(info);
                String filename = json.optString("FileName");
                String fileId = json.optString("FileID");
                holder.messageTextView.setText(filename);
                String url = String.format(Constant.MESSAGE_GET_FILE_DETIAL, fileId);
                holder.messageImage.setTag(url);
                setVideoScale(url, holder.messageImage);

                holder.frameLayout.setTag(message);
                holder.frameLayout.setOnClickListener(onclickOpenFile);
                holder.frameLayout.setOnLongClickListener(onMesgLongClick);

                holder.icon.setOnClickListener(onHeadClick);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return convertView;
    }

    //设置视频缩略图
    private void setVideoScale(final String url, final ImageView img) {
        if (!NetWorkUtils.isNetworkAvailable(mContext)) {
            img.setImageResource(R.mipmap.img_morentupian);
            return;
        }
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
                                        if (path.contains("@")) {
                                            int index = path.lastIndexOf("@");
                                            path = path.substring(0, index);
                                        }
                                        Picasso.with(mContext).load(path).placeholder(R.mipmap.img_morentupian)
                                                .error(R.mipmap.img_morentupian).config(Bitmap.Config.RGB_565).into(img);
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

    //设置系统消息
    private View getSystemView(View convertView, ViewGroup parent, int position) {
        SendViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.message_time, null);
            holder = new SendViewHolder();
            holder.messageTextView = (TextView) convertView.findViewById(R.id.message_time);
            holder.tv_time = (TextView) convertView.findViewById(R.id.message_time_2);
            convertView.setTag(holder);
        } else {
            holder = (SendViewHolder) convertView.getTag();
        }
        Message message = messageList.get(position);

        holder.tv_time.setVisibility(View.GONE);
        setTime(position, holder.tv_time);

        if (message.getContent() instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message.getContent();
            String info = textMessage.getContent();
            try {
                JSONObject json = new JSONObject(info);
                int messageType = json.getInt("MessageType");
                String requestName = json.getString("requestName");
                String content = json.getString("Content");
                if (messageType == 10) {
                    if (requestName != null) {
                        holder.messageTextView.setVisibility(View.VISIBLE);
                        if (message.getMessageDirection() == Message.MessageDirection.SEND) {
                            holder.messageTextView.setText("你撤回了一条消息");
                        } else if (message.getMessageDirection() == Message.MessageDirection.RECEIVE) {
                            holder.messageTextView.setText(requestName + "撤回了一条消息");
                        }
                    }
                } else if (messageType == 4 || messageType == 11) {
                    if (content != null) {
                        holder.messageTextView.setVisibility(View.VISIBLE);
                        holder.messageTextView.setText(content);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return convertView;
    }

    public void setImage(final String url, final String uid, final ImageView icon) {
        if (url == null || url.equals("") || !url.contains("http")) {
            icon.setImageResource(R.mipmap.ic_liaotiantouxiang);
            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_liaotiantouxiang);
            lruCache.put(uid, uid);
            bitmapLruCache.put(uid, bitmap);
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Bitmap bitmap = httpManager.getBitmap(url, true);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            String tag = (String) icon.getTag();
                            if (tag.equals(uid + "1")) {
                                if (bitmap != null) {
                                    icon.setImageBitmap(bitmap);
                                    bitmapLruCache.put(url, bitmap);
                                    FileUtils.writeBitmap2sd(bitmap, uid);
                                } else {
                                    icon.setImageResource(R.mipmap.ic_liaotiantouxiang);
                                }
                            }
                        }
                    });
                }
            }).start();
        }
    }

    //设置用户头像 type =true发送放  false 接收放
    public void setUImage(ImageView icon, String uid) {
        if (uid == null) {
            return;
        }
        icon.setTag(uid + "1");
        String iconUrl = lruCache.get(uid);
        if (iconUrl == null || iconUrl.equals("")) {
            Bitmap bitmap = FileUtils.read2SdBitmap(uid, mContext);
            if (bitmap != null) {
                icon.setImageBitmap(bitmap);
                lruCache.put(uid, uid);
                bitmapLruCache.put(uid, bitmap);
            } else {
                getHeadIcon(uid, icon);
            }
        } else {
            Bitmap bitmap = bitmapLruCache.get(iconUrl);
            if (bitmap != null) {
                icon.setImageBitmap(bitmap);
            } else {
                setImage(iconUrl, uid, icon);
            }
        }
    }

    private class FromViewHolder {
        public TextView nameTextView;
        public TextView messageTextView;
        public ImageView messageImage, icon;
        LinearLayout linearLayot;
        FrameLayout frameLayout;
        TextView tv_content, tv_time;
    }

    private class SendViewHolder {
        public TextView messageTextView;
        public ImageView messageImage, icon;
        TextView tv_progress;
        ImageView img_tv, img_pic;
        LinearLayout linearLayout;
        FrameLayout frameLayout;
        TextView tv_content, tv_time;
    }

//    Transformation transformation = new Transformation() {
//        @Override
//        public Bitmap transform(Bitmap source) {
//            if (source.getWidth() == 0) {
//                return source;
//            }
//            //如果图片小于设置的宽度，则返回原图
//            if (source.getWidth() < targetWidth) {
//                return source;
//            } else {
//                //如果图片大小大于等于设置的宽度，则按照设置的宽度比例来缩放
//                double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
//                int targetHeight = (int) (targetWidth * aspectRatio);
//                if (targetHeight != 0 && targetWidth != 0) {
//                    Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
//                    if (result != source) {
//                        // Same bitmap is returned if sizes are the same
//                        source.recycle();
//                    }
//                    return result;
//                } else {
//                    return source;
//                }
//            }
//
//        }
//
//        @Override
//        public String key() {
//            return "transformation" + " desiredWidth";
//        }
//    };

    //设置个人的用户名
    private void setUserName(final TextView tv, final String tid, final String name) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "";
                String uid = sharedPreferences.getString(Constant.ID_USER, "");
                url = String.format(Constant.MESSAGE_FIND_REMARK_NAME, uid, tid);
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
                                    String sname = result;
                                    try {
                                        JSONObject object = new JSONObject(sname);
                                        sname = object.optString("remark");
                                        if (sname != null) {
                                            tv.setText(sname);
                                            lruCache.put(tid + "2", sname);
                                        } else {
                                            tv.setText(name);
                                            lruCache.put(tid + "2", name);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        tv.setText(name);
                                        lruCache.put(tid + "2", name);
                                    }
                                }
                            });
                        } else {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    tv.setText(name);
                                    lruCache.put(tid + "2", name);
                                }
                            });
                        }
                    }
                });
            }
        }).start();
    }
}
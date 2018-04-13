package net.iclassmate.bxyd.bean.message;

import android.net.Uri;

import net.iclassmate.bxyd.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.CommandNotificationMessage;
import io.rong.message.ImageMessage;
import io.rong.message.TextMessage;

/**
 * Created by xydbj on 2016/9/8.
 */
public class RMessage implements Serializable {
    //TextMessage
    private int MessageType;
    private int ChatType;
    private int ContentType;
    private String Content;
    private int FontSize;
    private int FontStyle;
    private int FontColor;
    private String BulletinID;
    private String BulletinContent;
    private String requestName;
    private String requestRemark;
    private String requestGroupId;
    private String FileID;
    private String FileName;
    private long CreateTime;

    //ImageMessage
    private int type;
    private String fileid;
    private String name;
    private long size;
    private String CRC;
    private String memo;
    private String uri;

    //CommandNotificationMessage
    //content、user、extra
    private User user;
    private Extra extra;

    private Auth auth;

    public RMessage(Message message) {
        parserMessage(message);
    }

    public RMessage(MessageContent messageContent) {
        parserMessage(messageContent);
    }

    private void parserMessage(Message message) {
        if (message != null) {
            if (message.getContent() instanceof TextMessage || message.getContent() instanceof SpaceMessage) {
                TextMessage textMessage = (TextMessage) message.getContent();
                String info = textMessage.getContent();
                try {
                    JSONObject json = new JSONObject(info);
                    String type = json.optString("type");
                    if (type == null || type.equals("")) {
                        MessageType = json.getInt("MessageType");
                        ChatType = json.getInt("ChatType");
                        ContentType = json.getInt("ContentType");
                        Content = json.getString("Content");
                        FontSize = json.optInt("FontSize");
                        FontStyle = json.getInt("FontStyle");
                        FontColor = json.getInt("FontColor");
                        BulletinID = json.optString("BulletinID");
                        BulletinContent = json.optString("BulletinContent");
                        requestName = json.optString("requestName");
                        requestRemark = json.optString("requestRemark");
                        requestGroupId = json.optString("requestGroupId");
                        FileID = json.optString("FileID");
                        FileName = json.optString("FileName");
                        CreateTime = json.getLong("CreateTime");
                        size = json.optLong("FileSize");
                    } else {
                        auth = new Auth();
                        auth.parserJson(json);
                        setAuth(auth);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (message.getContent() instanceof ImageMessage) {
                ImageMessage imageMessage = (ImageMessage) message.getContent();
                String extra = imageMessage.getExtra();
                Content = extra;
                try {
                    JSONObject json = new JSONObject(extra);
                    type = json.optInt("type");
                    fileid = json.optString("fileid");
                    name = json.optString("name");
                    size = json.optLong("size");
                    CRC = json.optString("CRC");
                    memo = json.optString("memo");
                    CreateTime = json.optLong("createTime");
                    ChatType = json.optInt("chatType");
                    uri = json.optString("uri");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (message.getContent() instanceof CommandNotificationMessage) {
                CommandNotificationMessage cnm = (CommandNotificationMessage) message.getContent();
                String data = cnm.getData();
                if (data != null && !data.equals("")) {
                    try {
                        JSONObject json = new JSONObject(data);
                        Content = json.optString("content");

                        JSONObject jsonUser = json.optJSONObject("user");
                        if (jsonUser != null) {
                            String id = jsonUser.getString("id");
                            String name = jsonUser.getString("name");
                            String icon = jsonUser.getString("icon");
                            user = new User(id, name, icon);
                            setUser(user);
                        }

                        JSONObject jsonExtra = new JSONObject(Content);
                        if (jsonExtra != null) {
                            int cmd = jsonExtra.getInt("cmd");
                            String objectId = jsonExtra.optString("objectId");
                            String objectName = jsonExtra.optString("objectName");
                            extra = new Extra(cmd, objectId, objectName);
                            setExtra(extra);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void parserMessage(MessageContent message) {
        if (message != null) {
            if (message instanceof TextMessage || message instanceof SpaceMessage) {
                TextMessage textMessage = (TextMessage) message;
                String info = textMessage.getContent();
                try {
                    JSONObject json = new JSONObject(info);
                    String type = json.optString("type");
                    if (type == null || type.equals("")) {
                        MessageType = json.getInt("MessageType");
                        ChatType = json.getInt("ChatType");
                        ContentType = json.getInt("ContentType");
                        Content = json.optString("Content");
                        FontSize = json.optInt("FontSize");
                        FontStyle = json.getInt("FontStyle");
                        FontColor = json.getInt("FontColor");
                        BulletinID = json.optString("BulletinID");
                        BulletinContent = json.optString("BulletinContent");
                        requestName = json.optString("requestName");
                        requestRemark = json.optString("requestRemark");
                        requestGroupId = json.optString("requestGroupId");
                        FileID = json.optString("FileID");
                        FileName = json.optString("FileName");
                        CreateTime = json.getLong("CreateTime");
                        size = json.optLong("FileSize");
                    } else {
                        auth = new Auth();
                        auth.parserJson(json);
                        setAuth(auth);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (message instanceof ImageMessage) {
                ImageMessage imageMessage = (ImageMessage) message;
                String extra = imageMessage.getExtra();
                try {
                    JSONObject json = new JSONObject(extra);
                    type = json.optInt("type");
                    fileid = json.optString("fileid");
                    name = json.optString("name");
                    size = json.optLong("size");
                    CRC = json.optString("CRC");
                    memo = json.optString("memo");
                    CreateTime = json.optLong("createTime");
                    ChatType = json.optInt("chatType");
                    uri = json.optString("uri");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (message instanceof CommandNotificationMessage) {
                CommandNotificationMessage cnm = (CommandNotificationMessage) message;
                String data = cnm.getData();
                if (data != null && !data.equals("")) {
                    try {
                        JSONObject json = new JSONObject(data);
                        if (json != null) {
                            Content = json.optString("content");

                            JSONObject jsonUser = json.optJSONObject("user");
                            if (jsonUser != null) {
                                String id = jsonUser.getString("id");
                                String name = jsonUser.getString("name");
                                String icon = jsonUser.getString("icon");
                                user = new User(id, name, icon);
                                setUser(user);
                            }

                            JSONObject jsonExtra = json.optJSONObject("extra");
                            if (jsonExtra != null) {
                                int cmd = jsonExtra.getInt("cmd");
                                String groupid = jsonExtra.optString("groupid");
                                String groupname = jsonExtra.optString("groupname");
                                extra = new Extra(cmd, groupid, groupname);
                                setExtra(extra);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    public int getMessageType() {
        return MessageType;
    }

    public void setMessageType(int messageType) {
        MessageType = messageType;
    }

    public int getChatType() {
        return ChatType;
    }

    public void setChatType(int chatType) {
        ChatType = chatType;
    }

    public int getContentType() {
        return ContentType;
    }

    public void setContentType(int contentType) {
        ContentType = contentType;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public int getFontSize() {
        return FontSize;
    }

    public void setFontSize(int fontSize) {
        FontSize = fontSize;
    }

    public int getFontStyle() {
        return FontStyle;
    }

    public void setFontStyle(int fontStyle) {
        FontStyle = fontStyle;
    }

    public int getFontColor() {
        return FontColor;
    }

    public void setFontColor(int fontColor) {
        FontColor = fontColor;
    }

    public String getBulletinID() {
        return BulletinID;
    }

    public void setBulletinID(String bulletinID) {
        BulletinID = bulletinID;
    }

    public String getBulletinContent() {
        return BulletinContent;
    }

    public void setBulletinContent(String bulletinContent) {
        BulletinContent = bulletinContent;
    }

    public String getRequestName() {
        return requestName;
    }

    public void setRequestName(String requestName) {
        this.requestName = requestName;
    }

    public String getRequestRemark() {
        return requestRemark;
    }

    public void setRequestRemark(String requestRemark) {
        this.requestRemark = requestRemark;
    }

    public String getRequestGroupId() {
        return requestGroupId;
    }

    public void setRequestGroupId(String requestGroupId) {
        this.requestGroupId = requestGroupId;
    }

    public String getFileID() {
        return FileID;
    }

    public void setFileID(String fileID) {
        FileID = fileID;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public long getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(long createTime) {
        CreateTime = createTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getFileid() {
        return fileid;
    }

    public void setFileid(String fileid) {
        this.fileid = fileid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getCRC() {
        return CRC;
    }

    public void setCRC(String CRC) {
        this.CRC = CRC;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Extra getExtra() {
        return extra;
    }

    public void setExtra(Extra extra) {
        this.extra = extra;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Auth getAuth() {
        return auth;
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    /**
     *   /// 消息的生成时间
     /// </summary>
     public long CreateTime { get; set; }
     #region 消息类别
     // 0 普通消息，1 添加好友，2 添加空间，3 添加群组，4 添加好友响应，5 添加空间响应，6 添加群组响应
     // 7 删除好友响应， 8 删除空间响应， 9 删除群组响应  10 消息撤回
     public int MessageType { set; get; }
     // 0 默认，1 文本，2 图片，3 音频，4 视频，5 链接，6 图文混合，7 普通文件，8 普通动态，9 文件动态
     // 10 本地文件，11 网盘(络)文件，12 收藏
     public int ContentType { set; get; }
     // 0 单聊，1 群组，2 空间
     public int ChatType { set; get; }
     *
     * */
}

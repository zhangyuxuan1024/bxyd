package net.iclassmate.bxyd.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import net.iclassmate.bxyd.bean.message.Extra;
import net.iclassmate.bxyd.bean.message.User;
import net.iclassmate.bxyd.bean.study.FileDirList;
import net.iclassmate.bxyd.constant.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.CommandNotificationMessage;
import io.rong.message.TextMessage;

/**
 * 融云工具类
 * Created by xyd on 2016/8/13.
 */
public class RongIMClientUtils {

    /**
     * 获取消息免通知免打扰时间
     *
     * @author LvZhanFeng
     * @Time 2016/8/13
     */
    public static void getNotificationQuietHours() {
        RongIMClient.getInstance().getNotificationQuietHours(new RongIMClient.GetNotificationQuietHoursCallback() {
            @Override
            public void onSuccess(String s, int i) {

            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }

    /**
     * 发送命令消息
     *
     * @param operation  命令消息类型
     * @param targetId   目标id
     * @param targetName 目标名称
     * @param type       消息类型
     * @param content    消息内容
     */
    public static void sendCommend(Extra.Operation operation, String targetId, String targetName, Conversation.ConversationType type, String content) {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        User user = new User(sp.getString(Constant.ID_USER, ""), sp.getString(Constant.USER_NAME, ""), "");
        Extra extra = new Extra(operation, targetId, targetName);
        UserInfo userInfo = new UserInfo(sp.getString(Constant.ID_USER, ""), sp.getString("name", ""), null);
        sendCommendMessage(targetName, content, userInfo, user, extra, type, targetId);
    }

    /**
     * 发送命令消息
     *
     * @param targetName       目标名称
     * @param content          消息内容
     * @param userInfo
     * @param user
     * @param extra            命令消息类型
     * @param conversationType
     * @param targetId
     */
    public static void sendCommendMessage(final String targetName, String content, UserInfo userInfo, User user, Extra extra, Conversation.ConversationType conversationType, String targetId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", user.getId());
            jsonObject.put("name", user.getName());
            jsonObject.put("icon", user.getIcon());
            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("cmd", extra.getCmd());
            jsonObject2.put("objectId", extra.getGroupid());
            jsonObject2.put("objectName", extra.getGroupname());
            final String messageBody = jsonObject2.toString();
            Log.i("RongIMClentUtils", "CommendMessage发送内容" + messageBody+extra.getOperation());
            if (!messageBody.equals("")) {
                CommandNotificationMessage commandNotificationMessage = null;
                commandNotificationMessage = CommandNotificationMessage.obtain(extra.getGroupname(), messageBody);
                commandNotificationMessage.setUserInfo(userInfo);
                Log.i("RongIMClentUtils", "2CommendMessage发送内容" + commandNotificationMessage.getData()+extra.getOperation());
                RongIMClient.getInstance().sendMessage(conversationType, targetId,
                        commandNotificationMessage, null, null, new RongIMClient.SendMessageCallback() {
                            @Override
                            public void onSuccess(Integer integer) {
                                Log.i("RongIMClentUtils", "通知发送成功," + targetName);
                            }

                            @Override
                            public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {
                                Log.i("RongIMClentUtils", "通知发送失败" + errorCode.getValue() + "," + targetName);
                            }
                        }, new RongIMClient.ResultCallback<Message>() {
                            @Override
                            public void onSuccess(Message message) {
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {
                            }
                        });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送TextMessage消息
     *
     * @param type
     * @param MessageType
     * @param chatType
     * @param content
     * @param targetId
     */
    public static Message sendTxtMsg(Conversation.ConversationType type, int MessageType, int chatType,
                                     String content, String targetId) {
        JSONObject json = new JSONObject();
        try {
            json.put("MessageType", MessageType);
            json.put("ContentType", 1);
            json.put("ChatType", chatType);
            json.put("Content", content);
            json.put("FontSize", 14);
            json.put("FontStyle", 0);
            json.put("FontColor", 0);
            json.put("BulletinID", "");
            json.put("BulletinContent", "");
            json.put("requestName", "");
            json.put("requestRemark", "");
            json.put("requestGroupId", "");
            json.put("FileID", "");
            json.put("FileName", "");
            json.put("CreateTime", System.currentTimeMillis());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String messageBody = json.toString();
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        Log.i("RongIMClentUtils", "发送内容" + messageBody);
        Message message = null;
        if (messageBody.equals("")) {
            if (chatType == 1)
                Toast.makeText(UIUtils.getContext(), "发送内容不能为空", Toast.LENGTH_SHORT).show();
        } else {
            UserInfo userInfo = new UserInfo(sp.getString(Constant.ID_USER, ""), sp.getString("name", ""), null);
            final TextMessage textMessage = TextMessage.obtain(messageBody);
            textMessage.setUserInfo(userInfo);

            //添加消息
            message = Message.obtain(targetId, type, textMessage);
            message.setMessageDirection(Message.MessageDirection.SEND);
            message.setSenderUserId(sp.getString(Constant.ID_USER, ""));
            sendMessage(type, targetId, textMessage);   //发送
        }
        return message;
    }

    public static Message sendTxtMsg(Conversation.ConversationType type, int MessageType, int chatType,
                                     String content, String targetId, String name) {
        JSONObject json = new JSONObject();
        try {
            json.put("MessageType", MessageType);
            json.put("ContentType", 1);
            json.put("ChatType", chatType);
            json.put("Content", content);
            json.put("FontSize", 14);
            json.put("FontStyle", 0);
            json.put("FontColor", 0);
            json.put("BulletinID", "");
            json.put("BulletinContent", "");
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
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        Log.i("RongIMClentUtils", "发送内容" + messageBody);
        Message message = null;
        if (messageBody.equals("")) {
            if (chatType == 1)
                Toast.makeText(UIUtils.getContext(), "发送内容不能为空", Toast.LENGTH_SHORT).show();
        } else {
            UserInfo userInfo = new UserInfo(sp.getString(Constant.ID_USER, ""), sp.getString("name", ""), null);
            final TextMessage textMessage = TextMessage.obtain(messageBody);
            textMessage.setUserInfo(userInfo);

            //添加消息
            message = Message.obtain(targetId, type, textMessage);
            message.setMessageDirection(Message.MessageDirection.SEND);
            message.setSenderUserId(sp.getString(Constant.ID_USER, ""));
            sendMessage(type, targetId, textMessage);   //发送
        }
        return message;
    }

    /**
     * 发送消息
     *
     * @param type
     * @param targetId
     * @param msgContent
     */
    public static void sendMessage(Conversation.ConversationType type, String targetId, MessageContent msgContent) {
        RongIMClient.getInstance().sendMessage(type, targetId,
                msgContent, null, null, new RongIMClient.SendMessageCallback() {
                    @Override
                    public void onSuccess(Integer integer) {
                        Log.i("RongIMClentUtils", "TxtMsg通知发送成功");
                    }

                    @Override
                    public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {
                        Log.i("RongIMClentUtils", "TxtMsg通知发送失败," + errorCode.getValue());
                    }
                }, new RongIMClient.ResultCallback<Message>() {
                    @Override
                    public void onSuccess(Message message) {
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                    }
                });

    }

    //发送网盘文件
    public static Message sendNetFile(List<FileDirList> listSelectAll, int chatType, Conversation.ConversationType type, String targetId, SharedPreferences sp) {
        Message message = null;
        for (int i = 0; i < listSelectAll.size(); i++) {
            if (i % 6 == 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Object o = listSelectAll.get(i);
            if (o instanceof FileDirList) {
                FileDirList file = (FileDirList) o;
                JSONObject json = new JSONObject();
                try {
                    json.put("MessageType", 0);
                    json.put("ContentType", 11);
                    json.put("ChatType", chatType);
                    json.put("Content", "[文件]");
                    json.put("FontSize", 14);
                    json.put("FontStyle", 0);
                    json.put("FontColor", 0);
                    json.put("BulletinID", "");
                    json.put("BulletinContent", "");
                    json.put("requestName", "");
                    json.put("requestRemark", "");
                    json.put("requestGroupId", "");
                    json.put("FileID", file.getId());
                    json.put("FileName", file.getShortName());
                    json.put("FileSize", file.getSize());
                    json.put("CreateTime", System.currentTimeMillis());
                    final String messageBody = json.toString();
                    if (messageBody.equals("")) {
                        Toast.makeText(UIUtils.getContext(), "发送内容不能为空", Toast.LENGTH_SHORT).show();
                    } else {
                        String icon = sp.getString(Constant.USER_ICON, "");
                        Uri uri = Uri.parse(icon);
                        UserInfo userInfo = new UserInfo(sp.getString(Constant.ID_USER, ""), sp.getString(Constant.USER_NAME, ""), uri);
                        final TextMessage textMessage = TextMessage.obtain(messageBody);
                        textMessage.setUserInfo(userInfo);

                        message = Message.obtain(targetId, type, textMessage);
                        message.setMessageDirection(Message.MessageDirection.SEND);
                        message.setSenderUserId(sp.getString(Constant.ID_USER, ""));
                        sendMessage(type, targetId, textMessage);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return message;
    }

}

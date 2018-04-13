package net.iclassmate.bxyd.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.bean.message.RMessage;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.ui.activitys.chat.SendCopyMessageActivity;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.message.RecallNotificationMessage;
import io.rong.message.TextMessage;

/**
 * Created by xydbj on 2016/8/12.
 */
public class PopWindowUtils {
    private static SharedPreferences sharedPreferences;
    public static final String NEW_MESSAGE = "NEW_MESSAGE";

    //复制 message 复制消息,copyContent 复制文本内容,messageType 消息类型 文本或文件, chatType 聊天类型 单聊或群聊
    public static void showPopupWindowCopy(View view, final Message message, final String copyContent, final int messageType, final Context mContext) {
        sharedPreferences = mContext.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.message_pop_window, null);
        ImageView img_copy = (ImageView) contentView.findViewById(R.id.message_pop_window_copy);
        img_copy.setImageResource(R.mipmap.ic_copy);
        final PopupWindow popupWindow = new PopupWindow(contentView,
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        img_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    if (messageType == Constant.MESSAGE_TYPE_WORD) {
                        FileUtils.copy(copyContent, mContext);
                    } else if (messageType == Constant.MESSAGE_TYPE_FILE) {
                        sharedPreferences.edit().putInt(Constant.MESSAGE_ID, message.getMessageId()).commit();
                    }
                    sharedPreferences.edit().putInt(Constant.MESSAGE_TYPE, messageType).commit();
                }
            }
        });
        img_copy.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

//        // 使其聚集
        popupWindow.setFocusable(true);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        int[] location = new int[2];
        view.getLocationOnScreen(location);
        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, location[0], location[1] - popupWindow.getHeight() - (int) mContext.getResources().getDimension(R.dimen.view_40));
    }

    //粘贴
    public static void showPopupWindowPaste(View view, final Context mContext, final EditText editText, final String targetId, final int chatType) {
        sharedPreferences = mContext.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        final int messageType = sharedPreferences.getInt(Constant.MESSAGE_TYPE, -1);
        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.message_pop_window, null);
        ImageView img_copy = (ImageView) contentView.findViewById(R.id.message_pop_window_copy);
        img_copy.setImageResource(R.mipmap.ic_paste);
        final PopupWindow popupWindow = new PopupWindow(contentView,
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        img_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    if (chatType == -1 || messageType == -1) {
                        return;
                    }
                    int messageId = sharedPreferences.getInt(Constant.MESSAGE_ID, -1);
                    if (messageType == Constant.MESSAGE_TYPE_WORD) {
                        String result = FileUtils.paste(mContext);
                        if (result != null && result.trim().length() > 0) {
                            editText.setText(result.trim());
                            editText.setSelection(result.trim().length());
                        }
                    } else if (messageType == Constant.MESSAGE_TYPE_FILE) {
                        if (messageId > 0) {
                            Intent intent = new Intent(mContext, SendCopyMessageActivity.class);
                            intent.putExtra(Constant.MESSAGE_ID, messageId);
                            intent.putExtra("tid", targetId);
                            intent.putExtra(Constant.CHAT_TYPE, chatType);
                            mContext.startActivity(intent);
                        }
                    }
                }
            }
        });
        editText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });
        // 使其聚集
        popupWindow.setFocusable(true);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        int[] location = new int[2];
        view.getLocationOnScreen(location);
        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, location[0], location[1] - popupWindow.getHeight() - (int) mContext.getResources().getDimension(R.dimen.view_40));
    }

    //复制和撤销
    public static void showPopupWindowCopyRevoke(View view, final Context mContext, final String copyContent, final int messageType, final Message message) {
        sharedPreferences = mContext.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.message_pop_window, null);
        ImageView img_copy = (ImageView) contentView.findViewById(R.id.message_pop_window_copy);
        ImageView img_revoke = (ImageView) contentView.findViewById(R.id.message_pop_window_revoke);
        img_revoke.setVisibility(View.VISIBLE);
        img_copy.setImageResource(R.mipmap.img_copy);
        final PopupWindow popupWindow = new PopupWindow(contentView,
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        img_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    if (messageType == Constant.MESSAGE_TYPE_WORD) {
                        FileUtils.copy(copyContent, mContext);
                    } else if (messageType == Constant.MESSAGE_TYPE_FILE) {
                        sharedPreferences.edit().putInt(Constant.MESSAGE_ID, message.getMessageId()).commit();
                    }
                    sharedPreferences.edit().putInt(Constant.MESSAGE_TYPE, messageType).commit();
                }
            }
        });
        img_revoke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    RongIMClient.getInstance().recallMessage(message, new RongIMClient.ResultCallback<RecallNotificationMessage>() {
                        @Override
                        public void onSuccess(RecallNotificationMessage recallNotificationMessage) {
                            sendMessage(message, mContext);
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {
                        }
                    });
                }
            }
        });

        img_copy.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

        // 使其聚集
        popupWindow.setFocusable(true);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        int[] location = new int[2];
        view.getLocationOnScreen(location);
        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, location[0], location[1] - popupWindow.getHeight() - (int) mContext.getResources().getDimension(R.dimen.view_40));
    }

    private static void sendMessage(final Message messageSend, final Context mContext) {
        String name = sharedPreferences.getString(Constant.USER_NAME, "");
        JSONObject json = new JSONObject();
        try {
            RMessage rMessage = new RMessage(messageSend);
            int chatType = rMessage.getChatType();
            json.put("MessageType", 10);
            json.put("ContentType", 1);
            json.put("ChatType", chatType);
            json.put("Content", "消息撤回");
            json.put("FontSize", 14);
            json.put("FontStyle", 0);
            json.put("FontColor", 0);
            json.put("BulletinID", "");
            json.put("BulletinContent", "");
            json.put("requestName", name);
            json.put("requestRemark", "");
            json.put("requestGroupId", "");
            json.put("FileID", rMessage.getCreateTime());
            json.put("FileName", "");
            json.put("CreateTime", System.currentTimeMillis());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String messageBody = json.toString();
//        Log.i("info", "发送内容=" + messageBody);

        final UserInfo userInfo = new UserInfo(sharedPreferences.getString(Constant.ID_USER, ""), sharedPreferences.getString(Constant.USER_NAME, ""), null);
        final TextMessage textMessage = TextMessage.obtain(messageBody);
        textMessage.setUserInfo(userInfo);

        //添加消息
        final Message message = Message.obtain(messageSend.getTargetId(), messageSend.getConversationType(), textMessage);
        message.setMessageDirection(Message.MessageDirection.SEND);
        message.setSenderUserId(sharedPreferences.getString(Constant.ID_USER, ""));

        RongIMClient.getInstance().sendMessage(messageSend.getConversationType(), messageSend.getTargetId(),
                textMessage, null, null, new RongIMClient.SendMessageCallback() {
                    @Override
                    public void onSuccess(Integer integer) {
                        Message message = Message.obtain(messageSend.getTargetId(), messageSend.getConversationType(), textMessage);
                        message.setMessageDirection(Message.MessageDirection.SEND);
                        message.setSenderUserId(sharedPreferences.getString(Constant.ID_USER, ""));
                    }

                    @Override
                    public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {
//                            android.os.Message message1 = new android.os.Message();
//                            message1.what = SEND_FAIL;
//                            message1.obj = message;
//                            handler.sendMessage(message1);
                    }
                }, new RongIMClient.ResultCallback<Message>() {
                    @Override
                    public void onSuccess(Message message) {
                        //发送广播
                        Intent intent = new Intent();
                        intent.setAction("message_revoke");
                        intent.putExtra("new_message", "message");
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("message", message);
                        intent.putExtras(bundle);
                        mContext.sendBroadcast(intent);
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
//                            sendMessageFail(message);
                    }
                });
    }

    //删除消息
    public static void delMessage(Message message) {
        int[] ids = {message.getMessageId()};
        RongIMClient.getInstance().deleteMessages(ids);
    }

}

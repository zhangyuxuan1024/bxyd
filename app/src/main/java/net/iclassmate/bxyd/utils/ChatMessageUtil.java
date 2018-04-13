package net.iclassmate.bxyd.utils;

import android.content.Context;
import android.text.SpannableString;
import android.util.Log;
import android.widget.TextView;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.utils.emotion.SpanStringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.rong.imlib.model.Message;

/**
 * Created by xydbj on 2016/8/5.
 */
public class ChatMessageUtil {
    private int emotion_map_type = 0x0001;

    //消息界面设置
    public void setMessageText(int contentType, TextView tv, String content, Context mContext) {
        if (contentType == 0 || contentType == 1) {
            SpannableString string = SpanStringUtils.getEmotionContent(emotion_map_type, mContext, tv, content);
            tv.setText(string);
        } else if (contentType == 2) {
            tv.setText("[图片]");
        } else if (contentType == 3) {
            tv.setText("[音频]");
        } else if (contentType == 4) {
            tv.setText("[视频]");
        } else if (contentType == 5) {
            tv.setText("[链接]");
        } else if (contentType == 6) {
            tv.setText("[图文]");
        } else if (contentType == 7 || contentType == 10 || contentType == 11) {
            tv.setText("[文件]");
        } else if (contentType == 8 || contentType == 9) {
            tv.setText("[动态]");
        } else if (contentType == 12) {
            tv.setText("[收藏]");
        }
    }

    //聊天界面发送消息内容设置
    public void setMessageView(int contentType, TextView tv, String content, Context mContext) {
        if (contentType == 0 || contentType == 1) {
            SpannableString string = SpanStringUtils.getEmotionContent(emotion_map_type, mContext, tv, content);
            tv.setText(string);
        } else if (contentType == 2) {
            tv.setText("[图片]");
        } else if (contentType == 3) {
            tv.setText("[音频]");
        } else if (contentType == 4) {
            tv.setText("[视频]");
        } else if (contentType == 5) {
            tv.setText("[链接]");
        } else if (contentType == 6) {
            tv.setText("[图文]");
        } else if (contentType == 7 || contentType == 10 || contentType == 11) {
            tv.setText("[文件]");
        } else if (contentType == 8 || contentType == 9) {
            tv.setText("[动态]");
        } else if (contentType == 12) {
            tv.setText("[收藏]");
        }
    }

    //聊天界面发送消息内容设置
    public void setMessageView(int contentType, TextView tv, String content, Context mContext, Message message) {
        if (contentType == 0 || contentType == 1) {
            SpannableString string = SpanStringUtils.getEmotionContent(emotion_map_type, mContext, tv, content);
            tv.setText(string);
        } else if (contentType == 2) {
            tv.setText("[图片]");
        } else if (contentType == 3) {
            tv.setText("[音频]");
        } else if (contentType == 4) {
            tv.setText("[视频]");
        } else if (contentType == 5) {
            tv.setText("[链接]");
        } else if (contentType == 6) {
            tv.setText("[图文]");
        } else if (contentType == 7 || contentType == 10 || contentType == 11) {
            tv.setText("[文件]");
        } else if (contentType == 8 || contentType == 9) {
            tv.setText("[动态]");
        } else if (contentType == 12) {
            tv.setText("[收藏]");
        }
    }

    class ImageHolder {

    }
}
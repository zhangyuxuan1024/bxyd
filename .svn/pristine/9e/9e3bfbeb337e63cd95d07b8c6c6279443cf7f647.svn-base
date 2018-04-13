package net.iclassmate.bxyd.bean.message;

import android.os.Parcel;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.rong.common.ParcelUtils;
import io.rong.common.RLog;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MentionedInfo;
import io.rong.imlib.model.MessageContent;

/**
 * 用于发送网盘文件消息
 * Created by xyd on 2016/8/23.
 */
@MessageTag(value = "app:SpaceMsg", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class SpaceMessage extends MessageContent{

    private static final String TAG = "SpaceMessage";
    private String content;
    protected String extra;
    public static final Creator<SpaceMessage> CREATOR = new Creator() {
        public SpaceMessage createFromParcel(Parcel source) {
            return new SpaceMessage(source);
        }

        public SpaceMessage[] newArray(int size) {
            return new SpaceMessage[size];
        }
    };

    protected SpaceMessage(){

    }

    public SpaceMessage(String content){
        this.setContent(content);
    }

    public SpaceMessage(Parcel in){
        this.setExtra(ParcelUtils.readFromParcel(in));
        this.setContent(ParcelUtils.readFromParcel(in));
        this.setUserInfo((io.rong.imlib.model.UserInfo)ParcelUtils.readFromParcel(in, io.rong.imlib.model.UserInfo.class));
        this.setMentionedInfo((MentionedInfo)ParcelUtils.readFromParcel(in, MentionedInfo.class));
    }

    @Override
    public byte[] encode() {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("content", this.getEmotion(this.getContent()));
            if(!TextUtils.isEmpty(this.getExtra())) {
                jsonObj.put("extra", this.getExtra());
            }

            if(this.getJSONUserInfo() != null) {
                jsonObj.putOpt("user", this.getJSONUserInfo());
            }

            if(this.getJsonMentionInfo() != null) {
                jsonObj.putOpt("mentionedInfo", this.getJsonMentionInfo());
            }
        } catch (JSONException var4) {
            RLog.e("SpaceMessage", "JSONException " + var4.getMessage());
        }

        try {
            return jsonObj.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException var3) {
            var3.printStackTrace();
            return null;
        }
    }

    public static SpaceMessage obtain(String space){
        SpaceMessage model = new SpaceMessage();
        model.setContent(space);
        return model;
    }

    public SpaceMessage(byte[] data){
        String jsonStr = null;
        try {
            jsonStr = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            ;
        }

        try {
            JSONObject e = new JSONObject(jsonStr);
            if(e.has("content")) {
                this.setContent(e.optString("content"));
            }

            if(e.has("extra")) {
                this.setExtra(e.optString("extra"));
            }

            if(e.has("user")) {
                this.setUserInfo(this.parseJsonToUserInfo(e.getJSONObject("user")));
            }

            if(e.has("mentionedInfo")) {
                this.setMentionedInfo(this.parseJsonToMentionInfo(e.getJSONObject("mentionedInfo")));
            }
        } catch (JSONException var4) {
            RLog.e("SpaceMessage", "JSONException " + var4.getMessage());
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelUtils.writeToParcel(dest, this.getExtra());
        ParcelUtils.writeToParcel(dest, this.content);
        ParcelUtils.writeToParcel(dest, this.getUserInfo());
        ParcelUtils.writeToParcel(dest, this.getMentionedInfo());
    }

    private String getEmotion(String content) {
        Pattern pattern = Pattern.compile("\\[/u([0-9A-Fa-f]+)\\]");
        Matcher matcher = pattern.matcher(content);
        StringBuffer sb = new StringBuffer();

        while(matcher.find()) {
            int inthex = Integer.parseInt(matcher.group(1), 16);
            matcher.appendReplacement(sb, String.valueOf(Character.toChars(inthex)));
        }

        matcher.appendTail(sb);
        RLog.d("SpaceMessage", "getEmotion--" + sb.toString());
        return sb.toString();
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

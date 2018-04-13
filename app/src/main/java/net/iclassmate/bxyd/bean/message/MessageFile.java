
package net.iclassmate.bxyd.bean.message;

import android.net.Uri;

import net.iclassmate.bxyd.utils.HttpManager;

import io.rong.imlib.model.Message;

/**
 * Created by xyd on 2016/8/25.
 */
public class MessageFile {
    private String objectName;  //消息类型标识。如RC:TxtMsg，RC:ImgMsg，RC:VcMsg等
    private int MessageId;      //消息id
    private long time;          //接收消息时间
    private Uri uri;            //图片消息--图片uri
    private String fileId;      //网盘消息--网盘文件id
    private String fileName;    //网盘消息--网盘文件名称
    private String url;         //网盘消息--图片
    private int contentType;    //类型
    private boolean isCheck;    //是否选中
    private boolean isVisibility;//是否显示可选按钮
    private Message message;    //融云消息

    //图片
    public MessageFile(String objectName, long time, Uri uri, String fileName, int MessageId, Message message) {
        this.objectName = objectName;
        this.time = time;
        this.uri = uri;
        this.fileName = fileName;
        this.MessageId = MessageId;
        this.message = message;
    }

    //网盘
    public MessageFile(String objectName, long time, String fileId, String fileName, int contentTpye, int MessageId, Message message) {
        this.objectName = objectName;
        this.time = time;
        this.fileId = fileId;
        this.fileName = fileName;
        this.contentType = contentTpye;
        this.isCheck = false;
        this.MessageId = MessageId;
        this.message = message;
    }

    /**
     * 获取网盘文件缩略图url（视频、图片）
     *
     * @param fileId
     * @return
     */
    public String getFileInfo(final String fileId) {
        String result = null;
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpManager httpManager = new HttpManager();
                String result = httpManager.getThumbnailIconUrl(fileId);
            }
        }).start();


//        if (result != null && !result.equals("") && !result.equals("404")){
//            try {
//                JSONObject json = new JSONObject(result);
//                scale = json.getString("scale");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
        return result;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public int getMessageId() {
        return MessageId;
    }

    public void setMessageId(int messageId) {
        MessageId = messageId;
    }

    public boolean isVisibility() {
        return isVisibility;
    }

    public void setVisibility(boolean visibility) {
        isVisibility = visibility;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "MessageFile{" +
                "objectName='" + objectName + '\'' +
                ", MessageId=" + MessageId +
                ", time=" + time +
                ", uri=" + uri +
                ", fileId='" + fileId + '\'' +
                ", fileName='" + fileName + '\'' +
                ", url='" + url + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}

package net.iclassmate.bxyd.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import net.iclassmate.bxyd.bean.message.RMessage;
import net.iclassmate.bxyd.bean.message.SpaceMessage;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.ui.activitys.study.LookPicActivity;
import net.iclassmate.bxyd.ui.activitys.study.openfile.OpenAudioActivity;
import net.iclassmate.bxyd.ui.activitys.study.openfile.OpenFailActivity;
import net.iclassmate.bxyd.ui.activitys.study.openfile.OpenPicActivity;
import net.iclassmate.bxyd.ui.activitys.study.openfile.OpenTextFileActivity;
import net.iclassmate.bxyd.ui.activitys.study.openfile.OpenVideoActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.rong.imlib.model.Message;
import io.rong.message.ImageMessage;
import io.rong.message.TextMessage;

/**
 * Created by xydbj on 2016/8/19.
 */
public class OpenFile {
    private static final int RET_DEL_FILE = 5;

    //打开文件  文件id,文件名
    public static void openFile(String id, String filename, int type, Context mContext) {
        String name = filename.toLowerCase();
        if (name.contains(".")) {
            name = name.substring(name.lastIndexOf(".") + 1, name.length());
        }
        Intent intent = null;
        switch (name) {
            case "doc":
            case "docx":
            case "txt":
            case "pdf":
            case "wps":
            case "xls":
            case "xlsx":
                intent = new Intent(mContext, OpenTextFileActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("name", filename);
                break;
            case "wav":
            case "mp3":
            case "wma":
            case "wva":
            case "ogg":
            case "ape":
            case "aif":
            case "au":
            case "ram":
            case "mmf":
            case "amr":
            case "aac":
            case "flac":
                intent = new Intent(mContext, OpenAudioActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("name", filename);
                break;
            case "avi":
            case "mpg":
            case "mpeg":
            case "mov":
            case "rm":
            case "rmvb":
            case "mp4":
            case "3gp":
            case "flv":
            case "wmv":
                intent = new Intent(mContext, OpenVideoActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("fileName", filename);
                intent.putExtra("name", filename);
//                String url = String.format(Constant.STUDY_OPEN_FILE, id);
//                intent = new Intent(Intent.ACTION_VIEW);
//                Uri uri = Uri.parse(url);
//                intent.setDataAndType(uri, "video/*");
                break;
            case "bmp":
            case "gif":
            case "jpg":
            case "pic":
            case "png":
            case "tif":
            case "jpeg":
                intent = new Intent(mContext, OpenPicActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("name", filename);
                break;

            //            case "xls":
//            case "xlsx":
            case "et":

//                break;
            case "ppt":
            case "pptx":
            case "dps":

//                break;
            default:
                intent = new Intent(mContext, OpenFailActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("name", filename);
                break;
        }
        if (intent != null) {
            intent.putExtra("type", type);
            if (mContext instanceof Activity) {
                Activity activity = (Activity) mContext;
                activity.startActivityForResult(intent, RET_DEL_FILE);
            } else if (mContext instanceof FragmentActivity) {
                FragmentActivity activity = (FragmentActivity) mContext;
                activity.startActivityForResult(intent, RET_DEL_FILE);
            } else {
                mContext.startActivity(intent);
            }
        }
    }

    //打开图片
    public static void openPic(View v, Context mContext, List<Message> messageList) {
        Object o = v.getTag();
        Message messageTag = null;
        if (o instanceof Message) {
            messageTag = (Message) o;
        }
        if (messageTag == null) {
            return;
        }

        int msgId = messageTag.getMessageId();
        int index = 0;
        List<Object> list = new ArrayList<Object>();
        List<Message> picList = new ArrayList<Message>();
        if (messageList != null) {
            for (int i = 0; i < messageList.size(); i++) {
                Message message = messageList.get(i);
                if (message.getContent() instanceof ImageMessage) {
                    ImageMessage imageMessage = (ImageMessage) message.getContent();
                    Uri uri = imageMessage.getRemoteUri();
                    if (uri == null) {
                        RMessage rMessage = new RMessage(message);
                        String uri1 = rMessage.getUri();
                        if (uri1 != null && !uri1.equals("")) {
                            list.add(uri1);
                        }
                    } else {
                        list.add(uri);
                    }
                    picList.add(message);
                } else if (message.getContent() instanceof TextMessage || message.getContent() instanceof SpaceMessage) {
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
                        String filename = json.optString("FileName");
                        int contentType = json.optInt("ContentType");
                        String fileId = json.optString("FileID");
                        if (filename != null && !filename.equals("")) {
                            contentType = FileUtils.getContentType(filename);
                        }
                        if (contentType == 2) {
                            String url = "";
                            if (fileId.contains("http")) {
                                url = fileId;
                            } else {
                                url = String.format(Constant.STUDY_OPEN_FILE, fileId);
                            }
                            list.add(url);
                            picList.add(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            for (int i = picList.size() - 1; i >= 0; i--) {
                Message message = picList.get(i);
                if (message.getMessageId() == msgId) {
                    index = i;
                    break;
                }
            }
            Intent intent = new Intent(mContext, LookPicActivity.class);
            intent.putExtra("type", 3);
            intent.putExtra("index", index);
            intent.putExtra("list", (Serializable) list);
            mContext.startActivity(intent);
        }
    }

    public static boolean isVideo(String filename) {
        boolean ret = false;
        String name = filename.toLowerCase();
        if (name.contains(".")) {
            name = name.substring(name.lastIndexOf(".") + 1, name.length());
        }
        if (name.equals("avi") || name.equals("mpg") || name.equals("mpeg") || name.equals("mov") ||
                name.equals("rm") || name.equals("rmvb") || name.equals("mp4") || name.equals("3gp") || name.equals("flv") || name.equals("wmv")) {
            ret = true;
        }
        return ret;
    }
}

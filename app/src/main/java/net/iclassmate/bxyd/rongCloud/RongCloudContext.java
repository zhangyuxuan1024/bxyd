package net.iclassmate.bxyd.rongCloud;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import net.iclassmate.bxyd.utils.MessageCallback;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;

/**
 * Created by xyd on 2016/6/8.
 */
public class RongCloudContext {
    private static final String TAG = "RongCloudContext";

    private static RongCloudContext self;

    private SharedPreferences sharedPreferences;

    public Context mContext;

    public String userId;

    private MessageCallback messageCallback;


    public static RongCloudContext getInstance() {
        if (self == null) {
            self = new RongCloudContext();
        }
        return self;
    }

    public RongCloudContext() {

    }

    public RongCloudContext(Context context) {
        self = this;
    }

    public void init(Context context, MessageCallback callback) {
        mContext = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        messageCallback = callback;
    }


    /**
     * 监听接收消息
     */
    public void registerReceiveMessageListener() {
        RongIMClient.setOnReceiveMessageListener(onReceiveMessageListener);
    }

    RongIMClient.OnReceiveMessageListener onReceiveMessageListener = new RongIMClient.OnReceiveMessageListener() {
        @Override
        public boolean onReceived(Message message, int left) {
            messageCallback.getMessage(message);
            return false;
        }

    };

}
package net.iclassmate.bxyd.ui.activitys.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import net.iclassmate.bxyd.utils.DataCallback;

import io.rong.imlib.model.Message;

/**
 * Created by xydbj on 2016/8/27.
 */
public class ReceiverMessage extends BroadcastReceiver {
    private DataCallback dataCallback;

    public ReceiverMessage(DataCallback dataCallback) {
        this.dataCallback = dataCallback;
    }

    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Message message = bundle.getParcelable("message");
        dataCallback.sendData(message);
    }
}
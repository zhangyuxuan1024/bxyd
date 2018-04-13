package net.iclassmate.bxyd.rongCloud.message;

import android.os.Parcel;

import io.rong.imlib.MessageTag;
import io.rong.message.CommandNotificationMessage;


@MessageTag(value = "RC:CmdNtf", flag = MessageTag.NONE)
public class RCCommandNotificationMessage extends CommandNotificationMessage {

    public RCCommandNotificationMessage(Parcel in) {
        super(in);
    }
}
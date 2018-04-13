package net.iclassmate.bxyd.utils;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;

import net.iclassmate.bxyd.R;

/**
 * Created by xydbj on 2016/10/19.
 */
public class NoticeUtills {
    /**
     * song:是否有声音
     * shoch：是否有震动
     */
    public static void soundRing(Context context, boolean song, boolean shock) {
        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int ringerMode = manager.getRingerMode();
        int mode = manager.getMode();
        //系统声音
        int systemVolume = manager.getStreamVolume(AudioManager.STREAM_SYSTEM);
        //铃声
        int ringVolume = manager.getStreamVolume(AudioManager.STREAM_RING);
        if (ringerMode == AudioManager.RINGER_MODE_SILENT || mode == AudioManager.MODE_IN_CALL || systemVolume == 0 || ringVolume == 0) {
            song = false;
            shock = false;
        }
        try {
            if (song) {
                MediaPlayer mPlayer = MediaPlayer.create(context, R.raw.water);
                mPlayer.start();
            }
            if (shock) {
                if (context instanceof Activity) {
                    VibratorUtil.Vibrate((Activity) context, 500);   //震动500ms
                } else if (context instanceof FragmentActivity) {
                    VibratorUtil.Vibrate((FragmentActivity) context, 500);   //震动500ms
                }
            }
        } catch (Exception e) {

        }
    }

}

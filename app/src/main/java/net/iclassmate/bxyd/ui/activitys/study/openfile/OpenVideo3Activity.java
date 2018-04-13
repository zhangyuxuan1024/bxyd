package net.iclassmate.bxyd.ui.activitys.study.openfile;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.iclassmate.bxyd.R;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

/**
 * Created by xydbj on 2016.9.7.
 */
public class OpenVideo3Activity extends Activity implements View.OnClickListener, MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener {

    private String fileName, filePath, id, videoPath;
    private ProgressBar pb;
    private MediaController controller;
    private VideoView mVideoView;
    private ImageView openvideo3_play_or_stop;
    private ImageButton openvideo3_back;
    private TextView openvideo3_title;
    private boolean play_or_stop = false;
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1011:
                    Log.i("info", "OpenVideo3Activity:videoPath=" + videoPath);
                    initData(videoPath);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_openvideo3);
        //定义全屏参数
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //获得当前窗体对象
        Window window = OpenVideo3Activity.this.getWindow();
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag);
        //必须写这个，初始化加载库文件
        Vitamio.isInitialized(this);
        if (!LibsChecker.checkVitamioLibs(this)) {
            return;
        }
        //接收数据
        Intent intent = getIntent();
        fileName = intent.getStringExtra("fileName");
        filePath = intent.getStringExtra("videoPath");
        id = intent.getStringExtra("id");
        initView();
        initData("http://space-oss.iclassmate.cn/user/ctsi/filestore/buffer/2016102613/d61156954f174c1cbf2bab8e3f832ee6/d61156954f174c1cbf2bab8e3f832ee6.mp4");
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                videoPath = HttpManager.getVideoPath(id);
//                mHandler.sendEmptyMessage(1011);
//            }
//        }).start();

    }

    //初始化控件
    private void initView() {
        mVideoView = (VideoView) findViewById(R.id.openvideo3_vv);
        openvideo3_back = (ImageButton) findViewById(R.id.openvideo3_back);
        openvideo3_title = (TextView) findViewById(R.id.openvideo3_title);
        openvideo3_play_or_stop = (ImageView) findViewById(R.id.openvideo3_play_or_stop);
        openvideo3_play_or_stop.setOnClickListener(this);

        controller = new MediaController(this);
        pb = (ProgressBar) findViewById(R.id.openvideo3_loading);
    }

    //初始化数据
    private void initData(String videoPath) {
        openvideo3_title.setText(fileName);
        Uri uri = Uri.parse(videoPath);
        if (!"".equals(filePath) && !"null".equals(filePath) && filePath != null) {
            mVideoView.setVideoPath(filePath);
        } else {
            mVideoView.setVideoURI(uri);
        }
        controller.setFileName(fileName);
        mVideoView.setMediaController(controller);
        mVideoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);//高画质
        controller.show(2000);//并没有用
        mVideoView.requestFocus();
        mVideoView.setOnInfoListener(this);
        mVideoView.setOnBufferingUpdateListener(this);
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setPlaybackSpeed(1.0f);
            }
        });
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                    pb.setVisibility(View.VISIBLE);
                }
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                mVideoView.start();
                pb.setVisibility(View.GONE);
                break;
            case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                break;
        }
        return true;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        //屏幕切换时，设置全屏
        if (mVideoView != null) {
            mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //播放与暂停
            case R.id.openvideo3_play_or_stop:
//                if (play_or_stop){
//                    mVideoView.start();
//                    openvideo3_play_or_stop.setImageResource(R.mipmap.ic_bofang_shipinbofang);
//                } else {
//                    mVideoView.pause();
//                    openvideo3_play_or_stop.setImageResource(R.mipmap.ic_zanting_shipinbofang);
//                }
//                play_or_stop = !play_or_stop;
                break;
            case R.id.openvideo3_back:
                OpenVideo3Activity.this.finish();
                break;
        }
    }
}

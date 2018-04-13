package net.iclassmate.bxyd.ui.activitys.study.openfile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.utils.CustomMediaController;
import net.iclassmate.bxyd.utils.HttpManager;
import net.iclassmate.bxyd.utils.Md5;
import net.iclassmate.bxyd.utils.StringUtils;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.VideoView;

public class OpenVideo2Activity extends FragmentActivity implements MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener {
    private String filePath, fileName, id;
    private int type;

    private Uri uri;
    private ProgressBar pb;
    private TextView downloadRateView, loadRateView;
    private CustomMediaController mCustomMediaController;
    private VideoView mVideoView;

    private SharedPreferences preferences;
    private String key_progress;
    private HttpManager httpManager;
    private Handler mHandler = new Handler();
    public static final int RET_DEL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_video2);

        //定义全屏参数
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //获得当前窗体对象
        Window window = this.getWindow();
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag);
        //必须写这个，初始化加载库文件
        Vitamio.isInitialized(this);
        initView();
        init();
    }

    private void init() {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        type = intent.getIntExtra("type", 0);
        filePath = intent.getStringExtra("path");
        fileName = intent.getStringExtra("fileName");
        if (StringUtils.isEmpty(fileName)) {
            fileName = intent.getStringExtra("name");
        }

        if (!StringUtils.isEmpty(fileName)) {
            mCustomMediaController.setVideoName(fileName);
        }

        httpManager = new HttpManager();
        if (!StringUtils.isEmpty(filePath)) {
            Log.i("info","filePath不为空,马上进入 initData() 方法");
            initData();
        } else if (!StringUtils.isEmpty(id)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    filePath = httpManager.getVideoUrl(id);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            initData();
                        }
                    });
                }
            }).start();
        }
    }

    //初始化控件
    private void initView() {
        mVideoView = (VideoView) findViewById(R.id.buffer);
        pb = (ProgressBar) findViewById(R.id.probar);
        mCustomMediaController = new CustomMediaController(this, mVideoView, this);
        downloadRateView = (TextView) findViewById(R.id.download_rate);
        loadRateView = (TextView) findViewById(R.id.load_rate);
    }

    //初始化数据
    private void initData() {
        if (StringUtils.isEmpty(filePath)) {
            return;
        }
        Log.i("info", "OpenVideo2Activity中的initData()方法中filePath=" + filePath);
        Log.i("info", "OpenVideo2Activity中的initData()方法中type=" + type);
        uri = Uri.parse(filePath);
        Log.i("info", "OpenVideo2Activity中的initData()方法中uri=" + uri);
        mVideoView.setVideoURI(uri);//设置视频播放地址
        mCustomMediaController.show(5000);
        mVideoView.setMediaController(mCustomMediaController);
        mVideoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);//高画质
        mVideoView.requestFocus();
        mVideoView.setOnInfoListener(this);
        mVideoView.setOnBufferingUpdateListener(this);
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setPlaybackSpeed(1.0f);
            }
        });

        //获取上一次保存的进度
        preferences = getSharedPreferences(Constant.SHARED_PREFERENCES, MODE_PRIVATE);
        if (!StringUtils.isEmpty(id)) {
            key_progress = Md5.md5Password(id) + "_progress";
        } else if (!StringUtils.isEmpty(fileName)) {
            key_progress = Md5.md5Password(fileName) + "_progress";
        } else {
            key_progress = "";
        }
        long progress = preferences.getLong(key_progress, 0);

        mVideoView.seekTo(progress);
        mVideoView.start();

//        mCustomMediaController.setType(type);
        mCustomMediaController.setFid(id);
        mCustomMediaController.setFileName(fileName);
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                    pb.setVisibility(View.VISIBLE);
                    downloadRateView.setText("");
                    loadRateView.setText("");
                    downloadRateView.setVisibility(View.VISIBLE);
                    loadRateView.setVisibility(View.VISIBLE);
                }
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                mVideoView.start();
                pb.setVisibility(View.GONE);
                downloadRateView.setVisibility(View.GONE);
                loadRateView.setVisibility(View.GONE);
                break;
            case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                downloadRateView.setText("" + extra + "kb/s" + "  ");
                break;
        }
        return true;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        loadRateView.setText(percent + "%");
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
    protected void onPause() {
        super.onPause();
        //保存进度
        try {
            if (!StringUtils.isEmpty(key_progress)) {
                preferences.edit().putLong(key_progress, mVideoView.getCurrentPosition()).commit();
            }
        } catch (Exception e) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RET_DEL) {
                Intent intent = new Intent();
                intent.putExtra("id", id);
                setResult(RESULT_OK, intent);
                this.finish();
            }
        }
    }
}

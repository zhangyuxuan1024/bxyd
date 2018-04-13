package net.iclassmate.bxyd.ui.activitys.study.openfile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.iclassmate.bxyd.R;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

/**
 * Created by xydbj on 2017.4.21.
 */

public class ScanOpenVideoActivity extends Activity {

    private VideoView videoView;
    private ProgressBar progressBar;
    private TextView percentTv, netSpeedTv;
    private String path, fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanopenvideo);

        Vitamio.initialize(this);

        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        Window window = this.getWindow();
        window.setFlags(flag, flag);

        Intent intent = getIntent();
        path = intent.getStringExtra("path");
        fileName = intent.getStringExtra("fileName");
        if (Vitamio.initialize(this)) {
            initView();

            initPath();

            initListener();
        }
    }

    public void initView() {
        videoView = (VideoView) findViewById(R.id.scan_videoview);
        progressBar = (ProgressBar) findViewById(R.id.scan_progressbar);
        //显示缓冲百分比的TextView
        percentTv = (TextView) findViewById(R.id.buffer_percent);
        //显示下载网速的TextView
        netSpeedTv = (TextView) findViewById(R.id.net_speed);
    }

    public void initPath() {
        Uri uri = Uri.parse(path);
        videoView.setVideoURI(uri);
        MediaController controller = new MediaController(this);
        controller.setFileName(fileName);
        videoView.setMediaController(controller);
        videoView.start();
        progressBar.setVisibility(View.INVISIBLE);
    }

    public void initListener() {
        videoView.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                percentTv.setText("已缓冲：" + percent + "%");
            }
        });

        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    //开始缓冲
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        percentTv.setVisibility(View.VISIBLE);
                        netSpeedTv.setVisibility(View.VISIBLE);
                        mp.pause();
                        break;
                    //缓冲结束
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        percentTv.setVisibility(View.GONE);
                        netSpeedTv.setVisibility(View.GONE);
                        mp.start();
                        break;
                    //正在缓冲
                    case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                        netSpeedTv.setText("当前网速:" + extra + "kb/s");
                        break;
                }
                return true;
            }
        });
    }
}

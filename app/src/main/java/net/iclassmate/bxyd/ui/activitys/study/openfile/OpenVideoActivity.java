package net.iclassmate.bxyd.ui.activitys.study.openfile;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.ui.activitys.study.StudyWindowActivity;

import java.util.ArrayList;
import java.util.List;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;


public class OpenVideoActivity extends FragmentActivity implements View.OnClickListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {
    private TextView tv_back;
    private ImageView img_back, img_right, img_anim;
    private TextView tv_title;

    private AnimationDrawable anim;
    private VideoView videoView;
    private Context mContext;
    private static final int RET_DEL = 1;
    private String id;
    private String filePath, fileName, name;
    private int type;
    private boolean isPlaying;
    private int errorTime = 1;
    private long cur_position;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 404:
                    anim.stop();
                    img_anim.setVisibility(View.VISIBLE);
                    img_anim.setBackgroundColor(Color.BLACK);
                    img_anim.setImageResource(R.mipmap.img_jiazaishibai);
                    isPlaying = false;
                    break;
                case 1:
                    if (videoView.isPlaying()) {
                        anim.stop();
                        img_anim.setVisibility(View.GONE);
                        errorTime = 0;
                        if (videoView.getCurrentPosition() > cur_position) {
                            cur_position = videoView.getCurrentPosition();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_video);
        if (!LibsChecker.checkVitamioLibs(this)) {
            return;
        }
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        type = intent.getIntExtra("type", 0);
        filePath = intent.getStringExtra("path");
        fileName = intent.getStringExtra("fileName");
        name = intent.getStringExtra("name");
        init();
        playVideo(id, filePath);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (cur_position < 0) {
                cur_position = 0;
            }
            videoView.seekTo(cur_position);
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (cur_position < 0) {
                cur_position = 0;
            }
            videoView.seekTo(cur_position);
        }
    }

    private void init() {
        videoView = (VideoView) findViewById(R.id.open_file_videoview);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        tv_back = (TextView) findViewById(R.id.study_message_left_tv);
        tv_back.setOnClickListener(this);
        img_back = (ImageView) findViewById(R.id.study_message_back);
        img_back.setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.study_message_title_tv);
        tv_title.setVisibility(View.INVISIBLE);
        tv_back.setText("返回");
        img_anim = (ImageView) findViewById(R.id.img_anim);
        anim = (AnimationDrawable) img_anim.getBackground();
        anim.start();
        //img_anim.setVisibility(View.GONE);
        mContext = this;

        img_right = (ImageView) findViewById(R.id.study_message_right_icon);
        img_right.setVisibility(View.VISIBLE);
        img_right.setOnClickListener(this);
        if (type == 1) {
            img_right.setImageResource(R.mipmap.ic_xiaobaidian);
        } else if (type == 2) {
            img_right.setImageResource(R.drawable.img_study_del);
        } else if (type == 3) {
            img_right.setImageResource(R.mipmap.ic_xiaobaidian);
        }else if (type == 4) {
            img_right.setVisibility(View.INVISIBLE);
        } else {
            img_right.setVisibility(View.INVISIBLE);
        }

        isPlaying = true;
        new Timer().start();
    }

    private void playVideo(String id, String filePath) {
        try {
            String url = String.format(Constant.STUDY_OPEN_FILE, id);
//            Log.i("info", "视频路径=" + url);
            Uri video = Uri.parse(url);
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
            if (!"".equals(fileName) || !"null".equals(fileName) || fileName != null) {
                mediaController.setFileName(fileName);
            }
            videoView.requestFocus();
            videoView.setMediaController(mediaController);
            if (filePath != null && !"".equals(filePath) && !"null".equals(filePath)) {
                videoView.setVideoPath(filePath);
            } else {
                videoView.setVideoURI(video);
            }
            videoView.setOnErrorListener(this);
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    videoView.start();
//                    mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
//                        @Override
//                        public void onBufferingUpdate(MediaPlayer mp, int percent) {
//                            cur_position = mp.getCurrentPosition();
//                        }
//                    });
                }
            });
        } catch (Exception e) {
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.study_message_left_tv:
            case R.id.study_message_back:
                close();
                break;
            case R.id.study_message_right_icon:
                if (type == 1) {
                    Intent intent = new Intent(mContext, StudyWindowActivity.class);
                    List<String> list = new ArrayList<String>();
                    list.add("保存到网盘");
                    intent.putStringArrayListExtra("list", (ArrayList<String>) list);
                    intent.putExtra("saveid", id);
                    startActivity(intent);
                } else if (type == 2) {
                    Intent intent = new Intent(mContext, StudyWindowActivity.class);
                    List<String> list = new ArrayList<String>();
                    list.add("是否确认删除文件删除");
                    intent.putStringArrayListExtra("list", (ArrayList<String>) list);
                    startActivityForResult(intent, RET_DEL);
                } else if (type == 3) {
                    Intent intent = new Intent(mContext, StudyWindowActivity.class);
                    List<String> list = new ArrayList<String>();
                    list.add("转发到主页");
                    list.add("转发给好友");
                    list.add("下载");
                    intent.putStringArrayListExtra("list", (ArrayList<String>) list);
                    intent.putExtra("type", "file");
                    intent.putExtra("chatData", id);
                    intent.putExtra("filename", name);
                    startActivity(intent);
                }
                break;
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

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        if (errorTime < 6) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        playVideo(id, filePath);
                        if (cur_position > 0) {
                            videoView.seekTo(cur_position);
                        }
                    } catch (Exception e) {
                    }
                }
            }, 1000);
            errorTime++;
        } else {
            anim.stop();
            img_anim.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.INVISIBLE);
            img_anim.setBackgroundColor(Color.BLACK);
            img_anim.setImageResource(R.mipmap.img_jiazaishibai);
            isPlaying = false;
        }
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        playVideo(id, filePath);
    }

    @Override
    public void onBackPressed() {
        close();
    }

    private void close() {
        isPlaying = false;
        videoView.stopPlayback();
        this.finish();
    }

    class Timer extends Thread {
        @Override
        public void run() {
            while (isPlaying) {
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mHandler.sendEmptyMessage(1);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("OpenVideoActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("OpenVideoActivity");
        MobclickAgent.onPause(this);
    }
}

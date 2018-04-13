package net.iclassmate.bxyd.ui.activitys.study.openfile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.ui.activitys.study.StudyWindowActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OpenAudioActivity extends FragmentActivity implements View.OnClickListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener, SeekBar.OnSeekBarChangeListener {
    private TextView tv_back;
    private ImageView img_back, img_right, img_anim;
    private TextView tv_title;
    private Context mContext;
    private MediaPlayer mp;
    private boolean isPlaying;
    private ImageView img_start;
    private TextView tv_time;
    private SeekBar seekBar;
    private String url;
    private int cur_time;
    private boolean isachieve;

    private static final int RET_DEL = 1;
    private String id;
    private int type;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    try {
                        if (mp == null || !mp.isPlaying() || !isPlaying) {
                            return;
                        }
                        setTime(mp.getCurrentPosition(), mp.getDuration());
                        int position = mp.getCurrentPosition();
                        int duration = mp.getDuration();
                        //Log.i("info", "音频长度=" + duration);
                        if (duration == 0) {
                            duration = 200 * 1000;
                        }
                        if (duration > 0) {
                            long pos = seekBar.getMax() * position / duration;
                            seekBar.setProgress((int) pos);
                        }
                        img_anim.setVisibility(View.INVISIBLE);
                    } catch (Exception e) {
                        img_anim.setVisibility(View.VISIBLE);
                        img_anim.setBackgroundColor(Color.WHITE);
                        img_anim.setImageResource(R.mipmap.img_jiazaishibai);
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
        setContentView(R.layout.activity_open_audio);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        type = intent.getIntExtra("type", 0);
        init();
        playMusic(id);
    }

    private void playMusic(String id) {
        url = String.format(Constant.STUDY_OPEN_FILE, id);
        Log.i("info", "音乐路径=" + url);
        mp = new MediaPlayer();
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setOnCompletionListener(this);
        mp.setOnErrorListener(this);
        mp.setOnBufferingUpdateListener(this);
        mp.setOnPreparedListener(this);
        try {
            mp.reset();
            mp.setDataSource(url);
            mp.prepareAsync();
            seekBar.setProgress(0);
            img_anim.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            img_anim.setVisibility(View.VISIBLE);
            img_anim.setBackgroundColor(Color.WHITE);
            img_anim.setImageResource(R.mipmap.img_jiazaishibai);
        }
    }

    private void init() {
        tv_back = (TextView) findViewById(R.id.study_message_left_tv);
        tv_back.setOnClickListener(this);
        img_back = (ImageView) findViewById(R.id.study_message_back);
        img_back.setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.study_message_title_tv);
        tv_title.setVisibility(View.INVISIBLE);
        tv_back.setText("返回");
        img_anim = (ImageView) findViewById(R.id.img_anim);
        mContext = this;

        img_start = (ImageView) findViewById(R.id.open_audio_img);
        tv_time = (TextView) findViewById(R.id.open_audio_tv);
        img_start.setOnClickListener(this);
        seekBar = (SeekBar) findViewById(R.id.open_audio_seekbar);
        seekBar.setMax(100);
        seekBar.setOnSeekBarChangeListener(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        cur_time = 0;
        isachieve = false;

        img_right = (ImageView) findViewById(R.id.study_message_right_icon);
        img_right.setVisibility(View.VISIBLE);
        img_right.setOnClickListener(this);

        if (type == 1) {
            img_right.setImageResource(R.mipmap.ic_xiaobaidian);
        } else if (type == 2) {
            img_right.setImageResource(R.drawable.img_study_del);
        }else if(type==3){
            img_right.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.study_message_left_tv:
            case R.id.study_message_back:
                this.finish();
                break;
            case R.id.open_audio_img:
                setPlay();
                setImage();
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
                }
                break;
        }
    }

    private void setImage() {
        if (isPlaying) {
            img_start.setImageResource(R.mipmap.ic_audio_pause);
        } else {
            img_start.setImageResource(R.mipmap.ic_audio_play);
        }
    }

    private void setPlay() {
        isPlaying = !isPlaying;
        if (isPlaying) {
            if (!isachieve) {
                mp.start();
                new SetTime().start();
            } else {
                try {
                    mp.setDataSource(url);
                    mp.prepareAsync();
                    seekBar.setProgress(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            mp.pause();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        seekBar.setProgress(seekBar.getMax());
        isPlaying = false;
        mediaPlayer.reset();
        setImage();
        isachieve = true;
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        seekBar.setSecondaryProgress(percent);
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        Log.i("info", "音频总时长=" + mp.getDuration() + ",时长=" + mediaPlayer.getDuration());
        isPlaying = true;
        isachieve = false;
        new SetTime().start();
        setImage();
        setTime(0, mediaPlayer.getDuration());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mp != null) {
            if (isPlaying) {
                isPlaying = false;
                mp.stop();
            }
            mp.release();
        }
    }

    private void setTime(int time, int total) {
        int a = 0, b = 0;
        String ret = "", ret2 = "";
        time = time / 1000;
        total = total / 1000;
        a = time / 60;
        b = time % 60;
        if (b < 10) {
            ret = a + ":0" + b;
        } else {
            ret = a + ":" + b;
        }

        a = total / 60;
        b = total % 60;
        if (b < 10) {
            ret2 = a + ":0" + b;
        } else {
            ret2 = a + ":" + b;
        }
        tv_time.setText(ret);
    }

    //拖动中
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        seekBar.setProgress(progress);
        if (fromUser) {
            int duration = mp.getDuration();
            if (duration == 0) {
                duration = 200 * 1000;
            }
            mp.seekTo(progress * duration / seekBar.getMax());
        }
    }

    //开始拖动
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    //结束拖动
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

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

    class SetTime extends Thread {
        @Override
        public void run() {
            while (isPlaying) {
                try {
                    Thread.sleep(200);
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
        MobclickAgent.onPageStart("OpenAudioActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("OpenAudioActivity");
        MobclickAgent.onPause(this);
    }
}
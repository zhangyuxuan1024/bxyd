package net.iclassmate.bxyd.ui.activitys.study.openfile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.ui.activitys.study.StudyWindowActivity;
import net.iclassmate.bxyd.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OpenPicActivity extends FragmentActivity implements View.OnClickListener {
    private TextView tv_back;
    private ImageView img_back, img_right;
    private TextView tv_title;

    private ImageView zoom_pic;
    private Context mContext;
    private static final int RET_DEL = 1;
    private String id, path;
    private int type;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_pic);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        type = intent.getIntExtra("type", 0);
        path = intent.getStringExtra("filePath");
        uri = intent.getData();
        init();
        getImage();
    }

    private void init() {
        zoom_pic = (ImageView) findViewById(R.id.open_file_pic);

        tv_back = (TextView) findViewById(R.id.study_message_left_tv);
        tv_back.setOnClickListener(this);
        img_back = (ImageView) findViewById(R.id.study_message_back);
        img_back.setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.study_message_title_tv);
        tv_title.setVisibility(View.INVISIBLE);
        tv_back.setText("返回");
        mContext = this;

        img_right = (ImageView) findViewById(R.id.study_message_right_icon);
        img_right.setVisibility(View.VISIBLE);
        img_right.setOnClickListener(this);
        if (type == 1) {
            img_right.setImageResource(R.mipmap.ic_xiaobaidian);
        } else if (type == 2) {
            img_right.setImageResource(R.drawable.img_study_del);
        } else if (type == 3) {
            img_right.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.study_message_left_tv:
            case R.id.study_message_back:
                this.finish();
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

    public void getImage() {
        String url = "";
        if (id != null && !id.equals("")) {
            url = String.format(Constant.STUDY_OPEN_FILE, id);
            //Log.i("info", "图片路径=" + url);
            Picasso.with(mContext)
                    .load(url)
                    .placeholder(R.mipmap.img_morentupian)
                    .config(Bitmap.Config.RGB_565)
                    .error(R.mipmap.img_jiazaishibai2)
                    .into(zoom_pic);
        } else if (path != null && !path.equals("")) {
            File file = new File(path);
            try {
                if (file.exists()) {
                    if (FileUtils.getFileOrFilesSize(path, FileUtils.SIZETYPE_MB) > 5) {
                        zoom_pic.setImageResource(R.mipmap.img_morentupian);
                    } else {
                        Bitmap bm = BitmapFactory.decodeFile(path);
                        zoom_pic.setImageBitmap(bm);
                    }
                }
            } catch (Exception e) {
                zoom_pic.setImageResource(R.mipmap.img_morentupian);
            }
        } else if (uri != null && !uri.equals("")) {
            Picasso.with(mContext).load(uri).placeholder(R.mipmap.img_morentupian).config(Bitmap.Config.RGB_565).error(R.mipmap.img_jiazaishibai2).into(zoom_pic);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("OpenPicActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("OpenPicActivity");
        MobclickAgent.onPause(this);
    }
}
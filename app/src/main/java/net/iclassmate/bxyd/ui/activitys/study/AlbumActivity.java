package net.iclassmate.bxyd.ui.activitys.study;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.adapter.study.GridAlbumAdapter;
import net.iclassmate.bxyd.bean.study.ImageState;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.constant.StudyConstance;
import net.iclassmate.bxyd.ui.activitys.study.openfile.OpenAlbumPicActivity;
import net.iclassmate.bxyd.ui.activitys.study.openfile.OpenPhotoActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AlbumActivity extends FragmentActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private GridView gridView;
    private List<ImageState> mPicList;
    private GridAlbumAdapter adapter;
    private TextView tv_back, tv_sure;
    public static final int RESULT_CAMERA = 1;
    public static final int RESULT_CHECK = 2;
    private String camera_photo_name;
    private Context mContext;

    private ImageView img_anim;
    private AnimationDrawable anim;
    private List<String> listPic;
    private List<Object> listAllFile;
    private boolean isCanSelect;
    private int cur_click;

    private int chatType;
    private String tid;
    private boolean sendpic;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            anim.stop();
            img_anim.setVisibility(View.GONE);
            data2iew();
        }
    };

    private void data2iew() {
        for (int i = 0; i < listAllFile.size(); i++) {
            Object object = listAllFile.get(i);
            if (object instanceof String) {
                String ret = (String) object;
                if (listPic.contains(ret)) {
                    int index = listPic.indexOf(ret);
                    ImageState state = mPicList.get(index);
                    state.check = true;
                    mPicList.set(index, state);
                }
            }
        }
        adapter.notifyDataSetChanged();
        setTitleText();
        if (mPicList.size() < 1) {
            img_anim.setVisibility(View.VISIBLE);
            img_anim.setBackgroundColor(Color.parseColor("#f5f5f9"));
            img_anim.setImageResource(R.mipmap.img_meiwenjian);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        Intent intent = getIntent();
        listAllFile = (List<Object>) intent.getSerializableExtra("pic");
        sendpic = intent.getBooleanExtra("send", false);
        if (sendpic) {
            chatType = intent.getIntExtra("chatType", 0);
            tid = intent.getStringExtra("tid");
        }
        init();
        getImage();
    }

    private void init() {
        mContext = this;
        isCanSelect = true;
        gridView = (GridView) findViewById(R.id.album_pic_gridview);
        tv_back = (TextView) findViewById(R.id.tv_pic_back);
        tv_sure = (TextView) findViewById(R.id.tv_pic_release);
        img_anim = (ImageView) findViewById(R.id.img_anim);
        anim = (AnimationDrawable) img_anim.getBackground();
        anim.start();

        tv_back.setOnClickListener(this);
        tv_sure.setOnClickListener(this);
        mPicList = new ArrayList<>();
        listPic = new ArrayList<>();
        adapter = new GridAlbumAdapter(this, mPicList);
        gridView.setAdapter(adapter);
        adapter.setImgCheckClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = (String) view.getTag();
                for (int i = 0; i < mPicList.size(); i++) {
                    ImageState state = mPicList.get(i);
                    if (state.path.equals(path)) {
                        boolean flag = !state.check;
                        if (flag && !isCanSelect) {
                            Toast.makeText(mContext, "最多选择9个图片", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        state.check = flag;
                        mPicList.set(i, state);
                        adapter.notifyDataSetChanged();
                        if (flag) {
                            listAllFile.add(path);
                        } else {
                            for (int j = 0; j < listAllFile.size(); j++) {
                                Object object = listAllFile.get(j);
                                if (object instanceof String) {
                                    String ret = (String) object;
                                    if (ret.equals(path)) {
                                        listAllFile.remove(j);
                                    }
                                }
                            }
                        }
                        break;
                    }
                }
                setTitleText();
            }
        });
        gridView.setOnItemClickListener(this);
    }

    private void setTitleText() {
        int count = 0;
        for (int i = 0; i < mPicList.size(); i++) {
            ImageState state = mPicList.get(i);
            if (state.check) {
                count++;
            }
        }
        if (listAllFile.size() >= 9) {
            isCanSelect = false;
        } else {
            isCanSelect = true;
        }
        if (listAllFile.size() > 0) {
            tv_sure.setText("确定(" + listAllFile.size() + ")");
            tv_sure.setClickable(true);
            tv_sure.setTextColor(0xff65caff);
        } else {
            tv_sure.setText("确定");
            tv_sure.setClickable(false);
            tv_sure.setTextColor(0x7765caff);
        }
    }

    public void getImage() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = AlbumActivity.this.getContentResolver();
                Cursor mCursor = mContentResolver.query(mImageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png"},
                        MediaStore.Images.Media.DATE_MODIFIED);
                mPicList.clear();
                while (mCursor.moveToNext()) {
                    // 获取图片的路径
                    String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    ImageState state = new ImageState();
                    state.path = path;
                    state.check = false;
                    mPicList.add(state);
                    listPic.add(path);
                }
                mCursor.close();
                mHandler.sendEmptyMessage(0x110);
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_pic_back:
                this.finish();
                break;
            case R.id.tv_pic_release:
                Intent intent = new Intent();
                intent.putExtra("type", StudyConstance.FILE_PHONE_ALBUM);
                intent.putExtra("list", (Serializable) listAllFile);
                setResult(RESULT_OK, intent);
                this.finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        int len = mPicList.size();
        cur_click = len - i - 1;
        ImageState state = mPicList.get(cur_click);
        Intent intent = null;
        if (!sendpic) {
            intent = new Intent(mContext, OpenPhotoActivity.class);
            intent.putExtra("path", state.path);
            intent.putExtra("check", state.check);
            intent.putExtra("count", listAllFile.size());
            intent.putExtra("send", sendpic);
        } else {
            intent = new Intent(mContext, OpenAlbumPicActivity.class);
            intent.putExtra("all", (Serializable) mPicList);
            intent.putExtra("index", i);
            intent.putExtra("list", (Serializable) listAllFile);
            intent.putExtra("chatType", chatType);
            intent.putExtra("tid", tid);
        }
        startActivityForResult(intent, RESULT_CHECK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RESULT_CAMERA) {
                String sdStatus = Environment.getExternalStorageState();
                if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
                    Log.i("info", "SD card is not avaiable writeable right now.");
                    return;
                }
                FileOutputStream b = null;
                String path = Environment.getExternalStorageDirectory() + "/" + Constant.APP_DIR_NAME;
                File file = new File(path);
                if (!file.exists()) {
                    file.mkdirs();
                }

                String filename = path + "/" + camera_photo_name;
                try {
                    Bitmap bitmap = compressImageFromFile(filename);
                    b = new FileOutputStream(filename);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 60, b);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    return;
                } catch (OutOfMemoryError e) {
                    return;
                }
//                Log.i("info", "保存，图片路径=" + filename);
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(filename))));

                ImageState state = new ImageState();
                state.check = false;
                state.path = filename;
                mPicList.add(state);
                adapter.notifyDataSetChanged();

//                Intent intent = new Intent();
//                intent.putExtra(StudyConstance.FILE_TYPE, StudyConstance.FILE_CAMERA);
//                List<String> list = new ArrayList<>();
//                list.add(filename);
//                intent.putStringArrayListExtra("list", (ArrayList<String>) list);
//                setResult(RESULT_OK, intent);
                try {
                    b.flush();
                    b.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                this.finish();
            } else if (requestCode == RESULT_CHECK) {
                Bundle bundle = data.getExtras();
                boolean check = bundle.getBoolean("check");
                ImageState state = mPicList.get(cur_click);
                state.check = check;
                mPicList.set(cur_click, state);
                adapter.notifyDataSetChanged();
                String path = state.path;
                if (check) {
                    for (int i = 0; i < listAllFile.size(); i++) {
                        Object o = listAllFile.get(i);
                        if (o instanceof String) {
                            String url = (String) o;
                            if (path.equals(url)) {
                                listAllFile.remove(i);
                                listAllFile.add(path);
                                break;
                            }
                        }
                        if (i == listAllFile.size() - 1) {
                            listAllFile.add(path);
                        }
                    }
                    if (listAllFile.size() == 0) {
                        listAllFile.add(path);
                    }
                } else {
                    for (int i = 0; i < listAllFile.size(); i++) {
                        Object o = listAllFile.get(i);
                        if (o instanceof String) {
                            String url = (String) o;
                            if (path.equals(url)) {
                                listAllFile.remove(i);
                                break;
                            }
                        }
                    }
                }
                setTitleText();
            }
        } else if (resultCode == 1) {
            if (requestCode == RESULT_CHECK) {
                this.finish();
            }
        }
    }

    private Bitmap compressImageFromFile(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, null);

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        float hh = width;
        float ww = height;
        int be = 1;
        if (w > h && w > ww) {
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;

        newOpts.inPurgeable = true;
        newOpts.inInputShareable = true;

        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return bitmap;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("AlbumActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("AlbumActivity");
        MobclickAgent.onPause(this);
    }
}
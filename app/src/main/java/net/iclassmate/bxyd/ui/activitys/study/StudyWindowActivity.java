package net.iclassmate.bxyd.ui.activitys.study;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.adapter.study.WindowAdapter;
import net.iclassmate.bxyd.bean.study.FileDirList;
import net.iclassmate.bxyd.bean.study.StudyMessageItem;
import net.iclassmate.bxyd.bean.study.comment.CommentMessageItem;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.constant.StudyConstance;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class StudyWindowActivity extends FragmentActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private ListView listView;
    private WindowAdapter windowAdapter;
    private List<String> list;
    private TextView tv_cancel;
    public static final int RESULT_CAMERA = 1;
    public static final int RESULT_PIC = 2;
    public static final int RESULT_NET_FILE = 3;
    public static final int RESULT_LOADDATA = 4;
    public static final int RESULT_REPLY = 5;
    private String camera_photo_name;
    private Context mContext;
    private View view_close;
    private LinearLayout linearLayout;
    private StudyMessageItem message;
    private OkHttpClient client;
    //发布
    private List<Object> listSelectAll;
    private int pic_count;
    private int file_count;
    private CommentMessageItem commentMessageItem;

    //收藏
    private String saveId;

    //回复评论
    private String rid;

    private String spaceid, userid;
    private SharedPreferences sharedPreferences;
    private long last_click_time;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            if (what == 1) {
                try {
                    int code = (int) msg.obj;
                    if (code == 200) {
                        Toast.makeText(mContext, "评论已删除！", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        close();
                    } else {
                        Toast.makeText(mContext, "评论删除失败！", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(mContext, "评论删除失败！", Toast.LENGTH_SHORT).show();
                }
            } else if (what == 2) {
                try {
                    int code = (int) msg.obj;
//                    Log.i("info", "返回数据=" + code);
                    if (code == 200) {
                        Toast.makeText(mContext, "已收藏！", Toast.LENGTH_SHORT).show();
                        close();
                    } else if (code == 400) {
                        Toast.makeText(mContext, "不允许重复收藏！", Toast.LENGTH_SHORT).show();
                        close();
                    } else {
                        Toast.makeText(mContext, "收藏失败！", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(mContext, "收藏失败！", Toast.LENGTH_SHORT).show();
                }
            } else if (what == 3) {
                try {
                    int code = (int) msg.obj;
                    if (code == 200) {
                        Toast.makeText(mContext, "已取消收藏！", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.putExtra("del", true);
                        setResult(RESULT_OK, intent);
                        close();
                    } else {
                        Toast.makeText(mContext, "取消收藏失败！", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(mContext, "取消收藏失败！", Toast.LENGTH_SHORT).show();
                }
            } else if (what == 4) {
                try {
                    int code = (int) msg.obj;
                    if (code == 200) {
                        Toast.makeText(mContext, "已删除动态！", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.putExtra("del", true);
                        setResult(RESULT_OK, intent);
                        close();
                    } else {
                        Toast.makeText(mContext, "删除动态失败！", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(mContext, "删除动态失败！", Toast.LENGTH_SHORT).show();
                }
            } else if (what == 5) {
                try {
                    int code = (int) msg.obj;
                    if (code == 200) {
                        Toast.makeText(mContext, "已保存到网盘！", Toast.LENGTH_SHORT).show();
                        close();
                    } else {
                        Toast.makeText(mContext, "保存网盘失败！", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(mContext, "保存网盘失败！", Toast.LENGTH_SHORT).show();
                }
            } else if (what == 400) {
                Toast.makeText(mContext, "保存网盘失败！", Toast.LENGTH_SHORT).show();
            } else if (what == 401) {
                Toast.makeText(mContext, "删除动态失败！", Toast.LENGTH_SHORT).show();
            } else if (what == 402) {
                Toast.makeText(mContext, "取消收藏失败！", Toast.LENGTH_SHORT).show();
            } else if (what == 403) {
                Toast.makeText(mContext, "收藏失败！", Toast.LENGTH_SHORT).show();
            } else if (what == 404) {
                Toast.makeText(mContext, "评论删除失败！", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_window);

        Intent intent = getIntent();
        //对话框内容
        list = intent.getStringArrayListExtra("list");
        if (list == null || list.size() < 1) {
            return;
        }
        //转发，评论
        message = (StudyMessageItem) intent.getSerializableExtra("msg");

        //发布
        pic_count = intent.getIntExtra(StudyConstance.PIC_COUNT, 0);
        file_count = intent.getIntExtra(StudyConstance.FILE_COUNT, 0);
        listSelectAll = (List<Object>) intent.getSerializableExtra(StudyConstance.ALL_FILE);

        //回复和删除
        commentMessageItem = (CommentMessageItem) intent.getSerializableExtra("del");

        //收藏
        saveId = intent.getStringExtra("saveId");
        mContext = StudyWindowActivity.this;

        //回复评论
        rid = intent.getStringExtra("rid");

        sharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        userid = sharedPreferences.getString(Constant.ID_USER, "");
        spaceid = sharedPreferences.getString(Constant.ID_SPACE, "");
        init();
    }

    private void init() {
        this.getWindow().setGravity(Gravity.BOTTOM);
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); //设置宽度
        this.getWindow().setAttributes(lp);

        listView = (ListView) findViewById(R.id.study_window_listview);
        windowAdapter = new WindowAdapter(this, list);
        listView.setAdapter(windowAdapter);

        tv_cancel = (TextView) findViewById(R.id.study_window_tv_cancel);
        tv_cancel.setOnClickListener(this);
        listView.setOnItemClickListener(this);
        view_close = findViewById(R.id.view_close);
        view_close.setOnClickListener(this);
        overridePendingTransition(R.anim.window_in_anim, 0);

        linearLayout = (LinearLayout) findViewById(R.id.window_main_linear);
        linearLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.study_window_tv_cancel:
            case R.id.view_close:
                close();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (System.currentTimeMillis() - last_click_time < 3000) {
            return;
        }
        last_click_time = System.currentTimeMillis();

        String ret = list.get(i);
        Intent intent = null;
        switch (ret) {
            case "网盘文件":
                openNetFile();
                break;
            case "拍照":
                takePhoto();
                break;
            case "手机相册":
                openPic();
                break;
            case "转发到主页":
                intent = new Intent(mContext, TraStudyActivity.class);
                intent.putExtra("msg", message);
                startActivityForResult(intent, RESULT_LOADDATA);
                break;
            case "转发给好友":
                intent = new Intent(mContext, TraFriActivity.class);
                intent.putExtra("msg", message);
                startActivity(intent);
                close();
                break;
            case "保存到网盘":
                intent = getIntent();
                String saveid = intent.getStringExtra("saveid");
                upFile(saveid, spaceid);
                break;
            case "收藏":
                addSave(spaceid);
                break;
            case "取消收藏":
                cancelSave();
                break;
            case "举报":
                report();
                break;
            case "是否确认删除文件删除":
                intent = new Intent();
                setResult(RESULT_OK, intent);
                close();
                break;
            case "删除":
                delComment(userid);
                break;
            case "回复":
                intent = new Intent(mContext, ReplyActivity.class);
                String name = commentMessageItem.getCreateBy().getName();
                if (name.contains("回复")) {
                    int index = name.indexOf("回复");
                    name = name.substring(0, index);
                }
                intent.putExtra("reply", "回复" + name);
                Log.i("info", "名字=" + commentMessageItem.getCreateBy().getName());
                intent.putExtra("msg", message);
                if (rid != null && !rid.equals("")) {
                    intent.putExtra("rid", rid);
                }
                startActivityForResult(intent, RESULT_REPLY);
                break;
            case "删除动态":
                delRelease(userid);
                break;
            default:
                close();
                break;
        }
    }

    //保存到网盘
    private void upFile(final String saveId, final String spaceid) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                String json = "";
                JSONObject jsonObject = new JSONObject();
                JSONArray array = new JSONArray();
                try {
                    jsonObject.put("path", "/" + getResources().getString(R.string.my_cache));
                    jsonObject.put("spaceId", spaceid);
                    array.put(saveId);
                    jsonObject.put("resources", array.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                json = jsonObject.toString();
                if (json.contains("\\")) {
                    json = json.replace("\\", "");
                }
                if (json.contains("\"[")) {
                    json = json.replace("\"[", "[");
                }
                if (json.contains("]\"")) {
                    json = json.replace("]\"", "]");
                }
                //Log.i("info", "保存到网盘参数=" + json);
                RequestBody body = RequestBody.create(JSON, json);
                OkHttpClient.Builder builder = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS);
                client = builder.build();
                Request request = new Request.Builder()
                        .url(Constant.STUDY_SAVE_NET)
                        .post(body)
                        .build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    Message message = new Message();
                    message.what = 5;
                    message.obj = response.code();
                    mHandler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(400);
                }
            }
        }
        ).start();
    }

    //删除动态
    private void delRelease(final String userid) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                String json = "";
                JSONObject jsonObject = new JSONObject();
                JSONArray array = new JSONArray();
                try {
                    array.put(message.getId());
                    jsonObject.put("list", array.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                json = jsonObject.toString();
                if (json.contains("\\")) {
                    json = json.replace("\\", "");
                }
                if (json.contains("\"[")) {
                    json = json.replace("\"[", "[");
                }
                if (json.contains("]\"")) {
                    json = json.replace("]\"", "]");
                }
                //Log.i("info", "取消发布参数=" + json);
                RequestBody body = RequestBody.create(JSON, json);
                OkHttpClient.Builder builder = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS);
                client = builder.build();
                Request request = new Request.Builder()
                        .url(String.format(Constant.STUDY_CANCEL_RELEASE, userid))
                        .post(body)
                        .build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    Message message = new Message();
                    message.what = 4;
                    message.obj = response.code();
                    mHandler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(401);
                }
            }
        }
        ).start();
    }

    //取消收藏
    private void cancelSave() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                cancel();
            }
        }).start();
    }

    //取消收藏
    private void cancel() {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String json = "";
        JSONObject jsonObject = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            array.put(saveId);
            jsonObject.put("list", array.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        json = jsonObject.toString();
        if (json.contains("\\")) {
            json = json.replace("\\", "");
        }
        if (json.contains("\"[")) {
            json = json.replace("\"[", "[");
        }
        if (json.contains("]\"")) {
            json = json.replace("]\"", "]");
        }
        //Log.i("info", "取消收藏参数=" + json);
        RequestBody body = RequestBody.create(JSON, json);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS);
        client = builder.build();
        Request request = new Request.Builder()
                .url(Constant.STUDY_CANCEL_SAVE)
                .post(body)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            Message message = new Message();
            message.what = 3;
            message.obj = response.code();
            mHandler.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(402);
        }
    }

    private void addSave(final String spaceId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                save(spaceId);
            }
        }).start();
    }

    private void save(String spaceId) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String json = "";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("spaceId", spaceId);
            jsonObject.put("bulletinId", message.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        json = jsonObject.toString();
        //Log.i("info", "添加收藏参数=" + json);
        RequestBody body = RequestBody.create(JSON, json);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS);
        client = builder.build();
        Request request = new Request.Builder()
                .url(Constant.STUDY_ADD_SAVE)
                .post(body)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            Message message = new Message();
            message.what = 2;
            message.obj = response.code();
            mHandler.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(403);
        }
    }

    //删除评论
    private void delComment(final String userId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                del(userId);
            }
        }).start();
    }

    //删除评论
    private void del(String userId) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String json = "";
        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray array = new JSONArray();
            String id = commentMessageItem.getId();
            array.put(id);
            jsonObject.put("list", array.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        json = jsonObject.toString();
        if (json.contains("\\")) {
            json = json.replace("\\", "");
        }
        if (json.contains("\"[")) {
            json = json.replace("\"[", "[");
        }
        if (json.contains("]\"")) {
            json = json.replace("]\"", "]");
        }
//        Log.i("info", "删除评论参数=" + json);
        RequestBody body = RequestBody.create(JSON, json);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS);
        client = builder.build();
        Request request = new Request.Builder()
                .url(String.format(Constant.STUDY_DEL_COMMENT, userId))
                .post(body)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                Message message = new Message();
                message.what = 1;
                message.obj = response.code();
                mHandler.sendMessage(message);
            } else {
                mHandler.sendEmptyMessage(404);
            }
        } catch (IOException e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(404);
        }
    }

    private void close() {
        this.finish();
        overridePendingTransition(0, R.anim.window_out_anim);
    }

    private void openNetFile() {
        Intent intent = new Intent(StudyWindowActivity.this, NetFileActivity.class);
        intent.putExtra("file", (Serializable) listSelectAll);
        startActivityForResult(intent, RESULT_NET_FILE);
    }

    private void report() {
        Intent intent = new Intent(StudyWindowActivity.this, ReportActivity.class);
        intent.putExtra("msg", message);
        startActivity(intent);
        close();
    }

    private void openPic() {
        Intent intent = new Intent(StudyWindowActivity.this, AlbumActivity.class);
        intent.putExtra("pic", (Serializable) listSelectAll);
        startActivityForResult(intent, RESULT_PIC);
        linearLayout.setVisibility(View.GONE);
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String path = Environment.getExternalStorageDirectory() + "/" + Constant.APP_DIR_NAME;
        File f = new File(path);
        if (!f.exists()) {
            f.mkdirs();
        }
        camera_photo_name = System.currentTimeMillis() + ".jpg";
        File file = new File(path, camera_photo_name);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, RESULT_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            //照相
            if (requestCode == RESULT_CAMERA) {
                linearLayout.setVisibility(View.GONE);
                boolean flag = true;
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
                Bitmap bitmap = getBitmap(filename);
                try {
                    b = new FileOutputStream(filename);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, b);
                    //Log.i("info", "保存，图片路径=" + filename);
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(filename))));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    flag = false;
                } catch (Exception e) {
                    flag = false;
                }
                Intent intent = new Intent();
                intent.putExtra(StudyConstance.FILE_TYPE, StudyConstance.FILE_CAMERA);
                List<String> list = new ArrayList<>();
                if (flag) {
                    list.add(filename);
                } else {
                    Toast.makeText(mContext, "获取拍照图片失败！", Toast.LENGTH_SHORT).show();
                }
                intent.putStringArrayListExtra("list", (ArrayList<String>) list);
                setResult(RESULT_OK, intent);
                close();
                try {
                    b.flush();
                    b.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }//手机相册
            } else if (requestCode == RESULT_PIC) {
                Bundle bundle = data.getExtras();
                Intent intent = new Intent();
                intent.putExtra(StudyConstance.FILE_TYPE, StudyConstance.FILE_PHONE_ALBUM);
                listSelectAll = (List<Object>) bundle.getSerializable("list");
                intent.putExtra("list", (Serializable) listSelectAll);
                setResult(RESULT_OK, intent);
                close();
            } else if (requestCode == RESULT_NET_FILE) {
                Bundle bundle = data.getExtras();
                Intent intent = new Intent();
                intent.putExtra(StudyConstance.FILE_TYPE, StudyConstance.FILE_NET_FILE);
                List<FileDirList> list = (List<FileDirList>) bundle.getSerializable("list");
                intent.putExtra("list", (Serializable) list);
                setResult(RESULT_OK, intent);
                close();
            } else if (requestCode == RESULT_LOADDATA) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                close();
            } else if (requestCode == RESULT_REPLY) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                close();
            }
        }
        close();
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

    private Bitmap getBitmap(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;//只读边,不读内容
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = 800f;//
        float ww = 480f;//
        int be = 1;
        if (w > h && w > ww) {
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置采样率

        newOpts.inPreferredConfig = Bitmap.Config.ARGB_8888;//该模式是默认的,可不设
        newOpts.inPurgeable = true;// 同时设置才会有效
        newOpts.inInputShareable = true;//。当系统内存不够时候图片自动被回收

        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
//      return compressBmpFromBmp(bitmap);//原来的方法调用了这个方法企图进行二次压缩
        //其实是无效的,大家尽管尝试
        return bitmap;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("StudyWindowActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("StudyWindowActivity");
        MobclickAgent.onPause(this);
    }
}

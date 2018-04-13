package net.iclassmate.bxyd.ui.activitys.study;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.adapter.study.GridPicAdapter;
import net.iclassmate.bxyd.bean.study.FileDirList;
import net.iclassmate.bxyd.bean.study.StudyMessageItem;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.constant.StudyConstance;
import net.iclassmate.bxyd.utils.NetWorkUtils;
import net.iclassmate.bxyd.utils.OpenFile;
import net.iclassmate.bxyd.utils.UIUtils;
import net.iclassmate.bxyd.utils.emotion.SpanStringUtils;
import net.iclassmate.bxyd.view.FullSelectPicGridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReleaseActivity extends FragmentActivity implements AdapterView.OnItemClickListener, View.OnClickListener, TextWatcher {
    private List<String> listPic;
    private List<FileDirList> fileDirLists;
    private List<Object> listSelectAll;
    private GridPicAdapter adapter;
    private FullSelectPicGridView gridView;
    private Context mContext;
    private TextView tv_back, tv_release, tv_anim;
    public static final int REQ_EXIT = 1;
    public static final int RET_EXIT = 2;
    public static final int RET_DIALOG = 3;
    public static final int RET_DEL = 4;
    public static final int RET_DEL_FILE = 5;
    private long last_click_time_add, last_click_time_relase;
    private EditText editText;
    private OkHttpClient client;
    private List<String> ids;
    private List<String> pic_ids;
    private int pic_count;
    private boolean isrelease;
    private String spaceid, userid;
    private SharedPreferences sharedPreferences;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            //发布
            if (what == 1) {
                tv_anim.setVisibility(View.GONE);
                try {
                    int code = msg.arg1;
                    String ret = (String) msg.obj;
                    if (code == 200) {
                        Toast.makeText(ReleaseActivity.this, "已发布！", Toast.LENGTH_SHORT).show();
                        JSONObject jsonObject = new JSONObject(ret);
                        StudyMessageItem item = new StudyMessageItem();
                        if (jsonObject != null) {
                            item.parserJson(jsonObject);
                        }
                        Intent intent = new Intent();
                        intent.putExtra("item", item);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        isrelease = false;
                        Toast.makeText(ReleaseActivity.this, "发布失败！", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    isrelease = false;
                    Toast.makeText(ReleaseActivity.this, "发布失败！", Toast.LENGTH_SHORT).show();
                }
            } else if (what == 404) {
                isrelease = false;
                Toast.makeText(ReleaseActivity.this, "发布失败！", Toast.LENGTH_SHORT).show();
                //查找文件夹
            } else if (what == 2) {
                String ret = (String) msg.obj;
                findFile(ret);
                //创建文件夹
            } else if (what == 3) {
                int code = msg.arg1;
                if (code == 200) {
                    //Toast.makeText(ReleaseActivity.this, "文件夹创建成功！", Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(ReleaseActivity.this, "文件夹创建失败！", Toast.LENGTH_SHORT).show();
                }//保存图片
            } else if (what == 4) {
                int code = msg.arg1;
                String ret = (String) msg.obj;
                if (code == 200) {
                    pic_ids.add(ret);
                    pic_count--;
                    //Toast.makeText(ReleaseActivity.this, "保存图片成功！", Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(ReleaseActivity.this, "保存图片失败！", Toast.LENGTH_SHORT).show();
                }
                if (pic_count <= 0) {
                    int index = 0;
                    for (int i = 0; i < listSelectAll.size(); i++) {
                        Object object = listSelectAll.get(i);
                        if (object instanceof String) {
                            if (index < pic_ids.size()) {
                                ids.add(pic_ids.get(index));
                                index++;
                            }
                        } else if (object instanceof FileDirList) {
                            FileDirList file = (FileDirList) object;
                            String id = file.getId();
                            ids.add(id);
                        }
                    }
                    release(userid, spaceid);
                    isrelease = true;
                }
            } else if (what == 5) {
                int code = msg.arg1;
                if (code == 200) {
                    String ret = (String) msg.obj;
                    //Log.i("info", "获取spaceid=" + ret);
                    try {
                        JSONObject object = new JSONObject(ret);
                        spaceid = object.getString("uuid");
                        sharedPreferences.edit().putString(Constant.ID_SPACE, spaceid).commit();
                        release(userid, spaceid);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (!isrelease) {
                tv_anim.setVisibility(View.GONE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release);

        sharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        userid = sharedPreferences.getString(Constant.ID_USER, "");
        Intent intent = getIntent();
        if (intent != null) {
            spaceid = intent.getStringExtra("sid");
            if (spaceid == null || spaceid.equals("")) {
                spaceid = sharedPreferences.getString(Constant.ID_SPACE, "");
            }
        }
        init();
    }

    private void upALlFile() {
        for (int i = 0; i < listSelectAll.size(); i++) {
            Object object = listSelectAll.get(i);
            if (object instanceof String) {
                String ret = (String) object;
                upFile(ret);
            }
        }
    }

    private void findFile(String ret) {
        boolean isFind = false;
        try {
            JSONObject json = new JSONObject(ret);
            JSONArray array = json.getJSONArray("fileDirList");
            for (int i = 0; i < array.length(); i++) {
                FileDirList fileDirList = new FileDirList();
                JSONObject jsonObject = array.optJSONObject(i);
                if (jsonObject != null) {
                    fileDirList.parserJson(jsonObject);
                    if (fileDirList.getType() == 1) {
                        isFind = true;
                        break;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            isFind = false;
        }

        if (!isFind) {
            createFile();
        }
    }

    private void createFile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                String json = "";
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("fullPath", "");
                    jsonObject.put("shortName", getResources().getString(R.string.my_cache));
                    jsonObject.put("userId", userid);
                    jsonObject.put("spaceId", spaceid);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                json = jsonObject.toString();
//                Log.i("info", "创建文件夹参数=" + json);
                RequestBody body = RequestBody.create(JSON, json);
                OkHttpClient.Builder builder = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS);
                client = builder.build();
                Request request = new Request.Builder()
                        .url(Constant.STUDY_CREATE_FILE)
                        .post(body)
                        .build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    Message message = new Message();
                    message.what = 3;
                    message.arg1 = response.code();
                    message.obj = response.body().string();
                    mHandler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void upFile(final String filename) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient.Builder builder = new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS);
                client = builder.build();
                String name = filename;
                String temp[] = name.replaceAll("\\\\", "/").split("/");
                if (temp.length > 1) {
                    name = temp[temp.length - 1];
                }
                File file = new File(filename);
                String url = String.format(Constant.STUDY_UP_FILE, userid, spaceid, "/" + getResources().getString(R.string.my_cache));
                RequestBody formBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file", name, RequestBody.create(MediaType.parse("image/png"), file))
                        .addFormDataPart("other_field", "other_field_value")
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    Message message = new Message();
                    message.what = 4;
                    message.arg1 = response.code();
                    message.obj = response.body().string();
                    mHandler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    private void init() {
        mContext = this;
        gridView = (FullSelectPicGridView) findViewById(R.id.study_sapce_release_gridview);
        listPic = new ArrayList<>();
        fileDirLists = new ArrayList<>();
        listSelectAll = new ArrayList<>();
        adapter = new GridPicAdapter(mContext, listSelectAll);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);

        tv_back = (TextView) findViewById(R.id.tv_release_back);
        tv_back.setOnClickListener(this);
        tv_release = (TextView) findViewById(R.id.tv_release_release);
        tv_release.setOnClickListener(this);
        editText = (EditText) findViewById(R.id.et_relase);
        editText.addTextChangedListener(this);
        tv_anim = (TextView) findViewById(R.id.tv_anim);
        tv_anim.setOnClickListener(this);

        ids = new ArrayList<>();
        pic_ids = new ArrayList<>();
        listPic = new ArrayList<>();
        isrelease = false;
        receiveData();

        //查找和创建文件夹
        findAndcreateFile();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (i == listSelectAll.size()) {
            if (System.currentTimeMillis() - last_click_time_add < 3000) {
                return;
            }
            last_click_time_add = System.currentTimeMillis();

            Intent intent = new Intent(ReleaseActivity.this, StudyWindowActivity.class);
            List<String> list = new ArrayList<>();
            list.add("网盘文件");
            list.add("拍照");
            list.add("手机相册");
            intent.putStringArrayListExtra("list", (ArrayList<String>) list);
            intent.putExtra(StudyConstance.PIC_COUNT, listPic.size());
            intent.putExtra(StudyConstance.FILE_COUNT, fileDirLists.size());
            intent.putExtra(StudyConstance.ALL_FILE, (Serializable) listSelectAll);
            startActivityForResult(intent, RET_DIALOG);
        } else {
            Object object = listSelectAll.get(i);
            openPic(i, object);
        }
    }

    private void openPic(int index, Object obj) {
        boolean ispic = false;
        if (obj instanceof String) {
            ispic = true;
        } else if (obj instanceof FileDirList) {
            FileDirList file = (FileDirList) obj;
            String name = file.getShortName().toLowerCase();
            if (name.contains(".")) {
                name = name.substring(name.lastIndexOf(".") + 1, name.length());
            }
            if (name.equals("bmp") || name.equals("gif") || name.equals("jpg") ||
                    name.equals("pic") || name.equals("png") || name.equals("tif")) {
                ispic = true;
            }
        }
        if (ispic) {
            Intent intent = new Intent(mContext, LookPicActivity.class);
            intent.putExtra("type", 2);
            intent.putExtra("index", index);
            intent.putExtra("list", (Serializable) listSelectAll);
            startActivityForResult(intent, RET_DEL);
        } else {
            if (obj instanceof FileDirList) {
                FileDirList file = (FileDirList) obj;
                openFile(file);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_release_back:
                toActivity();
                break;
            case R.id.tv_release_release:
                if (!NetWorkUtils.isNetworkAvailable(UIUtils.getContext())) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    tv_anim.setVisibility(View.VISIBLE);
                    tv_anim.setText(getResources().getString(R.string.alert_msg_check_net));
                    return;
                }
                if (editText.getText().toString().trim().equals("") && listSelectAll.size() < 1) {
                    return;
                }
                if (isrelease || System.currentTimeMillis() - last_click_time_relase < 3000) {
                    return;
                }
                last_click_time_relase = System.currentTimeMillis();
                pic_count = 0;
                for (int i = 0; i < listSelectAll.size(); i++) {
                    Object object = listSelectAll.get(i);
                    if (object instanceof String) {
                        pic_count++;
                    } else if (object instanceof FileDirList) {
                        FileDirList file = (FileDirList) object;
                        String id = file.getId();
                        ids.add(id);
                    }
                }
                if (pic_count > 0) {
                    upALlFile();
                    ids.clear();
                    //Log.i("info", "上传照片到服务器");
                } else if (pic_count == 0) {
                    if (spaceid == null || spaceid.equals("")) {
                        getSpaceid();
                    } else {
                        release(userid, spaceid);
                    }
                }
                isrelease = true;

                tv_anim.setVisibility(View.VISIBLE);
                //隐藏键盘
                InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                tv_anim.setText("正在努力发布中...");
                break;
            case R.id.tv_anim:
                tv_anim.setVisibility(View.GONE);
                break;
        }
    }

    //查找和创建文件夹
    private void findAndcreateFile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = String.format(Constant.STUDY_FIND_FILE, spaceid, getResources().getString(R.string.my_cache));
//                Log.i("info", "查看文件是否存在路径=" + url);
                final Request request = new Request.Builder()
                        .url(url)
                        .build();

                //client = new OkHttpClient();
                int cacheSize = 10 * 1024 * 1024;
                OkHttpClient.Builder builder = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS);
                client = builder.build();
                client.newCall(request).enqueue(new Callback() {
                                                    @Override
                                                    public void onFailure(Call call, IOException e) {
//                                                        Log.i("info", "请求数据失败" + e.getMessage());
                                                        // mHandler.sendEmptyMessage(404);
                                                    }

                                                    @Override
                                                    public void onResponse(Call call, final Response response) throws IOException {
                                                        if (response.isSuccessful()) {
//                                                            Log.i("info", "请求数据,返回成功！");
                                                            Message message = new Message();
                                                            message.what = 2;
                                                            message.obj = response.body().string();
                                                            mHandler.sendMessage(message);
//                                                            Log.i("info", "查看文件夹=" + response.code());
                                                        } else {
                                                            //mHandler.sendEmptyMessage(404);
                                                        }
                                                        response.close();
                                                    }

                                                }

                );
            }
        }).start();
    }

    private void release(final String userId, final String spaceId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                execute(userId, spaceId);
            }
        }).start();
    }

    private void execute(String userId, String spaceId) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String json = "";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("commentable", true);
            jsonObject.put("downloadable", true);
            jsonObject.put("forwardable", true);
            String context = editText.getText().toString();
            context = SpanStringUtils.convertToMsg(context, mContext);
            jsonObject.put("content", context);
            jsonObject.put("sectionId", "sectionId");
            jsonObject.put("spaceId", spaceId);
            JSONArray array = new JSONArray();
            for (int i = ids.size() - 1; i >= 0; i--) {
                String id = ids.get(i);
                array.put(id);
            }
            jsonObject.put("resources", array.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(404);
            return;
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
        Log.i("info", "发布动态参数=" + json);
        RequestBody body = RequestBody.create(JSON, json);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS);
        client = builder.build();
        Request request = new Request.Builder()
                .url(String.format(Constant.STUDY_RELEASE, userId))
                .post(body)
                .build();
        Response response = null;
//        Log.i("info", "发布动态=" + String.format(Constant.STUDY_RELEASE, userId));
        try {
            response = client.newCall(request).execute();
            Message message = new Message();
            message.what = 1;
            message.arg1 = response.code();
            message.obj = response.body().string();
            mHandler.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(404);
        } catch (Exception e) {
            mHandler.sendEmptyMessage(404);
        }
    }

    @Override
    public void onBackPressed() {
        toActivity();
    }

    private void toActivity() {
        Intent intent = new Intent(ReleaseActivity.this, ExitActivity.class);
        startActivityForResult(intent, REQ_EXIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_EXIT) {
                this.finish();
            } else if (requestCode == RET_DIALOG) {
                Bundle bundle = data.getExtras();
                int type = bundle.getInt(StudyConstance.FILE_TYPE, -1);
                if (type == -1) {
                    return;
                } else if (type == StudyConstance.FILE_CAMERA) {
                    List<String> list = bundle.getStringArrayList("list");
                    listSelectAll.addAll(list);
                } else if (type == StudyConstance.FILE_PHONE_ALBUM) {
                    listSelectAll.clear();
                    List<Object> list = (List<Object>) bundle.getSerializable("list");
                    listSelectAll.addAll(list);
                } else if (type == StudyConstance.FILE_NET_FILE) {
                    List<Object> list = (List<Object>) bundle.getSerializable("list");
                    listSelectAll.clear();
                    listSelectAll.addAll(list);
                }
            } else if (requestCode == RET_DEL) {
                Bundle bundle = data.getExtras();
                List<Object> list = (List<Object>) bundle.getSerializable("list");
                listSelectAll.clear();
                listSelectAll.addAll(list);
            } else if (requestCode == RET_DEL_FILE) {
                Bundle bundle = data.getExtras();
                String id = bundle.getString("id");
                for (int i = 0; i < listSelectAll.size(); i++) {
                    Object object = listSelectAll.get(i);
                    if (object instanceof FileDirList) {
                        FileDirList file = (FileDirList) object;
                        if (file.getId().equals(id)) {
                            listSelectAll.remove(i);
                        }
                    }
                }
            }

            if (listSelectAll.size() >= 9) {
                adapter.setCantSelect(false);
            } else {
                adapter.setCantSelect(true);
            }
            adapter.notifyDataSetChanged();
        }
    }


    //打开文件
    private void openFile(FileDirList file) {
        String id = file.getId();
        int type = file.getType();
        if (type == 1) {
            return;
        }
        String name = file.getShortName().toLowerCase();
        OpenFile.openFile(id, name, 2, mContext);
    }

    public void setAlbumPic(List<Object> albumPic) {
        for (int i = listSelectAll.size() - 1; i >= 0; i--) {
            Object object = listSelectAll.get(i);
            if (!albumPic.contains(object)) {
                listSelectAll.remove(i);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void receiveData() {
        Intent intent = getIntent();
        if (intent != null) {
            List<FileDirList> list = (List<FileDirList>) intent.getSerializableExtra("NetFile");
            if (list != null && list.size() > 0) {
                listSelectAll.addAll(list);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() > 255) {
            String ret = s.toString();
            ret = ret.substring(0, 255);
            editText.setText(ret);

            CharSequence text = editText.getText();
            if (text instanceof Spannable) {
                Spannable spanText = (Spannable) text;
                Selection.setSelection(spanText, text.length());
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public String getSpaceid() {
        if (userid != null && !userid.equals("")) {
            if (spaceid == null || spaceid.equals("") || spaceid.equals("-1")) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkHttpClient okHttpClient = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url(Constant.GETSPACEID_URL + userid)
                                .get()
                                .build();
//                        Log.i("info", "获取spaceid=" + Constant.GETSPACEID_URL + userid);
                        okHttpClient.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                //mHandler.sendEmptyMessage(404);
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                Message message = new Message();
                                message.what = 5;
                                message.arg1 = response.code();
                                message.obj = response.body().string();
                                mHandler.sendMessage(message);
                            }
                        });
                    }
                }).start();
            }
        }
        return spaceid;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("ReleaseActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("ReleaseActivity");
        MobclickAgent.onPause(this);
    }
}
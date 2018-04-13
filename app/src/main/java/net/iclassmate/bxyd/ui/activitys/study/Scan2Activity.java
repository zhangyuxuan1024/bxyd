package net.iclassmate.bxyd.ui.activitys.study;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.bean.message.UserMessage;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.ui.activitys.information.FriendInformationActivity;
import net.iclassmate.bxyd.ui.activitys.study.openfile.ScanOpenVideoActivity;
import net.iclassmate.bxyd.ui.activitys.study.openfile.TxtActivity;
import net.iclassmate.bxyd.utils.HttpManager;
import net.iclassmate.bxyd.utils.NetWorkUtils;
import net.iclassmate.bxyd.utils.VibratorUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by xydbj on 2016.11.17.
 */
public class Scan2Activity extends FragmentActivity implements View.OnClickListener {

    private CaptureFragment captureFragment;
    private ImageView iv_back;
    private TextView result;
    private ImageView img_close;
    private String userId, userType, wkName, wkUrl;
    private int schoolId;
    private boolean isLoaded;
    private String reStr;
    public static final int REQUES_PLAY = 3;
    private String result2, result_twocode;
    private Context mContext;
    private boolean isFail;
    private List<UserMessage> findUserInfos;
    private SharedPreferences sharedPreferences;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1011:
                    JSONArray array = null;
                    if (result_twocode != null) {
                        try {
                            array = new JSONArray(result_twocode);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    for (int i = 0; i < array.length() && array != null; i++) {
                        JSONObject json = array.optJSONObject(i);
                        if (json != null) {
                            UserMessage message = new UserMessage();
                            message.parserJson(json);
                            findUserInfos.add(message);
                        }
                    }
                    Intent toInfor = new Intent(Scan2Activity.this, FriendInformationActivity.class);
                    toInfor.putExtra("from", "FriendResActivity");
                    //机构 个人
                    toInfor.putExtra("id", findUserInfos.get(0).getId());
                    toInfor.putExtra("spaceId", findUserInfos.get(0).getSpaceId());
                    //群组
                    toInfor.putExtra("uuid", findUserInfos.get(0).getUserInfo().getTaggetId());

                    toInfor.putExtra("icon", findUserInfos.get(0).getUserInfo().getUserIcon());
                    if (findUserInfos.get(0).getUserInfo().getUserType().equals("0")) {
                        toInfor.putExtra("type", "org");
                    } else if (findUserInfos.get(0).getUserInfo().getUserType().equals("1")) {
                        toInfor.putExtra("type", "person");
                    }
                    toInfor.putExtra("isFriend", false);
                    toInfor.putExtra("name", findUserInfos.get(0).getUserInfo().getName());
                    toInfor.putExtra("code", findUserInfos.get(0).getUserInfo().getUserCode());
                    startActivity(toInfor);
                    findUserInfos.clear();
                    break;
                case 200:
                    String str = (String) msg.obj;
                    String url = null;
                    try {
                        JSONObject jsonObject = new JSONObject(str);
                        url = jsonObject.getString("url");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.i("info", "用bxyd从服务器请求的url:" + url);
                    if (url.contains(".mp4")) {
                        int index = url.lastIndexOf("/");
                        String fileName = url.substring(index + 1, url.length());
                        //跳到自己写的OpenVideoActivity
                        Intent intent = new Intent(Scan2Activity.this, ScanOpenVideoActivity.class);
                        intent.putExtra("path", url);
                        intent.putExtra("fileName",fileName);
                        startActivity(intent);


                        //                        利用手机自带的播放器播放视频
                        //                        Uri video = Uri.parse(url);
                        //                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        //                        Log.i("info", "Uri之后的视频路径=" + video.toString());
                        //                        intent.setDataAndType(video, "video/*");
                        //                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(Scan2Activity.this, WebViewActivity.class);
                        intent.putExtra("url", url);
                        startActivity(intent);
                    }
                    break;
                default:
                    Toast.makeText(Scan2Activity.this, "服务器繁忙，请稍后再试", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        captureFragment = new CaptureFragment();

        CodeUtils.setFragmentArgs(captureFragment, R.layout.custom_scanner_layout);
        captureFragment.setAnalyzeCallback(analyzeCallback);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_my_container, captureFragment).commit();

        initUI();
    }

    public void initUI() {
        iv_back = (ImageView) findViewById(R.id.second_back);
        iv_back.setOnClickListener(this);
        findUserInfos = new ArrayList<>();
        sharedPreferences = Scan2Activity.this.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        isLoaded = false;
        isFail = false;
    }

    public CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            result2 = result;
            Log.i("info", "扫码得到的result:" + result2);
            execute(result2);
        }

        @Override
        public void onAnalyzeFailed() {

        }
    };

    public void execute(String result) {
        soundRing(this);
        if (!NetWorkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "请检查您的网络连接！", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isAddFri = false;
        try {
            JSONObject json = new JSONObject(result2);
            final String uid = json.optString(Constant.USER_CODE);
            String utype = json.optString(Constant.ID_USERTYPE);
            Log.i("info", "id=" + uid + ",utype=" + utype);
            if (uid != null && utype != null && !uid.equals("") && !utype.equals("")) {
                int type = Integer.parseInt(utype);

                Intent intent = null;
                //用户类型: person(个人),org(机构),group(群组),不传查全部
                //添加个人好友
                if (type == Constant.TYPE_PRIVATE) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            result_twocode = new HttpManager().searchInfo(uid, "person");
                            if (result_twocode != null && !result_twocode.equals("404")) {
                                mHandler.sendEmptyMessage(1011);
                            }
                        }
                    }).start();
                    isAddFri = true;
                }   //添加机构
                else if (type == Constant.TYPE_GROUP) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            result_twocode = new HttpManager().searchInfo(uid, "org");
                            if (result_twocode != null && !result_twocode.equals("404")) {
                                mHandler.sendEmptyMessage(1011);
                            }
                        }
                    }).start();
                    isAddFri = true;
                }    //添加空间
                else if (type == Constant.TYPE_SPACE) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            result_twocode = new HttpManager().searchInfo(uid, "group");
                            if (result_twocode != null && !result_twocode.equals("404")) {
                                mHandler.sendEmptyMessage(1011);
                            }
                        }
                    }).start();
                    isAddFri = true;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {

        }

        if (!isAddFri) {
            if (!isWifiActive(this)) {
                Intent intent = new Intent(mContext, IsWifiActivity.class);
                startActivityForResult(intent, REQUES_PLAY);
                return;
            }
            toActivty();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.second_back:
                Scan2Activity.this.finish();
                break;
        }
    }

    public void getUserId() {
        userId = sharedPreferences.getString(Constant.ID_USER, "");
        if (userId == null) {
            return;
        }
    }

    private void toActivty() {
        if (result2 == null) {
            return;
        }
        if (result2.contains("bxyd/#/")) {
            MediaType type = MediaType.parse("application/json;charset=utf-8");
            OkHttpClient okHttpClient = new OkHttpClient();
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("code", result2.substring(result2.lastIndexOf("/") + 1, result2.length()));
                jsonObject.put("platform", "Android");
                jsonObject.put("userId", userId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String json = jsonObject.toString();
            //            Log.i("info", "扫码截取的字符串：" + result2.substring(result2.lastIndexOf("/") + 1, result2.length()));
            //            Log.i("info", "扫码如果含有bxyd的话，向服务器发送的json：" + json);
            RequestBody body = RequestBody.create(type, json);
            Request request = new Request.Builder().post(body).url(Constant.SYS_URL).build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.i("info", "请求失败,网络，服务器：" + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Message msg = new Message();
                    msg.obj = response.body().string();
                    //                    Log.i("info", "扫码得到的msg.obj:" + msg.obj.toString());
                    msg.what = response.code();
                    mHandler.sendMessage(msg);
                }
            });
        } else if (result2.toLowerCase().contains("http")) {
            Intent intent = new Intent(Scan2Activity.this, WebViewActivity.class);
            intent.putExtra("url", result2);
            startActivity(intent);
        } else {
            Intent intent = new Intent(Scan2Activity.this, TxtActivity.class);
            intent.putExtra("text", result2);
            startActivity(intent);
        }
    }

    private void soundRing(Context context) {
        try {
            MediaPlayer mPlayer = MediaPlayer.create(this, R.raw.scan_sucess);
            mPlayer.start();
            VibratorUtil.Vibrate(Scan2Activity.this, 500);   //震动300ms
        } catch (Exception e) {

        }
    }

    public static boolean isWifiActive(Context icontext) {
        Context context = icontext.getApplicationContext();
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info;
        if (connectivity != null) {
            info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getTypeName().equals("WIFI") && info[i].isConnected()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}

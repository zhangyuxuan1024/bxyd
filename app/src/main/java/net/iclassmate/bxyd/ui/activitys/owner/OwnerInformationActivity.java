package net.iclassmate.bxyd.ui.activitys.owner;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.bean.area.Province;
import net.iclassmate.bxyd.bean.owner.Information;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.utils.JsonUtils;
import net.iclassmate.bxyd.utils.NetWorkUtils;
import net.iclassmate.bxyd.view.CircleImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by xydbj on 2016.6.13.
 */
public class OwnerInformationActivity extends Activity implements View.OnClickListener {

    private ImageView owner_information_iv_back;
    private CircleImageView owner_information_iv_icon;
    private TextView owner_information_tv_back, owner_information_tv_area, owner_information_tv_sex, owner_information_tv_birthday, item_sex_selector_man,
            item_sex_selector_woman, item_sex_selector_cancel, owner_information_tv_name, owner_information_tv_description, owner_information_tv_userCode;
    private RelativeLayout owner_information_rl_head, owner_information_rl_name, owner_information_rl_2code, owner_information_rl_info, owner_information_rl_area,
            owner_information_rl_birthday, owner_information_rl_sex;
    private OptionsPickerView pvOptions_area;
    private TimePickerView pvTime;
    private List<Province> provinceList;
    private View vMasker;
    private PopupWindow popupWindow;
    private SharedPreferences sharedPreferences;
    private static final int REQUEST_NAME = 1;
    private static final int REQUEST_DESCRIPTION = 2;
    private String userId, userType, userNum, icon;
    private Information info;
    private byte[] iconByte, iconDatas;
    private int type, cut_type;
    private Bitmap icon_bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_information);
        Intent intent = getIntent();
        icon = intent.getStringExtra("icon");
        iconByte = intent.getByteArrayExtra("byte");
        type = intent.getIntExtra("type", 1);
        initView();
        getArea();
        ShowBirthday();

        IntentFilter filter = new IntentFilter(CutActivity.action);
        registerReceiver(receiver, filter);
    }

    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            cut_type = intent.getIntExtra("cut_type", 1);
            iconDatas = intent.getByteArrayExtra("byte");
            int name_Type = intent.getIntExtra("type", 100);
            if (name_Type != 101) {
                if (iconDatas != null || !"".equals(iconDatas) || !"null".equals(iconDatas)) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(iconDatas, 0, iconDatas.length);
                    owner_information_iv_icon.setImageBitmap(bitmap);
                }
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    //性别
    public void initPopuwindow() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_sex_selector, null);
        item_sex_selector_man = (TextView) view.findViewById(R.id.item_sex_selector_man);
        item_sex_selector_woman = (TextView) view.findViewById(R.id.item_sex_selector_woman);
        item_sex_selector_cancel = (TextView) view.findViewById(R.id.item_sex_selector_cancel);
        item_sex_selector_man.setOnClickListener(this);
        item_sex_selector_woman.setOnClickListener(this);
        item_sex_selector_cancel.setOnClickListener(this);
        item_sex_selector_man.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                owner_information_tv_sex.setText("男");
                changeGender(userId, "男");
                popupWindow.dismiss();
            }
        });
        item_sex_selector_woman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                owner_information_tv_sex.setText("女");
                changeGender(userId, "女");
                popupWindow.dismiss();
            }
        });
        item_sex_selector_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        ShowPopwindow(view);
    }

    //性别
    public void ShowPopwindow(View view) {
        popupWindow = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, 273, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        //设置popupwindow弹出之后的背景颜色
        final WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;
        getWindow().setAttributes(lp);

        popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
        popupWindow.showAtLocation(OwnerInformationActivity.this.findViewById(R.id.owner_information_rl_sex), Gravity.BOTTOM, 0, 0);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });
    }

    //生日
    public static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    //生日
    public void ShowBirthday() {
        pvTime = new TimePickerView(OwnerInformationActivity.this, TimePickerView.Type.YEAR_MONTH_DAY);
        pvTime.setTime(new Date());
        pvTime.setCyclic(false);
        pvTime.setCancelable(true);
        pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                owner_information_tv_birthday.setText(getTime(date));
                changeBirth(userId, getTime(date));
            }
        });
    }

    //地区
    public void ShowArea() {
        final ArrayList<String> proviceList = new ArrayList<>();//省
        final ArrayList<ArrayList<String>> cityList = new ArrayList<>();//市
        final ArrayList<ArrayList<ArrayList<String>>> areaList = new ArrayList<>();//县

        for (int i = 0; i < provinceList.size(); i++) {
            proviceList.add(provinceList.get(i).getProvince_name());//把所有的省添加到proviceList集合中去
            ArrayList<String> cityListi = new ArrayList<>();
            ArrayList<ArrayList<String>> areaListi = new ArrayList<ArrayList<String>>();

            for (int j = 0; j < provinceList.get(i).getCityList().size(); j++) {
                cityListi.add(provinceList.get(i).getCityList().get(j).getCity_name());
                ArrayList<String> countyListi = new ArrayList<>();

                for (int k = 0; k < provinceList.get(i).getCityList().get(j).getCountyList().size(); k++) {
                    countyListi.add(provinceList.get(i).getCityList().get(j).getCountyList().get(k).getCounty_name());
                }
                areaListi.add(countyListi);
            }
            areaList.add(areaListi);
            cityList.add(cityListi);
        }
        pvOptions_area.setTitle("请选择地区");
        pvOptions_area.setPicker(proviceList, cityList, areaList, true);
        pvOptions_area.setCyclic(false, false, false);
        pvOptions_area.setSelectOptions(0, 0, 0);
        pvOptions_area.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override//选择地区的确定按钮
            public void onOptionsSelect(int o1, int o2, int o3) {
                String area = proviceList.get(o1) + cityList.get(o1).get(o2) + areaList.get(o1).get(o2).get(o3);
                changeArea(userId, proviceList.get(o1), cityList.get(o1).get(o2), areaList.get(o1).get(o2).get(o3));
                owner_information_tv_area.setText(area);
                vMasker.setVisibility(View.GONE);
            }

            @Override//选择地区的取消按钮
            public void onOptionsCancel() {

            }
        });
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    provinceList = (List<Province>) msg.obj;
//                    Log.i("info", "解析之后的用户信息：" + provinceList.toString());
                    ShowArea();
                    break;
                case 2:
                    int result_code = (int) msg.obj;
                    Log.i("info", "修改地区信息返回的code:" + result_code);
                    break;
                case 3:
                    int result_code3 = (int) msg.obj;
                    Log.i("info", "修改生日信息返回的code:" + result_code3);
                    break;
                case 200:
                    String information = (String) msg.obj;
                    info = JsonUtils.StartInformationJson(information);
                    Log.i("info", "解析之后的用户信息：" + info.toString());
                    showInfo(info);
                    break;
                case 404:
                    Toast.makeText(OwnerInformationActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    //向界面显示真实数据
    public void showInfo(Information info) {
        if (type == 1) {
            if (iconByte != null || !"".equals(iconByte) || !"null".equals(iconByte)) {
                icon_bitmap = BitmapFactory.decodeByteArray(iconByte, 0, iconByte.length);
                owner_information_iv_icon.setImageBitmap(icon_bitmap);
            }
        } else if (type == 2) {
            if (icon != null || !"".equals(icon) || !"null".equals(icon)) {
                Picasso.with(OwnerInformationActivity.this).load(icon).placeholder(R.mipmap.ic_touxiang_mingpian).into(owner_information_iv_icon);
            }
        }
        Log.i("info", "头像的url:" + info.getUserInfo().getIcon());
        owner_information_tv_userCode.setText(info.getUserInfo().getUserCode());
        owner_information_tv_name.setText(info.getUserInfo().getName());
        owner_information_tv_description.setText(info.getUserInfo().getIntroduction());
        owner_information_tv_area.setText(info.getUserInfo().getProvince() + info.getUserInfo().getCity() + info.getUserInfo().getArea());
        owner_information_tv_birthday.setText(info.getUserInfo().getDateBirth());
        owner_information_tv_sex.setText(info.getUserInfo().getGender());
    }

    public void getArea() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String str_area = getAssets("area.json");
                provinceList = JsonUtils.StartProvinceJson(str_area);
                Message message = new Message();
                message.obj = provinceList;
                message.what = 1;
                mHandler.sendMessage(message);
            }
        }).start();
    }

    public String getAssets(String fileName) {
        String str = "";
        try {
            StringBuffer sb = new StringBuffer();
            InputStream is = getAssets().open(fileName);
            int len = -1;
            byte[] buff = new byte[1024];
            while ((len = is.read(buff)) != -1) {
                sb.append(new String(buff, 0, len, "utf-8"));
            }
            is.close();
            str += sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    public void initView() {
        vMasker = findViewById(R.id.vMasker);
        owner_information_rl_head = (RelativeLayout) findViewById(R.id.owner_information_rl_head);
        owner_information_iv_icon = (CircleImageView) findViewById(R.id.owner_information_iv_icon);
        owner_information_rl_name = (RelativeLayout) findViewById(R.id.owner_information_rl_name);
        owner_information_rl_2code = (RelativeLayout) findViewById(R.id.owner_information_rl_2code);
        owner_information_rl_info = (RelativeLayout) findViewById(R.id.owner_information_rl_info);
        owner_information_rl_area = (RelativeLayout) findViewById(R.id.owner_information_rl_area);
        owner_information_rl_birthday = (RelativeLayout) findViewById(R.id.owner_information_rl_birthday);
        owner_information_rl_sex = (RelativeLayout) findViewById(R.id.owner_information_rl_sex);
        owner_information_iv_back = (ImageView) findViewById(R.id.owner_information_iv_back);
        owner_information_tv_back = (TextView) findViewById(R.id.owner_information_tv_back);
        owner_information_tv_area = (TextView) findViewById(R.id.owner_information_tv_area);
        owner_information_tv_birthday = (TextView) findViewById(R.id.owner_information_tv_birthday);
        owner_information_tv_sex = (TextView) findViewById(R.id.owner_information_tv_sex);
        owner_information_tv_userCode = (TextView) findViewById(R.id.owner_information_tv_userCode);
        owner_information_tv_description = (TextView) findViewById(R.id.owner_information_tv_description);
        owner_information_tv_name = (TextView) findViewById(R.id.owner_information_tv_name);
        owner_information_rl_head.setOnClickListener(this);
        owner_information_rl_name.setOnClickListener(this);
        owner_information_rl_2code.setOnClickListener(this);
        owner_information_rl_info.setOnClickListener(this);
        owner_information_rl_birthday.setOnClickListener(this);
        owner_information_rl_area.setOnClickListener(this);
        owner_information_rl_sex.setOnClickListener(this);
        owner_information_iv_back.setOnClickListener(this);
        owner_information_tv_back.setOnClickListener(this);
        provinceList = new ArrayList<>();
        pvOptions_area = new OptionsPickerView(this);
        sharedPreferences = OwnerInformationActivity.this.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        getUserId();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.owner_information_rl_head:
                if (NetWorkUtils.isNetworkAvailable(OwnerInformationActivity.this)) {
                    Intent intent_head = new Intent(this, ImageIconActivity.class);
                    String userIcon = info.getUserInfo().getIcon();
                    if (!userIcon.equals("") && !userIcon.equals("null") && userIcon != null){
                        userIcon = userIcon.substring(0, userIcon.indexOf("@"));
                    }
                    if (cut_type == 1) {
                        intent_head.putExtra("bitmap", iconDatas);
                    } else if (type == 1) {
                        intent_head.putExtra("bitmap", iconByte);
                    } else if (type == 2) {
                        intent_head.putExtra("icon", userIcon);
                    }
                    Log.i("info", "用户头像的url:" + userIcon);
                    startActivity(intent_head);
                } else {
                    Toast.makeText(OwnerInformationActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.owner_information_rl_name:
                Intent intent_name = new Intent(OwnerInformationActivity.this, ModificationNameActivity.class);
                intent_name.putExtra("userName", owner_information_tv_name.getText());
                startActivityForResult(intent_name, REQUEST_NAME);
                break;
            case R.id.owner_information_rl_2code:
                Intent intent_2code = new Intent(this, TwoCodeActivity.class);
                intent_2code.putExtra("userCode", owner_information_tv_userCode.getText().toString());
                intent_2code.putExtra("userName", owner_information_tv_name.getText().toString());
                intent_2code.putExtra(Constant.USER_CODE, owner_information_tv_userCode.getText().toString());
                intent_2code.putExtra(Constant.ID_USERTYPE, userType);
                Log.i("info", "向TwoCodeActivity中传入的信息：userCode=" + owner_information_tv_userCode.getText().toString()
                        + ",userName=" + owner_information_tv_name.getText().toString() + ",USER_CODE=" + userNum + ",userType=" + userType);
                startActivity(intent_2code);
                break;
            case R.id.owner_information_rl_info:
                Intent intent_information = new Intent(OwnerInformationActivity.this, DescriptionActivity.class);
                intent_information.putExtra("description", owner_information_tv_description.getText());
                startActivityForResult(intent_information, REQUEST_DESCRIPTION);
                break;
            case R.id.owner_information_rl_area:
                pvOptions_area.show();
                break;
            case R.id.owner_information_rl_birthday:
                pvTime.show();
                break;
            case R.id.owner_information_rl_sex:
                initPopuwindow();
                break;
            case R.id.owner_information_tv_back:
                OwnerInformationActivity.this.finish();
                break;
            case R.id.owner_information_iv_back:
                OwnerInformationActivity.this.finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_NAME) {
            if (resultCode == RESULT_OK) {
                String userName = data.getStringExtra("userName--");
                owner_information_tv_name.setText(userName);
            }
        } else if (requestCode == REQUEST_DESCRIPTION) {
            if (resultCode == RESULT_OK) {
                String description = data.getStringExtra("description");
                owner_information_tv_description.setText(description);
            }
        }
    }

    public void getUserId() {
        userId = sharedPreferences.getString(Constant.ID_USER, "");
        userType = sharedPreferences.getString(Constant.ID_USERTYPE, "");
        userNum = sharedPreferences.getString(Constant.USER_CODE, "");
        if (userId == null) {
            return;
        }
        getTrueData(userId);
    }

    //修改性别
    public void changeGender(final String userId, final String gender) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                execute_gender(userId, gender);
            }
        }).start();
    }

    public void execute_gender(String userId, String gender) {
        MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
        OkHttpClient okHttpClient = new OkHttpClient();
        JSONObject object = null;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("gender", gender);

            object = new JSONObject();
            object.put("userId", userId);
            object.put("userInfo", jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String json = object.toString();

        RequestBody body = RequestBody.create(mediaType, json);
        Request request = new Request.Builder()
                .put(body)
                .url(Constant.CHANGEINFO_URL)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("info", "修改性别失败:" + e.getMessage());
                Message msg = new Message();
                msg.what = 404;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message msg = new Message();
                msg.obj = response.code();
                msg.what = 4;
                mHandler.sendMessage(msg);
            }
        });
    }

    //修改生日
    public void changeBirth(final String userId, final String birth) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                execute(userId, birth);
            }
        }).start();
    }

    public void execute(String userId, String birth) {
        MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
        OkHttpClient okHttpClient = new OkHttpClient();
        JSONObject object = null;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("dateBirth", birth);

            object = new JSONObject();
            object.put("userId", userId);
            object.put("userInfo", jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String json = object.toString();

        RequestBody body = RequestBody.create(mediaType, json);
        Request request = new Request.Builder()
                .put(body)
                .url(Constant.CHANGEINFO_URL)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("info", "修改生日失败：" + e.getMessage());
                Message msg = new Message();
                msg.what = 404;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message msg = new Message();
                msg.obj = response.code();
                msg.what = 3;
                mHandler.sendMessage(msg);
            }
        });
    }

    //修改地区
    public void changeArea(final String userId, final String province, final String city, final String area) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                execute(userId, province, city, area);
            }
        }).start();
    }

    public void execute(String userId, String province, String city, String area) {
        MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("application/json;charset=utf-8");
        OkHttpClient okHttpClient = new OkHttpClient();
        JSONObject object = null;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("province", province);
            jsonObject.put("city", city);
            jsonObject.put("area", area);

            object = new JSONObject();
            object.put("userId", userId);
            object.put("userInfo", jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String json = object.toString();
        Log.i("info", "修改地区的json串：" + json);
        RequestBody body = RequestBody.create(MEDIA_TYPE_MARKDOWN, json);
        Request request = new Request.Builder()
                .put(body)
                .url(Constant.CHANGEINFO_URL)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("info", "修改地区失败:" + e.getMessage());
                Message msg = new Message();
                msg.what = 404;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message msg = new Message();
                msg.what = 2;
                msg.obj = response.code();
                mHandler.sendMessage(msg);
            }
        });
    }

    //获取真实的用户信息
    public void getTrueData(final String userId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                execute(userId);
            }
        }).start();
    }

    public void execute(String userId) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(Constant.GETUSERINFO_URL + userId + "?needIcon=true")
                .build();
        Log.i("info", "请求用户信息的url:" + Constant.GETUSERINFO_URL + userId + "?needIcon=true");
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("info", "获取用户信息失败：" + e.getMessage());
                Message msg = new Message();
                msg.what = 404;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message msg = new Message();
                msg.obj = response.body().string();
                msg.what = response.code();
                mHandler.sendMessage(msg);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("OwnerInformationActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("OwnerInformationActivity");
        MobclickAgent.onPause(this);
    }
}
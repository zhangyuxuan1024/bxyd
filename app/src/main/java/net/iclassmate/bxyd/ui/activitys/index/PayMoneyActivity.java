package net.iclassmate.bxyd.ui.activitys.index;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaCodec;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.bean.index.Order;
import net.iclassmate.bxyd.bean.index.PayParameter;
import net.iclassmate.bxyd.bean.index.PersonInfo;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.utils.IPUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PayMoneyActivity extends Activity implements View.OnClickListener {
    private ImageView img_back;
    private TextView tv_back, tv_title;
    private LinearLayout layout_wx;

    private SharedPreferences sp;
    private Context mContext;

    private String adsId, contactPhone, joinAdsTime, userNumber, userid;
    private List<PersonInfo> list;

    private Order order;
    private PayParameter parameter;

    private static final String APP_ID = Constant.PAY_WX_ID;
    private IWXAPI api;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String result = (String) msg.obj;
                    if (result != null && !result.equals("")) {
                        try {
                            JSONObject json = new JSONObject(result);
                            order.parserJson(json);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case 2:
                    result = (String) msg.obj;
                    try {
                        JSONObject josn = new JSONObject(result);
                        parameter.parserJson(josn);
                        payMoney(parameter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_money);

        api = WXAPIFactory.createWXAPI(this, APP_ID);
        initData();
        init();
        order();
    }

    private void initData() {
        mContext = this;
        sp = getSharedPreferences(Constant.SHARED_PREFERENCES, MODE_PRIVATE);
        userid = sp.getString(Constant.ID_USER, "");
        Intent intent = getIntent();
        adsId = intent.getStringExtra("adsId");
        contactPhone = intent.getStringExtra("contactPhone");
        joinAdsTime = intent.getStringExtra("joinAdsTime");
        userNumber = intent.getStringExtra("userNumber");
        list = (List<PersonInfo>) intent.getSerializableExtra("list");
        if (joinAdsTime.contains("年")) {
            joinAdsTime = joinAdsTime.replace("年", "-");
        }
        if (joinAdsTime.contains("月")) {
            joinAdsTime = joinAdsTime.replace("月", "-");
        }
        if (joinAdsTime.contains("日")) {
            joinAdsTime = joinAdsTime.replace("日", "");
        }
    }

    private void init() {
        img_back = (ImageView) findViewById(R.id.study_message_back);
        img_back.setOnClickListener(this);
        tv_back = (TextView) findViewById(R.id.study_message_left_tv);
        tv_back.setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.study_message_title_tv);
        tv_title.setText("支付");
        order = new Order();
        parameter = new PayParameter();

        layout_wx = (LinearLayout) findViewById(R.id.pay_linear_wx);
        layout_wx.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.study_message_back:
            case R.id.study_message_left_tv:
                finish();
                break;
            case R.id.pay_linear_wx:
                if (order == null) {
                    Toast.makeText(mContext, "获取订单信息失败，请重试！", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(mContext, "支付", Toast.LENGTH_SHORT).show();
                pay(order);
                break;
        }
    }

    //生成订单
    private void order() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient mOkHttpClient = new OkHttpClient();
                JSONObject json = new JSONObject();
                try {
                    json.put("adsId", adsId);
                    json.put("contactPhone", contactPhone);
                    json.put("joinAdsTime", joinAdsTime);
                    json.put("paymentWay", 1);
                    json.put("userId", userid);
                    int num = Integer.parseInt(userNumber);
                    json.put("userNumber", num);

                    JSONArray array = new JSONArray();
                    for (int i = 0; i < list.size(); i++) {
                        PersonInfo info = list.get(i);
                        JSONObject object = new JSONObject();
                        object.put("userCardNumber", info.getId());
                        if (info.isMaleSelect()) {
                            object.put("userGender", 1);
                        } else {
                            object.put("userGender", 0);
                        }
                        object.put("userName", info.getName());
                        object.put("userPhone", info.getPhone());
                        array.put(object);
                    }
                    json.put("users", array);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody formBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
                final Request request = new Request.Builder()
                        .url(Constant.BX_MAKE_ORDER)
                        .post(formBody)
                        .build();
                Log.i("info", "下单参数=" + json.toString());
                Response response = null;
                try {
                    response = mOkHttpClient.newCall(request).execute();
                    String result = response.body().string();
                    if (response.isSuccessful()) {
                        Message message = new Message();
                        message.what = 1;
                        message.obj = result;
                        mHandler.sendMessage(message);
                    } else {
                        mHandler.sendEmptyMessage(404);
                    }
                    Log.i("info", "订单=" + result);
                } catch (IOException e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(404);
                } finally {
                    if (response != null) {
                        response.close();
                    }
                }
            }
        }).start();
    }

    private void pay(final Order order) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient mOkHttpClient = new OkHttpClient();
                JSONObject json = new JSONObject();
                try {
                    String ip = IPUtils.getIPAddress(mContext);
                    json.put("ip", ip);
                    json.put("platform", "android");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                RequestBody formBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
                final Request request = new Request.Builder()
                        .url(String.format(Constant.BX_PAY_MONEY, order.getId(), userid))
                        .post(formBody)
                        .build();
                Log.i("info", "支付参数=" + json.toString());

                Response response = null;
                try {
                    response = mOkHttpClient.newCall(request).execute();
                    String result = response.body().string();
                    if (response.isSuccessful()) {
                        Message message = new Message();
                        message.what = 2;
                        message.obj = result;
                        mHandler.sendMessage(message);
                    } else {
                        mHandler.sendEmptyMessage(404);
                    }
                    Log.i("info", "支付=" + result);
                } catch (IOException e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(404);
                } finally {
                    if (response != null) {
                        response.close();
                    }
                }
            }
        }).start();

    }

    private void payMoney(PayParameter pay) {
        boolean isPaySupported = api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
        if (!isPaySupported) {
            Toast.makeText(mContext, "您当前微信版本过低，请更后进行支付", Toast.LENGTH_SHORT).show();
            return;
        }
        PayReq request = new PayReq();
        //应用ID
        request.appId = pay.getAppId();
        //商户号
        request.partnerId = pay.getPartnerId();
        //预支付交易会话ID
        request.prepayId = order.getId();
        //扩展字段 暂填写固定值Sign=WXPay
        request.packageValue = "Sign=WXPay";
        //随机字符串
        request.nonceStr = pay.getNonceStr();
        //时间戳
        request.timeStamp = pay.getTimestamp();
        //签名
        request.sign = pay.getSign();
        api.sendReq(request);
        Toast.makeText(mContext, "正在调起支付", Toast.LENGTH_SHORT).show();
    }
}

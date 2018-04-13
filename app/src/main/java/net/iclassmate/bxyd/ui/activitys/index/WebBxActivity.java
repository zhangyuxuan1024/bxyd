package net.iclassmate.bxyd.ui.activitys.index;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.bean.PayResult;
import net.iclassmate.bxyd.bean.index.Banner;
import net.iclassmate.bxyd.bean.index.PayParameter;
import net.iclassmate.bxyd.bean.index.Recommend;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.ui.activitys.owner.BoundPhoneNumActivity;
import net.iclassmate.bxyd.utils.HttpManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Map;

public class WebBxActivity extends Activity implements View.OnClickListener {
    private Context mContext;
    private String url, title, str_back;
    //type :1 活动详情界面  type 2 报名界面
    private String type, web_title, web_des, web_img_url;
    //type2:如果是1，有立即报名按钮；如果是2，没有立即报名按钮。
    private int type2;

    private ImageView img_back, img_right;
    private TextView tv_back, tv_title, tv_bm;
    private WebView webView;

    private Object data;

    private SharedPreferences sharedPreferences;
    private String userid, actionid, phone;

    private PayParameter parameter;
    private IWXAPI api;
    private static final String APP_ID = Constant.PAY_WX_ID;

    private static final int SDK_PAY_FLAG = 1;
    private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
    private LinearLayout linearLayout;
    private ScrollView web_scrollview;

    private HttpManager httpManager;
    private long last_click_time;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Toast.makeText(mContext, "支付成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(mContext, "支付失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                //接收Bitmap
                case 2:
                    Bitmap bitmap = (Bitmap) msg.obj;
                    if (bitmap != null) {
                        shareWx(bitmap);
                    } else {
                        Toast.makeText(mContext, "分享失败，请重试！", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_bx);
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        url = intent.getStringExtra("url");
        type = intent.getStringExtra("type");
        type2 = intent.getIntExtra("type2", 1);
        str_back = intent.getStringExtra("back");
        data = intent.getSerializableExtra("data");
//        Log.i("info", "url=" + url);

        httpManager = new HttpManager();
        init();
    }

    private void init() {
        mContext = this;
        //注册appid
        api = WXAPIFactory.createWXAPI(this, null);
        api.registerApp(APP_ID);

        parameter = new PayParameter();

        img_back = (ImageView) findViewById(R.id.study_message_back);
        img_back.setOnClickListener(this);

        tv_back = (TextView) findViewById(R.id.study_message_left_tv);
        tv_back.setText(str_back);
        tv_back.setOnClickListener(this);

        tv_title = (TextView) findViewById(R.id.study_message_title_tv);
        tv_title.setText(title);
        img_right = (ImageView) findViewById(R.id.study_message_right_icon);
        img_right.setOnClickListener(this);

        tv_bm = (TextView) findViewById(R.id.tv_bx_bm);
        tv_bm.setOnClickListener(this);
        if (type.equals("owner") || type.equals("bm") || type2 == 2) {
            tv_bm.setVisibility(View.GONE);
            img_right.setVisibility(View.INVISIBLE);
        } else if (type.equals("banner")) {
            img_right.setVisibility(View.VISIBLE);
            img_right.setImageResource(R.mipmap.ic_xiaobaidian);

            Banner banner = (Banner) data;
            actionid = banner.getId();
            web_title = banner.getName();
            web_img_url = banner.getImageUrl();
        } else if (type.equals("recommend")) {
            img_right.setVisibility(View.VISIBLE);
            img_right.setImageResource(R.mipmap.ic_xiaobaidian);

            Recommend recommend = (Recommend) data;
            actionid = recommend.getId();
            web_title = recommend.getName();
            web_img_url = recommend.getImageUrl();
        }
        img_right.setVisibility(View.INVISIBLE);

        linearLayout = (LinearLayout) findViewById(R.id.view_linear);

        webView = (WebView) findViewById(R.id.webview_bx);
        initWebView();

        sharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCES, MODE_PRIVATE);
        userid = sharedPreferences.getString(Constant.ID_USER, "");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.study_message_back:
            case R.id.study_message_left_tv:
                this.finish();
                break;
            case R.id.tv_bx_bm:
                phone = sharedPreferences.getString(Constant.USER_PHONE, "");
                if (phone == null || phone.length() != 11) {
                    Intent intent = new Intent(mContext, BoundPhoneNumActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(mContext, WebBxActivity.class);
                    intent.putExtra("title", "报名");
                    intent.putExtra("back", "返回");
                    intent.putExtra("url", String.format(Constant.BX_SIGN_UP, actionid, userid, phone));
                    intent.putExtra("type", "bm");
                    startActivity(intent);
                }
                break;
            //分享
            case R.id.study_message_right_icon:
                if (System.currentTimeMillis() - last_click_time > 2000) {
                    last_click_time = System.currentTimeMillis();
                    getShareImage(web_img_url);
                }
                break;
        }
    }

    private void getShareImage(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = httpManager.getBitmap(url, true);
                if (bitmap != null) {
                    bitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, true);
                }
                Message message = new Message();
                message.what = 2;
                message.obj = bitmap;
                mHandler.sendMessage(message);
            }
        }).start();
    }

    private void initWebView() {
        try {
            WebSettings settings = webView.getSettings();
            settings.setPluginState(WebSettings.PluginState.ON);
            settings.setJavaScriptCanOpenWindowsAutomatically(true);

            // 设置 缓存模式
            settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
            // 开启 DOM storage API 功能
            settings.setDomStorageEnabled(true);
            // 是否使用缓存
            settings.setAppCacheEnabled(true);

            // 支持javascript
            settings.setJavaScriptEnabled(true);
            // 设置可以支持缩放
            settings.setSupportZoom(true);
            // 设置出现缩放工具
            settings.setBuiltInZoomControls(true);
            settings.setDisplayZoomControls(false);
            // 扩大比例的缩放
            webView.getSettings().setUseWideViewPort(true);
            // 自适应屏幕
            // webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
            // webView.getSettings().setLoadWithOverviewMode(true);

            webView.getSettings().setUseWideViewPort(true);
            webView.getSettings().setLoadWithOverviewMode(true);

            webView.addJavascriptInterface(this, "myObj");
            // 自适应屏幕
//            webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//            webView.getSettings().setLoadWithOverviewMode(true);

            webView.getSettings().setUseWideViewPort(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    linearLayout.setVisibility(View.GONE);
                    //自动播放，不过有点问题
//                    webView.loadUrl("javascript:(function() { var videos = document.getElementsByTagName('video'); for(var i=0;i<videos.length;i++){videos[i].play();}})()");
                }

//                @Override
//                public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                    // 在APP内部打开链接，不要调用系统浏览器
//                    view.loadUrl(url);
//                    return true;
//                }
            });
            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onReceivedTitle(WebView view, String title) {
//                    tv_title.setText(title);
                }

                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    super.onProgressChanged(view, newProgress);
                    if (newProgress > 95) {
                        linearLayout.setVisibility(View.GONE);
                    }
                }

            });

            webView.getSettings().setPluginState(WebSettings.PluginState.ON);
            WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
            int width = wm.getDefaultDisplay().getWidth();
            if (width > 650) {
                this.webView.setInitialScale(190);
            } else if (width > 520) {
                this.webView.setInitialScale(160);
            } else if (width > 450) {
                this.webView.setInitialScale(140);
            } else if (width > 300) {
                this.webView.setInitialScale(120);
            } else {
                this.webView.setInitialScale(100);
            }
            settings.setUserAgentString("0"); // 0为手机默认, 1为PC台机，2为IPHONE
            settings.setUserAgentString("Mozilla/5.0 (Linux; U; Android 2.2.1; zh-cn; MB525 Build/3.4.2-117) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
            webView.loadUrl(url);
        } catch (Exception e) {
            this.finish();
        }
    }


    @JavascriptInterface
    public void immediatePayment(String result, int type) {
//        Log.i("info", "H5返回result=" + result + "；type=" + type);
        if (result != null && !result.equals("")) {
            JSONObject josn = null;
            try {
                josn = new JSONObject(result);
                if (type == 1){
                    parameter.parserJson(josn);
                    payMoney(parameter);
                }else if(type == 2){
                    String url = josn.getString("url");
//                    Log.i("info", "result=" + result + "\nurl=" + url);
                    playVideo(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //调用手机播放器播放视频
    public void playVideo(String url){
        Uri video = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(video, "video/*");
        startActivity(intent);
    }

    //微信支付
    private void payMoney(PayParameter pay) {
        if (!api.isWXAppInstalled()) {
            Toast.makeText(mContext, "您未安装微信，不能进行微信支付", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean isPaySupported = api.getWXAppSupportAPI() >= com.tencent.mm.sdk.constants.Build.PAY_SUPPORTED_SDK_INT;
        if (!isPaySupported) {
            Toast.makeText(mContext, "您当前微信版本过低，请更后进行支付", Toast.LENGTH_SHORT).show();
            return;
        }

        PayReq request = new PayReq();
        //应用ID
        request.appId = APP_ID;
        //商户号
        request.partnerId = pay.getPartnerId();
        //预支付交易会话ID
        request.prepayId = pay.getPrepayId();
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

    private void shareWx(Bitmap thumb) {
        if (!api.isWXAppInstalled()) {
            Toast.makeText(mContext, "您未安装微信，不能进行微信分享", Toast.LENGTH_SHORT).show();
            return;
        }
        int wxSdkVersion = api.getWXAppSupportAPI();
        if (wxSdkVersion < TIMELINE_SUPPORTED_VERSION) {
            Toast.makeText(mContext, "当前微信版本不支持分享功能", Toast.LENGTH_LONG).show();
            return;
        }

        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = web_title;
        msg.description = "冰雪运动，一场值得冒险的旅行！";

        // thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.img_morentupian);
        ByteArrayOutputStream output = new ByteArrayOutputStream();//初始化一个流对象
        thumb.compress(Bitmap.CompressFormat.PNG, 100, output);//把bitmap100%高质量压缩 到 output对象里
        thumb.recycle();//自由选择是否进行回收
        byte[] bytes = output.toByteArray();//转换成功了
        msg.thumbData = bytes;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "webpage";
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        api.sendReq(req);

        finish();
    }
    //支付宝
//    private void payAli(final String orderInfo) {
//        Runnable payRunnable = new Runnable() {
//            @Override
//            public void run() {
//                PayTask alipay = new PayTask(WebBxActivity.this);
//                Map<String, String> result = alipay.payV2(orderInfo, true);
//                Message msg = new Message();
//                msg.what = SDK_PAY_FLAG;
//                msg.obj = result;
//                mHandler.sendMessage(msg);
//            }
//        };
//        // 必须异步调用
//        Thread payThread = new Thread(payRunnable);
//        payThread.start();
//    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) {
            webView.onResume();
        }
    }

    @Override
    protected void onPause() {
        if (webView != null) {
            webView.reload();
            webView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            try {
                webView.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

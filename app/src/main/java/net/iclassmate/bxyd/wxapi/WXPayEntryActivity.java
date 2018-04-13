package net.iclassmate.bxyd.wxapi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.ui.activitys.index.WebBxActivity;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;
    private TextView tv_result;
    private Context mContext;
    private SharedPreferences sharedPreferences;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Intent intent = new Intent(mContext, WebBxActivity.class);
                    intent.putExtra("title", "活动订单");
                    intent.putExtra("back", "我的");
                    String uid = sharedPreferences.getString(Constant.ID_USER, "");
                    String url = String.format(Constant.BX_OWNER_ACTIVITY, uid);
                    intent.putExtra("url", url);
                    intent.putExtra("type", "owner");
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxentry);
        tv_result = (TextView) findViewById(R.id.tv_pay_result);

        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, Constant.PAY_WX_ID, false);
        // 将该app注册到微信
        api.registerApp(Constant.PAY_WX_ID);

        api.handleIntent(getIntent(), this);

        mContext = this;
        sharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCES, MODE_PRIVATE);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
    }

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    @Override
    public void onResp(BaseResp resp) {
        mHandler.sendEmptyMessage(1);
        String result = "";
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    result = "支付成功";
                    finish();
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    result = "用户取消";
                    finish();
                    break;
                case BaseResp.ErrCode.ERR_COMM:
                    result = "签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等";
                    break;
                default:
                    result = "未知错误";
                    break;
            }
            tv_result.setText(result);
        }
    }
}

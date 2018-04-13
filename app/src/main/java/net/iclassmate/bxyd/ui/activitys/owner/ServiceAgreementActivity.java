package net.iclassmate.bxyd.ui.activitys.owner;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

/**
 * Created by xydbj on 2016.6.21.
 */
public class ServiceAgreementActivity extends Activity implements View.OnClickListener{
    private ImageView serviceagreement_iv_back;
    private TextView serviceagreement_tv_back;
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serviceagreement);
        initView();
        webView.loadUrl("file:///android_asset/serive.html");
    }
    public void initView(){
        serviceagreement_iv_back = (ImageView) findViewById(R.id.serviceagreement_iv_back);
        serviceagreement_tv_back = (TextView) findViewById(R.id.serviceagreement_tv_back);

        serviceagreement_tv_back.setOnClickListener(this);
        serviceagreement_iv_back.setOnClickListener(this);

        webView = (WebView) findViewById(R.id.webView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.serviceagreement_tv_back:
            case R.id.serviceagreement_iv_back:
                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("ServiceAgreementActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("ServiceAgreementActivity");
        MobclickAgent.onPause(this);
    }
}

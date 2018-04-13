package net.iclassmate.bxyd.ui.activitys.owner;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;

/**
 * Created by xydbj on 2016.6.18.
 */
public class AboutUsActivity extends Activity implements View.OnClickListener {
    private ImageView aboutus_iv_back;
    private TextView aboutus_tv_back,aboutus_code;
    private RelativeLayout aboutus_rl_grade,aboutus_rl_service,aboutus_rl_welcome;
    private String versionCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);
        initView();
        versionCode = getVersionInfo();
        aboutus_code.setText(versionCode);
    }

    public void initView(){
        aboutus_code = (TextView) findViewById(R.id.aboutus_code);
        aboutus_rl_grade = (RelativeLayout) findViewById(R.id.aboutus_rl_grade);
        aboutus_rl_service = (RelativeLayout) findViewById(R.id.aboutus_rl_service);
        aboutus_rl_welcome = (RelativeLayout) findViewById(R.id.aboutus_rl_welcome);
        aboutus_iv_back = (ImageView) findViewById(R.id.aboutus_iv_back);
        aboutus_tv_back = (TextView) findViewById(R.id.aboutus_tv_back);

        aboutus_iv_back.setOnClickListener(this);
        aboutus_tv_back.setOnClickListener(this);
        aboutus_rl_grade.setOnClickListener(this);
        aboutus_rl_service.setOnClickListener(this);
        aboutus_rl_welcome.setOnClickListener(this);
    }

    public String getVersionInfo(){
        String versionName = null;
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(),0);
            versionName = "v"+packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.aboutus_iv_back:
            case R.id.aboutus_tv_back:
                finish();
                break;
            case R.id.aboutus_rl_grade:
//               去评分
                try{
                    Uri uri = Uri.parse("market://details?id=" + getPackageName());
                    Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }catch(ActivityNotFoundException e){
                    Toast.makeText(AboutUsActivity.this, "Couldn't launch the market !", Toast.LENGTH_SHORT).show();
                }
//                分享
//                Intent sendIntent = new Intent();
//                sendIntent.setAction(Intent.ACTION_SEND);
//                sendIntent.setType("text/*");
//                sendIntent.putExtra(Intent.EXTRA_TEXT, "分享？分享！");
//                startActivity(sendIntent);
                break;
            case R.id.aboutus_rl_service://服务协议
                Intent intent_service = new Intent(AboutUsActivity.this,ServiceAgreementActivity.class);
                startActivity(intent_service);
                break;
            case R.id.aboutus_rl_welcome://欢迎页
                Intent intent_welcomde = new Intent(AboutUsActivity.this, WelcomePageActivity.class);
                startActivity(intent_welcomde);
                break;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("AboutUsActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("AboutUsActivity");
        MobclickAgent.onPause(this);
    }
}
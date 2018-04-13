package net.iclassmate.bxyd.ui.fragment.tran;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;

/**
 * Created by xydbj on 2016.8.11.
 */
public class DeleteFileActivity extends Activity implements View.OnClickListener {
    private TextView deletefile_cancel,deletefile_sure,deletefile_content;
    private String fileName;
    private static final int RESULT_CODE = 9;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deletefile);
        Intent intent = getIntent();
        fileName = intent.getStringExtra("fileName");
        if (fileName == null) return;
        initView();
    }
    public void initView(){
        deletefile_sure = (TextView) findViewById(R.id.deletefile_sure);
        deletefile_cancel = (TextView) findViewById(R.id.deletefile_cancel);
        deletefile_content = (TextView) findViewById(R.id.deletefile_content);

        deletefile_sure.setOnClickListener(this);
        deletefile_cancel.setOnClickListener(this);
        deletefile_content.setText("删除文件"+fileName);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.deletefile_cancel:
                finish();
                break;
            case R.id.deletefile_sure:
                Intent intent = new Intent();
                setResult(RESULT_CODE,intent);
                finish();
                break;
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("DeleteFileActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("DeleteFileActivity");
        MobclickAgent.onPause(this);
    }
}
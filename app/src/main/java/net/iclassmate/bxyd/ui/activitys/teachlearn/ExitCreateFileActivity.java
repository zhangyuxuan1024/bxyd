package net.iclassmate.bxyd.ui.activitys.teachlearn;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import net.iclassmate.bxyd.R;

/**
 * Created by xydbj on 2016.10.18.
 */
public class ExitCreateFileActivity extends Activity implements OnClickListener{
    private TextView exitcreatefile_sure,exitcreatefile_cancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exitcreatefile);
        initView();
    }
    public void initView(){
        exitcreatefile_sure = (TextView) findViewById(R.id.exitcreatefile_sure);
        exitcreatefile_cancel = (TextView) findViewById(R.id.exitcreatefile_cancel);
        exitcreatefile_sure.setOnClickListener(this);
        exitcreatefile_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.exitcreatefile_sure:
                Intent intent = new Intent(ExitCreateFileActivity.this,CreateNewFileActivity.class);
                setResult(RESULT_OK,intent);
                finish();
                break;
            case R.id.exitcreatefile_cancel:
                finish();
                break;
        }
    }
}
package net.iclassmate.bxyd.ui.activitys.owner;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.utils.StringUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

/**
 * Created by xydbj on 2016.8.2.
 */
public class UpdateActivity extends Activity implements View.OnClickListener {
    private TextView update_count, update_all, update_percentage, update_cancel;
    private String size, url,all,now;
    private Dialog mDownloadDialog;
    private Dialog noticeDialog;

    /*是否取消更新*/
    private boolean cancelUpdate = false;
    /*记录进度条数量*/
    private int progress;
    /* 更新进度条 */
    private ProgressBar mProgress;
    /*下载保存路径*/
    private String mSavePath;
    /* 下载中 */
    private static final int DOWNLOAD = 1;
    /* 下载结束 */
    private static final int DOWNLOAD_FINISH = 2;
    /* 下载异常 */
    private static final int EXCEPTION = 0;
    /* 非法http协议 */
    private static final int MALFORMED = -1;
    /* 服务器不可用 */
    private static final int HOSTWRONG = -2;

    private DefaultHttpClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        Intent intent = getIntent();
        size = intent.getStringExtra("size");
        url = intent.getStringExtra("url");
        initView();
        downloadApk();
    }
    public void downloadApk(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                execute();
            }
        }).start();
    }
    public void execute(){
        try {
            // 判断SD卡是否存在，并且是否具有读写权限
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                // 获得存储卡的路径
                String sdpath = Environment.getExternalStorageDirectory() + "/";
                mSavePath = sdpath + "xyddownload";
                String ip = null;
                if (url != null){
                    ip = url;
                }else {
                    mHandler.sendEmptyMessage(10);
                    return;
                }
                client = new DefaultHttpClient();
                client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10 * 1000);
                client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10 * 1000);
                HttpGet httpGet = new HttpGet(ip);
                HttpResponse response;
                response = client.execute(httpGet);
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    HttpEntity entity = response.getEntity();
                    long length = entity.getContentLength();
                    all = StringUtils.doubletodouble((double) length / 1024 / 1024);
                    InputStream is = entity.getContent();
                    File file = new File(mSavePath);
                    // 判断文件目录是否存在
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    File apkFile = new File(mSavePath, "xydspace.apk");
                    FileOutputStream fos = new FileOutputStream(apkFile);
                    int count = 0;
                    // 缓存
                    byte buf[] = new byte[1024];
                    // 写入到文件中
                    do {
                        int numread = is.read(buf);
                        count += numread;
                        now = StringUtils.doubletodouble((double) count / 1024 / 1024);
                        // 计算进度条位置
                        progress = (int) (((float) count / length) * 100);
                        // 更新进度
                        mHandler.sendEmptyMessage(DOWNLOAD);
                        if (numread <= 0) {
                            // 下载完成
                            mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
                            break;
                        }
                        // 写入文件
                        fos.write(buf, 0, numread);
                    } while (!cancelUpdate);// 点击取消就停止下载.
                    if (cancelUpdate) {// 手动取消下载
                        client.getConnectionManager().shutdown();
                    }

                    fos.close();
                    is.close();
                } else {
                    mHandler.sendEmptyMessage(HOSTWRONG);
                }
            }
        } catch (MalformedURLException e) {
            mHandler.sendEmptyMessage(MALFORMED);
            e.printStackTrace();
        } catch (IOException e) {
            mHandler.sendEmptyMessage(EXCEPTION);
            e.printStackTrace();
        }
    }
    public Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            update_all.setText(all + "M");
            update_percentage.setText(progress + "%");
            update_count.setText(now + "M");
            switch (msg.what) {
                // 正在下载
                case DOWNLOAD:
                    // 设置进度条位置
                    mProgress.setProgress(progress);
                    break;
                case DOWNLOAD_FINISH:
                    //杀死下载窗口
                    UpdateActivity.this.finish();
                    // 安装文件
                    installApk();
                    break;
                case 3:
                    if (noticeDialog != null && noticeDialog.isShowing()) {
                        noticeDialog.dismiss();
                    }
                    // showDownloadDialog();
                    break;
                case 10:
                    if (mDownloadDialog != null) {
                        mDownloadDialog.dismiss();
                    }
                    Toast.makeText(UpdateActivity.this, "下载出错,重新更新版本", Toast.LENGTH_SHORT).show();
                    break;
                case EXCEPTION:
                    if (mDownloadDialog != null) {
                        mDownloadDialog.dismiss();
                    }
                    if (!cancelUpdate) {
                        Toast.makeText(UpdateActivity.this, "服务器超时,更新版本失败!", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case MALFORMED:
                    if (mDownloadDialog != null) {
                        mDownloadDialog.dismiss();
                    }
                    Toast.makeText(UpdateActivity.this, "非法的HTTP协议", Toast.LENGTH_SHORT).show();
                    break;
                case HOSTWRONG:
                    if (mDownloadDialog != null) {
                        mDownloadDialog.dismiss();
                    }
                    Toast toast = Toast.makeText(UpdateActivity.this, "服务器不可用", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    break;
                default:
                    break;
            }
        }
    };

    public void initView() {
        update_count = (TextView) findViewById(R.id.update_count);
        update_all = (TextView) findViewById(R.id.update_all);
        update_percentage = (TextView) findViewById(R.id.update_percentage);
        update_cancel = (TextView) findViewById(R.id.update_cancel);
        mProgress = (ProgressBar) findViewById(R.id.update_progressbar);

        update_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.update_cancel:
                cancelUpdate = true;
                finish();
                break;
        }
    }

    /**
     * 安装APK文件
     */
    public void installApk() {
        File apkfile = new File(mSavePath, "xydspace.apk");
        if (!apkfile.exists()) {
            return;
        }
        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("UpdateActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("UpdateActivity");
        MobclickAgent.onPause(this);
    }
}

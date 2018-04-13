package net.iclassmate.bxyd.ui.activitys.owner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.constant.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

/**
 * Created by xydbj on 2016.6.30.
 */
public class TwoCodeActivity extends Activity implements OnClickListener {
    private Bitmap bitmap_logo;
    private static final int IMAGE_HALFWIDTH = 40;
    private ImageView twocode_iv_twocode, twocode_iv_back;
    private static final int QR_WIDTH = 300;
    private static final int QR_HEIGHT = 300;
    private TextView tv_userName, tv_userCode, twocode_tv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twocode);
        initView();
        bitmap_logo = BitmapFactory.decodeResource(getResources(), R.mipmap.logo);
        Intent intent = getIntent();
        String userCode = intent.getStringExtra("userCode");
        String userName = intent.getStringExtra("userName");
        String userNum = intent.getStringExtra(Constant.USER_CODE);
        String userType = intent.getStringExtra(Constant.ID_USERTYPE);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constant.USER_CODE, userNum);
            jsonObject.put(Constant.ID_USERTYPE, userType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String information = jsonObject.toString();
        Log.i("info", "我的二维码内的信息：" + information);
        Bitmap src = createQRImage(information);
        twocode_iv_twocode.setImageBitmap(src);
        tv_userName.setText(userName);
        tv_userCode.setText("用户号：" + userCode);
    }

    public void initView() {
        twocode_iv_twocode = (ImageView) findViewById(R.id.twocode_iv_twocode);
        tv_userName = (TextView) findViewById(R.id.twocode_tv_name);
        tv_userCode = (TextView) findViewById(R.id.twocode_tv_code);
        twocode_tv_back = (TextView) findViewById(R.id.twocode_tv_back);
        twocode_iv_back = (ImageView) findViewById(R.id.twocode_iv_back);
        twocode_iv_back.setOnClickListener(this);
        twocode_tv_back.setOnClickListener(this);
    }

    public Bitmap createQRImage(String url) {
        try {
            //判断URL合法性
            if (url == null || "".equals(url) || url.length() < 1) {
                return null;
            }
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            //图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
            //下面这里按照二维码的算法，逐个生成二维码的图片，
            //两个for循环是图片横列扫描的结果
            for (int y = 0; y < QR_HEIGHT; y++) {
                for (int x = 0; x < QR_WIDTH; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * QR_WIDTH + x] = 0xff000000;
                    } else {
                        pixels[y * QR_WIDTH + x] = 0xffffffff;
                    }
                }
            }
            //生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
            //显示到一个ImageView上面
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap addLogo(Bitmap src, Bitmap logo) {
        if (src == null) {
            return null;
        }
        if (logo == null) {
            return src;
        }
        int src_width = src.getWidth();
        int src_height = src.getHeight();
        int logo_width = logo.getWidth();
        int logo_height = logo.getHeight();

        if (src_width == 0 || src_height == 0) {
            return null;
        }
        if (logo_width == 0 || src_height == 0) {
            return src;
        }
        float index = src_width * 1.0f / 5 / logo_width;
        Bitmap bm = Bitmap.createBitmap(src_width, src_height, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bm);
            canvas.drawBitmap(bm, 0, 0, null);
            canvas.scale(index, index, src_width / 2, src_height / 2);
            canvas.drawBitmap(logo, (src_width - logo_width) / 2, (src_height - logo_height) / 2, null);
            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bm;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.twocode_iv_back:
            case R.id.twocode_tv_back:
                TwoCodeActivity.this.finish();
                break;
        }
    }
}













package net.iclassmate.bxyd.ui.activitys.owner;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.utils.MyBitmapStore;
import net.iclassmate.bxyd.utils.emotion.DisplayUtils;
import net.iclassmate.bxyd.view.BlurImageView;
import net.iclassmate.bxyd.view.CircleImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by xydbj on 2016.6.29.
 */
public class ImageIconActivity extends Activity implements View.OnClickListener {
    private ImageView imageicon_iv_change, imageicon_iv_bigimg, imageicon_iv_back;
    private CircleImageView circleImageView;
    private TextView item_photo_selector_take, item_photo_selector_get, item_photo_selector_cancel, imageicon_tv_back;
    private PopupWindow window;
    private String fileName, icon, userType;
    private byte[] b;
    private static final int REQUEST_CODE = 1;
    private static final int GALLERY_DATA = 3022;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageicon);
        Intent intent = getIntent();
        icon = intent.getStringExtra("icon");
//        Log.i("info", "头像icon:" + icon);
        b = intent.getByteArrayExtra("bitmap");
        initView();

        if (b == null || b.length == 0) {
            if (userType.equals("0")) {
                Picasso.with(ImageIconActivity.this).load(icon).placeholder(R.mipmap.ic_jigou_gaitouxiang).into(circleImageView);
            } else if (userType.equals("1")) {
                Picasso.with(ImageIconActivity.this).load(icon).placeholder(R.mipmap.ic_touxiang_mingpian).into(circleImageView);
            }
        } else {
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            if (bitmap != null) {
                circleImageView.setImageBitmap(bitmap);
                imageicon_iv_bigimg.setBackground(BlurImageView.BlurImages(bitmap, ImageIconActivity.this));
            }
        }
    }

    public void initView() {
        circleImageView = (CircleImageView) findViewById(R.id.imageicon_iv_img);
        sharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        userType = sharedPreferences.getString(Constant.ID_USERTYPE, "0");
        Log.i("info", "userType=" + userType);
//        if (userType.equals("0")) {
//            circleImageView.setImageResource(R.mipmap.ic_jigou_gaitouxiang);
//        } else if (userType.equals("1")) {
//            circleImageView.setImageResource(R.mipmap.ic_geren_xuanren);
//        }
        imageicon_iv_bigimg = (ImageView) findViewById(R.id.imageicon_iv_bigimg);
        imageicon_iv_change = (ImageView) findViewById(R.id.imageicon_iv_change);
        imageicon_iv_back = (ImageView) findViewById(R.id.imageicon_iv_back);
        imageicon_tv_back = (TextView) findViewById(R.id.imageicon_tv_back);

        imageicon_iv_change.setOnClickListener(this);
        imageicon_iv_back.setOnClickListener(this);
        imageicon_tv_back.setOnClickListener(this);
    }


    public void initPhotoPopupWindow() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_photo_selector, null);
        item_photo_selector_take = (TextView) view.findViewById(R.id.item_photo_selector_take);
        item_photo_selector_get = (TextView) view.findViewById(R.id.item_photo_selector_get);
        item_photo_selector_cancel = (TextView) view.findViewById(R.id.item_photo_selector_cancel);
        item_photo_selector_take.setOnClickListener(this);
        item_photo_selector_get.setOnClickListener(this);
        item_photo_selector_cancel.setOnClickListener(this);
        ShowPhotoPopupWindow(view);
    }

    public void ShowPhotoPopupWindow(View view) {
        window = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        window.setOutsideTouchable(true);
        window.setBackgroundDrawable(new BitmapDrawable());

        final WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;
        getWindow().setAttributes(lp);

        window.setAnimationStyle(R.style.mypopwindow_anim_style);
        window.showAtLocation(ImageIconActivity.this.findViewById(R.id.imageicon_iv_change), Gravity.BOTTOM, 0, 0);
        window.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageicon_iv_back:
            case R.id.imageicon_tv_back:
                ImageIconActivity.this.finish();
                break;
            case R.id.imageicon_iv_change:
                initPhotoPopupWindow();
                break;
            case R.id.item_photo_selector_take://启动相机
                if (Build.VERSION.SDK_INT < 23){
                    openCamera();
                } else {
                    AndPermission.with(this)
                            .requestCode(1012)
                            .permission(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .send();
                }
                break;
            case R.id.item_photo_selector_get://启动相册
                if (Build.VERSION.SDK_INT < 23){
                    openAlbum();
                } else {
                    AndPermission.with(this)
                            .requestCode(1013)
                            .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .send();
                }
                break;
            case R.id.item_photo_selector_cancel://取消
                window.dismiss();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        AndPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults, listener);
    }

    public PermissionListener listener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode) {
            if (requestCode == 1012){
                openCamera();
            } else if (requestCode ==1013){
                openAlbum();
            }
        }

        @Override
        public void onFailed(int requestCode) {
            if (requestCode == 1012){
                Toast.makeText(ImageIconActivity.this,"您未开放相机权限",Toast.LENGTH_SHORT).show();
            } else if(requestCode == 1013){
                Toast.makeText(ImageIconActivity.this,"您未开放相册权限",Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File out = new File(getPhotopath());
        Uri uri = Uri.fromFile(out);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, REQUEST_CODE);
        window.dismiss();
    }

    public void openAlbum() {
        Intent intent_get = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent_get.setType("image/*");
        startActivityForResult(intent_get, GALLERY_DATA);
        window.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        int width = DisplayUtils.getScreenWidthPixels(this);
        int height = DisplayUtils.getScreenHeightPixels(this);
        switch (requestCode) {
            case REQUEST_CODE://相机拍照
                Bitmap bitMap = getBitmapFromUrl(fileName, width, height);
                MyBitmapStore.setBitmap(bitMap);
                Intent intent = new Intent(ImageIconActivity.this, CutActivity.class);
                startActivity(intent);
                ImageIconActivity.this.finish();
                break;
            case GALLERY_DATA://相册
                Cursor cursor = this.getContentResolver().query(data.getData(), new String[]{MediaStore.Images.Media.DATA}, null, null, null);
                cursor.moveToFirst();//游标移到第一位，即从第一位开始读取
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                Log.i("info", "更换头像，从相册中选取图片的path：" + path);
                cursor.close();

                Bitmap bitMap2 = getBitmapFromUrl(path, width, height - 200);
                MyBitmapStore.setBitmap(bitMap2);
                Intent intent_album = new Intent(ImageIconActivity.this, CutActivity.class);
                startActivity(intent_album);
                ImageIconActivity.this.finish();
                break;
        }
    }

    //得到照片路径
    private String getPhotopath() {
        fileName = "";
        String pathUrl = Environment.getExternalStorageDirectory() + "/xyd/";
        String imageName = DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
        File file = new File(pathUrl);
        if (!file.exists()) {
            file.mkdirs();
        }
        fileName = pathUrl + imageName;
        Log.i("info", "得到图片的路径：" + fileName);
        return fileName;
    }

    //处理照片，防止内存溢出
    private Bitmap getBitmapFromUrl(String url, double width, double height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(url);

        options.inJustDecodeBounds = false;
        int mWidth = bitmap.getWidth();
        int mHeight = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = 1;
        float scaleHeight = 1;

        if (mWidth <= mHeight) {
            scaleWidth = (float) (width / mWidth);
            scaleHeight = (float) (height / mHeight);
        } else {
            scaleWidth = (float) (height / mWidth);
            scaleHeight = (float) (width / mHeight);
        }
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, mWidth, mHeight, matrix, true);
        bitmap.recycle();
        return newBitmap;
    }

    //存储照片
    private void saveScalePhoto(Bitmap bitmap) {
        String fileName = "";
        String pathUrl = Environment.getExternalStorageDirectory().getPath()
                + "/xyd/";
        String imageName = DateFormat.format("yyyyMMdd_hhmmss",
                Calendar.getInstance(Locale.CHINA))
                + ".jpg";
        FileOutputStream fos = null;
        File file = new File(pathUrl);
        file.mkdirs();
        fileName = pathUrl + imageName;
        try {
            fos = new FileOutputStream(fileName);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("ImageIconActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("ImageIconActivity");
        MobclickAgent.onPause(this);
    }
}

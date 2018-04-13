package net.iclassmate.bxyd.adapter.study;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.bean.study.FileDirList;
import net.iclassmate.bxyd.bean.study.ImageState;
import net.iclassmate.bxyd.bean.study.Resources;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.utils.HttpManager;
import net.iclassmate.bxyd.utils.LoadImageSd;
import net.iclassmate.bxyd.view.ScaleImageView;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by xydbj on 2016/6/17.
 */
public class MyViewPagerAdapter extends PagerAdapter {
    private Context context;
    private List<Object> list;
    private LoadImageSd load;
    private int type;
    private HttpManager httpManager;
    private int targetWidth;

    private Handler mHandler = new Handler();

    public MyViewPagerAdapter(Context context, List<Object> list) {
        this.context = context;
        this.list = list;
        load = new LoadImageSd();
        httpManager = new HttpManager();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.study_viewpager_item, null);
        final ScaleImageView ret = (ScaleImageView) view.findViewById(R.id.pic_look);
//        ret.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        Object obj = list.get(position);
        if (obj instanceof String) {
            final String url = (String) obj;
            ret.setTag(url);
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            int width = wm.getDefaultDisplay().getWidth();
            int height = wm.getDefaultDisplay().getHeight();
            if (type == 1) {
                loadImage(ret, url);
            } else if (type == 2) {
                load.setmHeight(width);
                load.setmWidth(height);
                load.downloadImage(ret, url);
            } else if (type == 3) {
                ret.setImageResource(R.mipmap.img_morentupian);
                ret.setTag(url);
                String path = url;
                if (path.contains("@")) {
                    int index = path.indexOf("@");
                    path = url.substring(0, index);
                }
                Picasso.with(context)
                        .load(path)
                        .config(Bitmap.Config.RGB_565)
                        .placeholder(R.mipmap.img_morentupian)
                        .error(R.mipmap.img_morentupian)
                        .into(ret);
            }
        } else if (obj instanceof FileDirList) {
            FileDirList file = (FileDirList) obj;
            String id = file.getId();
            String url = String.format(Constant.STUDY_OPEN_FILE, id);
            Picasso.with(context).load(url).placeholder(R.mipmap.img_morentupian).config(Bitmap.Config.RGB_565).error(R.mipmap.img_jiazaishibai).into(ret);
        } else if (obj instanceof Resources) {
            Resources resources = (Resources) obj;
            String id = resources.getId();
            if (id == null || id.equals("-1")) {
                Picasso.with(context).load("null").placeholder(R.mipmap.img_shanchu_wenjian).config(Bitmap.Config.RGB_565).error(R.mipmap.img_shanchu_wenjian).into(ret);
            } else {
                String url = String.format(Constant.STUDY_OPEN_FILE, id);
                Picasso.with(context).load(url).placeholder(R.mipmap.img_morentupian).config(Bitmap.Config.RGB_565).error(R.mipmap.img_morentupian).into(ret);
            }
        } else if (obj instanceof Uri) {
            Uri uri = (Uri) obj;
            Picasso.with(context).load(uri).placeholder(R.mipmap.img_morentupian).config(Bitmap.Config.RGB_565).error(R.mipmap.img_morentupian).into(ret);
        } else if (obj instanceof ImageState) {

        }
        container.addView(view);
        return view;
    }

    private void loadImage(final ImageView ret, final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                try {
                    Request request = new Request.Builder().url(url).build();
                    Response response = client.newCall(request).execute();
                    InputStream is = response.body().byteStream();
                    final Bitmap bm = BitmapFactory.decodeStream(is);
                    final String tag = (String) ret.getTag();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (tag.equals(url) && null != bm) {
                                ret.setImageBitmap(bm);
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void loadImage(final ScaleImageView ret, final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                try {
                    Request request = new Request.Builder().url(url).build();
                    Response response = client.newCall(request).execute();
                    InputStream is = response.body().byteStream();
                    final Bitmap bm = BitmapFactory.decodeStream(is);
                    final String tag = (String) ret.getTag();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (tag.equals(url) && null != bm) {
                                ret.setImageBitmap(bm);
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
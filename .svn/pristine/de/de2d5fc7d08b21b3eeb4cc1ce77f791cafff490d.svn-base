package net.iclassmate.bxyd.adapter.study;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.bean.study.FileDirList;
import net.iclassmate.bxyd.utils.BitmapUtils;

import java.util.List;

/**
 * Created by xydbj on 2016/6/8.
 */
public class NetFileAdapter extends BaseAdapter {
    private Context context;
    private List<FileDirList> list;
    private View.OnClickListener imgCheckClick;
    private LruCache<String, Bitmap> lruCache;
    private Handler mHandler = new Handler();


    public void setImgCheckClick(View.OnClickListener imgCheckClick) {
        this.imgCheckClick = imgCheckClick;
    }

    public NetFileAdapter(Context context, List<FileDirList> list) {
        this.context = context;
        this.list = list;
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int mCacheSize = maxMemory / 4;
        lruCache = new LruCache<String, Bitmap>(mCacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
    }

    @Override
    public int getCount() {
        int ret = 0;
        if (list != null) {
            ret = list.size();
        }
        return ret;
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.study_grid_netfile_item, null);
            holder = new ViewHolder();
            holder.img = (ImageView) view.findViewById(R.id.netfile_item_img);
            holder.img_check = (ImageView) view.findViewById(R.id.netfile_item_img_check);
            holder.tv = (TextView) view.findViewById(R.id.netfile_item_tv);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        FileDirList file = list.get(i);
        if (file.isCheck()) {
            holder.img_check.setImageResource(R.mipmap.ic_checked);
        } else {
            holder.img_check.setImageResource(R.mipmap.ic_weixuanze_wangpan);
        }
        holder.img_check.setTag(i);
        Bitmap bm1 = BitmapUtils.stringtoBitmap("");
        holder.img.setImageBitmap(bm1);
        holder.img.setImageResource(R.mipmap.ic_qita);

        holder.img_check.setOnClickListener(imgCheckClick);
        String name = file.getShortName();
        if (name.contains(".")) {
            name = name.substring(0, name.indexOf("."));
        }
        holder.tv.setText(name);
        int type = file.getType();
        String path = file.getFullPath();
        String url = file.getScale();
        holder.img.setTag(i);
        setImage(holder.img, type, path, url, i);
        if (type == 1) {
            holder.img_check.setVisibility(View.GONE);
        } else if (type == 2) {
            holder.img_check.setVisibility(View.VISIBLE);
        }
        return view;
    }

    private void setImage(final ImageView img, final int type, final String path, String url, final int tag) {
        int index = (int) img.getTag();
        if (index != tag) {
            return;
        }
        if (type == 1) {
            img.setImageResource(R.mipmap.ic_wenjianjia);
        } else if (type == 2) {
            String name = path;
            if (name.contains(".")) {
                name = name.substring(name.lastIndexOf(".") + 1, name.length());
            }
            name = name.toLowerCase();
            switch (name) {
                case "doc":
                case "docx":
                case "txt":
                case "wps":
                    img.setImageResource(R.mipmap.ic_wendang02);
                    break;
                case "pdf":
                    img.setImageResource(R.mipmap.ic_pdf);
                    break;
                case "wav":
                case "mp3":
                case "wma":
                case "wva":
                case "ogg":
                case "ape":
                case "aif":
                case "au":
                case "ram":
                case "mmf":
                case "amr":
                case "aac":
                case "flac":
                    img.setImageResource(R.mipmap.ic_yinpin02);
                    break;
                case "xls":
                case "xlsx":
                case "et":
                    img.setImageResource(R.mipmap.ic_biaoge02);
                    break;
                case "ppt":
                case "pptx":
                case "dps":
                    img.setImageResource(R.mipmap.ic_ppt);
                    break;
                case "avi":
                case "mpg":
                case "mpeg":
                case "mov":
                case "rm":
                case "rmvb":
                case "mp4":
                case "3gp":
                case "flv":
                case "wmv":
                case "bmp":
                case "gif":
                case "jpg":
                case "pic":
                case "png":
                case "tif":
                case "jpeg":
                    if (url != null) {
                        if (!url.contains("base64")) {
                            Picasso.with(context).load(url).placeholder(R.mipmap.img_morentupian).config(Bitmap.Config.RGB_565).into(img);
                        } else {
                            url = url.substring(url.indexOf("base64") + 7);
                            Bitmap bm = BitmapUtils.stringtoBitmap(url);
                            img.setImageBitmap(bm);
                        }
                    }
                    break;
                default:
                    img.setImageResource(R.mipmap.ic_qita);
                    break;
            }
        }

    }


    class ViewHolder {
        ImageView img, img_check;
        TextView tv;
    }
}

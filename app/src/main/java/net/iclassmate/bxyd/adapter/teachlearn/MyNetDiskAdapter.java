package net.iclassmate.bxyd.adapter.teachlearn;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.bean.netdisk.FileDirList;

import java.util.List;

/**
 * Created by xydbj on 2016.7.15.
 */
public class MyNetDiskAdapter extends BaseAdapter {
    private List<FileDirList> list;
    private Context context;
    private boolean selector_flag;
    private View.OnClickListener imgCheckClick;


    public MyNetDiskAdapter(Context context, List<FileDirList> list, boolean selector_flag) {
        this.list = list;
        this.context = context;
        this.selector_flag = selector_flag;
    }

    public MyNetDiskAdapter(Context context, List<FileDirList> list) {
        this.list = list;
        this.context = context;
    }

    public void updateList(List<FileDirList> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public View.OnClickListener getImgCheckClick() {
        return imgCheckClick;
    }

    public void setImgCheckClick(View.OnClickListener imgCheckClick) {
        this.imgCheckClick = imgCheckClick;
    }

    public boolean isSelector_flag() {
        return selector_flag;
    }

    public void setSelector_flag(boolean selector_flag) {
        this.selector_flag = selector_flag;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.mygridview_item, null);
            holder = new ViewHolder();
            holder.iv = (ImageView) convertView.findViewById(R.id.mygridview_item_iv);
            holder.tv = (TextView) convertView.findViewById(R.id.mygridview_item_tv);
            holder.iv_xuanze = (ImageView) convertView.findViewById(R.id.mygridview_item_iv_xuanze);
            convertView.setTag(holder);
        } else {
            Object tag = convertView.getTag();
            if (tag instanceof ViewHolder) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                convertView = LayoutInflater.from(context).inflate(R.layout.mygridview_item, null);
                holder = new ViewHolder();
                holder.iv = (ImageView) convertView.findViewById(R.id.mygridview_item_iv);
                holder.tv = (TextView) convertView.findViewById(R.id.mygridview_item_tv);
                holder.iv_xuanze = (ImageView) convertView.findViewById(R.id.mygridview_item_iv_xuanze);
                convertView.setTag(holder);
            }
        }
        FileDirList fileDirList = list.get(position);
        int type = fileDirList.getType();//区分文件或文件夹的标识
        String scale = fileDirList.getScale();//如果是图片一类的，利用scale拼接接口展示图片
        String fullPath = fileDirList.getFullPath();
        holder.iv.setTag(position);

        setImageBackground(holder.iv, type, fullPath, scale, position);//文件或文件夹的图片显示

        holder.tv.setText(fileDirList.getShortName());//文件或文件夹的名称

        //控制选择图片的显示和隐藏
        if (selector_flag == true) {
            if (type == 1) {
                holder.iv_xuanze.setVisibility(View.INVISIBLE);
            } else if (type == 2 || type == 3) {
                holder.iv_xuanze.setVisibility(View.VISIBLE);
            }
        } else if (selector_flag == false) {
            holder.iv_xuanze.setVisibility(View.INVISIBLE);
        }
        if (fileDirList.isCheck()) {
            holder.iv_xuanze.setImageResource(R.mipmap.ic_checked);
        } else {
            holder.iv_xuanze.setImageResource(R.mipmap.ic_weixuanze_wangpan);
        }
        holder.iv_xuanze.setOnClickListener(imgCheckClick);
        holder.iv_xuanze.setTag(position);

        return convertView;
    }

    class ViewHolder {
        TextView tv;
        ImageView iv, iv_xuanze;
    }

    public void setImageBackground(ImageView img, int type, String fullPath, String url, int tag) {
        int index = (int) img.getTag();
        if (index != tag) {
            return;
        }
        if (type == 1) {
            Picasso.with(context)
                    .load("null")
                    .config(Bitmap.Config.RGB_565)
                    .placeholder(R.mipmap.ic_wenjianjia)
                    .into(img);
        } else if (type == 2) {
            String name = fullPath;
            if (name.contains(".")) {
                name = name.substring(name.lastIndexOf(".") + 1, name.length());
                name = name.toLowerCase();
                switch (name) {
                    case "pdf":
                        img.setImageResource(R.mipmap.ic_pdf);
                        break;
                    case "doc":
                    case "docx":
                    case "txt":
                    case "wps":
                        img.setImageResource(R.mipmap.ic_wendang02);
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
                    case "bmp":
                    case "gif":
                    case "jpg":
                    case "pic":
                    case "png":
                    case "tif":
                    case "flv":
                    case "jpeg":
                        Picasso.with(context).load(url).placeholder(R.mipmap.img_morentupian).resize(106, 106).into(img);
                        break;
                    default:
                        img.setImageResource(R.mipmap.ic_qita);
                        break;
                }
            }
        } else if (type == 3) {

        }
    }
}
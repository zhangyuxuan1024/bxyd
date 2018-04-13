package net.iclassmate.bxyd.adapter.teachlearn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.bean.attention.Attention;
import net.iclassmate.bxyd.utils.BitmapUtils;

import java.io.File;
import java.util.List;

/**
 * Created by xydbj on 2016.7.17.
 */
public class FragmentTranAdapter extends BaseAdapter {
    private List<Attention> list;
    private Context context;
    private View.OnClickListener deleteChecked;

    public FragmentTranAdapter(Context context, List<Attention> list) {
        this.context = context;
        this.list = list;
    }

    public void updateView(List<Attention> list) {
        this.list = list;
        this.notifyDataSetChanged();
    }

    public View.OnClickListener getDeleteChecked() {
        return deleteChecked;
    }

    public void setDeleteChecked(View.OnClickListener deleteChecked) {
        this.deleteChecked = deleteChecked;
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
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_tran_listview, null);
            holder.tv = (TextView) convertView.findViewById(R.id.item_tran_listview_tv_name);
            holder.tv_time = (TextView) convertView.findViewById(R.id.item_tran_listview_tv_am);
            holder.iv = (ImageView) convertView.findViewById(R.id.item_tran_listview_iv);
            holder.item_tran_listview_fl_tv = (TextView) convertView.findViewById(R.id.item_tran_listview_fl_tv);
            holder.item_tran_listview_fl_iv = (ImageView) convertView.findViewById(R.id.item_tran_listview_fl_iv);
            holder.item_tran_listview_fl_iv.setOnClickListener(deleteChecked);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv.setText(list.get(position).getUserName());
        holder.tv_time.setText(list.get(position).getUserPinyin());

        int type = list.get(position).getIndex();
        String fullPath = list.get(position).getSubSpaceId();
        String url = list.get(position).getUserIcon();
//        Log.i("info", "下载列表的缩略图：" + url);
        holder.iv.setTag(position);
        setImageBackground(holder.iv, type, fullPath, url, position);
        if (list.get(position).isDownload()) {
            holder.item_tran_listview_fl_iv.setVisibility(View.VISIBLE);
            holder.item_tran_listview_fl_tv.setVisibility(View.INVISIBLE);
        } else {
            holder.item_tran_listview_fl_iv.setVisibility(View.INVISIBLE);
            holder.item_tran_listview_fl_tv.setVisibility(View.VISIBLE);
        }
        holder.item_tran_listview_fl_iv.setTag(position);

        return convertView;
    }

    public void delete(int position) {
        String url = list.get(position).getUserIcon();
        File file = new File(url);
        if (file == null || !file.exists() || file.isDirectory()) {
            return;
        }
        file.delete();
    }

    static class ViewHolder {
        TextView tv, tv_time, item_tran_listview_fl_tv;
        ImageView iv, item_tran_listview_fl_iv;
    }

    public void setImageBackground(ImageView img, int type, String fullPath, String url, int tag) {
        int index = (int) img.getTag();
        if (index != tag) {
            return;
        }
        if (type == 1) {
            img.setImageResource(R.mipmap.ic_wenjianjia);
        } else if (type == 2) {
            String name = fullPath;
            if (name.contains(".")) {
                name = name.substring(name.indexOf(".") + 1, name.length());
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
                    case "jpeg":
                        name = BitmapUtils.getImageUrl(url);
                        if (name.contains("xyddownload")) {
                            File file = new File(name);
                            Picasso.with(context).load(file).placeholder(R.mipmap.img_morentupian).resize(106, 106).into(img);
                        } else {
                            Picasso.with(context).load(url).placeholder(R.mipmap.img_morentupian).resize(106, 106).into(img);
                        }                        break;
                    default:
                        img.setImageResource(R.mipmap.ic_qita);
                        break;
                }
            }
        } else if (type == 3) {

        }
    }
}

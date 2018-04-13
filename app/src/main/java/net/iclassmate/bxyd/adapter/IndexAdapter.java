package net.iclassmate.bxyd.adapter;

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
import net.iclassmate.bxyd.bean.index.Recommend;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by xydbj on 2016/11/14.
 */
public class IndexAdapter extends BaseAdapter {
    private Context context;
    private List<Recommend> list;
    public static final int NUM = 2;
    public static final int JIAGE = 0;
    public static final int ZIXUN = 1;

    public IndexAdapter(Context context, List<Recommend> list) {
        this.context = context;
        this.list = list;
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
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return NUM;
    }

    @Override
    public int getItemViewType(int position) {
        int result = 0;
        if (list.get(position).getType() == 1) {
            result = JIAGE;
        } else if (list.get(position).getType() == 2) {
            result = ZIXUN;
        }
        return result;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        ViewHolder2 holder2 = null;

        int type = list.get(position).getType();

        if (type == 1){
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_index_main, parent, false);
                holder = new ViewHolder();
                holder.img_main = (ImageView) convertView.findViewById(R.id.item_index_img);
                holder.tv_name = (TextView) convertView.findViewById(R.id.item_index_tv_name);
                holder.tv_loaction = (TextView) convertView.findViewById(R.id.item_index_tv_location);
                holder.tv_time = (TextView) convertView.findViewById(R.id.item_index_tv_time);
                holder.tv_money = (TextView) convertView.findViewById(R.id.item_index_tv_money);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Recommend recommend = list.get(position);
            Picasso.with(context).load(recommend.getImageUrl()).placeholder(R.mipmap.img_moren_banner).error(R.mipmap.img_moren_banner)
                    .config(Bitmap.Config.RGB_565)
                    .into(holder.img_main);
            holder.tv_name.setText(recommend.getName());
            holder.tv_loaction.setText(recommend.getAddress());
            SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy年MM月dd日");
            String start_time = dateFormater.format(recommend.getStart_time());
            String end_time = dateFormater.format(recommend.getEnd_time());
            holder.tv_time.setText(start_time + " - " + end_time);
            holder.tv_money.setText("￥" + String.format("%.2f", recommend.getPrice()));
        } else if (type == 2){
            if (convertView == null){
                convertView = LayoutInflater.from(context).inflate(R.layout.item_index_main2,null);
                holder2 = new ViewHolder2();
                holder2.img_main2 = (ImageView) convertView.findViewById(R.id.item_index_img2);
                holder2.tv_name2 = (TextView) convertView.findViewById(R.id.item_index_tv_name2);
                holder2.tv_zixun = (TextView) convertView.findViewById(R.id.item_index_tv_zixun);
                convertView.setTag(holder2);
            }else {
                holder2 = (ViewHolder2) convertView.getTag();
            }
            Picasso.with(context)
                    .load(list.get(position).getImageUrl())
                    .placeholder(R.mipmap.img_moren_banner)
                    .error(R.mipmap.img_moren_banner)
                    .config(Bitmap.Config.RGB_565)
                    .into(holder2.img_main2);
            holder2.tv_name2.setText(list.get(position).getName());
        }
        return convertView;
    }

    class ViewHolder {
        ImageView img_main;
        TextView tv_name, tv_loaction, tv_time, tv_money;
    }

    class ViewHolder2 {
        ImageView img_main2;
        TextView tv_name2, tv_zixun;
    }
}

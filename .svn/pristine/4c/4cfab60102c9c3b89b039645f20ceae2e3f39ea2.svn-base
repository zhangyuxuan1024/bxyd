package net.iclassmate.bxyd.adapter.teachlearn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.bean.netdisk.VideoInfo;

import java.util.List;

/**
 * Created by xydbj on 2016.9.22.
 */
public class AllVideoAdapter extends BaseAdapter {

    private Context context;
    private List<VideoInfo> list;
    private View.OnClickListener onSelectedListener;

    public AllVideoAdapter(Context context, List<VideoInfo> list) {
        this.context = context;
        this.list = list;
    }

    public View.OnClickListener getOnSelectedListener() {
        return onSelectedListener;
    }

    public void setOnSelectedListener(View.OnClickListener onSelectedListener) {
        this.onSelectedListener = onSelectedListener;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_allvideo, null);
            holder = new ViewHolder();
            holder.tv_time = (TextView) convertView.findViewById(R.id.item_allvideo_alltime);
            holder.tv_size = (TextView) convertView.findViewById(R.id.item_allvideo_size);
            holder.img_thumb = (ImageView) convertView.findViewById(R.id.item_allvideo_thumb);
            holder.img_selected = (ImageView) convertView.findViewById(R.id.item_allvideo_selected);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_time.setText(list.get(position).getAllTime() + "");
        holder.tv_size.setText(list.get(position).getAllSize() + "");
        holder.img_thumb.setImageBitmap(list.get(position).getBitmapThumb());
        holder.img_selected.setOnClickListener(onSelectedListener);
        holder.img_selected.setTag(position);
        if (list.get(position).isSelected()){
            holder.img_selected.setImageResource(R.mipmap.ic_selected);
        } else {
            holder.img_selected.setImageResource(R.mipmap.ic_unselected);
        }
        return convertView;
    }

    static class ViewHolder {
        TextView tv_time, tv_size;
        ImageView img_thumb, img_selected;
    }
}

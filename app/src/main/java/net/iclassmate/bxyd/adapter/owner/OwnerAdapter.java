package net.iclassmate.bxyd.adapter.owner;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import net.iclassmate.bxyd.R;

import java.util.List;
import java.util.Map;

/**
 * Created by xydbj on 2016.6.13.
 */
public class OwnerAdapter extends BaseAdapter {
    private List<Map<String,Object>> list;
    private Context context;
    public OwnerAdapter(Context context,List<Map<String,Object>> list){
        this.context = context;
        this.list = list;
    }
    @Override
    public int getCount() {
        return list.size() ;
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
        if(convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.items_owner_options,null);
            holder.imageView = (ImageView) convertView.findViewById(R.id.items_owner_options_iv);
            holder.textView = (TextView) convertView.findViewById(R.id.items_owner_options_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.imageView.setImageResource((Integer) list.get(position).get("img"));
        holder.textView.setText((String) list.get(position).get("text"));
        return convertView;
    }
    static class ViewHolder{
        public ImageView imageView;
        public TextView textView;
    }
}

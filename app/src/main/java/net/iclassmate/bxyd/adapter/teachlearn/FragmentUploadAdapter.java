package net.iclassmate.bxyd.adapter.teachlearn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.bean.UploadFile;

import java.util.List;

/**
 * Created by xydbj on 2016.10.21.
 */
public class FragmentUploadAdapter extends BaseAdapter {

    private List<UploadFile> list;
    private Context context;

    public FragmentUploadAdapter(Context context,List<UploadFile> list){
        this.context = context;
        this.list = list;
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
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_tran_listview,null);
            holder = new ViewHolder();
            holder.tv_title = (TextView) convertView.findViewById(R.id.item_tran_listview_tv_name);
            holder.iv_thumb = (ImageView) convertView.findViewById(R.id.item_tran_listview_iv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_title.setText(list.get(position).getFileName());
        holder.iv_thumb.setImageBitmap(list.get(position).getFileBitmapIcon());

        return convertView;
    }

    static class ViewHolder{
        ImageView iv_thumb;
        ImageView iv_delete;
        TextView tv_title;
        TextView tv_time;
    }

}
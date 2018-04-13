package net.iclassmate.bxyd.adapter.study;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.bean.study.NoticMessage;
import net.iclassmate.bxyd.view.study.CustomImageView;
import net.iclassmate.bxyd.view.study.ShapeImageView;

import java.util.List;

/**
 * Created by xydbj on 2016/6/4.
 */
public class MessageAdapter extends BaseAdapter {
    private Context context;
    private List<NoticMessage> list;

    public MessageAdapter(Context context, List<NoticMessage> list) {
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
            view = LayoutInflater.from(context).inflate(R.layout.study_message_listview_item, null);
            holder = new ViewHolder();
            holder.tv_name = (TextView) view.findViewById(R.id.message_item_center_name);
            holder.tv_content = (TextView) view.findViewById(R.id.message_item_center_content);
            holder.tv_time = (TextView) view.findViewById(R.id.message_item_center_time);
            holder.img_left = (ShapeImageView) view.findViewById(R.id.message_item_left_img);
            holder.img_right = (CustomImageView) view.findViewById(R.id.message_item_right_img);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        NoticMessage message = list.get(i);
        holder.tv_name.setText(message.getName());
        holder.tv_content.setText(message.getContent());
        holder.tv_time.setText(message.getTime());
        Picasso.with(context).load(message.getPath_left()).into(holder.img_left);
        Picasso.with(context).load(message.getPath_right()).into(holder.img_right);
        return view;
    }

    class ViewHolder {
        TextView tv_name, tv_content, tv_time;
        ShapeImageView img_left;
        CustomImageView img_right;
    }
}

package net.iclassmate.bxyd.adapter.study;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.bean.study.comment.CommentMessageItem;
import net.iclassmate.bxyd.utils.FileUtils;
import net.iclassmate.bxyd.view.study.ShapeImageView;

import java.util.List;

/**
 * Created by xydbj on 2016/6/15.
 */
public class CommentAdapter extends BaseAdapter {
    private Context context;
    private List<CommentMessageItem> list;
    private View.OnClickListener onHeadClick;

    public void setOnHeadClick(View.OnClickListener onHeadClick) {
        this.onHeadClick = onHeadClick;
    }

    public CommentAdapter(Context context, List<CommentMessageItem> list) {
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
            view = LayoutInflater.from(context).inflate(R.layout.study_comment_item, null);
            holder = new ViewHolder();
            holder.img = (ShapeImageView) view.findViewById(R.id.comment_item_img);
            holder.tv_name = (TextView) view.findViewById(R.id.comment_item_tv_name);
            holder.tv_com = (TextView) view.findViewById(R.id.comment_item_tv_com);
            holder.tv_time = (TextView) view.findViewById(R.id.comment_item_tv_time);
            holder.tv_reply = (TextView) view.findViewById(R.id.comment_item_tv_reply);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        CommentMessageItem message = list.get(i);
        String name = message.getCreateBy().getName();
        if (name.contains("回复")) {
            int index = name.indexOf("回复");
            SpannableStringBuilder style = new SpannableStringBuilder(name);
            style.setSpan(new ForegroundColorSpan(Color.parseColor("#65caff")), 0, index, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            style.setSpan(new ForegroundColorSpan(Color.parseColor("#8c8c98")), index, index + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            style.setSpan(new ForegroundColorSpan(Color.parseColor("#65caff")), index + 2, name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.tv_name.setText(style);
        } else {
            holder.tv_name.setTextColor(Color.parseColor("#65caff"));
            holder.tv_name.setText(name);
        }
        holder.tv_com.setText(message.getContent());
        holder.tv_time.setText(FileUtils.getTime(message.getCreatedOn()));
        holder.img.setImageResource(R.mipmap.ic_geren_xuanren);
        String path = message.getCreateBy().getAvatar();
        if (path != null) {
            String type = message.getCreateBy().getType();
            if (type != null && type.equals("org")) {
                Picasso.with(context).load(path).placeholder(R.drawable.ic_jigou_zhuyedongtai).error(R.drawable.ic_jigou_zhuyedongtai).resize(106, 106).into(holder.img);
            } else {
                Picasso.with(context).load(path).placeholder(R.drawable.ic_geren_xuanren).error(R.drawable.ic_geren_xuanren).resize(106, 106).into(holder.img);
            }
        }
        holder.tv_reply.setVisibility(View.GONE);
        holder.img.setTag(message);
        holder.img.setOnClickListener(onHeadClick);
        return view;
    }

    class ViewHolder {
        ShapeImageView img;
        TextView tv_name, tv_com, tv_time, tv_reply;
    }
}

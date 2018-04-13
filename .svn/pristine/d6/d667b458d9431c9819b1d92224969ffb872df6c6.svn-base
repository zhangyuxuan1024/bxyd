package net.iclassmate.bxyd.adapter.study;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.iclassmate.bxyd.R;

import java.util.List;

/**
 * Created by xydbj on 2016/6/4.
 */
public class WindowAdapter extends BaseAdapter {
    private Context context;
    private List<String> list;

    public WindowAdapter(Context context, List<String> list) {
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
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        int ret = 0;
        if (list.get(position).contains("是否确认删除文件删除")) {
            ret = 1;
        }
        return ret;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (getItemViewType(i) == 0) {
            view = bindTv(i, view, viewGroup);
        } else if (getItemViewType(i) == 1) {
            view = bindDel(i, view, viewGroup);
        }
        return view;
    }

    public View bindTv(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.study_window_listview_item, null);
            holder = new ViewHolder();
            holder.tv = (TextView) view.findViewById(R.id.study_window_tv);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.tv.setText(list.get(i));
        return view;
    }

    public View bindDel(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.study_window_listview_item_del, null);
            holder = new ViewHolder();
            holder.tv = (TextView) view.findViewById(R.id.study_window_tv_del);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        String str = list.get(i);

        if (!str.contains("删除")) {
            return null;
        }
        int index = str.lastIndexOf("删除");
        str = str.substring(0, index) + "\r\n" + str.substring(index, str.length());
        SpannableStringBuilder style = new SpannableStringBuilder(str);
        style.setSpan(new ForegroundColorSpan(Color.parseColor("#8c8c98")), 0, index, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        style.setSpan(new AbsoluteSizeSpan((int) context.getResources().getDimension(R.dimen.tv_11)), 0, index, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        style.setSpan(new ForegroundColorSpan(Color.parseColor("#f43531")), index, str.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        style.setSpan(new AbsoluteSizeSpan((int) context.getResources().getDimension(R.dimen.tv_15)), index, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.tv.setText(style);
        return view;
    }

    class ViewHolder {
        TextView tv;
    }
}

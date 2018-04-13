package net.iclassmate.bxyd.adapter.owner;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.bean.attention.Attention;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.ui.activitys.owner.HomePageActivity;
import net.iclassmate.bxyd.ui.activitys.owner.UnfollowActivity;
import net.iclassmate.bxyd.view.CircleImageView;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xydbj on 2016.7.2.
 */
public class AttentionAdapter extends BaseAdapter implements SectionIndexer, View.OnClickListener {

    private List<Attention> list;
    private Context context;
    private int index;

    public AttentionAdapter() {

    }

    public AttentionAdapter(Context context, List<Attention> list) {
        this.context = context;
        this.list = list;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param list
     */
    public void updateListView(List<Attention> list) {
        this.list = list;
        notifyDataSetChanged();
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
        final Attention attention = list.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_attention_name, null);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.item_attention_name_tv);
            holder.tvLetter = (TextView) convertView.findViewById(R.id.item_attention_pinyin_tv);
            holder.circleiv = (CircleImageView) convertView.findViewById(R.id.item_attention_name_iv);
            holder.iv_cancel = (ImageView) convertView.findViewById(R.id.item_attention_name_iv_cancel);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);
        // 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            holder.tvLetter.setVisibility(View.VISIBLE);
            holder.tvLetter.setText(attention.getUserPinyin());
        } else {
            holder.tvLetter.setVisibility(View.GONE);
        }
        holder.tvTitle.setText(attention.getUserName());
        holder.tvTitle.setOnClickListener(this);
        holder.tvTitle.setTag(attention);
        attention.setIndex(position);
        if (attention.getType().equals("person")) {
            Picasso.with(context)
                    .load(attention.getUserIcon())
                    .placeholder(R.mipmap.ic_geren_guanzhu)
                    .error(R.mipmap.ic_geren_guanzhu)
                    .into(holder.circleiv);
        } else if (attention.getType().equals("group")) {
            Picasso.with(context)
                    .load(attention.getUserIcon())
                    .placeholder(R.mipmap.ic_qunzu_guanzhu)
                    .error(R.mipmap.ic_qunzu_guanzhu)
                    .into(holder.circleiv);
        } else if (attention.getType().equals("org")) {
            Picasso.with(context)
                    .load(attention.getUserIcon())
                    .placeholder(R.mipmap.ic_jigou_guanzhu)
                    .error(R.mipmap.ic_jigou_guanzhu)
                    .into(holder.circleiv);
        }
        holder.iv_cancel.setTag(attention);
        holder.iv_cancel.setOnClickListener(this);

        return convertView;
    }

    @Override
    public void onClick(View v) {
        Attention attention = (Attention) v.getTag();
        switch (v.getId()) {
            case R.id.item_attention_name_tv:
                Intent intent_see = new Intent(context, HomePageActivity.class);
                intent_see.putExtra(Constant.ID_SPACE, attention.getSubSpaceId());
                intent_see.putExtra(Constant.ID_USER, attention.getOwnerId());
                intent_see.putExtra(Constant.HOME_PAGE_TITLE, attention.getUserName());
                if (attention.getType().equals("org")) {
                    intent_see.putExtra(Constant.ID_USERTYPE, 0);
                } else if (attention.getType().equals("person")) {
                    intent_see.putExtra(Constant.ID_USERTYPE, 1);
                } else if (attention.getType().equals("group")) {
                    intent_see.putExtra(Constant.ID_USERTYPE, 2);
                }
                context.startActivity(intent_see);
                break;
            case R.id.item_attention_name_iv_cancel:
                Intent intent_unfollow = new Intent(context, UnfollowActivity.class);
                intent_unfollow.putExtra("uuid", attention.getUuid());
                intent_unfollow.putExtra("list", (Serializable) list);
                intent_unfollow.putExtra("index", attention.getIndex());
                context.startActivity(intent_unfollow);
                break;
        }
    }

    final static class ViewHolder {
        TextView tvLetter;
        TextView tvTitle;
        CircleImageView circleiv;//关注列表的头像
        ImageView iv_cancel;
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    @Override
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getUserPinyin();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    @Override
    public int getSectionForPosition(int position) {
        return list.get(position).getUserPinyin().charAt(0);
    }

    /**
     * 提取英文的首字母，非英文字母用#代替。
     *
     * @param str
     * @return
     */
    private String getAlpha(String str) {
        String sortStr = str.trim().substring(0, 1).toUpperCase();
        // 正则表达式，判断首字母是否是英文字母
        if (sortStr.matches("[A-Z]")) {
            return sortStr;
        } else {
            return "#";
        }
    }
}
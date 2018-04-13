package net.iclassmate.bxyd.adapter.teachlearn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.bean.JGName;
import net.iclassmate.bxyd.bean.netdisk.Responses;
import net.iclassmate.bxyd.view.CircleImageView;

import java.util.List;

/**
 * Created by xydbj on 2016.7.5.
 */
public class JiGouDiskAdapter extends BaseAdapter/* implements OnClickListener*/ {

    private Context context;
    private List<Object> list;
    private List<JGName> adminList,adminqzList;
    private int type;
    private View.OnClickListener imgClick;

    public OnClickListener getImgClick() {
        return imgClick;
    }

    public void setImgClick(OnClickListener imgClick) {
        this.imgClick = imgClick;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public JiGouDiskAdapter(Context context, List<Object> list,List<JGName> adminList,List<JGName> adminqzList) {
        this.context = context;
        this.list = list;
        this.adminList = adminList;
        this.adminqzList = adminqzList;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_jigouwangpan_content, null);
            holder = new ViewHolder();
            holder.tv = (TextView) convertView.findViewById(R.id.jigouwangpan_content_tv);
            holder.iv = (CircleImageView) convertView.findViewById(R.id.jigouwangpan_content_iv);
            holder.jigouwangpan_content_tv_admin = (TextView) convertView.findViewById(R.id.jigouwangpan_content_tv_admin);
            holder.rl = (RelativeLayout) convertView.findViewById(R.id.jigouwangpan_content_rl);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.rl.setOnClickListener(imgClick);
        holder.rl.setTag(position);
        Object object = list.get(position);
        if (type == 1) {
            holder.iv.setImageResource(R.mipmap.ic_jigoumoren_wangpan);
            String admin = adminList.get(position).getAdminName().toString();
            admin = admin.substring(1,admin.length()-1);
            holder.jigouwangpan_content_tv_admin.setText("管理员：" + admin);
            if (object instanceof Responses) {
                Responses responses = (Responses) object;
                holder.tv.setText(responses.getName());
                Picasso.with(context)
                        .load(/*Constant.ADDRESS_STUDY + */responses.getIcon())
                        .error(R.mipmap.ic_jigoumoren_wangpan)
                        .placeholder(R.mipmap.ic_jigoumoren_wangpan)
                        .resize(80, 80)
                        .centerCrop()
                        .into(holder.iv);
                holder.rl.setOnClickListener(imgClick);
                holder.rl.setTag(responses);
            }
        } else if (type == 2) {
            holder.iv.setImageResource(R.mipmap.ic_qunzu_wangpan);
            String admin = adminqzList.get(position).getAdminName().toString();
            admin = admin.substring(1,admin.length()-1);
            holder.jigouwangpan_content_tv_admin.setText("管理员：" + admin);
            if (object instanceof Responses) {
                Responses responses = (Responses) object;
                holder.tv.setText(responses.getName());
                Picasso.with(context)
                        .load(/*Constant.ADDRESS_STUDY + */responses.getIcon())
                        .error(R.mipmap.ic_qunzu_wangpan)
                        .placeholder(R.mipmap.ic_qunzu_wangpan)
                        .resize(80, 80)
                        .centerCrop()
                        .into(holder.iv);
                holder.rl.setOnClickListener(imgClick);
                holder.rl.setTag(responses);
            }
        }

        return convertView;
    }

    static class ViewHolder {
        TextView tv,jigouwangpan_content_tv_admin;
        CircleImageView iv;
        RelativeLayout rl;
    }
}

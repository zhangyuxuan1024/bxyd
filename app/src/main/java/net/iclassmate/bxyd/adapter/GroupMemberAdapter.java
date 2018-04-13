package net.iclassmate.bxyd.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.bean.contacts.GroupMember;
import net.iclassmate.bxyd.utils.HttpManager;
import net.iclassmate.bxyd.utils.NetWorkUtils;
import net.iclassmate.bxyd.view.study.ShapeImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xyd on 2016/8/5.
 */
public class GroupMemberAdapter extends BaseAdapter
{
    private List<GroupMember> list = new ArrayList<GroupMember>();
    private Context context;
    private HttpManager httpManager;
    private Handler mHandler = new Handler();

    public GroupMemberAdapter(List<GroupMember> list, Context context) {
        this.list = list;
        this.context = context;
        httpManager = new HttpManager();
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
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        if(convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_information_gridview,null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.group_information_name);
            holder.iv = (ShapeImageView) convertView.findViewById(R.id.group_information_icon_siv);
            convertView.setTag(holder);
        }else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        GroupMember groupMember = list.get(position);
        if(groupMember.getUserId().equals("add"))
        {
//            holder.name.setVisibility(View.GONE);
            holder.name.setText("");
            holder.iv.setBackgroundResource(R.mipmap.ic_add_xiangqing);
        }else if(groupMember.getUserId().equals("exit"))
        {
//            holder.name.setVisibility(View.GONE);
            holder.name.setText("");
            holder.iv.setBackgroundResource(R.mipmap.ic_minus);
        }else
        {
//            holder.name.setVisibility(View.VISIBLE);
            String name = groupMember.getRemark();
            if(name == null || TextUtils.isEmpty(name)){
                name = groupMember.getUserName();
            }
            holder.name.setText(name);
            if(groupMember.getIcon() != null && !groupMember.getIcon().isEmpty()) {
                Picasso.with(context).load(groupMember.getIcon()).resize((int) context.getResources().getDimension(R.dimen.view_43),
                        (int) context.getResources().getDimension(R.dimen.view_43))
                        .placeholder(R.mipmap.moren_geren_xiaoxi).error(R.mipmap.moren_geren_xiaoxi).config(Bitmap.Config.RGB_565).into(holder.iv);
            }else
            {
                holder.iv.setTag(groupMember.getUserId());
                setHeadIcon(groupMember.getUserId(), holder.iv, groupMember);
            }
        }
        return convertView;
    }

    private void setHeadIcon(final String userId, final ShapeImageView icon, final GroupMember groupMember) {
        if (!NetWorkUtils.isNetworkAvailable(context)) {
            setImage("null", icon);
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                final String url = httpManager.getUserIconUrl(userId);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        String tag = (String) icon.getTag();
                        if (url != null && !url.equals("") && tag.equals(userId)) {
                            groupMember.setIcon(url);
                            setImage(url,  icon);
                        } else {
                            setImage("null", icon);
                        }
                    }
                });
            }
        }).start();
    }

    private void setImage(String iconUrl, ShapeImageView icon) {
        //ChatType  0 单聊，1 群聊，2 空间(群组)
        if (iconUrl == null || iconUrl.equals("")) {
            iconUrl = "null";
        }
            Picasso.with(context).load(iconUrl).resize((int) context.getResources().getDimension(R.dimen.view_43),
                    (int) context.getResources().getDimension(R.dimen.view_43))
                    .placeholder(R.mipmap.moren_geren_xiaoxi).error(R.mipmap.moren_geren_xiaoxi).config(Bitmap.Config.RGB_565).into(icon);

    }

    class ViewHolder
    {
        TextView name;
        ShapeImageView iv;
    }
}

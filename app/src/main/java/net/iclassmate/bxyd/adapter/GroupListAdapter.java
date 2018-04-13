package net.iclassmate.bxyd.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.bean.contacts.Group;
import net.iclassmate.bxyd.bean.contacts.GroupInfo;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.utils.HttpManager;

/**
 * Created by xyd on 2016/7/5.
 */
public class GroupListAdapter extends BaseAdapter {
    private Context mContext;
    private Group group;
    private Handler mHandler = new Handler();
    private HttpManager httpManager;
    private LruCache<String, String> lruCache;

    public GroupListAdapter(Context mContext, Group group) {
        this.mContext = mContext;
        this.group = group;
        httpManager = new HttpManager();
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int mCacheSize = maxMemory / 4;
        lruCache = new LruCache<String, String>(mCacheSize) {
            @Override
            protected int sizeOf(String key, String value) {
                return value.getBytes().length;
            }
        };
    }

    @Override
    public int getCount() {
        return group.getList().size();
    }

    @Override
    public Object getItem(int position) {
        return group.getList().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.contacts_item, null);
            holder = new ViewHolder();
            holder.tvName = (TextView) convertView.findViewById(R.id.contacts_person_name);
            holder.imageIcon = (ImageView) convertView.findViewById(R.id.contacts_person_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        GroupInfo groupInfo = group.getList().get(position);
        holder.tvName.setText(groupInfo.getSessionName());

        int sessionType = groupInfo.getSessionType();
        String sessionId = groupInfo.getSessionId();
        String url = groupInfo.getSessionIcon();
//        String url = lruCache.get(sessionId);
//        if (url == null || url.equals("")) {
//            holder.imageIcon.setTag(sessionId);
//            getUserIcon(holder.imageIcon, sessionId, sessionType);
//        } else {
        setImage(url, sessionType, holder.imageIcon);
        //}
        return convertView;
    }

    private void getUserIcon(final ImageView imageIcon, final String userId, final int sessionType) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String url = httpManager.getUserIconUrl(userId);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        String tag = (String) imageIcon.getTag();
                        if (tag.equals(userId)) {
                            lruCache.put(tag, url);
                            setImage(url, sessionType, imageIcon);
                        }
                    }
                });
            }
        }).start();
    }

    private void setImage(String url, int sessionType, ImageView imageIcon) {
        if (sessionType == 2) {
            if (url == null || url.equals("")) {
                imageIcon.setImageResource(R.mipmap.ic_qunliao);
            } else {
                Picasso.with(mContext).load(url).resize((int) mContext.getResources().getDimension(R.dimen.view_43),
                        (int) mContext.getResources().getDimension(R.dimen.view_43))
                        .placeholder(R.mipmap.ic_qunliao).error(R.mipmap.ic_qunliao).config(Bitmap.Config.RGB_565).into(imageIcon);
            }
        } else if (sessionType == 3) {
            if (url == null || url.equals("")) {
                imageIcon.setImageResource(R.mipmap.ic_qunzu_xuanren);
            } else {
                Picasso.with(mContext).load(url).resize((int) mContext.getResources().getDimension(R.dimen.view_43),
                        (int) mContext.getResources().getDimension(R.dimen.view_43))
                        .placeholder(R.mipmap.ic_qunzu_xuanren).error(R.mipmap.ic_qunzu_xuanren).config(Bitmap.Config.RGB_565).into(imageIcon);
            }
        } else if (sessionType == 1) {
            if (url == null || url.equals("")) {
                imageIcon.setImageResource(R.mipmap.ic_qunzu_xuanren);
            } else {
                Picasso.with(mContext).load(url).placeholder(R.mipmap.ic_qunzu_xuanren).error(R.mipmap.ic_qunzu_xuanren).config(Bitmap.Config.RGB_565).into(imageIcon);
            }
        }
    }

    class ViewHolder {
        TextView tvName;
        ImageView imageIcon;
    }
}

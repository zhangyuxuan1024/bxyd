package net.iclassmate.bxyd.adapter.study;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.bean.study.fri.Fri;
import net.iclassmate.bxyd.bean.study.group.GroupItem;
import net.iclassmate.bxyd.utils.HttpManager;
import net.iclassmate.bxyd.view.study.ShapeImageView;

import java.util.List;

/**
 * Created by xydbj on 2016/6/16.
 */
public class TraFriAdapter extends BaseAdapter {
    private Context context;
    private List<Object> list;

    private View.OnClickListener imgCheckImg;
    private Handler mHandler = new Handler();
    private HttpManager httpManager;
    private LruCache<String, String> lruCache;

    public void setImgCheckImg(View.OnClickListener imgCheckImg) {
        this.imgCheckImg = imgCheckImg;
    }

    public TraFriAdapter(Context context, List<Object> list) {
        this.context = context;
        this.list = list;
        httpManager = new HttpManager();
        int maxSize = (int) Runtime.getRuntime().maxMemory();
        maxSize = maxSize / 4;
        lruCache = new LruCache<String, String>(maxSize) {
            @Override
            protected int sizeOf(String key, String value) {
                return value.getBytes().length;
            }
        };
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
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        int ret = 0;
        Object object = list.get(position);
        if (object instanceof Fri) {
            ret = 0;
        } else if (object instanceof String) {
            ret = 1;
        } else if (object instanceof GroupItem) {
            ret = 2;
        }
        return ret;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (getItemViewType(i) == 0) {
            view = bindContent(i, view, viewGroup);
        } else if (getItemViewType(i) == 1) {
            view = bindTitle(i, view, viewGroup);
        } else if (getItemViewType(i) == 2) {
            view = bindContentGroup(i, view, viewGroup);
        }
        return view;
    }

    public View bindContent(int i, View view, ViewGroup viewGroup) {
        ViewContentHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.tra_fri_listview_item_content, null);
            holder = new ViewContentHolder();
            holder.img_check = (ImageView) view.findViewById(R.id.tra_fri_check_img);
            holder.img = (ShapeImageView) view.findViewById(R.id.tra_fri_head_img);
            holder.tv = (TextView) view.findViewById(R.id.tra_fri_name_tv);
            holder.linearLayout = (LinearLayout) view.findViewById(R.id.tra_fri_linear);
            view.setTag(holder);
        } else {
            holder = (ViewContentHolder) view.getTag();
        }
        Object object = list.get(i);
        Fri fri = null;
        if (object instanceof Fri) {
            fri = (Fri) object;
            String name = fri.getRemark();
            if (name == null || name.equals("") || name.equals("null")) {
                name = fri.getUserName();
            }
            holder.tv.setText(name);
            setUImage(fri.getFriendId(), 0, holder.img);

            if (fri.isCheck()) {
                holder.img_check.setImageResource(R.mipmap.ic_checked);
            } else {
                holder.img_check.setImageResource(R.mipmap.ic_circle);
            }
            holder.img_check.setTag(i);
            holder.img_check.setOnClickListener(imgCheckImg);
            holder.linearLayout.setTag(i);
            holder.linearLayout.setOnClickListener(imgCheckImg);
        }
        return view;
    }

    public View bindTitle(int i, View view, ViewGroup viewGroup) {
        ViewTitleHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.tra_fri_listview_item_title, null);
            holder = new ViewTitleHolder();
            holder.tv = (TextView) view.findViewById(R.id.tra_fri_title_tv);
            view.setTag(holder);
        } else {
            holder = (ViewTitleHolder) view.getTag();
        }
        Object object = list.get(i);
        String ret = (String) object;
        holder.tv.setText(ret);
        return view;
    }

    public View bindContentGroup(int i, View view, ViewGroup viewGroup) {
        ViewContentHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.tra_fri_listview_item_content, null);
            holder = new ViewContentHolder();
            holder.img_check = (ImageView) view.findViewById(R.id.tra_fri_check_img);
            holder.img = (ShapeImageView) view.findViewById(R.id.tra_fri_head_img);
            holder.tv = (TextView) view.findViewById(R.id.tra_fri_name_tv);
            holder.linearLayout = (LinearLayout) view.findViewById(R.id.tra_fri_linear);
            view.setTag(holder);
        } else {
            holder = (ViewContentHolder) view.getTag();
        }
        Object object = list.get(i);
        GroupItem item = null;
        if (object instanceof GroupItem) {
            item = (GroupItem) object;
            holder.tv.setText(item.getSessionName());
            int sessionType = item.getSessionType();
            if (sessionType == 2) {
                setUImage(item.getSessionIcon(), 1, holder.img);
            } else if (sessionType == 3) {
                setUImage(item.getSessionIcon(), 2, holder.img);
            } else if (sessionType == 1) {
                setUImage(item.getSessionIcon(), 0, holder.img);
            }
            holder.img_check.setVisibility(View.GONE);
//            holder.img_check.setTag(i);
//            holder.img_check.setOnClickListener(imgCheckImg);
//            holder.linearLayout.setTag(i);
//            holder.linearLayout.setOnClickListener(imgCheckImg);
        }
        return view;
    }

    public void setUImage(String uid, int type, ShapeImageView img) {
        if (type == 1 || type == 2) {
            setImage(uid, type, img);
            return;
        }

        String url = lruCache.get(uid);
        if (url == null || url.equals("") || !url.contains("http") || url.equals("404")) {
            img.setTag(uid);
            getUserIcon(img, uid, type);
        } else {
            setImage(url, type, img);
        }
    }

    private void setImage(String url, int type, ShapeImageView img) {
        if (type == 0) {
            if (url != null && !url.equals("")) {
                Picasso.with(context).load(url).resize((int) context.getResources().getDimension(R.dimen.view_34),
                        (int) context.getResources().getDimension(R.dimen.view_34))
                        .placeholder(R.mipmap.ic_head_wode).error(R.mipmap.ic_head_wode).config(Bitmap.Config.RGB_565).into(img);
            } else {
                img.setImageResource(R.mipmap.ic_head_wode);
            }
        } else if (type == 1) {
            if (url != null && !url.equals("")) {
                Picasso.with(context).load(url).resize((int) context.getResources().getDimension(R.dimen.view_34),
                        (int) context.getResources().getDimension(R.dimen.view_34))
                        .placeholder(R.mipmap.ic_qunliao).error(R.mipmap.ic_qunliao).config(Bitmap.Config.RGB_565).into(img);
            } else {
                img.setImageResource(R.mipmap.ic_qunliao);
            }
        } else if (type == 2) {
            if (url != null && !url.equals("")) {
                Picasso.with(context).load(url).resize((int) context.getResources().getDimension(R.dimen.view_34),
                        (int) context.getResources().getDimension(R.dimen.view_34))
                        .placeholder(R.mipmap.ic_qunzu_xuanren).error(R.mipmap.ic_qunzu_xuanren).config(Bitmap.Config.RGB_565).into(img);
            } else {
                img.setImageResource(R.mipmap.ic_qunzu_xuanren);
            }
        }
    }

    private void getUserIcon(final ShapeImageView imageIcon, final String userId, final int type) {
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
                            setImage(url, type, imageIcon);
                        }
                    }
                });
            }
        }).start();
    }

    class ViewContentHolder {
        ImageView img_check;
        ShapeImageView img;
        TextView tv;
        LinearLayout linearLayout;
    }

    class ViewTitleHolder {
        TextView tv;
    }
}
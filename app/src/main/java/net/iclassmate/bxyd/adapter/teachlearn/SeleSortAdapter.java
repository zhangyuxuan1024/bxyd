package net.iclassmate.bxyd.adapter.teachlearn;

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
import net.iclassmate.bxyd.bean.contacts.FriendInfo;
import net.iclassmate.bxyd.utils.HttpManager;
import net.iclassmate.bxyd.view.study.ShapeImageView;

import java.util.List;

/**
 * Created by xyd on 2016/6/28.
 */
public class SeleSortAdapter extends BaseAdapter {
    private Context context;
    private List<Object> list;
    private boolean visible;

    private View.OnClickListener imgCheckImg;
    private Handler mHandler = new Handler();
    private HttpManager httpManger;
    private LruCache<String, String> lruCache;

    public void setImgCheckImg(View.OnClickListener imgCheckImg) {
        this.imgCheckImg = imgCheckImg;
    }

    public SeleSortAdapter(Context context, List<Object> list, boolean visible) {
        this.context = context;
        this.list = list;
        this.visible = visible;
        httpManger = new HttpManager();
        int maxSize = (int) Runtime.getRuntime().maxMemory();
        maxSize = maxSize / 8;
        lruCache = new LruCache<String, String>(maxSize) {
            @Override
            protected int sizeOf(String key, String value) {
                return value.getBytes().length;
            }
        };
    }

    @Override
    public int getCount() {
        return list.size();
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
        FriendInfo message = null;
        if (object instanceof FriendInfo) {
            message = (FriendInfo) object;
        }
        if (message == null) {
            return null;
        }
        String name = message.getRemark();
        if (name != null && !name.equals("") && !name.equals("null")) {
            holder.tv.setText(name);
        } else {
            holder.tv.setText(message.getUserName());
        }

        setUImage(message.getFriendId(), holder.img);

//        Picasso.with(context).load(message.getIcon()).into(holder.img);
        if (visible) {
            holder.img_check.setVisibility(View.VISIBLE);
        } else {
            holder.img_check.setVisibility(View.GONE);
        }
        if (message.check) {
            holder.img_check.setImageResource(R.mipmap.ic_checked);
        } else {
            holder.img_check.setImageResource(R.mipmap.ic_circle);
        }
        if (message.isMember()) {
            holder.img_check.setImageResource(R.mipmap.ic_yicunzai);
        }
        if (message.isHead) {
            view.findViewById(R.id.llShowIndex).setVisibility(View.VISIBLE);
            ((TextView) view.findViewById(R.id.tvIndex)).setText("" + message.sortKey);
        } else {
            view.findViewById(R.id.llShowIndex).setVisibility(View.GONE);
        }
//        holder.img_check.setTag(i);
//        holder.img_check.setOnClickListener(imgCheckImg);
//        holder.linearLayout.setTag(i);
//        holder.linearLayout.setOnClickListener(imgCheckImg);

        return view;
    }

    private void setUImage(final String friendId, final ShapeImageView img) {
        String res = lruCache.get(friendId);
        img.setTag(friendId);
        if (res == null || res.equals("") || !res.contains("http")) {
            getHead(friendId, img);
        } else {
            setImage(res, img);
        }
    }

    private void getHead(final String friendId, final ShapeImageView img) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String url = httpManger.getUserIconUrl(friendId);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        String tag = (String) img.getTag();
                        if (tag.equals(friendId)) {
                            if (url != null && !url.equals("")) {
                                lruCache.put(friendId, url);
                            }
                            setImage(url, img);
                        }
                    }
                });
            }
        }).start();
    }

    public void setImage(String url, ShapeImageView img) {
        if (url == null || url.equals("")) {
            img.setImageResource(R.mipmap.ic_head_wode);
        } else {
            Picasso.with(context).load(url).resize(106, 106).placeholder(R.mipmap.ic_head_wode)
                    .error(R.mipmap.ic_head_wode).config(Bitmap.Config.RGB_565).into(img);
        }
    }

    class ViewContentHolder {
        ImageView img_check;
        ShapeImageView img;
        TextView tv;
        LinearLayout linearLayout;
    }

}

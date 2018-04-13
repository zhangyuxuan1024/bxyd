package net.iclassmate.bxyd.adapter.study;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.bean.study.ImageState;
import net.iclassmate.bxyd.utils.LoadImageSd;

import java.util.List;

/**
 * Created by xydbj on 2016/6/7.
 */
public class GridAlbumAdapter extends BaseAdapter {
    private Context context;
    private List<ImageState> list;
    private LoadImageSd load;

    private View.OnClickListener imgCheckClick;

    public GridAlbumAdapter(Context context, List<ImageState> list) {
        this.context = context;
        this.list = list;
        load = new LoadImageSd();
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
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        int ret = 0;
        if (position == 0) {
            ret = 0;
        } else {
            ret = 1;
        }
        ret = 0;
        return ret;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void setImgCheckClick(View.OnClickListener imgCheckClick) {
        this.imgCheckClick = imgCheckClick;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
//        if (getItemViewType(i) == 0) {
//            view = bindPic(i, view, viewGroup);
//        } else if (getItemViewType(i) == 1) {
//            view = bindCamera(i, view, viewGroup);
//        }
        view = bindPic(i, view, viewGroup);
        return view;
    }

    public View bindPic(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.album_pic_item, null);
            holder = new ViewHolder();
            holder.img = (ImageView) view.findViewById(R.id.album_pic_item_img);
            holder.img_check = (ImageView) view.findViewById(R.id.album_pic_item_img_check);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        FrameLayout.LayoutParams para = (FrameLayout.LayoutParams) holder.img.getLayoutParams();
        para.height = (int) context.getResources().getDimension(R.dimen.view_90);

        ImageState state = list.get(list.size() - i - 1);
        boolean check = state.check;
        holder.img.setTag(state.path);
        load.setmHeight(para.height);
        load.setmWidth(para.height);
        load.downloadImage(holder.img, state.path);
        if (check) {
            holder.img_check.setImageResource(R.mipmap.ic_selected);
        } else {
            holder.img_check.setImageResource(R.mipmap.ic_weixuanze_wangpan);
        }
        holder.img_check.setTag(state.path);
        holder.img_check.setOnClickListener(imgCheckClick);
        return view;
    }

    public View bindCamera(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(R.layout.camera_item, null);
        return view;
    }

    class ViewHolder {
        ImageView img, img_check;
    }
}

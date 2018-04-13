package net.iclassmate.bxyd.adapter.study;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.bean.study.ImageState;
import net.iclassmate.bxyd.utils.LoadImageSd;

import java.util.List;

/**
 * Created by xydbj on 2016/10/27.
 */
public class AlbumOpenAdapter extends PagerAdapter {
    private Context context;
    private List<ImageState> list;
    private LoadImageSd load;
    private View.OnClickListener onCheckListener;

    public AlbumOpenAdapter(Context context, List<ImageState> list) {
        this.context = context;
        this.list = list;
        load = new LoadImageSd();
    }

    public void setOnCheckListener(View.OnClickListener onCheckListener) {
        this.onCheckListener = onCheckListener;
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
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_open_album_pic, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.photo_look);
        ImageView imgCheck = (ImageView) view.findViewById(R.id.img_check);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        ImageState state = list.get(position);
        if (state.check) {
            imgCheck.setImageResource(R.mipmap.ic_checked);
        } else {
            imgCheck.setImageResource(R.mipmap.ic_unchecked);
        }
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        imageView.setTag(state.path);
        load.setmWidth(width);
        load.setmHeight(height);
        load.downloadImage(imageView, state.path);

        imgCheck.setTag(position);
        imgCheck.setOnClickListener(onCheckListener);
        view.setTag(position);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}

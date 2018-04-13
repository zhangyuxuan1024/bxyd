
package net.iclassmate.bxyd.adapter.teachlearn;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.bean.netdisk.FileDirList;
import net.iclassmate.bxyd.view.ScaleImageView;

import java.util.List;

/**
 * Created by xydbj on 2016.11.7.
 */
public class PicViewPagerAdapter extends PagerAdapter {

    private Context context;
    private List<FileDirList> list;

    public void update(List<FileDirList> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public PicViewPagerAdapter(Context context, List<FileDirList> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_seepic, null);
        ScaleImageView zoom_iv = (ScaleImageView) view.findViewById(R.id.item_seepic_iv);

        zoom_iv.setImageResource(R.mipmap.img_morentupian);
        String path = list.get(position).getScale();
        if (path.contains("@")) {
            path = path.substring(0, path.indexOf("@"));
        }
        Picasso.with(context)
                .load(path)
                .config(Bitmap.Config.RGB_565)
                .placeholder(R.mipmap.img_morentupian)
                .error(R.mipmap.img_moren_shibai)
//                .transform(new PicassioCropSquareTransformation())
                .into(zoom_iv);
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

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
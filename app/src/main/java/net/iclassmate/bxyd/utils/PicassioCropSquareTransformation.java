package net.iclassmate.bxyd.utils;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

/**
 * Created by xydbj on 2016/11/4.
 */
public class PicassioCropSquareTransformation implements Transformation {
    @Override
    public Bitmap transform(Bitmap source) {
        Bitmap result = source;
        try {
            int size = Math.min(source.getWidth(), source.getHeight());
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;
            result = Bitmap.createBitmap(source, x, y, size, size);
            if (result != source) {
                source.recycle();
            }
        } catch (Exception e) {

        }
        return result;
    }

    @Override
    public String key() {
        return "square()";
    }
}
package net.iclassmate.bxyd.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * Created by xydbj on 2016/6/29
 */
public class FullSelectPicGridView extends GridView {
    public FullSelectPicGridView(Context context) {
        super(context);
    }

    public FullSelectPicGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST
        );
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }
}
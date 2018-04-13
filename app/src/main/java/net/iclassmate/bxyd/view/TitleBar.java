package net.iclassmate.bxyd.view;

/**
 *标题栏
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.utils.StringUtils;
import net.iclassmate.bxyd.utils.UIUtils;


public class TitleBar extends FrameLayout implements View.OnClickListener {

    private TitleOnClickListener mOnClickLIstener;

    public TitleBar(Context context) {
        this(context, null);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.title_bar, this);
        view.findViewById(R.id.title_left).setOnClickListener(this);
        view.findViewById(R.id.title_right).setOnClickListener(this);
        view.findViewById(R.id.title_left_inner).setOnClickListener(this);
        view.findViewById(R.id.title_right_inner).setOnClickListener(this);
        view.findViewById(R.id.title).setOnClickListener(this);

    }

    public void setRightVisibility(int visibility) {
        findViewById(R.id.title_right).setVisibility(visibility);
    }

    public void setLeftVisibility(int visibility) {
        findViewById(R.id.title_left).setVisibility(visibility);
    }

    public void setTitle(String title) {
        if (StringUtils.isEmpty(title))
            return;
        ((TextView) findViewById(R.id.title_text)).setText(title);

    }

    public TextView getTitle() {
        return ((TextView) findViewById(R.id.title_text));
    }

    public ImageView getRightIcon(){
        return (ImageView) findViewById(R.id.title_right_btn);
    }

    public void setRightIcon(int id) {
        findViewById(R.id.title_right).setVisibility(View.VISIBLE);
        findViewById(R.id.title_right_text_view).setVisibility(View.GONE);
        findViewById(R.id.title_right_btn).setVisibility(View.VISIBLE);
        ((ImageView) findViewById(R.id.title_right_btn)).setImageResource(id);
    }

    public void setRightIconEmpty() {
        findViewById(R.id.title_right).setVisibility(View.INVISIBLE);
        findViewById(R.id.title_right_text_view).setVisibility(View.GONE);
        findViewById(R.id.title_right_btn).setVisibility(View.INVISIBLE);
    }

    public void setInnerRightIcon(int id){
        findViewById(R.id.title_right_inner).setVisibility(View.VISIBLE);
        findViewById(R.id.title_right_text_view_inner).setVisibility(View.GONE);
        findViewById(R.id.title_right_btn_inner).setVisibility(View.VISIBLE);
        ((ImageView) findViewById(R.id.title_right_btn_inner)).setImageResource(id);
    }

    public void setInnerRightIconEmpty(){
        findViewById(R.id.title_right_inner).setVisibility(View.INVISIBLE);
        findViewById(R.id.title_right_text_view_inner).setVisibility(View.GONE);
        findViewById(R.id.title_right_btn_inner).setVisibility(View.INVISIBLE);
    }



    public void setRightIcon(String text) {
        findViewById(R.id.title_right).setVisibility(View.VISIBLE);
        findViewById(R.id.title_right_btn).setVisibility(View.GONE);
        TextView textView = (TextView) findViewById(R.id.title_right_text_view);
        textView.setVisibility(View.VISIBLE);
        textView.setText(text);

    }

    public void setRightTextandColor(String text,int color){
        findViewById(R.id.title_right).setVisibility(View.VISIBLE);
        findViewById(R.id.title_right_btn).setVisibility(View.GONE);
        TextView textView = (TextView) findViewById(R.id.title_right_text_view);
        textView.setVisibility(View.VISIBLE);
        textView.setText(text);
        textView.setTextColor(color);
    }

    public void setRightBackground(int id) {
        findViewById(R.id.title_right_btn).setBackgroundResource(id);

    }

    public void setLeftBackground(int id) {
        findViewById(R.id.title_left).setBackgroundResource(id);
    }

    public void setLeftIcon(int id) {
        setLeftVisibility(View.VISIBLE);
        findViewById(R.id.title_left_btn).setVisibility(View.VISIBLE);
        findViewById(R.id.title_left_text_view).setVisibility(View.GONE);
        ((ImageView) findViewById(R.id.title_left_btn)).setImageResource(id);
    }

    public void setLeftIcon(String text) {
        findViewById(R.id.title_left).setVisibility(View.VISIBLE);
        findViewById(R.id.title_left_btn).setVisibility(View.GONE);
        TextView textView = (TextView) findViewById(R.id.title_left_text_view);
        FrameLayout.LayoutParams fp=new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        fp.setMargins(UIUtils.dip2px(10),UIUtils.dip2px(12),0,0);
        textView.setLayoutParams(fp);
        textView.setVisibility(View.VISIBLE);
        textView.setText(text);
    }

    public void setLeftIcon(int id, String text) {
        setLeftVisibility(View.VISIBLE);
        TextView textView = (TextView) findViewById(R.id.title_left_text_view);
        ImageView imageView = (ImageView) findViewById(R.id.title_left_btn);
        textView.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageResource(id);
        textView.setText(text);
    }

    public void setTitleClickListener(TitleOnClickListener l) {
        mOnClickLIstener = l;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_left:
                if (mOnClickLIstener != null) {
                    mOnClickLIstener.leftClick();
                }
                break;
            case R.id.title_right:
                if (mOnClickLIstener != null) {
                    mOnClickLIstener.rightClick();
                }
                break;
            case R.id.title_left_inner:
                if (mOnClickLIstener != null) {
                    mOnClickLIstener.innerleftClick();
                }
                break;
            case R.id.title_right_inner:
                if (mOnClickLIstener != null) {
                    mOnClickLIstener.innerRightClick();
                }
                break;
            case R.id.title:
                if (mOnClickLIstener != null) {
                    mOnClickLIstener.titleClick();
                }
                break;
        }
    }

    /**
     * 标题头事件返回监听
     */
    public interface TitleOnClickListener {
        public void leftClick();

        public void rightClick();

        public void titleClick();

        public void innerleftClick();

        public  void innerRightClick();
    }
}

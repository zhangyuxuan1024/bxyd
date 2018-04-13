package net.iclassmate.bxyd.view;

import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import net.iclassmate.bxyd.R;

/**
 * Created by xyd on 2016/6/22.
 *  加载动画帮助类
 */
public class Loading {
    private RelativeLayout loadinglayout;
    private ImageView animationView;
    private AnimationDrawable animationDrawable;
    private LinearLayout noLoading;
    private RelativeLayout withoutLoading;

    public  Loading(View rootView,LinearLayout noLoading){
        loadinglayout= (RelativeLayout) rootView.findViewById(R.id.loading_layout);
        this.noLoading=noLoading;
        animationView= (ImageView) rootView.findViewById(R.id.animation_view);
        animationView.setImageResource(R.drawable.loading_animation);
        animationDrawable= (AnimationDrawable) animationView.getDrawable();

    }

    public Loading(View rootView,RelativeLayout withoutloading){
        loadinglayout= (RelativeLayout) rootView.findViewById(R.id.loading_layout);
        withoutLoading=withoutloading;
        animationView= (ImageView) rootView.findViewById(R.id.animation_view);
        animationView.setImageResource(R.drawable.loading_animation);
        animationDrawable= (AnimationDrawable) animationView.getDrawable();
    }

    public void showLoading(boolean isLinear){
        loadinglayout.setVisibility(View.VISIBLE);
        if (isLinear){
            noLoading.setVisibility(View.GONE);
        }else {
            withoutLoading.setVisibility(View.GONE);
        }
        animationDrawable.start();
    }

    public void hideLoading(boolean isLinear){
        loadinglayout.setVisibility(View.GONE);
        if (isLinear){
            noLoading.setVisibility(View.VISIBLE);
        }else {
            withoutLoading.setVisibility(View.VISIBLE);
        }
        animationDrawable.stop();
    }

}

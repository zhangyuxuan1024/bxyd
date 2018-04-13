package net.iclassmate.bxyd.ui.activitys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.adapter.GuidePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends Activity {

    private Context mContext;
    private ViewPager mGuideViewpager;
    private Button mGuideBtn;
    private GuidePagerAdapter adapter;
    private int currentIndex = 0;
    // 引导图片资源
    private static final int[] pics = {R.mipmap.img_huanyingye1, R.mipmap.img_huanyingye2,
            R.mipmap.img_huanyingye3};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_guide);
        mContext=this;
        initView();
        initListener();
    }

    private void initView() {
        mGuideBtn= (Button) findViewById(R.id.guide_button);
        mGuideViewpager= (ViewPager) findViewById(R.id.guide_viewpager);

        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        List<View> views = new ArrayList<View>();
        initValues(views, mParams);
    }

    private void initValues(List<View> views, LinearLayout.LayoutParams mParams) {
        for (int i = 0; i < pics.length; i++) {
            ImageView image = new ImageView(this);
            image.setLayoutParams(mParams);
            image.setImageDrawable(getResources().getDrawable(pics[i]));
            views.add(image);
        }
        adapter = new GuidePagerAdapter(this, views);
        mGuideViewpager.setAdapter(adapter);
        mGuideViewpager.setOnPageChangeListener(new MypageChangeListener());
    }

    private void initListener() {
        mGuideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuideActivity.this,
                        LoginActivity.class);
                startActivity(intent);
                GuideActivity.this.finish();
                overridePendingTransition(R.anim.alpha_in_anim, // Activity的切换动画，从一个activity跳转到另外一个activity时的动画。
                        R.anim.alpha_out_anim);
            }
        });
    }
    private class MypageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int position) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onPageSelected(int arg0) {
            currentIndex = arg0;
            if (currentIndex == 2) {
                mGuideBtn.setVisibility(View.VISIBLE);
            } else {
                mGuideBtn.setVisibility(View.GONE);
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("GuideActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("GuideActivity");
        MobclickAgent.onPause(this);
    }
}

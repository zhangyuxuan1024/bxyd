package net.iclassmate.bxyd.ui.activitys.teachlearn;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.adapter.teachlearn.PicViewPagerAdapter;
import net.iclassmate.bxyd.bean.netdisk.FileDirList;
import net.iclassmate.bxyd.view.study.PhotoViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xydbj on 2016.11.3.
 */
public class SeePicActivity extends Activity implements OnClickListener, OnPageChangeListener {
    private List<FileDirList> fileDirLists;
    private List<FileDirList> picList;

    private TextView seepic_tv_back, seepic_tv_title;
    private ImageView seepic_iv_back;

    private String id;
    private int cur_index, all_index;

    private PhotoViewPager viewPager;
    private PicViewPagerAdapter pagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seepic);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        fileDirLists = (List<FileDirList>) intent.getSerializableExtra("fileDirList");

        initView();

        picList = getNewList(fileDirLists);
        pagerAdapter.update(picList);
        all_index = picList.size();
        for (int i = 0; i < picList.size(); i++) {
            if (id.equals(picList.get(i).getId())) {
                cur_index = i;
                break;
            }
        }
        seepic_tv_title.setText(cur_index + 1 + "/" + all_index);
        viewPager.setCurrentItem(cur_index);
    }

    public void initView() {
        viewPager = (PhotoViewPager) findViewById(R.id.pager_viewpager);
        seepic_tv_title = (TextView) findViewById(R.id.seepic_tv_title);
        seepic_tv_back = (TextView) findViewById(R.id.seepic_tv_back);
        seepic_iv_back = (ImageView) findViewById(R.id.seepic_iv_back);
        seepic_iv_back.setOnClickListener(this);
        seepic_tv_back.setOnClickListener(this);
        picList = new ArrayList<>();
        pagerAdapter = new PicViewPagerAdapter(this, picList);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(this);
    }

    public List<FileDirList> getNewList(List<FileDirList> fileDirList) {
        List<FileDirList> picList = new ArrayList<>();
        for (int i = 0; i < fileDirList.size(); i++) {
            String shortName = fileDirList.get(i).getShortName().toLowerCase();
            if (shortName.contains(".")) {
                String suffixName = shortName.substring(shortName.lastIndexOf(".") + 1);
                if (suffixName.equals("bmp") ||
                        suffixName.equals("gif") ||
                        suffixName.equals("jpg") ||
                        suffixName.equals("pic") ||
                        suffixName.equals("png") ||
                        suffixName.equals("jpeg") ||
                        suffixName.equals("tif")) {
                    picList.add(fileDirList.get(i));
                }
            }
        }
        return picList;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.seepic_iv_back:
            case R.id.seepic_tv_back:
                SeePicActivity.this.finish();
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        seepic_tv_title.setText((position + 1) + "/" + all_index);
        cur_index = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}

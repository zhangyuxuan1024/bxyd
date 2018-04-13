package net.iclassmate.bxyd.ui.activitys.teachlearn;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.adapter.teachlearn.TranAdapter;
import net.iclassmate.bxyd.bean.netdisk.FileDirList;
import net.iclassmate.bxyd.ui.fragment.tran.DownLoadFragment;
import net.iclassmate.bxyd.ui.fragment.tran.LazyFragment;
import net.iclassmate.bxyd.ui.fragment.tran.UpLoadFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xydbj on 2016.7.17.
 */
public class TranActivity extends FragmentActivity implements View.OnClickListener {
    private TextView tran_tv_back;
    private ImageView tran_iv_back;
    private TabLayout tran_tl;
    private ViewPager tran_vp;
    private List<LazyFragment> fragments;
    private TranAdapter adapter;
    private List<FileDirList> list;
    private List<Object> albumList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tran);
        Intent intent = getIntent();
        list = (List<FileDirList>) intent.getSerializableExtra("listSelected");
        albumList = (List<Object>) intent.getSerializableExtra("upload");
        if (albumList != null){
            Log.i("info", "向上传列表中传输的数据：" + albumList.toString());
        }
        if (list != null) {
            Log.i("info", "向下载列表中传输的数据：" + list.toString());
        }
        initView();
    }

    public void initView() {
        tran_iv_back = (ImageView) findViewById(R.id.tran_iv_back);
        tran_tv_back = (TextView) findViewById(R.id.tran_tv_back);
        tran_tl = (TabLayout) findViewById(R.id.tran_tl);
        tran_vp = (ViewPager) findViewById(R.id.tran_vp);

        tran_iv_back.setOnClickListener(this);
        tran_tv_back.setOnClickListener(this);

        fragments = new ArrayList<>();

        setFragments();
    }

    public void setFragments() {
        DownLoadFragment downLoadFragment = new DownLoadFragment();
        UpLoadFragment upLoadFragment = new UpLoadFragment();

        downLoadFragment.setFileDirListList(list);
        upLoadFragment.setAlbumList(albumList);

        fragments.add(downLoadFragment);
        fragments.add(upLoadFragment);

        adapter = new TranAdapter(getSupportFragmentManager(), fragments);
        tran_vp.setAdapter(adapter);
        tran_vp.setOffscreenPageLimit(2);
        tran_vp.setOnPageChangeListener(listener);
        tran_tl.setupWithViewPager(tran_vp);
        tran_vp.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    public ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tran_iv_back:
            case R.id.tran_tv_back:
                TranActivity.this.finish();
                break;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("TranActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("TranActivity");
        MobclickAgent.onPause(this);
    }
}

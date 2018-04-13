package net.iclassmate.bxyd.ui.activitys.study;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.adapter.study.MyViewPagerAdapter;
import net.iclassmate.bxyd.bean.study.FileDirList;
import net.iclassmate.bxyd.bean.study.Resources;
import net.iclassmate.bxyd.view.study.PhotoViewPager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LookPicActivity extends FragmentActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private TextView tv_title, tv_back;
    private ImageView img_right, img_back;
    private String title, url;
    private int type;
    public static final int RET_DEL = 1;
    private List<Object> listPic;
    private List<Object> listSelectAll;
    private int cur_index;
    private PhotoViewPager viewPager;
    private MyViewPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_pic);

        Intent intent = getIntent();
//        title = intent.getStringExtra("title");
        type = intent.getIntExtra("type", 0);
        cur_index = intent.getIntExtra("index", 0);
        listSelectAll = (List<Object>) intent.getSerializableExtra("list");
        init();
        setTitleAndList();
    }

    private void setTitleAndList() {
        listPic.clear();
        for (int i = 0; i < listSelectAll.size(); i++) {
            Object object = listSelectAll.get(i);
            if (object instanceof String) {
                String ret = (String) object;
                listPic.add(ret);
            } else if (object instanceof FileDirList) {
                FileDirList file = (FileDirList) object;
                String name = file.getShortName().toLowerCase();
                if (name.contains(".")) {
                    name = name.substring(name.lastIndexOf(".") + 1, name.length());
                }
                if (name.equals("bmp") || name.equals("gif") || name.equals("jpg") ||
                        name.equals("pic") || name.equals("png") || name.equals("tif")) {
                    listPic.add(file);
                }
            } else if (object instanceof Resources) {
                Resources resources = (Resources) object;
                String type = resources.getType();
                if (type.equals("图片")) {
                    listPic.add(resources);
                }
            } else if (object instanceof Uri) {
                listPic.add(object);
            }
        }
        if (type == 1 || type == 2) {
            initTitle();
        } else if (type == 3) {
            tv_title.setText(cur_index + 1 + "/" + listPic.size());
            viewPager.setCurrentItem(cur_index);
        }
        pagerAdapter.notifyDataSetChanged();
    }

    private void initTitle() {
        if (cur_index < listSelectAll.size()) {
            Object o = listSelectAll.get(cur_index);
            if (listPic.contains(o)) {
                cur_index = listPic.indexOf(o);
                tv_title.setText(cur_index + 1 + "/" + listPic.size());
                viewPager.setCurrentItem(cur_index);
            }
        }
    }

    private void init() {
        tv_title = (TextView) findViewById(R.id.study_message_title_tv);
        img_right = (ImageView) findViewById(R.id.study_message_right_icon);
        tv_title.setText(title);
        viewPager = (PhotoViewPager) findViewById(R.id.pager_viewpager);
        listPic = new ArrayList<>();
        pagerAdapter = new MyViewPagerAdapter(this, listPic);
        pagerAdapter.setType(type);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(this);

        img_back = (ImageView) findViewById(R.id.study_message_back);
        tv_back = (TextView) findViewById(R.id.study_message_left_tv);
        tv_back.setText("返回");
        img_back.setOnClickListener(this);
        tv_back.setOnClickListener(this);
        if (type == 1) {
            img_right.setVisibility(View.VISIBLE);
            img_right.setImageResource(R.mipmap.ic_xiaobaidian);
            img_right.setOnClickListener(this);
        } else if (type == 2) {
            img_right.setVisibility(View.VISIBLE);
            img_right.setImageResource(R.drawable.img_study_del);
            img_right.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.study_message_back:
            case R.id.study_message_left_tv:
                back();
                break;
            case R.id.study_message_right_icon:
                if (type == 1) {
                    Intent intent = new Intent(LookPicActivity.this, StudyWindowActivity.class);
                    List<String> list = new ArrayList<String>();
                    list.add("保存到网盘");
                    intent.putStringArrayListExtra("list", (ArrayList<String>) list);
                    Object obj = listPic.get(cur_index);
                    if (obj instanceof Resources) {
                        Resources resources = (Resources) obj;
                        intent.putExtra("saveid", resources.getId());
                    }
                    startActivity(intent);
                } else if (type == 2) {
                    Intent intent = new Intent(LookPicActivity.this, StudyWindowActivity.class);
                    List<String> list = new ArrayList<String>();
                    list.add("是否确认删除文件删除");
                    intent.putStringArrayListExtra("list", (ArrayList<String>) list);
                    startActivityForResult(intent, RET_DEL);
                }
                break;
        }
    }

    private void back() {
        if (type == 2) {
            sendResult();
        } else {
            this.finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RET_DEL) {
                if (listPic.size() < 1) {
                    sendResult();
                } else {
                    Object o = listPic.get(cur_index);
                    if (listSelectAll.contains(o)) {
                        int index = listSelectAll.indexOf(o);
                        listSelectAll.remove(index);
                    }
                    listPic.remove(cur_index);
                    if (listPic.size() < 1) {
                        sendResult();
                        return;
                    }
                    pagerAdapter.notifyDataSetChanged();
                    if (listPic.size() <= cur_index) {
                        cur_index = 0;
                    }
                    viewPager.setCurrentItem(cur_index);
                    tv_title.setText(cur_index + 1 + "/" + listPic.size());
                }
            }
        }
    }

    private void sendResult() {
        Intent intent = new Intent();
        intent.putExtra("list", (Serializable) listSelectAll);
        setResult(RESULT_OK, intent);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        back();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        tv_title.setText((position + 1) + "/" + listPic.size());
        cur_index = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("LookPicActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("LookPicActivity");
        MobclickAgent.onPause(this);
    }
}

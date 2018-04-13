package net.iclassmate.bxyd.ui.activitys.study;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.adapter.study.StudySpaceAdapter;
import net.iclassmate.bxyd.bean.study.StudyMessage;
import net.iclassmate.bxyd.view.FullListView;

import java.util.ArrayList;
import java.util.List;


public class LookIndexActivity extends FragmentActivity implements View.OnClickListener {
    private LinearLayout message_linear;
    private TextView message_tv;
    private StudySpaceAdapter adapter;
    private FullListView listView;
    private List<StudyMessage> listMessage;
    private Context mContext;

    private String url = "http://img2.3lian.com/2014/f2/181/109.jpg";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_study_space);
        initView();
        initEvent();
        loadData();
    }

    private void loadData() {
        String path = "http://ww1.sinaimg.cn/thumbnail/80ab1ad3gw1dx8tfjvbgdj.jpg";
        String path1 = "http://i01.pic.sogou.com/a0b00bdb0bb473f6";
//        String path2 = "http://ww3.sinaimg.cn/thumbnail/9d57e8e4jw1dx6topumz5j.jpg";
        String path3 = "http://i04.pic.sogou.com/f439d0dc6da969e4";
        String path4 = "http://i04.pic.sogou.com/b40dbc4c2c0a267c";
        String path5 = "http://i04.pic.sogou.com/e5e90880da9a344b";
        String path6 = "http://i02.pictn.sogoucdn.com/c297cedf6cc9f6ce";
        String path7 = "http://i04.pic.sogou.com/a68be9ae4a97d1b0";
        String path8 = "http://i01.pic.sogou.com/0c6e64aa04b03f8c";
        String path9 = "http://i01.pic.sogou.com/b0d73aa23fe40955";

        String[] data = {path, path1, path3, path4, path5, path6, path7, path8, path9};
        for (int i = 0; i < 20; i++) {
            StudyMessage message = new StudyMessage();
            message.setHeadUrl(url);
            message.setTime("下午13:50");
            message.setName("王小明");
            message.setComment(i);
            message.setLike(i);
            List<String> list = new ArrayList<>();
            for (int j = 0; j < i % 10; j++) {
                list.add(data[j]);
            }
           // message.setPicList(list);
            listMessage.add(message);
        }
        adapter.notifyDataSetChanged();
    }

    private void initView() {
        message_linear = (LinearLayout) findViewById(R.id.study_message_linear);
        message_tv = (TextView) findViewById(R.id.study_message_tv);
        mContext = this;
        listView = (FullListView) findViewById(R.id.study_sapce_listview);
        listMessage = new ArrayList<>();
        //adapter = new StudySpaceAdapter(mContext, listMessage);
        listView.setAdapter(adapter);
        adapter.setImgClickComent(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = (int) view.getTag();
                StudyMessage message = listMessage.get(index);
                Intent intent = new Intent(mContext, TraStudyActivity.class);
                intent.putExtra("msg", message);
                startActivity(intent);

                int com = message.getComment();
                com++;
                message.setComment(com);
                listMessage.set(index, message);
                adapter.notifyDataSetChanged();
            }
        });
        adapter.setImgClickLike(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = (int) view.getTag();
                StudyMessage message = listMessage.get(index);
                int like = message.getLike();
                like++;
                message.setIsLike(true);
                message.setLike(like);
                listMessage.set(index, message);
                adapter.notifyDataSetChanged();
            }
        });
        adapter.setImgClickShare(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, StudyWindowActivity.class);
                List<String> list = new ArrayList<String>();
                list.add("转发到学习圈");
                list.add("转发给好友");
                list.add("保存到网盘");
                list.add("收藏");
                list.add("举报");
                intent.putStringArrayListExtra("list", (ArrayList<String>) list);
                startActivity(intent);
            }
        });
//        adapter.setImgClickHead(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                int index = (int) view.getTag();
//                Intent intent = new Intent(mContext, LookIndexActivity.class);
//                startActivity(intent);
//            }
//        });
        adapter.setGridClick(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                int index = (int) adapterView.getTag();
//                List<String> listPic = listMessage.get(index).getPicList();
//                Intent intent = new Intent(mContext, LookPicActivity.class);
//                intent.putExtra("title", (i + 1) + "/" + listPic.size());
//                intent.putExtra("url", listPic.get(i));
//                intent.putExtra("type", 1);
//                startActivity(intent);
            }
        });
    }

    private void initEvent() {
        message_linear.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.study_message_linear:
                Intent intent = new Intent(mContext, MessageAlertActivity.class);
                startActivity(intent);
                break;
        }
    }
}
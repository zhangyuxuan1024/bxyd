package net.iclassmate.bxyd.ui.fragment;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.adapter.study.StudySpaceAdapter;
import net.iclassmate.bxyd.bean.study.StudyMessageItem;
import net.iclassmate.bxyd.bean.study.StudyMessageList;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.ui.activitys.study.CommentActivity;
import net.iclassmate.bxyd.ui.activitys.study.MessageAlertActivity;
import net.iclassmate.bxyd.ui.activitys.study.Scan2Activity;
import net.iclassmate.bxyd.ui.activitys.study.StudySpaceActivity;
import net.iclassmate.bxyd.ui.activitys.study.StudyWindowActivity;
import net.iclassmate.bxyd.view.FullListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudySpaceFragment extends Fragment implements View.OnClickListener {
    private LinearLayout message_linear;
    private TextView message_tv;
    private StudySpaceAdapter adapter;
    private FullListView listView;
    private List<StudyMessageItem> listMessage;
    private Context mContext;
    private StudyMessageList smlist;
    private OkHttpClient client;

    private LinearLayout open_index_linear, open_sys_linear;
    private long last_click_time;

    public StudySpaceFragment() {
        // Required empty public constructor
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            if (what == 1) {
                Toast.makeText(mContext, "请求数据成功！", Toast.LENGTH_SHORT).show();
                String result = (String) msg.obj;
                try {
                    result = removeBOM(result);
                    JSONObject json = new JSONObject(result);
                    smlist = new StudyMessageList();
                    smlist.parserJson(json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (smlist == null) {
                    return;
                }
                List<StudyMessageItem> smlistList = smlist.getList();
                listMessage.addAll(smlistList);
                adapter.notifyDataSetChanged();
            } else if (what == 404) {
                Toast.makeText(mContext, "请求数据失败", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view_study = inflater.inflate(R.layout.fragment_study_space, container, false);
        initView(view_study);
        initEvent();
        return view_study;
    }


    private void initView(View view) {
        message_linear = (LinearLayout) view.findViewById(R.id.study_message_linear);
        message_tv = (TextView) view.findViewById(R.id.study_message_tv);
        mContext = getContext();
        listView = (FullListView) view.findViewById(R.id.study_sapce_listview);
        listMessage = new ArrayList<>();
        mContext = getContext();
        adapter = new StudySpaceAdapter(mContext, listMessage);
        listView.setAdapter(adapter);
        adapter.setImgClickComent(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = (int) view.getTag();
                StudyMessageItem message = listMessage.get(index);
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("msg", message);
                startActivity(intent);
            }
        });
        adapter.setImgClickLike(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = (int) view.getTag();
                StudyMessageItem message = listMessage.get(index);
                int like = message.getLiked();
                boolean isLike = message.isClickLiked();
                isLike = !isLike;
                message.setIsClickLiked(isLike);
                if (isLike) {
                    Toast.makeText(mContext, "已点赞", Toast.LENGTH_SHORT).show();
                    like++;
                } else {
                    Toast.makeText(mContext, "取消点赞", Toast.LENGTH_SHORT).show();
                    like--;
                }
                message.setLiked(like);
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
                int index = (int) view.getTag();
                StudyMessageItem message = listMessage.get(index);
                intent.putExtra("msg", message);
                startActivity(intent);
            }
        });
        adapter.setImgClickHead(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                int index = (int) view.getTag();
//                Intent intent = new Intent(mContext, LookIndexActivity.class);
//                startActivity(intent);
            }
        });
        adapter.setGridClick(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int index = (int) adapterView.getTag();
//                List<String> listPic = listMessage.get(index).getPicList();
//                Intent intent = new Intent(mContext, LookPicActivity.class);
//                intent.putExtra("title", (i + 1) + "/" + listPic.size());
//                intent.putExtra("url", listPic.get(i));
//                intent.putExtra("type", 1);
//                intent.putStringArrayListExtra("list", (ArrayList<String>) listPic);
//                startActivity(intent);
            }
        });

        open_index_linear = (LinearLayout) view.findViewById(R.id.open_index_page_linear);
        open_index_linear.setOnClickListener(this);

        open_sys_linear = (LinearLayout) view.findViewById(R.id.open_sys_linear);
        open_sys_linear.setOnClickListener(this);
    }

    private void initEvent() {
        message_linear.setOnClickListener(this);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.study_message_linear:
                Intent intent = new Intent(mContext, MessageAlertActivity.class);
                startActivity(intent);
                break;
            case R.id.open_index_page_linear:
                Intent intent1 = new Intent(mContext, StudySpaceActivity.class);
                startActivity(intent1);
                break;
            case R.id.open_sys_linear:
                if (Build.VERSION.SDK_INT < 23){
                    Intent intent2 = new Intent(getActivity(), Scan2Activity.class);
                    startActivity(intent2);
                } else {
                    AndPermission.with(this)
                            .requestCode(1012)
                            .permission(Manifest.permission.CAMERA)
                            .send();
                }
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        AndPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults, listener);
    }

    private PermissionListener listener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode) {
            if (requestCode == 1012) {
                Intent intent2 = new Intent(getActivity(), Scan2Activity.class);
                startActivity(intent2);
            }
        }

        @Override
        public void onFailed(int requestCode) {
            if (requestCode == 1012) {
                Toast.makeText(getActivity(), "您未开放相机权限", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public File getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        File file = null;
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
            file = new File(sdDir + "/" + Constant.APP_DIR_NAME);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
        return file;
    }

    public String removeBOM(String data) {
        if (TextUtils.isEmpty(data)) {
            return data;
        }
        if (data.startsWith("\ufeff")) {
            return data.substring(1);
        } else {
            return data;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("StudySpaceFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("StudySpaceFragment");
    }
}

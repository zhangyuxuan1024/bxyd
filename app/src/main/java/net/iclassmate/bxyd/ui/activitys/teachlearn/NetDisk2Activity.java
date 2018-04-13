package net.iclassmate.bxyd.ui.activitys.teachlearn;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.adapter.teachlearn.MyNetDiskAdapter;
import net.iclassmate.bxyd.bean.netdisk.FileDirList;
import net.iclassmate.bxyd.bean.netdisk.NetDisk;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.ui.activitys.chat.SelectContactsActivity;
import net.iclassmate.bxyd.ui.activitys.study.AlbumActivity;
import net.iclassmate.bxyd.ui.activitys.study.ReleaseActivity;
import net.iclassmate.bxyd.ui.activitys.study.ScanActivity;
import net.iclassmate.bxyd.ui.activitys.study.openfile.OpenAudioActivity;
import net.iclassmate.bxyd.ui.activitys.study.openfile.OpenFailActivity;
import net.iclassmate.bxyd.ui.activitys.study.openfile.OpenTextFileActivity;
import net.iclassmate.bxyd.utils.JsonUtils;
import net.iclassmate.bxyd.utils.TitlePopup;
import net.iclassmate.bxyd.view.ActionItem;
import net.iclassmate.bxyd.view.FullListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by xydbj on 2016.7.15.
 */
public class NetDisk2Activity extends Activity implements View.OnClickListener {
    private ImageView netdisk2_iv_back, netdisk2_iv_chuanshuliebiao,
            item_four_selector_faxuexiquan2, item_four_selector_zhuanfa2,
            item_four_selector_download2, item_four_selector_shanchu2, netdisk2_iv_loading, netdisk2_iv_jiahao;
    private TextView netdisk2_tv_back, netdisk2_tv_selector, netdisk2_tv_title;
    private LinearLayout netdisk2_ll_four_selector;
    private FullListView flv;
    private NetDisk netDisk;
    private List<FileDirList> fileDirLists;
    private List<FileDirList> listSelected;//删除,下载
    private List<net.iclassmate.bxyd.bean.study.FileDirList> FBlistSelected;
    private net.iclassmate.bxyd.bean.study.FileDirList fbfileDirList;
    private List<Integer> positions;
    private int[] intpositions;
    private FileDirList f;
    private MyNetDiskAdapter adapter;
    private boolean flag = false;
    private boolean isMax, isAdmin, isJgDisk;
    private int count = 0;
    private int uploadPicCount = 0;
    private AnimationDrawable anim;
    private static final int REQUEST_CODE = 1;
    private static final int CREATE_FILE = 101;
    private static final int UPLOAD_ALBUM = 102;
    private String fullPath, spaceId, userId, type;
    private SharedPreferences sharedPreferences;
    public static final String action = "upload";
    private List<Object> albumList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_netdisk2);
        Intent intent = getIntent();
        spaceId = intent.getStringExtra("spaceId");
        fullPath = intent.getStringExtra("fullPath");
        Log.i("info", "当前界面的fullPath:" + fullPath);
        String shortName = intent.getStringExtra("ShortName");
        isAdmin = intent.getBooleanExtra("isAdmin", false);
        isJgDisk = intent.getBooleanExtra("isJgDisk", false);
        type = intent.getStringExtra("type");
        Log.i("info", "进入了哪个网盘：" + type);

        initView();
        if (type != null && !type.equals("null") && !type.equals("")) {
            if (!isAdmin && type.equals("org")) {
                netdisk2_ll_four_selector.setWeightSum(3);
                item_four_selector_faxuexiquan2.setVisibility(View.GONE);
                item_four_selector_zhuanfa2.setVisibility(View.INVISIBLE);
                item_four_selector_shanchu2.setVisibility(View.INVISIBLE);
            }
        }
        if (!isJgDisk) {
            netdisk2_iv_jiahao.setVisibility(View.INVISIBLE);
        }
        userId = sharedPreferences.getString(Constant.ID_USER, "");
        if (userId == null) {
            return;
        }
        if (shortName == null) {
            netdisk2_tv_title.setText("我的网盘");
        } else {
            netdisk2_tv_title.setText(shortName);
        }
        if (fullPath == null) {
            fullPath = "";
        }

        getData(spaceId, fullPath);

        IntentFilter filter = new IntentFilter(AllVideoActivity.action);
        registerReceiver(receiver, filter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            fileDirLists.clear();
            getData(spaceId, fullPath);
        }
    };

    //初始化
    public void initView() {
        sharedPreferences = this.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        netdisk2_iv_jiahao = (ImageView) findViewById(R.id.netdisk2_iv_jiahao);
        netdisk2_iv_back = (ImageView) findViewById(R.id.netdisk2_iv_back);
        netdisk2_tv_back = (TextView) findViewById(R.id.netdisk2_tv_back);
        netdisk2_tv_selector = (TextView) findViewById(R.id.netdisk2_tv_selector);
        netdisk2_iv_chuanshuliebiao = (ImageView) findViewById(R.id.netdisk2_iv_chuanshuliebiao);
        item_four_selector_faxuexiquan2 = (ImageView) findViewById(R.id.item_four_selector_faxuexiquan2);
        item_four_selector_zhuanfa2 = (ImageView) findViewById(R.id.item_four_selector_zhuanfa2);
        item_four_selector_download2 = (ImageView) findViewById(R.id.item_four_selector_download2);
        item_four_selector_shanchu2 = (ImageView) findViewById(R.id.item_four_selector_shanchu2);
        flv = (FullListView) findViewById(R.id.netdisk2_mylistview);
        netdisk2_tv_title = (TextView) findViewById(R.id.netdisk2_tv_title);
        netdisk2_ll_four_selector = (LinearLayout) findViewById(R.id.netdisk2_ll_four_selector);
        netdisk2_iv_loading = (ImageView) findViewById(R.id.netdisk2_iv_loading);

        anim = (AnimationDrawable) netdisk2_iv_loading.getBackground();
        anim.start();

        netdisk2_iv_back.setOnClickListener(this);
        netdisk2_tv_back.setOnClickListener(this);
        netdisk2_tv_selector.setOnClickListener(this);
        netdisk2_iv_chuanshuliebiao.setOnClickListener(this);
        netdisk2_iv_jiahao.setOnClickListener(this);
        item_four_selector_faxuexiquan2.setOnClickListener(this);
        item_four_selector_zhuanfa2.setOnClickListener(this);
        item_four_selector_download2.setOnClickListener(this);
        item_four_selector_shanchu2.setOnClickListener(this);
        flv.setOnItemClickListener(itemClickListener);

        View v_bottom = LayoutInflater.from(this).inflate(R.layout.item_bottom, null);
        v_bottom.setClickable(false);
        flv.addFooterView(v_bottom);
        albumList = new ArrayList<>();
        fileDirLists = new ArrayList<>();
        listSelected = new ArrayList<>();
        FBlistSelected = new ArrayList<>();
        positions = new ArrayList<>();
        intpositions = new int[10];
        adapter = new MyNetDiskAdapter(this, fileDirLists);
        flv.setAdapter(adapter);
        adapter.setImgCheckClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = (int) v.getTag();
                FileDirList file = fileDirLists.get(index);
                boolean flag = !file.isCheck();
                fileDirLists.set(index, file);
                if (count > 8) {
                    Toast.makeText(NetDisk2Activity.this, "最多选择9个文件", Toast.LENGTH_SHORT).show();
                    isMax = true;
                    file.setIsCheck(!flag);
                } else {
                    file.setIsCheck(flag);
                }

                if (flag && !isMax) {
                    count++;
                    positions.add(index);

                    f = new FileDirList();
                    f.setSpaceUuid(file.getSpaceUuid());
                    f.setId(file.getId());
                    f.setShortName(file.getShortName());
                    f.setScale(file.getScale());
                    f.setFullPath(file.getFullPath());
                    f.setType(file.getType());
                    listSelected.add(f);

                    fbfileDirList = new net.iclassmate.bxyd.bean.study.FileDirList();
                    fbfileDirList.setId(fileDirLists.get(index).getId());
                    fbfileDirList.setSpaceUuid(fileDirLists.get(index).getSpaceUuid());
                    fbfileDirList.setAuth(fileDirLists.get(index).getAuth());
                    fbfileDirList.setCreateTime(fileDirLists.get(index).getCreateTime());
                    fbfileDirList.setFileType(fileDirLists.get(index).getFileType());
                    fbfileDirList.setFullPath(fileDirLists.get(index).getFullPath());
                    fbfileDirList.setLabel(fileDirLists.get(index).getLabel());
                    fbfileDirList.setParentId(fileDirLists.get(index).getParentId());
                    fbfileDirList.setSaveUuid(fileDirLists.get(index).getSaveUuid());
                    fbfileDirList.setScale(fileDirLists.get(index).getScale());
                    fbfileDirList.setSeq(fileDirLists.get(index).getSeq());
                    fbfileDirList.setShortName(fileDirLists.get(index).getShortName());
                    fbfileDirList.setSize(Integer.parseInt(fileDirLists.get(index).getSize()));
                    fbfileDirList.setType(fileDirLists.get(index).getType());
                    fbfileDirList.setUpdateTime(fileDirLists.get(index).getUpdateTime());
                    fbfileDirList.setUserUuid(fileDirLists.get(index).getUserUuid());
                    FBlistSelected.add(fbfileDirList);
                } else if (!flag && !isMax) {
                    count--;
                    for (int i = 0; i < listSelected.size(); i++) {
                        if (listSelected.get(i).getId().equals(file.getId())) {
                            listSelected.remove(i);
                            break;
                        }
                    }
                    for (int j = 0; j < FBlistSelected.size(); j++) {
                        if (FBlistSelected.get(j).getId().equals(file.getId())) {
                            FBlistSelected.remove(j);
                            break;
                        }
                    }
                }
                Log.i("info", "选择文件listSelected:" + listSelected.size() + ",选择文件的count:" + count + ",isMax=" + isMax + ",flag=" + flag);
                adapter.notifyDataSetChanged();
            }
        });
    }


    //通过spaceId,fullPath请求网盘数据
    public void getData(final String spaceId, final String fullPath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                execute(spaceId, fullPath);
            }
        }).start();
        adapter.notifyDataSetChanged();
    }

    public void execute(String spaceId, String fullPath) {
        MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("application/json;charset=utf-8");
        OkHttpClient okHttpClient = new OkHttpClient();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("fullPath", fullPath);
            jsonObject.put("spaceId", spaceId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json = jsonObject.toString();
        RequestBody body = RequestBody.create(MEDIA_TYPE_MARKDOWN, json);
        Request request = new Request.Builder()
                .url(Constant.NETDISK_URL)
                .post(body)
                .build();
        Log.i("info", "获取网盘信息的url：" + Constant.NETDISK_URL);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message = new Message();
                message.what = 404;
                mHandler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message message = new Message();
                message.what = 1;
                message.obj = response.body().string();
                mHandler.sendMessage(message);
            }
        });
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1011:
                    Toast.makeText(NetDisk2Activity.this, "上传图片成功！", Toast.LENGTH_SHORT).show();
                    fileDirLists.clear();
                    uploadPicCount = 0;
                    getData(spaceId, fullPath);
                    break;
                case 405:
                    Toast.makeText(NetDisk2Activity.this, "文件上传失败,请检查网络连接!", Toast.LENGTH_SHORT).show();
                    break;
                case 404:
                    anim.stop();
                    Toast.makeText(NetDisk2Activity.this, "数据请求失败,请检查网络", Toast.LENGTH_SHORT).show();
                    netdisk2_iv_loading.setBackgroundResource(R.mipmap.img_jiazaishibai);
                    break;
                case 1:
                    anim.stop();
                    String str = (String) msg.obj;
//                    Log.i("info", "网盘的未解析的数据=" + str);
                    netDisk = JsonUtils.StartNetDiskJson(str);
//                    Log.i("info", "网盘解析的数据：" + netDisk.toString());
                    if (netDisk.equals("null") || netDisk.equals("") || netDisk == null) {
                        Toast.makeText(NetDisk2Activity.this, "服务器繁忙,稍后重试...", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    List<FileDirList> lists = netDisk.getFileDirLists();
                    fileDirLists.addAll(lists);
                    if (fileDirLists.size() != 0) {
                        netdisk2_iv_loading.setVisibility(View.INVISIBLE);
                    } else {
                        netdisk2_iv_loading.setBackgroundResource(R.mipmap.img_meiwenjian);
                        netdisk2_iv_chuanshuliebiao.setVisibility(View.INVISIBLE);
                    }
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    //点击每一个griView：如果是文件夹，则进入文件夹；如果是文件，则打开文件
    private ListView.OnItemClickListener itemClickListener = new ListView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.i("info", "点击网盘文件的position：" + position + ",fileDirLists.size()：" + fileDirLists.size());
            if (position == fileDirLists.size()) {
                return;
            }
            int type = fileDirLists.get(position).getType();
            view.setTag(position);
            if (type == 1) {
                Intent intent = new Intent(NetDisk2Activity.this, NetDisk2Activity.class);
                intent.putExtra("fullPath", fileDirLists.get(position).getFullPath());
                intent.putExtra("ShortName", fileDirLists.get(position).getShortName());
                intent.putExtra("spaceId", fileDirLists.get(position).getSpaceUuid());
                intent.putExtra("isAdmin", isAdmin);
                intent.putExtra("isJgDisk", isJgDisk);
                startActivity(intent);
            } else if (type == 2) {
                openFile(view, fileDirLists);
            }
        }
    };

    public void openFile(View view, List<FileDirList> fileDirLists) {
        int index = (int) view.getTag();
        String name = null;
        String allname = fileDirLists.get(index).getShortName().toLowerCase();
        String prefix = "";
        if (fileDirLists.get(index).getScale() != null && !fileDirLists.get(index).getScale().equals("") && !fileDirLists.get(index).getScale().equals("null")) {
            String scale = fileDirLists.get(index).getScale();
            prefix = scale.substring(0, scale.indexOf("user"));//地址前缀
        }
        String ossPath = fileDirLists.get(index).getOssPath();//相对地址，需要把prefix加在前面
        String id = fileDirLists.get(index).getId();
        if (allname.contains(".")) {
            name = allname.substring(allname.lastIndexOf(".") + 1, allname.length());
        }
        Intent intent = null;
        switch (name) {
            case "doc":
            case "docx":
            case "txt":
            case "xls":
            case "xlsx":
                intent = new Intent(NetDisk2Activity.this, OpenTextFileActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("name", fileDirLists.get(index).getShortName().toLowerCase());
                intent.putExtra("type", 3);
                startActivity(intent);
                break;
            case "wav":
            case "mp3":
            case "wma":
            case "wva":
            case "ogg":
            case "ape":
            case "aif":
            case "au":
            case "ram":
            case "mmf":
            case "amr":
            case "aac":
            case "flac":
                intent = new Intent(NetDisk2Activity.this, OpenAudioActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("type", 3);
                startActivity(intent);
                break;
            case "ppt":
            case "pptx":
            case "pdf":
                Toast.makeText(NetDisk2Activity.this, "请下载后查看", Toast.LENGTH_SHORT).show();
                break;
            case "avi":
            case "mpg":
            case "mpeg":
            case "mov":
            case "rm":
            case "rmvb":
            case "mp4":
            case "3gp":
            case "flv":
                /**
                 *
                 *
                 * aaa
                 * 你啊个屁啊
                 */
                //跳到自己写的OpenVideoActivity
                //                        Intent intent = new Intent(Scan2Activity.this, OpenVideo2Activity.class);
                //                        intent.putExtra("path", url);
                //                        intent.putExtra("type", 4);
                //                        startActivity(intent);
                //利用手机自带的播放器播放视频
                String url = prefix + ossPath;
                Log.i("info", "视频路径=" + url);
                Uri video = Uri.parse(url);
                intent = new Intent(Intent.ACTION_VIEW);
                Log.i("info", "Uri之后的视频路径=" + video.toString());
                intent.setDataAndType(video, "video/*");
                startActivity(intent);
                break;
            case "bmp":
            case "gif":
            case "jpg":
            case "pic":
            case "png":
            case "tif":
            case "jpeg":
//                intent = new Intent(NetDisk2Activity.this, OpenPicActivity.class);
//                intent.putExtra("id", id);
//                intent.putExtra("type", 3);
//                startActivity(intent);
                intent = new Intent(NetDisk2Activity.this, SeePicActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("fileDirList", (Serializable) fileDirLists);
                startActivity(intent);
                break;
            default:
                intent = new Intent(NetDisk2Activity.this, OpenFailActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("type", 3);
                startActivity(intent);
                break;
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.netdisk2_iv_back:
            case R.id.netdisk2_tv_back:
                finish();
                break;
            case R.id.netdisk2_tv_selector:
                showorhidden(flag);
                flag = !flag;
                break;
            case R.id.item_four_selector_faxuexiquan2:
                if (fileDirLists.size() == 0) {
                    Toast.makeText(NetDisk2Activity.this, "文件夹是空", Toast.LENGTH_SHORT).show();
                } else {
                    if (FBlistSelected != null && FBlistSelected.size() != 0) {
                        Intent intent_faxuexiquan = new Intent(NetDisk2Activity.this, ReleaseActivity.class);
                        intent_faxuexiquan.putExtra("NetFile", (Serializable) FBlistSelected);
                        startActivity(intent_faxuexiquan);
                        FBlistSelected.clear();
                        showorhidden(true);
                    } else {
                        Toast.makeText(NetDisk2Activity.this, "您还没有选择任何文件呢！", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.item_four_selector_zhuanfa2:
                if (fileDirLists.size() == 0) {
                    Toast.makeText(NetDisk2Activity.this, "文件夹是空", Toast.LENGTH_SHORT).show();
                } else {
                    if (FBlistSelected != null && FBlistSelected.size() != 0) {
                        Intent intent_zhuanfa = new Intent(NetDisk2Activity.this, SelectContactsActivity.class);
                        intent_zhuanfa.putExtra("NetFile", (Serializable) FBlistSelected);
                        intent_zhuanfa.putExtra("from", "NetFile");
                        startActivity(intent_zhuanfa);
                        showorhidden(true);
                    } else {
                        Toast.makeText(NetDisk2Activity.this, "您还没有选择任何文件呢！", Toast.LENGTH_SHORT).show();
                    }
                }
                FBlistSelected.clear();
                break;
            case R.id.item_four_selector_download2:
                if (Build.VERSION.SDK_INT < 23){
                    download();
                } else {
                    AndPermission.with(this)
                            .requestCode(1014)
                            .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .send();
                }
                break;
            case R.id.item_four_selector_shanchu2:
                if (fileDirLists.size() == 0) {
                    Toast.makeText(NetDisk2Activity.this, "文件夹是空", Toast.LENGTH_SHORT).show();
                } else {
                    if (listSelected != null && listSelected.size() != 0) {
                        Intent intent_shanchu = new Intent(this, DeleteActivity.class);
                        intent_shanchu.putExtra("listSelected", (Serializable) listSelected);
                        startActivityForResult(intent_shanchu, REQUEST_CODE);
                        listSelected.clear();
                    } else {
                        Toast.makeText(NetDisk2Activity.this, "您还没有选择任何文件呢！", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.netdisk2_iv_chuanshuliebiao:
                if (Build.VERSION.SDK_INT < 23) {
                    chuanshuliebiao();
                } else {
                    AndPermission.with(this)
                            .requestCode(1015)
                            .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .send();
                }
                break;
            case R.id.netdisk2_iv_jiahao:
                TitlePopup titlePopup = new TitlePopup(NetDisk2Activity.this, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                titlePopup.addAction(new ActionItem(getResources().getDrawable(R.mipmap.ic_xinjianwenjianjia), "新建文件夹"));
                titlePopup.addAction(new ActionItem(getResources().getDrawable(R.mipmap.ic_shangchuanzhaopian), "上传图片"));
                titlePopup.addAction(new ActionItem(getResources().getDrawable(R.mipmap.ic_shangchuanziyuan), "上传视频"));
                titlePopup.setBackgroundDrawable(getResources().getDrawable(R.mipmap.img_shangchuan_android));
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                titlePopup.showAtLocation(v, Gravity.NO_GRAVITY, location[0] - (int) getResources().getDimension(R.dimen.view_90), location[1] - 460);
                Log.i("info", "location[1]=" + location[1] + "-----v.getHeight()=" + v.getHeight() + "-----" + " titlePopup.getHeight()=" + titlePopup.getHeight());
                titlePopup.setItemOnClickListener(new TitlePopup.OnPopmenuItemClicked() {
                    @Override
                    public void onItemClick(ActionItem item, int position) {
                        commitFile(item, position);
                    }
                });
                titlePopup.show(v);
                break;
        }
    }


    public void download() {
        if (fileDirLists.size() == 0) {
            Toast.makeText(NetDisk2Activity.this, "文件夹是空", Toast.LENGTH_SHORT).show();
        } else {
            if (listSelected != null && listSelected.size() != 0) {
                Intent intent_download = new Intent(this, TranActivity.class);
                intent_download.putExtra("listSelected", (Serializable) listSelected);
                startActivity(intent_download);
                listSelected.clear();
                showorhidden(true);
            } else {
                Toast.makeText(NetDisk2Activity.this, "您还没有选择任何文件呢！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void chuanshuliebiao() {
        Intent intent_tran = new Intent(NetDisk2Activity.this, TranActivity.class);
        intent_tran.putExtra("listSelected", (Serializable) listSelected);
        intent_tran.putExtra("upload", (Serializable) albumList);
        startActivity(intent_tran);
        showorhidden(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        AndPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults, listener);
    }

    public PermissionListener listener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode) {
            if (requestCode == 1014) {
                download();
            } else if (requestCode == 1015) {
                chuanshuliebiao();
            }
        }

        @Override
        public void onFailed(int requestCode) {
            if (requestCode == 1014 || requestCode == 1015) {
                Toast.makeText(NetDisk2Activity.this, "您未开放读写文件权限", Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * 上传文件
     *
     * @param item
     * @param position
     */
    public void commitFile(ActionItem item, int position) {
        switch (position) {
            case 0://新建文件夹
                Toast.makeText(NetDisk2Activity.this, "新建文件夹", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(NetDisk2Activity.this, CreateNewFileActivity.class);
                intent.putExtra("fullPath", fullPath);
                intent.putExtra("spaceId", spaceId);
                intent.putExtra("userId", userId);
                startActivityForResult(intent, CREATE_FILE);
                break;
            case 1://上传图片
                if (!ScanActivity.isWifiActive(this)) {
                    Toast.makeText(NetDisk2Activity.this, "当前处于非wifi状态,继续上传会产生流量费用!", Toast.LENGTH_SHORT).show();
                }
                albumList.clear();
                Intent intent_album = new Intent(NetDisk2Activity.this, AlbumActivity.class);
                intent_album.putExtra("pic", (Serializable) albumList);
                startActivityForResult(intent_album, UPLOAD_ALBUM);
                break;
            case 2://上传视频
                Toast.makeText(NetDisk2Activity.this, "敬请期待", Toast.LENGTH_SHORT).show();
//                Intent intent_video = new Intent(NetDisk2Activity.this, AllVideoActivity.class);
//                intent_video.putExtra("userId", userId);
//                intent_video.putExtra("spaceId", spaceId);
//                intent_video.putExtra("fullPath", fullPath);
//                startActivity(intent_video);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.i("info", "删除之前，fileDirLists的长度：" + fileDirLists.size());
                for (int i = positions.size() - 1; i >= 0; i--) {
                    for (int j = fileDirLists.size() - 1; j >= 0; j--) {
                        if (j == positions.get(i)) {
                            fileDirLists.remove(positions.get(i).intValue());
                        }
                    }
                }
                Log.i("info", "删除之后，fileDirLists的长度：" + fileDirLists.size());
                adapter.updateList(fileDirLists);
                showorhidden(true);
            }
        } else if (requestCode == CREATE_FILE) {
            if (resultCode == 0) {
                fileDirLists.clear();
                getData(spaceId, fullPath);
            }
        } else if (requestCode == UPLOAD_ALBUM) {
            if (resultCode == RESULT_OK) {
                albumList = (List<Object>) data.getSerializableExtra("list");
                Log.i("info", "即将上传的图片list集合：" + albumList.toString());
                if (albumList.size() == 0) {
                    Toast.makeText(NetDisk2Activity.this, "您没有上传任何文件", Toast.LENGTH_SHORT).show();
                } else {
                    for (int i = 0; i < albumList.size(); i++) {
                        Object object = albumList.get(i);
                        if (object instanceof String) {
                            String str = (String) object;
                            updateAlbum(str, userId, spaceId, fullPath, i, albumList.size());
                        }
                    }
                }
            }
        }
    }

    public void updateAlbum(final String fileData, final String userId, final String spaceId, final String fullPath, final int cur, final int length) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                executeUpdateAlbum(fileData, userId, spaceId, fullPath, cur, length);
            }
        }).start();
    }

    public void executeUpdateAlbum(String fileData, String userId, String spaceId, String fullPath, final int cur, final int length) {
        OkHttpClient client = new OkHttpClient();

        String url = String.format(Constant.STUDY_UP_FILE, userId, spaceId, fullPath);
        File file = new File(fileData);
        String name = fileData.substring(fileData.lastIndexOf("/") + 1, fileData.length());

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", name, RequestBody.create(MediaType.parse("image/png"), file))
                .build();
        Request request = new Request.Builder()
                .post(body)
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("info", "上传文件失败：" + e.getMessage());
                mHandler.sendEmptyMessage(405);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("info", "上传图片，当前页：" + cur + "；共" + length + "页。上传图片返回的code:" + response.code());
                uploadPicCount++;
                if (uploadPicCount == length) {
                    Message msg = new Message();
                    Log.i("info", "上传图片：uploadPicCount=" + uploadPicCount + "；length=" + length);
                    msg.what = 1011;
                    mHandler.sendMessage(msg);
                }
            }
        });
    }

    //根据情况判断“选择”“取消”和弹窗的显示，隐藏
    public void showorhidden(boolean flag) {
        if (flag == false) {
            netdisk2_tv_selector.setText("取消");
            netdisk2_ll_four_selector.setVisibility(View.VISIBLE);
        } else if (flag == true) {
            netdisk2_tv_selector.setText("选择");
            netdisk2_ll_four_selector.setVisibility(View.INVISIBLE);
            //重置选中文件或图片的图标
            if (fileDirLists.size() != 0) {
                try {
                    for (int i = positions.size() - 1; i >= 0; i--) {
                        FileDirList file = fileDirLists.get(positions.get(i));
                        file.setIsCheck(false);
                        fileDirLists.set(positions.get(i), file);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //清空选中的list集合
            listSelected.clear();
            albumList.clear();
            positions.clear();
            //重置选择文件的计数
            count = 0;
            isMax = false;
            Log.i("info", "点击取消按钮，清空listSelected集合：" + listSelected.toString() + "，选择的文件count置0:" + count);
        }
        adapter.setSelector_flag(!flag);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("NetDisk2Activity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("NetDisk2Activity");
        MobclickAgent.onPause(this);
    }
}

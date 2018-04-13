package net.iclassmate.bxyd.ui.fragment.tran;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.adapter.teachlearn.FragmentTranAdapter;
import net.iclassmate.bxyd.bean.attention.Attention;
import net.iclassmate.bxyd.bean.netdisk.FileDirList;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.ui.activitys.study.openfile.OpenAudioActivity;
import net.iclassmate.bxyd.ui.activitys.study.openfile.OpenFailActivity;
import net.iclassmate.bxyd.ui.activitys.study.openfile.OpenPicActivity;
import net.iclassmate.bxyd.ui.activitys.study.openfile.OpenVideoActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by xydbj on 2016.7.17.
 */
public class DownLoadFragment extends LazyFragment implements View.OnClickListener {
    private ListView lv;
    private List<Attention> list;
    private FragmentTranAdapter adapter;
    private List<FileDirList> fileDirListList;
    private String SDPath = Environment.getExternalStorageDirectory() + "/xyddownload/";
    private int selectedposition;

    private static final int REQUEST_CODE = 10;
    private static final int RESULT_CODE = 9;

    public List<FileDirList> getFileDirListList() {
        return fileDirListList;
    }

    public void setFileDirListList(List<FileDirList> fileDirListList) {
        this.fileDirListList = fileDirListList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_download, container, false);
        fileDirListList = getFileDirListList();
        initView(view);
        if (fileDirListList != null) {
            Log.i("info", "在DownLoadFragment中的数据：" + fileDirListList.toString());
            for (int i = 0; i < fileDirListList.size(); i++) {
                String ID = fileDirListList.get(i).getId();
                String shortName = fileDirListList.get(i).getShortName();
                startDownLoad(ID, shortName, i);
            }
        }
        return view;
    }

    public void initView(View view) {
        lv = (ListView) view.findViewById(R.id.fragment_download_lv);
        list = new ArrayList<>();
        adapter = new FragmentTranAdapter(getActivity(), list);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(onItemClickListener);

        File file = new File(SDPath);
        getFileList(file);
        if (fileDirListList != null) {
            setData();
        }
        adapter.setDeleteChecked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedposition = (int) v.getTag();
                Intent intent = new Intent(getActivity(), DeleteFileActivity.class);
                intent.putExtra("fileName", list.get(selectedposition).getUserName());
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_CODE) {
                deleteFile();
            }
        }
    }

    public void deleteFile() {
        String url = list.get(selectedposition).getUserIcon();
        File file = new File(url);
        //为什么注释了呢？因为如果不注释掉的话，进入下载界面，无法删除刚下载的文件。
//        if (file == null || !file.exists() || file.isDirectory()) {
//            return;
//        }
        file.delete();
        list.remove(selectedposition);
        Log.i("info", "选择删除的selectedposition:" + selectedposition);
        adapter.updateView(list);
    }

    public AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            SDPath = Environment.getExternalStorageDirectory() + "/xyddownload/";
            String fileName = list.get(position).getUserName();
            String allName = SDPath + fileName;
            String name = allName.toLowerCase();
            if (name.contains(".")) {
                name = name.substring(name.lastIndexOf(".") + 1, name.length());
            }
            Intent intent = null;
            switch (name) {
                case "txt":
                    intent = new Intent("android.intent.action.VIEW");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri uri_txt = Uri.fromFile(new File(allName));
                    intent.setDataAndType(uri_txt, "text/plain");//  文档格式
                    startActivity(intent);
                    break;
                case "xlsx":
                    intent = new Intent("android.intent.action.VIEW");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri uri_xlsx = Uri.fromFile(new File(allName));
                    intent.setDataAndType(uri_xlsx, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");//  文档格式
                    startActivity(intent);
                    break;
                case "xls":
                case "xlt":
                    intent = new Intent("android.intent.action.VIEW");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri uri_xlt = Uri.fromFile(new File(allName));
                    intent.setDataAndType(uri_xlt, "application/vnd.ms-excel");//  文档格式
                    startActivity(intent);
                    break;
                case "doc":
                case "dot":
                    intent = new Intent("android.intent.action.VIEW");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri uri_doc = Uri.fromFile(new File(allName));
                    intent.setDataAndType(uri_doc, "application/msword");//  文档格式
                    startActivity(intent);
                    break;
                case "docx"://ok
                    intent = new Intent("android.intent.action.VIEW");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri uri_docx = Uri.fromFile(new File(allName));
                    intent.setDataAndType(uri_docx, "application/vnd.openxmlformats-officedocument.wordprocessingml.document");//  文档格式
                    startActivity(intent);
                    break;
                case "ppt":
                    intent = new Intent("android.intent.action.VIEW");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri uri_ppt = Uri.fromFile(new File(allName));
                    intent.setDataAndType(uri_ppt, "application/vnd.ms-powerpoint");//  文档格式
                    startActivity(intent);
                case "pptx"://ok
                    intent = new Intent("android.intent.action.VIEW");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri uri_pptx = Uri.fromFile(new File(allName));
                    intent.setDataAndType(uri_pptx, "application/vnd.openxmlformats-officedocument.presentationml.presentation");//  文档格式
                    startActivity(intent);
                    break;
                case "pdf":
                    intent = new Intent("android.intent.action.VIEW");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri uri_pdf = Uri.fromFile(new File(allName));
                    intent.setDataAndType(uri_pdf, "application/pdf");//  文档格式
                    startActivity(intent);
                    break;
                case "mp3":
                    intent = new Intent("android.intent.action.VIEW");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri uri_mp3 = Uri.fromFile(new File(allName));
                    intent.setDataAndType(uri_mp3, "audio/mpeg");//  文档格式
                    startActivity(intent);
                    break;
                case "wav":
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
                    intent = new Intent(getActivity(), OpenAudioActivity.class);
                    Log.i("info", "打开本地文件的文件全路径：" + allName);
                    intent.putExtra("filePath", allName);
                    intent.putExtra("type", 3);
                    intent.putExtra("fileName", fileName);
                    startActivity(intent);
                    break;
                case "bmp":
                case "gif":
                case "jpg":
                case "pic":
                case "png":
                case "tif":
                case "jpeg":
                    intent = new Intent(getActivity(), OpenPicActivity.class);
                    intent.putExtra("filePath", allName);
                    Log.i("info", "打开本地文件的文件全路径：" + allName);
                    intent.putExtra("type", 3);
                    startActivity(intent);
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
                    intent = new Intent(getActivity(), OpenVideoActivity.class);
                    String path = Environment.getExternalStorageDirectory().getPath() + "/xyddownload/" + fileName;
                    intent.putExtra("path", path);
                    intent.putExtra("type", 3);
                    intent.putExtra("fileName", fileName);
                    Log.i("info", "打开本地视频的路径：" + path);
                    startActivity(intent);
                    break;
                default:
                    intent = new Intent(getActivity(), OpenFailActivity.class);
                    intent.putExtra("filePath", allName);
                    Log.i("info", "打开本地文件的文件全路径：" + allName);
                    intent.putExtra("type", 3);
                    startActivity(intent);
                    break;
            }
        }
    };

    public void setData() {
        //获取当前时间
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = format.format(new Date());

        Attention attention = null;
        for (int i = 0; i < fileDirListList.size(); i++) {
            attention = new Attention();
            attention.setUserName(fileDirListList.get(i).getShortName());//下载文件的名称
            attention.setUserPinyin(date);//下载文件的时间
            attention.setUserIcon(fileDirListList.get(i).getScale());
            attention.setIndex(fileDirListList.get(i).getType());//区别文件的type
            attention.setSubSpaceId(fileDirListList.get(i).getFullPath());
            attention.setStatetype(0);
            list.add(attention);
        }
        adapter.notifyDataSetChanged();
    }


    @Override
    protected void lazyLoad() {

    }

    @Override
    public String getFragmentTitle() {
        return "下载列表";
    }

    //下载单个文件
    public void startDownLoad(final String ID, final String shortName, final int i) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                execute(ID, shortName, i);
            }
        }).start();
    }

    public void execute(String ID, final String shortName, final int i) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(Constant.DOWNLOAD_URL + ID)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("info", "下载文件失败:" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buff = new byte[1024];
                int len = 0;
                FileOutputStream fos = null;
                String SDPath = Environment.getExternalStorageDirectory() + "/xyddownload/";
                File savefile = new File(SDPath);
                if (!savefile.exists()) {
                    savefile.mkdirs();
                }
                try {
                    is = response.body().byteStream();//获得数据流
                    long total = response.body().contentLength();//获得数据流的长度，以便跟踪下载进度
                    File file = new File(savefile, shortName);//初始化一个file,把下载文件存放的位置及名称存进去
                    fos = new FileOutputStream(file);//用流的形式把获取的数据写入到file中
                    int sum = 0;
                    while ((len = is.read(buff)) != -1) {
                        fos.write(buff, 0, len);
                        sum += len;
//                        int progress = (int) (((float) sum / total) * 100);
//                        Message msg = mHandler.obtainMessage();
//                        msg.what = 1;
//                        msg.arg1 = progress;
//                        mHandler.sendMessage(msg);
                    }
                    fos.flush();
                    Log.i("info", "文件下载成功" + shortName);
                    Message msg = new Message();
                    msg.obj = shortName;
                    msg.what = 2;
                    mHandler.sendMessage(msg);
                } catch (IOException e) {
                    Log.i("info", "文件下载失败：" + e.getMessage());
                    Toast.makeText(getActivity(), "文件下载失败，请重试", Toast.LENGTH_SHORT).show();
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 2:
                    String name = (String) msg.obj;
                    Log.i("info", "收到消息=" + name);
                    int index = -1;
                    Attention attention = null;
                    for (int i = 0; i < list.size(); i++) {
                        attention = list.get(i);
                        if (attention.getUserName().equals(name)) {
                            index = i;
                            break;
                        }
                    }
                    if (index == -1) {
                        return;
                    }
                    attention.setIsDownload(true);
                    list.set(index, attention);
                    adapter.notifyDataSetChanged();
                    Log.i("info", "更新适配器");
                    break;
            }
        }
    };

    //从文件夹中获取已经下载的文件，显示到列表中
    public void getFileList(File file) {
        if (!file.exists()) {
            return;
        }
        File[] files = file.listFiles();
        if (files == null || files.length == 0) {
            return;
        }
        Attention attention = null;
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                getFileList(files[i]);
            } else if (files[i].isFile()) {
                String fileName = files[i].getName();
                String filePath = files[i].getAbsolutePath();

                long time = files[i].lastModified() / 1000;
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date(time * 1000);
                String fileTime = format.format(date);

//                Log.i("info", "文件路径：" + filePath + ",文件名称：" + fileName + ",文件时间：" + fileTime);
                attention = new Attention();
                attention.setUserName(fileName);//下载文件的名称
                attention.setUserPinyin(fileTime);//获取最后修改文件的时间
                attention.setUserIcon(filePath);//文件夹中文件的全路径
                attention.setIndex(2);//区分文件夹的type
                attention.setSubSpaceId(fileName);//类似于fullPath
                attention.setStatetype(1);
                attention.setIsDownload(true);
                list.add(attention);
            }
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("DownLoadFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("DownLoadFragment");
    }
}

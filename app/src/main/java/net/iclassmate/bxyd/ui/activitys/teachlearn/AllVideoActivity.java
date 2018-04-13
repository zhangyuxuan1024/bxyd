package net.iclassmate.bxyd.ui.activitys.teachlearn;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.adapter.teachlearn.AllVideoAdapter;
import net.iclassmate.bxyd.bean.netdisk.VideoInfo;
import net.iclassmate.bxyd.constant.Constant;
import net.iclassmate.bxyd.ui.activitys.study.ScanActivity;
import net.iclassmate.bxyd.utils.BlockUpLoad;
import net.iclassmate.bxyd.utils.NetWorkUtils;
import net.iclassmate.bxyd.view.FullGridView;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;


/**
 * 查询手机中的所有视频文件
 * Created by xydbj on 2016.9.22.
 */
public class AllVideoActivity extends Activity implements OnClickListener {

    public static List<VideoInfo> allVideolist = null;
    public List<VideoInfo> selectedList = null;
    public AllVideoAdapter adapter;
    public Context mContext;
    public FullGridView fgv;
    private boolean isCanSelected;
    private String userId, spaceId, fullPath;
    private TextView tv_cancel, tv_sure;
    private ProgressBar pb;
    private boolean isUploading;

    public static final String action = "AllVideoActivity";


    private long blockSize;
    private String fieldId;
    private static final int GET_BASENUM = 1001;
    private List<Map<String, String>> videoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allvideo);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        spaceId = intent.getStringExtra("spaceId");
        fullPath = intent.getStringExtra("fullPath");
        Log.i("info", "分快上传的参数：userId=" + userId + ";spaceId=" + spaceId + ";fullPath=" + fullPath);
        initView();

        if (!NetWorkUtils.isNetworkAvailable(this)) {
            Toast.makeText(AllVideoActivity.this, "请检查网络连接！", Toast.LENGTH_LONG).show();
            tv_sure.setClickable(false);
            return;
        }

        getVideoFile(allVideolist, Environment.getExternalStorageDirectory());
        Log.i("info", "检索出手机中的视频文件：" + allVideolist.toString());
        adapter.notifyDataSetChanged();
        cutFile();
    }

    public void initView() {
        mContext = this;
        isCanSelected = true;
        pb = (ProgressBar) findViewById(R.id.allvideo_pb);
        pb.setMax(100);
        tv_cancel = (TextView) findViewById(R.id.allvideo_cancel);
        tv_sure = (TextView) findViewById(R.id.allvideo_sure);
        tv_sure.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);

        allVideolist = new ArrayList<>();
        selectedList = new ArrayList<>();
        adapter = new AllVideoAdapter(mContext, allVideolist);
        fgv = (FullGridView) findViewById(R.id.allvideo_fgv);
        fgv.setAdapter(adapter);
        fgv.setOnItemClickListener(onItemClickListener);

        //选择上传的视频存到集合中
        adapter.setOnSelectedListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = (int) v.getTag();
                for (int i = 0; i < allVideolist.size(); i++) {
                    VideoInfo vi = allVideolist.get(i);
                    String tag = vi.getPath();
                    if (i == index) {
                        boolean flag = !vi.isSelected();
                        if (flag && !isCanSelected) {
                            Toast.makeText(mContext, "最多选择1个文件", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        allVideolist.set(index, vi);
                        vi.setIsSelected(flag);
                        adapter.notifyDataSetChanged();
                        if (flag) {
                            selectedList.add(vi);
                        } else {
                            for (int j = 0; j < selectedList.size(); j++) {
                                if (tag.equals(selectedList.get(j).getPath())) {
                                    selectedList.remove(j);
                                }
                            }
                        }
                    }
                }
                Log.i("info", "选中要上传的视频集合：" + selectedList.toString());
                if (selectedList.size() >= 1) {
                    isCanSelected = false;
                } else {
                    isCanSelected = true;
                }
            }
        });
    }

    //点击视频，播放
    public AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            File file = new File(allVideolist.get(position).getPath());
            Uri uri = Uri.fromFile(file);
            Log.i("info", "Uri之后的视频路径=" + uri.toString());
            intent.setDataAndType(uri, "video/*");
            startActivity(intent);

        }
    };

    /**
     * 获取视频缩略图1
     *
     * @param path
     * @return
     */
    public static Bitmap getVideoThumb(String path) {
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(path);
        return media.getFrameAtTime();
    }

    /**
     * 获取视频缩略图2
     *
     * @param path
     * @return
     */
    public static Bitmap getVideoThumb2(String path) {
        return ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
    }

    /**
     * 获取视频时长
     *
     * @param mUri
     * @return
     */
    public static String getRingDuring(String mUri) throws IOException {
        String duration = null;
        String allTime = "--:--";
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            if (mUri != null) {
                HashMap<String, String> headers = null;
                if (headers == null) {
                    headers = new HashMap<String, String>();
                    headers.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 4.4.2; zh-CN; MW-KW-001 Build/JRO03C) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 UCBrowser/1.0.0.001 U4/0.8.0 Mobile Safari/533.1");
                }
                mmr.setDataSource(mUri, headers);
            }

            duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            allTime = getVideoTime(duration);
        } catch (Exception ex) {
            Log.i("info", "MediaMetadataRetriever exception " + ex);
        } finally {
            mmr.release();
        }
        if (allTime.equals("--:--")) {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(mUri);
            mediaPlayer.prepare();
            allTime = getVideoTime(mediaPlayer.getDuration());
        }
        return allTime;
    }

    /**
     * 将时间值转换成00：00：00形式
     *
     * @param time
     * @return
     */
    public static String getVideoTime(String time) {
        int hour = 0;
        int minute = 0;
        int second = 0;

        int allTime = Integer.parseInt(time) / 1000;
        if (allTime <= 0) {
            return "00:00:00";
        } else if (allTime > 0 && allTime < 60) {
            return "00:00:" + allTime;
        } else if (allTime >= 60 && allTime <= 60 * 60) {
            minute = allTime / 60;//得到商
            second = allTime % 60;//得到余数
            return "00:" + minute + ":" + second;
        } else if (allTime >= 60 * 60) {
            hour = allTime / 60 / 60;
            if (hour >= 99) {
                return "99:59:59";
            }
            minute = (allTime / 60) % 60;
            second = allTime & 60;
            return hour + ":" + minute + ":" + second;
        }
        return "--:--";
    }

    public static String getVideoTime(int time) {
        int hour = 0;
        int minute = 0;
        int second = 0;

        int allTime = time / 1000;
        if (allTime <= 0) {
            return "00:00:00";
        } else if (allTime > 0 && allTime < 60) {
            return "00:00:" + allTime;
        } else if (allTime >= 60 && allTime <= 60 * 60) {
            minute = allTime / 60;//得到商
            second = allTime % 60;//得到余数
            return "00:" + minute + ":" + second;
        } else if (allTime >= 60 * 60) {
            hour = allTime / 60 / 60;
            if (hour >= 99) {
                return "99:59:59";
            }
            minute = (allTime / 60) % 60;
            second = allTime & 60;
            return hour + ":" + minute + ":" + second;
        }
        return "--:--";
    }


    public static String bitmap2File(Bitmap bitmap, String name) {
        File file = new File(Environment.getExternalStorageDirectory() + name);
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            return null;
        }
        return file.getAbsolutePath();
    }


    /**
     * 遍历文件夹，筛选出视频文件
     *
     * @param list
     * @param file
     */
    public void getVideoFile(final List<VideoInfo> list, File file) {
        file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                String name = file.getName();
                int i = name.indexOf(".");
                if (i != -1) {
                    name = name.substring(i);
                    if (name.equalsIgnoreCase(".mp4")
                            || name.equalsIgnoreCase(".3gp")
                            || name.equalsIgnoreCase(".wmv")
                            || name.equalsIgnoreCase(".ts")
                            || name.equalsIgnoreCase(".rmvb")
                            || name.equalsIgnoreCase(".mov")
                            || name.equalsIgnoreCase(".m4v")
                            || name.equalsIgnoreCase(".avi")
                            || name.equalsIgnoreCase(".m3u8")
                            || name.equalsIgnoreCase(".3gpp")
                            || name.equalsIgnoreCase(".3gpp2")
                            || name.equalsIgnoreCase(".mkv")
                            || name.equalsIgnoreCase(".flv")
                            || name.equalsIgnoreCase(".divx")
                            || name.equalsIgnoreCase(".f4v")
                            || name.equalsIgnoreCase(".rm")
                            || name.equalsIgnoreCase(".asf")
                            || name.equalsIgnoreCase(".ram")
                            || name.equalsIgnoreCase(".mpg")
                            || name.equalsIgnoreCase(".v8")
                            || name.equalsIgnoreCase(".swf")
                            || name.equalsIgnoreCase(".m2v")
                            || name.equalsIgnoreCase(".asx")
                            || name.equalsIgnoreCase(".ra")
                            || name.equalsIgnoreCase(".ndivx")
                            || name.equalsIgnoreCase(".xvid")) {
                        VideoInfo vi = new VideoInfo();
                        vi.setDisplayName(file.getName());
                        vi.setPath(file.getAbsolutePath());
                        vi.setAllSize(getFileTime(file.length()));
                        vi.setBitmapThumb(getVideoThumb2(file.getAbsolutePath()));
                        try {
                            vi.setAllTime(getRingDuring(file.getAbsolutePath()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        list.add(vi);
                        return true;
                    }
                } else if (file.isDirectory()) {
                    getVideoFile(list, file);
                }
                return false;
            }
        });
    }

    public String getFileTime(long time) {
        if (time < 1024 && time > 0) {
            return time + "B";
        } else if (time >= 1024 && time < 1024 * 1024) {
            return time / 1024 + "KB";
        } else if (time >= 1024 * 1024 && time < 1024 * 1024 * 1024) {
            return time / 1024 / 1024 + "MB";
        } else if (time >= 1024 * 1024 * 1024 && time < 1024 * 1024 * 1024 * 1024) {
            return time / 1024 / 1024 / 1024 + "GB";
        } else {
            return null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.allvideo_cancel:
                selectedList.clear();
                finish();
                break;
            case R.id.allvideo_sure:
                if (!NetWorkUtils.isNetworkAvailable(this)) {
                    Toast.makeText(AllVideoActivity.this, "请检查网络连接!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!ScanActivity.isWifiActive(this)) {
                    Toast.makeText(AllVideoActivity.this, "您现在是在非wifi条件下，继续上传会产生流量费用", Toast.LENGTH_SHORT).show();
                }

                if (isUploading) {
                    Toast.makeText(AllVideoActivity.this, "正在上传视频，请稍后...", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (selectedList.size() == 0) {
                    Toast.makeText(AllVideoActivity.this, "请选择需要上传的视频...", Toast.LENGTH_SHORT).show();
                    return;
                }
                //这个集合里只有一项！
                uploadVideo(selectedList.get(0), userId, spaceId, fullPath);
                break;
        }
    }

    public void uploadVideo(final VideoInfo vi, final String userId, final String spaceId, final String fullPath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                execute(vi, userId, spaceId, fullPath);
            }
        }).start();
        isUploading = true;
    }

    public void execute(VideoInfo vi, String userId, String spaceId, String fullPath) {
        OkHttpClient client = new OkHttpClient();
        String url = String.format(Constant.STUDY_UP_FILE, userId, spaceId, fullPath);
        File file = new File(vi.getPath());
        String name = vi.getDisplayName();
        RequestBody body = new MultipartBody.Builder()
                .addFormDataPart("file", name, RequestBody.create(MediaType.parse("video/mp4"), file))
                .build();

        ProgressRequestBody progressRequestBody = new ProgressRequestBody(body, progressListener);

        Request request = new Request.Builder()
                .post(progressRequestBody)
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("info", "上传视频失败：" + e.getMessage());
                mHandler.sendEmptyMessage(404);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("info", "上传视频返回的code:" + response.code());
                Message msg = new Message();
                msg.what = response.code();
                mHandler.sendMessage(msg);
            }
        });
    }

    //通过实现进度回调接口中的方法，来显示进度
    private ProgressListener progressListener = new ProgressListener() {
        @Override
        public void update(long bytesRead, long contentLength, boolean done) {
            int progress = (int) (100.0 * bytesRead / contentLength);
            pb.setProgress(progress);
        }
    };

    public class ProgressRequestBody extends RequestBody {

        //实际的待包装请求体
        private final RequestBody requestBody;
        //进度回调接口
        private final ProgressListener progressListener;
        //包装完成的BufferedSink
        private BufferedSink bufferedSink;

        /**
         * 构造函数，赋值
         *
         * @param requestBody      待包装的请求体
         * @param progressListener 回调接口
         */
        public ProgressRequestBody(RequestBody requestBody, ProgressListener progressListener) {
            this.requestBody = requestBody;
            this.progressListener = progressListener;
        }

        /**
         * 重写调用实际的响应体的contentType
         *
         * @return MediaType
         */
        @Override
        public MediaType contentType() {
            return requestBody.contentType();
        }

        /**
         * 重写调用实际的响应体的contentLength
         *
         * @return contentLength
         * @throws IOException 异常
         */
        @Override
        public long contentLength() throws IOException {
            return requestBody.contentLength();
        }

        /**
         * 重写进行写入
         *
         * @param sink BufferedSink
         * @throws IOException 异常
         */
        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            if (bufferedSink == null) {
                //包装
                bufferedSink = Okio.buffer(sink(sink));
            }
            //写入
            requestBody.writeTo(bufferedSink);
            //必须调用flush，否则最后一部分数据可能不会被写入
            bufferedSink.flush();

        }

        /**
         * 写入，回调进度接口
         *
         * @param sink Sink
         * @return Sink
         */
        private Sink sink(Sink sink) {
            return new ForwardingSink(sink) {
                //当前写入字节数
                long bytesWritten = 0L;
                //总字节长度，避免多次调用contentLength()方法
                long contentLength = 0L;

                @Override
                public void write(Buffer source, long byteCount) throws IOException {
                    super.write(source, byteCount);
                    if (contentLength == 0) {
                        //获得contentLength的值，后续不再调用
                        contentLength = contentLength();
                    }
                    //增加当前写入的字节数
                    bytesWritten += byteCount;
                    //回调
                    progressListener.update(bytesWritten, contentLength, bytesWritten == contentLength);
                }
            };
        }
    }

    //进度回调接口
    interface ProgressListener {
        void update(long bytesRead, long contentLength, boolean done);
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_BASENUM:

                    break;
                case 200:
                    Toast.makeText(AllVideoActivity.this, "上传视频成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(action);
                    sendBroadcast(intent);
                    AllVideoActivity.this.finish();
                    break;
                case 404:
                    Toast.makeText(AllVideoActivity.this, "请检查网络!", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(AllVideoActivity.this, "上传视频失败", Toast.LENGTH_SHORT).show();
                    AllVideoActivity.this.finish();
                    break;
            }
        }
    };

    /**
     * @param userId
     * @param spaceId
     * @param fullPath
     * @param fieldId
     * @param chunks
     * @param chunk:   当前是第几片
     * @param md5:     当前片的md5
     *                 http://space-new.iclassmate.cn:10000/fs/api/v1/simleBlockUpload
     *                 ?fieldId=64671eecf7534857bf29828412a7f1a2
     *                 &chunks=12
     *                 &chunk=3
     *                 &md5=7521507dbac87523973529eeb5d5ce86
     *                 &userId=a2bbac60909646318814dce601b0b721
     *                 &spaceId=fc9c2a5ef6684080b44fd161a9a84aa5
     *                 &fullPath=%2F
     */
    public String getUpLoadUrl(String fileName, String fieldId, int chunks, int chunk, String md5, String userId, String spaceId, String fullPath) {
        String url = String.format(Constant.BLOCK_UPLOAD, fileName, fieldId, chunks, chunk, md5, userId, spaceId, fullPath);
        return url;
    }

    public void cutFile() {
        BlockUpLoad.cutFile("/storage/emulated/0/xyddownload/bxyd000000v.mp4");
        final String tempath = Environment.getExternalStorageDirectory() + "/xyddownload/temp";
        fieldId = BlockUpLoad.getUUID();
        File file = new File(tempath);
        getFileList(file, videoList);
        Log.i("info", "分块之后的videoList=" + videoList.toString());
        for (int i = 0; i < videoList.size(); i++) {
            String fileName = videoList.get(i).keySet().toString();
            final int index = Integer.parseInt(fileName.substring(fileName.length() - 2, fileName.length() - 1));
            final String fileName2 = fileName.substring(1, fileName.length() - 1);
            if (i == index) {
                Log.i("info", "i=" + i + ";index=" + index + ";fileName2=" + fileName2);
                String md5 = videoList.get(i).values().toString();
                md5 = md5.substring(1, md5.length() - 1);
                final String url = getUpLoadUrl("bxyd000000v.mp4", fieldId, videoList.size(), i, md5, userId, spaceId, fullPath);
                Log.i("info", "分块上传的url:" + url);
                final int finalI = i;
                final String finalMd = md5;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        File file = new File(tempath, fileName2.substring(fileName2.lastIndexOf("/") + 1, fileName2.length()));
                        if (!file.exists()) {
                            Log.i("info", "文件不存在");
                            return;
                        }
                        Log.i("info", tempath + "---" + file + "---" + fileName2.substring(fileName2.lastIndexOf("/") + 1, fileName2.length()));
                        OkHttpClient client = new OkHttpClient();
                        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
                        RequestBody body = new MultipartBody.Builder()
                                .addFormDataPart("file---", fileName2.substring(fileName2.lastIndexOf("/") + 1, fileName2.length()), fileBody)
                                .addFormDataPart("fileName", "bxyd000000v.mp4")
                                .addFormDataPart("fielId", fieldId)
                                .addFormDataPart("chunks", 12 + "")
                                .addFormDataPart("chunk", index + "")
                                .addFormDataPart("md5", finalMd)
                                .addFormDataPart("userId", userId)
                                .addFormDataPart("spaceId", spaceId)
                                .addFormDataPart("fullPath", fullPath)
                                .build();

//                        ProgressRequestBody progressRequestBody = new ProgressRequestBody(body, progressListener);
                        Request request = new Request.Builder()
                                .post(body)
                                .url(url)
                                .build();
                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.i("info", "分块上传失败：" + e.getMessage());
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                Log.i("info", "返回的code：" + response.code() + ";返回的body：" + response.body().string());
                            }
                        });
                    }
                }).start();
            }
            return;
        }
    }

    //List<Map<String,String>> list:第一个String是：filePath  第二个String是：md5
    private void getFileList(File path, List<Map<String, String>> list) {
        Map<String, String> map = new HashMap<>();
        //如果是文件夹的话
        if (path.isDirectory()) {
            //返回文件夹中有的数据
            File[] files = path.listFiles();
            //先判断下有没有权限，如果没有权限的话，就不执行了
            if (null == files)
                return;
            for (int i = 0; i < files.length; i++) {
                getFileList(files[i], list);
            }
        }
        //如果是文件的话直接加入
        else {
            //进行文件的处理
            String filePath = path.getAbsolutePath();
            //文件名
            String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
            //添加
            String md5 = BlockUpLoad.getMD5Checksum(filePath);

            map.put(filePath, md5);

            list.add(map);
        }
    }
}
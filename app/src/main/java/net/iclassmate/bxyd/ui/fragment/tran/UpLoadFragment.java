package net.iclassmate.bxyd.ui.fragment.tran;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.adapter.teachlearn.FragmentUploadAdapter;
import net.iclassmate.bxyd.bean.UploadFile;
import net.iclassmate.bxyd.bean.attention.Attention;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xydbj on 2016.7.17.
 */
public class UpLoadFragment extends LazyFragment {

    private ListView lv;
    private List<Attention> list;
    private FragmentUploadAdapter uploadAdapter;
    private List<Object> albumList;
    private List<UploadFile> uploadFileList;

    public List<Object> getAlbumList() {
        return albumList;
    }

    public void setAlbumList(List<Object> albumList) {
        this.albumList = albumList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload, container, false);
        initView(view);
        albumList = getAlbumList();
        Log.i("info", "albumList=" + albumList);
        setData();
        Log.i("info", "uploadFileList=" + uploadFileList);
        return view;
    }

    public void initView(View view) {
        albumList = new ArrayList<>();
        uploadFileList = new ArrayList<>();
        lv = (ListView) view.findViewById(R.id.fragment_upload_lv);
        list = new ArrayList<>();
        uploadAdapter = new FragmentUploadAdapter(getActivity(), uploadFileList);
        lv.setAdapter(uploadAdapter);
    }

    public void setData() {
        for (int i = 0; i < albumList.size(); i++) {
            UploadFile uploadFile = new UploadFile();
            Object object = albumList.get(i);
            if (object instanceof String) {
                String string = (String) object;
                uploadFile.setFileName(string.substring(string.lastIndexOf("/"), string.length()));
                uploadFile.setFileBitmapIcon(getImageThumbnail(string, 75, 75));
            }
            uploadFileList.add(uploadFile);
        }
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    public String getFragmentTitle() {
        return "上传列表";
    }

    //根据本地图片路径，获取缩略图
    private Bitmap getImageThumbnail(String imagePath, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false; // 设为 false
        // 计算缩放比
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }
}

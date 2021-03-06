package net.iclassmate.bxyd.utils;

import android.os.Environment;
import android.util.Log;

import net.iclassmate.bxyd.constant.Constant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by xydbj on 2016.10.26.
 */
public class BlockUpLoad {

    /**
     * 获取分块的大小
     *
     * @return
     */
    public static Long getBlockSize() {
        long blockSize = 0;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(Constant.GET_BLOCK_SIZE)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                String result = response.body().string();
                blockSize = Long.parseLong(result);
            } else {
                blockSize = 0;
            }
            if (response != null) {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return blockSize;
    }

    /**
     * 生成fieldId,用于simpleBlockUpload中的fieldId参数
     *
     * @return
     */
    public static String getFieldId() {
        String result = null;
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS);
        OkHttpClient client = builder.build();
        Request request = new Request.Builder()
                .get()
                .url(Constant.GETFIELD_ID)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                result = response.body().string();
            } else {
                result = "404";
            }
            if (response != null) {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    //文件切块方法
    public static void cutFile(String filePath) {
//        String fieldId = getUUID();
        File file = new File(filePath);
        int chunkSize = 2 * 1024 * 1024;
        try {
            InputStream inputStream = new FileInputStream(file);
            long totalSize = inputStream.available();
            //文件分块总块数
            int chunks = (int) Math.ceil(inputStream.available() / (double) chunkSize);
            //检测当前上传的是第几块
            int chunk = 0;
            String partPath = Environment.getExternalStorageDirectory().getPath() + "/xyddownload/temp";
            Log.i("info", "创建临时目录的路径：" + partPath);
            File file2 = new File(partPath);
            if (!file2.exists()) {
                file2.mkdirs();
            }
            for (int i = chunk; i < chunks; i++) {
                UUID uuid = UUID.randomUUID();
                String str = uuid.toString();
                String md5 = str.replace("-", "");
                String partFileName = partPath + File.separator + md5 + createStrBlockIndex(chunk);
                int readSize = chunkSize;
                if (i == chunks - 1) {
                    readSize = (int) (totalSize - chunkSize * (chunks - 1));
                }
                byte[] data = new byte[readSize];
                inputStream.skip(0);
                int len = inputStream.read(data);
                FileOutputStream out = new FileOutputStream(new File(partFileName));
                out.write(data, 0, len);
                out.flush();
                out.close();
                chunk++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //获取fieldId
    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString();
        String fieldId = str.replace("-", "");
        return fieldId;
    }

    private static String createStrBlockIndex(int blockIndex) {
        String strBlockIndex;
        if (blockIndex < 10) {
            strBlockIndex = ".part000" + blockIndex;
        } else if (10 <= blockIndex && blockIndex < 100) {
            strBlockIndex = ".part00" + blockIndex;
        } else if (100 <= blockIndex && blockIndex < 1000) {
            strBlockIndex = ".part0" + blockIndex;
        } else {
            strBlockIndex = ".part" + blockIndex;
        }
        return strBlockIndex;
    }

    /**
     * 通过文件名获取MD5值
     * filename 是分块完成后，每一块的文件的name
     *
     * @param filename
     * @return
     */
    public static String getMD5Checksum(String filename) {

        if (!new File(filename).isFile()) {
            return null;
        }
        byte[] b = createChecksum(filename);
        if (null == b) {
            return null;
        }
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < b.length; i++) {
            result.append(Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1));
        }
        return result.toString();

    }

    private static byte[] createChecksum(String filename) {
        InputStream fis = null;
        try {
            fis = new FileInputStream(filename);
            byte[] buffer = new byte[1024];
            MessageDigest complete = MessageDigest.getInstance("MD5");
            int numRead = -1;

            while ((numRead = fis.read(buffer)) != -1) {
                complete.update(buffer, 0, numRead);
            }
            return complete.digest();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fis) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}

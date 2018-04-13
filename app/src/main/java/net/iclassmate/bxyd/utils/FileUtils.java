package net.iclassmate.bxyd.utils;

import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.constant.Constant;

import org.textmining.text.extraction.WordExtractor;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;

/**
 * Created by xydbj on 2016/3/26.
 */
public class FileUtils {
    public static int CACHE_TIME = 1 * 60 * 1000;
    public static int CACHE_TIME_PIC = 3 * 60 * 1000;
    public static final String ISO_8859_1 = "ISO-8859-1";
    public static final String UTF_8 = "UTF-8";
    public static final String US_ASCII = "US-ASCII";

    public static final int SIZETYPE_B = 1;//获取文件大小单位为B的double值
    public static final int SIZETYPE_KB = 2;//获取文件大小单位为KB的double值
    public static final int SIZETYPE_MB = 3;//获取文件大小单位为MB的double值
    public static final int SIZETYPE_GB = 4;//获取文件大小单位为GB的double值

    /*
    * 字符串写入SD卡
    * */
    public static boolean write2Sd(String str, String fileName) {
        boolean result = false;
        String filePath = getSdCardPath();
        try {
            if (filePath == null || filePath == "" || filePath.equals("null")) {
                return false;
            }
            File file = new File(filePath, fileName);
            if (!file.exists()) {
                file.createNewFile();
            } else {
                file.delete();
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(str.getBytes());
            fos.close();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    /*
    * 从SD卡中读取字符串
    * */
    public static String read2Sd(String fileName) {
        String result = "";
        try {
            String filePath = getSdCardPath();
            if (filePath == null || filePath == "" || filePath.equals("null")) {
                return "";
            }
            File file = new File(filePath, fileName);
            if (!file.exists()) {
                return result;
            }
            FileInputStream is = new FileInputStream(file);
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            result = new String(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String read2SdTXL(String fileName, Context context) {
        String result = "";
        try {
            String filePath = getSdCardPath();
            if (filePath == null || filePath == "" || filePath.equals("null")) {
                return "";
            }
            File file = new File(filePath, fileName);
            if (!file.exists()) {
                return result;
            } else if (NetWorkUtils.isNetworkAvailable(context) && System.currentTimeMillis() - file.lastModified() > CACHE_TIME) {
                file.delete();
                return result;
            }
            FileInputStream is = new FileInputStream(file);
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            result = new String(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    //把图片存入sd卡
    public static boolean writeBitmap2sd(Bitmap bitmap, String fileName) {
        boolean result = false;
        String filePath = getSdCardPath();
        try {
            if (filePath == null || filePath == "" || filePath.equals("null")) {
                return false;
            }
            File file = new File(filePath, fileName);
            if (!file.exists()) {
                file.createNewFile();
            } else {
                file.delete();
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    //从sd卡获取图片
    public static Bitmap read2SdBitmap(String fileName, Context context) {
        Bitmap bitmap = null;
        try {
            String filePath = getSdCardPath();
            if (filePath == null || filePath == "" || filePath.equals("null")) {
                return bitmap;
            }
            File file = new File(filePath, fileName);
            if (!file.exists()) {
                return bitmap;
            } else if (NetWorkUtils.isNetworkAvailable(context) && System.currentTimeMillis() - file.lastModified() > CACHE_TIME_PIC) {
                file.delete();
                return bitmap;
            }
            FileInputStream is = new FileInputStream(file);
            bitmap = BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 判断SDCard是否存在 [当没有外挂SD卡时，内置ROM也被识别为存在sd卡]
     *
     * @return
     */
    public static boolean isSdCardExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取读写SD路径
     *
     * @return
     */
    public static String getSdCardPath() {
        boolean exist = isSdCardExist();
//        FileUtils.clear();
        String sdpath = "";
        if (exist) {
            File f = Environment.getExternalStorageDirectory();
            String path = f.getPath() + "/" + Constant.APP_DIR_NAME;
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            sdpath = file.getAbsolutePath();
        }
        return sdpath;

    }

    /*
    * 清空文件夹
    * */
    public static boolean clear() {
        boolean result = false;
        File file = new File(getSdCardPath());
        try {
            deleteAllFiles(file);
            result = true;
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    /*
    * 删除文件
    * */
    public static void deleteAllFiles(File file) {
        File files[] = file.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    // 判断是否为文件夹
                    deleteAllFiles(f);
                } else {
                    f.delete();
                }
            }
        }
    }

    public static void write2Sd(InputStream in, String filepath) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] bytes = new byte[1024 * 8];
        int n = 0;
        try {
            while ((n = in.read(bytes)) != -1) {
                bos.write(bytes, 0, n);
            }
            File file = new File(getSdCardPath(), filepath);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bos.toByteArray());
            fos.close();
            bos.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //读取TXT格式文件
    public static String readStr2Sd(String filepath, String encoding) {
        String ret = "";
        File file = new File(getSdCardPath(), filepath);
        FileInputStream is = null;
        try {
            is = new FileInputStream(file);
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            ret = new String(bytes, encoding);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    //读取DOC格式文件
    public static String readDOC(String path) {
        // 创建输入流读取doc文件
        FileInputStream in;
        String text = null;
        File file = new File(getSdCardPath(), path);
        try {
            in = new FileInputStream(file);
            int a = in.available();
            WordExtractor extractor = null;
            // 创建WordExtractor
            extractor = new WordExtractor();
            // 对doc文件进行提取
            text = extractor.extractText(in);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (text == null) {
            text = "文件解析异常";
        }
        return text;
    }

    //读取DOCX格式文件
    public static String readDOCX(String path) {
        String river = "";
        File file = new File(getSdCardPath(), path);
        try {
            ZipFile xlsxFile = new ZipFile(file);
            ZipEntry sharedStringXML = xlsxFile.getEntry("word/document.xml");
            InputStream inputStream = xlsxFile.getInputStream(sharedStringXML);
            XmlPullParser xmlParser = Xml.newPullParser();
            xmlParser.setInput(inputStream, "utf-8");
            int evtType = xmlParser.getEventType();
            while (evtType != XmlPullParser.END_DOCUMENT) {
                switch (evtType) {
                    case XmlPullParser.START_TAG:
                        String tag = xmlParser.getName();
                        System.out.println(tag);
                        if (tag.equalsIgnoreCase("t")) {
                            river += xmlParser.nextText() + "\n";
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                    default:
                        break;
                }
                evtType = xmlParser.next();
            }
        } catch (ZipException e) {
            e.printStackTrace();
            Log.i("info", "解析文件异常--ZipExceptio" + e.getMessage());
            river = "文件解析异常";
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("info", "解析文件异常--IOException" + e.getMessage());
            river = "文件解析异常";
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            Log.i("info", "解析文件异常--XmlPullParserException" + e.getMessage());
            river = "文件解析异常";
        }
        if (river == null) {
            river = "文件解析异常";
        }
        return river;
    }

    public static String readXLS(String path) {
        String str = "";
        File file = new File(getSdCardPath(), path);
        try {
            Workbook workbook = null;
            workbook = Workbook.getWorkbook(file);
            Sheet sheet = workbook.getSheet(0);
            Cell cell = null;
            int columnCount = sheet.getColumns();
            int rowCount = sheet.getRows();
            for (int i = 0; i < rowCount; i++) {
                for (int j = 0; j < columnCount; j++) {
                    cell = sheet.getCell(j, i);
                    String temp2 = "";
                    if (cell.getType() == CellType.NUMBER) {
                        temp2 = ((NumberCell) cell).getValue() + "";
                    } else if (cell.getType() == CellType.DATE) {
                        temp2 = "" + ((DateCell) cell).getDate();
                    } else {
                        temp2 = "" + cell.getContents();
                    }
                    str = str + "  " + temp2;
                }
                str += "\n";
            }
            workbook.close();
        } catch (Exception e) {
            str = "文件解析异常";
        }
        if (str == null) {
            str = "文件解析异常";
        }
        return str;
    }

    public static String readXLSX(String path) {
        String str = "";
        String v = null;
        boolean flat = false;
        List<String> ls = new ArrayList<String>();
        File file = new File(getSdCardPath(), path);
        try {
            ZipFile xlsxFile = new ZipFile(new File(path));
            ZipEntry sharedStringXML = xlsxFile.getEntry("xl/sharedStrings.xml");
            InputStream inputStream = xlsxFile.getInputStream(sharedStringXML);
            XmlPullParser xmlParser = Xml.newPullParser();
            xmlParser.setInput(inputStream, "utf-8");
            int evtType = xmlParser.getEventType();
            while (evtType != XmlPullParser.END_DOCUMENT) {
                switch (evtType) {
                    case XmlPullParser.START_TAG:
                        String tag = xmlParser.getName();
                        if (tag.equalsIgnoreCase("t")) {
                            ls.add(xmlParser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                    default:
                        break;
                }
                evtType = xmlParser.next();
            }
            ZipEntry sheetXML = xlsxFile.getEntry("xl/worksheets/sheet1.xml");
            InputStream inputStreamsheet = xlsxFile.getInputStream(sheetXML);
            XmlPullParser xmlParsersheet = Xml.newPullParser();
            xmlParsersheet.setInput(inputStreamsheet, "utf-8");
            int evtTypesheet = xmlParsersheet.getEventType();
            while (evtTypesheet != XmlPullParser.END_DOCUMENT) {
                switch (evtTypesheet) {
                    case XmlPullParser.START_TAG:
                        String tag = xmlParsersheet.getName();
                        if (tag.equalsIgnoreCase("row")) {
                        } else if (tag.equalsIgnoreCase("c")) {
                            String t = xmlParsersheet.getAttributeValue(null, "t");
                            if (t != null) {
                                flat = true;
                            } else {
                                flat = false;
                            }
                        } else if (tag.equalsIgnoreCase("v")) {
                            v = xmlParsersheet.nextText();
                            if (v != null) {
                                if (flat) {
                                    str += ls.get(Integer.parseInt(v)) + "  ";
                                } else {
                                    str += v + "  ";
                                }
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (xmlParsersheet.getName().equalsIgnoreCase("row")
                                && v != null) {
                            str += "\n";
                        }
                        break;
                }
                evtTypesheet = xmlParsersheet.next();
            }
            System.out.println(str);
        } catch (ZipException e) {
            e.printStackTrace();
            str = "文件解析异常";
        } catch (IOException e) {
            e.printStackTrace();
            str = "文件解析异常";
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            str = "文件解析异常";
        }
        if (str == null) {
            str = "文件解析异常";
        }
        return str;
    }

    public static String readPPTX(String path) {
        List<String> ls = new ArrayList<String>();
        String river = "";
        ZipFile xlsxFile = null;
        File file = new File(getSdCardPath(), path);
        try {
            xlsxFile = new ZipFile(file);// pptx按照读取zip格式读取
        } catch (ZipException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            ZipEntry sharedStringXML = xlsxFile.getEntry("[Content_Types].xml");// 找到里面存放内容的文件
            InputStream inputStream = xlsxFile.getInputStream(sharedStringXML);// 将得到文件流
            XmlPullParser xmlParser = Xml.newPullParser();// 实例化pull
            xmlParser.setInput(inputStream, "utf-8");// 将流放进pull中
            int evtType = xmlParser.getEventType();// 得到标签类型的状态
            while (evtType != XmlPullParser.END_DOCUMENT) {// 循环读取流
                switch (evtType) {
                    case XmlPullParser.START_TAG: // 判断标签开始读取
                        String tag = xmlParser.getName();// 得到标签
                        if (tag.equalsIgnoreCase("Override")) {
                            String s = xmlParser
                                    .getAttributeValue(null, "PartName");
                            if (s.lastIndexOf("/ppt/slides/slide") == 0) {
                                ls.add(s);
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:// 标签读取结束
                        break;
                    default:
                        break;
                }
                evtType = xmlParser.next();// 读取下一个标签
            }
        } catch (ZipException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        for (int i = 1; i < (ls.size() + 1); i++) {// 假设有6张幻灯片
            try {
                ZipEntry sharedStringXML = xlsxFile.getEntry("ppt/slides/slide"
                        + i + ".xml");// 找到里面存放内容的文件
                InputStream inputStream = xlsxFile
                        .getInputStream(sharedStringXML);// 将得到文件流
                XmlPullParser xmlParser = Xml.newPullParser();// 实例化pull
                xmlParser.setInput(inputStream, "utf-8");// 将流放进pull中
                int evtType = xmlParser.getEventType();// 得到标签类型的状态
                while (evtType != XmlPullParser.END_DOCUMENT) {// 循环读取流
                    switch (evtType) {
                        case XmlPullParser.START_TAG: // 判断标签开始读取
                            String tag = xmlParser.getName();// 得到标签
                            if (tag.equalsIgnoreCase("t")) {
                                river += xmlParser.nextText() + "\n";
                            }
                            break;
                        case XmlPullParser.END_TAG:// 标签读取结束
                            break;
                        default:
                            break;
                    }
                    evtType = xmlParser.next();// 读取下一个标签
                }
            } catch (ZipException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
        }
        if (river == null) {
            river = "文件解析异常";
        }
        return river;
    }

    public static String readPDF(String path) {
        String content = "";  //存放读取出的文档内容
//        File file = new File(getSdCardPath(), path);
//        PdfReader reader = null; //读取pdf所使用的输出流
//        try {
//            reader = new PdfReader(file.getAbsolutePath());
//            int num = reader.getNumberOfPages();//获得页数
//            for (int i = 1; i < num; i++) {
//                content += PdfTextExtractor.getTextFromPage(reader, i); //读取第i页的文档内容
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            content = "";
//        }
        if (content == null || content.equals("")) {
            content = "文件解析异常";
        }
        return content;
    }

    public static String getTime(String time) {
        String result = "", nowTime = "", t = "", hm = "";
        int y = 0, ny = 0, m = 0, nm = 0, d = 0, nd = 0;
        try {
            SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            result = dataFormat.format(Long.parseLong(time));
            nowTime = dataFormat.format(System.currentTimeMillis());
            y = Integer.parseInt(result.substring(0, 4));
            ny = Integer.parseInt(nowTime.substring(0, 4));
            m = Integer.parseInt(result.substring(5, 7));
            nm = Integer.parseInt(nowTime.substring(5, 7));
            d = Integer.parseInt(result.substring(8, 10));
            nd = Integer.parseInt(nowTime.substring(8, 10));
            hm = result.substring(result.indexOf(" "), result.length());
            t = result;
            result = "";
            if (ny > y) {
                result = y + "-" + m + "-" + d;
            } else if (ny == y) {
                if (m == nm && d == nd) {
                    result = hm;
                } else if (m == nm && nd - d == 1) {
                    result = m + "-" + d;
                } else {
                    result = y + "-" + m + "-" + d;
                }
            }
        } catch (Exception e) {

        }
        return result;
    }

    //设置聊天界面显示时间格式
    public static String getMessageTime(String time) {
        String result = "", nowTime = "", t = "", hm = "";
        int y = 0, ny = 0, m = 0, nm = 0, d = 0, nd = 0;
        try {
            SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            result = dataFormat.format(Long.parseLong(time));
            nowTime = dataFormat.format(System.currentTimeMillis());
            y = Integer.parseInt(result.substring(0, 4));
            ny = Integer.parseInt(nowTime.substring(0, 4));
            m = Integer.parseInt(result.substring(5, 7));
            nm = Integer.parseInt(nowTime.substring(5, 7));
            d = Integer.parseInt(result.substring(8, 10));
            nd = Integer.parseInt(nowTime.substring(8, 10));
            hm = result.substring(result.indexOf(" "), result.length());
            t = result;
            result = "";
            if (ny != y || nm != m) {
                result = y + "年" + m + "月" + d + "日" + hm;
            } else if (ny == y && m == nm) {
                if (d == nd) {
                    result = hm;
                } else if (nd - 1 == d) {
                    result = "昨天" + hm;
                } else {
                    result = y + "年" + m + "月" + d + "日" + hm;
                }
            }
        } catch (Exception e) {

        }
        return result;
    }

    public static byte[] readImag2Sd(String filepath) {
        File file = new File(filepath);
        FileInputStream is = null;
        byte[] bytes = null;
        try {
            is = new FileInputStream(file);
            bytes = new byte[is.available()];
            is.read(bytes);
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public static String uploadFile(File file, String RequestURL) {
        int TIME_OUT = 10 * 10000000; //超时时间
        String CHARSET = "utf-8"; //设置编码
        String SUCCESS = "1";
        String FAILURE = "0";
        String PREFIX = "--";
        String LINE_END = "\r\n";
        String BOUNDARY = UUID.randomUUID().toString(); //边界标识 随机生成 String PREFIX = "--" , LINE_END = "\r\n";
        //String CONTENT_TYPE = "multipart/form-data"; //内容类型
        String CONTENT_TYPE = "image/jpeg"; //内容类型
        try {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true); //允许输入流
            conn.setDoOutput(true); //允许输出流
            conn.setUseCaches(false); //不允许使用缓存
            conn.setRequestMethod("POST"); //请求方式
            conn.setRequestProperty("Charset", CHARSET);
            //设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            if (file != null) {
                /** * 当文件不为空，把文件包装并且上传 */
                OutputStream outputSteam = conn.getOutputStream();
                DataOutputStream dos = new DataOutputStream(outputSteam);
                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                /**
                 * 这里重点注意：
                 * name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的 比如:abc.png
                 */
                sb.append("Content-Disposition: form-data; name=\"img\"; filename=\"" + file.getName() + "\"" + LINE_END);
                sb.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
                dos.write(end_data);
                dos.flush();
                /**
                 * 获取响应码 200=成功
                 * 当响应成功，获取响应的流
                 */
                int res = conn.getResponseCode();
                Log.i("info", "返回码=" + res);
                if (res == 200) {
                    return SUCCESS;
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return FAILURE;
    }

    /**
     * 是否是英文
     *
     * @param charaString
     * @return
     */
    public static boolean isEnglish(String charaString) {
        return charaString.matches("^[a-zA-Z]*");
    }

    public static int getContentType(String filename) {
        int contentType = 11;
        filename = filename.toLowerCase();
        if (filename.contains(".")) {
            filename = filename.substring(filename.lastIndexOf(".") + 1);
        }
        switch (filename) {
            case "bmp":
            case "gif":
            case "jpg":
            case "pic":
            case "png":
            case "jpeg":
                contentType = 2;
                break;
            case "doc":
            case "docx":
            case "txt":
            case "pdf":
            case "wps":
                contentType = 11;
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
                contentType = 3;
                break;
            case "xls":
            case "xlsx":
            case "et":
                contentType = 11;
                break;
            case "ppt":
            case "pptx":
            case "dps":
                contentType = 11;
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
            case "wmv":
                contentType = 4;
                break;
            case "":
                contentType = 1;
                break;
            default:
                contentType = 11;
                break;
        }
        return contentType;
    }

    /**
     * 实现文本复制功能
     *
     * @param content
     */
    public static void copy(String content, Context context) {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
    }

    /**
     * 实现粘贴功能
     *
     * @param context
     * @return
     */
    public static String paste(Context context) {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        return cmb.getText().toString().trim();
    }

    //设置图片
    public static void setImage(ImageView img, String name, String url, Context context) {
        if (name.contains(".")) {
            name = name.substring(name.lastIndexOf(".") + 1, name.length());
        }
        name = name.toLowerCase();
        switch (name) {
            case "doc":
            case "docx":
            case "txt":
            case "pdf":
            case "wps":
                img.setImageResource(R.mipmap.ic_wendang02);
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
                img.setImageResource(R.mipmap.ic_yinpin02);
                break;
            case "xls":
            case "xlsx":
            case "et":
                img.setImageResource(R.mipmap.ic_biaoge02);
                break;
            case "ppt":
            case "pptx":
            case "dps":
                img.setImageResource(R.mipmap.ic_ppt);
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
                if (url != null) {
                    if (!url.contains("base64")) {
                        url = Constant.ADDRESS_STUDY + url;
                        Picasso.with(context).load(url).placeholder(R.mipmap.img_morentupian).config(Bitmap.Config.RGB_565).into(img);
                    } else {
                        url = url.substring(url.indexOf("base64") + 7);
                        Bitmap bm = BitmapUtils.stringtoBitmap(url);
                        img.setImageBitmap(bm);
                    }
                }
                break;
            default:
                img.setImageResource(R.mipmap.ic_qita);
                break;
        }
    }

    public static void setImageInAdapter(ImageView img, String name, String url, Context context) {
        if (name.contains(".")) {
            name = name.substring(name.lastIndexOf(".") + 1, name.length());
        }
        name = name.toLowerCase();
        switch (name) {
            case "doc":
            case "docx":
            case "txt":
            case "wps":
                img.setImageResource(R.drawable.ic_wendang02_a);
                break;
            case "pdf":
                img.setImageResource(R.drawable.ic_pdf_a);
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
                img.setImageResource(R.drawable.ic_yinpin02_a);
                break;
            case "xls":
            case "xlsx":
            case "et":
                img.setImageResource(R.drawable.ic_biaoge02_a);
                break;
            case "ppt":
            case "pptx":
            case "dps":
                img.setImageResource(R.drawable.ic_ppt_a);
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
                if (url != null) {
                    if (!url.contains("base64")) {
                        url = Constant.ADDRESS_STUDY + url;
                        Picasso.with(context).load(url).placeholder(R.mipmap.img_morentupian).config(Bitmap.Config.RGB_565).into(img);
                    } else {
                        url = url.substring(url.indexOf("base64") + 7);
                        Bitmap bm = BitmapUtils.stringtoBitmap(url);
                        img.setImageBitmap(bm);
                    }
                }
                break;
            default:
                img.setImageResource(R.drawable.ic_qita_a);
                break;
        }
    }

    //获取文件名
    public static String getFileName(String filenmae) {
        if (filenmae.contains(".")) {
            filenmae = filenmae.substring(0, filenmae.lastIndexOf("."));
        }
        return filenmae;
    }

    //根据文件全路径获取文件名
    public static String getFileNameFullPath(String pathandname) {
        if (pathandname.contains("/")) {
            int start = pathandname.lastIndexOf("/");
            int end = pathandname.length();
            if (start != -1 && end != -1) {
                return pathandname.substring(start + 1, end);
            } else {
                return pathandname;
            }
        } else {
            return pathandname;
        }
    }

    //获取视频缩略图
    public static Bitmap getVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    //为视频和图片设置缩略图
    public static void setScaleImage(Context context, String url, ImageView img) {
        if (url != null) {
            if (!url.contains("base64")) {
                url = Constant.ADDRESS_STUDY + url;
                Picasso.with(context).load(url).placeholder(R.mipmap.img_morentupian).config(Bitmap.Config.RGB_565).into(img);
            } else {
                url = url.substring(url.indexOf("base64") + 7);
                Bitmap bm = BitmapUtils.stringtoBitmap(url);
                img.setImageBitmap(bm);
            }
        } else {
            img.setImageResource(R.mipmap.ic_qita);
        }
    }

    //判断当前是否是wifi状态
    public static boolean isWifiActive(Context icontext) {
        Context context = icontext.getApplicationContext();
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info;
        if (connectivity != null) {
            info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getTypeName().equals("WIFI") && info[i].isConnected()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 　　* 获取文件指定文件的指定单位的大小
     * 　　* @param filePath 文件路径
     * 　　* @param sizeType 获取大小的类型1为B、2为KB、3为MB、4为GB
     * 　　* @return double值的大小
     */
    public static double getFileOrFilesSize(String filePath, int sizeType) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //Log.e("获取文件大小", "获取失败!");
        }
        return FormetFileSize(blockSize, sizeType);
    }

    /**
     * 　　* 调用此方法自动计算指定文件或指定文件夹的大小
     * 　　* @param filePath 文件路径
     * 　　* @return 计算好的带B、KB、MB、GB的字符串
     */
    public static String getAutoFileOrFilesSize(String filePath) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //Log.e("获取文件大小", "获取失败!");
        }
        return FormetFileSize(blockSize);
    }

    /**
     * 　　* 获取指定文件大小
     * 　　* @param f
     * 　　* @return
     * 　　* @throws Exception
     */
    private static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
            //Log.e("获取文件大小", "文件不存在!");
        }
        return size;
    }


    /**
     * 　　* 获取指定文件夹
     * 　　* @param f
     * 　　* @return
     * 　　* @throws Exception
     */
    private static long getFileSizes(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }

    /**
     * 　　* 转换文件大小
     * 　　* @param fileS
     * 　　* @return
     */
    private static String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    /**
     * 　　* 转换文件大小,指定转换的类型
     * 　　* @param fileS
     * 　　* @param sizeType
     * 　　* @return
     */
    private static double FormetFileSize(long fileS, int sizeType) {
        DecimalFormat df = new DecimalFormat("#.00");
        double fileSizeLong = 0;
        switch (sizeType) {
            case SIZETYPE_B:
                fileSizeLong = Double.valueOf(df.format((double) fileS));
                break;
            case SIZETYPE_KB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1024));
                break;
            case SIZETYPE_MB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1048576));
                break;
            case SIZETYPE_GB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1073741824));
                break;
            default:
                break;
        }
        return fileSizeLong;
    }

}
package net.iclassmate.bxyd.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by xydbj on 2016/8/20.
 */
public class UTCTime {
    //获取当前的utc时间
    public static String getUtcTime() {
        String time = "";
        SimpleDateFormat fmt = new SimpleDateFormat("yyMMddHHmmss");
        fmt.setTimeZone(TimeZone.getTimeZone("Etc/GMT+0"));
        time = fmt.format(new Date());
        return time;
    }

    //根据utc时间获取当前时间
    public static String getLocalTime(String utcTime) {
        String time = "";
        long utc1 = Long.parseLong(utcTime);
        long utc2 = 1400000000L;//自己的偏移量
        time = (utc1 + utc2) * 1000 + "";
        return time;
    }

    /**
     * 把格林威治时间转换成年月日时分秒    yyyy年-MM月dd日-HH时mm分ss秒
     * @param tim 格林威治时间
     * @return
     */
    public static String getTime(long tim){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(tim);
        String time = formatter.format(date);
        return time;
    }

    /**
     * 格式化时间（输出类似于 刚刚, 4分钟前, 一小时前, 昨天这样的时间）
     *
     * @param time    需要格式化的时间 如"2014-07-14 19:01:45"
     * @param pattern 输入参数time的时间格式 如:"yyyy-MM-dd HH:mm:ss"
     *                <p/>如果为空则默认使用"yyyy-MM-dd HH:mm:ss"格式
     * @return time为null，或者时间格式不匹配，输出空字符""
     */
    public static String formatDisplayTime(String time, String pattern) {
        String display = "";
        int tMin = 60 * 1000;
        int tHour = 60 * tMin;
        int tDay = 24 * tHour;

        if (time != null) {
            try {
                Date tDate = new SimpleDateFormat(pattern).parse(time);
                Date today = new Date();
                SimpleDateFormat thisYearDf = new SimpleDateFormat("yyyy");
                SimpleDateFormat todayDf = new SimpleDateFormat("yyyy-MM-dd");
                Date thisYear = new Date(thisYearDf.parse(thisYearDf.format(today)).getTime());
                Date yesterday = new Date(todayDf.parse(todayDf.format(today)).getTime());
                Date beforeYes = new Date(yesterday.getTime() - tDay);
                if (tDate != null) {
                    SimpleDateFormat halfDf = new SimpleDateFormat("MM月dd日");
                    long dTime = today.getTime() - tDate.getTime();
                    if (tDate.before(thisYear)) {
                        display = new SimpleDateFormat("yyyy年MM月dd日").format(tDate);
                    } else {

                        if (dTime < tMin) {
                            display = "刚刚";
                        } else if (dTime < tHour) {
                            display = (int) Math.ceil(dTime / tMin) + "分钟前";
                        } else if (dTime < tDay && tDate.after(yesterday)) {
                            display = (int) Math.ceil(dTime / tHour) + "小时前";
                        } else if (tDate.after(beforeYes) && tDate.before(yesterday)) {
                            display = "昨天" + new SimpleDateFormat("HH:mm").format(tDate);
                        } else {
                            display = halfDf.format(tDate);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return display;
    }
}

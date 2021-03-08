package com.wzy.lamanpro.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {

    public static long dateToStamp(String s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        try {
            date = simpleDateFormat.parse(s);
            long ts = date.getTime();
            return ts;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }

    public static String dateToMinute(String s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        try {
            date = simpleDateFormat.parse(s);
            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("HH:mm");
            String ts = simpleDateFormat2.format(date);
            return ts;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(long lt) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    /**
     * 当前时间与传入时间的比较
     * 小于等于返回true
     */
    public static int compareTime(String comTime) {
        Date date = new Date();
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        res = simpleDateFormat.format(date);
        Date dateCompared;
        try {
            date = simpleDateFormat.parse(res);
            dateCompared = simpleDateFormat.parse(comTime);
            return date.compareTo(dateCompared);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 10;

    }

    public static int compareAfter(String comTime, int time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        long dateCompared;
        try {
            dateCompared = simpleDateFormat.parse(comTime).getTime();
            long befTime = dateCompared + 60000 * time;
            Date date2 = new Date(befTime);
            return compareTime(simpleDateFormat.format(date2));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 10;
    }

    public static boolean compareTimes(String max, String min) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.CHINESE);
        try {
            long maxTime = simpleDateFormat.parse(max).getTime();
            long minTime = simpleDateFormat.parse(min).getTime();
            minTime = isMoreThan23(min) && !isMoreThan23(max) ? minTime - 24 * 60 * 60 * 1000 : minTime;
            return maxTime > minTime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean isMoreThan23(String time) {
        return time.startsWith("23")||time.startsWith("22");

    }
}
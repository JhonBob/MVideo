package com.bob.mvideo.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/2/3.
 */
public class StringUtil {
    //格式化时间
    public static String formatVideoDuration(long duration){
        int HOUR=1000*60*60;//小时
        int MINUTE=1000*60;//分
        int SECOND=1000;

        int remainTime;
        //小时
        int hour= (int) (duration/HOUR);
        remainTime= (int) (duration%HOUR);
        //分钟
        int minute=remainTime/MINUTE;
        remainTime=remainTime%MINUTE;
        //秒
        int second=remainTime/SECOND;

        if (hour==0){
            //00:00
            return String.format("%02d:%02d",minute,second);
        }else {
            //00:00:00
            return String.format("%02d:%02d:%02d",hour,minute,second);
        }
    }

    //格式化系统时间
    public static String formatSystemTime(){
        SimpleDateFormat format=new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }

    public static String formatAudioName(String audioname){
        return audioname.substring(0,audioname.lastIndexOf("."));
    }
}

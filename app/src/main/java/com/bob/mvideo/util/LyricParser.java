package com.bob.mvideo.util;

import com.bob.mvideo.bean.Lyric;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Administrator on 2016/2/18.
 */
public class LyricParser  {
    private static final String TAG=LyricParser.class.getSimpleName();
    public static ArrayList<Lyric> parseLyricFromFile(File lyricFile){
        if (lyricFile==null || !lyricFile.exists()) return null;
        ArrayList<Lyric> list=new ArrayList<>();
        try{
            //1.获得每一行歌词
            BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(lyricFile),"gbk"));
            String line;
            int num=1;
            while((line=reader.readLine())!=null){
                LogUtil.e(TAG,num+":"+line);
                num++;
                //2.歌词内容转换为对象
                //多个时间戳使用split("]")分割
                String[] arr=line.split("\\]");
                for (int i=0;i<arr.length-1;i++){
                    Lyric lyric=new Lyric();
                    lyric.setContent(arr[arr.length-1]);
                    lyric.setStartPoint(formatLyricStartPoint(arr[i]));
                    list.add(lyric);
                }
            }
            //3.进行排序
            Collections.sort(list);
        }catch (Exception e){
            e.printStackTrace();
        }

        return list;
    }

    //[00:27.35]
    private static long formatLyricStartPoint(String startPoint){
        startPoint=startPoint.substring(1);//返回起始到结尾去[
        //split(":")->00 27.35
        String[] arr=startPoint.split("\\:");//第一个元素是分钟
        String[] arr2=arr[1].split("\\.");//分钟后面的 27 35
        //字符转整型
        int minute=Integer.parseInt(arr[0]);//00
        int second=Integer.parseInt(arr2[0]);//27
        int mills=Integer.parseInt(arr2[1]);//35
        return minute*60*1000+second*1000+mills*10;
    }
}

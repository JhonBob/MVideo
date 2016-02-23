package com.bob.mvideo.util;

import java.io.File;

/** ``
 * Created by Administrator on 2016/2/18.
 */

//模拟歌词加载模块
public class LyricLoader {
    private static String LYRIC_DIR="/storage/sdcard1/Lyric";
    public static File loadLyricFile(String audioName){
        File file=new File(LYRIC_DIR,StringUtil.formatAudioName(audioName)+".txt");
        if(!file.exists()){
            file=new File(LYRIC_DIR,StringUtil.formatAudioName(audioName)+".lrc");
            if (!file.exists()){
                return null;
            }
        }
        return file;
    }
}

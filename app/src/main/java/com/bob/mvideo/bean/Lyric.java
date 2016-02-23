package com.bob.mvideo.bean;

/**
 * Created by Administrator on 2016/2/18.
 */
public class Lyric implements Comparable<Lyric>{
    private String content;//歌词内容
    private long startPoint;//歌词起始点,时间戳

    public Lyric(){

    }

    public Lyric(String content, long startPoint) {
        this.content = content;
        this.startPoint = startPoint;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(long startPoint) {
        this.startPoint = startPoint;
    }

    @Override
    public int compareTo(Lyric another) {
        return (int) (this.startPoint-another.startPoint);
    }
}

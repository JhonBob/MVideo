package com.bob.mvideo.bean;

import android.database.Cursor;
import android.provider.MediaStore;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/2/3.
 */
public class VideoItem implements Serializable{
    private long size;
    private String title;
    private long duration;
    private String path;

    public static  VideoItem fromCursor(Cursor cursor){
        VideoItem videoItem=new VideoItem();
        videoItem.setDuration(cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION)));
        videoItem.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA)));
        videoItem.setSize(cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE)));
        videoItem.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE)));
        return videoItem;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}

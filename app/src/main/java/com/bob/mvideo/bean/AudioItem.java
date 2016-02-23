package com.bob.mvideo.bean;

import android.database.Cursor;
import android.provider.MediaStore;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/2/15.
 */
public class AudioItem implements Serializable{
    private String title;
    private String artist;
    private long duration;
    private String path;

    public static AudioItem fromCursor(Cursor cursor){
        AudioItem audioItem=new AudioItem();
        audioItem.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
        audioItem.setDuration(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
        audioItem.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
        audioItem.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
        return audioItem;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "AudioItem{" +
                "title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", duration=" + duration +
                ", path='" + path + '\'' +
                '}';
    }
}

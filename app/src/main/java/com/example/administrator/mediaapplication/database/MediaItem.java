package com.example.administrator.mediaapplication.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by Administrator on 2018/7/19.
 */

@Table(database = MediaDataBase.class)
public class MediaItem extends BaseModel {
    @PrimaryKey
    String path;
    @Column
    String parentPath;
    @Column
    long duration;
    @Column
    String title;
    @Column
    String artist;
    @Column
    String album;
    @Column
    boolean enable;



    public  MediaItem(){

    }

    public MediaItem(String path,String parentPath,String title,String artist,String album,long duration,boolean enable){
        this.path=path;
        this.parentPath = parentPath;
        this.title=title;
        this.artist=artist;
        this.album=album;
        this.duration=duration;
        this.enable=enable;
    }

    public String getPath() {
        return path;
    }

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
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

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}

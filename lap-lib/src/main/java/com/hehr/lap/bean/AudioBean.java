package com.hehr.lap.bean;

import android.text.TextUtils;

/**
 * @author hehr
 *
 */

public class AudioBean {

    /**
     * 歌手名
     */

    public String singer;

    /**
     * 歌曲名
     */

    public String song;

    /**
     * 文件路径
     */

    public String path;

    /**
     * 文件名
     */
    public String name;


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getPath() {
        return path;
    }


    public void setPath(String path) {
        this.path = path;
    }


    public String getSinger() {
        return singer;
    }


    public void setSinger(String singer) {
        this.singer = singer;
    }


    public String getSong() {
        return song;
    }


    public void setSong(String song) {
        this.song = song;
    }


    @Override
    public String toString() {
        return "AudioBean{" +
                "singer='" + singer + '\'' +
                ", song='" + song + '\'' +
                ", path='" + path + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {

        if(!(obj instanceof AudioBean)){
            return false;
        }

        AudioBean ab = (AudioBean) obj;

        if(TextUtils.equals(ab.getSong(),getSong()) && TextUtils.equals(ab.getSinger(),getSinger())){
            if(!TextUtils.isEmpty(ab.getPath())){
                return TextUtils.equals(ab.getName(),getName())?true:false;
            }else if(!TextUtils.isEmpty(ab.getName())){
                return TextUtils.equals(ab.getPath(),getPath())?true:false;
            }
        }else {
            return false;
        }

        return false;

    }
}

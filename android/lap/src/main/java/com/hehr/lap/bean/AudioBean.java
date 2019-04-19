package com.hehr.lap.bean;

import android.support.annotation.Keep;
import android.text.TextUtils;

/**
 * @author hehr
 *
 */
@Keep
public class AudioBean {

    /**
     * 歌手名
     */
    @Keep
    public String singer;

    /**
     * 歌曲名
     */
    @Keep
    public String song;

    /**
     * 文件路径
     */
    @Keep
    public String path;

    /**
     * 文件名
     */
    @Keep
    public String name;

    @Keep
    public String getName() {
        return name;
    }

    @Keep
    public void setName(String name) {
        this.name = name;
    }

    @Keep
    public String getPath() {
        return path;
    }

    @Keep
    public void setPath(String path) {
        this.path = path;
    }

    @Keep
    public String getSinger() {
        return singer;
    }

    @Keep
    public void setSinger(String singer) {
        this.singer = singer;
    }

    @Keep
    public String getSong() {
        return song;
    }

    @Keep
    public void setSong(String song) {
        this.song = song;
    }

    @Keep
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

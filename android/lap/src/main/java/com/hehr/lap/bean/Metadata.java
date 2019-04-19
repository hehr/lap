package com.hehr.lap.bean;

import android.text.TextUtils;

import java.util.List;


public class Metadata extends BaseBean<Metadata> {

    private String artist;

    private String title;

    private List<Metadata> extra;

    /**
     * 该值作为分词之后，依然不能准确区分出来歌手名和歌曲名的集合
     *
     * @return
     */
    public List<Metadata> getExtra() {
        return extra;
    }

    public void setExtra(List<Metadata> extra) {
        this.extra = extra;
    }
    public String getArtist() {
        return artist;
    }
    public void setArtist(String artist) {
        this.artist = artist;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "artist : " + this.getArtist()
                + " title : " + this.getTitle()
                + " extra : " + (getExtra() == null ? "" : getExtra().toString());
    }

    @Override
    public boolean isEmpty() {
        return TextUtils.isEmpty(this.artist)
                && TextUtils.isEmpty(this.title);
    }

    /**
     * 是否已成功解析过的
     * 只有完整具备歌手歌曲名或者含有extra字段了才认为是已完整解析过了
     *
     */
    public boolean isParsed(){
        return !TextUtils.isEmpty(this.getTitle())
                &&!TextUtils.isEmpty(getArtist())
                ||(getExtra()!= null && getExtra().size()!=0);
    }

    @Override
    public boolean isSameAudio(Metadata bean) {
        if (this.getArtist().equals(bean.getArtist()) || this.getTitle().equals(bean.getTitle())) {
            return true;
        }
        return false;
    }
}
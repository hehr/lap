package com.hehr.lap.bean;

import android.text.TextUtils;


public class ScannerBean extends BaseBean<ScannerBean>{

    /**
     * 原始文件名
     */
    private String originalName;

    /**
     * 文件完整路径
     */
    private String absolutePath;

    /**
     * 文件后缀名，如: mp3
     */
    private String suffix;

    /**
     * 包含后缀名的文件名称，如:周杰伦－发如雪.mp3
     */
    private String fileNameWithSuffix;


    /**
     * 不含后缀部分文件名称，如:周杰伦-发如雪
     */
    private String fileNameWithOutSuffix;

    /**
     * 媒体文件解析结果存放实体类
     */
    private Metadata metadata;

    public String getSuffix() { return suffix; }
    public String getAbsolutePath() {
        return absolutePath;
    }
    public String getFileNameWithSuffix() {
        return fileNameWithSuffix;
    }
    public String getFileNameWithOutSuffix() {
        return fileNameWithOutSuffix;
    }
    public Metadata getMetadata() {
        return metadata;
    }
    public String getOriginalName() {
        return originalName;
    }

    private void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    private void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    private void setFileNameWithSuffix(String fileNameWithSuffix) {
        this.fileNameWithSuffix = fileNameWithSuffix;
    }

    private void setFileNameWithOutSuffix(String fileNameWithOutSuffix) {
        this.fileNameWithOutSuffix = fileNameWithOutSuffix;
    }

    private void setSuffix(String suffix){this.suffix = suffix;}


    /**
     * 只对外放开getMetadata方法
     *
     * @param metadata
     */
    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    private ScannerBean(String originalName,String absolutePath, String fileNameWithSuffix, String suffix ,String fileNameWithOutSuffix,Metadata metadata) {
        setOriginalName(originalName);
        setMetadata(metadata);
        setAbsolutePath(absolutePath);
        setFileNameWithOutSuffix(fileNameWithOutSuffix);
        setFileNameWithSuffix(fileNameWithSuffix);
        setSuffix(suffix);
    }

    private ScannerBean(Builder builder){
        this(builder.originalName ,
                builder.absolutePath,
                builder.fileNameWithSuffix,
                builder.suffix,
                builder.fileNameWithOutSuffix,
                builder.metadata);
    }


    @Override
    public boolean isEmpty() {
        return TextUtils.isEmpty(this.absolutePath)&&TextUtils.isEmpty(this.fileNameWithOutSuffix)&&TextUtils.isEmpty(this.fileNameWithSuffix);
    }

    /**
     * 是否有效数据
     * @return
     */
    public boolean isEffect(){

        return getMetadata() != null && getMetadata().isParsed();

    }

    @Override
    public boolean isSameAudio(ScannerBean bean) {
        if(!this.isEmpty()&&!bean.isEmpty()){
            if(!bean.isEmpty()&&!this.isEmpty() ){

                if((TextUtils.isEmpty(this.getAbsolutePath())&&TextUtils.isEmpty(bean.getAbsolutePath()))){
                    //不含绝对路径的使用文件名比较
                    if(!TextUtils.isEmpty(this.getFileNameWithSuffix())
                            && !TextUtils.isEmpty(bean.getFileNameWithSuffix())
                            && this.getFileNameWithSuffix().equals(bean.getFileNameWithSuffix())) {
                        return true;
                    }else if(!TextUtils.isEmpty(this.getFileNameWithOutSuffix())
                            && !TextUtils.isEmpty(bean.getFileNameWithOutSuffix())
                            &&this.getFileNameWithOutSuffix().equals(bean.getFileNameWithOutSuffix())){
                        return true;
                    }
                }else {
                    //有绝对路径的使用绝对路径对比
                    if(!TextUtils.isEmpty(this.getAbsolutePath())
                            && !TextUtils.isEmpty(bean.getAbsolutePath())
                            && this.getAbsolutePath().equals(bean.getAbsolutePath())){
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public static class Builder {

        private String originalName;
        private String absolutePath;
        private String fileNameWithSuffix;
        private String fileNameWithOutSuffix;
        private Metadata metadata;
        private String suffix;

        public Builder setAbsolutePath(String absolutePath) {
            this.absolutePath = absolutePath;
            return this;
        }

        public Builder setFileNameWithSuffix(String fileNameWithSuffix) {
            this.fileNameWithSuffix = fileNameWithSuffix;
            return this;
        }

        public Builder setFileNameWithOutSuffix(String fileNameWithOutSuffix) {
            this.fileNameWithOutSuffix = fileNameWithOutSuffix;
            return this;
        }

        public Builder setMetadata(Metadata metadata) {
            this.metadata = metadata;
            return this;
        }

        public Builder setSuffix(String suffix) {
            this.suffix = suffix;
            return this;
        }

        public void setOriginalName(String originalName) {
            this.originalName = originalName;
        }


        public ScannerBean build() {
            return new ScannerBean(this);
        }

    }

    @Override
    public String toString() {
        return "fileName: " + (TextUtils.isEmpty(getAbsolutePath())? getFileNameWithOutSuffix():getAbsolutePath())+ " , metadata " + metadata + " \n";
    }
}

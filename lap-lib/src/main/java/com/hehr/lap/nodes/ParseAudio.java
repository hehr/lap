package com.hehr.lap.nodes;


import android.text.TextUtils;
import android.util.Log;

import com.hehr.lap.Bundle;
import com.hehr.lap.Error;
import com.hehr.lap.bean.Metadata;
import com.hehr.lap.bean.ScannerBean;
import com.hehr.lap.ffmrf.Constants;
import com.hehr.lap.utils.StringUtils;

import java.util.concurrent.ExecutionException;

import wseemann.media.FFmpegMediaMetadataRetriever;

public class ParseAudio extends BaseNode<Bundle> {

    private ParseAudio(Builder builder) {}

    @Override
    public String getName() {
        return Name.PARSE_AUDIO;
    }

    @Override
    public Bundle doWork(Bundle bundle) throws TaskFactory.ExecutorServiceShutdownException, ExecutionException, InterruptedException {

        return bundle.getTaskFactory().getExecutor().submit(
                new FFMARTask(bundle)
        ).get();

    }

    @Override
    public boolean hasNext(Bundle bundle) {
        //音频解析完成之后默认true,需更新据库
        return true;
    }

    public static class Builder {
        public ParseAudio build() {
            return new ParseAudio(this);
        }
    }

    /**
     * 音频解析TSAK
     */
    class FFMARTask extends BaseTask {

        public FFMARTask(Bundle bundle) {
            super(bundle);
        }

        private final String TAG = this.getClass().getSimpleName();

        @Override
        public Bundle call() {
            return this.run();
        }

        public synchronized Bundle run() {

            FFmpegMediaMetadataRetriever fmmr;

            Metadata metadata;

            try {

                for (int j = 0; j < bundle.getList().size(); j++) {
                    ScannerBean bean = bundle.getList().get(j);
                    //已有METADATA数据则无需重复解析
                    if (bean.getMetadata() != null && bean.isEffect()) {
                        continue;
                    }

                    fmmr = new FFmpegMediaMetadataRetriever();

                    fmmr.setDataSource(bean.getAbsolutePath());

                    metadata = new Metadata();

                    for (int i = 0; i < Constants.METADATA_KEYS.length; i++) {
                        String key = Constants.METADATA_KEYS[i];
                        String value = fmmr.extractMetadata(key);
                        if (value != null
                                && !TextUtils.isEmpty(value)) {
                            //去除括号内的内容
                            value = StringUtils.getInstance().subBrace(value);
                            //非中文字符处理
                            if (StringUtils.getInstance().isChineseAndEnglish(value)) {
                                if (FFmpegMediaMetadataRetriever.METADATA_KEY_TITLE.equals(key)) {
                                    //歌曲名要去空格处理
                                    metadata.setTitle(value);
                                } else if (FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST.equals(key)) {
                                    metadata.setArtist(value);
                                } else if(FFmpegMediaMetadataRetriever.METADATA_KEY_ALBUM.equals(key)){
                                    metadata.setAlbum(value);
                                }
                            }
                        }
                    }

                    if (!metadata.isEmpty()) {
                        bean.setMetadata(metadata);
                    }

                    if (fmmr != null) {
                        fmmr.release();
                    }
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                bundle.setError(new Error.Builder()
                        .setDesc(Error.ERROR.FFMAR_PARSE_ILLEGAL_ARGUMENT_ERROR.getDesc())
                        .setCode(Error.ERROR.FFMAR_PARSE_ILLEGAL_ARGUMENT_ERROR.getCode())
                        .build()

                );
            }

            return bundle;

        }

    }


}

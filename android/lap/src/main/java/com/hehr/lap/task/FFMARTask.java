package com.hehr.lap.task;

import android.text.TextUtils;

import com.hehr.lap.Bundle;
import com.hehr.lap.bean.Metadata;
import com.hehr.lap.bean.ScannerBean;
import com.hehr.lap.ffmrf.Constants;
import com.hehr.lap.utils.StringUtils;
import com.hehr.lap.Error;

import wseemann.media.FFmpegMediaMetadataRetriever;

/**
 * 音频解析TSAK
 */
public class FFMARTask extends BaseTask {

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

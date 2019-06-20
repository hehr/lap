package com.hehr.lap;

import java.util.HashSet;

public class Conf {


    /**
     * 音频过滤类型
     */
    public static HashSet AUDIO_TYPE = new HashSet() {{
        add("mp3");
        add("ogg");
        add("wav");
        add("m4a");
        add("ape");
        add("flac");
        add("aac");
        add("wma");
    }};


//    public static final String COMPANY_TITLE = "AISPEECH";

//    public static final String MODULE_TITLE = "Scanner";

    /**
     * 音频大小限制,小于该文件大小的不予解析
     * 默认文件大小 1024 字节
     */
    public static int AUDIO_SIZE_LIMIT = 1024;

    public static void setAudioSizeLimit(int limit) {
        AUDIO_SIZE_LIMIT = limit;
    }

    /**
     * 设置缓存区大小
     */
    public static int CACHE_SIZE = 200;

    public static void setCacheSize(int size) {
        CACHE_SIZE = size;
    }

    /**
     * 设置扫描文件条目数
     * 默认设置1000个文件数量限制
     */
    public static int SCAN_ENTRY_NUMBER = 500;

    public static void setEntryNumber(int number) {
        SCAN_ENTRY_NUMBER = number;
    }

    /**
     * 数据库名称
     */
    public static final String DB_NAME = "unencrypted.db";

    /**
     * 表名
     */
    public static final String TB_NAME = "metadata";


    /**
     * 数据库信息相关变量
     */
    public static class ScannerDB {

        /**
         * metadata表
         */
        public static final String TABLE_METADATA_NAME = "metadata";

        /**
         * 文件名
         */
        public static final String METADATA_COLUMN_FILE_NAME = "file_name";

        /**
         * 歌手名
         */
        public static final String METADATA_COLUMN_ARTIST = "artist";

        /**
         * 歌曲名
         */
        public static final String METADATA_COLUMN_TITLE = "title";
        /**
         * 专辑名
         */
        public static final String METADATA_COLUMN_ALBUM = "album";

        /**
         * 歌手名
         */
        public static final String TABLE_SINGER_NAME = "singer";

        /**
         * 文件名
         */
        public static final String SINGER_COLUMN_NAME = "name";
    }


}

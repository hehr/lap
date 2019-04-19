package com.hehr.lap;

import android.support.annotation.Keep;

/**
 * @author hehr
 *
 */

@Keep
public class Error {


    /**
     * Defined error here
     *
     */
    @Keep
   public enum ERROR {

        /**
         * 初始化 数据库文件不存在
         */
        INIT_DB_NOT_EXISTS_ERROR(1001,"encrypted.db not exists"),

        /**
         * 初始化方法未执行
         */
        INIT_NOT_EXECUTE_ERROR(1002,"Initialize method not excute"),

        /**
         * 传入音频路径非文件夹
         */
        SCAN_PATH_NOT_DIR_ERROR(1003,"the path not a directory"),

        /**
         * 音频解析线程非法参数异常
         */
        FFMAR_PARSE_ILLEGAL_ARGUMENT_ERROR(1004 , "FFMARTask task IllegalArgumentException"),

        /**
         *
         */
        FFMAR_TASK_INTRRRUPTED_EXCEPTION_ERROR(1005 , "FFMARTask task InterruptedException"),

        /**
         *
         */
        FFMAR_TASK_EXECUTION_EXCEPTION_ERROR(1006 , "FFMARTask ExecutionException"),

        /**
         *
         */
        CREATE_CACHE_FROM_DB_TASK_INTRRRUPTED_EXCEPTION_ERROR(1007 , "CreateCacheFromDBTask  InterruptedException"),

        /**
         *
         */
        CREATE_CACHE_FROM_DB_TASK_EXECUTION_EXCEPTION_ERROR(1008 , "CreateCacheFromDBTask  ExecutionException"),

        /**
         *
         */
        UPDATE_DB_TASK_INTRRRUPTED_EXCEPTION_ERROR(1009 , "UpdateDBTask InterruptedException"),

        /**
         *
         */
        UPDATE_DB_TASK_EXECUTION_EXCEPTION_ERROR(1010 , "UpdateDBTask  ExecutionException"),

        /**
         *
         */
        QUERY_DB_TASK_INTRRRUPTED_EXCEPTION_ERROR(1011 , "QueryDBTask  InterruptedException"),

        /**
         *
         */
        QUERY_DB_TASK_EXECUTION_EXCEPTION_ERROR(1012 , "QueryDBTask  ExecutionException"),

        /**
         *
         */
        TOKENIZE_TASK_INTRRRUPTED_EXCEPTION_ERROR(1013 , "TokenizeTask  InterruptedException"),

        /**
         *
         */
        TOKENIZE_TASK_EXECUTION_EXCEPTION_ERROR(1014 , "TokenizeTask  ExecutionException"),

        /**
         * 初始化 数据库文件拷贝IO异常
         */
        INIT_DB_COPY_IO_EXCEPTION_ERROR(1015,"copy encrypted.db IOException"),

        /**
         * trans 节点解析字符串异常
         */
        TRANS_NODE_EXCEPTION_ERROR(1016, "trans Exception"),

        /**
         * taskFactory  shutdown 之后在此提交任务
         */
        EXECUTOR_SERVICE_SHUTDOWN_EXCEPTION_ERROR(1017,"ExecutorServiceShutdownException"),

        COPY_DB_FROM_ASSET_TASK_INTRRRUPTED_EXCEPTION_ERROR(1018, "CopyDBFromAssetTask  InterruptedException"),

        /**
         *
         */
        COPY_DB_FROM_ASSET_TASK_EXECUTION_EXCEPTION_ERROR(1019 , "CopyDBFromAssetTask  ExecutionException"),


        ;

        private ERROR( int i , String s ){
            setCode(i); setDesc(s);
        }

        private int code;

        private String desc;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

    }

    private String desc ;

    private int code;


    @Keep
    private Error(Builder builder) {

        this(builder.code,builder.desc);

    }
    @Keep
    private Error(int code , String desc) {

        setCode(code);

        setDesc(desc);

    }
    @Keep
    public String getDesc() { return desc; }
    @Keep
    public void setDesc(String desc) {
        this.desc = desc;
    }
    @Keep
    public int getCode() {
        return code;
    }
    @Keep
    public void setCode(int code) {
        this.code = code;
    }
    @Keep
    public static class Builder{

        private int code;

        private String desc;

        public Builder setCode(int code){
            this.code = code;return this;
        }

        public Builder setDesc(String desc){
            this.desc = desc;return this;
        }

        public Error build(){
            return new Error(this) ;
        }
    }

    @Override
    @Keep
    public String toString() {
        return "code : " + this.getCode() + ",desc : " + this.getDesc();
    }
}

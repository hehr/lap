package com.hehr.lap;

/**
 * @author hehr
 *
 */


public class Error {


    /**
     * Defined error here
     *
     */

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

        /**
         *
         */
        TASK_INTRRRUPTED_EXCEPTION_ERROR(1018, " InterruptedException"),

        /**
         *
         */
        TASK_EXECUTION_EXCEPTION_ERROR(1019 , "ExecutionException"),


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



    private Error(Builder builder) {

        this(builder.code,builder.desc);

    }

    private Error(int code , String desc) {

        setCode(code);

        setDesc(desc);

    }

    public String getDesc() { return desc; }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

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

    public String toString() {
        return "code : " + this.getCode() + ",desc : " + this.getDesc();
    }
}

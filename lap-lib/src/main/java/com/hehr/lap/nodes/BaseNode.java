package com.hehr.lap.nodes;


import com.hehr.lap.Bundle;

import java.util.concurrent.ExecutionException;

/**
 * @param <T>
 * @author hehr
 */
public abstract class BaseNode<T extends Bundle> {

    /**
     * 在此处申明责任链上每一个节点的名称,
     */
    public static class Name {

        /**
         * 文件遍历节点
         */
        public final static String TRAVERSE_FOLDER = "TRAVERSE_FOLDER";

        /**
         * 缓存查询节点
         */
        public final static String QUERY_CACHE = "QUERY_CACHE";

        /**
         * 音频解析节点
         */
        public final static String PARSE_AUDIO = "PARSE_AUDIO";

        /**
         * 分词节点
         */
        public final static String TOKENIZE = "TOKENIZE";

        /**
         * 创建缓存节点
         */
        public final static String CREATE_CACHE = "CREATE_CACHE";

        /**
         * 更新数据库
         */
        public final static String UPDATE_DB = "UPDATE_DB";

        /**
         * 查询数数据库
         */
        public final static String QUERY_DB = "QUERY_DB";

        /**
         * 文件名LIST TO  SCANNER
         */
        public final static String TRANS = "TRANS";

        /**
         * 初始化节点
         */
        public final static String INITIALIZE = "INITIALIZE";

        /**
         * 查询数据库节点
         */
        public final static String OPT_DB = "OPT_DB";

    }

    /**
     * 设置节点名称
     *
     * @return
     */
    public abstract String getName();

    /**
     * 完成自己部分功课
     *
     * @param t
     * @return T
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TaskFactory.ExecutorServiceShutdownException
     */
    public abstract T doWork(T t) throws InterruptedException, ExecutionException, TaskFactory.ExecutorServiceShutdownException;


    /**
     * 允许任何一个节点在完成自己任务之后直接跳出整个责任链
     *
     * @param t
     * @return
     */
    public abstract boolean hasNext(T t);


}

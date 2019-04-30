package com.hehr.lap.nodes;

import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author hehr
 */
public class TaskFactory {

    private static final String TAG = TaskFactory.class.getSimpleName();

    private ExecutorService executorService = null;

    public TaskFactory() {
        //在此处仅创建一个单线程的线程池，保证每一个节点都能按照提交的顺序顺序执行
        this.executorService = Executors.newSingleThreadExecutor();
    }


    /**
     * 在ExecutorService shut down状态提交任务抛出此异常
     */
    public class ExecutorServiceShutdownException extends Exception {
        public ExecutorServiceShutdownException(String message) {
            super(message);
        }
    }

    /**
     * @return
     */
    public ExecutorService getExecutor() throws ExecutorServiceShutdownException {

        if (!executorService.isShutdown()) {

            return executorService;

        } else {
            Log.e(TAG, "executorService is isShutdown ,can not getExecutorService !");
            throw new ExecutorServiceShutdownException("ExecutorServiceShutdownException");
        }

    }


    /**
     * 销毁线程池资源
     */
    public void destroy() {

        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }

        executorService = null;

    }

}
package com.hehr.lap.nodes;


import com.hehr.lap.Bundle;
import com.hehr.lap.Conf;
import com.hehr.lap.bean.ScannerBean;
import com.hehr.lap.task.CreateCacheFromDBTask;
import com.hehr.lap.task.TaskFactory;
import com.hehr.lap.Error;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;


/**
 *
 * @author hehr
 */
public class CreateCache extends BaseNode{

    private CreateCache(){}

    public static class Builder{

        public CreateCache build(){
            return new CreateCache();
        }

    }

    @Override
    public String getName() {
        return Name.CREATE_CACHE;
    }

    @Override
    public Bundle doWork(Bundle bundle) {

        //在此节点创建bundle中对缓存对象
        bundle.setCache(new ArrayBlockingQueue<ScannerBean>(Conf.AUDIO_SIZE_LIMIT));

        try {
            bundle = bundle.getTaskFactory().getExecutor().submit(
                    new CreateCacheFromDBTask(bundle)
            ).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            bundle.setError(new Error.Builder()
                    .setCode(Error.ERROR.CREATE_CACHE_FROM_DB_TASK_INTRRRUPTED_EXCEPTION_ERROR.getCode())
                    .setDesc(Error.ERROR.CREATE_CACHE_FROM_DB_TASK_INTRRRUPTED_EXCEPTION_ERROR.getDesc())
                    .build());
        } catch (ExecutionException e) {
            e.printStackTrace();
            bundle.setError(new Error.Builder()
                    .setCode(Error.ERROR.CREATE_CACHE_FROM_DB_TASK_EXECUTION_EXCEPTION_ERROR.getCode())
                    .setDesc(Error.ERROR.CREATE_CACHE_FROM_DB_TASK_EXECUTION_EXCEPTION_ERROR.getDesc())
                    .build());
        } catch (TaskFactory.ExecutorServiceShutdownException e) {
            e.printStackTrace();
            bundle.setError(new Error.Builder()
                    .setCode(Error.ERROR.EXECUTOR_SERVICE_SHUTDOWN_EXCEPTION_ERROR.getCode())
                    .setDesc(Error.ERROR.EXECUTOR_SERVICE_SHUTDOWN_EXCEPTION_ERROR.getDesc())
                    .build());
        }

        return bundle;
    }

    @Override
    public boolean hasNext(Bundle bundle) {
        return false;
    }
}

package com.hehr.lap.nodes;

import com.hehr.lap.Bundle;
import com.hehr.lap.task.TaskFactory;
import com.hehr.lap.task.UpdateDBTask;
import com.hehr.lap.Error;

import java.util.concurrent.ExecutionException;

/**
 * 更新数据库节点
 */
public class UpdateDB extends BaseNode{

    private static final String TAG = UpdateDB.class.getSimpleName() ;

    private UpdateDB() {}

    public static class Builder{
        public UpdateDB build(){
            return new UpdateDB();
        }
    }

    @Override
    public String getName() {
        return Name.UPDATE_DB;
    }

    @Override
    public Bundle doWork(Bundle bundle) {

        //bundle中仍然没有解析出来metaDada数据项给丢掉
        bundle.removeInvalid();

        try {
            bundle = bundle.getTaskFactory().getExecutor().submit(
                    new UpdateDBTask(bundle))
                    .get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            bundle.setError(new Error.Builder()
                    .setCode(Error.ERROR.UPDATE_DB_TASK_INTRRRUPTED_EXCEPTION_ERROR.getCode())
                    .setDesc(Error.ERROR.UPDATE_DB_TASK_INTRRRUPTED_EXCEPTION_ERROR.getDesc())
                    .build());
        } catch (ExecutionException e) {
            e.printStackTrace();
            bundle.setError(new Error.Builder()
                    .setCode(Error.ERROR.UPDATE_DB_TASK_EXECUTION_EXCEPTION_ERROR.getCode())
                    .setDesc(Error.ERROR.UPDATE_DB_TASK_EXECUTION_EXCEPTION_ERROR.getDesc())
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
        return true; //更新完数据库之后还需更新缓存
    }
}

package com.hehr.lap.nodes;

import com.hehr.lap.Bundle;
import com.hehr.lap.bean.ScannerBean;
import com.hehr.lap.task.QueryDBTask;
import com.hehr.lap.task.TaskFactory;
import com.hehr.lap.Error;

import java.util.concurrent.ExecutionException;

public class QueryDB extends BaseNode {

    private QueryDB(){}

    public static class Builder{
        public QueryDB build(){
            return new QueryDB();
        }
    }

    @Override
    public String getName() {
        return Name.QUERY_DB;
    }

    @Override
    public Bundle doWork(Bundle bundle) {

        try {
            bundle = bundle.getTaskFactory().getExecutor().submit(
                    new QueryDBTask(bundle)
            ).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            bundle.setError(new Error.Builder()
                    .setCode(Error.ERROR.QUERY_DB_TASK_INTRRRUPTED_EXCEPTION_ERROR.getCode())
                    .setDesc(Error.ERROR.QUERY_DB_TASK_INTRRRUPTED_EXCEPTION_ERROR.getDesc())
                    .build());
        } catch (ExecutionException e) {
            e.printStackTrace();
            bundle.setError(new Error.Builder()
                    .setCode(Error.ERROR.QUERY_DB_TASK_EXECUTION_EXCEPTION_ERROR.getCode())
                    .setDesc(Error.ERROR.QUERY_DB_TASK_EXECUTION_EXCEPTION_ERROR.getDesc())
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
        for (ScannerBean bean : bundle.getList()) {
            if (bean.getMetadata() ==null || bean.getMetadata().isEmpty()) {
                return true;
            }
        }
        return false;
    }
}

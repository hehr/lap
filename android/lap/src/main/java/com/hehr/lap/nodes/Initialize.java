package com.hehr.lap.nodes;

import android.content.Context;

import com.hehr.lap.Bundle;
import com.hehr.lap.task.CopyDBFromAssetTask;
import com.hehr.lap.task.TaskFactory;
import com.hehr.lap.Error;

import java.util.concurrent.ExecutionException;

/**
 * @author hehr
 * 数据库初始化节点
 */
public class Initialize extends BaseNode {

    private Context mContext;

    private TaskFactory mTaskFactory;

    private Initialize(Context context ,TaskFactory taskFactory ) {
        this.mContext = context;
        this.mTaskFactory = taskFactory;
    }

    private Initialize(Builder builder){
        this(builder.getContext() , builder.getTaskFactory());
    }

    public static class Builder{

        public Context context;

        public TaskFactory TaskFactory;

        public Context getContext() {
            return context;
        }

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public TaskFactory getTaskFactory() {
            return TaskFactory;
        }

        public Builder setTaskFactory(TaskFactory taskFactory) {
            TaskFactory = taskFactory;
            return this;
        }

        public Initialize build(){
            return new Initialize(this);
        }
    }

    @Override
    public String getName() {
        return Name.INITIALIZE;
    }

    @Override
    public Bundle doWork(Bundle bundle) {

        //设置上下文
        bundle.setAppContext(mContext);

        //把线程池管理类也放进去
        bundle.setTaskFactory(mTaskFactory);

        //初始化数据库
        try {
            bundle = bundle.getTaskFactory()
                    .getExecutor()
                    .submit(new CopyDBFromAssetTask(bundle))
                    .get();
        } catch (TaskFactory.ExecutorServiceShutdownException e) {
            e.printStackTrace();
            bundle.setError(new Error.Builder()
                    .setCode(Error.ERROR.EXECUTOR_SERVICE_SHUTDOWN_EXCEPTION_ERROR.getCode())
                    .setDesc(Error.ERROR.EXECUTOR_SERVICE_SHUTDOWN_EXCEPTION_ERROR.getDesc())
                    .build());
        } catch (InterruptedException e) {
            e.printStackTrace();
            bundle.setError(new Error.Builder()
                    .setCode(Error.ERROR.COPY_DB_FROM_ASSET_TASK_INTRRRUPTED_EXCEPTION_ERROR.getCode())
                    .setDesc(Error.ERROR.COPY_DB_FROM_ASSET_TASK_INTRRRUPTED_EXCEPTION_ERROR.getDesc())
                    .build());
        } catch (ExecutionException e) {
            e.printStackTrace();
            bundle.setError(new Error.Builder()
                    .setCode(Error.ERROR.COPY_DB_FROM_ASSET_TASK_EXECUTION_EXCEPTION_ERROR.getCode())
                    .setDesc(Error.ERROR.COPY_DB_FROM_ASSET_TASK_EXECUTION_EXCEPTION_ERROR.getDesc())
                    .build());
        }

        bundle.setInitialize(true);

        return bundle;

    }

    @Override
    public boolean hasNext(Bundle bundle) {
        return true;
    }
}

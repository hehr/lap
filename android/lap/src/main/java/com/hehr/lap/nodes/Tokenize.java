package com.hehr.lap.nodes;

import com.hehr.lap.Bundle;
import com.hehr.lap.task.TaskFactory;
import com.hehr.lap.task.TokenizeTask;
import com.hehr.lap.Error;

import java.util.concurrent.ExecutionException;

/**
 * 分词节点
 * 分词完毕之后需要通过歌手数据库校验出歌手名和歌曲名
 *
 * @author hehr
 */
public class Tokenize extends BaseNode {

    private Tokenize() {}

    public static class Builder {
        public Tokenize build() {
            return new Tokenize();
        }
    }

    @Override
    public String getName() {
        return Name.TOKENIZE;
    }

    @Override
    public Bundle doWork(Bundle bundle) {

        try {
            bundle = bundle.getTaskFactory().getExecutor().submit(new TokenizeTask(bundle)).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            bundle.setError(new Error.Builder()
                    .setCode(Error.ERROR.TOKENIZE_TASK_INTRRRUPTED_EXCEPTION_ERROR.getCode())
                    .setDesc(Error.ERROR.TOKENIZE_TASK_INTRRRUPTED_EXCEPTION_ERROR.getDesc())
                    .build());
        } catch (ExecutionException e) {
            e.printStackTrace();
            bundle.setError(new Error.Builder()
                    .setCode(Error.ERROR.TOKENIZE_TASK_EXECUTION_EXCEPTION_ERROR.getCode())
                    .setDesc(Error.ERROR.TOKENIZE_TASK_EXECUTION_EXCEPTION_ERROR.getDesc())
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
    public boolean hasNext(Bundle bundle) {return true;}
}

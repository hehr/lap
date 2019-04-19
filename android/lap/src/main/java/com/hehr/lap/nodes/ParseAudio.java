package com.hehr.lap.nodes;


import com.hehr.lap.Bundle;
import com.hehr.lap.task.FFMARTask;
import com.hehr.lap.task.TaskFactory;
import com.hehr.lap.Error;

import java.util.concurrent.ExecutionException;

public class ParseAudio extends BaseNode<Bundle> {

    private ParseAudio(Builder builder){}

    @Override
    public String getName() {
        return Name.PARSE_AUDIO;
    }

    @Override
    public Bundle doWork(Bundle bundle) {

        try {
            bundle = bundle.getTaskFactory().getExecutor().submit(
                    new FFMARTask(bundle)
            ).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            bundle.setError(new Error.Builder()
                    .setCode(Error.ERROR.FFMAR_TASK_INTRRRUPTED_EXCEPTION_ERROR.getCode())
                    .setDesc(Error.ERROR.FFMAR_TASK_INTRRRUPTED_EXCEPTION_ERROR.getDesc())
                    .build());
        } catch (ExecutionException e) {
            e.printStackTrace();
            bundle.setError(new Error.Builder()
                    .setCode(Error.ERROR.FFMAR_TASK_EXECUTION_EXCEPTION_ERROR.getCode())
                    .setDesc(Error.ERROR.FFMAR_TASK_EXECUTION_EXCEPTION_ERROR.getDesc())
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
    public boolean hasNext( Bundle bundle) {
        //音频解析完成之后默认true,需更新据库
        return true;
    }

    public static class Builder {
        public ParseAudio build() {
            return new ParseAudio(this);
        }
    }
}

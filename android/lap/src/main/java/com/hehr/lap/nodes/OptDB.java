package com.hehr.lap.nodes;

import com.hehr.lap.Bundle;
import com.hehr.lap.task.OptDataFromDBTask;
import com.hehr.lap.task.TaskFactory;

import java.util.concurrent.ExecutionException;

/**
 * 直接从数据库中获取数据
 *
 */
public class OptDB extends BaseNode {


    private int limit;

    private boolean isDesc;

    public int getLimit() {
        return limit;
    }

    public OptDB setLimit(int limit) {
        this.limit = limit;
        return this;
    }

    public boolean isDesc() {
        return isDesc;
    }

    public OptDB setDesc(boolean desc) {
        isDesc = desc;
        return this;
    }

    private OptDB(int limit, boolean isDesc) {
        this.limit = limit;
        this.isDesc = isDesc;
    }

    private OptDB(Builder builder){
        this(builder.getLimit(),builder.isDesc());
    }

    public static class Builder{
        private int limit;
        private boolean isDesc;

        public int getLimit() {
            return limit;
        }

        public Builder setLimit(int limit) {
            this.limit = limit;
            return this;
        }

        public boolean isDesc() {
            return isDesc;
        }

        public Builder setDesc(boolean desc) {
            isDesc = desc;
            return this;
        }

        public OptDB build(){
            return new OptDB(this);
        }
    }

    @Override
    public String getName() {
        return Name.OPT_DB;
    }

    @Override
    public Bundle doWork(Bundle bundle) {

        try {
            bundle = bundle.getTaskFactory().getExecutor().submit(
                    new OptDataFromDBTask(
                            bundle ,
                            getLimit(),
                            isDesc()
                    )
            ).get();
        } catch (TaskFactory.ExecutorServiceShutdownException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return bundle;
    }

    @Override
    public boolean hasNext(Bundle bundle) {
        //查询数据后直接返回，无后续节点
        return false;
    }
}

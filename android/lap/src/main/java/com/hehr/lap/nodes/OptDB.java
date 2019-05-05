package com.hehr.lap.nodes;

import android.database.Cursor;

import com.hehr.lap.Bundle;
import com.hehr.lap.Conf;
import com.hehr.lap.bean.Metadata;
import com.hehr.lap.bean.ScannerBean;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 直接从数据库中获取数据
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

    private OptDB(Builder builder) {
        this(builder.getLimit(), builder.isDesc());
    }

    public static class Builder {
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

        public OptDB build() {
            return new OptDB(this);
        }
    }

    @Override
    public String getName() {
        return Name.OPT_DB;
    }

    @Override
    public Bundle doWork(Bundle bundle) throws InterruptedException, ExecutionException, TaskFactory.ExecutorServiceShutdownException {

        return bundle.getTaskFactory().getExecutor().submit(
                new OptDataFromDBTask(
                        bundle,
                        getLimit(),
                        isDesc()
                )
        ).get();
    }

    @Override
    public boolean hasNext(Bundle bundle) {
        //查询数据后直接返回，无后续节点
        return false;
    }


    class OptDataFromDBTask extends BaseTask {

        private int limit;

        private boolean isDesc;

        public OptDataFromDBTask(Bundle bundle, int limit, boolean isDesc) {
            super(bundle);
            this.limit = limit;
            this.isDesc = isDesc;
        }


        @Override
        public Bundle call() throws Exception {

            String[] columns = {Conf.ScannerDB.METADATA_COLUMN_FILE_NAME, Conf.ScannerDB.METADATA_COLUMN_ARTIST, Conf.ScannerDB.METADATA_COLUMN_TITLE};

            Cursor cursor = bundle.getDbManager().queryData(
                    Conf.ScannerDB.TABLE_METADATA_NAME,
                    columns,
                    null,
                    null,
                    null,
                    null,
                    isDesc ? "id desc" : null,
                    String.valueOf(limit)
            );

            Metadata data;

            List<ScannerBean> lstSb = new ArrayList<>();

            while (cursor != null
                    && cursor.getCount() > 0
                    && cursor.moveToNext()
            ) {

                data = new Metadata();

                String absolutePath = cursor.getString(cursor.getColumnIndex(Conf.ScannerDB.METADATA_COLUMN_FILE_NAME));


                data.setArtist(cursor.getString(cursor.getColumnIndex(Conf.ScannerDB.METADATA_COLUMN_ARTIST)));
                data.setTitle(cursor.getString(cursor.getColumnIndex(Conf.ScannerDB.METADATA_COLUMN_TITLE)));

                lstSb.add(new ScannerBean.Builder()
                        .setMetadata(data)
                        .setAbsolutePath(absolutePath)
                        .build());
            }

            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

            bundle.setList(lstSb);

            return bundle;
        }
    }


}

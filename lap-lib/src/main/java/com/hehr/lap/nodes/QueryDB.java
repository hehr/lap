package com.hehr.lap.nodes;

import android.database.Cursor;

import com.hehr.lap.Bundle;
import com.hehr.lap.Conf;
import com.hehr.lap.bean.Metadata;
import com.hehr.lap.bean.ScannerBean;

import java.util.concurrent.ExecutionException;

public class QueryDB extends BaseNode {

    private QueryDB() {
    }

    public static class Builder {
        public QueryDB build() {
            return new QueryDB();
        }
    }

    @Override
    public String getName() {
        return Name.QUERY_DB;
    }

    @Override
    public Bundle doWork(Bundle bundle) throws TaskFactory.ExecutorServiceShutdownException, ExecutionException, InterruptedException {

        return bundle.getTaskFactory().getExecutor().submit(
                new QueryDBTask(bundle)
        ).get();

    }

    @Override
    public boolean hasNext(Bundle bundle) {
        for (ScannerBean bean : bundle.getList()) {
            if (bean.getMetadata() == null || bean.getMetadata().isEmpty()) {
                return true;
            }
        }
        return false;
    }


    /**
     * @author hehr
     * 数据库查询
     */
    class QueryDBTask extends BaseTask {

        public QueryDBTask(Bundle bundle) {
            super(bundle);
        }

        @Override
        public Bundle call() throws Exception {

            String sql = "select "
                    + Conf.ScannerDB.METADATA_COLUMN_FILE_NAME
                    + " , "
                    + Conf.ScannerDB.METADATA_COLUMN_TITLE
                    + ", "
                    + Conf.ScannerDB.METADATA_COLUMN_ARTIST
                    + ","
                    + Conf.ScannerDB.METADATA_COLUMN_ALBUM
                    + " from "
                    + Conf.ScannerDB.TABLE_METADATA_NAME
                    + " where "
                    + Conf.ScannerDB.METADATA_COLUMN_FILE_NAME
                    + " in ";

            //SQL查询的占位符
            StringBuffer selectInBuffer = new StringBuffer();

            selectInBuffer.append(" ( ");

            String[] selectionArgs = new String[bundle.getToDoList().size()];

            for (int i = 0; i < bundle.getToDoList().size(); i++) {

                selectionArgs[i] = bundle.getToDoList().get(i).getAbsolutePath();

                if (i == 0) {
                    selectInBuffer.append(" ?");
                } else {
                    selectInBuffer.append(",?");
                }

            }
            selectInBuffer.append(" ) ; ");

            sql = sql + selectInBuffer;

            Cursor cursor = bundle.getDbManager().queryData2Cursor(sql, selectionArgs);

            Metadata data;

            while (cursor != null
                    && cursor.getCount() > 0
                    && cursor.moveToNext()) {
                data = new Metadata();
                String absolutePath = cursor.getString(cursor.getColumnIndex(Conf.ScannerDB.METADATA_COLUMN_FILE_NAME));
                data.setArtist(cursor.getString(cursor.getColumnIndex(Conf.ScannerDB.METADATA_COLUMN_ARTIST)));
                data.setTitle(cursor.getString(cursor.getColumnIndex(Conf.ScannerDB.METADATA_COLUMN_TITLE)));
                data.setAlbum(cursor.getString(cursor.getColumnIndex(Conf.ScannerDB.METADATA_COLUMN_ALBUM)));
                for (ScannerBean bean : bundle.getList()) {
                    if (!bean.isEffect() && bean.getAbsolutePath().equals(absolutePath)) {
                        bean.setMetadata(data);
                    }
                }

            }

            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

            return bundle;
        }
    }

}

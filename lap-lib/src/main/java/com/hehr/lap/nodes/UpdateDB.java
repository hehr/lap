package com.hehr.lap.nodes;

import android.content.ContentValues;
import android.text.TextUtils;
import android.util.Log;

import com.hehr.lap.Bundle;
import com.hehr.lap.Conf;
import com.hehr.lap.bean.AudioBean;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 更新数据库节点
 *
 * @author hehr
 */
public class UpdateDB extends BaseNode {

    private static final String TAG = UpdateDB.class.getSimpleName();

    private UpdateDB() {
    }

    public static class Builder {
        public UpdateDB build() {
            return new UpdateDB();
        }
    }

    @Override
    public String getName() {
        return Name.UPDATE_DB;
    }

    @Override
    public Bundle doWork(Bundle bundle) throws TaskFactory.ExecutorServiceShutdownException, ExecutionException, InterruptedException {

        return bundle.getTaskFactory().getExecutor().submit(
                new UpdateDBTask(bundle))
                .get();

    }

    @Override
    public boolean hasNext(Bundle bundle) {
        return false;
    }


    class UpdateDBTask extends BaseTask {

        public UpdateDBTask(Bundle bundle) {
            super(bundle);
        }

        @Override
        public Bundle call() {

            List<ContentValues> values = new ArrayList<ContentValues>();


            List<AudioBean> lst = bundle.transToAudio(bundle.getList());

            ContentValues value;

            for (AudioBean bean : lst) {

                //数据库中只保存含有绝对路径且被成功解析过的数据
                if (TextUtils.isEmpty(bean.getPath())) continue;
                value = new ContentValues();
                value.put(Conf.ScannerDB.METADATA_COLUMN_FILE_NAME, bean.getPath());
                value.put(Conf.ScannerDB.METADATA_COLUMN_ARTIST, bean.getSinger());
                value.put(Conf.ScannerDB.METADATA_COLUMN_TITLE, bean.getSong());
                value.put(Conf.ScannerDB.METADATA_COLUMN_ALBUM, bean.getAlbum());

                values.add(value);

            }

            if (values.size() > 0) {
                long rows = bundle.getDbManager().updateDateInTransaction(Conf.ScannerDB.TABLE_METADATA_NAME, values);
                Log.i(TAG, " updateDBTask update rows  " + rows);
            }

            return bundle;

        }
    }


}

package com.hehr.lap.nodes;

import android.content.ContentValues;
import android.text.TextUtils;
import android.util.Log;

import com.hehr.lap.Bundle;
import com.hehr.lap.Conf;
import com.hehr.lap.bean.ScannerBean;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 更新数据库节点
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

        //bundle中仍然没有解析出来metaDada数据项给丢掉
        bundle.removeInvalid();

        return bundle.getTaskFactory().getExecutor().submit(
                new UpdateDBTask(bundle))
                .get();

    }

    @Override
    public boolean hasNext(Bundle bundle) {
        return true; //更新完数据库之后还需更新缓存
    }


    class UpdateDBTask extends BaseTask {

        public UpdateDBTask(Bundle bundle) {
            super(bundle);
        }

        @Override
        public Bundle call() throws Exception {

            List<ContentValues> values = new ArrayList<ContentValues>();

            for (ScannerBean bean : bundle.getList()) {

                //数据库中只保存含有绝对路径且被成功解析过的数据
                if (TextUtils.isEmpty(bean.getAbsolutePath())
                        || TextUtils.isEmpty(bean.getMetadata().getArtist())
                        || TextUtils.isEmpty(bean.getMetadata().getTitle())
                        || bean.getMetadata().getExtra() != null && !bean.getMetadata().getExtra().isEmpty() //含有多个歌手名信息的也不保存
                ) {
                    continue;
                }

                ContentValues value = new ContentValues();

                value.put(Conf.ScannerDB.METADATA_COLUMN_FILE_NAME, bean.getAbsolutePath());
                value.put(Conf.ScannerDB.METADATA_COLUMN_ARTIST, bean.getMetadata().getArtist());
                value.put(Conf.ScannerDB.METADATA_COLUMN_TITLE, bean.getMetadata().getTitle());

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

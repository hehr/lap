package com.hehr.lap.task;


import android.content.ContentValues;
import android.text.TextUtils;
import android.util.Log;

import com.hehr.lap.Bundle;
import com.hehr.lap.Conf;
import com.hehr.lap.bean.ScannerBean;

import java.util.ArrayList;
import java.util.List;

public class UpdateDBTask extends BaseTask {

    public UpdateDBTask(Bundle bundle) {
        super(bundle);
    }

    public static final String TAG = UpdateDBTask.class.getSimpleName();

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

package com.hehr.lap.task;


import com.hehr.lap.Bundle;
import com.hehr.lap.Conf;
import com.hehr.lap.utils.FilesUtils;
import com.hehr.lap.utils.db.DBManager;
import com.hehr.lap.Error;

import java.io.File;
import java.io.IOException;

/**
 *
 * 拷贝数据库
 * @author hehr
 */
public class CopyDBFromAssetTask extends BaseTask {

    public CopyDBFromAssetTask(Bundle bundle) { super(bundle); }

    private static final String TAG = CopyDBFromAssetTask.class.getSimpleName();

    @Override
    public synchronized Bundle call() throws Exception {

        String dbAbsoluteName=null;

        try {
            dbAbsoluteName = FilesUtils.getInstance().copyAssetsFileToDataBase(bundle.getAppContext(), Conf.DB_NAME);
        } catch (IOException e) {
            bundle.setError(new Error.Builder()
                    .setDesc(Error.ERROR.INIT_DB_COPY_IO_EXCEPTION_ERROR.getDesc())
                    .setCode(Error.ERROR.INIT_DB_COPY_IO_EXCEPTION_ERROR.getCode())
                    .build());
        }

        File dbFile = new File(dbAbsoluteName);

        if(!dbFile.exists()){
            bundle.setError(new Error.Builder()
                    .setDesc(Error.ERROR.INIT_DB_NOT_EXISTS_ERROR.getDesc())
                    .setCode(Error.ERROR.INIT_DB_NOT_EXISTS_ERROR.getCode())
                    .build());
        }

        //初始化数据库设定
        bundle.setDbManager(new DBManager(bundle.getAppContext() , dbAbsoluteName));

        return bundle;
    }
}

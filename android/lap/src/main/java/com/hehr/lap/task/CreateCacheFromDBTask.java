package com.hehr.lap.task;


import android.database.Cursor;
import android.util.Log;

import com.hehr.lap.Bundle;
import com.hehr.lap.Conf;
import com.hehr.lap.bean.Metadata;
import com.hehr.lap.bean.ScannerBean;


/**
 * 从数据库中创建缓存
 */
public class CreateCacheFromDBTask extends BaseTask {

    public CreateCacheFromDBTask(Bundle bundle) { super(bundle); }

    private String TAG = CreateCacheFromDBTask.class.getSimpleName();

    @Override
    public synchronized Bundle call() throws Exception {

        String[] columns = {Conf.ScannerDB.METADATA_COLUMN_FILE_NAME , Conf.ScannerDB.METADATA_COLUMN_ARTIST , Conf.ScannerDB.METADATA_COLUMN_TITLE};

        Cursor cursor = bundle.getDbManager().queryData(
                Conf.ScannerDB.TABLE_METADATA_NAME,
                columns,
                null,
                null,
                null,
                null,
                "id desc",
                String.valueOf(Conf.CACHE_SIZE)
        );

        Metadata data;

        while ( cursor != null
                && cursor.getCount()>0
                && cursor.moveToNext()
                && bundle.getCache().size()<=Conf.CACHE_SIZE
                ){

            data =  new Metadata();

            String absolutePath = cursor.getString(cursor.getColumnIndex(Conf.ScannerDB.METADATA_COLUMN_FILE_NAME))  ;

            data.setArtist(cursor.getString(cursor.getColumnIndex(Conf.ScannerDB.METADATA_COLUMN_ARTIST)));
            data.setTitle(cursor.getString(cursor.getColumnIndex(Conf.ScannerDB.METADATA_COLUMN_TITLE)));

            String nameWithSuffix = absolutePath.substring( absolutePath.lastIndexOf("/") + 1 );

            String suffix = nameWithSuffix.substring(nameWithSuffix.lastIndexOf(".") + 1); //文件后缀名

            String nameWithOutSuffix = nameWithSuffix.substring(0 ,nameWithSuffix.indexOf("."));//去除文件后缀名的文件名

            bundle.getCache().offer(
                    new ScannerBean.Builder()
                            .setAbsolutePath(absolutePath)
                            .setMetadata(data)
                            .setSuffix(suffix)
                            .setFileNameWithSuffix(nameWithSuffix)
                            .setFileNameWithOutSuffix(nameWithOutSuffix)
                            .build()
            );
        }


        if(cursor != null && !cursor.isClosed()){
            cursor.close();
        }

        Log.i(TAG , "CreateCacheFromDBTask create cache size " + bundle.getCache().size());

        return bundle;

    }


}

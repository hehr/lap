package com.hehr.lap.task;

import android.database.Cursor;

import com.hehr.lap.Bundle;
import com.hehr.lap.Conf;
import com.hehr.lap.bean.Metadata;
import com.hehr.lap.bean.ScannerBean;

import java.util.ArrayList;
import java.util.List;

public class OptDataFromDBTask extends BaseTask{

    private int limit;

    private boolean isDesc;

    public OptDataFromDBTask(Bundle bundle, int limit, boolean isDesc) {
        super(bundle);
        this.limit = limit;
        this.isDesc = isDesc;
    }


    @Override
    public Bundle call() throws Exception {

        String[] columns = {Conf.ScannerDB.METADATA_COLUMN_FILE_NAME , Conf.ScannerDB.METADATA_COLUMN_ARTIST , Conf.ScannerDB.METADATA_COLUMN_TITLE};

        Cursor cursor = bundle.getDbManager().queryData(
                Conf.ScannerDB.TABLE_METADATA_NAME,
                columns,
                null,
                null,
                null,
                null,
                isDesc?"id desc":null,
                String.valueOf(limit)
        );

        Metadata data;

        List<ScannerBean> lstSb = new ArrayList<>();

        while ( cursor != null
                && cursor.getCount()>0
                && cursor.moveToNext()
                && bundle.getCache().size()<=Conf.CACHE_SIZE
                ) {

            data = new Metadata();

            String absolutePath = cursor.getString(cursor.getColumnIndex(Conf.ScannerDB.METADATA_COLUMN_FILE_NAME))  ;


            data.setArtist(cursor.getString(cursor.getColumnIndex(Conf.ScannerDB.METADATA_COLUMN_ARTIST)));
            data.setTitle(cursor.getString(cursor.getColumnIndex(Conf.ScannerDB.METADATA_COLUMN_TITLE)));

            lstSb.add(new ScannerBean.Builder()
                    .setMetadata(data)
                    .setAbsolutePath(absolutePath)
                    .build());
        }

        if(cursor != null && !cursor.isClosed()){
            cursor.close();
        }

        bundle.setList(lstSb);

        return bundle;
    }
}

package com.hehr.lap.nodes;

import android.database.Cursor;
import android.text.TextUtils;

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

    private String singer;

    private String song;

    private String album;

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getLimit() {
        return limit;
    }

    public boolean isDesc() {
        return isDesc;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setDesc(boolean desc) {
        isDesc = desc;
    }

    public OptDB(int limit, boolean isDesc, String singer, String song, String album) {
        setLimit(limit);
        setDesc(isDesc);
        setAlbum(album);
        setSinger(singer);
        setSong(song);
    }

    private OptDB(Builder builder) {
        this(builder.getLimit(), builder.isDesc(), builder.getSinger(), builder.getSong(), builder.getAlbum());
    }

    public static class Builder {

        private int limit;

        private boolean isDesc;

        private String singer;

        private String song;

        private String album;

        public String getSinger() {
            return singer;
        }

        public Builder setSinger(String singer) {
            this.singer = singer;
            return this;
        }

        public String getSong() {
            return song;
        }

        public Builder setSong(String song) {
            this.song = song;
            return this;
        }

        public String getAlbum() {
            return album;
        }

        public Builder setAlbum(String album) {
            this.album = album;
            return this;
        }

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
                        isDesc(),
                        getSinger(),
                        getSong(),
                        getAlbum()
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

        private String singer;

        private String song;

        private String album;

        public OptDataFromDBTask(Bundle bundle, int limit, boolean isDesc, String singer, String song, String album) {
            super(bundle);
            this.limit = limit;
            this.isDesc = isDesc;
            this.album = album;
            this.singer = singer;
            this.song = song;
        }


        @Override
        public Bundle call() throws Exception {

            String[] columns = {Conf.ScannerDB.METADATA_COLUMN_FILE_NAME, Conf.ScannerDB.METADATA_COLUMN_ARTIST, Conf.ScannerDB.METADATA_COLUMN_TITLE, Conf.ScannerDB.METADATA_COLUMN_ALBUM};

            SqlSelection sqlSelection = generateSqlSelection(this.singer, this.song, this.album);

            Cursor cursor = bundle.getDbManager().queryData(
                    Conf.ScannerDB.TABLE_METADATA_NAME,
                    columns,
                    sqlSelection == null ? null : sqlSelection.getSelection(),
                    sqlSelection == null ? null : sqlSelection.getSelectionArgs(),
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
                data.setAlbum(cursor.getString(cursor.getColumnIndex(Conf.ScannerDB.METADATA_COLUMN_ALBUM)));

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

    /**
     * sql查询条件
     */
    private class SqlSelection {

        public SqlSelection(String selection, String[] selectionArgs) {
            this.selection = selection;
            this.selectionArgs = selectionArgs;
        }

        private String selection;

        private String[] selectionArgs;

        public String[] getSelectionArgs() {
            return selectionArgs;
        }

        public String getSelection() {
            return selection;
        }
    }

    public SqlSelection generateSqlSelection(String singer, String song, String album) {

        if (!TextUtils.isEmpty(this.singer) && !TextUtils.isEmpty(this.song) && !TextUtils.isEmpty(album)) {
            String selection = Conf.ScannerDB.METADATA_COLUMN_ARTIST + " like ? " + " and " + Conf.ScannerDB.METADATA_COLUMN_TITLE + " like ?" + " and " + Conf.ScannerDB.METADATA_COLUMN_ALBUM + " like ?";
            String[] selectionArgs = new String[3];
            selectionArgs[0] = "%" + this.singer + "%";
            selectionArgs[1] = "%" + this.song + "%";
            selectionArgs[2] = "%" + this.album + "%";
            return new SqlSelection(selection, selectionArgs);
        } else if (!TextUtils.isEmpty(this.singer) && !TextUtils.isEmpty(this.song) && TextUtils.isEmpty(album)) {
            String selection = Conf.ScannerDB.METADATA_COLUMN_ARTIST + " like ? " + " and " + Conf.ScannerDB.METADATA_COLUMN_TITLE + " like ?";
            String[] selectionArgs = new String[2];
            selectionArgs[0] = "%" + this.singer + "%";
            selectionArgs[1] = "%" + this.song + "%";
            return new SqlSelection(selection, selectionArgs);
        } else if (!TextUtils.isEmpty(this.singer) && TextUtils.isEmpty(this.song) && !TextUtils.isEmpty(album)) {
            String selection = Conf.ScannerDB.METADATA_COLUMN_ARTIST + " like ? " + " and " + Conf.ScannerDB.METADATA_COLUMN_ALBUM + " like ?";
            String[] selectionArgs = new String[2];
            selectionArgs[0] = "%" + this.singer + "%";
            selectionArgs[1] = "%" + this.album + "%";
            return new SqlSelection(selection, selectionArgs);
        } else if (TextUtils.isEmpty(this.singer) && !TextUtils.isEmpty(this.song) && !TextUtils.isEmpty(album)) {
            String selection = Conf.ScannerDB.METADATA_COLUMN_TITLE + " like ? " + " and " + Conf.ScannerDB.METADATA_COLUMN_ALBUM + " like ?";
            String[] selectionArgs = new String[2];
            selectionArgs[0] = "%" + this.song + "%";
            selectionArgs[1] = "%" + this.album + "%";
            new SqlSelection(selection, selectionArgs);
        } else {
            String selection = Conf.ScannerDB.METADATA_COLUMN_ARTIST + " like ? " + " or " + Conf.ScannerDB.METADATA_COLUMN_TITLE + " like ? " + " or " + Conf.ScannerDB.METADATA_COLUMN_ALBUM + " like ? ";
            String[] selectionArgs = new String[3];
            selectionArgs[0] = "%" + this.singer + "%";
            selectionArgs[1] = "%" + this.song + "%";
            selectionArgs[2] = "%" + this.album + "%";
            new SqlSelection(selection, selectionArgs);
        }
        return null;
    }


}

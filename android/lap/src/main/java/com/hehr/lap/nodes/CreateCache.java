//package com.hehr.lap.nodes;
//
//
//import android.database.Cursor;
//import android.util.Log;
//
//import com.hehr.lap.Bundle;
//import com.hehr.lap.Conf;
//import com.hehr.lap.bean.Metadata;
//import com.hehr.lap.bean.ScannerBean;
//
//import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.ExecutionException;
//
//
///**
// * @author hehr
// */
//public class CreateCache extends BaseNode {
//
//    private CreateCache() {}
//
//    public static class Builder {
//        public CreateCache build() {
//            return new CreateCache();
//        }
//    }
//
//    @Override
//    public String getName() {
//        return Name.CREATE_CACHE;
//    }
//
//    @Override
//    public Bundle doWork(Bundle bundle) throws InterruptedException, ExecutionException, TaskFactory.ExecutorServiceShutdownException {
//        //在此节点创建bundle中对缓存对象
//        bundle.setCache(new ArrayBlockingQueue<ScannerBean>(Conf.AUDIO_SIZE_LIMIT));
//
//        return bundle.getTaskFactory().getExecutor().submit(
//                new CreateCacheFromDBTask(bundle)
//        ).get();
//    }
//
//    @Override
//    public boolean hasNext(Bundle bundle) {
//        return false;
//    }
//
//
//    /**
//     * 从数据库中创建缓存
//     */
//    private class CreateCacheFromDBTask extends BaseTask {
//
//        public CreateCacheFromDBTask(Bundle bundle) {
//            super(bundle);
//        }
//
//        private String TAG = CreateCacheFromDBTask.class.getSimpleName();
//
//        @Override
//        public synchronized Bundle call() throws Exception {
//
//            String[] columns = {Conf.ScannerDB.METADATA_COLUMN_FILE_NAME, Conf.ScannerDB.METADATA_COLUMN_ARTIST, Conf.ScannerDB.METADATA_COLUMN_TITLE};
//
//            Cursor cursor = bundle.getDbManager().queryData(
//                    Conf.ScannerDB.TABLE_METADATA_NAME,
//                    columns,
//                    null,
//                    null,
//                    null,
//                    null,
//                    "id desc",
//                    String.valueOf(Conf.CACHE_SIZE)
//            );
//
//            Metadata data;
//
//            while (cursor != null
//                    && cursor.getCount() > 0
//                    && cursor.moveToNext()
//                    && bundle.getCache().size() <= Conf.CACHE_SIZE
//            ) {
//
//                data = new Metadata();
//
//                String absolutePath = cursor.getString(cursor.getColumnIndex(Conf.ScannerDB.METADATA_COLUMN_FILE_NAME));
//
//                data.setArtist(cursor.getString(cursor.getColumnIndex(Conf.ScannerDB.METADATA_COLUMN_ARTIST)));
//                data.setTitle(cursor.getString(cursor.getColumnIndex(Conf.ScannerDB.METADATA_COLUMN_TITLE)));
//
//                String nameWithSuffix = absolutePath.substring(absolutePath.lastIndexOf("/") + 1);
//
//                String suffix = nameWithSuffix.substring(nameWithSuffix.lastIndexOf(".") + 1); //文件后缀名
//
//                String nameWithOutSuffix = nameWithSuffix.substring(0, nameWithSuffix.indexOf("."));//去除文件后缀名的文件名
//
//                bundle.getCache().offer(
//                        new ScannerBean.Builder()
//                                .setAbsolutePath(absolutePath)
//                                .setMetadata(data)
//                                .setSuffix(suffix)
//                                .setFileNameWithSuffix(nameWithSuffix)
//                                .setFileNameWithOutSuffix(nameWithOutSuffix)
//                                .build()
//                );
//            }
//
//
//            if (cursor != null && !cursor.isClosed()) {
//                cursor.close();
//            }
//
//            Log.i(TAG, "CreateCacheFromDBTask create cache size " + bundle.getCache().size());
//
//            return bundle;
//
//        }
//
//
//    }
//
//
//}

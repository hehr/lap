package com.hehr.lap.nodes;

import android.content.Context;

import com.hehr.lap.Bundle;
import com.hehr.lap.Conf;
import com.hehr.lap.Error;
import com.hehr.lap.utils.db.DBManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;

/**
 * @author hehr
 * 数据库初始化节点
 */
public class Initialize extends BaseNode {

    private Context mContext;

    private TaskFactory mTaskFactory;

    private Initialize(Context context, TaskFactory taskFactory) {
        this.mContext = context;
        this.mTaskFactory = taskFactory;
    }

    private Initialize(Builder builder) {
        this(builder.getContext(), builder.getTaskFactory());
    }

    public static class Builder {

        public Context context;

        public TaskFactory TaskFactory;

        public Context getContext() {
            return context;
        }

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public TaskFactory getTaskFactory() {
            return TaskFactory;
        }

        public Builder setTaskFactory(TaskFactory taskFactory) {
            TaskFactory = taskFactory;
            return this;
        }

        public Initialize build() {
            return new Initialize(this);
        }
    }

    @Override
    public String getName() {
        return Name.INITIALIZE;
    }

    @Override
    public Bundle doWork(Bundle bundle) throws InterruptedException, ExecutionException, TaskFactory.ExecutorServiceShutdownException {

        //设置上下文
        bundle.setAppContext(mContext);

        //把线程池管理类也放进去
        bundle.setTaskFactory(mTaskFactory);

        //初始化数据库
        bundle = bundle.getTaskFactory()
                .getExecutor()
                .submit(new CopyDBFromAssetTask(bundle))
                .get();

        bundle.setInitialize(true);

        return bundle;

    }

    @Override
    public boolean hasNext(Bundle bundle) {
        return true;
    }


    /**
     * 拷贝数据库
     *
     * @author hehr
     */
    private class CopyDBFromAssetTask extends BaseTask {

        public CopyDBFromAssetTask(Bundle bundle) {
            super(bundle);
        }


        /**
         * copy Assets file to pack database
         *
         * @param context
         * @param fileName
         * @return file 文件的绝对路径
         * @throws IOException
         */
        private String copyAssetsFileToDataBase(Context context, String fileName) throws IOException {

            String folderPath = "/data/data/" + context.getPackageName() + "/databases/";

            File folder = new File(folderPath);

            if (!folder.exists()) {
                folder.mkdirs();
            }

            //文件的完整绝对路径
            String filePath = folderPath + File.separator + fileName;

            File file = new File(filePath);

            //文件存在直接返回
            if (file.exists()) {
                return filePath;
            }

            file.setReadable(true);

            file.setWritable(true);

            InputStream im = context.getAssets().open(fileName);

            OutputStream om = new FileOutputStream(filePath);

            byte[] buffer = new byte[1024];

            int length;

            while ((length = im.read(buffer)) > 0) {
                om.write(buffer, 0, length);
            }

            om.flush();
            om.close();
            im.close();

            return filePath;

        }

        @Override
        public synchronized Bundle call() {

            String dbAbsoluteName = null;

            try {
                dbAbsoluteName = copyAssetsFileToDataBase(bundle.getAppContext(), Conf.DB_NAME);
            } catch (IOException e) {
                bundle.setError(new Error.Builder()
                        .setDesc(Error.ERROR.INIT_DB_COPY_IO_EXCEPTION_ERROR.getDesc())
                        .setCode(Error.ERROR.INIT_DB_COPY_IO_EXCEPTION_ERROR.getCode())
                        .build());
            }

            File dbFile = new File(dbAbsoluteName);

            if (!dbFile.exists()) {
                bundle.setError(new Error.Builder()
                        .setDesc(Error.ERROR.INIT_DB_NOT_EXISTS_ERROR.getDesc())
                        .setCode(Error.ERROR.INIT_DB_NOT_EXISTS_ERROR.getCode())
                        .build());
            }

            //初始化数据库设定
            bundle.setDbManager(new DBManager(bundle.getAppContext(), dbAbsoluteName));

            return bundle;
        }
    }
}

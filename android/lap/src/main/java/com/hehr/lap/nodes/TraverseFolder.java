package com.hehr.lap.nodes;


import android.util.Log;

import com.hehr.lap.Bundle;
import com.hehr.lap.Conf;
import com.hehr.lap.Error;
import com.hehr.lap.bean.ScannerBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author hehr
 * 文件扫描节点
 */
public class TraverseFolder extends BaseNode<Bundle> {


    private static final String TAG = TraverseFolder.class.getSimpleName();

    private String path;

    private TraverseFolder(Builder builder) {
        this.path = builder.getPath();
    }

    @Override
    public String getName() {
        return Name.TRAVERSE_FOLDER;
    }

    @Override
    public Bundle doWork(Bundle bundle) throws TaskFactory.ExecutorServiceShutdownException, ExecutionException, InterruptedException {

        return bundle
                .getTaskFactory()
                .getExecutor()
                .submit(new TraverTask(bundle)).get();

    }

    @Override
    public boolean hasNext(Bundle bundle) {
        return true;
    }

    /**
     * builder 传递特殊参数
     */
    public static class Builder {

        private String path;

        public String getPath() {
            return path;
        }

        public Builder setPath(String path) {
            this.path = path;
            return this;
        }

        public TraverseFolder build() {
            return new TraverseFolder(this);
        }

    }


    /**
     * 文件遍历线程
     */
    class TraverTask extends BaseTask {

        public TraverTask(Bundle bundle) {
            super(bundle);
        }

        /**
         * 深遍历文件夹并找出来所有音频文件
         * @param path
         * @param audioFiles
         * @return
         */
        public List<ScannerBean> traverAndGetAllAudioFiles(String path, List<ScannerBean> audioFiles) {

            File dir = new File(path);
            if (dir.exists() && dir.canRead() && dir.isDirectory() && dir.list() != null) {
                for (String name : dir.list()) {
                    //拼接出完整文件路径
                    File file = new File(path + File.separator + name);
                    if (!file.exists()) {
                        continue;
                    } else if (file.isDirectory()) { //如果是目录继续递归检索
                        traverAndGetAllAudioFiles(file.getAbsolutePath(), audioFiles);
                    } else if (name.length() > 3
                            && !file.isDirectory()
                            && file.length() >= Conf.AUDIO_SIZE_LIMIT) {

                        //如果文件不包含文件后缀名，则过滤掉
                        if (!name.contains(".")) continue;
                        //文件后缀名
                        String suffix = name.substring(name.lastIndexOf(".") + 1);
                        //去除文件后缀名的文件名
                        String nameWithOutSuffix = name.substring(0, name.lastIndexOf("."));
                        //此处不去除特殊字符和英文字符的文件名，分别在解析过后或者分词之后对每个词进行判断
                        if (Conf.AUDIO_TYPE.contains(suffix)) {
                            audioFiles.add(new ScannerBean.Builder()
                                    .setAbsolutePath(path + "/" + name)
                                    .setSuffix(suffix)
                                    .setFileNameWithOutSuffix(nameWithOutSuffix)
                                    .setFileNameWithSuffix(name)
                                    .build()
                            ); //完整文件路径
                        }
                        //限制扫描文件数量
                        if (audioFiles.size() >= Conf.SCAN_ENTRY_NUMBER) {
                            break;
                        }
                    }
                }
            } else {
                Log.e(TAG,  "path : "+ path + " is illegal !");
            }

            return audioFiles;

        }

        @Override
        public Bundle call(){

            File dir = new File(path);

            if (!dir.isDirectory()) {
                bundle.setError(new Error.Builder()
                        .setDesc(Error.ERROR.SCAN_PATH_NOT_DIR_ERROR.getDesc())
                        .setCode(Error.ERROR.SCAN_PATH_NOT_DIR_ERROR.getCode())
                        .build());
                return bundle;
            }


            List<ScannerBean> lst = new ArrayList<ScannerBean>();

            lst = traverAndGetAllAudioFiles(path, lst);

            Log.d(TAG, "TraverseFolder get all audio files :" + lst.toString());

            bundle.setList(lst);

            bundle.createMetaData();

            return bundle;
        }
    }

}

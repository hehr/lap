package com.hehr.lap.utils;

import android.content.Context;

import com.hehr.lap.Conf;
import com.hehr.lap.bean.ScannerBean;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class FilesUtils {

    private final String TAG = this.getClass().getSimpleName();

    private static class Holder {
        private static final FilesUtils INSTANCE = new FilesUtils();
    }

    private FilesUtils() {
    }

    public static final FilesUtils getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * traverse folder of current path ,get all files of audio
     *
     * @param path
     * @return
     */
    public List<ScannerBean> traverseDirGetAllAudio(String path) {

        File dir = new File(path);

        List<ScannerBean> audioFiles = new ArrayList<>();

        if (dir.isDirectory() && dir.list() != null && dir.list().length > 0) {
            for (String name : dir.list()) {
                //拼接出完整文件路径
                File file = new File(path + "/" + name);
                if (name.length() > 3
                        && file.exists()
                        && !file.isDirectory()
                        && file.length() >= Conf.AUDIO_SIZE_LIMIT) {

                    //如果文件不包含文件后缀名，则过滤掉
                    if(!name.contains(".")) continue;
                    //文件后缀名
                    String suffix = name.substring(name.lastIndexOf(".") + 1);
                    //去除文件后缀名的文件名
                    String nameWithOutSuffix = name.substring(0, name.lastIndexOf(".") );
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
                    if(audioFiles.size() >= Conf.SCAN_ENTRY_NUMBER){ break;}
                }
            }
        }

        return audioFiles;

    }


    /**
     * copy Assets file to pack database
     *
     * @param context
     * @param fileName
     * @return file 文件的绝对路径
     * @throws IOException
     */
    public String copyAssetsFileToDataBase(Context context, String fileName) throws IOException {

        String folderPath = "/data/data/" + context.getPackageName() + "/databases/";

        File folder = new File(folderPath);

        if (!folder.exists()) {
            folder.mkdirs();
        }

        //文件的完整绝对路径
        String filePath = folderPath + "/" + fileName;

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


}

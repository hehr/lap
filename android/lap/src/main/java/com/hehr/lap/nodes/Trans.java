package com.hehr.lap.nodes;

import android.text.TextUtils;

import com.hehr.lap.Bundle;
import com.hehr.lap.bean.ScannerBean;
import com.hehr.lap.Error;

import java.util.ArrayList;
import java.util.List;

/**
 * 将文件路径转换成ScannerBean
 *
 * @author hehr
 */
public class Trans extends BaseNode<Bundle> {

    private static final String TAG = Trans.class.getSimpleName();

    //文件名列表
    private List<String> namesList;

    public List<String> getFileNameList() {
        return namesList;
    }

    public void setFileNameList(List<String> fileNameList) {
        this.namesList = fileNameList;
    }

    private Trans(List<String> fileNameList) {
        setFileNameList(fileNameList);
    }

    private Trans(Builder builder) {
        this(builder.getNamesList());
    }

    public static class Builder {

        private List<String> namesList;

        public List<String> getNamesList() {
            return namesList;
        }

        public Builder setNamesList(List<String> namesList) {
            this.namesList = namesList;
            return this;
        }

        public Trans build() {
            return new Trans(this);
        }
    }

    @Override
    public String getName() {
        return Name.TRANS;
    }

    @Override
    public Bundle doWork(Bundle bundle) {

        try {
            List<ScannerBean> list = new ArrayList<>();
            ScannerBean.Builder builder;
            for (String name : namesList) {
                //传入字符串空或者不含文件类型
                if (TextUtils.isEmpty(name)) {
                    continue;
                }

                builder = new ScannerBean.Builder();
                //填充文件原始名称
                builder.setOriginalName(name);

                //开始拆分文件名
                name = name.trim();

                if (name.contains(".")) {
                    //文件后缀名
                    String suffix = name.substring(name.lastIndexOf(".") + 1);
                    //去除文件后缀名的文件名
                    String nameWithOutSuffix = name.substring(0, name.lastIndexOf(".") );
                    builder.setFileNameWithSuffix(name);
                    builder.setFileNameWithOutSuffix(nameWithOutSuffix);
                    builder.setSuffix(suffix);
                } else {
                    builder.setFileNameWithOutSuffix(name);
                }

                list.add(builder.build());

            }

            bundle.setList(list);

            bundle.createMetaData();

        } catch (Exception e) {
            e.printStackTrace();
            bundle.setError(new Error.Builder()
                    .setCode(Error.ERROR.TRANS_NODE_EXCEPTION_ERROR.getCode())
                    .setDesc(Error.ERROR.TRANS_NODE_EXCEPTION_ERROR.getDesc())
                    .build());
        }

        return bundle;

    }

    @Override
    public boolean hasNext(Bundle bundle) {
        return true;
    }
}

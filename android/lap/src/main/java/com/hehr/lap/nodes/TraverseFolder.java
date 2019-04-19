package com.hehr.lap.nodes;


import com.hehr.lap.Bundle;
import com.hehr.lap.utils.FilesUtils;
import com.hehr.lap.Error;

import java.io.File;

public class TraverseFolder extends BaseNode<Bundle> {

    private String path;

    private TraverseFolder(Builder builder) {
        this.path = builder.getPath();
    }

    @Override
    public String getName() {
        return Name.TRAVERSE_FOLDER;
    }

    @Override
    public Bundle doWork(Bundle bundle) {

        File dir = new File(path);

        if (!dir.isDirectory()) {
            bundle.setError(new Error.Builder()
                    .setDesc(Error.ERROR.SCAN_PATH_NOT_DIR_ERROR.getDesc())
                    .setCode(Error.ERROR.SCAN_PATH_NOT_DIR_ERROR.getCode())
                    .build());
            return bundle;
        }

        bundle.setList(FilesUtils.getInstance().traverseDirGetAllAudio(path));

        bundle.createMetaData();

        return bundle;
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

}

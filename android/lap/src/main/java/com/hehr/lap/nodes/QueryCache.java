package com.hehr.lap.nodes;

import com.hehr.lap.Bundle;
import com.hehr.lap.bean.Metadata;
import com.hehr.lap.bean.ScannerBean;

/**
 * 查询混存中已经存在的数据，并填充到LIST中
 *
 */
public class QueryCache extends BaseNode<Bundle>{

    private static final String TAG = QueryCache.class.getSimpleName();

    @Override
    public String getName() {
        return Name.QUERY_CACHE;
    }

    private QueryCache() {}

    @Override
    public Bundle doWork(Bundle bundle) {

        for (ScannerBean bean:bundle.getList()) {
            for (ScannerBean cacheScannerBean:bundle.getCache()) {
                if(cacheScannerBean.isSameAudio(bean)){
                    Metadata metadata =  new Metadata();
                    metadata.setTitle( cacheScannerBean.getMetadata().getTitle());
                    metadata.setArtist(cacheScannerBean.getMetadata().getArtist());
                    if(!cacheScannerBean.getMetadata().isEmpty()){
                        metadata.setExtra(cacheScannerBean.getMetadata().getExtra());
                    }
                    bean.setMetadata(metadata);
                }
            }

        }

        return bundle;
    }

    @Override
    public boolean hasNext(Bundle bundle) {
        for (ScannerBean bean : bundle.getList()) {
            if (bean.getMetadata() ==null || bean.getMetadata().isEmpty()) {
                return true;
            }
        }
        return false;
    }



    public static class Builder{
        public QueryCache build(){
            return new QueryCache();
        }
    }


}

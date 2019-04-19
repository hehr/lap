package com.hehr.lap.listener;

import android.support.annotation.Keep;

import com.hehr.lap.bean.AudioBean;
import com.hehr.lap.Error;

import java.util.List;

/**
 * 数据库查询监听器
 */
@Keep
public interface QueryListener {
    /**
     * 扫描结果回调
     * @param list
     */
    void onResult(List<AudioBean> list);

    /**
     * 扫描错误回调
     * @param error
     */
    void onError(Error error);
}

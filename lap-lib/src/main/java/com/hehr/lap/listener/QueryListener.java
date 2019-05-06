package com.hehr.lap.listener;

import com.hehr.lap.Error;
import com.hehr.lap.bean.AudioBean;

import java.util.List;

/**
 * 数据库查询监听器
 */

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

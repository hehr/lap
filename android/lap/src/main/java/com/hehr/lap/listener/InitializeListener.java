package com.hehr.lap.listener;

import com.hehr.lap.Error;

/**
 * 引擎初始化监听器
 *
 */

public interface InitializeListener {

    /**
     * 引擎初始化错误回调
     */
    void onError(Error error);

    /**
     * 引擎初始化成功
     */
    void onInit();


}

package com.hehr.lap.listener;

import com.hehr.lap.bean.ScannerBean;
import com.hehr.lap.Error;

import java.util.List;

/**
 * AudioScanner 模块对外统一回调
 * @author hehr
 */
public interface ChainsListener {
     /**
      * 执行完成结果回调
      * @param list
      */
     void onComplete(List<ScannerBean> list);

     /**
      * 运行时错误回调
      * @param e
      */
     void onError(Error e);
}

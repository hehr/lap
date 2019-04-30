package com.hehr.lap;

import android.util.Log;

import com.hehr.lap.bean.ScannerBean;
import com.hehr.lap.listener.ChainsListener;
import com.hehr.lap.nodes.BaseNode;
import com.hehr.lap.nodes.TaskFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * DESIGN:
 * 整个模块使用责任链模式实现，每个节点之间传递一个统一的数据集合
 * Node 在完成各自部分工作后直接修改传递的数据集合并返回，
 * 直至某个节点完成之后出现错误或者hasNext()返回false
 * @author hehr
 */
public class Chains {

    private static final String TAG = Holder.INSTANCE.getClass().getSimpleName();

    private static class Holder {
        private static final Chains INSTANCE = new Chains();
    }

    public static final Chains getInstance() {
        return Holder.INSTANCE;
    }


    /**
     * 创建并开启整个责任链循环
     */
    public void run(Bundle bundle , List<BaseNode> nodes, ChainsListener listener) {

        if( bundle == null){
            listener.onError(new Error.Builder()
                    .setDesc(Error.ERROR.INIT_NOT_EXECUTE_ERROR.getDesc())
                    .setCode(Error.ERROR.INIT_NOT_EXECUTE_ERROR.getCode())
                    .build());
            return ;
        }

        //责任链开始，开辟一个新的List<ScannerBean>
        bundle.setList(new ArrayList<ScannerBean>());

        for (BaseNode node : nodes) {

            Log.i(TAG , "to do list size : " + bundle.getToDoList().size());

            Log.i(TAG , "next nodes : " + node.getName());

            try {
                bundle = node.doWork(bundle);
            } catch (InterruptedException e) {
                e.printStackTrace();
                bundle.setError(new Error.Builder()
                        .setCode(Error.ERROR.TASK_INTRRRUPTED_EXCEPTION_ERROR.getCode())
                        .setDesc(Error.ERROR.TASK_INTRRRUPTED_EXCEPTION_ERROR.getDesc())
                        .build());
            } catch (ExecutionException e) {
                e.printStackTrace();
                bundle.setError(new Error.Builder()
                        .setCode(Error.ERROR.TASK_EXECUTION_EXCEPTION_ERROR.getCode())
                        .setDesc(Error.ERROR.TASK_EXECUTION_EXCEPTION_ERROR.getDesc())
                        .build());
            } catch (TaskFactory.ExecutorServiceShutdownException e) {
                e.printStackTrace();
                bundle.setError(new Error.Builder()
                        .setCode(Error.ERROR.EXECUTOR_SERVICE_SHUTDOWN_EXCEPTION_ERROR.getCode())
                        .setDesc(Error.ERROR.EXECUTOR_SERVICE_SHUTDOWN_EXCEPTION_ERROR.getDesc())
                        .build());
            }

            if (bundle.hasError() || ! node.hasNext(bundle)) {
                jump(bundle,listener);return;
            }

        }

        jump( bundle , listener);

    }


    /**
     * 处理跳出责任链的情况
     * @param bundle
     * @param listener
     */
    private void jump(Bundle bundle , ChainsListener listener){

        if(bundle.hasError()){

            listener.onError(bundle.getError());

        }else {

            bundle.removeInvalid();//去除无效数据

            bundle.updateCache();//更新缓存

            listener.onComplete(bundle.getList());
        }


    }

}

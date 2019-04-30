package com.hehr.lap;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.hehr.lap.bean.Metadata;
import com.hehr.lap.bean.ScannerBean;
import com.hehr.lap.nodes.TaskFactory;
import com.hehr.lap.utils.db.DBManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

public class Bundle {

    private static final String TAG = Bundle.class.getSimpleName();

    private List<ScannerBean> list;

    private Error error;

    //数据缓存
    public Queue<ScannerBean> cache;

    private Context appContext;

    private DBManager dbManager;

    private TaskFactory taskFactory;

    private boolean isInitialize;

    public TaskFactory getTaskFactory() {
        return taskFactory;
    }

    public Bundle setTaskFactory(TaskFactory taskFactory) {
        this.taskFactory = taskFactory;
        return this;
    }

    public DBManager getDbManager() {
        return dbManager;
    }

    public void setDbManager(DBManager dbManager) {
        this.dbManager = dbManager;
    }

    public Queue<ScannerBean> getCache() {
        return cache;
    }

    public void setCache(Queue<ScannerBean> cache) {
        this.cache = cache;
    }

    public Context getAppContext() {
        return appContext;
    }

    public void setAppContext(Context appContext) {
        this.appContext = appContext;
    }

    public List<ScannerBean> getList() {
        return list;
    }

    public Bundle setList(List<ScannerBean> list) {
        this.list = list;
        return this;
    }

    public Error getError() {
        return error;
    }

    public Bundle setError(Error error) {
        this.error = error;
        return this;
    }

    public boolean isInitialize() { return isInitialize; }

    public void setInitialize(boolean initialize) { isInitialize = initialize; }

    /**
     * 当前bundle中待处理的数据
     * 该list只能作为辅助使用，实际修改还是要修改bundle.getList()对象
     *
     * @return
     */
    public ArrayList<ScannerBean> getToDoList() {

        ArrayList<ScannerBean> todoList = new ArrayList<>();

        for (ScannerBean b : this.getList()) {
            if (!b.isEffect()) {
                todoList.add(b);
            }
        }

        return todoList;

    }

    /**
     * 创建依据当前list中的数据，创建对应的METADATA对象
     * 请在责任链创建完成，并且确定bundle中list的个数之后调用
     */
    public void createMetaData() {
        for (ScannerBean bean : this.getList()) {
            if (bean.getMetadata() == null) {
                bean.setMetadata(new Metadata());
            }
        }
    }

    /**
     * 当前bundle中是否执行完毕某个节点产生了错误
     *
     * @return
     */
    public boolean hasError() {
        return this.getError() != null && !TextUtils.isEmpty(getError().getDesc()) ? true : false;
    }

    /**
     * 删除当前bundle中依然没有被有效处理的数据
     */
    public void removeInvalid() {
        for (Iterator<ScannerBean> it = getList().iterator(); it.hasNext(); ) {
            ScannerBean bean = it.next();
            if (bean.getMetadata()==null
                    || bean.getMetadata().isEmpty()) {
                it.remove();
            }
        }
    }

    /**
     * 更新缓存数据
     */
    public void updateCache() {



        for (int i = 0; Conf.CACHE_SIZE > 0 && getList() != null&& i < getList().size(); i++) {

            ScannerBean bean = getList().get(i);

            if (!bean.isEffect()) {
                continue;
            }

            if (getCache().size() >= Conf.CACHE_SIZE) {
                getCache().poll();
            }

            getCache().offer(bean);

        }
        Log.i(TAG, "update cache , current cache size  " + getCache().size());
    }

    /**
     * 销毁bundle内所有数据
     */
    public void release(){

        //关闭数据库连接
        if(getDbManager()!= null){
            getDbManager().destroy();
        }

        //关闭线程池
        if(getTaskFactory()!=null){
            getTaskFactory().destroy();
        }

        //释放缓存
        while (getCache()!= null && getCache().size()>0){
            getCache().poll();
        }

        this.list = null;

        setInitialize(false);

    }

}

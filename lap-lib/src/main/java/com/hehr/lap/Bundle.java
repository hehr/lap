package com.hehr.lap;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.hehr.lap.bean.AudioBean;
import com.hehr.lap.bean.Metadata;
import com.hehr.lap.bean.ScannerBean;
import com.hehr.lap.nodes.TaskFactory;
import com.hehr.lap.utils.db.DBManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Bundle {

    private static final String TAG = Bundle.class.getSimpleName();

    private List<ScannerBean> list;

    private Error error;

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

        this.list = null;

        setInitialize(false);

    }

    /**
     *
     * scannerBean to AudioBean
     * @param list
     * @return
     */
    public List<AudioBean> transToAudio(List<ScannerBean> list) {

        List<AudioBean> audioList = new ArrayList<>();

        for (ScannerBean sb : list) {
            AudioBean item = new AudioBean();
            if (!TextUtils.isEmpty(sb.getAbsolutePath())) {
                item.setPath(sb.getAbsolutePath());
            } else {
                item.setName(sb.getFileNameWithOutSuffix());
            }
            Set title = new HashSet();
            Set artist = new HashSet();
            if (sb.getMetadata().getExtra() != null
                    && sb.getMetadata().getExtra().size() != 0) {
                for (Metadata extraMata : sb.getMetadata().getExtra()) {
                    if (!TextUtils.isEmpty(extraMata.getTitle())) {
                        title.add(extraMata.getTitle());
                    }
                    if (!TextUtils.isEmpty(extraMata.getArtist())) {
                        artist.add(extraMata.getArtist());
                    }
                }
            } else {
                if (!TextUtils.isEmpty(sb.getMetadata().getTitle())) {
                    title.add(sb.getMetadata().getTitle());
                }
                if (!TextUtils.isEmpty(sb.getMetadata().getArtist())) {
                    artist.add(sb.getMetadata().getArtist());
                }
            }
            item.setSong(title.iterator().hasNext()? title.iterator().next().toString():"");
            item.setSinger(artist.iterator().hasNext()? artist.iterator().next().toString():"");
            audioList.add(item);
        }

        return audioList;
    }

}

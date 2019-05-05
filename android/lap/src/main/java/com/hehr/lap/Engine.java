package com.hehr.lap;

import android.content.Context;

import com.hehr.lap.bean.AudioBean;
import com.hehr.lap.listener.ChainsListener;
import com.hehr.lap.listener.InitializeListener;
import com.hehr.lap.listener.QueryListener;
import com.hehr.lap.listener.ScanListener;
import com.hehr.lap.nodes.BaseNode;
import com.hehr.lap.nodes.Initialize;
import com.hehr.lap.nodes.OptDB;
import com.hehr.lap.nodes.ParseAudio;
import com.hehr.lap.nodes.QueryDB;
import com.hehr.lap.nodes.TaskFactory;
import com.hehr.lap.nodes.Tokenize;
import com.hehr.lap.nodes.Trans;
import com.hehr.lap.nodes.TraverseFolder;
import com.hehr.lap.nodes.UpdateDB;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hehr
 * 该版本为单线程模型，不支持多线程并发解析
 *
 */
public class Engine {

    private Engine(){}

    private static class ScannerHolder {
        private static final Engine INSTANCE = new Engine();
    }

    public static final Engine getInstance() {
        return ScannerHolder.INSTANCE;
    }

    private volatile Bundle bundle ;

    /**
     * 模块初始化
     * @param context
     * @param listener
     */
    public void initialize(Context context , InitializeListener listener){

        mInitializeListener = listener;

        //模块初始化，开辟一个新的bundle
        bundle = new Bundle();

        bundle.setInitialize(false);

        List<BaseNode> nodes = new ArrayList<>();
        //初始化数
        nodes.add(new Initialize.Builder()
                .setContext(context)
                .setTaskFactory(new TaskFactory())
                .build());

        Chains.getInstance().run(bundle, nodes, new InitListenerImpl());

    }

    private InitializeListener mInitializeListener;

    /**
     * 引起初始化监听器实现类
     *
     */
    private class InitListenerImpl implements ChainsListener{

        @Override
        public void onComplete(List<AudioBean> list) {
            if(mInitializeListener!=null){
                mInitializeListener.onInit();
            }
        }

        @Override
        public void onError(Error e) {
            if(mInitializeListener!=null){
                mInitializeListener.onError(e);
            }
        }
    }

    private ScanListener mScanListener;

    /**
     * 引擎扫描结果监听器实现类
     *
     */
    private class ScanListenerImpl implements ChainsListener{

        @Override
        public void onComplete(List<AudioBean> list) {
            if(mScanListener != null){
                mScanListener.onResult(list);
            }
        }

        @Override
        public void onError(Error e) {
            if(mScanListener!=null){
                mScanListener.onError(e);
            }
        }
    }

    /**
     * scan & parse files by path
     * @param folderPath
     * @param listener
     */
    public void scan(String folderPath , ScanListener listener){

        mScanListener = listener;

        //1、创建并按序组织责任链节点
        List<BaseNode> nodes = new ArrayList<>();
        //添加文件扫描节点
        nodes.add(new TraverseFolder.Builder()
                .setPath(folderPath)
                .build());
        //查询数据库
        nodes.add(new QueryDB.Builder().build());
        //添加音频解析节点
        nodes.add(new ParseAudio.Builder().build());
        //添加分词节点
        nodes.add(new Tokenize.Builder().build());
        //数据库更新节点
        nodes.add(new UpdateDB.Builder().build());

        //2、设置回调，并启动责任链
        Chains.getInstance().run(bundle , nodes , new ScanListenerImpl());

    }

    /**
     * scan & parse files by name
     * @param list
     * @param listener
     */
    public void scan(List<String> list ,ScanListener listener){

        mScanListener = listener;

        List<BaseNode> nodes = new ArrayList<>();

        //生成ScannerBean
        nodes.add( new Trans.Builder().setNamesList(list).build());

        //分词
        nodes.add( new Tokenize.Builder().build());

        //2、设置回调，并启动责任链
        Chains.getInstance().run(bundle , nodes , new ScanListenerImpl());

    }


    /**
     * 从数据库中获取已经解析过的全部媒体信息
     * @param isDesc 是否降序查询，默认true
     * @param limit 查询最大条目数限制,0<limit<1000
     *
     */

    public void optDB(int limit, boolean isDesc, QueryListener listener){

        mQueryListener = listener;

        limit = limit <= 0 ? 0 : limit>1000?1000: limit;

        List<BaseNode> nodes = new ArrayList<>();
        nodes.add(new OptDB.Builder()
                .setDesc(isDesc)
                .setLimit(limit)
                .build());

        Chains.getInstance().run(bundle , nodes , new QueryListenerImpl());

    }

    /**
     * 查询数据监听器
     *
     */
    private QueryListener mQueryListener;

    /**
     * 数据库查询监听器实现类
     *
     */
    private class QueryListenerImpl implements ChainsListener {

        @Override
        public void onComplete(List<AudioBean> list) {
            if(mQueryListener != null){
                mQueryListener.onResult(list);
            }
        }

        @Override
        public void onError(Error e) {
            if(mQueryListener != null){
                mQueryListener.onError(e);
            }
        }
    }


    /**
     * 设置扫描条目限制
     * @param number
     */

    public void setEntryNumber(int number){
        Conf.setEntryNumber(number);
    }


//    /**
//     * 设置缓存区音乐条目,
//     * 默认值 200 取值范围 0-500
//     * @param size
//     */
//
//    public void setCacheSize(int size){
//        Conf.setCacheSize(size<=0?0:size>=500?500:size);
//    }

    /**
     * 设置扫描过滤文件限制大小
     *
     */

    public void setAudioSize(int limit){
        Conf.setAudioSizeLimit(limit);
    }

    /**
     * 添加滤音乐文件类型
     */

    public void addAudioType(String type){
        Conf.AUDIO_TYPE.add(type);
    }

    /**
     * 模块销毁,
     * 在此方法中销毁线程池资源和数据库连接资源，
     * 此方法调用后必须重新调用 Initialize
     */
    public void release(){

        if(bundle != null){
            bundle.release();
            bundle = null;
        }

        mInitializeListener = null;

        mScanListener = null;
    }

    /**
     * 模块是否初始化完成
     * @return
     */

    public boolean isInitialized(){
        return bundle==null?false:bundle.isInitialize();
    }

}

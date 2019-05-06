package com.hehr.lap.bean;

abstract public class BaseBean<T extends BaseBean> {

    /**
     * 当前bean是否是空
     * @return
     */
    public abstract boolean isEmpty();

    /**
     * bean 和 当前bean是否相同
     * @param bean
     * @return
     */
    public abstract boolean isSameAudio( T bean);

}

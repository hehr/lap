package com.hehr.lap.task;



import com.hehr.lap.Bundle;

import java.util.concurrent.Callable;

/**
 *
 * @author hehr
 */
public abstract class BaseTask implements Callable<Bundle>{

    public Bundle bundle;

    public BaseTask(Bundle bundle){
        this.bundle = bundle;
    }


}

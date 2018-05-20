package com.dodles.gdx.dodleengine.util;

/**
 * Runnable-esque interface with a single generic parameter to the run method.
 */
public interface ParamRunnable<T> {
    
    /**
     * Runs the runnable.
     */
    void run(T parameter);
}

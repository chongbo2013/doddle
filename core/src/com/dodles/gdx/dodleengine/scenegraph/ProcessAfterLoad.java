package com.dodles.gdx.dodleengine.scenegraph;

import com.dodles.gdx.dodleengine.ObjectManager;

/**
 * Interface that denotes that the actor needs to do some processing after a dodle
 * it's included in has fully loaded.
 */
public interface ProcessAfterLoad {
    /**
     * Executed after dodle loading is finished.
     */
    void afterLoad(ObjectManager objectManager);
}

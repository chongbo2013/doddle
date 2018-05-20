package com.dodles.gdx.dodleengine.assets;

import com.badlogic.gdx.assets.AssetManager;

/**
 * Defines a dodle engine asset that can load itself into memory.
 */
public interface LoadableAsset {
    /**
     * Loads the asset into memory.
     */
    void load(AssetManager manager);
}

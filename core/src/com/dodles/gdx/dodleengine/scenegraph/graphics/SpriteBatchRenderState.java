package com.dodles.gdx.dodleengine.scenegraph.graphics;

import com.dodles.gdx.dodleengine.scenegraph.DodlesSpriteBatch;

/**
 * A marker class for denoting state to save in the sprite batch.
 */
public interface SpriteBatchRenderState {
    /**
     * Finishes the draw calls linked to the current state.
     */
    void finish(DodlesSpriteBatch batch);
}

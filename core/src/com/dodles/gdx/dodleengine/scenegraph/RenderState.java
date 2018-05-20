package com.dodles.gdx.dodleengine.scenegraph;

/**
 * A marker class for denoting state to save across calls in the rendering pipeline.
 */
public interface RenderState {
    /**
     * Called when the shape is being regenerated.
     */
    void onRegenerate();
}

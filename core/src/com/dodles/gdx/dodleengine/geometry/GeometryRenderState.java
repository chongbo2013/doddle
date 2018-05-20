package com.dodles.gdx.dodleengine.geometry;

import com.dodles.gdx.dodleengine.scenegraph.RenderState;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores rendering state for geometric shapes.
 */
public class GeometryRenderState implements RenderState {
    private final ArrayList<HandleHook> handleHooks = new ArrayList<HandleHook>();
    
    /**
     * Returns the handle hooks for the geometric shape.
     */
    public final List<HandleHook> getHandleHooks() {
        return handleHooks;
    }

    @Override
    public void onRegenerate() {
    }
}

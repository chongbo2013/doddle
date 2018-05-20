package com.dodles.gdx.dodleengine.input;

import com.dodles.gdx.dodleengine.scenegraph.RootGroup;

/**
 * Data to send to ZoomInputHandlers.
 */
public class ZoomInteractionData extends InteractionData {
    private final float scale;
    
    public ZoomInteractionData(RootGroup group, float x, float y, float scale) {
        super(group, x, y);

        this.scale = scale;
    }
    
    /**
     * Returns the current scale.
     */
    public final float getScale() {
        return scale;
    }    
}

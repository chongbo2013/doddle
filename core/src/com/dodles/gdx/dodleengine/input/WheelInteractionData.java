package com.dodles.gdx.dodleengine.input;

import com.dodles.gdx.dodleengine.scenegraph.RootGroup;

/**
 * Data to send to WheelInputHandlers.
 */
public class WheelInteractionData extends InteractionData {
    private static final float NEGATIVE_ZOOM = 0.75f;
    private static final float POSITIVE_ZOOM = 1.25f;
    private float scale;
    
    public WheelInteractionData(RootGroup group, float x, float y, int scrollAmount) {
        super(group, x, y);
        
        scale = POSITIVE_ZOOM;
        
        if (scrollAmount > 0) {
            scale = NEGATIVE_ZOOM;
        }
    }
    
    /**
     * Gets the change in scale to apply.
     */
    public final float getScale() {
        return scale;
    }
}

package com.dodles.gdx.dodleengine.input;

import com.dodles.gdx.dodleengine.scenegraph.RootGroup;

/**
 * Data to send to RotationInputHandlers.
 */
public class RotationInteractionData extends InteractionData {
    private final float rotation;

    public RotationInteractionData(RootGroup group, float x, float y, float rotation) {
        super(group, x, y);

        this.rotation = rotation;
    }
    
    /**
     * Returns the rotation.
     */
    public final float getRotation() {
        return rotation;
    }
}

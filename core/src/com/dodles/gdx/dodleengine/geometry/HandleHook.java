package com.dodles.gdx.dodleengine.geometry;

import com.badlogic.gdx.math.Vector2;

/**
 * A handle hook is a control point for a geometric shape, and a place to drag and stretch the shape.
 */
public interface HandleHook {    
    /**
     * Returns the position of the handle hook.
     */
    Vector2 getPosition();

    /**
     * Sets the position of the handle hook to the given point.
     */
    void setPosition(Vector2 point);
}

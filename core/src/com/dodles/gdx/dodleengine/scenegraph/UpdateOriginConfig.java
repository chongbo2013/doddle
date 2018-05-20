package com.dodles.gdx.dodleengine.scenegraph;

/**
 * Interface for denoting that this configuration should update the shape's origin point.
 */
public interface UpdateOriginConfig extends CustomToolConfig {
    /**
     * Updates the origin of the shape.
     */
    void updateOrigin(Shape shape);
}

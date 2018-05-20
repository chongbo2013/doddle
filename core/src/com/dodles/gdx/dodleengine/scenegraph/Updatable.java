package com.dodles.gdx.dodleengine.scenegraph;

/**
 * Interface to standardize the update methods for updatable objects.
 */

public interface Updatable {

    /**
     * Get the stroke config.
     */
    StrokeConfig getStrokeConfig();

    /**
     * Set the stroke config.
     */
    void setStrokeConfig(StrokeConfig strokeConfig);

    /**
     * Regenerate the object.
     */
    void regenerate();

    /**
     * Update the origin.
     */
    void updateOrigin();

}

package com.dodles.gdx.dodleengine.geometry;

import com.dodles.gdx.dodleengine.scenegraph.Shape;
import com.dodles.gdx.dodleengine.scenegraph.StrokeConfig;

/**
 * Defines logic for drawing geometric shapes.
 */
public interface Geometry extends Comparable<Geometry> {
    /**
     * Returns the name of the brush.
     */
    String getName();

    /**
     * Returns the name of the icon associated with the Geometry
     */
    String getIconName();

    /**
     * Returns the order of the brush to show in the UI.
     */
    int getOrder();
    
    /**
     * Returns the default stroke config for the geometry.
     */
    StrokeConfig getDefaultStrokeConfig();

    /**
     * Returns the default geometry configuration for the geometry.
     */
    GeometryConfig getDefaultGeometryConfig();
    
    /**
     * Initializes the geometry, attaching it to the given shape.
     */
    void init(Shape shape);
}

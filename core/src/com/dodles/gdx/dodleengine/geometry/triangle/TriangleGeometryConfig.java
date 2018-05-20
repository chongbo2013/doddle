package com.dodles.gdx.dodleengine.geometry.triangle;

import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.geometry.AbstractPolygonGeometryConfig;

/**
 * Configuration for triangle geometry.
 */
public class TriangleGeometryConfig extends AbstractPolygonGeometryConfig {
    public TriangleGeometryConfig() {
        super(150, 3, 1);
        setType(TriangleGeometry.GEOMETRY_NAME);
    }
    
    public TriangleGeometryConfig(JsonValue json) {
        super(json);
    }
}

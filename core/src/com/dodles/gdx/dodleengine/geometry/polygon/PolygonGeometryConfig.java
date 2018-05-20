package com.dodles.gdx.dodleengine.geometry.polygon;

import com.dodles.gdx.dodleengine.geometry.AbstractPolygonGeometryConfig;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Configuration for polygonal geometry.
 */
public class PolygonGeometryConfig extends AbstractPolygonGeometryConfig {
    
    public PolygonGeometryConfig() {
        super(150, 5, 1);
        setType(PolygonGeometry.GEOMETRY_NAME);
    }

    public void updateNumPoints(float value) {
        int intValue = (int) value;
        setNumPoints(intValue);
    }

    public PolygonGeometryConfig(JsonValue json) {
        super(json);
    }
}

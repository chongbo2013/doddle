package com.dodles.gdx.dodleengine.geometry.heart;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.geometry.GeometryConfig;
import java.util.HashMap;

/**
 * Configuration for heart geometry.
 */
public class HeartGeometryConfig extends GeometryConfig {
    private final HashMap<Axis, Float> axisRatios = new HashMap<Axis, Float>();
    
    public HeartGeometryConfig() {        
        setSize(150);
        setAxisRatio(Axis.X, 1);
        setAxisRatio(Axis.Y, 1);
        setType(HeartGeometry.GEOMETRY_NAME);
    }
    
    public HeartGeometryConfig(JsonValue json) {
        super(json);
        
        setAxisRatio(Axis.X, json.getFloat("xRatio"));
        setAxisRatio(Axis.Y, json.getFloat("yRatio"));
    }
    
    /**
     * Returns the ratio for the given axis.
     */
    public final float getAxisRatio(Axis ratio) {
        return axisRatios.get(ratio);
    }
    
    /**
     * Sets the ratio for the given axis.
     */
    public final void setAxisRatio(Axis ratio, float value) {
        axisRatios.put(ratio, value);
    }
    
    /**
     * Writes the geometry configuration to the json document.
     */
    @Override
    public final void onWriteConfig(Json json) {        
        json.writeValue("xRatio", getAxisRatio(Axis.X));
        json.writeValue("yRatio", getAxisRatio(Axis.Y));
    }
    
    /**
     * Defines the rectangle axis.
     */
    public enum Axis {
        X,
        Y
    }
}

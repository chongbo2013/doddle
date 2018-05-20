package com.dodles.gdx.dodleengine.geometry.circle;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.geometry.GeometryConfig;
import java.util.HashMap;

/**
 * Configuration for circle geometry.
 */
public class CircleGeometryConfig extends GeometryConfig {
    private final HashMap<Axis, Float> axisRatios = new HashMap<Axis, Float>();
    
    public CircleGeometryConfig() {        
        setSize(150);
        setAxisRatio(Axis.XPOS, 1);
        setAxisRatio(Axis.XNEG, 1);
        setAxisRatio(Axis.YPOS, 1);
        setAxisRatio(Axis.YNEG, 1);
        setType(CircleGeometry.GEOMETRY_NAME);

    }
    
    public CircleGeometryConfig(JsonValue json) {
        super(json);
        
        setAxisRatio(Axis.XPOS, json.getFloat("xPosRatio"));
        setAxisRatio(Axis.XNEG, json.getFloat("xNegRatio"));
        setAxisRatio(Axis.YPOS, json.getFloat("yPosRatio"));
        setAxisRatio(Axis.YNEG, json.getFloat("yNegRatio"));
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
        json.writeValue("xPosRatio", getAxisRatio(Axis.XPOS));
        json.writeValue("xNegRatio", getAxisRatio(Axis.XNEG));
        json.writeValue("yPosRatio", getAxisRatio(Axis.YPOS));
        json.writeValue("yNegRatio", getAxisRatio(Axis.YNEG));
    }
    
    /**
     * Defines the rectangle axis.
     */
    public enum Axis {
        XPOS,
        XNEG,
        YPOS,
        YNEG
    }
}

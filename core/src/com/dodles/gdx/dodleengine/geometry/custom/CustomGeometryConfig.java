package com.dodles.gdx.dodleengine.geometry.custom;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.geometry.GeometryConfig;
import com.dodles.gdx.dodleengine.util.JsonUtility;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomGeometryConfig extends GeometryConfig {
    private final HashMap<Axis, Float> axisRatios = new HashMap<Axis, Float>();
    private ArrayList<Vector2> points;

    public CustomGeometryConfig() {
        setSize(150);
        setAxisRatio(Axis.XPOS, 1);
        setAxisRatio(Axis.XNEG, 1);
        setAxisRatio(Axis.YPOS, 1);
        setAxisRatio(Axis.YNEG, 1);
        setType(CustomGeometry.GEOMETRY_NAME);

    }

    public CustomGeometryConfig(ArrayList<Vector2> pointsarray) {
        setSize(150);
        setAxisRatio(Axis.XPOS, 1);
        setAxisRatio(Axis.XNEG, 1);
        setAxisRatio(Axis.YPOS, 1);
        setAxisRatio(Axis.YNEG, 1);
        points = pointsarray;
        setType(CustomGeometry.GEOMETRY_NAME);
    }

    public CustomGeometryConfig(JsonValue json) {
        super(json);
        points = JsonUtility.readVectorArray(json.get("points"));
    }
    
    /**
     * Returns the ratio for the given axis.
     */
    public final ArrayList<Vector2> getPoints() {

        return points;
    }

    public final void setPointsArray(ArrayList<Vector2> newpoints) {points = newpoints;}

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
        JsonUtility.writeVectorArray(points, json, "points");
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

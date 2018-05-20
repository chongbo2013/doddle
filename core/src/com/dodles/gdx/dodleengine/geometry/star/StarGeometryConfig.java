package com.dodles.gdx.dodleengine.geometry.star;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.geometry.AbstractPolygonGeometryConfig;

/**
 * Configuration for star geometry.
 */
public class StarGeometryConfig extends AbstractPolygonGeometryConfig {
    private static final float DEPTH_ADJUST_RATIO = 1.35f;
    private int depth;
    
    public StarGeometryConfig() {
        super(150, 10, 0);
        depth = 50;
        setType(StarGeometry.GEOMETRY_NAME);
    }
    
    public StarGeometryConfig(JsonValue json) {
        super(json);
        depth = json.getInt("depth");
    }
    
    @Override
    /**
     * specialized wrtieConfig
     */
    public final void onWriteConfig(Json json) {        
        json.writeValue("cornerRadius", getCornerRadius());
        json.writeValue("numPoints", getNumPoints());
        json.writeValue("depth", depth);
        json.writeArrayStart("scales");
        for (int i = 0; i < getNumPoints(); i++) {
            json.writeValue(getScales()[i]);
        }
        json.writeArrayEnd();
        json.writeArrayStart("angles");
        for (int i = 0; i < getNumPoints(); i++) {
            json.writeValue(getAngles()[i]);
        }
        json.writeArrayEnd();
    }

    /**
     * need to override behavior for setting points as there is the 'depths' array.
     */
    public final void setNumPoints(int numPoints) {
        this.numPoints = numPoints;

        setScales(new float[numPoints]);
        setAngles(new float[numPoints]);
        setCorners(new Vector2[numPoints]);
    }

    /**
     * get the depth -- distance "in" towards the center of the star shape.
     * @return
     */
    public final int getDepth() {
        return Math.round(depth * DEPTH_ADJUST_RATIO);
    }

    /**
     * set the depth -- change the "in" distance.
     * @param depth
     */
    public final void setDepth(int depth) {
        this.depth = depth;
    }
}

package com.dodles.gdx.dodleengine.geometry;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Configuration for rectangular geometry.
 */
public abstract class AbstractPolygonGeometryConfig extends GeometryConfig {
    private float cornerRadius;
    private float[] scales;
    private float[] angles;
    private Vector2[] corners;
    
    // CHECKSTYLE.OFF: VisibilityModifier - need to alter this in child classes
    protected int numPoints;
    // CHECKSTYLE.ON: VisibilityModifier
    
    public AbstractPolygonGeometryConfig() {
        this.scales = new float[numPoints];
        this.angles = new float[numPoints];
        this.corners = new Vector2[numPoints];
    }
    
    public AbstractPolygonGeometryConfig(int size, int numPoints, float cornerRadius) {
        setSize(size);
        setNumPoints(numPoints);
        setCornerRadius(cornerRadius);
        
        this.scales = new float[numPoints];
        this.angles = new float[numPoints];
        this.corners = new Vector2[numPoints];
    }
    
    public AbstractPolygonGeometryConfig(JsonValue json) {
        super(json);
        setCornerRadius(json.getFloat("cornerRadius"));
        setNumPoints(json.getInt("numPoints"));
        scales = json.get("scales").asFloatArray();
        angles = json.get("angles").asFloatArray();
        corners = new Vector2[numPoints];
    }
    
    /**
     * Returns the corner radius.
     */
    public final float getCornerRadius() {
        return cornerRadius;
    }
    
    /**
     * Sets the corner radius.
     */
    public final void setCornerRadius(float newCornerRadius) {
        cornerRadius = newCornerRadius;
    }
    
    /**
     * Writes the geometry configuration to the json document.
     */
    //CHECKSTYLE.OFF: DesignForExtension - we really want a base implementation that can be overridden.
    @Override
    public void onWriteConfig(Json json) {
    //CHECKSTYLE.ON
        json.writeValue("cornerRadius", cornerRadius);
        json.writeValue("numPoints", numPoints);
        json.writeArrayStart("scales");
        for (int i = 0; i < numPoints; i++) {
            json.writeValue(scales[i]);
        }
        json.writeArrayEnd();
        json.writeArrayStart("angles");
        for (int i = 0; i < numPoints; i++) {
            json.writeValue(angles[i]);
        }
        json.writeArrayEnd();
    }

    /**
     * gets nbr of Points in the polygon.
     * @return
     */
    public final int getNumPoints() {
        return numPoints;
    }

    /**
     * sets the number of points.
     * @param numPoints
     */
    public void setNumPoints(int numPoints) {
        this.numPoints = numPoints;
        
        this.scales = new float[numPoints];
        this.angles = new float[numPoints];
        this.corners = new Vector2[numPoints];
    }

    /**
     * gets the scales array -- used by handlehook drawing.
     * @return
     */
    public final float[] getScales() {
        return scales;
    }

    /**
     * sets the scales array.
     * @param scales
     */
    public final void setScales(float[] scales) {
        this.scales = scales;
    }

    /**
     * gets the angles array list.
     * @return
     */
    public final float[] getAngles() {
        return angles;
    }

    /**
     * sets the angles array.
     * @param angles
     */
    public final void setAngles(float[] angles) {
        this.angles = angles;
    }

    /**
     * gets list of corner vertices.
     * @return
     */
    public final Vector2[] getCorners() {
        return corners;
    }

    /**
     * sets the list of corner vertices.
     * @param corners
     */
    public final void setCorners(Vector2[] corners) {
        this.corners = corners;
    }
}

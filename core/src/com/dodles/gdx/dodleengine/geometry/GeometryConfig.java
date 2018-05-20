package com.dodles.gdx.dodleengine.geometry;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.dodles.gdx.dodleengine.geometry.circle.CircleGeometry;
import com.dodles.gdx.dodleengine.geometry.circle.CircleGeometryConfig;
import com.dodles.gdx.dodleengine.geometry.custom.CustomGeometry;
import com.dodles.gdx.dodleengine.geometry.custom.CustomGeometryConfig;
import com.dodles.gdx.dodleengine.geometry.heart.HeartGeometry;
import com.dodles.gdx.dodleengine.geometry.heart.HeartGeometryConfig;
import com.dodles.gdx.dodleengine.geometry.polygon.PolygonGeometry;
import com.dodles.gdx.dodleengine.geometry.polygon.PolygonGeometryConfig;
import com.dodles.gdx.dodleengine.geometry.rectangle.RectangleGeometry;
import com.dodles.gdx.dodleengine.geometry.rectangle.RectangleGeometryConfig;
import com.dodles.gdx.dodleengine.geometry.star.StarGeometry;
import com.dodles.gdx.dodleengine.geometry.star.StarGeometryConfig;
import com.dodles.gdx.dodleengine.geometry.triangle.TriangleGeometry;
import com.dodles.gdx.dodleengine.geometry.triangle.TriangleGeometryConfig;
import com.dodles.gdx.dodleengine.scenegraph.CustomToolConfig;
import com.dodles.gdx.dodleengine.scenegraph.Shape;
import com.dodles.gdx.dodleengine.scenegraph.UpdateOriginConfig;
import com.dodles.gdx.dodleengine.util.JsonUtility;
import java.io.StringWriter;
import java.util.ArrayList;

/**
 * Geometry configuration options.
 */
public abstract class GeometryConfig implements CustomToolConfig, UpdateOriginConfig {
    private String type;
    private Vector2 point;
    private float size = 100;
    private float rotation;

    /**
     * Creates a new geometry configuration from the given JSON.
     */
    public static GeometryConfig create(JsonValue json) {
        String jsonType = json.getString("type");

        if (jsonType.equals(RectangleGeometry.GEOMETRY_NAME)) {
            return new RectangleGeometryConfig(json);
        } else if (jsonType.equals(CircleGeometry.GEOMETRY_NAME)) {
            return new CircleGeometryConfig(json);
        } else if (jsonType.equals(PolygonGeometry.GEOMETRY_NAME)) {
            return new PolygonGeometryConfig(json);
        } else if (jsonType.equals(TriangleGeometry.GEOMETRY_NAME)) {
            return new TriangleGeometryConfig(json);
        } else if (jsonType.equals(HeartGeometry.GEOMETRY_NAME)) {
            return new HeartGeometryConfig(json);
        } else if (jsonType.equals(StarGeometry.GEOMETRY_NAME)) {
            return new StarGeometryConfig(json);
        } else if (jsonType.equals(CustomGeometry.GEOMETRY_NAME)) {
            return new CustomGeometryConfig(json);
        }
        return null;
    }

    protected GeometryConfig() {
    }

    protected GeometryConfig(JsonValue json) {
        size = json.getInt("size");
        point = JsonUtility.readVector(json.get("point"));
        type = json.getString("type");
        rotation = json.getFloat("rotation");
    }

    /**
     * Returns the type of geometry configuration.
     */
    public final String getType() {
        return type;
    };

    /**
     * set the Geometry type.
     * @param t
     */
    public final void setType(String t) {
        this.type = t;
    }

    /**
     * Returns the point to draw the shape at.
     */
    public final Vector2 getPoint() {
        return point;
    }

    /**
     * Sets the point to draw the geometry at.
     */
    public final void setPoint(Vector2 newPoint) {
        point = newPoint;
    }

    /**
     * Returns the size of the geometry.
     */
    public final float getSize() {
        return size;
    }

    /**
     * Sets the size of the geometry.
     */
    public final void setSize(float newSize) {
        size = newSize;
    }

    /**
     * Returns the rotation of the geometry.
     */
    public final float getRotation() {
        return rotation;
    }

    /**
     * Returns the rotation of the geometry in radians.
     */
    public final float getRotationRadians() {
        return (float) (rotation * (Math.PI / 180));
    }

    /**
     * Sets the rotation of the geometry.
     */
    public final void setRotation(float newRotation) {
        rotation = newRotation;
    }

    /**
     * Writes the geometry configuration to the json document.
     */
    public final void writeConfig(Json json) {
        onWriteConfig(json);

        json.writeValue("type", getType());
        json.writeValue("size", size);
        json.writeValue("point", point);
        json.writeValue("rotation", rotation);
    }

    /**
     * Allows extending classes to write configuration information.
     */
    protected abstract void onWriteConfig(Json json);

    /**
     * Creates a copy of the geometry config.
     */
    public final GeometryConfig cpy() {
        StringWriter writer = new StringWriter();
        Json json = new Json(OutputType.json);
        json.setWriter(writer);
        json.setQuoteLongValues(true);
        json.writeObjectStart();
        this.writeConfig(json);
        json.writeObjectEnd();

        return create(new JsonReader().parse(writer.toString()));
    }

    @Override
    public final void updateOrigin(Shape shape) {
        shape.setOriginX(point.x);
        shape.setOriginY(point.y);
    }
}

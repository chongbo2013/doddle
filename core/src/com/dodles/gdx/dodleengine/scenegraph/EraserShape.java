package com.dodles.gdx.dodleengine.scenegraph;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.IdDatabase;
import com.dodles.gdx.dodleengine.scenegraph.graphics.FrameBufferAtlasManager;
import com.dodles.gdx.dodleengine.util.JsonUtility;

/**
 * We need to store eraser offsets separate from the shape itself because the offset
 * can be different for each shape the eraser is attached to.
 */
public class EraserShape {
    private Shape shape;
    private Vector2 offset;
    private float rotation;
    private float scale;
    
    public EraserShape(Shape shape, Vector2 offset, float rotation, float scale) {
        this.shape = shape;
        this.offset = offset;
        this.rotation = rotation;
        this.scale = scale;
    }
    
    public EraserShape(JsonValue json, IdDatabase idDB, DodlesActorFactory actorFactory, FrameBufferAtlasManager atlasManager) {
        shape = (Shape) actorFactory.createFromJson(idDB, json.get("shape"));
        offset = JsonUtility.readVector(json);
        rotation = json.getFloat("rotation");
        scale = json.getFloat("scale");
    }
    
    /**
     * Returns the shape.
     */
    public final Shape getShape() {
        return shape;
    }
    
    /**
     * Returns the translation offset.
     */
    public final Vector2 getOffset() {
        return offset;
    }
    
    /**
     * Returns the rotation offset.
     */
    public final float getRotation() {
        return rotation;
    }
    
    /**
     * Returns the scale offset.
     */
    public final float getScale() {
        return scale;
    }
    
    /**
     * Writes the JSON configuration.
     */
    public final void writeConfig(Json json) {
        json.writeObjectStart();
        shape.writeConfig(json, "shape");
        JsonUtility.writeVector(offset, json);
        json.writeValue("rotation", rotation);
        json.writeValue("scale", scale);
        json.writeObjectEnd();
    }
}

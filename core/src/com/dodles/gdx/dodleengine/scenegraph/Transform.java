package com.dodles.gdx.dodleengine.scenegraph;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Describes the current transform relative to the origin of a given scene actor.
 */
public class Transform {
    private Float x = 0f;
    private Float y = 0f;
    private Float scaleX = 1f;
    private Float scaleY = 1f;
    private Float rotation = 0f;

    public Transform() {
        this(false);
    }
    
    public Transform(boolean initNull) {
        if (initNull) {
            x = null;
            y = null;
            scaleX = null;
            scaleY = null;
            rotation = null;
        }
    }
    
    public Transform(DodlesActor actor) {
        x = actor.getX();
        y = actor.getY();
        scaleX = actor.getScaleX();
        scaleY = actor.getScaleY();
        rotation = actor.getRotation();
    }
    
    public Transform(JsonValue json) {
        x = json.getFloat("x");
        y = json.getFloat("y");
        scaleX = json.getFloat("scaleX");
        scaleY = json.getFloat("scaleY");
        rotation = json.getFloat("rotation");
    }
    
    /**
     * Returns the x coordinate of the actor.
     */
    public final Float getX() {
        return x;
    }

    /**
     * Sets the x coordinate of the actor.
     */
    public final void setX(float x) {
        this.x = x;
    }

    /**
     * Returns the y coordinate of the actor.
     */
    public final Float getY() {
        return y;
    }

    /**
     * Sets the y coordinate of the actor.
     */
    public final void setY(float y) {
        this.y = y;
    }

    /**
     * Returns the x-axis scaling of the actor.
     */
    public final Float getScaleX() {
        return scaleX;
    }

    /**
     * Sets the x-axis scaling of the actor.
     */
    public final void setScaleX(float scaleX) {
        this.scaleX = scaleX;
    }

    /**
     * Returns the y-axis scaling of the actor.
     */
    public final Float getScaleY() {
        return scaleY;
    }

    /**
     * Sets the y-axis scaling of the actor.
     */
    public final void setScaleY(float scaleY) {
        this.scaleY = scaleY;
    }

    /**
     * Returns the rotation of the actor.
     */
    public final Float getRotation() {
        return rotation;
    }

    /**
     * Sets the rotation of the actor.
     */
    public final void setRotation(float rotation) {
        this.rotation = rotation;
    }
    
    /**
     * Writes the transform to the json document.
     */
    public final void writeConfig(Json json) {
        json.writeValue("x", x);
        json.writeValue("y", y);
        json.writeValue("scaleX", scaleX);
        json.writeValue("scaleY", scaleY);
        json.writeValue("rotation", rotation);
    }
    
    /**
     * Creates a copy of the transform.
     */
    public final Transform cpy() {
        Transform result = new Transform();
        result.x = x;
        result.y = y;
        result.scaleX = scaleX;
        result.scaleY = scaleY;
        result.rotation = rotation;
        return result;
    }
}

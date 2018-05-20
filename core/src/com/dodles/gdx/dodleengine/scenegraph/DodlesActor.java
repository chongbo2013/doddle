package com.dodles.gdx.dodleengine.scenegraph;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Json;
import com.dodles.gdx.dodleengine.IdDatabase;
import com.dodles.gdx.dodleengine.ObjectManager;


/**
 * Common functionality for our Dodle actors.
 */
public interface DodlesActor {    
    /**
     * Returns the name (ID) of the actor.
     */
    String getName();
    
    /**
     * Returns the tracking ID of the actor.
     */
    String getTrackingID();
    
    /**
     * Sets the tracking ID of the actor.
     */
    void setTrackingID(String trackingID);
    
    /**
     * Gets the original ID of the actor (if this isn't it's source dodle).
     */
    String getOriginalID();
    
    /**
     * Sets the original ID of the actor.
     */
    void setOriginalID(String originalID);
    
    /**
     * Returns the type of the actor.
     */
    String getType();
    
    /**
     * Returns the parent DodlesViewGroup (will skip views).
     */
    BaseDodlesViewGroup getParentDodlesViewGroup();
    
    /**
     * Returns the immediate view this actor belongs to.
     */
    String getParentViewID();
    
    /**
     * Returns the "Draw Bounds" of the actor - a rectangle containing all
     * pixels drawn by the actor.
     */
    Rectangle getDrawBounds();
    
    /**
     * Returns true if the actor is visible.
     */
    boolean isVisible();
    
    /**
     * Updates the base transform of the actor.
     */
    void updateBaseTransform(Transform transform);
    
    /**
     * Returns the base transform of the actor.
     */
    Transform getBaseTransform();
    
    /**
     * Resets the actor to it's base transform.
     */
    void resetToBaseTransform();
        
    /**
     * Returns the X position of the actor.
     */
    float getX();
    
    /**
     * Sets the x position of the actor.
     */
    void setX(float x);
    
    /**
     * Returns the Y position of the actor.
     */
    float getY();
    
    /**
     * Sets the Y position of the actor.
     */
    void setY(float y);
    
    /**
     * Returns the X-component of the scale of the actor.
     */
    float getScaleX();
    
    /**
     * Sets the X-component of the scale of the actor.
     */
    void setScaleX(float scaleX);
    
    /**
     * Returns the Y-component of the scale of the actor.
     */
    float getScaleY();
    
    /**
     * Sets the Y-component of the scale of the actor.
     */
    void setScaleY(float scaleY);
    
    /**
     * Returns the rotation of the actor.
     */
    float getRotation();
    
    /**
     * Sets the rotation of the actor.
     */
    void setRotation(float rotation);
    
    /**
     * Sets whether or not the actor should be drawn. 
     */
    void setVisible(boolean isVisible);
    
    /**
     * Returns the deepest dodles actor that passes a hit test.
     */
    Actor hit(float x, float y, boolean touchable);


    /**
     * Return a clone of this shape.
     */
    DodlesActor dodleClone(IdDatabase iddb, ObjectManager objectManager);

    /**
     * Set the actor name.
     */
    void setName(String name);
    
    /**
     * Draws the actor to the scene.
     */
    void draw(Batch batch, float parentAlpha);
    
    /**
     * Returns the origin of the actor.
     */
    Vector2 getOrigin();
    
    /**
     * Updates the origin based on the DrawBounds of the actor.
     */
    void updateOrigin();
    
    /**
     * Serializes the actor to JSON.
     */
    void writeConfig(Json json);
}

package com.dodles.gdx.dodleengine.scenegraph;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.IdDatabase;
import com.dodles.gdx.dodleengine.ObjectManager;

/**
 * Since we can't have a consistent inheritance structure because Group inherits
 * from Actor, these are mixins that our objects can use to keep code DRYer.
 */
public final class ActorMixins {
    private ActorMixins() {
    }
    
    /**
     * Exports common actor information.
     */
    public static void exportToJson(DodlesActor actor, Json json) {
        json.writeValue("id", actor.getName());
        json.writeValue("trackingID", actor.getTrackingID());
        json.writeValue("type", actor.getType());
        actor.getBaseTransform().writeConfig(json);
        json.writeValue("originX", actor.getOrigin().x);
        json.writeValue("originY", actor.getOrigin().y);
        
        if (actor.getOriginalID() != null) {
            json.writeValue("originalID", actor.getOriginalID());
        }
        
        // Not necessary if we're doing nested JSON
        //json.writeValue("parentGroupID", actor.getParentDodlesViewGroup().getName());
        //json.writeValue("parentPhaseID", actor.getParentViewID());
    }
    
    /**
     * Imports common actor information.
     */
    public static void importFromJson(DodlesActor actor, IdDatabase idDB, JsonValue json) {
        String actorID = json.getString("id");
        String originalID = json.getString("originalID", null);
        
        if (idDB != null) {
            if (originalID == null) {
                originalID = actorID;
            }
            
            actorID = idDB.getNewID(actorID);
        }
        
        actor.setOriginalID(originalID);
        actor.setName(actorID);
        actor.setTrackingID(json.getString("trackingID", null));
        actor.updateBaseTransform(new Transform(json));
        ((Actor) actor).setOrigin(json.getFloat("originX"), json.getFloat("originY"));
    }
    
    /**
     * Common operations to perform on clone.
     */
    public static void commonClone(DodlesActor source, ObjectManager objectManager, DodlesActor target) {
        target.setOriginalID(source.getOriginalID());
        target.updateBaseTransform(source.getBaseTransform().cpy());
        ((Actor) target).setOrigin(((Actor) source).getOriginX(), ((Actor) source).getOriginY());
        
        if (!(target instanceof Scene)) {
            objectManager.addActor(target);
        }
    }
    
    /**
     * Updates the origin based on the DrawBounds of the actor.
     */
    public static void updateOrigin(Object actor) {
        Actor gdxActor = (Actor) actor;
        DodlesActor dodlesActor = (DodlesActor) actor;

        if (dodlesActor instanceof BaseDodlesViewGroup) {
            BaseDodlesViewGroup group = (BaseDodlesViewGroup) dodlesActor;

            // Update all phases
            for (Object view : group.getViews()) {
                updateOrigin((BaseDodlesGroup) view);
            }
        }
        
        if (dodlesActor instanceof BaseGroup && ((BaseGroup) dodlesActor).getChildren().size == 1) {
            Actor child = ((BaseGroup) dodlesActor).getChildren().get(0);
            Vector2 origin = ((DodlesActor) child).getOrigin();
            child.localToParentCoordinates(origin);
            gdxActor.setOriginX(origin.x);
            gdxActor.setOriginY(origin.y);
        } else {
            Rectangle bounds = dodlesActor.getDrawBounds();
        
            if (bounds != null) {
                gdxActor.setOriginX(bounds.x + bounds.width / 2);
                gdxActor.setOriginY(bounds.y + bounds.height / 2);
            }
        }
    }
    
    /**
     * Updates the transform properties on the actor from the given Transform.
     */
    public static void updateTransformProperties(Actor actor, Transform transform) {
        actor.setX(transform.getX());
        actor.setY(transform.getY());
        actor.setScaleX(transform.getScaleX());
        actor.setScaleY(transform.getScaleY());
        actor.setRotation(transform.getRotation());
    }
    
    /**
     * Sets the batch transform matrix for the actor according to it's x, y, rotation and scaling.
     */
    public static Matrix4 setBatchTransformMatrix(Batch batch, Object actor) {
        Actor gdxActor = (Actor) actor;
        Matrix4 originalTransform = batch.getTransformMatrix().cpy();
        
        Matrix4 shapeTransform = new Matrix4();
        shapeTransform.set(getActorTransformMatrix(actor));
        batch.setTransformMatrix(batch.getTransformMatrix().mul(shapeTransform));
        
        return originalTransform;
    }
    
    /**
     * Gets the Affine transform matrix for the given actor.
     */
    public static Affine2 getActorTransformMatrix(Object actor) {
        Actor gdxActor = (Actor) actor;
        
        Affine2 actorTransform = new Affine2();
        actorTransform.translate(gdxActor.getX() + gdxActor.getOriginX(), gdxActor.getY() + gdxActor.getOriginY());
        actorTransform.scale(gdxActor.getScaleX(), gdxActor.getScaleY());
        actorTransform.rotate(gdxActor.getRotation());
        actorTransform.translate(-gdxActor.getOriginX(), -gdxActor.getOriginY());
        return actorTransform;
    }
}

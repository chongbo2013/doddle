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
 * An actor that will redraw another actor at a different position.
 */
public class LinkActor extends Actor implements DodlesActor {
    public static final String ACTOR_TYPE = "LinkActor";
    
    private final DodlesActor linkedActor;
    
    private String trackingID;
    private String originalID;
    private Transform baseTransform = new Transform();
    
    public LinkActor(String id, String trackingID, DodlesActor linkedActor) {
        super();
        
        setName(id);
        setTrackingID(trackingID);
        this.linkedActor = linkedActor;
    }
    
    public LinkActor(JsonValue json, IdDatabase idDB, ObjectManager objectManager) {
        super();
        
        ActorMixins.importFromJson(this, idDB, json);
        this.linkedActor = objectManager.getActor(json.getString("linkedActorID"));
    }
    
    @Override
    public final String getType() {
        return ACTOR_TYPE;
    }
    
    @Override
    public final String getTrackingID() {
        return trackingID;
    }
    
    @Override
    public final void setTrackingID(String pTrackingID) {
        trackingID = pTrackingID;
    }
    
    @Override
    public final String getOriginalID() {
        return originalID;
    }
    
    @Override
    public final void setOriginalID(String pOriginalID) {
        originalID = pOriginalID;
    }

    @Override
    public final void updateOrigin() {
        // This shouldn't be necessary, right?
    }
    
    @Override
    public final Vector2 getOrigin() {
        return new Vector2(getOriginX(), getOriginY());
    }

    @Override
    public final Rectangle getDrawBounds() {
        return linkedActor.getDrawBounds();
    }

    @Override
    public final void updateBaseTransform(Transform transform) {
        baseTransform = transform;
        ActorMixins.updateTransformProperties(this, transform);
    }

    @Override
    public final Transform getBaseTransform() {
        return baseTransform;
    }

    @Override
    public final void resetToBaseTransform() {
        updateBaseTransform(baseTransform);
    }
    
    @Override
    public final void draw(Batch batch, float parentAlpha) {
        Matrix4 originalTransform = batch.getTransformMatrix().cpy();
        
        if (linkedActor instanceof BaseDodlesGroup) {
            BaseDodlesGroup dg = (BaseDodlesGroup) linkedActor;
            Affine2 currentWorld = new Affine2();
            currentWorld.set(originalTransform);
            dg.setForceTransform(true);
            dg.setWorldTransformOverride(currentWorld.mul(ActorMixins.getActorTransformMatrix(dg).mul(ActorMixins.getActorTransformMatrix(this))));
        }
        
        linkedActor.draw(batch, parentAlpha);
        batch.setTransformMatrix(originalTransform);
    }
    
    /**
     * Returns the linked actor.
     */
    public final DodlesActor getLinkedActor() {
        return linkedActor;
    }
    
    @Override
    public final BaseDodlesViewGroup getParentDodlesViewGroup() {
        return CommonActorOperations.getParentDodlesViewGroup(this);
    }
    
    @Override
    public final String getParentViewID() {
        return CommonActorOperations.getParentView(this).getName();
    }
    
    @Override
    public final DodlesActor dodleClone(IdDatabase iddb, ObjectManager objectManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public final void writeConfig(Json json) {
        json.writeObjectStart();
        ActorMixins.exportToJson(this, json);
        json.writeValue("linkedActorID", linkedActor.getName());
        json.writeObjectEnd();
    }
}

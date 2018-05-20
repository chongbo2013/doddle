package com.dodles.gdx.dodleengine.scenegraph;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Json;
import com.dodles.gdx.dodleengine.DodleEngine;
import com.dodles.gdx.dodleengine.IdDatabase;
import com.dodles.gdx.dodleengine.ObjectManager;

/**
 * A virtual camera for a single scene in the dodle.
 */
public class SceneCamera extends Actor implements DodlesActor {
    public static final String ACTOR_TYPE = "SceneCamera";
    public static final String CAMERA_ID = "CAMERA_POSITION";
    
    private Scene scene;
    private Transform baseTransform;
    
    public SceneCamera(Scene scene) {
        setName(CAMERA_ID);
        
        this.scene = scene;
        this.scene.getStage().addActor(this);
        baseTransform = new Transform();
    }
    
    @Override
    public final String getType() {
        return ACTOR_TYPE;
    }
    
    @Override
    public final String getTrackingID() {
        throw new UnsupportedOperationException("This shouldn't be necessary for scene camera...");
    }

    @Override
    public final void setTrackingID(String trackingID) {
        throw new UnsupportedOperationException("This shouldn't be necessary for scene camera...");
    }
    
    @Override
    public final String getOriginalID() {
        throw new UnsupportedOperationException("This shouldn't be necessary for scene camera...");
    }
    
    @Override
    public final void setOriginalID(String pOriginalID) {
        throw new UnsupportedOperationException("This shouldn't be necessary for scene camera...");
    }
    
    @Override
    public final void act(float delta) {
        super.act(delta);
        
        if (this.getActions().size > 0) {
            updateCamera();
        }
    }
    
    @Override
    public final void updateOrigin() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public final Vector2 getOrigin() {
        return new Vector2(getOriginX(), getOriginY());
    }

    @Override
    public final Rectangle getDrawBounds() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public final void updateBaseTransform(Transform transform) {
        baseTransform = transform;
        ActorMixins.updateTransformProperties(this, transform);
    }

    @Override
    public final void resetToBaseTransform() {
        updateBaseTransform(baseTransform);
        updateCamera();
    }

    @Override
    public final Transform getBaseTransform() {
        return baseTransform;
    }
    
    @Override
    public final BaseDodlesViewGroup getParentDodlesViewGroup() {
        return CommonActorOperations.getParentDodlesViewGroup(this);
    }
    
    @Override
    public final String getParentViewID() {
        return CommonActorOperations.getParentView(this).getName();
    }
    
    /**
     * Updates the position of the root containers according to the position of
     * this virtual camera.
     */
    public final void updateCamera() {
        scene.setX(-getX());
        scene.setY(-getY());
        scene.setOriginX(-getX() + DodleEngine.DODLE_SIDE / 2);
        scene.setOriginY(-getY() + DodleEngine.DODLE_SIDE / 2);
        scene.setScaleX(getScaleX());
        scene.setScaleY(getScaleY());
        scene.setRotation(getRotation());
    }
    
    @Override
    public final SceneCamera dodleClone(IdDatabase idDB, ObjectManager objectManager) {
        return null;
    }

    @Override
    public final void writeConfig(Json json) {
    }
}

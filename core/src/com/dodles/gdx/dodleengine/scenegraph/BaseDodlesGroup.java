package com.dodles.gdx.dodleengine.scenegraph;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.SnapshotArray;
import com.dodles.gdx.dodleengine.IdDatabase;
import com.dodles.gdx.dodleengine.ObjectManager;

/**
 * Core functionality for groups that live within the dodles scene graph.
 */
public abstract class BaseDodlesGroup extends BaseGroup implements DodlesActor {
    private Rectangle defaultBounds;
    private String trackingID;
    private String originalID;
    private Rectangle drawBounds;
    private Transform baseTransform = new Transform();
    private boolean forceTransform = false;

    private String displayName;
    private Integer number;
    
    public BaseDodlesGroup(String id, String trackingID) {
        setName(id);
        setTrackingID(trackingID);
    }
    
    public BaseDodlesGroup(DodlesActorFactory actorFactory, IdDatabase idDB, JsonValue json) {
        ActorMixins.importFromJson(this, idDB, json);
        
        JsonValue jsonChildren = json.get("children");
        
        for (int i = 0; i < jsonChildren.size; i++) {
            Actor curActor = (Actor) actorFactory.createFromJson(idDB, jsonChildren.get(i));
            super.addActor(curActor);
        }

        if (json.has("displayName")) {
            setDisplayName(json.getString("displayName"));
        }

        if (json.has("number")) {
            number = json.getInt("number");
        }
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
    
    /**
     * Allows a default boundary for the group to be set in case it has no children.
     */
    public final void setDefaultBounds(Rectangle rect) {
        defaultBounds = new Rectangle(rect);
    }

    @Override
    public final void setOriginalID(String pOriginalID) {
        originalID = pOriginalID;
    }

    @Override
    public abstract String getType();

    @Override
    public final BaseDodlesViewGroup getParentDodlesViewGroup() {
        return CommonActorOperations.getParentDodlesViewGroup(this);
    }

    @Override
    public final String getParentViewID() {
        return CommonActorOperations.getParentView(this).getName();
    }
    
    /**
     * Forces the group draw to transform instead of offset no matter what.
     */
    public final void setForceTransform(boolean newForceTransform) {
        forceTransform = newForceTransform;
    }

    // CHECKSTYLE.OFF: DesignForExtension - need to overide this full method in child classes
    @Override
    public Rectangle getDrawBounds() {
        if (drawBounds != null) {
            return drawBounds;
        }
        
        for (Actor child : getChildren()) {
            DodlesActor dodleChild = (DodlesActor) child;
            
            Rectangle groupBounds = CommonActorOperations.getLocalBounds(dodleChild, this);
            
            if (drawBounds == null) {
                drawBounds = groupBounds;
            } else if (groupBounds.width > 0 || groupBounds.height > 0) {
                drawBounds = drawBounds.merge(groupBounds);
            }
        }
        
        if (drawBounds == null) {
            return defaultBounds;
        }
        
        return drawBounds;
    }
    // CHECKSTYLE.ON: DesignForExtension

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
    public abstract DodlesActor dodleClone(IdDatabase iddb, ObjectManager objectManager);

    @Override
    public final Vector2 getOrigin() {
        return new Vector2(getOriginX(), getOriginY());
    }

    @Override
    public final void updateOrigin() {
        updateOrigin(true);
    }
    
    /**
     * Updates the origin, optionally updating children.
     */
    public final void updateOrigin(boolean updateChildren) {
        drawBounds = null;
        ActorMixins.updateOrigin(this);
        
        if (updateChildren) {
            for (Actor child : new SnapshotArray<Actor>(getChildren())) {
                if (child instanceof BaseDodlesGroup) {
                    ((BaseDodlesGroup) child).updateOrigin();
                }
            }            
        }
        
        if (getParent() instanceof BaseDodlesGroup) {
            ((BaseDodlesGroup) getParent()).updateOrigin(false);
        }
    }
    
    @Override
    public final void writeConfig(Json json) {
        json.writeObjectStart();
        
        ActorMixins.exportToJson(this, json);
        
        json.writeArrayStart("children");
        SnapshotArray<Actor> children = super.getChildren();
        for (int i = 0; i < children.size; i++) {
            DodlesActor child = (DodlesActor) children.get(i);
            child.writeConfig(json);
        }
        json.writeArrayEnd();

        if (displayName != null) {
            json.writeValue("displayName", displayName);
        }
        if (number != null) {
            json.writeValue("number", number);
        }
        
        onWriteConfig(json);
        
        json.writeObjectEnd();
    }
    
    /**
     * Called during configuration writing.
     */
    protected void onWriteConfig(Json json) {
    }
    
    /**
     * Allows subclasses to adjust the opacity of rendering.
     */
    protected abstract float drawAlphaMultiplier();
    
    @Override
    public final void draw(Batch batch, float parentAlpha) {
        // If there's no rotation/scale, we can simply reposition things and
        // prevent flushing the batch!
        setTransform(forceTransform || getRotation() != 0 || getScaleX() != 1 || getScaleY() != 1);
        
        float offsetX = 0;
        float offsetY = 0;
        
        if (isTransform()) {
            applyTransform(batch, computeTransform());
        } else {
            offsetX = getX();
            offsetY = getY();
            setX(0);
            setY(0);
        }
        
        if (!drawOverride(batch, parentAlpha, offsetX, offsetY)) {
            for (Actor child : super.getChildren()) {
                if (child.isVisible()) {
                    float childX = child.getX(), childY = child.getY();
        
                    child.setX(childX + offsetX);
                    child.setY(childY + offsetY);

                    child.draw(batch, parentAlpha * drawAlphaMultiplier());

                    child.setX(childX);
                    child.setY(childY);
                }
            }
        }
        
        if (isTransform()) {
            resetTransform(batch);
        } else {
            setX(offsetX);
            setY(offsetY);
        }
    }
    
    /**
     * Allows the draw process to be overridden.
     */
    // CHECKSTYLE.OFF: DesignForExtension - oh come on, we're just returning false here...
    protected boolean drawOverride(Batch batch, float parentAlpha, float offsetX, float offsetY) {
        return false;
    }
    // CHECKSTYLE.ON: DesignForExtension


    /**
     * return the display Name for the group.
     * @return
     */
    public final String getDisplayName() {
        return displayName;
    }

    /**
     * set the displayName for the group.
     * @param displayName
     */
    public final void setDisplayName(String displayName) {
        this.displayName = displayName;
    }


    /**
     * get the original scene id.
     * @return
     */
    public final Integer getNumber() {
        return number;
    }

    /**
     * set the original Scene id.
     * @param number
     */
    public final void setNumber(Integer number) {
        this.number = number;
    }

    /**
     * really remove the Actor from the Group.  Necessary to bypass some of the
     * behaviors of the BaseDodlesViewGroup.
     * @param actor
     * @return
     */
    public final boolean reallyRemoveActor(Actor actor) {
        return super.removeActor(actor, true);
    }
}

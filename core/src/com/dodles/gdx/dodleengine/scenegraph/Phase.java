package com.dodles.gdx.dodleengine.scenegraph;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.SnapshotArray;
import com.dodles.gdx.dodleengine.IdDatabase;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects.PhaseHierarchy;

/**
 * Every DodlesGroup has at least one phase.  Phases can be used to transition between
 * different states of an object.
 */
public class Phase extends BaseDodlesGroup implements DodlesActor, DodlesView, ProcessAfterLoad {
    public static final String ACTOR_TYPE = "Phase";

    private Spine spine;
    private DisplayMode displayMode = DisplayMode.SOURCE;
    private TextureRegion cachedTexture;
    private PhaseHierarchy phaseHierarchy;

    public Phase(String id, String trackingID) {
        super(id, trackingID);
    }

    public Phase(DodlesActorFactory actorFactory, IdDatabase idDB, JsonValue json) {
        super(actorFactory, idDB, json);
        
        for (Actor child : new Array<Actor>(getChildren())) {
            if (child instanceof Spine) {
                setSpine((Spine) child);
            }
        }

        if (json.has("phaseHierarchy")) {
            phaseHierarchy = new PhaseHierarchy(json.get("phaseHierarchy"));
        }

        displayMode = DisplayMode.valueOf(json.getString("displayMode"));
    }

    @Override
    public final String getType() {
        return ACTOR_TYPE;
    }

    @Override
    public final void setVisible(boolean visible) {
        // No-op: phase visibility should not be controlled this way!
    }

    /**
     * Sets the spine for the phase.
     */
    public final void setSpine(Spine pSpine) {
        if (pSpine != spine) {
            this.removeActor(spine);

            if (pSpine != null) {
                this.addActor(pSpine);
            }
        }

        spine = pSpine;

    }

    /**
     * Returns the spine for the phase.
     */
    public final Spine getSpine() {
        return spine;
    }

    /**
     * Returns the display mode for the phase.
     */
    public final DisplayMode getDisplayMode() {
        return displayMode;
    }

    /**
     * Sets the display mode for the phase.
     */
    public final void setDisplayMode(DisplayMode mode) {
        displayMode = mode;

        if (mode == DisplayMode.CACHED || mode == DisplayMode.SPINE_FINAL) {

            updateVisibleActors(DisplayMode.SOURCE);
            cachedTexture = FrameBufferRenderer.renderToTextureRegion(this);
            //cachedTexture = FrameBufferRenderer.cropTextureRegion(cc, 200, 200, 200, 200);
        } else {
            if (cachedTexture != null) {
                cachedTexture.getTexture().dispose();
                cachedTexture = null;
            }
        }

        if (spine != null) {
            if (mode == DisplayMode.SPINE_FINAL) {
                spine.setTexture(this);
            } else {
                spine.clearTexture();
            }
        }

        updateVisibleActors(mode);
    }

    @Override
    protected final boolean drawOverride(Batch batch, float parentAlpha, float offsetX, float offsetY) {
        if (displayMode == DisplayMode.CACHED && cachedTexture != null) {
            Rectangle bounds = this.getDrawBounds();
            Matrix4 originalTransform = ActorMixins.setBatchTransformMatrix(batch, this);
            batch.draw(cachedTexture, bounds.x, bounds.y);
            batch.setTransformMatrix(originalTransform);
            return true;
        }
        return false;
    }

    @Override
    public final void afterLoad(ObjectManager objectManager) {
        // Force spine to recalculate from new baseline
        setDisplayMode(displayMode);
    }

    private void updateVisibleActors(DisplayMode mode) {
        for (Actor actor : getChildren()) {
            boolean isSpine = actor instanceof Spine;
            boolean visible = mode == DisplayMode.SPINE_OUTLINE
                    || (isSpine && mode == DisplayMode.SPINE_FINAL)
                    || (!isSpine && mode == DisplayMode.SOURCE);

            actor.setVisible(visible);
        }
    }

    @Override
    public final DodlesActor dodleClone(IdDatabase idDB, ObjectManager objectManager) {
        SnapshotArray<Actor> children = getChildren();

        Phase newPhase = new Phase(idDB.getNewID(getName()), getTrackingID());
        ActorMixins.commonClone(this, objectManager, newPhase);
        newPhase.displayMode = displayMode;

        int size = children.size;
        for (int i = 0; i < size; i++) {
            DodlesActor child = (DodlesActor) children.get(i);
            DodlesActor clone = child.dodleClone(idDB, objectManager);
            objectManager.addActor(clone);

            if (clone instanceof Spine) {
                newPhase.setSpine((Spine) clone);
            } else {
                newPhase.addActor((Actor) clone);
            }
        }

        return newPhase;
    }

    @Override
    public final void onWriteConfig(Json json) {
        json.writeValue("displayMode", displayMode.name());

        if (phaseHierarchy != null) {
            json.writeObjectStart("phaseHierarchy");
            phaseHierarchy.writeConfig(json);
            json.writeObjectEnd();
        }
    }

    @Override
    protected final float drawAlphaMultiplier() {
        return 1;
    }

    /**
     * get the phase hierarchy for this phase.  basically this describes at what level
     * the phase will exist.
     *
     * @return
     */
    public final PhaseHierarchy getPhaseHierarchy() {
        return phaseHierarchy;
    }

    /**
     * set the phase hierarchy for his phase.
     *
     * @param phaseHierarchy
     */
    public final void setPhaseHierarchy(PhaseHierarchy phaseHierarchy) {
        this.phaseHierarchy = phaseHierarchy;
    }

    /**
     * Defines the available phase display modes.
     */
    public enum DisplayMode {
        SOURCE,
        CACHED,
        SPINE_OUTLINE,
        SPINE_FINAL
    }
}

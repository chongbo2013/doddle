package com.dodles.gdx.dodleengine.editor.overlays;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.dodles.gdx.dodleengine.IdDatabase;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.commands.GroupHelper;
import com.dodles.gdx.dodleengine.scenegraph.BaseDodlesViewGroup;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.scenegraph.Phase;
import com.dodles.gdx.dodleengine.scenegraph.ProcessAfterLoad;
import com.dodles.gdx.dodleengine.scenegraph.RootGroup;
import com.dodles.gdx.dodleengine.scenegraph.Transform;
import com.dodles.gdx.dodleengine.scenegraph.Updatable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

public class RotatePreviewOverlay {
    protected final ObjectManager objectManager;
    protected final DodleStageManager stageManager;
    protected final GroupHelper groupHelper;

    private static final float ANGLE_SPACING = 30f;
    private static final float OPACITY = .3f;
    private List<Float> rotations = null;
    private List<DodlesActor> copies = new ArrayList<DodlesActor>();

    @Inject
    public RotatePreviewOverlay(
            ObjectManager objectManager,
            DodleStageManager stageManager,
            GroupHelper groupHelper
    ) {
        // Subsystem References
        this.objectManager = objectManager;
        this.stageManager = stageManager;
        this.groupHelper = groupHelper;
    }

    public void setRotationSequence(List<Float> rotationSequence) {
        rotations = new ArrayList<Float>(rotationSequence);
    }

    public void displayOverlay(boolean display) {
        if (display) {
            if (rotations != null){
                initializeOverlay();
            }
        }
        else {
            clear();
        }
    }

    private void clear() {
        for (DodlesActor actor : copies) {
            stageManager.getDrawGroup().removeActor((Actor) actor);
        }
        copies.clear();
    }

    private void initializeOverlay() {
        DodlesActor originalActor = objectManager.getSelectedActor();
        if (originalActor != null) {
            RootGroup overlayHost = stageManager.getDrawGroup();
            float startingAngle = originalActor.getRotation();
            startingAngle = normalizeAngle(startingAngle);
            float currentMaxAngle = startingAngle;
            float currentMinAngle = startingAngle;
            float anglePad = ANGLE_SPACING / 2;
            boolean currentMinNormalized = false; // Only want to normalize once, otherwise will wrap around repeatedly

            // First taking out the obviously useless and redundant angles
            Iterator<Float> iterator = rotations.iterator();
            float previous = iterator.next();
            float current;
            while (iterator.hasNext()) {
                current = iterator.next();
                if (Math.abs(current - previous) < anglePad) {
                    iterator.remove();
                }
                else {
                    previous = current;
                }
            }

            // Find the frames to preview
            boolean modified = true;
            while (modified) {
                modified = false;
                for (Float rotation : rotations) {
                    rotation = normalizeAngle(rotation);
                    float newMax = currentMaxAngle + ANGLE_SPACING;
                    if (rotation > newMax - anglePad && rotation < newMax + anglePad) {
                        addNewObject(originalActor, newMax, overlayHost);
                        currentMaxAngle += ANGLE_SPACING;
                        modified = true;
                    }
                    float newMin = currentMinAngle - ANGLE_SPACING;
                    if (!currentMinNormalized && newMin < 0) {
                        newMin = normalizeAngle(newMin);
                        currentMinAngle += 360;
                        currentMinNormalized = true;
                    }
                    if (rotation > newMin - anglePad && rotation < newMin + anglePad) {
                        addNewObject(originalActor, newMin, overlayHost);
                        currentMinAngle -= ANGLE_SPACING;
                        modified = true;
                    }
                }
            }
        }
    }

    private float normalizeAngle(float angle) {
        angle = angle % 360;
        angle = (angle + 360) % 360;
        return angle;
    }

    private void addNewObject(DodlesActor originalActor, float rotation, RootGroup group) {
        DodlesActor clonedActor = createClonedActor(originalActor);
        clonedActor.setRotation(rotation);
        ((Actor) clonedActor).setOrigin(((Actor) originalActor).getOriginX(), ((Actor) originalActor).getOriginY());
        lowerOpacity(clonedActor);
        stageManager.updateStateUi();
        groupHelper.removeChildFromGroup(clonedActor, false, false);
        group.addActor((Actor) clonedActor);
    }

    private void lowerOpacity(DodlesActor actor) {
        if (actor instanceof Updatable) {
            Updatable thing = (Updatable) actor;
            thing.getStrokeConfig().setOpacity(OPACITY);
            thing.regenerate();
        } else if (actor instanceof BaseDodlesViewGroup) {
            Array<DodlesActor> children = objectManager.getLeafActors(actor);
            for (DodlesActor child : children) {
                lowerOpacity(child);
            }
        }
    }

    private DodlesActor createClonedActor(DodlesActor original) {
        IdDatabase idDB = new IdDatabase();
        DodlesActor clonedActor = original.dodleClone(idDB, objectManager);
        copies.add(clonedActor);

        if (original instanceof Phase) {
            original.getParentDodlesViewGroup().addView((Phase) clonedActor);
        } else {
            original.getParentDodlesViewGroup().addActor((Actor) clonedActor, original.getParentViewID());
        }

        final Transform ct = original.getBaseTransform();
        Transform nt = new Transform() { {
            setRotation(ct.getRotation());
            setScaleX(ct.getScaleX());
            setScaleY(ct.getScaleY());
            setX(ct.getX());
            setY(ct.getY());
        } };

        clonedActor.updateBaseTransform(nt);

        clonedActor.updateOrigin();

        for (String newID : idDB.getNewIDs()) {
            DodlesActor curActor = objectManager.getActor(newID);

            if (curActor instanceof ProcessAfterLoad) {
                ((ProcessAfterLoad) curActor).afterLoad(objectManager);
            }
        }

        return clonedActor;
    }
}

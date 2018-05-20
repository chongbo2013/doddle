package com.dodles.gdx.dodleengine.scenegraph;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.IdDatabase;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.PHNode;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects.PhaseAttribute;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects.PhaseAttributeType;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects.PhaseHierarchy;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects.PhaseSchema;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects.PhaseStep;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects.PhaseStepType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Adds Dodles-specific functionality to the LibGDX Group class.
 */
public class DodlesGroup extends BaseDodlesViewGroup<Phase> implements DodlesActor {
    public static final String ACTOR_TYPE = "DodlesGroup";
    
    private final HashMap<PhaseAttributeType, PhaseStepType> attributeState = new HashMap<PhaseAttributeType, PhaseStepType>();
    
    private Phase visiblePhase;
    private String stencilPhaseID;

    // holds the selected phaseSchema configured by the user,
    // ie:  "Mouth" which uses X items from Angle, Emotion, and Talk
    private PhaseSchema phaseSchema;
    
    public DodlesGroup(String id, String trackingID) {
        super(id, trackingID);
    }
    
    public DodlesGroup(DodlesActorFactory actorFactory, IdDatabase idDB, JsonValue json) {
        super(actorFactory, idDB, json);
        ActorMixins.importFromJson(this, idDB, json);
        
        // Make sure phase map is initialized
        childrenChanged();
        
        JsonValue jsonPhaseSchema = json.get("phaseSchema");

        if (jsonPhaseSchema != null) {
            phaseSchema = new PhaseSchema(jsonPhaseSchema);
        }
        
        if (visiblePhase == null) {
            visiblePhase = getViews().get(0);
        }
    }
    
    // CHECKSTYLE.OFF: DesignForExtension - needs to be overridable
    @Override
    public String getType() {
        return ACTOR_TYPE;
    }
    // CHECKSTYLE.ON: DesignForExtension - needs to be overridable
    
    /**
     * Sets the ID of the stencil phase.
     */
    public final void setStencilPhaseID(String phaseID) {
        stencilPhaseID = phaseID;
    }
    
    /**
     * Returns the ID of the stencil phase.
     */
    public final String getStencilPhaseID() {
        return stencilPhaseID;
    }
    
    /**
     * Sets the visible phase ID.
     */
    public final void setVisiblePhase(String phaseID) {       
        visiblePhase = getViewMap().get(validateViewID(phaseID));
    }
    
    /**
     * Sets the visible phase by child index.
     */
    public final void setVisiblePhase(int index) {
        if (index >= 0 && index < getViews().size()) {
            int i = 0;
            
            for (Phase phase : getViews()) {
                if (i == index) {
                    visiblePhase = phase;
                    break;
                }
                
                i++;
            }
        }
    }
    
    /**
     * Clears any phase attributes set for this group.
     */
    public final void clearPhaseAttributes() {
        attributeState.clear();
    }
    
    /**
     * Sets a phase attribute for the group.
     */
    public final void setPhaseAttribute(PhaseAttributeType type, PhaseStepType step) {
        attributeState.put(type, step);
        PhaseSchema schema = getPhaseSchema();
        
        if (schema != null) {
            Phase bestPhase = findBestPhase(new ArrayList<PhaseAttribute>(schema.getAttributeList()), new ArrayList<Phase>(getPhases()));
            visiblePhase = bestPhase;

            // Apply phase attributes to all child groups?  I think that makes sense (instead of just the visible phase)
            for (Phase phase : getViews()) {
                for (Actor child : phase.getChildren()) {
                    if (child instanceof DodlesGroup) {
                        ((DodlesGroup) child).setPhaseAttribute(type, step);
                    }
                }
            }
        }
    }
    
    private Phase findBestPhase(List<PhaseAttribute> attributes, List<Phase> candidatePhases) {
        PhaseAttribute curAttribute = attributes.remove(0);
        
        PhaseStepType target = curAttribute.getDefaultPhaseStep().getPhaseStepType();
        
        if (attributeState.containsKey(curAttribute.getPhaseAttributeType())) {
            target = attributeState.get(curAttribute.getPhaseAttributeType());
        }
        
        PhaseStep step = curAttribute.getValidPhaseStep(target);

        int matchLevel = Integer.MAX_VALUE;
        ArrayList<Phase> matches = new ArrayList<Phase>();

        for (Phase curPhase : candidatePhases) {
            Integer curMatch = matchLevel(step, curPhase.getPhaseHierarchy(), 0);

            if (curMatch != null) {
                if (curMatch < matchLevel) {
                    matches.clear();
                    matchLevel = curMatch;
                }

                if (curMatch == matchLevel) {
                    matches.add(curPhase);
                }
            }
        }

        if (matches.size() == 1) {
            return matches.get(0);
        }
        
        if (attributes.size() > 0) {
            return findBestPhase(attributes, matches);
        }
        
        if (matches.size() >= 0) {
            return matches.get(0);
        }
        
        return null;
    }
    
    private Integer matchLevel(PhaseStep step, PhaseHierarchy hierarchy, int curLevel) {
        for (PHNode node : hierarchy.getHierarchyStack()) {
            if (step.getPhaseStepType() == node.getStepType()) {
                return curLevel;
            }
        }
        
        if (step.getFallback() != null) {
            return matchLevel(step.getFallback(), hierarchy, curLevel + 1);
        }
        
        return null;
    }
    
    /**
     * Adds a phase to the group.
     */
    public final void addPhase(Phase phase) {
        addView(phase);
    }
    
    /**
     * Gets a phase.
     */
    public final Phase getPhase(String phaseID) {
        return getView(phaseID);
    }
    
    /**
     * Removes a phase.
     */
    public final void removePhase(String phaseID) {
        removeView(phaseID);
    }
    
    @Override
    public final Phase getActiveView() {
        return visiblePhase;
    }
    
    /**
     * Returns the visible phase ID.
     */
    public final String getVisiblePhaseID() {
        return getActiveViewID();
    }
    
    /**
     * Returns the visible phase.
     */
    public final Phase getVisiblePhase() {        
        return getActiveView();
    }
    
    /**
     * Returns all phases in the group.
     */
    public final List<Phase> getPhases() {
        return getViews();
    }
    
    @Override
    protected final void onClearChildren() {
        visiblePhase = addEmptyPhase(validateViewID(null), getTrackingID());
    }
    
    @Override
    protected final void onClearViews() {
        visiblePhase = null;
        stencilPhaseID = null;
    }
    
    /**
     * Transitions to the next visible phase.
     */
    public final void nextVisiblePhase() {
        if (getViewMap().size() > 1) {
            List<Phase> phaseList = new ArrayList<Phase>(getViews());
            int index = phaseList.indexOf(visiblePhase);

            if (index == getViews().size() - 1) {
                index = 0;
            } else {
                index++;
            }

            visiblePhase = phaseList.get(index);
        }
    }
    
    /**
     * Adds a new empty phase with the given ID.
     */
    public final Phase addEmptyPhase(String id, String newTrackingID) {
        Phase phase = new Phase(id, newTrackingID);
        addView(phase);
        return phase;
    }

    /**
     * get the saved phaseSchema.
     * @return
     */
    public final PhaseSchema getPhaseSchema() {
        return phaseSchema;
    }

    /**
     * set the phaseSchema for this DodleGroup.
     */
    public final void setPhaseSchema(PhaseSchema phaseSchema) {
        this.phaseSchema = phaseSchema;
    }
    
    @Override
    public final boolean hasChildren() {
        boolean result = false;
        
        for (Phase phase : getViews()) {
            result |= phase.hasChildren();
        }
        
        return result;
    }
    
    @Override
    protected final boolean drawOverride(Batch batch, float parentAlpha, float offsetX, float offsetY) {
        if (stencilPhaseID != null) {
            for (Phase phase : getViews()) {
                if (phase.getName().equals(stencilPhaseID)) {
                    drawPhase(batch, phase, parentAlpha * 0.2f, offsetX, offsetY);
                    break;
                }
            }
        }

        drawPhase(batch, visiblePhase, parentAlpha, offsetX, offsetY);
        
        return true;
    }
    
    private void drawPhase(Batch batch, Phase phase, float alpha, float offsetX, float offsetY) {
        float phaseX = phase.getX(), phaseY = phase.getY();
        
        phase.setX(phaseX + offsetX);
        phase.setY(phaseY + offsetY);
        
        phase.draw(batch, alpha);
        
        phase.setX(phaseX);
        phase.setY(phaseY);
    }
    
    @Override
    public final DodlesGroup dodleClone(IdDatabase idDB, ObjectManager objectManager) {
        DodlesGroup newGroup = new DodlesGroup(idDB.getNewID(getName()), getTrackingID());
        ActorMixins.commonClone(this, objectManager, newGroup);
        newGroup.clearViews();
        
        if (this.phaseSchema != null) {
            newGroup.setPhaseSchema(this.phaseSchema.dodleClone());
        }
        
        for (Phase phase : getViews()) {
            Phase clonePhase = (Phase) phase.dodleClone(idDB, objectManager);
            newGroup.addView(clonePhase);
            objectManager.addActor(clonePhase);
            
            if (newGroup.visiblePhase == null) {
                newGroup.visiblePhase = clonePhase;
            }
        }

        return newGroup;
    }

    @Override
    public final void onWriteConfig(Json json) {
        if (phaseSchema != null) {
            json.writeObjectStart("phaseSchema");
            phaseSchema.writeConfig(json);
            json.writeObjectEnd();
        }
    }

    @Override
    protected final float drawAlphaMultiplier() {
        return 1;
    }
}

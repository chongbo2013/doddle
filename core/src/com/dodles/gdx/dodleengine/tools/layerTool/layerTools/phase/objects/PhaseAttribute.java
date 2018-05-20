package com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A phase object that may be hierarchical.
 */
public class PhaseAttribute {

    private PhaseAttributeType phaseAttributeType;

    private ArrayList<PhaseStep> phaseSteps = new ArrayList<PhaseStep>();

    public PhaseAttribute() {

    }

    public PhaseAttribute(JsonValue json) {
        loadConfig(json);
    }

    public PhaseAttribute(PhaseAttributeType type) {
        this.phaseAttributeType = type;
    }

    /**
     * Get the PhaseAttributeType of this PhaseAttribute.
     */
    public final PhaseAttributeType getPhaseAttributeType() {
        return phaseAttributeType;
    }

    /**
     * Get the PhaseStep children of this PhaseAttribute.
     */
    public final ArrayList<PhaseStep> getPhaseSteps() {
        return phaseSteps;
    }
    
    /**
     * Returns a phase step that's valid in this schema for the given step type.
     */
    public final PhaseStep getValidPhaseStep(PhaseStepType type) {
        for (PhaseStep step : phaseSteps) {
            if (step.getPhaseStepType().equals(type)) {
                if (step.isSelected()) {
                    return step;
                } else if (step.getFallback() != null) {
                    return getValidPhaseStep(step.getFallback().getPhaseStepType());
                }
            }
        }
        
        return getDefaultPhaseStep();
    }
    
    /**
     * Returns the default phase step for the attribute.
     */
    public final PhaseStep getDefaultPhaseStep() {
        for (PhaseStep step : phaseSteps) {
            if (step.getFallback() == null) {
                return step;
            }
        }
        
        // We should never get here...
        return phaseSteps.get(0);
    }
    
    /**
     * Initializes the phase step graph for the attribute.
     */
    public final void setPhaseSteps(List<PhaseStepType> phaseStepTypes) {
        this.phaseSteps = new ArrayList<PhaseStep>();
        ArrayList<PhaseStepType> typesToInit = new ArrayList<PhaseStepType>(phaseStepTypes);
        HashMap<PhaseStepType, PhaseStep> lookup = new HashMap<PhaseStepType, PhaseStep>();
        
        while (!typesToInit.isEmpty()) {
            for (PhaseStepType type : (List<PhaseStepType>) typesToInit.clone()) {    
                PhaseStep newStep;
                
                if (type.getDefaultParent() != null) {
                    if (!lookup.containsKey(type.getDefaultParent())) {
                        continue;
                    }
                    
                    newStep = new PhaseStep(type, lookup.get(type.getDefaultParent()));
                } else {
                    newStep = new PhaseStep(type);
                }
                
                this.phaseSteps.add(newStep);
                lookup.put(type, newStep);
                typesToInit.remove(type);
            }
        }
    }

    /**
     * Set the PhaseStep children of this PhaseAttribute.
     * @param phaseSteps
     */
    public final void setPhaseSteps(ArrayList<PhaseStep> phaseSteps) {
        this.phaseSteps = phaseSteps;
    }

    /**
     * Write this object to json.
     */
    public final void writeConfig(Json json) {
        json.writeValue("phaseAttributeType", phaseAttributeType.name());
        json.writeArrayStart("phaseSteps");
        for (PhaseStep step: phaseSteps) {
            json.writeObjectStart();
            step.writeConfig(json);
            json.writeObjectEnd();
        }
        json.writeArrayEnd();
    }

    /**
     * Load this object from json.
     */
    private void loadConfig(JsonValue json) {
        String typeName = json.getString("phaseAttributeType");
        phaseAttributeType = PhaseAttributeType.valueOf(typeName);

        JsonValue jsonPhaseSteps = json.get("phaseSteps");
        for (JsonValue step : jsonPhaseSteps.iterator()) {
            PhaseStep ps = new PhaseStep(step);
            phaseSteps.add(ps);
        }
    }

    /**
     * deep clone a PhaseAttribute.
     * @return
     */
    public final PhaseAttribute dodleClone() {
        PhaseAttribute clone = new PhaseAttribute();
        clone.phaseAttributeType = PhaseAttributeType.valueOf(this.phaseAttributeType.name());

        for (PhaseStep step: this.phaseSteps) {
            clone.phaseSteps.add(step.dodleClone());
        }

        return clone;
    }
}

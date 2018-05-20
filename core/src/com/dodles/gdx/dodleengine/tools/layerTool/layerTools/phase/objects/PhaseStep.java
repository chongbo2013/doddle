package com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

/**
 * A leaf phase object.
 */
public class PhaseStep {

    private PhaseStepType phaseStepType;

    // used on the 'schema' screen to indicate that this step is available to the 'database' screen
    private boolean selected;
    private PhaseStep fallback;
    private boolean initial;
    private String phaseID;

    // used on the 'database' screen to indicate that a phase has been assigned to this step.
    private boolean active;

    public PhaseStep() {
    }

    public PhaseStep(JsonValue json) {
        loadConfig(json);
    }

    public PhaseStep(PhaseStepType type) {
        this.phaseStepType = type;
        this.initial = true;
        this.selected = true;
    }

    public PhaseStep(PhaseStepType type, PhaseStep fallback) {
        this.phaseStepType = type;
        this.fallback = fallback;
        this.initial = false;
    }

    /**
     * Get the PhaseStepType of this PhaseStep.
     */
    public final PhaseStepType getPhaseStepType() {
        return phaseStepType;
    }

    /**
     * assign PhaseStepType.
     * @param phaseStepType
     */
    public final void setPhaseStepType(PhaseStepType phaseStepType) {
        this.phaseStepType = phaseStepType;
    }

    /**
     * Determine if this leaf was selected for use.
     */
    public final boolean isSelected() {
        return selected;
    }

    /**
     * Set that this leaf is selected or not.
     */
    public final void setSelected(boolean selected) {
        if (!initial) {
            this.selected = selected;
        }
    }

    /**
     * Get the fallback PhaseStep from this one.
     */
    public final PhaseStep getFallback() {
        return fallback;
    }

    /**
     * Set the fallback PhaseStep from this one.
     */
    public final void setFallback(PhaseStep fallback) {
        this.fallback = fallback;
    }

    /**
     * Determine if this PhaseStep is the initial one.
     */
    public final boolean isInitial() {
        return initial;
    }

    /**
     * Determine if this PhaseStep is the active one.
     */
    public final boolean isActive() {
        return active;
    }

    /**
     * Set that this PhaseStep is the active one.
     */
    public final void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Write this object to json.
     */
    public final void writeConfig(Json json) {
        json.writeValue("phaseStepType", phaseStepType.name());
        json.writeValue("selected", selected);
        json.writeValue("active", active);

        if (fallback != null) {
            json.writeValue("fallback", fallback.phaseStepType.name());
        }
        json.writeValue("initial", initial);
    }

    /**
     * Read this object from json.
     */
    private void loadConfig(JsonValue json) {
        selected = json.getBoolean("selected");
        initial = json.getBoolean("initial");
        active = json.getBoolean("active");

        String typeName = json.getString("phaseStepType");
        phaseStepType = PhaseStepType.valueOf(typeName);

        if (!initial) {
            JsonValue fb = json.get("fallback");
            if (fb != null) {
                PhaseStep s = new PhaseStep();
                s.setPhaseStepType(PhaseStepType.valueOf(json.getString("fallback")));
                fallback = s;
            }
        }
    }

    /**
     * get the Phase ID.
     * @return
     */
    public final String getPhaseID() {
        return phaseID;
    }

    /**
     * deep clone the PhaseStep object.
     * @return
     */
    public final PhaseStep dodleClone() {
        PhaseStep clone = new PhaseStep();
        clone.phaseStepType = PhaseStepType.valueOf(this.phaseStepType.name());
        clone.selected = this.selected;
        clone.initial = this.initial;
        clone.active = this.active;

        if (this.fallback != null) {
            clone.fallback = this.fallback.dodleClone();
        }
        return clone;
    }

    /**
     * Sets the phase id assigned to this phaseStep.
     */
    public final void setPhaseID(String phaseID) {
        this.phaseID = phaseID;
    }
}

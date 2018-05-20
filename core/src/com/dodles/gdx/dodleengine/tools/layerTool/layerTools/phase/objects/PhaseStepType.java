package com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects;

import java.util.ArrayList;

/**
 * The known PhaseStepTypes.
 */
public enum PhaseStepType {

    // root
    ROOT("Root", PhaseAttributeType.ROOT, null),

    // types for emotion
    NORMAL("Normal", PhaseAttributeType.EMOTION, null),
    DAT_GRIN("Dat grin!", PhaseAttributeType.EMOTION, NORMAL),
    DISGUST("Disgust", PhaseAttributeType.EMOTION, NORMAL),
    GRIN("Grin", PhaseAttributeType.EMOTION, NORMAL),
    NOT_IMPRESSED("Not impressed", PhaseAttributeType.EMOTION, NORMAL),
    PISSED("Pissed", PhaseAttributeType.EMOTION, NORMAL),
    SAD("Sad", PhaseAttributeType.EMOTION, NORMAL),
    SHOCK_SURPRISE("Shock surprise", PhaseAttributeType.EMOTION, NORMAL),
    SMILE("Smile", PhaseAttributeType.EMOTION, NORMAL),
    SMIRK("Smirk", PhaseAttributeType.EMOTION, NORMAL),

    // types for talk
    RESTING("Resting", PhaseAttributeType.TALK, null),
    A("A", PhaseAttributeType.TALK, RESTING),
    O("O", PhaseAttributeType.TALK, RESTING),
    E("E", PhaseAttributeType.TALK, RESTING),
    W_R("W_R", PhaseAttributeType.TALK, RESTING),
    T_S("T_S", PhaseAttributeType.TALK, RESTING),
    L_N("L_N", PhaseAttributeType.TALK, RESTING),
    U_Q("U_Q", PhaseAttributeType.TALK, RESTING),
    M_B_P("M_B_P", PhaseAttributeType.TALK, RESTING),
    F_V("F_V", PhaseAttributeType.TALK, RESTING),

    // types for angle
    FORWARD("Forward", PhaseAttributeType.ANGLE, null),
    FORWARD_LEFT_QUARTER("Forward Left Quarter", PhaseAttributeType.ANGLE, FORWARD),
    LEFT("Left", PhaseAttributeType.ANGLE, FORWARD),
    BACKWARD_LEFT_QUARTER("Backward Left Quarter", PhaseAttributeType.ANGLE, FORWARD),
    BACKWARD("Backward", PhaseAttributeType.ANGLE, FORWARD),
    BACKWARD_RIGHT_QUARTER("Backward Right Quarter", PhaseAttributeType.ANGLE, FORWARD),
    RIGHT("Right", PhaseAttributeType.ANGLE, FORWARD),
    FORWARD_RIGHT_QUARTER("Forward Right Quarter", PhaseAttributeType.ANGLE, FORWARD),
    TOP("Top", PhaseAttributeType.ANGLE, FORWARD),
    BOTTOM("Bottom", PhaseAttributeType.ANGLE, FORWARD);

    private final String description;
    private final PhaseAttributeType attributeType;
    private final PhaseStepType defaultParent;

    PhaseStepType(String description, PhaseAttributeType attributeType, PhaseStepType defaultParent) {
        this.description = description;
        this.attributeType = attributeType;
        this.defaultParent = defaultParent;
    }

    /**
     * Returns the parameter type mapped to the value used to store it in JSON.
     */
    public static PhaseStepType getByDescription(String description) {
        for (PhaseStepType pst : PhaseStepType.values()) {
            if (pst.description.equals(description)) {
                return pst;
            }
        }

        return null;
    }
    
    /**
     * Returns all phase step types for the given attribute type.
     */
    public static ArrayList<PhaseStepType> getByAttributeType(PhaseAttributeType attributeType) {
        ArrayList<PhaseStepType> result = new ArrayList<PhaseStepType>();
        
        for (PhaseStepType pst : PhaseStepType.values()) {
            if (pst.attributeType == attributeType) {
                if (pst.defaultParent == null) {
                    result.add(0, pst);
                } else {
                    result.add(pst);
                }
            }
        }
        
        return result;
    }
    
    /**
     * Returns the key in the icon atlas for the icon describing this step.
     */
    public final String getIconAtlasKey() {
        return attributeType.name() + "_" + name();
    }

    /**
     * Return the jsonKey.
     */
    public final String getDescription() {
        return this.description;
    }
    
    /**
     * Returns the attribute type for this step type.
     */
    public final PhaseAttributeType getAttributeType() {
        return attributeType;
    }
    
    /**
     * Returns the default parent step type for this step type.
     */
    public final PhaseStepType getDefaultParent() {
        return defaultParent;
    }
}

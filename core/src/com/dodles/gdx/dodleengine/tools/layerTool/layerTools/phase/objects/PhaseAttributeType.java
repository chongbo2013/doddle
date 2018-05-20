package com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects;

/**
 * The known PhaseAttributeTypes.
 */
public enum PhaseAttributeType {

    ROOT("Root"),

    EMOTION("Emotion"),
    ANGLE("Angle"),
    TALK("Talk");

    private final String description;

    PhaseAttributeType(String description) {
        this.description = description;
    }

    /**
     * Returns the parameter type mapped to the value used to store it in JSON.
     */
    public static PhaseAttributeType getByDescription(String description) {
        for (PhaseAttributeType pat : PhaseAttributeType.values()) {
            if (pat.description.equals(description)) {
                return pat;
            }
        }

        return null;
    }

    /**
     * return the jsonKey.
     */
    public final String getDescription() {
        return this.description;
    }
}

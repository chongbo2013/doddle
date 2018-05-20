package com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects;

/**
 * The known PhaseTypes.
 */
public enum PhaseType {
    EMPTY(""),
    MOUTH("Mouth"),
    GENERIC("Generic"),
    CAR("Car");

    private final String description;

    PhaseType(String description) {
        this.description = description;
    }

    /**
     * Returns the parameter type mapped to the value used to store it in JSON.
     */
    public static PhaseType getByDescription(String description) {
        for (PhaseType pt : PhaseType.values()) {
            if (pt.description.equals(description)) {
                return pt;
            }
        }

        return null;
    }

    /**
     * return the jsonKey.
     * @return
     */
    public final String getDescription() {
        return this.description;
    }
}

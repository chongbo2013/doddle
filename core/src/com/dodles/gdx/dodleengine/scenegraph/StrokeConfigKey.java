package com.dodles.gdx.dodleengine.scenegraph;

/**
 * Defines the available keys in the strokeconfig object.
 */
public enum StrokeConfigKey {
    SIZE("size"),
    COLOR("color"),
    OPACITY("opacity"),
    FONT("font"),
    FILL("fillcolor");

    private final String value;

    StrokeConfigKey(String val) {
        this.value = val;
    }

    /**
     * return the value.
     */
    public String get() {
        return this.value;
    }
}

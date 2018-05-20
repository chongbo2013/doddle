package com.dodles.gdx.dodleengine.animation;

/**
 * Defines the available parameter types.
 */
public enum EffectParameterType {
    DEGREES("degrees", true),
    LENGTH("length", true),
    SCALE("scale", true),
    TRANSLATE_X("translateX", true),
    TRANSLATE_Y("translateY", true),
    SCENE_NUM("sceneNum", true),
    PHASE_NUM("phaseNum", true),
    PHASE_ATTRIBUTE("phaseAttribute", false),
    PHASE_STEP("phaseStep", false),
    SOUND_ASSET("soundAsset", false);
    
    private final String jsonKey;
    private final boolean isConfigurable;
    
    EffectParameterType(String jsonKey, boolean isConfigurable) {
        this.jsonKey = jsonKey;
        this.isConfigurable = isConfigurable;
    }
    
    /**
     * Returns the parameter type mapped to the value used to store it in JSON.
     */
    public static EffectParameterType getByJsonKey(String jsonKey) {
        for (EffectParameterType ept : EffectParameterType.values()) {
            if (ept.jsonKey.equals(jsonKey)) {
                return ept;
            }
        }
        
        return null;
    }

    /**
     * Returns the jsonKey.
     */
    public String getJsonKey() {
        return this.jsonKey;
    }
    
    /**
     * Returns a value indicating whether this parameter is configurable by the end user.
     */
    public boolean isConfigurable() {
        return isConfigurable;
    }
}

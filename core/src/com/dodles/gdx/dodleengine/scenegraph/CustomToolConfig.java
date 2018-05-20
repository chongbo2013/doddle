package com.dodles.gdx.dodleengine.scenegraph;

import com.badlogic.gdx.utils.Json;

/**
 * Marker interface for tool configuration classes.
 */
public interface CustomToolConfig {
    /**
     * Copy this custom tool config.
     */
    CustomToolConfig cpy();
    
    /**
     * Returns the type of the custom configuration.
     */
    String getType();
    
    /**
     * Writes the geometry configuration to the json document.
     */
    void writeConfig(Json json);
}

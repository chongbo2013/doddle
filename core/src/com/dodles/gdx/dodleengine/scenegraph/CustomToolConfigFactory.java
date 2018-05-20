package com.dodles.gdx.dodleengine.scenegraph;

import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.brushes.BrushConfig;
import com.dodles.gdx.dodleengine.commands.ImportedAssetConfig;
import com.dodles.gdx.dodleengine.commands.PathConfig;
import com.dodles.gdx.dodleengine.geometry.GeometryConfig;

/**
 * Handles instantiation of custom tool configurations.
 */
public final class CustomToolConfigFactory {
    private CustomToolConfigFactory() {
    }

    /**
     * Creates a custom tool config instance from JSON.
     */
    public static CustomToolConfig create(JsonValue json) {
        if (json == null) {
            return null;
        }
        
        CustomToolConfig result = GeometryConfig.create(json);
        
        if (result != null) {
            return result;
        }
        
        result = BrushConfig.create(json);
        
        if (result != null) {
            return result;
        }
        
        result = ImportedAssetConfig.create(json);
        
        if (result != null) {
            return result;
        }
        
        result = PathConfig.create(json);
        
        if (result != null) {
            return result;
        }
        
        throw new UnsupportedOperationException("Unrecognized custom tool config type: " + json.getString("type"));
    }
}

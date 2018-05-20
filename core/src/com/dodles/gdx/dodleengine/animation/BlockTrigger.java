package com.dodles.gdx.dodleengine.animation;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.ObjectManager;

/**
 * A trigger for activating blocks.
 */
public abstract class BlockTrigger {
    /**
     * Returns the type of trigger.
     */
    public abstract String getType();
    
    /**
     * Returns true if the trigger is currently active.
     */
    public abstract boolean isTriggered();
    
    /**
     * Writes the configuration to JSON.
     */
    public final void writeConfig(Json json, String name) {
        json.writeObjectStart(name);
        json.writeValue("type", getType());
        onWriteConfig(json);
        json.writeObjectEnd();
    }
    
    /**
     * Called after the JSON object is set up to finish writing the trigger to JSON.
     */
    protected abstract void onWriteConfig(Json json);
    
    /**
     * Creates a block trigger from json.
     */
    public static BlockTrigger create(JsonValue json, ObjectManager objectManager) {
        String type = json.getString("type");
        
        if (type.equals(TouchBlockTrigger.TRIGGER_TYPE)) {
            return new TouchBlockTrigger(json, objectManager);
        }
        
        throw new GdxRuntimeException("Unrecognized trigger type: " + type);
    }
}

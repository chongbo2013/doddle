package com.dodles.gdx.dodleengine.animation;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Base keyframe functionality.
 */
public abstract class BaseKeyframe {
    private Float percent = null;
    
    public BaseKeyframe(JsonValue json) {
        if (json.has("pct")) {
            percent = new Float(json.getFloat("pct"));
        }
    }
    
    public BaseKeyframe(float percent) {
        this.percent = percent;
    }
    
    /**
     * Returns the type of the keyframe.
     */
    public abstract String getType();
    
    /**
     * Returns the percent of time for the effect this keyframe takes.
     */
    public final Float getPercent() {
        return percent;
    }
    
    /**
     * write the config into the Json object.
     * @param json
     */
    public final void writeConfig(Json json) {
        json.writeValue("keyframeType", getType());
        
        if (percent != null) {
            json.writeValue("pct", percent);
        }
        
        onWriteConfig(json);
    }
    
    /**
     * Allows extension of writeConfig.
     */
    public abstract void onWriteConfig(Json json);
    
    /**
     * Parses a keyframe into the appropriate type from JSON.
     */
    public static BaseKeyframe parseKeyframe(JsonValue json) {
        String keyframeType = json.getString("keyframeType", MotionKeyframe.KEYFRAME_TYPE);
        
        if (keyframeType.equals(MotionKeyframe.KEYFRAME_TYPE)) {
            return new MotionKeyframe(json);
        } else if (keyframeType.equals(VisemeKeyframe.KEYFRAME_TYPE)) {
            return new VisemeKeyframe(json);
        }
        
        throw new GdxRuntimeException("Unrecognized keyframe type: " + keyframeType);
    }
}

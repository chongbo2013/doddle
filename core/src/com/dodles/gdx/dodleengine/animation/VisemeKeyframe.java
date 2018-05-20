package com.dodles.gdx.dodleengine.animation;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import static com.dodles.gdx.dodleengine.animation.MotionKeyframe.KEYFRAME_TYPE;

/**
 * A keyframe that triggers a change in viseme.
 */
public class VisemeKeyframe extends BaseKeyframe {
    public static final String KEYFRAME_TYPE = "Viseme";
    
    private Viseme viseme;
    
    public VisemeKeyframe(JsonValue json) {
        super(json);
        
        if (json.has("viseme")) {
            viseme = Viseme.valueOf(json.getString("viseme"));
        }
    }
    
    public VisemeKeyframe(float totalLength, float start, Viseme viseme) {
        super((start / totalLength) * 100f);
        
        this.viseme = viseme;
    }

    @Override
    public final String getType() {
        return KEYFRAME_TYPE;
    }
    
    /**
     * Returns the viseme associated with this keyframe.
     */
    public final Viseme getViseme() {
        return viseme;
    }
    
    @Override
    public final void onWriteConfig(Json json) {
        json.writeValue("viseme", viseme.name());
    }
}

package com.dodles.gdx.dodleengine.animation;

import com.dodles.gdx.dodleengine.PerDodleEngine;
import javax.inject.Inject;

/**
 * Global settings for playback.
 */
@PerDodleEngine
public class PlaybackSettings {
    private float playSpeed = 1;
    
    @Inject
    public PlaybackSettings() {
    }
    
    /**
     * Returns the play speed for the dodle.
     */
    public final float getPlaySpeed() {
        return playSpeed;
    }
    
    /**
     * Sets the play speed of the dodle.
     */
    public final void setPlaySpeed(float newSpeed) {
        playSpeed = newSpeed;
    }
}

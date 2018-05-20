package com.dodles.gdx.dodleengine.animation;

/**
 * Defines the available effect types.
 */
public enum EffectType {
    MOVE(true, false),
    SCENES(false, false),
    PHASES(true, false),
    EMOTION(true, true),
    ANGLE(true, true),
    AUDIO(true, true);
    
    private boolean actorSpecific;
    private boolean dynamic;
    
    EffectType(boolean actorSpecific, boolean dynamic) {
        this.actorSpecific = actorSpecific;
        this.dynamic = dynamic;
    }
    
    /**
     * Returns true if the effect type must be chained to an actor.
     */
    public boolean isActorSpecific() {
        return actorSpecific;
    }
    
    /**
     * Returns true if the effects within this type are dynamically generated instead of read from the json configuration.
     */
    public boolean isDynamic() {
        return dynamic;
    }
}

package com.dodles.gdx.dodleengine.animation;

/**
 * Encapsulates timing information about when an effect should be played within a block.
 */
public class EffectTiming {
    private final EffectAnimator effect;
    private EffectTiming afterEffect;
    private float delay;

    public EffectTiming(EffectAnimator effect, EffectTiming afterEffect, float delay) {
        this.effect = effect;
        this.afterEffect = afterEffect;
        this.delay = delay;
    }
    
    /**
     * Returns the effect this timing applies to.
     */
    public final EffectAnimator getEffect() {
        return effect;
    }
    
    /**
     * Returns the effect ID of the effect this effect should play after.
     */
    public final String getAfterEffectID() {
        if (afterEffect == null) {
            return null;
        }
        
        return afterEffect.getEffect().getEffectID();
    }
    
    /**
     * Returns the effect this effect should play after.
     */
    public final EffectTiming getAfterEffect() {
        return afterEffect;
    }
    
    /**
     * Calculates the start time of this effect.
     */
    public final float calculateStartTime() {
        float result = delay;
        
        if (afterEffect != null) {
            result += afterEffect.calculateStartTime() + afterEffect.getEffect().getEffectLength();
        }
        
        return result;
    }
    
    /**
     * Calculates the end time of this effect.
     */
    public final float calculateEndTime() {
        return calculateStartTime() + effect.getEffectLength();
    }
    
    /**
     * Sets the effect ID of the effect this effect should play after.
     */
    public final void setAfterEffect(EffectTiming effectTiming) {
        afterEffect = effectTiming;
    }
    
    /**
     * Returns any additional delay to wait before playing the effect.
     */
    public final float getDelay() {
        return delay;
    }
    
    /**
     * Sets the delay before the effect starts.
     */
    public final void setDelay(float pDelay) {
        delay = pDelay;
    }
    
    /**
     * Creates a timing status object.
     */
    public final EffectTimingStatus createTimingStatus() {
        return new EffectTimingStatus(delay);
    }
    
    /**
     * Tracks delay progress during animation.
     */
    public class EffectTimingStatus {
        private float delayRemaining;
        
        public EffectTimingStatus(float delayRemaining) {
            this.delayRemaining = delayRemaining;
        }
        
        /**
         * Returns the amount of time remaining to delay the animation. 
         */
        public final float getDelayRemaining() {
            return delayRemaining;
        }
        
        /**
         * Decrements the delay time remaining.
         */
        public final void decrementDelayRemaining(float deltaTime) {
            delayRemaining -= deltaTime;
        }
    }
}

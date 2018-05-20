package com.dodles.gdx.dodleengine.animation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a (possibly branching) chain of EffectTimings.
 */
public class EffectChain {
    private final EffectTiming currentTiming;
    private final ArrayList<EffectChain> dependentEffects = new ArrayList<EffectChain>();
    
    public EffectChain(EffectTiming currentTiming) {
        this.currentTiming = currentTiming;
    }
    
    /**
     * Returns the current timing at this link in the chain.
     */
    public final EffectTiming getCurrentTiming() {
        return currentTiming;
    }
    
    /**
     * Returns all effects immediately dependent on this link in the chain.
     */
    public final List<EffectChain> getDependentEffects() {
        return Collections.unmodifiableList(dependentEffects);
    }
    
    /**
     * Returns all timings that are children of this link in the chain.
     */
    public final List<EffectTiming> getAllTimings() {
        ArrayList<EffectTiming> result = new ArrayList<EffectTiming>();
        result.add(currentTiming);
        
        for (EffectChain depChain : dependentEffects) {
            result.addAll(depChain.getAllTimings());
        }
        
        return result;
    }
    
    /**
     * Adds a new dependent effect.
     */
    public final boolean addDependentEffect(EffectChain chain) {        
        if (currentTiming.getEffect().getEffectID().equals(chain.getCurrentTiming().getAfterEffectID())) {
            dependentEffects.add(chain);
            return true;
        }
        
        for (EffectChain curChain : dependentEffects) {
            if (curChain.addDependentEffect(chain)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Calculates the length of the effect chain.
     */
    public final float calculateChainDuration() {
        float maxChildLength = 0;

        for (EffectChain dependentLink : dependentEffects) {
            maxChildLength = Math.max(maxChildLength, dependentLink.calculateChainDuration());
        }

        return currentTiming.getDelay() + currentTiming.getEffect().getEffectLength() + maxChildLength;
    }
}

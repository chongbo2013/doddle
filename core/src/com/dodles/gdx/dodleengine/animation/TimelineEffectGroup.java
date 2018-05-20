package com.dodles.gdx.dodleengine.animation;

import java.util.ArrayList;
import java.util.List;

/**
 * Groups together effects by actor and animation type for display on the timeline.
 */
public class TimelineEffectGroup {
    private String actorID;
    private EffectType effectType;
    private ArrayList<EffectTiming> timings = new ArrayList<EffectTiming>();
    
    public TimelineEffectGroup(EffectTiming timing) {
        this(timing.getEffect().getActor().getName(), timing.getEffect().getEffectType());
        timings.add(timing);
    }
    
    public TimelineEffectGroup(String actorID, EffectType effectType) {
        this.actorID = actorID;
        this.effectType = effectType;
    }
    
    /**
     * Returns the actor ID.
     */
    public final String getActorID() {
        return actorID;
    }
    
    /**
     * Returns the effect type.
     */
    public final EffectType getEffectType() {
        return effectType;
    }
    
    /**
     * Returns the timings in this group on as few timelines as necessary.
     */
    public final List<List<EffectTiming>> getGroupTimings() {
        ArrayList<List<EffectTiming>> result = new ArrayList<List<EffectTiming>>();
        
        source: for (EffectTiming sourceTiming : timings) {
            // If there's a conflict in all lists in the result, add a new result
            result: for (List<EffectTiming> list : result) {                
                for (EffectTiming resultTiming : list) {
                    if (resultTiming.calculateEndTime() > sourceTiming.calculateStartTime() && sourceTiming.calculateEndTime() > resultTiming.calculateEndTime()) {
                        // Conflict!
                        continue result;
                    }
                }
                
                // This list is OK, add the timing...
                list.add(sourceTiming);
                continue source;
            }
            
            ArrayList<EffectTiming> newList = new ArrayList<EffectTiming>();
            newList.add(sourceTiming);
            result.add(newList);
        }
        
        return result;
    }
    
    /**
     * Adds a timing to this group.
     */
    public final void addTiming(EffectTiming timing) {
        timings.add(timing);
    }
    
    /**
     * Returns true if this group contains the given timing.
     */
    public final boolean contains(EffectTiming timing) {
        return timings.contains(timing);
    }
    
    /**
     * Returns a key that can be used to identify this effect group.
     */
    public final String getKey() {
        return getKey(actorID, effectType);
    }
    
    /**
     * Returns a key that would identify an effect group with this timing.
     */
    public static String getKey(EffectTiming timing) {
        return getKey(timing.getEffect().getActor().getName(), timing.getEffect().getEffectType());
    }
    
    /**
     * Returns an effect group key.
     */
    public static String getKey(String actorID, EffectType effectType) {
        if (!effectType.isActorSpecific()) {
            actorID = "N/A";
        }
        
        return actorID + "-" + effectType.name();
    }
}

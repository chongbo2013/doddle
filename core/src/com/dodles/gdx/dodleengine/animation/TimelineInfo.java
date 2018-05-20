package com.dodles.gdx.dodleengine.animation;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

/**
 * Calculates timeline information for a block.
 */
public class TimelineInfo {
    private final Block block;
    private final float sceneLength;
    private final HashMap<String, TimelineEffectGroup> effectGroups = new HashMap<String, TimelineEffectGroup>();

    public TimelineInfo(SceneAnimation sAnim, Block block) {
        this.block = block;
        
        for (EffectTiming timing : block.allEffects()) {
            String key = TimelineEffectGroup.getKey(timing);
            
            if (!effectGroups.containsKey(key)) {
                effectGroups.put(key, new TimelineEffectGroup(timing));
            } else {
                effectGroups.get(key).addTiming(timing);
            }
        }
        
        sceneLength = block.calculateDuration();
    }
    
    /**
     * Returns the block this info is for.
     */
    public final Block getBlock() {
        return block;
    }

    /**
     * Returns the length of the scene.
     */
    public final float getSceneLength() {
        return sceneLength;
    }
    
    /**
     * Returns all effect groups on the current timeline.
     */
    public final Collection<TimelineEffectGroup> getAllEffectGroups() {
        return Collections.unmodifiableCollection(effectGroups.values());
    }
    
    /**
     * Returns a count of all independent animation "threads" in this block.
     */
    public final int getThreadCount() {
        int result = 0;
        
        for (TimelineEffectGroup effectGroup : effectGroups.values()) {
            result += effectGroup.getGroupTimings().size();
        }
        
        return result;
    }
}

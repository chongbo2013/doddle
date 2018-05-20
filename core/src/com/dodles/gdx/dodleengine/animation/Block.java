package com.dodles.gdx.dodleengine.animation;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.scenegraph.DodlesGroup;
import com.dodles.gdx.dodleengine.scenegraph.SceneCamera;
import com.dodles.gdx.dodleengine.scenegraph.Transform;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A "Block" of animation within a scene - can be triggered by an event or the
 * conclusion of another block.
 */
public class Block {
    private final ObjectManager objectManager;
    private final Block parent;
    private final String blockID;
    private final ArrayList<Block> childBlocks = new ArrayList<Block>();
    private final Map<String, EffectTiming> effects = new LinkedHashMap<String, EffectTiming>();
    
    private String displayName = "Main";
    private SceneAnimation sceneAnimation;
    private float delay = 0;
    private BlockTrigger trigger;
    
    public Block(String blockID, Block parent, ObjectManager objectManager, SceneAnimation sceneAnimation) {
        this.blockID = blockID;
        this.objectManager = objectManager;
        this.sceneAnimation = sceneAnimation;
        this.parent = parent;
    }
    
    public Block(JsonValue json, Block parent, AnimationManager animationManager, ObjectManager objectManager, SceneAnimation sceneAnimation) {
        this(json.getString("blockID"), parent, objectManager, sceneAnimation);
        this.delay = json.getFloat("delay");
        this.displayName = json.getString("displayName", "Main");
        loadTimingsFromJson(json, animationManager);
        
        if (json.has("trigger")) {
            this.trigger = BlockTrigger.create(json.get("trigger"), objectManager);
        }
        
        if (json.has("childBlocks")) {
            for (JsonValue childJson : json.get("childBlocks").iterator()) {
                childBlocks.add(new Block(childJson, this, animationManager, objectManager, sceneAnimation));
            }
        }
    }
    
    /**
     * Returns the display name for the block.
     */
    public final String getDisplayName() {
        return displayName;
    }
    
    /**
     * Sets the display name for the block.
     */
    public final void setDisplayName(String pDisplayName) {
        displayName = pDisplayName;
    }
    
    /**
     * Returns the trigger for the block.
     */
    public final BlockTrigger getTrigger() {
        return trigger;
    }
    
    /**
     * Sets the trigger for the block.
     */
    public final void setTrigger(BlockTrigger newTrigger) {
        trigger = newTrigger;
    }
    
    /**
     * Returns the ID of the block.
     */
    public final String getBlockId() {
        return blockID;
    }
    
    /**
     * Returns the parent block.
     */
    public final Block getParentBlock() {
        return parent;
    }
    
    /**
     * Finds the block within this block's chain with the given block ID.
     */
    public final Block findBlock(String targetBlockID) {
        if (blockID.equals(targetBlockID)) {
            return this;
        }
        
        for (Block child : childBlocks) {
            Block result = child.findBlock(targetBlockID);
            
            if (result != null) {
                return result;
            }
        }
        
        return null;
    }
    
    /**
     * Returns all child blocks of this block.
     */
    public final List<Block> getChildBlocks() {
        return Collections.unmodifiableList(childBlocks);
    }
    
    /**
     * Returns all blocks in this block's chain (including the current block).
     */
    public final Collection<Block> getAllBlocks() {
        ArrayList<Block> result = new ArrayList<Block>();
        result.add(this);
        
        for (Block child : childBlocks) {
            result.addAll(child.getAllBlocks());
        }
        
        return result;
    }
    
    /**
     * Adds a child block to this block.
     */
    public final void addBlock(Block childBlock) {
        childBlocks.add(childBlock);
    }
    
    /**
     * Removes a block from this block.
     */
    public final void removeBlock(String childBlockID) {
        for (Block child : new ArrayList<Block>(childBlocks)) {
            if (child.getBlockId().equals(childBlockID)) {
                childBlocks.remove(child);
                break;
            }
        }
    }
    
    /**
     * Calculates the changes in positions within this block for all objects.
     */
    public final HashMap<String, Transform> calculateBlockTransformDelta() {
        HashMap<String, Transform> result = new HashMap<String, Transform>();
        calculateBlockTransformDelta(result);
        return result;
    }
    
    /**
     * Calculates the changes in positions within this block for all objects, placing the result in the given hash map.
     */
    public final void calculateBlockTransformDelta(HashMap<String, Transform> transformMap) {        
        for (String actorID : transformMap.keySet()) {
            Transform totalDelta = transformMap.get(actorID);
            
            for (EffectTiming effect : allEffects(actorID)) {
                Transform curDelta = effect.getEffect().calculateTransformDelta();

                totalDelta.setX(totalDelta.getX() + curDelta.getX());
                totalDelta.setY(totalDelta.getY() + curDelta.getY());
                totalDelta.setScaleX(totalDelta.getScaleX() + curDelta.getScaleX());
                totalDelta.setScaleY(totalDelta.getScaleY() + curDelta.getScaleY());
                totalDelta.setRotation(totalDelta.getRotation() + curDelta.getRotation());
            }
        }
    }
    
    /**
     * Calculates the start positions of all objects in the given block.
     */
    public final HashMap<String, Transform> calculateBlockStartPositions() {
        HashMap<String, Transform> result = new HashMap<String, Transform>();
        
        for (DodlesActor actor : objectManager.allActorsInScene(sceneAnimation.getSceneID()).values()) {
            Transform blockStartTransform = actor.getBaseTransform().cpy();
            
            if (result.containsKey(actor.getName())) {
                Transform t = result.get(actor.getName());
                t.setX(t.getX() + blockStartTransform.getX());
                t.setY(t.getY() + blockStartTransform.getY());
                t.setScaleX(t.getScaleX() + blockStartTransform.getScaleX());
                t.setScaleY(t.getScaleY() + blockStartTransform.getScaleY());
                t.setRotation(t.getRotation() + blockStartTransform.getRotation());
            } else {
                result.put(actor.getName(), blockStartTransform);
            }
        }
        
        return result;
    }
    
    /**
     * Resets the block to it's base transform.
     */
    public final void resetToBaseTransform() {
        Map<String, DodlesActor> sceneActors = objectManager.allActorsInScene(sceneAnimation.getSceneID());
        HashMap<String, Transform> blockStartPositions = calculateBlockStartPositions();
        
        for (String actorID : blockStartPositions.keySet()) {
            DodlesActor actor = sceneActors.get(actorID);
            Transform delta = blockStartPositions.get(actorID);
            actor.resetToBaseTransform();
            
            actor.setX(delta.getX());
            actor.setY(delta.getY());
            actor.setScaleX(delta.getScaleX());
            actor.setScaleY(delta.getScaleY());
            actor.setRotation(delta.getRotation());
            
            if (actor instanceof SceneCamera) {
                ((SceneCamera) actor).updateCamera();
            } else if (actor instanceof DodlesGroup) {
                ((DodlesGroup) actor).clearPhaseAttributes();
                ((DodlesGroup) actor).setVisiblePhase(0);
            }
        }
    }
    
    /**
     * Adds an effect to the block, optionally after a specified effect or after a specified delay.
     */
    public final EffectAnimator addEffect(EffectAnimator effect, EffectTiming afterEffect, float effectDelay) {        
        EffectTiming newTiming = new EffectTiming(effect, afterEffect, effectDelay);
        effects.put(effect.getEffectID(), newTiming);
        
        return effect;
    }
    
    /**
     * Returns the effect with the given ID.
     */
    public final EffectTiming getEffectTiming(String id) {
        return effects.get(id);
    }
    
    /**
     * Returns the total number of effects in the group.
     */
    public final int totalEffectCount() {
        return effects.size();
    }
    
    /**
     * Returns the effects ordered how they should play in the block.
     */
    public final ArrayList<EffectChain> getEffectChains() {
        ArrayList<EffectChain> result = new ArrayList<EffectChain>();
        ArrayList<EffectTiming> dependentEffects = new ArrayList<EffectTiming>();
        
        // Add all core effects that aren't dependent on other effects...
        for (EffectTiming effect : effects.values()) {
            if (effect.getAfterEffectID() == null) {
                ArrayList<EffectTiming> effectList = new ArrayList<EffectTiming>();
                effectList.add(effect);
                result.add(new EffectChain(effect));
            } else {
                dependentEffects.add(effect);
            }
        }
        
        // Now add the remainder, dependent effects in order...
        while (dependentEffects.size() > 0) {
            for (EffectTiming timing : new ArrayList<EffectTiming>(dependentEffects)) {
                EffectChain newLink = new EffectChain(timing);
                for (EffectChain curChain : result) {
                    if (curChain.addDependentEffect(newLink)) {
                        dependentEffects.remove(timing);
                    }
                }
            }
        }
        
        return result;
    }
    
    /**
     * Returns all effects in the group.
     */
    public final Collection<EffectTiming> allEffects() {
        return allEffects(null);
    }
    
    /**
     * Calculates the total duration of the block.
     */
    public final float calculateDuration() {
        float sceneLength = 0;
        
        for (EffectChain chain : getEffectChains()) {
            sceneLength = Math.max(sceneLength, chain.calculateChainDuration());
        }
        
        return sceneLength;
    }
    
    /**
     * Returns all effects in the group for the given actor.
     */
    public final Collection<EffectTiming> allEffects(String actorID) {
        ArrayList<EffectTiming> result = new ArrayList<EffectTiming>();
        
        for (EffectTiming effect : effects.values()) {
            if (actorID == null || effect.getEffect().getActor().getName().equals(actorID)) {
                result.add(effect);
            }
        }
        
        return result;
    }

    /**
     * Returns the timings for a specific actor.
     */
    public final ArrayList<EffectTiming> getTimingsForActor(String actorID) {
        ArrayList<EffectTiming> result = new ArrayList<EffectTiming>();
        
        for (EffectChain chain : getEffectChains()) {
            if (chain.getCurrentTiming().getEffect().getActor().getName().equals(actorID)) {
                result.add(chain.getCurrentTiming());
            }
        }
        
        return result;
    }
    
    /**
     * Removes an effect from the block.
     */
    public final void removeEffect(String effectID) {
        EffectTiming effectTiming = effects.remove(effectID);
        
        for (EffectTiming timing : effects.values()) {
            String afterEffectID = timing.getAfterEffectID();
            if (afterEffectID != null && afterEffectID.equals(effectID)) {
                // Update dependent effect appropriately
                timing.setAfterEffect(null);
                timing.setDelay(timing.getDelay() + effectTiming.getDelay() + effectTiming.getEffect().getEffectLength());
            }
        }
    }
    
    /**
     * Serializes the block to JSON.
     */
    public final void writeConfig(Json json) {
        writeConfig(json, null);
    }
    
    /**
     * Serializes the block to JSON.
     */
    public final void writeConfig(Json json, String name) {
        if (name != null) {
            json.writeObjectStart(name);
        } else {
            json.writeObjectStart();
        }
        
        json.writeValue("blockID", blockID);
        json.writeValue("delay", delay);
        json.writeValue("displayName", displayName);
        
        if (trigger != null) {
            trigger.writeConfig(json, "trigger");
        }
        
        json.writeArrayStart("effectTimings");
        for (EffectTiming timing : effects.values()) {
            json.writeObjectStart();
            
            json.writeObjectStart("effect");
            timing.getEffect().writeConfig(json);
            json.writeObjectEnd();
            
            json.writeValue("afterEffectID", timing.getAfterEffectID());
            json.writeValue("delay", timing.getDelay());
            
            json.writeObjectEnd();
        }
        json.writeArrayEnd();
        
        json.writeArrayStart("childBlocks");
        for (Block childBlock : childBlocks) {
            childBlock.writeConfig(json);
        }
        json.writeArrayEnd();
        
        json.writeObjectEnd();
    }

    /**
     * Serializes the block to JSON.
     */
    public final void writeAnimations(Json json, String name) {
        if (name != null) {
            json.writeObjectStart(name);
        } else {
            json.writeObjectStart();
        }

        json.writeValue("blockID", blockID);
        json.writeValue("delay", delay);
        json.writeValue("displayName", displayName);

        if (trigger != null) {
            trigger.writeConfig(json, "trigger");
        }

        json.writeArrayStart("effectTimings");
        for (EffectTiming timing : effects.values()) {
            json.writeObjectStart();

            json.writeObjectStart("effect");
            timing.getEffect().writeAnimationToJson(json);
            json.writeObjectEnd();

            json.writeValue("afterEffectID", timing.getAfterEffectID());
            json.writeValue("delay", timing.getDelay());

            json.writeObjectEnd();
        }
        json.writeArrayEnd();

        json.writeArrayStart("childBlocks");
        for (Block childBlock : childBlocks) {
            childBlock.writeConfig(json);
        }
        json.writeArrayEnd();

        json.writeObjectEnd();
    }

    
    /**
     * Updates the group from a JSON snippet.
     */
    public final void updateFromConfig(JsonValue json, AnimationManager animationManager) {
        effects.clear();
        loadTimingsFromJson(json, animationManager);
    }
    
    private void loadTimingsFromJson(JsonValue json, AnimationManager animationManager) {
        ArrayList<JsonValue> timingsToProcess = new ArrayList<JsonValue>();
        
        if (!json.has("effectTimings")) {
            return;
        }
        
        for (JsonValue timingJson : json.get("effectTimings").iterator()) {
            timingsToProcess.add(timingJson);
        }
        
        while (!timingsToProcess.isEmpty()) {
            for (JsonValue timingJson : new ArrayList<JsonValue>(timingsToProcess)) {
                String afterEffectID = timingJson.getString("afterEffectID");
                EffectTiming afterEffect = null;
                
                if (afterEffectID != null) {
                    if (!effects.containsKey(afterEffectID)) {
                        continue;
                    }
                    
                    afterEffect = effects.get(afterEffectID);
                }
                
                EffectAnimator ea = new EffectAnimator(timingJson.get("effect"), animationManager, objectManager, sceneAnimation);
                EffectTiming timing = new EffectTiming(ea, afterEffect, timingJson.getFloat("delay"));
                effects.put(ea.getEffectID(), timing);
                timingsToProcess.remove(timingJson);
            }
        }
    }
}

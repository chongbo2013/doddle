package com.dodles.gdx.dodleengine.commands.animation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.animation.AnimationManager;
import com.dodles.gdx.dodleengine.animation.Block;
import com.dodles.gdx.dodleengine.animation.EffectAnimator;
import com.dodles.gdx.dodleengine.animation.EffectChain;
import com.dodles.gdx.dodleengine.animation.EffectDefinition;
import com.dodles.gdx.dodleengine.animation.EffectParameterType;
import com.dodles.gdx.dodleengine.animation.EffectTiming;
import com.dodles.gdx.dodleengine.animation.EffectType;
import com.dodles.gdx.dodleengine.animation.SceneAnimation;
import com.dodles.gdx.dodleengine.commands.Command;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import de.hypergraphs.hyena.core.shared.data.UUID;

/**
 * A command that creates or modifies an animation effect.
 */
public class ModifyEffectCommand implements Command {
    public static final String COMMAND_NAME = "modifyEffect";
    
    private final AnimationManager animationManager;
    private final ObjectManager objectManager;
    
    private String sceneID;
    private String blockID;
    private String effectID;
    private ArrayList<String> effectIDList;
    private String objectID;
    private String afterEffectID;
    private float delay;
    private EffectType effectType;
    private String effectName;
    private EffectDefinition effectDefinition;
    private String[] allEffectIDs;
    private HashMap<EffectParameterType, Object> effectParameters = new HashMap<EffectParameterType, Object>();
    
    private boolean createdActor = false;
    private boolean createdEffect = false;
    private boolean createdEffectGroup = false;
    
    @Inject
    public ModifyEffectCommand(AnimationManager animationManager, ObjectManager objectManager) {
        this.animationManager = animationManager;
        this.objectManager = objectManager;
    }
    
    @Override
    public final String getName() {
        return COMMAND_NAME;
    }
    
    /**
     * Initializes the command.
     */
    public final void init(String newSceneID, String newEffectID, String newBlockID, String newObjectID, EffectType newEffectType, String newEffectName, EffectDefinition newEffectDefinition, HashMap<EffectParameterType, Object> newEffectParameters, String newAfterEffectID, float newDelay) {
        sceneID = newSceneID;
        effectID = newEffectID;
        blockID = newBlockID;
        objectID = newObjectID;
        effectType = newEffectType;
        effectName = newEffectName;
        effectDefinition = newEffectDefinition;
        effectParameters = newEffectParameters;
        afterEffectID = newAfterEffectID;
        delay = newDelay;
        effectIDList = new ArrayList<String>();
    }

    @Override
    public final void execute() {
        SceneAnimation scene = animationManager.getSceneAnimation(sceneID);
        Block block = scene.getRootBlock().findBlock(blockID);
        
        EffectTiming timing = block.getEffectTiming(effectID);

        if (timing == null) {
            createdEffect = true;
            block.addEffect(new EffectAnimator(
                effectID,
                animationManager,
                objectManager,
                effectType,
                effectName,
                effectDefinition,
                effectParameters,
                objectManager.allActorsInScene(sceneID).get(objectID)
            ), block.getEffectTiming(afterEffectID), delay);
        } else {
            // TODO: edit effects (wasn't implemented in original code base)
            createdEffect = true;
            block.addEffect(new EffectAnimator(
                    effectID,
                    animationManager,
                    objectManager,
                    effectType,
                    effectName,
                    effectDefinition,
                    effectParameters,
                    objectManager.allActorsInScene(sceneID).get(objectID)
            ), block.getEffectTiming(afterEffectID), delay);
        }
        effectIDList.add(effectID);
    }

    public final void modifyEffect(String blockID, String EffectID, float newStartTime, float newLength) {
        SceneAnimation scene = animationManager.getSceneAnimation(sceneID);
        Block block = scene.getRootBlock().findBlock(blockID);

        EffectTiming timing = block.getEffectTiming(EffectID);
        if(newStartTime != -1) {
            timing.setDelay(newStartTime);
        }
        if(newLength != -1) {
            timing.getEffect().getEffectParameters().put(EffectParameterType.LENGTH, newLength);
        }
    }

    public final void reverseBack(EffectDefinition reverseeffectDefinition, HashMap<EffectParameterType, Object> newEffectParameters) {
        SceneAnimation scene = animationManager.getSceneAnimation(sceneID);
        Block block = scene.getRootBlock().findBlock(blockID);
        String EffectID = UUID.uuid();
        float maxLength = -1;
        for (EffectTiming timing : block.allEffects(objectManager.getSelectedActor().getName())) {
            float curLength = timing.calculateEndTime();
            if (curLength > maxLength) {
                maxLength = curLength;
                afterEffectID = timing.getEffect().getEffectID();
            }
        }
        float delay = block.getEffectTiming(afterEffectID).calculateEndTime() + 0.1f;
        block.addEffect(new EffectAnimator(
                EffectID,
                animationManager,
                objectManager,
                effectType,
                effectName,
                reverseeffectDefinition,
                newEffectParameters,
                objectManager.allActorsInScene(sceneID).get(objectManager.getSelectedActor().getName())
        ), null , delay);
        effectIDList.add(EffectID);
    }



    public final void repeatIterations (int numberOfIteration) {
        SceneAnimation scene = animationManager.getSceneAnimation(sceneID);
        Block block = scene.getRootBlock().findBlock(blockID);
        Collection<EffectTiming> allEffects = block.allEffects(objectManager.getSelectedActor().getName());
        for(int i = 0; i< numberOfIteration; i++) {
            for (EffectTiming timing : allEffects) {
                if (timing != null) {
                    createdEffect = true;
                    addEffectTimingToBlock(timing, block);

                }
            }
        }
    }

    public final void addEffectTimingToBlock(EffectTiming timing, Block block) {
        String EffectID = UUID.uuid();
        float maxLength = -1;
        for (EffectTiming effect : block.allEffects(objectManager.getSelectedActor().getName())) {
            float curLength = effect.calculateEndTime();
            if (curLength > maxLength) {
                maxLength = curLength;
                afterEffectID = effect.getEffect().getEffectID();
            }
        }


        float delay = block.getEffectTiming(afterEffectID).calculateEndTime() + 0.1f;
        block.addEffect(new EffectAnimator(
                EffectID,
                animationManager,
                objectManager,
                timing.getEffect().getEffectType(),
                timing.getEffect().getEffectName(),
                timing.getEffect().getEffectDefinition(),
                timing.getEffect().getEffectParameters(),
                objectManager.allActorsInScene(sceneID).get(objectManager.getSelectedActor().getName())
        ), null , delay);
        effectIDList.add(EffectID);
    }


    @Override
    public final void undo() {
        SceneAnimation scene = animationManager.getSceneAnimation(sceneID);
        Block block = scene.getRootBlock().findBlock(blockID);
        
        if (createdEffect) {
            for (String effectID : effectIDList) {
                block.removeEffect(effectID);
            }
        } /* else {
             // TODO: edit effects (wasn't implemented in original code base)
        }*/
    }

    @Override
    public final void writeConfig(Json json) {
        json.writeValue("sceneID", sceneID);
        json.writeValue("blockID", blockID);
        json.writeValue("effectID", effectID);
        json.writeValue("objectID", objectID);
        json.writeValue("afterEffectID", afterEffectID);
        json.writeValue("delay", delay);
        json.writeValue("effectType", effectType.name());
        json.writeValue("effectName", effectName);
        
        effectDefinition.writeConfig(json);

        json.writeObjectStart("effectParameters");
        for (HashMap.Entry<EffectParameterType, Object> param : effectParameters.entrySet()) {
            json.writeValue(param.getKey().getJsonKey(), param.getValue());
        }
        json.writeObjectEnd();
    }

    @Override
    public final void loadConfig(JsonValue json, CommandFactory factory) {
        sceneID = json.getString("sceneID");
        blockID = json.getString("blockID");
        effectID = json.getString("effectID");
        objectID = json.getString("objectID");
        afterEffectID = json.getString("afterEffectID", null);
        delay = json.getFloat("delay", 0);
        effectType = EffectType.valueOf(json.getString("effectType"));
        effectName = json.getString("effectName", null);
        
        effectDefinition = new EffectDefinition(json.get("effectDefinition"));
        
        JsonValue jsonParameters = json.get("effectParameters");
        
        if (jsonParameters != null) {
            for (int i = 0; i < jsonParameters.size; i++) {
                String key = jsonParameters.get(i).name;
                EffectParameterType ept = EffectParameterType.getByJsonKey(key);
                effectParameters.put(ept, jsonParameters.getFloat(key));
            }
        }
    }
}

package com.dodles.gdx.dodleengine.animation;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.ObjectManager;

/**
 * Manages all animations within a single scene.
 */
public class SceneAnimation {
    private final Block rootBlock;
    private final ObjectManager objectManager;
    
    private String sceneID;
    
    public SceneAnimation(String sceneID, String blockID, ObjectManager objectManager) {
        this.sceneID = sceneID;
        this.objectManager = objectManager;
        rootBlock = new Block(blockID, null, objectManager, this);
    }
    
    public SceneAnimation(JsonValue json, AnimationManager animationManager, ObjectManager objectManager) {
        this.sceneID = json.getString("sceneID");
        this.objectManager = objectManager;
        
        rootBlock = new Block(json.get("rootBlock"), null, animationManager, objectManager, this);
    }
    
    /**
     * Returns the block at the given index in the ordered list.
     */
    public final Block getRootBlock() {
        return rootBlock;
    }
    
    /**
     * Returns the ID of the scene.
     */
    public final String getSceneID() {
        return sceneID;
    }
    
    /**
     * Serializes the sceneAnimation to JSON.
     */
    public final void writeConfig(Json json) {
        json.writeObjectStart();
        json.writeValue("sceneID", sceneID);
        rootBlock.writeConfig(json, "rootBlock");
        json.writeObjectEnd();
    }

    /**
     * Serializes the sceneAnimation to JSON.
     */
    public final void writeAnimationWithoutKeyFrames(Json json) {
        json.writeObjectStart();
        json.writeValue("sceneID", sceneID);
        rootBlock.writeAnimations(json, "rootBlock");
        json.writeObjectEnd();
    }

}

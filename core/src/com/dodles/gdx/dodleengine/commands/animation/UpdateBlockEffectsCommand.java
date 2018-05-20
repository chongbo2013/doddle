package com.dodles.gdx.dodleengine.commands.animation;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.animation.AnimationManager;
import com.dodles.gdx.dodleengine.animation.Block;
import com.dodles.gdx.dodleengine.animation.SceneAnimation;
import com.dodles.gdx.dodleengine.commands.Command;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import java.io.StringWriter;
import javax.inject.Inject;

/**
 * Command to update effects in a block.
 */
public class UpdateBlockEffectsCommand implements Command {
    public static final String COMMAND_NAME = "updateBlockEffects";
    
    private final AnimationManager animationManager;
    private final ObjectManager objectManager;
    
    private String sceneID;
    private String blockID;
    private String executeBlockEffects;
    private String undoBlockEffects;
    
    @Inject
    public UpdateBlockEffectsCommand(AnimationManager animationManager, ObjectManager objectManager) {
        this.animationManager = animationManager;
        this.objectManager = objectManager;
    }
    
    
    @Override
    public final String getName() {
        return COMMAND_NAME;
    }
    
    /**
     * Begins the live execution process.
     */
    public final void beginExecuteLive(String pSceneID, String pBlockID) {
        sceneID = pSceneID;
        blockID = pBlockID;
        undoBlockEffects = archiveBlockEffects();
    }
    
    /**
     * Finishes the live execution process.
     */
    public final void finishExecuteLive() {
        executeBlockEffects = archiveBlockEffects();
    }

    @Override
    public final void execute() {
        undoBlockEffects = archiveBlockEffects();
        restoreBlockEffects(executeBlockEffects);
    }

    @Override
    public final void undo() {
        restoreBlockEffects(undoBlockEffects);
    }

    @Override
    public final void writeConfig(Json json) {
        json.writeValue("sceneId", sceneID);
        json.writeValue("blockId", blockID);
        json.writeValue("blockEffects", executeBlockEffects);
    }

    @Override
    public final void loadConfig(JsonValue json, CommandFactory factory) {
        sceneID = json.getString("sceneId");
        blockID = json.getString("blockId");
        executeBlockEffects = json.getString("blockEffects");
    }
    
    private String archiveBlockEffects() {
        SceneAnimation sAnim = animationManager.getSceneAnimation(sceneID);
        Block block = sAnim.getRootBlock().findBlock(blockID);
        
        StringWriter writer = new StringWriter();
        Json json = new Json(OutputType.json);
        json.setWriter(writer);
        json.setQuoteLongValues(true);
        
        block.writeConfig(json);

        return writer.toString();
    }
    
    private void restoreBlockEffects(String blockEffects) {
        SceneAnimation sAnim = animationManager.getSceneAnimation(sceneID);
        Block block = sAnim.getRootBlock().findBlock(blockID);
        JsonValue data = new JsonReader().parse(blockEffects);
        
        block.updateFromConfig(data, animationManager);
    }
}

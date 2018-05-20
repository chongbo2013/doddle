package com.dodles.gdx.dodleengine.commands.scene;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.commands.Command;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.scenegraph.Scene;
import javax.inject.Inject;

/**
 * Edits scene information.
 */
public class EditSceneCommand implements Command {
    public static final String COMMAND_NAME = "editScene";
    
    private final ObjectManager objectManager;
    
    private Scene scene;
    
    private String origSceneName;
    private String finalSceneName;
    
    @Inject
    public EditSceneCommand(ObjectManager objectManager) {
        this.objectManager = objectManager;
    }
    
    @Override
    public final String getName() {
        return COMMAND_NAME;
    }
    
    /**
     * Initializes the command.
     */
    public final void init(Scene pScene, String sceneName) {
        scene = pScene;
        finalSceneName = sceneName;
    }

    @Override
    public final void execute() {
        origSceneName = scene.getDisplayName();
        
        scene.setDisplayName(finalSceneName);
    }

    @Override
    public final void undo() {
        scene.setDisplayName(origSceneName);
    }

    @Override
    public final void writeConfig(Json json) {
        throw new GdxRuntimeException("Serialization Not Supported!");
    }

    @Override
    public final void loadConfig(JsonValue json, CommandFactory factory) {
        throw new GdxRuntimeException("Serialization Not Supported!");
    }
}

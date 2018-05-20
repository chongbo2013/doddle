package com.dodles.gdx.dodleengine.commands.scene;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.commands.Command;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.scenegraph.Scene;
import java.util.ArrayList;
import javax.inject.Inject;

/**
 * Reorders scenes.
 */
public class ReorderScenesCommand implements Command {
    public static final String COMMAND_NAME = "reorderScenes";
    
    private final ObjectManager objectManager;
    
    private ArrayList<Scene> originalSceneList;
    private ArrayList<Scene> finalSceneList;
    
    @Inject
    public ReorderScenesCommand(ObjectManager objectManager) {
        this.objectManager = objectManager;
    }
    
    @Override
    public final String getName() {
        return COMMAND_NAME;
    }
    
    /**
     * Initializes the command.
     */
    public final void init(ArrayList<Scene> scenes) {
        this.finalSceneList = scenes;
    }

    @Override
    public final void execute() {
        originalSceneList = new ArrayList<Scene>(objectManager.getScenes());
        objectManager.clearScenes();
        
        for (Scene s : finalSceneList) {
            objectManager.addScene(s);
        }
    }

    @Override
    public final void undo() {
        objectManager.clearScenes();
                
        for (Scene s : originalSceneList) {
            objectManager.addScene(s);
        }
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

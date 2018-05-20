package com.dodles.gdx.dodleengine.commands.scene;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.animation.AnimationManager;
import com.dodles.gdx.dodleengine.animation.SceneAnimation;
import com.dodles.gdx.dodleengine.commands.Command;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.scenegraph.Scene;

import javax.inject.Inject;

/**
 * Deletes a phase from an existing PhaseGroup.
 */
public class DeleteSceneCommand implements Command {
    public static final String COMMAND_NAME = "deleteScene";

    private final AnimationManager animationManager;
    private final ObjectManager objectManager;

    private String sceneID;
    private Scene scene;
    private SceneAnimation sceneAnimation;

    @Inject
    public DeleteSceneCommand(AnimationManager animationManager, ObjectManager objectManager) {
        this.animationManager = animationManager;
        this.objectManager = objectManager;
    }
    
    /**
     * Initializes the command, deleting a scene with id "sID" from the ObjectManager.
     */
    public final void init(String sID) {
        sceneID = sID;
        // hold a reference to the Scene
        scene = objectManager.getScene(sceneID);
        sceneAnimation = animationManager.getSceneAnimation(sceneID);
    }
    
    @Override
    public final String getName() {
        return COMMAND_NAME;
    }

    @Override
    public final void execute() {
        objectManager.removeScene(sceneID);
        animationManager.removeSceneAnimation(sceneID);
    }

    @Override
    public final void undo() {
        objectManager.addScene(scene);
        animationManager.addSceneAnimation(scene, sceneAnimation);
    }

    @Override
    public final void writeConfig(Json json) {
        json.writeValue("sceneID", sceneID);
    }

    @Override
    public final void loadConfig(JsonValue json, CommandFactory factory) {
        sceneID = json.getString("sceneID");
    }
}

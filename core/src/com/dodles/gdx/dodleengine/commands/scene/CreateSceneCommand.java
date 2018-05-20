package com.dodles.gdx.dodleengine.commands.scene;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.IdDatabase;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.animation.AnimationManager;
import com.dodles.gdx.dodleengine.commands.Command;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.scenegraph.Scene;
import de.hypergraphs.hyena.core.shared.data.UUID;
import javax.inject.Inject;

/**
 * Command that creates a new scene in the dodle.
 */
public class CreateSceneCommand implements Command {
    public static final String COMMAND_NAME = "createScene";
    
    private final AnimationManager animationManager;
    private final ObjectManager objectManager;
    
    private String id;
    private String rootBlockID;
    private String displayName;
    private String sceneIDToCopy;

    @Inject
    public CreateSceneCommand(AnimationManager animationManager, ObjectManager objectManager) {
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
    public final void init(String pDisplayName, String pSceneIDToCopy) {
        id = UUID.uuid();
        rootBlockID = UUID.uuid();
        displayName = pDisplayName;
        sceneIDToCopy = pSceneIDToCopy;
    }

    @Override
    public final void execute() {
        Scene scene;
        
        if (sceneIDToCopy != null) {
            scene = (Scene) objectManager.getScene(sceneIDToCopy).dodleClone(new IdDatabase(), objectManager);
            id = scene.getName();
        } else {
            scene = objectManager.addScene(id);
        }
        
        scene.setDisplayName(displayName);
        animationManager.addSceneAnimation(id, rootBlockID);
    }

    @Override
    public final void undo() {
        objectManager.removeScene(id);
        animationManager.removeSceneAnimation(id);
    }

    @Override
    public final void writeConfig(Json json) {
        json.writeValue("id", id);
        json.writeValue("rootBlockID", rootBlockID);
        json.writeValue("displayName", displayName);
        json.writeValue("sceneIDToCopy", sceneIDToCopy);
    }

    @Override
    public final void loadConfig(JsonValue json, CommandFactory factory) {
        id = json.getString("id");
        rootBlockID = json.getString("rootBlockID");
        displayName = json.getString("displayName", "scene");
        sceneIDToCopy = json.getString("sceneIDToCopy", null);
    }
}

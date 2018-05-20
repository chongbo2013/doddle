package com.dodles.gdx.dodleengine.commands.scene;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.commands.Command;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.scenegraph.Layer;
import com.dodles.gdx.dodleengine.scenegraph.Scene;

import javax.inject.Inject;

/**
 * Creates a new layer in a scene.
 */
public class DeleteLayerCommand implements Command {
    public static final String COMMAND_NAME = "deleteLayer";

    private final ObjectManager objectManager;

    private String sceneID;
    private String layerID;
    private Scene scene;
    private Layer layer;

    @Inject
    public DeleteLayerCommand(ObjectManager objectManager) {
        this.objectManager = objectManager;
    }
    
    @Override
    public final String getName() {
        return COMMAND_NAME;
    }
    
    /**
     * Initializes the command.
     */
    public final void init(String pSceneID, String pLayerID) {
        this.sceneID = pSceneID;
        this.layerID = pLayerID;

        scene = objectManager.getScene(sceneID);
        layer = scene.getView(pLayerID);
    }

    @Override
    public final void execute() {
        scene.removeView(layerID);
    }

    @Override
    public final void undo() {
        scene.addLayer(layer);
    }

    @Override
    public final void writeConfig(Json json) {
        json.writeValue("sceneID", sceneID);
        json.writeValue("layerID", layerID);
    }

    @Override
    public final void loadConfig(JsonValue json, CommandFactory factory) {
        sceneID = json.getString("sceneID");
        layerID = json.getString("layerID");
    }
}

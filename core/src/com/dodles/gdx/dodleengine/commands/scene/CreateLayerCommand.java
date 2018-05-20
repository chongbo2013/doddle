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
public class CreateLayerCommand implements Command {
    public static final String COMMAND_NAME = "createLayer";
    
    private final ObjectManager objectManager;
    
    private String sceneID;
    private String layerID;
    private String displayName;
    private String passthroughSceneID;
    private Integer addAtIndex;
    
    @Inject
    public CreateLayerCommand(ObjectManager objectManager) {
        this.objectManager = objectManager;
    }
    
    @Override
    public final String getName() {
        return COMMAND_NAME;
    }
    
    /**
     * Initializes the command.
     */
    public final void init(String pSceneID, String pLayerID, String pPassthroughSceneID, String layerDisplayName) {
        this.sceneID = pSceneID;
        this.layerID = pLayerID;
        this.passthroughSceneID = pPassthroughSceneID;
        this.displayName = layerDisplayName;
    }

    @Override
    public final void execute() {
        Scene scene = objectManager.getScene(sceneID);
        Layer layer = new Layer(layerID, objectManager.getTrackingID());
        layer.setDisplayName(displayName);
        
        if (addAtIndex != null) {
            scene.addLayer(addAtIndex, layer);
        } else {
            scene.addLayer(layer);
        }
        
        objectManager.addActor(layer);
        
        if (passthroughSceneID != null) {
            layer.setPassthroughScene(objectManager.getScene(passthroughSceneID));
        }
    }

    @Override
    public final void undo() {
        objectManager.getScene(sceneID).removeView(layerID);
        objectManager.removeActor(layerID);
    }

    @Override
    public final void writeConfig(Json json) {
        json.writeValue("sceneID", sceneID);
        json.writeValue("layerID", layerID);
        
        if (passthroughSceneID != null) {
            json.writeValue("passthroughSceneID", passthroughSceneID);
        }
        
        if (addAtIndex != null) {
            json.writeValue("addAtIndex", addAtIndex.intValue());
        }
    }

    @Override
    public final void loadConfig(JsonValue json, CommandFactory factory) {
        sceneID = json.getString("sceneID");
        layerID = json.getString("layerID");
        
        if (json.has("passthroughSceneID")) {
            passthroughSceneID = json.getString("passthroughSceneID");
        }
        
        if (json.has("addAtIndex")) {
            addAtIndex = json.getInt("addAtIndex");
        }
    }
}

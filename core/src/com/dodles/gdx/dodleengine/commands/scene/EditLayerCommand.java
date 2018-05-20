package com.dodles.gdx.dodleengine.commands.scene;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.commands.Command;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.scenegraph.Layer;
import com.dodles.gdx.dodleengine.scenegraph.Scene;
import javax.inject.Inject;

/**
 * Edits layer information.
 */
public class EditLayerCommand implements Command {
    public static final String COMMAND_NAME = "editLayer";
    
    private final ObjectManager objectManager;
    
    private Layer layer;
    
    private Scene origPassthroughScene;
    private String origLayerName;
    
    private Scene finalPassthroughScene;
    private String finalLayerName;
    
    @Inject
    public EditLayerCommand(ObjectManager objectManager) {
        this.objectManager = objectManager;
    }
    
    @Override
    public final String getName() {
        return COMMAND_NAME;
    }
    
    /**
     * Initializes the command.
     */
    public final void init(Layer pLayer, Scene passthroughScene, String layerName) {
        layer = pLayer;
        finalPassthroughScene = passthroughScene;
        finalLayerName = layerName;
    }

    @Override
    public final void execute() {
        origPassthroughScene = layer.getPassthroughScene();
        origLayerName = layer.getDisplayName();
        
        layer.setPassthroughScene(finalPassthroughScene);
        layer.setDisplayName(finalLayerName);
    }

    @Override
    public final void undo() {
        layer.setPassthroughScene(origPassthroughScene);
        layer.setDisplayName(origLayerName);
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

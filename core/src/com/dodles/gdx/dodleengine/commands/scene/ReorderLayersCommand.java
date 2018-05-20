package com.dodles.gdx.dodleengine.commands.scene;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.commands.Command;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.scenegraph.Layer;
import com.dodles.gdx.dodleengine.scenegraph.Scene;
import java.util.ArrayList;
import javax.inject.Inject;

/**
 * Reorders layers in a scene.
 */
public class ReorderLayersCommand implements Command {
    public static final String COMMAND_NAME = "reorderLayers";
    
    private final ObjectManager objectManager;
    
    private Scene scene;
    private ArrayList<Layer> originalLayerList;
    private ArrayList<Layer> finalLayerList;
    
    @Inject
    public ReorderLayersCommand(ObjectManager objectManager) {
        this.objectManager = objectManager;
    }
    
    @Override
    public final String getName() {
        return COMMAND_NAME;
    }
    
    /**
     * Initializes the command.
     */
    public final void init(Scene pScene, ArrayList<Layer> layers) {
        this.scene = pScene;
        this.finalLayerList = layers;
    }

    @Override
    public final void execute() {
        originalLayerList = new ArrayList<Layer>(scene.getViews());
        scene.replaceViews(finalLayerList);
    }

    @Override
    public final void undo() {        
        scene.replaceViews(originalLayerList);
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

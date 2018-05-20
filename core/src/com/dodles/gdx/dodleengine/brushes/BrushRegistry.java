package com.dodles.gdx.dodleengine.brushes;

import com.badlogic.gdx.math.Vector2;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.editor.EditorState;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.scenegraph.Shape;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Registry for managing brushes.
 */
@PerDodleEngine
public class BrushRegistry {
    private final HashMap<String, Brush> brushMap = new HashMap<String, Brush>();
    private final EditorState editorState;
    private final EngineEventManager eventManager;

    private Brush activeBrush = null;
    
    @Inject
    public BrushRegistry(EditorState editorState, EngineEventManager eventManager) {
        this.editorState = editorState;
        this.eventManager = eventManager;
    }
    
    /**
     * Registers a brush with the registry.
     */
    public final void registerBrush(Brush brush) {
        brushMap.put(brush.getName(), brush);
        
        // TODO: handle achievement brushes
    }
    
    /**
     * Returns all brushes in the registry.
     */
    public final List<Brush> getBrushes() {
        ArrayList<Brush> result = new ArrayList<Brush>(brushMap.values());
        Collections.sort(result);
        return result;
    }
    
    /**
     * Returns the brush with the given name.
     */
    public final Brush getBrush(String name) {
        return brushMap.get(name);
    }
    
    /**
     * Returns the active brush.
     */
    public final Brush getActiveBrush() {
        return activeBrush;
    }
    
    /**
     * Sets the active brush.
     */
    public final void setActiveBrush(String name) {
        if(activeBrush instanceof EraserBrush) {
            editorState.revertToPreviousStrokeConfig();
        }
        activeBrush = brushMap.get(name);
        
        if (activeBrush != null) {
            editorState.setStrokeConfig(activeBrush.getBrushStrokeConfig());
        }

        this.eventManager.fireEvent(EngineEventType.BRUSH_CHANGED, name);
    }
    
    /**
     * Initializes the shape according to it's brush configuration (if any).
     */
    public final void init(Shape shape) {
        if (shape.getCustomConfig() instanceof BrushConfig) {
            BrushConfig bc = (BrushConfig) shape.getCustomConfig();
            Brush brush = getBrush(bc.getBrush());
            brush.init(shape, bc.getRulerMode());
            
            for (Vector2 point : bc.getPoints()) {
                brush.mouseMove(point);
            }
        }
    }
}

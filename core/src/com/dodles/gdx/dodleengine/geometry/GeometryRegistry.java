package com.dodles.gdx.dodleengine.geometry;

import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.editor.EditorState;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.events.EventBus;
import com.dodles.gdx.dodleengine.events.EventTopic;
import com.dodles.gdx.dodleengine.events.EventType;
import com.dodles.gdx.dodleengine.scenegraph.Shape;
import com.dodles.gdx.dodleengine.scenegraph.StrokeConfig;
import com.dodles.gdx.dodleengine.tools.Tool;
import com.dodles.gdx.dodleengine.tools.ToolRegistry;
import com.dodles.gdx.dodleengine.tools.geometry.GeometryTool;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Registry for geometric shape providers.
 */
@PerDodleEngine
public class GeometryRegistry {
    private final HashMap<String, Geometry> geometryMap = new HashMap<String, Geometry>();
    private final EditorState editorState;
    private final EngineEventManager eventManager;
    private final EventBus eventBus;
    private final ToolRegistry toolRegistry;
    private Geometry activeGeometry;
    private GeometryConfig geometryConfig;
    
    @Inject
    public GeometryRegistry(
            EditorState editorState,
            EngineEventManager eventManager,
            EventBus eventBus,
            ToolRegistry toolRegistry
    ) {
        this.editorState = editorState;
        this.eventManager = eventManager;
        this.eventBus = eventBus;
        this.toolRegistry = toolRegistry;
    }    
    
    /**
     * Registers a geometry manager with the registry.
     */
    public final void registerGeometry(Geometry geometry) {
        geometryMap.put(geometry.getName(), geometry);
    }
    
    /**
     * Returns all geometries in the registry.
     */
    public final List<Geometry> getGeometries() {
        ArrayList<Geometry> result = new ArrayList<Geometry>(geometryMap.values());
        Collections.sort(result);
        return result;
    }
    
    /**
     * Returns the geometry with the given name.
     */
    public final Geometry getGeometry(String name) {
        return geometryMap.get(name);
    }
    
    /**
     * Sets the active geometry.
     */
    public final Geometry setActiveGeometry(String name) {
        return setActiveGeometry(name, null, null);
    }
    
    /**
     * Sets the active geometry.
     */
    public final Geometry setActiveGeometry(String name, GeometryConfig gc, StrokeConfig sc) {
        activeGeometry = geometryMap.get(name);
        String newToolName = GeometryTool.TOOL_NAME;
        if (activeGeometry != null) {
            if (gc == null) {
                gc = activeGeometry.getDefaultGeometryConfig();
            }
            geometryConfig = gc;
            
            if (sc == null) {
                sc = activeGeometry.getDefaultStrokeConfig();
            }
            editorState.setStrokeConfig(sc);
            eventBus.publish(EventTopic.DEFAULT, EventType.SHAPE_ACTIVATED, activeGeometry.getName());

            newToolName = GeometryTool.TOOL_NAME + "." + activeGeometry.getName();
        }

        // If the geometry tool is active, update its state
        Tool activeTool = toolRegistry.getActiveTool();
        if (activeTool != null && activeTool.getName().startsWith(GeometryTool.TOOL_NAME)) {
            eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, newToolName);
        }

        return activeGeometry;
    }
    
    /**
     * Returns the active geometry.
     */
    public final Geometry getActiveGeometry() {
        return activeGeometry;
    }
    
    /**
     * Returns the current geometry configuration.
     */
    public final GeometryConfig getGeometryConfig() {
        return geometryConfig;
    }
    
    /**
     * Sets the current geometry configuration.
     */    
    public final void setGeometryConfig(GeometryConfig newGeometryConfig) {
        geometryConfig = newGeometryConfig;
    }
    
    /**
     * Initializes the shape according to it's geometry (if any).
     */
    public final void init(Shape shape) {
        if (shape.getCustomConfig() instanceof GeometryConfig) {
            GeometryConfig gc = (GeometryConfig) shape.getCustomConfig();
            getGeometry(gc.getType()).init(shape);
        }
    }
}

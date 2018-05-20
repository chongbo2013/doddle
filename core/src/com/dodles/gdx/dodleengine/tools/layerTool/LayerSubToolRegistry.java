package com.dodles.gdx.dodleengine.tools.layerTool;

import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.events.EngineEventData;
import com.dodles.gdx.dodleengine.events.EngineEventListener;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.events.EventBus;
import com.dodles.gdx.dodleengine.events.EventTopic;
import com.dodles.gdx.dodleengine.events.EventType;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Registry for managing tool state.
 */
@PerDodleEngine
public class LayerSubToolRegistry {
    private final HashMap<String, LayerSubTool> toolMap = new HashMap<String, LayerSubTool>();
    private final EngineEventManager eventManager;
    private final EventBus eventBus;

    private LayerSubTool activeTool = null;

    @Inject
    public LayerSubToolRegistry(EngineEventManager eventManager, EventBus eventBus) {
        this.eventManager = eventManager;
        this.eventBus = eventBus;

        eventManager.addListener(new EngineEventListener(EngineEventType.CHANGE_EDITOR_STATE) {
            @Override
            public void listen(EngineEventData data) {
                String state = data.getFirstStringParam();
                
                if (state != null && state.toUpperCase().indexOf(LayerTool.TOOL_NAME + ".") == 0) {
                    int periodPos = state.indexOf(".", LayerTool.TOOL_NAME.length() + 1);
                    
                    if (periodPos >= 0) {
                        state = state.substring(0, periodPos);
                    }
                    
                    setActiveTool(state);
                } else {
                    setActiveTool(null);
                }
            }
        });
    }
    
    /**
     * Registers a layerTool with the registry.
     */
    public final void registerTool(LayerSubTool layerSubTool) {
        toolMap.put(layerSubTool.getName(), layerSubTool);
    }
    
    /**
     * Returns all tools for the given row, optionally including the undo layerTool.
     */
    public final List<LayerSubTool> getTools(int row) {
        ArrayList<LayerSubTool> sorted = new ArrayList();
        
        for (LayerSubTool layerSubTool : toolMap.values()) {
            if (layerSubTool.getRow() == row) {
                sorted.add(layerSubTool);
            }
        }

        Collections.sort(sorted);
        return sorted;
    }
    
    /**
     * Returns the layerTool with the given name.
     */
    public final LayerSubTool getTool(String name) {
        return toolMap.get(name);
    }

    /**
     * Returns the active layerTool.
     */
    public final LayerSubTool getActiveTool() {
        if (activeTool == null) {
            setActiveTool(null);
        }
        
        return activeTool;
    }
    
    /**
     * Sets the active layerTool.
     */
    public final void setActiveTool(String toolName) {
        LayerSubTool newTool = toolMap.get(toolName);
        
        if (newTool != activeTool) {
            if (activeTool != null) {
                activeTool.onDeactivation();
            }
            
            activeTool = newTool;
            
            if (activeTool != null) {
                activeTool.onActivation();
                this.eventBus.publish(EventTopic.DEFAULT, EventType.TOOL_CHANGED, activeTool.getName());
            }
        }
    }
}

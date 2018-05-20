package com.dodles.gdx.dodleengine.tools;

import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.editor.full.FullEditorInterface;
import com.dodles.gdx.dodleengine.editor.full.FullEditorViewState;
import com.dodles.gdx.dodleengine.events.EngineEventData;
import com.dodles.gdx.dodleengine.events.EngineEventListener;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.events.EventBus;
import com.dodles.gdx.dodleengine.events.EventTopic;
import com.dodles.gdx.dodleengine.events.EventType;
import com.dodles.gdx.dodleengine.tools.nullTool.NullTool;
import com.dodles.gdx.dodleengine.tools.redo.RedoTool;
import com.dodles.gdx.dodleengine.tools.undo.UndoTool;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Registry for managing tool state.
 */
@PerDodleEngine
public class ToolRegistry {
    public static final String TOOL_NAMESPACE = "TOOL.";
    private final HashMap<String, Tool> toolMap = new HashMap<String, Tool>();
    private final EngineEventManager eventManager;
    private final EventBus eventBus;
    private Tool activeTool = null;

    @Inject
    public ToolRegistry(EngineEventManager eventManager, EventBus eventBus) {
        this.eventManager = eventManager;
        this.eventBus = eventBus;
        eventManager.addListener(new EngineEventListener(EngineEventType.CHANGE_EDITOR_STATE) {
            @Override
            public void listen(EngineEventData data) {
                String toolName = getToolNameFromState(data.getFirstStringParam());

                if (toolName != null) {
                    setActiveTool(toolName);
                }
            }
        });
    }

    /**
     * Parses and returns the tool name from the current state, or null if the state
     * doesn't contain a tool name.
     */
    public final String getToolNameFromState(String state) {
        if (state.equals(FullEditorViewState.PREVIOUS_STATE)) {
            if (activeTool != null) {
                return activeTool.getName();
            }
            return null;
        }
        if (state != null && state.indexOf(TOOL_NAMESPACE) == 0) {
            int periodPos = state.indexOf(".", TOOL_NAMESPACE.length());

            if (periodPos >= 0) {
                return state.substring(0, periodPos);
            }

            return state;
        }

        return null;
    }

    /**
     * Registers a tool with the registry.
     */
    public final void registerTool(Tool tool) {
        toolMap.put(tool.getName(), tool);
    }

    /**
     * Returns all tools for the given row, optionally including the undo tool.
     */
    public final List<Tool> getTools(int row, boolean includeUndo) {
        ArrayList<Tool> sorted = new ArrayList();

        for (Tool tool : toolMap.values()) {
            if (tool.getRow() == row) {
                sorted.add(tool);
            }
        }

        Collections.sort(sorted);

        while (sorted.size() < FullEditorInterface.MAX_COLUMNS) {
            sorted.add(toolMap.get(NullTool.TOOL_NAME));
        }
        if (includeUndo) {
            sorted.remove(sorted.size() - 1);
            sorted.remove(sorted.size() - 1);
            sorted.add(sorted.size(), toolMap.get(UndoTool.TOOL_NAME));
            sorted.add(sorted.size(), toolMap.get(RedoTool.TOOL_NAME));
        }
        return sorted;
    }

    /**
     * Returns the tool with the given name.
     */
    public final Tool getTool(String name) {
        return toolMap.get(name);
    }

    /**
     * Returns the active tool.
     */
    public final Tool getActiveTool() {
        if (activeTool == null) {
            setActiveTool(null);
        }

        return activeTool;
    }

    /**
     * Sets the active tool.
     */
    public final void setActiveTool(String toolName) {
        Tool newTool = toolMap.get(toolName);
        if (newTool == null) {
            newTool = toolMap.get(NullTool.TOOL_NAME);
        }

        if (newTool != activeTool) {
            if (activeTool != null) {
                activeTool.onDeactivation();
            }
            activeTool = newTool;
            activeTool.onActivation();
            this.eventBus.publish(EventTopic.DEFAULT, EventType.TOOL_CHANGED, activeTool.getName());
        }
    }

    /**
     * Deactivates a tool if it is the current active tool
     */
    public final void deactivateTool(String toolName)
    {
        if (activeTool != null && activeTool.getName() == toolName) {
            setActiveTool(null);
        }
    }
}
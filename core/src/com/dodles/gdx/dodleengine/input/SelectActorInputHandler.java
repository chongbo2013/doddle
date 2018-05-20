package com.dodles.gdx.dodleengine.input;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.editor.overlays.SelectedActorOverlay;
import com.dodles.gdx.dodleengine.scenegraph.CommonActorOperations;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.tools.ToolRegistry;
import com.dodles.gdx.dodleengine.tools.layerTool.LayerTool;

import javax.inject.Inject;
import java.util.HashSet;

/**
 * A common handler to allow single object selection.
 */
public class SelectActorInputHandler implements TouchInputHandler {
    private final ObjectManager objectManager;
    private final DodleStageManager stageManager;
    private final SelectedActorOverlay selectedActorOverlay;
    private final ToolRegistry toolRegistry;
    
    private Vector2 downPoint = null;
    private Runnable selectionCallback = null;
    private boolean multiSelectEnabled = false;
    private boolean isMultiSelecting = false;
    
    private HashSet<Integer> pointers = new HashSet<Integer>();
    
    @Inject
    public SelectActorInputHandler(
            ObjectManager objectManager,
            DodleStageManager stageManager,
            SelectedActorOverlay selectedActorOverlay,
            ToolRegistry toolRegistry
    ) {
        this.objectManager = objectManager;
        this.stageManager = stageManager;
        this.selectedActorOverlay = selectedActorOverlay;
        this.toolRegistry = toolRegistry;
    }
    
    /**
     * Allows a callback to be set that will be triggered when an actor is successfully selected.
     */
    public final void setSelectionCallback(Runnable callback) {
        selectionCallback = callback;
    }
    
    /**
     * Indicates whether multi select is enabled.
     */
    public final void setMultiSelectEnabled(boolean enabled) {
        multiSelectEnabled = enabled;
    }

    @Override
    public final void handleTouchStart(InteractionData startData, int pointer) {
        pointers.add(pointer);
        
        if (pointer == 0) {
            downPoint = startData.getDodlePoint();
            
            if (multiSelectEnabled && objectManager.findActiveActor(startData.getDodlePoint()) == null && !layerToolActivated()) {
                isMultiSelecting = true;
                objectManager.clearSelectedActors();
            }
        }
    }

    @Override
    public final void handleTouchMove(InteractionData moveData, int pointer) {
        if (isMultiSelecting && pointer == 0 && pointers.size() == 1) {
            Rectangle dragRect = new Rectangle(downPoint.x, downPoint.y, 0, 0);
            dragRect = dragRect.merge(moveData.getDodlePoint());

            if (dragRect.width > 10 || dragRect.height > 10) {
                boolean anySelected = false;
            
                objectManager.clearSelectedActors();

                for (DodlesActor actor : objectManager.activeActors()) {
                    Rectangle bounds = CommonActorOperations.getDodleBounds(actor);

                    if (bounds != null && dragRect.overlaps(bounds)) {
                        objectManager.addToMultiSelect(actor);
                    }
                }

                selectedActorOverlay.setDragRectangle(dragRect);

                stageManager.updateStateUi();
            }
        }
    }

    @Override
    public final void handleTouchEnd(InteractionData endData, int pointer) {
        if (pointer == 0 && pointers.size() == 1) {
            if (!isMultiSelecting && downPoint != null && downPoint.dst(endData.getDodlePoint()) < 5 && !layerToolActivated()) {
                objectManager.select(endData.getDodlePoint());
            }

            if (objectManager.getSelectedActors().size() > 0 && selectionCallback != null) {
                selectionCallback.run();
            }

            selectedActorOverlay.setDragRectangle(null);
            isMultiSelecting = false;
            downPoint = null;

            stageManager.updateStateUi();
        }
        
        pointers.remove(pointer);
    }

    @Override
    public void handleTouchCancel() {
    }

    private boolean layerToolActivated() {
        return toolRegistry.getActiveTool().getName().equals(LayerTool.TOOL_NAME);
    }
}

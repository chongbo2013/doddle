package com.dodles.gdx.dodleengine.input;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.editor.overlays.SelectedActorOverlay;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import javax.inject.Inject;

/**
 * A common handler to allow single object selection.
 */
public class CropActorInputHandler implements TouchInputHandler {
    private final ObjectManager objectManager;
    private final DodleStageManager stageManager;
    private final SelectedActorOverlay selectedActorOverlay;

    private Vector2 downPoint = null;
    private Runnable selectionCallback = null;
    private boolean multiSelectEnabled = false;

    private HashSet<Integer> pointers = new HashSet<Integer>();
    private int closestCorner;
    private Rectangle bounds;
    private ArrayList<Vector2> corners;

    @Inject
    public CropActorInputHandler(ObjectManager objectManager, DodleStageManager stageManager, SelectedActorOverlay selectedActorOverlay) {
        this.objectManager = objectManager;
        this.stageManager = stageManager;
        this.selectedActorOverlay = selectedActorOverlay;
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
            double distance = Double.MAX_VALUE;

            if (bounds == null) {
                bounds = objectManager.getActiveLayer().getDrawBounds();
            }

            corners = new ArrayList<Vector2>();
            corners.add(new Vector2(bounds.x, bounds.y));
            corners.add(new Vector2(bounds.width + bounds.x, bounds.y));
            corners.add(new Vector2(bounds.width + bounds.x, bounds.height + bounds.y));
            corners.add(new Vector2(bounds.x, bounds.height + bounds.y));

            downPoint = startData.getDodlePoint();

            for (int i = 0; i < corners.size(); i++) {
                Vector2 corner = corners.get(i);
                double cornerDistance = Math.hypot(corner.x - downPoint.x, corner.y - downPoint.y);
                if (cornerDistance < distance) {
                    distance = cornerDistance;
                    closestCorner = i;
                }
            }
        }
    }

    @Override
    public final void handleTouchMove(InteractionData moveData, int pointer) {
        //CHECKSTYLE.OFF: AvoidInlineConditionals - this is ok for now
        int previousCorner = closestCorner > 0 ? closestCorner - 1 : corners.size() - 1;
        int nextCorner = closestCorner < corners.size() - 1 ? closestCorner + 1 : 0;
        //CHECKSTYLE.ON: AvoidInlineConditionals - this is ok for now

        ArrayList<Integer> cornerIndexes = new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3));
        cornerIndexes.removeAll(Arrays.asList(previousCorner, nextCorner));

        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float maxY = Float.MIN_VALUE;

        for (int i = 0; i < corners.size(); i++) {
            if (i == closestCorner) {
                corners.set(i, moveData.getDodlePoint());
            }

            if (cornerIndexes.contains(i)) {
                minX = Math.min(minX, corners.get(i).x);
                minY = Math.min(minY, corners.get(i).y);
                maxX = Math.max(maxX, corners.get(i).x);
                maxY = Math.max(maxY, corners.get(i).y);
            }
        }

        bounds = new Rectangle(minX, minY, maxX - minX, maxY - minY);

        selectedActorOverlay.setDragRectangle(bounds);

        stageManager.updateStateUi();

        selectionCallback.run();
    }

    @Override
    public final void handleTouchEnd(InteractionData endData, int pointer) {
        selectedActorOverlay.setDragRectangle(null);
        pointers.remove(pointer);
    }

    @Override
    public void handleTouchCancel() {

    }

    /**
     * Return the selected region.
     */
    public final Rectangle getSelection() {
        return this.bounds;
    }
}

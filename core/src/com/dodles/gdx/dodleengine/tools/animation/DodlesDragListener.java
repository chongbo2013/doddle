package com.dodles.gdx.dodleengine.tools.animation;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.kotcrab.vis.ui.layout.DragPane;
import com.kotcrab.vis.ui.widget.Draggable;

import java.util.ArrayList;

/**
 * Custom drag pane listener behavior.
 */
public class DodlesDragListener extends DragPane.DefaultDragListener {
    public static final String SCENE_DRAGTYPE = "Scene";
    public static final String LAYER_DRAGTYPE = "Layer";

    private ArrayList<Actor> dragHandles = new ArrayList<Actor>();
    private ArrayList<Runnable> dragEndRunnables = new ArrayList<Runnable>();
    private String dragType;

    private EngineEventManager eventManager;

    public DodlesDragListener() {
        super();
    }

    public DodlesDragListener(EngineEventManager eventManager, String dragType) {
        super();
        this.eventManager = eventManager;
        this.dragType = dragType;
    }
    
    /**
     * Adds a handle that will allow dragging.
     */
    public final void addDragHandle(Actor actor) {
        this.dragHandles.add(actor);
    }
    
    /**
     * Adds a runnable to be called at the end of a drag.
     */
    public final void addDragEndRunnable(Runnable runnable) {
        dragEndRunnables.add(runnable);
    }
    
    /**
     * Returns true if the hit test hits a drag handle.
     */
    public final boolean hitDragHandle(Actor actor, float localX, float localY) {
        Actor hitActor = actor.hit(localX, localY, true);
        return dragHandles.contains(hitActor);
    }
    
    @Override
    public final boolean onStart(final Draggable draggable, final Actor actor, final float stageX, final float stageY) {
        Vector2 local = actor.stageToLocalCoordinates(new Vector2(stageX, stageY));
        return hitDragHandle(actor, local.x, local.y);
    }
                
    @Override
    public final boolean onEnd(final Draggable draggable, final Actor actor, final float stageX, final float stageY) {
        if (actor == null || actor.getStage() == null) {
                return CANCEL;
        }
        final Actor overActor = actor.getStage().hit(stageX, stageY, true);
        
        // Not sure what we're doing wrong that we need this and it isn't in the base, but whatever...
        if (overActor != null && overActor.isDescendantOf(actor)) {
            return CANCEL;
        }

        boolean retVal = super.onEnd(draggable, actor, stageX, stageY);
        
        for (Runnable runnable : dragEndRunnables) {
            runnable.run();
        }
        
        return retVal;
    }
}

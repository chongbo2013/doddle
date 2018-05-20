package com.dodles.gdx.dodleengine.animation;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.input.InteractionData;
import com.dodles.gdx.dodleengine.input.TouchInputHandler;
import com.dodles.gdx.dodleengine.scenegraph.CommonActorOperations;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;

/**
 * A block trigger that fires on a touch event for an actor.
 */
public class TouchBlockTrigger extends BlockTrigger implements TouchInputHandler {
    public static final String TRIGGER_TYPE = "Touch";
    private final DodlesActor actor;
    private boolean triggered = false;
    
    public TouchBlockTrigger(DodlesActor actor) {
        this.actor = actor;
    }
    
    public TouchBlockTrigger(JsonValue json, ObjectManager objectManager) {
        this(objectManager.getActor(json.getString("actorID")));
    }
    
    @Override
    public final String getType() {
        return TRIGGER_TYPE;
    }

    @Override
    public final boolean isTriggered() {
        return triggered;
    }

    @Override
    public final void handleTouchStart(InteractionData startData, int pointer) {
        Vector2 local = CommonActorOperations.dodleToLocalCoordinates(actor, startData.getDodlePoint());
        
        if (pointer == 0) {
            Rectangle bounds = actor.getDrawBounds();
            triggered = bounds != null && bounds.contains(local);
        }
    }

    @Override
    public final void handleTouchMove(InteractionData moveData, int pointer) {
    }

    @Override
    public final void handleTouchEnd(InteractionData endData, int pointer) {
        if (pointer == 0) {
            triggered = false;
        }
    }

    @Override
    public void handleTouchCancel() {
        
    }

    @Override
    public final void onWriteConfig(Json json) {
        json.writeValue("actorID", actor.getName());
    }
}

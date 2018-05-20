package com.dodles.gdx.dodleengine.events;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract listener to assist with creating listeners.
 */
public abstract class EngineEventListener {
    private final ArrayList<EngineEventType> eventTypes = new ArrayList<EngineEventType>();
    
    public EngineEventListener(EngineEventType eventType) {
        this.eventTypes.add(eventType);
    }
    
    public EngineEventListener(EngineEventType... eventTypes) {
        for (EngineEventType eventType : eventTypes) {
            this.eventTypes.add(eventType);
        }
    }
    
    /**
     * Returns the types of event the listener is listening for.
     */
    public final List<EngineEventType> getEventTypes() {
        return eventTypes;
    }
    
    /**
     * Called when the event this listener is listening for is fired.
     */
    public abstract void listen(EngineEventData data);
}

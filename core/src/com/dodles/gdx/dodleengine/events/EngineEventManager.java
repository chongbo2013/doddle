package com.dodles.gdx.dodleengine.events;

import com.dodles.gdx.dodleengine.PerDodleEngine;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javax.inject.Inject;

/**
 * Brokers events being sent inside the DodleEngine.
 */
@PerDodleEngine
public class EngineEventManager {
    private final HashMap<EngineEventType, ArrayList<EngineEventListener>> eventRegistry = new HashMap<EngineEventType, ArrayList<EngineEventListener>>();
    
    @Inject
    public EngineEventManager() {
    }
    
    /**
     * Registers a listener to receive events.
     */
    public final void addListener(EngineEventListener listener) {
        for (EngineEventType eventType : listener.getEventTypes()) {
            if (!eventRegistry.containsKey(eventType)) {
                eventRegistry.put(eventType, new ArrayList<EngineEventListener>());
            }

            eventRegistry.get(eventType).add(listener);
        }
    }

    /**
     * Removes a listener from the registry so it no longer receives events.
     */
    public final void removeListener(EngineEventListener listener) {
        for (EngineEventType eventType : listener.getEventTypes()) {
            if (eventRegistry.containsKey(eventType)) {
                eventRegistry.get(eventType).remove(listener);
            }
        }
    }

    /**
     * Fires an event with no parameters.
     */
    public final void fireEvent(EngineEventType type) {
        fireEvent(type, new EngineEventData());
    }

    /**
     * Fires an event with the given parameters.
     * @param parameters 
     */
    public final void fireEvent(EngineEventType type, String... parameters) {
        EngineEventData data = new EngineEventData(new ArrayList<String>(Arrays.asList(parameters)));
        fireEvent(type, data);
    }

    /**
     * Fires the given event with the specific eventdata instance given.
     */
    public final void fireEvent(EngineEventType type, EngineEventData data) {
        if (eventRegistry.containsKey(type)) {
            for (EngineEventListener listener : new ArrayList<EngineEventListener>(eventRegistry.get(type))) {
                listener.listen(data);
            }
        }
    }
}

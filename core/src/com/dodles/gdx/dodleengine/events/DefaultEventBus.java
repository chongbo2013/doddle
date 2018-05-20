package com.dodles.gdx.dodleengine.events;

import com.dodles.gdx.dodleengine.PerDodleEngine;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Generic event framework to communicate between the website, app, and engine.
 */
@PerDodleEngine
public class DefaultEventBus implements EventBus {
    private final HashMap<EventTopic, ArrayList<EventSubscriber>> eventRegistry = new HashMap<EventTopic, ArrayList<EventSubscriber>>();

    @Inject
    public DefaultEventBus() {
    }

    /**
     * Registers a subscriber to subscribe to events.
     */
    @Override
    public final void addSubscriber(EventSubscriber subscriber) {
        for (EventTopic eventTopic : subscriber.getEventTopics()) {
            if (!eventRegistry.containsKey(eventTopic)) {
                eventRegistry.put(eventTopic, new ArrayList<EventSubscriber>());
            }

            eventRegistry.get(eventTopic).add(subscriber);
        }
    }

    /**
     * Removes a subscriber from the registry so it no longer receives events.
     */
    @Override
    public final void removeSubscriber(EventSubscriber subscriber) {
        for (EventTopic eventTopic : subscriber.getEventTopics()) {
            if (eventRegistry.containsKey(eventTopic)) {
                eventRegistry.get(eventTopic).remove(subscriber);
            }
        }
    }

    /**
     * Publishes an event with no parameters.
     */
    @Override
    public final void publish(EventType eventType) {
        publish(EventTopic.DEFAULT, eventType, new EventData());
    }

    /**
     * Publishes an event with no parameters.
     */
    @Override
    public final void publish(EventTopic eventTopic, EventType eventType) {
        publish(eventTopic, eventType, new EventData());
    }

    /**
     * Publishes an event with the given parameters.
     * @param parameters
     */
    @Override
    public final void publish(EventType eventType, String... parameters) {
        publish(EventTopic.DEFAULT, eventType, parameters);
    }

    /**
     * Publishes an event with the given parameters.
     * @param parameters
     */
    @Override
    public final void publish(EventTopic eventTopic, EventType eventType, String... parameters) {
        EventData data = new EventData(new ArrayList<String>(Arrays.asList(parameters)));
        publish(eventTopic, eventType, data);
    }

    /**
     * Publishes the given event with the specific eventdata instance given.
     */
    @Override
    public final void publish(EventTopic eventTopic, EventType eventType, EventData data) {
        if (eventRegistry.containsKey(eventTopic)) {
            for (EventSubscriber subscriber : new ArrayList<EventSubscriber>(eventRegistry.get(eventTopic))) {
                subscriber.listen(eventTopic, eventType, data);
            }
        }
    }
}
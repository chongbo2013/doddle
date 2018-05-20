package com.dodles.gdx.dodleengine.events;

/**
 * Generic event framework to communicate between the website, app, and engine.
 */
public interface EventBus {

    /**
     * Registers a subscriber to subscribe to events.
     */
    void addSubscriber(EventSubscriber subscriber);

    /**
     * Removes a subscriber from the registry so it no longer receives events.
     */
    void removeSubscriber(EventSubscriber subscriber);

    /**
     * Publishes an event with no parameters.
     */
    void publish(EventType eventType);

    /**
     * Publishes an event with no parameters.
     */
    void publish(EventTopic eventTopic, EventType eventType);

    /**
     * Publishes an event with the given parameters.
     * @param parameters
     */
    void publish(EventType eventType, String... parameters);

    /**
     * Publishes an event with the given parameters.
     * @param parameters
     */
    void publish(EventTopic eventTopic, EventType eventType, String... parameters);

    /**
     * Publishes the given event with the specific eventdata instance given.
     */
    void publish(EventTopic eventTopic, EventType eventType, EventData data);
}
package com.dodles.gdx.dodleengine.events;

/**
 * Describes an events parameters.
 */
public class EventCallbackCacheKey {
    private String topic;
    private EventSubscriber eventSubscriber;

    public EventCallbackCacheKey(String topic, EventSubscriber eventSubscriber) {
        this.topic = topic;
        this.eventSubscriber = eventSubscriber;
    }
}

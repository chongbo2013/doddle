package com.dodles.gdx.dodleengine.events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Abstract subscriber to assist with creating subscribers.
 */
public abstract class EventSubscriber {
    private final ArrayList<EventTopic> eventTopics = new ArrayList<EventTopic>();

    public EventSubscriber() {
        this.eventTopics.add(EventTopic.DEFAULT);
    }

    public EventSubscriber(EventTopic eventTopic) {
        this.eventTopics.add(eventTopic);
    }

    public EventSubscriber(EventTopic... eventTopics) {
        for (EventTopic eventTopic : eventTopics) {
            this.eventTopics.add(eventTopic);
        }
    }
    
    /**
     * Returns the topics the subscriber is listening for.
     */
    public final List<EventTopic> getEventTopics() {
        return eventTopics;
    }
    
    /**
     * Called when the an event is published for a topic to which the subscriber has subscribed.
     */
    public abstract void listen(EventTopic eventTopic, EventType eventType, EventData data);

    /**
     * Called when the an event is published for a topic to which the subscriber has subscribed.
     */
    public final void listen(String topic, String eventType, String parameter1, String parameter2, String parameter3) {
        listen(EventTopic.fromString(topic), EventType.valueOf(eventType), new EventData(new ArrayList<String>(
                Arrays.asList(parameter1, parameter2, parameter3))));
    }
}

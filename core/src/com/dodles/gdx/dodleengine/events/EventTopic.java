package com.dodles.gdx.dodleengine.events;

/**
 * Defines the available types of event topics.
 */
public enum EventTopic {
    EDITOR("EDITOR"),
    DEFAULT("DEFAULT");

    private final String eventTopicDescription;

    EventTopic(String eventTopicDescription) {
        this.eventTopicDescription = eventTopicDescription;
    }

    /**
     * Get an EventTopic from a string.
     */
    public static EventTopic fromString(String text) {
        if (text != null) {
            for (EventTopic e : EventTopic.values()) {
                if (text.equals(e.eventTopicDescription)) {
                    return e;
                }
            }
        }
        return null;
    }

    /**
     * Get the name of the event topic as a string.
     */
    public String toString() {
        return eventTopicDescription;
    }
}

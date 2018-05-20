package com.dodles.gdx.dodleengine.input;

/**
 * Interface for handling mouse wheel scroll events.
 */
public interface WheelInputHandler extends InputHandler {
    /**
     * Handles a mouse wheel scroll event.
     */
    void handleWheel(WheelInteractionData wheelData);
}
